/*    1:     */ package weka.classifiers.meta;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.classifiers.AbstractClassifier;
/*   11:     */ import weka.classifiers.Classifier;
/*   12:     */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*   13:     */ import weka.classifiers.UpdateableClassifier;
/*   14:     */ import weka.classifiers.rules.ZeroR;
/*   15:     */ import weka.classifiers.trees.DecisionStump;
/*   16:     */ import weka.core.Attribute;
/*   17:     */ import weka.core.Capabilities;
/*   18:     */ import weka.core.Capabilities.Capability;
/*   19:     */ import weka.core.Instance;
/*   20:     */ import weka.core.Instances;
/*   21:     */ import weka.core.Option;
/*   22:     */ import weka.core.RevisionHandler;
/*   23:     */ import weka.core.RevisionUtils;
/*   24:     */ import weka.core.SelectedTag;
/*   25:     */ import weka.core.Tag;
/*   26:     */ import weka.core.TechnicalInformation;
/*   27:     */ import weka.core.TechnicalInformation.Field;
/*   28:     */ import weka.core.TechnicalInformation.Type;
/*   29:     */ import weka.core.TechnicalInformationHandler;
/*   30:     */ import weka.core.Utils;
/*   31:     */ import weka.core.WeightedInstancesHandler;
/*   32:     */ 
/*   33:     */ public class RacedIncrementalLogitBoost
/*   34:     */   extends RandomizableSingleClassifierEnhancer
/*   35:     */   implements UpdateableClassifier, TechnicalInformationHandler
/*   36:     */ {
/*   37:     */   static final long serialVersionUID = 908598343772170052L;
/*   38:     */   public static final int PRUNETYPE_NONE = 0;
/*   39:     */   public static final int PRUNETYPE_LOGLIKELIHOOD = 1;
/*   40: 155 */   public static final Tag[] TAGS_PRUNETYPE = { new Tag(0, "No pruning"), new Tag(1, "Log likelihood pruning") };
/*   41:     */   protected ArrayList<Committee> m_committees;
/*   42: 163 */   protected int m_PruningType = 1;
/*   43: 166 */   protected boolean m_UseResampling = false;
/*   44:     */   protected int m_NumClasses;
/*   45:     */   protected static final double Z_MAX = 4.0D;
/*   46:     */   protected Instances m_NumericClassData;
/*   47:     */   protected Attribute m_ClassAttribute;
/*   48: 181 */   protected int m_minChunkSize = 500;
/*   49: 184 */   protected int m_maxChunkSize = 2000;
/*   50: 187 */   protected int m_validationChunkSize = 1000;
/*   51:     */   protected int m_numInstancesConsumed;
/*   52:     */   protected Instances m_validationSet;
/*   53:     */   protected Instances m_currentSet;
/*   54:     */   protected Committee m_bestCommittee;
/*   55: 202 */   protected ZeroR m_zeroR = null;
/*   56:     */   protected boolean m_validationSetChanged;
/*   57:     */   protected int m_maxBatchSizeRequired;
/*   58: 211 */   protected Random m_RandomInstance = null;
/*   59:     */   
/*   60:     */   public RacedIncrementalLogitBoost()
/*   61:     */   {
/*   62: 218 */     this.m_Classifier = new DecisionStump();
/*   63:     */   }
/*   64:     */   
/*   65:     */   protected String defaultClassifierString()
/*   66:     */   {
/*   67: 229 */     return "weka.classifiers.trees.DecisionStump";
/*   68:     */   }
/*   69:     */   
/*   70:     */   protected class Committee
/*   71:     */     implements Serializable, RevisionHandler
/*   72:     */   {
/*   73:     */     static final long serialVersionUID = 5559880306684082199L;
/*   74:     */     protected int m_chunkSize;
/*   75:     */     protected int m_instancesConsumed;
/*   76:     */     protected ArrayList<Classifier[]> m_models;
/*   77:     */     protected double m_lastValidationError;
/*   78:     */     protected double m_lastLogLikelihood;
/*   79:     */     protected boolean m_modelHasChanged;
/*   80:     */     protected boolean m_modelHasChangedLL;
/*   81:     */     protected double[][] m_validationFs;
/*   82:     */     protected double[][] m_newValidationFs;
/*   83:     */     
/*   84:     */     public Committee(int chunkSize)
/*   85:     */     {
/*   86: 260 */       this.m_chunkSize = chunkSize;
/*   87: 261 */       this.m_instancesConsumed = 0;
/*   88: 262 */       this.m_models = new ArrayList();
/*   89: 263 */       this.m_lastValidationError = 1.0D;
/*   90: 264 */       this.m_lastLogLikelihood = 1.7976931348623157E+308D;
/*   91: 265 */       this.m_modelHasChanged = true;
/*   92: 266 */       this.m_modelHasChangedLL = true;
/*   93: 267 */       this.m_validationFs = new double[RacedIncrementalLogitBoost.this.m_validationChunkSize][RacedIncrementalLogitBoost.this.m_NumClasses];
/*   94: 268 */       this.m_newValidationFs = new double[RacedIncrementalLogitBoost.this.m_validationChunkSize][RacedIncrementalLogitBoost.this.m_NumClasses];
/*   95:     */     }
/*   96:     */     
/*   97:     */     public boolean update()
/*   98:     */       throws Exception
/*   99:     */     {
/*  100: 279 */       boolean hasChanged = false;
/*  101: 280 */       while (RacedIncrementalLogitBoost.this.m_currentSet.numInstances() - this.m_instancesConsumed >= this.m_chunkSize)
/*  102:     */       {
/*  103: 281 */         Classifier[] newModel = boost(new Instances(RacedIncrementalLogitBoost.this.m_currentSet, this.m_instancesConsumed, this.m_chunkSize));
/*  104: 283 */         for (int i = 0; i < RacedIncrementalLogitBoost.this.m_validationSet.numInstances(); i++) {
/*  105: 284 */           this.m_newValidationFs[i] = updateFS(RacedIncrementalLogitBoost.this.m_validationSet.instance(i), newModel, this.m_validationFs[i]);
/*  106:     */         }
/*  107: 287 */         this.m_models.add(newModel);
/*  108: 288 */         this.m_instancesConsumed += this.m_chunkSize;
/*  109: 289 */         hasChanged = true;
/*  110:     */       }
/*  111: 291 */       if (hasChanged)
/*  112:     */       {
/*  113: 292 */         this.m_modelHasChanged = true;
/*  114: 293 */         this.m_modelHasChangedLL = true;
/*  115:     */       }
/*  116: 295 */       return hasChanged;
/*  117:     */     }
/*  118:     */     
/*  119:     */     public void resetConsumed()
/*  120:     */     {
/*  121: 301 */       this.m_instancesConsumed = 0;
/*  122:     */     }
/*  123:     */     
/*  124:     */     public void pruneLastModel()
/*  125:     */     {
/*  126: 307 */       if (this.m_models.size() > 0)
/*  127:     */       {
/*  128: 308 */         this.m_models.remove(this.m_models.size() - 1);
/*  129: 309 */         this.m_modelHasChanged = true;
/*  130: 310 */         this.m_modelHasChangedLL = true;
/*  131:     */       }
/*  132:     */     }
/*  133:     */     
/*  134:     */     public void keepLastModel()
/*  135:     */       throws Exception
/*  136:     */     {
/*  137: 321 */       this.m_validationFs = this.m_newValidationFs;
/*  138: 322 */       this.m_newValidationFs = new double[RacedIncrementalLogitBoost.this.m_validationChunkSize][RacedIncrementalLogitBoost.this.m_NumClasses];
/*  139: 323 */       this.m_modelHasChanged = true;
/*  140: 324 */       this.m_modelHasChangedLL = true;
/*  141:     */     }
/*  142:     */     
/*  143:     */     public double logLikelihood()
/*  144:     */       throws Exception
/*  145:     */     {
/*  146: 335 */       if (this.m_modelHasChangedLL)
/*  147:     */       {
/*  148: 338 */         double llsum = 0.0D;
/*  149: 339 */         for (int i = 0; i < RacedIncrementalLogitBoost.this.m_validationSet.numInstances(); i++)
/*  150:     */         {
/*  151: 340 */           Instance inst = RacedIncrementalLogitBoost.this.m_validationSet.instance(i);
/*  152: 341 */           llsum += logLikelihood(this.m_validationFs[i], (int)inst.classValue());
/*  153:     */         }
/*  154: 343 */         this.m_lastLogLikelihood = (llsum / RacedIncrementalLogitBoost.this.m_validationSet.numInstances());
/*  155: 344 */         this.m_modelHasChangedLL = false;
/*  156:     */       }
/*  157: 346 */       return this.m_lastLogLikelihood;
/*  158:     */     }
/*  159:     */     
/*  160:     */     public double logLikelihoodAfter()
/*  161:     */       throws Exception
/*  162:     */     {
/*  163: 359 */       double llsum = 0.0D;
/*  164: 360 */       for (int i = 0; i < RacedIncrementalLogitBoost.this.m_validationSet.numInstances(); i++)
/*  165:     */       {
/*  166: 361 */         Instance inst = RacedIncrementalLogitBoost.this.m_validationSet.instance(i);
/*  167: 362 */         llsum += logLikelihood(this.m_newValidationFs[i], (int)inst.classValue());
/*  168:     */       }
/*  169: 364 */       return llsum / RacedIncrementalLogitBoost.this.m_validationSet.numInstances();
/*  170:     */     }
/*  171:     */     
/*  172:     */     private double logLikelihood(double[] Fs, int classIndex)
/*  173:     */       throws Exception
/*  174:     */     {
/*  175: 377 */       return -Math.log(distributionForInstance(Fs)[classIndex]);
/*  176:     */     }
/*  177:     */     
/*  178:     */     public double validationError()
/*  179:     */       throws Exception
/*  180:     */     {
/*  181: 388 */       if (this.m_modelHasChanged)
/*  182:     */       {
/*  183: 391 */         int numIncorrect = 0;
/*  184: 392 */         for (int i = 0; i < RacedIncrementalLogitBoost.this.m_validationSet.numInstances(); i++)
/*  185:     */         {
/*  186: 393 */           Instance inst = RacedIncrementalLogitBoost.this.m_validationSet.instance(i);
/*  187: 394 */           if (classifyInstance(this.m_validationFs[i]) != inst.classValue()) {
/*  188: 395 */             numIncorrect++;
/*  189:     */           }
/*  190:     */         }
/*  191: 398 */         this.m_lastValidationError = (numIncorrect / RacedIncrementalLogitBoost.this.m_validationSet.numInstances());
/*  192:     */         
/*  193: 400 */         this.m_modelHasChanged = false;
/*  194:     */       }
/*  195: 402 */       return this.m_lastValidationError;
/*  196:     */     }
/*  197:     */     
/*  198:     */     public int chunkSize()
/*  199:     */     {
/*  200: 412 */       return this.m_chunkSize;
/*  201:     */     }
/*  202:     */     
/*  203:     */     public int committeeSize()
/*  204:     */     {
/*  205: 422 */       return this.m_models.size();
/*  206:     */     }
/*  207:     */     
/*  208:     */     public double classifyInstance(double[] Fs)
/*  209:     */       throws Exception
/*  210:     */     {
/*  211: 434 */       double[] dist = distributionForInstance(Fs);
/*  212:     */       
/*  213: 436 */       double max = 0.0D;
/*  214: 437 */       int maxIndex = 0;
/*  215: 439 */       for (int i = 0; i < dist.length; i++) {
/*  216: 440 */         if (dist[i] > max)
/*  217:     */         {
/*  218: 441 */           maxIndex = i;
/*  219: 442 */           max = dist[i];
/*  220:     */         }
/*  221:     */       }
/*  222: 445 */       if (max > 0.0D) {
/*  223: 446 */         return maxIndex;
/*  224:     */       }
/*  225: 448 */       return Utils.missingValue();
/*  226:     */     }
/*  227:     */     
/*  228:     */     public double classifyInstance(Instance instance)
/*  229:     */       throws Exception
/*  230:     */     {
/*  231: 461 */       double[] dist = distributionForInstance(instance);
/*  232: 462 */       switch (instance.classAttribute().type())
/*  233:     */       {
/*  234:     */       case 1: 
/*  235: 464 */         double max = 0.0D;
/*  236: 465 */         int maxIndex = 0;
/*  237: 467 */         for (int i = 0; i < dist.length; i++) {
/*  238: 468 */           if (dist[i] > max)
/*  239:     */           {
/*  240: 469 */             maxIndex = i;
/*  241: 470 */             max = dist[i];
/*  242:     */           }
/*  243:     */         }
/*  244: 473 */         if (max > 0.0D) {
/*  245: 474 */           return maxIndex;
/*  246:     */         }
/*  247: 476 */         return Utils.missingValue();
/*  248:     */       case 0: 
/*  249: 479 */         return dist[0];
/*  250:     */       }
/*  251: 481 */       return Utils.missingValue();
/*  252:     */     }
/*  253:     */     
/*  254:     */     public double[] distributionForInstance(double[] Fs)
/*  255:     */       throws Exception
/*  256:     */     {
/*  257: 495 */       double[] distribution = new double[RacedIncrementalLogitBoost.this.m_NumClasses];
/*  258: 496 */       for (int j = 0; j < RacedIncrementalLogitBoost.this.m_NumClasses; j++) {
/*  259: 497 */         distribution[j] = RacedIncrementalLogitBoost.RtoP(Fs, j);
/*  260:     */       }
/*  261: 499 */       return distribution;
/*  262:     */     }
/*  263:     */     
/*  264:     */     public double[] updateFS(Instance instance, Classifier[] newModel, double[] Fs)
/*  265:     */       throws Exception
/*  266:     */     {
/*  267: 514 */       instance = (Instance)instance.copy();
/*  268: 515 */       instance.setDataset(RacedIncrementalLogitBoost.this.m_NumericClassData);
/*  269:     */       
/*  270: 517 */       double[] Fi = new double[RacedIncrementalLogitBoost.this.m_NumClasses];
/*  271: 518 */       double Fsum = 0.0D;
/*  272: 519 */       for (int j = 0; j < RacedIncrementalLogitBoost.this.m_NumClasses; j++)
/*  273:     */       {
/*  274: 520 */         Fi[j] = newModel[j].classifyInstance(instance);
/*  275: 521 */         Fsum += Fi[j];
/*  276:     */       }
/*  277: 523 */       Fsum /= RacedIncrementalLogitBoost.this.m_NumClasses;
/*  278:     */       
/*  279: 525 */       double[] newFs = new double[Fs.length];
/*  280: 526 */       for (int j = 0; j < RacedIncrementalLogitBoost.this.m_NumClasses; j++) {
/*  281: 527 */         Fs[j] += (Fi[j] - Fsum) * (RacedIncrementalLogitBoost.this.m_NumClasses - 1) / RacedIncrementalLogitBoost.this.m_NumClasses;
/*  282:     */       }
/*  283: 529 */       return newFs;
/*  284:     */     }
/*  285:     */     
/*  286:     */     public double[] distributionForInstance(Instance instance)
/*  287:     */       throws Exception
/*  288:     */     {
/*  289: 541 */       instance = (Instance)instance.copy();
/*  290: 542 */       instance.setDataset(RacedIncrementalLogitBoost.this.m_NumericClassData);
/*  291: 543 */       double[] Fs = new double[RacedIncrementalLogitBoost.this.m_NumClasses];
/*  292: 544 */       for (int i = 0; i < this.m_models.size(); i++)
/*  293:     */       {
/*  294: 545 */         double[] Fi = new double[RacedIncrementalLogitBoost.this.m_NumClasses];
/*  295: 546 */         double Fsum = 0.0D;
/*  296: 547 */         Classifier[] model = (Classifier[])this.m_models.get(i);
/*  297: 548 */         for (int j = 0; j < RacedIncrementalLogitBoost.this.m_NumClasses; j++)
/*  298:     */         {
/*  299: 549 */           Fi[j] = model[j].classifyInstance(instance);
/*  300: 550 */           Fsum += Fi[j];
/*  301:     */         }
/*  302: 552 */         Fsum /= RacedIncrementalLogitBoost.this.m_NumClasses;
/*  303: 553 */         for (int j = 0; j < RacedIncrementalLogitBoost.this.m_NumClasses; j++) {
/*  304: 554 */           Fs[j] += (Fi[j] - Fsum) * (RacedIncrementalLogitBoost.this.m_NumClasses - 1) / RacedIncrementalLogitBoost.this.m_NumClasses;
/*  305:     */         }
/*  306:     */       }
/*  307: 557 */       double[] distribution = new double[RacedIncrementalLogitBoost.this.m_NumClasses];
/*  308: 558 */       for (int j = 0; j < RacedIncrementalLogitBoost.this.m_NumClasses; j++) {
/*  309: 559 */         distribution[j] = RacedIncrementalLogitBoost.RtoP(Fs, j);
/*  310:     */       }
/*  311: 561 */       return distribution;
/*  312:     */     }
/*  313:     */     
/*  314:     */     protected Classifier[] boost(Instances data)
/*  315:     */       throws Exception
/*  316:     */     {
/*  317: 573 */       Classifier[] newModel = AbstractClassifier.makeCopies(RacedIncrementalLogitBoost.this.m_Classifier, RacedIncrementalLogitBoost.this.m_NumClasses);
/*  318:     */       
/*  319:     */ 
/*  320:     */ 
/*  321: 577 */       Instances boostData = new Instances(data);
/*  322: 578 */       boostData.deleteWithMissingClass();
/*  323: 579 */       int numInstances = boostData.numInstances();
/*  324:     */       
/*  325:     */ 
/*  326: 582 */       int classIndex = data.classIndex();
/*  327: 583 */       boostData.setClassIndex(-1);
/*  328: 584 */       boostData.deleteAttributeAt(classIndex);
/*  329: 585 */       boostData.insertAttributeAt(new Attribute("'pseudo class'"), classIndex);
/*  330: 586 */       boostData.setClassIndex(classIndex);
/*  331: 587 */       double[][] trainFs = new double[numInstances][RacedIncrementalLogitBoost.this.m_NumClasses];
/*  332: 588 */       double[][] trainYs = new double[numInstances][RacedIncrementalLogitBoost.this.m_NumClasses];
/*  333: 589 */       for (int j = 0; j < RacedIncrementalLogitBoost.this.m_NumClasses; j++)
/*  334:     */       {
/*  335: 590 */         int i = 0;
/*  336: 590 */         for (int k = 0; i < numInstances; k++)
/*  337:     */         {
/*  338: 591 */           while (data.instance(k).classIsMissing()) {
/*  339: 592 */             k++;
/*  340:     */           }
/*  341: 594 */           trainYs[i][j] = (data.instance(k).classValue() == j ? 1.0D : 0.0D);i++;
/*  342:     */         }
/*  343:     */       }
/*  344: 599 */       for (int x = 0; x < this.m_models.size(); x++) {
/*  345: 600 */         for (int i = 0; i < numInstances; i++)
/*  346:     */         {
/*  347: 601 */           double[] pred = new double[RacedIncrementalLogitBoost.this.m_NumClasses];
/*  348: 602 */           double predSum = 0.0D;
/*  349: 603 */           Classifier[] model = (Classifier[])this.m_models.get(x);
/*  350: 604 */           for (int j = 0; j < RacedIncrementalLogitBoost.this.m_NumClasses; j++)
/*  351:     */           {
/*  352: 605 */             pred[j] = model[j].classifyInstance(boostData.instance(i));
/*  353: 606 */             predSum += pred[j];
/*  354:     */           }
/*  355: 608 */           predSum /= RacedIncrementalLogitBoost.this.m_NumClasses;
/*  356: 609 */           for (int j = 0; j < RacedIncrementalLogitBoost.this.m_NumClasses; j++) {
/*  357: 610 */             trainFs[i][j] += (pred[j] - predSum) * (RacedIncrementalLogitBoost.this.m_NumClasses - 1) / RacedIncrementalLogitBoost.this.m_NumClasses;
/*  358:     */           }
/*  359:     */         }
/*  360:     */       }
/*  361: 616 */       for (int j = 0; j < RacedIncrementalLogitBoost.this.m_NumClasses; j++)
/*  362:     */       {
/*  363: 619 */         for (int i = 0; i < numInstances; i++)
/*  364:     */         {
/*  365: 620 */           double p = RacedIncrementalLogitBoost.RtoP(trainFs[i], j);
/*  366: 621 */           Instance current = boostData.instance(i);
/*  367: 622 */           double actual = trainYs[i][j];
/*  368:     */           double z;
/*  369: 623 */           if (actual == 1.0D)
/*  370:     */           {
/*  371: 624 */             double z = 1.0D / p;
/*  372: 625 */             if (z > 4.0D) {
/*  373: 626 */               z = 4.0D;
/*  374:     */             }
/*  375:     */           }
/*  376: 628 */           else if (actual == 0.0D)
/*  377:     */           {
/*  378: 629 */             double z = -1.0D / (1.0D - p);
/*  379: 630 */             if (z < -4.0D) {
/*  380: 631 */               z = -4.0D;
/*  381:     */             }
/*  382:     */           }
/*  383:     */           else
/*  384:     */           {
/*  385: 634 */             z = (actual - p) / (p * (1.0D - p));
/*  386:     */           }
/*  387: 637 */           double w = (actual - p) / z;
/*  388: 638 */           current.setValue(classIndex, z);
/*  389: 639 */           current.setWeight(numInstances * w);
/*  390:     */         }
/*  391: 642 */         Instances trainData = boostData;
/*  392: 643 */         if (RacedIncrementalLogitBoost.this.m_UseResampling)
/*  393:     */         {
/*  394: 644 */           double[] weights = new double[boostData.numInstances()];
/*  395: 645 */           for (int kk = 0; kk < weights.length; kk++) {
/*  396: 646 */             weights[kk] = boostData.instance(kk).weight();
/*  397:     */           }
/*  398: 648 */           trainData = boostData.resampleWithWeights(RacedIncrementalLogitBoost.this.m_RandomInstance, weights);
/*  399:     */         }
/*  400: 652 */         newModel[j].buildClassifier(trainData);
/*  401:     */       }
/*  402: 655 */       return newModel;
/*  403:     */     }
/*  404:     */     
/*  405:     */     public String toString()
/*  406:     */     {
/*  407: 666 */       StringBuffer text = new StringBuffer();
/*  408:     */       
/*  409: 668 */       text.append("RacedIncrementalLogitBoost: Best committee on validation data\n");
/*  410:     */       
/*  411: 670 */       text.append("Base classifiers: \n");
/*  412: 672 */       for (int i = 0; i < this.m_models.size(); i++)
/*  413:     */       {
/*  414: 673 */         text.append("\nModel " + (i + 1));
/*  415: 674 */         Classifier[] cModels = (Classifier[])this.m_models.get(i);
/*  416: 675 */         for (int j = 0; j < RacedIncrementalLogitBoost.this.m_NumClasses; j++) {
/*  417: 676 */           text.append("\n\tClass " + (j + 1) + " (" + RacedIncrementalLogitBoost.this.m_ClassAttribute.name() + "=" + RacedIncrementalLogitBoost.this.m_ClassAttribute.value(j) + ")\n\n" + cModels[j].toString() + "\n");
/*  418:     */         }
/*  419:     */       }
/*  420: 681 */       text.append("Number of models: " + this.m_models.size() + "\n");
/*  421: 682 */       text.append("Chunk size per model: " + this.m_chunkSize + "\n");
/*  422:     */       
/*  423: 684 */       return text.toString();
/*  424:     */     }
/*  425:     */     
/*  426:     */     public String getRevision()
/*  427:     */     {
/*  428: 694 */       return RevisionUtils.extract("$Revision: 10374 $");
/*  429:     */     }
/*  430:     */   }
/*  431:     */   
/*  432:     */   public Capabilities getCapabilities()
/*  433:     */   {
/*  434: 705 */     Capabilities result = super.getCapabilities();
/*  435:     */     
/*  436:     */ 
/*  437: 708 */     result.disableAllClasses();
/*  438: 709 */     result.disableAllClassDependencies();
/*  439: 710 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  440:     */     
/*  441:     */ 
/*  442: 713 */     result.setMinimumNumberInstances(0);
/*  443:     */     
/*  444: 715 */     return result;
/*  445:     */   }
/*  446:     */   
/*  447:     */   public void buildClassifier(Instances data)
/*  448:     */     throws Exception
/*  449:     */   {
/*  450: 727 */     this.m_RandomInstance = new Random(this.m_Seed);
/*  451:     */     
/*  452:     */ 
/*  453: 730 */     int classIndex = data.classIndex();
/*  454:     */     
/*  455:     */ 
/*  456: 733 */     getCapabilities().testWithFail(data);
/*  457:     */     
/*  458:     */ 
/*  459: 736 */     data = new Instances(data);
/*  460: 737 */     data.deleteWithMissingClass();
/*  461: 739 */     if (this.m_Classifier == null) {
/*  462: 740 */       throw new Exception("A base classifier has not been specified!");
/*  463:     */     }
/*  464: 743 */     if ((!(this.m_Classifier instanceof WeightedInstancesHandler)) && (!this.m_UseResampling)) {
/*  465: 744 */       this.m_UseResampling = true;
/*  466:     */     }
/*  467: 747 */     this.m_NumClasses = data.numClasses();
/*  468: 748 */     this.m_ClassAttribute = data.classAttribute();
/*  469:     */     
/*  470:     */ 
/*  471: 751 */     Instances boostData = new Instances(data);
/*  472:     */     
/*  473:     */ 
/*  474: 754 */     boostData.setClassIndex(-1);
/*  475: 755 */     boostData.deleteAttributeAt(classIndex);
/*  476: 756 */     boostData.insertAttributeAt(new Attribute("'pseudo class'"), classIndex);
/*  477: 757 */     boostData.setClassIndex(classIndex);
/*  478: 758 */     this.m_NumericClassData = new Instances(boostData, 0);
/*  479:     */     
/*  480: 760 */     data.randomize(this.m_RandomInstance);
/*  481:     */     
/*  482:     */ 
/*  483: 763 */     int cSize = this.m_minChunkSize;
/*  484: 764 */     this.m_committees = new ArrayList();
/*  485: 765 */     while (cSize <= this.m_maxChunkSize)
/*  486:     */     {
/*  487: 766 */       this.m_committees.add(new Committee(cSize));
/*  488: 767 */       this.m_maxBatchSizeRequired = cSize;
/*  489: 768 */       cSize *= 2;
/*  490:     */     }
/*  491: 772 */     this.m_validationSet = new Instances(data, this.m_validationChunkSize);
/*  492: 773 */     this.m_currentSet = new Instances(data, this.m_maxBatchSizeRequired);
/*  493: 774 */     this.m_bestCommittee = null;
/*  494: 775 */     this.m_numInstancesConsumed = 0;
/*  495: 778 */     for (int i = 0; i < data.numInstances(); i++) {
/*  496: 779 */       updateClassifier(data.instance(i));
/*  497:     */     }
/*  498:     */   }
/*  499:     */   
/*  500:     */   public void updateClassifier(Instance instance)
/*  501:     */     throws Exception
/*  502:     */   {
/*  503: 792 */     this.m_numInstancesConsumed += 1;
/*  504: 794 */     if (this.m_validationSet.numInstances() < this.m_validationChunkSize)
/*  505:     */     {
/*  506: 795 */       this.m_validationSet.add(instance);
/*  507: 796 */       this.m_validationSetChanged = true;
/*  508:     */     }
/*  509:     */     else
/*  510:     */     {
/*  511: 798 */       this.m_currentSet.add(instance);
/*  512: 799 */       boolean hasChanged = false;
/*  513: 802 */       for (int i = 0; i < this.m_committees.size(); i++)
/*  514:     */       {
/*  515: 803 */         Committee c = (Committee)this.m_committees.get(i);
/*  516: 804 */         if (c.update())
/*  517:     */         {
/*  518: 806 */           hasChanged = true;
/*  519: 808 */           if (this.m_PruningType == 1)
/*  520:     */           {
/*  521: 809 */             double oldLL = c.logLikelihood();
/*  522: 810 */             double newLL = c.logLikelihoodAfter();
/*  523: 811 */             if ((newLL >= oldLL) && (c.committeeSize() > 1))
/*  524:     */             {
/*  525: 812 */               c.pruneLastModel();
/*  526: 813 */               if (this.m_Debug) {
/*  527: 814 */                 System.out.println("Pruning " + c.chunkSize() + " committee (" + oldLL + " < " + newLL + ")");
/*  528:     */               }
/*  529:     */             }
/*  530:     */             else
/*  531:     */             {
/*  532: 818 */               c.keepLastModel();
/*  533:     */             }
/*  534:     */           }
/*  535:     */           else
/*  536:     */           {
/*  537: 821 */             c.keepLastModel();
/*  538:     */           }
/*  539:     */         }
/*  540:     */       }
/*  541: 825 */       if (hasChanged)
/*  542:     */       {
/*  543: 827 */         if (this.m_Debug) {
/*  544: 828 */           System.out.println("After consuming " + this.m_numInstancesConsumed + " instances... (" + this.m_validationSet.numInstances() + " + " + this.m_currentSet.numInstances() + " instances currently in memory)");
/*  545:     */         }
/*  546: 834 */         double lowestError = 1.0D;
/*  547: 835 */         for (int i = 0; i < this.m_committees.size(); i++)
/*  548:     */         {
/*  549: 836 */           Committee c = (Committee)this.m_committees.get(i);
/*  550: 838 */           if (c.committeeSize() > 0)
/*  551:     */           {
/*  552: 840 */             double err = c.validationError();
/*  553: 841 */             double ll = c.logLikelihood();
/*  554: 843 */             if (this.m_Debug) {
/*  555: 844 */               System.out.println("Chunk size " + c.chunkSize() + " with " + c.committeeSize() + " models, has validation error of " + err + ", log likelihood of " + ll);
/*  556:     */             }
/*  557: 848 */             if (err < lowestError)
/*  558:     */             {
/*  559: 849 */               lowestError = err;
/*  560: 850 */               this.m_bestCommittee = c;
/*  561:     */             }
/*  562:     */           }
/*  563:     */         }
/*  564:     */       }
/*  565: 855 */       if (this.m_currentSet.numInstances() >= this.m_maxBatchSizeRequired)
/*  566:     */       {
/*  567: 856 */         this.m_currentSet = new Instances(this.m_currentSet, this.m_maxBatchSizeRequired);
/*  568: 859 */         for (int i = 0; i < this.m_committees.size(); i++)
/*  569:     */         {
/*  570: 860 */           Committee c = (Committee)this.m_committees.get(i);
/*  571: 861 */           c.resetConsumed();
/*  572:     */         }
/*  573:     */       }
/*  574:     */     }
/*  575:     */   }
/*  576:     */   
/*  577:     */   protected static double RtoP(double[] Fs, int j)
/*  578:     */     throws Exception
/*  579:     */   {
/*  580: 877 */     double maxF = -1.797693134862316E+308D;
/*  581: 878 */     for (double element : Fs) {
/*  582: 879 */       if (element > maxF) {
/*  583: 880 */         maxF = element;
/*  584:     */       }
/*  585:     */     }
/*  586: 883 */     double sum = 0.0D;
/*  587: 884 */     double[] probs = new double[Fs.length];
/*  588: 885 */     for (int i = 0; i < Fs.length; i++)
/*  589:     */     {
/*  590: 886 */       probs[i] = Math.exp(Fs[i] - maxF);
/*  591: 887 */       sum += probs[i];
/*  592:     */     }
/*  593: 889 */     if (sum == 0.0D) {
/*  594: 890 */       throw new Exception("Can't normalize");
/*  595:     */     }
/*  596: 892 */     return probs[j] / sum;
/*  597:     */   }
/*  598:     */   
/*  599:     */   public double[] distributionForInstance(Instance instance)
/*  600:     */     throws Exception
/*  601:     */   {
/*  602: 905 */     if (this.m_bestCommittee != null) {
/*  603: 906 */       return this.m_bestCommittee.distributionForInstance(instance);
/*  604:     */     }
/*  605: 908 */     if ((this.m_validationSetChanged) || (this.m_zeroR == null))
/*  606:     */     {
/*  607: 909 */       this.m_zeroR = new ZeroR();
/*  608: 910 */       this.m_zeroR.buildClassifier(this.m_validationSet);
/*  609: 911 */       this.m_validationSetChanged = false;
/*  610:     */     }
/*  611: 913 */     return this.m_zeroR.distributionForInstance(instance);
/*  612:     */   }
/*  613:     */   
/*  614:     */   public Enumeration<Option> listOptions()
/*  615:     */   {
/*  616: 925 */     Vector<Option> newVector = new Vector(5);
/*  617:     */     
/*  618: 927 */     newVector.addElement(new Option("\tMinimum size of chunks.\n\t(default 500)", "C", 1, "-C <num>"));
/*  619:     */     
/*  620:     */ 
/*  621: 930 */     newVector.addElement(new Option("\tMaximum size of chunks.\n\t(default 2000)", "M", 1, "-M <num>"));
/*  622:     */     
/*  623:     */ 
/*  624: 933 */     newVector.addElement(new Option("\tSize of validation set.\n\t(default 1000)", "V", 1, "-V <num>"));
/*  625:     */     
/*  626:     */ 
/*  627: 936 */     newVector.addElement(new Option("\tCommittee pruning to perform.\n\t0=none, 1=log likelihood (default)", "P", 1, "-P <pruning type>"));
/*  628:     */     
/*  629:     */ 
/*  630: 939 */     newVector.addElement(new Option("\tUse resampling for boosting.", "Q", 0, "-Q"));
/*  631:     */     
/*  632:     */ 
/*  633: 942 */     newVector.addAll(Collections.list(super.listOptions()));
/*  634:     */     
/*  635: 944 */     return newVector.elements();
/*  636:     */   }
/*  637:     */   
/*  638:     */   public void setOptions(String[] options)
/*  639:     */     throws Exception
/*  640:     */   {
/*  641:1013 */     String minChunkSize = Utils.getOption('C', options);
/*  642:1014 */     if (minChunkSize.length() != 0) {
/*  643:1015 */       setMinChunkSize(Integer.parseInt(minChunkSize));
/*  644:     */     } else {
/*  645:1017 */       setMinChunkSize(500);
/*  646:     */     }
/*  647:1020 */     String maxChunkSize = Utils.getOption('M', options);
/*  648:1021 */     if (maxChunkSize.length() != 0) {
/*  649:1022 */       setMaxChunkSize(Integer.parseInt(maxChunkSize));
/*  650:     */     } else {
/*  651:1024 */       setMaxChunkSize(2000);
/*  652:     */     }
/*  653:1027 */     String validationChunkSize = Utils.getOption('V', options);
/*  654:1028 */     if (validationChunkSize.length() != 0) {
/*  655:1029 */       setValidationChunkSize(Integer.parseInt(validationChunkSize));
/*  656:     */     } else {
/*  657:1031 */       setValidationChunkSize(1000);
/*  658:     */     }
/*  659:1034 */     String pruneType = Utils.getOption('P', options);
/*  660:1035 */     if (pruneType.length() != 0) {
/*  661:1036 */       setPruningType(new SelectedTag(Integer.parseInt(pruneType), TAGS_PRUNETYPE));
/*  662:     */     } else {
/*  663:1039 */       setPruningType(new SelectedTag(1, TAGS_PRUNETYPE));
/*  664:     */     }
/*  665:1042 */     setUseResampling(Utils.getFlag('Q', options));
/*  666:     */     
/*  667:1044 */     super.setOptions(options);
/*  668:     */     
/*  669:1046 */     Utils.checkForRemainingOptions(options);
/*  670:     */   }
/*  671:     */   
/*  672:     */   public String[] getOptions()
/*  673:     */   {
/*  674:1057 */     Vector<String> options = new Vector();
/*  675:1059 */     if (getUseResampling()) {
/*  676:1060 */       options.add("-Q");
/*  677:     */     }
/*  678:1062 */     options.add("-C");
/*  679:1063 */     options.add("" + getMinChunkSize());
/*  680:     */     
/*  681:1065 */     options.add("-M");
/*  682:1066 */     options.add("" + getMaxChunkSize());
/*  683:     */     
/*  684:1068 */     options.add("-V");
/*  685:1069 */     options.add("" + getValidationChunkSize());
/*  686:     */     
/*  687:1071 */     options.add("-P");
/*  688:1072 */     options.add("" + this.m_PruningType);
/*  689:     */     
/*  690:1074 */     Collections.addAll(options, super.getOptions());
/*  691:     */     
/*  692:1076 */     return (String[])options.toArray(new String[0]);
/*  693:     */   }
/*  694:     */   
/*  695:     */   public TechnicalInformation getTechnicalInformation()
/*  696:     */   {
/*  697:1090 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  698:1091 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank and Geoffrey Holmes and Richard Kirkby and Mark Hall");
/*  699:     */     
/*  700:1093 */     result.setValue(TechnicalInformation.Field.TITLE, " Racing committees for large datasets");
/*  701:1094 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the 5th International Conferenceon Discovery Science");
/*  702:     */     
/*  703:     */ 
/*  704:1097 */     result.setValue(TechnicalInformation.Field.YEAR, "2002");
/*  705:1098 */     result.setValue(TechnicalInformation.Field.PAGES, "153-164");
/*  706:1099 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  707:     */     
/*  708:1101 */     return result;
/*  709:     */   }
/*  710:     */   
/*  711:     */   public String globalInfo()
/*  712:     */   {
/*  713:1110 */     return "Classifier for incremental learning of large datasets by way of racing logit-boosted committees.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  714:     */   }
/*  715:     */   
/*  716:     */   public void setClassifier(Classifier newClassifier)
/*  717:     */   {
/*  718:1124 */     Capabilities cap = newClassifier.getCapabilities();
/*  719:1126 */     if (!cap.handles(Capabilities.Capability.NUMERIC_CLASS)) {
/*  720:1127 */       throw new IllegalArgumentException("Base classifier cannot handle numeric class!");
/*  721:     */     }
/*  722:1131 */     super.setClassifier(newClassifier);
/*  723:     */   }
/*  724:     */   
/*  725:     */   public String minChunkSizeTipText()
/*  726:     */   {
/*  727:1140 */     return "The minimum number of instances to train the base learner with.";
/*  728:     */   }
/*  729:     */   
/*  730:     */   public void setMinChunkSize(int chunkSize)
/*  731:     */   {
/*  732:1150 */     this.m_minChunkSize = chunkSize;
/*  733:     */   }
/*  734:     */   
/*  735:     */   public int getMinChunkSize()
/*  736:     */   {
/*  737:1160 */     return this.m_minChunkSize;
/*  738:     */   }
/*  739:     */   
/*  740:     */   public String maxChunkSizeTipText()
/*  741:     */   {
/*  742:1169 */     return "The maximum number of instances to train the base learner with. The chunk sizes used will start at minChunkSize and grow twice as large for as many times as they are less than or equal to the maximum size.";
/*  743:     */   }
/*  744:     */   
/*  745:     */   public void setMaxChunkSize(int chunkSize)
/*  746:     */   {
/*  747:1179 */     this.m_maxChunkSize = chunkSize;
/*  748:     */   }
/*  749:     */   
/*  750:     */   public int getMaxChunkSize()
/*  751:     */   {
/*  752:1189 */     return this.m_maxChunkSize;
/*  753:     */   }
/*  754:     */   
/*  755:     */   public String validationChunkSizeTipText()
/*  756:     */   {
/*  757:1198 */     return "The number of instances to hold out for validation. These instances will be taken from the beginning of the stream, so learning will not start until these instances have been consumed first.";
/*  758:     */   }
/*  759:     */   
/*  760:     */   public void setValidationChunkSize(int chunkSize)
/*  761:     */   {
/*  762:1208 */     this.m_validationChunkSize = chunkSize;
/*  763:     */   }
/*  764:     */   
/*  765:     */   public int getValidationChunkSize()
/*  766:     */   {
/*  767:1218 */     return this.m_validationChunkSize;
/*  768:     */   }
/*  769:     */   
/*  770:     */   public String pruningTypeTipText()
/*  771:     */   {
/*  772:1227 */     return "The pruning method to use within each committee. Log likelihood pruning will discard new models if they have a negative effect on the log likelihood of the validation data.";
/*  773:     */   }
/*  774:     */   
/*  775:     */   public void setPruningType(SelectedTag pruneType)
/*  776:     */   {
/*  777:1237 */     if (pruneType.getTags() == TAGS_PRUNETYPE) {
/*  778:1238 */       this.m_PruningType = pruneType.getSelectedTag().getID();
/*  779:     */     }
/*  780:     */   }
/*  781:     */   
/*  782:     */   public SelectedTag getPruningType()
/*  783:     */   {
/*  784:1249 */     return new SelectedTag(this.m_PruningType, TAGS_PRUNETYPE);
/*  785:     */   }
/*  786:     */   
/*  787:     */   public String useResamplingTipText()
/*  788:     */   {
/*  789:1258 */     return "Force the use of resampling data rather than using the weight-handling capabilities of the base classifier. Resampling is always used if the base classifier cannot handle weighted instances.";
/*  790:     */   }
/*  791:     */   
/*  792:     */   public void setUseResampling(boolean r)
/*  793:     */   {
/*  794:1268 */     this.m_UseResampling = r;
/*  795:     */   }
/*  796:     */   
/*  797:     */   public boolean getUseResampling()
/*  798:     */   {
/*  799:1278 */     return this.m_UseResampling;
/*  800:     */   }
/*  801:     */   
/*  802:     */   public int getBestCommitteeChunkSize()
/*  803:     */   {
/*  804:1288 */     if (this.m_bestCommittee != null) {
/*  805:1289 */       return this.m_bestCommittee.chunkSize();
/*  806:     */     }
/*  807:1291 */     return 0;
/*  808:     */   }
/*  809:     */   
/*  810:     */   public int getBestCommitteeSize()
/*  811:     */   {
/*  812:1302 */     if (this.m_bestCommittee != null) {
/*  813:1303 */       return this.m_bestCommittee.committeeSize();
/*  814:     */     }
/*  815:1305 */     return 0;
/*  816:     */   }
/*  817:     */   
/*  818:     */   public double getBestCommitteeErrorEstimate()
/*  819:     */   {
/*  820:1316 */     if (this.m_bestCommittee != null) {
/*  821:     */       try
/*  822:     */       {
/*  823:1318 */         return this.m_bestCommittee.validationError() * 100.0D;
/*  824:     */       }
/*  825:     */       catch (Exception e)
/*  826:     */       {
/*  827:1320 */         System.err.println(e.getMessage());
/*  828:1321 */         return 100.0D;
/*  829:     */       }
/*  830:     */     }
/*  831:1324 */     return 100.0D;
/*  832:     */   }
/*  833:     */   
/*  834:     */   public double getBestCommitteeLLEstimate()
/*  835:     */   {
/*  836:1335 */     if (this.m_bestCommittee != null) {
/*  837:     */       try
/*  838:     */       {
/*  839:1337 */         return this.m_bestCommittee.logLikelihood();
/*  840:     */       }
/*  841:     */       catch (Exception e)
/*  842:     */       {
/*  843:1339 */         System.err.println(e.getMessage());
/*  844:1340 */         return 1.7976931348623157E+308D;
/*  845:     */       }
/*  846:     */     }
/*  847:1343 */     return 1.7976931348623157E+308D;
/*  848:     */   }
/*  849:     */   
/*  850:     */   public String toString()
/*  851:     */   {
/*  852:1355 */     if (this.m_bestCommittee != null) {
/*  853:1356 */       return this.m_bestCommittee.toString();
/*  854:     */     }
/*  855:1358 */     if (((this.m_validationSetChanged) || (this.m_zeroR == null)) && (this.m_validationSet != null) && (this.m_validationSet.numInstances() > 0))
/*  856:     */     {
/*  857:1360 */       this.m_zeroR = new ZeroR();
/*  858:     */       try
/*  859:     */       {
/*  860:1362 */         this.m_zeroR.buildClassifier(this.m_validationSet);
/*  861:     */       }
/*  862:     */       catch (Exception e) {}
/*  863:1365 */       this.m_validationSetChanged = false;
/*  864:     */     }
/*  865:1367 */     if (this.m_zeroR != null) {
/*  866:1368 */       return "RacedIncrementalLogitBoost: insufficient data to build model, resorting to ZeroR:\n\n" + this.m_zeroR.toString();
/*  867:     */     }
/*  868:1371 */     return "RacedIncrementalLogitBoost: no model built yet.";
/*  869:     */   }
/*  870:     */   
/*  871:     */   public String getRevision()
/*  872:     */   {
/*  873:1383 */     return RevisionUtils.extract("$Revision: 10374 $");
/*  874:     */   }
/*  875:     */   
/*  876:     */   public static void main(String[] argv)
/*  877:     */   {
/*  878:1392 */     runClassifier(new RacedIncrementalLogitBoost(), argv);
/*  879:     */   }
/*  880:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.RacedIncrementalLogitBoost
 * JD-Core Version:    0.7.0.1
 */