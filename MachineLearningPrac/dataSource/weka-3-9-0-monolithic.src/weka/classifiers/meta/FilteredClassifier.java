/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.classifiers.IterativeClassifier;
/*   8:    */ import weka.classifiers.SingleClassifierEnhancer;
/*   9:    */ import weka.classifiers.trees.J48;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.BatchPredictor;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Capabilities.Capability;
/*  14:    */ import weka.core.Drawable;
/*  15:    */ import weka.core.Instance;
/*  16:    */ import weka.core.Instances;
/*  17:    */ import weka.core.Option;
/*  18:    */ import weka.core.OptionHandler;
/*  19:    */ import weka.core.PartitionGenerator;
/*  20:    */ import weka.core.RevisionUtils;
/*  21:    */ import weka.core.Utils;
/*  22:    */ import weka.core.WekaException;
/*  23:    */ import weka.filters.Filter;
/*  24:    */ import weka.filters.supervised.attribute.AttributeSelection;
/*  25:    */ import weka.filters.supervised.attribute.Discretize;
/*  26:    */ 
/*  27:    */ public class FilteredClassifier
/*  28:    */   extends SingleClassifierEnhancer
/*  29:    */   implements Drawable, PartitionGenerator, IterativeClassifier, BatchPredictor
/*  30:    */ {
/*  31:    */   static final long serialVersionUID = -4523450618538717400L;
/*  32:134 */   protected Filter m_Filter = new AttributeSelection();
/*  33:    */   protected Instances m_FilteredInstances;
/*  34:141 */   protected boolean m_DoNotCheckForModifiedClassAttribute = false;
/*  35:    */   
/*  36:    */   public String globalInfo()
/*  37:    */   {
/*  38:150 */     return "Class for running an arbitrary classifier on data that has been passed through an arbitrary filter. Like the classifier, the structure of the filter is based exclusively on the training data and test instances will be processed by the filter without changing their structure.";
/*  39:    */   }
/*  40:    */   
/*  41:    */   protected String defaultClassifierString()
/*  42:    */   {
/*  43:163 */     return "weka.classifiers.trees.J48";
/*  44:    */   }
/*  45:    */   
/*  46:    */   protected String defaultFilterString()
/*  47:    */   {
/*  48:171 */     return "weka.filters.supervised.attribute.Discretize";
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setDoNotCheckForModifiedClassAttribute(boolean flag)
/*  52:    */   {
/*  53:179 */     this.m_DoNotCheckForModifiedClassAttribute = flag;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public FilteredClassifier()
/*  57:    */   {
/*  58:187 */     this.m_Classifier = new J48();
/*  59:188 */     this.m_Filter = new Discretize();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public int graphType()
/*  63:    */   {
/*  64:198 */     if ((this.m_Classifier instanceof Drawable)) {
/*  65:199 */       return ((Drawable)this.m_Classifier).graphType();
/*  66:    */     }
/*  67:201 */     return 0;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String graph()
/*  71:    */     throws Exception
/*  72:    */   {
/*  73:212 */     if ((this.m_Classifier instanceof Drawable)) {
/*  74:213 */       return ((Drawable)this.m_Classifier).graph();
/*  75:    */     }
/*  76:215 */     throw new Exception("Classifier: " + getClassifierSpec() + " cannot be graphed");
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void generatePartition(Instances data)
/*  80:    */     throws Exception
/*  81:    */   {
/*  82:225 */     if ((this.m_Classifier instanceof PartitionGenerator)) {
/*  83:226 */       buildClassifier(data);
/*  84:    */     } else {
/*  85:228 */       throw new Exception("Classifier: " + getClassifierSpec() + " cannot generate a partition");
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public double[] getMembershipValues(Instance inst)
/*  90:    */     throws Exception
/*  91:    */   {
/*  92:238 */     if ((this.m_Classifier instanceof PartitionGenerator))
/*  93:    */     {
/*  94:239 */       Instance newInstance = filterInstance(inst);
/*  95:240 */       if (newInstance == null)
/*  96:    */       {
/*  97:241 */         double[] unclassified = new double[numElements()];
/*  98:242 */         for (int i = 0; i < unclassified.length; i++) {
/*  99:243 */           unclassified[i] = Utils.missingValue();
/* 100:    */         }
/* 101:245 */         return unclassified;
/* 102:    */       }
/* 103:247 */       return ((PartitionGenerator)this.m_Classifier).getMembershipValues(newInstance);
/* 104:    */     }
/* 105:251 */     throw new Exception("Classifier: " + getClassifierSpec() + " cannot generate a partition");
/* 106:    */   }
/* 107:    */   
/* 108:    */   public int numElements()
/* 109:    */     throws Exception
/* 110:    */   {
/* 111:261 */     if ((this.m_Classifier instanceof PartitionGenerator)) {
/* 112:262 */       return ((PartitionGenerator)this.m_Classifier).numElements();
/* 113:    */     }
/* 114:264 */     throw new Exception("Classifier: " + getClassifierSpec() + " cannot generate a partition");
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void initializeClassifier(Instances data)
/* 118:    */     throws Exception
/* 119:    */   {
/* 120:277 */     if ((this.m_Classifier instanceof IterativeClassifier)) {
/* 121:278 */       ((IterativeClassifier)this.m_Classifier).initializeClassifier(setUp(data));
/* 122:    */     } else {
/* 123:280 */       throw new Exception("Classifier: " + getClassifierSpec() + " is not an IterativeClassifier");
/* 124:    */     }
/* 125:    */   }
/* 126:    */   
/* 127:    */   public boolean next()
/* 128:    */     throws Exception
/* 129:    */   {
/* 130:292 */     if ((this.m_Classifier instanceof IterativeClassifier)) {
/* 131:293 */       return ((IterativeClassifier)this.m_Classifier).next();
/* 132:    */     }
/* 133:295 */     throw new Exception("Classifier: " + getClassifierSpec() + " is not an IterativeClassifier");
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void done()
/* 137:    */     throws Exception
/* 138:    */   {
/* 139:307 */     if ((this.m_Classifier instanceof IterativeClassifier)) {
/* 140:308 */       ((IterativeClassifier)this.m_Classifier).done();
/* 141:    */     } else {
/* 142:310 */       throw new Exception("Classifier: " + getClassifierSpec() + " is not an IterativeClassifier");
/* 143:    */     }
/* 144:    */   }
/* 145:    */   
/* 146:    */   public Enumeration<Option> listOptions()
/* 147:    */   {
/* 148:321 */     Vector<Option> newVector = new Vector(1);
/* 149:322 */     newVector.addElement(new Option("\tFull class name of filter to use, followed\n\tby filter options.\n\teg: \"weka.filters.unsupervised.attribute.Remove -V -R 1,2\"", "F", 1, "-F <filter specification>"));
/* 150:    */     
/* 151:    */ 
/* 152:    */ 
/* 153:    */ 
/* 154:    */ 
/* 155:328 */     newVector.addAll(Collections.list(super.listOptions()));
/* 156:330 */     if ((getFilter() instanceof OptionHandler))
/* 157:    */     {
/* 158:331 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to filter " + getFilter().getClass().getName() + ":"));
/* 159:    */       
/* 160:333 */       newVector.addAll(Collections.list(getFilter().listOptions()));
/* 161:    */     }
/* 162:337 */     return newVector.elements();
/* 163:    */   }
/* 164:    */   
/* 165:    */   public void setOptions(String[] options)
/* 166:    */     throws Exception
/* 167:    */   {
/* 168:431 */     String filterString = Utils.getOption('F', options);
/* 169:432 */     if (filterString.length() <= 0) {
/* 170:433 */       filterString = defaultFilterString();
/* 171:    */     }
/* 172:435 */     String[] filterSpec = Utils.splitOptions(filterString);
/* 173:436 */     if (filterSpec.length == 0) {
/* 174:437 */       throw new IllegalArgumentException("Invalid filter specification string");
/* 175:    */     }
/* 176:439 */     String filterName = filterSpec[0];
/* 177:440 */     filterSpec[0] = "";
/* 178:441 */     setFilter((Filter)Utils.forName(Filter.class, filterName, filterSpec));
/* 179:    */     
/* 180:443 */     super.setOptions(options);
/* 181:    */     
/* 182:445 */     Utils.checkForRemainingOptions(options);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public String[] getOptions()
/* 186:    */   {
/* 187:455 */     Vector<String> options = new Vector();
/* 188:    */     
/* 189:457 */     options.add("-F");
/* 190:458 */     options.add("" + getFilterSpec());
/* 191:    */     
/* 192:460 */     Collections.addAll(options, super.getOptions());
/* 193:    */     
/* 194:462 */     return (String[])options.toArray(new String[0]);
/* 195:    */   }
/* 196:    */   
/* 197:    */   public String filterTipText()
/* 198:    */   {
/* 199:472 */     return "The filter to be used.";
/* 200:    */   }
/* 201:    */   
/* 202:    */   public void setFilter(Filter filter)
/* 203:    */   {
/* 204:482 */     this.m_Filter = filter;
/* 205:    */   }
/* 206:    */   
/* 207:    */   public Filter getFilter()
/* 208:    */   {
/* 209:492 */     return this.m_Filter;
/* 210:    */   }
/* 211:    */   
/* 212:    */   protected String getFilterSpec()
/* 213:    */   {
/* 214:503 */     Filter c = getFilter();
/* 215:504 */     if ((c instanceof OptionHandler)) {
/* 216:505 */       return c.getClass().getName() + " " + Utils.joinOptions(c.getOptions());
/* 217:    */     }
/* 218:508 */     return c.getClass().getName();
/* 219:    */   }
/* 220:    */   
/* 221:    */   public Capabilities getCapabilities()
/* 222:    */   {
/* 223:    */     Capabilities result;
/* 224:    */     Capabilities result;
/* 225:519 */     if (getFilter() == null) {
/* 226:520 */       result = super.getCapabilities();
/* 227:    */     } else {
/* 228:522 */       result = getFilter().getCapabilities();
/* 229:    */     }
/* 230:525 */     result.disable(Capabilities.Capability.NO_CLASS);
/* 231:528 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/* 232:529 */       result.enableDependency(cap);
/* 233:    */     }
/* 234:531 */     return result;
/* 235:    */   }
/* 236:    */   
/* 237:    */   protected Instances setUp(Instances data)
/* 238:    */     throws Exception
/* 239:    */   {
/* 240:541 */     if (this.m_Classifier == null) {
/* 241:542 */       throw new Exception("No base classifiers have been set!");
/* 242:    */     }
/* 243:545 */     getCapabilities().testWithFail(data);
/* 244:    */     
/* 245:    */ 
/* 246:548 */     data = new Instances(data);
/* 247:    */     
/* 248:    */ 
/* 249:    */ 
/* 250:    */ 
/* 251:    */ 
/* 252:    */ 
/* 253:555 */     Attribute classAttribute = (Attribute)data.classAttribute().copy();
/* 254:556 */     this.m_Filter.setInputFormat(data);
/* 255:557 */     data = Filter.useFilter(data, this.m_Filter);
/* 256:558 */     if ((!classAttribute.equals(data.classAttribute())) && (!this.m_DoNotCheckForModifiedClassAttribute)) {
/* 257:559 */       throw new IllegalArgumentException("Cannot proceed: " + getFilterSpec() + " has modified the class attribute!");
/* 258:    */     }
/* 259:564 */     getClassifier().getCapabilities().testWithFail(data);
/* 260:    */     
/* 261:566 */     this.m_FilteredInstances = data.stringFreeStructure();
/* 262:567 */     return data;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public void buildClassifier(Instances data)
/* 266:    */     throws Exception
/* 267:    */   {
/* 268:578 */     this.m_Classifier.buildClassifier(setUp(data));
/* 269:    */   }
/* 270:    */   
/* 271:    */   protected Instance filterInstance(Instance instance)
/* 272:    */     throws Exception
/* 273:    */   {
/* 274:590 */     if (this.m_Filter.numPendingOutput() > 0) {
/* 275:591 */       throw new Exception("Filter output queue not empty!");
/* 276:    */     }
/* 277:598 */     if (!this.m_Filter.input(instance))
/* 278:    */     {
/* 279:599 */       if (!this.m_Filter.mayRemoveInstanceAfterFirstBatchDone()) {
/* 280:600 */         throw new Exception("Filter didn't make the test instance immediately available!");
/* 281:    */       }
/* 282:603 */       this.m_Filter.batchFinished();
/* 283:604 */       return null;
/* 284:    */     }
/* 285:607 */     this.m_Filter.batchFinished();
/* 286:608 */     return this.m_Filter.output();
/* 287:    */   }
/* 288:    */   
/* 289:    */   public double[] distributionForInstance(Instance instance)
/* 290:    */     throws Exception
/* 291:    */   {
/* 292:625 */     Instance newInstance = filterInstance(instance);
/* 293:626 */     if (newInstance == null)
/* 294:    */     {
/* 295:631 */       double[] unclassified = null;
/* 296:632 */       if (instance.classAttribute().isNumeric())
/* 297:    */       {
/* 298:633 */         unclassified = new double[1];
/* 299:634 */         unclassified[0] = Utils.missingValue();
/* 300:    */       }
/* 301:    */       else
/* 302:    */       {
/* 303:637 */         unclassified = new double[instance.classAttribute().numValues()];
/* 304:    */       }
/* 305:639 */       return unclassified;
/* 306:    */     }
/* 307:641 */     return this.m_Classifier.distributionForInstance(newInstance);
/* 308:    */   }
/* 309:    */   
/* 310:    */   public String batchSizeTipText()
/* 311:    */   {
/* 312:651 */     return "Batch size to use if base learner is a BatchPredictor";
/* 313:    */   }
/* 314:    */   
/* 315:    */   public void setBatchSize(String size)
/* 316:    */   {
/* 317:662 */     if ((getClassifier() instanceof BatchPredictor)) {
/* 318:663 */       ((BatchPredictor)getClassifier()).setBatchSize(size);
/* 319:    */     }
/* 320:    */   }
/* 321:    */   
/* 322:    */   public String getBatchSize()
/* 323:    */   {
/* 324:675 */     if ((getClassifier() instanceof BatchPredictor)) {
/* 325:676 */       return ((BatchPredictor)getClassifier()).getBatchSize();
/* 326:    */     }
/* 327:678 */     return "1";
/* 328:    */   }
/* 329:    */   
/* 330:    */   public double[][] distributionsForInstances(Instances insts)
/* 331:    */     throws Exception
/* 332:    */   {
/* 333:694 */     if ((getClassifier() instanceof BatchPredictor))
/* 334:    */     {
/* 335:695 */       Instances filteredInsts = Filter.useFilter(insts, this.m_Filter);
/* 336:696 */       if (filteredInsts.numInstances() != insts.numInstances()) {
/* 337:697 */         throw new WekaException("FilteredClassifier: filter has returned more/less instances than required.");
/* 338:    */       }
/* 339:700 */       return ((BatchPredictor)getClassifier()).distributionsForInstances(filteredInsts);
/* 340:    */     }
/* 341:703 */     double[][] result = new double[insts.numInstances()][insts.numClasses()];
/* 342:704 */     for (int i = 0; i < insts.numInstances(); i++) {
/* 343:705 */       result[i] = distributionForInstance(insts.instance(i));
/* 344:    */     }
/* 345:707 */     return result;
/* 346:    */   }
/* 347:    */   
/* 348:    */   public boolean implementsMoreEfficientBatchPrediction()
/* 349:    */   {
/* 350:719 */     if (!(getClassifier() instanceof BatchPredictor)) {
/* 351:720 */       return false;
/* 352:    */     }
/* 353:723 */     return ((BatchPredictor)getClassifier()).implementsMoreEfficientBatchPrediction();
/* 354:    */   }
/* 355:    */   
/* 356:    */   public String toString()
/* 357:    */   {
/* 358:734 */     if (this.m_FilteredInstances == null) {
/* 359:735 */       return "FilteredClassifier: No model built yet.";
/* 360:    */     }
/* 361:738 */     String result = "FilteredClassifier using " + getClassifierSpec() + " on data filtered through " + getFilterSpec() + "\n\nFiltered Header\n" + this.m_FilteredInstances.toString() + "\n\nClassifier Model\n" + this.m_Classifier.toString();
/* 362:    */     
/* 363:    */ 
/* 364:    */ 
/* 365:742 */     return result;
/* 366:    */   }
/* 367:    */   
/* 368:    */   public String getRevision()
/* 369:    */   {
/* 370:751 */     return RevisionUtils.extract("$Revision: 12647 $");
/* 371:    */   }
/* 372:    */   
/* 373:    */   public static void main(String[] argv)
/* 374:    */   {
/* 375:761 */     runClassifier(new FilteredClassifier(), argv);
/* 376:    */   }
/* 377:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.FilteredClassifier
 * JD-Core Version:    0.7.0.1
 */