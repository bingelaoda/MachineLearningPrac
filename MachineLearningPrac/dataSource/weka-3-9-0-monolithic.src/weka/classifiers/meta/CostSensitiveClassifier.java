/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileReader;
/*   6:    */ import java.io.StringReader;
/*   7:    */ import java.io.StringWriter;
/*   8:    */ import java.util.Collections;
/*   9:    */ import java.util.Enumeration;
/*  10:    */ import java.util.Random;
/*  11:    */ import java.util.Vector;
/*  12:    */ import weka.classifiers.Classifier;
/*  13:    */ import weka.classifiers.CostMatrix;
/*  14:    */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*  15:    */ import weka.classifiers.rules.ZeroR;
/*  16:    */ import weka.core.BatchPredictor;
/*  17:    */ import weka.core.Capabilities;
/*  18:    */ import weka.core.Capabilities.Capability;
/*  19:    */ import weka.core.Drawable;
/*  20:    */ import weka.core.Instance;
/*  21:    */ import weka.core.Instances;
/*  22:    */ import weka.core.Option;
/*  23:    */ import weka.core.OptionHandler;
/*  24:    */ import weka.core.RevisionUtils;
/*  25:    */ import weka.core.SelectedTag;
/*  26:    */ import weka.core.Tag;
/*  27:    */ import weka.core.Utils;
/*  28:    */ import weka.core.WeightedInstancesHandler;
/*  29:    */ 
/*  30:    */ public class CostSensitiveClassifier
/*  31:    */   extends RandomizableSingleClassifierEnhancer
/*  32:    */   implements OptionHandler, Drawable, BatchPredictor
/*  33:    */ {
/*  34:    */   static final long serialVersionUID = -110658209263002404L;
/*  35:    */   public static final int MATRIX_ON_DEMAND = 1;
/*  36:    */   public static final int MATRIX_SUPPLIED = 2;
/*  37:106 */   public static final Tag[] TAGS_MATRIX_SOURCE = { new Tag(1, "Load cost matrix on demand"), new Tag(2, "Use explicit cost matrix") };
/*  38:112 */   protected int m_MatrixSource = 1;
/*  39:118 */   protected File m_OnDemandDirectory = new File(System.getProperty("user.dir"));
/*  40:    */   protected String m_CostFile;
/*  41:124 */   protected CostMatrix m_CostMatrix = new CostMatrix(1);
/*  42:    */   protected boolean m_MinimizeExpectedCost;
/*  43:    */   
/*  44:    */   protected String defaultClassifierString()
/*  45:    */   {
/*  46:139 */     return "weka.classifiers.rules.ZeroR";
/*  47:    */   }
/*  48:    */   
/*  49:    */   public CostSensitiveClassifier()
/*  50:    */   {
/*  51:146 */     this.m_Classifier = new ZeroR();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public Enumeration<Option> listOptions()
/*  55:    */   {
/*  56:156 */     Vector<Option> newVector = new Vector(4);
/*  57:    */     
/*  58:158 */     newVector.addElement(new Option("\tMinimize expected misclassification cost. Default is to\n\treweight training instances according to costs per class", "M", 0, "-M"));
/*  59:    */     
/*  60:    */ 
/*  61:    */ 
/*  62:162 */     newVector.addElement(new Option("\tFile name of a cost matrix to use. If this is not supplied,\n\ta cost matrix will be loaded on demand. The name of the\n\ton-demand file is the relation name of the training data\n\tplus \".cost\", and the path to the on-demand file is\n\tspecified with the -N option.", "C", 1, "-C <cost file name>"));
/*  63:    */     
/*  64:    */ 
/*  65:    */ 
/*  66:    */ 
/*  67:    */ 
/*  68:    */ 
/*  69:169 */     newVector.addElement(new Option("\tName of a directory to search for cost files when loading\n\tcosts on demand (default current directory).", "N", 1, "-N <directory>"));
/*  70:    */     
/*  71:    */ 
/*  72:    */ 
/*  73:173 */     newVector.addElement(new Option("\tThe cost matrix in Matlab single line format.", "cost-matrix", 1, "-cost-matrix <matrix>"));
/*  74:    */     
/*  75:    */ 
/*  76:    */ 
/*  77:177 */     newVector.addAll(Collections.list(super.listOptions()));
/*  78:    */     
/*  79:179 */     return newVector.elements();
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setOptions(String[] options)
/*  83:    */     throws Exception
/*  84:    */   {
/*  85:235 */     setMinimizeExpectedCost(Utils.getFlag('M', options));
/*  86:    */     
/*  87:237 */     String costFile = Utils.getOption('C', options);
/*  88:238 */     if (costFile.length() != 0)
/*  89:    */     {
/*  90:    */       try
/*  91:    */       {
/*  92:240 */         setCostMatrix(new CostMatrix(new BufferedReader(new FileReader(costFile))));
/*  93:    */       }
/*  94:    */       catch (Exception ex)
/*  95:    */       {
/*  96:245 */         setCostMatrix(null);
/*  97:    */       }
/*  98:247 */       setCostMatrixSource(new SelectedTag(2, TAGS_MATRIX_SOURCE));
/*  99:    */       
/* 100:249 */       this.m_CostFile = costFile;
/* 101:    */     }
/* 102:    */     else
/* 103:    */     {
/* 104:251 */       setCostMatrixSource(new SelectedTag(1, TAGS_MATRIX_SOURCE));
/* 105:    */     }
/* 106:255 */     String demandDir = Utils.getOption('N', options);
/* 107:256 */     if (demandDir.length() != 0) {
/* 108:257 */       setOnDemandDirectory(new File(demandDir));
/* 109:    */     }
/* 110:260 */     String cost_matrix = Utils.getOption("cost-matrix", options);
/* 111:261 */     if (cost_matrix.length() != 0)
/* 112:    */     {
/* 113:262 */       StringWriter writer = new StringWriter();
/* 114:263 */       CostMatrix.parseMatlab(cost_matrix).write(writer);
/* 115:264 */       setCostMatrix(new CostMatrix(new StringReader(writer.toString())));
/* 116:265 */       setCostMatrixSource(new SelectedTag(2, TAGS_MATRIX_SOURCE));
/* 117:    */     }
/* 118:269 */     super.setOptions(options);
/* 119:    */     
/* 120:271 */     Utils.checkForRemainingOptions(options);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public String[] getOptions()
/* 124:    */   {
/* 125:282 */     Vector<String> options = new Vector();
/* 126:284 */     if (this.m_MatrixSource == 2)
/* 127:    */     {
/* 128:285 */       if (this.m_CostFile != null)
/* 129:    */       {
/* 130:286 */         options.add("-C");
/* 131:287 */         options.add("" + this.m_CostFile);
/* 132:    */       }
/* 133:    */       else
/* 134:    */       {
/* 135:290 */         options.add("-cost-matrix");
/* 136:291 */         options.add(getCostMatrix().toMatlab());
/* 137:    */       }
/* 138:    */     }
/* 139:    */     else
/* 140:    */     {
/* 141:294 */       options.add("-N");
/* 142:295 */       options.add("" + getOnDemandDirectory());
/* 143:    */     }
/* 144:298 */     if (getMinimizeExpectedCost()) {
/* 145:299 */       options.add("-M");
/* 146:    */     }
/* 147:302 */     Collections.addAll(options, super.getOptions());
/* 148:    */     
/* 149:304 */     return (String[])options.toArray(new String[0]);
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String globalInfo()
/* 153:    */   {
/* 154:313 */     return "A metaclassifier that makes its base classifier cost-sensitive. Two methods can be used to introduce cost-sensitivity: reweighting training instances according to the total cost assigned to each class; or predicting the class with minimum expected misclassification cost (rather than the most likely class). Performance can often be improved by using a Bagged classifier to improve the probability estimates of the base classifier.";
/* 155:    */   }
/* 156:    */   
/* 157:    */   public String costMatrixSourceTipText()
/* 158:    */   {
/* 159:329 */     return "Sets where to get the cost matrix. The two options areto use the supplied explicit cost matrix (the setting of the costMatrix property), or to load a cost matrix from a file when required (this file will be loaded from the directory set by the onDemandDirectory property and will be named relation_name" + CostMatrix.FILE_EXTENSION + ").";
/* 160:    */   }
/* 161:    */   
/* 162:    */   public SelectedTag getCostMatrixSource()
/* 163:    */   {
/* 164:345 */     return new SelectedTag(this.m_MatrixSource, TAGS_MATRIX_SOURCE);
/* 165:    */   }
/* 166:    */   
/* 167:    */   public void setCostMatrixSource(SelectedTag newMethod)
/* 168:    */   {
/* 169:356 */     if (newMethod.getTags() == TAGS_MATRIX_SOURCE) {
/* 170:357 */       this.m_MatrixSource = newMethod.getSelectedTag().getID();
/* 171:    */     }
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String onDemandDirectoryTipText()
/* 175:    */   {
/* 176:367 */     return "Sets the directory where cost files are loaded from. This option is used when the costMatrixSource is set to \"On Demand\".";
/* 177:    */   }
/* 178:    */   
/* 179:    */   public File getOnDemandDirectory()
/* 180:    */   {
/* 181:379 */     return this.m_OnDemandDirectory;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public void setOnDemandDirectory(File newDir)
/* 185:    */   {
/* 186:390 */     if (newDir.isDirectory()) {
/* 187:391 */       this.m_OnDemandDirectory = newDir;
/* 188:    */     } else {
/* 189:393 */       this.m_OnDemandDirectory = new File(newDir.getParent());
/* 190:    */     }
/* 191:395 */     this.m_MatrixSource = 1;
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String minimizeExpectedCostTipText()
/* 195:    */   {
/* 196:404 */     return "Sets whether the minimum expected cost criteria will be used. If this is false, the training data will be reweighted according to the costs assigned to each class. If true, the minimum expected cost criteria will be used.";
/* 197:    */   }
/* 198:    */   
/* 199:    */   public boolean getMinimizeExpectedCost()
/* 200:    */   {
/* 201:417 */     return this.m_MinimizeExpectedCost;
/* 202:    */   }
/* 203:    */   
/* 204:    */   public void setMinimizeExpectedCost(boolean newMinimizeExpectedCost)
/* 205:    */   {
/* 206:427 */     this.m_MinimizeExpectedCost = newMinimizeExpectedCost;
/* 207:    */   }
/* 208:    */   
/* 209:    */   protected String getClassifierSpec()
/* 210:    */   {
/* 211:438 */     Classifier c = getClassifier();
/* 212:439 */     if ((c instanceof OptionHandler)) {
/* 213:440 */       return c.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)c).getOptions());
/* 214:    */     }
/* 215:443 */     return c.getClass().getName();
/* 216:    */   }
/* 217:    */   
/* 218:    */   public String costMatrixTipText()
/* 219:    */   {
/* 220:451 */     return "Sets the cost matrix explicitly. This matrix is used if the costMatrixSource property is set to \"Supplied\".";
/* 221:    */   }
/* 222:    */   
/* 223:    */   public CostMatrix getCostMatrix()
/* 224:    */   {
/* 225:462 */     return this.m_CostMatrix;
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void setCostMatrix(CostMatrix newCostMatrix)
/* 229:    */   {
/* 230:472 */     this.m_CostMatrix = newCostMatrix;
/* 231:473 */     this.m_MatrixSource = 2;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public Capabilities getCapabilities()
/* 235:    */   {
/* 236:482 */     Capabilities result = super.getCapabilities();
/* 237:    */     
/* 238:    */ 
/* 239:485 */     result.disableAllClasses();
/* 240:486 */     result.disableAllClassDependencies();
/* 241:487 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 242:    */     
/* 243:489 */     return result;
/* 244:    */   }
/* 245:    */   
/* 246:    */   public void buildClassifier(Instances data)
/* 247:    */     throws Exception
/* 248:    */   {
/* 249:501 */     getCapabilities().testWithFail(data);
/* 250:    */     
/* 251:    */ 
/* 252:504 */     data = new Instances(data);
/* 253:505 */     data.deleteWithMissingClass();
/* 254:507 */     if (this.m_Classifier == null) {
/* 255:508 */       throw new Exception("No base classifier has been set!");
/* 256:    */     }
/* 257:510 */     if (this.m_MatrixSource == 1)
/* 258:    */     {
/* 259:511 */       String costName = data.relationName() + CostMatrix.FILE_EXTENSION;
/* 260:512 */       File costFile = new File(getOnDemandDirectory(), costName);
/* 261:513 */       if (!costFile.exists()) {
/* 262:514 */         throw new Exception("On-demand cost file doesn't exist: " + costFile);
/* 263:    */       }
/* 264:516 */       setCostMatrix(new CostMatrix(new BufferedReader(new FileReader(costFile))));
/* 265:    */     }
/* 266:518 */     else if (this.m_CostMatrix == null)
/* 267:    */     {
/* 268:520 */       this.m_CostMatrix = new CostMatrix(data.numClasses());
/* 269:521 */       this.m_CostMatrix.readOldFormat(new BufferedReader(new FileReader(this.m_CostFile)));
/* 270:    */     }
/* 271:525 */     if (!this.m_MinimizeExpectedCost)
/* 272:    */     {
/* 273:526 */       Random random = null;
/* 274:527 */       if (!(this.m_Classifier instanceof WeightedInstancesHandler)) {
/* 275:528 */         random = new Random(this.m_Seed);
/* 276:    */       }
/* 277:530 */       data = this.m_CostMatrix.applyCostMatrix(data, random);
/* 278:    */     }
/* 279:532 */     this.m_Classifier.buildClassifier(data);
/* 280:    */   }
/* 281:    */   
/* 282:    */   public double[] distributionForInstance(Instance instance)
/* 283:    */     throws Exception
/* 284:    */   {
/* 285:547 */     if (!this.m_MinimizeExpectedCost) {
/* 286:548 */       return this.m_Classifier.distributionForInstance(instance);
/* 287:    */     }
/* 288:550 */     return convertDistribution(this.m_Classifier.distributionForInstance(instance), instance);
/* 289:    */   }
/* 290:    */   
/* 291:    */   protected double[] convertDistribution(double[] pred, Instance instance)
/* 292:    */     throws Exception
/* 293:    */   {
/* 294:564 */     double[] costs = this.m_CostMatrix.expectedCosts(pred, instance);
/* 295:    */     
/* 296:    */ 
/* 297:567 */     int classIndex = Utils.minIndex(costs);
/* 298:568 */     for (int i = 0; i < pred.length; i++) {
/* 299:569 */       if (i == classIndex) {
/* 300:570 */         pred[i] = 1.0D;
/* 301:    */       } else {
/* 302:572 */         pred[i] = 0.0D;
/* 303:    */       }
/* 304:    */     }
/* 305:575 */     return pred;
/* 306:    */   }
/* 307:    */   
/* 308:    */   public double[][] distributionsForInstances(Instances insts)
/* 309:    */     throws Exception
/* 310:    */   {
/* 311:589 */     if ((getClassifier() instanceof BatchPredictor))
/* 312:    */     {
/* 313:590 */       double[][] dists = ((BatchPredictor)getClassifier()).distributionsForInstances(insts);
/* 314:591 */       if (!this.m_MinimizeExpectedCost) {
/* 315:592 */         return dists;
/* 316:    */       }
/* 317:594 */       for (int i = 0; i < dists.length; i++) {
/* 318:595 */         dists[i] = convertDistribution(dists[i], insts.instance(i));
/* 319:    */       }
/* 320:597 */       return dists;
/* 321:    */     }
/* 322:600 */     double[][] result = new double[insts.numInstances()][insts.numClasses()];
/* 323:601 */     for (int i = 0; i < insts.numInstances(); i++) {
/* 324:602 */       result[i] = distributionForInstance(insts.instance(i));
/* 325:    */     }
/* 326:604 */     return result;
/* 327:    */   }
/* 328:    */   
/* 329:    */   public String batchSizeTipText()
/* 330:    */   {
/* 331:614 */     return "Batch size to use if base learner is a BatchPredictor";
/* 332:    */   }
/* 333:    */   
/* 334:    */   public void setBatchSize(String size)
/* 335:    */   {
/* 336:625 */     if ((getClassifier() instanceof BatchPredictor)) {
/* 337:626 */       ((BatchPredictor)getClassifier()).setBatchSize(size);
/* 338:    */     }
/* 339:    */   }
/* 340:    */   
/* 341:    */   public String getBatchSize()
/* 342:    */   {
/* 343:638 */     if ((getClassifier() instanceof BatchPredictor)) {
/* 344:639 */       return ((BatchPredictor)getClassifier()).getBatchSize();
/* 345:    */     }
/* 346:641 */     return "1";
/* 347:    */   }
/* 348:    */   
/* 349:    */   public boolean implementsMoreEfficientBatchPrediction()
/* 350:    */   {
/* 351:653 */     if (!(getClassifier() instanceof BatchPredictor)) {
/* 352:654 */       return false;
/* 353:    */     }
/* 354:657 */     return ((BatchPredictor)getClassifier()).implementsMoreEfficientBatchPrediction();
/* 355:    */   }
/* 356:    */   
/* 357:    */   public int graphType()
/* 358:    */   {
/* 359:669 */     if ((this.m_Classifier instanceof Drawable)) {
/* 360:670 */       return ((Drawable)this.m_Classifier).graphType();
/* 361:    */     }
/* 362:672 */     return 0;
/* 363:    */   }
/* 364:    */   
/* 365:    */   public String graph()
/* 366:    */     throws Exception
/* 367:    */   {
/* 368:683 */     if ((this.m_Classifier instanceof Drawable)) {
/* 369:684 */       return ((Drawable)this.m_Classifier).graph();
/* 370:    */     }
/* 371:685 */     throw new Exception("Classifier: " + getClassifierSpec() + " cannot be graphed");
/* 372:    */   }
/* 373:    */   
/* 374:    */   public String toString()
/* 375:    */   {
/* 376:696 */     if (this.m_Classifier == null) {
/* 377:697 */       return "CostSensitiveClassifier: No model built yet.";
/* 378:    */     }
/* 379:700 */     String result = "CostSensitiveClassifier using ";
/* 380:701 */     if (this.m_MinimizeExpectedCost) {
/* 381:702 */       result = result + "minimized expected misclasification cost\n";
/* 382:    */     } else {
/* 383:704 */       result = result + "reweighted training instances\n";
/* 384:    */     }
/* 385:706 */     result = result + "\n" + getClassifierSpec() + "\n\nClassifier Model\n" + this.m_Classifier.toString() + "\n\nCost Matrix\n" + this.m_CostMatrix.toString();
/* 386:    */     
/* 387:    */ 
/* 388:    */ 
/* 389:    */ 
/* 390:    */ 
/* 391:712 */     return result;
/* 392:    */   }
/* 393:    */   
/* 394:    */   public String getRevision()
/* 395:    */   {
/* 396:721 */     return RevisionUtils.extract("$Revision: 12180 $");
/* 397:    */   }
/* 398:    */   
/* 399:    */   public static void main(String[] argv)
/* 400:    */   {
/* 401:731 */     runClassifier(new CostSensitiveClassifier(), argv);
/* 402:    */   }
/* 403:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.CostSensitiveClassifier
 * JD-Core Version:    0.7.0.1
 */