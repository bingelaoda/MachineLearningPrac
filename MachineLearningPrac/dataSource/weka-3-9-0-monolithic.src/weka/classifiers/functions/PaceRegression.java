/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.AbstractClassifier;
/*   7:    */ import weka.classifiers.functions.pace.ChisqMixture;
/*   8:    */ import weka.classifiers.functions.pace.NormalMixture;
/*   9:    */ import weka.classifiers.functions.pace.PaceMatrix;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.Capabilities;
/*  12:    */ import weka.core.Capabilities.Capability;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.NoSupportForMissingValuesException;
/*  16:    */ import weka.core.Option;
/*  17:    */ import weka.core.OptionHandler;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.SelectedTag;
/*  20:    */ import weka.core.Tag;
/*  21:    */ import weka.core.TechnicalInformation;
/*  22:    */ import weka.core.TechnicalInformation.Field;
/*  23:    */ import weka.core.TechnicalInformation.Type;
/*  24:    */ import weka.core.TechnicalInformationHandler;
/*  25:    */ import weka.core.Utils;
/*  26:    */ import weka.core.WeightedInstancesHandler;
/*  27:    */ import weka.core.WekaException;
/*  28:    */ import weka.core.matrix.DoubleVector;
/*  29:    */ import weka.core.matrix.IntVector;
/*  30:    */ 
/*  31:    */ public class PaceRegression
/*  32:    */   extends AbstractClassifier
/*  33:    */   implements OptionHandler, WeightedInstancesHandler, TechnicalInformationHandler
/*  34:    */ {
/*  35:    */   static final long serialVersionUID = 7230266976059115435L;
/*  36:147 */   Instances m_Model = null;
/*  37:    */   private double[] m_Coefficients;
/*  38:    */   private int m_ClassIndex;
/*  39:    */   private static final int olsEstimator = 0;
/*  40:    */   private static final int ebEstimator = 1;
/*  41:    */   private static final int nestedEstimator = 2;
/*  42:    */   private static final int subsetEstimator = 3;
/*  43:    */   private static final int pace2Estimator = 4;
/*  44:    */   private static final int pace4Estimator = 5;
/*  45:    */   private static final int pace6Estimator = 6;
/*  46:    */   private static final int olscEstimator = 7;
/*  47:    */   private static final int aicEstimator = 8;
/*  48:    */   private static final int bicEstimator = 9;
/*  49:    */   private static final int ricEstimator = 10;
/*  50:178 */   public static final Tag[] TAGS_ESTIMATOR = { new Tag(0, "Ordinary least squares"), new Tag(1, "Empirical Bayes"), new Tag(2, "Nested model selector"), new Tag(3, "Subset selector"), new Tag(4, "PACE2"), new Tag(5, "PACE4"), new Tag(6, "PACE6"), new Tag(7, "Ordinary least squares selection"), new Tag(8, "AIC"), new Tag(9, "BIC"), new Tag(10, "RIC") };
/*  51:190 */   private int paceEstimator = 1;
/*  52:192 */   private double olscThreshold = 2.0D;
/*  53:    */   
/*  54:    */   public String globalInfo()
/*  55:    */   {
/*  56:201 */     return "Class for building pace regression linear models and using them for prediction. \n\nUnder regularity conditions, pace regression is provably optimal when the number of coefficients tends to infinity. It consists of a group of estimators that are either overall optimal or optimal under certain conditions.\n\nThe current work of the pace regression theory, and therefore also this implementation, do not handle: \n\n- missing values \n- non-binary nominal attributes \n- the case that n - k is small where n is the number of instances and k is the number of coefficients (the threshold used in this implmentation is 20)\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public TechnicalInformation getTechnicalInformation()
/*  60:    */   {
/*  61:228 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.PHDTHESIS);
/*  62:229 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Wang, Y");
/*  63:230 */     result.setValue(TechnicalInformation.Field.YEAR, "2000");
/*  64:231 */     result.setValue(TechnicalInformation.Field.TITLE, "A new approach to fitting linear models in high dimensional spaces");
/*  65:    */     
/*  66:233 */     result.setValue(TechnicalInformation.Field.SCHOOL, "Department of Computer Science, University of Waikato");
/*  67:    */     
/*  68:235 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Hamilton, New Zealand");
/*  69:    */     
/*  70:237 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*  71:238 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Wang, Y. and Witten, I. H.");
/*  72:239 */     additional.setValue(TechnicalInformation.Field.YEAR, "2002");
/*  73:240 */     additional.setValue(TechnicalInformation.Field.TITLE, "Modeling for optimal probability prediction");
/*  74:    */     
/*  75:242 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the Nineteenth International Conference in Machine Learning");
/*  76:    */     
/*  77:    */ 
/*  78:245 */     additional.setValue(TechnicalInformation.Field.YEAR, "2002");
/*  79:246 */     additional.setValue(TechnicalInformation.Field.PAGES, "650-657");
/*  80:247 */     additional.setValue(TechnicalInformation.Field.ADDRESS, "Sydney, Australia");
/*  81:    */     
/*  82:249 */     return result;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Capabilities getCapabilities()
/*  86:    */   {
/*  87:259 */     Capabilities result = super.getCapabilities();
/*  88:260 */     result.disableAll();
/*  89:    */     
/*  90:    */ 
/*  91:263 */     result.enable(Capabilities.Capability.BINARY_ATTRIBUTES);
/*  92:264 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  93:    */     
/*  94:    */ 
/*  95:267 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  96:268 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  97:269 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  98:    */     
/*  99:271 */     return result;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void buildClassifier(Instances data)
/* 103:    */     throws Exception
/* 104:    */   {
/* 105:285 */     Capabilities cap = getCapabilities();
/* 106:286 */     cap.setMinimumNumberInstances(20 + data.numAttributes());
/* 107:287 */     cap.testWithFail(data);
/* 108:    */     
/* 109:    */ 
/* 110:290 */     data = new Instances(data);
/* 111:291 */     data.deleteWithMissingClass();
/* 112:    */     
/* 113:    */ 
/* 114:    */ 
/* 115:    */ 
/* 116:296 */     this.m_Model = new Instances(data, 0);
/* 117:297 */     this.m_ClassIndex = data.classIndex();
/* 118:298 */     double[][] transformedDataMatrix = getTransformedDataMatrix(data, this.m_ClassIndex);
/* 119:    */     
/* 120:300 */     double[] classValueVector = data.attributeToDoubleArray(this.m_ClassIndex);
/* 121:    */     
/* 122:302 */     this.m_Coefficients = null;
/* 123:    */     
/* 124:    */ 
/* 125:    */ 
/* 126:    */ 
/* 127:307 */     this.m_Coefficients = pace(transformedDataMatrix, classValueVector);
/* 128:    */   }
/* 129:    */   
/* 130:    */   private double[] pace(double[][] matrix_X, double[] vector_Y)
/* 131:    */   {
/* 132:319 */     PaceMatrix X = new PaceMatrix(matrix_X);
/* 133:320 */     PaceMatrix Y = new PaceMatrix(vector_Y, vector_Y.length);
/* 134:321 */     IntVector pvt = IntVector.seq(0, X.getColumnDimension() - 1);
/* 135:322 */     int n = X.getRowDimension();
/* 136:323 */     int kr = X.getColumnDimension();
/* 137:    */     
/* 138:325 */     X.lsqrSelection(Y, pvt, 1);
/* 139:326 */     X.positiveDiagonal(Y, pvt);
/* 140:    */     
/* 141:328 */     PaceMatrix sol = (PaceMatrix)Y.clone();
/* 142:329 */     X.rsolve(sol, pvt, pvt.size());
/* 143:330 */     DoubleVector r = Y.getColumn(pvt.size(), n - 1, 0);
/* 144:331 */     double sde = Math.sqrt(r.sum2() / r.size());
/* 145:    */     
/* 146:333 */     DoubleVector aHat = Y.getColumn(0, pvt.size() - 1, 0).times(1.0D / sde);
/* 147:    */     
/* 148:335 */     DoubleVector aTilde = null;
/* 149:336 */     switch (this.paceEstimator)
/* 150:    */     {
/* 151:    */     case 1: 
/* 152:    */     case 2: 
/* 153:    */     case 3: 
/* 154:340 */       NormalMixture d = new NormalMixture();
/* 155:341 */       d.fit(aHat, 1);
/* 156:342 */       if (this.paceEstimator == 1) {
/* 157:343 */         aTilde = d.empiricalBayesEstimate(aHat);
/* 158:344 */       } else if (this.paceEstimator == 1) {
/* 159:345 */         aTilde = d.subsetEstimate(aHat);
/* 160:    */       } else {
/* 161:347 */         aTilde = d.nestedEstimate(aHat);
/* 162:    */       }
/* 163:349 */       break;
/* 164:    */     case 4: 
/* 165:    */     case 5: 
/* 166:    */     case 6: 
/* 167:353 */       DoubleVector AHat = aHat.square();
/* 168:354 */       ChisqMixture dc = new ChisqMixture();
/* 169:355 */       dc.fit(AHat, 1);
/* 170:    */       DoubleVector ATilde;
/* 171:    */       DoubleVector ATilde;
/* 172:357 */       if (this.paceEstimator == 6)
/* 173:    */       {
/* 174:358 */         ATilde = dc.pace6(AHat);
/* 175:    */       }
/* 176:    */       else
/* 177:    */       {
/* 178:    */         DoubleVector ATilde;
/* 179:359 */         if (this.paceEstimator == 4) {
/* 180:360 */           ATilde = dc.pace2(AHat);
/* 181:    */         } else {
/* 182:362 */           ATilde = dc.pace4(AHat);
/* 183:    */         }
/* 184:    */       }
/* 185:364 */       aTilde = ATilde.sqrt().times(aHat.sign());
/* 186:365 */       break;
/* 187:    */     case 0: 
/* 188:367 */       aTilde = aHat.copy();
/* 189:368 */       break;
/* 190:    */     case 7: 
/* 191:    */     case 8: 
/* 192:    */     case 9: 
/* 193:    */     case 10: 
/* 194:373 */       if (this.paceEstimator == 8) {
/* 195:374 */         this.olscThreshold = 2.0D;
/* 196:375 */       } else if (this.paceEstimator == 9) {
/* 197:376 */         this.olscThreshold = Math.log(n);
/* 198:377 */       } else if (this.paceEstimator == 10) {
/* 199:378 */         this.olscThreshold = (2.0D * Math.log(kr));
/* 200:    */       }
/* 201:380 */       aTilde = aHat.copy();
/* 202:381 */       for (int i = 0; i < aTilde.size(); i++) {
/* 203:382 */         if (Math.abs(aTilde.get(i)) < Math.sqrt(this.olscThreshold)) {
/* 204:383 */           aTilde.set(i, 0.0D);
/* 205:    */         }
/* 206:    */       }
/* 207:    */     }
/* 208:387 */     PaceMatrix YTilde = new PaceMatrix(new PaceMatrix(aTilde).times(sde));
/* 209:388 */     X.rsolve(YTilde, pvt, pvt.size());
/* 210:389 */     DoubleVector betaTilde = YTilde.getColumn(0).unpivoting(pvt, kr);
/* 211:    */     
/* 212:391 */     return betaTilde.getArrayCopy();
/* 213:    */   }
/* 214:    */   
/* 215:    */   public boolean checkForMissing(Instance instance, Instances model)
/* 216:    */   {
/* 217:403 */     for (int j = 0; j < instance.numAttributes(); j++) {
/* 218:404 */       if ((j != model.classIndex()) && 
/* 219:405 */         (instance.isMissing(j))) {
/* 220:406 */         return true;
/* 221:    */       }
/* 222:    */     }
/* 223:410 */     return false;
/* 224:    */   }
/* 225:    */   
/* 226:    */   private double[][] getTransformedDataMatrix(Instances data, int classIndex)
/* 227:    */   {
/* 228:421 */     int numInstances = data.numInstances();
/* 229:422 */     int numAttributes = data.numAttributes();
/* 230:423 */     int middle = classIndex;
/* 231:424 */     if (middle < 0) {
/* 232:425 */       middle = numAttributes;
/* 233:    */     }
/* 234:428 */     double[][] result = new double[numInstances][numAttributes];
/* 235:429 */     for (int i = 0; i < numInstances; i++)
/* 236:    */     {
/* 237:430 */       Instance inst = data.instance(i);
/* 238:    */       
/* 239:432 */       result[i][0] = 1.0D;
/* 240:435 */       for (int j = 0; j < middle; j++) {
/* 241:436 */         result[i][(j + 1)] = inst.value(j);
/* 242:    */       }
/* 243:438 */       for (int j = middle + 1; j < numAttributes; j++) {
/* 244:439 */         result[i][j] = inst.value(j);
/* 245:    */       }
/* 246:    */     }
/* 247:442 */     return result;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public double classifyInstance(Instance instance)
/* 251:    */     throws Exception
/* 252:    */   {
/* 253:455 */     if (this.m_Coefficients == null) {
/* 254:456 */       throw new Exception("Pace Regression: No model built yet.");
/* 255:    */     }
/* 256:460 */     if (checkForMissing(instance, this.m_Model)) {
/* 257:461 */       throw new NoSupportForMissingValuesException("Can't handle missing values!");
/* 258:    */     }
/* 259:466 */     return regressionPrediction(instance, this.m_Coefficients);
/* 260:    */   }
/* 261:    */   
/* 262:    */   public String toString()
/* 263:    */   {
/* 264:477 */     if (this.m_Coefficients == null) {
/* 265:478 */       return "Pace Regression: No model built yet.";
/* 266:    */     }
/* 267:481 */     StringBuffer text = new StringBuffer();
/* 268:    */     
/* 269:483 */     text.append("\nPace Regression Model\n\n");
/* 270:    */     
/* 271:485 */     text.append(this.m_Model.classAttribute().name() + " =\n\n");
/* 272:486 */     int index = 0;
/* 273:    */     
/* 274:488 */     text.append(Utils.doubleToString(this.m_Coefficients[0], 12, 4));
/* 275:490 */     for (int i = 1; i < this.m_Coefficients.length; i++)
/* 276:    */     {
/* 277:493 */       if (index == this.m_ClassIndex) {
/* 278:494 */         index++;
/* 279:    */       }
/* 280:497 */       if (this.m_Coefficients[i] != 0.0D)
/* 281:    */       {
/* 282:499 */         text.append(" +\n");
/* 283:500 */         text.append(Utils.doubleToString(this.m_Coefficients[i], 12, 4) + " * ");
/* 284:501 */         text.append(this.m_Model.attribute(index).name());
/* 285:    */       }
/* 286:503 */       index++;
/* 287:    */     }
/* 288:506 */     return text.toString();
/* 289:    */   }
/* 290:    */   
/* 291:    */   public Enumeration<Option> listOptions()
/* 292:    */   {
/* 293:517 */     Vector<Option> newVector = new Vector(2);
/* 294:    */     
/* 295:519 */     newVector.addElement(new Option("\tThe estimator can be one of the following:\n\t\teb -- Empirical Bayes estimator for noraml mixture (default)\n\t\tnested -- Optimal nested model selector for normal mixture\n\t\tsubset -- Optimal subset selector for normal mixture\n\t\tpace2 -- PACE2 for Chi-square mixture\n\t\tpace4 -- PACE4 for Chi-square mixture\n\t\tpace6 -- PACE6 for Chi-square mixture\n\n\t\tols -- Ordinary least squares estimator\n\t\taic -- AIC estimator\n\t\tbic -- BIC estimator\n\t\tric -- RIC estimator\n\t\tolsc -- Ordinary least squares subset selector with a threshold", "E", 0, "-E <estimator>"));
/* 296:    */     
/* 297:    */ 
/* 298:    */ 
/* 299:    */ 
/* 300:    */ 
/* 301:    */ 
/* 302:    */ 
/* 303:    */ 
/* 304:    */ 
/* 305:    */ 
/* 306:    */ 
/* 307:    */ 
/* 308:    */ 
/* 309:    */ 
/* 310:534 */     newVector.addElement(new Option("\tThreshold value for the OLSC estimator", "S", 0, "-S <threshold value>"));
/* 311:    */     
/* 312:    */ 
/* 313:537 */     newVector.addAll(Collections.list(super.listOptions()));
/* 314:    */     
/* 315:539 */     return newVector.elements();
/* 316:    */   }
/* 317:    */   
/* 318:    */   public void setOptions(String[] options)
/* 319:    */     throws Exception
/* 320:    */   {
/* 321:579 */     String estimator = Utils.getOption('E', options);
/* 322:580 */     if (estimator.equals("ols")) {
/* 323:581 */       this.paceEstimator = 0;
/* 324:582 */     } else if (estimator.equals("olsc")) {
/* 325:583 */       this.paceEstimator = 7;
/* 326:584 */     } else if ((estimator.equals("eb")) || (estimator.equals(""))) {
/* 327:585 */       this.paceEstimator = 1;
/* 328:586 */     } else if (estimator.equals("nested")) {
/* 329:587 */       this.paceEstimator = 2;
/* 330:588 */     } else if (estimator.equals("subset")) {
/* 331:589 */       this.paceEstimator = 3;
/* 332:590 */     } else if (estimator.equals("pace2")) {
/* 333:591 */       this.paceEstimator = 4;
/* 334:592 */     } else if (estimator.equals("pace4")) {
/* 335:593 */       this.paceEstimator = 5;
/* 336:594 */     } else if (estimator.equals("pace6")) {
/* 337:595 */       this.paceEstimator = 6;
/* 338:596 */     } else if (estimator.equals("aic")) {
/* 339:597 */       this.paceEstimator = 8;
/* 340:598 */     } else if (estimator.equals("bic")) {
/* 341:599 */       this.paceEstimator = 9;
/* 342:600 */     } else if (estimator.equals("ric")) {
/* 343:601 */       this.paceEstimator = 10;
/* 344:    */     } else {
/* 345:603 */       throw new WekaException("unknown estimator " + estimator + " for -E option");
/* 346:    */     }
/* 347:607 */     String string = Utils.getOption('S', options);
/* 348:608 */     if (!string.equals("")) {
/* 349:609 */       this.olscThreshold = Double.parseDouble(string);
/* 350:    */     }
/* 351:612 */     super.setOptions(options);
/* 352:    */     
/* 353:614 */     Utils.checkForRemainingOptions(options);
/* 354:    */   }
/* 355:    */   
/* 356:    */   public double[] coefficients()
/* 357:    */   {
/* 358:624 */     double[] coefficients = new double[this.m_Coefficients.length];
/* 359:625 */     for (int i = 0; i < coefficients.length; i++) {
/* 360:626 */       coefficients[i] = this.m_Coefficients[i];
/* 361:    */     }
/* 362:628 */     return coefficients;
/* 363:    */   }
/* 364:    */   
/* 365:    */   public String[] getOptions()
/* 366:    */   {
/* 367:639 */     Vector<String> options = new Vector();
/* 368:    */     
/* 369:641 */     options.add("-E");
/* 370:642 */     switch (this.paceEstimator)
/* 371:    */     {
/* 372:    */     case 0: 
/* 373:644 */       options.add("ols");
/* 374:645 */       break;
/* 375:    */     case 7: 
/* 376:647 */       options.add("olsc");
/* 377:648 */       options.add("-S");
/* 378:649 */       options.add("" + this.olscThreshold);
/* 379:650 */       break;
/* 380:    */     case 1: 
/* 381:652 */       options.add("eb");
/* 382:653 */       break;
/* 383:    */     case 2: 
/* 384:655 */       options.add("nested");
/* 385:656 */       break;
/* 386:    */     case 3: 
/* 387:658 */       options.add("subset");
/* 388:659 */       break;
/* 389:    */     case 4: 
/* 390:661 */       options.add("pace2");
/* 391:662 */       break;
/* 392:    */     case 5: 
/* 393:664 */       options.add("pace4");
/* 394:665 */       break;
/* 395:    */     case 6: 
/* 396:667 */       options.add("pace6");
/* 397:668 */       break;
/* 398:    */     case 8: 
/* 399:670 */       options.add("aic");
/* 400:671 */       break;
/* 401:    */     case 9: 
/* 402:673 */       options.add("bic");
/* 403:674 */       break;
/* 404:    */     case 10: 
/* 405:676 */       options.add("ric");
/* 406:    */     }
/* 407:680 */     Collections.addAll(options, super.getOptions());
/* 408:    */     
/* 409:682 */     return (String[])options.toArray(new String[0]);
/* 410:    */   }
/* 411:    */   
/* 412:    */   public int numParameters()
/* 413:    */   {
/* 414:691 */     return this.m_Coefficients.length - 1;
/* 415:    */   }
/* 416:    */   
/* 417:    */   public String estimatorTipText()
/* 418:    */   {
/* 419:701 */     return "The estimator to use.\n\neb -- Empirical Bayes estimator for noraml mixture (default)\nnested -- Optimal nested model selector for normal mixture\nsubset -- Optimal subset selector for normal mixture\npace2 -- PACE2 for Chi-square mixture\npace4 -- PACE4 for Chi-square mixture\npace6 -- PACE6 for Chi-square mixture\nols -- Ordinary least squares estimator\naic -- AIC estimator\nbic -- BIC estimator\nric -- RIC estimator\nolsc -- Ordinary least squares subset selector with a threshold";
/* 420:    */   }
/* 421:    */   
/* 422:    */   public SelectedTag getEstimator()
/* 423:    */   {
/* 424:720 */     return new SelectedTag(this.paceEstimator, TAGS_ESTIMATOR);
/* 425:    */   }
/* 426:    */   
/* 427:    */   public void setEstimator(SelectedTag estimator)
/* 428:    */   {
/* 429:730 */     if (estimator.getTags() == TAGS_ESTIMATOR) {
/* 430:731 */       this.paceEstimator = estimator.getSelectedTag().getID();
/* 431:    */     }
/* 432:    */   }
/* 433:    */   
/* 434:    */   public String thresholdTipText()
/* 435:    */   {
/* 436:742 */     return "Threshold for the olsc estimator.";
/* 437:    */   }
/* 438:    */   
/* 439:    */   public void setThreshold(double newThreshold)
/* 440:    */   {
/* 441:752 */     this.olscThreshold = newThreshold;
/* 442:    */   }
/* 443:    */   
/* 444:    */   public double getThreshold()
/* 445:    */   {
/* 446:762 */     return this.olscThreshold;
/* 447:    */   }
/* 448:    */   
/* 449:    */   private double regressionPrediction(Instance transformedInstance, double[] coefficients)
/* 450:    */     throws Exception
/* 451:    */   {
/* 452:778 */     int column = 0;
/* 453:779 */     double result = coefficients[column];
/* 454:780 */     for (int j = 0; j < transformedInstance.numAttributes(); j++) {
/* 455:781 */       if (this.m_ClassIndex != j)
/* 456:    */       {
/* 457:782 */         column++;
/* 458:783 */         result += coefficients[column] * transformedInstance.value(j);
/* 459:    */       }
/* 460:    */     }
/* 461:787 */     return result;
/* 462:    */   }
/* 463:    */   
/* 464:    */   public String getRevision()
/* 465:    */   {
/* 466:797 */     return RevisionUtils.extract("$Revision: 10374 $");
/* 467:    */   }
/* 468:    */   
/* 469:    */   public static void main(String[] argv)
/* 470:    */   {
/* 471:806 */     runClassifier(new PaceRegression(), argv);
/* 472:    */   }
/* 473:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.PaceRegression
 * JD-Core Version:    0.7.0.1
 */