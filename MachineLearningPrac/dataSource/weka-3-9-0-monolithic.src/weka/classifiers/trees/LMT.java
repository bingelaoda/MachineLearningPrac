/*   1:    */ package weka.classifiers.trees;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.AbstractClassifier;
/*   7:    */ import weka.classifiers.trees.j48.C45ModelSelection;
/*   8:    */ import weka.classifiers.trees.j48.ModelSelection;
/*   9:    */ import weka.classifiers.trees.lmt.LMTNode;
/*  10:    */ import weka.classifiers.trees.lmt.ResidualModelSelection;
/*  11:    */ import weka.core.AdditionalMeasureProducer;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Capabilities.Capability;
/*  14:    */ import weka.core.Drawable;
/*  15:    */ import weka.core.Instance;
/*  16:    */ import weka.core.Instances;
/*  17:    */ import weka.core.Option;
/*  18:    */ import weka.core.OptionHandler;
/*  19:    */ import weka.core.RevisionUtils;
/*  20:    */ import weka.core.TechnicalInformation;
/*  21:    */ import weka.core.TechnicalInformation.Field;
/*  22:    */ import weka.core.TechnicalInformation.Type;
/*  23:    */ import weka.core.TechnicalInformationHandler;
/*  24:    */ import weka.core.Utils;
/*  25:    */ import weka.filters.Filter;
/*  26:    */ import weka.filters.supervised.attribute.NominalToBinary;
/*  27:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  28:    */ 
/*  29:    */ public class LMT
/*  30:    */   extends AbstractClassifier
/*  31:    */   implements OptionHandler, AdditionalMeasureProducer, Drawable, TechnicalInformationHandler
/*  32:    */ {
/*  33:    */   static final long serialVersionUID = -1113212459618104943L;
/*  34:    */   protected ReplaceMissingValues m_replaceMissing;
/*  35:    */   protected NominalToBinary m_nominalToBinary;
/*  36:    */   protected LMTNode m_tree;
/*  37:    */   protected boolean m_fastRegression;
/*  38:    */   protected boolean m_convertNominal;
/*  39:    */   protected boolean m_splitOnResiduals;
/*  40:    */   protected boolean m_errorOnProbabilities;
/*  41:    */   protected int m_minNumInstances;
/*  42:    */   protected int m_numBoostingIterations;
/*  43:    */   protected double m_weightTrimBeta;
/*  44:193 */   private boolean m_useAIC = false;
/*  45:    */   private boolean m_doNotMakeSplitPointActualValue;
/*  46:    */   
/*  47:    */   public LMT()
/*  48:    */   {
/*  49:202 */     this.m_fastRegression = true;
/*  50:203 */     this.m_numBoostingIterations = -1;
/*  51:204 */     this.m_minNumInstances = 15;
/*  52:205 */     this.m_weightTrimBeta = 0.0D;
/*  53:206 */     this.m_useAIC = false;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Capabilities getCapabilities()
/*  57:    */   {
/*  58:216 */     Capabilities result = super.getCapabilities();
/*  59:217 */     result.disableAll();
/*  60:    */     
/*  61:    */ 
/*  62:220 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  63:221 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  64:222 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  65:223 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  66:    */     
/*  67:    */ 
/*  68:226 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  69:227 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  70:    */     
/*  71:229 */     return result;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void buildClassifier(Instances data)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:242 */     getCapabilities().testWithFail(data);
/*  78:    */     
/*  79:    */ 
/*  80:245 */     Instances filteredData = new Instances(data);
/*  81:246 */     filteredData.deleteWithMissingClass();
/*  82:    */     
/*  83:    */ 
/*  84:249 */     this.m_replaceMissing = new ReplaceMissingValues();
/*  85:250 */     this.m_replaceMissing.setInputFormat(filteredData);
/*  86:251 */     filteredData = Filter.useFilter(filteredData, this.m_replaceMissing);
/*  87:    */     
/*  88:    */ 
/*  89:254 */     this.m_nominalToBinary = new NominalToBinary();
/*  90:255 */     this.m_nominalToBinary.setInputFormat(filteredData);
/*  91:256 */     if (this.m_convertNominal) {
/*  92:257 */       filteredData = Filter.useFilter(filteredData, this.m_nominalToBinary);
/*  93:    */     }
/*  94:260 */     int minNumInstances = 2;
/*  95:    */     ModelSelection modSelection;
/*  96:    */     ModelSelection modSelection;
/*  97:265 */     if (this.m_splitOnResiduals) {
/*  98:266 */       modSelection = new ResidualModelSelection(minNumInstances);
/*  99:    */     } else {
/* 100:268 */       modSelection = new C45ModelSelection(minNumInstances, filteredData, true, this.m_doNotMakeSplitPointActualValue);
/* 101:    */     }
/* 102:273 */     this.m_tree = new LMTNode(modSelection, this.m_numBoostingIterations, this.m_fastRegression, this.m_errorOnProbabilities, this.m_minNumInstances, this.m_weightTrimBeta, this.m_useAIC, this.m_nominalToBinary, this.m_numDecimalPlaces);
/* 103:    */     
/* 104:    */ 
/* 105:    */ 
/* 106:277 */     this.m_tree.buildClassifier(filteredData);
/* 107:279 */     if ((modSelection instanceof C45ModelSelection)) {
/* 108:280 */       ((C45ModelSelection)modSelection).cleanup();
/* 109:    */     }
/* 110:    */   }
/* 111:    */   
/* 112:    */   public double[] distributionForInstance(Instance instance)
/* 113:    */     throws Exception
/* 114:    */   {
/* 115:295 */     this.m_replaceMissing.input(instance);
/* 116:296 */     instance = this.m_replaceMissing.output();
/* 117:299 */     if (this.m_convertNominal)
/* 118:    */     {
/* 119:300 */       this.m_nominalToBinary.input(instance);
/* 120:301 */       instance = this.m_nominalToBinary.output();
/* 121:    */     }
/* 122:304 */     return this.m_tree.distributionForInstance(instance);
/* 123:    */   }
/* 124:    */   
/* 125:    */   public double classifyInstance(Instance instance)
/* 126:    */     throws Exception
/* 127:    */   {
/* 128:317 */     double maxProb = -1.0D;
/* 129:318 */     int maxIndex = 0;
/* 130:    */     
/* 131:    */ 
/* 132:321 */     double[] probs = distributionForInstance(instance);
/* 133:322 */     for (int j = 0; j < instance.numClasses(); j++) {
/* 134:323 */       if (Utils.gr(probs[j], maxProb))
/* 135:    */       {
/* 136:324 */         maxIndex = j;
/* 137:325 */         maxProb = probs[j];
/* 138:    */       }
/* 139:    */     }
/* 140:328 */     return maxIndex;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public String toString()
/* 144:    */   {
/* 145:338 */     if (this.m_tree != null) {
/* 146:339 */       return "Logistic model tree \n------------------\n" + this.m_tree.toString();
/* 147:    */     }
/* 148:341 */     return "No tree build";
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Enumeration<Option> listOptions()
/* 152:    */   {
/* 153:352 */     Vector<Option> newVector = new Vector(9);
/* 154:    */     
/* 155:354 */     newVector.addElement(new Option("\tBinary splits (convert nominal attributes to binary ones)", "B", 0, "-B"));
/* 156:    */     
/* 157:    */ 
/* 158:    */ 
/* 159:358 */     newVector.addElement(new Option("\tSplit on residuals instead of class values", "R", 0, "-R"));
/* 160:    */     
/* 161:    */ 
/* 162:361 */     newVector.addElement(new Option("\tUse cross-validation for boosting at all nodes (i.e., disable heuristic)", "C", 0, "-C"));
/* 163:    */     
/* 164:    */ 
/* 165:    */ 
/* 166:    */ 
/* 167:366 */     newVector.addElement(new Option("\tUse error on probabilities instead of misclassification error for stopping criterion of LogitBoost.", "P", 0, "-P"));
/* 168:    */     
/* 169:    */ 
/* 170:    */ 
/* 171:370 */     newVector.addElement(new Option("\tSet fixed number of iterations for LogitBoost (instead of using cross-validation)", "I", 1, "-I <numIterations>"));
/* 172:    */     
/* 173:    */ 
/* 174:    */ 
/* 175:374 */     newVector.addElement(new Option("\tSet minimum number of instances at which a node can be split (default 15)", "M", 1, "-M <numInstances>"));
/* 176:    */     
/* 177:    */ 
/* 178:    */ 
/* 179:    */ 
/* 180:379 */     newVector.addElement(new Option("\tSet beta for weight trimming for LogitBoost. Set to 0 (default) for no weight trimming.", "W", 1, "-W <beta>"));
/* 181:    */     
/* 182:    */ 
/* 183:    */ 
/* 184:    */ 
/* 185:384 */     newVector.addElement(new Option("\tThe AIC is used to choose the best iteration.", "A", 0, "-A"));
/* 186:    */     
/* 187:386 */     newVector.addElement(new Option("\tDo not make split point actual value.", "-doNotMakeSplitPointActualValue", 0, "-doNotMakeSplitPointActualValue"));
/* 188:    */     
/* 189:    */ 
/* 190:389 */     newVector.addAll(Collections.list(super.listOptions()));
/* 191:    */     
/* 192:391 */     return newVector.elements();
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void setOptions(String[] options)
/* 196:    */     throws Exception
/* 197:    */   {
/* 198:454 */     setConvertNominal(Utils.getFlag('B', options));
/* 199:455 */     setSplitOnResiduals(Utils.getFlag('R', options));
/* 200:456 */     setFastRegression(!Utils.getFlag('C', options));
/* 201:457 */     setErrorOnProbabilities(Utils.getFlag('P', options));
/* 202:    */     
/* 203:459 */     String optionString = Utils.getOption('I', options);
/* 204:460 */     if (optionString.length() != 0) {
/* 205:461 */       setNumBoostingIterations(new Integer(optionString).intValue());
/* 206:    */     }
/* 207:464 */     optionString = Utils.getOption('M', options);
/* 208:465 */     if (optionString.length() != 0) {
/* 209:466 */       setMinNumInstances(new Integer(optionString).intValue());
/* 210:    */     }
/* 211:469 */     optionString = Utils.getOption('W', options);
/* 212:470 */     if (optionString.length() != 0) {
/* 213:471 */       setWeightTrimBeta(new Double(optionString).doubleValue());
/* 214:    */     }
/* 215:474 */     setUseAIC(Utils.getFlag('A', options));
/* 216:475 */     this.m_doNotMakeSplitPointActualValue = Utils.getFlag("doNotMakeSplitPointActualValue", options);
/* 217:    */     
/* 218:    */ 
/* 219:478 */     super.setOptions(options);
/* 220:    */     
/* 221:480 */     Utils.checkForRemainingOptions(options);
/* 222:    */   }
/* 223:    */   
/* 224:    */   public String[] getOptions()
/* 225:    */   {
/* 226:492 */     Vector<String> options = new Vector();
/* 227:494 */     if (getConvertNominal()) {
/* 228:495 */       options.add("-B");
/* 229:    */     }
/* 230:498 */     if (getSplitOnResiduals()) {
/* 231:499 */       options.add("-R");
/* 232:    */     }
/* 233:502 */     if (!getFastRegression()) {
/* 234:503 */       options.add("-C");
/* 235:    */     }
/* 236:506 */     if (getErrorOnProbabilities()) {
/* 237:507 */       options.add("-P");
/* 238:    */     }
/* 239:510 */     options.add("-I");
/* 240:511 */     options.add("" + getNumBoostingIterations());
/* 241:    */     
/* 242:513 */     options.add("-M");
/* 243:514 */     options.add("" + getMinNumInstances());
/* 244:    */     
/* 245:516 */     options.add("-W");
/* 246:517 */     options.add("" + getWeightTrimBeta());
/* 247:519 */     if (getUseAIC()) {
/* 248:520 */       options.add("-A");
/* 249:    */     }
/* 250:523 */     if (this.m_doNotMakeSplitPointActualValue) {
/* 251:524 */       options.add("-doNotMakeSplitPointActualValue");
/* 252:    */     }
/* 253:527 */     Collections.addAll(options, super.getOptions());
/* 254:    */     
/* 255:529 */     return (String[])options.toArray(new String[0]);
/* 256:    */   }
/* 257:    */   
/* 258:    */   public double getWeightTrimBeta()
/* 259:    */   {
/* 260:536 */     return this.m_weightTrimBeta;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public boolean getUseAIC()
/* 264:    */   {
/* 265:545 */     return this.m_useAIC;
/* 266:    */   }
/* 267:    */   
/* 268:    */   public void setWeightTrimBeta(double n)
/* 269:    */   {
/* 270:552 */     this.m_weightTrimBeta = n;
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void setUseAIC(boolean c)
/* 274:    */   {
/* 275:561 */     this.m_useAIC = c;
/* 276:    */   }
/* 277:    */   
/* 278:    */   public boolean getConvertNominal()
/* 279:    */   {
/* 280:570 */     return this.m_convertNominal;
/* 281:    */   }
/* 282:    */   
/* 283:    */   public boolean getSplitOnResiduals()
/* 284:    */   {
/* 285:579 */     return this.m_splitOnResiduals;
/* 286:    */   }
/* 287:    */   
/* 288:    */   public boolean getFastRegression()
/* 289:    */   {
/* 290:588 */     return this.m_fastRegression;
/* 291:    */   }
/* 292:    */   
/* 293:    */   public boolean getErrorOnProbabilities()
/* 294:    */   {
/* 295:597 */     return this.m_errorOnProbabilities;
/* 296:    */   }
/* 297:    */   
/* 298:    */   public int getNumBoostingIterations()
/* 299:    */   {
/* 300:606 */     return this.m_numBoostingIterations;
/* 301:    */   }
/* 302:    */   
/* 303:    */   public int getMinNumInstances()
/* 304:    */   {
/* 305:615 */     return this.m_minNumInstances;
/* 306:    */   }
/* 307:    */   
/* 308:    */   public void setConvertNominal(boolean c)
/* 309:    */   {
/* 310:624 */     this.m_convertNominal = c;
/* 311:    */   }
/* 312:    */   
/* 313:    */   public void setSplitOnResiduals(boolean c)
/* 314:    */   {
/* 315:633 */     this.m_splitOnResiduals = c;
/* 316:    */   }
/* 317:    */   
/* 318:    */   public void setFastRegression(boolean c)
/* 319:    */   {
/* 320:642 */     this.m_fastRegression = c;
/* 321:    */   }
/* 322:    */   
/* 323:    */   public void setErrorOnProbabilities(boolean c)
/* 324:    */   {
/* 325:651 */     this.m_errorOnProbabilities = c;
/* 326:    */   }
/* 327:    */   
/* 328:    */   public void setNumBoostingIterations(int c)
/* 329:    */   {
/* 330:660 */     this.m_numBoostingIterations = c;
/* 331:    */   }
/* 332:    */   
/* 333:    */   public void setMinNumInstances(int c)
/* 334:    */   {
/* 335:669 */     this.m_minNumInstances = c;
/* 336:    */   }
/* 337:    */   
/* 338:    */   public int graphType()
/* 339:    */   {
/* 340:679 */     return 1;
/* 341:    */   }
/* 342:    */   
/* 343:    */   public String graph()
/* 344:    */     throws Exception
/* 345:    */   {
/* 346:691 */     return this.m_tree.graph();
/* 347:    */   }
/* 348:    */   
/* 349:    */   public int measureTreeSize()
/* 350:    */   {
/* 351:700 */     return this.m_tree.numNodes();
/* 352:    */   }
/* 353:    */   
/* 354:    */   public int measureNumLeaves()
/* 355:    */   {
/* 356:709 */     return this.m_tree.numLeaves();
/* 357:    */   }
/* 358:    */   
/* 359:    */   public Enumeration<String> enumerateMeasures()
/* 360:    */   {
/* 361:719 */     Vector<String> newVector = new Vector(2);
/* 362:720 */     newVector.addElement("measureTreeSize");
/* 363:721 */     newVector.addElement("measureNumLeaves");
/* 364:    */     
/* 365:723 */     return newVector.elements();
/* 366:    */   }
/* 367:    */   
/* 368:    */   public double getMeasure(String additionalMeasureName)
/* 369:    */   {
/* 370:735 */     if (additionalMeasureName.compareToIgnoreCase("measureTreeSize") == 0) {
/* 371:736 */       return measureTreeSize();
/* 372:    */     }
/* 373:737 */     if (additionalMeasureName.compareToIgnoreCase("measureNumLeaves") == 0) {
/* 374:738 */       return measureNumLeaves();
/* 375:    */     }
/* 376:740 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (LMT)");
/* 377:    */   }
/* 378:    */   
/* 379:    */   public String globalInfo()
/* 380:    */   {
/* 381:752 */     return "Classifier for building 'logistic model trees', which are classification trees with logistic regression functions at the leaves. The algorithm can deal with binary and multi-class target variables, numeric and nominal attributes and missing values.\n\nFor more information see: \n\n" + getTechnicalInformation().toString();
/* 382:    */   }
/* 383:    */   
/* 384:    */   public TechnicalInformation getTechnicalInformation()
/* 385:    */   {
/* 386:770 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/* 387:771 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Niels Landwehr and Mark Hall and Eibe Frank");
/* 388:    */     
/* 389:773 */     result.setValue(TechnicalInformation.Field.TITLE, "Logistic Model Trees");
/* 390:774 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/* 391:775 */     result.setValue(TechnicalInformation.Field.YEAR, "2005");
/* 392:776 */     result.setValue(TechnicalInformation.Field.VOLUME, "95");
/* 393:777 */     result.setValue(TechnicalInformation.Field.PAGES, "161-205");
/* 394:778 */     result.setValue(TechnicalInformation.Field.NUMBER, "1-2");
/* 395:    */     
/* 396:780 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/* 397:781 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Marc Sumner and Eibe Frank and Mark Hall");
/* 398:    */     
/* 399:783 */     additional.setValue(TechnicalInformation.Field.TITLE, "Speeding up Logistic Model Tree Induction");
/* 400:    */     
/* 401:785 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "9th European Conference on Principles and Practice of Knowledge Discovery in Databases");
/* 402:    */     
/* 403:    */ 
/* 404:    */ 
/* 405:789 */     additional.setValue(TechnicalInformation.Field.YEAR, "2005");
/* 406:790 */     additional.setValue(TechnicalInformation.Field.PAGES, "675-683");
/* 407:791 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/* 408:    */     
/* 409:793 */     return result;
/* 410:    */   }
/* 411:    */   
/* 412:    */   public String convertNominalTipText()
/* 413:    */   {
/* 414:803 */     return "Convert all nominal attributes to binary ones before building the tree. This means that all splits in the final tree will be binary.";
/* 415:    */   }
/* 416:    */   
/* 417:    */   public String splitOnResidualsTipText()
/* 418:    */   {
/* 419:814 */     return "Set splitting criterion based on the residuals of LogitBoost. There are two possible splitting criteria for LMT: the default is to use the C4.5 splitting criterion that uses information gain on the class variable. The other splitting criterion tries to improve the purity in the residuals produces when fitting the logistic regression functions. The choice of the splitting criterion does not usually affect classification accuracy much, but can produce different trees.";
/* 420:    */   }
/* 421:    */   
/* 422:    */   public String fastRegressionTipText()
/* 423:    */   {
/* 424:829 */     return "Use heuristic that avoids cross-validating the number of Logit-Boost iterations at every node. When fitting the logistic regression functions at a node, LMT has to determine the number of LogitBoost iterations to run. Originally, this number was cross-validated at every node in the tree. To save time, this heuristic cross-validates the number only once and then uses that number at every node in the tree. Usually this does not decrease accuracy but improves runtime considerably.";
/* 425:    */   }
/* 426:    */   
/* 427:    */   public String errorOnProbabilitiesTipText()
/* 428:    */   {
/* 429:843 */     return "Minimize error on probabilities instead of misclassification error when cross-validating the number of LogitBoost iterations. When set, the number of LogitBoost iterations is chosen that minimizes the root mean squared error instead of the misclassification error.";
/* 430:    */   }
/* 431:    */   
/* 432:    */   public String numBoostingIterationsTipText()
/* 433:    */   {
/* 434:855 */     return "Set a fixed number of iterations for LogitBoost. If >= 0, this sets a fixed number of LogitBoost iterations that is used everywhere in the tree. If < 0, the number is cross-validated.";
/* 435:    */   }
/* 436:    */   
/* 437:    */   public String minNumInstancesTipText()
/* 438:    */   {
/* 439:866 */     return "Set the minimum number of instances at which a node is considered for splitting. The default value is 15.";
/* 440:    */   }
/* 441:    */   
/* 442:    */   public String weightTrimBetaTipText()
/* 443:    */   {
/* 444:877 */     return "Set the beta value used for weight trimming in LogitBoost. Only instances carrying (1 - beta)% of the weight from previous iteration are used in the next iteration. Set to 0 for no weight trimming. The default value is 0.";
/* 445:    */   }
/* 446:    */   
/* 447:    */   public String useAICTipText()
/* 448:    */   {
/* 449:890 */     return "The AIC is used to determine when to stop LogitBoost iterations. The default is not to use AIC.";
/* 450:    */   }
/* 451:    */   
/* 452:    */   public String doNotMakeSplitPointActualValueTipText()
/* 453:    */   {
/* 454:901 */     return "If true, the split point is not relocated to an actual data value. This can yield substantial speed-ups for large datasets with numeric attributes.";
/* 455:    */   }
/* 456:    */   
/* 457:    */   public String numDecimalPlacesTipText()
/* 458:    */   {
/* 459:912 */     return "The number of decimal places to be used for the output of coefficients.";
/* 460:    */   }
/* 461:    */   
/* 462:    */   public boolean getDoNotMakeSplitPointActualValue()
/* 463:    */   {
/* 464:921 */     return this.m_doNotMakeSplitPointActualValue;
/* 465:    */   }
/* 466:    */   
/* 467:    */   public void setDoNotMakeSplitPointActualValue(boolean m_doNotMakeSplitPointActualValue)
/* 468:    */   {
/* 469:931 */     this.m_doNotMakeSplitPointActualValue = m_doNotMakeSplitPointActualValue;
/* 470:    */   }
/* 471:    */   
/* 472:    */   public String getRevision()
/* 473:    */   {
/* 474:941 */     return RevisionUtils.extract("$Revision: 11568 $");
/* 475:    */   }
/* 476:    */   
/* 477:    */   public static void main(String[] argv)
/* 478:    */   {
/* 479:950 */     runClassifier(new LMT(), argv);
/* 480:    */   }
/* 481:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.LMT
 * JD-Core Version:    0.7.0.1
 */