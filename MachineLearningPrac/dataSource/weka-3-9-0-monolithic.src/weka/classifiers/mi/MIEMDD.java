/*   1:    */ package weka.classifiers.mi;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Random;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.classifiers.RandomizableClassifier;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.Capabilities;
/*  12:    */ import weka.core.Capabilities.Capability;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*  16:    */ import weka.core.Optimization;
/*  17:    */ import weka.core.Option;
/*  18:    */ import weka.core.OptionHandler;
/*  19:    */ import weka.core.RevisionUtils;
/*  20:    */ import weka.core.SelectedTag;
/*  21:    */ import weka.core.Tag;
/*  22:    */ import weka.core.TechnicalInformation;
/*  23:    */ import weka.core.TechnicalInformation.Field;
/*  24:    */ import weka.core.TechnicalInformation.Type;
/*  25:    */ import weka.core.TechnicalInformationHandler;
/*  26:    */ import weka.core.Utils;
/*  27:    */ import weka.filters.Filter;
/*  28:    */ import weka.filters.unsupervised.attribute.Normalize;
/*  29:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  30:    */ import weka.filters.unsupervised.attribute.Standardize;
/*  31:    */ 
/*  32:    */ public class MIEMDD
/*  33:    */   extends RandomizableClassifier
/*  34:    */   implements OptionHandler, MultiInstanceCapabilitiesHandler, TechnicalInformationHandler
/*  35:    */ {
/*  36:    */   static final long serialVersionUID = 3899547154866223734L;
/*  37:    */   protected int m_ClassIndex;
/*  38:    */   protected double[] m_Par;
/*  39:    */   protected int m_NumClasses;
/*  40:    */   protected int[] m_Classes;
/*  41:    */   protected double[][][] m_Data;
/*  42:    */   protected Instances m_Attributes;
/*  43:    */   protected double[][] m_emData;
/*  44:    */   protected Filter m_Filter;
/*  45:    */   protected int m_filterType;
/*  46:    */   public static final int FILTER_NORMALIZE = 0;
/*  47:    */   public static final int FILTER_STANDARDIZE = 1;
/*  48:    */   public static final int FILTER_NONE = 2;
/*  49:149 */   public static final Tag[] TAGS_FILTER = { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
/*  50:    */   protected ReplaceMissingValues m_Missing;
/*  51:    */   
/*  52:    */   public MIEMDD()
/*  53:    */   {
/*  54:137 */     this.m_Filter = null;
/*  55:    */     
/*  56:    */ 
/*  57:140 */     this.m_filterType = 1;
/*  58:    */     
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62:    */ 
/*  63:    */ 
/*  64:    */ 
/*  65:    */ 
/*  66:    */ 
/*  67:    */ 
/*  68:    */ 
/*  69:    */ 
/*  70:    */ 
/*  71:    */ 
/*  72:155 */     this.m_Missing = new ReplaceMissingValues();
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String globalInfo()
/*  76:    */   {
/*  77:164 */     return "EMDD model builds heavily upon Dietterich's Diverse Density (DD) algorithm.\nIt is a general framework for MI learning of converting the MI problem to a single-instance setting using EM. In this implementation, we use most-likely cause DD model and only use 3 random selected postive bags as initial starting points of EM.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  78:    */   }
/*  79:    */   
/*  80:    */   public TechnicalInformation getTechnicalInformation()
/*  81:    */   {
/*  82:183 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  83:184 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Qi Zhang and Sally A. Goldman");
/*  84:185 */     result.setValue(TechnicalInformation.Field.TITLE, "EM-DD: An Improved Multiple-Instance Learning Technique");
/*  85:    */     
/*  86:187 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Advances in Neural Information Processing Systems 14");
/*  87:    */     
/*  88:189 */     result.setValue(TechnicalInformation.Field.YEAR, "2001");
/*  89:190 */     result.setValue(TechnicalInformation.Field.PAGES, "1073-108");
/*  90:191 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "MIT Press");
/*  91:    */     
/*  92:193 */     return result;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public Enumeration<Option> listOptions()
/*  96:    */   {
/*  97:204 */     Vector<Option> result = new Vector();
/*  98:    */     
/*  99:206 */     result.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither.\n\t(default 1=standardize)", "N", 1, "-N <num>"));
/* 100:    */     
/* 101:    */ 
/* 102:    */ 
/* 103:210 */     result.addAll(Collections.list(super.listOptions()));
/* 104:    */     
/* 105:212 */     return result.elements();
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void setOptions(String[] options)
/* 109:    */     throws Exception
/* 110:    */   {
/* 111:249 */     String tmpStr = Utils.getOption('N', options);
/* 112:250 */     if (tmpStr.length() != 0) {
/* 113:251 */       setFilterType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_FILTER));
/* 114:    */     } else {
/* 115:253 */       setFilterType(new SelectedTag(1, TAGS_FILTER));
/* 116:    */     }
/* 117:256 */     super.setOptions(options);
/* 118:    */     
/* 119:258 */     Utils.checkForRemainingOptions(options);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String[] getOptions()
/* 123:    */   {
/* 124:269 */     Vector<String> result = new Vector();
/* 125:    */     
/* 126:271 */     result.add("-N");
/* 127:272 */     result.add("" + this.m_filterType);
/* 128:    */     
/* 129:274 */     Collections.addAll(result, super.getOptions());
/* 130:    */     
/* 131:276 */     return (String[])result.toArray(new String[result.size()]);
/* 132:    */   }
/* 133:    */   
/* 134:    */   public String filterTypeTipText()
/* 135:    */   {
/* 136:286 */     return "The filter type for transforming the training data.";
/* 137:    */   }
/* 138:    */   
/* 139:    */   public SelectedTag getFilterType()
/* 140:    */   {
/* 141:296 */     return new SelectedTag(this.m_filterType, TAGS_FILTER);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void setFilterType(SelectedTag newType)
/* 145:    */   {
/* 146:307 */     if (newType.getTags() == TAGS_FILTER) {
/* 147:308 */       this.m_filterType = newType.getSelectedTag().getID();
/* 148:    */     }
/* 149:    */   }
/* 150:    */   
/* 151:    */   private class OptEng
/* 152:    */     extends Optimization
/* 153:    */   {
/* 154:    */     private OptEng() {}
/* 155:    */     
/* 156:    */     protected double objectiveFunction(double[] x)
/* 157:    */     {
/* 158:321 */       double nll = 0.0D;
/* 159:322 */       for (int i = 0; i < MIEMDD.this.m_Classes.length; i++)
/* 160:    */       {
/* 161:323 */         double ins = 0.0D;
/* 162:324 */         for (int k = 0; k < MIEMDD.this.m_emData[i].length; k++) {
/* 163:325 */           ins += (MIEMDD.this.m_emData[i][k] - x[(k * 2)]) * (MIEMDD.this.m_emData[i][k] - x[(k * 2)]) * x[(k * 2 + 1)] * x[(k * 2 + 1)];
/* 164:    */         }
/* 165:328 */         ins = Math.exp(-ins);
/* 166:330 */         if (MIEMDD.this.m_Classes[i] == 1)
/* 167:    */         {
/* 168:331 */           if (ins <= m_Zero) {
/* 169:332 */             ins = m_Zero;
/* 170:    */           }
/* 171:334 */           nll -= Math.log(ins);
/* 172:    */         }
/* 173:    */         else
/* 174:    */         {
/* 175:336 */           ins = 1.0D - ins;
/* 176:337 */           if (ins <= m_Zero) {
/* 177:338 */             ins = m_Zero;
/* 178:    */           }
/* 179:340 */           nll -= Math.log(ins);
/* 180:    */         }
/* 181:    */       }
/* 182:343 */       return nll;
/* 183:    */     }
/* 184:    */     
/* 185:    */     protected double[] evaluateGradient(double[] x)
/* 186:    */     {
/* 187:354 */       double[] grad = new double[x.length];
/* 188:355 */       for (int i = 0; i < MIEMDD.this.m_Classes.length; i++)
/* 189:    */       {
/* 190:356 */         double[] numrt = new double[x.length];
/* 191:357 */         double exp = 0.0D;
/* 192:358 */         for (int k = 0; k < MIEMDD.this.m_emData[i].length; k++) {
/* 193:359 */           exp += (MIEMDD.this.m_emData[i][k] - x[(k * 2)]) * (MIEMDD.this.m_emData[i][k] - x[(k * 2)]) * x[(k * 2 + 1)] * x[(k * 2 + 1)];
/* 194:    */         }
/* 195:362 */         exp = Math.exp(-exp);
/* 196:365 */         for (int p = 0; p < MIEMDD.this.m_emData[i].length; p++)
/* 197:    */         {
/* 198:366 */           numrt[(2 * p)] = (2.0D * (x[(2 * p)] - MIEMDD.this.m_emData[i][p]) * x[(p * 2 + 1)] * x[(p * 2 + 1)]);
/* 199:    */           
/* 200:368 */           numrt[(2 * p + 1)] = (2.0D * (x[(2 * p)] - MIEMDD.this.m_emData[i][p]) * (x[(2 * p)] - MIEMDD.this.m_emData[i][p]) * x[(p * 2 + 1)]);
/* 201:    */         }
/* 202:373 */         for (int q = 0; q < MIEMDD.this.m_emData[i].length; q++) {
/* 203:374 */           if (MIEMDD.this.m_Classes[i] == 1)
/* 204:    */           {
/* 205:376 */             grad[(2 * q)] += numrt[(2 * q)];
/* 206:377 */             grad[(2 * q + 1)] += numrt[(2 * q + 1)];
/* 207:    */           }
/* 208:    */           else
/* 209:    */           {
/* 210:379 */             grad[(2 * q)] -= numrt[(2 * q)] * exp / (1.0D - exp);
/* 211:380 */             grad[(2 * q + 1)] -= numrt[(2 * q + 1)] * exp / (1.0D - exp);
/* 212:    */           }
/* 213:    */         }
/* 214:    */       }
/* 215:385 */       return grad;
/* 216:    */     }
/* 217:    */     
/* 218:    */     public String getRevision()
/* 219:    */     {
/* 220:395 */       return RevisionUtils.extract("$Revision: 10369 $");
/* 221:    */     }
/* 222:    */   }
/* 223:    */   
/* 224:    */   public Capabilities getCapabilities()
/* 225:    */   {
/* 226:406 */     Capabilities result = super.getCapabilities();
/* 227:407 */     result.disableAll();
/* 228:    */     
/* 229:    */ 
/* 230:410 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 231:411 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 232:    */     
/* 233:    */ 
/* 234:414 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/* 235:415 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 236:    */     
/* 237:    */ 
/* 238:418 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/* 239:    */     
/* 240:420 */     return result;
/* 241:    */   }
/* 242:    */   
/* 243:    */   public Capabilities getMultiInstanceCapabilities()
/* 244:    */   {
/* 245:432 */     Capabilities result = super.getCapabilities();
/* 246:433 */     result.disableAll();
/* 247:    */     
/* 248:    */ 
/* 249:436 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 250:437 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 251:438 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 252:439 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 253:    */     
/* 254:    */ 
/* 255:442 */     result.disableAllClasses();
/* 256:443 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 257:    */     
/* 258:445 */     return result;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public void buildClassifier(Instances train)
/* 262:    */     throws Exception
/* 263:    */   {
/* 264:458 */     getCapabilities().testWithFail(train);
/* 265:    */     
/* 266:    */ 
/* 267:461 */     train = new Instances(train);
/* 268:462 */     train.deleteWithMissingClass();
/* 269:    */     
/* 270:464 */     this.m_ClassIndex = train.classIndex();
/* 271:465 */     this.m_NumClasses = train.numClasses();
/* 272:    */     
/* 273:467 */     int nR = train.attribute(1).relation().numAttributes();
/* 274:468 */     int nC = train.numInstances();
/* 275:469 */     int[] bagSize = new int[nC];
/* 276:470 */     Instances datasets = new Instances(train.attribute(1).relation(), 0);
/* 277:    */     
/* 278:472 */     this.m_Data = new double[nC][nR][];
/* 279:473 */     this.m_Classes = new int[nC];
/* 280:474 */     this.m_Attributes = datasets.stringFreeStructure();
/* 281:475 */     if (this.m_Debug) {
/* 282:476 */       System.out.println("\n\nExtracting data...");
/* 283:    */     }
/* 284:479 */     for (int h = 0; h < nC; h++)
/* 285:    */     {
/* 286:480 */       Instance current = train.instance(h);
/* 287:481 */       this.m_Classes[h] = ((int)current.classValue());
/* 288:482 */       Instances currInsts = current.relationalValue(1);
/* 289:483 */       for (int i = 0; i < currInsts.numInstances(); i++)
/* 290:    */       {
/* 291:484 */         Instance inst = currInsts.instance(i);
/* 292:485 */         datasets.add(inst);
/* 293:    */       }
/* 294:488 */       int nI = currInsts.numInstances();
/* 295:489 */       bagSize[h] = nI;
/* 296:    */     }
/* 297:493 */     if (this.m_filterType == 1) {
/* 298:494 */       this.m_Filter = new Standardize();
/* 299:495 */     } else if (this.m_filterType == 0) {
/* 300:496 */       this.m_Filter = new Normalize();
/* 301:    */     } else {
/* 302:498 */       this.m_Filter = null;
/* 303:    */     }
/* 304:501 */     if (this.m_Filter != null)
/* 305:    */     {
/* 306:502 */       this.m_Filter.setInputFormat(datasets);
/* 307:503 */       datasets = Filter.useFilter(datasets, this.m_Filter);
/* 308:    */     }
/* 309:506 */     this.m_Missing.setInputFormat(datasets);
/* 310:507 */     datasets = Filter.useFilter(datasets, this.m_Missing);
/* 311:    */     
/* 312:509 */     int instIndex = 0;
/* 313:510 */     int start = 0;
/* 314:511 */     for (int h = 0; h < nC; h++)
/* 315:    */     {
/* 316:512 */       for (int i = 0; i < datasets.numAttributes(); i++)
/* 317:    */       {
/* 318:514 */         this.m_Data[h][i] = new double[bagSize[h]];
/* 319:515 */         instIndex = start;
/* 320:516 */         for (int k = 0; k < bagSize[h]; k++)
/* 321:    */         {
/* 322:517 */           this.m_Data[h][i][k] = datasets.instance(instIndex).value(i);
/* 323:518 */           instIndex++;
/* 324:    */         }
/* 325:    */       }
/* 326:521 */       start = instIndex;
/* 327:    */     }
/* 328:524 */     if (this.m_Debug) {
/* 329:525 */       System.out.println("\n\nIteration History...");
/* 330:    */     }
/* 331:528 */     this.m_emData = new double[nC][nR];
/* 332:529 */     this.m_Par = new double[2 * nR];
/* 333:    */     
/* 334:531 */     double[] x = new double[nR * 2];
/* 335:532 */     double[] tmp = new double[x.length];
/* 336:533 */     double[] pre_x = new double[x.length];
/* 337:534 */     double[] best_hypothesis = new double[x.length];
/* 338:535 */     double[][] b = new double[2][x.length];
/* 339:    */     
/* 340:    */ 
/* 341:538 */     double bestnll = 1.7976931348623157E+308D;
/* 342:539 */     double min_error = 1.7976931348623157E+308D;
/* 343:543 */     for (int t = 0; t < x.length; t++)
/* 344:    */     {
/* 345:544 */       b[0][t] = (0.0D / 0.0D);
/* 346:545 */       b[1][t] = (0.0D / 0.0D);
/* 347:    */     }
/* 348:549 */     Random r = new Random(getSeed());
/* 349:550 */     ArrayList<Integer> index = new ArrayList();
/* 350:    */     int n1;
/* 351:    */     do
/* 352:    */     {
/* 353:553 */       n1 = r.nextInt(nC - 1);
/* 354:554 */     } while (this.m_Classes[n1] == 0);
/* 355:555 */     index.add(new Integer(n1));
/* 356:    */     int n2;
/* 357:    */     do
/* 358:    */     {
/* 359:558 */       n2 = r.nextInt(nC - 1);
/* 360:559 */     } while ((n2 == n1) || (this.m_Classes[n2] == 0));
/* 361:560 */     index.add(new Integer(n2));
/* 362:    */     int n3;
/* 363:    */     do
/* 364:    */     {
/* 365:563 */       n3 = r.nextInt(nC - 1);
/* 366:564 */     } while ((n3 == n1) || (n3 == n2) || (this.m_Classes[n3] == 0));
/* 367:565 */     index.add(new Integer(n3));
/* 368:567 */     for (int s = 0; s < index.size(); s++)
/* 369:    */     {
/* 370:568 */       int exIdx = ((Integer)index.get(s)).intValue();
/* 371:569 */       if (this.m_Debug) {
/* 372:570 */         System.out.println("\nH0 at " + exIdx);
/* 373:    */       }
/* 374:573 */       for (int p = 0; p < this.m_Data[exIdx][0].length; p++)
/* 375:    */       {
/* 376:575 */         for (int q = 0; q < nR; q++)
/* 377:    */         {
/* 378:576 */           x[(2 * q)] = this.m_Data[exIdx][q][p];
/* 379:577 */           x[(2 * q + 1)] = 1.0D;
/* 380:    */         }
/* 381:580 */         double pre_nll = 1.7976931348623157E+308D;
/* 382:581 */         double nll = 1.797693134862316E+307D;
/* 383:582 */         int iterationCount = 0;
/* 384:585 */         while ((nll < pre_nll) && (iterationCount < 10))
/* 385:    */         {
/* 386:586 */           iterationCount++;
/* 387:587 */           pre_nll = nll;
/* 388:589 */           if (this.m_Debug) {
/* 389:590 */             System.out.println("\niteration: " + iterationCount);
/* 390:    */           }
/* 391:594 */           for (int i = 0; i < this.m_Data.length; i++)
/* 392:    */           {
/* 393:596 */             int insIndex = findInstance(i, x);
/* 394:598 */             for (int att = 0; att < this.m_Data[0].length; att++) {
/* 395:599 */               this.m_emData[i][att] = this.m_Data[i][att][insIndex];
/* 396:    */             }
/* 397:    */           }
/* 398:602 */           if (this.m_Debug) {
/* 399:603 */             System.out.println("E-step for new H' finished");
/* 400:    */           }
/* 401:607 */           OptEng opt = new OptEng(null);
/* 402:608 */           tmp = opt.findArgmin(x, b);
/* 403:609 */           while (tmp == null)
/* 404:    */           {
/* 405:610 */             tmp = opt.getVarbValues();
/* 406:611 */             if (this.m_Debug) {
/* 407:612 */               System.out.println("200 iterations finished, not enough!");
/* 408:    */             }
/* 409:614 */             tmp = opt.findArgmin(tmp, b);
/* 410:    */           }
/* 411:616 */           nll = opt.getMinFunction();
/* 412:    */           
/* 413:618 */           pre_x = x;
/* 414:619 */           x = tmp;
/* 415:    */         }
/* 416:634 */         double[] distribution = new double[2];
/* 417:635 */         int error = 0;
/* 418:636 */         if (nll > pre_nll) {
/* 419:637 */           this.m_Par = pre_x;
/* 420:    */         } else {
/* 421:639 */           this.m_Par = x;
/* 422:    */         }
/* 423:642 */         for (int i = 0; i < train.numInstances(); i++)
/* 424:    */         {
/* 425:643 */           distribution = distributionForInstance(train.instance(i));
/* 426:644 */           if ((distribution[1] >= 0.5D) && (this.m_Classes[i] == 0)) {
/* 427:645 */             error++;
/* 428:646 */           } else if ((distribution[1] < 0.5D) && (this.m_Classes[i] == 1)) {
/* 429:647 */             error++;
/* 430:    */           }
/* 431:    */         }
/* 432:650 */         if (error < min_error)
/* 433:    */         {
/* 434:651 */           best_hypothesis = this.m_Par;
/* 435:652 */           min_error = error;
/* 436:653 */           if (nll > pre_nll) {
/* 437:654 */             bestnll = pre_nll;
/* 438:    */           } else {
/* 439:656 */             bestnll = nll;
/* 440:    */           }
/* 441:658 */           if (this.m_Debug) {
/* 442:659 */             System.out.println("error= " + error + "  nll= " + bestnll);
/* 443:    */           }
/* 444:    */         }
/* 445:    */       }
/* 446:663 */       if (this.m_Debug)
/* 447:    */       {
/* 448:664 */         System.out.println(exIdx + ":  -------------<Converged>--------------");
/* 449:665 */         System.out.println("current minimum error= " + min_error + "  nll= " + bestnll);
/* 450:    */       }
/* 451:    */     }
/* 452:669 */     this.m_Par = best_hypothesis;
/* 453:    */   }
/* 454:    */   
/* 455:    */   protected int findInstance(int i, double[] x)
/* 456:    */   {
/* 457:685 */     double min = 1.7976931348623157E+308D;
/* 458:686 */     int insIndex = 0;
/* 459:687 */     int nI = this.m_Data[i][0].length;
/* 460:689 */     for (int j = 0; j < nI; j++)
/* 461:    */     {
/* 462:690 */       double ins = 0.0D;
/* 463:691 */       for (int k = 0; k < this.m_Data[i].length; k++) {
/* 464:692 */         ins += (this.m_Data[i][k][j] - x[(k * 2)]) * (this.m_Data[i][k][j] - x[(k * 2)]) * x[(k * 2 + 1)] * x[(k * 2 + 1)];
/* 465:    */       }
/* 466:699 */       if (ins < min)
/* 467:    */       {
/* 468:700 */         min = ins;
/* 469:701 */         insIndex = j;
/* 470:    */       }
/* 471:    */     }
/* 472:704 */     return insIndex;
/* 473:    */   }
/* 474:    */   
/* 475:    */   public double[] distributionForInstance(Instance exmp)
/* 476:    */     throws Exception
/* 477:    */   {
/* 478:718 */     Instances ins = exmp.relationalValue(1);
/* 479:719 */     if (this.m_Filter != null) {
/* 480:720 */       ins = Filter.useFilter(ins, this.m_Filter);
/* 481:    */     }
/* 482:723 */     ins = Filter.useFilter(ins, this.m_Missing);
/* 483:    */     
/* 484:725 */     int nI = ins.numInstances();int nA = ins.numAttributes();
/* 485:726 */     double[][] dat = new double[nI][nA];
/* 486:727 */     for (int j = 0; j < nI; j++) {
/* 487:728 */       for (int k = 0; k < nA; k++) {
/* 488:729 */         dat[j][k] = ins.instance(j).value(k);
/* 489:    */       }
/* 490:    */     }
/* 491:733 */     double min = 1.7976931348623157E+308D;
/* 492:734 */     double maxProb = -1.0D;
/* 493:735 */     for (int j = 0; j < nI; j++)
/* 494:    */     {
/* 495:736 */       double exp = 0.0D;
/* 496:737 */       for (int k = 0; k < nA; k++) {
/* 497:738 */         exp += (dat[j][k] - this.m_Par[(k * 2)]) * (dat[j][k] - this.m_Par[(k * 2)]) * this.m_Par[(k * 2 + 1)] * this.m_Par[(k * 2 + 1)];
/* 498:    */       }
/* 499:744 */       if (exp < min)
/* 500:    */       {
/* 501:745 */         min = exp;
/* 502:746 */         maxProb = Math.exp(-exp);
/* 503:    */       }
/* 504:    */     }
/* 505:751 */     double[] distribution = new double[2];
/* 506:752 */     distribution[1] = maxProb;
/* 507:753 */     distribution[0] = (1.0D - distribution[1]);
/* 508:    */     
/* 509:755 */     return distribution;
/* 510:    */   }
/* 511:    */   
/* 512:    */   public String toString()
/* 513:    */   {
/* 514:766 */     String result = "MIEMDD";
/* 515:767 */     if (this.m_Par == null) {
/* 516:768 */       return result + ": No model built yet.";
/* 517:    */     }
/* 518:771 */     result = result + "\nCoefficients...\nVariable       Point       Scale\n";
/* 519:772 */     int j = 0;
/* 520:772 */     for (int idx = 0; j < this.m_Par.length / 2; idx++)
/* 521:    */     {
/* 522:773 */       result = result + this.m_Attributes.attribute(idx).name();
/* 523:774 */       result = result + " " + Utils.doubleToString(this.m_Par[(j * 2)], 12, 4);
/* 524:775 */       result = result + " " + Utils.doubleToString(this.m_Par[(j * 2 + 1)], 12, 4) + "\n";j++;
/* 525:    */     }
/* 526:778 */     return result;
/* 527:    */   }
/* 528:    */   
/* 529:    */   public String getRevision()
/* 530:    */   {
/* 531:788 */     return RevisionUtils.extract("$Revision: 10369 $");
/* 532:    */   }
/* 533:    */   
/* 534:    */   public static void main(String[] argv)
/* 535:    */   {
/* 536:798 */     runClassifier(new MIEMDD(), argv);
/* 537:    */   }
/* 538:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.MIEMDD
 * JD-Core Version:    0.7.0.1
 */