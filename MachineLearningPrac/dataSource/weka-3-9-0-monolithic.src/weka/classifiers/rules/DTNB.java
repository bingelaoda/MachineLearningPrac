/*    1:     */ package weka.classifiers.rules;
/*    2:     */ 
/*    3:     */ import java.util.BitSet;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Hashtable;
/*    7:     */ import java.util.Vector;
/*    8:     */ import weka.attributeSelection.ASEvaluation;
/*    9:     */ import weka.attributeSelection.ASSearch;
/*   10:     */ import weka.attributeSelection.SubsetEvaluator;
/*   11:     */ import weka.classifiers.Evaluation;
/*   12:     */ import weka.classifiers.bayes.NaiveBayes;
/*   13:     */ import weka.classifiers.lazy.IBk;
/*   14:     */ import weka.core.Attribute;
/*   15:     */ import weka.core.Capabilities;
/*   16:     */ import weka.core.Capabilities.Capability;
/*   17:     */ import weka.core.Instance;
/*   18:     */ import weka.core.Instances;
/*   19:     */ import weka.core.Option;
/*   20:     */ import weka.core.RevisionUtils;
/*   21:     */ import weka.core.SelectedTag;
/*   22:     */ import weka.core.TechnicalInformation;
/*   23:     */ import weka.core.TechnicalInformation.Field;
/*   24:     */ import weka.core.TechnicalInformation.Type;
/*   25:     */ import weka.core.Utils;
/*   26:     */ import weka.filters.Filter;
/*   27:     */ import weka.filters.unsupervised.attribute.Remove;
/*   28:     */ 
/*   29:     */ public class DTNB
/*   30:     */   extends DecisionTable
/*   31:     */ {
/*   32:     */   protected NaiveBayes m_NB;
/*   33:     */   private double m_percentUsedByDT;
/*   34:     */   static final long serialVersionUID = 2999557077765701326L;
/*   35:     */   protected ASSearch m_backwardWithDelete;
/*   36:     */   
/*   37:     */   public String globalInfo()
/*   38:     */   {
/*   39: 146 */     return "Class for building and using a decision table/naive bayes hybrid classifier. At each point in the search, the algorithm evaluates the merit of dividing the attributes into two disjoint subsets: one for the decision table, the other for naive Bayes. A forward selection search is used, where at each step, selected attributes are modeled by naive Bayes and the remainder by the decision table, and all attributes are modelled by the decision table initially. At each step, the algorithm also considers dropping an attribute entirely from the model.\n\nFor more information, see: \n\n" + getTechnicalInformation().toString();
/*   40:     */   }
/*   41:     */   
/*   42:     */   public TechnicalInformation getTechnicalInformation()
/*   43:     */   {
/*   44: 167 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   45: 168 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Mark Hall and Eibe Frank");
/*   46: 169 */     result.setValue(TechnicalInformation.Field.TITLE, "Combining Naive Bayes and Decision Tables");
/*   47: 170 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the 21st Florida Artificial Intelligence Society Conference (FLAIRS)");
/*   48:     */     
/*   49:     */ 
/*   50: 173 */     result.setValue(TechnicalInformation.Field.YEAR, "2008");
/*   51: 174 */     result.setValue(TechnicalInformation.Field.PAGES, "318-319");
/*   52: 175 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "AAAI press");
/*   53:     */     
/*   54: 177 */     return result;
/*   55:     */   }
/*   56:     */   
/*   57:     */   double evaluateFoldCV(Instances fold, int[] fs)
/*   58:     */     throws Exception
/*   59:     */   {
/*   60: 194 */     int numFold = fold.numInstances();
/*   61: 195 */     int numCl = this.m_theInstances.classAttribute().numValues();
/*   62: 196 */     double[][] class_distribs = new double[numFold][numCl];
/*   63: 197 */     double[] instA = new double[fs.length];
/*   64:     */     
/*   65:     */ 
/*   66: 200 */     double acc = 0.0D;
/*   67: 201 */     int classI = this.m_theInstances.classIndex();
/*   68:     */     double[] normDist;
/*   69:     */     double[] normDist;
/*   70: 204 */     if (this.m_classIsNominal) {
/*   71: 205 */       normDist = new double[numCl];
/*   72:     */     } else {
/*   73: 207 */       normDist = new double[2];
/*   74:     */     }
/*   75: 211 */     for (int i = 0; i < numFold; i++)
/*   76:     */     {
/*   77: 212 */       Instance inst = fold.instance(i);
/*   78: 213 */       for (int j = 0; j < fs.length; j++) {
/*   79: 214 */         if (fs[j] == classI) {
/*   80: 215 */           instA[j] = 1.7976931348623157E+308D;
/*   81: 216 */         } else if (inst.isMissing(fs[j])) {
/*   82: 217 */           instA[j] = 1.7976931348623157E+308D;
/*   83:     */         } else {
/*   84: 219 */           instA[j] = inst.value(fs[j]);
/*   85:     */         }
/*   86:     */       }
/*   87: 222 */       DecisionTableHashKey thekey = new DecisionTableHashKey(instA);
/*   88: 223 */       if ((class_distribs[i] =  = (double[])this.m_entries.get(thekey)) == null) {
/*   89: 224 */         throw new Error("This should never happen!");
/*   90:     */       }
/*   91: 226 */       if (this.m_classIsNominal)
/*   92:     */       {
/*   93: 227 */         class_distribs[i][((int)inst.classValue())] -= inst.weight();
/*   94: 228 */         inst.setWeight(-inst.weight());
/*   95: 229 */         this.m_NB.updateClassifier(inst);
/*   96: 230 */         inst.setWeight(-inst.weight());
/*   97:     */       }
/*   98:     */       else
/*   99:     */       {
/*  100: 232 */         class_distribs[i][0] -= inst.classValue() * inst.weight();
/*  101: 233 */         class_distribs[i][1] -= inst.weight();
/*  102:     */       }
/*  103: 237 */       this.m_classPriorCounts[((int)inst.classValue())] -= inst.weight();
/*  104:     */     }
/*  105: 239 */     double[] classPriors = (double[])this.m_classPriorCounts.clone();
/*  106: 240 */     Utils.normalize(classPriors);
/*  107: 243 */     for (i = 0; i < numFold; i++)
/*  108:     */     {
/*  109: 244 */       Instance inst = fold.instance(i);
/*  110: 245 */       System.arraycopy(class_distribs[i], 0, normDist, 0, normDist.length);
/*  111: 246 */       if (this.m_classIsNominal)
/*  112:     */       {
/*  113: 247 */         boolean ok = false;
/*  114: 248 */         for (double element : normDist) {
/*  115: 249 */           if (Utils.gr(element, 1.0D))
/*  116:     */           {
/*  117: 250 */             ok = true;
/*  118: 251 */             break;
/*  119:     */           }
/*  120:     */         }
/*  121: 255 */         if (!ok) {
/*  122: 256 */           normDist = (double[])classPriors.clone();
/*  123:     */         } else {
/*  124: 258 */           Utils.normalize(normDist);
/*  125:     */         }
/*  126: 261 */         double[] nbDist = this.m_NB.distributionForInstance(inst);
/*  127: 263 */         for (int l = 0; l < normDist.length; l++)
/*  128:     */         {
/*  129: 264 */           normDist[l] = (Math.log(normDist[l]) - Math.log(classPriors[l]));
/*  130: 265 */           normDist[l] += Math.log(nbDist[l]);
/*  131:     */         }
/*  132: 267 */         normDist = Utils.logs2probs(normDist);
/*  133: 273 */         if (this.m_evaluationMeasure == 5) {
/*  134: 274 */           this.m_evaluation.evaluateModelOnceAndRecordPrediction(normDist, inst);
/*  135:     */         } else {
/*  136: 276 */           this.m_evaluation.evaluateModelOnce(normDist, inst);
/*  137:     */         }
/*  138:     */       }
/*  139: 285 */       else if (Utils.eq(normDist[1], 0.0D))
/*  140:     */       {
/*  141: 286 */         double[] temp = new double[1];
/*  142: 287 */         temp[0] = this.m_majority;
/*  143: 288 */         this.m_evaluation.evaluateModelOnce(temp, inst);
/*  144:     */       }
/*  145:     */       else
/*  146:     */       {
/*  147: 290 */         double[] temp = new double[1];
/*  148: 291 */         normDist[0] /= normDist[1];
/*  149: 292 */         this.m_evaluation.evaluateModelOnce(temp, inst);
/*  150:     */       }
/*  151:     */     }
/*  152: 298 */     for (i = 0; i < numFold; i++)
/*  153:     */     {
/*  154: 299 */       Instance inst = fold.instance(i);
/*  155:     */       
/*  156: 301 */       this.m_classPriorCounts[((int)inst.classValue())] += inst.weight();
/*  157: 303 */       if (this.m_classIsNominal)
/*  158:     */       {
/*  159: 304 */         class_distribs[i][((int)inst.classValue())] += inst.weight();
/*  160: 305 */         this.m_NB.updateClassifier(inst);
/*  161:     */       }
/*  162:     */       else
/*  163:     */       {
/*  164: 307 */         class_distribs[i][0] += inst.classValue() * inst.weight();
/*  165: 308 */         class_distribs[i][1] += inst.weight();
/*  166:     */       }
/*  167:     */     }
/*  168: 311 */     return acc;
/*  169:     */   }
/*  170:     */   
/*  171:     */   double evaluateInstanceLeaveOneOut(Instance instance, double[] instA)
/*  172:     */     throws Exception
/*  173:     */   {
/*  174: 331 */     DecisionTableHashKey thekey = new DecisionTableHashKey(instA);
/*  175:     */     double[] tempDist;
/*  176: 334 */     if ((tempDist = (double[])this.m_entries.get(thekey)) == null) {
/*  177: 335 */       throw new Error("This should never happen!");
/*  178:     */     }
/*  179: 337 */     double[] normDist = new double[tempDist.length];
/*  180: 338 */     System.arraycopy(tempDist, 0, normDist, 0, tempDist.length);
/*  181: 339 */     normDist[((int)instance.classValue())] -= instance.weight();
/*  182:     */     
/*  183:     */ 
/*  184:     */ 
/*  185: 343 */     boolean ok = false;
/*  186: 344 */     for (double element : normDist) {
/*  187: 345 */       if (Utils.gr(element, 1.0D))
/*  188:     */       {
/*  189: 346 */         ok = true;
/*  190: 347 */         break;
/*  191:     */       }
/*  192:     */     }
/*  193: 352 */     this.m_classPriorCounts[((int)instance.classValue())] -= instance.weight();
/*  194: 353 */     double[] classPriors = (double[])this.m_classPriorCounts.clone();
/*  195: 354 */     Utils.normalize(classPriors);
/*  196: 355 */     if (!ok) {
/*  197: 356 */       normDist = classPriors;
/*  198:     */     } else {
/*  199: 358 */       Utils.normalize(normDist);
/*  200:     */     }
/*  201: 361 */     this.m_classPriorCounts[((int)instance.classValue())] += instance.weight();
/*  202: 363 */     if (this.m_NB != null)
/*  203:     */     {
/*  204: 366 */       instance.setWeight(-instance.weight());
/*  205: 367 */       this.m_NB.updateClassifier(instance);
/*  206: 368 */       double[] nbDist = this.m_NB.distributionForInstance(instance);
/*  207: 369 */       instance.setWeight(-instance.weight());
/*  208: 370 */       this.m_NB.updateClassifier(instance);
/*  209: 372 */       for (int i = 0; i < normDist.length; i++)
/*  210:     */       {
/*  211: 373 */         normDist[i] = (Math.log(normDist[i]) - Math.log(classPriors[i]));
/*  212: 374 */         normDist[i] += Math.log(nbDist[i]);
/*  213:     */       }
/*  214: 376 */       normDist = Utils.logs2probs(normDist);
/*  215:     */     }
/*  216: 380 */     if (this.m_evaluationMeasure == 5) {
/*  217: 381 */       this.m_evaluation.evaluateModelOnceAndRecordPrediction(normDist, instance);
/*  218:     */     } else {
/*  219: 383 */       this.m_evaluation.evaluateModelOnce(normDist, instance);
/*  220:     */     }
/*  221: 385 */     return Utils.maxIndex(normDist);
/*  222:     */   }
/*  223:     */   
/*  224:     */   protected void setUpEvaluator()
/*  225:     */     throws Exception
/*  226:     */   {
/*  227: 395 */     this.m_evaluator = new EvalWithDelete();
/*  228: 396 */     this.m_evaluator.buildEvaluator(this.m_theInstances);
/*  229:     */   }
/*  230:     */   
/*  231:     */   protected class EvalWithDelete
/*  232:     */     extends ASEvaluation
/*  233:     */     implements SubsetEvaluator
/*  234:     */   {
/*  235:     */     private static final long serialVersionUID = -1376466463585895163L;
/*  236:     */     private BitSet m_deletedFromDTNB;
/*  237:     */     
/*  238:     */     protected EvalWithDelete() {}
/*  239:     */     
/*  240:     */     public void buildEvaluator(Instances data)
/*  241:     */       throws Exception
/*  242:     */     {
/*  243: 410 */       DTNB.this.m_NB = null;
/*  244: 411 */       this.m_deletedFromDTNB = new BitSet(data.numAttributes());
/*  245:     */     }
/*  246:     */     
/*  247:     */     private int setUpForEval(BitSet subset)
/*  248:     */       throws Exception
/*  249:     */     {
/*  250: 417 */       int fc = 0;
/*  251: 418 */       for (int jj = 0; jj < DTNB.this.m_numAttributes; jj++) {
/*  252: 419 */         if (subset.get(jj)) {
/*  253: 420 */           fc++;
/*  254:     */         }
/*  255:     */       }
/*  256: 427 */       for (int j = 0; j < DTNB.this.m_numAttributes; j++)
/*  257:     */       {
/*  258: 428 */         DTNB.this.m_theInstances.attribute(j).setWeight(1.0D);
/*  259: 429 */         if ((j != DTNB.this.m_theInstances.classIndex()) && 
/*  260: 430 */           (subset.get(j))) {
/*  261: 432 */           DTNB.this.m_theInstances.attribute(j).setWeight(0.0D);
/*  262:     */         }
/*  263:     */       }
/*  264: 438 */       for (int i = 0; i < DTNB.this.m_numAttributes; i++) {
/*  265: 439 */         if (this.m_deletedFromDTNB.get(i)) {
/*  266: 440 */           DTNB.this.m_theInstances.attribute(i).setWeight(0.0D);
/*  267:     */         }
/*  268:     */       }
/*  269: 444 */       if (DTNB.this.m_NB == null)
/*  270:     */       {
/*  271: 446 */         DTNB.this.m_NB = new NaiveBayes();
/*  272: 447 */         DTNB.this.m_NB.buildClassifier(DTNB.this.m_theInstances);
/*  273:     */       }
/*  274: 449 */       return fc;
/*  275:     */     }
/*  276:     */     
/*  277:     */     public double evaluateSubset(BitSet subset)
/*  278:     */       throws Exception
/*  279:     */     {
/*  280: 454 */       int fc = setUpForEval(subset);
/*  281:     */       
/*  282: 456 */       return DTNB.this.estimatePerformance(subset, fc);
/*  283:     */     }
/*  284:     */     
/*  285:     */     public double evaluateSubsetDelete(BitSet subset, int potentialDelete)
/*  286:     */       throws Exception
/*  287:     */     {
/*  288: 462 */       int fc = setUpForEval(subset);
/*  289:     */       
/*  290:     */ 
/*  291: 465 */       DTNB.this.m_theInstances.attribute(potentialDelete).setWeight(0.0D);
/*  292:     */       
/*  293:     */ 
/*  294: 468 */       return DTNB.this.estimatePerformance(subset, fc);
/*  295:     */     }
/*  296:     */     
/*  297:     */     public BitSet getDeletedList()
/*  298:     */     {
/*  299: 472 */       return this.m_deletedFromDTNB;
/*  300:     */     }
/*  301:     */     
/*  302:     */     public String getRevision()
/*  303:     */     {
/*  304: 482 */       return RevisionUtils.extract("$Revision: 10341 $");
/*  305:     */     }
/*  306:     */   }
/*  307:     */   
/*  308:     */   protected class BackwardsWithDelete
/*  309:     */     extends ASSearch
/*  310:     */   {
/*  311:     */     private static final long serialVersionUID = -67415171406110705L;
/*  312:     */     
/*  313:     */     protected BackwardsWithDelete() {}
/*  314:     */     
/*  315:     */     public String globalInfo()
/*  316:     */     {
/*  317: 499 */       return "Specialized search that performs a forward selection (naive Bayes)/backward elimination (decision table). Also considers dropping attributes entirely from the combined model.";
/*  318:     */     }
/*  319:     */     
/*  320:     */     public String toString()
/*  321:     */     {
/*  322: 506 */       return "";
/*  323:     */     }
/*  324:     */     
/*  325:     */     public int[] search(ASEvaluation eval, Instances data)
/*  326:     */       throws Exception
/*  327:     */     {
/*  328: 512 */       double best_merit = -1.797693134862316E+308D;
/*  329: 513 */       double temp_best = 0.0D;double temp_merit = 0.0D;double temp_merit_delete = 0.0D;
/*  330: 514 */       int temp_index = 0;
/*  331:     */       
/*  332: 516 */       BitSet best_group = null;
/*  333:     */       
/*  334: 518 */       int numAttribs = data.numAttributes();
/*  335: 520 */       if (best_group == null) {
/*  336: 521 */         best_group = new BitSet(numAttribs);
/*  337:     */       }
/*  338: 524 */       int classIndex = data.classIndex();
/*  339: 525 */       for (int i = 0; i < numAttribs; i++) {
/*  340: 526 */         if (i != classIndex) {
/*  341: 527 */           best_group.set(i);
/*  342:     */         }
/*  343:     */       }
/*  344: 535 */       best_merit = ((SubsetEvaluator)eval).evaluateSubset(best_group);
/*  345:     */       
/*  346:     */ 
/*  347:     */ 
/*  348:     */ 
/*  349: 540 */       boolean done = false;
/*  350: 541 */       boolean addone = false;
/*  351:     */       
/*  352: 543 */       boolean deleted = false;
/*  353: 544 */       while (!done)
/*  354:     */       {
/*  355: 545 */         BitSet temp_group = (BitSet)best_group.clone();
/*  356: 546 */         temp_best = best_merit;
/*  357:     */         
/*  358: 548 */         done = true;
/*  359: 549 */         addone = false;
/*  360: 550 */         for (i = 0; i < numAttribs; i++)
/*  361:     */         {
/*  362: 551 */           boolean z = (i != classIndex) && (temp_group.get(i));
/*  363: 553 */           if (z)
/*  364:     */           {
/*  365: 555 */             temp_group.clear(i);
/*  366:     */             
/*  367:     */ 
/*  368: 558 */             temp_merit = ((SubsetEvaluator)eval).evaluateSubset(temp_group);
/*  369:     */             
/*  370:     */ 
/*  371:     */ 
/*  372: 562 */             temp_merit_delete = ((DTNB.EvalWithDelete)eval).evaluateSubsetDelete(temp_group, i);
/*  373:     */             
/*  374: 564 */             boolean deleteBetter = false;
/*  375: 567 */             if (temp_merit_delete >= temp_merit)
/*  376:     */             {
/*  377: 568 */               temp_merit = temp_merit_delete;
/*  378: 569 */               deleteBetter = true;
/*  379:     */             }
/*  380: 572 */             z = temp_merit >= temp_best;
/*  381: 574 */             if (z)
/*  382:     */             {
/*  383: 575 */               temp_best = temp_merit;
/*  384: 576 */               temp_index = i;
/*  385: 577 */               addone = true;
/*  386: 578 */               done = false;
/*  387: 579 */               if (deleteBetter) {
/*  388: 580 */                 deleted = true;
/*  389:     */               } else {
/*  390: 582 */                 deleted = false;
/*  391:     */               }
/*  392:     */             }
/*  393: 587 */             temp_group.set(i);
/*  394:     */           }
/*  395:     */         }
/*  396: 590 */         if (addone)
/*  397:     */         {
/*  398: 591 */           best_group.clear(temp_index);
/*  399: 592 */           best_merit = temp_best;
/*  400: 593 */           if (deleted) {
/*  401: 595 */             ((DTNB.EvalWithDelete)eval).getDeletedList().set(temp_index);
/*  402:     */           }
/*  403:     */         }
/*  404:     */       }
/*  405: 604 */       return attributeList(best_group);
/*  406:     */     }
/*  407:     */     
/*  408:     */     protected int[] attributeList(BitSet group)
/*  409:     */     {
/*  410: 614 */       int count = 0;
/*  411: 615 */       BitSet copy = (BitSet)group.clone();
/*  412: 624 */       for (int i = 0; i < DTNB.this.m_numAttributes; i++) {
/*  413: 625 */         if (copy.get(i)) {
/*  414: 626 */           count++;
/*  415:     */         }
/*  416:     */       }
/*  417: 630 */       int[] list = new int[count];
/*  418: 631 */       count = 0;
/*  419: 633 */       for (int i = 0; i < DTNB.this.m_numAttributes; i++) {
/*  420: 634 */         if (copy.get(i)) {
/*  421: 635 */           list[(count++)] = i;
/*  422:     */         }
/*  423:     */       }
/*  424: 639 */       return list;
/*  425:     */     }
/*  426:     */     
/*  427:     */     public String getRevision()
/*  428:     */     {
/*  429: 649 */       return RevisionUtils.extract("$Revision: 10341 $");
/*  430:     */     }
/*  431:     */   }
/*  432:     */   
/*  433:     */   private void setUpSearch()
/*  434:     */   {
/*  435: 654 */     this.m_backwardWithDelete = new BackwardsWithDelete();
/*  436:     */   }
/*  437:     */   
/*  438:     */   public void buildClassifier(Instances data)
/*  439:     */     throws Exception
/*  440:     */   {
/*  441: 666 */     this.m_saveMemory = false;
/*  442: 668 */     if (data.classAttribute().isNumeric()) {
/*  443: 669 */       throw new Exception("Can only handle nominal class!");
/*  444:     */     }
/*  445: 672 */     if (this.m_backwardWithDelete == null)
/*  446:     */     {
/*  447: 673 */       setUpSearch();
/*  448: 674 */       this.m_search = this.m_backwardWithDelete;
/*  449:     */     }
/*  450: 681 */     super.buildClassifier(data);
/*  451: 686 */     for (int i = 0; i < this.m_theInstances.numAttributes(); i++) {
/*  452: 687 */       this.m_theInstances.attribute(i).setWeight(1.0D);
/*  453:     */     }
/*  454: 690 */     int count = 0;
/*  455: 692 */     for (int m_decisionFeature : this.m_decisionFeatures) {
/*  456: 693 */       if (m_decisionFeature != this.m_theInstances.classIndex())
/*  457:     */       {
/*  458: 694 */         count++;
/*  459:     */         
/*  460: 696 */         this.m_theInstances.attribute(m_decisionFeature).setWeight(0.0D);
/*  461:     */       }
/*  462:     */     }
/*  463: 704 */     BitSet deleted = ((EvalWithDelete)this.m_evaluator).getDeletedList();
/*  464: 705 */     for (int i = 0; i < this.m_theInstances.numAttributes(); i++) {
/*  465: 706 */       if (deleted.get(i)) {
/*  466: 707 */         this.m_theInstances.attribute(i).setWeight(0.0D);
/*  467:     */       }
/*  468:     */     }
/*  469: 714 */     this.m_percentUsedByDT = (count / (this.m_theInstances.numAttributes() - 1));
/*  470:     */     
/*  471:     */ 
/*  472:     */ 
/*  473: 718 */     this.m_NB = new NaiveBayes();
/*  474: 719 */     this.m_NB.buildClassifier(this.m_theInstances);
/*  475:     */     
/*  476: 721 */     this.m_dtInstances = new Instances(this.m_dtInstances, 0);
/*  477: 722 */     this.m_theInstances = new Instances(this.m_theInstances, 0);
/*  478:     */   }
/*  479:     */   
/*  480:     */   public double[] distributionForInstance(Instance instance)
/*  481:     */     throws Exception
/*  482:     */   {
/*  483: 739 */     this.m_disTransform.input(instance);
/*  484: 740 */     this.m_disTransform.batchFinished();
/*  485: 741 */     instance = this.m_disTransform.output();
/*  486:     */     
/*  487: 743 */     this.m_delTransform.input(instance);
/*  488: 744 */     this.m_delTransform.batchFinished();
/*  489: 745 */     Instance dtInstance = this.m_delTransform.output();
/*  490:     */     
/*  491: 747 */     DecisionTableHashKey thekey = new DecisionTableHashKey(dtInstance, dtInstance.numAttributes(), false);
/*  492: 751 */     if ((tempDist = (double[])this.m_entries.get(thekey)) == null)
/*  493:     */     {
/*  494: 752 */       if (this.m_useIBk) {
/*  495: 753 */         tempDist = this.m_ibk.distributionForInstance(dtInstance);
/*  496:     */       } else {
/*  497: 758 */         tempDist = (double[])this.m_classPriors.clone();
/*  498:     */       }
/*  499:     */     }
/*  500:     */     else
/*  501:     */     {
/*  502: 763 */       double[] normDist = new double[tempDist.length];
/*  503: 764 */       System.arraycopy(tempDist, 0, normDist, 0, tempDist.length);
/*  504: 765 */       Utils.normalize(normDist);
/*  505: 766 */       tempDist = normDist;
/*  506:     */     }
/*  507: 769 */     double[] nbDist = this.m_NB.distributionForInstance(instance);
/*  508: 770 */     for (int i = 0; i < nbDist.length; i++)
/*  509:     */     {
/*  510: 771 */       tempDist[i] = (Math.log(tempDist[i]) - Math.log(this.m_classPriors[i]));
/*  511: 772 */       tempDist[i] += Math.log(nbDist[i]);
/*  512:     */     }
/*  513: 778 */     double[] tempDist = Utils.logs2probs(tempDist);
/*  514: 779 */     Utils.normalize(tempDist);
/*  515:     */     
/*  516: 781 */     return tempDist;
/*  517:     */   }
/*  518:     */   
/*  519:     */   public String toString()
/*  520:     */   {
/*  521: 787 */     String sS = super.toString();
/*  522: 788 */     if ((this.m_displayRules) && (this.m_NB != null)) {
/*  523: 789 */       sS = sS + this.m_NB.toString();
/*  524:     */     }
/*  525: 791 */     return sS;
/*  526:     */   }
/*  527:     */   
/*  528:     */   public double measurePercentAttsUsedByDT()
/*  529:     */   {
/*  530: 800 */     return this.m_percentUsedByDT;
/*  531:     */   }
/*  532:     */   
/*  533:     */   public Enumeration<String> enumerateMeasures()
/*  534:     */   {
/*  535: 810 */     Vector<String> newVector = new Vector(2);
/*  536: 811 */     newVector.addElement("measureNumRules");
/*  537: 812 */     newVector.addElement("measurePercentAttsUsedByDT");
/*  538: 813 */     return newVector.elements();
/*  539:     */   }
/*  540:     */   
/*  541:     */   public double getMeasure(String additionalMeasureName)
/*  542:     */   {
/*  543: 825 */     if (additionalMeasureName.compareToIgnoreCase("measureNumRules") == 0) {
/*  544: 826 */       return measureNumRules();
/*  545:     */     }
/*  546: 827 */     if (additionalMeasureName.compareToIgnoreCase("measurePercentAttsUsedByDT") == 0) {
/*  547: 829 */       return measurePercentAttsUsedByDT();
/*  548:     */     }
/*  549: 831 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (DecisionTable)");
/*  550:     */   }
/*  551:     */   
/*  552:     */   public Capabilities getCapabilities()
/*  553:     */   {
/*  554: 843 */     Capabilities result = super.getCapabilities();
/*  555:     */     
/*  556: 845 */     result.disable(Capabilities.Capability.NUMERIC_CLASS);
/*  557: 846 */     result.disable(Capabilities.Capability.DATE_CLASS);
/*  558:     */     
/*  559: 848 */     return result;
/*  560:     */   }
/*  561:     */   
/*  562:     */   public void setSearch(ASSearch search) {}
/*  563:     */   
/*  564:     */   public ASSearch getSearch()
/*  565:     */   {
/*  566: 870 */     if (this.m_backwardWithDelete == null)
/*  567:     */     {
/*  568: 871 */       setUpSearch();
/*  569:     */       
/*  570: 873 */       this.m_search = this.m_backwardWithDelete;
/*  571:     */     }
/*  572: 875 */     return this.m_search;
/*  573:     */   }
/*  574:     */   
/*  575:     */   public Enumeration<Option> listOptions()
/*  576:     */   {
/*  577: 886 */     Vector<Option> newVector = new Vector(4);
/*  578:     */     
/*  579: 888 */     newVector.addElement(new Option("\tUse cross validation to evaluate features.\n\tUse number of folds = 1 for leave one out CV.\n\t(Default = leave one out CV)", "X", 1, "-X <number of folds>"));
/*  580:     */     
/*  581:     */ 
/*  582:     */ 
/*  583:     */ 
/*  584: 893 */     newVector.addElement(new Option("\tPerformance evaluation measure to use for selecting attributes.\n\t(Default = accuracy for discrete class and rmse for numeric class)", "E", 1, "-E <acc | rmse | mae | auc>"));
/*  585:     */     
/*  586:     */ 
/*  587:     */ 
/*  588:     */ 
/*  589:     */ 
/*  590: 899 */     newVector.addElement(new Option("\tUse nearest neighbour instead of global table majority.", "I", 0, "-I"));
/*  591:     */     
/*  592:     */ 
/*  593:     */ 
/*  594:     */ 
/*  595: 904 */     newVector.addElement(new Option("\tDisplay decision table rules.\n", "R", 0, "-R"));
/*  596:     */     
/*  597:     */ 
/*  598: 907 */     newVector.addAll(Collections.list(super.listOptions()));
/*  599:     */     
/*  600: 909 */     return newVector.elements();
/*  601:     */   }
/*  602:     */   
/*  603:     */   public void setOptions(String[] options)
/*  604:     */     throws Exception
/*  605:     */   {
/*  606: 952 */     resetOptions();
/*  607:     */     
/*  608: 954 */     String optionString = Utils.getOption('X', options);
/*  609: 955 */     if (optionString.length() != 0) {
/*  610: 956 */       setCrossVal(Integer.parseInt(optionString));
/*  611:     */     }
/*  612: 959 */     this.m_useIBk = Utils.getFlag('I', options);
/*  613:     */     
/*  614: 961 */     this.m_displayRules = Utils.getFlag('R', options);
/*  615:     */     
/*  616: 963 */     optionString = Utils.getOption('E', options);
/*  617: 964 */     if (optionString.length() != 0) {
/*  618: 965 */       if (optionString.equals("acc")) {
/*  619: 966 */         setEvaluationMeasure(new SelectedTag(2, TAGS_EVALUATION));
/*  620: 967 */       } else if (optionString.equals("rmse")) {
/*  621: 968 */         setEvaluationMeasure(new SelectedTag(3, TAGS_EVALUATION));
/*  622: 969 */       } else if (optionString.equals("mae")) {
/*  623: 970 */         setEvaluationMeasure(new SelectedTag(4, TAGS_EVALUATION));
/*  624: 971 */       } else if (optionString.equals("auc")) {
/*  625: 972 */         setEvaluationMeasure(new SelectedTag(5, TAGS_EVALUATION));
/*  626:     */       } else {
/*  627: 974 */         throw new IllegalArgumentException("Invalid evaluation measure");
/*  628:     */       }
/*  629:     */     }
/*  630:     */   }
/*  631:     */   
/*  632:     */   public String[] getOptions()
/*  633:     */   {
/*  634: 987 */     String[] options = new String[9];
/*  635: 988 */     int current = 0;
/*  636:     */     
/*  637: 990 */     options[(current++)] = "-X";
/*  638: 991 */     options[(current++)] = ("" + getCrossVal());
/*  639: 993 */     if (this.m_evaluationMeasure != 1)
/*  640:     */     {
/*  641: 994 */       options[(current++)] = "-E";
/*  642: 995 */       switch (this.m_evaluationMeasure)
/*  643:     */       {
/*  644:     */       case 2: 
/*  645: 997 */         options[(current++)] = "acc";
/*  646: 998 */         break;
/*  647:     */       case 3: 
/*  648:1000 */         options[(current++)] = "rmse";
/*  649:1001 */         break;
/*  650:     */       case 4: 
/*  651:1003 */         options[(current++)] = "mae";
/*  652:1004 */         break;
/*  653:     */       case 5: 
/*  654:1006 */         options[(current++)] = "auc";
/*  655:     */       }
/*  656:     */     }
/*  657:1010 */     if (this.m_useIBk) {
/*  658:1011 */       options[(current++)] = "-I";
/*  659:     */     }
/*  660:1013 */     if (this.m_displayRules) {
/*  661:1014 */       options[(current++)] = "-R";
/*  662:     */     }
/*  663:1017 */     while (current < options.length) {
/*  664:1018 */       options[(current++)] = "";
/*  665:     */     }
/*  666:1020 */     return options;
/*  667:     */   }
/*  668:     */   
/*  669:     */   public String getRevision()
/*  670:     */   {
/*  671:1030 */     return RevisionUtils.extract("$Revision: 10341 $");
/*  672:     */   }
/*  673:     */   
/*  674:     */   public static void main(String[] argv)
/*  675:     */   {
/*  676:1039 */     runClassifier(new DTNB(), argv);
/*  677:     */   }
/*  678:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.DTNB
 * JD-Core Version:    0.7.0.1
 */