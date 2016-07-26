/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.AbstractClassifier;
/*   9:    */ import weka.classifiers.Classifier;
/*  10:    */ import weka.classifiers.IteratedSingleClassifierEnhancer;
/*  11:    */ import weka.classifiers.IterativeClassifier;
/*  12:    */ import weka.classifiers.trees.DecisionStump;
/*  13:    */ import weka.core.AdditionalMeasureProducer;
/*  14:    */ import weka.core.Capabilities;
/*  15:    */ import weka.core.Capabilities.Capability;
/*  16:    */ import weka.core.Instance;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Option;
/*  19:    */ import weka.core.OptionHandler;
/*  20:    */ import weka.core.RevisionUtils;
/*  21:    */ import weka.core.TechnicalInformation;
/*  22:    */ import weka.core.TechnicalInformation.Field;
/*  23:    */ import weka.core.TechnicalInformation.Type;
/*  24:    */ import weka.core.TechnicalInformationHandler;
/*  25:    */ import weka.core.UnassignedClassException;
/*  26:    */ import weka.core.Utils;
/*  27:    */ import weka.core.WeightedInstancesHandler;
/*  28:    */ 
/*  29:    */ public class AdditiveRegression
/*  30:    */   extends IteratedSingleClassifierEnhancer
/*  31:    */   implements OptionHandler, AdditionalMeasureProducer, WeightedInstancesHandler, TechnicalInformationHandler, IterativeClassifier
/*  32:    */ {
/*  33:    */   static final long serialVersionUID = -2368937577670527151L;
/*  34:    */   protected ArrayList<Classifier> m_Classifiers;
/*  35:121 */   protected double m_shrinkage = 1.0D;
/*  36:    */   protected double m_InitialPrediction;
/*  37:127 */   protected boolean m_SuitableData = true;
/*  38:    */   protected Instances m_Data;
/*  39:    */   protected double m_Error;
/*  40:    */   protected double m_Diff;
/*  41:139 */   protected boolean m_MinimizeAbsoluteError = false;
/*  42:    */   
/*  43:    */   public String globalInfo()
/*  44:    */   {
/*  45:147 */     return " Meta classifier that enhances the performance of a regression base classifier. Each iteration fits a model to the residuals left by the classifier on the previous iteration. Prediction is accomplished by adding the predictions of each classifier. Reducing the shrinkage (learning rate) parameter helps prevent overfitting and has a smoothing effect but increases the learning time.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public TechnicalInformation getTechnicalInformation()
/*  49:    */   {
/*  50:168 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.TECHREPORT);
/*  51:169 */     result.setValue(TechnicalInformation.Field.AUTHOR, "J.H. Friedman");
/*  52:170 */     result.setValue(TechnicalInformation.Field.YEAR, "1999");
/*  53:171 */     result.setValue(TechnicalInformation.Field.TITLE, "Stochastic Gradient Boosting");
/*  54:172 */     result.setValue(TechnicalInformation.Field.INSTITUTION, "Stanford University");
/*  55:173 */     result.setValue(TechnicalInformation.Field.PS, "http://www-stat.stanford.edu/~jhf/ftp/stobst.ps");
/*  56:    */     
/*  57:175 */     return result;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public AdditiveRegression()
/*  61:    */   {
/*  62:183 */     this(new DecisionStump());
/*  63:    */   }
/*  64:    */   
/*  65:    */   public AdditiveRegression(Classifier classifier)
/*  66:    */   {
/*  67:193 */     this.m_Classifier = classifier;
/*  68:    */   }
/*  69:    */   
/*  70:    */   protected String defaultClassifierString()
/*  71:    */   {
/*  72:203 */     return "weka.classifiers.trees.DecisionStump";
/*  73:    */   }
/*  74:    */   
/*  75:    */   public Enumeration<Option> listOptions()
/*  76:    */   {
/*  77:213 */     Vector<Option> newVector = new Vector(2);
/*  78:    */     
/*  79:215 */     newVector.addElement(new Option("\tSpecify shrinkage rate. (default = 1.0, i.e., no shrinkage)", "S", 1, "-S"));
/*  80:    */     
/*  81:    */ 
/*  82:    */ 
/*  83:219 */     newVector.addElement(new Option("\tMinimize absolute error instead of squared error (assumes that base learner minimizes absolute error).", "A", 0, "-A"));
/*  84:    */     
/*  85:    */ 
/*  86:    */ 
/*  87:223 */     newVector.addAll(Collections.list(super.listOptions()));
/*  88:    */     
/*  89:225 */     return newVector.elements();
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setOptions(String[] options)
/*  93:    */     throws Exception
/*  94:    */   {
/*  95:268 */     String optionString = Utils.getOption('S', options);
/*  96:269 */     if (optionString.length() != 0)
/*  97:    */     {
/*  98:270 */       Double temp = Double.valueOf(optionString);
/*  99:271 */       setShrinkage(temp.doubleValue());
/* 100:    */     }
/* 101:273 */     setMinimizeAbsoluteError(Utils.getFlag('A', options));
/* 102:    */     
/* 103:275 */     super.setOptions(options);
/* 104:    */     
/* 105:277 */     Utils.checkForRemainingOptions(options);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public String[] getOptions()
/* 109:    */   {
/* 110:287 */     Vector<String> options = new Vector();
/* 111:    */     
/* 112:289 */     options.add("-S");options.add("" + getShrinkage());
/* 113:291 */     if (getMinimizeAbsoluteError()) {
/* 114:292 */       options.add("-A");
/* 115:    */     }
/* 116:295 */     Collections.addAll(options, super.getOptions());
/* 117:    */     
/* 118:297 */     return (String[])options.toArray(new String[0]);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public String shrinkageTipText()
/* 122:    */   {
/* 123:306 */     return "Shrinkage rate. Smaller values help prevent overfitting and have a smoothing effect (but increase learning time). Default = 1.0, ie. no shrinkage.";
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setShrinkage(double l)
/* 127:    */   {
/* 128:317 */     this.m_shrinkage = l;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public double getShrinkage()
/* 132:    */   {
/* 133:326 */     return this.m_shrinkage;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public String minimizeAbsoluteErrorTipText()
/* 137:    */   {
/* 138:335 */     return "Minimize absolute error instead of squared error (assume base learner minimizes absolute error)";
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void setMinimizeAbsoluteError(boolean f)
/* 142:    */   {
/* 143:344 */     this.m_MinimizeAbsoluteError = f;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public boolean getMinimizeAbsoluteError()
/* 147:    */   {
/* 148:353 */     return this.m_MinimizeAbsoluteError;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Capabilities getCapabilities()
/* 152:    */   {
/* 153:362 */     Capabilities result = super.getCapabilities();
/* 154:    */     
/* 155:    */ 
/* 156:365 */     result.disableAllClasses();
/* 157:366 */     result.disableAllClassDependencies();
/* 158:367 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 159:368 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 160:    */     
/* 161:370 */     return result;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void buildClassifier(Instances data)
/* 165:    */     throws Exception
/* 166:    */   {
/* 167:379 */     initializeClassifier(data);
/* 168:382 */     while (next()) {}
/* 169:385 */     done();
/* 170:    */   }
/* 171:    */   
/* 172:    */   public void initializeClassifier(Instances data)
/* 173:    */     throws Exception
/* 174:    */   {
/* 175:397 */     getCapabilities().testWithFail(data);
/* 176:    */     
/* 177:    */ 
/* 178:400 */     this.m_Data = new Instances(data);
/* 179:401 */     this.m_Data.deleteWithMissingClass();
/* 180:404 */     if (getMinimizeAbsoluteError()) {
/* 181:405 */       this.m_InitialPrediction = this.m_Data.kthSmallestValue(this.m_Data.classIndex(), this.m_Data.numInstances() / 2);
/* 182:    */     } else {
/* 183:407 */       this.m_InitialPrediction = this.m_Data.meanOrMode(this.m_Data.classIndex());
/* 184:    */     }
/* 185:411 */     if (this.m_Data.numAttributes() == 1)
/* 186:    */     {
/* 187:412 */       System.err.println("Cannot build non-trivial model (only class attribute present in data!).");
/* 188:413 */       this.m_SuitableData = false;
/* 189:414 */       return;
/* 190:    */     }
/* 191:416 */     this.m_SuitableData = true;
/* 192:    */     
/* 193:    */ 
/* 194:    */ 
/* 195:420 */     this.m_Classifiers = new ArrayList(this.m_NumIterations);
/* 196:421 */     this.m_Data = residualReplace(this.m_Data, this.m_InitialPrediction);
/* 197:    */     
/* 198:    */ 
/* 199:424 */     this.m_Error = 0.0D;
/* 200:425 */     this.m_Diff = 1.7976931348623157E+308D;
/* 201:426 */     for (int i = 0; i < this.m_Data.numInstances(); i++) {
/* 202:427 */       if (getMinimizeAbsoluteError()) {
/* 203:428 */         this.m_Error += this.m_Data.instance(i).weight() * Math.abs(this.m_Data.instance(i).classValue());
/* 204:    */       } else {
/* 205:430 */         this.m_Error += this.m_Data.instance(i).weight() * this.m_Data.instance(i).classValue() * this.m_Data.instance(i).classValue();
/* 206:    */       }
/* 207:    */     }
/* 208:433 */     if (this.m_Debug) {
/* 209:434 */       if (getMinimizeAbsoluteError()) {
/* 210:435 */         System.err.println("Sum of absolute residuals (predicting the median) : " + this.m_Error);
/* 211:    */       } else {
/* 212:437 */         System.err.println("Sum of squared residuals (predicting the mean) : " + this.m_Error);
/* 213:    */       }
/* 214:    */     }
/* 215:    */   }
/* 216:    */   
/* 217:    */   public boolean next()
/* 218:    */     throws Exception
/* 219:    */   {
/* 220:447 */     if ((!this.m_SuitableData) || (this.m_Classifiers.size() >= this.m_NumIterations) || (this.m_Diff <= Utils.SMALL)) {
/* 221:449 */       return false;
/* 222:    */     }
/* 223:453 */     this.m_Classifiers.add(AbstractClassifier.makeCopy(this.m_Classifier));
/* 224:454 */     ((Classifier)this.m_Classifiers.get(this.m_Classifiers.size() - 1)).buildClassifier(this.m_Data);
/* 225:    */     
/* 226:456 */     this.m_Data = residualReplace(this.m_Data, (Classifier)this.m_Classifiers.get(this.m_Classifiers.size() - 1));
/* 227:457 */     double sum = 0.0D;
/* 228:458 */     for (int i = 0; i < this.m_Data.numInstances(); i++) {
/* 229:459 */       if (getMinimizeAbsoluteError()) {
/* 230:460 */         sum += this.m_Data.instance(i).weight() * Math.abs(this.m_Data.instance(i).classValue());
/* 231:    */       } else {
/* 232:462 */         sum += this.m_Data.instance(i).weight() * this.m_Data.instance(i).classValue() * this.m_Data.instance(i).classValue();
/* 233:    */       }
/* 234:    */     }
/* 235:465 */     if (this.m_Debug) {
/* 236:466 */       if (getMinimizeAbsoluteError()) {
/* 237:467 */         System.err.println("Sum of absolute residuals: " + sum);
/* 238:    */       } else {
/* 239:469 */         System.err.println("Sum of squared residuals: " + sum);
/* 240:    */       }
/* 241:    */     }
/* 242:473 */     this.m_Diff = (this.m_Error - sum);
/* 243:474 */     this.m_Error = sum;
/* 244:    */     
/* 245:476 */     return true;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public void done()
/* 249:    */   {
/* 250:484 */     this.m_Data = null;
/* 251:    */   }
/* 252:    */   
/* 253:    */   public double classifyInstance(Instance inst)
/* 254:    */     throws Exception
/* 255:    */   {
/* 256:496 */     double prediction = this.m_InitialPrediction;
/* 257:499 */     if (!this.m_SuitableData) {
/* 258:500 */       return prediction;
/* 259:    */     }
/* 260:503 */     for (Classifier classifier : this.m_Classifiers)
/* 261:    */     {
/* 262:504 */       double toAdd = classifier.classifyInstance(inst);
/* 263:505 */       if (Utils.isMissingValue(toAdd)) {
/* 264:506 */         throw new UnassignedClassException("AdditiveRegression: base learner predicted missing value.");
/* 265:    */       }
/* 266:508 */       prediction += toAdd * getShrinkage();
/* 267:    */     }
/* 268:511 */     return prediction;
/* 269:    */   }
/* 270:    */   
/* 271:    */   private Instances residualReplace(Instances data, Classifier c)
/* 272:    */     throws Exception
/* 273:    */   {
/* 274:525 */     Instances newInst = new Instances(data);
/* 275:526 */     for (int i = 0; i < newInst.numInstances(); i++)
/* 276:    */     {
/* 277:527 */       double pred = c.classifyInstance(newInst.instance(i));
/* 278:528 */       if (Utils.isMissingValue(pred)) {
/* 279:529 */         throw new UnassignedClassException("AdditiveRegression: base learner predicted missing value.");
/* 280:    */       }
/* 281:531 */       newInst.instance(i).setClassValue(newInst.instance(i).classValue() - pred * getShrinkage());
/* 282:    */     }
/* 283:533 */     return newInst;
/* 284:    */   }
/* 285:    */   
/* 286:    */   private Instances residualReplace(Instances data, double c)
/* 287:    */     throws Exception
/* 288:    */   {
/* 289:547 */     Instances newInst = new Instances(data);
/* 290:548 */     for (int i = 0; i < newInst.numInstances(); i++) {
/* 291:549 */       newInst.instance(i).setClassValue(newInst.instance(i).classValue() - c);
/* 292:    */     }
/* 293:551 */     return newInst;
/* 294:    */   }
/* 295:    */   
/* 296:    */   public Enumeration<String> enumerateMeasures()
/* 297:    */   {
/* 298:559 */     Vector<String> newVector = new Vector(1);
/* 299:560 */     newVector.addElement("measureNumIterations");
/* 300:561 */     return newVector.elements();
/* 301:    */   }
/* 302:    */   
/* 303:    */   public double getMeasure(String additionalMeasureName)
/* 304:    */   {
/* 305:571 */     if (additionalMeasureName.compareToIgnoreCase("measureNumIterations") == 0) {
/* 306:572 */       return measureNumIterations();
/* 307:    */     }
/* 308:574 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (AdditiveRegression)");
/* 309:    */   }
/* 310:    */   
/* 311:    */   public double measureNumIterations()
/* 312:    */   {
/* 313:585 */     return this.m_Classifiers.size();
/* 314:    */   }
/* 315:    */   
/* 316:    */   public String toString()
/* 317:    */   {
/* 318:594 */     StringBuffer text = new StringBuffer();
/* 319:596 */     if ((this.m_SuitableData) && (this.m_Classifiers == null)) {
/* 320:597 */       return "Classifier hasn't been built yet!";
/* 321:    */     }
/* 322:601 */     if (!this.m_SuitableData)
/* 323:    */     {
/* 324:602 */       StringBuffer buf = new StringBuffer();
/* 325:603 */       buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/* 326:604 */       buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/* 327:605 */       buf.append("Warning: Non-trivial model could not be built, initial prediction is: ");
/* 328:606 */       buf.append(this.m_InitialPrediction);
/* 329:607 */       return buf.toString();
/* 330:    */     }
/* 331:610 */     text.append("Additive Regression\n\n");
/* 332:    */     
/* 333:612 */     text.append("Initial prediction: " + this.m_InitialPrediction + "\n\n");
/* 334:    */     
/* 335:614 */     text.append("Base classifier " + getClassifier().getClass().getName() + "\n\n");
/* 336:    */     
/* 337:    */ 
/* 338:617 */     text.append("" + this.m_Classifiers.size() + " models generated.\n");
/* 339:619 */     for (int i = 0; i < this.m_Classifiers.size(); i++) {
/* 340:620 */       text.append("\nModel number " + i + "\n\n" + this.m_Classifiers.get(i) + "\n");
/* 341:    */     }
/* 342:624 */     return text.toString();
/* 343:    */   }
/* 344:    */   
/* 345:    */   public String getRevision()
/* 346:    */   {
/* 347:633 */     return RevisionUtils.extract("$Revision: 12091 $");
/* 348:    */   }
/* 349:    */   
/* 350:    */   public static void main(String[] argv)
/* 351:    */   {
/* 352:643 */     runClassifier(new AdditiveRegression(), argv);
/* 353:    */   }
/* 354:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.AdditiveRegression
 * JD-Core Version:    0.7.0.1
 */