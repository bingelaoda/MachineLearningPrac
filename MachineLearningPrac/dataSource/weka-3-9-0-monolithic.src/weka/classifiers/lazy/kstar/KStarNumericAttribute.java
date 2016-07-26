/*   1:    */ package weka.classifiers.lazy.kstar;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionHandler;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ 
/*  10:    */ public class KStarNumericAttribute
/*  11:    */   implements KStarConstants, RevisionHandler
/*  12:    */ {
/*  13:    */   protected Instances m_TrainSet;
/*  14:    */   protected Instance m_Test;
/*  15:    */   protected Instance m_Train;
/*  16:    */   protected int m_AttrIndex;
/*  17: 55 */   protected double m_Scale = 1.0D;
/*  18: 61 */   protected double m_MissingProb = 1.0D;
/*  19: 66 */   protected double m_AverageProb = 1.0D;
/*  20: 71 */   protected double m_SmallestProb = 1.0D;
/*  21:    */   protected double[] m_Distances;
/*  22:    */   protected int[][] m_RandClassCols;
/*  23: 86 */   protected int m_ActualCount = 0;
/*  24:    */   protected KStarCache m_Cache;
/*  25:    */   protected int m_NumInstances;
/*  26:    */   protected int m_NumClasses;
/*  27:    */   protected int m_NumAttributes;
/*  28:    */   protected int m_ClassType;
/*  29:107 */   protected int m_MissingMode = 4;
/*  30:110 */   protected int m_BlendMethod = 1;
/*  31:113 */   protected int m_BlendFactor = 20;
/*  32:    */   
/*  33:    */   public KStarNumericAttribute(Instance test, Instance train, int attrIndex, Instances trainSet, int[][] randClassCols, KStarCache cache)
/*  34:    */   {
/*  35:120 */     this.m_Test = test;
/*  36:121 */     this.m_Train = train;
/*  37:122 */     this.m_AttrIndex = attrIndex;
/*  38:123 */     this.m_TrainSet = trainSet;
/*  39:124 */     this.m_RandClassCols = randClassCols;
/*  40:125 */     this.m_Cache = cache;
/*  41:126 */     init();
/*  42:    */   }
/*  43:    */   
/*  44:    */   private void init()
/*  45:    */   {
/*  46:    */     try
/*  47:    */     {
/*  48:134 */       this.m_NumInstances = this.m_TrainSet.numInstances();
/*  49:135 */       this.m_NumClasses = this.m_TrainSet.numClasses();
/*  50:136 */       this.m_NumAttributes = this.m_TrainSet.numAttributes();
/*  51:137 */       this.m_ClassType = this.m_TrainSet.classAttribute().type();
/*  52:    */     }
/*  53:    */     catch (Exception e)
/*  54:    */     {
/*  55:139 */       e.printStackTrace();
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:    */   public double transProb()
/*  60:    */   {
/*  61:154 */     if (this.m_Cache.containsKey(this.m_Test.value(this.m_AttrIndex)))
/*  62:    */     {
/*  63:155 */       KStarCache.TableEntry te = this.m_Cache.getCacheValues(this.m_Test.value(this.m_AttrIndex));
/*  64:    */       
/*  65:157 */       this.m_Scale = te.value;
/*  66:158 */       this.m_MissingProb = te.pmiss;
/*  67:    */     }
/*  68:    */     else
/*  69:    */     {
/*  70:160 */       if (this.m_BlendMethod == 2) {
/*  71:161 */         this.m_Scale = scaleFactorUsingEntropy();
/*  72:    */       } else {
/*  73:163 */         this.m_Scale = scaleFactorUsingBlend();
/*  74:    */       }
/*  75:165 */       this.m_Cache.store(this.m_Test.value(this.m_AttrIndex), this.m_Scale, this.m_MissingProb);
/*  76:    */     }
/*  77:    */     double transProb;
/*  78:    */     double transProb;
/*  79:168 */     if (this.m_Train.isMissing(this.m_AttrIndex))
/*  80:    */     {
/*  81:169 */       transProb = this.m_MissingProb;
/*  82:    */     }
/*  83:    */     else
/*  84:    */     {
/*  85:171 */       double distance = Math.abs(this.m_Test.value(this.m_AttrIndex) - this.m_Train.value(this.m_AttrIndex));
/*  86:    */       
/*  87:173 */       transProb = PStar(distance, this.m_Scale);
/*  88:    */     }
/*  89:175 */     return transProb;
/*  90:    */   }
/*  91:    */   
/*  92:    */   private double scaleFactorUsingBlend()
/*  93:    */   {
/*  94:185 */     int lowestcount = 0;
/*  95:186 */     double lowest = -1.0D;double nextlowest = -1.0D;
/*  96:    */     
/*  97:188 */     double min_val = 9.0E+300D;double scale = 1.0D;
/*  98:189 */     double avgprob = 0.0D;double minprob = 0.0D;double min_pos = 0.0D;
/*  99:    */     
/* 100:191 */     KStarWrapper botvals = new KStarWrapper();
/* 101:192 */     KStarWrapper upvals = new KStarWrapper();
/* 102:193 */     KStarWrapper vals = new KStarWrapper();
/* 103:    */     
/* 104:195 */     this.m_Distances = new double[this.m_NumInstances];
/* 105:197 */     for (int j = 0; j < this.m_NumInstances; j++) {
/* 106:198 */       if (this.m_TrainSet.instance(j).isMissing(this.m_AttrIndex))
/* 107:    */       {
/* 108:201 */         this.m_Distances[j] = -1.0D;
/* 109:    */       }
/* 110:    */       else
/* 111:    */       {
/* 112:203 */         this.m_Distances[j] = Math.abs(this.m_TrainSet.instance(j).value(this.m_AttrIndex) - this.m_Test.value(this.m_AttrIndex));
/* 113:205 */         if ((this.m_Distances[j] + 1.E-005D < nextlowest) || (nextlowest == -1.0D)) {
/* 114:206 */           if ((this.m_Distances[j] + 1.E-005D < lowest) || (lowest == -1.0D))
/* 115:    */           {
/* 116:207 */             nextlowest = lowest;
/* 117:208 */             lowest = this.m_Distances[j];
/* 118:209 */             lowestcount = 1;
/* 119:    */           }
/* 120:210 */           else if (Math.abs(this.m_Distances[j] - lowest) < 1.E-005D)
/* 121:    */           {
/* 122:213 */             lowestcount++;
/* 123:    */           }
/* 124:    */           else
/* 125:    */           {
/* 126:215 */             nextlowest = this.m_Distances[j];
/* 127:    */           }
/* 128:    */         }
/* 129:219 */         this.m_ActualCount += 1;
/* 130:    */       }
/* 131:    */     }
/* 132:223 */     if ((nextlowest == -1.0D) || (lowest == -1.0D))
/* 133:    */     {
/* 134:224 */       scale = 1.0D;
/* 135:225 */       this.m_SmallestProb = (this.m_AverageProb = 1.0D);
/* 136:226 */       return scale;
/* 137:    */     }
/* 138:229 */     double root = 1.0D / (nextlowest - lowest);
/* 139:230 */     int i = 0;
/* 140:    */     
/* 141:    */ 
/* 142:    */ 
/* 143:234 */     double aimfor = (this.m_ActualCount - lowestcount) * this.m_BlendFactor / 100.0D + lowestcount;
/* 144:236 */     if (this.m_BlendFactor == 0) {
/* 145:237 */       aimfor += 1.0D;
/* 146:    */     }
/* 147:240 */     double bot = 0.005D;
/* 148:241 */     double up = root * 16.0D;
/* 149:    */     
/* 150:243 */     calculateSphereSize(bot, botvals);
/* 151:244 */     botvals.sphere -= aimfor;
/* 152:    */     
/* 153:246 */     calculateSphereSize(up, upvals);
/* 154:247 */     upvals.sphere -= aimfor;
/* 155:249 */     if (botvals.sphere < 0.0D)
/* 156:    */     {
/* 157:251 */       min_pos = bot;
/* 158:252 */       avgprob = botvals.avgProb;
/* 159:253 */       minprob = botvals.minProb;
/* 160:    */     }
/* 161:254 */     else if (upvals.sphere > 0.0D)
/* 162:    */     {
/* 163:256 */       min_pos = up;
/* 164:257 */       avgprob = upvals.avgProb;
/* 165:258 */       minprob = upvals.minProb;
/* 166:    */     }
/* 167:    */     else
/* 168:    */     {
/* 169:    */       do
/* 170:    */       {
/* 171:262 */         calculateSphereSize(root, vals);
/* 172:263 */         vals.sphere -= aimfor;
/* 173:264 */         if (Math.abs(vals.sphere) < min_val)
/* 174:    */         {
/* 175:265 */           min_val = Math.abs(vals.sphere);
/* 176:266 */           min_pos = root;
/* 177:267 */           avgprob = vals.avgProb;
/* 178:268 */           minprob = vals.minProb;
/* 179:    */         }
/* 180:270 */         if (Math.abs(vals.sphere) <= 0.01D) {
/* 181:    */           break;
/* 182:    */         }
/* 183:273 */         if (vals.sphere > 0.0D)
/* 184:    */         {
/* 185:274 */           double broot = (root + up) / 2.0D;
/* 186:275 */           bot = root;
/* 187:276 */           root = broot;
/* 188:    */         }
/* 189:    */         else
/* 190:    */         {
/* 191:278 */           double broot = (root + bot) / 2.0D;
/* 192:279 */           up = root;
/* 193:280 */           root = broot;
/* 194:    */         }
/* 195:282 */         i++;
/* 196:283 */       } while (i <= 40);
/* 197:286 */       root = min_pos;
/* 198:    */     }
/* 199:292 */     this.m_SmallestProb = minprob;
/* 200:293 */     this.m_AverageProb = avgprob;
/* 201:295 */     switch (this.m_MissingMode)
/* 202:    */     {
/* 203:    */     case 1: 
/* 204:297 */       this.m_MissingProb = 0.0D;
/* 205:298 */       break;
/* 206:    */     case 3: 
/* 207:300 */       this.m_MissingProb = 1.0D;
/* 208:301 */       break;
/* 209:    */     case 2: 
/* 210:303 */       this.m_MissingProb = this.m_SmallestProb;
/* 211:304 */       break;
/* 212:    */     case 4: 
/* 213:306 */       this.m_MissingProb = this.m_AverageProb;
/* 214:    */     }
/* 215:310 */     scale = min_pos;
/* 216:311 */     return scale;
/* 217:    */   }
/* 218:    */   
/* 219:    */   private void calculateSphereSize(double scale, KStarWrapper params)
/* 220:    */   {
/* 221:322 */     double minprob = 1.0D;
/* 222:    */     
/* 223:324 */     double pstarSum = 0.0D;
/* 224:325 */     double pstarSquareSum = 0.0D;
/* 225:327 */     for (int i = 0; i < this.m_NumInstances; i++) {
/* 226:328 */       if (this.m_Distances[i] >= 0.0D)
/* 227:    */       {
/* 228:332 */         double pstar = PStar(this.m_Distances[i], scale);
/* 229:333 */         if (minprob > pstar) {
/* 230:334 */           minprob = pstar;
/* 231:    */         }
/* 232:336 */         double inc = pstar / this.m_ActualCount;
/* 233:337 */         pstarSum += inc;
/* 234:338 */         pstarSquareSum += inc * inc;
/* 235:    */       }
/* 236:    */     }
/* 237:341 */     double sphereSize = pstarSquareSum == 0.0D ? 0.0D : pstarSum * pstarSum / pstarSquareSum;
/* 238:    */     
/* 239:    */ 
/* 240:344 */     params.sphere = sphereSize;
/* 241:345 */     params.avgProb = pstarSum;
/* 242:346 */     params.minProb = minprob;
/* 243:    */   }
/* 244:    */   
/* 245:    */   private double scaleFactorUsingEntropy()
/* 246:    */   {
/* 247:355 */     String debug = "(KStarNumericAttribute.scaleFactorUsingEntropy)";
/* 248:356 */     if (this.m_ClassType != 1)
/* 249:    */     {
/* 250:357 */       System.err.println("Error: " + debug + " attribute class must be nominal!");
/* 251:    */       
/* 252:359 */       System.exit(1);
/* 253:    */     }
/* 254:362 */     double lowest = -1.0D;double nextlowest = -1.0D;
/* 255:    */     
/* 256:    */ 
/* 257:    */ 
/* 258:366 */     double scale = 1.0D;
/* 259:    */     
/* 260:368 */     KStarWrapper botvals = new KStarWrapper();
/* 261:369 */     KStarWrapper upvals = new KStarWrapper();
/* 262:370 */     KStarWrapper vals = new KStarWrapper();
/* 263:    */     
/* 264:372 */     this.m_Distances = new double[this.m_NumInstances];
/* 265:374 */     for (int j = 0; j < this.m_NumInstances; j++) {
/* 266:375 */       if (this.m_TrainSet.instance(j).isMissing(this.m_AttrIndex))
/* 267:    */       {
/* 268:378 */         this.m_Distances[j] = -1.0D;
/* 269:    */       }
/* 270:    */       else
/* 271:    */       {
/* 272:380 */         this.m_Distances[j] = Math.abs(this.m_TrainSet.instance(j).value(this.m_AttrIndex) - this.m_Test.value(this.m_AttrIndex));
/* 273:383 */         if ((this.m_Distances[j] + 1.E-005D < nextlowest) || (nextlowest == -1.0D)) {
/* 274:384 */           if ((this.m_Distances[j] + 1.E-005D < lowest) || (lowest == -1.0D))
/* 275:    */           {
/* 276:385 */             nextlowest = lowest;
/* 277:386 */             lowest = this.m_Distances[j];
/* 278:    */           }
/* 279:387 */           else if (Math.abs(this.m_Distances[j] - lowest) >= 1.E-005D)
/* 280:    */           {
/* 281:389 */             nextlowest = this.m_Distances[j];
/* 282:    */           }
/* 283:    */         }
/* 284:393 */         this.m_ActualCount += 1;
/* 285:    */       }
/* 286:    */     }
/* 287:397 */     if ((nextlowest == -1.0D) || (lowest == -1.0D))
/* 288:    */     {
/* 289:398 */       scale = 1.0D;
/* 290:399 */       this.m_SmallestProb = (this.m_AverageProb = 1.0D);
/* 291:400 */       return scale;
/* 292:    */     }
/* 293:403 */     double root = 1.0D / (nextlowest - lowest);
/* 294:    */     
/* 295:405 */     double bot = 0.005D;
/* 296:406 */     double up = root * 8.0D;
/* 297:    */     
/* 298:408 */     calculateEntropy(up, upvals);
/* 299:409 */     calculateEntropy(bot, botvals);
/* 300:410 */     double randscale = botvals.randEntropy - upvals.randEntropy;
/* 301:    */     
/* 302:412 */     double bestroot = root = bot;
/* 303:    */     double currentdiff;
/* 304:413 */     double bestdiff = currentdiff = 0.1D;
/* 305:414 */     double bestpsum = botvals.avgProb;
/* 306:415 */     double bestminprob = botvals.minProb;
/* 307:416 */     double stepsize = (up - bot) / 20.0D;
/* 308:417 */     int itcount = 0;
/* 309:    */     for (;;)
/* 310:    */     {
/* 311:420 */       itcount++;
/* 312:421 */       double lastdiff = currentdiff;
/* 313:422 */       root += Math.log(root + 1.0D) * stepsize;
/* 314:    */       double delta;
/* 315:    */       double delta;
/* 316:423 */       if (root <= bot)
/* 317:    */       {
/* 318:424 */         root = bot;
/* 319:425 */         currentdiff = 0.0D;
/* 320:426 */         delta = -1.0D;
/* 321:    */       }
/* 322:    */       else
/* 323:    */       {
/* 324:    */         double delta;
/* 325:427 */         if (root >= up)
/* 326:    */         {
/* 327:428 */           root = up;
/* 328:429 */           currentdiff = 0.0D;
/* 329:430 */           delta = -1.0D;
/* 330:    */         }
/* 331:    */         else
/* 332:    */         {
/* 333:432 */           calculateEntropy(root, vals);
/* 334:    */           
/* 335:434 */           vals.randEntropy = ((vals.randEntropy - upvals.randEntropy) / randscale);
/* 336:    */           
/* 337:436 */           vals.actEntropy = ((vals.actEntropy - upvals.actEntropy) / randscale);
/* 338:437 */           currentdiff = vals.randEntropy - vals.actEntropy;
/* 339:439 */           if (currentdiff < 0.1D)
/* 340:    */           {
/* 341:440 */             currentdiff = 0.1D;
/* 342:441 */             if (stepsize < 0.0D)
/* 343:    */             {
/* 344:444 */               bestdiff = currentdiff;
/* 345:445 */               bestroot = bot;
/* 346:446 */               bestpsum = botvals.avgProb;
/* 347:447 */               bestminprob = botvals.minProb;
/* 348:448 */               break;
/* 349:    */             }
/* 350:    */           }
/* 351:451 */           delta = currentdiff - lastdiff;
/* 352:    */         }
/* 353:    */       }
/* 354:453 */       if (currentdiff > bestdiff)
/* 355:    */       {
/* 356:454 */         bestdiff = currentdiff;
/* 357:455 */         bestroot = root;
/* 358:456 */         bestminprob = vals.minProb;
/* 359:457 */         bestpsum = vals.avgProb;
/* 360:    */       }
/* 361:459 */       if (delta < 0.0D)
/* 362:    */       {
/* 363:460 */         if (Math.abs(stepsize) >= 0.01D) {
/* 364:463 */           stepsize /= -4.0D;
/* 365:    */         }
/* 366:    */       }
/* 367:466 */       else if (itcount > 40) {
/* 368:    */         break;
/* 369:    */       }
/* 370:    */     }
/* 371:473 */     this.m_SmallestProb = bestminprob;
/* 372:474 */     this.m_AverageProb = bestpsum;
/* 373:476 */     switch (this.m_MissingMode)
/* 374:    */     {
/* 375:    */     case 1: 
/* 376:478 */       this.m_MissingProb = 0.0D;
/* 377:479 */       break;
/* 378:    */     case 3: 
/* 379:481 */       this.m_MissingProb = 1.0D;
/* 380:482 */       break;
/* 381:    */     case 2: 
/* 382:484 */       this.m_MissingProb = this.m_SmallestProb;
/* 383:485 */       break;
/* 384:    */     case 4: 
/* 385:487 */       this.m_MissingProb = this.m_AverageProb;
/* 386:    */     }
/* 387:491 */     scale = bestroot;
/* 388:    */     
/* 389:493 */     return scale;
/* 390:    */   }
/* 391:    */   
/* 392:    */   private void calculateEntropy(double scale, KStarWrapper params)
/* 393:    */   {
/* 394:504 */     double actent = 0.0D;double randent = 0.0D;
/* 395:505 */     double avgprob = 0.0D;double minprob = 1.0D;
/* 396:    */     
/* 397:507 */     double[][] pseudoClassProbs = new double[6][this.m_NumClasses];
/* 398:509 */     for (int j = 0; j <= 5; j++) {
/* 399:510 */       for (int i = 0; i < this.m_NumClasses; i++) {
/* 400:511 */         pseudoClassProbs[j][i] = 0.0D;
/* 401:    */       }
/* 402:    */     }
/* 403:514 */     for (int i = 0; i < this.m_NumInstances; i++) {
/* 404:515 */       if (this.m_Distances[i] >= 0.0D)
/* 405:    */       {
/* 406:519 */         double pstar = PStar(this.m_Distances[i], scale);
/* 407:520 */         double tprob = pstar / this.m_ActualCount;
/* 408:521 */         avgprob += tprob;
/* 409:522 */         if (pstar < minprob) {
/* 410:523 */           minprob = pstar;
/* 411:    */         }
/* 412:526 */         for (int k = 0; k <= 5; k++) {
/* 413:530 */           pseudoClassProbs[k][this.m_RandClassCols[k][i]] += tprob;
/* 414:    */         }
/* 415:    */       }
/* 416:    */     }
/* 417:536 */     for (j = this.m_NumClasses - 1; j >= 0; j--)
/* 418:    */     {
/* 419:537 */       double actClassProb = pseudoClassProbs[5][j] / avgprob;
/* 420:538 */       if (actClassProb > 0.0D) {
/* 421:539 */         actent -= actClassProb * Math.log(actClassProb) / 0.693147181D;
/* 422:    */       }
/* 423:    */     }
/* 424:544 */     for (int k = 0; k < 5; k++) {
/* 425:545 */       for (i = this.m_NumClasses - 1; i >= 0; i--)
/* 426:    */       {
/* 427:546 */         double randClassProb = pseudoClassProbs[k][i] / avgprob;
/* 428:547 */         if (randClassProb > 0.0D) {
/* 429:548 */           randent -= randClassProb * Math.log(randClassProb) / 0.693147181D;
/* 430:    */         }
/* 431:    */       }
/* 432:    */     }
/* 433:552 */     randent /= 5.0D;
/* 434:    */     
/* 435:554 */     params.actEntropy = actent;
/* 436:555 */     params.randEntropy = randent;
/* 437:556 */     params.avgProb = avgprob;
/* 438:557 */     params.minProb = minprob;
/* 439:    */   }
/* 440:    */   
/* 441:    */   private double PStar(double x, double scale)
/* 442:    */   {
/* 443:569 */     return scale * Math.exp(-2.0D * x * scale);
/* 444:    */   }
/* 445:    */   
/* 446:    */   public void setOptions(int missingmode, int blendmethod, int blendfactor)
/* 447:    */   {
/* 448:580 */     this.m_MissingMode = missingmode;
/* 449:581 */     this.m_BlendMethod = blendmethod;
/* 450:582 */     this.m_BlendFactor = blendfactor;
/* 451:    */   }
/* 452:    */   
/* 453:    */   public void setMissingMode(int mode)
/* 454:    */   {
/* 455:591 */     this.m_MissingMode = mode;
/* 456:    */   }
/* 457:    */   
/* 458:    */   public void setBlendMethod(int method)
/* 459:    */   {
/* 460:600 */     this.m_BlendMethod = method;
/* 461:    */   }
/* 462:    */   
/* 463:    */   public void setBlendFactor(int factor)
/* 464:    */   {
/* 465:609 */     this.m_BlendFactor = factor;
/* 466:    */   }
/* 467:    */   
/* 468:    */   public String getRevision()
/* 469:    */   {
/* 470:619 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 471:    */   }
/* 472:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.lazy.kstar.KStarNumericAttribute
 * JD-Core Version:    0.7.0.1
 */