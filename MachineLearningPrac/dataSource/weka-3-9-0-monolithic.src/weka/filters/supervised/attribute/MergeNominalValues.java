/*   1:    */ package weka.filters.supervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.ContingencyTables;
/*  12:    */ import weka.core.DenseInstance;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.Range;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.SpecialFunctions;
/*  19:    */ import weka.core.Statistics;
/*  20:    */ import weka.core.TechnicalInformation;
/*  21:    */ import weka.core.TechnicalInformation.Field;
/*  22:    */ import weka.core.TechnicalInformation.Type;
/*  23:    */ import weka.core.TechnicalInformationHandler;
/*  24:    */ import weka.core.Utils;
/*  25:    */ import weka.core.WeightedInstancesHandler;
/*  26:    */ import weka.filters.SimpleBatchFilter;
/*  27:    */ import weka.filters.SupervisedFilter;
/*  28:    */ 
/*  29:    */ public class MergeNominalValues
/*  30:    */   extends SimpleBatchFilter
/*  31:    */   implements SupervisedFilter, WeightedInstancesHandler, TechnicalInformationHandler
/*  32:    */ {
/*  33:    */   static final long serialVersionUID = 7447337831221353842L;
/*  34:116 */   protected double m_SigLevel = 0.05D;
/*  35:119 */   protected Range m_SelectCols = new Range("first-last");
/*  36:    */   protected int[] m_SelectedAttributes;
/*  37:    */   protected boolean[] m_AttToBeModified;
/*  38:    */   protected int[][] m_Indicators;
/*  39:131 */   protected boolean m_UseShortIdentifiers = false;
/*  40:    */   
/*  41:    */   public String globalInfo()
/*  42:    */   {
/*  43:141 */     return "Merges values of all nominal attributes among the specified attributes, excluding the class attribute, using the CHAID method, but without considering to re-split merged subsets. It implements Steps 1 and 2 described by Kass (1980), see\n\n" + getTechnicalInformation().toString() + "\n\n" + "Once attribute values have been merged, a chi-squared test using the Bonferroni " + "correction is applied to check if the resulting attribute is a valid predictor, " + "based on the Bonferroni multiplier in Equation 3.2 in Kass (1980). If an attribute does " + "not pass this test, all remaining values (if any) are merged. Nevertheless, useless " + "predictors can slip through without being fully merged, e.g. identifier attributes.\n\n" + "The code applies the Yates correction when the chi-squared statistic is computed.\n\n" + "Note that the algorithm is quadratic in the number of attribute values for an attribute.";
/*  44:    */   }
/*  45:    */   
/*  46:    */   public TechnicalInformation getTechnicalInformation()
/*  47:    */   {
/*  48:166 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  49:167 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Gordon V. Kass");
/*  50:168 */     result.setValue(TechnicalInformation.Field.TITLE, "An Exploratory Technique for Investigating Large Quantities of Categorical Data");
/*  51:    */     
/*  52:    */ 
/*  53:    */ 
/*  54:172 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Applied Statistics");
/*  55:173 */     result.setValue(TechnicalInformation.Field.YEAR, "1980");
/*  56:174 */     result.setValue(TechnicalInformation.Field.VOLUME, "29");
/*  57:175 */     result.setValue(TechnicalInformation.Field.NUMBER, "2");
/*  58:176 */     result.setValue(TechnicalInformation.Field.PAGES, "119-127");
/*  59:    */     
/*  60:178 */     return result;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Enumeration<Option> listOptions()
/*  64:    */   {
/*  65:189 */     Vector<Option> result = new Vector();
/*  66:    */     
/*  67:191 */     result.addElement(new Option("\tThe significance level (default: 0.05).\n", "-L", 1, "-L <double>"));
/*  68:    */     
/*  69:    */ 
/*  70:194 */     result.addElement(new Option("\tSets list of attributes to act on (or its inverse). 'first and 'last' are accepted as well.'\n\tE.g.: first-5,7,9,20-last\n\t(default: first-last)", "R", 1, "-R <range>"));
/*  71:    */     
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:    */ 
/*  76:200 */     result.addElement(new Option("\tInvert matching sense (i.e. act on all attributes not specified in list)", "V", 0, "-V"));
/*  77:    */     
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81:205 */     result.addElement(new Option("\tUse short identifiers for merged subsets.", "O", 0, "-O"));
/*  82:    */     
/*  83:    */ 
/*  84:208 */     result.addAll(Collections.list(super.listOptions()));
/*  85:    */     
/*  86:210 */     return result.elements();
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String[] getOptions()
/*  90:    */   {
/*  91:221 */     Vector<String> result = new Vector();
/*  92:    */     
/*  93:223 */     result.add("-L");
/*  94:224 */     result.add("" + getSignificanceLevel());
/*  95:226 */     if (!getAttributeIndices().equals("")) {}
/*  96:230 */     result.add("-R");
/*  97:231 */     result.add(getAttributeIndices());
/*  98:234 */     if (getInvertSelection()) {
/*  99:235 */       result.add("-V");
/* 100:    */     }
/* 101:238 */     if (getUseShortIdentifiers()) {
/* 102:239 */       result.add("-O");
/* 103:    */     }
/* 104:242 */     Collections.addAll(result, super.getOptions());
/* 105:    */     
/* 106:244 */     return (String[])result.toArray(new String[result.size()]);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setOptions(String[] options)
/* 110:    */     throws Exception
/* 111:    */   {
/* 112:289 */     String significanceLevelString = Utils.getOption('L', options);
/* 113:290 */     if (significanceLevelString.length() != 0) {
/* 114:291 */       setSignificanceLevel(Double.parseDouble(significanceLevelString));
/* 115:    */     } else {
/* 116:293 */       setSignificanceLevel(0.05D);
/* 117:    */     }
/* 118:296 */     String tmpStr = Utils.getOption('R', options);
/* 119:297 */     if (tmpStr.length() != 0) {
/* 120:298 */       setAttributeIndices(tmpStr);
/* 121:    */     } else {
/* 122:300 */       setAttributeIndices("first-last");
/* 123:    */     }
/* 124:303 */     setInvertSelection(Utils.getFlag('V', options));
/* 125:    */     
/* 126:305 */     setUseShortIdentifiers(Utils.getFlag('O', options));
/* 127:    */     
/* 128:307 */     super.setOptions(options);
/* 129:    */     
/* 130:309 */     Utils.checkForRemainingOptions(options);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String significanceLevelTipText()
/* 134:    */   {
/* 135:320 */     return "The significance level for the chi-squared test used to decide when to stop merging.";
/* 136:    */   }
/* 137:    */   
/* 138:    */   public double getSignificanceLevel()
/* 139:    */   {
/* 140:330 */     return this.m_SigLevel;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void setSignificanceLevel(double sF)
/* 144:    */   {
/* 145:340 */     this.m_SigLevel = sF;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public String attributeIndicesTipText()
/* 149:    */   {
/* 150:351 */     return "Specify range of attributes to act on (or its inverse). This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 151:    */   }
/* 152:    */   
/* 153:    */   public String getAttributeIndices()
/* 154:    */   {
/* 155:364 */     return this.m_SelectCols.getRanges();
/* 156:    */   }
/* 157:    */   
/* 158:    */   public void setAttributeIndices(String rangeList)
/* 159:    */   {
/* 160:377 */     this.m_SelectCols.setRanges(rangeList);
/* 161:    */   }
/* 162:    */   
/* 163:    */   public void setAttributeIndicesArray(int[] attributes)
/* 164:    */   {
/* 165:389 */     setAttributeIndices(Range.indicesToRangeList(attributes));
/* 166:    */   }
/* 167:    */   
/* 168:    */   public String invertSelectionTipText()
/* 169:    */   {
/* 170:400 */     return "Determines whether selected attributes are to be acted on or all other attributes are used instead.";
/* 171:    */   }
/* 172:    */   
/* 173:    */   public boolean getInvertSelection()
/* 174:    */   {
/* 175:412 */     return this.m_SelectCols.getInvert();
/* 176:    */   }
/* 177:    */   
/* 178:    */   public void setInvertSelection(boolean invert)
/* 179:    */   {
/* 180:422 */     this.m_SelectCols.setInvert(invert);
/* 181:    */   }
/* 182:    */   
/* 183:    */   public String useShortIdentifiersTipText()
/* 184:    */   {
/* 185:433 */     return "Whether to use short identifiers for the merged values.";
/* 186:    */   }
/* 187:    */   
/* 188:    */   public boolean getUseShortIdentifiers()
/* 189:    */   {
/* 190:443 */     return this.m_UseShortIdentifiers;
/* 191:    */   }
/* 192:    */   
/* 193:    */   public void setUseShortIdentifiers(boolean b)
/* 194:    */   {
/* 195:453 */     this.m_UseShortIdentifiers = b;
/* 196:    */   }
/* 197:    */   
/* 198:    */   public boolean allowAccessToFullInputFormat()
/* 199:    */   {
/* 200:461 */     return true;
/* 201:    */   }
/* 202:    */   
/* 203:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 204:    */   {
/* 205:474 */     this.m_SelectCols.setUpper(inputFormat.numAttributes() - 1);
/* 206:    */     
/* 207:    */ 
/* 208:477 */     this.m_SelectedAttributes = this.m_SelectCols.getSelection();
/* 209:    */     
/* 210:    */ 
/* 211:480 */     double[][][] freqs = new double[inputFormat.numAttributes()][][];
/* 212:481 */     for (int m_SelectedAttribute : this.m_SelectedAttributes)
/* 213:    */     {
/* 214:482 */       int current = m_SelectedAttribute;
/* 215:483 */       Attribute att = inputFormat.attribute(current);
/* 216:484 */       if ((current != inputFormat.classIndex()) && (att.isNominal())) {
/* 217:485 */         freqs[current] = new double[att.numValues()][inputFormat.numClasses()];
/* 218:    */       }
/* 219:    */     }
/* 220:490 */     for (Instance inst : inputFormat) {
/* 221:491 */       for (int m_SelectedAttribute : this.m_SelectedAttributes)
/* 222:    */       {
/* 223:492 */         int current = m_SelectedAttribute;
/* 224:493 */         if ((current != inputFormat.classIndex()) && (inputFormat.attribute(current).isNominal())) {
/* 225:495 */           if ((!inst.isMissing(current)) && (!inst.classIsMissing())) {
/* 226:496 */             freqs[current][((int)inst.value(current))][((int)inst.classValue())] += inst.weight();
/* 227:    */           }
/* 228:    */         }
/* 229:    */       }
/* 230:    */     }
/* 231:504 */     this.m_AttToBeModified = new boolean[inputFormat.numAttributes()];
/* 232:505 */     this.m_Indicators = new int[inputFormat.numAttributes()][];
/* 233:506 */     for (int m_SelectedAttribute : this.m_SelectedAttributes)
/* 234:    */     {
/* 235:507 */       int current = m_SelectedAttribute;
/* 236:508 */       if ((current != inputFormat.classIndex()) && (inputFormat.attribute(current).isNominal()))
/* 237:    */       {
/* 238:511 */         if (this.m_Debug) {
/* 239:512 */           System.err.println(inputFormat.attribute(current));
/* 240:    */         }
/* 241:516 */         this.m_Indicators[current] = mergeValues(freqs[current]);
/* 242:518 */         if (this.m_Debug)
/* 243:    */         {
/* 244:519 */           for (int j = 0; j < this.m_Indicators[current].length; j++) {
/* 245:520 */             System.err.print(" - " + this.m_Indicators[current][j] + " - ");
/* 246:    */           }
/* 247:522 */           System.err.println();
/* 248:    */         }
/* 249:526 */         for (int k = 0; k < this.m_Indicators[current].length; k++) {
/* 250:527 */           if (this.m_Indicators[current][k] != k) {
/* 251:528 */             this.m_AttToBeModified[current] = true;
/* 252:    */           }
/* 253:    */         }
/* 254:    */       }
/* 255:    */     }
/* 256:535 */     ArrayList<Attribute> atts = new ArrayList();
/* 257:536 */     for (int i = 0; i < inputFormat.numAttributes(); i++)
/* 258:    */     {
/* 259:537 */       int current = i;
/* 260:538 */       Attribute att = inputFormat.attribute(current);
/* 261:539 */       if (this.m_AttToBeModified[i] != 0)
/* 262:    */       {
/* 263:542 */         int numValues = 0;
/* 264:543 */         for (int j = 0; j < this.m_Indicators[current].length; j++) {
/* 265:544 */           if (this.m_Indicators[current][j] + 1 > numValues) {
/* 266:545 */             numValues = this.m_Indicators[current][j] + 1;
/* 267:    */           }
/* 268:    */         }
/* 269:550 */         ArrayList<StringBuilder> vals = new ArrayList(numValues);
/* 270:551 */         for (int j = 0; j < numValues; j++) {
/* 271:552 */           vals.add(null);
/* 272:    */         }
/* 273:554 */         for (int j = 0; j < this.m_Indicators[current].length; j++)
/* 274:    */         {
/* 275:555 */           int index = this.m_Indicators[current][j];
/* 276:    */           
/* 277:    */ 
/* 278:558 */           StringBuilder val = (StringBuilder)vals.get(index);
/* 279:559 */           if (val == null)
/* 280:    */           {
/* 281:560 */             if (this.m_UseShortIdentifiers) {
/* 282:561 */               vals.set(index, new StringBuilder("" + (index + 1)));
/* 283:    */             } else {
/* 284:563 */               vals.set(index, new StringBuilder(att.value(j)));
/* 285:    */             }
/* 286:    */           }
/* 287:566 */           else if (!this.m_UseShortIdentifiers) {
/* 288:567 */             ((StringBuilder)vals.get(index)).append("_or_").append(att.value(j));
/* 289:    */           }
/* 290:    */         }
/* 291:571 */         ArrayList<String> valsAsStrings = new ArrayList(vals.size());
/* 292:572 */         for (StringBuilder val : vals) {
/* 293:573 */           valsAsStrings.add(val.toString());
/* 294:    */         }
/* 295:575 */         atts.add(new Attribute(att.name() + "_merged_values", valsAsStrings));
/* 296:    */       }
/* 297:    */       else
/* 298:    */       {
/* 299:577 */         atts.add((Attribute)att.copy());
/* 300:    */       }
/* 301:    */     }
/* 302:582 */     Instances data = new Instances(inputFormat.relationName(), atts, 0);
/* 303:583 */     data.setClassIndex(inputFormat.classIndex());
/* 304:584 */     return data;
/* 305:    */   }
/* 306:    */   
/* 307:    */   protected double BFfactor(int c, int r)
/* 308:    */   {
/* 309:593 */     double sum = 0.0D;
/* 310:594 */     double multiplier = 1.0D;
/* 311:595 */     for (int i = 0; i < r; i++)
/* 312:    */     {
/* 313:596 */       sum += multiplier * Math.exp(c * Math.log(r - i) - (SpecialFunctions.lnFactorial(i) + SpecialFunctions.lnFactorial(r - i)));
/* 314:    */       
/* 315:    */ 
/* 316:    */ 
/* 317:600 */       multiplier *= -1.0D;
/* 318:    */     }
/* 319:602 */     return sum;
/* 320:    */   }
/* 321:    */   
/* 322:    */   protected int[] mergeValues(double[][] counts)
/* 323:    */   {
/* 324:610 */     int[] indicators = new int[counts.length];
/* 325:613 */     for (int i = 0; i < indicators.length; i++) {
/* 326:614 */       indicators[i] = i;
/* 327:    */     }
/* 328:618 */     while (counts.length > 1)
/* 329:    */     {
/* 330:621 */       double[][] reducedCounts = new double[2][];
/* 331:622 */       double minVal = 1.7976931348623157E+308D;
/* 332:623 */       int toMergeOne = -1;
/* 333:624 */       int toMergeTwo = -1;
/* 334:625 */       for (int i = 0; i < counts.length; i++)
/* 335:    */       {
/* 336:626 */         reducedCounts[0] = counts[i];
/* 337:627 */         for (int j = i + 1; j < counts.length; j++)
/* 338:    */         {
/* 339:628 */           reducedCounts[1] = counts[j];
/* 340:629 */           double val = ContingencyTables.chiVal(reducedCounts, true);
/* 341:630 */           if (val < minVal)
/* 342:    */           {
/* 343:631 */             minVal = val;
/* 344:632 */             toMergeOne = i;
/* 345:633 */             toMergeTwo = j;
/* 346:    */           }
/* 347:    */         }
/* 348:    */       }
/* 349:639 */       if (Statistics.chiSquaredProbability(minVal, reducedCounts[0].length - 1) <= this.m_SigLevel)
/* 350:    */       {
/* 351:643 */         double val = ContingencyTables.chiVal(counts, true);
/* 352:644 */         int df = (counts[0].length - 1) * (counts.length - 1);
/* 353:645 */         double originalSig = Statistics.chiSquaredProbability(val, df);
/* 354:646 */         double adjustedSig = originalSig * BFfactor(indicators.length, counts.length);
/* 355:648 */         if (this.m_Debug) {
/* 356:649 */           System.err.println("Original p-value: " + originalSig + "\tAdjusted p-value: " + adjustedSig);
/* 357:    */         }
/* 358:652 */         if (adjustedSig <= this.m_SigLevel) {
/* 359:    */           break;
/* 360:    */         }
/* 361:655 */         for (int i = 0; i < indicators.length; i++) {
/* 362:656 */           indicators[i] = 0;
/* 363:    */         }
/* 364:    */         break;
/* 365:    */       }
/* 366:663 */       double[][] newCounts = new double[counts.length - 1][];
/* 367:664 */       for (int i = 0; i < counts.length; i++) {
/* 368:665 */         if (i < toMergeTwo) {
/* 369:668 */           newCounts[i] = counts[i];
/* 370:670 */         } else if (i == toMergeTwo) {
/* 371:673 */           for (int k = 0; k < counts[i].length; k++) {
/* 372:674 */             newCounts[toMergeOne][k] += counts[i][k];
/* 373:    */           }
/* 374:    */         } else {
/* 375:680 */           newCounts[(i - 1)] = counts[i];
/* 376:    */         }
/* 377:    */       }
/* 378:685 */       for (int i = 0; i < indicators.length; i++) {
/* 379:688 */         if (indicators[i] >= toMergeTwo) {
/* 380:689 */           if (indicators[i] == toMergeTwo) {
/* 381:693 */             indicators[i] = toMergeOne;
/* 382:    */           } else {
/* 383:697 */             indicators[i] -= 1;
/* 384:    */           }
/* 385:    */         }
/* 386:    */       }
/* 387:703 */       counts = newCounts;
/* 388:    */     }
/* 389:705 */     return indicators;
/* 390:    */   }
/* 391:    */   
/* 392:    */   public Capabilities getCapabilities()
/* 393:    */   {
/* 394:718 */     Capabilities result = super.getCapabilities();
/* 395:719 */     result.disableAll();
/* 396:    */     
/* 397:    */ 
/* 398:722 */     result.enableAllAttributes();
/* 399:723 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 400:    */     
/* 401:    */ 
/* 402:726 */     result.enableAllClasses();
/* 403:727 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 404:    */     
/* 405:729 */     return result;
/* 406:    */   }
/* 407:    */   
/* 408:    */   protected Instances process(Instances instances)
/* 409:    */     throws Exception
/* 410:    */   {
/* 411:743 */     Instances result = new Instances(getOutputFormat(), instances.numInstances());
/* 412:745 */     for (int i = 0; i < instances.numInstances(); i++)
/* 413:    */     {
/* 414:746 */       Instance inst = instances.instance(i);
/* 415:747 */       double[] newData = new double[instances.numAttributes()];
/* 416:748 */       for (int j = 0; j < instances.numAttributes(); j++) {
/* 417:749 */         if ((this.m_AttToBeModified[j] != 0) && (!inst.isMissing(j))) {
/* 418:750 */           newData[j] = this.m_Indicators[j][((int)inst.value(j))];
/* 419:    */         } else {
/* 420:752 */           newData[j] = inst.value(j);
/* 421:    */         }
/* 422:    */       }
/* 423:755 */       DenseInstance instNew = new DenseInstance(1.0D, newData);
/* 424:756 */       instNew.setDataset(result);
/* 425:    */       
/* 426:    */ 
/* 427:759 */       copyValues(instNew, false, inst.dataset(), outputFormatPeek());
/* 428:    */       
/* 429:    */ 
/* 430:762 */       result.add(instNew);
/* 431:    */     }
/* 432:764 */     return result;
/* 433:    */   }
/* 434:    */   
/* 435:    */   public String getRevision()
/* 436:    */   {
/* 437:774 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 438:    */   }
/* 439:    */   
/* 440:    */   public static void main(String[] args)
/* 441:    */   {
/* 442:783 */     runFilter(new MergeNominalValues(), args);
/* 443:    */   }
/* 444:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.attribute.MergeNominalValues
 * JD-Core Version:    0.7.0.1
 */