/*   1:    */ package weka.gui.explorer;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import weka.classifiers.Classifier;
/*   5:    */ import weka.classifiers.Evaluation;
/*   6:    */ import weka.classifiers.IntervalEstimator;
/*   7:    */ import weka.classifiers.evaluation.NumericPrediction;
/*   8:    */ import weka.classifiers.evaluation.Prediction;
/*   9:    */ import weka.classifiers.misc.InputMappedClassifier;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.DenseInstance;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.gui.visualize.PlotData2D;
/*  16:    */ 
/*  17:    */ public class ClassifierErrorsPlotInstances
/*  18:    */   extends AbstractPlotInstances
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = -3941976365792013279L;
/*  21:    */   protected int m_MinimumPlotSizeNumeric;
/*  22:    */   protected int m_MaximumPlotSizeNumeric;
/*  23:    */   protected boolean m_SaveForVisualization;
/*  24:    */   protected boolean m_pointSizeProportionalToMargin;
/*  25:    */   protected ArrayList<Integer> m_PlotShapes;
/*  26:    */   protected ArrayList<Object> m_PlotSizes;
/*  27:    */   protected Classifier m_Classifier;
/*  28:    */   protected int m_ClassIndex;
/*  29:    */   protected Evaluation m_Evaluation;
/*  30:    */   
/*  31:    */   protected void initialize()
/*  32:    */   {
/*  33:108 */     super.initialize();
/*  34:    */     
/*  35:110 */     this.m_PlotShapes = new ArrayList();
/*  36:111 */     this.m_PlotSizes = new ArrayList();
/*  37:112 */     this.m_Classifier = null;
/*  38:113 */     this.m_ClassIndex = -1;
/*  39:114 */     this.m_Evaluation = null;
/*  40:115 */     this.m_SaveForVisualization = true;
/*  41:116 */     this.m_MinimumPlotSizeNumeric = ExplorerDefaults.getClassifierErrorsMinimumPlotSizeNumeric();
/*  42:    */     
/*  43:118 */     this.m_MaximumPlotSizeNumeric = ExplorerDefaults.getClassifierErrorsMaximumPlotSizeNumeric();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public ArrayList<Integer> getPlotShapes()
/*  47:    */   {
/*  48:128 */     return this.m_PlotShapes;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public ArrayList<Object> getPlotSizes()
/*  52:    */   {
/*  53:137 */     return this.m_PlotSizes;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setPlotShapes(ArrayList<Integer> plotShapes)
/*  57:    */   {
/*  58:146 */     this.m_PlotShapes = plotShapes;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setPlotSizes(ArrayList<Object> plotSizes)
/*  62:    */   {
/*  63:155 */     this.m_PlotSizes = plotSizes;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setClassifier(Classifier value)
/*  67:    */   {
/*  68:164 */     this.m_Classifier = value;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public Classifier getClassifier()
/*  72:    */   {
/*  73:173 */     return this.m_Classifier;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setClassIndex(int index)
/*  77:    */   {
/*  78:182 */     this.m_ClassIndex = index;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public int getClassIndex()
/*  82:    */   {
/*  83:191 */     return this.m_ClassIndex;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setEvaluation(Evaluation value)
/*  87:    */   {
/*  88:200 */     this.m_Evaluation = value;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public Evaluation getEvaluation()
/*  92:    */   {
/*  93:209 */     return this.m_Evaluation;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setSaveForVisualization(boolean value)
/*  97:    */   {
/*  98:219 */     this.m_SaveForVisualization = value;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public boolean getSaveForVisualization()
/* 102:    */   {
/* 103:229 */     return this.m_SaveForVisualization;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void setPointSizeProportionalToMargin(boolean b)
/* 107:    */   {
/* 108:239 */     this.m_pointSizeProportionalToMargin = b;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public boolean getPointSizeProportionalToMargin()
/* 112:    */   {
/* 113:249 */     return this.m_pointSizeProportionalToMargin;
/* 114:    */   }
/* 115:    */   
/* 116:    */   protected void check()
/* 117:    */   {
/* 118:257 */     super.check();
/* 119:259 */     if (this.m_Classifier == null) {
/* 120:260 */       throw new IllegalStateException("No classifier set!");
/* 121:    */     }
/* 122:263 */     if (this.m_ClassIndex == -1) {
/* 123:264 */       throw new IllegalStateException("No class index set!");
/* 124:    */     }
/* 125:267 */     if (this.m_Evaluation == null) {
/* 126:268 */       throw new IllegalStateException("No evaluation set");
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:    */   protected void determineFormat()
/* 131:    */   {
/* 132:283 */     Attribute margin = null;
/* 133:287 */     if (!this.m_SaveForVisualization)
/* 134:    */     {
/* 135:288 */       this.m_PlotInstances = null;
/* 136:289 */       return;
/* 137:    */     }
/* 138:292 */     ArrayList<Attribute> hv = new ArrayList();
/* 139:    */     
/* 140:294 */     Attribute classAt = this.m_Instances.attribute(this.m_ClassIndex);
/* 141:    */     Attribute predictedClass;
/* 142:295 */     if (classAt.isNominal())
/* 143:    */     {
/* 144:296 */       ArrayList<String> attVals = new ArrayList();
/* 145:297 */       for (int i = 0; i < classAt.numValues(); i++) {
/* 146:298 */         attVals.add(classAt.value(i));
/* 147:    */       }
/* 148:300 */       Attribute predictedClass = new Attribute("predicted " + classAt.name(), attVals);
/* 149:301 */       margin = new Attribute("prediction margin");
/* 150:    */     }
/* 151:    */     else
/* 152:    */     {
/* 153:303 */       predictedClass = new Attribute("predicted" + classAt.name());
/* 154:    */     }
/* 155:306 */     for (int i = 0; i < this.m_Instances.numAttributes(); i++)
/* 156:    */     {
/* 157:307 */       if (i == this.m_Instances.classIndex())
/* 158:    */       {
/* 159:308 */         if (classAt.isNominal()) {
/* 160:309 */           hv.add(margin);
/* 161:    */         }
/* 162:311 */         hv.add(predictedClass);
/* 163:    */       }
/* 164:313 */       hv.add((Attribute)this.m_Instances.attribute(i).copy());
/* 165:    */     }
/* 166:316 */     this.m_PlotInstances = new Instances(this.m_Instances.relationName() + "_predicted", hv, this.m_Instances.numInstances());
/* 167:318 */     if (classAt.isNominal()) {
/* 168:319 */       this.m_PlotInstances.setClassIndex(this.m_ClassIndex + 2);
/* 169:    */     } else {
/* 170:321 */       this.m_PlotInstances.setClassIndex(this.m_ClassIndex + 1);
/* 171:    */     }
/* 172:    */   }
/* 173:    */   
/* 174:    */   public void process(Instances batch, double[][] predictions, Evaluation eval)
/* 175:    */   {
/* 176:    */     try
/* 177:    */     {
/* 178:328 */       for (int j = 0; j < batch.numInstances(); j++)
/* 179:    */       {
/* 180:329 */         Instance toPredict = batch.instance(j);
/* 181:330 */         double[] preds = predictions[j];
/* 182:331 */         double probActual = 0.0D;
/* 183:332 */         double probNext = 0.0D;
/* 184:    */         
/* 185:334 */         double pred = 0.0D;
/* 186:335 */         if (batch.classAttribute().isNominal())
/* 187:    */         {
/* 188:336 */           pred = Utils.sum(preds) == 0.0D ? Utils.missingValue() : Utils.maxIndex(preds);
/* 189:    */           
/* 190:    */ 
/* 191:339 */           probActual = !Utils.isMissingValue(toPredict.classIndex()) ? preds[((int)toPredict.classValue())] : Utils.sum(preds) == 0.0D ? Utils.missingValue() : preds[Utils.maxIndex(preds)];
/* 192:343 */           for (int i = 0; i < toPredict.classAttribute().numValues(); i++) {
/* 193:344 */             if ((i != (int)toPredict.classValue()) && (preds[i] > probNext)) {
/* 194:345 */               probNext = preds[i];
/* 195:    */             }
/* 196:    */           }
/* 197:    */         }
/* 198:    */         else
/* 199:    */         {
/* 200:349 */           pred = preds[0];
/* 201:    */         }
/* 202:352 */         eval.evaluationForSingleInstance(preds, toPredict, true);
/* 203:354 */         if (this.m_SaveForVisualization) {
/* 204:358 */           if (this.m_PlotInstances != null)
/* 205:    */           {
/* 206:359 */             double[] values = new double[this.m_PlotInstances.numAttributes()];
/* 207:360 */             boolean isNominal = toPredict.classAttribute().isNominal();
/* 208:361 */             for (int i = 0; i < this.m_PlotInstances.numAttributes(); i++) {
/* 209:362 */               if (i < toPredict.classIndex()) {
/* 210:363 */                 values[i] = toPredict.value(i);
/* 211:364 */               } else if (i == toPredict.classIndex())
/* 212:    */               {
/* 213:365 */                 if (isNominal)
/* 214:    */                 {
/* 215:366 */                   values[i] = (probActual - probNext);
/* 216:367 */                   values[(i + 1)] = pred;
/* 217:368 */                   values[(i + 2)] = toPredict.value(i);
/* 218:369 */                   i += 2;
/* 219:    */                 }
/* 220:    */                 else
/* 221:    */                 {
/* 222:371 */                   values[i] = pred;
/* 223:372 */                   values[(i + 1)] = toPredict.value(i);
/* 224:373 */                   i++;
/* 225:    */                 }
/* 226:    */               }
/* 227:376 */               else if (isNominal) {
/* 228:377 */                 values[i] = toPredict.value(i - 2);
/* 229:    */               } else {
/* 230:379 */                 values[i] = toPredict.value(i - 1);
/* 231:    */               }
/* 232:    */             }
/* 233:384 */             this.m_PlotInstances.add(new DenseInstance(1.0D, values));
/* 234:386 */             if (toPredict.classAttribute().isNominal())
/* 235:    */             {
/* 236:387 */               if ((toPredict.isMissing(toPredict.classIndex())) || (Utils.isMissingValue(pred))) {
/* 237:389 */                 this.m_PlotShapes.add(new Integer(2000));
/* 238:390 */               } else if (pred != toPredict.classValue()) {
/* 239:392 */                 this.m_PlotShapes.add(new Integer(1000));
/* 240:    */               } else {
/* 241:395 */                 this.m_PlotShapes.add(new Integer(-1));
/* 242:    */               }
/* 243:398 */               if (this.m_pointSizeProportionalToMargin)
/* 244:    */               {
/* 245:400 */                 this.m_PlotSizes.add(new Double(probActual - probNext));
/* 246:    */               }
/* 247:    */               else
/* 248:    */               {
/* 249:402 */                 int sizeAdj = 0;
/* 250:403 */                 if (pred != toPredict.classValue()) {
/* 251:404 */                   sizeAdj = 1;
/* 252:    */                 }
/* 253:406 */                 this.m_PlotSizes.add(new Integer(2 + sizeAdj));
/* 254:    */               }
/* 255:    */             }
/* 256:    */             else
/* 257:    */             {
/* 258:410 */               Double errd = null;
/* 259:411 */               if ((!toPredict.isMissing(toPredict.classIndex())) && (!Utils.isMissingValue(pred)))
/* 260:    */               {
/* 261:413 */                 errd = new Double(pred - toPredict.classValue());
/* 262:414 */                 this.m_PlotShapes.add(new Integer(-1));
/* 263:    */               }
/* 264:    */               else
/* 265:    */               {
/* 266:418 */                 this.m_PlotShapes.add(new Integer(2000));
/* 267:    */               }
/* 268:420 */               this.m_PlotSizes.add(errd);
/* 269:    */             }
/* 270:    */           }
/* 271:    */         }
/* 272:    */       }
/* 273:    */     }
/* 274:    */     catch (Exception ex)
/* 275:    */     {
/* 276:425 */       ex.printStackTrace();
/* 277:    */     }
/* 278:    */   }
/* 279:    */   
/* 280:    */   public void process(Instance toPredict, Classifier classifier, Evaluation eval)
/* 281:    */   {
/* 282:    */     try
/* 283:    */     {
/* 284:452 */       double pred = 0.0D;
/* 285:    */       
/* 286:454 */       double[] preds = null;
/* 287:455 */       double probActual = 0.0D;
/* 288:456 */       double probNext = 0.0D;
/* 289:457 */       int mappedClass = -1;
/* 290:    */       
/* 291:459 */       Instance classMissing = (Instance)toPredict.copy();
/* 292:460 */       classMissing.setDataset(toPredict.dataset());
/* 293:465 */       if (((classifier instanceof InputMappedClassifier)) && (toPredict.classAttribute().isNominal()))
/* 294:    */       {
/* 295:467 */         toPredict = (Instance)toPredict.copy();
/* 296:468 */         toPredict = ((InputMappedClassifier)classifier).constructMappedInstance(toPredict);
/* 297:    */         
/* 298:470 */         mappedClass = ((InputMappedClassifier)classifier).getMappedClassIndex();
/* 299:    */         
/* 300:472 */         classMissing.setMissing(mappedClass);
/* 301:    */       }
/* 302:    */       else
/* 303:    */       {
/* 304:474 */         classMissing.setClassMissing();
/* 305:    */       }
/* 306:477 */       if (toPredict.classAttribute().isNominal())
/* 307:    */       {
/* 308:478 */         preds = classifier.distributionForInstance(classMissing);
/* 309:    */         
/* 310:480 */         pred = Utils.sum(preds) == 0.0D ? Utils.missingValue() : Utils.maxIndex(preds);
/* 311:    */         
/* 312:    */ 
/* 313:483 */         probActual = !Utils.isMissingValue(toPredict.classIndex()) ? preds[((int)toPredict.classValue())] : Utils.sum(preds) == 0.0D ? Utils.missingValue() : preds[Utils.maxIndex(preds)];
/* 314:487 */         for (int i = 0; i < toPredict.classAttribute().numValues(); i++) {
/* 315:488 */           if ((i != (int)toPredict.classValue()) && (preds[i] > probNext)) {
/* 316:489 */             probNext = preds[i];
/* 317:    */           }
/* 318:    */         }
/* 319:493 */         eval.evaluationForSingleInstance(preds, toPredict, true);
/* 320:    */       }
/* 321:    */       else
/* 322:    */       {
/* 323:498 */         pred = eval.evaluateModelOnceAndRecordPrediction(classifier, toPredict);
/* 324:    */       }
/* 325:503 */       if (!this.m_SaveForVisualization) {
/* 326:504 */         return;
/* 327:    */       }
/* 328:507 */       if (this.m_PlotInstances != null)
/* 329:    */       {
/* 330:508 */         boolean isNominal = toPredict.classAttribute().isNominal();
/* 331:509 */         double[] values = new double[this.m_PlotInstances.numAttributes()];
/* 332:510 */         for (int i = 0; i < this.m_PlotInstances.numAttributes(); i++) {
/* 333:511 */           if (i < toPredict.classIndex()) {
/* 334:512 */             values[i] = toPredict.value(i);
/* 335:513 */           } else if (i == toPredict.classIndex())
/* 336:    */           {
/* 337:514 */             if (isNominal)
/* 338:    */             {
/* 339:515 */               values[i] = (probActual - probNext);
/* 340:516 */               values[(i + 1)] = pred;
/* 341:517 */               values[(i + 2)] = toPredict.value(i);
/* 342:518 */               i += 2;
/* 343:    */             }
/* 344:    */             else
/* 345:    */             {
/* 346:520 */               values[i] = pred;
/* 347:521 */               values[(i + 1)] = toPredict.value(i);
/* 348:522 */               i++;
/* 349:    */             }
/* 350:    */           }
/* 351:525 */           else if (isNominal) {
/* 352:526 */             values[i] = toPredict.value(i - 2);
/* 353:    */           } else {
/* 354:528 */             values[i] = toPredict.value(i - 1);
/* 355:    */           }
/* 356:    */         }
/* 357:533 */         this.m_PlotInstances.add(new DenseInstance(1.0D, values));
/* 358:535 */         if (toPredict.classAttribute().isNominal())
/* 359:    */         {
/* 360:536 */           if ((toPredict.isMissing(toPredict.classIndex())) || (Utils.isMissingValue(pred))) {
/* 361:538 */             this.m_PlotShapes.add(new Integer(2000));
/* 362:539 */           } else if (pred != toPredict.classValue()) {
/* 363:541 */             this.m_PlotShapes.add(new Integer(1000));
/* 364:    */           } else {
/* 365:544 */             this.m_PlotShapes.add(new Integer(-1));
/* 366:    */           }
/* 367:546 */           if (this.m_pointSizeProportionalToMargin)
/* 368:    */           {
/* 369:548 */             this.m_PlotSizes.add(new Double(probActual - probNext));
/* 370:    */           }
/* 371:    */           else
/* 372:    */           {
/* 373:550 */             int sizeAdj = 0;
/* 374:551 */             if (pred != toPredict.classValue()) {
/* 375:552 */               sizeAdj = 1;
/* 376:    */             }
/* 377:554 */             this.m_PlotSizes.add(new Integer(2 + sizeAdj));
/* 378:    */           }
/* 379:    */         }
/* 380:    */         else
/* 381:    */         {
/* 382:558 */           Double errd = null;
/* 383:559 */           if ((!toPredict.isMissing(toPredict.classIndex())) && (!Utils.isMissingValue(pred)))
/* 384:    */           {
/* 385:562 */             errd = new Double(pred - toPredict.classValue());
/* 386:563 */             this.m_PlotShapes.add(new Integer(-1));
/* 387:    */           }
/* 388:    */           else
/* 389:    */           {
/* 390:567 */             this.m_PlotShapes.add(new Integer(2000));
/* 391:    */           }
/* 392:569 */           this.m_PlotSizes.add(errd);
/* 393:    */         }
/* 394:    */       }
/* 395:    */     }
/* 396:    */     catch (Exception ex)
/* 397:    */     {
/* 398:573 */       ex.printStackTrace();
/* 399:    */     }
/* 400:    */   }
/* 401:    */   
/* 402:    */   protected void scaleNumericPredictions()
/* 403:    */   {
/* 404:589 */     double maxErr = (-1.0D / 0.0D);
/* 405:590 */     double minErr = (1.0D / 0.0D);
/* 406:592 */     if (this.m_Instances.classAttribute().isNominal())
/* 407:    */     {
/* 408:593 */       maxErr = 1.0D;
/* 409:594 */       minErr = 0.0D;
/* 410:    */     }
/* 411:    */     else
/* 412:    */     {
/* 413:598 */       for (int i = 0; i < this.m_PlotSizes.size(); i++)
/* 414:    */       {
/* 415:599 */         Double errd = (Double)this.m_PlotSizes.get(i);
/* 416:600 */         if (errd != null)
/* 417:    */         {
/* 418:601 */           double err = Math.abs(errd.doubleValue());
/* 419:602 */           if (err < minErr) {
/* 420:603 */             minErr = err;
/* 421:    */           }
/* 422:605 */           if (err > maxErr) {
/* 423:606 */             maxErr = err;
/* 424:    */           }
/* 425:    */         }
/* 426:    */       }
/* 427:    */     }
/* 428:613 */     for (int i = 0; i < this.m_PlotSizes.size(); i++)
/* 429:    */     {
/* 430:614 */       Double errd = (Double)this.m_PlotSizes.get(i);
/* 431:615 */       if (errd != null)
/* 432:    */       {
/* 433:616 */         double err = Math.abs(errd.doubleValue());
/* 434:617 */         if (maxErr - minErr > 0.0D)
/* 435:    */         {
/* 436:618 */           double temp = (err - minErr) / (maxErr - minErr) * (this.m_MaximumPlotSizeNumeric - this.m_MinimumPlotSizeNumeric + 1);
/* 437:    */           
/* 438:620 */           this.m_PlotSizes.set(i, Integer.valueOf(new Integer((int)temp).intValue() + this.m_MinimumPlotSizeNumeric));
/* 439:    */         }
/* 440:    */         else
/* 441:    */         {
/* 442:623 */           this.m_PlotSizes.set(i, new Integer(this.m_MinimumPlotSizeNumeric));
/* 443:    */         }
/* 444:    */       }
/* 445:    */       else
/* 446:    */       {
/* 447:626 */         this.m_PlotSizes.set(i, new Integer(this.m_MinimumPlotSizeNumeric));
/* 448:    */       }
/* 449:    */     }
/* 450:    */   }
/* 451:    */   
/* 452:    */   protected void addPredictionIntervals()
/* 453:    */   {
/* 454:650 */     int maxNum = 0;
/* 455:651 */     ArrayList<Prediction> preds = this.m_Evaluation.predictions();
/* 456:652 */     for (int i = 0; i < preds.size(); i++)
/* 457:    */     {
/* 458:653 */       int num = ((NumericPrediction)preds.get(i)).predictionIntervals().length;
/* 459:654 */       if (num > maxNum) {
/* 460:655 */         maxNum = num;
/* 461:    */       }
/* 462:    */     }
/* 463:660 */     ArrayList<Attribute> atts = new ArrayList();
/* 464:661 */     for (i = 0; i < this.m_PlotInstances.numAttributes(); i++) {
/* 465:662 */       atts.add(this.m_PlotInstances.attribute(i));
/* 466:    */     }
/* 467:664 */     for (i = 0; i < maxNum; i++)
/* 468:    */     {
/* 469:665 */       atts.add(new Attribute("predictionInterval_" + (i + 1) + "-lowerBoundary"));
/* 470:    */       
/* 471:667 */       atts.add(new Attribute("predictionInterval_" + (i + 1) + "-upperBoundary"));
/* 472:    */       
/* 473:669 */       atts.add(new Attribute("predictionInterval_" + (i + 1) + "-width"));
/* 474:    */     }
/* 475:671 */     Instances data = new Instances(this.m_PlotInstances.relationName(), atts, this.m_PlotInstances.numInstances());
/* 476:    */     
/* 477:673 */     data.setClassIndex(this.m_PlotInstances.classIndex());
/* 478:676 */     for (i = 0; i < this.m_PlotInstances.numInstances(); i++)
/* 479:    */     {
/* 480:677 */       Instance inst = this.m_PlotInstances.instance(i);
/* 481:    */       
/* 482:679 */       double[] values = new double[data.numAttributes()];
/* 483:680 */       System.arraycopy(inst.toDoubleArray(), 0, values, 0, inst.numAttributes());
/* 484:    */       
/* 485:    */ 
/* 486:683 */       double[][] predInt = ((NumericPrediction)preds.get(i)).predictionIntervals();
/* 487:684 */       for (int n = 0; n < maxNum; n++) {
/* 488:685 */         if (n < predInt.length)
/* 489:    */         {
/* 490:686 */           values[(this.m_PlotInstances.numAttributes() + n * 3 + 0)] = predInt[n][0];
/* 491:687 */           values[(this.m_PlotInstances.numAttributes() + n * 3 + 1)] = predInt[n][1];
/* 492:688 */           values[(this.m_PlotInstances.numAttributes() + n * 3 + 2)] = (predInt[n][1] - predInt[n][0]);
/* 493:    */         }
/* 494:    */         else
/* 495:    */         {
/* 496:691 */           values[(this.m_PlotInstances.numAttributes() + n * 3 + 0)] = Utils.missingValue();
/* 497:    */           
/* 498:693 */           values[(this.m_PlotInstances.numAttributes() + n * 3 + 1)] = Utils.missingValue();
/* 499:    */           
/* 500:695 */           values[(this.m_PlotInstances.numAttributes() + n * 3 + 2)] = Utils.missingValue();
/* 501:    */         }
/* 502:    */       }
/* 503:700 */       Instance newInst = new DenseInstance(inst.weight(), values);
/* 504:701 */       data.add(newInst);
/* 505:    */     }
/* 506:704 */     this.m_PlotInstances = data;
/* 507:    */   }
/* 508:    */   
/* 509:    */   protected void finishUp()
/* 510:    */   {
/* 511:715 */     super.finishUp();
/* 512:717 */     if (!this.m_SaveForVisualization) {
/* 513:718 */       return;
/* 514:    */     }
/* 515:721 */     if ((this.m_Instances.classAttribute().isNumeric()) || (this.m_pointSizeProportionalToMargin)) {
/* 516:723 */       scaleNumericPredictions();
/* 517:    */     }
/* 518:727 */     if ((this.m_Instances.attribute(this.m_ClassIndex).isNumeric()) && 
/* 519:728 */       ((this.m_Classifier instanceof IntervalEstimator))) {
/* 520:729 */       addPredictionIntervals();
/* 521:    */     }
/* 522:    */   }
/* 523:    */   
/* 524:    */   protected PlotData2D createPlotData(String name)
/* 525:    */     throws Exception
/* 526:    */   {
/* 527:745 */     if (!this.m_SaveForVisualization) {
/* 528:746 */       return null;
/* 529:    */     }
/* 530:749 */     PlotData2D result = new PlotData2D(this.m_PlotInstances);
/* 531:750 */     result.setShapeSize(this.m_PlotSizes);
/* 532:751 */     result.setShapeType(this.m_PlotShapes);
/* 533:752 */     result.setPlotName(name + " (" + this.m_Instances.relationName() + ")");
/* 534:    */     
/* 535:    */ 
/* 536:755 */     return result;
/* 537:    */   }
/* 538:    */   
/* 539:    */   public void cleanUp()
/* 540:    */   {
/* 541:763 */     super.cleanUp();
/* 542:    */     
/* 543:765 */     this.m_Classifier = null;
/* 544:766 */     this.m_PlotShapes = null;
/* 545:767 */     this.m_PlotSizes = null;
/* 546:768 */     this.m_Evaluation = null;
/* 547:    */   }
/* 548:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.ClassifierErrorsPlotInstances
 * JD-Core Version:    0.7.0.1
 */