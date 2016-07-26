/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Hashtable;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.ContingencyTables;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.RevisionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.WekaEnumeration;
/*  14:    */ 
/*  15:    */ public class AprioriItemSet
/*  16:    */   extends ItemSet
/*  17:    */   implements Serializable, RevisionHandler
/*  18:    */ {
/*  19:    */   static final long serialVersionUID = 7684467755712672058L;
/*  20:    */   
/*  21:    */   public AprioriItemSet(int totalTrans)
/*  22:    */   {
/*  23: 58 */     super(totalTrans);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public static double confidenceForRule(AprioriItemSet premise, AprioriItemSet consequence)
/*  27:    */   {
/*  28: 71 */     return consequence.m_counter / premise.m_counter;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public double liftForRule(AprioriItemSet premise, AprioriItemSet consequence, int consequenceCount)
/*  32:    */   {
/*  33: 86 */     double confidence = confidenceForRule(premise, consequence);
/*  34:    */     
/*  35: 88 */     return confidence / (consequenceCount / this.m_totalTransactions);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public double leverageForRule(AprioriItemSet premise, AprioriItemSet consequence, int premiseCount, int consequenceCount)
/*  39:    */   {
/*  40:106 */     double coverageForItemSet = consequence.m_counter / this.m_totalTransactions;
/*  41:    */     
/*  42:108 */     double expectedCoverageIfIndependent = premiseCount / this.m_totalTransactions * (consequenceCount / this.m_totalTransactions);
/*  43:    */     
/*  44:110 */     double lev = coverageForItemSet - expectedCoverageIfIndependent;
/*  45:111 */     return lev;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public double convictionForRule(AprioriItemSet premise, AprioriItemSet consequence, int premiseCount, int consequenceCount)
/*  49:    */   {
/*  50:128 */     double num = premiseCount * (this.m_totalTransactions - consequenceCount) / this.m_totalTransactions;
/*  51:    */     
/*  52:130 */     double denom = premiseCount - consequence.m_counter + 1;
/*  53:132 */     if ((num < 0.0D) || (denom < 0.0D))
/*  54:    */     {
/*  55:133 */       System.err.println("*** " + num + " " + denom);
/*  56:134 */       System.err.println("premis count: " + premiseCount + " consequence count " + consequenceCount + " total trans " + this.m_totalTransactions);
/*  57:    */     }
/*  58:138 */     return num / denom;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public ArrayList<Object>[] generateRules(double minConfidence, ArrayList<Hashtable<ItemSet, Integer>> hashtables, int numItemsInSet)
/*  62:    */   {
/*  63:153 */     ArrayList<Object> premises = new ArrayList();ArrayList<Object> consequences = new ArrayList();ArrayList<Object> conf = new ArrayList();
/*  64:    */     
/*  65:155 */     ArrayList<Object> lift = new ArrayList();ArrayList<Object> lev = new ArrayList();ArrayList<Object> conv = new ArrayList();
/*  66:    */     
/*  67:    */ 
/*  68:158 */     ArrayList<Object>[] rules = new ArrayList[6];
/*  69:    */     
/*  70:160 */     Hashtable<ItemSet, Integer> hashtable = (Hashtable)hashtables.get(numItemsInSet - 2);
/*  71:163 */     for (int i = 0; i < this.m_items.length; i++) {
/*  72:164 */       if (this.m_items[i] != -1)
/*  73:    */       {
/*  74:165 */         AprioriItemSet premise = new AprioriItemSet(this.m_totalTransactions);
/*  75:166 */         AprioriItemSet consequence = new AprioriItemSet(this.m_totalTransactions);
/*  76:167 */         premise.m_items = new int[this.m_items.length];
/*  77:168 */         consequence.m_items = new int[this.m_items.length];
/*  78:169 */         consequence.m_counter = this.m_counter;
/*  79:171 */         for (int j = 0; j < this.m_items.length; j++) {
/*  80:172 */           consequence.m_items[j] = -1;
/*  81:    */         }
/*  82:174 */         System.arraycopy(this.m_items, 0, premise.m_items, 0, this.m_items.length);
/*  83:175 */         premise.m_items[i] = -1;
/*  84:    */         
/*  85:177 */         consequence.m_items[i] = this.m_items[i];
/*  86:178 */         premise.m_counter = ((Integer)hashtable.get(premise)).intValue();
/*  87:    */         
/*  88:180 */         Hashtable<ItemSet, Integer> hashtableForConsequence = (Hashtable)hashtables.get(0);
/*  89:181 */         int consequenceUnconditionedCounter = ((Integer)hashtableForConsequence.get(consequence)).intValue();
/*  90:    */         
/*  91:183 */         consequence.m_secondaryCounter = consequenceUnconditionedCounter;
/*  92:    */         
/*  93:185 */         premises.add(premise);
/*  94:186 */         consequences.add(consequence);
/*  95:187 */         conf.add(new Double(confidenceForRule(premise, consequence)));
/*  96:    */         
/*  97:189 */         double tempLift = liftForRule(premise, consequence, consequenceUnconditionedCounter);
/*  98:    */         
/*  99:191 */         double tempLev = leverageForRule(premise, consequence, premise.m_counter, consequenceUnconditionedCounter);
/* 100:    */         
/* 101:193 */         double tempConv = convictionForRule(premise, consequence, premise.m_counter, consequenceUnconditionedCounter);
/* 102:    */         
/* 103:195 */         lift.add(new Double(tempLift));
/* 104:196 */         lev.add(new Double(tempLev));
/* 105:197 */         conv.add(new Double(tempConv));
/* 106:    */       }
/* 107:    */     }
/* 108:200 */     rules[0] = premises;
/* 109:201 */     rules[1] = consequences;
/* 110:202 */     rules[2] = conf;
/* 111:    */     
/* 112:204 */     rules[3] = lift;
/* 113:205 */     rules[4] = lev;
/* 114:206 */     rules[5] = conv;
/* 115:    */     
/* 116:208 */     pruneRules(rules, minConfidence);
/* 117:    */     
/* 118:    */ 
/* 119:211 */     ArrayList<Object>[] moreResults = moreComplexRules(rules, numItemsInSet, 1, minConfidence, hashtables);
/* 120:213 */     if (moreResults != null) {
/* 121:214 */       for (int i = 0; i < moreResults[0].size(); i++)
/* 122:    */       {
/* 123:215 */         rules[0].add(moreResults[0].get(i));
/* 124:216 */         rules[1].add(moreResults[1].get(i));
/* 125:217 */         rules[2].add(moreResults[2].get(i));
/* 126:    */         
/* 127:    */ 
/* 128:220 */         rules[3].add(moreResults[3].get(i));
/* 129:221 */         rules[4].add(moreResults[4].get(i));
/* 130:222 */         rules[5].add(moreResults[5].get(i));
/* 131:    */       }
/* 132:    */     }
/* 133:225 */     return rules;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public final ArrayList<Object>[] generateRulesBruteForce(double minMetric, int metricType, ArrayList<Hashtable<ItemSet, Integer>> hashtables, int numItemsInSet, int numTransactions, double significanceLevel)
/* 137:    */     throws Exception
/* 138:    */   {
/* 139:247 */     ArrayList<Object> premises = new ArrayList();ArrayList<Object> consequences = new ArrayList();ArrayList<Object> conf = new ArrayList();ArrayList<Object> lift = new ArrayList();ArrayList<Object> lev = new ArrayList();ArrayList<Object> conv = new ArrayList();
/* 140:    */     
/* 141:249 */     ArrayList<Object>[] rules = new ArrayList[6];
/* 142:    */     
/* 143:    */ 
/* 144:    */ 
/* 145:253 */     double[][] contingencyTable = new double[2][2];
/* 146:254 */     double chiSquared = 0.0D;
/* 147:    */     
/* 148:    */ 
/* 149:    */ 
/* 150:258 */     int max = (int)Math.pow(2.0D, numItemsInSet);
/* 151:259 */     for (int j = 1; j < max; j++)
/* 152:    */     {
/* 153:260 */       int numItemsInPremise = 0;
/* 154:261 */       int help = j;
/* 155:262 */       while (help > 0)
/* 156:    */       {
/* 157:263 */         if (help % 2 == 1) {
/* 158:264 */           numItemsInPremise++;
/* 159:    */         }
/* 160:266 */         help /= 2;
/* 161:    */       }
/* 162:268 */       if (numItemsInPremise < numItemsInSet)
/* 163:    */       {
/* 164:269 */         Hashtable<ItemSet, Integer> hashtableForPremise = (Hashtable)hashtables.get(numItemsInPremise - 1);
/* 165:270 */         Hashtable<ItemSet, Integer> hashtableForConsequence = (Hashtable)hashtables.get(numItemsInSet - numItemsInPremise - 1);
/* 166:    */         
/* 167:272 */         AprioriItemSet premise = new AprioriItemSet(this.m_totalTransactions);
/* 168:273 */         AprioriItemSet consequence = new AprioriItemSet(this.m_totalTransactions);
/* 169:274 */         premise.m_items = new int[this.m_items.length];
/* 170:    */         
/* 171:276 */         consequence.m_items = new int[this.m_items.length];
/* 172:277 */         consequence.m_counter = this.m_counter;
/* 173:278 */         help = j;
/* 174:279 */         for (int i = 0; i < this.m_items.length; i++) {
/* 175:280 */           if (this.m_items[i] != -1)
/* 176:    */           {
/* 177:281 */             if (help % 2 == 1)
/* 178:    */             {
/* 179:282 */               premise.m_items[i] = this.m_items[i];
/* 180:283 */               consequence.m_items[i] = -1;
/* 181:    */             }
/* 182:    */             else
/* 183:    */             {
/* 184:285 */               premise.m_items[i] = -1;
/* 185:286 */               consequence.m_items[i] = this.m_items[i];
/* 186:    */             }
/* 187:288 */             help /= 2;
/* 188:    */           }
/* 189:    */           else
/* 190:    */           {
/* 191:290 */             premise.m_items[i] = -1;
/* 192:291 */             consequence.m_items[i] = -1;
/* 193:    */           }
/* 194:    */         }
/* 195:294 */         premise.m_counter = ((Integer)hashtableForPremise.get(premise)).intValue();
/* 196:295 */         int consequenceUnconditionedCounter = ((Integer)hashtableForConsequence.get(consequence)).intValue();
/* 197:    */         
/* 198:297 */         consequence.m_secondaryCounter = consequenceUnconditionedCounter;
/* 199:299 */         if (significanceLevel != -1.0D)
/* 200:    */         {
/* 201:300 */           contingencyTable[0][0] = consequence.m_counter;
/* 202:301 */           contingencyTable[0][1] = (premise.m_counter - consequence.m_counter);
/* 203:302 */           contingencyTable[1][0] = (consequenceUnconditionedCounter - consequence.m_counter);
/* 204:303 */           contingencyTable[1][1] = (numTransactions - premise.m_counter - consequenceUnconditionedCounter + consequence.m_counter);
/* 205:    */           
/* 206:305 */           chiSquared = ContingencyTables.chiSquared(contingencyTable, false);
/* 207:    */         }
/* 208:308 */         if (metricType == 0)
/* 209:    */         {
/* 210:310 */           double metric = confidenceForRule(premise, consequence);
/* 211:312 */           if ((metric >= minMetric) && ((significanceLevel == -1.0D) || (chiSquared <= significanceLevel)))
/* 212:    */           {
/* 213:314 */             premises.add(premise);
/* 214:315 */             consequences.add(consequence);
/* 215:316 */             conf.add(new Double(metric));
/* 216:317 */             lift.add(new Double(liftForRule(premise, consequence, consequenceUnconditionedCounter)));
/* 217:    */             
/* 218:319 */             lev.add(new Double(leverageForRule(premise, consequence, premise.m_counter, consequenceUnconditionedCounter)));
/* 219:    */             
/* 220:321 */             conv.add(new Double(convictionForRule(premise, consequence, premise.m_counter, consequenceUnconditionedCounter)));
/* 221:    */           }
/* 222:    */         }
/* 223:    */         else
/* 224:    */         {
/* 225:325 */           double tempConf = confidenceForRule(premise, consequence);
/* 226:326 */           double tempLift = liftForRule(premise, consequence, consequenceUnconditionedCounter);
/* 227:    */           
/* 228:328 */           double tempLev = leverageForRule(premise, consequence, premise.m_counter, consequenceUnconditionedCounter);
/* 229:    */           
/* 230:330 */           double tempConv = convictionForRule(premise, consequence, premise.m_counter, consequenceUnconditionedCounter);
/* 231:    */           double metric;
/* 232:332 */           switch (metricType)
/* 233:    */           {
/* 234:    */           case 1: 
/* 235:334 */             metric = tempLift;
/* 236:335 */             break;
/* 237:    */           case 2: 
/* 238:337 */             metric = tempLev;
/* 239:338 */             break;
/* 240:    */           case 3: 
/* 241:340 */             metric = tempConv;
/* 242:341 */             break;
/* 243:    */           default: 
/* 244:343 */             throw new Exception("ItemSet: Unknown metric type!");
/* 245:    */           }
/* 246:345 */           if ((metric >= minMetric) && ((significanceLevel == -1.0D) || (chiSquared <= significanceLevel)))
/* 247:    */           {
/* 248:347 */             premises.add(premise);
/* 249:348 */             consequences.add(consequence);
/* 250:349 */             conf.add(new Double(tempConf));
/* 251:350 */             lift.add(new Double(tempLift));
/* 252:351 */             lev.add(new Double(tempLev));
/* 253:352 */             conv.add(new Double(tempConv));
/* 254:    */           }
/* 255:    */         }
/* 256:    */       }
/* 257:    */     }
/* 258:357 */     rules[0] = premises;
/* 259:358 */     rules[1] = consequences;
/* 260:359 */     rules[2] = conf;
/* 261:360 */     rules[3] = lift;
/* 262:361 */     rules[4] = lev;
/* 263:362 */     rules[5] = conv;
/* 264:363 */     return rules;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public final AprioriItemSet subtract(AprioriItemSet toSubtract)
/* 268:    */   {
/* 269:375 */     AprioriItemSet result = new AprioriItemSet(this.m_totalTransactions);
/* 270:    */     
/* 271:377 */     result.m_items = new int[this.m_items.length];
/* 272:379 */     for (int i = 0; i < this.m_items.length; i++) {
/* 273:380 */       if (toSubtract.m_items[i] == -1) {
/* 274:381 */         result.m_items[i] = this.m_items[i];
/* 275:    */       } else {
/* 276:383 */         result.m_items[i] = -1;
/* 277:    */       }
/* 278:    */     }
/* 279:386 */     result.m_counter = 0;
/* 280:387 */     return result;
/* 281:    */   }
/* 282:    */   
/* 283:    */   private final ArrayList<Object>[] moreComplexRules(ArrayList<Object>[] rules, int numItemsInSet, int numItemsInConsequence, double minConfidence, ArrayList<Hashtable<ItemSet, Integer>> hashtables)
/* 284:    */   {
/* 285:409 */     ArrayList<Object> newPremises = new ArrayList();ArrayList<Object> newConf = new ArrayList();
/* 286:    */     
/* 287:    */ 
/* 288:412 */     ArrayList<Object> newLift = null;ArrayList<Object> newLev = null;ArrayList<Object> newConv = null;
/* 289:    */     
/* 290:414 */     newLift = new ArrayList();
/* 291:415 */     newLev = new ArrayList();
/* 292:416 */     newConv = new ArrayList();
/* 293:419 */     if (numItemsInSet > numItemsInConsequence + 1)
/* 294:    */     {
/* 295:420 */       Hashtable<ItemSet, Integer> hashtable = (Hashtable)hashtables.get(numItemsInSet - numItemsInConsequence - 2);
/* 296:421 */       ArrayList<Object> newConsequences = mergeAllItemSets(rules[1], numItemsInConsequence - 1, this.m_totalTransactions);
/* 297:    */       
/* 298:423 */       int newNumInConsequence = numItemsInConsequence + 1;
/* 299:    */       
/* 300:425 */       Hashtable<ItemSet, Integer> hashtableForConsequence = (Hashtable)hashtables.get(newNumInConsequence - 1);
/* 301:    */       
/* 302:    */ 
/* 303:428 */       Enumeration<Object> enu = new WekaEnumeration(newConsequences);
/* 304:429 */       while (enu.hasMoreElements())
/* 305:    */       {
/* 306:430 */         AprioriItemSet current = (AprioriItemSet)enu.nextElement();
/* 307:431 */         for (int m_item : current.m_items) {
/* 308:432 */           if (m_item == -1) {}
/* 309:    */         }
/* 310:436 */         current.m_counter = this.m_counter;
/* 311:437 */         AprioriItemSet newPremise = subtract(current);
/* 312:438 */         newPremise.m_counter = ((Integer)hashtable.get(newPremise)).intValue();
/* 313:439 */         newPremises.add(newPremise);
/* 314:440 */         newConf.add(new Double(confidenceForRule(newPremise, current)));
/* 315:    */         
/* 316:    */ 
/* 317:443 */         int consequenceUnconditionedCounter = ((Integer)hashtableForConsequence.get(current)).intValue();
/* 318:    */         
/* 319:445 */         current.m_secondaryCounter = consequenceUnconditionedCounter;
/* 320:    */         
/* 321:447 */         double tempLift = liftForRule(newPremise, current, consequenceUnconditionedCounter);
/* 322:    */         
/* 323:449 */         double tempLev = leverageForRule(newPremise, current, newPremise.m_counter, consequenceUnconditionedCounter);
/* 324:    */         
/* 325:451 */         double tempConv = convictionForRule(newPremise, current, newPremise.m_counter, consequenceUnconditionedCounter);
/* 326:    */         
/* 327:    */ 
/* 328:454 */         newLift.add(new Double(tempLift));
/* 329:455 */         newLev.add(new Double(tempLev));
/* 330:456 */         newConv.add(new Double(tempConv));
/* 331:    */       }
/* 332:459 */       ArrayList<Object>[] result = new ArrayList[rules.length];
/* 333:460 */       result[0] = newPremises;
/* 334:461 */       result[1] = newConsequences;
/* 335:462 */       result[2] = newConf;
/* 336:    */       
/* 337:    */ 
/* 338:465 */       result[3] = newLift;
/* 339:466 */       result[4] = newLev;
/* 340:467 */       result[5] = newConv;
/* 341:    */       
/* 342:469 */       pruneRules(result, minConfidence);
/* 343:470 */       ArrayList<Object>[] moreResults = moreComplexRules(result, numItemsInSet, numItemsInConsequence + 1, minConfidence, hashtables);
/* 344:472 */       if (moreResults != null) {
/* 345:473 */         for (int i = 0; i < moreResults[0].size(); i++)
/* 346:    */         {
/* 347:474 */           result[0].add(moreResults[0].get(i));
/* 348:475 */           result[1].add(moreResults[1].get(i));
/* 349:476 */           result[2].add(moreResults[2].get(i));
/* 350:    */           
/* 351:478 */           result[3].add(moreResults[3].get(i));
/* 352:479 */           result[4].add(moreResults[4].get(i));
/* 353:480 */           result[5].add(moreResults[5].get(i));
/* 354:    */         }
/* 355:    */       }
/* 356:483 */       return result;
/* 357:    */     }
/* 358:485 */     return null;
/* 359:    */   }
/* 360:    */   
/* 361:    */   public final String toString(Instances instances)
/* 362:    */   {
/* 363:498 */     return super.toString(instances);
/* 364:    */   }
/* 365:    */   
/* 366:    */   public static ArrayList<Object> singletons(Instances instances, boolean treatZeroAsMissing)
/* 367:    */     throws Exception
/* 368:    */   {
/* 369:513 */     ArrayList<Object> setOfItemSets = new ArrayList();
/* 370:516 */     for (int i = 0; i < instances.numAttributes(); i++)
/* 371:    */     {
/* 372:517 */       if (instances.attribute(i).isNumeric()) {
/* 373:518 */         throw new Exception("Can't handle numeric attributes!");
/* 374:    */       }
/* 375:520 */       for (int j = treatZeroAsMissing ? 1 : 0; j < instances.attribute(i).numValues(); j++)
/* 376:    */       {
/* 377:522 */         AprioriItemSet current = new AprioriItemSet(instances.numInstances());
/* 378:523 */         current.m_items = new int[instances.numAttributes()];
/* 379:524 */         for (int k = 0; k < instances.numAttributes(); k++) {
/* 380:525 */           current.m_items[k] = -1;
/* 381:    */         }
/* 382:527 */         current.m_items[i] = j;
/* 383:528 */         setOfItemSets.add(current);
/* 384:    */       }
/* 385:    */     }
/* 386:531 */     return setOfItemSets;
/* 387:    */   }
/* 388:    */   
/* 389:    */   public static ArrayList<Object> mergeAllItemSets(ArrayList<Object> itemSets, int size, int totalTrans)
/* 390:    */   {
/* 391:546 */     ArrayList<Object> newVector = new ArrayList();
/* 392:    */     label269:
/* 393:550 */     for (int i = 0; i < itemSets.size(); i++)
/* 394:    */     {
/* 395:551 */       ItemSet first = (ItemSet)itemSets.get(i);
/* 396:552 */       for (int j = i + 1; j < itemSets.size(); j++)
/* 397:    */       {
/* 398:553 */         ItemSet second = (ItemSet)itemSets.get(j);
/* 399:554 */         AprioriItemSet result = new AprioriItemSet(totalTrans);
/* 400:555 */         result.m_items = new int[first.m_items.length];
/* 401:    */         
/* 402:    */ 
/* 403:558 */         int numFound = 0;
/* 404:559 */         int k = 0;
/* 405:560 */         while (numFound < size)
/* 406:    */         {
/* 407:561 */           if (first.m_items[k] != second.m_items[k]) {
/* 408:    */             break label269;
/* 409:    */           }
/* 410:562 */           if (first.m_items[k] != -1) {
/* 411:563 */             numFound++;
/* 412:    */           }
/* 413:565 */           result.m_items[k] = first.m_items[k];
/* 414:    */           
/* 415:    */ 
/* 416:    */ 
/* 417:569 */           k++;
/* 418:    */         }
/* 419:573 */         while ((k < first.m_items.length) && (
/* 420:574 */           (first.m_items[k] == -1) || (second.m_items[k] == -1)))
/* 421:    */         {
/* 422:577 */           if (first.m_items[k] != -1) {
/* 423:578 */             result.m_items[k] = first.m_items[k];
/* 424:    */           } else {
/* 425:580 */             result.m_items[k] = second.m_items[k];
/* 426:    */           }
/* 427:583 */           k++;
/* 428:    */         }
/* 429:585 */         if (k == first.m_items.length)
/* 430:    */         {
/* 431:586 */           result.m_counter = 0;
/* 432:587 */           newVector.add(result);
/* 433:    */         }
/* 434:    */       }
/* 435:    */     }
/* 436:591 */     return newVector;
/* 437:    */   }
/* 438:    */   
/* 439:    */   public String getRevision()
/* 440:    */   {
/* 441:601 */     return RevisionUtils.extract("$Revision: 12014 $");
/* 442:    */   }
/* 443:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.AprioriItemSet
 * JD-Core Version:    0.7.0.1
 */