/*   1:    */ package weka.classifiers.mi;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.classifiers.Classifier;
/*   9:    */ import weka.classifiers.SingleClassifierEnhancer;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*  15:    */ import weka.core.Optimization;
/*  16:    */ import weka.core.Option;
/*  17:    */ import weka.core.OptionHandler;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.TechnicalInformation;
/*  20:    */ import weka.core.TechnicalInformation.Field;
/*  21:    */ import weka.core.TechnicalInformation.Type;
/*  22:    */ import weka.core.TechnicalInformationHandler;
/*  23:    */ import weka.core.Utils;
/*  24:    */ import weka.core.WeightedInstancesHandler;
/*  25:    */ import weka.filters.Filter;
/*  26:    */ import weka.filters.unsupervised.attribute.Discretize;
/*  27:    */ import weka.filters.unsupervised.attribute.MultiInstanceToPropositional;
/*  28:    */ 
/*  29:    */ public class MIBoost
/*  30:    */   extends SingleClassifierEnhancer
/*  31:    */   implements OptionHandler, MultiInstanceCapabilitiesHandler, TechnicalInformationHandler
/*  32:    */ {
/*  33:    */   static final long serialVersionUID = -3808427225599279539L;
/*  34:    */   protected Classifier[] m_Models;
/*  35:    */   protected int m_NumClasses;
/*  36:    */   protected int[] m_Classes;
/*  37:    */   protected Instances m_Attributes;
/*  38:    */   private int m_NumIterations;
/*  39:    */   protected double[] m_Beta;
/*  40:    */   protected int m_MaxIterations;
/*  41:    */   protected int m_DiscretizeBin;
/*  42:    */   protected Discretize m_Filter;
/*  43:    */   protected MultiInstanceToPropositional m_ConvertToSI;
/*  44:    */   
/*  45:    */   public MIBoost()
/*  46:    */   {
/*  47:125 */     this.m_NumIterations = 100;
/*  48:    */     
/*  49:    */ 
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:131 */     this.m_MaxIterations = 10;
/*  54:    */     
/*  55:    */ 
/*  56:134 */     this.m_DiscretizeBin = 0;
/*  57:    */     
/*  58:    */ 
/*  59:137 */     this.m_Filter = null;
/*  60:    */     
/*  61:    */ 
/*  62:140 */     this.m_ConvertToSI = new MultiInstanceToPropositional();
/*  63:    */   }
/*  64:    */   
/*  65:    */   public String globalInfo()
/*  66:    */   {
/*  67:149 */     return "MI AdaBoost method, considers the geometric mean of posterior of instances inside a bag (arithmatic mean of log-posterior) and the expectation for a bag is taken inside the loss function.\n\nFor more information about Adaboost, see:\n\n" + getTechnicalInformation().toString();
/*  68:    */   }
/*  69:    */   
/*  70:    */   public TechnicalInformation getTechnicalInformation()
/*  71:    */   {
/*  72:167 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  73:168 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Yoav Freund and Robert E. Schapire");
/*  74:169 */     result.setValue(TechnicalInformation.Field.TITLE, "Experiments with a new boosting algorithm");
/*  75:170 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Thirteenth International Conference on Machine Learning");
/*  76:    */     
/*  77:172 */     result.setValue(TechnicalInformation.Field.YEAR, "1996");
/*  78:173 */     result.setValue(TechnicalInformation.Field.PAGES, "148-156");
/*  79:174 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
/*  80:175 */     result.setValue(TechnicalInformation.Field.ADDRESS, "San Francisco");
/*  81:    */     
/*  82:177 */     return result;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Enumeration<Option> listOptions()
/*  86:    */   {
/*  87:188 */     Vector<Option> result = new Vector();
/*  88:    */     
/*  89:190 */     result.addElement(new Option("\tThe number of bins in discretization\n\t(default 0, no discretization)", "B", 1, "-B <num>"));
/*  90:    */     
/*  91:    */ 
/*  92:193 */     result.addElement(new Option("\tMaximum number of boost iterations.\n\t(default 10)", "R", 1, "-R <num>"));
/*  93:    */     
/*  94:    */ 
/*  95:196 */     result.addAll(Collections.list(super.listOptions()));
/*  96:    */     
/*  97:198 */     return result.elements();
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void setOptions(String[] options)
/* 101:    */     throws Exception
/* 102:    */   {
/* 103:233 */     setDebug(Utils.getFlag('D', options));
/* 104:    */     
/* 105:235 */     String bin = Utils.getOption('B', options);
/* 106:236 */     if (bin.length() != 0) {
/* 107:237 */       setDiscretizeBin(Integer.parseInt(bin));
/* 108:    */     } else {
/* 109:239 */       setDiscretizeBin(0);
/* 110:    */     }
/* 111:242 */     String boostIterations = Utils.getOption('R', options);
/* 112:243 */     if (boostIterations.length() != 0) {
/* 113:244 */       setMaxIterations(Integer.parseInt(boostIterations));
/* 114:    */     } else {
/* 115:246 */       setMaxIterations(10);
/* 116:    */     }
/* 117:249 */     super.setOptions(options);
/* 118:    */     
/* 119:251 */     Utils.checkForRemainingOptions(options);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String[] getOptions()
/* 123:    */   {
/* 124:262 */     Vector<String> result = new Vector(4);
/* 125:    */     
/* 126:264 */     result.add("-R");
/* 127:265 */     result.add("" + getMaxIterations());
/* 128:    */     
/* 129:267 */     result.add("-B");
/* 130:268 */     result.add("" + getDiscretizeBin());
/* 131:    */     
/* 132:270 */     Collections.addAll(result, super.getOptions());
/* 133:    */     
/* 134:272 */     return (String[])result.toArray(new String[result.size()]);
/* 135:    */   }
/* 136:    */   
/* 137:    */   public String maxIterationsTipText()
/* 138:    */   {
/* 139:282 */     return "The maximum number of boost iterations.";
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void setMaxIterations(int maxIterations)
/* 143:    */   {
/* 144:291 */     this.m_MaxIterations = maxIterations;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public int getMaxIterations()
/* 148:    */   {
/* 149:301 */     return this.m_MaxIterations;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String discretizeBinTipText()
/* 153:    */   {
/* 154:311 */     return "The number of bins in discretization.";
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setDiscretizeBin(int bin)
/* 158:    */   {
/* 159:320 */     this.m_DiscretizeBin = bin;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public int getDiscretizeBin()
/* 163:    */   {
/* 164:329 */     return this.m_DiscretizeBin;
/* 165:    */   }
/* 166:    */   
/* 167:    */   private class OptEng
/* 168:    */     extends Optimization
/* 169:    */   {
/* 170:    */     private double[] weights;
/* 171:    */     private double[] errs;
/* 172:    */     
/* 173:    */     private OptEng() {}
/* 174:    */     
/* 175:    */     public void setWeights(double[] w)
/* 176:    */     {
/* 177:337 */       this.weights = w;
/* 178:    */     }
/* 179:    */     
/* 180:    */     public void setErrs(double[] e)
/* 181:    */     {
/* 182:341 */       this.errs = e;
/* 183:    */     }
/* 184:    */     
/* 185:    */     protected double objectiveFunction(double[] x)
/* 186:    */       throws Exception
/* 187:    */     {
/* 188:353 */       double obj = 0.0D;
/* 189:354 */       for (int i = 0; i < this.weights.length; i++)
/* 190:    */       {
/* 191:355 */         obj += this.weights[i] * Math.exp(x[0] * (2.0D * this.errs[i] - 1.0D));
/* 192:356 */         if (Double.isNaN(obj)) {
/* 193:357 */           throw new Exception("Objective function value is NaN!");
/* 194:    */         }
/* 195:    */       }
/* 196:361 */       return obj;
/* 197:    */     }
/* 198:    */     
/* 199:    */     protected double[] evaluateGradient(double[] x)
/* 200:    */       throws Exception
/* 201:    */     {
/* 202:373 */       double[] grad = new double[1];
/* 203:374 */       for (int i = 0; i < this.weights.length; i++)
/* 204:    */       {
/* 205:375 */         grad[0] += this.weights[i] * (2.0D * this.errs[i] - 1.0D) * Math.exp(x[0] * (2.0D * this.errs[i] - 1.0D));
/* 206:377 */         if (Double.isNaN(grad[0])) {
/* 207:378 */           throw new Exception("Gradient is NaN!");
/* 208:    */         }
/* 209:    */       }
/* 210:382 */       return grad;
/* 211:    */     }
/* 212:    */     
/* 213:    */     public String getRevision()
/* 214:    */     {
/* 215:392 */       return RevisionUtils.extract("$Revision: 10369 $");
/* 216:    */     }
/* 217:    */   }
/* 218:    */   
/* 219:    */   public Capabilities getCapabilities()
/* 220:    */   {
/* 221:403 */     Capabilities result = super.getCapabilities();
/* 222:    */     
/* 223:    */ 
/* 224:406 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 225:407 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 226:408 */     result.disable(Capabilities.Capability.MISSING_VALUES);
/* 227:    */     
/* 228:    */ 
/* 229:411 */     result.disableAllClasses();
/* 230:412 */     result.disableAllClassDependencies();
/* 231:413 */     if (super.getCapabilities().handles(Capabilities.Capability.BINARY_CLASS)) {
/* 232:414 */       result.enable(Capabilities.Capability.BINARY_CLASS);
/* 233:    */     }
/* 234:416 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 235:    */     
/* 236:    */ 
/* 237:419 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/* 238:    */     
/* 239:421 */     return result;
/* 240:    */   }
/* 241:    */   
/* 242:    */   public Capabilities getMultiInstanceCapabilities()
/* 243:    */   {
/* 244:433 */     Capabilities result = super.getCapabilities();
/* 245:    */     
/* 246:    */ 
/* 247:436 */     result.disableAllClasses();
/* 248:437 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 249:    */     
/* 250:439 */     return result;
/* 251:    */   }
/* 252:    */   
/* 253:    */   public void buildClassifier(Instances exps)
/* 254:    */     throws Exception
/* 255:    */   {
/* 256:453 */     getCapabilities().testWithFail(exps);
/* 257:    */     
/* 258:    */ 
/* 259:456 */     Instances train = new Instances(exps);
/* 260:457 */     train.deleteWithMissingClass();
/* 261:    */     
/* 262:459 */     this.m_NumClasses = train.numClasses();
/* 263:460 */     this.m_NumIterations = this.m_MaxIterations;
/* 264:462 */     if (this.m_Classifier == null) {
/* 265:463 */       throw new Exception("A base classifier has not been specified!");
/* 266:    */     }
/* 267:465 */     if (!(this.m_Classifier instanceof WeightedInstancesHandler)) {
/* 268:466 */       throw new Exception("Base classifier cannot handle weighted instances!");
/* 269:    */     }
/* 270:469 */     this.m_Models = AbstractClassifier.makeCopies(this.m_Classifier, getMaxIterations());
/* 271:470 */     if (this.m_Debug) {
/* 272:471 */       System.err.println("Base classifier: " + this.m_Classifier.getClass().getName());
/* 273:    */     }
/* 274:475 */     this.m_Beta = new double[this.m_NumIterations];
/* 275:    */     
/* 276:    */ 
/* 277:    */ 
/* 278:    */ 
/* 279:    */ 
/* 280:    */ 
/* 281:    */ 
/* 282:483 */     double N = train.numInstances();double sumNi = 0.0D;
/* 283:484 */     for (int i = 0; i < N; i++)
/* 284:    */     {
/* 285:485 */       int nn = train.instance(i).relationalValue(1).numInstances();
/* 286:486 */       sumNi += nn;
/* 287:    */     }
/* 288:488 */     for (int i = 0; i < N; i++) {
/* 289:489 */       train.instance(i).setWeight(sumNi / N);
/* 290:    */     }
/* 291:493 */     this.m_ConvertToSI.setInputFormat(train);
/* 292:494 */     Instances data = Filter.useFilter(train, this.m_ConvertToSI);
/* 293:    */     
/* 294:496 */     data.deleteAttributeAt(0);
/* 295:499 */     if (this.m_DiscretizeBin > 0)
/* 296:    */     {
/* 297:500 */       this.m_Filter = new Discretize();
/* 298:501 */       this.m_Filter.setInputFormat(new Instances(data, 0));
/* 299:502 */       this.m_Filter.setBins(this.m_DiscretizeBin);
/* 300:503 */       data = Filter.useFilter(data, this.m_Filter);
/* 301:    */     }
/* 302:508 */     for (int m = 0; m < this.m_MaxIterations; m++)
/* 303:    */     {
/* 304:511 */       this.m_Models[m].buildClassifier(data);
/* 305:    */       
/* 306:    */ 
/* 307:514 */       double[] err = new double[(int)N];double[] weights = new double[(int)N];
/* 308:515 */       boolean perfect = true;boolean tooWrong = true;
/* 309:516 */       int dataIdx = 0;
/* 310:517 */       for (int n = 0; n < N; n++)
/* 311:    */       {
/* 312:518 */         Instance exn = train.instance(n);
/* 313:    */         
/* 314:    */ 
/* 315:521 */         double nn = exn.relationalValue(1).numInstances();
/* 316:522 */         for (int p = 0; p < nn; p++)
/* 317:    */         {
/* 318:523 */           Instance testIns = data.instance(dataIdx++);
/* 319:524 */           if ((int)this.m_Models[m].classifyInstance(testIns) != (int)exn.classValue()) {
/* 320:526 */             err[n] += 1.0D;
/* 321:    */           }
/* 322:    */         }
/* 323:529 */         weights[n] = exn.weight();
/* 324:530 */         err[n] /= nn;
/* 325:531 */         if (err[n] > 0.5D) {
/* 326:532 */           perfect = false;
/* 327:    */         }
/* 328:534 */         if (err[n] < 0.5D) {
/* 329:535 */           tooWrong = false;
/* 330:    */         }
/* 331:    */       }
/* 332:539 */       if ((perfect) || (tooWrong))
/* 333:    */       {
/* 334:541 */         if (m == 0) {
/* 335:542 */           this.m_Beta[m] = 1.0D;
/* 336:    */         } else {
/* 337:544 */           this.m_Beta[m] = 0.0D;
/* 338:    */         }
/* 339:546 */         this.m_NumIterations = (m + 1);
/* 340:547 */         if (!this.m_Debug) {
/* 341:    */           break;
/* 342:    */         }
/* 343:548 */         System.err.println("No errors"); break;
/* 344:    */       }
/* 345:553 */       double[] x = new double[1];
/* 346:554 */       x[0] = 0.0D;
/* 347:555 */       double[][] b = new double[2][x.length];
/* 348:556 */       b[0][0] = (0.0D / 0.0D);
/* 349:557 */       b[1][0] = (0.0D / 0.0D);
/* 350:    */       
/* 351:559 */       OptEng opt = new OptEng(null);
/* 352:560 */       opt.setWeights(weights);
/* 353:561 */       opt.setErrs(err);
/* 354:563 */       if (this.m_Debug) {
/* 355:564 */         System.out.println("Start searching for c... ");
/* 356:    */       }
/* 357:566 */       x = opt.findArgmin(x, b);
/* 358:567 */       while (x == null)
/* 359:    */       {
/* 360:568 */         x = opt.getVarbValues();
/* 361:569 */         if (this.m_Debug) {
/* 362:570 */           System.out.println("200 iterations finished, not enough!");
/* 363:    */         }
/* 364:572 */         x = opt.findArgmin(x, b);
/* 365:    */       }
/* 366:574 */       if (this.m_Debug) {
/* 367:575 */         System.out.println("Finished.");
/* 368:    */       }
/* 369:577 */       this.m_Beta[m] = x[0];
/* 370:579 */       if (this.m_Debug) {
/* 371:580 */         System.err.println("c = " + this.m_Beta[m]);
/* 372:    */       }
/* 373:584 */       if ((Double.isInfinite(this.m_Beta[m])) || (Utils.smOrEq(this.m_Beta[m], 0.0D)))
/* 374:    */       {
/* 375:585 */         if (m == 0) {
/* 376:586 */           this.m_Beta[m] = 1.0D;
/* 377:    */         } else {
/* 378:588 */           this.m_Beta[m] = 0.0D;
/* 379:    */         }
/* 380:590 */         this.m_NumIterations = (m + 1);
/* 381:591 */         if (!this.m_Debug) {
/* 382:    */           break;
/* 383:    */         }
/* 384:592 */         System.err.println("Errors out of range!"); break;
/* 385:    */       }
/* 386:598 */       dataIdx = 0;
/* 387:599 */       double totWeights = 0.0D;
/* 388:600 */       for (int r = 0; r < N; r++)
/* 389:    */       {
/* 390:601 */         Instance exr = train.instance(r);
/* 391:602 */         exr.setWeight(weights[r] * Math.exp(this.m_Beta[m] * (2.0D * err[r] - 1.0D)));
/* 392:603 */         totWeights += exr.weight();
/* 393:    */       }
/* 394:606 */       if (this.m_Debug) {
/* 395:607 */         System.err.println("Total weights = " + totWeights);
/* 396:    */       }
/* 397:610 */       for (int r = 0; r < N; r++)
/* 398:    */       {
/* 399:611 */         Instance exr = train.instance(r);
/* 400:612 */         double num = exr.relationalValue(1).numInstances();
/* 401:613 */         exr.setWeight(sumNi * exr.weight() / totWeights);
/* 402:616 */         for (int s = 0; s < num; s++)
/* 403:    */         {
/* 404:617 */           Instance inss = data.instance(dataIdx);
/* 405:618 */           inss.setWeight(exr.weight() / num);
/* 406:622 */           if (Double.isNaN(inss.weight())) {
/* 407:623 */             throw new Exception("instance " + s + " in bag " + r + " has weight NaN!");
/* 408:    */           }
/* 409:626 */           dataIdx++;
/* 410:    */         }
/* 411:    */       }
/* 412:    */     }
/* 413:    */   }
/* 414:    */   
/* 415:    */   public double[] distributionForInstance(Instance exmp)
/* 416:    */     throws Exception
/* 417:    */   {
/* 418:644 */     double[] rt = new double[this.m_NumClasses];
/* 419:    */     
/* 420:646 */     Instances insts = new Instances(exmp.dataset(), 0);
/* 421:647 */     insts.add(exmp);
/* 422:    */     
/* 423:    */ 
/* 424:650 */     insts = Filter.useFilter(insts, this.m_ConvertToSI);
/* 425:651 */     insts.deleteAttributeAt(0);
/* 426:    */     
/* 427:653 */     double n = insts.numInstances();
/* 428:655 */     if (this.m_DiscretizeBin > 0) {
/* 429:656 */       insts = Filter.useFilter(insts, this.m_Filter);
/* 430:    */     }
/* 431:659 */     for (int y = 0; y < n; y++)
/* 432:    */     {
/* 433:660 */       Instance ins = insts.instance(y);
/* 434:661 */       for (int x = 0; x < this.m_NumIterations; x++) {
/* 435:662 */         rt[((int)this.m_Models[x].classifyInstance(ins))] += this.m_Beta[x] / n;
/* 436:    */       }
/* 437:    */     }
/* 438:666 */     for (int i = 0; i < rt.length; i++) {
/* 439:667 */       rt[i] = Math.exp(rt[i]);
/* 440:    */     }
/* 441:670 */     Utils.normalize(rt);
/* 442:671 */     return rt;
/* 443:    */   }
/* 444:    */   
/* 445:    */   public String toString()
/* 446:    */   {
/* 447:682 */     if (this.m_Models == null) {
/* 448:683 */       return "No model built yet!";
/* 449:    */     }
/* 450:685 */     StringBuffer text = new StringBuffer();
/* 451:686 */     text.append("MIBoost: number of bins in discretization = " + this.m_DiscretizeBin + "\n");
/* 452:688 */     if (this.m_NumIterations == 0)
/* 453:    */     {
/* 454:689 */       text.append("No model built yet.\n");
/* 455:    */     }
/* 456:690 */     else if (this.m_NumIterations == 1)
/* 457:    */     {
/* 458:691 */       text.append("No boosting possible, one classifier used: Weight = " + Utils.roundDouble(this.m_Beta[0], 2) + "\n");
/* 459:    */       
/* 460:693 */       text.append("Base classifiers:\n" + this.m_Models[0].toString());
/* 461:    */     }
/* 462:    */     else
/* 463:    */     {
/* 464:695 */       text.append("Base classifiers and their weights: \n");
/* 465:696 */       for (int i = 0; i < this.m_NumIterations; i++) {
/* 466:697 */         text.append("\n\n" + i + ": Weight = " + Utils.roundDouble(this.m_Beta[i], 2) + "\nBase classifier:\n" + this.m_Models[i].toString());
/* 467:    */       }
/* 468:    */     }
/* 469:703 */     text.append("\n\nNumber of performed Iterations: " + this.m_NumIterations + "\n");
/* 470:    */     
/* 471:    */ 
/* 472:706 */     return text.toString();
/* 473:    */   }
/* 474:    */   
/* 475:    */   public String getRevision()
/* 476:    */   {
/* 477:716 */     return RevisionUtils.extract("$Revision: 10369 $");
/* 478:    */   }
/* 479:    */   
/* 480:    */   public static void main(String[] argv)
/* 481:    */   {
/* 482:726 */     runClassifier(new MIBoost(), argv);
/* 483:    */   }
/* 484:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.MIBoost
 * JD-Core Version:    0.7.0.1
 */