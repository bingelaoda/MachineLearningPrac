/*    1:     */ package weka.classifiers.pmml.consumer;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import org.w3c.dom.Element;
/*    7:     */ import org.w3c.dom.Node;
/*    8:     */ import org.w3c.dom.NodeList;
/*    9:     */ import weka.core.Attribute;
/*   10:     */ import weka.core.Instance;
/*   11:     */ import weka.core.Instances;
/*   12:     */ import weka.core.RevisionUtils;
/*   13:     */ import weka.core.Utils;
/*   14:     */ import weka.core.matrix.Maths;
/*   15:     */ import weka.core.pmml.FieldMetaInfo.Optype;
/*   16:     */ import weka.core.pmml.MappingInfo;
/*   17:     */ import weka.core.pmml.MiningSchema;
/*   18:     */ import weka.core.pmml.PMMLUtils;
/*   19:     */ import weka.core.pmml.TargetMetaInfo;
/*   20:     */ import weka.gui.Logger;
/*   21:     */ 
/*   22:     */ public class GeneralRegression
/*   23:     */   extends PMMLClassifier
/*   24:     */   implements Serializable
/*   25:     */ {
/*   26:     */   private static final long serialVersionUID = 2583880411828388959L;
/*   27:     */   
/*   28:     */   static enum ModelType
/*   29:     */   {
/*   30:  62 */     REGRESSION("regression"),  GENERALLINEAR("generalLinear"),  MULTINOMIALLOGISTIC("multinomialLogistic"),  ORDINALMULTINOMIAL("ordinalMultinomial"),  GENERALIZEDLINEAR("generalizedLinear");
/*   31:     */     
/*   32:     */     private final String m_stringVal;
/*   33:     */     
/*   34:     */     private ModelType(String name)
/*   35:     */     {
/*   36:  70 */       this.m_stringVal = name;
/*   37:     */     }
/*   38:     */     
/*   39:     */     public String toString()
/*   40:     */     {
/*   41:  74 */       return this.m_stringVal;
/*   42:     */     }
/*   43:     */   }
/*   44:     */   
/*   45:  79 */   protected ModelType m_modelType = ModelType.REGRESSION;
/*   46:     */   protected String m_modelName;
/*   47:     */   protected String m_algorithmName;
/*   48:  88 */   protected int m_functionType = 0;
/*   49:     */   
/*   50:     */   static abstract enum CumulativeLinkFunction
/*   51:     */   {
/*   52:  95 */     NONE("none"),  LOGIT("logit"),  PROBIT("probit"),  CLOGLOG("cloglog"),  LOGLOG("loglog"),  CAUCHIT("cauchit");
/*   53:     */     
/*   54:     */     private final String m_stringVal;
/*   55:     */     
/*   56:     */     abstract double eval(double paramDouble1, double paramDouble2);
/*   57:     */     
/*   58:     */     private CumulativeLinkFunction(String name)
/*   59:     */     {
/*   60: 143 */       this.m_stringVal = name;
/*   61:     */     }
/*   62:     */     
/*   63:     */     public String toString()
/*   64:     */     {
/*   65: 150 */       return this.m_stringVal;
/*   66:     */     }
/*   67:     */   }
/*   68:     */   
/*   69: 155 */   protected CumulativeLinkFunction m_cumulativeLinkFunction = CumulativeLinkFunction.NONE;
/*   70:     */   
/*   71:     */   static abstract enum LinkFunction
/*   72:     */   {
/*   73: 164 */     NONE("none"),  CLOGLOG("cloglog"),  IDENTITY("identity"),  LOG("log"),  LOGC("logc"),  LOGIT("logit"),  LOGLOG("loglog"),  NEGBIN("negbin"),  ODDSPOWER("oddspower"),  POWER("power"),  PROBIT("probit");
/*   74:     */     
/*   75:     */     private final String m_stringVal;
/*   76:     */     
/*   77:     */     abstract double eval(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5);
/*   78:     */     
/*   79:     */     private LinkFunction(String name)
/*   80:     */     {
/*   81: 256 */       this.m_stringVal = name;
/*   82:     */     }
/*   83:     */     
/*   84:     */     public String toString()
/*   85:     */     {
/*   86: 263 */       return this.m_stringVal;
/*   87:     */     }
/*   88:     */   }
/*   89:     */   
/*   90: 268 */   protected LinkFunction m_linkFunction = LinkFunction.NONE;
/*   91: 269 */   protected double m_linkParameter = (0.0D / 0.0D);
/*   92:     */   protected String m_trialsVariable;
/*   93: 271 */   protected double m_trialsValue = (0.0D / 0.0D);
/*   94:     */   
/*   95:     */   static enum Distribution
/*   96:     */   {
/*   97: 278 */     NONE("none"),  NORMAL("normal"),  BINOMIAL("binomial"),  GAMMA("gamma"),  INVGAUSSIAN("igauss"),  NEGBINOMIAL("negbin"),  POISSON("poisson");
/*   98:     */     
/*   99:     */     private final String m_stringVal;
/*  100:     */     
/*  101:     */     private Distribution(String name)
/*  102:     */     {
/*  103: 288 */       this.m_stringVal = name;
/*  104:     */     }
/*  105:     */     
/*  106:     */     public String toString()
/*  107:     */     {
/*  108: 295 */       return this.m_stringVal;
/*  109:     */     }
/*  110:     */   }
/*  111:     */   
/*  112: 300 */   protected Distribution m_distribution = Distribution.NORMAL;
/*  113: 303 */   protected double m_distParameter = (0.0D / 0.0D);
/*  114:     */   protected String m_offsetVariable;
/*  115: 312 */   protected double m_offsetValue = (0.0D / 0.0D);
/*  116:     */   
/*  117:     */   static class Parameter
/*  118:     */     implements Serializable
/*  119:     */   {
/*  120:     */     private static final long serialVersionUID = 6502780192411755341L;
/*  121: 324 */     protected String m_name = null;
/*  122: 325 */     protected String m_label = null;
/*  123:     */   }
/*  124:     */   
/*  125: 329 */   protected ArrayList<Parameter> m_parameterList = new ArrayList();
/*  126:     */   
/*  127:     */   static class Predictor
/*  128:     */     implements Serializable
/*  129:     */   {
/*  130:     */     private static final long serialVersionUID = 6502780192411755341L;
/*  131: 341 */     protected String m_name = null;
/*  132: 342 */     protected int m_miningSchemaIndex = -1;
/*  133:     */     
/*  134:     */     public String toString()
/*  135:     */     {
/*  136: 345 */       return this.m_name;
/*  137:     */     }
/*  138:     */   }
/*  139:     */   
/*  140: 350 */   protected ArrayList<Predictor> m_factorList = new ArrayList();
/*  141: 353 */   protected ArrayList<Predictor> m_covariateList = new ArrayList();
/*  142:     */   protected PPCell[][] m_ppMatrix;
/*  143:     */   protected PCell[][] m_paramMatrix;
/*  144:     */   
/*  145:     */   static class PPCell
/*  146:     */     implements Serializable
/*  147:     */   {
/*  148:     */     private static final long serialVersionUID = 6502780192411755341L;
/*  149: 364 */     protected String m_predictorName = null;
/*  150: 365 */     protected String m_parameterName = null;
/*  151: 369 */     protected double m_value = 0.0D;
/*  152: 374 */     protected String m_targetCategory = null;
/*  153:     */   }
/*  154:     */   
/*  155:     */   static class PCell
/*  156:     */     implements Serializable
/*  157:     */   {
/*  158:     */     private static final long serialVersionUID = 6502780192411755341L;
/*  159: 394 */     protected String m_targetCategory = null;
/*  160: 395 */     protected String m_parameterName = null;
/*  161: 397 */     protected double m_beta = 0.0D;
/*  162: 399 */     protected int m_df = -1;
/*  163:     */   }
/*  164:     */   
/*  165:     */   public GeneralRegression(Element model, Instances dataDictionary, MiningSchema miningSchema)
/*  166:     */     throws Exception
/*  167:     */   {
/*  168: 418 */     super(dataDictionary, miningSchema);
/*  169:     */     
/*  170:     */ 
/*  171: 421 */     String mType = model.getAttribute("modelType");
/*  172: 422 */     boolean found = false;
/*  173: 423 */     for (ModelType m : ModelType.values()) {
/*  174: 424 */       if (m.toString().equals(mType))
/*  175:     */       {
/*  176: 425 */         this.m_modelType = m;
/*  177: 426 */         found = true;
/*  178: 427 */         break;
/*  179:     */       }
/*  180:     */     }
/*  181: 430 */     if (!found) {
/*  182: 431 */       throw new Exception("[GeneralRegression] unknown model type: " + mType);
/*  183:     */     }
/*  184: 434 */     if (this.m_modelType == ModelType.ORDINALMULTINOMIAL)
/*  185:     */     {
/*  186: 436 */       String cLink = model.getAttribute("cumulativeLink");
/*  187: 437 */       found = false;
/*  188: 438 */       for (CumulativeLinkFunction c : CumulativeLinkFunction.values()) {
/*  189: 439 */         if (c.toString().equals(cLink))
/*  190:     */         {
/*  191: 440 */           this.m_cumulativeLinkFunction = c;
/*  192: 441 */           found = true;
/*  193: 442 */           break;
/*  194:     */         }
/*  195:     */       }
/*  196: 445 */       if (!found) {
/*  197: 446 */         throw new Exception("[GeneralRegression] cumulative link function " + cLink);
/*  198:     */       }
/*  199:     */     }
/*  200: 448 */     else if ((this.m_modelType == ModelType.GENERALIZEDLINEAR) || (this.m_modelType == ModelType.GENERALLINEAR))
/*  201:     */     {
/*  202: 451 */       String link = model.getAttribute("linkFunction");
/*  203: 452 */       found = false;
/*  204: 453 */       for (LinkFunction l : LinkFunction.values()) {
/*  205: 454 */         if (l.toString().equals(link))
/*  206:     */         {
/*  207: 455 */           this.m_linkFunction = l;
/*  208: 456 */           found = true;
/*  209: 457 */           break;
/*  210:     */         }
/*  211:     */       }
/*  212: 460 */       if (!found) {
/*  213: 461 */         throw new Exception("[GeneralRegression] unknown link function " + link);
/*  214:     */       }
/*  215: 465 */       String linkP = model.getAttribute("linkParameter");
/*  216: 466 */       if ((linkP != null) && (linkP.length() > 0)) {
/*  217:     */         try
/*  218:     */         {
/*  219: 468 */           this.m_linkParameter = Double.parseDouble(linkP);
/*  220:     */         }
/*  221:     */         catch (IllegalArgumentException ex)
/*  222:     */         {
/*  223: 470 */           throw new Exception("[GeneralRegression] unable to parse the link parameter");
/*  224:     */         }
/*  225:     */       }
/*  226: 475 */       String trials = model.getAttribute("trialsVariable");
/*  227: 476 */       if ((trials != null) && (trials.length() > 0)) {
/*  228: 477 */         this.m_trialsVariable = trials;
/*  229:     */       }
/*  230: 481 */       String trialsV = model.getAttribute("trialsValue");
/*  231: 482 */       if ((trialsV != null) && (trialsV.length() > 0)) {
/*  232:     */         try
/*  233:     */         {
/*  234: 484 */           this.m_trialsValue = Double.parseDouble(trialsV);
/*  235:     */         }
/*  236:     */         catch (IllegalArgumentException ex)
/*  237:     */         {
/*  238: 486 */           throw new Exception("[GeneralRegression] unable to parse the trials value");
/*  239:     */         }
/*  240:     */       }
/*  241:     */     }
/*  242: 491 */     String mName = model.getAttribute("modelName");
/*  243: 492 */     if ((mName != null) && (mName.length() > 0)) {
/*  244: 493 */       this.m_modelName = mName;
/*  245:     */     }
/*  246: 496 */     String fName = model.getAttribute("functionName");
/*  247: 497 */     if (fName.equals("classification")) {
/*  248: 498 */       this.m_functionType = 1;
/*  249:     */     }
/*  250: 501 */     String algName = model.getAttribute("algorithmName");
/*  251: 502 */     if ((algName != null) && (algName.length() > 0)) {
/*  252: 503 */       this.m_algorithmName = algName;
/*  253:     */     }
/*  254: 506 */     String distribution = model.getAttribute("distribution");
/*  255: 507 */     if ((distribution != null) && (distribution.length() > 0))
/*  256:     */     {
/*  257: 508 */       found = false;
/*  258: 509 */       for (Distribution d : Distribution.values()) {
/*  259: 510 */         if (d.toString().equals(distribution))
/*  260:     */         {
/*  261: 511 */           this.m_distribution = d;
/*  262: 512 */           found = true;
/*  263: 513 */           break;
/*  264:     */         }
/*  265:     */       }
/*  266: 516 */       if (!found) {
/*  267: 517 */         throw new Exception("[GeneralRegression] unknown distribution type " + distribution);
/*  268:     */       }
/*  269:     */     }
/*  270: 521 */     String distP = model.getAttribute("distParameter");
/*  271: 522 */     if ((distP != null) && (distP.length() > 0)) {
/*  272:     */       try
/*  273:     */       {
/*  274: 524 */         this.m_distParameter = Double.parseDouble(distP);
/*  275:     */       }
/*  276:     */       catch (IllegalArgumentException ex)
/*  277:     */       {
/*  278: 526 */         throw new Exception("[GeneralRegression] unable to parse the distribution parameter");
/*  279:     */       }
/*  280:     */     }
/*  281: 530 */     String offsetV = model.getAttribute("offsetVariable");
/*  282: 531 */     if ((offsetV != null) && (offsetV.length() > 0)) {
/*  283: 532 */       this.m_offsetVariable = offsetV;
/*  284:     */     }
/*  285: 535 */     String offsetVal = model.getAttribute("offsetValue");
/*  286: 536 */     if ((offsetVal != null) && (offsetVal.length() > 0)) {
/*  287:     */       try
/*  288:     */       {
/*  289: 538 */         this.m_offsetValue = Double.parseDouble(offsetVal);
/*  290:     */       }
/*  291:     */       catch (IllegalArgumentException ex)
/*  292:     */       {
/*  293: 540 */         throw new Exception("[GeneralRegression] unable to parse the offset value");
/*  294:     */       }
/*  295:     */     }
/*  296: 545 */     readParameterList(model);
/*  297:     */     
/*  298:     */ 
/*  299: 548 */     readFactorsAndCovariates(model, "FactorList");
/*  300: 549 */     readFactorsAndCovariates(model, "CovariateList");
/*  301:     */     
/*  302:     */ 
/*  303: 552 */     readPPMatrix(model);
/*  304:     */     
/*  305:     */ 
/*  306: 555 */     readParamMatrix(model);
/*  307:     */   }
/*  308:     */   
/*  309:     */   protected void readParameterList(Element model)
/*  310:     */     throws Exception
/*  311:     */   {
/*  312: 566 */     NodeList paramL = model.getElementsByTagName("ParameterList");
/*  313: 569 */     if (paramL.getLength() == 1)
/*  314:     */     {
/*  315: 570 */       Node paramN = paramL.item(0);
/*  316: 571 */       if (paramN.getNodeType() == 1)
/*  317:     */       {
/*  318: 572 */         NodeList parameterList = ((Element)paramN).getElementsByTagName("Parameter");
/*  319: 573 */         for (int i = 0; i < parameterList.getLength(); i++)
/*  320:     */         {
/*  321: 574 */           Node parameter = parameterList.item(i);
/*  322: 575 */           if (parameter.getNodeType() == 1)
/*  323:     */           {
/*  324: 576 */             Parameter p = new Parameter();
/*  325: 577 */             p.m_name = ((Element)parameter).getAttribute("name");
/*  326: 578 */             String label = ((Element)parameter).getAttribute("label");
/*  327: 579 */             if ((label != null) && (label.length() > 0)) {
/*  328: 580 */               p.m_label = label;
/*  329:     */             }
/*  330: 582 */             this.m_parameterList.add(p);
/*  331:     */           }
/*  332:     */         }
/*  333:     */       }
/*  334:     */     }
/*  335:     */     else
/*  336:     */     {
/*  337: 587 */       throw new Exception("[GeneralRegression] more than one parameter list!");
/*  338:     */     }
/*  339:     */   }
/*  340:     */   
/*  341:     */   protected void readFactorsAndCovariates(Element model, String factorOrCovariate)
/*  342:     */     throws Exception
/*  343:     */   {
/*  344: 603 */     Instances miningSchemaI = this.m_miningSchema.getFieldsAsInstances();
/*  345:     */     
/*  346: 605 */     NodeList factorL = model.getElementsByTagName(factorOrCovariate);
/*  347: 606 */     if (factorL.getLength() == 1)
/*  348:     */     {
/*  349: 607 */       Node factor = factorL.item(0);
/*  350: 608 */       if (factor.getNodeType() == 1)
/*  351:     */       {
/*  352: 609 */         NodeList predL = ((Element)factor).getElementsByTagName("Predictor");
/*  353: 610 */         for (int i = 0; i < predL.getLength(); i++)
/*  354:     */         {
/*  355: 611 */           Node pred = predL.item(i);
/*  356: 612 */           if (pred.getNodeType() == 1)
/*  357:     */           {
/*  358: 613 */             Predictor p = new Predictor();
/*  359: 614 */             p.m_name = ((Element)pred).getAttribute("name");
/*  360:     */             
/*  361: 616 */             boolean found = false;
/*  362: 617 */             for (int j = 0; j < miningSchemaI.numAttributes(); j++) {
/*  363: 618 */               if (miningSchemaI.attribute(j).name().equals(p.m_name))
/*  364:     */               {
/*  365: 619 */                 found = true;
/*  366: 620 */                 p.m_miningSchemaIndex = j;
/*  367: 621 */                 break;
/*  368:     */               }
/*  369:     */             }
/*  370: 624 */             if (found)
/*  371:     */             {
/*  372: 625 */               if (factorOrCovariate.equals("FactorList")) {
/*  373: 626 */                 this.m_factorList.add(p);
/*  374:     */               } else {
/*  375: 628 */                 this.m_covariateList.add(p);
/*  376:     */               }
/*  377:     */             }
/*  378:     */             else {
/*  379: 631 */               throw new Exception("[GeneralRegression] reading factors and covariates - unable to find predictor " + p.m_name + " in the mining schema");
/*  380:     */             }
/*  381:     */           }
/*  382:     */         }
/*  383:     */       }
/*  384:     */     }
/*  385: 638 */     else if (factorL.getLength() > 1)
/*  386:     */     {
/*  387: 639 */       throw new Exception("[GeneralRegression] more than one " + factorOrCovariate + "! ");
/*  388:     */     }
/*  389:     */   }
/*  390:     */   
/*  391:     */   protected void readPPMatrix(Element model)
/*  392:     */     throws Exception
/*  393:     */   {
/*  394: 651 */     Instances miningSchemaI = this.m_miningSchema.getFieldsAsInstances();
/*  395:     */     
/*  396: 653 */     NodeList matrixL = model.getElementsByTagName("PPMatrix");
/*  397: 656 */     if (matrixL.getLength() == 1)
/*  398:     */     {
/*  399: 660 */       this.m_ppMatrix = new PPCell[this.m_parameterList.size()][miningSchemaI.numAttributes()];
/*  400:     */       
/*  401: 662 */       Node ppM = matrixL.item(0);
/*  402: 663 */       if (ppM.getNodeType() == 1)
/*  403:     */       {
/*  404: 664 */         NodeList cellL = ((Element)ppM).getElementsByTagName("PPCell");
/*  405: 665 */         for (int i = 0; i < cellL.getLength(); i++)
/*  406:     */         {
/*  407: 666 */           Node cell = cellL.item(i);
/*  408: 667 */           if (cell.getNodeType() == 1)
/*  409:     */           {
/*  410: 668 */             String predictorName = ((Element)cell).getAttribute("predictorName");
/*  411: 669 */             String parameterName = ((Element)cell).getAttribute("parameterName");
/*  412: 670 */             String value = ((Element)cell).getAttribute("value");
/*  413: 671 */             double expOrIndex = -1.0D;
/*  414: 672 */             int predictorIndex = -1;
/*  415: 673 */             int parameterIndex = -1;
/*  416: 674 */             for (int j = 0; j < this.m_parameterList.size(); j++) {
/*  417: 675 */               if (((Parameter)this.m_parameterList.get(j)).m_name.equals(parameterName))
/*  418:     */               {
/*  419: 676 */                 parameterIndex = j;
/*  420: 677 */                 break;
/*  421:     */               }
/*  422:     */             }
/*  423: 680 */             if (parameterIndex == -1) {
/*  424: 681 */               throw new Exception("[GeneralRegression] unable to find parameter name " + parameterName + " in parameter list");
/*  425:     */             }
/*  426: 685 */             Predictor p = getCovariate(predictorName);
/*  427: 686 */             if (p != null)
/*  428:     */             {
/*  429:     */               try
/*  430:     */               {
/*  431: 688 */                 expOrIndex = Double.parseDouble(value);
/*  432: 689 */                 predictorIndex = p.m_miningSchemaIndex;
/*  433:     */               }
/*  434:     */               catch (IllegalArgumentException ex)
/*  435:     */               {
/*  436: 691 */                 throw new Exception("[GeneralRegression] unable to parse PPCell value: " + value);
/*  437:     */               }
/*  438:     */             }
/*  439:     */             else
/*  440:     */             {
/*  441: 696 */               p = getFactor(predictorName);
/*  442: 697 */               if (p != null)
/*  443:     */               {
/*  444: 701 */                 if (miningSchemaI.attribute(p.m_miningSchemaIndex).isNumeric())
/*  445:     */                 {
/*  446:     */                   try
/*  447:     */                   {
/*  448: 706 */                     expOrIndex = Double.parseDouble(value);
/*  449:     */                   }
/*  450:     */                   catch (IllegalArgumentException ex)
/*  451:     */                   {
/*  452: 708 */                     throw new Exception("[GeneralRegresion] unable to parse PPCell value: " + value);
/*  453:     */                   }
/*  454:     */                 }
/*  455:     */                 else
/*  456:     */                 {
/*  457: 714 */                   Attribute att = miningSchemaI.attribute(p.m_miningSchemaIndex);
/*  458: 715 */                   expOrIndex = att.indexOfValue(value);
/*  459: 716 */                   if (expOrIndex == -1.0D) {
/*  460: 717 */                     throw new Exception("[GeneralRegression] unable to find PPCell value " + value + " in mining schema attribute " + att.name());
/*  461:     */                   }
/*  462:     */                 }
/*  463:     */               }
/*  464:     */               else {
/*  465: 723 */                 throw new Exception("[GeneralRegression] cant find predictor " + predictorName + "in either the factors list " + "or the covariates list");
/*  466:     */               }
/*  467: 727 */               predictorIndex = p.m_miningSchemaIndex;
/*  468:     */             }
/*  469: 731 */             PPCell ppc = new PPCell();
/*  470: 732 */             ppc.m_predictorName = predictorName;ppc.m_parameterName = parameterName;
/*  471: 733 */             ppc.m_value = expOrIndex;
/*  472:     */             
/*  473:     */ 
/*  474: 736 */             this.m_ppMatrix[parameterIndex][predictorIndex] = ppc;
/*  475:     */           }
/*  476:     */         }
/*  477:     */       }
/*  478:     */     }
/*  479:     */     else
/*  480:     */     {
/*  481: 741 */       throw new Exception("[GeneralRegression] more than one PPMatrix!");
/*  482:     */     }
/*  483:     */   }
/*  484:     */   
/*  485:     */   private Predictor getCovariate(String predictorName)
/*  486:     */   {
/*  487: 746 */     for (int i = 0; i < this.m_covariateList.size(); i++) {
/*  488: 747 */       if (predictorName.equals(((Predictor)this.m_covariateList.get(i)).m_name)) {
/*  489: 748 */         return (Predictor)this.m_covariateList.get(i);
/*  490:     */       }
/*  491:     */     }
/*  492: 751 */     return null;
/*  493:     */   }
/*  494:     */   
/*  495:     */   private Predictor getFactor(String predictorName)
/*  496:     */   {
/*  497: 755 */     for (int i = 0; i < this.m_factorList.size(); i++) {
/*  498: 756 */       if (predictorName.equals(((Predictor)this.m_factorList.get(i)).m_name)) {
/*  499: 757 */         return (Predictor)this.m_factorList.get(i);
/*  500:     */       }
/*  501:     */     }
/*  502: 760 */     return null;
/*  503:     */   }
/*  504:     */   
/*  505:     */   private void readParamMatrix(Element model)
/*  506:     */     throws Exception
/*  507:     */   {
/*  508: 772 */     Instances miningSchemaI = this.m_miningSchema.getFieldsAsInstances();
/*  509: 773 */     Attribute classAtt = miningSchemaI.classAttribute();
/*  510:     */     
/*  511:     */ 
/*  512:     */ 
/*  513: 777 */     ArrayList<String> targetVals = null;
/*  514:     */     
/*  515: 779 */     NodeList matrixL = model.getElementsByTagName("ParamMatrix");
/*  516: 780 */     if (matrixL.getLength() != 1) {
/*  517: 781 */       throw new Exception("[GeneralRegression] more than one ParamMatrix!");
/*  518:     */     }
/*  519: 783 */     Element matrix = (Element)matrixL.item(0);
/*  520: 788 */     if ((this.m_functionType == 1) && (classAtt.isNumeric()))
/*  521:     */     {
/*  522: 792 */       if (!this.m_miningSchema.hasTargetMetaData()) {
/*  523: 793 */         throw new Exception("[GeneralRegression] function type is classification and class attribute in mining schema is numeric, however, there is no Target element specifying legal discrete values for the target!");
/*  524:     */       }
/*  525: 800 */       if (this.m_miningSchema.getTargetMetaData().getOptype() != FieldMetaInfo.Optype.CATEGORICAL) {
/*  526: 802 */         throw new Exception("[GeneralRegression] function type is classification and class attribute in mining schema is numeric, however Target element in PMML does not have optype categorical!");
/*  527:     */       }
/*  528: 808 */       targetVals = this.m_miningSchema.getTargetMetaData().getValues();
/*  529: 809 */       if (targetVals.size() == 0) {
/*  530: 810 */         throw new Exception("[GeneralRegression] function type is classification and class attribute in mining schema is numeric, however Target element in PMML does not have any discrete values defined!");
/*  531:     */       }
/*  532: 817 */       this.m_miningSchema.convertNumericAttToNominal(miningSchemaI.classIndex(), targetVals);
/*  533:     */     }
/*  534: 821 */     this.m_paramMatrix = new PCell[classAtt.isNumeric() ? 1 : classAtt.numValues()][this.m_parameterList.size()];
/*  535:     */     
/*  536:     */ 
/*  537:     */ 
/*  538:     */ 
/*  539: 826 */     NodeList pcellL = matrix.getElementsByTagName("PCell");
/*  540: 827 */     for (int i = 0; i < pcellL.getLength(); i++)
/*  541:     */     {
/*  542: 830 */       int targetCategoryIndex = -1;
/*  543: 831 */       int parameterIndex = -1;
/*  544: 832 */       Node pcell = pcellL.item(i);
/*  545: 833 */       if (pcell.getNodeType() == 1)
/*  546:     */       {
/*  547: 834 */         String paramName = ((Element)pcell).getAttribute("parameterName");
/*  548: 835 */         String targetCatName = ((Element)pcell).getAttribute("targetCategory");
/*  549: 836 */         String coefficient = ((Element)pcell).getAttribute("beta");
/*  550: 837 */         String df = ((Element)pcell).getAttribute("df");
/*  551: 839 */         for (int j = 0; j < this.m_parameterList.size(); j++) {
/*  552: 840 */           if (((Parameter)this.m_parameterList.get(j)).m_name.equals(paramName))
/*  553:     */           {
/*  554: 841 */             parameterIndex = j;
/*  555: 843 */             if (((Parameter)this.m_parameterList.get(j)).m_label == null) {
/*  556:     */               break;
/*  557:     */             }
/*  558: 844 */             paramName = ((Parameter)this.m_parameterList.get(j)).m_label; break;
/*  559:     */           }
/*  560:     */         }
/*  561: 849 */         if (parameterIndex == -1) {
/*  562: 850 */           throw new Exception("[GeneralRegression] unable to find parameter name " + paramName + " in parameter list");
/*  563:     */         }
/*  564: 854 */         if ((targetCatName != null) && (targetCatName.length() > 0)) {
/*  565: 855 */           if ((classAtt.isNominal()) || (classAtt.isString())) {
/*  566: 856 */             targetCategoryIndex = classAtt.indexOfValue(targetCatName);
/*  567:     */           } else {
/*  568: 858 */             throw new Exception("[GeneralRegression] found a PCell with a named target category: " + targetCatName + " but class attribute is numeric in " + "mining schema");
/*  569:     */           }
/*  570:     */         }
/*  571: 865 */         PCell p = new PCell();
/*  572: 866 */         if (targetCategoryIndex != -1) {
/*  573: 867 */           p.m_targetCategory = targetCatName;
/*  574:     */         }
/*  575: 869 */         p.m_parameterName = paramName;
/*  576:     */         try
/*  577:     */         {
/*  578: 871 */           p.m_beta = Double.parseDouble(coefficient);
/*  579:     */         }
/*  580:     */         catch (IllegalArgumentException ex)
/*  581:     */         {
/*  582: 873 */           throw new Exception("[GeneralRegression] unable to parse beta value " + coefficient + " as a double from PCell");
/*  583:     */         }
/*  584: 876 */         if ((df != null) && (df.length() > 0)) {
/*  585:     */           try
/*  586:     */           {
/*  587: 878 */             p.m_df = Integer.parseInt(df);
/*  588:     */           }
/*  589:     */           catch (IllegalArgumentException ex)
/*  590:     */           {
/*  591: 880 */             throw new Exception("[GeneralRegression] unable to parse df value " + df + " as an int from PCell");
/*  592:     */           }
/*  593:     */         }
/*  594: 885 */         if (targetCategoryIndex != -1) {
/*  595: 886 */           this.m_paramMatrix[targetCategoryIndex][parameterIndex] = p;
/*  596:     */         } else {
/*  597: 890 */           for (int j = 0; j < this.m_paramMatrix.length; j++) {
/*  598: 891 */             this.m_paramMatrix[j][parameterIndex] = p;
/*  599:     */           }
/*  600:     */         }
/*  601:     */       }
/*  602:     */     }
/*  603:     */   }
/*  604:     */   
/*  605:     */   public String toString()
/*  606:     */   {
/*  607: 904 */     StringBuffer temp = new StringBuffer();
/*  608: 905 */     temp.append("PMML version " + getPMMLVersion());
/*  609: 906 */     if (!getCreatorApplication().equals("?")) {
/*  610: 907 */       temp.append("\nApplication: " + getCreatorApplication());
/*  611:     */     }
/*  612: 909 */     temp.append("\nPMML Model: " + this.m_modelType);
/*  613: 910 */     temp.append("\n\n");
/*  614: 911 */     temp.append(this.m_miningSchema);
/*  615: 913 */     if (this.m_factorList.size() > 0)
/*  616:     */     {
/*  617: 914 */       temp.append("Factors:\n");
/*  618: 915 */       for (Predictor p : this.m_factorList) {
/*  619: 916 */         temp.append("\t" + p + "\n");
/*  620:     */       }
/*  621:     */     }
/*  622: 919 */     temp.append("\n");
/*  623: 920 */     if (this.m_covariateList.size() > 0)
/*  624:     */     {
/*  625: 921 */       temp.append("Covariates:\n");
/*  626: 922 */       for (Predictor p : this.m_covariateList) {
/*  627: 923 */         temp.append("\t" + p + "\n");
/*  628:     */       }
/*  629:     */     }
/*  630: 926 */     temp.append("\n");
/*  631:     */     
/*  632: 928 */     printPPMatrix(temp);
/*  633: 929 */     temp.append("\n");
/*  634: 930 */     printParameterMatrix(temp);
/*  635:     */     
/*  636:     */ 
/*  637: 933 */     temp.append("\n");
/*  638: 935 */     if (this.m_linkFunction != LinkFunction.NONE)
/*  639:     */     {
/*  640: 936 */       temp.append("Link function: " + this.m_linkFunction);
/*  641: 937 */       if (this.m_offsetVariable != null) {
/*  642: 938 */         temp.append("\n\tOffset variable " + this.m_offsetVariable);
/*  643: 939 */       } else if (!Double.isNaN(this.m_offsetValue)) {
/*  644: 940 */         temp.append("\n\tOffset value " + this.m_offsetValue);
/*  645:     */       }
/*  646: 943 */       if (this.m_trialsVariable != null) {
/*  647: 944 */         temp.append("\n\tTrials variable " + this.m_trialsVariable);
/*  648: 945 */       } else if (!Double.isNaN(this.m_trialsValue)) {
/*  649: 946 */         temp.append("\n\tTrials value " + this.m_trialsValue);
/*  650:     */       }
/*  651: 949 */       if (this.m_distribution != Distribution.NONE) {
/*  652: 950 */         temp.append("\nDistribution: " + this.m_distribution);
/*  653:     */       }
/*  654: 953 */       if ((this.m_linkFunction == LinkFunction.NEGBIN) && (this.m_distribution == Distribution.NEGBINOMIAL) && (!Double.isNaN(this.m_distParameter))) {
/*  655: 956 */         temp.append("\n\tDistribution parameter " + this.m_distParameter);
/*  656:     */       }
/*  657: 959 */       if ((this.m_linkFunction == LinkFunction.POWER) || (this.m_linkFunction == LinkFunction.ODDSPOWER)) {
/*  658: 961 */         if (!Double.isNaN(this.m_linkParameter)) {
/*  659: 962 */           temp.append("\n\nLink parameter " + this.m_linkParameter);
/*  660:     */         }
/*  661:     */       }
/*  662:     */     }
/*  663: 967 */     if (this.m_cumulativeLinkFunction != CumulativeLinkFunction.NONE)
/*  664:     */     {
/*  665: 968 */       temp.append("Cumulative link function: " + this.m_cumulativeLinkFunction);
/*  666: 970 */       if (this.m_offsetVariable != null) {
/*  667: 971 */         temp.append("\n\tOffset variable " + this.m_offsetVariable);
/*  668: 972 */       } else if (!Double.isNaN(this.m_offsetValue)) {
/*  669: 973 */         temp.append("\n\tOffset value " + this.m_offsetValue);
/*  670:     */       }
/*  671:     */     }
/*  672: 976 */     temp.append("\n");
/*  673:     */     
/*  674: 978 */     return temp.toString();
/*  675:     */   }
/*  676:     */   
/*  677:     */   protected void printPPMatrix(StringBuffer buff)
/*  678:     */   {
/*  679: 987 */     Instances miningSchemaI = this.m_miningSchema.getFieldsAsInstances();
/*  680: 988 */     int maxAttWidth = 0;
/*  681: 989 */     for (int i = 0; i < miningSchemaI.numAttributes(); i++)
/*  682:     */     {
/*  683: 990 */       Attribute a = miningSchemaI.attribute(i);
/*  684: 991 */       if (a.name().length() > maxAttWidth) {
/*  685: 992 */         maxAttWidth = a.name().length();
/*  686:     */       }
/*  687:     */     }
/*  688: 997 */     for (int i = 0; i < this.m_parameterList.size(); i++) {
/*  689: 998 */       for (int j = 0; j < miningSchemaI.numAttributes(); j++) {
/*  690: 999 */         if (this.m_ppMatrix[i][j] != null)
/*  691:     */         {
/*  692:1000 */           double width = Math.log(Math.abs(this.m_ppMatrix[i][j].m_value)) / Math.log(10.0D);
/*  693:1002 */           if (width < 0.0D) {
/*  694:1003 */             width = 1.0D;
/*  695:     */           }
/*  696:1006 */           width += 2.0D;
/*  697:1007 */           if ((int)width > maxAttWidth) {
/*  698:1008 */             maxAttWidth = (int)width;
/*  699:     */           }
/*  700:1010 */           if ((miningSchemaI.attribute(j).isNominal()) || (miningSchemaI.attribute(j).isString()))
/*  701:     */           {
/*  702:1013 */             String val = miningSchemaI.attribute(j).value((int)this.m_ppMatrix[i][j].m_value) + " ";
/*  703:1014 */             if (val.length() > maxAttWidth) {
/*  704:1015 */               maxAttWidth = val.length();
/*  705:     */             }
/*  706:     */           }
/*  707:     */         }
/*  708:     */       }
/*  709:     */     }
/*  710:1023 */     int maxParamWidth = "Parameter  ".length();
/*  711:1024 */     for (Parameter p : this.m_parameterList)
/*  712:     */     {
/*  713:1025 */       String temp = p.m_name + " ";
/*  714:1029 */       if (temp.length() > maxParamWidth) {
/*  715:1030 */         maxParamWidth = temp.length();
/*  716:     */       }
/*  717:     */     }
/*  718:1034 */     buff.append("Predictor-to-Parameter matrix:\n");
/*  719:1035 */     buff.append(PMMLUtils.pad("Predictor", " ", maxParamWidth + (maxAttWidth * 2 + 2) - "Predictor".length(), true));
/*  720:     */     
/*  721:1037 */     buff.append("\n" + PMMLUtils.pad("Parameter", " ", maxParamWidth - "Parameter".length(), false));
/*  722:1039 */     for (int i = 0; i < miningSchemaI.numAttributes(); i++) {
/*  723:1040 */       if (i != miningSchemaI.classIndex())
/*  724:     */       {
/*  725:1041 */         String attName = miningSchemaI.attribute(i).name();
/*  726:1042 */         buff.append(PMMLUtils.pad(attName, " ", maxAttWidth + 1 - attName.length(), true));
/*  727:     */       }
/*  728:     */     }
/*  729:1045 */     buff.append("\n");
/*  730:1047 */     for (int i = 0; i < this.m_parameterList.size(); i++)
/*  731:     */     {
/*  732:1048 */       Parameter param = (Parameter)this.m_parameterList.get(i);
/*  733:1049 */       String paramS = param.m_label != null ? param.m_label : param.m_name;
/*  734:     */       
/*  735:     */ 
/*  736:1052 */       buff.append(PMMLUtils.pad(paramS, " ", maxParamWidth - paramS.length(), false));
/*  737:1054 */       for (int j = 0; j < miningSchemaI.numAttributes(); j++) {
/*  738:1055 */         if (j != miningSchemaI.classIndex())
/*  739:     */         {
/*  740:1056 */           PPCell p = this.m_ppMatrix[i][j];
/*  741:1057 */           String val = " ";
/*  742:1058 */           if (p != null) {
/*  743:1059 */             if ((miningSchemaI.attribute(j).isNominal()) || (miningSchemaI.attribute(j).isString())) {
/*  744:1061 */               val = miningSchemaI.attribute(j).value((int)p.m_value);
/*  745:     */             } else {
/*  746:1063 */               val = "" + Utils.doubleToString(p.m_value, maxAttWidth, 4).trim();
/*  747:     */             }
/*  748:     */           }
/*  749:1066 */           buff.append(PMMLUtils.pad(val, " ", maxAttWidth + 1 - val.length(), true));
/*  750:     */         }
/*  751:     */       }
/*  752:1069 */       buff.append("\n");
/*  753:     */     }
/*  754:     */   }
/*  755:     */   
/*  756:     */   protected void printParameterMatrix(StringBuffer buff)
/*  757:     */   {
/*  758:1079 */     Instances miningSchemaI = this.m_miningSchema.getFieldsAsInstances();
/*  759:     */     
/*  760:     */ 
/*  761:1082 */     int maxClassWidth = miningSchemaI.classAttribute().name().length();
/*  762:1083 */     if ((miningSchemaI.classAttribute().isNominal()) || (miningSchemaI.classAttribute().isString())) {
/*  763:1085 */       for (int i = 0; i < miningSchemaI.classAttribute().numValues(); i++) {
/*  764:1086 */         if (miningSchemaI.classAttribute().value(i).length() > maxClassWidth) {
/*  765:1087 */           maxClassWidth = miningSchemaI.classAttribute().value(i).length();
/*  766:     */         }
/*  767:     */       }
/*  768:     */     }
/*  769:1093 */     int maxParamWidth = 0;
/*  770:1094 */     for (int i = 0; i < this.m_parameterList.size(); i++)
/*  771:     */     {
/*  772:1095 */       Parameter p = (Parameter)this.m_parameterList.get(i);
/*  773:1096 */       String val = p.m_name + " ";
/*  774:1099 */       if (val.length() > maxParamWidth) {
/*  775:1100 */         maxParamWidth = val.length();
/*  776:     */       }
/*  777:     */     }
/*  778:1105 */     int maxBetaWidth = "Coeff.".length();
/*  779:1106 */     for (int i = 0; i < this.m_paramMatrix.length; i++) {
/*  780:1107 */       for (int j = 0; j < this.m_parameterList.size(); j++)
/*  781:     */       {
/*  782:1108 */         PCell p = this.m_paramMatrix[i][j];
/*  783:1109 */         if (p != null)
/*  784:     */         {
/*  785:1110 */           double width = Math.log(Math.abs(p.m_beta)) / Math.log(10.0D);
/*  786:1111 */           if (width < 0.0D) {
/*  787:1112 */             width = 1.0D;
/*  788:     */           }
/*  789:1115 */           width += 7.0D;
/*  790:1116 */           if ((int)width > maxBetaWidth) {
/*  791:1117 */             maxBetaWidth = (int)width;
/*  792:     */           }
/*  793:     */         }
/*  794:     */       }
/*  795:     */     }
/*  796:1123 */     buff.append("Parameter estimates:\n");
/*  797:1124 */     buff.append(PMMLUtils.pad(miningSchemaI.classAttribute().name(), " ", maxClassWidth + maxParamWidth + 2 - miningSchemaI.classAttribute().name().length(), false));
/*  798:     */     
/*  799:     */ 
/*  800:1127 */     buff.append(PMMLUtils.pad("Coeff.", " ", maxBetaWidth + 1 - "Coeff.".length(), true));
/*  801:1128 */     buff.append(PMMLUtils.pad("df", " ", maxBetaWidth - "df".length(), true));
/*  802:1129 */     buff.append("\n");
/*  803:1130 */     for (int i = 0; i < this.m_paramMatrix.length; i++)
/*  804:     */     {
/*  805:1132 */       boolean ok = false;
/*  806:1133 */       for (int j = 0; j < this.m_parameterList.size(); j++) {
/*  807:1134 */         if (this.m_paramMatrix[i][j] != null) {
/*  808:1135 */           ok = true;
/*  809:     */         }
/*  810:     */       }
/*  811:1138 */       if (ok)
/*  812:     */       {
/*  813:1142 */         String cVal = (miningSchemaI.classAttribute().isNominal()) || (miningSchemaI.classAttribute().isString()) ? miningSchemaI.classAttribute().value(i) : " ";
/*  814:     */         
/*  815:     */ 
/*  816:     */ 
/*  817:1146 */         buff.append(PMMLUtils.pad(cVal, " ", maxClassWidth - cVal.length(), false));
/*  818:1147 */         buff.append("\n");
/*  819:1148 */         for (int j = 0; j < this.m_parameterList.size(); j++)
/*  820:     */         {
/*  821:1149 */           PCell p = this.m_paramMatrix[i][j];
/*  822:1150 */           if (p != null)
/*  823:     */           {
/*  824:1151 */             String label = p.m_parameterName;
/*  825:1152 */             buff.append(PMMLUtils.pad(label, " ", maxClassWidth + maxParamWidth + 2 - label.length(), true));
/*  826:     */             
/*  827:1154 */             String betaS = Utils.doubleToString(p.m_beta, maxBetaWidth, 4).trim();
/*  828:1155 */             buff.append(PMMLUtils.pad(betaS, " ", maxBetaWidth + 1 - betaS.length(), true));
/*  829:1156 */             String dfS = Utils.doubleToString(p.m_df, maxBetaWidth, 4).trim();
/*  830:1157 */             buff.append(PMMLUtils.pad(dfS, " ", maxBetaWidth - dfS.length(), true));
/*  831:1158 */             buff.append("\n");
/*  832:     */           }
/*  833:     */         }
/*  834:     */       }
/*  835:     */     }
/*  836:     */   }
/*  837:     */   
/*  838:     */   private double[] incomingParamVector(double[] incomingInst)
/*  839:     */     throws Exception
/*  840:     */   {
/*  841:1175 */     Instances miningSchemaI = this.m_miningSchema.getFieldsAsInstances();
/*  842:1176 */     double[] incomingPV = new double[this.m_parameterList.size()];
/*  843:1178 */     for (int i = 0; i < this.m_parameterList.size(); i++)
/*  844:     */     {
/*  845:1182 */       incomingPV[i] = 1.0D;
/*  846:1185 */       for (int j = 0; j < miningSchemaI.numAttributes(); j++)
/*  847:     */       {
/*  848:1186 */         PPCell cellEntry = this.m_ppMatrix[i][j];
/*  849:1187 */         Predictor p = null;
/*  850:1188 */         if (cellEntry != null) {
/*  851:1189 */           if ((p = getFactor(cellEntry.m_predictorName)) != null)
/*  852:     */           {
/*  853:1190 */             if ((int)incomingInst[p.m_miningSchemaIndex] == (int)cellEntry.m_value) {
/*  854:1191 */               incomingPV[i] *= 1.0D;
/*  855:     */             } else {
/*  856:1193 */               incomingPV[i] *= 0.0D;
/*  857:     */             }
/*  858:     */           }
/*  859:1195 */           else if ((p = getCovariate(cellEntry.m_predictorName)) != null) {
/*  860:1196 */             incomingPV[i] *= Math.pow(incomingInst[p.m_miningSchemaIndex], cellEntry.m_value);
/*  861:     */           } else {
/*  862:1198 */             throw new Exception("[GeneralRegression] can't find predictor " + cellEntry.m_predictorName + " in either the list of factors or covariates");
/*  863:     */           }
/*  864:     */         }
/*  865:     */       }
/*  866:     */     }
/*  867:1205 */     return incomingPV;
/*  868:     */   }
/*  869:     */   
/*  870:     */   public double[] distributionForInstance(Instance inst)
/*  871:     */     throws Exception
/*  872:     */   {
/*  873:1218 */     if (!this.m_initialized) {
/*  874:1219 */       mapToMiningSchema(inst.dataset());
/*  875:     */     }
/*  876:1221 */     double[] preds = null;
/*  877:1222 */     if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric()) {
/*  878:1223 */       preds = new double[1];
/*  879:     */     } else {
/*  880:1225 */       preds = new double[this.m_miningSchema.getFieldsAsInstances().classAttribute().numValues()];
/*  881:     */     }
/*  882:1231 */     double[] incoming = this.m_fieldsMap.instanceToSchema(inst, this.m_miningSchema);
/*  883:     */     
/*  884:     */ 
/*  885:     */ 
/*  886:     */ 
/*  887:     */ 
/*  888:1237 */     boolean hasMissing = false;
/*  889:1238 */     for (int i = 0; i < incoming.length; i++) {
/*  890:1239 */       if ((i != this.m_miningSchema.getFieldsAsInstances().classIndex()) && (Double.isNaN(incoming[i])))
/*  891:     */       {
/*  892:1241 */         hasMissing = true;
/*  893:1242 */         break;
/*  894:     */       }
/*  895:     */     }
/*  896:1246 */     if (hasMissing)
/*  897:     */     {
/*  898:1247 */       if (!this.m_miningSchema.hasTargetMetaData())
/*  899:     */       {
/*  900:1248 */         String message = "[GeneralRegression] WARNING: Instance to predict has missing value(s) but there is no missing value handling meta data and no prior probabilities/default value to fall back to. No prediction will be made (" + ((this.m_miningSchema.getFieldsAsInstances().classAttribute().isNominal()) || (this.m_miningSchema.getFieldsAsInstances().classAttribute().isString()) ? "zero probabilities output)." : "NaN output).");
/*  901:1256 */         if (this.m_log == null) {
/*  902:1257 */           System.err.println(message);
/*  903:     */         } else {
/*  904:1259 */           this.m_log.logMessage(message);
/*  905:     */         }
/*  906:1262 */         if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric()) {
/*  907:1263 */           preds[0] = Utils.missingValue();
/*  908:     */         }
/*  909:1265 */         return preds;
/*  910:     */       }
/*  911:1268 */       TargetMetaInfo targetData = this.m_miningSchema.getTargetMetaData();
/*  912:1269 */       if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric())
/*  913:     */       {
/*  914:1270 */         preds[0] = targetData.getDefaultValue();
/*  915:     */       }
/*  916:     */       else
/*  917:     */       {
/*  918:1272 */         Instances miningSchemaI = this.m_miningSchema.getFieldsAsInstances();
/*  919:1273 */         for (int i = 0; i < miningSchemaI.classAttribute().numValues(); i++) {
/*  920:1274 */           preds[i] = targetData.getPriorProbability(miningSchemaI.classAttribute().value(i));
/*  921:     */         }
/*  922:     */       }
/*  923:1277 */       return preds;
/*  924:     */     }
/*  925:1281 */     double[] inputParamVector = incomingParamVector(incoming);
/*  926:1282 */     computeResponses(incoming, inputParamVector, preds);
/*  927:     */     
/*  928:     */ 
/*  929:1285 */     return preds;
/*  930:     */   }
/*  931:     */   
/*  932:     */   private void computeResponses(double[] incomingInst, double[] incomingParamVector, double[] responses)
/*  933:     */     throws Exception
/*  934:     */   {
/*  935:1301 */     for (int i = 0; i < responses.length; i++) {
/*  936:1302 */       for (int j = 0; j < this.m_parameterList.size(); j++)
/*  937:     */       {
/*  938:1307 */         PCell p = this.m_paramMatrix[i][j];
/*  939:1308 */         if (p == null) {
/*  940:1309 */           responses[i] += 0.0D * incomingParamVector[j];
/*  941:     */         } else {
/*  942:1311 */           responses[i] += incomingParamVector[j] * p.m_beta;
/*  943:     */         }
/*  944:     */       }
/*  945:     */     }
/*  946:1316 */     switch (1.$SwitchMap$weka$classifiers$pmml$consumer$GeneralRegression$ModelType[this.m_modelType.ordinal()])
/*  947:     */     {
/*  948:     */     case 1: 
/*  949:1318 */       computeProbabilitiesMultinomialLogistic(responses);
/*  950:1319 */       break;
/*  951:     */     case 2: 
/*  952:     */       break;
/*  953:     */     case 3: 
/*  954:     */     case 4: 
/*  955:1325 */       if (this.m_linkFunction != LinkFunction.NONE) {
/*  956:1326 */         computeResponseGeneralizedLinear(incomingInst, responses);
/*  957:     */       } else {
/*  958:1328 */         throw new Exception("[GeneralRegression] no link function specified!");
/*  959:     */       }
/*  960:     */       break;
/*  961:     */     case 5: 
/*  962:1332 */       if (this.m_cumulativeLinkFunction != CumulativeLinkFunction.NONE) {
/*  963:1333 */         computeResponseOrdinalMultinomial(incomingInst, responses);
/*  964:     */       } else {
/*  965:1335 */         throw new Exception("[GeneralRegression] no cumulative link function specified!");
/*  966:     */       }
/*  967:     */       break;
/*  968:     */     default: 
/*  969:1339 */       throw new Exception("[GeneralRegression] unknown model type");
/*  970:     */     }
/*  971:     */   }
/*  972:     */   
/*  973:     */   private static void computeProbabilitiesMultinomialLogistic(double[] responses)
/*  974:     */   {
/*  975:1349 */     double[] r = (double[])responses.clone();
/*  976:1350 */     for (int j = 0; j < r.length; j++)
/*  977:     */     {
/*  978:1351 */       double sum = 0.0D;
/*  979:1352 */       boolean overflow = false;
/*  980:1353 */       for (int k = 0; k < r.length; k++)
/*  981:     */       {
/*  982:1354 */         if (r[k] - r[j] > 700.0D)
/*  983:     */         {
/*  984:1355 */           overflow = true;
/*  985:1356 */           break;
/*  986:     */         }
/*  987:1358 */         sum += Math.exp(r[k] - r[j]);
/*  988:     */       }
/*  989:1360 */       if (overflow) {
/*  990:1361 */         responses[j] = 0.0D;
/*  991:     */       } else {
/*  992:1363 */         responses[j] = (1.0D / sum);
/*  993:     */       }
/*  994:     */     }
/*  995:     */   }
/*  996:     */   
/*  997:     */   private void computeResponseGeneralizedLinear(double[] incomingInst, double[] responses)
/*  998:     */     throws Exception
/*  999:     */   {
/* 1000:1380 */     double[] r = (double[])responses.clone();
/* 1001:     */     
/* 1002:1382 */     double offset = 0.0D;
/* 1003:1383 */     if (this.m_offsetVariable != null)
/* 1004:     */     {
/* 1005:1384 */       Attribute offsetAtt = this.m_miningSchema.getFieldsAsInstances().attribute(this.m_offsetVariable);
/* 1006:1386 */       if (offsetAtt == null) {
/* 1007:1387 */         throw new Exception("[GeneralRegression] unable to find offset variable " + this.m_offsetVariable + " in the mining schema!");
/* 1008:     */       }
/* 1009:1390 */       offset = incomingInst[offsetAtt.index()];
/* 1010:     */     }
/* 1011:1391 */     else if (!Double.isNaN(this.m_offsetValue))
/* 1012:     */     {
/* 1013:1392 */       offset = this.m_offsetValue;
/* 1014:     */     }
/* 1015:1395 */     double trials = 1.0D;
/* 1016:1396 */     if (this.m_trialsVariable != null)
/* 1017:     */     {
/* 1018:1397 */       Attribute trialsAtt = this.m_miningSchema.getFieldsAsInstances().attribute(this.m_trialsVariable);
/* 1019:1398 */       if (trialsAtt == null) {
/* 1020:1399 */         throw new Exception("[GeneralRegression] unable to find trials variable " + this.m_trialsVariable + " in the mining schema!");
/* 1021:     */       }
/* 1022:1402 */       trials = incomingInst[trialsAtt.index()];
/* 1023:     */     }
/* 1024:1403 */     else if (!Double.isNaN(this.m_trialsValue))
/* 1025:     */     {
/* 1026:1404 */       trials = this.m_trialsValue;
/* 1027:     */     }
/* 1028:1407 */     double distParam = 0.0D;
/* 1029:1408 */     if ((this.m_linkFunction == LinkFunction.NEGBIN) && (this.m_distribution == Distribution.NEGBINOMIAL))
/* 1030:     */     {
/* 1031:1410 */       if (Double.isNaN(this.m_distParameter)) {
/* 1032:1411 */         throw new Exception("[GeneralRegression] no distribution parameter defined!");
/* 1033:     */       }
/* 1034:1413 */       distParam = this.m_distParameter;
/* 1035:     */     }
/* 1036:1416 */     double linkParam = 0.0D;
/* 1037:1417 */     if ((this.m_linkFunction == LinkFunction.POWER) || (this.m_linkFunction == LinkFunction.ODDSPOWER))
/* 1038:     */     {
/* 1039:1419 */       if (Double.isNaN(this.m_linkParameter)) {
/* 1040:1420 */         throw new Exception("[GeneralRegression] no link parameter defined!");
/* 1041:     */       }
/* 1042:1422 */       linkParam = this.m_linkParameter;
/* 1043:     */     }
/* 1044:1425 */     for (int i = 0; i < r.length; i++) {
/* 1045:1426 */       responses[i] = this.m_linkFunction.eval(r[i], offset, trials, distParam, linkParam);
/* 1046:     */     }
/* 1047:     */   }
/* 1048:     */   
/* 1049:     */   private void computeResponseOrdinalMultinomial(double[] incomingInst, double[] responses)
/* 1050:     */     throws Exception
/* 1051:     */   {
/* 1052:1441 */     double[] r = (double[])responses.clone();
/* 1053:     */     
/* 1054:1443 */     double offset = 0.0D;
/* 1055:1444 */     if (this.m_offsetVariable != null)
/* 1056:     */     {
/* 1057:1445 */       Attribute offsetAtt = this.m_miningSchema.getFieldsAsInstances().attribute(this.m_offsetVariable);
/* 1058:1447 */       if (offsetAtt == null) {
/* 1059:1448 */         throw new Exception("[GeneralRegression] unable to find offset variable " + this.m_offsetVariable + " in the mining schema!");
/* 1060:     */       }
/* 1061:1451 */       offset = incomingInst[offsetAtt.index()];
/* 1062:     */     }
/* 1063:1452 */     else if (!Double.isNaN(this.m_offsetValue))
/* 1064:     */     {
/* 1065:1453 */       offset = this.m_offsetValue;
/* 1066:     */     }
/* 1067:1456 */     for (int i = 0; i < r.length; i++) {
/* 1068:1457 */       if (i == 0) {
/* 1069:1458 */         responses[i] = this.m_cumulativeLinkFunction.eval(r[i], offset);
/* 1070:1460 */       } else if (i == r.length - 1) {
/* 1071:1461 */         responses[i] = (1.0D - responses[(i - 1)]);
/* 1072:     */       } else {
/* 1073:1463 */         responses[i] = (this.m_cumulativeLinkFunction.eval(r[i], offset) - responses[(i - 1)]);
/* 1074:     */       }
/* 1075:     */     }
/* 1076:     */   }
/* 1077:     */   
/* 1078:     */   public String getRevision()
/* 1079:     */   {
/* 1080:1472 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 1081:     */   }
/* 1082:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.pmml.consumer.GeneralRegression
 * JD-Core Version:    0.7.0.1
 */