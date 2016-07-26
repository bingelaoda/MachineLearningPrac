/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionHandler;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public class Distribution
/*  12:    */   implements Cloneable, Serializable, RevisionHandler
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 8526859638230806576L;
/*  15:    */   protected final double[][] m_perClassPerBag;
/*  16:    */   protected final double[] m_perBag;
/*  17:    */   protected final double[] m_perClass;
/*  18:    */   protected double totaL;
/*  19:    */   
/*  20:    */   public Distribution(int numBags, int numClasses)
/*  21:    */   {
/*  22: 63 */     this.m_perClassPerBag = new double[numBags][0];
/*  23: 64 */     this.m_perBag = new double[numBags];
/*  24: 65 */     this.m_perClass = new double[numClasses];
/*  25: 66 */     for (int i = 0; i < numBags; i++) {
/*  26: 67 */       this.m_perClassPerBag[i] = new double[numClasses];
/*  27:    */     }
/*  28: 69 */     this.totaL = 0.0D;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Distribution(double[][] table)
/*  32:    */   {
/*  33: 80 */     this.m_perClassPerBag = table;
/*  34: 81 */     this.m_perBag = new double[table.length];
/*  35: 82 */     this.m_perClass = new double[table[0].length];
/*  36: 83 */     for (int i = 0; i < table.length; i++) {
/*  37: 84 */       for (int j = 0; j < table[i].length; j++)
/*  38:    */       {
/*  39: 85 */         this.m_perBag[i] += table[i][j];
/*  40: 86 */         this.m_perClass[j] += table[i][j];
/*  41: 87 */         this.totaL += table[i][j];
/*  42:    */       }
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Distribution(Instances source)
/*  47:    */     throws Exception
/*  48:    */   {
/*  49: 99 */     this.m_perClassPerBag = new double[1][0];
/*  50:100 */     this.m_perBag = new double[1];
/*  51:101 */     this.totaL = 0.0D;
/*  52:102 */     this.m_perClass = new double[source.numClasses()];
/*  53:103 */     this.m_perClassPerBag[0] = new double[source.numClasses()];
/*  54:104 */     Enumeration<Instance> enu = source.enumerateInstances();
/*  55:105 */     while (enu.hasMoreElements()) {
/*  56:106 */       add(0, (Instance)enu.nextElement());
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   public Distribution(Instances source, ClassifierSplitModel modelToUse)
/*  61:    */     throws Exception
/*  62:    */   {
/*  63:122 */     this.m_perClassPerBag = new double[modelToUse.numSubsets()][0];
/*  64:123 */     this.m_perBag = new double[modelToUse.numSubsets()];
/*  65:124 */     this.totaL = 0.0D;
/*  66:125 */     this.m_perClass = new double[source.numClasses()];
/*  67:126 */     for (int i = 0; i < modelToUse.numSubsets(); i++) {
/*  68:127 */       this.m_perClassPerBag[i] = new double[source.numClasses()];
/*  69:    */     }
/*  70:129 */     Enumeration<Instance> enu = source.enumerateInstances();
/*  71:130 */     while (enu.hasMoreElements())
/*  72:    */     {
/*  73:131 */       Instance instance = (Instance)enu.nextElement();
/*  74:132 */       int index = modelToUse.whichSubset(instance);
/*  75:133 */       if (index != -1)
/*  76:    */       {
/*  77:134 */         add(index, instance);
/*  78:    */       }
/*  79:    */       else
/*  80:    */       {
/*  81:136 */         double[] weights = modelToUse.weights(instance);
/*  82:137 */         addWeights(instance, weights);
/*  83:    */       }
/*  84:    */     }
/*  85:    */   }
/*  86:    */   
/*  87:    */   public Distribution(Distribution toMerge)
/*  88:    */   {
/*  89:148 */     this.totaL = toMerge.totaL;
/*  90:149 */     this.m_perClass = new double[toMerge.numClasses()];
/*  91:150 */     System.arraycopy(toMerge.m_perClass, 0, this.m_perClass, 0, toMerge.numClasses());
/*  92:    */     
/*  93:152 */     this.m_perClassPerBag = new double[1][0];
/*  94:153 */     this.m_perClassPerBag[0] = new double[toMerge.numClasses()];
/*  95:154 */     System.arraycopy(toMerge.m_perClass, 0, this.m_perClassPerBag[0], 0, toMerge.numClasses());
/*  96:    */     
/*  97:156 */     this.m_perBag = new double[1];
/*  98:157 */     this.m_perBag[0] = this.totaL;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public Distribution(Distribution toMerge, int index)
/* 102:    */   {
/* 103:168 */     this.totaL = toMerge.totaL;
/* 104:169 */     this.m_perClass = new double[toMerge.numClasses()];
/* 105:170 */     System.arraycopy(toMerge.m_perClass, 0, this.m_perClass, 0, toMerge.numClasses());
/* 106:    */     
/* 107:172 */     this.m_perClassPerBag = new double[2][0];
/* 108:173 */     this.m_perClassPerBag[0] = new double[toMerge.numClasses()];
/* 109:174 */     System.arraycopy(toMerge.m_perClassPerBag[index], 0, this.m_perClassPerBag[0], 0, toMerge.numClasses());
/* 110:    */     
/* 111:176 */     this.m_perClassPerBag[1] = new double[toMerge.numClasses()];
/* 112:177 */     for (int i = 0; i < toMerge.numClasses(); i++) {
/* 113:178 */       this.m_perClassPerBag[1][i] = (toMerge.m_perClass[i] - this.m_perClassPerBag[0][i]);
/* 114:    */     }
/* 115:180 */     this.m_perBag = new double[2];
/* 116:181 */     this.m_perBag[0] = toMerge.m_perBag[index];
/* 117:182 */     this.m_perBag[1] = (this.totaL - this.m_perBag[0]);
/* 118:    */   }
/* 119:    */   
/* 120:    */   public final int actualNumBags()
/* 121:    */   {
/* 122:190 */     int returnValue = 0;
/* 123:193 */     for (int i = 0; i < this.m_perBag.length; i++) {
/* 124:194 */       if (Utils.gr(this.m_perBag[i], 0.0D)) {
/* 125:195 */         returnValue++;
/* 126:    */       }
/* 127:    */     }
/* 128:199 */     return returnValue;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public final int actualNumClasses()
/* 132:    */   {
/* 133:207 */     int returnValue = 0;
/* 134:210 */     for (int i = 0; i < this.m_perClass.length; i++) {
/* 135:211 */       if (Utils.gr(this.m_perClass[i], 0.0D)) {
/* 136:212 */         returnValue++;
/* 137:    */       }
/* 138:    */     }
/* 139:216 */     return returnValue;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public final int actualNumClasses(int bagIndex)
/* 143:    */   {
/* 144:224 */     int returnValue = 0;
/* 145:227 */     for (int i = 0; i < this.m_perClass.length; i++) {
/* 146:228 */       if (Utils.gr(this.m_perClassPerBag[bagIndex][i], 0.0D)) {
/* 147:229 */         returnValue++;
/* 148:    */       }
/* 149:    */     }
/* 150:233 */     return returnValue;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public final void add(int bagIndex, Instance instance)
/* 154:    */     throws Exception
/* 155:    */   {
/* 156:246 */     int classIndex = (int)instance.classValue();
/* 157:247 */     double weight = instance.weight();
/* 158:248 */     this.m_perClassPerBag[bagIndex][classIndex] += weight;
/* 159:    */     
/* 160:250 */     this.m_perBag[bagIndex] += weight;
/* 161:251 */     this.m_perClass[classIndex] += weight;
/* 162:252 */     this.totaL += weight;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public final void sub(int bagIndex, Instance instance)
/* 166:    */     throws Exception
/* 167:    */   {
/* 168:265 */     int classIndex = (int)instance.classValue();
/* 169:266 */     double weight = instance.weight();
/* 170:267 */     this.m_perClassPerBag[bagIndex][classIndex] -= weight;
/* 171:    */     
/* 172:269 */     this.m_perBag[bagIndex] -= weight;
/* 173:270 */     this.m_perClass[classIndex] -= weight;
/* 174:271 */     this.totaL -= weight;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public final void add(int bagIndex, double[] counts)
/* 178:    */   {
/* 179:279 */     double sum = Utils.sum(counts);
/* 180:281 */     for (int i = 0; i < counts.length; i++) {
/* 181:282 */       this.m_perClassPerBag[bagIndex][i] += counts[i];
/* 182:    */     }
/* 183:284 */     this.m_perBag[bagIndex] += sum;
/* 184:285 */     for (int i = 0; i < counts.length; i++) {
/* 185:286 */       this.m_perClass[i] += counts[i];
/* 186:    */     }
/* 187:288 */     this.totaL += sum;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public final void addInstWithUnknown(Instances source, int attIndex)
/* 191:    */     throws Exception
/* 192:    */   {
/* 193:306 */     double[] probs = new double[this.m_perBag.length];
/* 194:307 */     for (int j = 0; j < this.m_perBag.length; j++) {
/* 195:308 */       if (Utils.eq(this.totaL, 0.0D)) {
/* 196:309 */         probs[j] = (1.0D / probs.length);
/* 197:    */       } else {
/* 198:311 */         probs[j] = (this.m_perBag[j] / this.totaL);
/* 199:    */       }
/* 200:    */     }
/* 201:314 */     Enumeration<Instance> enu = source.enumerateInstances();
/* 202:315 */     while (enu.hasMoreElements())
/* 203:    */     {
/* 204:316 */       Instance instance = (Instance)enu.nextElement();
/* 205:317 */       if (instance.isMissing(attIndex))
/* 206:    */       {
/* 207:318 */         int classIndex = (int)instance.classValue();
/* 208:319 */         double weight = instance.weight();
/* 209:320 */         this.m_perClass[classIndex] += weight;
/* 210:321 */         this.totaL += weight;
/* 211:322 */         for (j = 0; j < this.m_perBag.length; j++)
/* 212:    */         {
/* 213:323 */           double newWeight = probs[j] * weight;
/* 214:324 */           this.m_perClassPerBag[j][classIndex] += newWeight;
/* 215:    */           
/* 216:326 */           this.m_perBag[j] += newWeight;
/* 217:    */         }
/* 218:    */       }
/* 219:    */     }
/* 220:    */   }
/* 221:    */   
/* 222:    */   public final void addRange(int bagIndex, Instances source, int startIndex, int lastPlusOne)
/* 223:    */     throws Exception
/* 224:    */   {
/* 225:340 */     double sumOfWeights = 0.0D;
/* 226:345 */     for (int i = startIndex; i < lastPlusOne; i++)
/* 227:    */     {
/* 228:346 */       Instance instance = source.instance(i);
/* 229:347 */       int classIndex = (int)instance.classValue();
/* 230:348 */       sumOfWeights += instance.weight();
/* 231:349 */       this.m_perClassPerBag[bagIndex][classIndex] += instance.weight();
/* 232:350 */       this.m_perClass[classIndex] += instance.weight();
/* 233:    */     }
/* 234:352 */     this.m_perBag[bagIndex] += sumOfWeights;
/* 235:353 */     this.totaL += sumOfWeights;
/* 236:    */   }
/* 237:    */   
/* 238:    */   public final void addWeights(Instance instance, double[] weights)
/* 239:    */     throws Exception
/* 240:    */   {
/* 241:367 */     int classIndex = (int)instance.classValue();
/* 242:368 */     for (int i = 0; i < this.m_perBag.length; i++)
/* 243:    */     {
/* 244:369 */       double weight = instance.weight() * weights[i];
/* 245:370 */       this.m_perClassPerBag[i][classIndex] += weight;
/* 246:    */       
/* 247:372 */       this.m_perBag[i] += weight;
/* 248:373 */       this.m_perClass[classIndex] += weight;
/* 249:374 */       this.totaL += weight;
/* 250:    */     }
/* 251:    */   }
/* 252:    */   
/* 253:    */   public final boolean check(double minNoObj)
/* 254:    */   {
/* 255:383 */     int counter = 0;
/* 256:386 */     for (int i = 0; i < this.m_perBag.length; i++) {
/* 257:387 */       if (Utils.grOrEq(this.m_perBag[i], minNoObj)) {
/* 258:388 */         counter++;
/* 259:    */       }
/* 260:    */     }
/* 261:391 */     if (counter > 1) {
/* 262:392 */       return true;
/* 263:    */     }
/* 264:394 */     return false;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public final Object clone()
/* 268:    */   {
/* 269:406 */     Distribution newDistribution = new Distribution(this.m_perBag.length, this.m_perClass.length);
/* 270:408 */     for (int i = 0; i < this.m_perBag.length; i++)
/* 271:    */     {
/* 272:409 */       newDistribution.m_perBag[i] = this.m_perBag[i];
/* 273:410 */       for (int j = 0; j < this.m_perClass.length; j++) {
/* 274:411 */         newDistribution.m_perClassPerBag[i][j] = this.m_perClassPerBag[i][j];
/* 275:    */       }
/* 276:    */     }
/* 277:414 */     for (int j = 0; j < this.m_perClass.length; j++) {
/* 278:415 */       newDistribution.m_perClass[j] = this.m_perClass[j];
/* 279:    */     }
/* 280:417 */     newDistribution.totaL = this.totaL;
/* 281:    */     
/* 282:419 */     return newDistribution;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public final void del(int bagIndex, Instance instance)
/* 286:    */     throws Exception
/* 287:    */   {
/* 288:432 */     int classIndex = (int)instance.classValue();
/* 289:433 */     double weight = instance.weight();
/* 290:434 */     this.m_perClassPerBag[bagIndex][classIndex] -= weight;
/* 291:    */     
/* 292:436 */     this.m_perBag[bagIndex] -= weight;
/* 293:437 */     this.m_perClass[classIndex] -= weight;
/* 294:438 */     this.totaL -= weight;
/* 295:    */   }
/* 296:    */   
/* 297:    */   public final void delRange(int bagIndex, Instances source, int startIndex, int lastPlusOne)
/* 298:    */     throws Exception
/* 299:    */   {
/* 300:449 */     double sumOfWeights = 0.0D;
/* 301:454 */     for (int i = startIndex; i < lastPlusOne; i++)
/* 302:    */     {
/* 303:455 */       Instance instance = source.instance(i);
/* 304:456 */       int classIndex = (int)instance.classValue();
/* 305:457 */       sumOfWeights += instance.weight();
/* 306:458 */       this.m_perClassPerBag[bagIndex][classIndex] -= instance.weight();
/* 307:459 */       this.m_perClass[classIndex] -= instance.weight();
/* 308:    */     }
/* 309:461 */     this.m_perBag[bagIndex] -= sumOfWeights;
/* 310:462 */     this.totaL -= sumOfWeights;
/* 311:    */   }
/* 312:    */   
/* 313:    */   public final String dumpDistribution()
/* 314:    */   {
/* 315:474 */     StringBuffer text = new StringBuffer();
/* 316:475 */     for (int i = 0; i < this.m_perBag.length; i++)
/* 317:    */     {
/* 318:476 */       text.append("Bag num " + i + "\n");
/* 319:477 */       for (int j = 0; j < this.m_perClass.length; j++) {
/* 320:478 */         text.append("Class num " + j + " " + this.m_perClassPerBag[i][j] + "\n");
/* 321:    */       }
/* 322:    */     }
/* 323:481 */     return text.toString();
/* 324:    */   }
/* 325:    */   
/* 326:    */   public final void initialize()
/* 327:    */   {
/* 328:489 */     for (int i = 0; i < this.m_perClass.length; i++) {
/* 329:490 */       this.m_perClass[i] = 0.0D;
/* 330:    */     }
/* 331:492 */     for (int i = 0; i < this.m_perBag.length; i++) {
/* 332:493 */       this.m_perBag[i] = 0.0D;
/* 333:    */     }
/* 334:495 */     for (int i = 0; i < this.m_perBag.length; i++) {
/* 335:496 */       for (int j = 0; j < this.m_perClass.length; j++) {
/* 336:497 */         this.m_perClassPerBag[i][j] = 0.0D;
/* 337:    */       }
/* 338:    */     }
/* 339:500 */     this.totaL = 0.0D;
/* 340:    */   }
/* 341:    */   
/* 342:    */   public final double[][] matrix()
/* 343:    */   {
/* 344:508 */     return this.m_perClassPerBag;
/* 345:    */   }
/* 346:    */   
/* 347:    */   public final int maxBag()
/* 348:    */   {
/* 349:520 */     double max = 0.0D;
/* 350:521 */     int maxIndex = -1;
/* 351:522 */     for (int i = 0; i < this.m_perBag.length; i++) {
/* 352:523 */       if (Utils.grOrEq(this.m_perBag[i], max))
/* 353:    */       {
/* 354:524 */         max = this.m_perBag[i];
/* 355:525 */         maxIndex = i;
/* 356:    */       }
/* 357:    */     }
/* 358:528 */     return maxIndex;
/* 359:    */   }
/* 360:    */   
/* 361:    */   public final int maxClass()
/* 362:    */   {
/* 363:536 */     double maxCount = 0.0D;
/* 364:537 */     int maxIndex = 0;
/* 365:540 */     for (int i = 0; i < this.m_perClass.length; i++) {
/* 366:541 */       if (Utils.gr(this.m_perClass[i], maxCount))
/* 367:    */       {
/* 368:542 */         maxCount = this.m_perClass[i];
/* 369:543 */         maxIndex = i;
/* 370:    */       }
/* 371:    */     }
/* 372:547 */     return maxIndex;
/* 373:    */   }
/* 374:    */   
/* 375:    */   public final int maxClass(int index)
/* 376:    */   {
/* 377:555 */     double maxCount = 0.0D;
/* 378:556 */     int maxIndex = 0;
/* 379:559 */     if (Utils.gr(this.m_perBag[index], 0.0D))
/* 380:    */     {
/* 381:560 */       for (int i = 0; i < this.m_perClass.length; i++) {
/* 382:561 */         if (Utils.gr(this.m_perClassPerBag[index][i], maxCount))
/* 383:    */         {
/* 384:562 */           maxCount = this.m_perClassPerBag[index][i];
/* 385:563 */           maxIndex = i;
/* 386:    */         }
/* 387:    */       }
/* 388:566 */       return maxIndex;
/* 389:    */     }
/* 390:568 */     return maxClass();
/* 391:    */   }
/* 392:    */   
/* 393:    */   public final int numBags()
/* 394:    */   {
/* 395:577 */     return this.m_perBag.length;
/* 396:    */   }
/* 397:    */   
/* 398:    */   public final int numClasses()
/* 399:    */   {
/* 400:585 */     return this.m_perClass.length;
/* 401:    */   }
/* 402:    */   
/* 403:    */   public final double numCorrect()
/* 404:    */   {
/* 405:593 */     return this.m_perClass[maxClass()];
/* 406:    */   }
/* 407:    */   
/* 408:    */   public final double numCorrect(int index)
/* 409:    */   {
/* 410:601 */     return this.m_perClassPerBag[index][maxClass(index)];
/* 411:    */   }
/* 412:    */   
/* 413:    */   public final double numIncorrect()
/* 414:    */   {
/* 415:609 */     return this.totaL - numCorrect();
/* 416:    */   }
/* 417:    */   
/* 418:    */   public final double numIncorrect(int index)
/* 419:    */   {
/* 420:617 */     return this.m_perBag[index] - numCorrect(index);
/* 421:    */   }
/* 422:    */   
/* 423:    */   public final double perClassPerBag(int bagIndex, int classIndex)
/* 424:    */   {
/* 425:626 */     return this.m_perClassPerBag[bagIndex][classIndex];
/* 426:    */   }
/* 427:    */   
/* 428:    */   public final double perBag(int bagIndex)
/* 429:    */   {
/* 430:634 */     return this.m_perBag[bagIndex];
/* 431:    */   }
/* 432:    */   
/* 433:    */   public final double perClass(int classIndex)
/* 434:    */   {
/* 435:642 */     return this.m_perClass[classIndex];
/* 436:    */   }
/* 437:    */   
/* 438:    */   public final double laplaceProb(int classIndex)
/* 439:    */   {
/* 440:650 */     return (this.m_perClass[classIndex] + 1.0D) / (this.totaL + this.m_perClass.length);
/* 441:    */   }
/* 442:    */   
/* 443:    */   public final double laplaceProb(int classIndex, int intIndex)
/* 444:    */   {
/* 445:658 */     if (Utils.gr(this.m_perBag[intIndex], 0.0D)) {
/* 446:659 */       return (this.m_perClassPerBag[intIndex][classIndex] + 1.0D) / (this.m_perBag[intIndex] + this.m_perClass.length);
/* 447:    */     }
/* 448:662 */     return laplaceProb(classIndex);
/* 449:    */   }
/* 450:    */   
/* 451:    */   public final double prob(int classIndex)
/* 452:    */   {
/* 453:672 */     if (!Utils.eq(this.totaL, 0.0D)) {
/* 454:673 */       return this.m_perClass[classIndex] / this.totaL;
/* 455:    */     }
/* 456:675 */     return 0.0D;
/* 457:    */   }
/* 458:    */   
/* 459:    */   public final double prob(int classIndex, int intIndex)
/* 460:    */   {
/* 461:684 */     if (Utils.gr(this.m_perBag[intIndex], 0.0D)) {
/* 462:685 */       return this.m_perClassPerBag[intIndex][classIndex] / this.m_perBag[intIndex];
/* 463:    */     }
/* 464:687 */     return prob(classIndex);
/* 465:    */   }
/* 466:    */   
/* 467:    */   public final Distribution subtract(Distribution toSubstract)
/* 468:    */   {
/* 469:697 */     Distribution newDist = new Distribution(1, this.m_perClass.length);
/* 470:    */     
/* 471:699 */     newDist.m_perBag[0] = (this.totaL - toSubstract.totaL);
/* 472:700 */     newDist.totaL = newDist.m_perBag[0];
/* 473:701 */     for (int i = 0; i < this.m_perClass.length; i++)
/* 474:    */     {
/* 475:702 */       newDist.m_perClassPerBag[0][i] = (this.m_perClass[i] - toSubstract.m_perClass[i]);
/* 476:    */       
/* 477:704 */       newDist.m_perClass[i] = newDist.m_perClassPerBag[0][i];
/* 478:    */     }
/* 479:706 */     return newDist;
/* 480:    */   }
/* 481:    */   
/* 482:    */   public final double total()
/* 483:    */   {
/* 484:714 */     return this.totaL;
/* 485:    */   }
/* 486:    */   
/* 487:    */   public final void shift(int from, int to, Instance instance)
/* 488:    */     throws Exception
/* 489:    */   {
/* 490:727 */     int classIndex = (int)instance.classValue();
/* 491:728 */     double weight = instance.weight();
/* 492:729 */     this.m_perClassPerBag[from][classIndex] -= weight;
/* 493:730 */     this.m_perClassPerBag[to][classIndex] += weight;
/* 494:731 */     this.m_perBag[from] -= weight;
/* 495:732 */     this.m_perBag[to] += weight;
/* 496:    */   }
/* 497:    */   
/* 498:    */   public final void shiftRange(int from, int to, Instances source, int startIndex, int lastPlusOne)
/* 499:    */     throws Exception
/* 500:    */   {
/* 501:748 */     for (int i = startIndex; i < lastPlusOne; i++)
/* 502:    */     {
/* 503:749 */       Instance instance = source.instance(i);
/* 504:750 */       int classIndex = (int)instance.classValue();
/* 505:751 */       double weight = instance.weight();
/* 506:752 */       this.m_perClassPerBag[from][classIndex] -= weight;
/* 507:753 */       this.m_perClassPerBag[to][classIndex] += weight;
/* 508:754 */       this.m_perBag[from] -= weight;
/* 509:755 */       this.m_perBag[to] += weight;
/* 510:    */     }
/* 511:    */   }
/* 512:    */   
/* 513:    */   public String getRevision()
/* 514:    */   {
/* 515:766 */     return RevisionUtils.extract("$Revision: 10531 $");
/* 516:    */   }
/* 517:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.Distribution
 * JD-Core Version:    0.7.0.1
 */