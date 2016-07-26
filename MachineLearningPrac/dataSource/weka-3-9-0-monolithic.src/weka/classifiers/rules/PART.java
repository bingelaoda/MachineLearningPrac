/*   1:    */ package weka.classifiers.rules;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.AbstractClassifier;
/*   7:    */ import weka.classifiers.rules.part.MakeDecList;
/*   8:    */ import weka.classifiers.trees.j48.BinC45ModelSelection;
/*   9:    */ import weka.classifiers.trees.j48.C45ModelSelection;
/*  10:    */ import weka.classifiers.trees.j48.ModelSelection;
/*  11:    */ import weka.core.AdditionalMeasureProducer;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Capabilities.Capability;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.Option;
/*  17:    */ import weka.core.OptionHandler;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.Summarizable;
/*  20:    */ import weka.core.TechnicalInformation;
/*  21:    */ import weka.core.TechnicalInformation.Field;
/*  22:    */ import weka.core.TechnicalInformation.Type;
/*  23:    */ import weka.core.TechnicalInformationHandler;
/*  24:    */ import weka.core.Utils;
/*  25:    */ import weka.core.WeightedInstancesHandler;
/*  26:    */ 
/*  27:    */ public class PART
/*  28:    */   extends AbstractClassifier
/*  29:    */   implements OptionHandler, WeightedInstancesHandler, Summarizable, AdditionalMeasureProducer, TechnicalInformationHandler
/*  30:    */ {
/*  31:    */   static final long serialVersionUID = 8121455039782598361L;
/*  32:    */   private MakeDecList m_root;
/*  33:148 */   private float m_CF = 0.25F;
/*  34:151 */   private int m_minNumObj = 2;
/*  35:154 */   private boolean m_useMDLcorrection = true;
/*  36:157 */   private boolean m_reducedErrorPruning = false;
/*  37:160 */   private int m_numFolds = 3;
/*  38:163 */   private boolean m_binarySplits = false;
/*  39:166 */   private boolean m_unpruned = false;
/*  40:169 */   private int m_Seed = 1;
/*  41:    */   private boolean m_doNotMakeSplitPointActualValue;
/*  42:    */   
/*  43:    */   public String globalInfo()
/*  44:    */   {
/*  45:182 */     return "Class for generating a PART decision list. Uses separate-and-conquer. Builds a partial C4.5 decision tree in each iteration and makes the \"best\" leaf into a rule.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public TechnicalInformation getTechnicalInformation()
/*  49:    */   {
/*  50:199 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  51:200 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank and Ian H. Witten");
/*  52:201 */     result.setValue(TechnicalInformation.Field.TITLE, "Generating Accurate Rule Sets Without Global Optimization");
/*  53:    */     
/*  54:203 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Fifteenth International Conference on Machine Learning");
/*  55:    */     
/*  56:205 */     result.setValue(TechnicalInformation.Field.EDITOR, "J. Shavlik");
/*  57:206 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  58:207 */     result.setValue(TechnicalInformation.Field.PAGES, "144-151");
/*  59:208 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
/*  60:209 */     result.setValue(TechnicalInformation.Field.PS, "http://www.cs.waikato.ac.nz/~eibe/pubs/ML98-57.ps.gz");
/*  61:    */     
/*  62:    */ 
/*  63:212 */     return result;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public Capabilities getCapabilities()
/*  67:    */   {
/*  68:224 */     Capabilities result = new Capabilities(this);
/*  69:225 */     result.disableAll();
/*  70:    */     
/*  71:227 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  72:228 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  73:229 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  74:230 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  75:    */     
/*  76:    */ 
/*  77:233 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  78:234 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  79:    */     
/*  80:    */ 
/*  81:237 */     result.setMinimumNumberInstances(0);
/*  82:    */     
/*  83:239 */     return result;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void buildClassifier(Instances instances)
/*  87:    */     throws Exception
/*  88:    */   {
/*  89:252 */     getCapabilities().testWithFail(instances);
/*  90:    */     
/*  91:    */ 
/*  92:255 */     instances = new Instances(instances);
/*  93:256 */     instances.deleteWithMissingClass();
/*  94:    */     ModelSelection modSelection;
/*  95:    */     ModelSelection modSelection;
/*  96:260 */     if (this.m_binarySplits) {
/*  97:261 */       modSelection = new BinC45ModelSelection(this.m_minNumObj, instances, this.m_useMDLcorrection, this.m_doNotMakeSplitPointActualValue);
/*  98:    */     } else {
/*  99:264 */       modSelection = new C45ModelSelection(this.m_minNumObj, instances, this.m_useMDLcorrection, this.m_doNotMakeSplitPointActualValue);
/* 100:    */     }
/* 101:267 */     if (this.m_unpruned) {
/* 102:268 */       this.m_root = new MakeDecList(modSelection, this.m_minNumObj);
/* 103:269 */     } else if (this.m_reducedErrorPruning) {
/* 104:270 */       this.m_root = new MakeDecList(modSelection, this.m_numFolds, this.m_minNumObj, this.m_Seed);
/* 105:    */     } else {
/* 106:272 */       this.m_root = new MakeDecList(modSelection, this.m_CF, this.m_minNumObj);
/* 107:    */     }
/* 108:274 */     this.m_root.buildClassifier(instances);
/* 109:275 */     if (this.m_binarySplits) {
/* 110:276 */       ((BinC45ModelSelection)modSelection).cleanup();
/* 111:    */     } else {
/* 112:278 */       ((C45ModelSelection)modSelection).cleanup();
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   public double classifyInstance(Instance instance)
/* 117:    */     throws Exception
/* 118:    */   {
/* 119:292 */     return this.m_root.classifyInstance(instance);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public final double[] distributionForInstance(Instance instance)
/* 123:    */     throws Exception
/* 124:    */   {
/* 125:306 */     return this.m_root.distributionForInstance(instance);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public Enumeration<Option> listOptions()
/* 129:    */   {
/* 130:349 */     Vector<Option> newVector = new Vector(8);
/* 131:    */     
/* 132:351 */     newVector.addElement(new Option("\tSet confidence threshold for pruning.\n\t(default 0.25)", "C", 1, "-C <pruning confidence>"));
/* 133:    */     
/* 134:353 */     newVector.addElement(new Option("\tSet minimum number of objects per leaf.\n\t(default 2)", "M", 1, "-M <minimum number of objects>"));
/* 135:    */     
/* 136:    */ 
/* 137:356 */     newVector.addElement(new Option("\tUse reduced error pruning.", "R", 0, "-R"));
/* 138:    */     
/* 139:358 */     newVector.addElement(new Option("\tSet number of folds for reduced error\n\tpruning. One fold is used as pruning set.\n\t(default 3)", "N", 1, "-N <number of folds>"));
/* 140:    */     
/* 141:    */ 
/* 142:361 */     newVector.addElement(new Option("\tUse binary splits only.", "B", 0, "-B"));
/* 143:362 */     newVector.addElement(new Option("\tGenerate unpruned decision list.", "U", 0, "-U"));
/* 144:    */     
/* 145:364 */     newVector.addElement(new Option("\tDo not use MDL correction for info gain on numeric attributes.", "J", 0, "-J"));
/* 146:    */     
/* 147:    */ 
/* 148:367 */     newVector.addElement(new Option("\tSeed for random data shuffling (default 1).", "Q", 1, "-Q <seed>"));
/* 149:    */     
/* 150:369 */     newVector.addElement(new Option("\tDo not make split point actual value.", "-doNotMakeSplitPointActualValue", 0, "-doNotMakeSplitPointActualValue"));
/* 151:    */     
/* 152:    */ 
/* 153:372 */     newVector.addAll(Collections.list(super.listOptions()));
/* 154:    */     
/* 155:374 */     return newVector.elements();
/* 156:    */   }
/* 157:    */   
/* 158:    */   public void setOptions(String[] options)
/* 159:    */     throws Exception
/* 160:    */   {
/* 161:442 */     this.m_unpruned = Utils.getFlag('U', options);
/* 162:443 */     this.m_reducedErrorPruning = Utils.getFlag('R', options);
/* 163:444 */     this.m_binarySplits = Utils.getFlag('B', options);
/* 164:445 */     this.m_useMDLcorrection = (!Utils.getFlag('J', options));
/* 165:446 */     this.m_doNotMakeSplitPointActualValue = Utils.getFlag("doNotMakeSplitPointActualValue", options);
/* 166:    */     
/* 167:448 */     String confidenceString = Utils.getOption('C', options);
/* 168:449 */     if (confidenceString.length() != 0)
/* 169:    */     {
/* 170:450 */       if (this.m_reducedErrorPruning) {
/* 171:451 */         throw new Exception("Setting CF doesn't make sense for reduced error pruning.");
/* 172:    */       }
/* 173:454 */       this.m_CF = new Float(confidenceString).floatValue();
/* 174:455 */       if ((this.m_CF <= 0.0F) || (this.m_CF >= 1.0F)) {
/* 175:456 */         throw new Exception("CF has to be greater than zero and smaller than one!");
/* 176:    */       }
/* 177:    */     }
/* 178:    */     else
/* 179:    */     {
/* 180:461 */       this.m_CF = 0.25F;
/* 181:    */     }
/* 182:463 */     String numFoldsString = Utils.getOption('N', options);
/* 183:464 */     if (numFoldsString.length() != 0)
/* 184:    */     {
/* 185:465 */       if (!this.m_reducedErrorPruning) {
/* 186:466 */         throw new Exception("Setting the number of folds does only make sense for reduced error pruning.");
/* 187:    */       }
/* 188:469 */       this.m_numFolds = Integer.parseInt(numFoldsString);
/* 189:    */     }
/* 190:    */     else
/* 191:    */     {
/* 192:472 */       this.m_numFolds = 3;
/* 193:    */     }
/* 194:476 */     String minNumString = Utils.getOption('M', options);
/* 195:477 */     if (minNumString.length() != 0) {
/* 196:478 */       this.m_minNumObj = Integer.parseInt(minNumString);
/* 197:    */     } else {
/* 198:480 */       this.m_minNumObj = 2;
/* 199:    */     }
/* 200:482 */     String seedString = Utils.getOption('Q', options);
/* 201:483 */     if (seedString.length() != 0) {
/* 202:484 */       this.m_Seed = Integer.parseInt(seedString);
/* 203:    */     } else {
/* 204:486 */       this.m_Seed = 1;
/* 205:    */     }
/* 206:489 */     super.setOptions(options);
/* 207:    */   }
/* 208:    */   
/* 209:    */   public String[] getOptions()
/* 210:    */   {
/* 211:500 */     Vector<String> options = new Vector(13);
/* 212:502 */     if (this.m_unpruned) {
/* 213:503 */       options.add("-U");
/* 214:    */     }
/* 215:505 */     if (this.m_reducedErrorPruning) {
/* 216:506 */       options.add("-R");
/* 217:    */     }
/* 218:508 */     if (this.m_binarySplits) {
/* 219:509 */       options.add("-B");
/* 220:    */     }
/* 221:511 */     options.add("-M");
/* 222:512 */     options.add("" + this.m_minNumObj);
/* 223:513 */     if (!this.m_reducedErrorPruning)
/* 224:    */     {
/* 225:514 */       options.add("-C");
/* 226:515 */       options.add("" + this.m_CF);
/* 227:    */     }
/* 228:517 */     if (this.m_reducedErrorPruning)
/* 229:    */     {
/* 230:518 */       options.add("-N");
/* 231:519 */       options.add("" + this.m_numFolds);
/* 232:    */     }
/* 233:521 */     options.add("-Q");
/* 234:522 */     options.add("" + this.m_Seed);
/* 235:523 */     if (!this.m_useMDLcorrection) {
/* 236:524 */       options.add("-J");
/* 237:    */     }
/* 238:526 */     if (this.m_doNotMakeSplitPointActualValue) {
/* 239:527 */       options.add("-doNotMakeSplitPointActualValue");
/* 240:    */     }
/* 241:530 */     Collections.addAll(options, super.getOptions());
/* 242:    */     
/* 243:532 */     return (String[])options.toArray(new String[0]);
/* 244:    */   }
/* 245:    */   
/* 246:    */   public String toString()
/* 247:    */   {
/* 248:543 */     if (this.m_root == null) {
/* 249:544 */       return "No classifier built";
/* 250:    */     }
/* 251:546 */     return "PART decision list\n------------------\n\n" + this.m_root.toString();
/* 252:    */   }
/* 253:    */   
/* 254:    */   public String toSummaryString()
/* 255:    */   {
/* 256:557 */     return "Number of rules: " + this.m_root.numRules() + "\n";
/* 257:    */   }
/* 258:    */   
/* 259:    */   public double measureNumRules()
/* 260:    */   {
/* 261:566 */     return this.m_root.numRules();
/* 262:    */   }
/* 263:    */   
/* 264:    */   public Enumeration<String> enumerateMeasures()
/* 265:    */   {
/* 266:576 */     Vector<String> newVector = new Vector(1);
/* 267:577 */     newVector.addElement("measureNumRules");
/* 268:578 */     return newVector.elements();
/* 269:    */   }
/* 270:    */   
/* 271:    */   public double getMeasure(String additionalMeasureName)
/* 272:    */   {
/* 273:590 */     if (additionalMeasureName.compareToIgnoreCase("measureNumRules") == 0) {
/* 274:591 */       return measureNumRules();
/* 275:    */     }
/* 276:593 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (PART)");
/* 277:    */   }
/* 278:    */   
/* 279:    */   public String confidenceFactorTipText()
/* 280:    */   {
/* 281:605 */     return "The confidence factor used for pruning (smaller values incur more pruning).";
/* 282:    */   }
/* 283:    */   
/* 284:    */   public float getConfidenceFactor()
/* 285:    */   {
/* 286:616 */     return this.m_CF;
/* 287:    */   }
/* 288:    */   
/* 289:    */   public void setConfidenceFactor(float v)
/* 290:    */   {
/* 291:626 */     this.m_CF = v;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public String minNumObjTipText()
/* 295:    */   {
/* 296:636 */     return "The minimum number of instances per rule.";
/* 297:    */   }
/* 298:    */   
/* 299:    */   public int getMinNumObj()
/* 300:    */   {
/* 301:646 */     return this.m_minNumObj;
/* 302:    */   }
/* 303:    */   
/* 304:    */   public void setMinNumObj(int v)
/* 305:    */   {
/* 306:656 */     this.m_minNumObj = v;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public String reducedErrorPruningTipText()
/* 310:    */   {
/* 311:666 */     return "Whether reduced-error pruning is used instead of C.4.5 pruning.";
/* 312:    */   }
/* 313:    */   
/* 314:    */   public boolean getReducedErrorPruning()
/* 315:    */   {
/* 316:676 */     return this.m_reducedErrorPruning;
/* 317:    */   }
/* 318:    */   
/* 319:    */   public void setReducedErrorPruning(boolean v)
/* 320:    */   {
/* 321:686 */     this.m_reducedErrorPruning = v;
/* 322:    */   }
/* 323:    */   
/* 324:    */   public String unprunedTipText()
/* 325:    */   {
/* 326:696 */     return "Whether pruning is performed.";
/* 327:    */   }
/* 328:    */   
/* 329:    */   public boolean getUnpruned()
/* 330:    */   {
/* 331:706 */     return this.m_unpruned;
/* 332:    */   }
/* 333:    */   
/* 334:    */   public void setUnpruned(boolean newunpruned)
/* 335:    */   {
/* 336:716 */     this.m_unpruned = newunpruned;
/* 337:    */   }
/* 338:    */   
/* 339:    */   public String useMDLcorrectionTipText()
/* 340:    */   {
/* 341:726 */     return "Whether MDL correction is used when finding splits on numeric attributes.";
/* 342:    */   }
/* 343:    */   
/* 344:    */   public boolean getUseMDLcorrection()
/* 345:    */   {
/* 346:736 */     return this.m_useMDLcorrection;
/* 347:    */   }
/* 348:    */   
/* 349:    */   public void setUseMDLcorrection(boolean newuseMDLcorrection)
/* 350:    */   {
/* 351:746 */     this.m_useMDLcorrection = newuseMDLcorrection;
/* 352:    */   }
/* 353:    */   
/* 354:    */   public String numFoldsTipText()
/* 355:    */   {
/* 356:756 */     return "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the rules.";
/* 357:    */   }
/* 358:    */   
/* 359:    */   public int getNumFolds()
/* 360:    */   {
/* 361:767 */     return this.m_numFolds;
/* 362:    */   }
/* 363:    */   
/* 364:    */   public void setNumFolds(int v)
/* 365:    */   {
/* 366:777 */     this.m_numFolds = v;
/* 367:    */   }
/* 368:    */   
/* 369:    */   public String seedTipText()
/* 370:    */   {
/* 371:787 */     return "The seed used for randomizing the data when reduced-error pruning is used.";
/* 372:    */   }
/* 373:    */   
/* 374:    */   public int getSeed()
/* 375:    */   {
/* 376:798 */     return this.m_Seed;
/* 377:    */   }
/* 378:    */   
/* 379:    */   public void setSeed(int newSeed)
/* 380:    */   {
/* 381:808 */     this.m_Seed = newSeed;
/* 382:    */   }
/* 383:    */   
/* 384:    */   public String binarySplitsTipText()
/* 385:    */   {
/* 386:818 */     return "Whether to use binary splits on nominal attributes when building the partial trees.";
/* 387:    */   }
/* 388:    */   
/* 389:    */   public boolean getBinarySplits()
/* 390:    */   {
/* 391:829 */     return this.m_binarySplits;
/* 392:    */   }
/* 393:    */   
/* 394:    */   public void setBinarySplits(boolean v)
/* 395:    */   {
/* 396:839 */     this.m_binarySplits = v;
/* 397:    */   }
/* 398:    */   
/* 399:    */   public String doNotMakeSplitPointActualValueTipText()
/* 400:    */   {
/* 401:849 */     return "If true, the split point is not relocated to an actual data value. This can yield substantial speed-ups for large datasets with numeric attributes.";
/* 402:    */   }
/* 403:    */   
/* 404:    */   public boolean getDoNotMakeSplitPointActualValue()
/* 405:    */   {
/* 406:859 */     return this.m_doNotMakeSplitPointActualValue;
/* 407:    */   }
/* 408:    */   
/* 409:    */   public void setDoNotMakeSplitPointActualValue(boolean m_doNotMakeSplitPointActualValue)
/* 410:    */   {
/* 411:869 */     this.m_doNotMakeSplitPointActualValue = m_doNotMakeSplitPointActualValue;
/* 412:    */   }
/* 413:    */   
/* 414:    */   public String getRevision()
/* 415:    */   {
/* 416:879 */     return RevisionUtils.extract("$Revision: 11004 $");
/* 417:    */   }
/* 418:    */   
/* 419:    */   public static void main(String[] argv)
/* 420:    */   {
/* 421:888 */     runClassifier(new PART(), argv);
/* 422:    */   }
/* 423:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.PART
 * JD-Core Version:    0.7.0.1
 */