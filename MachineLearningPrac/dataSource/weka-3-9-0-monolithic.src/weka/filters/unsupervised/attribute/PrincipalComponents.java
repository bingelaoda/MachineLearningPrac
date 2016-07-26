/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import no.uib.cipr.matrix.Matrices;
/*   7:    */ import no.uib.cipr.matrix.SymmDenseEVD;
/*   8:    */ import no.uib.cipr.matrix.UpperSymmDenseMatrix;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.DenseInstance;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.SparseInstance;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.filters.Filter;
/*  21:    */ import weka.filters.UnsupervisedFilter;
/*  22:    */ 
/*  23:    */ public class PrincipalComponents
/*  24:    */   extends Filter
/*  25:    */   implements OptionHandler, UnsupervisedFilter
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = -5649876869480249303L;
/*  28:    */   protected Instances m_TrainInstances;
/*  29:    */   protected Instances m_TrainCopy;
/*  30:    */   protected Instances m_TransformedFormat;
/*  31:    */   protected boolean m_HasClass;
/*  32:    */   protected int m_ClassIndex;
/*  33:    */   protected int m_NumAttribs;
/*  34:    */   protected int m_NumInstances;
/*  35:    */   protected UpperSymmDenseMatrix m_Correlation;
/*  36:125 */   private boolean m_center = false;
/*  37:    */   protected double[][] m_Eigenvectors;
/*  38:134 */   protected double[] m_Eigenvalues = null;
/*  39:    */   protected int[] m_SortedEigens;
/*  40:140 */   protected double m_SumOfEigenValues = 0.0D;
/*  41:    */   protected ReplaceMissingValues m_ReplaceMissingFilter;
/*  42:    */   protected NominalToBinary m_NominalToBinaryFilter;
/*  43:    */   protected Remove m_AttributeFilter;
/*  44:    */   protected Standardize m_standardizeFilter;
/*  45:    */   protected Center m_centerFilter;
/*  46:158 */   protected int m_OutputNumAtts = -1;
/*  47:164 */   protected double m_CoverVariance = 0.95D;
/*  48:167 */   protected int m_MaxAttrsInName = 5;
/*  49:170 */   protected int m_MaxAttributes = -1;
/*  50:    */   
/*  51:    */   public String globalInfo()
/*  52:    */   {
/*  53:179 */     return "Performs a principal components analysis and transformation of the data.\nDimensionality reduction is accomplished by choosing enough eigenvectors to account for some percentage of the variance in the original data -- default 0.95 (95%).\nBased on code of the attribute selection scheme 'PrincipalComponents' by Mark Hall and Gabi Schmidberger.";
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Enumeration<Option> listOptions()
/*  57:    */   {
/*  58:196 */     Vector<Option> result = new Vector();
/*  59:    */     
/*  60:198 */     result.addElement(new Option("\tCenter (rather than standardize) the\n\tdata and compute PCA using the covariance (rather\n\t than the correlation) matrix.", "C", 0, "-C"));
/*  61:    */     
/*  62:    */ 
/*  63:    */ 
/*  64:202 */     result.addElement(new Option("\tRetain enough PC attributes to account\n\tfor this proportion of variance in the original data.\n\t(default: 0.95)", "R", 1, "-R <num>"));
/*  65:    */     
/*  66:    */ 
/*  67:    */ 
/*  68:206 */     result.addElement(new Option("\tMaximum number of attributes to include in \n\ttransformed attribute names.\n\t(-1 = include all, default: 5)", "A", 1, "-A <num>"));
/*  69:    */     
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:211 */     result.addElement(new Option("\tMaximum number of PC attributes to retain.\n\t(-1 = include all, default: -1)", "M", 1, "-M <num>"));
/*  74:    */     
/*  75:    */ 
/*  76:    */ 
/*  77:215 */     return result.elements();
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setOptions(String[] options)
/*  81:    */     throws Exception
/*  82:    */   {
/*  83:260 */     String tmpStr = Utils.getOption('R', options);
/*  84:261 */     if (tmpStr.length() != 0) {
/*  85:262 */       setVarianceCovered(Double.parseDouble(tmpStr));
/*  86:    */     } else {
/*  87:264 */       setVarianceCovered(0.95D);
/*  88:    */     }
/*  89:267 */     tmpStr = Utils.getOption('A', options);
/*  90:268 */     if (tmpStr.length() != 0) {
/*  91:269 */       setMaximumAttributeNames(Integer.parseInt(tmpStr));
/*  92:    */     } else {
/*  93:271 */       setMaximumAttributeNames(5);
/*  94:    */     }
/*  95:274 */     tmpStr = Utils.getOption('M', options);
/*  96:275 */     if (tmpStr.length() != 0) {
/*  97:276 */       setMaximumAttributes(Integer.parseInt(tmpStr));
/*  98:    */     } else {
/*  99:278 */       setMaximumAttributes(-1);
/* 100:    */     }
/* 101:281 */     setCenterData(Utils.getFlag('C', options));
/* 102:    */     
/* 103:283 */     Utils.checkForRemainingOptions(options);
/* 104:    */   }
/* 105:    */   
/* 106:    */   public String[] getOptions()
/* 107:    */   {
/* 108:294 */     Vector<String> result = new Vector();
/* 109:    */     
/* 110:296 */     result.add("-R");
/* 111:297 */     result.add("" + getVarianceCovered());
/* 112:    */     
/* 113:299 */     result.add("-A");
/* 114:300 */     result.add("" + getMaximumAttributeNames());
/* 115:    */     
/* 116:302 */     result.add("-M");
/* 117:303 */     result.add("" + getMaximumAttributes());
/* 118:305 */     if (getCenterData()) {
/* 119:306 */       result.add("-C");
/* 120:    */     }
/* 121:309 */     return (String[])result.toArray(new String[result.size()]);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public String centerDataTipText()
/* 125:    */   {
/* 126:319 */     return "Center (rather than standardize) the data. PCA will be computed from the covariance (rather than correlation) matrix";
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void setCenterData(boolean center)
/* 130:    */   {
/* 131:330 */     this.m_center = center;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public boolean getCenterData()
/* 135:    */   {
/* 136:340 */     return this.m_center;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public String varianceCoveredTipText()
/* 140:    */   {
/* 141:350 */     return "Retain enough PC attributes to account for this proportion of variance.";
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void setVarianceCovered(double value)
/* 145:    */   {
/* 146:360 */     this.m_CoverVariance = value;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public double getVarianceCovered()
/* 150:    */   {
/* 151:370 */     return this.m_CoverVariance;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public String maximumAttributeNamesTipText()
/* 155:    */   {
/* 156:380 */     return "The maximum number of attributes to include in transformed attribute names.";
/* 157:    */   }
/* 158:    */   
/* 159:    */   public void setMaximumAttributeNames(int value)
/* 160:    */   {
/* 161:390 */     this.m_MaxAttrsInName = value;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public int getMaximumAttributeNames()
/* 165:    */   {
/* 166:400 */     return this.m_MaxAttrsInName;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public String maximumAttributesTipText()
/* 170:    */   {
/* 171:410 */     return "The maximum number of PC attributes to retain.";
/* 172:    */   }
/* 173:    */   
/* 174:    */   public void setMaximumAttributes(int value)
/* 175:    */   {
/* 176:419 */     this.m_MaxAttributes = value;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public int getMaximumAttributes()
/* 180:    */   {
/* 181:428 */     return this.m_MaxAttributes;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public Capabilities getCapabilities()
/* 185:    */   {
/* 186:439 */     Capabilities result = super.getCapabilities();
/* 187:440 */     result.disableAll();
/* 188:    */     
/* 189:    */ 
/* 190:443 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 191:444 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 192:445 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 193:446 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 194:    */     
/* 195:    */ 
/* 196:449 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 197:450 */     result.enable(Capabilities.Capability.UNARY_CLASS);
/* 198:451 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 199:452 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 200:453 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 201:454 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 202:    */     
/* 203:456 */     return result;
/* 204:    */   }
/* 205:    */   
/* 206:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 207:    */     throws Exception
/* 208:    */   {
/* 209:483 */     if (this.m_Eigenvalues == null) {
/* 210:484 */       return inputFormat;
/* 211:    */     }
/* 212:    */     int numAttsLowerBound;
/* 213:    */     int numAttsLowerBound;
/* 214:487 */     if (this.m_MaxAttributes > 0) {
/* 215:488 */       numAttsLowerBound = this.m_NumAttribs - this.m_MaxAttributes;
/* 216:    */     } else {
/* 217:490 */       numAttsLowerBound = 0;
/* 218:    */     }
/* 219:492 */     if (numAttsLowerBound < 0) {
/* 220:493 */       numAttsLowerBound = 0;
/* 221:    */     }
/* 222:496 */     double cumulative = 0.0D;
/* 223:497 */     ArrayList<Attribute> attributes = new ArrayList();
/* 224:498 */     for (int i = this.m_NumAttribs - 1; i >= numAttsLowerBound; i--)
/* 225:    */     {
/* 226:499 */       StringBuffer attName = new StringBuffer();
/* 227:    */       
/* 228:501 */       double[] coeff_mags = new double[this.m_NumAttribs];
/* 229:502 */       for (int j = 0; j < this.m_NumAttribs; j++) {
/* 230:503 */         coeff_mags[j] = (-Math.abs(this.m_Eigenvectors[j][this.m_SortedEigens[i]]));
/* 231:    */       }
/* 232:505 */       int num_attrs = this.m_MaxAttrsInName > 0 ? Math.min(this.m_NumAttribs, this.m_MaxAttrsInName) : this.m_NumAttribs;
/* 233:    */       int[] coeff_inds;
/* 234:    */       int[] coeff_inds;
/* 235:509 */       if (this.m_NumAttribs > 0)
/* 236:    */       {
/* 237:511 */         coeff_inds = Utils.sort(coeff_mags);
/* 238:    */       }
/* 239:    */       else
/* 240:    */       {
/* 241:514 */         coeff_inds = new int[this.m_NumAttribs];
/* 242:515 */         for (j = 0; j < this.m_NumAttribs; j++) {
/* 243:516 */           coeff_inds[j] = j;
/* 244:    */         }
/* 245:    */       }
/* 246:520 */       for (j = 0; j < num_attrs; j++)
/* 247:    */       {
/* 248:521 */         double coeff_value = this.m_Eigenvectors[coeff_inds[j]][this.m_SortedEigens[i]];
/* 249:522 */         if ((j > 0) && (coeff_value >= 0.0D)) {
/* 250:523 */           attName.append("+");
/* 251:    */         }
/* 252:525 */         attName.append(Utils.doubleToString(coeff_value, 5, 3) + inputFormat.attribute(coeff_inds[j]).name());
/* 253:    */       }
/* 254:528 */       if (num_attrs < this.m_NumAttribs) {
/* 255:529 */         attName.append("...");
/* 256:    */       }
/* 257:532 */       attributes.add(new Attribute(attName.toString()));
/* 258:533 */       cumulative += this.m_Eigenvalues[this.m_SortedEigens[i]];
/* 259:535 */       if (cumulative / this.m_SumOfEigenValues >= this.m_CoverVariance) {
/* 260:    */         break;
/* 261:    */       }
/* 262:    */     }
/* 263:540 */     if (this.m_HasClass) {
/* 264:541 */       attributes.add((Attribute)this.m_TrainCopy.classAttribute().copy());
/* 265:    */     }
/* 266:544 */     Instances outputFormat = new Instances(this.m_TrainCopy.relationName() + "_principal components", attributes, 0);
/* 267:548 */     if (this.m_HasClass) {
/* 268:549 */       outputFormat.setClassIndex(outputFormat.numAttributes() - 1);
/* 269:    */     }
/* 270:552 */     this.m_OutputNumAtts = outputFormat.numAttributes();
/* 271:    */     
/* 272:554 */     return outputFormat;
/* 273:    */   }
/* 274:    */   
/* 275:    */   protected void fillCovariance()
/* 276:    */     throws Exception
/* 277:    */   {
/* 278:561 */     if (this.m_center)
/* 279:    */     {
/* 280:562 */       this.m_centerFilter = new Center();
/* 281:563 */       this.m_centerFilter.setInputFormat(this.m_TrainInstances);
/* 282:564 */       this.m_TrainInstances = Filter.useFilter(this.m_TrainInstances, this.m_centerFilter);
/* 283:    */     }
/* 284:    */     else
/* 285:    */     {
/* 286:566 */       this.m_standardizeFilter = new Standardize();
/* 287:567 */       this.m_standardizeFilter.setInputFormat(this.m_TrainInstances);
/* 288:568 */       this.m_TrainInstances = Filter.useFilter(this.m_TrainInstances, this.m_standardizeFilter);
/* 289:    */     }
/* 290:572 */     this.m_Correlation = new UpperSymmDenseMatrix(this.m_NumAttribs);
/* 291:574 */     for (int i = 0; i < this.m_NumAttribs; i++) {
/* 292:575 */       for (int j = i; j < this.m_NumAttribs; j++)
/* 293:    */       {
/* 294:577 */         double cov = 0.0D;
/* 295:578 */         for (Instance inst : this.m_TrainInstances) {
/* 296:579 */           cov += inst.value(i) * inst.value(j);
/* 297:    */         }
/* 298:582 */         cov /= (this.m_TrainInstances.numInstances() - 1);
/* 299:583 */         this.m_Correlation.set(i, j, cov);
/* 300:    */       }
/* 301:    */     }
/* 302:    */   }
/* 303:    */   
/* 304:    */   protected Instance convertInstance(Instance instance)
/* 305:    */     throws Exception
/* 306:    */   {
/* 307:605 */     double[] newVals = new double[this.m_OutputNumAtts];
/* 308:606 */     Instance tempInst = (Instance)instance.copy();
/* 309:    */     
/* 310:608 */     this.m_ReplaceMissingFilter.input(tempInst);
/* 311:609 */     this.m_ReplaceMissingFilter.batchFinished();
/* 312:610 */     tempInst = this.m_ReplaceMissingFilter.output();
/* 313:    */     
/* 314:612 */     this.m_NominalToBinaryFilter.input(tempInst);
/* 315:613 */     this.m_NominalToBinaryFilter.batchFinished();
/* 316:614 */     tempInst = this.m_NominalToBinaryFilter.output();
/* 317:616 */     if (this.m_AttributeFilter != null)
/* 318:    */     {
/* 319:617 */       this.m_AttributeFilter.input(tempInst);
/* 320:618 */       this.m_AttributeFilter.batchFinished();
/* 321:619 */       tempInst = this.m_AttributeFilter.output();
/* 322:    */     }
/* 323:622 */     if (!this.m_center)
/* 324:    */     {
/* 325:623 */       this.m_standardizeFilter.input(tempInst);
/* 326:624 */       this.m_standardizeFilter.batchFinished();
/* 327:625 */       tempInst = this.m_standardizeFilter.output();
/* 328:    */     }
/* 329:    */     else
/* 330:    */     {
/* 331:627 */       this.m_centerFilter.input(tempInst);
/* 332:628 */       this.m_centerFilter.batchFinished();
/* 333:629 */       tempInst = this.m_centerFilter.output();
/* 334:    */     }
/* 335:632 */     if (this.m_HasClass) {
/* 336:633 */       newVals[(this.m_OutputNumAtts - 1)] = instance.value(instance.classIndex());
/* 337:    */     }
/* 338:    */     int numAttsLowerBound;
/* 339:    */     int numAttsLowerBound;
/* 340:636 */     if (this.m_MaxAttributes > 0) {
/* 341:637 */       numAttsLowerBound = this.m_NumAttribs - this.m_MaxAttributes;
/* 342:    */     } else {
/* 343:639 */       numAttsLowerBound = 0;
/* 344:    */     }
/* 345:641 */     if (numAttsLowerBound < 0) {
/* 346:642 */       numAttsLowerBound = 0;
/* 347:    */     }
/* 348:645 */     double cumulative = 0.0D;
/* 349:646 */     for (int i = this.m_NumAttribs - 1; i >= numAttsLowerBound; i--)
/* 350:    */     {
/* 351:647 */       double tempval = 0.0D;
/* 352:648 */       for (int j = 0; j < this.m_NumAttribs; j++) {
/* 353:649 */         tempval += this.m_Eigenvectors[j][this.m_SortedEigens[i]] * tempInst.value(j);
/* 354:    */       }
/* 355:652 */       newVals[(this.m_NumAttribs - i - 1)] = tempval;
/* 356:653 */       cumulative += this.m_Eigenvalues[this.m_SortedEigens[i]];
/* 357:654 */       if (cumulative / this.m_SumOfEigenValues >= this.m_CoverVariance) {
/* 358:    */         break;
/* 359:    */       }
/* 360:    */     }
/* 361:    */     Instance result;
/* 362:    */     Instance result;
/* 363:660 */     if ((instance instanceof SparseInstance)) {
/* 364:661 */       result = new SparseInstance(instance.weight(), newVals);
/* 365:    */     } else {
/* 366:663 */       result = new DenseInstance(instance.weight(), newVals);
/* 367:    */     }
/* 368:666 */     return result;
/* 369:    */   }
/* 370:    */   
/* 371:    */   protected void setup(Instances instances)
/* 372:    */     throws Exception
/* 373:    */   {
/* 374:683 */     this.m_TrainInstances = new Instances(instances);
/* 375:    */     
/* 376:    */ 
/* 377:    */ 
/* 378:687 */     this.m_TrainCopy = new Instances(this.m_TrainInstances, 0);
/* 379:    */     
/* 380:689 */     this.m_ReplaceMissingFilter = new ReplaceMissingValues();
/* 381:690 */     this.m_ReplaceMissingFilter.setInputFormat(this.m_TrainInstances);
/* 382:691 */     this.m_TrainInstances = Filter.useFilter(this.m_TrainInstances, this.m_ReplaceMissingFilter);
/* 383:    */     
/* 384:    */ 
/* 385:694 */     this.m_NominalToBinaryFilter = new NominalToBinary();
/* 386:695 */     this.m_NominalToBinaryFilter.setInputFormat(this.m_TrainInstances);
/* 387:696 */     this.m_TrainInstances = Filter.useFilter(this.m_TrainInstances, this.m_NominalToBinaryFilter);
/* 388:    */     
/* 389:    */ 
/* 390:    */ 
/* 391:700 */     Vector<Integer> deleteCols = new Vector();
/* 392:701 */     for (int i = 0; i < this.m_TrainInstances.numAttributes(); i++) {
/* 393:702 */       if (this.m_TrainInstances.numDistinctValues(i) <= 1) {
/* 394:703 */         deleteCols.addElement(Integer.valueOf(i));
/* 395:    */       }
/* 396:    */     }
/* 397:707 */     if (this.m_TrainInstances.classIndex() >= 0)
/* 398:    */     {
/* 399:709 */       this.m_HasClass = true;
/* 400:710 */       this.m_ClassIndex = this.m_TrainInstances.classIndex();
/* 401:711 */       deleteCols.addElement(new Integer(this.m_ClassIndex));
/* 402:    */     }
/* 403:715 */     if (deleteCols.size() > 0)
/* 404:    */     {
/* 405:716 */       this.m_AttributeFilter = new Remove();
/* 406:717 */       int[] todelete = new int[deleteCols.size()];
/* 407:718 */       for (i = 0; i < deleteCols.size(); i++) {
/* 408:719 */         todelete[i] = ((Integer)deleteCols.elementAt(i)).intValue();
/* 409:    */       }
/* 410:721 */       this.m_AttributeFilter.setAttributeIndicesArray(todelete);
/* 411:722 */       this.m_AttributeFilter.setInvertSelection(false);
/* 412:723 */       this.m_AttributeFilter.setInputFormat(this.m_TrainInstances);
/* 413:724 */       this.m_TrainInstances = Filter.useFilter(this.m_TrainInstances, this.m_AttributeFilter);
/* 414:    */     }
/* 415:728 */     getCapabilities().testWithFail(this.m_TrainInstances);
/* 416:    */     
/* 417:730 */     this.m_NumInstances = this.m_TrainInstances.numInstances();
/* 418:731 */     this.m_NumAttribs = this.m_TrainInstances.numAttributes();
/* 419:    */     
/* 420:    */ 
/* 421:734 */     fillCovariance();
/* 422:    */     
/* 423:    */ 
/* 424:    */ 
/* 425:738 */     SymmDenseEVD evd = SymmDenseEVD.factorize(this.m_Correlation);
/* 426:    */     
/* 427:740 */     this.m_Eigenvectors = Matrices.getArray(evd.getEigenvectors());
/* 428:741 */     this.m_Eigenvalues = evd.getEigenvalues();
/* 429:744 */     for (i = 0; i < this.m_Eigenvalues.length; i++) {
/* 430:745 */       if (this.m_Eigenvalues[i] < 0.0D) {
/* 431:746 */         this.m_Eigenvalues[i] = 0.0D;
/* 432:    */       }
/* 433:    */     }
/* 434:749 */     this.m_SortedEigens = Utils.sort(this.m_Eigenvalues);
/* 435:750 */     this.m_SumOfEigenValues = Utils.sum(this.m_Eigenvalues);
/* 436:    */     
/* 437:752 */     this.m_TransformedFormat = determineOutputFormat(this.m_TrainInstances);
/* 438:753 */     setOutputFormat(this.m_TransformedFormat);
/* 439:    */     
/* 440:755 */     this.m_TrainInstances = null;
/* 441:    */   }
/* 442:    */   
/* 443:    */   public boolean setInputFormat(Instances instanceInfo)
/* 444:    */     throws Exception
/* 445:    */   {
/* 446:769 */     super.setInputFormat(instanceInfo);
/* 447:    */     
/* 448:771 */     this.m_Eigenvalues = null;
/* 449:772 */     this.m_OutputNumAtts = -1;
/* 450:773 */     this.m_AttributeFilter = null;
/* 451:774 */     this.m_NominalToBinaryFilter = null;
/* 452:775 */     this.m_SumOfEigenValues = 0.0D;
/* 453:    */     
/* 454:777 */     return false;
/* 455:    */   }
/* 456:    */   
/* 457:    */   public boolean input(Instance instance)
/* 458:    */     throws Exception
/* 459:    */   {
/* 460:793 */     if (getInputFormat() == null) {
/* 461:794 */       throw new IllegalStateException("No input instance format defined");
/* 462:    */     }
/* 463:797 */     if (isNewBatch())
/* 464:    */     {
/* 465:798 */       resetQueue();
/* 466:799 */       this.m_NewBatch = false;
/* 467:    */     }
/* 468:802 */     if (isFirstBatchDone())
/* 469:    */     {
/* 470:803 */       Instance inst = convertInstance(instance);
/* 471:804 */       inst.setDataset(getOutputFormat());
/* 472:805 */       push(inst, false);
/* 473:806 */       return true;
/* 474:    */     }
/* 475:808 */     bufferInput(instance);
/* 476:809 */     return false;
/* 477:    */   }
/* 478:    */   
/* 479:    */   public boolean batchFinished()
/* 480:    */     throws Exception
/* 481:    */   {
/* 482:826 */     if (getInputFormat() == null) {
/* 483:827 */       throw new NullPointerException("No input instance format defined");
/* 484:    */     }
/* 485:830 */     Instances insts = getInputFormat();
/* 486:832 */     if (!isFirstBatchDone()) {
/* 487:833 */       setup(insts);
/* 488:    */     }
/* 489:836 */     for (int i = 0; i < insts.numInstances(); i++)
/* 490:    */     {
/* 491:837 */       Instance inst = convertInstance(insts.instance(i));
/* 492:838 */       inst.setDataset(getOutputFormat());
/* 493:839 */       push(inst, false);
/* 494:    */     }
/* 495:842 */     flushInput();
/* 496:843 */     this.m_NewBatch = true;
/* 497:844 */     this.m_FirstBatchDone = true;
/* 498:    */     
/* 499:846 */     return numPendingOutput() != 0;
/* 500:    */   }
/* 501:    */   
/* 502:    */   public String getRevision()
/* 503:    */   {
/* 504:856 */     return RevisionUtils.extract("$Revision: 12660 $");
/* 505:    */   }
/* 506:    */   
/* 507:    */   public static void main(String[] args)
/* 508:    */   {
/* 509:865 */     runFilter(new PrincipalComponents(), args);
/* 510:    */   }
/* 511:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.PrincipalComponents
 * JD-Core Version:    0.7.0.1
 */