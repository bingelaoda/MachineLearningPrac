/*   1:    */ package weka.classifiers.mi;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*  14:    */ import weka.core.Optimization;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.SelectedTag;
/*  19:    */ import weka.core.Tag;
/*  20:    */ import weka.core.Utils;
/*  21:    */ 
/*  22:    */ public class MILR
/*  23:    */   extends AbstractClassifier
/*  24:    */   implements OptionHandler, MultiInstanceCapabilitiesHandler
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = 1996101190172373826L;
/*  27:    */   protected double[] m_Par;
/*  28:    */   protected int m_NumClasses;
/*  29:    */   protected double m_Ridge;
/*  30:    */   protected int[] m_Classes;
/*  31:    */   protected double[][][] m_Data;
/*  32:    */   protected Instances m_Attributes;
/*  33:    */   protected double[] xMean;
/*  34:    */   protected double[] xSD;
/*  35:    */   protected int m_AlgorithmType;
/*  36:    */   public static final int ALGORITHMTYPE_DEFAULT = 0;
/*  37:    */   public static final int ALGORITHMTYPE_ARITHMETIC = 1;
/*  38:    */   public static final int ALGORITHMTYPE_GEOMETRIC = 2;
/*  39:    */   
/*  40:    */   public MILR()
/*  41:    */   {
/*  42: 88 */     this.m_Ridge = 1.0E-006D;
/*  43:    */     
/*  44:    */ 
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53: 99 */     this.xMean = null;this.xSD = null;
/*  54:    */     
/*  55:    */ 
/*  56:102 */     this.m_AlgorithmType = 0;
/*  57:    */   }
/*  58:    */   
/*  59:111 */   public static final Tag[] TAGS_ALGORITHMTYPE = { new Tag(0, "standard MI assumption"), new Tag(1, "collective MI assumption, arithmetic mean for posteriors"), new Tag(2, "collective MI assumption, geometric mean for posteriors") };
/*  60:    */   
/*  61:    */   public String globalInfo()
/*  62:    */   {
/*  63:125 */     return "Uses either standard or collective multi-instance assumption, but within linear regression. For the collective assumption, it offers arithmetic or geometric mean for the posteriors.";
/*  64:    */   }
/*  65:    */   
/*  66:    */   public Enumeration<Option> listOptions()
/*  67:    */   {
/*  68:138 */     Vector<Option> result = new Vector();
/*  69:    */     
/*  70:140 */     result.addElement(new Option("\tSet the ridge in the log-likelihood.", "R", 1, "-R <ridge>"));
/*  71:    */     
/*  72:    */ 
/*  73:143 */     result.addElement(new Option("\tDefines the type of algorithm:\n\t 0. standard MI assumption\n\t 1. collective MI assumption, arithmetic mean for posteriors\n\t 2. collective MI assumption, geometric mean for posteriors", "A", 1, "-A [0|1|2]"));
/*  74:    */     
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79:149 */     result.addAll(Collections.list(super.listOptions()));
/*  80:    */     
/*  81:151 */     return result.elements();
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setOptions(String[] options)
/*  85:    */     throws Exception
/*  86:    */   {
/*  87:164 */     String tmpStr = Utils.getOption('R', options);
/*  88:165 */     if (tmpStr.length() != 0) {
/*  89:166 */       setRidge(Double.parseDouble(tmpStr));
/*  90:    */     } else {
/*  91:168 */       setRidge(1.0E-006D);
/*  92:    */     }
/*  93:171 */     tmpStr = Utils.getOption('A', options);
/*  94:172 */     if (tmpStr.length() != 0) {
/*  95:173 */       setAlgorithmType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_ALGORITHMTYPE));
/*  96:    */     } else {
/*  97:176 */       setAlgorithmType(new SelectedTag(0, TAGS_ALGORITHMTYPE));
/*  98:    */     }
/*  99:180 */     super.setOptions(options);
/* 100:    */     
/* 101:182 */     Utils.checkForRemainingOptions(options);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String[] getOptions()
/* 105:    */   {
/* 106:193 */     Vector<String> result = new Vector();
/* 107:    */     
/* 108:195 */     result.add("-R");
/* 109:196 */     result.add("" + getRidge());
/* 110:    */     
/* 111:198 */     result.add("-A");
/* 112:199 */     result.add("" + this.m_AlgorithmType);
/* 113:    */     
/* 114:201 */     Collections.addAll(result, super.getOptions());
/* 115:    */     
/* 116:203 */     return (String[])result.toArray(new String[result.size()]);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public String ridgeTipText()
/* 120:    */   {
/* 121:213 */     return "The ridge in the log-likelihood.";
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void setRidge(double ridge)
/* 125:    */   {
/* 126:222 */     this.m_Ridge = ridge;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public double getRidge()
/* 130:    */   {
/* 131:231 */     return this.m_Ridge;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public String algorithmTypeTipText()
/* 135:    */   {
/* 136:241 */     return "The mean type for the posteriors.";
/* 137:    */   }
/* 138:    */   
/* 139:    */   public SelectedTag getAlgorithmType()
/* 140:    */   {
/* 141:250 */     return new SelectedTag(this.m_AlgorithmType, TAGS_ALGORITHMTYPE);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void setAlgorithmType(SelectedTag newType)
/* 145:    */   {
/* 146:259 */     if (newType.getTags() == TAGS_ALGORITHMTYPE) {
/* 147:260 */       this.m_AlgorithmType = newType.getSelectedTag().getID();
/* 148:    */     }
/* 149:    */   }
/* 150:    */   
/* 151:    */   private class OptEng
/* 152:    */     extends Optimization
/* 153:    */   {
/* 154:    */     private final int m_Type;
/* 155:    */     
/* 156:    */     public OptEng(int type)
/* 157:    */     {
/* 158:282 */       this.m_Type = type;
/* 159:    */     }
/* 160:    */     
/* 161:    */     protected double objectiveFunction(double[] x)
/* 162:    */     {
/* 163:293 */       double nll = 0.0D;
/* 164:295 */       switch (this.m_Type)
/* 165:    */       {
/* 166:    */       case 0: 
/* 167:297 */         for (int i = 0; i < MILR.this.m_Classes.length; i++)
/* 168:    */         {
/* 169:298 */           int nI = MILR.this.m_Data[i][0].length;
/* 170:299 */           double bag = 0.0D;
/* 171:300 */           double prod = 0.0D;
/* 172:302 */           for (int j = 0; j < nI; j++)
/* 173:    */           {
/* 174:303 */             double exp = 0.0D;
/* 175:304 */             for (int k = MILR.this.m_Data[i].length - 1; k >= 0; k--) {
/* 176:305 */               exp += MILR.this.m_Data[i][k][j] * x[(k + 1)];
/* 177:    */             }
/* 178:307 */             exp += x[0];
/* 179:308 */             exp = Math.exp(exp);
/* 180:310 */             if (MILR.this.m_Classes[i] == 1) {
/* 181:311 */               prod -= Math.log(1.0D + exp);
/* 182:    */             } else {
/* 183:313 */               bag += Math.log(1.0D + exp);
/* 184:    */             }
/* 185:    */           }
/* 186:317 */           if (MILR.this.m_Classes[i] == 1) {
/* 187:318 */             bag = -Math.log(1.0D - Math.exp(prod));
/* 188:    */           }
/* 189:321 */           nll += bag;
/* 190:    */         }
/* 191:323 */         break;
/* 192:    */       case 1: 
/* 193:326 */         for (int i = 0; i < MILR.this.m_Classes.length; i++)
/* 194:    */         {
/* 195:327 */           int nI = MILR.this.m_Data[i][0].length;
/* 196:328 */           double bag = 0.0D;
/* 197:330 */           for (int j = 0; j < nI; j++)
/* 198:    */           {
/* 199:331 */             double exp = 0.0D;
/* 200:332 */             for (int k = MILR.this.m_Data[i].length - 1; k >= 0; k--) {
/* 201:333 */               exp += MILR.this.m_Data[i][k][j] * x[(k + 1)];
/* 202:    */             }
/* 203:335 */             exp += x[0];
/* 204:336 */             exp = Math.exp(exp);
/* 205:338 */             if (MILR.this.m_Classes[i] == 1) {
/* 206:339 */               bag += 1.0D - 1.0D / (1.0D + exp);
/* 207:    */             } else {
/* 208:341 */               bag += 1.0D / (1.0D + exp);
/* 209:    */             }
/* 210:    */           }
/* 211:344 */           bag /= nI;
/* 212:    */           
/* 213:346 */           nll -= Math.log(bag);
/* 214:    */         }
/* 215:348 */         break;
/* 216:    */       case 2: 
/* 217:351 */         for (int i = 0; i < MILR.this.m_Classes.length; i++)
/* 218:    */         {
/* 219:352 */           int nI = MILR.this.m_Data[i][0].length;
/* 220:353 */           double bag = 0.0D;
/* 221:355 */           for (int j = 0; j < nI; j++)
/* 222:    */           {
/* 223:356 */             double exp = 0.0D;
/* 224:357 */             for (int k = MILR.this.m_Data[i].length - 1; k >= 0; k--) {
/* 225:358 */               exp += MILR.this.m_Data[i][k][j] * x[(k + 1)];
/* 226:    */             }
/* 227:360 */             exp += x[0];
/* 228:362 */             if (MILR.this.m_Classes[i] == 1) {
/* 229:363 */               bag -= exp / nI;
/* 230:    */             } else {
/* 231:365 */               bag += exp / nI;
/* 232:    */             }
/* 233:    */           }
/* 234:369 */           nll += Math.log(1.0D + Math.exp(bag));
/* 235:    */         }
/* 236:    */       }
/* 237:375 */       for (int r = 1; r < x.length; r++) {
/* 238:376 */         nll += MILR.this.m_Ridge * x[r] * x[r];
/* 239:    */       }
/* 240:379 */       return nll;
/* 241:    */     }
/* 242:    */     
/* 243:    */     protected double[] evaluateGradient(double[] x)
/* 244:    */     {
/* 245:390 */       double[] grad = new double[x.length];
/* 246:392 */       switch (this.m_Type)
/* 247:    */       {
/* 248:    */       case 0: 
/* 249:394 */         for (int i = 0; i < MILR.this.m_Classes.length; i++)
/* 250:    */         {
/* 251:395 */           int nI = MILR.this.m_Data[i][0].length;
/* 252:    */           
/* 253:397 */           double denom = 0.0D;
/* 254:398 */           double[] bag = new double[grad.length];
/* 255:401 */           for (int j = 0; j < nI; j++)
/* 256:    */           {
/* 257:403 */             double exp = 0.0D;
/* 258:404 */             for (int k = MILR.this.m_Data[i].length - 1; k >= 0; k--) {
/* 259:405 */               exp += MILR.this.m_Data[i][k][j] * x[(k + 1)];
/* 260:    */             }
/* 261:407 */             exp += x[0];
/* 262:408 */             exp = Math.exp(exp) / (1.0D + Math.exp(exp));
/* 263:410 */             if (MILR.this.m_Classes[i] == 1) {
/* 264:413 */               denom -= Math.log(1.0D - exp);
/* 265:    */             }
/* 266:417 */             for (int p = 0; p < x.length; p++)
/* 267:    */             {
/* 268:418 */               double m = 1.0D;
/* 269:419 */               if (p > 0) {
/* 270:420 */                 m = MILR.this.m_Data[i][(p - 1)][j];
/* 271:    */               }
/* 272:422 */               bag[p] += m * exp;
/* 273:    */             }
/* 274:    */           }
/* 275:426 */           denom = Math.exp(denom);
/* 276:429 */           for (int q = 0; q < grad.length; q++) {
/* 277:430 */             if (MILR.this.m_Classes[i] == 1) {
/* 278:431 */               grad[q] -= bag[q] / (denom - 1.0D);
/* 279:    */             } else {
/* 280:433 */               grad[q] += bag[q];
/* 281:    */             }
/* 282:    */           }
/* 283:    */         }
/* 284:437 */         break;
/* 285:    */       case 1: 
/* 286:440 */         for (int i = 0; i < MILR.this.m_Classes.length; i++)
/* 287:    */         {
/* 288:441 */           int nI = MILR.this.m_Data[i][0].length;
/* 289:    */           
/* 290:443 */           double denom = 0.0D;
/* 291:444 */           double[] numrt = new double[x.length];
/* 292:446 */           for (int j = 0; j < nI; j++)
/* 293:    */           {
/* 294:448 */             double exp = 0.0D;
/* 295:449 */             for (int k = MILR.this.m_Data[i].length - 1; k >= 0; k--) {
/* 296:450 */               exp += MILR.this.m_Data[i][k][j] * x[(k + 1)];
/* 297:    */             }
/* 298:452 */             exp += x[0];
/* 299:453 */             exp = Math.exp(exp);
/* 300:454 */             if (MILR.this.m_Classes[i] == 1) {
/* 301:455 */               denom += exp / (1.0D + exp);
/* 302:    */             } else {
/* 303:457 */               denom += 1.0D / (1.0D + exp);
/* 304:    */             }
/* 305:461 */             for (int p = 0; p < x.length; p++)
/* 306:    */             {
/* 307:462 */               double m = 1.0D;
/* 308:463 */               if (p > 0) {
/* 309:464 */                 m = MILR.this.m_Data[i][(p - 1)][j];
/* 310:    */               }
/* 311:466 */               numrt[p] += m * exp / ((1.0D + exp) * (1.0D + exp));
/* 312:    */             }
/* 313:    */           }
/* 314:471 */           for (int q = 0; q < grad.length; q++) {
/* 315:472 */             if (MILR.this.m_Classes[i] == 1) {
/* 316:473 */               grad[q] -= numrt[q] / denom;
/* 317:    */             } else {
/* 318:475 */               grad[q] += numrt[q] / denom;
/* 319:    */             }
/* 320:    */           }
/* 321:    */         }
/* 322:479 */         break;
/* 323:    */       case 2: 
/* 324:482 */         for (int i = 0; i < MILR.this.m_Classes.length; i++)
/* 325:    */         {
/* 326:483 */           int nI = MILR.this.m_Data[i][0].length;
/* 327:484 */           double bag = 0.0D;
/* 328:485 */           double[] sumX = new double[x.length];
/* 329:486 */           for (int j = 0; j < nI; j++)
/* 330:    */           {
/* 331:488 */             double exp = 0.0D;
/* 332:489 */             for (int k = MILR.this.m_Data[i].length - 1; k >= 0; k--) {
/* 333:490 */               exp += MILR.this.m_Data[i][k][j] * x[(k + 1)];
/* 334:    */             }
/* 335:492 */             exp += x[0];
/* 336:494 */             if (MILR.this.m_Classes[i] == 1)
/* 337:    */             {
/* 338:495 */               bag -= exp / nI;
/* 339:496 */               for (int q = 0; q < grad.length; q++)
/* 340:    */               {
/* 341:497 */                 double m = 1.0D;
/* 342:498 */                 if (q > 0) {
/* 343:499 */                   m = MILR.this.m_Data[i][(q - 1)][j];
/* 344:    */                 }
/* 345:501 */                 sumX[q] -= m / nI;
/* 346:    */               }
/* 347:    */             }
/* 348:    */             else
/* 349:    */             {
/* 350:504 */               bag += exp / nI;
/* 351:505 */               for (int q = 0; q < grad.length; q++)
/* 352:    */               {
/* 353:506 */                 double m = 1.0D;
/* 354:507 */                 if (q > 0) {
/* 355:508 */                   m = MILR.this.m_Data[i][(q - 1)][j];
/* 356:    */                 }
/* 357:510 */                 sumX[q] += m / nI;
/* 358:    */               }
/* 359:    */             }
/* 360:    */           }
/* 361:515 */           for (int p = 0; p < x.length; p++) {
/* 362:516 */             grad[p] += Math.exp(bag) * sumX[p] / (1.0D + Math.exp(bag));
/* 363:    */           }
/* 364:    */         }
/* 365:    */       }
/* 366:523 */       for (int r = 1; r < x.length; r++) {
/* 367:524 */         grad[r] += 2.0D * MILR.this.m_Ridge * x[r];
/* 368:    */       }
/* 369:527 */       return grad;
/* 370:    */     }
/* 371:    */     
/* 372:    */     public String getRevision()
/* 373:    */     {
/* 374:537 */       return RevisionUtils.extract("$Revision: 10369 $");
/* 375:    */     }
/* 376:    */   }
/* 377:    */   
/* 378:    */   public Capabilities getCapabilities()
/* 379:    */   {
/* 380:548 */     Capabilities result = super.getCapabilities();
/* 381:549 */     result.disableAll();
/* 382:    */     
/* 383:    */ 
/* 384:552 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 385:553 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 386:    */     
/* 387:    */ 
/* 388:556 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/* 389:557 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 390:    */     
/* 391:    */ 
/* 392:560 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/* 393:    */     
/* 394:562 */     return result;
/* 395:    */   }
/* 396:    */   
/* 397:    */   public Capabilities getMultiInstanceCapabilities()
/* 398:    */   {
/* 399:574 */     Capabilities result = super.getCapabilities();
/* 400:575 */     result.disableAll();
/* 401:    */     
/* 402:    */ 
/* 403:578 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 404:579 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 405:580 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 406:581 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 407:    */     
/* 408:    */ 
/* 409:584 */     result.disableAllClasses();
/* 410:585 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 411:    */     
/* 412:587 */     return result;
/* 413:    */   }
/* 414:    */   
/* 415:    */   public void buildClassifier(Instances train)
/* 416:    */     throws Exception
/* 417:    */   {
/* 418:600 */     getCapabilities().testWithFail(train);
/* 419:    */     
/* 420:    */ 
/* 421:603 */     train = new Instances(train);
/* 422:604 */     train.deleteWithMissingClass();
/* 423:    */     
/* 424:606 */     this.m_NumClasses = train.numClasses();
/* 425:    */     
/* 426:608 */     int nR = train.attribute(1).relation().numAttributes();
/* 427:609 */     int nC = train.numInstances();
/* 428:    */     
/* 429:611 */     this.m_Data = new double[nC][nR][];
/* 430:612 */     this.m_Classes = new int[nC];
/* 431:613 */     this.m_Attributes = train.attribute(1).relation();
/* 432:    */     
/* 433:615 */     this.xMean = new double[nR];
/* 434:616 */     this.xSD = new double[nR];
/* 435:    */     
/* 436:618 */     double sY1 = 0.0D;double sY0 = 0.0D;
/* 437:619 */     int[] missingbags = new int[nR];
/* 438:621 */     if (this.m_Debug) {
/* 439:622 */       System.out.println("Extracting data...");
/* 440:    */     }
/* 441:625 */     for (int h = 0; h < this.m_Data.length; h++)
/* 442:    */     {
/* 443:626 */       Instance current = train.instance(h);
/* 444:627 */       this.m_Classes[h] = ((int)current.classValue());
/* 445:628 */       Instances currInsts = current.relationalValue(1);
/* 446:629 */       int nI = currInsts.numInstances();
/* 447:630 */       for (int i = 0; i < nR; i++)
/* 448:    */       {
/* 449:632 */         this.m_Data[h][i] = new double[nI];
/* 450:633 */         double avg = 0.0D;double std = 0.0D;double num = 0.0D;
/* 451:634 */         for (int k = 0; k < nI; k++) {
/* 452:635 */           if (!currInsts.instance(k).isMissing(i))
/* 453:    */           {
/* 454:636 */             this.m_Data[h][i][k] = currInsts.instance(k).value(i);
/* 455:637 */             avg += this.m_Data[h][i][k];
/* 456:638 */             std += this.m_Data[h][i][k] * this.m_Data[h][i][k];
/* 457:639 */             num += 1.0D;
/* 458:    */           }
/* 459:    */           else
/* 460:    */           {
/* 461:641 */             this.m_Data[h][i][k] = (0.0D / 0.0D);
/* 462:    */           }
/* 463:    */         }
/* 464:645 */         if (num > 0.0D)
/* 465:    */         {
/* 466:646 */           this.xMean[i] += avg / num;
/* 467:647 */           this.xSD[i] += std / num;
/* 468:    */         }
/* 469:    */         else
/* 470:    */         {
/* 471:649 */           missingbags[i] += 1;
/* 472:    */         }
/* 473:    */       }
/* 474:654 */       if (this.m_Classes[h] == 1) {
/* 475:655 */         sY1 += 1.0D;
/* 476:    */       } else {
/* 477:657 */         sY0 += 1.0D;
/* 478:    */       }
/* 479:    */     }
/* 480:661 */     for (int j = 0; j < nR; j++)
/* 481:    */     {
/* 482:662 */       this.xMean[j] /= (nC - missingbags[j]);
/* 483:663 */       this.xSD[j] = Math.sqrt(Math.abs(this.xSD[j] / (nC - missingbags[j] - 1.0D) - this.xMean[j] * this.xMean[j] * (nC - missingbags[j]) / (nC - missingbags[j] - 1.0D)));
/* 484:    */     }
/* 485:668 */     if (this.m_Debug)
/* 486:    */     {
/* 487:670 */       System.out.println("Descriptives...");
/* 488:671 */       System.out.println(sY0 + " bags have class 0 and " + sY1 + " bags have class 1");
/* 489:    */       
/* 490:673 */       System.out.println("\n Variable     Avg       SD    ");
/* 491:674 */       for (int j = 0; j < nR; j++) {
/* 492:675 */         System.out.println(Utils.doubleToString(j, 8, 4) + Utils.doubleToString(this.xMean[j], 10, 4) + Utils.doubleToString(this.xSD[j], 10, 4));
/* 493:    */       }
/* 494:    */     }
/* 495:682 */     for (int i = 0; i < nC; i++) {
/* 496:683 */       for (int j = 0; j < nR; j++) {
/* 497:684 */         for (int k = 0; k < this.m_Data[i][j].length; k++) {
/* 498:685 */           if (this.xSD[j] != 0.0D) {
/* 499:686 */             if (!Double.isNaN(this.m_Data[i][j][k])) {
/* 500:687 */               this.m_Data[i][j][k] = ((this.m_Data[i][j][k] - this.xMean[j]) / this.xSD[j]);
/* 501:    */             } else {
/* 502:689 */               this.m_Data[i][j][k] = 0.0D;
/* 503:    */             }
/* 504:    */           }
/* 505:    */         }
/* 506:    */       }
/* 507:    */     }
/* 508:696 */     if (this.m_Debug) {
/* 509:697 */       System.out.println("\nIteration History...");
/* 510:    */     }
/* 511:700 */     double[] x = new double[nR + 1];
/* 512:701 */     x[0] = Math.log((sY1 + 1.0D) / (sY0 + 1.0D));
/* 513:702 */     double[][] b = new double[2][x.length];
/* 514:703 */     b[0][0] = (0.0D / 0.0D);
/* 515:704 */     b[1][0] = (0.0D / 0.0D);
/* 516:705 */     for (int q = 1; q < x.length; q++)
/* 517:    */     {
/* 518:706 */       x[q] = 0.0D;
/* 519:707 */       b[0][q] = (0.0D / 0.0D);
/* 520:708 */       b[1][q] = (0.0D / 0.0D);
/* 521:    */     }
/* 522:711 */     OptEng opt = new OptEng(this.m_AlgorithmType);
/* 523:712 */     opt.setDebug(this.m_Debug);
/* 524:713 */     this.m_Par = opt.findArgmin(x, b);
/* 525:714 */     while (this.m_Par == null)
/* 526:    */     {
/* 527:715 */       this.m_Par = opt.getVarbValues();
/* 528:716 */       if (this.m_Debug) {
/* 529:717 */         System.out.println("200 iterations finished, not enough!");
/* 530:    */       }
/* 531:719 */       this.m_Par = opt.findArgmin(this.m_Par, b);
/* 532:    */     }
/* 533:721 */     if (this.m_Debug) {
/* 534:722 */       System.out.println(" -------------<Converged>--------------");
/* 535:    */     }
/* 536:726 */     if (this.m_AlgorithmType == 1)
/* 537:    */     {
/* 538:727 */       double[] fs = new double[nR];
/* 539:728 */       for (int k = 1; k < nR + 1; k++) {
/* 540:729 */         fs[(k - 1)] = Math.abs(this.m_Par[k]);
/* 541:    */       }
/* 542:731 */       int[] idx = Utils.sort(fs);
/* 543:732 */       double max = fs[idx[(idx.length - 1)]];
/* 544:733 */       for (int k = idx.length - 1; k >= 0; k--) {
/* 545:734 */         System.out.println(this.m_Attributes.attribute(idx[k]).name() + "\t" + fs[idx[k]] * 100.0D / max);
/* 546:    */       }
/* 547:    */     }
/* 548:740 */     for (int j = 1; j < nR + 1; j++) {
/* 549:741 */       if (this.xSD[(j - 1)] != 0.0D)
/* 550:    */       {
/* 551:742 */         this.m_Par[j] /= this.xSD[(j - 1)];
/* 552:743 */         this.m_Par[0] -= this.m_Par[j] * this.xMean[(j - 1)];
/* 553:    */       }
/* 554:    */     }
/* 555:    */   }
/* 556:    */   
/* 557:    */   public double[] distributionForInstance(Instance exmp)
/* 558:    */     throws Exception
/* 559:    */   {
/* 560:759 */     Instances ins = exmp.relationalValue(1);
/* 561:760 */     int nI = ins.numInstances();int nA = ins.numAttributes();
/* 562:761 */     double[][] dat = new double[nI][nA + 1];
/* 563:762 */     for (int j = 0; j < nI; j++)
/* 564:    */     {
/* 565:763 */       dat[j][0] = 1.0D;
/* 566:764 */       int idx = 1;
/* 567:765 */       for (int k = 0; k < nA; k++)
/* 568:    */       {
/* 569:766 */         if (!ins.instance(j).isMissing(k)) {
/* 570:767 */           dat[j][idx] = ins.instance(j).value(k);
/* 571:    */         } else {
/* 572:769 */           dat[j][idx] = this.xMean[(idx - 1)];
/* 573:    */         }
/* 574:771 */         idx++;
/* 575:    */       }
/* 576:    */     }
/* 577:776 */     double[] distribution = new double[2];
/* 578:777 */     switch (this.m_AlgorithmType)
/* 579:    */     {
/* 580:    */     case 0: 
/* 581:779 */       distribution[0] = 0.0D;
/* 582:781 */       for (int i = 0; i < nI; i++)
/* 583:    */       {
/* 584:782 */         double exp = 0.0D;
/* 585:783 */         for (int r = 0; r < this.m_Par.length; r++) {
/* 586:784 */           exp += this.m_Par[r] * dat[i][r];
/* 587:    */         }
/* 588:786 */         exp = Math.exp(exp);
/* 589:    */         
/* 590:    */ 
/* 591:789 */         distribution[0] -= Math.log(1.0D + exp);
/* 592:    */       }
/* 593:793 */       distribution[0] = Math.exp(distribution[0]);
/* 594:    */       
/* 595:795 */       distribution[1] = (1.0D - distribution[0]);
/* 596:796 */       break;
/* 597:    */     case 1: 
/* 598:799 */       distribution[0] = 0.0D;
/* 599:801 */       for (int i = 0; i < nI; i++)
/* 600:    */       {
/* 601:802 */         double exp = 0.0D;
/* 602:803 */         for (int r = 0; r < this.m_Par.length; r++) {
/* 603:804 */           exp += this.m_Par[r] * dat[i][r];
/* 604:    */         }
/* 605:806 */         exp = Math.exp(exp);
/* 606:    */         
/* 607:    */ 
/* 608:809 */         distribution[0] += 1.0D / (1.0D + exp);
/* 609:    */       }
/* 610:813 */       distribution[0] /= nI;
/* 611:    */       
/* 612:815 */       distribution[1] = (1.0D - distribution[0]);
/* 613:816 */       break;
/* 614:    */     case 2: 
/* 615:819 */       for (int i = 0; i < nI; i++)
/* 616:    */       {
/* 617:820 */         double exp = 0.0D;
/* 618:821 */         for (int r = 0; r < this.m_Par.length; r++) {
/* 619:822 */           exp += this.m_Par[r] * dat[i][r];
/* 620:    */         }
/* 621:824 */         distribution[1] += exp / nI;
/* 622:    */       }
/* 623:828 */       distribution[1] = (1.0D / (1.0D + Math.exp(-distribution[1])));
/* 624:    */       
/* 625:830 */       distribution[0] = (1.0D - distribution[1]);
/* 626:    */     }
/* 627:834 */     return distribution;
/* 628:    */   }
/* 629:    */   
/* 630:    */   public String toString()
/* 631:    */   {
/* 632:845 */     String result = "Modified Logistic Regression";
/* 633:846 */     if (this.m_Par == null) {
/* 634:847 */       return result + ": No model built yet.";
/* 635:    */     }
/* 636:850 */     result = result + "\nMean type: " + getAlgorithmType().getSelectedTag().getReadable() + "\n";
/* 637:    */     
/* 638:852 */     result = result + "\nCoefficients...\nVariable      Coeff.\n";
/* 639:853 */     int j = 1;
/* 640:853 */     for (int idx = 0; j < this.m_Par.length; idx++)
/* 641:    */     {
/* 642:854 */       result = result + this.m_Attributes.attribute(idx).name();
/* 643:855 */       result = result + " " + Utils.doubleToString(this.m_Par[j], 12, 4);
/* 644:856 */       result = result + "\n";j++;
/* 645:    */     }
/* 646:859 */     result = result + "Intercept:";
/* 647:860 */     result = result + " " + Utils.doubleToString(this.m_Par[0], 10, 4);
/* 648:861 */     result = result + "\n";
/* 649:    */     
/* 650:863 */     result = result + "\nOdds Ratios...\nVariable         O.R.\n";
/* 651:864 */     int j = 1;
/* 652:864 */     for (int idx = 0; j < this.m_Par.length; idx++)
/* 653:    */     {
/* 654:865 */       result = result + " " + this.m_Attributes.attribute(idx).name();
/* 655:866 */       double ORc = Math.exp(this.m_Par[j]);
/* 656:867 */       result = result + " " + (ORc > 10000000000.0D ? "" + ORc : Utils.doubleToString(ORc, 12, 4));j++;
/* 657:    */     }
/* 658:870 */     result = result + "\n";
/* 659:871 */     return result;
/* 660:    */   }
/* 661:    */   
/* 662:    */   public String getRevision()
/* 663:    */   {
/* 664:881 */     return RevisionUtils.extract("$Revision: 10369 $");
/* 665:    */   }
/* 666:    */   
/* 667:    */   public static void main(String[] argv)
/* 668:    */   {
/* 669:891 */     runClassifier(new MILR(), argv);
/* 670:    */   }
/* 671:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.MILR
 * JD-Core Version:    0.7.0.1
 */