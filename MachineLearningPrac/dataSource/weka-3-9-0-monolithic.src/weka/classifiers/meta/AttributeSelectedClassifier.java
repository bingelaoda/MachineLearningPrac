/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.attributeSelection.ASEvaluation;
/*   8:    */ import weka.attributeSelection.ASSearch;
/*   9:    */ import weka.attributeSelection.AttributeSelection;
/*  10:    */ import weka.attributeSelection.BestFirst;
/*  11:    */ import weka.attributeSelection.CfsSubsetEval;
/*  12:    */ import weka.classifiers.Classifier;
/*  13:    */ import weka.classifiers.SingleClassifierEnhancer;
/*  14:    */ import weka.classifiers.trees.J48;
/*  15:    */ import weka.core.AdditionalMeasureProducer;
/*  16:    */ import weka.core.Attribute;
/*  17:    */ import weka.core.Capabilities;
/*  18:    */ import weka.core.Capabilities.Capability;
/*  19:    */ import weka.core.Drawable;
/*  20:    */ import weka.core.Instance;
/*  21:    */ import weka.core.Instances;
/*  22:    */ import weka.core.Option;
/*  23:    */ import weka.core.OptionHandler;
/*  24:    */ import weka.core.RevisionUtils;
/*  25:    */ import weka.core.Utils;
/*  26:    */ import weka.core.WeightedInstancesHandler;
/*  27:    */ 
/*  28:    */ public class AttributeSelectedClassifier
/*  29:    */   extends SingleClassifierEnhancer
/*  30:    */   implements OptionHandler, Drawable, AdditionalMeasureProducer, WeightedInstancesHandler
/*  31:    */ {
/*  32:    */   static final long serialVersionUID = -1151805453487947577L;
/*  33:126 */   protected AttributeSelection m_AttributeSelection = null;
/*  34:129 */   protected ASEvaluation m_Evaluator = new CfsSubsetEval();
/*  35:133 */   protected ASSearch m_Search = new BestFirst();
/*  36:    */   protected Instances m_ReducedHeader;
/*  37:    */   protected int m_numClasses;
/*  38:    */   protected double m_numAttributesSelected;
/*  39:    */   protected double m_selectionTime;
/*  40:    */   protected double m_totalTime;
/*  41:    */   
/*  42:    */   protected String defaultClassifierString()
/*  43:    */   {
/*  44:158 */     return "weka.classifiers.trees.J48";
/*  45:    */   }
/*  46:    */   
/*  47:    */   public AttributeSelectedClassifier()
/*  48:    */   {
/*  49:165 */     this.m_Classifier = new J48();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String globalInfo()
/*  53:    */   {
/*  54:174 */     return "Dimensionality of training and test data is reduced by attribute selection before being passed on to a classifier.";
/*  55:    */   }
/*  56:    */   
/*  57:    */   public Enumeration<Option> listOptions()
/*  58:    */   {
/*  59:184 */     Vector<Option> newVector = new Vector(2);
/*  60:    */     
/*  61:186 */     newVector.addElement(new Option("\tFull class name of attribute evaluator, followed\n\tby its options.\n\teg: \"weka.attributeSelection.CfsSubsetEval -L\"\n\t(default weka.attributeSelection.CfsSubsetEval)", "E", 1, "-E <attribute evaluator specification>"));
/*  62:    */     
/*  63:    */ 
/*  64:    */ 
/*  65:    */ 
/*  66:    */ 
/*  67:    */ 
/*  68:193 */     newVector.addElement(new Option("\tFull class name of search method, followed\n\tby its options.\n\teg: \"weka.attributeSelection.BestFirst -D 1\"\n\t(default weka.attributeSelection.BestFirst)", "S", 1, "-S <search method specification>"));
/*  69:    */     
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:200 */     newVector.addAll(Collections.list(super.listOptions()));
/*  76:202 */     if ((getEvaluator() instanceof OptionHandler))
/*  77:    */     {
/*  78:203 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to attribute evaluator " + getEvaluator().getClass().getName() + ":"));
/*  79:    */       
/*  80:    */ 
/*  81:    */ 
/*  82:207 */       newVector.addAll(Collections.list(((OptionHandler)getEvaluator()).listOptions()));
/*  83:    */     }
/*  84:210 */     if ((getSearch() instanceof OptionHandler))
/*  85:    */     {
/*  86:211 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to search method " + getSearch().getClass().getName() + ":"));
/*  87:    */       
/*  88:    */ 
/*  89:    */ 
/*  90:215 */       newVector.addAll(Collections.list(((OptionHandler)getSearch()).listOptions()));
/*  91:    */     }
/*  92:218 */     return newVector.elements();
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setOptions(String[] options)
/*  96:    */     throws Exception
/*  97:    */   {
/*  98:293 */     String evaluatorString = Utils.getOption('E', options);
/*  99:294 */     if (evaluatorString.length() == 0) {
/* 100:295 */       evaluatorString = CfsSubsetEval.class.getName();
/* 101:    */     }
/* 102:296 */     String[] evaluatorSpec = Utils.splitOptions(evaluatorString);
/* 103:297 */     if (evaluatorSpec.length == 0) {
/* 104:298 */       throw new Exception("Invalid attribute evaluator specification string");
/* 105:    */     }
/* 106:300 */     String evaluatorName = evaluatorSpec[0];
/* 107:301 */     evaluatorSpec[0] = "";
/* 108:302 */     setEvaluator(ASEvaluation.forName(evaluatorName, evaluatorSpec));
/* 109:    */     
/* 110:    */ 
/* 111:305 */     String searchString = Utils.getOption('S', options);
/* 112:306 */     if (searchString.length() == 0) {
/* 113:307 */       searchString = BestFirst.class.getName();
/* 114:    */     }
/* 115:308 */     String[] searchSpec = Utils.splitOptions(searchString);
/* 116:309 */     if (searchSpec.length == 0) {
/* 117:310 */       throw new Exception("Invalid search specification string");
/* 118:    */     }
/* 119:312 */     String searchName = searchSpec[0];
/* 120:313 */     searchSpec[0] = "";
/* 121:314 */     setSearch(ASSearch.forName(searchName, searchSpec));
/* 122:    */     
/* 123:316 */     super.setOptions(options);
/* 124:    */     
/* 125:318 */     Utils.checkForRemainingOptions(options);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public String[] getOptions()
/* 129:    */   {
/* 130:328 */     Vector<String> options = new Vector();
/* 131:    */     
/* 132:    */ 
/* 133:331 */     options.add("-E");
/* 134:332 */     options.add("" + getEvaluatorSpec());
/* 135:    */     
/* 136:    */ 
/* 137:335 */     options.add("-S");
/* 138:336 */     options.add("" + getSearchSpec());
/* 139:    */     
/* 140:338 */     Collections.addAll(options, super.getOptions());
/* 141:    */     
/* 142:340 */     return (String[])options.toArray(new String[0]);
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String evaluatorTipText()
/* 146:    */   {
/* 147:349 */     return "Set the attribute evaluator to use. This evaluator is used during the attribute selection phase before the classifier is invoked.";
/* 148:    */   }
/* 149:    */   
/* 150:    */   public void setEvaluator(ASEvaluation evaluator)
/* 151:    */   {
/* 152:360 */     this.m_Evaluator = evaluator;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public ASEvaluation getEvaluator()
/* 156:    */   {
/* 157:369 */     return this.m_Evaluator;
/* 158:    */   }
/* 159:    */   
/* 160:    */   protected String getEvaluatorSpec()
/* 161:    */   {
/* 162:380 */     ASEvaluation e = getEvaluator();
/* 163:381 */     if ((e instanceof OptionHandler)) {
/* 164:382 */       return e.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)e).getOptions());
/* 165:    */     }
/* 166:385 */     return e.getClass().getName();
/* 167:    */   }
/* 168:    */   
/* 169:    */   public String searchTipText()
/* 170:    */   {
/* 171:394 */     return "Set the search method. This search method is used during the attribute selection phase before the classifier is invoked.";
/* 172:    */   }
/* 173:    */   
/* 174:    */   public void setSearch(ASSearch search)
/* 175:    */   {
/* 176:405 */     this.m_Search = search;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public ASSearch getSearch()
/* 180:    */   {
/* 181:414 */     return this.m_Search;
/* 182:    */   }
/* 183:    */   
/* 184:    */   protected String getSearchSpec()
/* 185:    */   {
/* 186:425 */     ASSearch s = getSearch();
/* 187:426 */     if ((s instanceof OptionHandler)) {
/* 188:427 */       return s.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)s).getOptions());
/* 189:    */     }
/* 190:430 */     return s.getClass().getName();
/* 191:    */   }
/* 192:    */   
/* 193:    */   public Capabilities getCapabilities()
/* 194:    */   {
/* 195:    */     Capabilities result;
/* 196:    */     Capabilities result;
/* 197:441 */     if (getEvaluator() == null) {
/* 198:442 */       result = super.getCapabilities();
/* 199:    */     } else {
/* 200:444 */       result = getEvaluator().getCapabilities();
/* 201:    */     }
/* 202:447 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/* 203:448 */       result.enableDependency(cap);
/* 204:    */     }
/* 205:450 */     return result;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public void buildClassifier(Instances data)
/* 209:    */     throws Exception
/* 210:    */   {
/* 211:460 */     if (this.m_Classifier == null) {
/* 212:461 */       throw new Exception("No base classifier has been set!");
/* 213:    */     }
/* 214:464 */     if (this.m_Evaluator == null) {
/* 215:465 */       throw new Exception("No attribute evaluator has been set!");
/* 216:    */     }
/* 217:468 */     if (this.m_Search == null) {
/* 218:469 */       throw new Exception("No search method has been set!");
/* 219:    */     }
/* 220:473 */     getCapabilities().testWithFail(data);
/* 221:    */     
/* 222:    */ 
/* 223:476 */     Instances newData = new Instances(data);
/* 224:478 */     if (newData.numInstances() == 0)
/* 225:    */     {
/* 226:479 */       this.m_Classifier.buildClassifier(newData);
/* 227:480 */       return;
/* 228:    */     }
/* 229:482 */     if (newData.classAttribute().isNominal()) {
/* 230:483 */       this.m_numClasses = newData.classAttribute().numValues();
/* 231:    */     } else {
/* 232:485 */       this.m_numClasses = 1;
/* 233:    */     }
/* 234:488 */     Instances resampledData = null;
/* 235:    */     
/* 236:490 */     double weight = newData.instance(0).weight();
/* 237:491 */     boolean ok = false;
/* 238:492 */     for (int i = 1; i < newData.numInstances(); i++) {
/* 239:493 */       if (newData.instance(i).weight() != weight)
/* 240:    */       {
/* 241:494 */         ok = true;
/* 242:495 */         break;
/* 243:    */       }
/* 244:    */     }
/* 245:499 */     if (ok)
/* 246:    */     {
/* 247:500 */       if ((!(this.m_Evaluator instanceof WeightedInstancesHandler)) || (!(this.m_Classifier instanceof WeightedInstancesHandler)))
/* 248:    */       {
/* 249:502 */         Random r = new Random(1L);
/* 250:503 */         for (int i = 0; i < 10; i++) {
/* 251:504 */           r.nextDouble();
/* 252:    */         }
/* 253:506 */         resampledData = newData.resampleWithWeights(r);
/* 254:    */       }
/* 255:    */     }
/* 256:    */     else {
/* 257:510 */       resampledData = newData;
/* 258:    */     }
/* 259:513 */     this.m_AttributeSelection = new AttributeSelection();
/* 260:514 */     this.m_AttributeSelection.setEvaluator(this.m_Evaluator);
/* 261:515 */     this.m_AttributeSelection.setSearch(this.m_Search);
/* 262:516 */     long start = System.currentTimeMillis();
/* 263:517 */     this.m_AttributeSelection.SelectAttributes((this.m_Evaluator instanceof WeightedInstancesHandler) ? newData : resampledData);
/* 264:    */     
/* 265:    */ 
/* 266:    */ 
/* 267:521 */     long end = System.currentTimeMillis();
/* 268:522 */     if ((this.m_Classifier instanceof WeightedInstancesHandler))
/* 269:    */     {
/* 270:523 */       newData = this.m_AttributeSelection.reduceDimensionality(newData);
/* 271:524 */       this.m_Classifier.buildClassifier(newData);
/* 272:    */     }
/* 273:    */     else
/* 274:    */     {
/* 275:526 */       resampledData = this.m_AttributeSelection.reduceDimensionality(resampledData);
/* 276:527 */       this.m_Classifier.buildClassifier(resampledData);
/* 277:    */     }
/* 278:530 */     long end2 = System.currentTimeMillis();
/* 279:531 */     this.m_numAttributesSelected = this.m_AttributeSelection.numberAttributesSelected();
/* 280:532 */     this.m_ReducedHeader = new Instances((this.m_Classifier instanceof WeightedInstancesHandler) ? newData : resampledData, 0);
/* 281:    */     
/* 282:    */ 
/* 283:    */ 
/* 284:536 */     this.m_selectionTime = (end - start);
/* 285:537 */     this.m_totalTime = (end2 - start);
/* 286:    */   }
/* 287:    */   
/* 288:    */   public double[] distributionForInstance(Instance instance)
/* 289:    */     throws Exception
/* 290:    */   {
/* 291:    */     Instance newInstance;
/* 292:    */     Instance newInstance;
/* 293:552 */     if (this.m_AttributeSelection == null) {
/* 294:554 */       newInstance = instance;
/* 295:    */     } else {
/* 296:556 */       newInstance = this.m_AttributeSelection.reduceDimensionality(instance);
/* 297:    */     }
/* 298:559 */     return this.m_Classifier.distributionForInstance(newInstance);
/* 299:    */   }
/* 300:    */   
/* 301:    */   public int graphType()
/* 302:    */   {
/* 303:570 */     if ((this.m_Classifier instanceof Drawable)) {
/* 304:571 */       return ((Drawable)this.m_Classifier).graphType();
/* 305:    */     }
/* 306:573 */     return 0;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public String graph()
/* 310:    */     throws Exception
/* 311:    */   {
/* 312:584 */     if ((this.m_Classifier instanceof Drawable)) {
/* 313:585 */       return ((Drawable)this.m_Classifier).graph();
/* 314:    */     }
/* 315:586 */     throw new Exception("Classifier: " + getClassifierSpec() + " cannot be graphed");
/* 316:    */   }
/* 317:    */   
/* 318:    */   public String toString()
/* 319:    */   {
/* 320:596 */     if (this.m_AttributeSelection == null) {
/* 321:597 */       return "AttributeSelectedClassifier: No attribute selection possible.\n\n" + this.m_Classifier.toString();
/* 322:    */     }
/* 323:601 */     StringBuffer result = new StringBuffer();
/* 324:602 */     result.append("AttributeSelectedClassifier:\n\n");
/* 325:603 */     result.append(this.m_AttributeSelection.toResultsString());
/* 326:604 */     result.append("\n\nHeader of reduced data:\n" + this.m_ReducedHeader.toString());
/* 327:605 */     result.append("\n\nClassifier Model\n" + this.m_Classifier.toString());
/* 328:    */     
/* 329:607 */     return result.toString();
/* 330:    */   }
/* 331:    */   
/* 332:    */   public double measureNumAttributesSelected()
/* 333:    */   {
/* 334:615 */     return this.m_numAttributesSelected;
/* 335:    */   }
/* 336:    */   
/* 337:    */   public double measureSelectionTime()
/* 338:    */   {
/* 339:623 */     return this.m_selectionTime;
/* 340:    */   }
/* 341:    */   
/* 342:    */   public double measureTime()
/* 343:    */   {
/* 344:632 */     return this.m_totalTime;
/* 345:    */   }
/* 346:    */   
/* 347:    */   public Enumeration<String> enumerateMeasures()
/* 348:    */   {
/* 349:640 */     Vector<String> newVector = new Vector(3);
/* 350:641 */     newVector.addElement("measureNumAttributesSelected");
/* 351:642 */     newVector.addElement("measureSelectionTime");
/* 352:643 */     newVector.addElement("measureTime");
/* 353:644 */     if ((this.m_Classifier instanceof AdditionalMeasureProducer)) {
/* 354:645 */       newVector.addAll(Collections.list(((AdditionalMeasureProducer)this.m_Classifier).enumerateMeasures()));
/* 355:    */     }
/* 356:648 */     return newVector.elements();
/* 357:    */   }
/* 358:    */   
/* 359:    */   public double getMeasure(String additionalMeasureName)
/* 360:    */   {
/* 361:658 */     if (additionalMeasureName.compareToIgnoreCase("measureNumAttributesSelected") == 0) {
/* 362:659 */       return measureNumAttributesSelected();
/* 363:    */     }
/* 364:660 */     if (additionalMeasureName.compareToIgnoreCase("measureSelectionTime") == 0) {
/* 365:661 */       return measureSelectionTime();
/* 366:    */     }
/* 367:662 */     if (additionalMeasureName.compareToIgnoreCase("measureTime") == 0) {
/* 368:663 */       return measureTime();
/* 369:    */     }
/* 370:664 */     if ((this.m_Classifier instanceof AdditionalMeasureProducer)) {
/* 371:665 */       return ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(additionalMeasureName);
/* 372:    */     }
/* 373:668 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (AttributeSelectedClassifier)");
/* 374:    */   }
/* 375:    */   
/* 376:    */   public String getRevision()
/* 377:    */   {
/* 378:679 */     return RevisionUtils.extract("$Revision: 11461 $");
/* 379:    */   }
/* 380:    */   
/* 381:    */   public static void main(String[] argv)
/* 382:    */   {
/* 383:689 */     runClassifier(new AttributeSelectedClassifier(), argv);
/* 384:    */   }
/* 385:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.AttributeSelectedClassifier
 * JD-Core Version:    0.7.0.1
 */