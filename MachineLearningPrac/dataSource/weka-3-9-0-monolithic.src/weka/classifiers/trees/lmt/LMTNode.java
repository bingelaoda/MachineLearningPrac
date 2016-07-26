/*   1:    */ package weka.classifiers.trees.lmt;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.classifiers.Evaluation;
/*   6:    */ import weka.classifiers.trees.j48.ClassifierSplitModel;
/*   7:    */ import weka.classifiers.trees.j48.ModelSelection;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.Utils;
/*  12:    */ import weka.filters.Filter;
/*  13:    */ import weka.filters.supervised.attribute.NominalToBinary;
/*  14:    */ 
/*  15:    */ public class LMTNode
/*  16:    */   extends LogisticBase
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = 1862737145870398755L;
/*  19:    */   protected double m_totalInstanceWeight;
/*  20:    */   protected int m_id;
/*  21:    */   protected int m_leafModelNum;
/*  22:    */   public double m_alpha;
/*  23:    */   public double m_numIncorrectModel;
/*  24:    */   public double m_numIncorrectTree;
/*  25:    */   protected int m_minNumInstances;
/*  26:    */   protected ModelSelection m_modelSelection;
/*  27:    */   protected NominalToBinary m_nominalToBinary;
/*  28:121 */   protected static int m_numFoldsPruning = 5;
/*  29:    */   protected boolean m_fastRegression;
/*  30:    */   protected int m_numInstances;
/*  31:    */   protected ClassifierSplitModel m_localModel;
/*  32:    */   protected LMTNode[] m_sons;
/*  33:    */   protected boolean m_isLeaf;
/*  34:    */   
/*  35:    */   public LMTNode(ModelSelection modelSelection, int numBoostingIterations, boolean fastRegression, boolean errorOnProbabilities, int minNumInstances, double weightTrimBeta, boolean useAIC, NominalToBinary ntb, int numDecimalPlaces)
/*  36:    */   {
/*  37:155 */     this.m_modelSelection = modelSelection;
/*  38:156 */     this.m_fixedNumIterations = numBoostingIterations;
/*  39:157 */     this.m_fastRegression = fastRegression;
/*  40:158 */     this.m_errorOnProbabilities = errorOnProbabilities;
/*  41:159 */     this.m_minNumInstances = minNumInstances;
/*  42:160 */     this.m_maxIterations = 200;
/*  43:161 */     setWeightTrimBeta(weightTrimBeta);
/*  44:162 */     setUseAIC(useAIC);
/*  45:163 */     this.m_nominalToBinary = ntb;
/*  46:164 */     this.m_numDecimalPlaces = numDecimalPlaces;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void buildClassifier(Instances data)
/*  50:    */     throws Exception
/*  51:    */   {
/*  52:182 */     if ((this.m_fastRegression) && (this.m_fixedNumIterations < 0)) {
/*  53:183 */       this.m_fixedNumIterations = tryLogistic(data);
/*  54:    */     }
/*  55:187 */     Instances cvData = new Instances(data);
/*  56:188 */     cvData.stratify(m_numFoldsPruning);
/*  57:    */     
/*  58:190 */     double[][] alphas = new double[m_numFoldsPruning][];
/*  59:191 */     double[][] errors = new double[m_numFoldsPruning][];
/*  60:193 */     for (int i = 0; i < m_numFoldsPruning; i++)
/*  61:    */     {
/*  62:195 */       Instances train = cvData.trainCV(m_numFoldsPruning, i);
/*  63:196 */       Instances test = cvData.testCV(m_numFoldsPruning, i);
/*  64:    */       
/*  65:198 */       buildTree(train, (SimpleLinearRegression[][])null, train.numInstances(), 0.0D, null);
/*  66:    */       
/*  67:200 */       int numNodes = getNumInnerNodes();
/*  68:201 */       alphas[i] = new double[numNodes + 2];
/*  69:202 */       errors[i] = new double[numNodes + 2];
/*  70:    */       
/*  71:    */ 
/*  72:205 */       prune(alphas[i], errors[i], test);
/*  73:    */     }
/*  74:209 */     cvData = null;
/*  75:    */     
/*  76:    */ 
/*  77:212 */     buildTree(data, (SimpleLinearRegression[][])null, data.numInstances(), 0.0D, null);
/*  78:213 */     int numNodes = getNumInnerNodes();
/*  79:    */     
/*  80:215 */     double[] treeAlphas = new double[numNodes + 2];
/*  81:    */     
/*  82:    */ 
/*  83:218 */     int iterations = prune(treeAlphas, null, null);
/*  84:    */     
/*  85:220 */     double[] treeErrors = new double[numNodes + 2];
/*  86:222 */     for (int i = 0; i <= iterations; i++)
/*  87:    */     {
/*  88:224 */       double alpha = Math.sqrt(treeAlphas[i] * treeAlphas[(i + 1)]);
/*  89:225 */       double error = 0.0D;
/*  90:230 */       for (int k = 0; k < m_numFoldsPruning; k++)
/*  91:    */       {
/*  92:231 */         int l = 0;
/*  93:232 */         while (alphas[k][l] <= alpha) {
/*  94:233 */           l++;
/*  95:    */         }
/*  96:235 */         error += errors[k][(l - 1)];
/*  97:    */       }
/*  98:238 */       treeErrors[i] = error;
/*  99:    */     }
/* 100:242 */     int best = -1;
/* 101:243 */     double bestError = 1.7976931348623157E+308D;
/* 102:244 */     for (int i = iterations; i >= 0; i--) {
/* 103:245 */       if (treeErrors[i] < bestError)
/* 104:    */       {
/* 105:246 */         bestError = treeErrors[i];
/* 106:247 */         best = i;
/* 107:    */       }
/* 108:    */     }
/* 109:251 */     double bestAlpha = Math.sqrt(treeAlphas[best] * treeAlphas[(best + 1)]);
/* 110:    */     
/* 111:    */ 
/* 112:254 */     unprune();
/* 113:    */     
/* 114:    */ 
/* 115:257 */     prune(bestAlpha);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void buildTree(Instances data, SimpleLinearRegression[][] higherRegressions, double totalInstanceWeight, double higherNumParameters, Instances numericDataHeader)
/* 119:    */     throws Exception
/* 120:    */   {
/* 121:278 */     this.m_totalInstanceWeight = totalInstanceWeight;
/* 122:279 */     this.m_train = data;
/* 123:    */     
/* 124:281 */     this.m_isLeaf = true;
/* 125:282 */     this.m_sons = null;
/* 126:    */     
/* 127:284 */     this.m_numInstances = this.m_train.numInstances();
/* 128:285 */     this.m_numClasses = this.m_train.numClasses();
/* 129:    */     
/* 130:    */ 
/* 131:288 */     this.m_numericDataHeader = numericDataHeader;
/* 132:289 */     this.m_numericData = getNumericData(this.m_train);
/* 133:291 */     if (higherRegressions == null) {
/* 134:292 */       this.m_regressions = initRegressions();
/* 135:    */     } else {
/* 136:294 */       this.m_regressions = higherRegressions;
/* 137:    */     }
/* 138:297 */     this.m_numParameters = higherNumParameters;
/* 139:298 */     this.m_numRegressions = 0;
/* 140:301 */     if (this.m_numInstances >= m_numFoldsBoosting) {
/* 141:302 */       if (this.m_fixedNumIterations > 0) {
/* 142:303 */         performBoosting(this.m_fixedNumIterations);
/* 143:304 */       } else if (getUseAIC()) {
/* 144:305 */         performBoostingInfCriterion();
/* 145:    */       } else {
/* 146:307 */         performBoostingCV();
/* 147:    */       }
/* 148:    */     }
/* 149:311 */     this.m_numParameters += this.m_numRegressions;
/* 150:    */     
/* 151:    */ 
/* 152:314 */     Evaluation eval = new Evaluation(this.m_train);
/* 153:315 */     eval.evaluateModel(this, this.m_train, new Object[0]);
/* 154:316 */     this.m_numIncorrectModel = eval.incorrect();
/* 155:    */     boolean grow;
/* 156:    */     boolean grow;
/* 157:320 */     if (this.m_numInstances > this.m_minNumInstances)
/* 158:    */     {
/* 159:323 */       if ((this.m_modelSelection instanceof ResidualModelSelection))
/* 160:    */       {
/* 161:325 */         double[][] probs = getProbs(getFs(this.m_numericData));
/* 162:326 */         double[][] trainYs = getYs(this.m_train);
/* 163:327 */         double[][] dataZs = getZs(probs, trainYs);
/* 164:328 */         double[][] dataWs = getWs(probs, trainYs);
/* 165:329 */         this.m_localModel = ((ResidualModelSelection)this.m_modelSelection).selectModel(this.m_train, dataZs, dataWs);
/* 166:    */       }
/* 167:    */       else
/* 168:    */       {
/* 169:332 */         this.m_localModel = this.m_modelSelection.selectModel(this.m_train);
/* 170:    */       }
/* 171:335 */       grow = this.m_localModel.numSubsets() > 1;
/* 172:    */     }
/* 173:    */     else
/* 174:    */     {
/* 175:337 */       grow = false;
/* 176:    */     }
/* 177:340 */     if (grow)
/* 178:    */     {
/* 179:342 */       this.m_isLeaf = false;
/* 180:343 */       Instances[] localInstances = this.m_localModel.split(this.m_train);
/* 181:    */       
/* 182:    */ 
/* 183:346 */       cleanup();
/* 184:    */       
/* 185:348 */       this.m_sons = new LMTNode[this.m_localModel.numSubsets()];
/* 186:349 */       for (int i = 0; i < this.m_sons.length; i++)
/* 187:    */       {
/* 188:350 */         this.m_sons[i] = new LMTNode(this.m_modelSelection, this.m_fixedNumIterations, this.m_fastRegression, this.m_errorOnProbabilities, this.m_minNumInstances, getWeightTrimBeta(), getUseAIC(), this.m_nominalToBinary, this.m_numDecimalPlaces);
/* 189:    */         
/* 190:    */ 
/* 191:353 */         this.m_sons[i].buildTree(localInstances[i], copyRegressions(this.m_regressions), this.m_totalInstanceWeight, this.m_numParameters, this.m_numericDataHeader);
/* 192:    */         
/* 193:355 */         localInstances[i] = null;
/* 194:    */       }
/* 195:    */     }
/* 196:    */     else
/* 197:    */     {
/* 198:358 */       cleanup();
/* 199:    */     }
/* 200:    */   }
/* 201:    */   
/* 202:    */   public void prune(double alpha)
/* 203:    */     throws Exception
/* 204:    */   {
/* 205:372 */     CompareNode comparator = new CompareNode();
/* 206:    */     
/* 207:    */ 
/* 208:    */ 
/* 209:376 */     treeErrors();
/* 210:377 */     calculateAlphas();
/* 211:    */     
/* 212:    */ 
/* 213:380 */     Vector<LMTNode> nodeList = getNodes();
/* 214:    */     
/* 215:382 */     boolean prune = nodeList.size() > 0;
/* 216:384 */     while (prune)
/* 217:    */     {
/* 218:387 */       LMTNode nodeToPrune = (LMTNode)Collections.min(nodeList, comparator);
/* 219:390 */       if (nodeToPrune.m_alpha > alpha) {
/* 220:    */         break;
/* 221:    */       }
/* 222:394 */       nodeToPrune.m_isLeaf = true;
/* 223:395 */       nodeToPrune.m_sons = null;
/* 224:    */       
/* 225:    */ 
/* 226:398 */       treeErrors();
/* 227:399 */       calculateAlphas();
/* 228:    */       
/* 229:401 */       nodeList = getNodes();
/* 230:402 */       prune = nodeList.size() > 0;
/* 231:    */     }
/* 232:407 */     for (Object node : getNodes())
/* 233:    */     {
/* 234:408 */       LMTNode lnode = (LMTNode)node;
/* 235:409 */       if (!lnode.m_isLeaf) {
/* 236:410 */         this.m_regressions = ((SimpleLinearRegression[][])null);
/* 237:    */       }
/* 238:    */     }
/* 239:    */   }
/* 240:    */   
/* 241:    */   public int prune(double[] alphas, double[] errors, Instances test)
/* 242:    */     throws Exception
/* 243:    */   {
/* 244:431 */     CompareNode comparator = new CompareNode();
/* 245:    */     
/* 246:    */ 
/* 247:    */ 
/* 248:435 */     treeErrors();
/* 249:436 */     calculateAlphas();
/* 250:    */     
/* 251:    */ 
/* 252:439 */     Vector<LMTNode> nodeList = getNodes();
/* 253:    */     
/* 254:441 */     boolean prune = nodeList.size() > 0;
/* 255:    */     
/* 256:    */ 
/* 257:444 */     alphas[0] = 0.0D;
/* 258:449 */     if (errors != null)
/* 259:    */     {
/* 260:450 */       Evaluation eval = new Evaluation(test);
/* 261:451 */       eval.evaluateModel(this, test, new Object[0]);
/* 262:452 */       errors[0] = eval.errorRate();
/* 263:    */     }
/* 264:455 */     int iteration = 0;
/* 265:456 */     while (prune)
/* 266:    */     {
/* 267:458 */       iteration++;
/* 268:    */       
/* 269:    */ 
/* 270:461 */       LMTNode nodeToPrune = (LMTNode)Collections.min(nodeList, comparator);
/* 271:    */       
/* 272:463 */       nodeToPrune.m_isLeaf = true;
/* 273:    */       
/* 274:    */ 
/* 275:    */ 
/* 276:467 */       alphas[iteration] = nodeToPrune.m_alpha;
/* 277:470 */       if (errors != null)
/* 278:    */       {
/* 279:471 */         Evaluation eval = new Evaluation(test);
/* 280:472 */         eval.evaluateModel(this, test, new Object[0]);
/* 281:473 */         errors[iteration] = eval.errorRate();
/* 282:    */       }
/* 283:477 */       treeErrors();
/* 284:478 */       calculateAlphas();
/* 285:    */       
/* 286:480 */       nodeList = getNodes();
/* 287:481 */       prune = nodeList.size() > 0;
/* 288:    */     }
/* 289:485 */     alphas[(iteration + 1)] = 1.0D;
/* 290:486 */     return iteration;
/* 291:    */   }
/* 292:    */   
/* 293:    */   protected void unprune()
/* 294:    */   {
/* 295:495 */     if (this.m_sons != null)
/* 296:    */     {
/* 297:496 */       this.m_isLeaf = false;
/* 298:497 */       for (LMTNode m_son : this.m_sons) {
/* 299:498 */         m_son.unprune();
/* 300:    */       }
/* 301:    */     }
/* 302:    */   }
/* 303:    */   
/* 304:    */   protected int tryLogistic(Instances data)
/* 305:    */     throws Exception
/* 306:    */   {
/* 307:515 */     Instances filteredData = Filter.useFilter(data, this.m_nominalToBinary);
/* 308:    */     
/* 309:517 */     LogisticBase logistic = new LogisticBase(0, true, this.m_errorOnProbabilities);
/* 310:    */     
/* 311:    */ 
/* 312:520 */     logistic.setMaxIterations(200);
/* 313:521 */     logistic.setWeightTrimBeta(getWeightTrimBeta());
/* 314:    */     
/* 315:523 */     logistic.setUseAIC(getUseAIC());
/* 316:524 */     logistic.buildClassifier(filteredData);
/* 317:    */     
/* 318:    */ 
/* 319:527 */     return logistic.getNumRegressions();
/* 320:    */   }
/* 321:    */   
/* 322:    */   public int getNumInnerNodes()
/* 323:    */   {
/* 324:536 */     if (this.m_isLeaf) {
/* 325:537 */       return 0;
/* 326:    */     }
/* 327:539 */     int numNodes = 1;
/* 328:540 */     for (LMTNode m_son : this.m_sons) {
/* 329:541 */       numNodes += m_son.getNumInnerNodes();
/* 330:    */     }
/* 331:543 */     return numNodes;
/* 332:    */   }
/* 333:    */   
/* 334:    */   public int getNumLeaves()
/* 335:    */   {
/* 336:    */     int numLeaves;
/* 337:554 */     if (!this.m_isLeaf)
/* 338:    */     {
/* 339:555 */       int numLeaves = 0;
/* 340:556 */       int numEmptyLeaves = 0;
/* 341:557 */       for (int i = 0; i < this.m_sons.length; i++)
/* 342:    */       {
/* 343:558 */         numLeaves += this.m_sons[i].getNumLeaves();
/* 344:559 */         if ((this.m_sons[i].m_isLeaf) && (!this.m_sons[i].hasModels())) {
/* 345:560 */           numEmptyLeaves++;
/* 346:    */         }
/* 347:    */       }
/* 348:563 */       if (numEmptyLeaves > 1) {
/* 349:564 */         numLeaves -= numEmptyLeaves - 1;
/* 350:    */       }
/* 351:    */     }
/* 352:    */     else
/* 353:    */     {
/* 354:567 */       numLeaves = 1;
/* 355:    */     }
/* 356:569 */     return numLeaves;
/* 357:    */   }
/* 358:    */   
/* 359:    */   public void treeErrors()
/* 360:    */   {
/* 361:577 */     if (this.m_isLeaf)
/* 362:    */     {
/* 363:578 */       this.m_numIncorrectTree = this.m_numIncorrectModel;
/* 364:    */     }
/* 365:    */     else
/* 366:    */     {
/* 367:580 */       this.m_numIncorrectTree = 0.0D;
/* 368:581 */       for (LMTNode m_son : this.m_sons)
/* 369:    */       {
/* 370:582 */         m_son.treeErrors();
/* 371:583 */         this.m_numIncorrectTree += m_son.m_numIncorrectTree;
/* 372:    */       }
/* 373:    */     }
/* 374:    */   }
/* 375:    */   
/* 376:    */   public void calculateAlphas()
/* 377:    */     throws Exception
/* 378:    */   {
/* 379:593 */     if (!this.m_isLeaf)
/* 380:    */     {
/* 381:594 */       double errorDiff = this.m_numIncorrectModel - this.m_numIncorrectTree;
/* 382:596 */       if (errorDiff <= 0.0D)
/* 383:    */       {
/* 384:599 */         this.m_isLeaf = true;
/* 385:600 */         this.m_sons = null;
/* 386:601 */         this.m_alpha = 1.7976931348623157E+308D;
/* 387:    */       }
/* 388:    */       else
/* 389:    */       {
/* 390:604 */         errorDiff /= this.m_totalInstanceWeight;
/* 391:605 */         this.m_alpha = (errorDiff / (getNumLeaves() - 1));
/* 392:607 */         for (LMTNode m_son : this.m_sons) {
/* 393:608 */           m_son.calculateAlphas();
/* 394:    */         }
/* 395:    */       }
/* 396:    */     }
/* 397:    */     else
/* 398:    */     {
/* 399:613 */       this.m_alpha = 1.7976931348623157E+308D;
/* 400:    */     }
/* 401:    */   }
/* 402:    */   
/* 403:    */   public Vector<LMTNode> getNodes()
/* 404:    */   {
/* 405:623 */     Vector<LMTNode> nodeList = new Vector();
/* 406:624 */     getNodes(nodeList);
/* 407:625 */     return nodeList;
/* 408:    */   }
/* 409:    */   
/* 410:    */   public void getNodes(Vector<LMTNode> nodeList)
/* 411:    */   {
/* 412:634 */     if (!this.m_isLeaf)
/* 413:    */     {
/* 414:635 */       nodeList.add(this);
/* 415:636 */       for (LMTNode m_son : this.m_sons) {
/* 416:637 */         m_son.getNodes(nodeList);
/* 417:    */       }
/* 418:    */     }
/* 419:    */   }
/* 420:    */   
/* 421:    */   protected Instances getNumericData(Instances train)
/* 422:    */     throws Exception
/* 423:    */   {
/* 424:650 */     Instances filteredData = Filter.useFilter(train, this.m_nominalToBinary);
/* 425:    */     
/* 426:652 */     return super.getNumericData(filteredData);
/* 427:    */   }
/* 428:    */   
/* 429:    */   public boolean hasModels()
/* 430:    */   {
/* 431:662 */     return this.m_numRegressions > 0;
/* 432:    */   }
/* 433:    */   
/* 434:    */   public double[] modelDistributionForInstance(Instance instance)
/* 435:    */     throws Exception
/* 436:    */   {
/* 437:676 */     this.m_nominalToBinary.input(instance);
/* 438:677 */     instance = this.m_nominalToBinary.output();
/* 439:    */     
/* 440:    */ 
/* 441:680 */     instance.setDataset(this.m_numericDataHeader);
/* 442:    */     
/* 443:682 */     return probs(getFs(instance));
/* 444:    */   }
/* 445:    */   
/* 446:    */   public double[] distributionForInstance(Instance instance)
/* 447:    */     throws Exception
/* 448:    */   {
/* 449:    */     double[] probs;
/* 450:    */     double[] probs;
/* 451:697 */     if (this.m_isLeaf)
/* 452:    */     {
/* 453:699 */       probs = modelDistributionForInstance(instance);
/* 454:    */     }
/* 455:    */     else
/* 456:    */     {
/* 457:702 */       int branch = this.m_localModel.whichSubset(instance);
/* 458:703 */       probs = this.m_sons[branch].distributionForInstance(instance);
/* 459:    */     }
/* 460:705 */     return probs;
/* 461:    */   }
/* 462:    */   
/* 463:    */   public int numLeaves()
/* 464:    */   {
/* 465:714 */     if (this.m_isLeaf) {
/* 466:715 */       return 1;
/* 467:    */     }
/* 468:717 */     int numLeaves = 0;
/* 469:718 */     for (LMTNode m_son : this.m_sons) {
/* 470:719 */       numLeaves += m_son.numLeaves();
/* 471:    */     }
/* 472:721 */     return numLeaves;
/* 473:    */   }
/* 474:    */   
/* 475:    */   public int numNodes()
/* 476:    */   {
/* 477:730 */     if (this.m_isLeaf) {
/* 478:731 */       return 1;
/* 479:    */     }
/* 480:733 */     int numNodes = 1;
/* 481:734 */     for (LMTNode m_son : this.m_sons) {
/* 482:735 */       numNodes += m_son.numNodes();
/* 483:    */     }
/* 484:737 */     return numNodes;
/* 485:    */   }
/* 486:    */   
/* 487:    */   public String toString()
/* 488:    */   {
/* 489:749 */     assignLeafModelNumbers(0);
/* 490:    */     try
/* 491:    */     {
/* 492:751 */       StringBuffer text = new StringBuffer();
/* 493:753 */       if (this.m_isLeaf)
/* 494:    */       {
/* 495:754 */         text.append(": ");
/* 496:755 */         text.append("LM_" + this.m_leafModelNum + ":" + getModelParameters());
/* 497:    */       }
/* 498:    */       else
/* 499:    */       {
/* 500:757 */         dumpTree(0, text);
/* 501:    */       }
/* 502:759 */       text.append("\n\nNumber of Leaves  : \t" + numLeaves() + "\n");
/* 503:760 */       text.append("\nSize of the Tree : \t" + numNodes() + "\n");
/* 504:    */       
/* 505:    */ 
/* 506:    */ 
/* 507:764 */       text.append(modelsToString());
/* 508:765 */       return text.toString();
/* 509:    */     }
/* 510:    */     catch (Exception e) {}
/* 511:767 */     return "Can't print logistic model tree";
/* 512:    */   }
/* 513:    */   
/* 514:    */   public String getModelParameters()
/* 515:    */   {
/* 516:782 */     StringBuffer text = new StringBuffer();
/* 517:783 */     int numModels = (int)this.m_numParameters;
/* 518:784 */     text.append(this.m_numRegressions + "/" + numModels + " (" + this.m_numInstances + ")");
/* 519:    */     
/* 520:786 */     return text.toString();
/* 521:    */   }
/* 522:    */   
/* 523:    */   protected void dumpTree(int depth, StringBuffer text)
/* 524:    */     throws Exception
/* 525:    */   {
/* 526:796 */     for (int i = 0; i < this.m_sons.length; i++)
/* 527:    */     {
/* 528:797 */       text.append("\n");
/* 529:798 */       for (int j = 0; j < depth; j++) {
/* 530:799 */         text.append("|   ");
/* 531:    */       }
/* 532:801 */       text.append(this.m_localModel.leftSide(this.m_train));
/* 533:802 */       text.append(this.m_localModel.rightSide(i, this.m_train));
/* 534:803 */       if (this.m_sons[i].m_isLeaf)
/* 535:    */       {
/* 536:804 */         text.append(": ");
/* 537:805 */         text.append("LM_" + this.m_sons[i].m_leafModelNum + ":" + this.m_sons[i].getModelParameters());
/* 538:    */       }
/* 539:    */       else
/* 540:    */       {
/* 541:808 */         this.m_sons[i].dumpTree(depth + 1, text);
/* 542:    */       }
/* 543:    */     }
/* 544:    */   }
/* 545:    */   
/* 546:    */   public int assignIDs(int lastID)
/* 547:    */   {
/* 548:818 */     int currLastID = lastID + 1;
/* 549:    */     
/* 550:820 */     this.m_id = currLastID;
/* 551:821 */     if (this.m_sons != null) {
/* 552:822 */       for (LMTNode m_son : this.m_sons) {
/* 553:823 */         currLastID = m_son.assignIDs(currLastID);
/* 554:    */       }
/* 555:    */     }
/* 556:826 */     return currLastID;
/* 557:    */   }
/* 558:    */   
/* 559:    */   public int assignLeafModelNumbers(int leafCounter)
/* 560:    */   {
/* 561:833 */     if (!this.m_isLeaf)
/* 562:    */     {
/* 563:834 */       this.m_leafModelNum = 0;
/* 564:835 */       for (LMTNode m_son : this.m_sons) {
/* 565:836 */         leafCounter = m_son.assignLeafModelNumbers(leafCounter);
/* 566:    */       }
/* 567:    */     }
/* 568:    */     else
/* 569:    */     {
/* 570:839 */       leafCounter++;
/* 571:840 */       this.m_leafModelNum = leafCounter;
/* 572:    */     }
/* 573:842 */     return leafCounter;
/* 574:    */   }
/* 575:    */   
/* 576:    */   public String modelsToString()
/* 577:    */   {
/* 578:850 */     StringBuffer text = new StringBuffer();
/* 579:851 */     if (this.m_isLeaf) {
/* 580:852 */       text.append("LM_" + this.m_leafModelNum + ":" + super.toString());
/* 581:    */     } else {
/* 582:854 */       for (LMTNode m_son : this.m_sons) {
/* 583:855 */         text.append("\n" + m_son.modelsToString());
/* 584:    */       }
/* 585:    */     }
/* 586:858 */     return text.toString();
/* 587:    */   }
/* 588:    */   
/* 589:    */   public String graph()
/* 590:    */     throws Exception
/* 591:    */   {
/* 592:868 */     StringBuffer text = new StringBuffer();
/* 593:    */     
/* 594:870 */     assignIDs(-1);
/* 595:871 */     assignLeafModelNumbers(0);
/* 596:872 */     text.append("digraph LMTree {\n");
/* 597:873 */     if (this.m_isLeaf)
/* 598:    */     {
/* 599:874 */       text.append("N" + this.m_id + " [label=\"LM_" + this.m_leafModelNum + ":" + getModelParameters() + "\" " + "shape=box style=filled");
/* 600:    */       
/* 601:876 */       text.append("]\n");
/* 602:    */     }
/* 603:    */     else
/* 604:    */     {
/* 605:878 */       text.append("N" + this.m_id + " [label=\"" + Utils.backQuoteChars(this.m_localModel.leftSide(this.m_train)) + "\" ");
/* 606:    */       
/* 607:880 */       text.append("]\n");
/* 608:881 */       graphTree(text);
/* 609:    */     }
/* 610:884 */     return text.toString() + "}\n";
/* 611:    */   }
/* 612:    */   
/* 613:    */   private void graphTree(StringBuffer text)
/* 614:    */     throws Exception
/* 615:    */   {
/* 616:894 */     for (int i = 0; i < this.m_sons.length; i++)
/* 617:    */     {
/* 618:895 */       text.append("N" + this.m_id + "->" + "N" + this.m_sons[i].m_id + " [label=\"" + Utils.backQuoteChars(this.m_localModel.rightSide(i, this.m_train).trim()) + "\"]\n");
/* 619:898 */       if (this.m_sons[i].m_isLeaf)
/* 620:    */       {
/* 621:899 */         text.append("N" + this.m_sons[i].m_id + " [label=\"LM_" + this.m_sons[i].m_leafModelNum + ":" + this.m_sons[i].getModelParameters() + "\" " + "shape=box style=filled");
/* 622:    */         
/* 623:    */ 
/* 624:902 */         text.append("]\n");
/* 625:    */       }
/* 626:    */       else
/* 627:    */       {
/* 628:904 */         text.append("N" + this.m_sons[i].m_id + " [label=\"" + Utils.backQuoteChars(this.m_sons[i].m_localModel.leftSide(this.m_train)) + "\" ");
/* 629:    */         
/* 630:    */ 
/* 631:907 */         text.append("]\n");
/* 632:908 */         this.m_sons[i].graphTree(text);
/* 633:    */       }
/* 634:    */     }
/* 635:    */   }
/* 636:    */   
/* 637:    */   public String getRevision()
/* 638:    */   {
/* 639:920 */     return RevisionUtils.extract("$Revision: 11566 $");
/* 640:    */   }
/* 641:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.lmt.LMTNode
 * JD-Core Version:    0.7.0.1
 */