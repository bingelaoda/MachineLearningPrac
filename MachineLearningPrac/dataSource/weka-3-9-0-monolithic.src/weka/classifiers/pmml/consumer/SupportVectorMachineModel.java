/*    1:     */ package weka.classifiers.pmml.consumer;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.List;
/*    7:     */ import org.w3c.dom.Element;
/*    8:     */ import org.w3c.dom.Node;
/*    9:     */ import org.w3c.dom.NodeList;
/*   10:     */ import weka.core.Attribute;
/*   11:     */ import weka.core.Instance;
/*   12:     */ import weka.core.Instances;
/*   13:     */ import weka.core.RevisionUtils;
/*   14:     */ import weka.core.Utils;
/*   15:     */ import weka.core.pmml.MappingInfo;
/*   16:     */ import weka.core.pmml.MiningSchema;
/*   17:     */ import weka.core.pmml.TargetMetaInfo;
/*   18:     */ import weka.core.pmml.VectorDictionary;
/*   19:     */ import weka.core.pmml.VectorInstance;
/*   20:     */ import weka.gui.Logger;
/*   21:     */ 
/*   22:     */ public class SupportVectorMachineModel
/*   23:     */   extends PMMLClassifier
/*   24:     */   implements Serializable
/*   25:     */ {
/*   26:     */   private static final long serialVersionUID = 6225095165118374296L;
/*   27:     */   
/*   28:     */   static abstract class Kernel
/*   29:     */     implements Serializable
/*   30:     */   {
/*   31:  62 */     protected Logger m_log = null;
/*   32:     */     private static final long serialVersionUID = -6696443459968934767L;
/*   33:     */     
/*   34:     */     protected Kernel(Logger log)
/*   35:     */     {
/*   36:  65 */       this.m_log = log;
/*   37:     */     }
/*   38:     */     
/*   39:     */     public abstract double evaluate(VectorInstance paramVectorInstance1, VectorInstance paramVectorInstance2)
/*   40:     */       throws Exception;
/*   41:     */     
/*   42:     */     public abstract double evaluate(VectorInstance paramVectorInstance, double[] paramArrayOfDouble)
/*   43:     */       throws Exception;
/*   44:     */     
/*   45:     */     public static Kernel getKernel(Element svmMachineModelElement, Logger log)
/*   46:     */       throws Exception
/*   47:     */     {
/*   48: 106 */       NodeList kList = svmMachineModelElement.getElementsByTagName("LinearKernelType");
/*   49: 107 */       if (kList.getLength() > 0) {
/*   50: 108 */         return new SupportVectorMachineModel.LinearKernel(log);
/*   51:     */       }
/*   52: 111 */       kList = svmMachineModelElement.getElementsByTagName("PolynomialKernelType");
/*   53: 112 */       if (kList.getLength() > 0) {
/*   54: 113 */         return new SupportVectorMachineModel.PolynomialKernel((Element)kList.item(0), log);
/*   55:     */       }
/*   56: 116 */       kList = svmMachineModelElement.getElementsByTagName("RadialBasisKernelType");
/*   57: 117 */       if (kList.getLength() > 0) {
/*   58: 118 */         return new SupportVectorMachineModel.RadialBasisKernel((Element)kList.item(0), log);
/*   59:     */       }
/*   60: 121 */       kList = svmMachineModelElement.getElementsByTagName("SigmoidKernelType");
/*   61: 122 */       if (kList.getLength() > 0) {
/*   62: 123 */         return new SupportVectorMachineModel.SigmoidKernel((Element)kList.item(0), log);
/*   63:     */       }
/*   64: 126 */       throw new Exception("[Kernel] Can't find a kernel that I recognize!");
/*   65:     */     }
/*   66:     */   }
/*   67:     */   
/*   68:     */   static class LinearKernel
/*   69:     */     extends SupportVectorMachineModel.Kernel
/*   70:     */     implements Serializable
/*   71:     */   {
/*   72:     */     private static final long serialVersionUID = 8991716708484953837L;
/*   73:     */     
/*   74:     */     public LinearKernel(Logger log)
/*   75:     */     {
/*   76: 136 */       super();
/*   77:     */     }
/*   78:     */     
/*   79:     */     public LinearKernel()
/*   80:     */     {
/*   81: 140 */       super();
/*   82:     */     }
/*   83:     */     
/*   84:     */     public double evaluate(VectorInstance x, VectorInstance y)
/*   85:     */       throws Exception
/*   86:     */     {
/*   87: 156 */       return x.dotProduct(y);
/*   88:     */     }
/*   89:     */     
/*   90:     */     public double evaluate(VectorInstance x, double[] y)
/*   91:     */       throws Exception
/*   92:     */     {
/*   93: 169 */       return x.dotProduct(y);
/*   94:     */     }
/*   95:     */     
/*   96:     */     public String toString()
/*   97:     */     {
/*   98: 178 */       return "Linear kernel: K(x,y) = <x,y>";
/*   99:     */     }
/*  100:     */   }
/*  101:     */   
/*  102:     */   static class PolynomialKernel
/*  103:     */     extends SupportVectorMachineModel.Kernel
/*  104:     */     implements Serializable
/*  105:     */   {
/*  106:     */     private static final long serialVersionUID = -616176630397865281L;
/*  107: 190 */     protected double m_gamma = 1.0D;
/*  108: 191 */     protected double m_coef0 = 1.0D;
/*  109: 192 */     protected double m_degree = 1.0D;
/*  110:     */     
/*  111:     */     public PolynomialKernel(Element polyNode)
/*  112:     */     {
/*  113: 195 */       this(polyNode, null);
/*  114:     */     }
/*  115:     */     
/*  116:     */     public PolynomialKernel(Element polyNode, Logger log)
/*  117:     */     {
/*  118: 199 */       super();
/*  119:     */       
/*  120: 201 */       String gammaString = polyNode.getAttribute("gamma");
/*  121: 202 */       if ((gammaString != null) && (gammaString.length() > 0)) {
/*  122:     */         try
/*  123:     */         {
/*  124: 204 */           this.m_gamma = Double.parseDouble(gammaString);
/*  125:     */         }
/*  126:     */         catch (NumberFormatException e)
/*  127:     */         {
/*  128: 206 */           String message = "[PolynomialKernel] : WARNING, can't parse gamma attribute. Using default value of 1.";
/*  129: 208 */           if (this.m_log == null) {
/*  130: 209 */             System.err.println(message);
/*  131:     */           } else {
/*  132: 211 */             this.m_log.logMessage(message);
/*  133:     */           }
/*  134:     */         }
/*  135:     */       }
/*  136: 216 */       String coefString = polyNode.getAttribute("coef0");
/*  137: 217 */       if ((coefString != null) && (coefString.length() > 0)) {
/*  138:     */         try
/*  139:     */         {
/*  140: 219 */           this.m_coef0 = Double.parseDouble(coefString);
/*  141:     */         }
/*  142:     */         catch (NumberFormatException e)
/*  143:     */         {
/*  144: 221 */           String message = "[PolynomialKernel] : WARNING, can't parse coef0 attribute. Using default value of 1.";
/*  145: 223 */           if (this.m_log == null) {
/*  146: 224 */             System.err.println(message);
/*  147:     */           } else {
/*  148: 226 */             this.m_log.logMessage(message);
/*  149:     */           }
/*  150:     */         }
/*  151:     */       }
/*  152: 231 */       String degreeString = polyNode.getAttribute("degree");
/*  153: 232 */       if ((degreeString != null) && (degreeString.length() > 0)) {
/*  154:     */         try
/*  155:     */         {
/*  156: 234 */           this.m_degree = Double.parseDouble(degreeString);
/*  157:     */         }
/*  158:     */         catch (NumberFormatException e)
/*  159:     */         {
/*  160: 236 */           String message = "[PolynomialKernel] : WARNING, can't parse degree attribute. Using default value of 1.";
/*  161: 238 */           if (this.m_log == null) {
/*  162: 239 */             System.err.println(message);
/*  163:     */           } else {
/*  164: 241 */             this.m_log.logMessage(message);
/*  165:     */           }
/*  166:     */         }
/*  167:     */       }
/*  168:     */     }
/*  169:     */     
/*  170:     */     public double evaluate(VectorInstance x, VectorInstance y)
/*  171:     */       throws Exception
/*  172:     */     {
/*  173: 257 */       double dotProd = x.dotProduct(y);
/*  174: 258 */       return Math.pow(this.m_gamma * dotProd + this.m_coef0, this.m_degree);
/*  175:     */     }
/*  176:     */     
/*  177:     */     public double evaluate(VectorInstance x, double[] y)
/*  178:     */       throws Exception
/*  179:     */     {
/*  180: 271 */       double dotProd = x.dotProduct(y);
/*  181: 272 */       return Math.pow(this.m_gamma * dotProd + this.m_coef0, this.m_degree);
/*  182:     */     }
/*  183:     */     
/*  184:     */     public String toString()
/*  185:     */     {
/*  186: 281 */       return "Polynomial kernel: K(x,y) = (" + this.m_gamma + " * <x,y> + " + this.m_coef0 + ")^" + this.m_degree;
/*  187:     */     }
/*  188:     */   }
/*  189:     */   
/*  190:     */   static class RadialBasisKernel
/*  191:     */     extends SupportVectorMachineModel.Kernel
/*  192:     */     implements Serializable
/*  193:     */   {
/*  194:     */     private static final long serialVersionUID = -3834238621822239042L;
/*  195: 294 */     protected double m_gamma = 1.0D;
/*  196:     */     
/*  197:     */     public RadialBasisKernel(Element radialElement)
/*  198:     */     {
/*  199: 297 */       this(radialElement, null);
/*  200:     */     }
/*  201:     */     
/*  202:     */     public RadialBasisKernel(Element radialElement, Logger log)
/*  203:     */     {
/*  204: 301 */       super();
/*  205:     */       
/*  206: 303 */       String gammaString = radialElement.getAttribute("gamma");
/*  207: 304 */       if ((gammaString != null) && (gammaString.length() > 0)) {
/*  208:     */         try
/*  209:     */         {
/*  210: 306 */           this.m_gamma = Double.parseDouble(gammaString);
/*  211:     */         }
/*  212:     */         catch (NumberFormatException e)
/*  213:     */         {
/*  214: 308 */           String message = "[RadialBasisKernel] : WARNING, can't parse gamma attribute. Using default value of 1.";
/*  215: 310 */           if (this.m_log == null) {
/*  216: 311 */             System.err.println(message);
/*  217:     */           } else {
/*  218: 313 */             this.m_log.logMessage(message);
/*  219:     */           }
/*  220:     */         }
/*  221:     */       }
/*  222:     */     }
/*  223:     */     
/*  224:     */     public double evaluate(VectorInstance x, VectorInstance y)
/*  225:     */       throws Exception
/*  226:     */     {
/*  227: 329 */       VectorInstance diff = x.subtract(y);
/*  228: 330 */       double result = -this.m_gamma * diff.dotProduct(diff);
/*  229:     */       
/*  230: 332 */       return Math.exp(result);
/*  231:     */     }
/*  232:     */     
/*  233:     */     public double evaluate(VectorInstance x, double[] y)
/*  234:     */       throws Exception
/*  235:     */     {
/*  236: 345 */       VectorInstance diff = x.subtract(y);
/*  237:     */       
/*  238: 347 */       double result = -this.m_gamma * diff.dotProduct(diff);
/*  239:     */       
/*  240: 349 */       return Math.exp(result);
/*  241:     */     }
/*  242:     */     
/*  243:     */     public String toString()
/*  244:     */     {
/*  245: 358 */       return "Radial kernel: K(x,y) = exp(-" + this.m_gamma + " * ||x - y||^2)";
/*  246:     */     }
/*  247:     */   }
/*  248:     */   
/*  249:     */   static class SigmoidKernel
/*  250:     */     extends SupportVectorMachineModel.Kernel
/*  251:     */     implements Serializable
/*  252:     */   {
/*  253:     */     private static final long serialVersionUID = 8713475894705750117L;
/*  254: 370 */     protected double m_gamma = 1.0D;
/*  255: 372 */     protected double m_coef0 = 1.0D;
/*  256:     */     
/*  257:     */     public SigmoidKernel(Element sigElement)
/*  258:     */     {
/*  259: 375 */       this(sigElement, null);
/*  260:     */     }
/*  261:     */     
/*  262:     */     public SigmoidKernel(Element sigElement, Logger log)
/*  263:     */     {
/*  264: 379 */       super();
/*  265:     */       
/*  266: 381 */       String gammaString = sigElement.getAttribute("gamma");
/*  267: 382 */       if ((gammaString != null) && (gammaString.length() > 0)) {
/*  268:     */         try
/*  269:     */         {
/*  270: 384 */           this.m_gamma = Double.parseDouble(gammaString);
/*  271:     */         }
/*  272:     */         catch (NumberFormatException e)
/*  273:     */         {
/*  274: 386 */           String message = "[SigmoidKernel] : WARNING, can't parse gamma attribute. Using default value of 1.";
/*  275: 388 */           if (this.m_log == null) {
/*  276: 389 */             System.err.println(message);
/*  277:     */           } else {
/*  278: 391 */             this.m_log.logMessage(message);
/*  279:     */           }
/*  280:     */         }
/*  281:     */       }
/*  282: 396 */       String coefString = sigElement.getAttribute("coef0");
/*  283: 397 */       if ((coefString != null) && (coefString.length() > 0)) {
/*  284:     */         try
/*  285:     */         {
/*  286: 399 */           this.m_coef0 = Double.parseDouble(coefString);
/*  287:     */         }
/*  288:     */         catch (NumberFormatException e)
/*  289:     */         {
/*  290: 401 */           String message = "[SigmoidKernel] : WARNING, can't parse coef0 attribute. Using default value of 1.";
/*  291: 403 */           if (this.m_log == null) {
/*  292: 404 */             System.err.println(message);
/*  293:     */           } else {
/*  294: 406 */             this.m_log.logMessage(message);
/*  295:     */           }
/*  296:     */         }
/*  297:     */       }
/*  298:     */     }
/*  299:     */     
/*  300:     */     public double evaluate(VectorInstance x, VectorInstance y)
/*  301:     */       throws Exception
/*  302:     */     {
/*  303: 423 */       double dotProd = x.dotProduct(y);
/*  304: 424 */       double z = this.m_gamma * dotProd + this.m_coef0;
/*  305: 425 */       double a = Math.exp(z);
/*  306: 426 */       double b = Math.exp(-z);
/*  307: 427 */       return (a - b) / (a + b);
/*  308:     */     }
/*  309:     */     
/*  310:     */     public double evaluate(VectorInstance x, double[] y)
/*  311:     */       throws Exception
/*  312:     */     {
/*  313: 441 */       double dotProd = x.dotProduct(y);
/*  314: 442 */       double z = this.m_gamma * dotProd + this.m_coef0;
/*  315: 443 */       double a = Math.exp(z);
/*  316: 444 */       double b = Math.exp(-z);
/*  317: 445 */       return (a - b) / (a + b);
/*  318:     */     }
/*  319:     */     
/*  320:     */     public String toString()
/*  321:     */     {
/*  322: 454 */       return "Sigmoid kernel: K(x,y) = tanh(" + this.m_gamma + " * <x,y> + " + this.m_coef0 + ")";
/*  323:     */     }
/*  324:     */   }
/*  325:     */   
/*  326:     */   static class SupportVectorMachine
/*  327:     */     implements Serializable
/*  328:     */   {
/*  329:     */     private static final long serialVersionUID = -7650496802836815608L;
/*  330:     */     protected String m_targetCategory;
/*  331: 471 */     protected int m_globalAlternateTargetCategoryIndex = -1;
/*  332: 474 */     protected int m_targetCategoryIndex = -1;
/*  333: 477 */     protected int m_localAlternateTargetCategoryIndex = -1;
/*  334: 480 */     protected double m_localThreshold = 1.7976931348623157E+308D;
/*  335:     */     protected MiningSchema m_miningSchema;
/*  336:     */     protected Logger m_log;
/*  337: 492 */     protected boolean m_coeffsOnly = false;
/*  338: 495 */     protected List<VectorInstance> m_supportVectors = new ArrayList();
/*  339: 499 */     protected double m_intercept = 0.0D;
/*  340:     */     protected double[] m_coefficients;
/*  341:     */     
/*  342:     */     public void distributionForInstance(double[] input, SupportVectorMachineModel.Kernel kernel, VectorDictionary vecDict, double[] preds, SupportVectorMachineModel.classificationMethod cMethod, double globalThreshold)
/*  343:     */       throws Exception
/*  344:     */     {
/*  345: 526 */       int targetIndex = 0;
/*  346: 528 */       if (!this.m_coeffsOnly) {
/*  347: 531 */         input = vecDict.incomingInstanceToVectorFieldVals(input);
/*  348:     */       }
/*  349: 534 */       if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNominal()) {
/*  350: 535 */         targetIndex = this.m_targetCategoryIndex;
/*  351:     */       }
/*  352: 538 */       double result = 0.0D;
/*  353: 539 */       for (int i = 0; i < this.m_coefficients.length; i++)
/*  354:     */       {
/*  355: 541 */         double val = 0.0D;
/*  356: 542 */         if (!this.m_coeffsOnly) {
/*  357: 543 */           val = kernel.evaluate((VectorInstance)this.m_supportVectors.get(i), input);
/*  358:     */         } else {
/*  359: 545 */           val = input[i];
/*  360:     */         }
/*  361: 547 */         val *= this.m_coefficients[i];
/*  362:     */         
/*  363: 549 */         result += val;
/*  364:     */       }
/*  365: 551 */       result += this.m_intercept;
/*  366: 570 */       if ((cMethod == SupportVectorMachineModel.classificationMethod.NONE) || (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric()))
/*  367:     */       {
/*  368: 592 */         if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNominal())
/*  369:     */         {
/*  370: 593 */           if (result < 0.0D) {
/*  371: 594 */             preds[targetIndex] = 1.0D;
/*  372:     */           } else {
/*  373: 596 */             preds[targetIndex] = 0.0D;
/*  374:     */           }
/*  375:     */         }
/*  376:     */         else {
/*  377: 599 */           preds[targetIndex] = result;
/*  378:     */         }
/*  379:     */       }
/*  380: 603 */       else if (cMethod == SupportVectorMachineModel.classificationMethod.ONE_AGAINST_ALL)
/*  381:     */       {
/*  382: 605 */         preds[targetIndex] = result;
/*  383:     */       }
/*  384:     */       else
/*  385:     */       {
/*  386: 608 */         double threshold = this.m_localThreshold < 1.7976931348623157E+308D ? this.m_localThreshold : globalThreshold;
/*  387: 613 */         if (result < threshold)
/*  388:     */         {
/*  389: 614 */           preds[targetIndex] += 1.0D;
/*  390:     */         }
/*  391:     */         else
/*  392:     */         {
/*  393: 616 */           int altCat = this.m_localAlternateTargetCategoryIndex != -1 ? this.m_localAlternateTargetCategoryIndex : this.m_globalAlternateTargetCategoryIndex;
/*  394:     */           
/*  395:     */ 
/*  396:     */ 
/*  397: 620 */           preds[altCat] += 1.0D;
/*  398:     */         }
/*  399:     */       }
/*  400:     */     }
/*  401:     */     
/*  402:     */     public SupportVectorMachine(Element machineElement, MiningSchema miningSchema, VectorDictionary dictionary, SupportVectorMachineModel.SVM_representation svmRep, int altCategoryInd, Logger log)
/*  403:     */       throws Exception
/*  404:     */     {
/*  405: 645 */       this.m_miningSchema = miningSchema;
/*  406: 646 */       this.m_log = log;
/*  407:     */       
/*  408: 648 */       String targetCat = machineElement.getAttribute("targetCategory");
/*  409: 649 */       if ((targetCat != null) && (targetCat.length() > 0))
/*  410:     */       {
/*  411: 650 */         this.m_targetCategory = targetCat;
/*  412: 651 */         Attribute classAtt = this.m_miningSchema.getFieldsAsInstances().classAttribute();
/*  413: 652 */         if (classAtt.isNominal())
/*  414:     */         {
/*  415: 653 */           int index = classAtt.indexOfValue(this.m_targetCategory);
/*  416: 655 */           if (index < 0) {
/*  417: 656 */             throw new Exception("[SupportVectorMachine] : can't find target category: " + this.m_targetCategory + " in the class attribute!");
/*  418:     */           }
/*  419: 660 */           this.m_targetCategoryIndex = index;
/*  420:     */           
/*  421:     */ 
/*  422: 663 */           String altTargetCat = machineElement.getAttribute("alternateTargetCategory");
/*  423: 664 */           if ((altTargetCat != null) && (altTargetCat.length() > 0))
/*  424:     */           {
/*  425: 665 */             index = classAtt.indexOfValue(altTargetCat);
/*  426: 666 */             if (index < 0) {
/*  427: 667 */               throw new Exception("[SupportVectorMachine] : can't find alternate target category: " + altTargetCat + " in the class attribute!");
/*  428:     */             }
/*  429: 670 */             this.m_localAlternateTargetCategoryIndex = index;
/*  430:     */           }
/*  431:     */           else
/*  432:     */           {
/*  433: 673 */             this.m_globalAlternateTargetCategoryIndex = altCategoryInd;
/*  434:     */           }
/*  435:     */         }
/*  436:     */         else
/*  437:     */         {
/*  438: 677 */           throw new Exception("[SupportVectorMachine] : target category supplied but class attribute is numeric!");
/*  439:     */         }
/*  440:     */       }
/*  441: 681 */       else if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNominal())
/*  442:     */       {
/*  443: 682 */         this.m_targetCategoryIndex = (altCategoryInd == 0 ? 1 : 0);
/*  444:     */         
/*  445:     */ 
/*  446:     */ 
/*  447: 686 */         this.m_globalAlternateTargetCategoryIndex = altCategoryInd;
/*  448: 687 */         System.err.println("Setting target index for machine to " + this.m_targetCategoryIndex);
/*  449:     */       }
/*  450: 691 */       if (svmRep == SupportVectorMachineModel.SVM_representation.SUPPORT_VECTORS)
/*  451:     */       {
/*  452: 693 */         NodeList vectorsL = machineElement.getElementsByTagName("SupportVectors");
/*  453: 695 */         if (vectorsL.getLength() > 0)
/*  454:     */         {
/*  455: 696 */           Element vectors = (Element)vectorsL.item(0);
/*  456: 697 */           NodeList allTheVectorsL = vectors.getElementsByTagName("SupportVector");
/*  457: 699 */           for (int i = 0; i < allTheVectorsL.getLength(); i++)
/*  458:     */           {
/*  459: 700 */             Node vec = allTheVectorsL.item(i);
/*  460: 701 */             String vecId = ((Element)vec).getAttribute("vectorId");
/*  461: 702 */             VectorInstance suppV = dictionary.getVector(vecId);
/*  462: 703 */             if (suppV == null) {
/*  463: 704 */               throw new Exception("[SupportVectorMachine] : can't find vector with ID: " + vecId + " in the " + "vector dictionary!");
/*  464:     */             }
/*  465: 708 */             this.m_supportVectors.add(suppV);
/*  466:     */           }
/*  467:     */         }
/*  468:     */       }
/*  469:     */       else
/*  470:     */       {
/*  471: 712 */         this.m_coeffsOnly = true;
/*  472:     */       }
/*  473: 716 */       NodeList coefficientsL = machineElement.getElementsByTagName("Coefficients");
/*  474: 718 */       if (coefficientsL.getLength() != 1) {
/*  475: 719 */         throw new Exception("[SupportVectorMachine] Should be just one list of coefficients per binary SVM!");
/*  476:     */       }
/*  477: 722 */       Element cL = (Element)coefficientsL.item(0);
/*  478: 723 */       String intercept = cL.getAttribute("absoluteValue");
/*  479: 724 */       if ((intercept != null) && (intercept.length() > 0)) {
/*  480: 725 */         this.m_intercept = Double.parseDouble(intercept);
/*  481:     */       }
/*  482: 729 */       NodeList coeffL = cL.getElementsByTagName("Coefficient");
/*  483: 730 */       if (coeffL.getLength() == 0) {
/*  484: 731 */         throw new Exception("[SupportVectorMachine] No coefficients defined!");
/*  485:     */       }
/*  486: 734 */       this.m_coefficients = new double[coeffL.getLength()];
/*  487: 736 */       for (int i = 0; i < coeffL.getLength(); i++)
/*  488:     */       {
/*  489: 737 */         Element coeff = (Element)coeffL.item(i);
/*  490: 738 */         String val = coeff.getAttribute("value");
/*  491: 739 */         this.m_coefficients[i] = Double.parseDouble(val);
/*  492:     */       }
/*  493:     */     }
/*  494:     */     
/*  495:     */     public String toString()
/*  496:     */     {
/*  497: 749 */       StringBuffer temp = new StringBuffer();
/*  498:     */       
/*  499: 751 */       temp.append("Binary SVM");
/*  500: 752 */       if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNominal())
/*  501:     */       {
/*  502: 753 */         temp.append(" (target category = " + this.m_targetCategory + ")");
/*  503: 754 */         if (this.m_localAlternateTargetCategoryIndex != -1) {
/*  504: 755 */           temp.append("\n (alternate category = " + this.m_miningSchema.getFieldsAsInstances().classAttribute().value(this.m_localAlternateTargetCategoryIndex) + ")");
/*  505:     */         }
/*  506:     */       }
/*  507: 760 */       temp.append("\n\n");
/*  508: 762 */       for (int i = 0; i < this.m_supportVectors.size(); i++) {
/*  509: 763 */         temp.append("\n" + this.m_coefficients[i] + " * [" + ((VectorInstance)this.m_supportVectors.get(i)).getValues() + " * X]");
/*  510:     */       }
/*  511: 767 */       if (this.m_intercept >= 0.0D) {
/*  512: 768 */         temp.append("\n +" + this.m_intercept);
/*  513:     */       } else {
/*  514: 770 */         temp.append("\n " + this.m_intercept);
/*  515:     */       }
/*  516: 772 */       return temp.toString();
/*  517:     */     }
/*  518:     */   }
/*  519:     */   
/*  520:     */   static enum SVM_representation
/*  521:     */   {
/*  522: 777 */     SUPPORT_VECTORS,  COEFFICIENTS;
/*  523:     */     
/*  524:     */     private SVM_representation() {}
/*  525:     */   }
/*  526:     */   
/*  527:     */   static enum classificationMethod
/*  528:     */   {
/*  529: 782 */     NONE,  ONE_AGAINST_ALL,  ONE_AGAINST_ONE;
/*  530:     */     
/*  531:     */     private classificationMethod() {}
/*  532:     */   }
/*  533:     */   
/*  534: 788 */   protected NeuralNetwork.MiningFunction m_functionType = NeuralNetwork.MiningFunction.CLASSIFICATION;
/*  535: 791 */   protected classificationMethod m_classificationMethod = classificationMethod.NONE;
/*  536:     */   protected String m_modelName;
/*  537:     */   protected String m_algorithmName;
/*  538:     */   protected VectorDictionary m_vectorDictionary;
/*  539:     */   protected Kernel m_kernel;
/*  540: 807 */   protected List<SupportVectorMachine> m_machines = new ArrayList();
/*  541: 811 */   protected int m_alternateBinaryTargetCategory = -1;
/*  542: 814 */   protected SVM_representation m_svmRepresentation = SVM_representation.SUPPORT_VECTORS;
/*  543: 817 */   protected double m_threshold = 0.0D;
/*  544:     */   
/*  545:     */   public SupportVectorMachineModel(Element model, Instances dataDictionary, MiningSchema miningSchema)
/*  546:     */     throws Exception
/*  547:     */   {
/*  548: 831 */     super(dataDictionary, miningSchema);
/*  549: 833 */     if (!getPMMLVersion().equals("3.2")) {}
/*  550: 837 */     String fn = model.getAttribute("functionName");
/*  551: 838 */     if (fn.equals("regression")) {
/*  552: 839 */       this.m_functionType = NeuralNetwork.MiningFunction.REGRESSION;
/*  553:     */     }
/*  554: 842 */     String modelName = model.getAttribute("modelName");
/*  555: 843 */     if ((modelName != null) && (modelName.length() > 0)) {
/*  556: 844 */       this.m_modelName = modelName;
/*  557:     */     }
/*  558: 847 */     String algoName = model.getAttribute("algorithmName");
/*  559: 848 */     if ((algoName != null) && (algoName.length() > 0)) {
/*  560: 849 */       this.m_algorithmName = algoName;
/*  561:     */     }
/*  562: 852 */     String svmRep = model.getAttribute("svmRepresentation");
/*  563: 853 */     if ((svmRep != null) && (svmRep.length() > 0) && 
/*  564: 854 */       (svmRep.equals("Coefficients"))) {
/*  565: 855 */       this.m_svmRepresentation = SVM_representation.COEFFICIENTS;
/*  566:     */     }
/*  567: 859 */     String altTargetCat = model.getAttribute("alternateBinaryTargetCategory");
/*  568: 860 */     if ((altTargetCat != null) && (altTargetCat.length() > 0))
/*  569:     */     {
/*  570: 861 */       int altTargetInd = this.m_miningSchema.getFieldsAsInstances().classAttribute().indexOfValue(altTargetCat);
/*  571: 864 */       if (altTargetInd < 0) {
/*  572: 865 */         throw new Exception("[SupportVectorMachineModel] can't find alternate target value " + altTargetCat);
/*  573:     */       }
/*  574: 868 */       this.m_alternateBinaryTargetCategory = altTargetInd;
/*  575:     */     }
/*  576: 872 */     String thresholdS = model.getAttribute("threshold");
/*  577: 873 */     if ((thresholdS != null) && (thresholdS.length() > 0)) {
/*  578: 874 */       this.m_threshold = Double.parseDouble(thresholdS);
/*  579:     */     }
/*  580: 878 */     if (getPMMLVersion().startsWith("4.")) {
/*  581: 879 */       this.m_classificationMethod = classificationMethod.ONE_AGAINST_ALL;
/*  582:     */     }
/*  583: 882 */     String classificationMethodS = model.getAttribute("classificationMethod");
/*  584: 883 */     if ((classificationMethodS != null) && (classificationMethodS.length() > 0) && 
/*  585: 884 */       (classificationMethodS.equals("OneAgainstOne"))) {
/*  586: 885 */       this.m_classificationMethod = classificationMethod.ONE_AGAINST_ONE;
/*  587:     */     }
/*  588: 889 */     if (this.m_svmRepresentation == SVM_representation.SUPPORT_VECTORS) {
/*  589: 890 */       this.m_vectorDictionary = VectorDictionary.getVectorDictionary(model, miningSchema);
/*  590:     */     }
/*  591: 893 */     this.m_kernel = Kernel.getKernel(model, this.m_log);
/*  592: 894 */     if ((this.m_svmRepresentation == SVM_representation.COEFFICIENTS) && (!(this.m_kernel instanceof LinearKernel))) {
/*  593: 896 */       throw new Exception("[SupportVectorMachineModel] representation is coefficients, but kernel is not linear!");
/*  594:     */     }
/*  595: 901 */     NodeList machineL = model.getElementsByTagName("SupportVectorMachine");
/*  596: 902 */     if (machineL.getLength() == 0) {
/*  597: 903 */       throw new Exception("[SupportVectorMachineModel] No binary SVMs defined in model file!");
/*  598:     */     }
/*  599: 906 */     for (int i = 0; i < machineL.getLength(); i++)
/*  600:     */     {
/*  601: 907 */       Node machine = machineL.item(i);
/*  602: 908 */       SupportVectorMachine newMach = new SupportVectorMachine((Element)machine, this.m_miningSchema, this.m_vectorDictionary, this.m_svmRepresentation, this.m_alternateBinaryTargetCategory, this.m_log);
/*  603:     */       
/*  604:     */ 
/*  605:     */ 
/*  606: 912 */       this.m_machines.add(newMach);
/*  607:     */     }
/*  608:     */   }
/*  609:     */   
/*  610:     */   public double[] distributionForInstance(Instance inst)
/*  611:     */     throws Exception
/*  612:     */   {
/*  613: 926 */     if (!this.m_initialized) {
/*  614: 927 */       mapToMiningSchema(inst.dataset());
/*  615:     */     }
/*  616: 929 */     double[] preds = null;
/*  617: 931 */     if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric())
/*  618:     */     {
/*  619: 932 */       preds = new double[1];
/*  620:     */     }
/*  621:     */     else
/*  622:     */     {
/*  623: 934 */       preds = new double[this.m_miningSchema.getFieldsAsInstances().classAttribute().numValues()];
/*  624: 935 */       for (int i = 0; i < preds.length; i++) {
/*  625: 936 */         preds[i] = -1.0D;
/*  626:     */       }
/*  627:     */     }
/*  628: 940 */     double[] incoming = this.m_fieldsMap.instanceToSchema(inst, this.m_miningSchema);
/*  629:     */     
/*  630: 942 */     boolean hasMissing = false;
/*  631: 943 */     for (int i = 0; i < incoming.length; i++) {
/*  632: 944 */       if ((i != this.m_miningSchema.getFieldsAsInstances().classIndex()) && (Double.isNaN(incoming[i])))
/*  633:     */       {
/*  634: 946 */         hasMissing = true;
/*  635:     */         
/*  636: 948 */         break;
/*  637:     */       }
/*  638:     */     }
/*  639: 952 */     if (hasMissing)
/*  640:     */     {
/*  641: 953 */       if (!this.m_miningSchema.hasTargetMetaData())
/*  642:     */       {
/*  643: 954 */         String message = "[SupportVectorMachineModel] WARNING: Instance to predict has missing value(s) but there is no missing value handling meta data and no prior probabilities/default value to fall back to. No prediction will be made (" + ((this.m_miningSchema.getFieldsAsInstances().classAttribute().isNominal()) || (this.m_miningSchema.getFieldsAsInstances().classAttribute().isString()) ? "zero probabilities output)." : "NaN output).");
/*  644: 962 */         if (this.m_log == null) {
/*  645: 963 */           System.err.println(message);
/*  646:     */         } else {
/*  647: 965 */           this.m_log.logMessage(message);
/*  648:     */         }
/*  649: 968 */         if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric()) {
/*  650: 969 */           preds[0] = Utils.missingValue();
/*  651:     */         }
/*  652: 971 */         return preds;
/*  653:     */       }
/*  654: 974 */       TargetMetaInfo targetData = this.m_miningSchema.getTargetMetaData();
/*  655: 975 */       if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric())
/*  656:     */       {
/*  657: 976 */         preds[0] = targetData.getDefaultValue();
/*  658:     */       }
/*  659:     */       else
/*  660:     */       {
/*  661: 978 */         Instances miningSchemaI = this.m_miningSchema.getFieldsAsInstances();
/*  662: 979 */         for (int i = 0; i < miningSchemaI.classAttribute().numValues(); i++) {
/*  663: 980 */           preds[i] = targetData.getPriorProbability(miningSchemaI.classAttribute().value(i));
/*  664:     */         }
/*  665:     */       }
/*  666: 983 */       return preds;
/*  667:     */     }
/*  668: 986 */     for (SupportVectorMachine m : this.m_machines) {
/*  669: 987 */       m.distributionForInstance(incoming, this.m_kernel, this.m_vectorDictionary, preds, this.m_classificationMethod, this.m_threshold);
/*  670:     */     }
/*  671: 992 */     if ((this.m_classificationMethod != classificationMethod.NONE) && (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNominal())) {
/*  672: 995 */       if (this.m_classificationMethod == classificationMethod.ONE_AGAINST_ALL)
/*  673:     */       {
/*  674: 997 */         int minI = Utils.minIndex(preds);
/*  675: 998 */         preds = new double[preds.length];
/*  676: 999 */         preds[minI] = 1.0D;
/*  677:     */       }
/*  678:     */     }
/*  679:1006 */     if (this.m_machines.size() == preds.length - 1)
/*  680:     */     {
/*  681:1007 */       double total = 0.0D;
/*  682:1008 */       int unset = -1;
/*  683:1009 */       for (int i = 0; i < preds.length; i++) {
/*  684:1010 */         if (preds[i] != -1.0D) {
/*  685:1011 */           total += preds[i];
/*  686:     */         } else {
/*  687:1013 */           unset = i;
/*  688:     */         }
/*  689:     */       }
/*  690:1017 */       if (total > 1.0D) {
/*  691:1018 */         throw new Exception("[SupportVectorMachineModel] total of probabilities is greater than 1!");
/*  692:     */       }
/*  693:1022 */       preds[unset] = (1.0D - total);
/*  694:     */     }
/*  695:1025 */     if (preds.length > 1) {
/*  696:1026 */       Utils.normalize(preds);
/*  697:     */     }
/*  698:1029 */     return preds;
/*  699:     */   }
/*  700:     */   
/*  701:     */   public String getRevision()
/*  702:     */   {
/*  703:1033 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  704:     */   }
/*  705:     */   
/*  706:     */   public String toString()
/*  707:     */   {
/*  708:1042 */     StringBuffer temp = new StringBuffer();
/*  709:     */     
/*  710:1044 */     temp.append("PMML version " + getPMMLVersion());
/*  711:1045 */     if (!getCreatorApplication().equals("?")) {
/*  712:1046 */       temp.append("\nApplication: " + getCreatorApplication());
/*  713:     */     }
/*  714:1048 */     temp.append("\nPMML Model: Support Vector Machine Model");
/*  715:     */     
/*  716:1050 */     temp.append("\n\n");
/*  717:1051 */     temp.append(this.m_miningSchema);
/*  718:     */     
/*  719:1053 */     temp.append("Kernel: \n\t");
/*  720:1054 */     temp.append(this.m_kernel);
/*  721:1055 */     temp.append("\n");
/*  722:1057 */     if (this.m_classificationMethod != classificationMethod.NONE)
/*  723:     */     {
/*  724:1058 */       temp.append("Multi-class classifcation using ");
/*  725:1059 */       if (this.m_classificationMethod == classificationMethod.ONE_AGAINST_ALL) {
/*  726:1060 */         temp.append("one-against-all");
/*  727:     */       } else {
/*  728:1062 */         temp.append("one-against-one");
/*  729:     */       }
/*  730:1064 */       temp.append("\n\n");
/*  731:     */     }
/*  732:1067 */     for (SupportVectorMachine v : this.m_machines) {
/*  733:1068 */       temp.append("\n" + v);
/*  734:     */     }
/*  735:1071 */     return temp.toString();
/*  736:     */   }
/*  737:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.pmml.consumer.SupportVectorMachineModel
 * JD-Core Version:    0.7.0.1
 */