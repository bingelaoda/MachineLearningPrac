/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.DenseInstance;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.Randomizable;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.SelectedTag;
/*  18:    */ import weka.core.Tag;
/*  19:    */ import weka.core.TechnicalInformation;
/*  20:    */ import weka.core.TechnicalInformation.Field;
/*  21:    */ import weka.core.TechnicalInformation.Type;
/*  22:    */ import weka.core.TechnicalInformationHandler;
/*  23:    */ import weka.core.Utils;
/*  24:    */ import weka.filters.Filter;
/*  25:    */ import weka.filters.UnsupervisedFilter;
/*  26:    */ 
/*  27:    */ public class RandomProjection
/*  28:    */   extends Filter
/*  29:    */   implements UnsupervisedFilter, OptionHandler, TechnicalInformationHandler, Randomizable
/*  30:    */ {
/*  31:    */   static final long serialVersionUID = 4428905532728645880L;
/*  32:117 */   protected int m_k = 10;
/*  33:123 */   protected double m_percent = 0.0D;
/*  34:    */   public static final int SPARSE1 = 1;
/*  35:    */   public static final int SPARSE2 = 2;
/*  36:    */   public static final int GAUSSIAN = 3;
/*  37:136 */   public static final Tag[] TAGS_DSTRS_TYPE = { new Tag(1, "Sparse1"), new Tag(2, "Sparse2"), new Tag(3, "Gaussian") };
/*  38:142 */   protected int m_distribution = 1;
/*  39:148 */   protected boolean m_useReplaceMissing = false;
/*  40:151 */   protected boolean m_OutputFormatDefined = false;
/*  41:    */   protected Filter m_ntob;
/*  42:    */   protected Filter m_replaceMissing;
/*  43:161 */   protected int m_rndmSeed = 42;
/*  44:    */   protected double[][] m_rmatrix;
/*  45:    */   protected Random m_random;
/*  46:    */   
/*  47:    */   public Enumeration<Option> listOptions()
/*  48:    */   {
/*  49:177 */     Vector<Option> newVector = new Vector(5);
/*  50:    */     
/*  51:179 */     newVector.addElement(new Option("\tThe number of dimensions (attributes) the data should be reduced to\n\t(default 10; exclusive of the class attribute, if it is set).", "N", 1, "-N <number>"));
/*  52:    */     
/*  53:    */ 
/*  54:    */ 
/*  55:    */ 
/*  56:184 */     newVector.addElement(new Option("\tThe distribution to use for calculating the random matrix.\n\tSparse1 is:\n\t  sqrt(3)*{-1 with prob(1/6), 0 with prob(2/3), +1 with prob(1/6)}\n\tSparse2 is:\n\t  {-1 with prob(1/2), +1 with prob(1/2)}\n", "D", 1, "-D [SPARSE1|SPARSE2|GAUSSIAN]"));
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
/*  68:196 */     newVector.addElement(new Option("\tThe percentage of dimensions (attributes) the data should\n\tbe reduced to (exclusive of the class attribute, if it is set). The -N\n\toption is ignored if this option is present and is greater\n\tthan zero.", "P", 1, "-P <percent>"));
/*  69:    */     
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:203 */     newVector.addElement(new Option("\tReplace missing values using the ReplaceMissingValues filter", "M", 0, "-M"));
/*  76:    */     
/*  77:    */ 
/*  78:    */ 
/*  79:207 */     newVector.addElement(new Option("\tThe random seed for the random number generator used for\n\tcalculating the random matrix (default 42).", "R", 0, "-R <num>"));
/*  80:    */     
/*  81:    */ 
/*  82:    */ 
/*  83:211 */     return newVector.elements();
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setOptions(String[] options)
/*  87:    */     throws Exception
/*  88:    */   {
/*  89:254 */     String mString = Utils.getOption('P', options);
/*  90:255 */     if (mString.length() != 0)
/*  91:    */     {
/*  92:256 */       setPercent(Double.parseDouble(mString));
/*  93:    */     }
/*  94:    */     else
/*  95:    */     {
/*  96:259 */       setPercent(0.0D);
/*  97:260 */       mString = Utils.getOption('N', options);
/*  98:261 */       if (mString.length() != 0) {
/*  99:262 */         setNumberOfAttributes(Integer.parseInt(mString));
/* 100:    */       } else {
/* 101:264 */         setNumberOfAttributes(10);
/* 102:    */       }
/* 103:    */     }
/* 104:268 */     mString = Utils.getOption('R', options);
/* 105:269 */     if (mString.length() != 0) {
/* 106:270 */       setSeed(Integer.parseInt(mString));
/* 107:    */     }
/* 108:273 */     mString = Utils.getOption('D', options);
/* 109:274 */     if (mString.length() != 0) {
/* 110:275 */       if (mString.equalsIgnoreCase("sparse1")) {
/* 111:276 */         setDistribution(new SelectedTag(1, TAGS_DSTRS_TYPE));
/* 112:277 */       } else if (mString.equalsIgnoreCase("sparse2")) {
/* 113:278 */         setDistribution(new SelectedTag(2, TAGS_DSTRS_TYPE));
/* 114:279 */       } else if (mString.equalsIgnoreCase("gaussian")) {
/* 115:280 */         setDistribution(new SelectedTag(3, TAGS_DSTRS_TYPE));
/* 116:    */       }
/* 117:    */     }
/* 118:284 */     if (Utils.getFlag('M', options)) {
/* 119:285 */       setReplaceMissingValues(true);
/* 120:    */     } else {
/* 121:287 */       setReplaceMissingValues(false);
/* 122:    */     }
/* 123:295 */     Utils.checkForRemainingOptions(options);
/* 124:    */   }
/* 125:    */   
/* 126:    */   public String[] getOptions()
/* 127:    */   {
/* 128:306 */     Vector<String> options = new Vector();
/* 129:312 */     if (getReplaceMissingValues()) {
/* 130:313 */       options.add("-M");
/* 131:    */     }
/* 132:316 */     if (getPercent() <= 0.0D)
/* 133:    */     {
/* 134:317 */       options.add("-N");
/* 135:318 */       options.add("" + getNumberOfAttributes());
/* 136:    */     }
/* 137:    */     else
/* 138:    */     {
/* 139:320 */       options.add("-P");
/* 140:321 */       options.add("" + getPercent());
/* 141:    */     }
/* 142:324 */     options.add("-R");
/* 143:325 */     options.add("" + getSeed());
/* 144:    */     
/* 145:327 */     SelectedTag t = getDistribution();
/* 146:328 */     options.add("-D");
/* 147:329 */     options.add("" + t.getSelectedTag().getReadable());
/* 148:    */     
/* 149:331 */     return (String[])options.toArray(new String[0]);
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String globalInfo()
/* 153:    */   {
/* 154:342 */     return "Reduces the dimensionality of the data by projecting it onto a lower dimensional subspace using a random matrix with columns of unit length (i.e. It will reduce the number of attributes in the data while preserving much of its variation like PCA, but at a much less computational cost).\nIt first applies the  NominalToBinary filter to convert all attributes to numeric before reducing the dimension. It preserves the class attribute.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/* 155:    */   }
/* 156:    */   
/* 157:    */   public TechnicalInformation getTechnicalInformation()
/* 158:    */   {
/* 159:365 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/* 160:366 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Dmitriy Fradkin and David Madigan");
/* 161:367 */     result.setValue(TechnicalInformation.Field.TITLE, "Experiments with random projections for machine learning");
/* 162:    */     
/* 163:369 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "KDD '03: Proceedings of the ninth ACM SIGKDD international conference on Knowledge discovery and data mining");
/* 164:    */     
/* 165:    */ 
/* 166:    */ 
/* 167:373 */     result.setValue(TechnicalInformation.Field.YEAR, "003");
/* 168:374 */     result.setValue(TechnicalInformation.Field.PAGES, "517-522");
/* 169:375 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "ACM Press");
/* 170:376 */     result.setValue(TechnicalInformation.Field.ADDRESS, "New York, NY, USA");
/* 171:    */     
/* 172:378 */     return result;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public String numberOfAttributesTipText()
/* 176:    */   {
/* 177:389 */     return "The number of dimensions (attributes) the data should be reduced to.";
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void setNumberOfAttributes(int newAttNum)
/* 181:    */   {
/* 182:399 */     this.m_k = newAttNum;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public int getNumberOfAttributes()
/* 186:    */   {
/* 187:409 */     return this.m_k;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String percentTipText()
/* 191:    */   {
/* 192:420 */     return " The percentage of dimensions (attributes) the data should be reduced to  (inclusive of the class attribute). This  NumberOfAttributes option is ignored if this option is present or is greater than zero.";
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void setPercent(double newPercent)
/* 196:    */   {
/* 197:433 */     if (newPercent > 0.0D) {
/* 198:434 */       newPercent /= 100.0D;
/* 199:    */     }
/* 200:436 */     this.m_percent = newPercent;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public double getPercent()
/* 204:    */   {
/* 205:445 */     return this.m_percent * 100.0D;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String seedTipText()
/* 209:    */   {
/* 210:455 */     return "The random seed used by the random number generator used for generating the random matrix ";
/* 211:    */   }
/* 212:    */   
/* 213:    */   public void setSeed(int seed)
/* 214:    */   {
/* 215:466 */     this.m_rndmSeed = seed;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public int getSeed()
/* 219:    */   {
/* 220:476 */     return this.m_rndmSeed;
/* 221:    */   }
/* 222:    */   
/* 223:    */   public String distributionTipText()
/* 224:    */   {
/* 225:486 */     return "The distribution to use for calculating the random matrix.\nSparse1 is:\n sqrt(3) * { -1 with prob(1/6), \n               0 with prob(2/3),  \n              +1 with prob(1/6) } \nSparse2 is:\n { -1 with prob(1/2), \n   +1 with prob(1/2) } ";
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void setDistribution(SelectedTag newDstr)
/* 229:    */   {
/* 230:501 */     if (newDstr.getTags() == TAGS_DSTRS_TYPE) {
/* 231:502 */       this.m_distribution = newDstr.getSelectedTag().getID();
/* 232:    */     }
/* 233:    */   }
/* 234:    */   
/* 235:    */   public SelectedTag getDistribution()
/* 236:    */   {
/* 237:513 */     return new SelectedTag(this.m_distribution, TAGS_DSTRS_TYPE);
/* 238:    */   }
/* 239:    */   
/* 240:    */   public String replaceMissingValuesTipText()
/* 241:    */   {
/* 242:524 */     return "If set the filter uses weka.filters.unsupervised.attribute.ReplaceMissingValues to replace the missing values";
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void setReplaceMissingValues(boolean t)
/* 246:    */   {
/* 247:534 */     this.m_useReplaceMissing = t;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public boolean getReplaceMissingValues()
/* 251:    */   {
/* 252:543 */     return this.m_useReplaceMissing;
/* 253:    */   }
/* 254:    */   
/* 255:    */   public Capabilities getCapabilities()
/* 256:    */   {
/* 257:554 */     Capabilities result = super.getCapabilities();
/* 258:555 */     result.disableAll();
/* 259:    */     
/* 260:    */ 
/* 261:558 */     result.enableAllAttributes();
/* 262:559 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 263:560 */     result.disable(Capabilities.Capability.STRING_ATTRIBUTES);
/* 264:    */     
/* 265:    */ 
/* 266:563 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 267:564 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 268:565 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 269:566 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 270:567 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 271:    */     
/* 272:569 */     return result;
/* 273:    */   }
/* 274:    */   
/* 275:    */   public boolean setInputFormat(Instances instanceInfo)
/* 276:    */     throws Exception
/* 277:    */   {
/* 278:583 */     super.setInputFormat(instanceInfo);
/* 279:590 */     for (int i = 0; i < instanceInfo.numAttributes(); i++) {
/* 280:591 */       if ((i != instanceInfo.classIndex()) && (instanceInfo.attribute(i).isNominal()))
/* 281:    */       {
/* 282:593 */         if (instanceInfo.classIndex() >= 0)
/* 283:    */         {
/* 284:594 */           this.m_ntob = new weka.filters.supervised.attribute.NominalToBinary(); break;
/* 285:    */         }
/* 286:596 */         this.m_ntob = new NominalToBinary();
/* 287:    */         
/* 288:    */ 
/* 289:599 */         break;
/* 290:    */       }
/* 291:    */     }
/* 292:606 */     boolean temp = true;
/* 293:607 */     if (this.m_replaceMissing != null)
/* 294:    */     {
/* 295:608 */       this.m_replaceMissing = new ReplaceMissingValues();
/* 296:609 */       if (this.m_replaceMissing.setInputFormat(instanceInfo)) {
/* 297:610 */         temp = true;
/* 298:    */       } else {
/* 299:612 */         temp = false;
/* 300:    */       }
/* 301:    */     }
/* 302:616 */     if (this.m_ntob != null)
/* 303:    */     {
/* 304:617 */       if (this.m_ntob.setInputFormat(instanceInfo))
/* 305:    */       {
/* 306:618 */         setOutputFormat();
/* 307:619 */         return temp;
/* 308:    */       }
/* 309:621 */       return false;
/* 310:    */     }
/* 311:624 */     setOutputFormat();
/* 312:625 */     return temp;
/* 313:    */   }
/* 314:    */   
/* 315:    */   public boolean input(Instance instance)
/* 316:    */     throws Exception
/* 317:    */   {
/* 318:639 */     Instance newInstance = null;
/* 319:641 */     if (getInputFormat() == null) {
/* 320:642 */       throw new IllegalStateException("No input instance format defined");
/* 321:    */     }
/* 322:644 */     if (this.m_NewBatch)
/* 323:    */     {
/* 324:645 */       resetQueue();
/* 325:    */       
/* 326:    */ 
/* 327:648 */       this.m_NewBatch = false;
/* 328:    */     }
/* 329:651 */     boolean replaceDone = false;
/* 330:652 */     if (this.m_replaceMissing != null) {
/* 331:653 */       if (this.m_replaceMissing.input(instance))
/* 332:    */       {
/* 333:654 */         if (!this.m_OutputFormatDefined) {
/* 334:655 */           setOutputFormat();
/* 335:    */         }
/* 336:657 */         newInstance = this.m_replaceMissing.output();
/* 337:658 */         replaceDone = true;
/* 338:    */       }
/* 339:    */       else
/* 340:    */       {
/* 341:660 */         return false;
/* 342:    */       }
/* 343:    */     }
/* 344:665 */     if (this.m_ntob != null)
/* 345:    */     {
/* 346:666 */       if (!replaceDone) {
/* 347:667 */         newInstance = instance;
/* 348:    */       }
/* 349:669 */       if (this.m_ntob.input(newInstance))
/* 350:    */       {
/* 351:670 */         if (!this.m_OutputFormatDefined) {
/* 352:671 */           setOutputFormat();
/* 353:    */         }
/* 354:673 */         newInstance = this.m_ntob.output();
/* 355:674 */         newInstance = convertInstance(newInstance);
/* 356:675 */         push(newInstance, false);
/* 357:676 */         return true;
/* 358:    */       }
/* 359:678 */       return false;
/* 360:    */     }
/* 361:681 */     if (!replaceDone) {
/* 362:682 */       newInstance = instance;
/* 363:    */     }
/* 364:684 */     newInstance = convertInstance(newInstance);
/* 365:685 */     push(newInstance, false);
/* 366:686 */     return true;
/* 367:    */   }
/* 368:    */   
/* 369:    */   public boolean batchFinished()
/* 370:    */     throws Exception
/* 371:    */   {
/* 372:699 */     if (getInputFormat() == null) {
/* 373:700 */       throw new NullPointerException("No input instance format defined");
/* 374:    */     }
/* 375:703 */     boolean conversionDone = false;
/* 376:704 */     if ((this.m_replaceMissing != null) && 
/* 377:705 */       (this.m_replaceMissing.batchFinished()))
/* 378:    */     {
/* 379:    */       Instance instance;
/* 380:708 */       while ((instance = this.m_replaceMissing.output()) != null)
/* 381:    */       {
/* 382:709 */         if (!this.m_OutputFormatDefined) {
/* 383:710 */           setOutputFormat();
/* 384:    */         }
/* 385:712 */         if (this.m_ntob != null)
/* 386:    */         {
/* 387:713 */           this.m_ntob.input(instance);
/* 388:    */         }
/* 389:    */         else
/* 390:    */         {
/* 391:715 */           Instance newInstance = convertInstance(instance);
/* 392:716 */           push(newInstance, false);
/* 393:    */         }
/* 394:    */       }
/* 395:720 */       if ((this.m_ntob != null) && 
/* 396:721 */         (this.m_ntob.batchFinished()))
/* 397:    */       {
/* 398:723 */         while ((instance = this.m_ntob.output()) != null)
/* 399:    */         {
/* 400:724 */           if (!this.m_OutputFormatDefined) {
/* 401:725 */             setOutputFormat();
/* 402:    */           }
/* 403:727 */           Instance newInstance = convertInstance(instance);
/* 404:728 */           push(newInstance, false);
/* 405:    */         }
/* 406:730 */         this.m_ntob = null;
/* 407:    */       }
/* 408:733 */       this.m_replaceMissing = null;
/* 409:734 */       conversionDone = true;
/* 410:    */     }
/* 411:738 */     if ((!conversionDone) && (this.m_ntob != null) && 
/* 412:739 */       (this.m_ntob.batchFinished()))
/* 413:    */     {
/* 414:    */       Instance instance;
/* 415:741 */       while ((instance = this.m_ntob.output()) != null)
/* 416:    */       {
/* 417:742 */         if (!this.m_OutputFormatDefined) {
/* 418:743 */           setOutputFormat();
/* 419:    */         }
/* 420:745 */         Instance newInstance = convertInstance(instance);
/* 421:746 */         push(newInstance, false);
/* 422:    */       }
/* 423:748 */       this.m_ntob = null;
/* 424:    */     }
/* 425:751 */     this.m_OutputFormatDefined = false;
/* 426:752 */     return super.batchFinished();
/* 427:    */   }
/* 428:    */   
/* 429:    */   protected void setOutputFormat()
/* 430:    */   {
/* 431:    */     Instances currentFormat;
/* 432:    */     Instances currentFormat;
/* 433:758 */     if (this.m_ntob != null) {
/* 434:759 */       currentFormat = this.m_ntob.getOutputFormat();
/* 435:    */     } else {
/* 436:761 */       currentFormat = getInputFormat();
/* 437:    */     }
/* 438:764 */     if (this.m_percent > 0.0D) {
/* 439:765 */       this.m_k = ((int)((getInputFormat().numAttributes() - 1) * this.m_percent));
/* 440:    */     }
/* 441:773 */     int newClassIndex = -1;
/* 442:774 */     ArrayList<Attribute> attributes = new ArrayList();
/* 443:775 */     for (int i = 0; i < this.m_k; i++) {
/* 444:776 */       attributes.add(new Attribute("K" + (i + 1)));
/* 445:    */     }
/* 446:778 */     if (currentFormat.classIndex() != -1)
/* 447:    */     {
/* 448:780 */       attributes.add((Attribute)currentFormat.attribute(currentFormat.classIndex()).copy());
/* 449:    */       
/* 450:782 */       newClassIndex = attributes.size() - 1;
/* 451:    */     }
/* 452:785 */     Instances newFormat = new Instances(currentFormat.relationName(), attributes, 0);
/* 453:786 */     if (newClassIndex != -1) {
/* 454:787 */       newFormat.setClassIndex(newClassIndex);
/* 455:    */     }
/* 456:789 */     this.m_OutputFormatDefined = true;
/* 457:    */     
/* 458:791 */     this.m_random = new Random();
/* 459:792 */     this.m_random.setSeed(this.m_rndmSeed);
/* 460:    */     
/* 461:794 */     this.m_rmatrix = new double[this.m_k][currentFormat.numAttributes()];
/* 462:795 */     if (this.m_distribution == 3)
/* 463:    */     {
/* 464:796 */       for (int i = 0; i < this.m_rmatrix.length; i++) {
/* 465:797 */         for (int j = 0; j < this.m_rmatrix[i].length; j++) {
/* 466:798 */           this.m_rmatrix[i][j] = this.m_random.nextGaussian();
/* 467:    */         }
/* 468:    */       }
/* 469:    */     }
/* 470:    */     else
/* 471:    */     {
/* 472:802 */       boolean useDstrWithZero = this.m_distribution == 1;
/* 473:803 */       for (int i = 0; i < this.m_rmatrix.length; i++) {
/* 474:804 */         for (int j = 0; j < this.m_rmatrix[i].length; j++) {
/* 475:805 */           this.m_rmatrix[i][j] = rndmNum(useDstrWithZero);
/* 476:    */         }
/* 477:    */       }
/* 478:    */     }
/* 479:810 */     setOutputFormat(newFormat);
/* 480:    */   }
/* 481:    */   
/* 482:    */   protected Instance convertInstance(Instance currentInstance)
/* 483:    */   {
/* 484:822 */     double[] vals = new double[getOutputFormat().numAttributes()];
/* 485:823 */     int classIndex = this.m_ntob == null ? getInputFormat().classIndex() : this.m_ntob.getOutputFormat().classIndex();
/* 486:826 */     for (int i = 0; i < this.m_k; i++) {
/* 487:827 */       vals[i] = computeRandomProjection(i, classIndex, currentInstance);
/* 488:    */     }
/* 489:829 */     if (classIndex != -1) {
/* 490:830 */       vals[this.m_k] = currentInstance.value(classIndex);
/* 491:    */     }
/* 492:833 */     Instance newInstance = new DenseInstance(currentInstance.weight(), vals);
/* 493:834 */     newInstance.setDataset(getOutputFormat());
/* 494:    */     
/* 495:836 */     return newInstance;
/* 496:    */   }
/* 497:    */   
/* 498:    */   protected double computeRandomProjection(int rpIndex, int classIndex, Instance instance)
/* 499:    */   {
/* 500:851 */     double sum = 0.0D;
/* 501:852 */     for (int i = 0; i < instance.numValues(); i++)
/* 502:    */     {
/* 503:853 */       int index = instance.index(i);
/* 504:854 */       if (index != classIndex)
/* 505:    */       {
/* 506:855 */         double value = instance.valueSparse(i);
/* 507:856 */         if (!Utils.isMissingValue(value)) {
/* 508:857 */           sum += this.m_rmatrix[rpIndex][index] * value;
/* 509:    */         }
/* 510:    */       }
/* 511:    */     }
/* 512:861 */     return sum;
/* 513:    */   }
/* 514:    */   
/* 515:864 */   private static final int[] weights = { 1, 1, 4 };
/* 516:865 */   private static final int[] vals = { -1, 1, 0 };
/* 517:866 */   private static final int[] weights2 = { 1, 1 };
/* 518:867 */   private static final int[] vals2 = { -1, 1 };
/* 519:868 */   private static final double sqrt3 = Math.sqrt(3.0D);
/* 520:    */   
/* 521:    */   protected double rndmNum(boolean useDstrWithZero)
/* 522:    */   {
/* 523:878 */     if (useDstrWithZero) {
/* 524:879 */       return sqrt3 * vals[weightedDistribution(weights)];
/* 525:    */     }
/* 526:881 */     return vals2[weightedDistribution(weights2)];
/* 527:    */   }
/* 528:    */   
/* 529:    */   protected int weightedDistribution(int[] weights)
/* 530:    */   {
/* 531:892 */     int sum = 0;
/* 532:894 */     for (int weight : weights) {
/* 533:895 */       sum += weight;
/* 534:    */     }
/* 535:898 */     int val = (int)Math.floor(this.m_random.nextDouble() * sum);
/* 536:900 */     for (int i = 0; i < weights.length; i++)
/* 537:    */     {
/* 538:901 */       val -= weights[i];
/* 539:902 */       if (val < 0) {
/* 540:903 */         return i;
/* 541:    */       }
/* 542:    */     }
/* 543:906 */     return -1;
/* 544:    */   }
/* 545:    */   
/* 546:    */   public String getRevision()
/* 547:    */   {
/* 548:916 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 549:    */   }
/* 550:    */   
/* 551:    */   public static void main(String[] argv)
/* 552:    */   {
/* 553:925 */     runFilter(new RandomProjection(), argv);
/* 554:    */   }
/* 555:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.RandomProjection
 * JD-Core Version:    0.7.0.1
 */