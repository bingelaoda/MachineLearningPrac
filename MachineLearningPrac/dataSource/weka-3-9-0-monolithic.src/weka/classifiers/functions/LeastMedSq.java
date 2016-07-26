/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.AbstractClassifier;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SelectedTag;
/*  17:    */ import weka.core.TechnicalInformation;
/*  18:    */ import weka.core.TechnicalInformation.Field;
/*  19:    */ import weka.core.TechnicalInformation.Type;
/*  20:    */ import weka.core.TechnicalInformationHandler;
/*  21:    */ import weka.core.Utils;
/*  22:    */ import weka.filters.Filter;
/*  23:    */ import weka.filters.supervised.attribute.NominalToBinary;
/*  24:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  25:    */ import weka.filters.unsupervised.instance.RemoveRange;
/*  26:    */ 
/*  27:    */ public class LeastMedSq
/*  28:    */   extends AbstractClassifier
/*  29:    */   implements OptionHandler, TechnicalInformationHandler
/*  30:    */ {
/*  31:    */   static final long serialVersionUID = 4288954049987652970L;
/*  32:    */   private double[] m_Residuals;
/*  33:    */   private double[] m_weight;
/*  34:    */   private double m_scalefactor;
/*  35:113 */   private double m_bestMedian = (1.0D / 0.0D);
/*  36:    */   private LinearRegression m_currentRegression;
/*  37:    */   private LinearRegression m_bestRegression;
/*  38:    */   private LinearRegression m_ls;
/*  39:    */   private Instances m_Data;
/*  40:    */   private Instances m_RLSData;
/*  41:    */   private Instances m_SubSample;
/*  42:    */   private ReplaceMissingValues m_MissingFilter;
/*  43:    */   private NominalToBinary m_TransformFilter;
/*  44:    */   private RemoveRange m_SplitFilter;
/*  45:133 */   private int m_samplesize = 4;
/*  46:    */   private int m_samples;
/*  47:    */   private Random m_random;
/*  48:141 */   private long m_randomseed = 0L;
/*  49:143 */   private SelectedTag m_tag = new SelectedTag(1, LinearRegression.TAGS_SELECTION);
/*  50:    */   
/*  51:    */   public String globalInfo()
/*  52:    */   {
/*  53:152 */     return "Implements a least median sqaured linear regression utilising the existing weka LinearRegression class to form predictions. \nLeast squared regression functions are generated from random subsamples of the data. The least squared regression with the lowest meadian squared error is chosen as the final model.\n\nThe basis of the algorithm is \n\n" + getTechnicalInformation().toString();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public TechnicalInformation getTechnicalInformation()
/*  57:    */   {
/*  58:172 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.BOOK);
/*  59:173 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Peter J. Rousseeuw and Annick M. Leroy");
/*  60:174 */     result.setValue(TechnicalInformation.Field.YEAR, "1987");
/*  61:175 */     result.setValue(TechnicalInformation.Field.TITLE, "Robust regression and outlier detection");
/*  62:    */     
/*  63:177 */     return result;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public Capabilities getCapabilities()
/*  67:    */   {
/*  68:187 */     Capabilities result = super.getCapabilities();
/*  69:188 */     result.disableAll();
/*  70:    */     
/*  71:    */ 
/*  72:191 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  73:192 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  74:193 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  75:194 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  76:    */     
/*  77:    */ 
/*  78:197 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  79:198 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  80:199 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  81:    */     
/*  82:201 */     return result;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void buildClassifier(Instances data)
/*  86:    */     throws Exception
/*  87:    */   {
/*  88:214 */     getCapabilities().testWithFail(data);
/*  89:    */     
/*  90:    */ 
/*  91:217 */     data = new Instances(data);
/*  92:218 */     data.deleteWithMissingClass();
/*  93:    */     
/*  94:220 */     cleanUpData(data);
/*  95:    */     
/*  96:222 */     getSamples();
/*  97:    */     
/*  98:224 */     findBestRegression();
/*  99:    */     
/* 100:226 */     buildRLSRegression();
/* 101:    */   }
/* 102:    */   
/* 103:    */   public double classifyInstance(Instance instance)
/* 104:    */     throws Exception
/* 105:    */   {
/* 106:241 */     Instance transformedInstance = instance;
/* 107:242 */     this.m_TransformFilter.input(transformedInstance);
/* 108:243 */     transformedInstance = this.m_TransformFilter.output();
/* 109:244 */     this.m_MissingFilter.input(transformedInstance);
/* 110:245 */     transformedInstance = this.m_MissingFilter.output();
/* 111:    */     
/* 112:247 */     return this.m_ls.classifyInstance(transformedInstance);
/* 113:    */   }
/* 114:    */   
/* 115:    */   private void cleanUpData(Instances data)
/* 116:    */     throws Exception
/* 117:    */   {
/* 118:258 */     this.m_Data = data;
/* 119:259 */     this.m_TransformFilter = new NominalToBinary();
/* 120:260 */     this.m_TransformFilter.setInputFormat(this.m_Data);
/* 121:261 */     this.m_Data = Filter.useFilter(this.m_Data, this.m_TransformFilter);
/* 122:262 */     this.m_MissingFilter = new ReplaceMissingValues();
/* 123:263 */     this.m_MissingFilter.setInputFormat(this.m_Data);
/* 124:264 */     this.m_Data = Filter.useFilter(this.m_Data, this.m_MissingFilter);
/* 125:265 */     this.m_Data.deleteWithMissingClass();
/* 126:    */   }
/* 127:    */   
/* 128:    */   private void getSamples()
/* 129:    */     throws Exception
/* 130:    */   {
/* 131:275 */     int[] stuf = { 500, 50, 22, 17, 15, 14 };
/* 132:276 */     if (this.m_samplesize < 7)
/* 133:    */     {
/* 134:277 */       if (this.m_Data.numInstances() < stuf[(this.m_samplesize - 1)]) {
/* 135:278 */         this.m_samples = combinations(this.m_Data.numInstances(), this.m_samplesize);
/* 136:    */       } else {
/* 137:280 */         this.m_samples = (this.m_samplesize * 500);
/* 138:    */       }
/* 139:    */     }
/* 140:    */     else {
/* 141:284 */       this.m_samples = 3000;
/* 142:    */     }
/* 143:286 */     if (this.m_Debug)
/* 144:    */     {
/* 145:287 */       System.out.println("m_samplesize: " + this.m_samplesize);
/* 146:288 */       System.out.println("m_samples: " + this.m_samples);
/* 147:289 */       System.out.println("m_randomseed: " + this.m_randomseed);
/* 148:    */     }
/* 149:    */   }
/* 150:    */   
/* 151:    */   private void setRandom()
/* 152:    */   {
/* 153:300 */     this.m_random = new Random(getRandomSeed());
/* 154:    */   }
/* 155:    */   
/* 156:    */   private void findBestRegression()
/* 157:    */     throws Exception
/* 158:    */   {
/* 159:311 */     setRandom();
/* 160:312 */     this.m_bestMedian = (1.0D / 0.0D);
/* 161:313 */     if (this.m_Debug) {
/* 162:314 */       System.out.println("Starting:");
/* 163:    */     }
/* 164:316 */     for (int s = 0; s < this.m_samples; s++)
/* 165:    */     {
/* 166:317 */       if ((this.m_Debug) && 
/* 167:318 */         (s % (this.m_samples / 100) == 0)) {
/* 168:319 */         System.out.print("*");
/* 169:    */       }
/* 170:322 */       genRegression();
/* 171:323 */       getMedian();
/* 172:    */     }
/* 173:325 */     if (this.m_Debug) {
/* 174:326 */       System.out.println("");
/* 175:    */     }
/* 176:328 */     this.m_currentRegression = this.m_bestRegression;
/* 177:    */   }
/* 178:    */   
/* 179:    */   private void genRegression()
/* 180:    */     throws Exception
/* 181:    */   {
/* 182:338 */     this.m_currentRegression = new LinearRegression();
/* 183:339 */     this.m_currentRegression.setAttributeSelectionMethod(this.m_tag);
/* 184:    */     
/* 185:341 */     selectSubSample(this.m_Data);
/* 186:342 */     this.m_currentRegression.buildClassifier(this.m_SubSample);
/* 187:    */   }
/* 188:    */   
/* 189:    */   private void findResiduals()
/* 190:    */     throws Exception
/* 191:    */   {
/* 192:353 */     this.m_Residuals = new double[this.m_Data.numInstances()];
/* 193:354 */     for (int i = 0; i < this.m_Data.numInstances(); i++)
/* 194:    */     {
/* 195:355 */       this.m_Residuals[i] = this.m_currentRegression.classifyInstance(this.m_Data.instance(i));
/* 196:356 */       this.m_Residuals[i] -= this.m_Data.instance(i).value(this.m_Data.classAttribute());
/* 197:357 */       this.m_Residuals[i] *= this.m_Residuals[i];
/* 198:    */     }
/* 199:    */   }
/* 200:    */   
/* 201:    */   private void getMedian()
/* 202:    */     throws Exception
/* 203:    */   {
/* 204:369 */     findResiduals();
/* 205:370 */     int p = this.m_Residuals.length;
/* 206:371 */     select(this.m_Residuals, 0, p - 1, p / 2);
/* 207:372 */     if (this.m_Residuals[(p / 2)] < this.m_bestMedian)
/* 208:    */     {
/* 209:373 */       this.m_bestMedian = this.m_Residuals[(p / 2)];
/* 210:374 */       this.m_bestRegression = this.m_currentRegression;
/* 211:    */     }
/* 212:    */   }
/* 213:    */   
/* 214:    */   public String toString()
/* 215:    */   {
/* 216:386 */     if (this.m_ls == null) {
/* 217:387 */       return "model has not been built";
/* 218:    */     }
/* 219:389 */     return this.m_ls.toString();
/* 220:    */   }
/* 221:    */   
/* 222:    */   private void buildWeight()
/* 223:    */     throws Exception
/* 224:    */   {
/* 225:400 */     findResiduals();
/* 226:401 */     this.m_scalefactor = (1.4826D * (1 + 5 / (this.m_Data.numInstances() - this.m_Data.numAttributes())) * Math.sqrt(this.m_bestMedian));
/* 227:    */     
/* 228:    */ 
/* 229:404 */     this.m_weight = new double[this.m_Residuals.length];
/* 230:405 */     for (int i = 0; i < this.m_Residuals.length; i++) {
/* 231:406 */       this.m_weight[i] = (Math.sqrt(this.m_Residuals[i]) / this.m_scalefactor < 2.5D ? 1.0D : 0.0D);
/* 232:    */     }
/* 233:    */   }
/* 234:    */   
/* 235:    */   private void buildRLSRegression()
/* 236:    */     throws Exception
/* 237:    */   {
/* 238:418 */     buildWeight();
/* 239:419 */     this.m_RLSData = new Instances(this.m_Data);
/* 240:420 */     int x = 0;
/* 241:421 */     int y = 0;
/* 242:422 */     int n = this.m_RLSData.numInstances();
/* 243:423 */     while (y < n)
/* 244:    */     {
/* 245:424 */       if (this.m_weight[x] == 0.0D)
/* 246:    */       {
/* 247:425 */         this.m_RLSData.delete(y);
/* 248:426 */         n = this.m_RLSData.numInstances();
/* 249:427 */         y--;
/* 250:    */       }
/* 251:429 */       x++;
/* 252:430 */       y++;
/* 253:    */     }
/* 254:432 */     if (this.m_RLSData.numInstances() == 0)
/* 255:    */     {
/* 256:433 */       System.err.println("rls regression unbuilt");
/* 257:434 */       this.m_ls = this.m_currentRegression;
/* 258:    */     }
/* 259:    */     else
/* 260:    */     {
/* 261:436 */       this.m_ls = new LinearRegression();
/* 262:    */       
/* 263:438 */       this.m_ls.setAttributeSelectionMethod(this.m_tag);
/* 264:439 */       this.m_ls.buildClassifier(this.m_RLSData);
/* 265:440 */       this.m_currentRegression = this.m_ls;
/* 266:    */     }
/* 267:    */   }
/* 268:    */   
/* 269:    */   private static void select(double[] a, int l, int r, int k)
/* 270:    */   {
/* 271:455 */     if (r <= l) {
/* 272:456 */       return;
/* 273:    */     }
/* 274:458 */     int i = partition(a, l, r);
/* 275:459 */     if (i > k) {
/* 276:460 */       select(a, l, i - 1, k);
/* 277:    */     }
/* 278:462 */     if (i < k) {
/* 279:463 */       select(a, i + 1, r, k);
/* 280:    */     }
/* 281:    */   }
/* 282:    */   
/* 283:    */   private static int partition(double[] a, int l, int r)
/* 284:    */   {
/* 285:479 */     int i = l - 1;int j = r;
/* 286:480 */     double v = a[r];
/* 287:    */     for (;;)
/* 288:    */     {
/* 289:482 */       if (a[(++i)] >= v)
/* 290:    */       {
/* 291:485 */         while (v < a[(--j)]) {
/* 292:486 */           if (j == l) {
/* 293:    */             break;
/* 294:    */           }
/* 295:    */         }
/* 296:490 */         if (i >= j) {
/* 297:    */           break;
/* 298:    */         }
/* 299:493 */         double temp = a[i];
/* 300:494 */         a[i] = a[j];
/* 301:495 */         a[j] = temp;
/* 302:    */       }
/* 303:    */     }
/* 304:497 */     double temp = a[i];
/* 305:498 */     a[i] = a[r];
/* 306:499 */     a[r] = temp;
/* 307:500 */     return i;
/* 308:    */   }
/* 309:    */   
/* 310:    */   private void selectSubSample(Instances data)
/* 311:    */     throws Exception
/* 312:    */   {
/* 313:511 */     this.m_SplitFilter = new RemoveRange();
/* 314:512 */     this.m_SplitFilter.setInvertSelection(true);
/* 315:513 */     this.m_SubSample = data;
/* 316:514 */     this.m_SplitFilter.setInputFormat(this.m_SubSample);
/* 317:515 */     this.m_SplitFilter.setInstancesIndices(selectIndices(this.m_SubSample));
/* 318:516 */     this.m_SubSample = Filter.useFilter(this.m_SubSample, this.m_SplitFilter);
/* 319:    */   }
/* 320:    */   
/* 321:    */   private String selectIndices(Instances data)
/* 322:    */   {
/* 323:528 */     StringBuffer text = new StringBuffer();
/* 324:529 */     int i = 0;
/* 325:529 */     for (int x = 0; i < this.m_samplesize; i++)
/* 326:    */     {
/* 327:    */       do
/* 328:    */       {
/* 329:531 */         x = (int)(this.m_random.nextDouble() * data.numInstances());
/* 330:532 */       } while (x == 0);
/* 331:533 */       text.append(Integer.toString(x));
/* 332:534 */       if (i < this.m_samplesize - 1) {
/* 333:535 */         text.append(",");
/* 334:    */       } else {
/* 335:537 */         text.append("\n");
/* 336:    */       }
/* 337:    */     }
/* 338:540 */     return text.toString();
/* 339:    */   }
/* 340:    */   
/* 341:    */   public String sampleSizeTipText()
/* 342:    */   {
/* 343:550 */     return "Set the size of the random samples used to generate the least sqaured regression functions.";
/* 344:    */   }
/* 345:    */   
/* 346:    */   public void setSampleSize(int samplesize)
/* 347:    */   {
/* 348:561 */     this.m_samplesize = samplesize;
/* 349:    */   }
/* 350:    */   
/* 351:    */   public int getSampleSize()
/* 352:    */   {
/* 353:571 */     return this.m_samplesize;
/* 354:    */   }
/* 355:    */   
/* 356:    */   public String randomSeedTipText()
/* 357:    */   {
/* 358:581 */     return "Set the seed for selecting random subsamples of the training data.";
/* 359:    */   }
/* 360:    */   
/* 361:    */   public void setRandomSeed(long randomseed)
/* 362:    */   {
/* 363:591 */     this.m_randomseed = randomseed;
/* 364:    */   }
/* 365:    */   
/* 366:    */   public long getRandomSeed()
/* 367:    */   {
/* 368:601 */     return this.m_randomseed;
/* 369:    */   }
/* 370:    */   
/* 371:    */   public Enumeration<Option> listOptions()
/* 372:    */   {
/* 373:612 */     Vector<Option> newVector = new Vector(4);
/* 374:613 */     newVector.addElement(new Option("\tSet sample size\n\t(default: 4)\n", "S", 4, "-S <sample size>"));
/* 375:    */     
/* 376:615 */     newVector.addElement(new Option("\tSet the seed used to generate samples\n\t(default: 0)\n", "G", 0, "-G <seed>"));
/* 377:    */     
/* 378:    */ 
/* 379:618 */     newVector.addAll(Collections.list(super.listOptions()));
/* 380:    */     
/* 381:620 */     return newVector.elements();
/* 382:    */   }
/* 383:    */   
/* 384:    */   public void setOptions(String[] options)
/* 385:    */     throws Exception
/* 386:    */   {
/* 387:657 */     String curropt = Utils.getOption('S', options);
/* 388:658 */     if (curropt.length() != 0) {
/* 389:659 */       setSampleSize(Integer.parseInt(curropt));
/* 390:    */     } else {
/* 391:661 */       setSampleSize(4);
/* 392:    */     }
/* 393:664 */     curropt = Utils.getOption('G', options);
/* 394:665 */     if (curropt.length() != 0) {
/* 395:666 */       setRandomSeed(Long.parseLong(curropt));
/* 396:    */     } else {
/* 397:668 */       setRandomSeed(0L);
/* 398:    */     }
/* 399:671 */     super.setOptions(options);
/* 400:    */     
/* 401:673 */     Utils.checkForRemainingOptions(options);
/* 402:    */   }
/* 403:    */   
/* 404:    */   public String[] getOptions()
/* 405:    */   {
/* 406:684 */     Vector<String> options = new Vector();
/* 407:    */     
/* 408:686 */     options.add("-S");
/* 409:687 */     options.add("" + getSampleSize());
/* 410:    */     
/* 411:689 */     options.add("-G");
/* 412:690 */     options.add("" + getRandomSeed());
/* 413:    */     
/* 414:692 */     Collections.addAll(options, super.getOptions());
/* 415:    */     
/* 416:694 */     return (String[])options.toArray(new String[0]);
/* 417:    */   }
/* 418:    */   
/* 419:    */   public static int combinations(int n, int r)
/* 420:    */     throws Exception
/* 421:    */   {
/* 422:708 */     int c = 1;int denom = 1;int num = 1;int orig = r;
/* 423:709 */     if (r > n) {
/* 424:710 */       throw new Exception("r must be less that or equal to n.");
/* 425:    */     }
/* 426:712 */     r = Math.min(r, n - r);
/* 427:714 */     for (int i = 1; i <= r; i++)
/* 428:    */     {
/* 429:716 */       num *= (n - i + 1);
/* 430:717 */       denom *= i;
/* 431:    */     }
/* 432:720 */     c = num / denom;
/* 433:    */     
/* 434:    */ 
/* 435:    */ 
/* 436:    */ 
/* 437:725 */     return c;
/* 438:    */   }
/* 439:    */   
/* 440:    */   public String getRevision()
/* 441:    */   {
/* 442:735 */     return RevisionUtils.extract("$Revision: 11720 $");
/* 443:    */   }
/* 444:    */   
/* 445:    */   public static void main(String[] argv)
/* 446:    */   {
/* 447:744 */     runClassifier(new LeastMedSq(), argv);
/* 448:    */   }
/* 449:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.LeastMedSq
 * JD-Core Version:    0.7.0.1
 */