/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.functions.SMO;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.SelectedTag;
/*  15:    */ import weka.core.Tag;
/*  16:    */ import weka.core.TechnicalInformation;
/*  17:    */ import weka.core.TechnicalInformation.Field;
/*  18:    */ import weka.core.TechnicalInformation.Type;
/*  19:    */ import weka.core.TechnicalInformationHandler;
/*  20:    */ import weka.core.Utils;
/*  21:    */ import weka.filters.Filter;
/*  22:    */ import weka.filters.unsupervised.attribute.MakeIndicator;
/*  23:    */ import weka.filters.unsupervised.attribute.Remove;
/*  24:    */ 
/*  25:    */ public class SVMAttributeEval
/*  26:    */   extends ASEvaluation
/*  27:    */   implements AttributeEvaluator, OptionHandler, TechnicalInformationHandler
/*  28:    */ {
/*  29:    */   static final long serialVersionUID = -6489975709033967447L;
/*  30:    */   private double[] m_attScores;
/*  31:150 */   private int m_numToEliminate = 1;
/*  32:156 */   private int m_percentToEliminate = 0;
/*  33:161 */   private int m_percentThreshold = 0;
/*  34:164 */   private double m_smoCParameter = 1.0D;
/*  35:167 */   private double m_smoTParameter = 1.0E-010D;
/*  36:170 */   private double m_smoPParameter = 1.0E-025D;
/*  37:173 */   private int m_smoFilterType = 0;
/*  38:    */   
/*  39:    */   public String globalInfo()
/*  40:    */   {
/*  41:182 */     return "SVMAttributeEval :\n\nEvaluates the worth of an attribute by using an SVM classifier. Attributes are ranked by the square of the weight assigned by the SVM. Attribute selection for multiclass problems is handled by ranking attributes for each class seperately using a one-vs-all method and then \"dealing\" from the top of each pile to give a final ranking.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public TechnicalInformation getTechnicalInformation()
/*  45:    */   {
/*  46:202 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  47:203 */     result.setValue(TechnicalInformation.Field.AUTHOR, "I. Guyon and J. Weston and S. Barnhill and V. Vapnik");
/*  48:    */     
/*  49:205 */     result.setValue(TechnicalInformation.Field.YEAR, "2002");
/*  50:206 */     result.setValue(TechnicalInformation.Field.TITLE, "Gene selection for cancer classification using support vector machines");
/*  51:    */     
/*  52:208 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  53:209 */     result.setValue(TechnicalInformation.Field.VOLUME, "46");
/*  54:210 */     result.setValue(TechnicalInformation.Field.PAGES, "389-422");
/*  55:    */     
/*  56:212 */     return result;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public SVMAttributeEval()
/*  60:    */   {
/*  61:219 */     resetOptions();
/*  62:    */   }
/*  63:    */   
/*  64:    */   public Enumeration<Option> listOptions()
/*  65:    */   {
/*  66:230 */     Vector<Option> newVector = new Vector(7);
/*  67:    */     
/*  68:232 */     newVector.addElement(new Option("\tSpecify the constant rate of attribute\n\telimination per invocation of\n\tthe support vector machine.\n\tDefault = 1.", "X", 1, "-X <constant rate of elimination>"));
/*  69:    */     
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74:238 */     newVector.addElement(new Option("\tSpecify the percentage rate of attributes to\n\telimination per invocation of\n\tthe support vector machine.\n\tTrumps constant rate (above threshold).\n\tDefault = 0.", "Y", 1, "-Y <percent rate of elimination>"));
/*  75:    */     
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81:245 */     newVector.addElement(new Option("\tSpecify the threshold below which \n\tpercentage attribute elimination\n\treverts to the constant method.", "Z", 1, "-Z <threshold for percent elimination>"));
/*  82:    */     
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:250 */     newVector.addElement(new Option("\tSpecify the value of P (epsilon\n\tparameter) to pass on to the\n\tsupport vector machine.\n\tDefault = 1.0e-25", "P", 1, "-P <epsilon>"));
/*  87:    */     
/*  88:    */ 
/*  89:    */ 
/*  90:254 */     newVector.addElement(new Option("\tSpecify the value of T (tolerance\n\tparameter) to pass on to the\n\tsupport vector machine.\n\tDefault = 1.0e-10", "T", 1, "-T <tolerance>"));
/*  91:    */     
/*  92:    */ 
/*  93:    */ 
/*  94:258 */     newVector.addElement(new Option("\tSpecify the value of C (complexity\n\tparameter) to pass on to the\n\tsupport vector machine.\n\tDefault = 1.0", "C", 1, "-C <complexity>"));
/*  95:    */     
/*  96:    */ 
/*  97:    */ 
/*  98:262 */     newVector.addElement(new Option("\tWhether the SVM should 0=normalize/1=standardize/2=neither.\n\t(default 0=normalize)", "N", 1, "-N"));
/*  99:    */     
/* 100:    */ 
/* 101:    */ 
/* 102:266 */     return newVector.elements();
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void setOptions(String[] options)
/* 106:    */     throws Exception
/* 107:    */   {
/* 108:339 */     String optionString = Utils.getOption('X', options);
/* 109:340 */     if (optionString.length() != 0) {
/* 110:341 */       setAttsToEliminatePerIteration(Integer.parseInt(optionString));
/* 111:    */     }
/* 112:344 */     optionString = Utils.getOption('Y', options);
/* 113:345 */     if (optionString.length() != 0) {
/* 114:346 */       setPercentToEliminatePerIteration(Integer.parseInt(optionString));
/* 115:    */     }
/* 116:349 */     optionString = Utils.getOption('Z', options);
/* 117:350 */     if (optionString.length() != 0) {
/* 118:351 */       setPercentThreshold(Integer.parseInt(optionString));
/* 119:    */     }
/* 120:354 */     optionString = Utils.getOption('P', options);
/* 121:355 */     if (optionString.length() != 0) {
/* 122:356 */       setEpsilonParameter(new Double(optionString).doubleValue());
/* 123:    */     }
/* 124:359 */     optionString = Utils.getOption('T', options);
/* 125:360 */     if (optionString.length() != 0) {
/* 126:361 */       setToleranceParameter(new Double(optionString).doubleValue());
/* 127:    */     }
/* 128:364 */     optionString = Utils.getOption('C', options);
/* 129:365 */     if (optionString.length() != 0) {
/* 130:366 */       setComplexityParameter(new Double(optionString).doubleValue());
/* 131:    */     }
/* 132:369 */     optionString = Utils.getOption('N', options);
/* 133:370 */     if (optionString.length() != 0) {
/* 134:371 */       setFilterType(new SelectedTag(Integer.parseInt(optionString), SMO.TAGS_FILTER));
/* 135:    */     } else {
/* 136:374 */       setFilterType(new SelectedTag(0, SMO.TAGS_FILTER));
/* 137:    */     }
/* 138:377 */     Utils.checkForRemainingOptions(options);
/* 139:    */   }
/* 140:    */   
/* 141:    */   public String[] getOptions()
/* 142:    */   {
/* 143:388 */     Vector<String> options = new Vector();
/* 144:    */     
/* 145:390 */     options.add("-X");
/* 146:391 */     options.add("" + getAttsToEliminatePerIteration());
/* 147:    */     
/* 148:393 */     options.add("-Y");
/* 149:394 */     options.add("" + getPercentToEliminatePerIteration());
/* 150:    */     
/* 151:396 */     options.add("-Z");
/* 152:397 */     options.add("" + getPercentThreshold());
/* 153:    */     
/* 154:399 */     options.add("-P");
/* 155:400 */     options.add("" + getEpsilonParameter());
/* 156:    */     
/* 157:402 */     options.add("-T");
/* 158:403 */     options.add("" + getToleranceParameter());
/* 159:    */     
/* 160:405 */     options.add("-C");
/* 161:406 */     options.add("" + getComplexityParameter());
/* 162:    */     
/* 163:408 */     options.add("-N");
/* 164:409 */     options.add("" + this.m_smoFilterType);
/* 165:    */     
/* 166:411 */     return (String[])options.toArray(new String[0]);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public String attsToEliminatePerIterationTipText()
/* 170:    */   {
/* 171:422 */     return "Constant rate of attribute elimination.";
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String percentToEliminatePerIterationTipText()
/* 175:    */   {
/* 176:431 */     return "Percent rate of attribute elimination.";
/* 177:    */   }
/* 178:    */   
/* 179:    */   public String percentThresholdTipText()
/* 180:    */   {
/* 181:440 */     return "Threshold below which percent elimination reverts to constant elimination.";
/* 182:    */   }
/* 183:    */   
/* 184:    */   public String epsilonParameterTipText()
/* 185:    */   {
/* 186:449 */     return "P epsilon parameter to pass to the SVM";
/* 187:    */   }
/* 188:    */   
/* 189:    */   public String toleranceParameterTipText()
/* 190:    */   {
/* 191:458 */     return "T tolerance parameter to pass to the SVM";
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String complexityParameterTipText()
/* 195:    */   {
/* 196:467 */     return "C complexity parameter to pass to the SVM";
/* 197:    */   }
/* 198:    */   
/* 199:    */   public String filterTypeTipText()
/* 200:    */   {
/* 201:476 */     return "filtering used by the SVM";
/* 202:    */   }
/* 203:    */   
/* 204:    */   public void setAttsToEliminatePerIteration(int cRate)
/* 205:    */   {
/* 206:487 */     this.m_numToEliminate = cRate;
/* 207:    */   }
/* 208:    */   
/* 209:    */   public int getAttsToEliminatePerIteration()
/* 210:    */   {
/* 211:496 */     return this.m_numToEliminate;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public void setPercentToEliminatePerIteration(int pRate)
/* 215:    */   {
/* 216:505 */     this.m_percentToEliminate = pRate;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public int getPercentToEliminatePerIteration()
/* 220:    */   {
/* 221:514 */     return this.m_percentToEliminate;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public void setPercentThreshold(int pThresh)
/* 225:    */   {
/* 226:524 */     this.m_percentThreshold = pThresh;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public int getPercentThreshold()
/* 230:    */   {
/* 231:534 */     return this.m_percentThreshold;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public void setEpsilonParameter(double svmP)
/* 235:    */   {
/* 236:543 */     this.m_smoPParameter = svmP;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public double getEpsilonParameter()
/* 240:    */   {
/* 241:552 */     return this.m_smoPParameter;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public void setToleranceParameter(double svmT)
/* 245:    */   {
/* 246:561 */     this.m_smoTParameter = svmT;
/* 247:    */   }
/* 248:    */   
/* 249:    */   public double getToleranceParameter()
/* 250:    */   {
/* 251:570 */     return this.m_smoTParameter;
/* 252:    */   }
/* 253:    */   
/* 254:    */   public void setComplexityParameter(double svmC)
/* 255:    */   {
/* 256:579 */     this.m_smoCParameter = svmC;
/* 257:    */   }
/* 258:    */   
/* 259:    */   public double getComplexityParameter()
/* 260:    */   {
/* 261:588 */     return this.m_smoCParameter;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public void setFilterType(SelectedTag newType)
/* 265:    */   {
/* 266:598 */     if (newType.getTags() == SMO.TAGS_FILTER) {
/* 267:599 */       this.m_smoFilterType = newType.getSelectedTag().getID();
/* 268:    */     }
/* 269:    */   }
/* 270:    */   
/* 271:    */   public SelectedTag getFilterType()
/* 272:    */   {
/* 273:610 */     return new SelectedTag(this.m_smoFilterType, SMO.TAGS_FILTER);
/* 274:    */   }
/* 275:    */   
/* 276:    */   public Capabilities getCapabilities()
/* 277:    */   {
/* 278:625 */     Capabilities result = new SMO().getCapabilities();
/* 279:    */     
/* 280:627 */     result.setOwner(this);
/* 281:    */     
/* 282:    */ 
/* 283:    */ 
/* 284:    */ 
/* 285:632 */     result.disable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 286:633 */     result.enable(Capabilities.Capability.BINARY_ATTRIBUTES);
/* 287:634 */     result.disableAllAttributeDependencies();
/* 288:    */     
/* 289:636 */     return result;
/* 290:    */   }
/* 291:    */   
/* 292:    */   public void buildEvaluator(Instances data)
/* 293:    */     throws Exception
/* 294:    */   {
/* 295:648 */     getCapabilities().testWithFail(data);
/* 296:    */     
/* 297:    */ 
/* 298:    */ 
/* 299:    */ 
/* 300:653 */     this.m_numToEliminate = (this.m_numToEliminate > 1 ? this.m_numToEliminate : 1);
/* 301:654 */     this.m_percentToEliminate = (this.m_percentToEliminate < 100 ? this.m_percentToEliminate : 100);
/* 302:    */     
/* 303:656 */     this.m_percentToEliminate = (this.m_percentToEliminate > 0 ? this.m_percentToEliminate : 0);
/* 304:    */     
/* 305:658 */     this.m_percentThreshold = (this.m_percentThreshold < data.numAttributes() ? this.m_percentThreshold : data.numAttributes() - 1);
/* 306:    */     
/* 307:660 */     this.m_percentThreshold = (this.m_percentThreshold > 0 ? this.m_percentThreshold : 0);
/* 308:    */     
/* 309:    */ 
/* 310:    */ 
/* 311:664 */     int numAttr = data.numAttributes() - 1;
/* 312:    */     int[][] attScoresByClass;
/* 313:665 */     if (data.numClasses() > 2)
/* 314:    */     {
/* 315:666 */       int[][] attScoresByClass = new int[data.numClasses()][numAttr];
/* 316:667 */       for (int i = 0; i < data.numClasses(); i++) {
/* 317:668 */         attScoresByClass[i] = rankBySVM(i, data);
/* 318:    */       }
/* 319:    */     }
/* 320:    */     else
/* 321:    */     {
/* 322:671 */       attScoresByClass = new int[1][numAttr];
/* 323:672 */       attScoresByClass[0] = rankBySVM(0, data);
/* 324:    */     }
/* 325:679 */     ArrayList<Integer> ordered = new ArrayList(numAttr);
/* 326:680 */     for (int i = 0; i < numAttr; i++) {
/* 327:681 */       for (int j = 0; j < (data.numClasses() > 2 ? data.numClasses() : 1); j++)
/* 328:    */       {
/* 329:682 */         Integer rank = new Integer(attScoresByClass[j][i]);
/* 330:683 */         if (!ordered.contains(rank)) {
/* 331:684 */           ordered.add(rank);
/* 332:    */         }
/* 333:    */       }
/* 334:    */     }
/* 335:688 */     this.m_attScores = new double[data.numAttributes()];
/* 336:689 */     Iterator<Integer> listIt = ordered.iterator();
/* 337:690 */     for (double i = numAttr; listIt.hasNext(); i -= 1.0D) {
/* 338:691 */       this.m_attScores[((Integer)listIt.next()).intValue()] = i;
/* 339:    */     }
/* 340:    */   }
/* 341:    */   
/* 342:    */   private int[] rankBySVM(int classInd, Instances data)
/* 343:    */   {
/* 344:701 */     int[] origIndices = new int[data.numAttributes()];
/* 345:702 */     for (int i = 0; i < origIndices.length; i++) {
/* 346:703 */       origIndices[i] = i;
/* 347:    */     }
/* 348:707 */     int numAttrLeft = data.numAttributes() - 1;
/* 349:    */     
/* 350:709 */     int[] attRanks = new int[numAttrLeft];
/* 351:    */     try
/* 352:    */     {
/* 353:712 */       MakeIndicator filter = new MakeIndicator();
/* 354:713 */       filter.setAttributeIndex("" + (data.classIndex() + 1));
/* 355:714 */       filter.setNumeric(false);
/* 356:715 */       filter.setValueIndex(classInd);
/* 357:716 */       filter.setInputFormat(data);
/* 358:717 */       Instances trainCopy = Filter.useFilter(data, filter);
/* 359:718 */       double pctToElim = this.m_percentToEliminate / 100.0D;
/* 360:719 */       while (numAttrLeft > 0)
/* 361:    */       {
/* 362:    */         int numToElim;
/* 363:721 */         if (pctToElim > 0.0D)
/* 364:    */         {
/* 365:722 */           int numToElim = (int)(trainCopy.numAttributes() * pctToElim);
/* 366:723 */           numToElim = numToElim > 1 ? numToElim : 1;
/* 367:724 */           if (numAttrLeft - numToElim <= this.m_percentThreshold)
/* 368:    */           {
/* 369:725 */             pctToElim = 0.0D;
/* 370:726 */             numToElim = numAttrLeft - this.m_percentThreshold;
/* 371:    */           }
/* 372:    */         }
/* 373:    */         else
/* 374:    */         {
/* 375:729 */           numToElim = numAttrLeft >= this.m_numToEliminate ? this.m_numToEliminate : numAttrLeft;
/* 376:    */         }
/* 377:734 */         SMO smo = new SMO();
/* 378:    */         
/* 379:    */ 
/* 380:    */ 
/* 381:    */ 
/* 382:739 */         smo.setFilterType(new SelectedTag(this.m_smoFilterType, SMO.TAGS_FILTER));
/* 383:740 */         smo.setEpsilon(this.m_smoPParameter);
/* 384:741 */         smo.setToleranceParameter(this.m_smoTParameter);
/* 385:742 */         smo.setC(this.m_smoCParameter);
/* 386:743 */         smo.buildClassifier(trainCopy);
/* 387:    */         
/* 388:    */ 
/* 389:746 */         double[] weightsSparse = smo.sparseWeights()[0][1];
/* 390:747 */         int[] indicesSparse = smo.sparseIndices()[0][1];
/* 391:748 */         double[] weights = new double[trainCopy.numAttributes()];
/* 392:749 */         for (int j = 0; j < weightsSparse.length; j++) {
/* 393:750 */           weights[indicesSparse[j]] = (weightsSparse[j] * weightsSparse[j]);
/* 394:    */         }
/* 395:752 */         weights[trainCopy.classIndex()] = 1.7976931348623157E+308D;
/* 396:    */         
/* 397:754 */         int[] featArray = new int[numToElim];
/* 398:755 */         boolean[] eliminated = new boolean[origIndices.length];
/* 399:756 */         for (int j = 0; j < numToElim; j++)
/* 400:    */         {
/* 401:757 */           int minWeightIndex = Utils.minIndex(weights);
/* 402:758 */           attRanks[(--numAttrLeft)] = origIndices[minWeightIndex];
/* 403:759 */           featArray[j] = minWeightIndex;
/* 404:760 */           eliminated[minWeightIndex] = true;
/* 405:761 */           weights[minWeightIndex] = 1.7976931348623157E+308D;
/* 406:    */         }
/* 407:765 */         Remove delTransform = new Remove();
/* 408:766 */         delTransform.setInvertSelection(false);
/* 409:767 */         delTransform.setAttributeIndicesArray(featArray);
/* 410:768 */         delTransform.setInputFormat(trainCopy);
/* 411:769 */         trainCopy = Filter.useFilter(trainCopy, delTransform);
/* 412:    */         
/* 413:    */ 
/* 414:772 */         int[] temp = new int[origIndices.length - numToElim];
/* 415:773 */         int k = 0;
/* 416:774 */         for (int j = 0; j < origIndices.length; j++) {
/* 417:775 */           if (eliminated[j] == 0) {
/* 418:776 */             temp[(k++)] = origIndices[j];
/* 419:    */           }
/* 420:    */         }
/* 421:779 */         origIndices = temp;
/* 422:    */       }
/* 423:    */     }
/* 424:    */     catch (Exception e)
/* 425:    */     {
/* 426:783 */       e.printStackTrace();
/* 427:    */     }
/* 428:785 */     return attRanks;
/* 429:    */   }
/* 430:    */   
/* 431:    */   protected void resetOptions()
/* 432:    */   {
/* 433:792 */     this.m_attScores = null;
/* 434:    */   }
/* 435:    */   
/* 436:    */   public double evaluateAttribute(int attribute)
/* 437:    */     throws Exception
/* 438:    */   {
/* 439:804 */     return this.m_attScores[attribute];
/* 440:    */   }
/* 441:    */   
/* 442:    */   public String toString()
/* 443:    */   {
/* 444:815 */     StringBuffer text = new StringBuffer();
/* 445:816 */     if (this.m_attScores == null) {
/* 446:817 */       text.append("\tSVM feature evaluator has not been built yet");
/* 447:    */     } else {
/* 448:819 */       text.append("\tSVM feature evaluator");
/* 449:    */     }
/* 450:822 */     text.append("\n");
/* 451:823 */     return text.toString();
/* 452:    */   }
/* 453:    */   
/* 454:    */   public String getRevision()
/* 455:    */   {
/* 456:833 */     return RevisionUtils.extract("$Revision: 10390 $");
/* 457:    */   }
/* 458:    */   
/* 459:    */   public static void main(String[] args)
/* 460:    */   {
/* 461:842 */     runEvaluator(new SVMAttributeEval(), args);
/* 462:    */   }
/* 463:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.SVMAttributeEval
 * JD-Core Version:    0.7.0.1
 */