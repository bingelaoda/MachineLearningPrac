/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.classifiers.Classifier;
/*   9:    */ import weka.classifiers.SingleClassifierEnhancer;
/*  10:    */ import weka.classifiers.rules.ZeroR;
/*  11:    */ import weka.classifiers.trees.J48;
/*  12:    */ import weka.core.BatchPredictor;
/*  13:    */ import weka.core.Capabilities;
/*  14:    */ import weka.core.Capabilities.Capability;
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
/*  26:    */ import weka.filters.unsupervised.attribute.MakeIndicator;
/*  27:    */ 
/*  28:    */ public class OrdinalClassClassifier
/*  29:    */   extends SingleClassifierEnhancer
/*  30:    */   implements OptionHandler, TechnicalInformationHandler, BatchPredictor
/*  31:    */ {
/*  32:    */   static final long serialVersionUID = -3461971774059603636L;
/*  33:    */   private Classifier[] m_Classifiers;
/*  34:    */   private MakeIndicator[] m_ClassFilters;
/*  35:    */   private ZeroR m_ZeroR;
/*  36:189 */   private boolean m_UseSmoothing = true;
/*  37:    */   
/*  38:    */   protected String defaultClassifierString()
/*  39:    */   {
/*  40:199 */     return "weka.classifiers.trees.J48";
/*  41:    */   }
/*  42:    */   
/*  43:    */   public OrdinalClassClassifier()
/*  44:    */   {
/*  45:206 */     this.m_Classifier = new J48();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String globalInfo()
/*  49:    */   {
/*  50:216 */     return "Meta classifier that allows standard classification algorithms to be applied to ordinal class problems.\n\nFor more information see: \n\n" + getTechnicalInformation().toString();
/*  51:    */   }
/*  52:    */   
/*  53:    */   public TechnicalInformation getTechnicalInformation()
/*  54:    */   {
/*  55:233 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  56:234 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank and Mark Hall");
/*  57:235 */     result.setValue(TechnicalInformation.Field.TITLE, "A Simple Approach to Ordinal Classification");
/*  58:236 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "12th European Conference on Machine Learning");
/*  59:    */     
/*  60:238 */     result.setValue(TechnicalInformation.Field.YEAR, "2001");
/*  61:239 */     result.setValue(TechnicalInformation.Field.PAGES, "145-156");
/*  62:240 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  63:    */     
/*  64:242 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*  65:243 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Robert E. Schapire and Peter Stone and David A. McAllester and Michael L. Littman and Janos A. Csirik");
/*  66:    */     
/*  67:    */ 
/*  68:246 */     additional.setValue(TechnicalInformation.Field.TITLE, "Modeling Auction Price Uncertainty Using Boosting-based Conditional Density Estimation");
/*  69:    */     
/*  70:    */ 
/*  71:249 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Machine Learning, Proceedings of the Nineteenth International Conference (ICML 2002)");
/*  72:    */     
/*  73:    */ 
/*  74:252 */     additional.setValue(TechnicalInformation.Field.YEAR, "2002");
/*  75:253 */     additional.setValue(TechnicalInformation.Field.PAGES, "546-553");
/*  76:254 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
/*  77:    */     
/*  78:256 */     return result;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public Capabilities getCapabilities()
/*  82:    */   {
/*  83:266 */     Capabilities result = super.getCapabilities();
/*  84:    */     
/*  85:    */ 
/*  86:269 */     result.disableAllClasses();
/*  87:270 */     result.disableAllClassDependencies();
/*  88:271 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  89:    */     
/*  90:273 */     return result;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void buildClassifier(Instances insts)
/*  94:    */     throws Exception
/*  95:    */   {
/*  96:288 */     getCapabilities().testWithFail(insts);
/*  97:    */     
/*  98:    */ 
/*  99:291 */     insts = new Instances(insts);
/* 100:292 */     insts.deleteWithMissingClass();
/* 101:294 */     if (this.m_Classifier == null) {
/* 102:295 */       throw new Exception("No base classifier has been set!");
/* 103:    */     }
/* 104:297 */     this.m_ZeroR = new ZeroR();
/* 105:298 */     this.m_ZeroR.buildClassifier(insts);
/* 106:    */     
/* 107:300 */     int numClassifiers = insts.numClasses() - 1;
/* 108:    */     
/* 109:302 */     numClassifiers = numClassifiers == 0 ? 1 : numClassifiers;
/* 110:304 */     if (numClassifiers == 1)
/* 111:    */     {
/* 112:305 */       this.m_Classifiers = AbstractClassifier.makeCopies(this.m_Classifier, 1);
/* 113:306 */       this.m_Classifiers[0].buildClassifier(insts);
/* 114:    */     }
/* 115:    */     else
/* 116:    */     {
/* 117:308 */       this.m_Classifiers = AbstractClassifier.makeCopies(this.m_Classifier, numClassifiers);
/* 118:    */       
/* 119:310 */       this.m_ClassFilters = new MakeIndicator[numClassifiers];
/* 120:312 */       for (int i = 0; i < this.m_Classifiers.length; i++)
/* 121:    */       {
/* 122:313 */         this.m_ClassFilters[i] = new MakeIndicator();
/* 123:314 */         this.m_ClassFilters[i].setAttributeIndex("" + (insts.classIndex() + 1));
/* 124:315 */         this.m_ClassFilters[i].setValueIndices("" + (i + 2) + "-last");
/* 125:316 */         this.m_ClassFilters[i].setNumeric(false);
/* 126:317 */         this.m_ClassFilters[i].setInputFormat(insts);
/* 127:318 */         Instances newInsts = Filter.useFilter(insts, this.m_ClassFilters[i]);
/* 128:319 */         this.m_Classifiers[i].buildClassifier(newInsts);
/* 129:    */       }
/* 130:    */     }
/* 131:    */   }
/* 132:    */   
/* 133:    */   protected double[] computeProbabilities(double[][] distributions, int numClasses)
/* 134:    */   {
/* 135:329 */     double[] probs = new double[numClasses];
/* 136:332 */     if (getUseSmoothing())
/* 137:    */     {
/* 138:334 */       double[] fScores = new double[distributions.length + 2];
/* 139:335 */       fScores[0] = 1.0D;
/* 140:336 */       fScores[(distributions.length + 1)] = 0.0D;
/* 141:337 */       for (int i = 0; i < distributions.length; i++) {
/* 142:338 */         fScores[(i + 1)] = distributions[i][1];
/* 143:    */       }
/* 144:342 */       int[] sortOrder = Utils.sort(fScores);
/* 145:    */       
/* 146:    */ 
/* 147:345 */       int minSoFar = sortOrder[0];
/* 148:346 */       int index = 0;
/* 149:347 */       double[] pointwiseMaxLowerBound = new double[fScores.length];
/* 150:348 */       for (int i = 0; i < sortOrder.length; i++)
/* 151:    */       {
/* 152:351 */         while (minSoFar > sortOrder.length - i - 1) {
/* 153:352 */           minSoFar = sortOrder[(++index)];
/* 154:    */         }
/* 155:354 */         pointwiseMaxLowerBound[(sortOrder.length - i - 1)] = fScores[minSoFar];
/* 156:    */       }
/* 157:358 */       int[] newSortOrder = new int[sortOrder.length];
/* 158:359 */       for (int i = sortOrder.length - 1; i >= 0; i--) {
/* 159:360 */         newSortOrder[(sortOrder.length - i - 1)] = sortOrder[i];
/* 160:    */       }
/* 161:362 */       sortOrder = newSortOrder;
/* 162:    */       
/* 163:    */ 
/* 164:365 */       int maxSoFar = sortOrder[0];
/* 165:366 */       index = 0;
/* 166:367 */       double[] pointwiseMinUpperBound = new double[fScores.length];
/* 167:368 */       for (int i = 0; i < sortOrder.length; i++)
/* 168:    */       {
/* 169:371 */         while (maxSoFar < i) {
/* 170:372 */           maxSoFar = sortOrder[(++index)];
/* 171:    */         }
/* 172:374 */         pointwiseMinUpperBound[i] = fScores[maxSoFar];
/* 173:    */       }
/* 174:378 */       for (int i = 0; i < distributions.length; i++) {
/* 175:379 */         distributions[i][1] = ((pointwiseMinUpperBound[(i + 1)] + pointwiseMaxLowerBound[(i + 1)]) / 2.0D);
/* 176:    */       }
/* 177:    */     }
/* 178:383 */     for (int i = 0; i < numClasses; i++) {
/* 179:384 */       if (i == 0)
/* 180:    */       {
/* 181:385 */         probs[i] = (1.0D - distributions[0][1]);
/* 182:    */       }
/* 183:386 */       else if (i == numClasses - 1)
/* 184:    */       {
/* 185:387 */         probs[i] = distributions[(i - 1)][1];
/* 186:    */       }
/* 187:    */       else
/* 188:    */       {
/* 189:389 */         probs[i] = (distributions[(i - 1)][1] - distributions[i][1]);
/* 190:390 */         if (probs[i] < 0.0D)
/* 191:    */         {
/* 192:391 */           System.err.println("Warning: estimated probability " + probs[i] + ". Rounding to 0.");
/* 193:    */           
/* 194:393 */           probs[i] = 0.0D;
/* 195:    */         }
/* 196:    */       }
/* 197:    */     }
/* 198:398 */     return probs;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public double[] distributionForInstance(Instance inst)
/* 202:    */     throws Exception
/* 203:    */   {
/* 204:411 */     if (this.m_Classifiers.length == 1) {
/* 205:412 */       return this.m_Classifiers[0].distributionForInstance(inst);
/* 206:    */     }
/* 207:415 */     double[][] distributions = new double[this.m_ClassFilters.length][0];
/* 208:416 */     for (int i = 0; i < this.m_ClassFilters.length; i++)
/* 209:    */     {
/* 210:417 */       this.m_ClassFilters[i].input(inst);
/* 211:418 */       this.m_ClassFilters[i].batchFinished();
/* 212:419 */       distributions[i] = this.m_Classifiers[i].distributionForInstance(this.m_ClassFilters[i].output());
/* 213:    */     }
/* 214:422 */     double[] probs = computeProbabilities(distributions, inst.numClasses());
/* 215:424 */     if (Utils.gr(Utils.sum(probs), 0.0D))
/* 216:    */     {
/* 217:425 */       Utils.normalize(probs);
/* 218:426 */       return probs;
/* 219:    */     }
/* 220:428 */     return this.m_ZeroR.distributionForInstance(inst);
/* 221:    */   }
/* 222:    */   
/* 223:    */   public String batchSizeTipText()
/* 224:    */   {
/* 225:437 */     return "Preferred batch size for prediction";
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void setBatchSize(String i) {}
/* 229:    */   
/* 230:    */   public String getBatchSize()
/* 231:    */   {
/* 232:450 */     return "";
/* 233:    */   }
/* 234:    */   
/* 235:    */   public boolean implementsMoreEfficientBatchPrediction()
/* 236:    */   {
/* 237:461 */     if (!(getClassifier() instanceof BatchPredictor)) {
/* 238:462 */       return false;
/* 239:    */     }
/* 240:465 */     return ((BatchPredictor)getClassifier()).implementsMoreEfficientBatchPrediction();
/* 241:    */   }
/* 242:    */   
/* 243:    */   public double[][] distributionsForInstances(Instances insts)
/* 244:    */     throws Exception
/* 245:    */   {
/* 246:481 */     if (this.m_Classifiers.length == 1)
/* 247:    */     {
/* 248:482 */       if ((this.m_Classifier instanceof BatchPredictor)) {
/* 249:483 */         return ((BatchPredictor)this.m_Classifiers[0]).distributionsForInstances(insts);
/* 250:    */       }
/* 251:485 */       double[][] dists = new double[insts.numInstances()][];
/* 252:486 */       for (int i = 0; i < dists.length; i++) {
/* 253:487 */         dists[i] = this.m_Classifiers[0].distributionForInstance(insts.instance(i));
/* 254:    */       }
/* 255:489 */       return dists;
/* 256:    */     }
/* 257:492 */     double[][][] distributions = new double[insts.numInstances()][this.m_ClassFilters.length][0];
/* 258:493 */     for (int i = 0; i < this.m_ClassFilters.length; i++) {
/* 259:494 */       if ((this.m_Classifier instanceof BatchPredictor))
/* 260:    */       {
/* 261:495 */         Instances filtered = Filter.useFilter(insts, this.m_ClassFilters[i]);
/* 262:496 */         double[][] currentDist = ((BatchPredictor)this.m_Classifiers[i]).distributionsForInstances(filtered);
/* 263:497 */         for (int j = 0; j < currentDist.length; j++) {
/* 264:498 */           distributions[j][i] = currentDist[j];
/* 265:    */         }
/* 266:    */       }
/* 267:    */       else
/* 268:    */       {
/* 269:501 */         for (int j = 0; j < insts.numInstances(); j++)
/* 270:    */         {
/* 271:502 */           this.m_ClassFilters[i].input(insts.instance(j));
/* 272:503 */           this.m_ClassFilters[i].batchFinished();
/* 273:504 */           distributions[j][i] = this.m_Classifiers[i].distributionForInstance(this.m_ClassFilters[i].output());
/* 274:    */         }
/* 275:    */       }
/* 276:    */     }
/* 277:509 */     double[][] probs = new double[insts.numInstances()][];
/* 278:510 */     for (int i = 0; i < probs.length; i++)
/* 279:    */     {
/* 280:511 */       probs[i] = computeProbabilities(distributions[i], insts.numClasses());
/* 281:512 */       if (Utils.gr(Utils.sum(probs[i]), 0.0D)) {
/* 282:513 */         Utils.normalize(probs[i]);
/* 283:    */       } else {
/* 284:515 */         probs[i] = this.m_ZeroR.distributionForInstance(insts.instance(i));
/* 285:    */       }
/* 286:    */     }
/* 287:518 */     return probs;
/* 288:    */   }
/* 289:    */   
/* 290:    */   public Enumeration<Option> listOptions()
/* 291:    */   {
/* 292:529 */     Vector<Option> vec = new Vector();
/* 293:530 */     vec.addElement(new Option("\tTurn off Schapire et al.'s smoothing heuristic (ICML02, pp. 550).", "S", 0, "-S"));
/* 294:    */     
/* 295:    */ 
/* 296:533 */     vec.addAll(Collections.list(super.listOptions()));
/* 297:    */     
/* 298:535 */     return vec.elements();
/* 299:    */   }
/* 300:    */   
/* 301:    */   public void setOptions(String[] options)
/* 302:    */     throws Exception
/* 303:    */   {
/* 304:622 */     setUseSmoothing(!Utils.getFlag('S', options));
/* 305:623 */     super.setOptions(options);
/* 306:    */     
/* 307:625 */     Utils.checkForRemainingOptions(options);
/* 308:    */   }
/* 309:    */   
/* 310:    */   public String[] getOptions()
/* 311:    */   {
/* 312:636 */     Vector<String> options = new Vector();
/* 313:638 */     if (!getUseSmoothing()) {
/* 314:639 */       options.add("-S");
/* 315:    */     }
/* 316:642 */     Collections.addAll(options, super.getOptions());
/* 317:    */     
/* 318:644 */     return (String[])options.toArray(new String[0]);
/* 319:    */   }
/* 320:    */   
/* 321:    */   public String useSmoothingTipText()
/* 322:    */   {
/* 323:653 */     return "If true, use Schapire et al.'s heuristic (ICML02, pp. 550).";
/* 324:    */   }
/* 325:    */   
/* 326:    */   public void setUseSmoothing(boolean b)
/* 327:    */   {
/* 328:663 */     this.m_UseSmoothing = b;
/* 329:    */   }
/* 330:    */   
/* 331:    */   public boolean getUseSmoothing()
/* 332:    */   {
/* 333:673 */     return this.m_UseSmoothing;
/* 334:    */   }
/* 335:    */   
/* 336:    */   public String toString()
/* 337:    */   {
/* 338:684 */     if (this.m_Classifiers == null) {
/* 339:685 */       return "OrdinalClassClassifier: No model built yet.";
/* 340:    */     }
/* 341:687 */     StringBuffer text = new StringBuffer();
/* 342:688 */     text.append("OrdinalClassClassifier\n\n");
/* 343:689 */     for (int i = 0; i < this.m_Classifiers.length; i++)
/* 344:    */     {
/* 345:690 */       text.append("Classifier ").append(i + 1);
/* 346:691 */       if (this.m_Classifiers[i] != null)
/* 347:    */       {
/* 348:692 */         if ((this.m_ClassFilters != null) && (this.m_ClassFilters[i] != null))
/* 349:    */         {
/* 350:693 */           text.append(", using indicator values: ");
/* 351:694 */           text.append(this.m_ClassFilters[i].getValueRange());
/* 352:    */         }
/* 353:696 */         text.append('\n');
/* 354:697 */         text.append(this.m_Classifiers[i].toString() + "\n");
/* 355:    */       }
/* 356:    */       else
/* 357:    */       {
/* 358:699 */         text.append(" Skipped (no training examples)\n");
/* 359:    */       }
/* 360:    */     }
/* 361:703 */     return text.toString();
/* 362:    */   }
/* 363:    */   
/* 364:    */   public String getRevision()
/* 365:    */   {
/* 366:713 */     return RevisionUtils.extract("$Revision: 12181 $");
/* 367:    */   }
/* 368:    */   
/* 369:    */   public static void main(String[] argv)
/* 370:    */   {
/* 371:722 */     runClassifier(new OrdinalClassClassifier(), argv);
/* 372:    */   }
/* 373:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.OrdinalClassClassifier
 * JD-Core Version:    0.7.0.1
 */