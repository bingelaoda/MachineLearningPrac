/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Hashtable;
/*   7:    */ import java.util.TreeSet;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.TechnicalInformation;
/*  16:    */ import weka.core.TechnicalInformation.Field;
/*  17:    */ import weka.core.TechnicalInformation.Type;
/*  18:    */ import weka.core.TechnicalInformationHandler;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.core.WekaEnumeration;
/*  21:    */ 
/*  22:    */ public class PredictiveApriori
/*  23:    */   extends AbstractAssociator
/*  24:    */   implements OptionHandler, CARuleMiner, TechnicalInformationHandler
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = 8109088846865075341L;
/*  27:    */   protected int m_premiseCount;
/*  28:    */   protected int m_numRules;
/*  29:    */   protected static final int m_numRandRules = 1000;
/*  30:    */   protected static final int m_numIntervals = 100;
/*  31:    */   protected ArrayList<ArrayList<Object>> m_Ls;
/*  32:    */   protected ArrayList<Hashtable<ItemSet, Integer>> m_hashtables;
/*  33:    */   protected ArrayList<Object>[] m_allTheRules;
/*  34:    */   protected Instances m_instances;
/*  35:    */   protected Hashtable<Double, Double> m_priors;
/*  36:    */   protected double[] m_midPoints;
/*  37:    */   protected double m_expectation;
/*  38:    */   protected TreeSet<RuleItem> m_best;
/*  39:    */   protected boolean m_bestChanged;
/*  40:    */   protected int m_count;
/*  41:    */   protected PriorEstimation m_priorEstimator;
/*  42:    */   protected int m_classIndex;
/*  43:    */   protected boolean m_car;
/*  44:    */   
/*  45:    */   public String globalInfo()
/*  46:    */   {
/*  47:172 */     return "Class implementing the predictive apriori algorithm to mine association rules.\nIt searches with an increasing support threshold for the best 'n' rules concerning a support-based corrected confidence value.\n\nFor more information see:\n\n" + getTechnicalInformation().toString() + "\n\n" + "The implementation follows the paper expect for adding a rule to the " + "output of the 'n' best rules. A rule is added if:\n" + "the expected predictive accuracy of this rule is among the 'n' best " + "and it is not subsumed by a rule with at least the same expected " + "predictive accuracy (out of an unpublished manuscript from T. " + "Scheffer).";
/*  48:    */   }
/*  49:    */   
/*  50:    */   public TechnicalInformation getTechnicalInformation()
/*  51:    */   {
/*  52:197 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  53:198 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Tobias Scheffer");
/*  54:199 */     result.setValue(TechnicalInformation.Field.TITLE, "Finding Association Rules That Trade Support Optimally against Confidence");
/*  55:    */     
/*  56:    */ 
/*  57:202 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "5th European Conference on Principles of Data Mining and Knowledge Discovery");
/*  58:    */     
/*  59:    */ 
/*  60:205 */     result.setValue(TechnicalInformation.Field.YEAR, "2001");
/*  61:206 */     result.setValue(TechnicalInformation.Field.PAGES, "424-435");
/*  62:207 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  63:    */     
/*  64:209 */     return result;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public PredictiveApriori()
/*  68:    */   {
/*  69:218 */     resetOptions();
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void resetOptions()
/*  73:    */   {
/*  74:226 */     this.m_numRules = 105;
/*  75:227 */     this.m_premiseCount = 1;
/*  76:228 */     this.m_best = new TreeSet();
/*  77:229 */     this.m_bestChanged = false;
/*  78:230 */     this.m_expectation = 0.0D;
/*  79:231 */     this.m_count = 1;
/*  80:232 */     this.m_car = false;
/*  81:233 */     this.m_classIndex = -1;
/*  82:234 */     this.m_priors = new Hashtable();
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Capabilities getCapabilities()
/*  86:    */   {
/*  87:245 */     Capabilities result = super.getCapabilities();
/*  88:246 */     result.disableAll();
/*  89:    */     
/*  90:    */ 
/*  91:249 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  92:250 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  93:    */     
/*  94:    */ 
/*  95:253 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  96:254 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  97:255 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  98:    */     
/*  99:257 */     return result;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void buildAssociations(Instances instances)
/* 103:    */     throws Exception
/* 104:    */   {
/* 105:271 */     int temp = this.m_premiseCount;int exactNumber = this.m_numRules - 5;
/* 106:    */     
/* 107:273 */     this.m_premiseCount = 1;
/* 108:274 */     this.m_best = new TreeSet();
/* 109:275 */     this.m_bestChanged = false;
/* 110:276 */     this.m_expectation = 0.0D;
/* 111:277 */     this.m_count = 1;
/* 112:278 */     this.m_instances = new Instances(instances);
/* 113:280 */     if (this.m_classIndex == -1) {
/* 114:281 */       this.m_instances.setClassIndex(this.m_instances.numAttributes() - 1);
/* 115:282 */     } else if ((this.m_classIndex <= this.m_instances.numAttributes()) && (this.m_classIndex > 0)) {
/* 116:283 */       this.m_instances.setClassIndex(this.m_classIndex - 1);
/* 117:    */     } else {
/* 118:285 */       throw new Exception("Invalid class index.");
/* 119:    */     }
/* 120:289 */     getCapabilities().testWithFail(this.m_instances);
/* 121:    */     
/* 122:    */ 
/* 123:292 */     this.m_priorEstimator = new PriorEstimation(this.m_instances, 1000, 100, this.m_car);
/* 124:    */     
/* 125:294 */     this.m_priors = this.m_priorEstimator.estimatePrior();
/* 126:295 */     this.m_midPoints = this.m_priorEstimator.getMidPoints();
/* 127:    */     
/* 128:297 */     this.m_Ls = new ArrayList();
/* 129:298 */     this.m_hashtables = new ArrayList();
/* 130:300 */     for (int i = 1; i < this.m_instances.numAttributes(); i++)
/* 131:    */     {
/* 132:301 */       this.m_bestChanged = false;
/* 133:302 */       if (!this.m_car)
/* 134:    */       {
/* 135:304 */         findLargeItemSets(i);
/* 136:    */         
/* 137:    */ 
/* 138:307 */         findRulesQuickly();
/* 139:    */       }
/* 140:    */       else
/* 141:    */       {
/* 142:309 */         findLargeCarItemSets(i);
/* 143:310 */         findCaRulesQuickly();
/* 144:    */       }
/* 145:313 */       if (this.m_bestChanged)
/* 146:    */       {
/* 147:314 */         temp = this.m_premiseCount;
/* 148:316 */         while (RuleGeneration.expectation(this.m_premiseCount, this.m_premiseCount, this.m_midPoints, this.m_priors) <= this.m_expectation)
/* 149:    */         {
/* 150:317 */           this.m_premiseCount += 1;
/* 151:318 */           if (this.m_premiseCount > this.m_instances.numInstances()) {
/* 152:    */             break;
/* 153:    */           }
/* 154:    */         }
/* 155:    */       }
/* 156:323 */       if (this.m_premiseCount > this.m_instances.numInstances())
/* 157:    */       {
/* 158:326 */         this.m_allTheRules = new ArrayList[3];
/* 159:327 */         this.m_allTheRules[0] = new ArrayList();
/* 160:328 */         this.m_allTheRules[1] = new ArrayList();
/* 161:329 */         this.m_allTheRules[2] = new ArrayList();
/* 162:    */         
/* 163:331 */         int k = 0;
/* 164:332 */         while ((this.m_best.size() > 0) && (exactNumber > 0))
/* 165:    */         {
/* 166:333 */           this.m_allTheRules[0].add(k, ((RuleItem)this.m_best.last()).premise());
/* 167:334 */           this.m_allTheRules[1].add(k, ((RuleItem)this.m_best.last()).consequence());
/* 168:335 */           this.m_allTheRules[2].add(k, new Double(((RuleItem)this.m_best.last()).accuracy()));
/* 169:336 */           this.m_best.remove(this.m_best.last());
/* 170:337 */           k++;
/* 171:338 */           exactNumber--;
/* 172:    */         }
/* 173:340 */         return;
/* 174:    */       }
/* 175:343 */       if ((temp != this.m_premiseCount) && (this.m_Ls.size() > 0))
/* 176:    */       {
/* 177:344 */         ArrayList<Object> kSets = (ArrayList)this.m_Ls.get(this.m_Ls.size() - 1);
/* 178:345 */         this.m_Ls.remove(this.m_Ls.size() - 1);
/* 179:346 */         kSets = ItemSet.deleteItemSets(kSets, this.m_premiseCount, 2147483647);
/* 180:    */         
/* 181:348 */         this.m_Ls.add(kSets);
/* 182:    */       }
/* 183:    */     }
/* 184:353 */     this.m_allTheRules = new ArrayList[3];
/* 185:354 */     this.m_allTheRules[0] = new ArrayList();
/* 186:355 */     this.m_allTheRules[1] = new ArrayList();
/* 187:356 */     this.m_allTheRules[2] = new ArrayList();
/* 188:    */     
/* 189:358 */     int k = 0;
/* 190:359 */     while ((this.m_best.size() > 0) && (exactNumber > 0))
/* 191:    */     {
/* 192:360 */       this.m_allTheRules[0].add(k, ((RuleItem)this.m_best.last()).premise());
/* 193:361 */       this.m_allTheRules[1].add(k, ((RuleItem)this.m_best.last()).consequence());
/* 194:362 */       this.m_allTheRules[2].add(k, new Double(((RuleItem)this.m_best.last()).accuracy()));
/* 195:363 */       this.m_best.remove(this.m_best.last());
/* 196:364 */       k++;
/* 197:365 */       exactNumber--;
/* 198:    */     }
/* 199:    */   }
/* 200:    */   
/* 201:    */   public ArrayList<Object>[] mineCARs(Instances data)
/* 202:    */     throws Exception
/* 203:    */   {
/* 204:381 */     this.m_car = true;
/* 205:382 */     this.m_best = new TreeSet();
/* 206:383 */     this.m_premiseCount = 1;
/* 207:384 */     this.m_bestChanged = false;
/* 208:385 */     this.m_expectation = 0.0D;
/* 209:386 */     this.m_count = 1;
/* 210:387 */     buildAssociations(data);
/* 211:388 */     ArrayList<Object>[] allCARRules = new ArrayList[3];
/* 212:389 */     allCARRules[0] = new ArrayList();
/* 213:390 */     allCARRules[1] = new ArrayList();
/* 214:391 */     allCARRules[2] = new ArrayList();
/* 215:392 */     for (int k = 0; k < this.m_allTheRules[0].size(); k++)
/* 216:    */     {
/* 217:393 */       int[] newPremiseArray = new int[this.m_instances.numAttributes() - 1];
/* 218:394 */       int help = 0;
/* 219:395 */       for (int j = 0; j < this.m_instances.numAttributes(); j++) {
/* 220:396 */         if (j != this.m_instances.classIndex())
/* 221:    */         {
/* 222:397 */           newPremiseArray[help] = ((ItemSet)this.m_allTheRules[0].get(k)).itemAt(j);
/* 223:398 */           help++;
/* 224:    */         }
/* 225:    */       }
/* 226:401 */       ItemSet newPremise = new ItemSet(this.m_instances.numInstances(), newPremiseArray);
/* 227:    */       
/* 228:403 */       newPremise.setCounter(((ItemSet)this.m_allTheRules[0].get(k)).counter());
/* 229:404 */       allCARRules[0].add(newPremise);
/* 230:405 */       int[] newConsArray = new int[1];
/* 231:406 */       newConsArray[0] = ((ItemSet)this.m_allTheRules[1].get(k)).itemAt(this.m_instances.classIndex());
/* 232:    */       
/* 233:408 */       ItemSet newCons = new ItemSet(this.m_instances.numInstances(), newConsArray);
/* 234:409 */       newCons.setCounter(((ItemSet)this.m_allTheRules[1].get(k)).counter());
/* 235:410 */       allCARRules[1].add(newCons);
/* 236:411 */       allCARRules[2].add(this.m_allTheRules[2].get(k));
/* 237:    */     }
/* 238:414 */     return allCARRules;
/* 239:    */   }
/* 240:    */   
/* 241:    */   public Instances getInstancesNoClass()
/* 242:    */   {
/* 243:425 */     Instances noClass = null;
/* 244:    */     try
/* 245:    */     {
/* 246:427 */       noClass = LabeledItemSet.divide(this.m_instances, false);
/* 247:    */     }
/* 248:    */     catch (Exception e)
/* 249:    */     {
/* 250:429 */       e.printStackTrace();
/* 251:430 */       System.out.println("\n" + e.getMessage());
/* 252:    */     }
/* 253:433 */     return noClass;
/* 254:    */   }
/* 255:    */   
/* 256:    */   public Instances getInstancesOnlyClass()
/* 257:    */   {
/* 258:444 */     Instances onlyClass = null;
/* 259:    */     try
/* 260:    */     {
/* 261:446 */       onlyClass = LabeledItemSet.divide(this.m_instances, true);
/* 262:    */     }
/* 263:    */     catch (Exception e)
/* 264:    */     {
/* 265:448 */       e.printStackTrace();
/* 266:449 */       System.out.println("\n" + e.getMessage());
/* 267:    */     }
/* 268:451 */     return onlyClass;
/* 269:    */   }
/* 270:    */   
/* 271:    */   public Enumeration<Option> listOptions()
/* 272:    */   {
/* 273:463 */     String string1 = "\tThe required number of rules. (default = " + (this.m_numRules - 5) + ")";
/* 274:464 */     String string2 = "\tIf set class association rules are mined. (default = no)";String string3 = "\tThe class index. (default = last)";
/* 275:465 */     Vector<Option> newVector = new Vector(3);
/* 276:    */     
/* 277:467 */     newVector.addElement(new Option(string1, "N", 1, "-N <required number of rules output>"));
/* 278:    */     
/* 279:469 */     newVector.addElement(new Option(string2, "A", 0, "-A"));
/* 280:470 */     newVector.addElement(new Option(string3, "c", 1, "-c <the class index>"));
/* 281:471 */     return newVector.elements();
/* 282:    */   }
/* 283:    */   
/* 284:    */   public void setOptions(String[] options)
/* 285:    */     throws Exception
/* 286:    */   {
/* 287:504 */     resetOptions();
/* 288:    */     
/* 289:506 */     String numRulesString = Utils.getOption('N', options);
/* 290:507 */     if (numRulesString.length() != 0) {
/* 291:508 */       this.m_numRules = (Integer.parseInt(numRulesString) + 5);
/* 292:    */     } else {
/* 293:510 */       this.m_numRules = 2147483647;
/* 294:    */     }
/* 295:513 */     String classIndexString = Utils.getOption('c', options);
/* 296:514 */     if (classIndexString.length() != 0) {
/* 297:515 */       if (classIndexString.equals("first")) {
/* 298:516 */         this.m_classIndex = 1;
/* 299:517 */       } else if (classIndexString.equals("last")) {
/* 300:518 */         this.m_classIndex = -1;
/* 301:    */       } else {
/* 302:520 */         this.m_classIndex = Integer.parseInt(classIndexString);
/* 303:    */       }
/* 304:    */     }
/* 305:524 */     this.m_car = Utils.getFlag('A', options);
/* 306:    */   }
/* 307:    */   
/* 308:    */   public String[] getOptions()
/* 309:    */   {
/* 310:536 */     Vector<String> result = new Vector();
/* 311:    */     
/* 312:538 */     result.add("-N");
/* 313:539 */     result.add("" + (this.m_numRules - 5));
/* 314:541 */     if (this.m_car) {
/* 315:542 */       result.add("-A");
/* 316:    */     }
/* 317:545 */     result.add("-c");
/* 318:546 */     result.add("" + this.m_classIndex);
/* 319:    */     
/* 320:548 */     return (String[])result.toArray(new String[result.size()]);
/* 321:    */   }
/* 322:    */   
/* 323:    */   public String toString()
/* 324:    */   {
/* 325:559 */     StringBuffer text = new StringBuffer();
/* 326:561 */     if (this.m_allTheRules[0].size() == 0) {
/* 327:562 */       return "\nNo large itemsets and rules found!\n";
/* 328:    */     }
/* 329:564 */     text.append("\nPredictiveApriori\n===================\n\n");
/* 330:565 */     text.append("\nBest rules found:\n\n");
/* 331:567 */     for (int i = 0; i < this.m_allTheRules[0].size(); i++)
/* 332:    */     {
/* 333:568 */       text.append(Utils.doubleToString(i + 1.0D, (int)(Math.log(this.m_numRules) / Math.log(10.0D) + 1.0D), 0) + ". " + ((ItemSet)this.m_allTheRules[0].get(i)).toString(this.m_instances) + " ==> " + ((ItemSet)this.m_allTheRules[1].get(i)).toString(this.m_instances) + "    acc:(" + Utils.doubleToString(((Double)this.m_allTheRules[2].get(i)).doubleValue(), 5) + ")");
/* 334:    */       
/* 335:    */ 
/* 336:    */ 
/* 337:    */ 
/* 338:    */ 
/* 339:    */ 
/* 340:    */ 
/* 341:    */ 
/* 342:    */ 
/* 343:578 */       text.append('\n');
/* 344:    */     }
/* 345:581 */     return text.toString();
/* 346:    */   }
/* 347:    */   
/* 348:    */   public String numRulesTipText()
/* 349:    */   {
/* 350:591 */     return "Number of rules to find.";
/* 351:    */   }
/* 352:    */   
/* 353:    */   public int getNumRules()
/* 354:    */   {
/* 355:601 */     return this.m_numRules - 5;
/* 356:    */   }
/* 357:    */   
/* 358:    */   public void setNumRules(int v)
/* 359:    */   {
/* 360:611 */     this.m_numRules = (v + 5);
/* 361:    */   }
/* 362:    */   
/* 363:    */   public void setClassIndex(int index)
/* 364:    */   {
/* 365:622 */     this.m_classIndex = index;
/* 366:    */   }
/* 367:    */   
/* 368:    */   public int getClassIndex()
/* 369:    */   {
/* 370:632 */     return this.m_classIndex;
/* 371:    */   }
/* 372:    */   
/* 373:    */   public String classIndexTipText()
/* 374:    */   {
/* 375:642 */     return "Index of the class attribute.\n If set to -1, the last attribute will be taken as the class attribute.";
/* 376:    */   }
/* 377:    */   
/* 378:    */   public void setCar(boolean flag)
/* 379:    */   {
/* 380:652 */     this.m_car = flag;
/* 381:    */   }
/* 382:    */   
/* 383:    */   public boolean getCar()
/* 384:    */   {
/* 385:662 */     return this.m_car;
/* 386:    */   }
/* 387:    */   
/* 388:    */   public String carTipText()
/* 389:    */   {
/* 390:672 */     return "If enabled class association rules are mined instead of (general) association rules.";
/* 391:    */   }
/* 392:    */   
/* 393:    */   public String metricString()
/* 394:    */   {
/* 395:685 */     return "acc";
/* 396:    */   }
/* 397:    */   
/* 398:    */   private void findLargeItemSets(int index)
/* 399:    */     throws Exception
/* 400:    */   {
/* 401:696 */     ArrayList<Object> kSets = new ArrayList();
/* 402:    */     
/* 403:698 */     int i = 0;
/* 404:701 */     if (index == 1)
/* 405:    */     {
/* 406:702 */       kSets = ItemSet.singletons(this.m_instances);
/* 407:703 */       ItemSet.upDateCounters(kSets, this.m_instances);
/* 408:704 */       kSets = ItemSet.deleteItemSets(kSets, this.m_premiseCount, 2147483647);
/* 409:705 */       if (kSets.size() == 0) {
/* 410:706 */         return;
/* 411:    */       }
/* 412:708 */       this.m_Ls.add(kSets);
/* 413:    */     }
/* 414:711 */     if (index > 1)
/* 415:    */     {
/* 416:712 */       if (this.m_Ls.size() > 0) {
/* 417:713 */         kSets = (ArrayList)this.m_Ls.get(this.m_Ls.size() - 1);
/* 418:    */       }
/* 419:715 */       this.m_Ls.clear();
/* 420:716 */       i = index - 2;
/* 421:717 */       ArrayList<Object> kMinusOneSets = kSets;
/* 422:718 */       kSets = ItemSet.mergeAllItemSets(kMinusOneSets, i, this.m_instances.numInstances());
/* 423:    */       
/* 424:720 */       Hashtable<ItemSet, Integer> hashtable = ItemSet.getHashtable(kMinusOneSets, kMinusOneSets.size());
/* 425:721 */       this.m_hashtables.add(hashtable);
/* 426:722 */       kSets = ItemSet.pruneItemSets(kSets, hashtable);
/* 427:723 */       ItemSet.upDateCounters(kSets, this.m_instances);
/* 428:724 */       kSets = ItemSet.deleteItemSets(kSets, this.m_premiseCount, 2147483647);
/* 429:725 */       if (kSets.size() == 0) {
/* 430:726 */         return;
/* 431:    */       }
/* 432:728 */       this.m_Ls.add(kSets);
/* 433:    */     }
/* 434:    */   }
/* 435:    */   
/* 436:    */   private void findRulesQuickly()
/* 437:    */     throws Exception
/* 438:    */   {
/* 439:742 */     for (int j = 0; j < this.m_Ls.size(); j++)
/* 440:    */     {
/* 441:743 */       ArrayList<Object> currentItemSets = (ArrayList)this.m_Ls.get(j);
/* 442:744 */       Enumeration<Object> enumItemSets = new WekaEnumeration(currentItemSets);
/* 443:746 */       while (enumItemSets.hasMoreElements())
/* 444:    */       {
/* 445:747 */         RuleGeneration currentItemSet = new RuleGeneration((ItemSet)enumItemSets.nextElement());
/* 446:    */         
/* 447:749 */         this.m_best = currentItemSet.generateRules(this.m_numRules - 5, this.m_midPoints, this.m_priors, this.m_expectation, this.m_instances, this.m_best, this.m_count);
/* 448:    */         
/* 449:    */ 
/* 450:752 */         this.m_count = currentItemSet.m_count;
/* 451:753 */         if ((!this.m_bestChanged) && (currentItemSet.m_change)) {
/* 452:754 */           this.m_bestChanged = true;
/* 453:    */         }
/* 454:757 */         if (this.m_best.size() >= this.m_numRules - 5) {
/* 455:758 */           this.m_expectation = ((RuleItem)this.m_best.first()).accuracy();
/* 456:    */         } else {
/* 457:760 */           this.m_expectation = 0.0D;
/* 458:    */         }
/* 459:    */       }
/* 460:    */     }
/* 461:    */   }
/* 462:    */   
/* 463:    */   private void findLargeCarItemSets(int index)
/* 464:    */     throws Exception
/* 465:    */   {
/* 466:775 */     ArrayList<Object> kSets = new ArrayList();
/* 467:    */     
/* 468:777 */     int i = 0;
/* 469:779 */     if (index == 1)
/* 470:    */     {
/* 471:780 */       kSets = CaRuleGeneration.singletons(this.m_instances);
/* 472:781 */       ItemSet.upDateCounters(kSets, this.m_instances);
/* 473:782 */       kSets = ItemSet.deleteItemSets(kSets, this.m_premiseCount, 2147483647);
/* 474:783 */       if (kSets.size() == 0) {
/* 475:784 */         return;
/* 476:    */       }
/* 477:786 */       this.m_Ls.add(kSets);
/* 478:    */     }
/* 479:789 */     if (index > 1)
/* 480:    */     {
/* 481:790 */       if (this.m_Ls.size() > 0) {
/* 482:791 */         kSets = (ArrayList)this.m_Ls.get(this.m_Ls.size() - 1);
/* 483:    */       }
/* 484:793 */       this.m_Ls.clear();
/* 485:794 */       i = index - 2;
/* 486:795 */       ArrayList<Object> kMinusOneSets = kSets;
/* 487:796 */       kSets = ItemSet.mergeAllItemSets(kMinusOneSets, i, this.m_instances.numInstances());
/* 488:    */       
/* 489:798 */       Hashtable<ItemSet, Integer> hashtable = ItemSet.getHashtable(kMinusOneSets, kMinusOneSets.size());
/* 490:799 */       this.m_hashtables.add(hashtable);
/* 491:800 */       kSets = ItemSet.pruneItemSets(kSets, hashtable);
/* 492:801 */       ItemSet.upDateCounters(kSets, this.m_instances);
/* 493:802 */       kSets = ItemSet.deleteItemSets(kSets, this.m_premiseCount, 2147483647);
/* 494:803 */       if (kSets.size() == 0) {
/* 495:804 */         return;
/* 496:    */       }
/* 497:806 */       this.m_Ls.add(kSets);
/* 498:    */     }
/* 499:    */   }
/* 500:    */   
/* 501:    */   private void findCaRulesQuickly()
/* 502:    */     throws Exception
/* 503:    */   {
/* 504:819 */     for (int j = 0; j < this.m_Ls.size(); j++)
/* 505:    */     {
/* 506:820 */       ArrayList<Object> currentItemSets = (ArrayList)this.m_Ls.get(j);
/* 507:821 */       Enumeration<Object> enumItemSets = new WekaEnumeration(currentItemSets);
/* 508:823 */       while (enumItemSets.hasMoreElements())
/* 509:    */       {
/* 510:824 */         CaRuleGeneration currentLItemSet = new CaRuleGeneration((ItemSet)enumItemSets.nextElement());
/* 511:    */         
/* 512:826 */         this.m_best = currentLItemSet.generateRules(this.m_numRules - 5, this.m_midPoints, this.m_priors, this.m_expectation, this.m_instances, this.m_best, this.m_count);
/* 513:    */         
/* 514:828 */         this.m_count = currentLItemSet.count();
/* 515:829 */         if ((!this.m_bestChanged) && (currentLItemSet.change())) {
/* 516:830 */           this.m_bestChanged = true;
/* 517:    */         }
/* 518:832 */         if (this.m_best.size() == this.m_numRules - 5) {
/* 519:833 */           this.m_expectation = ((RuleItem)this.m_best.first()).accuracy();
/* 520:    */         } else {
/* 521:835 */           this.m_expectation = 0.0D;
/* 522:    */         }
/* 523:    */       }
/* 524:    */     }
/* 525:    */   }
/* 526:    */   
/* 527:    */   public ArrayList<Object>[] getAllTheRules()
/* 528:    */   {
/* 529:848 */     return this.m_allTheRules;
/* 530:    */   }
/* 531:    */   
/* 532:    */   public String getRevision()
/* 533:    */   {
/* 534:858 */     return RevisionUtils.extract("$Revision: 11047 $");
/* 535:    */   }
/* 536:    */   
/* 537:    */   public static void main(String[] args)
/* 538:    */   {
/* 539:867 */     runAssociator(new PredictiveApriori(), args);
/* 540:    */   }
/* 541:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.PredictiveApriori
 * JD-Core Version:    0.7.0.1
 */