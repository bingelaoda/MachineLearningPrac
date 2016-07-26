/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.AbstractClassifier;
/*   7:    */ import weka.classifiers.trees.lmt.LogisticBase;
/*   8:    */ import weka.core.AdditionalMeasureProducer;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.TechnicalInformation;
/*  17:    */ import weka.core.TechnicalInformation.Field;
/*  18:    */ import weka.core.TechnicalInformation.Type;
/*  19:    */ import weka.core.TechnicalInformationHandler;
/*  20:    */ import weka.core.Utils;
/*  21:    */ import weka.core.WeightedInstancesHandler;
/*  22:    */ import weka.filters.Filter;
/*  23:    */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*  24:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  25:    */ 
/*  26:    */ public class SimpleLogistic
/*  27:    */   extends AbstractClassifier
/*  28:    */   implements OptionHandler, AdditionalMeasureProducer, WeightedInstancesHandler, TechnicalInformationHandler
/*  29:    */ {
/*  30:    */   static final long serialVersionUID = 7397710626304705059L;
/*  31:    */   protected LogisticBase m_boostedModel;
/*  32:141 */   protected NominalToBinary m_NominalToBinary = null;
/*  33:144 */   protected ReplaceMissingValues m_ReplaceMissingValues = null;
/*  34:    */   protected int m_numBoostingIterations;
/*  35:150 */   protected int m_maxBoostingIterations = 500;
/*  36:153 */   protected int m_heuristicStop = 50;
/*  37:    */   protected boolean m_useCrossValidation;
/*  38:    */   protected boolean m_errorOnProbabilities;
/*  39:168 */   protected double m_weightTrimBeta = 0.0D;
/*  40:171 */   private boolean m_useAIC = false;
/*  41:    */   
/*  42:    */   public SimpleLogistic()
/*  43:    */   {
/*  44:177 */     this.m_numBoostingIterations = 0;
/*  45:178 */     this.m_useCrossValidation = true;
/*  46:179 */     this.m_errorOnProbabilities = false;
/*  47:180 */     this.m_weightTrimBeta = 0.0D;
/*  48:181 */     this.m_useAIC = false;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public SimpleLogistic(int numBoostingIterations, boolean useCrossValidation, boolean errorOnProbabilities)
/*  52:    */   {
/*  53:195 */     this.m_numBoostingIterations = numBoostingIterations;
/*  54:196 */     this.m_useCrossValidation = useCrossValidation;
/*  55:197 */     this.m_errorOnProbabilities = errorOnProbabilities;
/*  56:198 */     this.m_weightTrimBeta = 0.0D;
/*  57:199 */     this.m_useAIC = false;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public static void main(String[] argv)
/*  61:    */   {
/*  62:208 */     runClassifier(new SimpleLogistic(), argv);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public Capabilities getCapabilities()
/*  66:    */   {
/*  67:217 */     Capabilities result = super.getCapabilities();
/*  68:218 */     result.disableAll();
/*  69:    */     
/*  70:    */ 
/*  71:221 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  72:222 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  73:223 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  74:224 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  75:    */     
/*  76:    */ 
/*  77:227 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  78:228 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  79:    */     
/*  80:230 */     return result;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void buildClassifier(Instances data)
/*  84:    */     throws Exception
/*  85:    */   {
/*  86:242 */     getCapabilities().testWithFail(data);
/*  87:    */     
/*  88:    */ 
/*  89:245 */     data = new Instances(data);
/*  90:246 */     data.deleteWithMissingClass();
/*  91:    */     
/*  92:    */ 
/*  93:249 */     this.m_ReplaceMissingValues = new ReplaceMissingValues();
/*  94:250 */     this.m_ReplaceMissingValues.setInputFormat(data);
/*  95:251 */     data = Filter.useFilter(data, this.m_ReplaceMissingValues);
/*  96:    */     
/*  97:    */ 
/*  98:254 */     this.m_NominalToBinary = new NominalToBinary();
/*  99:255 */     this.m_NominalToBinary.setInputFormat(data);
/* 100:256 */     data = Filter.useFilter(data, this.m_NominalToBinary);
/* 101:    */     
/* 102:    */ 
/* 103:259 */     this.m_boostedModel = new LogisticBase(this.m_numBoostingIterations, this.m_useCrossValidation, this.m_errorOnProbabilities);
/* 104:    */     
/* 105:    */ 
/* 106:262 */     this.m_boostedModel.setMaxIterations(this.m_maxBoostingIterations);
/* 107:263 */     this.m_boostedModel.setHeuristicStop(this.m_heuristicStop);
/* 108:264 */     this.m_boostedModel.setWeightTrimBeta(this.m_weightTrimBeta);
/* 109:265 */     this.m_boostedModel.setUseAIC(this.m_useAIC);
/* 110:266 */     this.m_boostedModel.setNumDecimalPlaces(this.m_numDecimalPlaces);
/* 111:    */     
/* 112:    */ 
/* 113:269 */     this.m_boostedModel.buildClassifier(data);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public double[] distributionForInstance(Instance inst)
/* 117:    */     throws Exception
/* 118:    */   {
/* 119:282 */     this.m_ReplaceMissingValues.input(inst);
/* 120:283 */     inst = this.m_ReplaceMissingValues.output();
/* 121:284 */     this.m_NominalToBinary.input(inst);
/* 122:285 */     inst = this.m_NominalToBinary.output();
/* 123:    */     
/* 124:    */ 
/* 125:288 */     return this.m_boostedModel.distributionForInstance(inst);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public Enumeration<Option> listOptions()
/* 129:    */   {
/* 130:297 */     Vector<Option> newVector = new Vector();
/* 131:    */     
/* 132:299 */     newVector.addElement(new Option("\tSet fixed number of iterations for LogitBoost", "I", 1, "-I <iterations>"));
/* 133:    */     
/* 134:    */ 
/* 135:    */ 
/* 136:303 */     newVector.addElement(new Option("\tUse stopping criterion on training set (instead of\n\tcross-validation)", "S", 0, "-S"));
/* 137:    */     
/* 138:    */ 
/* 139:    */ 
/* 140:307 */     newVector.addElement(new Option("\tUse error on probabilities (rmse) instead of\n\tmisclassification error for stopping criterion", "P", 0, "-P"));
/* 141:    */     
/* 142:    */ 
/* 143:    */ 
/* 144:311 */     newVector.addElement(new Option("\tSet maximum number of boosting iterations", "M", 1, "-M <iterations>"));
/* 145:    */     
/* 146:    */ 
/* 147:    */ 
/* 148:315 */     newVector.addElement(new Option("\tSet parameter for heuristic for early stopping of\n\tLogitBoost.\n\tIf enabled, the minimum is selected greedily, stopping\n\tif the current minimum has not changed for iter iterations.\n\tBy default, heuristic is enabled with value 50. Set to\n\tzero to disable heuristic.", "H", 1, "-H <iterations>"));
/* 149:    */     
/* 150:    */ 
/* 151:    */ 
/* 152:    */ 
/* 153:    */ 
/* 154:    */ 
/* 155:    */ 
/* 156:323 */     newVector.addElement(new Option("\tSet beta for weight trimming for LogitBoost. Set to 0 for no weight trimming.\n", "W", 1, "-W <beta>"));
/* 157:    */     
/* 158:    */ 
/* 159:    */ 
/* 160:    */ 
/* 161:328 */     newVector.addElement(new Option("\tThe AIC is used to choose the best iteration (instead of CV or training error).\n", "A", 0, "-A"));
/* 162:    */     
/* 163:    */ 
/* 164:    */ 
/* 165:    */ 
/* 166:333 */     newVector.addAll(Collections.list(super.listOptions()));
/* 167:    */     
/* 168:335 */     return newVector.elements();
/* 169:    */   }
/* 170:    */   
/* 171:    */   public String[] getOptions()
/* 172:    */   {
/* 173:344 */     Vector<String> options = new Vector();
/* 174:    */     
/* 175:346 */     options.add("-I");
/* 176:347 */     options.add("" + getNumBoostingIterations());
/* 177:349 */     if (!getUseCrossValidation()) {
/* 178:350 */       options.add("-S");
/* 179:    */     }
/* 180:353 */     if (getErrorOnProbabilities()) {
/* 181:354 */       options.add("-P");
/* 182:    */     }
/* 183:357 */     options.add("-M");
/* 184:358 */     options.add("" + getMaxBoostingIterations());
/* 185:    */     
/* 186:360 */     options.add("-H");
/* 187:361 */     options.add("" + getHeuristicStop());
/* 188:    */     
/* 189:363 */     options.add("-W");
/* 190:364 */     options.add("" + getWeightTrimBeta());
/* 191:366 */     if (getUseAIC()) {
/* 192:367 */       options.add("-A");
/* 193:    */     }
/* 194:370 */     Collections.addAll(options, super.getOptions());
/* 195:    */     
/* 196:372 */     return (String[])options.toArray(new String[0]);
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void setOptions(String[] options)
/* 200:    */     throws Exception
/* 201:    */   {
/* 202:432 */     String optionString = Utils.getOption('I', options);
/* 203:433 */     if (optionString.length() != 0) {
/* 204:434 */       setNumBoostingIterations(new Integer(optionString).intValue());
/* 205:    */     }
/* 206:437 */     setUseCrossValidation(!Utils.getFlag('S', options));
/* 207:438 */     setErrorOnProbabilities(Utils.getFlag('P', options));
/* 208:    */     
/* 209:440 */     optionString = Utils.getOption('M', options);
/* 210:441 */     if (optionString.length() != 0) {
/* 211:442 */       setMaxBoostingIterations(new Integer(optionString).intValue());
/* 212:    */     }
/* 213:445 */     optionString = Utils.getOption('H', options);
/* 214:446 */     if (optionString.length() != 0) {
/* 215:447 */       setHeuristicStop(new Integer(optionString).intValue());
/* 216:    */     }
/* 217:450 */     optionString = Utils.getOption('W', options);
/* 218:451 */     if (optionString.length() != 0) {
/* 219:452 */       setWeightTrimBeta(new Double(optionString).doubleValue());
/* 220:    */     }
/* 221:455 */     setUseAIC(Utils.getFlag('A', options));
/* 222:    */     
/* 223:457 */     super.setOptions(options);
/* 224:    */     
/* 225:459 */     Utils.checkForRemainingOptions(options);
/* 226:    */   }
/* 227:    */   
/* 228:    */   public int getNumBoostingIterations()
/* 229:    */   {
/* 230:468 */     return this.m_numBoostingIterations;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public void setNumBoostingIterations(int n)
/* 234:    */   {
/* 235:477 */     this.m_numBoostingIterations = n;
/* 236:    */   }
/* 237:    */   
/* 238:    */   public boolean getUseCrossValidation()
/* 239:    */   {
/* 240:486 */     return this.m_useCrossValidation;
/* 241:    */   }
/* 242:    */   
/* 243:    */   public void setUseCrossValidation(boolean l)
/* 244:    */   {
/* 245:495 */     this.m_useCrossValidation = l;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public boolean getErrorOnProbabilities()
/* 249:    */   {
/* 250:505 */     return this.m_errorOnProbabilities;
/* 251:    */   }
/* 252:    */   
/* 253:    */   public void setErrorOnProbabilities(boolean l)
/* 254:    */   {
/* 255:515 */     this.m_errorOnProbabilities = l;
/* 256:    */   }
/* 257:    */   
/* 258:    */   public int getMaxBoostingIterations()
/* 259:    */   {
/* 260:524 */     return this.m_maxBoostingIterations;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public void setMaxBoostingIterations(int n)
/* 264:    */   {
/* 265:533 */     this.m_maxBoostingIterations = n;
/* 266:    */   }
/* 267:    */   
/* 268:    */   public int getHeuristicStop()
/* 269:    */   {
/* 270:542 */     return this.m_heuristicStop;
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void setHeuristicStop(int n)
/* 274:    */   {
/* 275:551 */     if (n == 0) {
/* 276:552 */       this.m_heuristicStop = this.m_maxBoostingIterations;
/* 277:    */     } else {
/* 278:554 */       this.m_heuristicStop = n;
/* 279:    */     }
/* 280:    */   }
/* 281:    */   
/* 282:    */   public double getWeightTrimBeta()
/* 283:    */   {
/* 284:561 */     return this.m_weightTrimBeta;
/* 285:    */   }
/* 286:    */   
/* 287:    */   public void setWeightTrimBeta(double n)
/* 288:    */   {
/* 289:568 */     this.m_weightTrimBeta = n;
/* 290:    */   }
/* 291:    */   
/* 292:    */   public boolean getUseAIC()
/* 293:    */   {
/* 294:577 */     return this.m_useAIC;
/* 295:    */   }
/* 296:    */   
/* 297:    */   public void setUseAIC(boolean c)
/* 298:    */   {
/* 299:586 */     this.m_useAIC = c;
/* 300:    */   }
/* 301:    */   
/* 302:    */   public int getNumRegressions()
/* 303:    */   {
/* 304:596 */     return this.m_boostedModel.getNumRegressions();
/* 305:    */   }
/* 306:    */   
/* 307:    */   public String toString()
/* 308:    */   {
/* 309:605 */     if (this.m_boostedModel == null) {
/* 310:606 */       return "No model built";
/* 311:    */     }
/* 312:607 */     return "SimpleLogistic:\n" + this.m_boostedModel.toString();
/* 313:    */   }
/* 314:    */   
/* 315:    */   public double measureAttributesUsed()
/* 316:    */   {
/* 317:618 */     return this.m_boostedModel.percentAttributesUsed();
/* 318:    */   }
/* 319:    */   
/* 320:    */   public Enumeration<String> enumerateMeasures()
/* 321:    */   {
/* 322:627 */     Vector<String> newVector = new Vector(3);
/* 323:628 */     newVector.addElement("measureAttributesUsed");
/* 324:629 */     newVector.addElement("measureNumIterations");
/* 325:630 */     return newVector.elements();
/* 326:    */   }
/* 327:    */   
/* 328:    */   public double getMeasure(String additionalMeasureName)
/* 329:    */   {
/* 330:641 */     if (additionalMeasureName.compareToIgnoreCase("measureAttributesUsed") == 0) {
/* 331:642 */       return measureAttributesUsed();
/* 332:    */     }
/* 333:643 */     if (additionalMeasureName.compareToIgnoreCase("measureNumIterations") == 0) {
/* 334:645 */       return getNumRegressions();
/* 335:    */     }
/* 336:647 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (SimpleLogistic)");
/* 337:    */   }
/* 338:    */   
/* 339:    */   public String globalInfo()
/* 340:    */   {
/* 341:659 */     return "Classifier for building linear logistic regression models. LogitBoost with simple regression functions as base learners is used for fitting the logistic models. The optimal number of LogitBoost iterations to perform is cross-validated, which leads to automatic attribute selection. For more information see:\n" + getTechnicalInformation().toString();
/* 342:    */   }
/* 343:    */   
/* 344:    */   public TechnicalInformation getTechnicalInformation()
/* 345:    */   {
/* 346:676 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/* 347:677 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Niels Landwehr and Mark Hall and Eibe Frank");
/* 348:    */     
/* 349:679 */     result.setValue(TechnicalInformation.Field.TITLE, "Logistic Model Trees");
/* 350:680 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Machine Learning");
/* 351:681 */     result.setValue(TechnicalInformation.Field.YEAR, "2005");
/* 352:682 */     result.setValue(TechnicalInformation.Field.VOLUME, "95");
/* 353:683 */     result.setValue(TechnicalInformation.Field.PAGES, "161-205");
/* 354:684 */     result.setValue(TechnicalInformation.Field.NUMBER, "1-2");
/* 355:    */     
/* 356:686 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/* 357:687 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Marc Sumner and Eibe Frank and Mark Hall");
/* 358:    */     
/* 359:689 */     additional.setValue(TechnicalInformation.Field.TITLE, "Speeding up Logistic Model Tree Induction");
/* 360:    */     
/* 361:691 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "9th European Conference on Principles and Practice of Knowledge Discovery in Databases");
/* 362:    */     
/* 363:    */ 
/* 364:694 */     additional.setValue(TechnicalInformation.Field.YEAR, "2005");
/* 365:695 */     additional.setValue(TechnicalInformation.Field.PAGES, "675-683");
/* 366:696 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/* 367:    */     
/* 368:698 */     return result;
/* 369:    */   }
/* 370:    */   
/* 371:    */   public String numBoostingIterationsTipText()
/* 372:    */   {
/* 373:708 */     return "Set fixed number of iterations for LogitBoost. If >= 0, this sets the number of LogitBoost iterations to perform. If < 0, the number is cross-validated or a stopping criterion on the training set is used (depending on the value of useCrossValidation).";
/* 374:    */   }
/* 375:    */   
/* 376:    */   public String useCrossValidationTipText()
/* 377:    */   {
/* 378:720 */     return "Sets whether the number of LogitBoost iterations is to be cross-validated or the stopping criterion on the training set should be used. If not set (and no fixed number of iterations was given), the number of LogitBoost iterations is used that minimizes the error on the training set (misclassification error or error on probabilities depending on errorOnProbabilities).";
/* 379:    */   }
/* 380:    */   
/* 381:    */   public String errorOnProbabilitiesTipText()
/* 382:    */   {
/* 383:733 */     return "Use error on the probabilties as error measure when determining the best number of LogitBoost iterations. If set, the number of LogitBoost iterations is chosen that minimizes the root mean squared error (either on the training set or in the cross-validation, depending on useCrossValidation).";
/* 384:    */   }
/* 385:    */   
/* 386:    */   public String maxBoostingIterationsTipText()
/* 387:    */   {
/* 388:745 */     return "Sets the maximum number of iterations for LogitBoost. Default value is 500, for very small/large datasets a lower/higher value might be preferable.";
/* 389:    */   }
/* 390:    */   
/* 391:    */   public String heuristicStopTipText()
/* 392:    */   {
/* 393:756 */     return "If heuristicStop > 0, the heuristic for greedy stopping while cross-validating the number of LogitBoost iterations is enabled. This means LogitBoost is stopped if no new error minimum has been reached in the last heuristicStop iterations. It is recommended to use this heuristic, it gives a large speed-up especially on small datasets. The default value is 50.";
/* 394:    */   }
/* 395:    */   
/* 396:    */   public String weightTrimBetaTipText()
/* 397:    */   {
/* 398:769 */     return "Set the beta value used for weight trimming in LogitBoost. Only instances carrying (1 - beta)% of the weight from previous iteration are used in the next iteration. Set to 0 for no weight trimming. The default value is 0.";
/* 399:    */   }
/* 400:    */   
/* 401:    */   public String useAICTipText()
/* 402:    */   {
/* 403:782 */     return "The AIC is used to determine when to stop LogitBoost iterations (instead of cross-validation or training error).";
/* 404:    */   }
/* 405:    */   
/* 406:    */   public String numDecimalPlacesTipText()
/* 407:    */   {
/* 408:793 */     return "The number of decimal places to be used for the output of coefficients.";
/* 409:    */   }
/* 410:    */   
/* 411:    */   public String getRevision()
/* 412:    */   {
/* 413:802 */     return RevisionUtils.extract("$Revision: 11568 $");
/* 414:    */   }
/* 415:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.SimpleLogistic
 * JD-Core Version:    0.7.0.1
 */