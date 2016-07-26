/*    1:     */ package weka.classifiers.mi;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Vector;
/*    7:     */ import weka.classifiers.AbstractClassifier;
/*    8:     */ import weka.core.Attribute;
/*    9:     */ import weka.core.Capabilities;
/*   10:     */ import weka.core.Capabilities.Capability;
/*   11:     */ import weka.core.DenseInstance;
/*   12:     */ import weka.core.Instance;
/*   13:     */ import weka.core.Instances;
/*   14:     */ import weka.core.MultiInstanceCapabilitiesHandler;
/*   15:     */ import weka.core.Option;
/*   16:     */ import weka.core.OptionHandler;
/*   17:     */ import weka.core.RevisionUtils;
/*   18:     */ import weka.core.TechnicalInformation;
/*   19:     */ import weka.core.TechnicalInformation.Field;
/*   20:     */ import weka.core.TechnicalInformation.Type;
/*   21:     */ import weka.core.TechnicalInformationHandler;
/*   22:     */ import weka.core.Utils;
/*   23:     */ 
/*   24:     */ public class MINND
/*   25:     */   extends AbstractClassifier
/*   26:     */   implements OptionHandler, MultiInstanceCapabilitiesHandler, TechnicalInformationHandler
/*   27:     */ {
/*   28:     */   static final long serialVersionUID = -4512599203273864994L;
/*   29: 115 */   protected int m_Neighbour = 1;
/*   30: 118 */   protected double[][] m_Mean = (double[][])null;
/*   31: 121 */   protected double[][] m_Variance = (double[][])null;
/*   32: 124 */   protected int m_Dimension = 0;
/*   33:     */   protected Instances m_Attributes;
/*   34: 130 */   protected double[] m_Class = null;
/*   35: 133 */   protected int m_NumClasses = 0;
/*   36: 136 */   protected double[] m_Weights = null;
/*   37: 139 */   private static double m_ZERO = 1.0E-045D;
/*   38: 142 */   protected double m_Rate = -1.0D;
/*   39: 145 */   private double[] m_MinArray = null;
/*   40: 148 */   private double[] m_MaxArray = null;
/*   41: 151 */   private final double m_STOP = 1.0E-045D;
/*   42: 154 */   private double[][] m_Change = (double[][])null;
/*   43: 157 */   private double[][] m_NoiseM = (double[][])null;
/*   44: 157 */   private double[][] m_NoiseV = (double[][])null;
/*   45: 157 */   private double[][] m_ValidM = (double[][])null;
/*   46: 157 */   private double[][] m_ValidV = (double[][])null;
/*   47: 164 */   private int m_Select = 1;
/*   48: 170 */   private int m_Choose = 1;
/*   49: 173 */   private final double m_Decay = 0.5D;
/*   50:     */   
/*   51:     */   public String globalInfo()
/*   52:     */   {
/*   53: 182 */     return "Multiple-Instance Nearest Neighbour with Distribution learner.\n\nIt uses gradient descent to find the weight for each dimension of each exeamplar from the starting point of 1.0. In order to avoid overfitting, it uses mean-square function (i.e. the Euclidean distance) to search for the weights.\n It then uses the weights to cleanse the training data. After that it searches for the weights again from the starting points of the weights searched before.\n Finally it uses the most updated weights to cleanse the test exemplar and then finds the nearest neighbour of the test exemplar using partly-weighted Kullback distance. But the variances in the Kullback distance are the ones before cleansing.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*   54:     */   }
/*   55:     */   
/*   56:     */   public TechnicalInformation getTechnicalInformation()
/*   57:     */   {
/*   58: 208 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MISC);
/*   59: 209 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Xin Xu");
/*   60: 210 */     result.setValue(TechnicalInformation.Field.YEAR, "2001");
/*   61: 211 */     result.setValue(TechnicalInformation.Field.TITLE, "A nearest distribution approach to multiple-instance learning");
/*   62:     */     
/*   63: 213 */     result.setValue(TechnicalInformation.Field.SCHOOL, "University of Waikato");
/*   64: 214 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Hamilton, NZ");
/*   65: 215 */     result.setValue(TechnicalInformation.Field.NOTE, "0657.591B");
/*   66:     */     
/*   67: 217 */     return result;
/*   68:     */   }
/*   69:     */   
/*   70:     */   public Capabilities getCapabilities()
/*   71:     */   {
/*   72: 227 */     Capabilities result = super.getCapabilities();
/*   73: 228 */     result.disableAll();
/*   74:     */     
/*   75:     */ 
/*   76: 231 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*   77: 232 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*   78:     */     
/*   79:     */ 
/*   80: 235 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*   81: 236 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*   82:     */     
/*   83:     */ 
/*   84: 239 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/*   85:     */     
/*   86: 241 */     return result;
/*   87:     */   }
/*   88:     */   
/*   89:     */   public Capabilities getMultiInstanceCapabilities()
/*   90:     */   {
/*   91: 253 */     Capabilities result = super.getCapabilities();
/*   92: 254 */     result.disableAll();
/*   93:     */     
/*   94:     */ 
/*   95: 257 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   96: 258 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*   97: 259 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*   98:     */     
/*   99:     */ 
/*  100: 262 */     result.disableAllClasses();
/*  101: 263 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  102:     */     
/*  103: 265 */     return result;
/*  104:     */   }
/*  105:     */   
/*  106:     */   public void buildClassifier(Instances exs)
/*  107:     */     throws Exception
/*  108:     */   {
/*  109: 280 */     getCapabilities().testWithFail(exs);
/*  110:     */     
/*  111:     */ 
/*  112: 283 */     Instances newData = new Instances(exs);
/*  113: 284 */     newData.deleteWithMissingClass();
/*  114:     */     
/*  115: 286 */     int numegs = newData.numInstances();
/*  116: 287 */     this.m_Dimension = newData.attribute(1).relation().numAttributes();
/*  117: 288 */     this.m_Attributes = newData.stringFreeStructure();
/*  118: 289 */     this.m_Change = new double[numegs][this.m_Dimension];
/*  119: 290 */     this.m_NumClasses = exs.numClasses();
/*  120: 291 */     this.m_Mean = new double[numegs][this.m_Dimension];
/*  121: 292 */     this.m_Variance = new double[numegs][this.m_Dimension];
/*  122: 293 */     this.m_Class = new double[numegs];
/*  123: 294 */     this.m_Weights = new double[numegs];
/*  124: 295 */     this.m_NoiseM = new double[numegs][this.m_Dimension];
/*  125: 296 */     this.m_NoiseV = new double[numegs][this.m_Dimension];
/*  126: 297 */     this.m_ValidM = new double[numegs][this.m_Dimension];
/*  127: 298 */     this.m_ValidV = new double[numegs][this.m_Dimension];
/*  128: 299 */     this.m_MinArray = new double[this.m_Dimension];
/*  129: 300 */     this.m_MaxArray = new double[this.m_Dimension];
/*  130: 301 */     for (int v = 0; v < this.m_Dimension; v++)
/*  131:     */     {
/*  132: 302 */       double tmp209_206 = (0.0D / 0.0D);this.m_MaxArray[v] = tmp209_206;this.m_MinArray[v] = tmp209_206;
/*  133:     */     }
/*  134: 305 */     for (int w = 0; w < numegs; w++) {
/*  135: 306 */       updateMinMax(newData.instance(w));
/*  136:     */     }
/*  137: 310 */     Instances data = this.m_Attributes;
/*  138: 312 */     for (int x = 0; x < numegs; x++)
/*  139:     */     {
/*  140: 313 */       Instance example = newData.instance(x);
/*  141: 314 */       example = scale(example);
/*  142: 315 */       for (int i = 0; i < this.m_Dimension; i++)
/*  143:     */       {
/*  144: 316 */         this.m_Mean[x][i] = example.relationalValue(1).meanOrMode(i);
/*  145: 317 */         this.m_Variance[x][i] = example.relationalValue(1).variance(i);
/*  146: 318 */         if (Utils.eq(this.m_Variance[x][i], 0.0D)) {
/*  147: 319 */           this.m_Variance[x][i] = m_ZERO;
/*  148:     */         }
/*  149: 321 */         this.m_Change[x][i] = 1.0D;
/*  150:     */       }
/*  151: 329 */       data.add(example);
/*  152: 330 */       this.m_Class[x] = example.classValue();
/*  153: 331 */       this.m_Weights[x] = example.weight();
/*  154:     */     }
/*  155: 334 */     for (int z = 0; z < numegs; z++) {
/*  156: 335 */       findWeights(z, this.m_Mean);
/*  157:     */     }
/*  158: 339 */     for (int x = 0; x < numegs; x++)
/*  159:     */     {
/*  160: 340 */       Instance example = preprocess(data, x);
/*  161: 341 */       if (getDebug()) {
/*  162: 342 */         System.out.println("???Exemplar " + x + " has been pre-processed:" + data.instance(x).relationalValue(1).sumOfWeights() + "|" + example.relationalValue(1).sumOfWeights() + "; class:" + this.m_Class[x]);
/*  163:     */       }
/*  164: 348 */       if (Utils.gr(example.relationalValue(1).sumOfWeights(), 0.0D))
/*  165:     */       {
/*  166: 349 */         for (int i = 0; i < this.m_Dimension; i++)
/*  167:     */         {
/*  168: 350 */           this.m_ValidM[x][i] = example.relationalValue(1).meanOrMode(i);
/*  169: 351 */           this.m_ValidV[x][i] = example.relationalValue(1).variance(i);
/*  170: 352 */           if (Utils.eq(this.m_ValidV[x][i], 0.0D)) {
/*  171: 353 */             this.m_ValidV[x][i] = m_ZERO;
/*  172:     */           }
/*  173:     */         }
/*  174:     */       }
/*  175:     */       else
/*  176:     */       {
/*  177: 361 */         this.m_ValidM[x] = null;
/*  178: 362 */         this.m_ValidV[x] = null;
/*  179:     */       }
/*  180:     */     }
/*  181: 366 */     for (int z = 0; z < numegs; z++) {
/*  182: 367 */       if (this.m_ValidM[z] != null) {
/*  183: 368 */         findWeights(z, this.m_ValidM);
/*  184:     */       }
/*  185:     */     }
/*  186:     */   }
/*  187:     */   
/*  188:     */   public Instance preprocess(Instances data, int pos)
/*  189:     */     throws Exception
/*  190:     */   {
/*  191: 384 */     Instance before = data.instance(pos);
/*  192: 385 */     if ((int)before.classValue() == 0)
/*  193:     */     {
/*  194: 386 */       this.m_NoiseM[pos] = null;
/*  195: 387 */       this.m_NoiseV[pos] = null;
/*  196: 388 */       return before;
/*  197:     */     }
/*  198: 391 */     Instances after_relationInsts = before.attribute(1).relation().stringFreeStructure();
/*  199:     */     
/*  200: 393 */     Instances noises_relationInsts = before.attribute(1).relation().stringFreeStructure();
/*  201:     */     
/*  202:     */ 
/*  203: 396 */     Instances newData = this.m_Attributes;
/*  204: 397 */     Instance after = new DenseInstance(before.numAttributes());
/*  205: 398 */     Instance noises = new DenseInstance(before.numAttributes());
/*  206: 399 */     after.setDataset(newData);
/*  207: 400 */     noises.setDataset(newData);
/*  208: 402 */     for (int g = 0; g < before.relationalValue(1).numInstances(); g++)
/*  209:     */     {
/*  210: 403 */       Instance datum = before.relationalValue(1).instance(g);
/*  211: 404 */       double[] dists = new double[data.numInstances()];
/*  212: 406 */       for (int i = 0; i < data.numInstances(); i++) {
/*  213: 407 */         if (i != pos) {
/*  214: 408 */           dists[i] = distance(datum, this.m_Mean[i], this.m_Variance[i], i);
/*  215:     */         } else {
/*  216: 410 */           dists[i] = (1.0D / 0.0D);
/*  217:     */         }
/*  218:     */       }
/*  219: 414 */       int[] pred = new int[this.m_NumClasses];
/*  220: 415 */       for (int n = 0; n < pred.length; n++) {
/*  221: 416 */         pred[n] = 0;
/*  222:     */       }
/*  223: 419 */       for (int o = 0; o < this.m_Select; o++)
/*  224:     */       {
/*  225: 420 */         int index = Utils.minIndex(dists);
/*  226: 421 */         pred[((int)this.m_Class[index])] += 1;
/*  227: 422 */         dists[index] = (1.0D / 0.0D);
/*  228:     */       }
/*  229: 425 */       int clas = Utils.maxIndex(pred);
/*  230: 426 */       if ((int)before.classValue() != clas) {
/*  231: 427 */         noises_relationInsts.add(datum);
/*  232:     */       } else {
/*  233: 429 */         after_relationInsts.add(datum);
/*  234:     */       }
/*  235:     */     }
/*  236: 434 */     int relationValue = noises.attribute(1).addRelation(noises_relationInsts);
/*  237: 435 */     noises.setValue(0, before.value(0));
/*  238: 436 */     noises.setValue(1, relationValue);
/*  239: 437 */     noises.setValue(2, before.classValue());
/*  240:     */     
/*  241: 439 */     relationValue = after.attribute(1).addRelation(after_relationInsts);
/*  242: 440 */     after.setValue(0, before.value(0));
/*  243: 441 */     after.setValue(1, relationValue);
/*  244: 442 */     after.setValue(2, before.classValue());
/*  245: 444 */     if (Utils.gr(noises.relationalValue(1).sumOfWeights(), 0.0D))
/*  246:     */     {
/*  247: 445 */       for (int i = 0; i < this.m_Dimension; i++)
/*  248:     */       {
/*  249: 446 */         this.m_NoiseM[pos][i] = noises.relationalValue(1).meanOrMode(i);
/*  250: 447 */         this.m_NoiseV[pos][i] = noises.relationalValue(1).variance(i);
/*  251: 448 */         if (Utils.eq(this.m_NoiseV[pos][i], 0.0D)) {
/*  252: 449 */           this.m_NoiseV[pos][i] = m_ZERO;
/*  253:     */         }
/*  254:     */       }
/*  255:     */     }
/*  256:     */     else
/*  257:     */     {
/*  258: 457 */       this.m_NoiseM[pos] = null;
/*  259: 458 */       this.m_NoiseV[pos] = null;
/*  260:     */     }
/*  261: 461 */     return after;
/*  262:     */   }
/*  263:     */   
/*  264:     */   private double distance(Instance first, double[] mean, double[] var, int pos)
/*  265:     */   {
/*  266: 473 */     double distance = 0.0D;
/*  267: 475 */     for (int i = 0; i < this.m_Dimension; i++) {
/*  268: 477 */       if (first.attribute(i).isNumeric()) {
/*  269: 478 */         if (!first.isMissing(i))
/*  270:     */         {
/*  271: 479 */           double diff = first.value(i) - mean[i];
/*  272: 480 */           if (Utils.gr(var[i], m_ZERO)) {
/*  273: 481 */             distance += this.m_Change[pos][i] * var[i] * diff * diff;
/*  274:     */           } else {
/*  275: 483 */             distance += this.m_Change[pos][i] * diff * diff;
/*  276:     */           }
/*  277:     */         }
/*  278: 486 */         else if (Utils.gr(var[i], m_ZERO))
/*  279:     */         {
/*  280: 487 */           distance += this.m_Change[pos][i] * var[i];
/*  281:     */         }
/*  282:     */         else
/*  283:     */         {
/*  284: 489 */           distance += this.m_Change[pos][i] * 1.0D;
/*  285:     */         }
/*  286:     */       }
/*  287:     */     }
/*  288: 496 */     return distance;
/*  289:     */   }
/*  290:     */   
/*  291:     */   private void updateMinMax(Instance ex)
/*  292:     */   {
/*  293: 506 */     Instances insts = ex.relationalValue(1);
/*  294: 507 */     for (int j = 0; j < this.m_Dimension; j++) {
/*  295: 508 */       if (insts.attribute(j).isNumeric()) {
/*  296: 509 */         for (int k = 0; k < insts.numInstances(); k++)
/*  297:     */         {
/*  298: 510 */           Instance ins = insts.instance(k);
/*  299: 511 */           if (!ins.isMissing(j)) {
/*  300: 512 */             if (Double.isNaN(this.m_MinArray[j]))
/*  301:     */             {
/*  302: 513 */               this.m_MinArray[j] = ins.value(j);
/*  303: 514 */               this.m_MaxArray[j] = ins.value(j);
/*  304:     */             }
/*  305: 516 */             else if (ins.value(j) < this.m_MinArray[j])
/*  306:     */             {
/*  307: 517 */               this.m_MinArray[j] = ins.value(j);
/*  308:     */             }
/*  309: 518 */             else if (ins.value(j) > this.m_MaxArray[j])
/*  310:     */             {
/*  311: 519 */               this.m_MaxArray[j] = ins.value(j);
/*  312:     */             }
/*  313:     */           }
/*  314:     */         }
/*  315:     */       }
/*  316:     */     }
/*  317:     */   }
/*  318:     */   
/*  319:     */   private Instance scale(Instance before)
/*  320:     */     throws Exception
/*  321:     */   {
/*  322: 538 */     Instances afterInsts = before.relationalValue(1).stringFreeStructure();
/*  323: 539 */     Instance after = new DenseInstance(before.numAttributes());
/*  324: 540 */     after.setDataset(this.m_Attributes);
/*  325: 542 */     for (int i = 0; i < before.relationalValue(1).numInstances(); i++)
/*  326:     */     {
/*  327: 543 */       Instance datum = before.relationalValue(1).instance(i);
/*  328: 544 */       Instance inst = (Instance)datum.copy();
/*  329: 546 */       for (int j = 0; j < this.m_Dimension; j++) {
/*  330: 547 */         if (before.relationalValue(1).attribute(j).isNumeric()) {
/*  331: 548 */           inst.setValue(j, (datum.value(j) - this.m_MinArray[j]) / (this.m_MaxArray[j] - this.m_MinArray[j]));
/*  332:     */         }
/*  333:     */       }
/*  334: 552 */       afterInsts.add(inst);
/*  335:     */     }
/*  336: 555 */     int attValue = after.attribute(1).addRelation(afterInsts);
/*  337: 556 */     after.setValue(0, before.value(0));
/*  338: 557 */     after.setValue(1, attValue);
/*  339: 558 */     after.setValue(2, before.value(2));
/*  340:     */     
/*  341: 560 */     return after;
/*  342:     */   }
/*  343:     */   
/*  344:     */   public void findWeights(int row, double[][] mean)
/*  345:     */   {
/*  346: 573 */     double[] neww = new double[this.m_Dimension];
/*  347: 574 */     double[] oldw = new double[this.m_Dimension];
/*  348: 575 */     System.arraycopy(this.m_Change[row], 0, neww, 0, this.m_Dimension);
/*  349:     */     
/*  350:     */ 
/*  351: 578 */     double newresult = target(neww, mean, row, this.m_Class);
/*  352: 579 */     double result = (1.0D / 0.0D);
/*  353: 580 */     double rate = 0.05D;
/*  354: 581 */     if (this.m_Rate != -1.0D) {
/*  355: 582 */       rate = this.m_Rate;
/*  356:     */     }
/*  357: 585 */     while (Utils.gr(result - newresult, 1.0E-045D))
/*  358:     */     {
/*  359: 586 */       oldw = neww;
/*  360: 587 */       neww = new double[this.m_Dimension];
/*  361:     */       
/*  362: 589 */       double[] delta = delta(oldw, mean, row, this.m_Class);
/*  363: 591 */       for (int i = 0; i < this.m_Dimension; i++) {
/*  364: 592 */         if (Utils.gr(this.m_Variance[row][i], 0.0D)) {
/*  365: 593 */           oldw[i] += rate * delta[i];
/*  366:     */         }
/*  367:     */       }
/*  368: 597 */       result = newresult;
/*  369: 598 */       newresult = target(neww, mean, row, this.m_Class);
/*  370: 601 */       while (Utils.gr(newresult, result)) {
/*  371: 603 */         if (this.m_Rate == -1.0D)
/*  372:     */         {
/*  373: 604 */           rate *= 0.5D;
/*  374: 605 */           for (int i = 0; i < this.m_Dimension; i++) {
/*  375: 606 */             if (Utils.gr(this.m_Variance[row][i], 0.0D)) {
/*  376: 607 */               oldw[i] += rate * delta[i];
/*  377:     */             }
/*  378:     */           }
/*  379: 610 */           newresult = target(neww, mean, row, this.m_Class);
/*  380:     */         }
/*  381:     */         else
/*  382:     */         {
/*  383: 612 */           for (int i = 0; i < this.m_Dimension; i++) {
/*  384: 613 */             neww[i] = oldw[i];
/*  385:     */           }
/*  386:     */           break label308;
/*  387:     */         }
/*  388:     */       }
/*  389:     */     }
/*  390:     */     label308:
/*  391: 620 */     this.m_Change[row] = neww;
/*  392:     */   }
/*  393:     */   
/*  394:     */   private double[] delta(double[] x, double[][] X, int rowpos, double[] Y)
/*  395:     */   {
/*  396: 635 */     double y = Y[rowpos];
/*  397:     */     
/*  398: 637 */     double[] delta = new double[this.m_Dimension];
/*  399: 638 */     for (int h = 0; h < this.m_Dimension; h++) {
/*  400: 639 */       delta[h] = 0.0D;
/*  401:     */     }
/*  402: 642 */     for (int i = 0; i < X.length; i++) {
/*  403: 643 */       if ((i != rowpos) && (X[i] != null))
/*  404:     */       {
/*  405: 644 */         double var = y == Y[i] ? 0.0D : Math.sqrt(this.m_Dimension - 1.0D);
/*  406: 645 */         double distance = 0.0D;
/*  407: 646 */         for (int j = 0; j < this.m_Dimension; j++) {
/*  408: 647 */           if (Utils.gr(this.m_Variance[rowpos][j], 0.0D)) {
/*  409: 648 */             distance += x[j] * (X[rowpos][j] - X[i][j]) * (X[rowpos][j] - X[i][j]);
/*  410:     */           }
/*  411:     */         }
/*  412: 652 */         distance = Math.sqrt(distance);
/*  413: 653 */         if (distance != 0.0D) {
/*  414: 654 */           for (int k = 0; k < this.m_Dimension; k++) {
/*  415: 655 */             if (this.m_Variance[rowpos][k] > 0.0D) {
/*  416: 656 */               delta[k] += (var / distance - 1.0D) * 0.5D * (X[rowpos][k] - X[i][k]) * (X[rowpos][k] - X[i][k]);
/*  417:     */             }
/*  418:     */           }
/*  419:     */         }
/*  420:     */       }
/*  421:     */     }
/*  422: 664 */     return delta;
/*  423:     */   }
/*  424:     */   
/*  425:     */   public double target(double[] x, double[][] X, int rowpos, double[] Y)
/*  426:     */   {
/*  427: 682 */     double y = Y[rowpos];double result = 0.0D;
/*  428: 684 */     for (int i = 0; i < X.length; i++) {
/*  429: 685 */       if ((i != rowpos) && (X[i] != null))
/*  430:     */       {
/*  431: 686 */         double var = y == Y[i] ? 0.0D : Math.sqrt(this.m_Dimension - 1.0D);
/*  432: 687 */         double f = 0.0D;
/*  433: 688 */         for (int j = 0; j < this.m_Dimension; j++) {
/*  434: 689 */           if (Utils.gr(this.m_Variance[rowpos][j], 0.0D)) {
/*  435: 690 */             f += x[j] * (X[rowpos][j] - X[i][j]) * (X[rowpos][j] - X[i][j]);
/*  436:     */           }
/*  437:     */         }
/*  438: 694 */         f = Math.sqrt(f);
/*  439: 696 */         if (Double.isInfinite(f)) {
/*  440: 697 */           System.exit(1);
/*  441:     */         }
/*  442: 699 */         result += 0.5D * (f - var) * (f - var);
/*  443:     */       }
/*  444:     */     }
/*  445: 703 */     return result;
/*  446:     */   }
/*  447:     */   
/*  448:     */   public double classifyInstance(Instance ex)
/*  449:     */     throws Exception
/*  450:     */   {
/*  451: 718 */     ex = scale(ex);
/*  452:     */     
/*  453: 720 */     double[] var = new double[this.m_Dimension];
/*  454: 721 */     for (int i = 0; i < this.m_Dimension; i++) {
/*  455: 722 */       var[i] = ex.relationalValue(1).variance(i);
/*  456:     */     }
/*  457: 726 */     double[] kullback = new double[this.m_Class.length];
/*  458:     */     
/*  459:     */ 
/*  460: 729 */     double[] predict = new double[this.m_NumClasses];
/*  461: 730 */     for (int h = 0; h < predict.length; h++) {
/*  462: 731 */       predict[h] = 0.0D;
/*  463:     */     }
/*  464: 733 */     ex = cleanse(ex);
/*  465: 735 */     if (ex.relationalValue(1).numInstances() == 0)
/*  466:     */     {
/*  467: 736 */       if (getDebug()) {
/*  468: 737 */         System.out.println("???Whole exemplar falls into ambiguous area!");
/*  469:     */       }
/*  470: 739 */       return 1.0D;
/*  471:     */     }
/*  472: 742 */     double[] mean = new double[this.m_Dimension];
/*  473: 743 */     for (int i = 0; i < this.m_Dimension; i++) {
/*  474: 744 */       mean[i] = ex.relationalValue(1).meanOrMode(i);
/*  475:     */     }
/*  476: 748 */     for (int h = 0; h < var.length; h++) {
/*  477: 749 */       if (Utils.eq(var[h], 0.0D)) {
/*  478: 750 */         var[h] = m_ZERO;
/*  479:     */       }
/*  480:     */     }
/*  481: 754 */     for (int i = 0; i < this.m_Class.length; i++) {
/*  482: 755 */       if (this.m_ValidM[i] != null) {
/*  483: 756 */         kullback[i] = kullback(mean, this.m_ValidM[i], var, this.m_Variance[i], i);
/*  484:     */       } else {
/*  485: 758 */         kullback[i] = (1.0D / 0.0D);
/*  486:     */       }
/*  487:     */     }
/*  488: 762 */     for (int j = 0; j < this.m_Neighbour; j++)
/*  489:     */     {
/*  490: 763 */       int pos = Utils.minIndex(kullback);
/*  491: 764 */       predict[((int)this.m_Class[pos])] += this.m_Weights[pos];
/*  492: 765 */       kullback[pos] = (1.0D / 0.0D);
/*  493:     */     }
/*  494: 768 */     if (getDebug()) {
/*  495: 769 */       System.out.println("???There are still some unambiguous instances in this exemplar! Predicted as: " + Utils.maxIndex(predict));
/*  496:     */     }
/*  497: 773 */     return Utils.maxIndex(predict);
/*  498:     */   }
/*  499:     */   
/*  500:     */   public Instance cleanse(Instance before)
/*  501:     */     throws Exception
/*  502:     */   {
/*  503: 785 */     Instances insts = before.relationalValue(1).stringFreeStructure();
/*  504: 786 */     Instance after = new DenseInstance(before.numAttributes());
/*  505: 787 */     after.setDataset(this.m_Attributes);
/*  506: 789 */     for (int g = 0; g < before.relationalValue(1).numInstances(); g++)
/*  507:     */     {
/*  508: 790 */       Instance datum = before.relationalValue(1).instance(g);
/*  509: 791 */       double[] minNoiDists = new double[this.m_Choose];
/*  510: 792 */       double[] minValDists = new double[this.m_Choose];
/*  511:     */       
/*  512: 794 */       double[] nDist = new double[this.m_Mean.length];
/*  513: 795 */       double[] vDist = new double[this.m_Mean.length];
/*  514: 797 */       for (int h = 0; h < this.m_Mean.length; h++)
/*  515:     */       {
/*  516: 798 */         if (this.m_ValidM[h] == null) {
/*  517: 799 */           vDist[h] = (1.0D / 0.0D);
/*  518:     */         } else {
/*  519: 801 */           vDist[h] = distance(datum, this.m_ValidM[h], this.m_ValidV[h], h);
/*  520:     */         }
/*  521: 804 */         if (this.m_NoiseM[h] == null) {
/*  522: 805 */           nDist[h] = (1.0D / 0.0D);
/*  523:     */         } else {
/*  524: 807 */           nDist[h] = distance(datum, this.m_NoiseM[h], this.m_NoiseV[h], h);
/*  525:     */         }
/*  526:     */       }
/*  527: 811 */       for (int k = 0; k < this.m_Choose; k++)
/*  528:     */       {
/*  529: 812 */         int pos = Utils.minIndex(vDist);
/*  530: 813 */         minValDists[k] = vDist[pos];
/*  531: 814 */         vDist[pos] = (1.0D / 0.0D);
/*  532: 815 */         pos = Utils.minIndex(nDist);
/*  533: 816 */         minNoiDists[k] = nDist[pos];
/*  534: 817 */         nDist[pos] = (1.0D / 0.0D);
/*  535:     */       }
/*  536: 820 */       int x = 0;int y = 0;
/*  537: 821 */       while (x + y < this.m_Choose) {
/*  538: 822 */         if (minValDists[x] <= minNoiDists[y]) {
/*  539: 824 */           x++;
/*  540:     */         } else {
/*  541: 827 */           y++;
/*  542:     */         }
/*  543:     */       }
/*  544: 830 */       if (x >= y) {
/*  545: 831 */         insts.add(datum);
/*  546:     */       }
/*  547:     */     }
/*  548: 836 */     after.setValue(0, before.value(0));
/*  549: 837 */     after.setValue(1, after.attribute(1).addRelation(insts));
/*  550: 838 */     after.setValue(2, before.value(2));
/*  551:     */     
/*  552: 840 */     return after;
/*  553:     */   }
/*  554:     */   
/*  555:     */   public double kullback(double[] mu1, double[] mu2, double[] var1, double[] var2, int pos)
/*  556:     */   {
/*  557: 865 */     int p = mu1.length;
/*  558: 866 */     double result = 0.0D;
/*  559: 868 */     for (int y = 0; y < p; y++) {
/*  560: 869 */       if ((Utils.gr(var1[y], 0.0D)) && (Utils.gr(var2[y], 0.0D))) {
/*  561: 870 */         result += Math.log(Math.sqrt(var2[y] / var1[y])) + var1[y] / (2.0D * var2[y]) + this.m_Change[pos][y] * (mu1[y] - mu2[y]) * (mu1[y] - mu2[y]) / (2.0D * var2[y]) - 0.5D;
/*  562:     */       }
/*  563:     */     }
/*  564: 876 */     return result;
/*  565:     */   }
/*  566:     */   
/*  567:     */   public Enumeration<Option> listOptions()
/*  568:     */   {
/*  569: 887 */     Vector<Option> result = new Vector(3);
/*  570:     */     
/*  571: 889 */     result.addElement(new Option("\tSet number of nearest neighbour for prediction\n\t(default 1)", "K", 1, "-K <number of neighbours>"));
/*  572:     */     
/*  573:     */ 
/*  574:     */ 
/*  575: 893 */     result.addElement(new Option("\tSet number of nearest neighbour for cleansing the training data\n\t(default 1)", "S", 1, "-S <number of neighbours>"));
/*  576:     */     
/*  577:     */ 
/*  578:     */ 
/*  579: 897 */     result.addElement(new Option("\tSet number of nearest neighbour for cleansing the testing data\n\t(default 1)", "E", 1, "-E <number of neighbours>"));
/*  580:     */     
/*  581:     */ 
/*  582:     */ 
/*  583: 901 */     result.addAll(Collections.list(super.listOptions()));
/*  584:     */     
/*  585: 903 */     return result.elements();
/*  586:     */   }
/*  587:     */   
/*  588:     */   public void setOptions(String[] options)
/*  589:     */     throws Exception
/*  590:     */   {
/*  591: 939 */     String numNeighbourString = Utils.getOption('K', options);
/*  592: 940 */     if (numNeighbourString.length() != 0) {
/*  593: 941 */       setNumNeighbours(Integer.parseInt(numNeighbourString));
/*  594:     */     } else {
/*  595: 943 */       setNumNeighbours(1);
/*  596:     */     }
/*  597: 946 */     numNeighbourString = Utils.getOption('S', options);
/*  598: 947 */     if (numNeighbourString.length() != 0) {
/*  599: 948 */       setNumTrainingNoises(Integer.parseInt(numNeighbourString));
/*  600:     */     } else {
/*  601: 950 */       setNumTrainingNoises(1);
/*  602:     */     }
/*  603: 953 */     numNeighbourString = Utils.getOption('E', options);
/*  604: 954 */     if (numNeighbourString.length() != 0) {
/*  605: 955 */       setNumTestingNoises(Integer.parseInt(numNeighbourString));
/*  606:     */     } else {
/*  607: 957 */       setNumTestingNoises(1);
/*  608:     */     }
/*  609: 960 */     super.setOptions(options);
/*  610:     */     
/*  611: 962 */     Utils.checkForRemainingOptions(options);
/*  612:     */   }
/*  613:     */   
/*  614:     */   public String[] getOptions()
/*  615:     */   {
/*  616: 973 */     Vector<String> result = new Vector();
/*  617:     */     
/*  618: 975 */     result.add("-K");
/*  619: 976 */     result.add("" + getNumNeighbours());
/*  620:     */     
/*  621: 978 */     result.add("-S");
/*  622: 979 */     result.add("" + getNumTrainingNoises());
/*  623:     */     
/*  624: 981 */     result.add("-E");
/*  625: 982 */     result.add("" + getNumTestingNoises());
/*  626:     */     
/*  627: 984 */     Collections.addAll(result, super.getOptions());
/*  628:     */     
/*  629: 986 */     return (String[])result.toArray(new String[result.size()]);
/*  630:     */   }
/*  631:     */   
/*  632:     */   public String numNeighboursTipText()
/*  633:     */   {
/*  634: 996 */     return "The number of nearest neighbours to the estimate the class prediction of test bags.";
/*  635:     */   }
/*  636:     */   
/*  637:     */   public void setNumNeighbours(int numNeighbour)
/*  638:     */   {
/*  639:1006 */     this.m_Neighbour = numNeighbour;
/*  640:     */   }
/*  641:     */   
/*  642:     */   public int getNumNeighbours()
/*  643:     */   {
/*  644:1016 */     return this.m_Neighbour;
/*  645:     */   }
/*  646:     */   
/*  647:     */   public String numTrainingNoisesTipText()
/*  648:     */   {
/*  649:1026 */     return "The number of nearest neighbour instances in the selection of noises in the training data.";
/*  650:     */   }
/*  651:     */   
/*  652:     */   public void setNumTrainingNoises(int numTraining)
/*  653:     */   {
/*  654:1036 */     this.m_Select = numTraining;
/*  655:     */   }
/*  656:     */   
/*  657:     */   public int getNumTrainingNoises()
/*  658:     */   {
/*  659:1046 */     return this.m_Select;
/*  660:     */   }
/*  661:     */   
/*  662:     */   public String numTestingNoisesTipText()
/*  663:     */   {
/*  664:1056 */     return "The number of nearest neighbour instances in the selection of noises in the test data.";
/*  665:     */   }
/*  666:     */   
/*  667:     */   public int getNumTestingNoises()
/*  668:     */   {
/*  669:1066 */     return this.m_Choose;
/*  670:     */   }
/*  671:     */   
/*  672:     */   public void setNumTestingNoises(int numTesting)
/*  673:     */   {
/*  674:1076 */     this.m_Choose = numTesting;
/*  675:     */   }
/*  676:     */   
/*  677:     */   public String getRevision()
/*  678:     */   {
/*  679:1086 */     return RevisionUtils.extract("$Revision: 10369 $");
/*  680:     */   }
/*  681:     */   
/*  682:     */   public static void main(String[] args)
/*  683:     */   {
/*  684:1095 */     runClassifier(new MINND(), args);
/*  685:     */   }
/*  686:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.MINND
 * JD-Core Version:    0.7.0.1
 */