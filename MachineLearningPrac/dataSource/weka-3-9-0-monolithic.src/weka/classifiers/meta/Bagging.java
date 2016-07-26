/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Random;
/*   9:    */ import java.util.Vector;
/*  10:    */ import weka.classifiers.Classifier;
/*  11:    */ import weka.classifiers.RandomizableParallelIteratedSingleClassifierEnhancer;
/*  12:    */ import weka.classifiers.evaluation.Evaluation;
/*  13:    */ import weka.classifiers.trees.REPTree;
/*  14:    */ import weka.core.AdditionalMeasureProducer;
/*  15:    */ import weka.core.Aggregateable;
/*  16:    */ import weka.core.Attribute;
/*  17:    */ import weka.core.Capabilities;
/*  18:    */ import weka.core.Instance;
/*  19:    */ import weka.core.Instances;
/*  20:    */ import weka.core.Option;
/*  21:    */ import weka.core.PartitionGenerator;
/*  22:    */ import weka.core.Randomizable;
/*  23:    */ import weka.core.RevisionUtils;
/*  24:    */ import weka.core.TechnicalInformation;
/*  25:    */ import weka.core.TechnicalInformation.Field;
/*  26:    */ import weka.core.TechnicalInformation.Type;
/*  27:    */ import weka.core.TechnicalInformationHandler;
/*  28:    */ import weka.core.Utils;
/*  29:    */ import weka.core.WeightedInstancesHandler;
/*  30:    */ 
/*  31:    */ public class Bagging
/*  32:    */   extends RandomizableParallelIteratedSingleClassifierEnhancer
/*  33:    */   implements WeightedInstancesHandler, AdditionalMeasureProducer, TechnicalInformationHandler, PartitionGenerator, Aggregateable<Bagging>
/*  34:    */ {
/*  35:    */   static final long serialVersionUID = -115879962237199703L;
/*  36:165 */   protected int m_BagSizePercent = 100;
/*  37:168 */   protected boolean m_CalcOutOfBag = false;
/*  38:171 */   protected boolean m_RepresentUsingWeights = false;
/*  39:174 */   protected Evaluation m_OutOfBagEvaluationObject = null;
/*  40:177 */   private boolean m_StoreOutOfBagPredictions = false;
/*  41:    */   private boolean m_OutputOutOfBagComplexityStatistics;
/*  42:183 */   private boolean m_Numeric = false;
/*  43:    */   private boolean m_printClassifiers;
/*  44:    */   protected Random m_random;
/*  45:    */   protected boolean[][] m_inBag;
/*  46:    */   protected Instances m_data;
/*  47:    */   protected List<Classifier> m_classifiersCache;
/*  48:    */   
/*  49:    */   public Bagging()
/*  50:    */   {
/*  51:193 */     this.m_Classifier = new REPTree();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String globalInfo()
/*  55:    */   {
/*  56:203 */     return "Class for bagging a classifier to reduce variance. Can do classification and regression depending on the base learner. \n\nFor more information, see\n\n" + getTechnicalInformation().toString();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public TechnicalInformation getTechnicalInformation()
/*  60:    */   {
/*  61:220 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  62:221 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Leo Breiman");
/*  63:222 */     result.setValue(TechnicalInformation.Field.YEAR, "1996");
/*  64:223 */     result.setValue(TechnicalInformation.Field.TITLE, "Bagging predictors");
/*  65:224 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  66:225 */     result.setValue(TechnicalInformation.Field.VOLUME, "24");
/*  67:226 */     result.setValue(TechnicalInformation.Field.NUMBER, "2");
/*  68:227 */     result.setValue(TechnicalInformation.Field.PAGES, "123-140");
/*  69:    */     
/*  70:229 */     return result;
/*  71:    */   }
/*  72:    */   
/*  73:    */   protected String defaultClassifierString()
/*  74:    */   {
/*  75:240 */     return "weka.classifiers.trees.REPTree";
/*  76:    */   }
/*  77:    */   
/*  78:    */   public Enumeration<Option> listOptions()
/*  79:    */   {
/*  80:251 */     Vector<Option> newVector = new Vector(4);
/*  81:    */     
/*  82:253 */     newVector.addElement(new Option("\tSize of each bag, as a percentage of the\n\ttraining set size. (default 100)", "P", 1, "-P"));
/*  83:    */     
/*  84:    */ 
/*  85:    */ 
/*  86:257 */     newVector.addElement(new Option("\tCalculate the out of bag error.", "O", 0, "-O"));
/*  87:    */     
/*  88:    */ 
/*  89:260 */     newVector.addElement(new Option("\tWhether to store out of bag predictions in internal evaluation object.", "store-out-of-bag-predictions", 0, "-store-out-of-bag-predictions"));
/*  90:    */     
/*  91:    */ 
/*  92:263 */     newVector.addElement(new Option("\tWhether to output complexity-based statistics when out-of-bag evaluation is performed.", "output-out-of-bag-complexity-statistics", 0, "-output-out-of-bag-complexity-statistics"));
/*  93:    */     
/*  94:    */ 
/*  95:266 */     newVector.addElement(new Option("\tRepresent copies of instances using weights rather than explicitly.", "represent-copies-using-weights", 0, "-represent-copies-using-weights"));
/*  96:    */     
/*  97:    */ 
/*  98:269 */     newVector.addElement(new Option("\tPrint the individual classifiers in the output", "print", 0, "-print"));
/*  99:    */     
/* 100:    */ 
/* 101:272 */     newVector.addAll(Collections.list(super.listOptions()));
/* 102:    */     
/* 103:274 */     return newVector.elements();
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void setOptions(String[] options)
/* 107:    */     throws Exception
/* 108:    */   {
/* 109:362 */     String bagSize = Utils.getOption('P', options);
/* 110:363 */     if (bagSize.length() != 0) {
/* 111:364 */       setBagSizePercent(Integer.parseInt(bagSize));
/* 112:    */     } else {
/* 113:366 */       setBagSizePercent(100);
/* 114:    */     }
/* 115:369 */     setCalcOutOfBag(Utils.getFlag('O', options));
/* 116:    */     
/* 117:371 */     setStoreOutOfBagPredictions(Utils.getFlag("store-out-of-bag-predictions", options));
/* 118:    */     
/* 119:373 */     setOutputOutOfBagComplexityStatistics(Utils.getFlag("output-out-of-bag-complexity-statistics", options));
/* 120:    */     
/* 121:375 */     setRepresentCopiesUsingWeights(Utils.getFlag("represent-copies-using-weights", options));
/* 122:    */     
/* 123:377 */     setPrintClassifiers(Utils.getFlag("print", options));
/* 124:    */     
/* 125:379 */     super.setOptions(options);
/* 126:    */     
/* 127:381 */     Utils.checkForRemainingOptions(options);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public String[] getOptions()
/* 131:    */   {
/* 132:392 */     Vector<String> options = new Vector();
/* 133:    */     
/* 134:394 */     options.add("-P");
/* 135:395 */     options.add("" + getBagSizePercent());
/* 136:397 */     if (getCalcOutOfBag()) {
/* 137:398 */       options.add("-O");
/* 138:    */     }
/* 139:401 */     if (getStoreOutOfBagPredictions()) {
/* 140:402 */       options.add("-store-out-of-bag-predictions");
/* 141:    */     }
/* 142:405 */     if (getOutputOutOfBagComplexityStatistics()) {
/* 143:406 */       options.add("-output-out-of-bag-complexity-statistics");
/* 144:    */     }
/* 145:409 */     if (getRepresentCopiesUsingWeights()) {
/* 146:410 */       options.add("-represent-copies-using-weights");
/* 147:    */     }
/* 148:413 */     if (getPrintClassifiers()) {
/* 149:414 */       options.add("-print");
/* 150:    */     }
/* 151:417 */     Collections.addAll(options, super.getOptions());
/* 152:    */     
/* 153:419 */     return (String[])options.toArray(new String[0]);
/* 154:    */   }
/* 155:    */   
/* 156:    */   public String bagSizePercentTipText()
/* 157:    */   {
/* 158:428 */     return "Size of each bag, as a percentage of the training set size.";
/* 159:    */   }
/* 160:    */   
/* 161:    */   public int getBagSizePercent()
/* 162:    */   {
/* 163:438 */     return this.m_BagSizePercent;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void setBagSizePercent(int newBagSizePercent)
/* 167:    */   {
/* 168:448 */     this.m_BagSizePercent = newBagSizePercent;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public String representCopiesUsingWeightsTipText()
/* 172:    */   {
/* 173:457 */     return "Whether to represent copies of instances using weights rather than explicitly.";
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void setRepresentCopiesUsingWeights(boolean representUsingWeights)
/* 177:    */   {
/* 178:467 */     this.m_RepresentUsingWeights = representUsingWeights;
/* 179:    */   }
/* 180:    */   
/* 181:    */   public boolean getRepresentCopiesUsingWeights()
/* 182:    */   {
/* 183:477 */     return this.m_RepresentUsingWeights;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public String storeOutOfBagPredictionsTipText()
/* 187:    */   {
/* 188:486 */     return "Whether to store the out-of-bag predictions.";
/* 189:    */   }
/* 190:    */   
/* 191:    */   public void setStoreOutOfBagPredictions(boolean storeOutOfBag)
/* 192:    */   {
/* 193:496 */     this.m_StoreOutOfBagPredictions = storeOutOfBag;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public boolean getStoreOutOfBagPredictions()
/* 197:    */   {
/* 198:506 */     return this.m_StoreOutOfBagPredictions;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public String calcOutOfBagTipText()
/* 202:    */   {
/* 203:515 */     return "Whether the out-of-bag error is calculated.";
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void setCalcOutOfBag(boolean calcOutOfBag)
/* 207:    */   {
/* 208:525 */     this.m_CalcOutOfBag = calcOutOfBag;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public boolean getCalcOutOfBag()
/* 212:    */   {
/* 213:535 */     return this.m_CalcOutOfBag;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public String outputOutOfBagComplexityStatisticsTipText()
/* 217:    */   {
/* 218:544 */     return "Whether to output complexity-based statistics when out-of-bag evaluation is performed.";
/* 219:    */   }
/* 220:    */   
/* 221:    */   public boolean getOutputOutOfBagComplexityStatistics()
/* 222:    */   {
/* 223:554 */     return this.m_OutputOutOfBagComplexityStatistics;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public void setOutputOutOfBagComplexityStatistics(boolean b)
/* 227:    */   {
/* 228:564 */     this.m_OutputOutOfBagComplexityStatistics = b;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public String printClassifiersTipText()
/* 232:    */   {
/* 233:574 */     return "Print the individual classifiers in the output";
/* 234:    */   }
/* 235:    */   
/* 236:    */   public void setPrintClassifiers(boolean print)
/* 237:    */   {
/* 238:583 */     this.m_printClassifiers = print;
/* 239:    */   }
/* 240:    */   
/* 241:    */   public boolean getPrintClassifiers()
/* 242:    */   {
/* 243:592 */     return this.m_printClassifiers;
/* 244:    */   }
/* 245:    */   
/* 246:    */   public double measureOutOfBagError()
/* 247:    */   {
/* 248:604 */     if (this.m_OutOfBagEvaluationObject == null) {
/* 249:605 */       return -1.0D;
/* 250:    */     }
/* 251:607 */     if (this.m_Numeric) {
/* 252:608 */       return this.m_OutOfBagEvaluationObject.meanAbsoluteError();
/* 253:    */     }
/* 254:610 */     return this.m_OutOfBagEvaluationObject.errorRate();
/* 255:    */   }
/* 256:    */   
/* 257:    */   public Enumeration<String> enumerateMeasures()
/* 258:    */   {
/* 259:622 */     Vector<String> newVector = new Vector(1);
/* 260:623 */     newVector.addElement("measureOutOfBagError");
/* 261:624 */     return newVector.elements();
/* 262:    */   }
/* 263:    */   
/* 264:    */   public double getMeasure(String additionalMeasureName)
/* 265:    */   {
/* 266:637 */     if (additionalMeasureName.equalsIgnoreCase("measureOutOfBagError")) {
/* 267:638 */       return measureOutOfBagError();
/* 268:    */     }
/* 269:640 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (Bagging)");
/* 270:    */   }
/* 271:    */   
/* 272:    */   protected synchronized Instances getTrainingSet(int iteration)
/* 273:    */     throws Exception
/* 274:    */   {
/* 275:658 */     int bagSize = (int)(this.m_data.numInstances() * (this.m_BagSizePercent / 100.0D));
/* 276:659 */     Instances bagData = null;
/* 277:660 */     Random r = new Random(this.m_Seed + iteration);
/* 278:663 */     if (this.m_CalcOutOfBag)
/* 279:    */     {
/* 280:664 */       this.m_inBag[iteration] = new boolean[this.m_data.numInstances()];
/* 281:665 */       bagData = this.m_data.resampleWithWeights(r, this.m_inBag[iteration], getRepresentCopiesUsingWeights());
/* 282:    */     }
/* 283:667 */     else if (bagSize < this.m_data.numInstances())
/* 284:    */     {
/* 285:668 */       bagData = this.m_data.resampleWithWeights(r, false);
/* 286:669 */       bagData.randomize(r);
/* 287:670 */       Instances newBagData = new Instances(bagData, 0, bagSize);
/* 288:671 */       bagData = newBagData;
/* 289:    */     }
/* 290:    */     else
/* 291:    */     {
/* 292:673 */       bagData = this.m_data.resampleWithWeights(r, getRepresentCopiesUsingWeights());
/* 293:    */     }
/* 294:677 */     return bagData;
/* 295:    */   }
/* 296:    */   
/* 297:    */   public Evaluation getOutOfBagEvaluationObject()
/* 298:    */   {
/* 299:687 */     return this.m_OutOfBagEvaluationObject;
/* 300:    */   }
/* 301:    */   
/* 302:    */   public void buildClassifier(Instances data)
/* 303:    */     throws Exception
/* 304:    */   {
/* 305:701 */     getCapabilities().testWithFail(data);
/* 306:704 */     if ((getRepresentCopiesUsingWeights()) && (!(this.m_Classifier instanceof WeightedInstancesHandler))) {
/* 307:705 */       throw new IllegalArgumentException("Cannot represent copies using weights when base learner in bagging does not implement WeightedInstancesHandler.");
/* 308:    */     }
/* 309:711 */     this.m_data = new Instances(data);
/* 310:    */     
/* 311:713 */     super.buildClassifier(this.m_data);
/* 312:715 */     if ((this.m_CalcOutOfBag) && (this.m_BagSizePercent != 100)) {
/* 313:716 */       throw new IllegalArgumentException("Bag size needs to be 100% if out-of-bag error is to be calculated!");
/* 314:    */     }
/* 315:720 */     this.m_random = new Random(this.m_Seed);
/* 316:    */     
/* 317:722 */     this.m_inBag = ((boolean[][])null);
/* 318:723 */     if (this.m_CalcOutOfBag) {
/* 319:724 */       this.m_inBag = new boolean[this.m_Classifiers.length][];
/* 320:    */     }
/* 321:726 */     for (int j = 0; j < this.m_Classifiers.length; j++) {
/* 322:727 */       if ((this.m_Classifier instanceof Randomizable)) {
/* 323:728 */         ((Randomizable)this.m_Classifiers[j]).setSeed(this.m_random.nextInt());
/* 324:    */       }
/* 325:    */     }
/* 326:732 */     this.m_Numeric = this.m_data.classAttribute().isNumeric();
/* 327:    */     
/* 328:734 */     buildClassifiers();
/* 329:737 */     if (getCalcOutOfBag())
/* 330:    */     {
/* 331:738 */       this.m_OutOfBagEvaluationObject = new Evaluation(this.m_data);
/* 332:740 */       for (int i = 0; i < this.m_data.numInstances(); i++)
/* 333:    */       {
/* 334:    */         double[] votes;
/* 335:    */         double[] votes;
/* 336:743 */         if (this.m_Numeric) {
/* 337:744 */           votes = new double[1];
/* 338:    */         } else {
/* 339:746 */           votes = new double[this.m_data.numClasses()];
/* 340:    */         }
/* 341:749 */         int voteCount = 0;
/* 342:750 */         for (int j = 0; j < this.m_Classifiers.length; j++) {
/* 343:751 */           if (this.m_inBag[j][i] == 0) {
/* 344:754 */             if (this.m_Numeric)
/* 345:    */             {
/* 346:755 */               double pred = this.m_Classifiers[j].classifyInstance(this.m_data.instance(i));
/* 347:756 */               if (!Utils.isMissingValue(pred))
/* 348:    */               {
/* 349:757 */                 votes[0] += pred;
/* 350:758 */                 voteCount++;
/* 351:    */               }
/* 352:    */             }
/* 353:    */             else
/* 354:    */             {
/* 355:761 */               voteCount++;
/* 356:762 */               double[] newProbs = this.m_Classifiers[j].distributionForInstance(this.m_data.instance(i));
/* 357:764 */               for (int k = 0; k < newProbs.length; k++) {
/* 358:765 */                 votes[k] += newProbs[k];
/* 359:    */               }
/* 360:    */             }
/* 361:    */           }
/* 362:    */         }
/* 363:771 */         if (this.m_Numeric)
/* 364:    */         {
/* 365:772 */           if (voteCount > 0)
/* 366:    */           {
/* 367:773 */             votes[0] /= voteCount;
/* 368:774 */             this.m_OutOfBagEvaluationObject.evaluationForSingleInstance(votes, this.m_data.instance(i), getStoreOutOfBagPredictions());
/* 369:    */           }
/* 370:    */         }
/* 371:    */         else
/* 372:    */         {
/* 373:777 */           double sum = Utils.sum(votes);
/* 374:778 */           if (sum > 0.0D)
/* 375:    */           {
/* 376:779 */             Utils.normalize(votes, sum);
/* 377:780 */             this.m_OutOfBagEvaluationObject.evaluationForSingleInstance(votes, this.m_data.instance(i), getStoreOutOfBagPredictions());
/* 378:    */           }
/* 379:    */         }
/* 380:    */       }
/* 381:    */     }
/* 382:    */     else
/* 383:    */     {
/* 384:785 */       this.m_OutOfBagEvaluationObject = null;
/* 385:    */     }
/* 386:789 */     this.m_data = null;
/* 387:    */   }
/* 388:    */   
/* 389:    */   public double[] distributionForInstance(Instance instance)
/* 390:    */     throws Exception
/* 391:    */   {
/* 392:803 */     double[] sums = new double[instance.numClasses()];
/* 393:    */     
/* 394:805 */     double numPreds = 0.0D;
/* 395:806 */     for (int i = 0; i < this.m_NumIterations; i++) {
/* 396:807 */       if (this.m_Numeric)
/* 397:    */       {
/* 398:808 */         double pred = this.m_Classifiers[i].classifyInstance(instance);
/* 399:809 */         if (!Utils.isMissingValue(pred))
/* 400:    */         {
/* 401:810 */           sums[0] += pred;
/* 402:811 */           numPreds += 1.0D;
/* 403:    */         }
/* 404:    */       }
/* 405:    */       else
/* 406:    */       {
/* 407:814 */         double[] newProbs = this.m_Classifiers[i].distributionForInstance(instance);
/* 408:815 */         for (int j = 0; j < newProbs.length; j++) {
/* 409:816 */           sums[j] += newProbs[j];
/* 410:    */         }
/* 411:    */       }
/* 412:    */     }
/* 413:819 */     if (this.m_Numeric)
/* 414:    */     {
/* 415:820 */       if (numPreds == 0.0D) {
/* 416:821 */         sums[0] = Utils.missingValue();
/* 417:    */       } else {
/* 418:823 */         sums[0] /= numPreds;
/* 419:    */       }
/* 420:825 */       return sums;
/* 421:    */     }
/* 422:826 */     if (Utils.eq(Utils.sum(sums), 0.0D)) {
/* 423:827 */       return sums;
/* 424:    */     }
/* 425:829 */     Utils.normalize(sums);
/* 426:830 */     return sums;
/* 427:    */   }
/* 428:    */   
/* 429:    */   public String toString()
/* 430:    */   {
/* 431:842 */     if (this.m_Classifiers == null) {
/* 432:843 */       return "Bagging: No model built yet.";
/* 433:    */     }
/* 434:845 */     StringBuffer text = new StringBuffer();
/* 435:846 */     text.append("Bagging with " + getNumIterations() + " iterations and base learner\n\n" + getClassifierSpec());
/* 436:847 */     if (getPrintClassifiers())
/* 437:    */     {
/* 438:848 */       text.append("All the base classifiers: \n\n");
/* 439:849 */       for (int i = 0; i < this.m_Classifiers.length; i++) {
/* 440:850 */         text.append(this.m_Classifiers[i].toString() + "\n\n");
/* 441:    */       }
/* 442:    */     }
/* 443:852 */     if (this.m_CalcOutOfBag) {
/* 444:853 */       text.append(this.m_OutOfBagEvaluationObject.toSummaryString("*** Out-of-bag estimates ***\n", getOutputOutOfBagComplexityStatistics()));
/* 445:    */     }
/* 446:856 */     return text.toString();
/* 447:    */   }
/* 448:    */   
/* 449:    */   public void generatePartition(Instances data)
/* 450:    */     throws Exception
/* 451:    */   {
/* 452:865 */     if ((this.m_Classifier instanceof PartitionGenerator)) {
/* 453:866 */       buildClassifier(data);
/* 454:    */     } else {
/* 455:867 */       throw new Exception("Classifier: " + getClassifierSpec() + " cannot generate a partition");
/* 456:    */     }
/* 457:    */   }
/* 458:    */   
/* 459:    */   public double[] getMembershipValues(Instance inst)
/* 460:    */     throws Exception
/* 461:    */   {
/* 462:877 */     if ((this.m_Classifier instanceof PartitionGenerator))
/* 463:    */     {
/* 464:878 */       ArrayList<double[]> al = new ArrayList();
/* 465:879 */       int size = 0;
/* 466:880 */       for (int i = 0; i < this.m_Classifiers.length; i++)
/* 467:    */       {
/* 468:881 */         double[] r = ((PartitionGenerator)this.m_Classifiers[i]).getMembershipValues(inst);
/* 469:    */         
/* 470:883 */         size += r.length;
/* 471:884 */         al.add(r);
/* 472:    */       }
/* 473:886 */       double[] values = new double[size];
/* 474:887 */       int pos = 0;
/* 475:888 */       for (double[] v : al)
/* 476:    */       {
/* 477:889 */         System.arraycopy(v, 0, values, pos, v.length);
/* 478:890 */         pos += v.length;
/* 479:    */       }
/* 480:892 */       return values;
/* 481:    */     }
/* 482:893 */     throw new Exception("Classifier: " + getClassifierSpec() + " cannot generate a partition");
/* 483:    */   }
/* 484:    */   
/* 485:    */   public int numElements()
/* 486:    */     throws Exception
/* 487:    */   {
/* 488:903 */     if ((this.m_Classifier instanceof PartitionGenerator))
/* 489:    */     {
/* 490:904 */       int size = 0;
/* 491:905 */       for (int i = 0; i < this.m_Classifiers.length; i++) {
/* 492:906 */         size += ((PartitionGenerator)this.m_Classifiers[i]).numElements();
/* 493:    */       }
/* 494:908 */       return size;
/* 495:    */     }
/* 496:909 */     throw new Exception("Classifier: " + getClassifierSpec() + " cannot generate a partition");
/* 497:    */   }
/* 498:    */   
/* 499:    */   public String getRevision()
/* 500:    */   {
/* 501:920 */     return RevisionUtils.extract("$Revision: 12579 $");
/* 502:    */   }
/* 503:    */   
/* 504:    */   public static void main(String[] argv)
/* 505:    */   {
/* 506:929 */     runClassifier(new Bagging(), argv);
/* 507:    */   }
/* 508:    */   
/* 509:    */   public Bagging aggregate(Bagging toAggregate)
/* 510:    */     throws Exception
/* 511:    */   {
/* 512:944 */     if (!this.m_Classifier.getClass().isAssignableFrom(toAggregate.m_Classifier.getClass())) {
/* 513:945 */       throw new Exception("Can't aggregate because base classifiers differ");
/* 514:    */     }
/* 515:948 */     if (this.m_classifiersCache == null)
/* 516:    */     {
/* 517:949 */       this.m_classifiersCache = new ArrayList();
/* 518:950 */       this.m_classifiersCache.addAll(Arrays.asList(this.m_Classifiers));
/* 519:    */     }
/* 520:952 */     this.m_classifiersCache.addAll(Arrays.asList(toAggregate.m_Classifiers));
/* 521:    */     
/* 522:954 */     return this;
/* 523:    */   }
/* 524:    */   
/* 525:    */   public void finalizeAggregation()
/* 526:    */     throws Exception
/* 527:    */   {
/* 528:965 */     this.m_Classifiers = ((Classifier[])this.m_classifiersCache.toArray(new Classifier[1]));
/* 529:966 */     this.m_NumIterations = this.m_Classifiers.length;
/* 530:    */     
/* 531:968 */     this.m_classifiersCache = null;
/* 532:    */   }
/* 533:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.Bagging
 * JD-Core Version:    0.7.0.1
 */