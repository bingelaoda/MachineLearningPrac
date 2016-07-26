/*   1:    */ package weka.classifiers;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.FileReader;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.Reader;
/*   7:    */ import java.util.Collections;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.Random;
/*  10:    */ import java.util.Vector;
/*  11:    */ import weka.classifiers.rules.ZeroR;
/*  12:    */ import weka.core.Attribute;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.RevisionHandler;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.TechnicalInformation;
/*  20:    */ import weka.core.TechnicalInformation.Field;
/*  21:    */ import weka.core.TechnicalInformation.Type;
/*  22:    */ import weka.core.TechnicalInformationHandler;
/*  23:    */ import weka.core.Utils;
/*  24:    */ 
/*  25:    */ public class BVDecompose
/*  26:    */   implements OptionHandler, TechnicalInformationHandler, RevisionHandler
/*  27:    */ {
/*  28:    */   protected boolean m_Debug;
/*  29:120 */   protected Classifier m_Classifier = new ZeroR();
/*  30:    */   protected String[] m_ClassifierOptions;
/*  31:126 */   protected int m_TrainIterations = 50;
/*  32:    */   protected String m_DataFileName;
/*  33:132 */   protected int m_ClassIndex = -1;
/*  34:135 */   protected int m_Seed = 1;
/*  35:    */   protected double m_Bias;
/*  36:    */   protected double m_Variance;
/*  37:    */   protected double m_Sigma;
/*  38:    */   protected double m_Error;
/*  39:150 */   protected int m_TrainPoolSize = 100;
/*  40:    */   
/*  41:    */   public String globalInfo()
/*  42:    */   {
/*  43:159 */     return "Class for performing a Bias-Variance decomposition on any classifier using the method specified in:\n\n" + getTechnicalInformation().toString();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public TechnicalInformation getTechnicalInformation()
/*  47:    */   {
/*  48:175 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  49:176 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Ron Kohavi and David H. Wolpert");
/*  50:177 */     result.setValue(TechnicalInformation.Field.YEAR, "1996");
/*  51:178 */     result.setValue(TechnicalInformation.Field.TITLE, "Bias Plus Variance Decomposition for Zero-One Loss Functions");
/*  52:179 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Machine Learning: Proceedings of the Thirteenth International Conference");
/*  53:180 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
/*  54:181 */     result.setValue(TechnicalInformation.Field.EDITOR, "Lorenza Saitta");
/*  55:182 */     result.setValue(TechnicalInformation.Field.PAGES, "275-283");
/*  56:183 */     result.setValue(TechnicalInformation.Field.PS, "http://robotics.stanford.edu/~ronnyk/biasVar.ps");
/*  57:    */     
/*  58:185 */     return result;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public Enumeration<Option> listOptions()
/*  62:    */   {
/*  63:195 */     Vector<Option> newVector = new Vector(7);
/*  64:    */     
/*  65:197 */     newVector.addElement(new Option("\tThe index of the class attribute.\n\t(default last)", "c", 1, "-c <class index>"));
/*  66:    */     
/*  67:    */ 
/*  68:    */ 
/*  69:201 */     newVector.addElement(new Option("\tThe name of the arff file used for the decomposition.", "t", 1, "-t <name of arff file>"));
/*  70:    */     
/*  71:    */ 
/*  72:204 */     newVector.addElement(new Option("\tThe number of instances placed in the training pool.\n\tThe remainder will be used for testing. (default 100)", "T", 1, "-T <training pool size>"));
/*  73:    */     
/*  74:    */ 
/*  75:    */ 
/*  76:208 */     newVector.addElement(new Option("\tThe random number seed used.", "s", 1, "-s <seed>"));
/*  77:    */     
/*  78:    */ 
/*  79:211 */     newVector.addElement(new Option("\tThe number of training repetitions used.\n\t(default 50)", "x", 1, "-x <num>"));
/*  80:    */     
/*  81:    */ 
/*  82:    */ 
/*  83:215 */     newVector.addElement(new Option("\tTurn on debugging output.", "D", 0, "-D"));
/*  84:    */     
/*  85:    */ 
/*  86:218 */     newVector.addElement(new Option("\tFull class name of the learner used in the decomposition.\n\teg: weka.classifiers.bayes.NaiveBayes", "W", 1, "-W <classifier class name>"));
/*  87:223 */     if ((this.m_Classifier != null) && ((this.m_Classifier instanceof OptionHandler)))
/*  88:    */     {
/*  89:225 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to learner " + this.m_Classifier.getClass().getName() + ":"));
/*  90:    */       
/*  91:    */ 
/*  92:    */ 
/*  93:    */ 
/*  94:230 */       newVector.addAll(Collections.list(((OptionHandler)this.m_Classifier).listOptions()));
/*  95:    */     }
/*  96:232 */     return newVector.elements();
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setOptions(String[] options)
/* 100:    */     throws Exception
/* 101:    */   {
/* 102:283 */     setDebug(Utils.getFlag('D', options));
/* 103:    */     
/* 104:285 */     String classIndex = Utils.getOption('c', options);
/* 105:286 */     if (classIndex.length() != 0)
/* 106:    */     {
/* 107:287 */       if (classIndex.toLowerCase().equals("last")) {
/* 108:288 */         setClassIndex(0);
/* 109:289 */       } else if (classIndex.toLowerCase().equals("first")) {
/* 110:290 */         setClassIndex(1);
/* 111:    */       } else {
/* 112:292 */         setClassIndex(Integer.parseInt(classIndex));
/* 113:    */       }
/* 114:    */     }
/* 115:    */     else {
/* 116:295 */       setClassIndex(0);
/* 117:    */     }
/* 118:298 */     String trainIterations = Utils.getOption('x', options);
/* 119:299 */     if (trainIterations.length() != 0) {
/* 120:300 */       setTrainIterations(Integer.parseInt(trainIterations));
/* 121:    */     } else {
/* 122:302 */       setTrainIterations(50);
/* 123:    */     }
/* 124:305 */     String trainPoolSize = Utils.getOption('T', options);
/* 125:306 */     if (trainPoolSize.length() != 0) {
/* 126:307 */       setTrainPoolSize(Integer.parseInt(trainPoolSize));
/* 127:    */     } else {
/* 128:309 */       setTrainPoolSize(100);
/* 129:    */     }
/* 130:312 */     String seedString = Utils.getOption('s', options);
/* 131:313 */     if (seedString.length() != 0) {
/* 132:314 */       setSeed(Integer.parseInt(seedString));
/* 133:    */     } else {
/* 134:316 */       setSeed(1);
/* 135:    */     }
/* 136:319 */     String dataFile = Utils.getOption('t', options);
/* 137:320 */     if (dataFile.length() == 0) {
/* 138:321 */       throw new Exception("An arff file must be specified with the -t option.");
/* 139:    */     }
/* 140:324 */     setDataFileName(dataFile);
/* 141:    */     
/* 142:326 */     String classifierName = Utils.getOption('W', options);
/* 143:327 */     if (classifierName.length() == 0) {
/* 144:328 */       throw new Exception("A learner must be specified with the -W option.");
/* 145:    */     }
/* 146:330 */     setClassifier(AbstractClassifier.forName(classifierName, Utils.partitionOptions(options)));
/* 147:    */   }
/* 148:    */   
/* 149:    */   public String[] getOptions()
/* 150:    */   {
/* 151:341 */     String[] classifierOptions = new String[0];
/* 152:342 */     if ((this.m_Classifier != null) && ((this.m_Classifier instanceof OptionHandler))) {
/* 153:344 */       classifierOptions = ((OptionHandler)this.m_Classifier).getOptions();
/* 154:    */     }
/* 155:346 */     String[] options = new String[classifierOptions.length + 14];
/* 156:347 */     int current = 0;
/* 157:348 */     if (getDebug()) {
/* 158:349 */       options[(current++)] = "-D";
/* 159:    */     }
/* 160:351 */     options[(current++)] = "-c";options[(current++)] = ("" + getClassIndex());
/* 161:352 */     options[(current++)] = "-x";options[(current++)] = ("" + getTrainIterations());
/* 162:353 */     options[(current++)] = "-T";options[(current++)] = ("" + getTrainPoolSize());
/* 163:354 */     options[(current++)] = "-s";options[(current++)] = ("" + getSeed());
/* 164:355 */     if (getDataFileName() != null)
/* 165:    */     {
/* 166:356 */       options[(current++)] = "-t";options[(current++)] = ("" + getDataFileName());
/* 167:    */     }
/* 168:358 */     if (getClassifier() != null)
/* 169:    */     {
/* 170:359 */       options[(current++)] = "-W";
/* 171:360 */       options[(current++)] = getClassifier().getClass().getName();
/* 172:    */     }
/* 173:362 */     options[(current++)] = "--";
/* 174:363 */     System.arraycopy(classifierOptions, 0, options, current, classifierOptions.length);
/* 175:    */     
/* 176:365 */     current += classifierOptions.length;
/* 177:366 */     while (current < options.length) {
/* 178:367 */       options[(current++)] = "";
/* 179:    */     }
/* 180:369 */     return options;
/* 181:    */   }
/* 182:    */   
/* 183:    */   public int getTrainPoolSize()
/* 184:    */   {
/* 185:379 */     return this.m_TrainPoolSize;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public void setTrainPoolSize(int numTrain)
/* 189:    */   {
/* 190:389 */     this.m_TrainPoolSize = numTrain;
/* 191:    */   }
/* 192:    */   
/* 193:    */   public void setClassifier(Classifier newClassifier)
/* 194:    */   {
/* 195:399 */     this.m_Classifier = newClassifier;
/* 196:    */   }
/* 197:    */   
/* 198:    */   public Classifier getClassifier()
/* 199:    */   {
/* 200:409 */     return this.m_Classifier;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public void setDebug(boolean debug)
/* 204:    */   {
/* 205:419 */     this.m_Debug = debug;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public boolean getDebug()
/* 209:    */   {
/* 210:429 */     return this.m_Debug;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public void setSeed(int seed)
/* 214:    */   {
/* 215:439 */     this.m_Seed = seed;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public int getSeed()
/* 219:    */   {
/* 220:449 */     return this.m_Seed;
/* 221:    */   }
/* 222:    */   
/* 223:    */   public void setTrainIterations(int trainIterations)
/* 224:    */   {
/* 225:459 */     this.m_TrainIterations = trainIterations;
/* 226:    */   }
/* 227:    */   
/* 228:    */   public int getTrainIterations()
/* 229:    */   {
/* 230:469 */     return this.m_TrainIterations;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public void setDataFileName(String dataFileName)
/* 234:    */   {
/* 235:479 */     this.m_DataFileName = dataFileName;
/* 236:    */   }
/* 237:    */   
/* 238:    */   public String getDataFileName()
/* 239:    */   {
/* 240:489 */     return this.m_DataFileName;
/* 241:    */   }
/* 242:    */   
/* 243:    */   public int getClassIndex()
/* 244:    */   {
/* 245:499 */     return this.m_ClassIndex + 1;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public void setClassIndex(int classIndex)
/* 249:    */   {
/* 250:509 */     this.m_ClassIndex = (classIndex - 1);
/* 251:    */   }
/* 252:    */   
/* 253:    */   public double getBias()
/* 254:    */   {
/* 255:519 */     return this.m_Bias;
/* 256:    */   }
/* 257:    */   
/* 258:    */   public double getVariance()
/* 259:    */   {
/* 260:529 */     return this.m_Variance;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public double getSigma()
/* 264:    */   {
/* 265:539 */     return this.m_Sigma;
/* 266:    */   }
/* 267:    */   
/* 268:    */   public double getError()
/* 269:    */   {
/* 270:549 */     return this.m_Error;
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void decompose()
/* 274:    */     throws Exception
/* 275:    */   {
/* 276:559 */     Reader dataReader = new BufferedReader(new FileReader(this.m_DataFileName));
/* 277:560 */     Instances data = new Instances(dataReader);
/* 278:562 */     if (this.m_ClassIndex < 0) {
/* 279:563 */       data.setClassIndex(data.numAttributes() - 1);
/* 280:    */     } else {
/* 281:565 */       data.setClassIndex(this.m_ClassIndex);
/* 282:    */     }
/* 283:567 */     if (data.classAttribute().type() != 1) {
/* 284:568 */       throw new Exception("Class attribute must be nominal");
/* 285:    */     }
/* 286:570 */     int numClasses = data.numClasses();
/* 287:    */     
/* 288:572 */     data.deleteWithMissingClass();
/* 289:573 */     if (data.checkForStringAttributes()) {
/* 290:574 */       throw new Exception("Can't handle string attributes!");
/* 291:    */     }
/* 292:577 */     if (data.numInstances() < 2 * this.m_TrainPoolSize) {
/* 293:578 */       throw new Exception("The dataset must contain at least " + 2 * this.m_TrainPoolSize + " instances");
/* 294:    */     }
/* 295:581 */     Random random = new Random(this.m_Seed);
/* 296:582 */     data.randomize(random);
/* 297:583 */     Instances trainPool = new Instances(data, 0, this.m_TrainPoolSize);
/* 298:584 */     Instances test = new Instances(data, this.m_TrainPoolSize, data.numInstances() - this.m_TrainPoolSize);
/* 299:    */     
/* 300:586 */     int numTest = test.numInstances();
/* 301:587 */     double[][] instanceProbs = new double[numTest][numClasses];
/* 302:    */     
/* 303:589 */     this.m_Error = 0.0D;
/* 304:590 */     for (int i = 0; i < this.m_TrainIterations; i++)
/* 305:    */     {
/* 306:591 */       if (this.m_Debug) {
/* 307:592 */         System.err.println("Iteration " + (i + 1));
/* 308:    */       }
/* 309:594 */       trainPool.randomize(random);
/* 310:595 */       Instances train = new Instances(trainPool, 0, this.m_TrainPoolSize / 2);
/* 311:    */       
/* 312:597 */       Classifier current = AbstractClassifier.makeCopy(this.m_Classifier);
/* 313:598 */       current.buildClassifier(train);
/* 314:601 */       for (int j = 0; j < numTest; j++)
/* 315:    */       {
/* 316:602 */         int pred = (int)current.classifyInstance(test.instance(j));
/* 317:603 */         if (pred != test.instance(j).classValue()) {
/* 318:604 */           this.m_Error += 1.0D;
/* 319:    */         }
/* 320:606 */         instanceProbs[j][pred] += 1.0D;
/* 321:    */       }
/* 322:    */     }
/* 323:609 */     this.m_Error /= this.m_TrainIterations * numTest;
/* 324:    */     
/* 325:    */ 
/* 326:612 */     this.m_Bias = 0.0D;
/* 327:613 */     this.m_Variance = 0.0D;
/* 328:614 */     this.m_Sigma = 0.0D;
/* 329:615 */     for (int i = 0; i < numTest; i++)
/* 330:    */     {
/* 331:616 */       Instance current = test.instance(i);
/* 332:617 */       double[] predProbs = instanceProbs[i];
/* 333:    */       
/* 334:619 */       double bsum = 0.0D;double vsum = 0.0D;double ssum = 0.0D;
/* 335:620 */       for (int j = 0; j < numClasses; j++)
/* 336:    */       {
/* 337:621 */         double pActual = current.classValue() == j ? 1.0D : 0.0D;
/* 338:622 */         double pPred = predProbs[j] / this.m_TrainIterations;
/* 339:623 */         bsum += (pActual - pPred) * (pActual - pPred) - pPred * (1.0D - pPred) / (this.m_TrainIterations - 1);
/* 340:    */         
/* 341:625 */         vsum += pPred * pPred;
/* 342:626 */         ssum += pActual * pActual;
/* 343:    */       }
/* 344:628 */       this.m_Bias += bsum;
/* 345:629 */       this.m_Variance += 1.0D - vsum;
/* 346:630 */       this.m_Sigma += 1.0D - ssum;
/* 347:    */     }
/* 348:632 */     this.m_Bias /= 2 * numTest;
/* 349:633 */     this.m_Variance /= 2 * numTest;
/* 350:634 */     this.m_Sigma /= 2 * numTest;
/* 351:636 */     if (this.m_Debug) {
/* 352:637 */       System.err.println("Decomposition finished");
/* 353:    */     }
/* 354:    */   }
/* 355:    */   
/* 356:    */   public String toString()
/* 357:    */   {
/* 358:649 */     String result = "\nBias-Variance Decomposition\n";
/* 359:651 */     if (getClassifier() == null) {
/* 360:652 */       return "Invalid setup";
/* 361:    */     }
/* 362:655 */     result = result + "\nClassifier   : " + getClassifier().getClass().getName();
/* 363:656 */     if ((getClassifier() instanceof OptionHandler)) {
/* 364:657 */       result = result + Utils.joinOptions(((OptionHandler)this.m_Classifier).getOptions());
/* 365:    */     }
/* 366:659 */     result = result + "\nData File    : " + getDataFileName();
/* 367:660 */     result = result + "\nClass Index  : ";
/* 368:661 */     if (getClassIndex() == 0) {
/* 369:662 */       result = result + "last";
/* 370:    */     } else {
/* 371:664 */       result = result + getClassIndex();
/* 372:    */     }
/* 373:666 */     result = result + "\nTraining Pool: " + getTrainPoolSize();
/* 374:667 */     result = result + "\nIterations   : " + getTrainIterations();
/* 375:668 */     result = result + "\nSeed         : " + getSeed();
/* 376:669 */     result = result + "\nError        : " + Utils.doubleToString(getError(), 6, 4);
/* 377:670 */     result = result + "\nSigma^2      : " + Utils.doubleToString(getSigma(), 6, 4);
/* 378:671 */     result = result + "\nBias^2       : " + Utils.doubleToString(getBias(), 6, 4);
/* 379:672 */     result = result + "\nVariance     : " + Utils.doubleToString(getVariance(), 6, 4);
/* 380:    */     
/* 381:674 */     return result + "\n";
/* 382:    */   }
/* 383:    */   
/* 384:    */   public String getRevision()
/* 385:    */   {
/* 386:683 */     return RevisionUtils.extract("$Revision: 10141 $");
/* 387:    */   }
/* 388:    */   
/* 389:    */   public static void main(String[] args)
/* 390:    */   {
/* 391:    */     try
/* 392:    */     {
/* 393:694 */       BVDecompose bvd = new BVDecompose();
/* 394:    */       try
/* 395:    */       {
/* 396:697 */         bvd.setOptions(args);
/* 397:698 */         Utils.checkForRemainingOptions(args);
/* 398:    */       }
/* 399:    */       catch (Exception ex)
/* 400:    */       {
/* 401:700 */         String result = ex.getMessage() + "\nBVDecompose Options:\n\n";
/* 402:701 */         Enumeration<Option> enu = bvd.listOptions();
/* 403:702 */         while (enu.hasMoreElements())
/* 404:    */         {
/* 405:703 */           Option option = (Option)enu.nextElement();
/* 406:704 */           result = result + option.synopsis() + "\n" + option.description() + "\n";
/* 407:    */         }
/* 408:706 */         throw new Exception(result);
/* 409:    */       }
/* 410:709 */       bvd.decompose();
/* 411:710 */       System.out.println(bvd.toString());
/* 412:    */     }
/* 413:    */     catch (Exception ex)
/* 414:    */     {
/* 415:712 */       System.err.println(ex.getMessage());
/* 416:    */     }
/* 417:    */   }
/* 418:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.BVDecompose
 * JD-Core Version:    0.7.0.1
 */