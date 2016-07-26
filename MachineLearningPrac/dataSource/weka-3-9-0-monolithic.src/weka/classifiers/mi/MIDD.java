/*   1:    */ package weka.classifiers.mi;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.AbstractClassifier;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*  15:    */ import weka.core.Optimization;
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
/*  26:    */ import weka.filters.Filter;
/*  27:    */ import weka.filters.unsupervised.attribute.Normalize;
/*  28:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  29:    */ import weka.filters.unsupervised.attribute.Standardize;
/*  30:    */ 
/*  31:    */ public class MIDD
/*  32:    */   extends AbstractClassifier
/*  33:    */   implements OptionHandler, MultiInstanceCapabilitiesHandler, TechnicalInformationHandler
/*  34:    */ {
/*  35:    */   static final long serialVersionUID = 4263507733600536168L;
/*  36:    */   protected int m_ClassIndex;
/*  37:    */   protected double[] m_Par;
/*  38:    */   protected int m_NumClasses;
/*  39:    */   protected int[] m_Classes;
/*  40:    */   protected double[][][] m_Data;
/*  41:    */   protected Instances m_Attributes;
/*  42:    */   protected Filter m_Filter;
/*  43:    */   protected int m_filterType;
/*  44:    */   public static final int FILTER_NORMALIZE = 0;
/*  45:    */   public static final int FILTER_STANDARDIZE = 1;
/*  46:    */   public static final int FILTER_NONE = 2;
/*  47:139 */   public static final Tag[] TAGS_FILTER = { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
/*  48:    */   protected ReplaceMissingValues m_Missing;
/*  49:    */   
/*  50:    */   public MIDD()
/*  51:    */   {
/*  52:127 */     this.m_Filter = null;
/*  53:    */     
/*  54:    */ 
/*  55:130 */     this.m_filterType = 1;
/*  56:    */     
/*  57:    */ 
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
/*  70:145 */     this.m_Missing = new ReplaceMissingValues();
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String globalInfo()
/*  74:    */   {
/*  75:154 */     return "Re-implement the Diverse Density algorithm, changes the testing procedure.\n\n" + getTechnicalInformation().toString();
/*  76:    */   }
/*  77:    */   
/*  78:    */   public TechnicalInformation getTechnicalInformation()
/*  79:    */   {
/*  80:170 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.PHDTHESIS);
/*  81:171 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Oded Maron");
/*  82:172 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  83:173 */     result.setValue(TechnicalInformation.Field.TITLE, "Learning from ambiguity");
/*  84:174 */     result.setValue(TechnicalInformation.Field.SCHOOL, "Massachusetts Institute of Technology");
/*  85:    */     
/*  86:176 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.ARTICLE);
/*  87:177 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "O. Maron and T. Lozano-Perez");
/*  88:178 */     additional.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  89:179 */     additional.setValue(TechnicalInformation.Field.TITLE, "A Framework for Multiple Instance Learning");
/*  90:    */     
/*  91:181 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Neural Information Processing Systems");
/*  92:182 */     additional.setValue(TechnicalInformation.Field.VOLUME, "10");
/*  93:    */     
/*  94:184 */     return result;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public Enumeration<Option> listOptions()
/*  98:    */   {
/*  99:194 */     Vector<Option> result = new Vector(2);
/* 100:    */     
/* 101:196 */     result.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither.\n\t(default 1=standardize)", "N", 1, "-N <num>"));
/* 102:    */     
/* 103:    */ 
/* 104:    */ 
/* 105:200 */     result.addAll(Collections.list(super.listOptions()));
/* 106:    */     
/* 107:202 */     return result.elements();
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setOptions(String[] options)
/* 111:    */     throws Exception
/* 112:    */   {
/* 113:231 */     String nString = Utils.getOption('N', options);
/* 114:232 */     if (nString.length() != 0) {
/* 115:233 */       setFilterType(new SelectedTag(Integer.parseInt(nString), TAGS_FILTER));
/* 116:    */     } else {
/* 117:235 */       setFilterType(new SelectedTag(1, TAGS_FILTER));
/* 118:    */     }
/* 119:238 */     super.setOptions(options);
/* 120:    */     
/* 121:240 */     Utils.checkForRemainingOptions(options);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public String[] getOptions()
/* 125:    */   {
/* 126:251 */     Vector<String> result = new Vector();
/* 127:    */     
/* 128:253 */     result.add("-N");
/* 129:254 */     result.add("" + this.m_filterType);
/* 130:    */     
/* 131:256 */     Collections.addAll(result, super.getOptions());
/* 132:    */     
/* 133:258 */     return (String[])result.toArray(new String[result.size()]);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public String filterTypeTipText()
/* 137:    */   {
/* 138:268 */     return "The filter type for transforming the training data.";
/* 139:    */   }
/* 140:    */   
/* 141:    */   public SelectedTag getFilterType()
/* 142:    */   {
/* 143:278 */     return new SelectedTag(this.m_filterType, TAGS_FILTER);
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void setFilterType(SelectedTag newType)
/* 147:    */   {
/* 148:289 */     if (newType.getTags() == TAGS_FILTER) {
/* 149:290 */       this.m_filterType = newType.getSelectedTag().getID();
/* 150:    */     }
/* 151:    */   }
/* 152:    */   
/* 153:    */   private class OptEng
/* 154:    */     extends Optimization
/* 155:    */   {
/* 156:    */     private OptEng() {}
/* 157:    */     
/* 158:    */     protected double objectiveFunction(double[] x)
/* 159:    */     {
/* 160:304 */       double nll = 0.0D;
/* 161:305 */       for (int i = 0; i < MIDD.this.m_Classes.length; i++)
/* 162:    */       {
/* 163:306 */         int nI = MIDD.this.m_Data[i][0].length;
/* 164:307 */         double bag = 0.0D;
/* 165:309 */         for (int j = 0; j < nI; j++)
/* 166:    */         {
/* 167:310 */           double ins = 0.0D;
/* 168:311 */           for (int k = 0; k < MIDD.this.m_Data[i].length; k++) {
/* 169:312 */             ins += (MIDD.this.m_Data[i][k][j] - x[(k * 2)]) * (MIDD.this.m_Data[i][k][j] - x[(k * 2)]) * x[(k * 2 + 1)] * x[(k * 2 + 1)];
/* 170:    */           }
/* 171:315 */           ins = Math.exp(-ins);
/* 172:316 */           ins = 1.0D - ins;
/* 173:318 */           if (MIDD.this.m_Classes[i] == 1)
/* 174:    */           {
/* 175:319 */             bag += Math.log(ins);
/* 176:    */           }
/* 177:    */           else
/* 178:    */           {
/* 179:321 */             if (ins <= m_Zero) {
/* 180:322 */               ins = m_Zero;
/* 181:    */             }
/* 182:324 */             nll -= Math.log(ins);
/* 183:    */           }
/* 184:    */         }
/* 185:328 */         if (MIDD.this.m_Classes[i] == 1)
/* 186:    */         {
/* 187:329 */           bag = 1.0D - Math.exp(bag);
/* 188:330 */           if (bag <= m_Zero) {
/* 189:331 */             bag = m_Zero;
/* 190:    */           }
/* 191:333 */           nll -= Math.log(bag);
/* 192:    */         }
/* 193:    */       }
/* 194:336 */       return nll;
/* 195:    */     }
/* 196:    */     
/* 197:    */     protected double[] evaluateGradient(double[] x)
/* 198:    */     {
/* 199:347 */       double[] grad = new double[x.length];
/* 200:348 */       for (int i = 0; i < MIDD.this.m_Classes.length; i++)
/* 201:    */       {
/* 202:349 */         int nI = MIDD.this.m_Data[i][0].length;
/* 203:    */         
/* 204:351 */         double denom = 0.0D;
/* 205:352 */         double[] numrt = new double[x.length];
/* 206:354 */         for (int j = 0; j < nI; j++)
/* 207:    */         {
/* 208:355 */           double exp = 0.0D;
/* 209:356 */           for (int k = 0; k < MIDD.this.m_Data[i].length; k++) {
/* 210:357 */             exp += (MIDD.this.m_Data[i][k][j] - x[(k * 2)]) * (MIDD.this.m_Data[i][k][j] - x[(k * 2)]) * x[(k * 2 + 1)] * x[(k * 2 + 1)];
/* 211:    */           }
/* 212:360 */           exp = Math.exp(-exp);
/* 213:361 */           exp = 1.0D - exp;
/* 214:362 */           if (MIDD.this.m_Classes[i] == 1) {
/* 215:363 */             denom += Math.log(exp);
/* 216:    */           }
/* 217:366 */           if (exp <= m_Zero) {
/* 218:367 */             exp = m_Zero;
/* 219:    */           }
/* 220:370 */           for (int p = 0; p < MIDD.this.m_Data[i].length; p++)
/* 221:    */           {
/* 222:371 */             numrt[(2 * p)] += (1.0D - exp) * 2.0D * (x[(2 * p)] - MIDD.this.m_Data[i][p][j]) * x[(p * 2 + 1)] * x[(p * 2 + 1)] / exp;
/* 223:    */             
/* 224:373 */             numrt[(2 * p + 1)] += 2.0D * (1.0D - exp) * (x[(2 * p)] - MIDD.this.m_Data[i][p][j]) * (x[(2 * p)] - MIDD.this.m_Data[i][p][j]) * x[(p * 2 + 1)] / exp;
/* 225:    */           }
/* 226:    */         }
/* 227:380 */         denom = 1.0D - Math.exp(denom);
/* 228:381 */         if (denom <= m_Zero) {
/* 229:382 */           denom = m_Zero;
/* 230:    */         }
/* 231:384 */         for (int q = 0; q < MIDD.this.m_Data[i].length; q++) {
/* 232:385 */           if (MIDD.this.m_Classes[i] == 1)
/* 233:    */           {
/* 234:386 */             grad[(2 * q)] += numrt[(2 * q)] * (1.0D - denom) / denom;
/* 235:387 */             grad[(2 * q + 1)] += numrt[(2 * q + 1)] * (1.0D - denom) / denom;
/* 236:    */           }
/* 237:    */           else
/* 238:    */           {
/* 239:389 */             grad[(2 * q)] -= numrt[(2 * q)];
/* 240:390 */             grad[(2 * q + 1)] -= numrt[(2 * q + 1)];
/* 241:    */           }
/* 242:    */         }
/* 243:    */       }
/* 244:395 */       return grad;
/* 245:    */     }
/* 246:    */     
/* 247:    */     public String getRevision()
/* 248:    */     {
/* 249:405 */       return RevisionUtils.extract("$Revision: 10369 $");
/* 250:    */     }
/* 251:    */   }
/* 252:    */   
/* 253:    */   public Capabilities getCapabilities()
/* 254:    */   {
/* 255:416 */     Capabilities result = super.getCapabilities();
/* 256:417 */     result.disableAll();
/* 257:    */     
/* 258:    */ 
/* 259:420 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 260:421 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 261:    */     
/* 262:    */ 
/* 263:424 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/* 264:425 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 265:    */     
/* 266:    */ 
/* 267:428 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/* 268:    */     
/* 269:430 */     return result;
/* 270:    */   }
/* 271:    */   
/* 272:    */   public Capabilities getMultiInstanceCapabilities()
/* 273:    */   {
/* 274:442 */     Capabilities result = super.getCapabilities();
/* 275:443 */     result.disableAll();
/* 276:    */     
/* 277:    */ 
/* 278:446 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 279:447 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 280:448 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 281:449 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 282:    */     
/* 283:    */ 
/* 284:452 */     result.disableAllClasses();
/* 285:453 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 286:    */     
/* 287:455 */     return result;
/* 288:    */   }
/* 289:    */   
/* 290:    */   public void buildClassifier(Instances train)
/* 291:    */     throws Exception
/* 292:    */   {
/* 293:468 */     getCapabilities().testWithFail(train);
/* 294:    */     
/* 295:    */ 
/* 296:471 */     train = new Instances(train);
/* 297:472 */     train.deleteWithMissingClass();
/* 298:    */     
/* 299:474 */     this.m_ClassIndex = train.classIndex();
/* 300:475 */     this.m_NumClasses = train.numClasses();
/* 301:    */     
/* 302:477 */     int nR = train.attribute(1).relation().numAttributes();
/* 303:478 */     int nC = train.numInstances();
/* 304:479 */     ArrayList<Integer> maxSzIdx = new ArrayList();
/* 305:480 */     int maxSz = 0;
/* 306:481 */     int[] bagSize = new int[nC];
/* 307:482 */     Instances datasets = new Instances(train.attribute(1).relation(), 0);
/* 308:    */     
/* 309:484 */     this.m_Data = new double[nC][nR][];
/* 310:485 */     this.m_Classes = new int[nC];
/* 311:486 */     this.m_Attributes = datasets.stringFreeStructure();
/* 312:487 */     if (this.m_Debug) {
/* 313:488 */       System.out.println("Extracting data...");
/* 314:    */     }
/* 315:491 */     for (int h = 0; h < nC; h++)
/* 316:    */     {
/* 317:492 */       Instance current = train.instance(h);
/* 318:493 */       this.m_Classes[h] = ((int)current.classValue());
/* 319:494 */       Instances currInsts = current.relationalValue(1);
/* 320:495 */       for (int i = 0; i < currInsts.numInstances(); i++)
/* 321:    */       {
/* 322:496 */         Instance inst = currInsts.instance(i);
/* 323:497 */         datasets.add(inst);
/* 324:    */       }
/* 325:500 */       int nI = currInsts.numInstances();
/* 326:501 */       bagSize[h] = nI;
/* 327:502 */       if (this.m_Classes[h] == 1) {
/* 328:503 */         if (nI > maxSz)
/* 329:    */         {
/* 330:504 */           maxSz = nI;
/* 331:505 */           maxSzIdx = new ArrayList(1);
/* 332:506 */           maxSzIdx.add(new Integer(h));
/* 333:    */         }
/* 334:507 */         else if (nI == maxSz)
/* 335:    */         {
/* 336:508 */           maxSzIdx.add(new Integer(h));
/* 337:    */         }
/* 338:    */       }
/* 339:    */     }
/* 340:515 */     if (this.m_filterType == 1) {
/* 341:516 */       this.m_Filter = new Standardize();
/* 342:517 */     } else if (this.m_filterType == 0) {
/* 343:518 */       this.m_Filter = new Normalize();
/* 344:    */     } else {
/* 345:520 */       this.m_Filter = null;
/* 346:    */     }
/* 347:523 */     if (this.m_Filter != null)
/* 348:    */     {
/* 349:524 */       this.m_Filter.setInputFormat(datasets);
/* 350:525 */       datasets = Filter.useFilter(datasets, this.m_Filter);
/* 351:    */     }
/* 352:528 */     this.m_Missing.setInputFormat(datasets);
/* 353:529 */     datasets = Filter.useFilter(datasets, this.m_Missing);
/* 354:    */     
/* 355:531 */     int instIndex = 0;
/* 356:532 */     int start = 0;
/* 357:533 */     for (int h = 0; h < nC; h++)
/* 358:    */     {
/* 359:534 */       for (int i = 0; i < datasets.numAttributes(); i++)
/* 360:    */       {
/* 361:536 */         this.m_Data[h][i] = new double[bagSize[h]];
/* 362:537 */         instIndex = start;
/* 363:538 */         for (int k = 0; k < bagSize[h]; k++)
/* 364:    */         {
/* 365:539 */           this.m_Data[h][i][k] = datasets.instance(instIndex).value(i);
/* 366:540 */           instIndex++;
/* 367:    */         }
/* 368:    */       }
/* 369:543 */       start = instIndex;
/* 370:    */     }
/* 371:546 */     if (this.m_Debug) {
/* 372:547 */       System.out.println("\nIteration History...");
/* 373:    */     }
/* 374:550 */     double[] x = new double[nR * 2];double[] tmp = new double[x.length];
/* 375:551 */     double[][] b = new double[2][x.length];
/* 376:    */     
/* 377:    */ 
/* 378:554 */     double bestnll = 1.7976931348623157E+308D;
/* 379:555 */     for (int t = 0; t < x.length; t++)
/* 380:    */     {
/* 381:556 */       b[0][t] = (0.0D / 0.0D);
/* 382:557 */       b[1][t] = (0.0D / 0.0D);
/* 383:    */     }
/* 384:561 */     for (int s = 0; s < maxSzIdx.size(); s++)
/* 385:    */     {
/* 386:562 */       int exIdx = ((Integer)maxSzIdx.get(s)).intValue();
/* 387:563 */       for (int p = 0; p < this.m_Data[exIdx][0].length; p++)
/* 388:    */       {
/* 389:564 */         for (int q = 0; q < nR; q++)
/* 390:    */         {
/* 391:565 */           x[(2 * q)] = this.m_Data[exIdx][q][p];
/* 392:566 */           x[(2 * q + 1)] = 1.0D;
/* 393:    */         }
/* 394:569 */         OptEng opt = new OptEng(null);
/* 395:    */         
/* 396:571 */         tmp = opt.findArgmin(x, b);
/* 397:572 */         while (tmp == null)
/* 398:    */         {
/* 399:573 */           tmp = opt.getVarbValues();
/* 400:574 */           if (this.m_Debug) {
/* 401:575 */             System.out.println("200 iterations finished, not enough!");
/* 402:    */           }
/* 403:577 */           tmp = opt.findArgmin(tmp, b);
/* 404:    */         }
/* 405:579 */         double nll = opt.getMinFunction();
/* 406:581 */         if (nll < bestnll)
/* 407:    */         {
/* 408:582 */           bestnll = nll;
/* 409:583 */           this.m_Par = tmp;
/* 410:584 */           tmp = new double[x.length];
/* 411:585 */           if (this.m_Debug) {
/* 412:586 */             System.out.println("!!!!!!!!!!!!!!!!Smaller NLL found: " + nll);
/* 413:    */           }
/* 414:    */         }
/* 415:589 */         if (this.m_Debug) {
/* 416:590 */           System.out.println(exIdx + ":  -------------<Converged>--------------");
/* 417:    */         }
/* 418:    */       }
/* 419:    */     }
/* 420:    */   }
/* 421:    */   
/* 422:    */   public double[] distributionForInstance(Instance exmp)
/* 423:    */     throws Exception
/* 424:    */   {
/* 425:608 */     Instances ins = exmp.relationalValue(1);
/* 426:609 */     if (this.m_Filter != null) {
/* 427:610 */       ins = Filter.useFilter(ins, this.m_Filter);
/* 428:    */     }
/* 429:613 */     ins = Filter.useFilter(ins, this.m_Missing);
/* 430:    */     
/* 431:615 */     int nI = ins.numInstances();int nA = ins.numAttributes();
/* 432:616 */     double[][] dat = new double[nI][nA];
/* 433:617 */     for (int j = 0; j < nI; j++) {
/* 434:618 */       for (int k = 0; k < nA; k++) {
/* 435:619 */         dat[j][k] = ins.instance(j).value(k);
/* 436:    */       }
/* 437:    */     }
/* 438:624 */     double[] distribution = new double[2];
/* 439:625 */     distribution[0] = 0.0D;
/* 440:627 */     for (int i = 0; i < nI; i++)
/* 441:    */     {
/* 442:628 */       double exp = 0.0D;
/* 443:629 */       for (int r = 0; r < nA; r++) {
/* 444:630 */         exp += (this.m_Par[(r * 2)] - dat[i][r]) * (this.m_Par[(r * 2)] - dat[i][r]) * this.m_Par[(r * 2 + 1)] * this.m_Par[(r * 2 + 1)];
/* 445:    */       }
/* 446:633 */       exp = Math.exp(-exp);
/* 447:    */       
/* 448:    */ 
/* 449:636 */       distribution[0] += Math.log(1.0D - exp);
/* 450:    */     }
/* 451:639 */     distribution[0] = Math.exp(distribution[0]);
/* 452:640 */     distribution[1] = (1.0D - distribution[0]);
/* 453:    */     
/* 454:642 */     return distribution;
/* 455:    */   }
/* 456:    */   
/* 457:    */   public String toString()
/* 458:    */   {
/* 459:655 */     String result = "Diverse Density";
/* 460:656 */     if (this.m_Par == null) {
/* 461:657 */       return result + ": No model built yet.";
/* 462:    */     }
/* 463:660 */     result = result + "\nCoefficients...\nVariable       Point       Scale\n";
/* 464:661 */     int j = 0;
/* 465:661 */     for (int idx = 0; j < this.m_Par.length / 2; idx++)
/* 466:    */     {
/* 467:662 */       result = result + this.m_Attributes.attribute(idx).name();
/* 468:663 */       result = result + " " + Utils.doubleToString(this.m_Par[(j * 2)], 12, 4);
/* 469:664 */       result = result + " " + Utils.doubleToString(this.m_Par[(j * 2 + 1)], 12, 4) + "\n";j++;
/* 470:    */     }
/* 471:667 */     return result;
/* 472:    */   }
/* 473:    */   
/* 474:    */   public String getRevision()
/* 475:    */   {
/* 476:677 */     return RevisionUtils.extract("$Revision: 10369 $");
/* 477:    */   }
/* 478:    */   
/* 479:    */   public static void main(String[] argv)
/* 480:    */   {
/* 481:687 */     runClassifier(new MIDD(), argv);
/* 482:    */   }
/* 483:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.MIDD
 * JD-Core Version:    0.7.0.1
 */