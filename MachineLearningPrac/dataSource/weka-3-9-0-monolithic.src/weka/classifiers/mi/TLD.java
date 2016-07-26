/*   1:    */ package weka.classifiers.mi;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.RandomizableClassifier;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.TechnicalInformation;
/*  19:    */ import weka.core.TechnicalInformation.Field;
/*  20:    */ import weka.core.TechnicalInformation.Type;
/*  21:    */ import weka.core.TechnicalInformationHandler;
/*  22:    */ import weka.core.Utils;
/*  23:    */ 
/*  24:    */ public class TLD
/*  25:    */   extends RandomizableClassifier
/*  26:    */   implements OptionHandler, MultiInstanceCapabilitiesHandler, TechnicalInformationHandler
/*  27:    */ {
/*  28:    */   static final long serialVersionUID = 6657315525171152210L;
/*  29:106 */   protected double[][] m_MeanP = (double[][])null;
/*  30:109 */   protected double[][] m_VarianceP = (double[][])null;
/*  31:112 */   protected double[][] m_MeanN = (double[][])null;
/*  32:115 */   protected double[][] m_VarianceN = (double[][])null;
/*  33:118 */   protected double[][] m_SumP = (double[][])null;
/*  34:121 */   protected double[][] m_SumN = (double[][])null;
/*  35:124 */   protected double[] m_ParamsP = null;
/*  36:127 */   protected double[] m_ParamsN = null;
/*  37:130 */   protected int m_Dimension = 0;
/*  38:133 */   protected double[] m_Class = null;
/*  39:136 */   protected int m_NumClasses = 2;
/*  40:139 */   public static double ZERO = 1.0E-006D;
/*  41:142 */   protected int m_Run = 1;
/*  42:    */   protected double m_Cutoff;
/*  43:146 */   protected boolean m_UseEmpiricalCutOff = false;
/*  44:    */   
/*  45:    */   public String globalInfo()
/*  46:    */   {
/*  47:155 */     return "Two-Level Distribution approach, changes the starting value of the searching algorithm, supplement the cut-off modification and check missing values.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  48:    */   }
/*  49:    */   
/*  50:    */   public TechnicalInformation getTechnicalInformation()
/*  51:    */   {
/*  52:172 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MASTERSTHESIS);
/*  53:173 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Xin Xu");
/*  54:174 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/*  55:175 */     result.setValue(TechnicalInformation.Field.TITLE, "Statistical learning in multiple instance problem");
/*  56:    */     
/*  57:177 */     result.setValue(TechnicalInformation.Field.SCHOOL, "University of Waikato");
/*  58:178 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Hamilton, NZ");
/*  59:179 */     result.setValue(TechnicalInformation.Field.NOTE, "0657.594");
/*  60:    */     
/*  61:181 */     return result;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public Capabilities getCapabilities()
/*  65:    */   {
/*  66:191 */     Capabilities result = super.getCapabilities();
/*  67:192 */     result.disableAll();
/*  68:    */     
/*  69:    */ 
/*  70:195 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  71:196 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  72:    */     
/*  73:    */ 
/*  74:199 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/*  75:200 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  76:    */     
/*  77:    */ 
/*  78:203 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/*  79:    */     
/*  80:205 */     return result;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public Capabilities getMultiInstanceCapabilities()
/*  84:    */   {
/*  85:217 */     Capabilities result = super.getCapabilities();
/*  86:218 */     result.disableAll();
/*  87:    */     
/*  88:    */ 
/*  89:221 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  90:222 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  91:    */     
/*  92:    */ 
/*  93:225 */     result.disableAllClasses();
/*  94:226 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  95:    */     
/*  96:228 */     return result;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void buildClassifier(Instances exs)
/* 100:    */     throws Exception
/* 101:    */   {
/* 102:239 */     getCapabilities().testWithFail(exs);
/* 103:    */     
/* 104:    */ 
/* 105:242 */     exs = new Instances(exs);
/* 106:243 */     exs.deleteWithMissingClass();
/* 107:    */     
/* 108:245 */     int numegs = exs.numInstances();
/* 109:246 */     this.m_Dimension = exs.attribute(1).relation().numAttributes();
/* 110:247 */     Instances pos = new Instances(exs, 0);Instances neg = new Instances(exs, 0);
/* 111:249 */     for (int u = 0; u < numegs; u++)
/* 112:    */     {
/* 113:250 */       Instance example = exs.instance(u);
/* 114:251 */       if (example.classValue() == 1.0D) {
/* 115:252 */         pos.add(example);
/* 116:    */       } else {
/* 117:254 */         neg.add(example);
/* 118:    */       }
/* 119:    */     }
/* 120:258 */     int pnum = pos.numInstances();int nnum = neg.numInstances();
/* 121:    */     
/* 122:260 */     this.m_MeanP = new double[pnum][this.m_Dimension];
/* 123:261 */     this.m_VarianceP = new double[pnum][this.m_Dimension];
/* 124:262 */     this.m_SumP = new double[pnum][this.m_Dimension];
/* 125:263 */     this.m_MeanN = new double[nnum][this.m_Dimension];
/* 126:264 */     this.m_VarianceN = new double[nnum][this.m_Dimension];
/* 127:265 */     this.m_SumN = new double[nnum][this.m_Dimension];
/* 128:266 */     this.m_ParamsP = new double[4 * this.m_Dimension];
/* 129:267 */     this.m_ParamsN = new double[4 * this.m_Dimension];
/* 130:    */     
/* 131:    */ 
/* 132:270 */     double[] pSumVal = new double[this.m_Dimension];
/* 133:271 */     double[] nSumVal = new double[this.m_Dimension];
/* 134:272 */     double[] maxVarsP = new double[this.m_Dimension];
/* 135:273 */     double[] maxVarsN = new double[this.m_Dimension];
/* 136:    */     
/* 137:275 */     double[] varMeanP = new double[this.m_Dimension];double[] varMeanN = new double[this.m_Dimension];
/* 138:    */     
/* 139:277 */     double[] meanVarP = new double[this.m_Dimension];double[] meanVarN = new double[this.m_Dimension];
/* 140:    */     
/* 141:279 */     double[] numExsP = new double[this.m_Dimension];double[] numExsN = new double[this.m_Dimension];
/* 142:282 */     for (int v = 0; v < pnum; v++)
/* 143:    */     {
/* 144:288 */       Instances pxi = pos.instance(v).relationalValue(1);
/* 145:289 */       for (int k = 0; k < pxi.numAttributes(); k++)
/* 146:    */       {
/* 147:290 */         this.m_MeanP[v][k] = pxi.meanOrMode(k);
/* 148:291 */         this.m_VarianceP[v][k] = pxi.variance(k);
/* 149:    */       }
/* 150:294 */       int w = 0;
/* 151:294 */       for (int t = 0; w < this.m_Dimension; t++)
/* 152:    */       {
/* 153:298 */         if (!Double.isNaN(this.m_MeanP[v][w]))
/* 154:    */         {
/* 155:299 */           for (int u = 0; u < pxi.numInstances(); u++)
/* 156:    */           {
/* 157:300 */             Instance ins = pxi.instance(u);
/* 158:301 */             if (!ins.isMissing(t)) {
/* 159:302 */               this.m_SumP[v][w] += ins.weight();
/* 160:    */             }
/* 161:    */           }
/* 162:305 */           numExsP[w] += 1.0D;
/* 163:306 */           pSumVal[w] += this.m_MeanP[v][w];
/* 164:307 */           meanVarP[w] += this.m_MeanP[v][w] * this.m_MeanP[v][w];
/* 165:308 */           if (maxVarsP[w] < this.m_VarianceP[v][w]) {
/* 166:309 */             maxVarsP[w] = this.m_VarianceP[v][w];
/* 167:    */           }
/* 168:311 */           varMeanP[w] += this.m_VarianceP[v][w];
/* 169:312 */           this.m_VarianceP[v][w] *= (this.m_SumP[v][w] - 1.0D);
/* 170:313 */           if (this.m_VarianceP[v][w] < 0.0D) {
/* 171:314 */             this.m_VarianceP[v][w] = 0.0D;
/* 172:    */           }
/* 173:    */         }
/* 174:294 */         w++;
/* 175:    */       }
/* 176:    */     }
/* 177:320 */     for (int v = 0; v < nnum; v++)
/* 178:    */     {
/* 179:325 */       Instances nxi = neg.instance(v).relationalValue(1);
/* 180:326 */       for (int k = 0; k < nxi.numAttributes(); k++)
/* 181:    */       {
/* 182:327 */         this.m_MeanN[v][k] = nxi.meanOrMode(k);
/* 183:328 */         this.m_VarianceN[v][k] = nxi.variance(k);
/* 184:    */       }
/* 185:331 */       int w = 0;
/* 186:331 */       for (int t = 0; w < this.m_Dimension; t++)
/* 187:    */       {
/* 188:335 */         if (!Double.isNaN(this.m_MeanN[v][w]))
/* 189:    */         {
/* 190:336 */           for (int u = 0; u < nxi.numInstances(); u++) {
/* 191:337 */             if (!nxi.instance(u).isMissing(t)) {
/* 192:338 */               this.m_SumN[v][w] += nxi.instance(u).weight();
/* 193:    */             }
/* 194:    */           }
/* 195:341 */           numExsN[w] += 1.0D;
/* 196:342 */           nSumVal[w] += this.m_MeanN[v][w];
/* 197:343 */           meanVarN[w] += this.m_MeanN[v][w] * this.m_MeanN[v][w];
/* 198:344 */           if (maxVarsN[w] < this.m_VarianceN[v][w]) {
/* 199:345 */             maxVarsN[w] = this.m_VarianceN[v][w];
/* 200:    */           }
/* 201:347 */           varMeanN[w] += this.m_VarianceN[v][w];
/* 202:348 */           this.m_VarianceN[v][w] *= (this.m_SumN[v][w] - 1.0D);
/* 203:349 */           if (this.m_VarianceN[v][w] < 0.0D) {
/* 204:350 */             this.m_VarianceN[v][w] = 0.0D;
/* 205:    */           }
/* 206:    */         }
/* 207:331 */         w++;
/* 208:    */       }
/* 209:    */     }
/* 210:356 */     for (int w = 0; w < this.m_Dimension; w++)
/* 211:    */     {
/* 212:357 */       pSumVal[w] /= numExsP[w];
/* 213:358 */       nSumVal[w] /= numExsN[w];
/* 214:359 */       if (numExsP[w] > 1.0D) {
/* 215:360 */         meanVarP[w] = (meanVarP[w] / (numExsP[w] - 1.0D) - pSumVal[w] * numExsP[w] / (numExsP[w] - 1.0D));
/* 216:    */       }
/* 217:363 */       if (numExsN[w] > 1.0D) {
/* 218:364 */         meanVarN[w] = (meanVarN[w] / (numExsN[w] - 1.0D) - nSumVal[w] * numExsN[w] / (numExsN[w] - 1.0D));
/* 219:    */       }
/* 220:367 */       varMeanP[w] /= numExsP[w];
/* 221:368 */       varMeanN[w] /= numExsN[w];
/* 222:    */     }
/* 223:372 */     double[][] bounds = new double[2][4];
/* 224:373 */     double[] pThisParam = new double[4];double[] nThisParam = new double[4];
/* 225:    */     double a;
/* 226:    */     double b;
/* 227:    */     double w;
/* 228:    */     double m;
/* 229:    */     double pminVal;
/* 230:    */     double nminVal;
/* 231:    */     Random whichEx;
/* 232:    */     TLD_Optm pOp;
/* 233:    */     TLD_Optm nOp;
/* 234:    */     boolean isRunValid;
/* 235:    */     double[] sumP;
/* 236:    */     double[] meanP;
/* 237:    */     double[] varP;
/* 238:    */     double[] sumN;
/* 239:    */     double[] meanN;
/* 240:    */     double[] varN;
/* 241:    */     int y;
/* 242:379 */     for (int x = 0; x < this.m_Dimension; x++)
/* 243:    */     {
/* 244:380 */       if (getDebug()) {
/* 245:381 */         System.err.println("\n\n!!!!!!!!!!!!!!!!!!!!!!???Dimension #" + x);
/* 246:    */       }
/* 247:385 */       a = maxVarsP[x] > ZERO ? maxVarsP[x] : 1.0D;
/* 248:386 */       if (varMeanP[x] <= ZERO) {
/* 249:387 */         varMeanP[x] = ZERO;
/* 250:    */       }
/* 251:389 */       b = a / varMeanP[x] + 2.0D;
/* 252:390 */       w = meanVarP[x] / varMeanP[x];
/* 253:391 */       if (w <= ZERO) {
/* 254:392 */         w = 1.0D;
/* 255:    */       }
/* 256:395 */       m = pSumVal[x];
/* 257:396 */       pThisParam[0] = a;
/* 258:397 */       pThisParam[1] = b;
/* 259:398 */       pThisParam[2] = w;
/* 260:399 */       pThisParam[3] = m;
/* 261:    */       
/* 262:    */ 
/* 263:402 */       a = maxVarsN[x] > ZERO ? maxVarsN[x] : 1.0D;
/* 264:403 */       if (varMeanN[x] <= ZERO) {
/* 265:404 */         varMeanN[x] = ZERO;
/* 266:    */       }
/* 267:406 */       b = a / varMeanN[x] + 2.0D;
/* 268:407 */       w = meanVarN[x] / varMeanN[x];
/* 269:408 */       if (w <= ZERO) {
/* 270:409 */         w = 1.0D;
/* 271:    */       }
/* 272:412 */       m = nSumVal[x];
/* 273:413 */       nThisParam[0] = a;
/* 274:414 */       nThisParam[1] = b;
/* 275:415 */       nThisParam[2] = w;
/* 276:416 */       nThisParam[3] = m;
/* 277:    */       
/* 278:    */ 
/* 279:419 */       bounds[0][0] = ZERO;
/* 280:420 */       bounds[0][1] = (2.0D + ZERO);
/* 281:421 */       bounds[0][2] = ZERO;
/* 282:422 */       bounds[0][3] = (0.0D / 0.0D);
/* 283:424 */       for (int t = 0; t < 4; t++)
/* 284:    */       {
/* 285:425 */         bounds[1][t] = (0.0D / 0.0D);
/* 286:426 */         this.m_ParamsP[(4 * x + t)] = pThisParam[t];
/* 287:427 */         this.m_ParamsN[(4 * x + t)] = nThisParam[t];
/* 288:    */       }
/* 289:429 */       pminVal = 1.7976931348623157E+308D;nminVal = 1.7976931348623157E+308D;
/* 290:430 */       whichEx = new Random(this.m_Seed);
/* 291:431 */       pOp = null;nOp = null;
/* 292:432 */       isRunValid = true;
/* 293:433 */       sumP = new double[pnum];meanP = new double[pnum];varP = new double[pnum];
/* 294:434 */       sumN = new double[nnum];meanN = new double[nnum];varN = new double[nnum];
/* 295:437 */       for (int p = 0; p < pnum; p++)
/* 296:    */       {
/* 297:438 */         sumP[p] = this.m_SumP[p][x];
/* 298:439 */         meanP[p] = this.m_MeanP[p][x];
/* 299:440 */         varP[p] = this.m_VarianceP[p][x];
/* 300:    */       }
/* 301:442 */       for (int q = 0; q < nnum; q++)
/* 302:    */       {
/* 303:443 */         sumN[q] = this.m_SumN[q][x];
/* 304:444 */         meanN[q] = this.m_MeanN[q][x];
/* 305:445 */         varN[q] = this.m_VarianceN[q][x];
/* 306:    */       }
/* 307:448 */       for (y = 0; y < this.m_Run;)
/* 308:    */       {
/* 309:449 */         if (getDebug()) {
/* 310:450 */           System.err.println("\n\n!!!!!!!!!!!!!!!!!!!!!!???Run #" + y);
/* 311:    */         }
/* 312:454 */         if (getDebug()) {
/* 313:455 */           System.err.println("\nPositive exemplars");
/* 314:    */         }
/* 315:457 */         pOp = new TLD_Optm();
/* 316:458 */         pOp.setNum(sumP);
/* 317:459 */         pOp.setSSquare(varP);
/* 318:460 */         pOp.setXBar(meanP);
/* 319:    */         
/* 320:462 */         pThisParam = pOp.findArgmin(pThisParam, bounds);
/* 321:463 */         while (pThisParam == null)
/* 322:    */         {
/* 323:464 */           pThisParam = pOp.getVarbValues();
/* 324:465 */           if (getDebug()) {
/* 325:466 */             System.err.println("!!! 200 iterations finished, not enough!");
/* 326:    */           }
/* 327:468 */           pThisParam = pOp.findArgmin(pThisParam, bounds);
/* 328:    */         }
/* 329:471 */         double thisMin = pOp.getMinFunction();
/* 330:472 */         if ((!Double.isNaN(thisMin)) && (thisMin < pminVal))
/* 331:    */         {
/* 332:473 */           pminVal = thisMin;
/* 333:474 */           for (int z = 0; z < 4; z++) {
/* 334:475 */             this.m_ParamsP[(4 * x + z)] = pThisParam[z];
/* 335:    */           }
/* 336:    */         }
/* 337:479 */         if (Double.isNaN(thisMin))
/* 338:    */         {
/* 339:480 */           pThisParam = new double[4];
/* 340:481 */           isRunValid = false;
/* 341:    */         }
/* 342:484 */         if (getDebug()) {
/* 343:485 */           System.err.println("\nNegative exemplars");
/* 344:    */         }
/* 345:487 */         nOp = new TLD_Optm();
/* 346:488 */         nOp.setNum(sumN);
/* 347:489 */         nOp.setSSquare(varN);
/* 348:490 */         nOp.setXBar(meanN);
/* 349:    */         
/* 350:492 */         nThisParam = nOp.findArgmin(nThisParam, bounds);
/* 351:493 */         while (nThisParam == null)
/* 352:    */         {
/* 353:494 */           nThisParam = nOp.getVarbValues();
/* 354:495 */           if (getDebug()) {
/* 355:496 */             System.err.println("!!! 200 iterations finished, not enough!");
/* 356:    */           }
/* 357:498 */           nThisParam = nOp.findArgmin(nThisParam, bounds);
/* 358:    */         }
/* 359:500 */         thisMin = nOp.getMinFunction();
/* 360:501 */         if ((!Double.isNaN(thisMin)) && (thisMin < nminVal))
/* 361:    */         {
/* 362:502 */           nminVal = thisMin;
/* 363:503 */           for (int z = 0; z < 4; z++) {
/* 364:504 */             this.m_ParamsN[(4 * x + z)] = nThisParam[z];
/* 365:    */           }
/* 366:    */         }
/* 367:508 */         if (Double.isNaN(thisMin))
/* 368:    */         {
/* 369:509 */           nThisParam = new double[4];
/* 370:510 */           isRunValid = false;
/* 371:    */         }
/* 372:513 */         if (!isRunValid)
/* 373:    */         {
/* 374:514 */           y--;
/* 375:515 */           isRunValid = true;
/* 376:    */         }
/* 377:518 */         y++;
/* 378:518 */         if (y < this.m_Run)
/* 379:    */         {
/* 380:520 */           int pone = whichEx.nextInt(pnum);
/* 381:521 */           int none = whichEx.nextInt(nnum);
/* 382:524 */           while ((this.m_SumP[pone][x] <= 1.0D) || (Double.isNaN(this.m_MeanP[pone][x]))) {
/* 383:525 */             pone = whichEx.nextInt(pnum);
/* 384:    */           }
/* 385:528 */           a = this.m_VarianceP[pone][x] / (this.m_SumP[pone][x] - 1.0D);
/* 386:529 */           if (a <= ZERO) {
/* 387:530 */             a = this.m_ParamsN[(4 * x)];
/* 388:    */           }
/* 389:532 */           m = this.m_MeanP[pone][x];
/* 390:533 */           double sq = (m - this.m_ParamsP[(4 * x + 3)]) * (m - this.m_ParamsP[(4 * x + 3)]);
/* 391:    */           
/* 392:535 */           b = a * this.m_ParamsP[(4 * x + 2)] / sq + 2.0D;
/* 393:537 */           if ((b <= ZERO) || (Double.isNaN(b)) || (Double.isInfinite(b))) {
/* 394:538 */             b = this.m_ParamsN[(4 * x + 1)];
/* 395:    */           }
/* 396:541 */           w = sq * (this.m_ParamsP[(4 * x + 1)] - 2.0D) / this.m_ParamsP[(4 * x)];
/* 397:544 */           if ((w <= ZERO) || (Double.isNaN(w)) || (Double.isInfinite(w))) {
/* 398:545 */             w = this.m_ParamsN[(4 * x + 2)];
/* 399:    */           }
/* 400:548 */           pThisParam[0] = a;
/* 401:549 */           pThisParam[1] = b;
/* 402:550 */           pThisParam[2] = w;
/* 403:551 */           pThisParam[3] = m;
/* 404:554 */           while ((this.m_SumN[none][x] <= 1.0D) || (Double.isNaN(this.m_MeanN[none][x]))) {
/* 405:555 */             none = whichEx.nextInt(nnum);
/* 406:    */           }
/* 407:558 */           a = this.m_VarianceN[none][x] / (this.m_SumN[none][x] - 1.0D);
/* 408:559 */           if (a <= ZERO) {
/* 409:560 */             a = this.m_ParamsP[(4 * x)];
/* 410:    */           }
/* 411:562 */           m = this.m_MeanN[none][x];
/* 412:563 */           sq = (m - this.m_ParamsN[(4 * x + 3)]) * (m - this.m_ParamsN[(4 * x + 3)]);
/* 413:    */           
/* 414:565 */           b = a * this.m_ParamsN[(4 * x + 2)] / sq + 2.0D;
/* 415:567 */           if ((b <= ZERO) || (Double.isNaN(b)) || (Double.isInfinite(b))) {
/* 416:568 */             b = this.m_ParamsP[(4 * x + 1)];
/* 417:    */           }
/* 418:571 */           w = sq * (this.m_ParamsN[(4 * x + 1)] - 2.0D) / this.m_ParamsN[(4 * x)];
/* 419:574 */           if ((w <= ZERO) || (Double.isNaN(w)) || (Double.isInfinite(w))) {
/* 420:575 */             w = this.m_ParamsP[(4 * x + 2)];
/* 421:    */           }
/* 422:578 */           nThisParam[0] = a;
/* 423:579 */           nThisParam[1] = b;
/* 424:580 */           nThisParam[2] = w;
/* 425:581 */           nThisParam[3] = m;
/* 426:    */         }
/* 427:    */       }
/* 428:    */     }
/* 429:586 */     int x = 0;
/* 430:586 */     for (int y = 0; x < this.m_Dimension; y++)
/* 431:    */     {
/* 432:589 */       double a = this.m_ParamsP[(4 * x)];
/* 433:590 */       double b = this.m_ParamsP[(4 * x + 1)];
/* 434:591 */       double w = this.m_ParamsP[(4 * x + 2)];
/* 435:592 */       double m = this.m_ParamsP[(4 * x + 3)];
/* 436:593 */       if (getDebug()) {
/* 437:594 */         System.err.println("\n\n???Positive: ( " + exs.attribute(1).relation().attribute(y) + "): a=" + a + ", b=" + b + ", w=" + w + ", m=" + m);
/* 438:    */       }
/* 439:599 */       a = this.m_ParamsN[(4 * x)];
/* 440:600 */       b = this.m_ParamsN[(4 * x + 1)];
/* 441:601 */       w = this.m_ParamsN[(4 * x + 2)];
/* 442:602 */       m = this.m_ParamsN[(4 * x + 3)];
/* 443:603 */       if (getDebug()) {
/* 444:604 */         System.err.println("???Negative: (" + exs.attribute(1).relation().attribute(y) + "): a=" + a + ", b=" + b + ", w=" + w + ", m=" + m);
/* 445:    */       }
/* 446:586 */       x++;
/* 447:    */     }
/* 448:610 */     if (this.m_UseEmpiricalCutOff)
/* 449:    */     {
/* 450:612 */       double[] pLogOdds = new double[pnum];double[] nLogOdds = new double[nnum];
/* 451:613 */       for (int p = 0; p < pnum; p++) {
/* 452:614 */         pLogOdds[p] = likelihoodRatio(this.m_SumP[p], this.m_MeanP[p], this.m_VarianceP[p]);
/* 453:    */       }
/* 454:617 */       for (int q = 0; q < nnum; q++) {
/* 455:618 */         nLogOdds[q] = likelihoodRatio(this.m_SumN[q], this.m_MeanN[q], this.m_VarianceN[q]);
/* 456:    */       }
/* 457:622 */       findCutOff(pLogOdds, nLogOdds);
/* 458:    */     }
/* 459:    */     else
/* 460:    */     {
/* 461:624 */       this.m_Cutoff = (-Math.log(pnum / nnum));
/* 462:    */     }
/* 463:627 */     if (getDebug()) {
/* 464:628 */       System.err.println("???Cut-off=" + this.m_Cutoff);
/* 465:    */     }
/* 466:    */   }
/* 467:    */   
/* 468:    */   public double classifyInstance(Instance ex)
/* 469:    */     throws Exception
/* 470:    */   {
/* 471:641 */     Instances exi = ex.relationalValue(1);
/* 472:642 */     double[] n = new double[this.m_Dimension];
/* 473:643 */     double[] xBar = new double[this.m_Dimension];
/* 474:644 */     double[] sSq = new double[this.m_Dimension];
/* 475:645 */     for (int i = 0; i < exi.numAttributes(); i++)
/* 476:    */     {
/* 477:646 */       xBar[i] = exi.meanOrMode(i);
/* 478:647 */       sSq[i] = exi.variance(i);
/* 479:    */     }
/* 480:650 */     int w = 0;
/* 481:650 */     for (int t = 0; w < this.m_Dimension; t++)
/* 482:    */     {
/* 483:653 */       for (int u = 0; u < exi.numInstances(); u++) {
/* 484:654 */         if (!exi.instance(u).isMissing(t)) {
/* 485:655 */           n[w] += exi.instance(u).weight();
/* 486:    */         }
/* 487:    */       }
/* 488:659 */       sSq[w] *= (n[w] - 1.0D);
/* 489:660 */       if (sSq[w] <= 0.0D) {
/* 490:661 */         sSq[w] = 0.0D;
/* 491:    */       }
/* 492:650 */       w++;
/* 493:    */     }
/* 494:665 */     double logOdds = likelihoodRatio(n, xBar, sSq);
/* 495:666 */     return logOdds > this.m_Cutoff ? 1.0D : 0.0D;
/* 496:    */   }
/* 497:    */   
/* 498:    */   private double likelihoodRatio(double[] n, double[] xBar, double[] sSq)
/* 499:    */   {
/* 500:670 */     double LLP = 0.0D;double LLN = 0.0D;
/* 501:672 */     for (int x = 0; x < this.m_Dimension; x++) {
/* 502:673 */       if (!Double.isNaN(xBar[x]))
/* 503:    */       {
/* 504:677 */         int halfN = (int)n[x] / 2;
/* 505:    */         
/* 506:679 */         double a = this.m_ParamsP[(4 * x)];double b = this.m_ParamsP[(4 * x + 1)];double w = this.m_ParamsP[(4 * x + 2)];double m = this.m_ParamsP[(4 * x + 3)];
/* 507:680 */         LLP += 0.5D * b * Math.log(a) + 0.5D * (b + n[x] - 1.0D) * Math.log(1.0D + n[x] * w) - 0.5D * (b + n[x]) * Math.log((1.0D + n[x] * w) * (a + sSq[x]) + n[x] * (xBar[x] - m) * (xBar[x] - m)) - 0.5D * n[x] * Math.log(3.141592653589793D);
/* 508:690 */         for (int y = 1; y <= halfN; y++) {
/* 509:691 */           LLP += Math.log(b / 2.0D + n[x] / 2.0D - y);
/* 510:    */         }
/* 511:694 */         if (n[x] / 2.0D > halfN) {
/* 512:695 */           LLP += TLD_Optm.diffLnGamma(b / 2.0D);
/* 513:    */         }
/* 514:699 */         a = this.m_ParamsN[(4 * x)];
/* 515:700 */         b = this.m_ParamsN[(4 * x + 1)];
/* 516:701 */         w = this.m_ParamsN[(4 * x + 2)];
/* 517:702 */         m = this.m_ParamsN[(4 * x + 3)];
/* 518:703 */         LLN += 0.5D * b * Math.log(a) + 0.5D * (b + n[x] - 1.0D) * Math.log(1.0D + n[x] * w) - 0.5D * (b + n[x]) * Math.log((1.0D + n[x] * w) * (a + sSq[x]) + n[x] * (xBar[x] - m) * (xBar[x] - m)) - 0.5D * n[x] * Math.log(3.141592653589793D);
/* 519:713 */         for (int y = 1; y <= halfN; y++) {
/* 520:714 */           LLN += Math.log(b / 2.0D + n[x] / 2.0D - y);
/* 521:    */         }
/* 522:717 */         if (n[x] / 2.0D > halfN) {
/* 523:718 */           LLN += TLD_Optm.diffLnGamma(b / 2.0D);
/* 524:    */         }
/* 525:    */       }
/* 526:    */     }
/* 527:722 */     return LLP - LLN;
/* 528:    */   }
/* 529:    */   
/* 530:    */   private void findCutOff(double[] pos, double[] neg)
/* 531:    */   {
/* 532:726 */     int[] pOrder = Utils.sort(pos);int[] nOrder = Utils.sort(neg);
/* 533:    */     
/* 534:    */ 
/* 535:    */ 
/* 536:    */ 
/* 537:    */ 
/* 538:    */ 
/* 539:    */ 
/* 540:    */ 
/* 541:735 */     int pNum = pos.length;int nNum = neg.length;int p = 0;int n = 0;
/* 542:736 */     double fstAccu = 0.0D;double sndAccu = pNum;
/* 543:737 */     double maxAccu = 0.0D;double minDistTo0 = 1.7976931348623157E+308D;
/* 544:740 */     for (; (n < nNum) && (pos[pOrder[0]] >= neg[nOrder[n]]); fstAccu += 1.0D) {
/* 545:740 */       n++;
/* 546:    */     }
/* 547:744 */     if (n >= nNum)
/* 548:    */     {
/* 549:745 */       this.m_Cutoff = ((neg[nOrder[(nNum - 1)]] + pos[pOrder[0]]) / 2.0D);
/* 550:    */       
/* 551:747 */       return;
/* 552:    */     }
/* 553:751 */     while ((p < pNum) && (n < nNum))
/* 554:    */     {
/* 555:    */       double split;
/* 556:753 */       if (pos[pOrder[p]] >= neg[nOrder[n]])
/* 557:    */       {
/* 558:754 */         fstAccu += 1.0D;
/* 559:755 */         double split = neg[nOrder[n]];
/* 560:756 */         n++;
/* 561:    */       }
/* 562:    */       else
/* 563:    */       {
/* 564:758 */         sndAccu -= 1.0D;
/* 565:759 */         split = pos[pOrder[p]];
/* 566:760 */         p++;
/* 567:    */       }
/* 568:763 */       if ((fstAccu + sndAccu > maxAccu) || ((fstAccu + sndAccu == maxAccu) && (Math.abs(split) < minDistTo0)))
/* 569:    */       {
/* 570:765 */         maxAccu = fstAccu + sndAccu;
/* 571:766 */         this.m_Cutoff = split;
/* 572:767 */         minDistTo0 = Math.abs(split);
/* 573:    */       }
/* 574:    */     }
/* 575:    */   }
/* 576:    */   
/* 577:    */   public Enumeration<Option> listOptions()
/* 578:    */   {
/* 579:779 */     Vector<Option> result = new Vector();
/* 580:    */     
/* 581:781 */     result.addElement(new Option("\tSet whether or not use empirical\n\tlog-odds cut-off instead of 0", "C", 0, "-C"));
/* 582:    */     
/* 583:    */ 
/* 584:784 */     result.addElement(new Option("\tSet the number of multiple runs \n\tneeded for searching the MLE.", "R", 1, "-R <numOfRuns>"));
/* 585:    */     
/* 586:    */ 
/* 587:787 */     result.addAll(Collections.list(super.listOptions()));
/* 588:    */     
/* 589:789 */     return result.elements();
/* 590:    */   }
/* 591:    */   
/* 592:    */   public void setOptions(String[] options)
/* 593:    */     throws Exception
/* 594:    */   {
/* 595:825 */     setUsingCutOff(Utils.getFlag('C', options));
/* 596:    */     
/* 597:827 */     String runString = Utils.getOption('R', options);
/* 598:828 */     if (runString.length() != 0) {
/* 599:829 */       setNumRuns(Integer.parseInt(runString));
/* 600:    */     } else {
/* 601:831 */       setNumRuns(1);
/* 602:    */     }
/* 603:834 */     super.setOptions(options);
/* 604:    */     
/* 605:836 */     Utils.checkForRemainingOptions(options);
/* 606:    */   }
/* 607:    */   
/* 608:    */   public String[] getOptions()
/* 609:    */   {
/* 610:847 */     Vector<String> result = new Vector();
/* 611:849 */     if (getUsingCutOff()) {
/* 612:850 */       result.add("-C");
/* 613:    */     }
/* 614:853 */     result.add("-R");
/* 615:854 */     result.add("" + getNumRuns());
/* 616:    */     
/* 617:856 */     Collections.addAll(result, super.getOptions());
/* 618:    */     
/* 619:858 */     return (String[])result.toArray(new String[result.size()]);
/* 620:    */   }
/* 621:    */   
/* 622:    */   public String numRunsTipText()
/* 623:    */   {
/* 624:868 */     return "The number of runs to perform.";
/* 625:    */   }
/* 626:    */   
/* 627:    */   public void setNumRuns(int numRuns)
/* 628:    */   {
/* 629:877 */     this.m_Run = numRuns;
/* 630:    */   }
/* 631:    */   
/* 632:    */   public int getNumRuns()
/* 633:    */   {
/* 634:886 */     return this.m_Run;
/* 635:    */   }
/* 636:    */   
/* 637:    */   public String usingCutOffTipText()
/* 638:    */   {
/* 639:896 */     return "Whether to use an empirical cutoff.";
/* 640:    */   }
/* 641:    */   
/* 642:    */   public void setUsingCutOff(boolean cutOff)
/* 643:    */   {
/* 644:905 */     this.m_UseEmpiricalCutOff = cutOff;
/* 645:    */   }
/* 646:    */   
/* 647:    */   public boolean getUsingCutOff()
/* 648:    */   {
/* 649:914 */     return this.m_UseEmpiricalCutOff;
/* 650:    */   }
/* 651:    */   
/* 652:    */   public String getRevision()
/* 653:    */   {
/* 654:924 */     return RevisionUtils.extract("$Revision: 10369 $");
/* 655:    */   }
/* 656:    */   
/* 657:    */   public static void main(String[] args)
/* 658:    */   {
/* 659:933 */     runClassifier(new TLD(), args);
/* 660:    */   }
/* 661:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.TLD
 * JD-Core Version:    0.7.0.1
 */