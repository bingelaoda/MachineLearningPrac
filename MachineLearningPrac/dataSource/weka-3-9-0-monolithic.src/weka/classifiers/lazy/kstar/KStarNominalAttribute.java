/*   1:    */ package weka.classifiers.lazy.kstar;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionHandler;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ 
/*  10:    */ public class KStarNominalAttribute
/*  11:    */   implements KStarConstants, RevisionHandler
/*  12:    */ {
/*  13:    */   protected Instances m_TrainSet;
/*  14:    */   protected Instance m_Test;
/*  15:    */   protected Instance m_Train;
/*  16:    */   protected int m_AttrIndex;
/*  17: 55 */   protected double m_Stop = 1.0D;
/*  18: 61 */   protected double m_MissingProb = 1.0D;
/*  19: 66 */   protected double m_AverageProb = 1.0D;
/*  20: 71 */   protected double m_SmallestProb = 1.0D;
/*  21:    */   protected int m_TotalCount;
/*  22:    */   protected int[] m_Distribution;
/*  23:    */   protected int[][] m_RandClassCols;
/*  24:    */   protected KStarCache m_Cache;
/*  25:    */   protected int m_NumInstances;
/*  26:    */   protected int m_NumClasses;
/*  27:    */   protected int m_NumAttributes;
/*  28:    */   protected int m_ClassType;
/*  29:106 */   protected int m_MissingMode = 4;
/*  30:109 */   protected int m_BlendMethod = 1;
/*  31:112 */   protected int m_BlendFactor = 20;
/*  32:    */   
/*  33:    */   public KStarNominalAttribute(Instance test, Instance train, int attrIndex, Instances trainSet, int[][] randClassCol, KStarCache cache)
/*  34:    */   {
/*  35:119 */     this.m_Test = test;
/*  36:120 */     this.m_Train = train;
/*  37:121 */     this.m_AttrIndex = attrIndex;
/*  38:122 */     this.m_TrainSet = trainSet;
/*  39:123 */     this.m_RandClassCols = randClassCol;
/*  40:124 */     this.m_Cache = cache;
/*  41:125 */     init();
/*  42:    */   }
/*  43:    */   
/*  44:    */   private void init()
/*  45:    */   {
/*  46:    */     try
/*  47:    */     {
/*  48:133 */       this.m_NumInstances = this.m_TrainSet.numInstances();
/*  49:134 */       this.m_NumClasses = this.m_TrainSet.numClasses();
/*  50:135 */       this.m_NumAttributes = this.m_TrainSet.numAttributes();
/*  51:136 */       this.m_ClassType = this.m_TrainSet.classAttribute().type();
/*  52:    */     }
/*  53:    */     catch (Exception e)
/*  54:    */     {
/*  55:138 */       e.printStackTrace();
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:    */   public double transProb()
/*  60:    */   {
/*  61:150 */     double transProb = 0.0D;
/*  62:153 */     if (this.m_Cache.containsKey(this.m_Test.value(this.m_AttrIndex)))
/*  63:    */     {
/*  64:154 */       KStarCache.TableEntry te = this.m_Cache.getCacheValues(this.m_Test.value(this.m_AttrIndex));
/*  65:    */       
/*  66:156 */       this.m_Stop = te.value;
/*  67:157 */       this.m_MissingProb = te.pmiss;
/*  68:    */     }
/*  69:    */     else
/*  70:    */     {
/*  71:159 */       generateAttrDistribution();
/*  72:161 */       if (this.m_BlendMethod == 2) {
/*  73:162 */         this.m_Stop = stopProbUsingEntropy();
/*  74:    */       } else {
/*  75:164 */         this.m_Stop = stopProbUsingBlend();
/*  76:    */       }
/*  77:167 */       this.m_Cache.store(this.m_Test.value(this.m_AttrIndex), this.m_Stop, this.m_MissingProb);
/*  78:    */     }
/*  79:170 */     if (this.m_Train.isMissing(this.m_AttrIndex)) {
/*  80:171 */       transProb = this.m_MissingProb;
/*  81:    */     } else {
/*  82:    */       try
/*  83:    */       {
/*  84:174 */         transProb = (1.0D - this.m_Stop) / this.m_Test.attribute(this.m_AttrIndex).numValues();
/*  85:175 */         if ((int)this.m_Test.value(this.m_AttrIndex) == (int)this.m_Train.value(this.m_AttrIndex)) {
/*  86:176 */           transProb += this.m_Stop;
/*  87:    */         }
/*  88:    */       }
/*  89:    */       catch (Exception e)
/*  90:    */       {
/*  91:179 */         e.printStackTrace();
/*  92:    */       }
/*  93:    */     }
/*  94:182 */     return transProb;
/*  95:    */   }
/*  96:    */   
/*  97:    */   private double stopProbUsingEntropy()
/*  98:    */   {
/*  99:196 */     String debug = "(KStarNominalAttribute.stopProbUsingEntropy)";
/* 100:197 */     if (this.m_ClassType != 1)
/* 101:    */     {
/* 102:198 */       System.err.println("Error: " + debug + " attribute class must be nominal!");
/* 103:    */       
/* 104:200 */       System.exit(1);
/* 105:    */     }
/* 106:202 */     int itcount = 0;
/* 107:    */     
/* 108:    */ 
/* 109:205 */     double bestminprob = 0.0D;double bestpsum = 0.0D;
/* 110:206 */     double bestdiff = 0.0D;double bestpstop = 0.0D;
/* 111:    */     
/* 112:    */ 
/* 113:209 */     KStarWrapper botvals = new KStarWrapper();
/* 114:210 */     KStarWrapper upvals = new KStarWrapper();
/* 115:211 */     KStarWrapper vals = new KStarWrapper();
/* 116:    */     
/* 117:    */ 
/* 118:214 */     double lower = 0.005D;
/* 119:215 */     double upper = 0.995D;
/* 120:    */     
/* 121:    */ 
/* 122:218 */     calculateEntropy(upper, upvals);
/* 123:219 */     calculateEntropy(lower, botvals);
/* 124:221 */     if (upvals.avgProb == 0.0D)
/* 125:    */     {
/* 126:225 */       calculateEntropy(lower, vals);
/* 127:    */     }
/* 128:    */     else
/* 129:    */     {
/* 130:    */       double pstop;
/* 131:    */       double stepsize;
/* 132:228 */       if ((upvals.randEntropy - upvals.actEntropy < botvals.randEntropy - botvals.actEntropy) && (botvals.randEntropy - botvals.actEntropy > 0.0D))
/* 133:    */       {
/* 134:    */         double pstop;
/* 135:231 */         bestpstop = pstop = lower;
/* 136:232 */         double stepsize = 0.05D;
/* 137:233 */         bestminprob = botvals.minProb;
/* 138:234 */         bestpsum = botvals.avgProb;
/* 139:    */       }
/* 140:    */       else
/* 141:    */       {
/* 142:236 */         bestpstop = pstop = upper;
/* 143:237 */         stepsize = -0.05D;
/* 144:238 */         bestminprob = upvals.minProb;
/* 145:239 */         bestpsum = upvals.avgProb;
/* 146:    */       }
/* 147:    */       double currentdiff;
/* 148:241 */       bestdiff = currentdiff = 0.0D;
/* 149:242 */       itcount = 0;
/* 150:    */       for (;;)
/* 151:    */       {
/* 152:245 */         itcount++;
/* 153:246 */         double lastdiff = currentdiff;
/* 154:247 */         pstop += stepsize;
/* 155:    */         double delta;
/* 156:    */         double delta;
/* 157:248 */         if (pstop <= lower)
/* 158:    */         {
/* 159:249 */           pstop = lower;
/* 160:250 */           currentdiff = 0.0D;
/* 161:251 */           delta = -1.0D;
/* 162:    */         }
/* 163:    */         else
/* 164:    */         {
/* 165:    */           double delta;
/* 166:252 */           if (pstop >= upper)
/* 167:    */           {
/* 168:253 */             pstop = upper;
/* 169:254 */             currentdiff = 0.0D;
/* 170:255 */             delta = -1.0D;
/* 171:    */           }
/* 172:    */           else
/* 173:    */           {
/* 174:257 */             calculateEntropy(pstop, vals);
/* 175:258 */             currentdiff = vals.randEntropy - vals.actEntropy;
/* 176:260 */             if (currentdiff < 0.0D)
/* 177:    */             {
/* 178:261 */               currentdiff = 0.0D;
/* 179:262 */               if ((Math.abs(stepsize) < 0.05D) && (bestdiff == 0.0D))
/* 180:    */               {
/* 181:263 */                 bestpstop = lower;
/* 182:264 */                 bestminprob = botvals.minProb;
/* 183:265 */                 bestpsum = botvals.avgProb;
/* 184:266 */                 break;
/* 185:    */               }
/* 186:    */             }
/* 187:269 */             delta = currentdiff - lastdiff;
/* 188:    */           }
/* 189:    */         }
/* 190:271 */         if (currentdiff > bestdiff)
/* 191:    */         {
/* 192:272 */           bestdiff = currentdiff;
/* 193:273 */           bestpstop = pstop;
/* 194:274 */           bestminprob = vals.minProb;
/* 195:275 */           bestpsum = vals.avgProb;
/* 196:    */         }
/* 197:277 */         if (delta < 0.0D)
/* 198:    */         {
/* 199:278 */           if (Math.abs(stepsize) >= 0.01D) {
/* 200:281 */             stepsize /= -2.0D;
/* 201:    */           }
/* 202:    */         }
/* 203:284 */         else if (itcount > 40) {
/* 204:    */           break;
/* 205:    */         }
/* 206:    */       }
/* 207:    */     }
/* 208:290 */     this.m_SmallestProb = bestminprob;
/* 209:291 */     this.m_AverageProb = bestpsum;
/* 210:293 */     switch (this.m_MissingMode)
/* 211:    */     {
/* 212:    */     case 1: 
/* 213:295 */       this.m_MissingProb = 0.0D;
/* 214:296 */       break;
/* 215:    */     case 3: 
/* 216:298 */       this.m_MissingProb = 1.0D;
/* 217:299 */       break;
/* 218:    */     case 2: 
/* 219:301 */       this.m_MissingProb = this.m_SmallestProb;
/* 220:302 */       break;
/* 221:    */     case 4: 
/* 222:304 */       this.m_MissingProb = this.m_AverageProb;
/* 223:    */     }
/* 224:    */     double stopProb;
/* 225:    */     double stopProb;
/* 226:308 */     if (Math.abs(bestpsum - this.m_TotalCount) < 1.E-005D) {
/* 227:310 */       stopProb = 1.0D;
/* 228:    */     } else {
/* 229:312 */       stopProb = bestpstop;
/* 230:    */     }
/* 231:314 */     return stopProb;
/* 232:    */   }
/* 233:    */   
/* 234:    */   private void calculateEntropy(double stop, KStarWrapper params)
/* 235:    */   {
/* 236:331 */     double actent = 0.0D;double randent = 0.0D;
/* 237:332 */     double psum = 0.0D;double minprob = 1.0D;
/* 238:    */     
/* 239:334 */     double[][] pseudoClassProb = new double[6][this.m_NumClasses];
/* 240:336 */     for (int j = 0; j <= 5; j++) {
/* 241:337 */       for (int i = 0; i < this.m_NumClasses; i++) {
/* 242:338 */         pseudoClassProb[j][i] = 0.0D;
/* 243:    */       }
/* 244:    */     }
/* 245:341 */     for (int i = 0; i < this.m_NumInstances; i++)
/* 246:    */     {
/* 247:342 */       Instance train = this.m_TrainSet.instance(i);
/* 248:343 */       if (!train.isMissing(this.m_AttrIndex))
/* 249:    */       {
/* 250:344 */         double pstar = PStar(this.m_Test, train, this.m_AttrIndex, stop);
/* 251:345 */         double tprob = pstar / this.m_TotalCount;
/* 252:346 */         if (pstar < minprob) {
/* 253:347 */           minprob = pstar;
/* 254:    */         }
/* 255:349 */         psum += tprob;
/* 256:351 */         for (int k = 0; k <= 5; k++) {
/* 257:355 */           pseudoClassProb[k][this.m_RandClassCols[k][i]] += tprob;
/* 258:    */         }
/* 259:    */       }
/* 260:    */     }
/* 261:361 */     for (j = this.m_NumClasses - 1; j >= 0; j--)
/* 262:    */     {
/* 263:362 */       double actClassProb = pseudoClassProb[5][j] / psum;
/* 264:363 */       if (actClassProb > 0.0D) {
/* 265:364 */         actent -= actClassProb * Math.log(actClassProb) / 0.693147181D;
/* 266:    */       }
/* 267:    */     }
/* 268:369 */     for (int k = 0; k < 5; k++) {
/* 269:370 */       for (i = this.m_NumClasses - 1; i >= 0; i--)
/* 270:    */       {
/* 271:371 */         double randClassProb = pseudoClassProb[k][i] / psum;
/* 272:372 */         if (randClassProb > 0.0D) {
/* 273:373 */           randent -= randClassProb * Math.log(randClassProb) / 0.693147181D;
/* 274:    */         }
/* 275:    */       }
/* 276:    */     }
/* 277:377 */     randent /= 5.0D;
/* 278:    */     
/* 279:379 */     params.actEntropy = actent;
/* 280:380 */     params.randEntropy = randent;
/* 281:381 */     params.avgProb = psum;
/* 282:382 */     params.minProb = minprob;
/* 283:    */   }
/* 284:    */   
/* 285:    */   private double stopProbUsingBlend()
/* 286:    */   {
/* 287:396 */     int itcount = 0;
/* 288:    */     
/* 289:    */ 
/* 290:    */ 
/* 291:400 */     KStarWrapper botvals = new KStarWrapper();
/* 292:401 */     KStarWrapper upvals = new KStarWrapper();
/* 293:402 */     KStarWrapper vals = new KStarWrapper();
/* 294:    */     
/* 295:404 */     int testvalue = (int)this.m_Test.value(this.m_AttrIndex);
/* 296:405 */     double aimfor = (this.m_TotalCount - this.m_Distribution[testvalue]) * this.m_BlendFactor / 100.0D + this.m_Distribution[testvalue];
/* 297:    */     
/* 298:    */ 
/* 299:    */ 
/* 300:409 */     double tstop = 1.0D - this.m_BlendFactor / 100.0D;
/* 301:410 */     double lower = 0.005D;
/* 302:411 */     double upper = 0.995D;
/* 303:    */     
/* 304:    */ 
/* 305:414 */     calculateSphereSize(testvalue, lower, botvals);
/* 306:415 */     botvals.sphere -= aimfor;
/* 307:416 */     calculateSphereSize(testvalue, upper, upvals);
/* 308:417 */     upvals.sphere -= aimfor;
/* 309:419 */     if (upvals.avgProb == 0.0D)
/* 310:    */     {
/* 311:423 */       calculateSphereSize(testvalue, tstop, vals);
/* 312:    */     }
/* 313:424 */     else if (upvals.sphere > 0.0D)
/* 314:    */     {
/* 315:426 */       tstop = upper;
/* 316:427 */       vals.avgProb = upvals.avgProb;
/* 317:    */     }
/* 318:    */     else
/* 319:    */     {
/* 320:    */       for (;;)
/* 321:    */       {
/* 322:431 */         itcount++;
/* 323:432 */         calculateSphereSize(testvalue, tstop, vals);
/* 324:433 */         vals.sphere -= aimfor;
/* 325:434 */         if ((Math.abs(vals.sphere) <= 0.01D) || (itcount >= 40)) {
/* 326:    */           break;
/* 327:    */         }
/* 328:438 */         if (vals.sphere > 0.0D)
/* 329:    */         {
/* 330:439 */           lower = tstop;
/* 331:440 */           tstop = (upper + lower) / 2.0D;
/* 332:    */         }
/* 333:    */         else
/* 334:    */         {
/* 335:442 */           upper = tstop;
/* 336:443 */           tstop = (upper + lower) / 2.0D;
/* 337:    */         }
/* 338:    */       }
/* 339:    */     }
/* 340:448 */     this.m_SmallestProb = vals.minProb;
/* 341:449 */     this.m_AverageProb = vals.avgProb;
/* 342:451 */     switch (this.m_MissingMode)
/* 343:    */     {
/* 344:    */     case 1: 
/* 345:453 */       this.m_MissingProb = 0.0D;
/* 346:454 */       break;
/* 347:    */     case 3: 
/* 348:456 */       this.m_MissingProb = 1.0D;
/* 349:457 */       break;
/* 350:    */     case 2: 
/* 351:459 */       this.m_MissingProb = this.m_SmallestProb;
/* 352:460 */       break;
/* 353:    */     case 4: 
/* 354:462 */       this.m_MissingProb = this.m_AverageProb;
/* 355:    */     }
/* 356:    */     double stopProb;
/* 357:    */     double stopProb;
/* 358:466 */     if (Math.abs(vals.avgProb - this.m_TotalCount) < 1.E-005D) {
/* 359:468 */       stopProb = 1.0D;
/* 360:    */     } else {
/* 361:470 */       stopProb = tstop;
/* 362:    */     }
/* 363:472 */     return stopProb;
/* 364:    */   }
/* 365:    */   
/* 366:    */   private void calculateSphereSize(int testvalue, double stop, KStarWrapper params)
/* 367:    */   {
/* 368:493 */     double tval = 0.0D;double t1 = 0.0D;
/* 369:494 */     double minprob = 1.0D;double transprob = 0.0D;
/* 370:496 */     for (int i = 0; i < this.m_Distribution.length; i++)
/* 371:    */     {
/* 372:497 */       int thiscount = this.m_Distribution[i];
/* 373:498 */       if (thiscount != 0)
/* 374:    */       {
/* 375:    */         double tprob;
/* 376:499 */         if (testvalue == i)
/* 377:    */         {
/* 378:500 */           double tprob = (stop + (1.0D - stop) / this.m_Distribution.length) / this.m_TotalCount;
/* 379:501 */           tval += tprob * thiscount;
/* 380:502 */           t1 += tprob * tprob * thiscount;
/* 381:    */         }
/* 382:    */         else
/* 383:    */         {
/* 384:504 */           tprob = (1.0D - stop) / this.m_Distribution.length / this.m_TotalCount;
/* 385:505 */           tval += tprob * thiscount;
/* 386:506 */           t1 += tprob * tprob * thiscount;
/* 387:    */         }
/* 388:508 */         if (minprob > tprob * this.m_TotalCount) {
/* 389:509 */           minprob = tprob * this.m_TotalCount;
/* 390:    */         }
/* 391:    */       }
/* 392:    */     }
/* 393:513 */     transprob = tval;
/* 394:514 */     double sphere = t1 == 0.0D ? 0.0D : tval * tval / t1;
/* 395:    */     
/* 396:516 */     params.sphere = sphere;
/* 397:517 */     params.avgProb = transprob;
/* 398:518 */     params.minProb = minprob;
/* 399:    */   }
/* 400:    */   
/* 401:    */   private double PStar(Instance test, Instance train, int col, double stop)
/* 402:    */   {
/* 403:534 */     int numvalues = 0;
/* 404:    */     try
/* 405:    */     {
/* 406:536 */       numvalues = test.attribute(col).numValues();
/* 407:    */     }
/* 408:    */     catch (Exception ex)
/* 409:    */     {
/* 410:538 */       ex.printStackTrace();
/* 411:    */     }
/* 412:    */     double pstar;
/* 413:    */     double pstar;
/* 414:540 */     if ((int)test.value(col) == (int)train.value(col)) {
/* 415:541 */       pstar = stop + (1.0D - stop) / numvalues;
/* 416:    */     } else {
/* 417:543 */       pstar = (1.0D - stop) / numvalues;
/* 418:    */     }
/* 419:545 */     return pstar;
/* 420:    */   }
/* 421:    */   
/* 422:    */   private void generateAttrDistribution()
/* 423:    */   {
/* 424:555 */     this.m_Distribution = new int[this.m_TrainSet.attribute(this.m_AttrIndex).numValues()];
/* 425:558 */     for (int i = 0; i < this.m_NumInstances; i++)
/* 426:    */     {
/* 427:559 */       Instance train = this.m_TrainSet.instance(i);
/* 428:560 */       if (!train.isMissing(this.m_AttrIndex))
/* 429:    */       {
/* 430:561 */         this.m_TotalCount += 1;
/* 431:562 */         this.m_Distribution[((int)train.value(this.m_AttrIndex))] += 1;
/* 432:    */       }
/* 433:    */     }
/* 434:    */   }
/* 435:    */   
/* 436:    */   public void setOptions(int missingmode, int blendmethod, int blendfactor)
/* 437:    */   {
/* 438:572 */     this.m_MissingMode = missingmode;
/* 439:573 */     this.m_BlendMethod = blendmethod;
/* 440:574 */     this.m_BlendFactor = blendfactor;
/* 441:    */   }
/* 442:    */   
/* 443:    */   public String getRevision()
/* 444:    */   {
/* 445:584 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 446:    */   }
/* 447:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.lazy.kstar.KStarNominalAttribute
 * JD-Core Version:    0.7.0.1
 */