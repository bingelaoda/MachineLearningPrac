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
/*  31:    */ public class MDD
/*  32:    */   extends AbstractClassifier
/*  33:    */   implements OptionHandler, MultiInstanceCapabilitiesHandler, TechnicalInformationHandler
/*  34:    */ {
/*  35:    */   static final long serialVersionUID = -7273119490545290581L;
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
/*  47:141 */   public static final Tag[] TAGS_FILTER = { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
/*  48:    */   protected ReplaceMissingValues m_Missing;
/*  49:    */   
/*  50:    */   public MDD()
/*  51:    */   {
/*  52:129 */     this.m_Filter = null;
/*  53:    */     
/*  54:    */ 
/*  55:132 */     this.m_filterType = 1;
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
/*  70:147 */     this.m_Missing = new ReplaceMissingValues();
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String globalInfo()
/*  74:    */   {
/*  75:156 */     return "Modified Diverse Density algorithm, with collective assumption.\n\nMore information about DD:\n\n" + getTechnicalInformation().toString();
/*  76:    */   }
/*  77:    */   
/*  78:    */   public TechnicalInformation getTechnicalInformation()
/*  79:    */   {
/*  80:172 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.PHDTHESIS);
/*  81:173 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Oded Maron");
/*  82:174 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  83:175 */     result.setValue(TechnicalInformation.Field.TITLE, "Learning from ambiguity");
/*  84:176 */     result.setValue(TechnicalInformation.Field.SCHOOL, "Massachusetts Institute of Technology");
/*  85:    */     
/*  86:178 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.ARTICLE);
/*  87:179 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "O. Maron and T. Lozano-Perez");
/*  88:180 */     additional.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  89:181 */     additional.setValue(TechnicalInformation.Field.TITLE, "A Framework for Multiple Instance Learning");
/*  90:    */     
/*  91:183 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Neural Information Processing Systems");
/*  92:184 */     additional.setValue(TechnicalInformation.Field.VOLUME, "10");
/*  93:    */     
/*  94:186 */     return result;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public Enumeration<Option> listOptions()
/*  98:    */   {
/*  99:197 */     Vector<Option> result = new Vector();
/* 100:    */     
/* 101:199 */     result.addElement(new Option("\tTurn on debugging output.", "D", 0, "-D"));
/* 102:    */     
/* 103:201 */     result.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither.\n\t(default 1=standardize)", "N", 1, "-N <num>"));
/* 104:    */     
/* 105:    */ 
/* 106:    */ 
/* 107:205 */     result.addAll(Collections.list(super.listOptions()));
/* 108:    */     
/* 109:207 */     return result.elements();
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void setOptions(String[] options)
/* 113:    */     throws Exception
/* 114:    */   {
/* 115:218 */     setDebug(Utils.getFlag('D', options));
/* 116:    */     
/* 117:220 */     String nString = Utils.getOption('N', options);
/* 118:221 */     if (nString.length() != 0) {
/* 119:222 */       setFilterType(new SelectedTag(Integer.parseInt(nString), TAGS_FILTER));
/* 120:    */     } else {
/* 121:224 */       setFilterType(new SelectedTag(1, TAGS_FILTER));
/* 122:    */     }
/* 123:227 */     super.setOptions(options);
/* 124:    */     
/* 125:229 */     Utils.checkForRemainingOptions(options);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public String[] getOptions()
/* 129:    */   {
/* 130:240 */     Vector<String> result = new Vector();
/* 131:242 */     if (getDebug()) {
/* 132:243 */       result.add("-D");
/* 133:    */     }
/* 134:246 */     result.add("-N");
/* 135:247 */     result.add("" + this.m_filterType);
/* 136:    */     
/* 137:249 */     Collections.addAll(result, super.getOptions());
/* 138:    */     
/* 139:251 */     return (String[])result.toArray(new String[result.size()]);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public String filterTypeTipText()
/* 143:    */   {
/* 144:261 */     return "The filter type for transforming the training data.";
/* 145:    */   }
/* 146:    */   
/* 147:    */   public SelectedTag getFilterType()
/* 148:    */   {
/* 149:271 */     return new SelectedTag(this.m_filterType, TAGS_FILTER);
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void setFilterType(SelectedTag newType)
/* 153:    */   {
/* 154:282 */     if (newType.getTags() == TAGS_FILTER) {
/* 155:283 */       this.m_filterType = newType.getSelectedTag().getID();
/* 156:    */     }
/* 157:    */   }
/* 158:    */   
/* 159:    */   private class OptEng
/* 160:    */     extends Optimization
/* 161:    */   {
/* 162:    */     private OptEng() {}
/* 163:    */     
/* 164:    */     protected double objectiveFunction(double[] x)
/* 165:    */     {
/* 166:297 */       double nll = 0.0D;
/* 167:298 */       for (int i = 0; i < MDD.this.m_Classes.length; i++)
/* 168:    */       {
/* 169:299 */         int nI = MDD.this.m_Data[i][0].length;
/* 170:300 */         double bag = 0.0D;
/* 171:302 */         for (int j = 0; j < nI; j++)
/* 172:    */         {
/* 173:303 */           double ins = 0.0D;
/* 174:304 */           for (int k = 0; k < MDD.this.m_Data[i].length; k++) {
/* 175:305 */             ins += (MDD.this.m_Data[i][k][j] - x[(k * 2)]) * (MDD.this.m_Data[i][k][j] - x[(k * 2)]) / (x[(k * 2 + 1)] * x[(k * 2 + 1)]);
/* 176:    */           }
/* 177:308 */           ins = Math.exp(-ins);
/* 178:310 */           if (MDD.this.m_Classes[i] == 1) {
/* 179:311 */             bag += ins / nI;
/* 180:    */           } else {
/* 181:313 */             bag += (1.0D - ins) / nI;
/* 182:    */           }
/* 183:    */         }
/* 184:316 */         if (bag <= m_Zero) {
/* 185:317 */           bag = m_Zero;
/* 186:    */         }
/* 187:319 */         nll -= Math.log(bag);
/* 188:    */       }
/* 189:322 */       return nll;
/* 190:    */     }
/* 191:    */     
/* 192:    */     protected double[] evaluateGradient(double[] x)
/* 193:    */     {
/* 194:333 */       double[] grad = new double[x.length];
/* 195:334 */       for (int i = 0; i < MDD.this.m_Classes.length; i++)
/* 196:    */       {
/* 197:335 */         int nI = MDD.this.m_Data[i][0].length;
/* 198:    */         
/* 199:337 */         double denom = 0.0D;
/* 200:338 */         double[] numrt = new double[x.length];
/* 201:340 */         for (int j = 0; j < nI; j++)
/* 202:    */         {
/* 203:341 */           double exp = 0.0D;
/* 204:342 */           for (int k = 0; k < MDD.this.m_Data[i].length; k++) {
/* 205:343 */             exp += (MDD.this.m_Data[i][k][j] - x[(k * 2)]) * (MDD.this.m_Data[i][k][j] - x[(k * 2)]) / (x[(k * 2 + 1)] * x[(k * 2 + 1)]);
/* 206:    */           }
/* 207:346 */           exp = Math.exp(-exp);
/* 208:347 */           if (MDD.this.m_Classes[i] == 1) {
/* 209:348 */             denom += exp;
/* 210:    */           } else {
/* 211:350 */             denom += 1.0D - exp;
/* 212:    */           }
/* 213:354 */           for (int p = 0; p < MDD.this.m_Data[i].length; p++)
/* 214:    */           {
/* 215:355 */             numrt[(2 * p)] += exp * 2.0D * (x[(2 * p)] - MDD.this.m_Data[i][p][j]) / (x[(2 * p + 1)] * x[(2 * p + 1)]);
/* 216:    */             
/* 217:357 */             numrt[(2 * p + 1)] += exp * (x[(2 * p)] - MDD.this.m_Data[i][p][j]) * (x[(2 * p)] - MDD.this.m_Data[i][p][j]) / (x[(2 * p + 1)] * x[(2 * p + 1)] * x[(2 * p + 1)]);
/* 218:    */           }
/* 219:    */         }
/* 220:363 */         if (denom <= m_Zero) {
/* 221:364 */           denom = m_Zero;
/* 222:    */         }
/* 223:368 */         for (int q = 0; q < MDD.this.m_Data[i].length; q++) {
/* 224:369 */           if (MDD.this.m_Classes[i] == 1)
/* 225:    */           {
/* 226:370 */             grad[(2 * q)] += numrt[(2 * q)] / denom;
/* 227:371 */             grad[(2 * q + 1)] -= numrt[(2 * q + 1)] / denom;
/* 228:    */           }
/* 229:    */           else
/* 230:    */           {
/* 231:373 */             grad[(2 * q)] -= numrt[(2 * q)] / denom;
/* 232:374 */             grad[(2 * q + 1)] += numrt[(2 * q + 1)] / denom;
/* 233:    */           }
/* 234:    */         }
/* 235:    */       }
/* 236:379 */       return grad;
/* 237:    */     }
/* 238:    */     
/* 239:    */     public String getRevision()
/* 240:    */     {
/* 241:389 */       return RevisionUtils.extract("$Revision: 10369 $");
/* 242:    */     }
/* 243:    */   }
/* 244:    */   
/* 245:    */   public Capabilities getCapabilities()
/* 246:    */   {
/* 247:400 */     Capabilities result = super.getCapabilities();
/* 248:401 */     result.disableAll();
/* 249:    */     
/* 250:    */ 
/* 251:404 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 252:405 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 253:    */     
/* 254:    */ 
/* 255:408 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/* 256:409 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 257:    */     
/* 258:    */ 
/* 259:412 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/* 260:    */     
/* 261:414 */     return result;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public Capabilities getMultiInstanceCapabilities()
/* 265:    */   {
/* 266:426 */     Capabilities result = super.getCapabilities();
/* 267:427 */     result.disableAll();
/* 268:    */     
/* 269:    */ 
/* 270:430 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 271:431 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 272:432 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 273:433 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 274:    */     
/* 275:    */ 
/* 276:436 */     result.disableAllClasses();
/* 277:437 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 278:    */     
/* 279:439 */     return result;
/* 280:    */   }
/* 281:    */   
/* 282:    */   public void buildClassifier(Instances train)
/* 283:    */     throws Exception
/* 284:    */   {
/* 285:452 */     getCapabilities().testWithFail(train);
/* 286:    */     
/* 287:    */ 
/* 288:455 */     train = new Instances(train);
/* 289:456 */     train.deleteWithMissingClass();
/* 290:    */     
/* 291:458 */     this.m_ClassIndex = train.classIndex();
/* 292:459 */     this.m_NumClasses = train.numClasses();
/* 293:    */     
/* 294:461 */     int nR = train.attribute(1).relation().numAttributes();
/* 295:462 */     int nC = train.numInstances();
/* 296:463 */     int[] bagSize = new int[nC];
/* 297:464 */     Instances datasets = new Instances(train.attribute(1).relation(), 0);
/* 298:    */     
/* 299:466 */     this.m_Data = new double[nC][nR][];
/* 300:467 */     this.m_Classes = new int[nC];
/* 301:468 */     this.m_Attributes = datasets.stringFreeStructure();
/* 302:471 */     if (this.m_Debug) {
/* 303:472 */       System.out.println("Extracting data...");
/* 304:    */     }
/* 305:474 */     ArrayList<Integer> maxSzIdx = new ArrayList();
/* 306:475 */     int maxSz = 0;
/* 307:477 */     for (int h = 0; h < nC; h++)
/* 308:    */     {
/* 309:478 */       Instance current = train.instance(h);
/* 310:479 */       this.m_Classes[h] = ((int)current.classValue());
/* 311:480 */       Instances currInsts = current.relationalValue(1);
/* 312:481 */       int nI = currInsts.numInstances();
/* 313:482 */       bagSize[h] = nI;
/* 314:484 */       for (int i = 0; i < nI; i++)
/* 315:    */       {
/* 316:485 */         Instance inst = currInsts.instance(i);
/* 317:486 */         datasets.add(inst);
/* 318:    */       }
/* 319:489 */       if (this.m_Classes[h] == 1) {
/* 320:490 */         if (nI > maxSz)
/* 321:    */         {
/* 322:491 */           maxSz = nI;
/* 323:492 */           maxSzIdx = new ArrayList(1);
/* 324:493 */           maxSzIdx.add(new Integer(h));
/* 325:    */         }
/* 326:494 */         else if (nI == maxSz)
/* 327:    */         {
/* 328:495 */           maxSzIdx.add(new Integer(h));
/* 329:    */         }
/* 330:    */       }
/* 331:    */     }
/* 332:501 */     if (this.m_filterType == 1) {
/* 333:502 */       this.m_Filter = new Standardize();
/* 334:503 */     } else if (this.m_filterType == 0) {
/* 335:504 */       this.m_Filter = new Normalize();
/* 336:    */     } else {
/* 337:506 */       this.m_Filter = null;
/* 338:    */     }
/* 339:509 */     if (this.m_Filter != null)
/* 340:    */     {
/* 341:510 */       this.m_Filter.setInputFormat(datasets);
/* 342:511 */       datasets = Filter.useFilter(datasets, this.m_Filter);
/* 343:    */     }
/* 344:514 */     this.m_Missing.setInputFormat(datasets);
/* 345:515 */     datasets = Filter.useFilter(datasets, this.m_Missing);
/* 346:    */     
/* 347:517 */     int instIndex = 0;
/* 348:518 */     int start = 0;
/* 349:519 */     for (int h = 0; h < nC; h++)
/* 350:    */     {
/* 351:520 */       for (int i = 0; i < datasets.numAttributes(); i++)
/* 352:    */       {
/* 353:522 */         this.m_Data[h][i] = new double[bagSize[h]];
/* 354:523 */         instIndex = start;
/* 355:524 */         for (int k = 0; k < bagSize[h]; k++)
/* 356:    */         {
/* 357:525 */           this.m_Data[h][i][k] = datasets.instance(instIndex).value(i);
/* 358:526 */           instIndex++;
/* 359:    */         }
/* 360:    */       }
/* 361:529 */       start = instIndex;
/* 362:    */     }
/* 363:537 */     if (this.m_Debug) {
/* 364:538 */       System.out.println("\nIteration History...");
/* 365:    */     }
/* 366:541 */     double[] x = new double[nR * 2];double[] tmp = new double[x.length];
/* 367:542 */     double[][] b = new double[2][x.length];
/* 368:    */     
/* 369:    */ 
/* 370:545 */     double bestnll = 1.7976931348623157E+308D;
/* 371:546 */     for (int t = 0; t < x.length; t++)
/* 372:    */     {
/* 373:547 */       b[0][t] = (0.0D / 0.0D);
/* 374:548 */       b[1][t] = (0.0D / 0.0D);
/* 375:    */     }
/* 376:552 */     for (int s = 0; s < maxSzIdx.size(); s++)
/* 377:    */     {
/* 378:553 */       int exIdx = ((Integer)maxSzIdx.get(s)).intValue();
/* 379:554 */       for (int p = 0; p < this.m_Data[exIdx][0].length; p++)
/* 380:    */       {
/* 381:555 */         for (int q = 0; q < nR; q++)
/* 382:    */         {
/* 383:556 */           x[(2 * q)] = this.m_Data[exIdx][q][p];
/* 384:557 */           x[(2 * q + 1)] = 1.0D;
/* 385:    */         }
/* 386:560 */         OptEng opt = new OptEng(null);
/* 387:561 */         tmp = opt.findArgmin(x, b);
/* 388:562 */         while (tmp == null)
/* 389:    */         {
/* 390:563 */           tmp = opt.getVarbValues();
/* 391:564 */           if (this.m_Debug) {
/* 392:565 */             System.out.println("200 iterations finished, not enough!");
/* 393:    */           }
/* 394:567 */           tmp = opt.findArgmin(tmp, b);
/* 395:    */         }
/* 396:569 */         double nll = opt.getMinFunction();
/* 397:571 */         if (nll < bestnll)
/* 398:    */         {
/* 399:572 */           bestnll = nll;
/* 400:573 */           this.m_Par = tmp;
/* 401:574 */           if (this.m_Debug) {
/* 402:575 */             System.out.println("!!!!!!!!!!!!!!!!Smaller NLL found: " + nll);
/* 403:    */           }
/* 404:    */         }
/* 405:578 */         if (this.m_Debug) {
/* 406:579 */           System.out.println(exIdx + ":  -------------<Converged>--------------");
/* 407:    */         }
/* 408:    */       }
/* 409:    */     }
/* 410:    */   }
/* 411:    */   
/* 412:    */   public double[] distributionForInstance(Instance exmp)
/* 413:    */     throws Exception
/* 414:    */   {
/* 415:597 */     Instances ins = exmp.relationalValue(1);
/* 416:598 */     if (this.m_Filter != null) {
/* 417:599 */       ins = Filter.useFilter(ins, this.m_Filter);
/* 418:    */     }
/* 419:602 */     ins = Filter.useFilter(ins, this.m_Missing);
/* 420:    */     
/* 421:604 */     int nI = ins.numInstances();int nA = ins.numAttributes();
/* 422:605 */     double[][] dat = new double[nI][nA];
/* 423:606 */     for (int j = 0; j < nI; j++) {
/* 424:607 */       for (int k = 0; k < nA; k++) {
/* 425:608 */         dat[j][k] = ins.instance(j).value(k);
/* 426:    */       }
/* 427:    */     }
/* 428:613 */     double[] distribution = new double[2];
/* 429:614 */     distribution[1] = 0.0D;
/* 430:616 */     for (int i = 0; i < nI; i++)
/* 431:    */     {
/* 432:617 */       double exp = 0.0D;
/* 433:618 */       for (int r = 0; r < nA; r++) {
/* 434:619 */         exp += (this.m_Par[(r * 2)] - dat[i][r]) * (this.m_Par[(r * 2)] - dat[i][r]) / (this.m_Par[(r * 2 + 1)] * this.m_Par[(r * 2 + 1)]);
/* 435:    */       }
/* 436:622 */       exp = Math.exp(-exp);
/* 437:    */       
/* 438:    */ 
/* 439:625 */       distribution[1] += exp / nI;
/* 440:626 */       distribution[0] += (1.0D - exp) / nI;
/* 441:    */     }
/* 442:629 */     return distribution;
/* 443:    */   }
/* 444:    */   
/* 445:    */   public String toString()
/* 446:    */   {
/* 447:640 */     String result = "Modified Logistic Regression";
/* 448:641 */     if (this.m_Par == null) {
/* 449:642 */       return result + ": No model built yet.";
/* 450:    */     }
/* 451:645 */     result = result + "\nCoefficients...\nVariable      Coeff.\n";
/* 452:646 */     int j = 0;
/* 453:646 */     for (int idx = 0; j < this.m_Par.length / 2; idx++)
/* 454:    */     {
/* 455:648 */       result = result + this.m_Attributes.attribute(idx).name();
/* 456:649 */       result = result + " " + Utils.doubleToString(this.m_Par[(j * 2)], 12, 4);
/* 457:650 */       result = result + " " + Utils.doubleToString(this.m_Par[(j * 2 + 1)], 12, 4) + "\n";j++;
/* 458:    */     }
/* 459:653 */     return result;
/* 460:    */   }
/* 461:    */   
/* 462:    */   public String getRevision()
/* 463:    */   {
/* 464:663 */     return RevisionUtils.extract("$Revision: 10369 $");
/* 465:    */   }
/* 466:    */   
/* 467:    */   public static void main(String[] argv)
/* 468:    */   {
/* 469:673 */     runClassifier(new MDD(), argv);
/* 470:    */   }
/* 471:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.MDD
 * JD-Core Version:    0.7.0.1
 */