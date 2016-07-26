/*   1:    */ package weka.classifiers.lazy;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.Classifier;
/*   8:    */ import weka.classifiers.SingleClassifierEnhancer;
/*   9:    */ import weka.classifiers.UpdateableClassifier;
/*  10:    */ import weka.classifiers.rules.ZeroR;
/*  11:    */ import weka.classifiers.trees.DecisionStump;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Capabilities.Capability;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.Option;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.TechnicalInformation;
/*  19:    */ import weka.core.TechnicalInformation.Field;
/*  20:    */ import weka.core.TechnicalInformation.Type;
/*  21:    */ import weka.core.TechnicalInformationHandler;
/*  22:    */ import weka.core.Utils;
/*  23:    */ import weka.core.WeightedInstancesHandler;
/*  24:    */ import weka.core.neighboursearch.LinearNNSearch;
/*  25:    */ import weka.core.neighboursearch.NearestNeighbourSearch;
/*  26:    */ 
/*  27:    */ public class LWL
/*  28:    */   extends SingleClassifierEnhancer
/*  29:    */   implements UpdateableClassifier, WeightedInstancesHandler, TechnicalInformationHandler
/*  30:    */ {
/*  31:    */   static final long serialVersionUID = 1979797405383665815L;
/*  32:    */   protected Instances m_Train;
/*  33:132 */   protected int m_kNN = -1;
/*  34:135 */   protected int m_WeightKernel = 0;
/*  35:138 */   protected boolean m_UseAllK = true;
/*  36:143 */   protected NearestNeighbourSearch m_NNSearch = new LinearNNSearch();
/*  37:    */   public static final int LINEAR = 0;
/*  38:    */   public static final int EPANECHNIKOV = 1;
/*  39:    */   public static final int TRICUBE = 2;
/*  40:    */   public static final int INVERSE = 3;
/*  41:    */   public static final int GAUSS = 4;
/*  42:    */   public static final int CONSTANT = 5;
/*  43:    */   protected Classifier m_ZeroR;
/*  44:    */   
/*  45:    */   public String globalInfo()
/*  46:    */   {
/*  47:162 */     return "Locally weighted learning. Uses an instance-based algorithm to assign instance weights which are then used by a specified WeightedInstancesHandler.\nCan do classification (e.g. using naive Bayes) or regression (e.g. using linear regression).\n\nFor more info, see\n\n" + getTechnicalInformation().toString();
/*  48:    */   }
/*  49:    */   
/*  50:    */   public TechnicalInformation getTechnicalInformation()
/*  51:    */   {
/*  52:183 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  53:184 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank and Mark Hall and Bernhard Pfahringer");
/*  54:185 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/*  55:186 */     result.setValue(TechnicalInformation.Field.TITLE, "Locally Weighted Naive Bayes");
/*  56:187 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "19th Conference in Uncertainty in Artificial Intelligence");
/*  57:188 */     result.setValue(TechnicalInformation.Field.PAGES, "249-256");
/*  58:189 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
/*  59:    */     
/*  60:191 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.ARTICLE);
/*  61:192 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "C. Atkeson and A. Moore and S. Schaal");
/*  62:193 */     additional.setValue(TechnicalInformation.Field.YEAR, "1996");
/*  63:194 */     additional.setValue(TechnicalInformation.Field.TITLE, "Locally weighted learning");
/*  64:195 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "AI Review");
/*  65:    */     
/*  66:197 */     return result;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public LWL()
/*  70:    */   {
/*  71:204 */     this.m_Classifier = new DecisionStump();
/*  72:    */   }
/*  73:    */   
/*  74:    */   protected String defaultClassifierString()
/*  75:    */   {
/*  76:214 */     return "weka.classifiers.trees.DecisionStump";
/*  77:    */   }
/*  78:    */   
/*  79:    */   public Enumeration<String> enumerateMeasures()
/*  80:    */   {
/*  81:223 */     return this.m_NNSearch.enumerateMeasures();
/*  82:    */   }
/*  83:    */   
/*  84:    */   public double getMeasure(String additionalMeasureName)
/*  85:    */   {
/*  86:234 */     return this.m_NNSearch.getMeasure(additionalMeasureName);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public Enumeration<Option> listOptions()
/*  90:    */   {
/*  91:244 */     Vector<Option> newVector = new Vector(3);
/*  92:245 */     newVector.addElement(new Option("\tThe nearest neighbour search algorithm to use (default: weka.core.neighboursearch.LinearNNSearch).\n", "A", 0, "-A"));
/*  93:    */     
/*  94:    */ 
/*  95:    */ 
/*  96:249 */     newVector.addElement(new Option("\tSet the number of neighbours used to set the kernel bandwidth.\n\t(default all)", "K", 1, "-K <number of neighbours>"));
/*  97:    */     
/*  98:    */ 
/*  99:    */ 
/* 100:253 */     newVector.addElement(new Option("\tSet the weighting kernel shape to use. 0=Linear, 1=Epanechnikov,\n\t2=Tricube, 3=Inverse, 4=Gaussian.\n\t(default 0 = Linear)", "U", 1, "-U <number of weighting method>"));
/* 101:    */     
/* 102:    */ 
/* 103:    */ 
/* 104:    */ 
/* 105:    */ 
/* 106:259 */     newVector.addAll(Collections.list(super.listOptions()));
/* 107:    */     
/* 108:261 */     return newVector.elements();
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void setOptions(String[] options)
/* 112:    */     throws Exception
/* 113:    */   {
/* 114:306 */     String knnString = Utils.getOption('K', options);
/* 115:307 */     if (knnString.length() != 0) {
/* 116:308 */       setKNN(Integer.parseInt(knnString));
/* 117:    */     } else {
/* 118:310 */       setKNN(-1);
/* 119:    */     }
/* 120:313 */     String weightString = Utils.getOption('U', options);
/* 121:314 */     if (weightString.length() != 0) {
/* 122:315 */       setWeightingKernel(Integer.parseInt(weightString));
/* 123:    */     } else {
/* 124:317 */       setWeightingKernel(0);
/* 125:    */     }
/* 126:320 */     String nnSearchClass = Utils.getOption('A', options);
/* 127:321 */     if (nnSearchClass.length() != 0)
/* 128:    */     {
/* 129:322 */       String[] nnSearchClassSpec = Utils.splitOptions(nnSearchClass);
/* 130:323 */       if (nnSearchClassSpec.length == 0) {
/* 131:324 */         throw new Exception("Invalid NearestNeighbourSearch algorithm specification string.");
/* 132:    */       }
/* 133:327 */       String className = nnSearchClassSpec[0];
/* 134:328 */       nnSearchClassSpec[0] = "";
/* 135:    */       
/* 136:330 */       setNearestNeighbourSearchAlgorithm((NearestNeighbourSearch)Utils.forName(NearestNeighbourSearch.class, className, nnSearchClassSpec));
/* 137:    */     }
/* 138:    */     else
/* 139:    */     {
/* 140:337 */       setNearestNeighbourSearchAlgorithm(new LinearNNSearch());
/* 141:    */     }
/* 142:339 */     super.setOptions(options);
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String[] getOptions()
/* 146:    */   {
/* 147:349 */     Vector<String> options = new Vector();
/* 148:    */     
/* 149:351 */     options.add("-U");options.add("" + getWeightingKernel());
/* 150:352 */     if ((getKNN() == 0) && (this.m_UseAllK))
/* 151:    */     {
/* 152:353 */       options.add("-K");options.add("-1");
/* 153:    */     }
/* 154:    */     else
/* 155:    */     {
/* 156:356 */       options.add("-K");options.add("" + getKNN());
/* 157:    */     }
/* 158:358 */     options.add("-A");
/* 159:359 */     options.add(this.m_NNSearch.getClass().getName() + " " + Utils.joinOptions(this.m_NNSearch.getOptions()));
/* 160:    */     
/* 161:361 */     Collections.addAll(options, super.getOptions());
/* 162:    */     
/* 163:363 */     return (String[])options.toArray(new String[0]);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public String KNNTipText()
/* 167:    */   {
/* 168:372 */     return "How many neighbours are used to determine the width of the weighting function (<= 0 means all neighbours).";
/* 169:    */   }
/* 170:    */   
/* 171:    */   public void setKNN(int knn)
/* 172:    */   {
/* 173:385 */     this.m_kNN = knn;
/* 174:386 */     if (knn <= 0)
/* 175:    */     {
/* 176:387 */       this.m_kNN = 0;
/* 177:388 */       this.m_UseAllK = true;
/* 178:    */     }
/* 179:    */     else
/* 180:    */     {
/* 181:390 */       this.m_UseAllK = false;
/* 182:    */     }
/* 183:    */   }
/* 184:    */   
/* 185:    */   public int getKNN()
/* 186:    */   {
/* 187:403 */     return this.m_kNN;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String weightingKernelTipText()
/* 191:    */   {
/* 192:412 */     return "Determines weighting function. [0 = Linear, 1 = Epnechnikov,2 = Tricube, 3 = Inverse, 4 = Gaussian and 5 = Constant. (default 0 = Linear)].";
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void setWeightingKernel(int kernel)
/* 196:    */   {
/* 197:427 */     if ((kernel != 0) && (kernel != 1) && (kernel != 2) && (kernel != 3) && (kernel != 4) && (kernel != 5)) {
/* 198:433 */       return;
/* 199:    */     }
/* 200:435 */     this.m_WeightKernel = kernel;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public int getWeightingKernel()
/* 204:    */   {
/* 205:446 */     return this.m_WeightKernel;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String nearestNeighbourSearchAlgorithmTipText()
/* 209:    */   {
/* 210:455 */     return "The nearest neighbour search algorithm to use (Default: LinearNN).";
/* 211:    */   }
/* 212:    */   
/* 213:    */   public NearestNeighbourSearch getNearestNeighbourSearchAlgorithm()
/* 214:    */   {
/* 215:463 */     return this.m_NNSearch;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public void setNearestNeighbourSearchAlgorithm(NearestNeighbourSearch nearestNeighbourSearchAlgorithm)
/* 219:    */   {
/* 220:472 */     this.m_NNSearch = nearestNeighbourSearchAlgorithm;
/* 221:    */   }
/* 222:    */   
/* 223:    */   public Capabilities getCapabilities()
/* 224:    */   {
/* 225:    */     Capabilities result;
/* 226:    */     Capabilities result;
/* 227:483 */     if (this.m_Classifier != null) {
/* 228:484 */       result = this.m_Classifier.getCapabilities();
/* 229:    */     } else {
/* 230:486 */       result = super.getCapabilities();
/* 231:    */     }
/* 232:489 */     result.setMinimumNumberInstances(0);
/* 233:492 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/* 234:493 */       result.enableDependency(cap);
/* 235:    */     }
/* 236:495 */     return result;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public void buildClassifier(Instances instances)
/* 240:    */     throws Exception
/* 241:    */   {
/* 242:506 */     if (!(this.m_Classifier instanceof WeightedInstancesHandler)) {
/* 243:507 */       throw new IllegalArgumentException("Classifier must be a WeightedInstancesHandler!");
/* 244:    */     }
/* 245:512 */     getCapabilities().testWithFail(instances);
/* 246:    */     
/* 247:    */ 
/* 248:515 */     instances = new Instances(instances);
/* 249:516 */     instances.deleteWithMissingClass();
/* 250:519 */     if (instances.numAttributes() == 1)
/* 251:    */     {
/* 252:520 */       System.err.println("Cannot build model (only class attribute present in data!), using ZeroR model instead!");
/* 253:    */       
/* 254:    */ 
/* 255:523 */       this.m_ZeroR = new ZeroR();
/* 256:524 */       this.m_ZeroR.buildClassifier(instances);
/* 257:525 */       return;
/* 258:    */     }
/* 259:528 */     this.m_ZeroR = null;
/* 260:    */     
/* 261:    */ 
/* 262:531 */     this.m_Train = new Instances(instances, 0, instances.numInstances());
/* 263:    */     
/* 264:533 */     this.m_NNSearch.setInstances(this.m_Train);
/* 265:    */   }
/* 266:    */   
/* 267:    */   public void updateClassifier(Instance instance)
/* 268:    */     throws Exception
/* 269:    */   {
/* 270:545 */     if (this.m_Train == null) {
/* 271:546 */       throw new Exception("No training instance structure set!");
/* 272:    */     }
/* 273:548 */     if (!this.m_Train.equalHeaders(instance.dataset())) {
/* 274:549 */       throw new Exception("Incompatible instance types\n" + this.m_Train.equalHeadersMsg(instance.dataset()));
/* 275:    */     }
/* 276:551 */     if (!instance.classIsMissing())
/* 277:    */     {
/* 278:552 */       this.m_NNSearch.update(instance);
/* 279:553 */       this.m_Train.add(instance);
/* 280:    */     }
/* 281:    */   }
/* 282:    */   
/* 283:    */   public double[] distributionForInstance(Instance instance)
/* 284:    */     throws Exception
/* 285:    */   {
/* 286:567 */     if (this.m_ZeroR != null) {
/* 287:568 */       return this.m_ZeroR.distributionForInstance(instance);
/* 288:    */     }
/* 289:571 */     if (this.m_Train.numInstances() == 0) {
/* 290:572 */       throw new Exception("No training instances!");
/* 291:    */     }
/* 292:575 */     this.m_NNSearch.addInstanceInfo(instance);
/* 293:    */     
/* 294:577 */     int k = this.m_Train.numInstances();
/* 295:578 */     if ((!this.m_UseAllK) && (this.m_kNN < k)) {
/* 296:581 */       k = this.m_kNN;
/* 297:    */     }
/* 298:584 */     Instances neighbours = this.m_NNSearch.kNearestNeighbours(instance, k);
/* 299:585 */     double[] distances = this.m_NNSearch.getDistances();
/* 300:587 */     if (this.m_Debug)
/* 301:    */     {
/* 302:588 */       System.out.println("Test Instance: " + instance);
/* 303:589 */       System.out.println("For " + k + " kept " + neighbours.numInstances() + " out of " + this.m_Train.numInstances() + " instances.");
/* 304:    */     }
/* 305:594 */     if (k > distances.length) {
/* 306:595 */       k = distances.length;
/* 307:    */     }
/* 308:597 */     if (this.m_Debug)
/* 309:    */     {
/* 310:598 */       System.out.println("Instance Distances");
/* 311:599 */       for (int i = 0; i < distances.length; i++) {
/* 312:600 */         System.out.println("" + distances[i]);
/* 313:    */       }
/* 314:    */     }
/* 315:605 */     double bandwidth = distances[(k - 1)];
/* 316:608 */     if (bandwidth <= 0.0D) {
/* 317:610 */       for (int i = 0; i < distances.length; i++) {
/* 318:611 */         distances[i] = 1.0D;
/* 319:    */       }
/* 320:    */     } else {
/* 321:614 */       for (int i = 0; i < distances.length; i++) {
/* 322:615 */         distances[i] /= bandwidth;
/* 323:    */       }
/* 324:    */     }
/* 325:619 */     for (int i = 0; i < distances.length; i++) {
/* 326:620 */       switch (this.m_WeightKernel)
/* 327:    */       {
/* 328:    */       case 0: 
/* 329:622 */         distances[i] = (1.0001D - distances[i]);
/* 330:623 */         break;
/* 331:    */       case 1: 
/* 332:625 */         distances[i] = (0.75D * (1.0001D - distances[i] * distances[i]));
/* 333:626 */         break;
/* 334:    */       case 2: 
/* 335:628 */         distances[i] = Math.pow(1.0001D - Math.pow(distances[i], 3.0D), 3.0D);
/* 336:629 */         break;
/* 337:    */       case 5: 
/* 338:632 */         distances[i] = 1.0D;
/* 339:633 */         break;
/* 340:    */       case 3: 
/* 341:635 */         distances[i] = (1.0D / (1.0D + distances[i]));
/* 342:636 */         break;
/* 343:    */       case 4: 
/* 344:638 */         distances[i] = Math.exp(-distances[i] * distances[i]);
/* 345:    */       }
/* 346:    */     }
/* 347:643 */     if (this.m_Debug)
/* 348:    */     {
/* 349:644 */       System.out.println("Instance Weights");
/* 350:645 */       for (int i = 0; i < distances.length; i++) {
/* 351:646 */         System.out.println("" + distances[i]);
/* 352:    */       }
/* 353:    */     }
/* 354:651 */     double sumOfWeights = 0.0D;double newSumOfWeights = 0.0D;
/* 355:652 */     for (int i = 0; i < distances.length; i++)
/* 356:    */     {
/* 357:653 */       double weight = distances[i];
/* 358:654 */       Instance inst = neighbours.instance(i);
/* 359:655 */       sumOfWeights += inst.weight();
/* 360:656 */       newSumOfWeights += inst.weight() * weight;
/* 361:657 */       inst.setWeight(inst.weight() * weight);
/* 362:    */     }
/* 363:662 */     for (int i = 0; i < neighbours.numInstances(); i++)
/* 364:    */     {
/* 365:663 */       Instance inst = neighbours.instance(i);
/* 366:664 */       inst.setWeight(inst.weight() * sumOfWeights / newSumOfWeights);
/* 367:    */     }
/* 368:668 */     this.m_Classifier.buildClassifier(neighbours);
/* 369:670 */     if (this.m_Debug)
/* 370:    */     {
/* 371:671 */       System.out.println("Classifying test instance: " + instance);
/* 372:672 */       System.out.println("Built base classifier:\n" + this.m_Classifier.toString());
/* 373:    */     }
/* 374:677 */     return this.m_Classifier.distributionForInstance(instance);
/* 375:    */   }
/* 376:    */   
/* 377:    */   public String toString()
/* 378:    */   {
/* 379:688 */     if (this.m_ZeroR != null)
/* 380:    */     {
/* 381:689 */       StringBuffer buf = new StringBuffer();
/* 382:690 */       buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/* 383:691 */       buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/* 384:692 */       buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
/* 385:693 */       buf.append(this.m_ZeroR.toString());
/* 386:694 */       return buf.toString();
/* 387:    */     }
/* 388:697 */     if (this.m_Train == null) {
/* 389:698 */       return "Locally weighted learning: No model built yet.";
/* 390:    */     }
/* 391:700 */     String result = "Locally weighted learning\n===========================\n";
/* 392:    */     
/* 393:    */ 
/* 394:703 */     result = result + "Using classifier: " + this.m_Classifier.getClass().getName() + "\n";
/* 395:705 */     switch (this.m_WeightKernel)
/* 396:    */     {
/* 397:    */     case 0: 
/* 398:707 */       result = result + "Using linear weighting kernels\n";
/* 399:708 */       break;
/* 400:    */     case 1: 
/* 401:710 */       result = result + "Using epanechnikov weighting kernels\n";
/* 402:711 */       break;
/* 403:    */     case 2: 
/* 404:713 */       result = result + "Using tricube weighting kernels\n";
/* 405:714 */       break;
/* 406:    */     case 3: 
/* 407:716 */       result = result + "Using inverse-distance weighting kernels\n";
/* 408:717 */       break;
/* 409:    */     case 4: 
/* 410:719 */       result = result + "Using gaussian weighting kernels\n";
/* 411:720 */       break;
/* 412:    */     case 5: 
/* 413:722 */       result = result + "Using constant weighting kernels\n";
/* 414:    */     }
/* 415:725 */     result = result + "Using " + (this.m_UseAllK ? "all" : new StringBuilder().append("").append(this.m_kNN).toString()) + " neighbours";
/* 416:726 */     return result;
/* 417:    */   }
/* 418:    */   
/* 419:    */   public String getRevision()
/* 420:    */   {
/* 421:735 */     return RevisionUtils.extract("$Revision: 10141 $");
/* 422:    */   }
/* 423:    */   
/* 424:    */   public static void main(String[] argv)
/* 425:    */   {
/* 426:744 */     runClassifier(new LWL(), argv);
/* 427:    */   }
/* 428:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.lazy.LWL
 * JD-Core Version:    0.7.0.1
 */