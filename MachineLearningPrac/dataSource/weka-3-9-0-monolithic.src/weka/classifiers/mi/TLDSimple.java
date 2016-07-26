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
/*  24:    */ public class TLDSimple
/*  25:    */   extends RandomizableClassifier
/*  26:    */   implements OptionHandler, MultiInstanceCapabilitiesHandler, TechnicalInformationHandler
/*  27:    */ {
/*  28:    */   static final long serialVersionUID = 9040995947243286591L;
/*  29:105 */   protected double[][] m_MeanP = (double[][])null;
/*  30:108 */   protected double[][] m_MeanN = (double[][])null;
/*  31:111 */   protected double[][] m_SumP = (double[][])null;
/*  32:114 */   protected double[][] m_SumN = (double[][])null;
/*  33:    */   protected double[] m_SgmSqP;
/*  34:    */   protected double[] m_SgmSqN;
/*  35:123 */   protected double[] m_ParamsP = null;
/*  36:126 */   protected double[] m_ParamsN = null;
/*  37:129 */   protected int m_Dimension = 0;
/*  38:132 */   protected double[] m_Class = null;
/*  39:135 */   protected int m_NumClasses = 2;
/*  40:138 */   public static double ZERO = 1.0E-012D;
/*  41:140 */   protected int m_Run = 1;
/*  42:    */   protected double m_Cutoff;
/*  43:144 */   protected boolean m_UseEmpiricalCutOff = false;
/*  44:    */   private double[] m_LkRatio;
/*  45:148 */   private Instances m_Attribute = null;
/*  46:    */   
/*  47:    */   public String globalInfo()
/*  48:    */   {
/*  49:157 */     return "A simpler version of TLD, mu random but sigma^2 fixed and estimated via data.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public TechnicalInformation getTechnicalInformation()
/*  53:    */   {
/*  54:174 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MASTERSTHESIS);
/*  55:175 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Xin Xu");
/*  56:176 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/*  57:177 */     result.setValue(TechnicalInformation.Field.TITLE, "Statistical learning in multiple instance problem");
/*  58:    */     
/*  59:179 */     result.setValue(TechnicalInformation.Field.SCHOOL, "University of Waikato");
/*  60:180 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Hamilton, NZ");
/*  61:181 */     result.setValue(TechnicalInformation.Field.NOTE, "0657.594");
/*  62:    */     
/*  63:183 */     return result;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public Capabilities getCapabilities()
/*  67:    */   {
/*  68:193 */     Capabilities result = super.getCapabilities();
/*  69:194 */     result.disableAll();
/*  70:    */     
/*  71:    */ 
/*  72:197 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  73:198 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  74:    */     
/*  75:    */ 
/*  76:201 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/*  77:202 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  78:    */     
/*  79:    */ 
/*  80:205 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/*  81:    */     
/*  82:207 */     return result;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Capabilities getMultiInstanceCapabilities()
/*  86:    */   {
/*  87:219 */     Capabilities result = super.getCapabilities();
/*  88:220 */     result.disableAll();
/*  89:    */     
/*  90:    */ 
/*  91:223 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  92:224 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  93:225 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  94:226 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  95:    */     
/*  96:    */ 
/*  97:229 */     result.disableAllClasses();
/*  98:230 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  99:    */     
/* 100:232 */     return result;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void buildClassifier(Instances exs)
/* 104:    */     throws Exception
/* 105:    */   {
/* 106:243 */     getCapabilities().testWithFail(exs);
/* 107:    */     
/* 108:    */ 
/* 109:246 */     exs = new Instances(exs);
/* 110:247 */     exs.deleteWithMissingClass();
/* 111:    */     
/* 112:249 */     int numegs = exs.numInstances();
/* 113:250 */     this.m_Dimension = exs.attribute(1).relation().numAttributes();
/* 114:251 */     this.m_Attribute = exs.attribute(1).relation().stringFreeStructure();
/* 115:252 */     Instances pos = new Instances(exs, 0);Instances neg = new Instances(exs, 0);
/* 116:255 */     for (int u = 0; u < numegs; u++)
/* 117:    */     {
/* 118:256 */       Instance example = exs.instance(u);
/* 119:257 */       if (example.classValue() == 1.0D) {
/* 120:258 */         pos.add(example);
/* 121:    */       } else {
/* 122:260 */         neg.add(example);
/* 123:    */       }
/* 124:    */     }
/* 125:263 */     int pnum = pos.numInstances();int nnum = neg.numInstances();
/* 126:    */     
/* 127:    */ 
/* 128:266 */     this.m_MeanP = new double[pnum][this.m_Dimension];
/* 129:267 */     this.m_SumP = new double[pnum][this.m_Dimension];
/* 130:268 */     this.m_MeanN = new double[nnum][this.m_Dimension];
/* 131:269 */     this.m_SumN = new double[nnum][this.m_Dimension];
/* 132:    */     
/* 133:271 */     this.m_ParamsP = new double[2 * this.m_Dimension];
/* 134:272 */     this.m_ParamsN = new double[2 * this.m_Dimension];
/* 135:    */     
/* 136:274 */     this.m_SgmSqP = new double[this.m_Dimension];
/* 137:275 */     this.m_SgmSqN = new double[this.m_Dimension];
/* 138:    */     
/* 139:277 */     double[][] varP = new double[pnum][this.m_Dimension];double[][] varN = new double[nnum][this.m_Dimension];
/* 140:    */     
/* 141:279 */     double[] effNumExP = new double[this.m_Dimension];double[] effNumExN = new double[this.m_Dimension];
/* 142:    */     
/* 143:281 */     double[] pMM = new double[this.m_Dimension];double[] nMM = new double[this.m_Dimension];double[] pVM = new double[this.m_Dimension];double[] nVM = new double[this.m_Dimension];
/* 144:    */     
/* 145:283 */     double[] numOneInsExsP = new double[this.m_Dimension];double[] numOneInsExsN = new double[this.m_Dimension];
/* 146:    */     
/* 147:285 */     double[] pInvN = new double[this.m_Dimension];double[] nInvN = new double[this.m_Dimension];
/* 148:288 */     for (int v = 0; v < pnum; v++)
/* 149:    */     {
/* 150:290 */       Instances pxi = pos.instance(v).relationalValue(1);
/* 151:291 */       for (int k = 0; k < pxi.numAttributes(); k++)
/* 152:    */       {
/* 153:292 */         this.m_MeanP[v][k] = pxi.meanOrMode(k);
/* 154:293 */         varP[v][k] = pxi.variance(k);
/* 155:    */       }
/* 156:296 */       int w = 0;
/* 157:296 */       for (int t = 0; w < this.m_Dimension; t++)
/* 158:    */       {
/* 159:299 */         if (varP[v][w] <= 0.0D) {
/* 160:300 */           varP[v][w] = 0.0D;
/* 161:    */         }
/* 162:302 */         if (!Double.isNaN(this.m_MeanP[v][w]))
/* 163:    */         {
/* 164:304 */           for (int u = 0; u < pxi.numInstances(); u++) {
/* 165:305 */             if (!pxi.instance(u).isMissing(t)) {
/* 166:306 */               this.m_SumP[v][w] += pxi.instance(u).weight();
/* 167:    */             }
/* 168:    */           }
/* 169:310 */           pMM[w] += this.m_MeanP[v][w];
/* 170:311 */           pVM[w] += this.m_MeanP[v][w] * this.m_MeanP[v][w];
/* 171:312 */           if ((this.m_SumP[v][w] > 1.0D) && (varP[v][w] > ZERO))
/* 172:    */           {
/* 173:314 */             this.m_SgmSqP[w] += varP[v][w] * (this.m_SumP[v][w] - 1.0D) / this.m_SumP[v][w];
/* 174:    */             
/* 175:    */ 
/* 176:317 */             effNumExP[w] += 1.0D;
/* 177:318 */             pInvN[w] += 1.0D / this.m_SumP[v][w];
/* 178:    */           }
/* 179:    */           else
/* 180:    */           {
/* 181:321 */             numOneInsExsP[w] += 1.0D;
/* 182:    */           }
/* 183:    */         }
/* 184:296 */         w++;
/* 185:    */       }
/* 186:    */     }
/* 187:328 */     for (int v = 0; v < nnum; v++)
/* 188:    */     {
/* 189:330 */       Instances nxi = neg.instance(v).relationalValue(1);
/* 190:331 */       for (int k = 0; k < nxi.numAttributes(); k++)
/* 191:    */       {
/* 192:332 */         this.m_MeanN[v][k] = nxi.meanOrMode(k);
/* 193:333 */         varN[v][k] = nxi.variance(k);
/* 194:    */       }
/* 195:337 */       int w = 0;
/* 196:337 */       for (int t = 0; w < this.m_Dimension; t++)
/* 197:    */       {
/* 198:341 */         if (varN[v][w] <= 0.0D) {
/* 199:342 */           varN[v][w] = 0.0D;
/* 200:    */         }
/* 201:344 */         if (!Double.isNaN(this.m_MeanN[v][w]))
/* 202:    */         {
/* 203:345 */           for (int u = 0; u < nxi.numInstances(); u++) {
/* 204:346 */             if (!nxi.instance(u).isMissing(t)) {
/* 205:347 */               this.m_SumN[v][w] += nxi.instance(u).weight();
/* 206:    */             }
/* 207:    */           }
/* 208:351 */           nMM[w] += this.m_MeanN[v][w];
/* 209:352 */           nVM[w] += this.m_MeanN[v][w] * this.m_MeanN[v][w];
/* 210:353 */           if ((this.m_SumN[v][w] > 1.0D) && (varN[v][w] > ZERO))
/* 211:    */           {
/* 212:354 */             this.m_SgmSqN[w] += varN[v][w] * (this.m_SumN[v][w] - 1.0D) / this.m_SumN[v][w];
/* 213:    */             
/* 214:356 */             effNumExN[w] += 1.0D;
/* 215:357 */             nInvN[w] += 1.0D / this.m_SumN[v][w];
/* 216:    */           }
/* 217:    */           else
/* 218:    */           {
/* 219:360 */             numOneInsExsN[w] += 1.0D;
/* 220:    */           }
/* 221:    */         }
/* 222:337 */         w++;
/* 223:    */       }
/* 224:    */     }
/* 225:371 */     for (int u = 0; u < this.m_Dimension; u++)
/* 226:    */     {
/* 227:374 */       if (this.m_SgmSqP[u] != 0.0D) {
/* 228:375 */         this.m_SgmSqP[u] /= (effNumExP[u] - pInvN[u]);
/* 229:    */       } else {
/* 230:377 */         this.m_SgmSqP[u] = 0.0D;
/* 231:    */       }
/* 232:379 */       if (this.m_SgmSqN[u] != 0.0D) {
/* 233:380 */         this.m_SgmSqN[u] /= (effNumExN[u] - nInvN[u]);
/* 234:    */       } else {
/* 235:382 */         this.m_SgmSqN[u] = 0.0D;
/* 236:    */       }
/* 237:387 */       effNumExP[u] += numOneInsExsP[u];
/* 238:388 */       effNumExN[u] += numOneInsExsN[u];
/* 239:389 */       pMM[u] /= effNumExP[u];
/* 240:390 */       nMM[u] /= effNumExN[u];
/* 241:391 */       pVM[u] = (pVM[u] / (effNumExP[u] - 1.0D) - pMM[u] * pMM[u] * effNumExP[u] / (effNumExP[u] - 1.0D));
/* 242:    */       
/* 243:393 */       nVM[u] = (nVM[u] / (effNumExN[u] - 1.0D) - nMM[u] * nMM[u] * effNumExN[u] / (effNumExN[u] - 1.0D));
/* 244:    */     }
/* 245:398 */     double[][] bounds = new double[2][2];
/* 246:399 */     double[] pThisParam = new double[2];double[] nThisParam = new double[2];
/* 247:    */     
/* 248:    */ 
/* 249:    */ 
/* 250:403 */     Random whichEx = new Random(this.m_Seed);
/* 251:406 */     for (int x = 0; x < this.m_Dimension; x++)
/* 252:    */     {
/* 253:410 */       pThisParam[0] = pVM[x];
/* 254:411 */       if (pThisParam[0] <= ZERO) {
/* 255:412 */         pThisParam[0] = 1.0D;
/* 256:    */       }
/* 257:414 */       pThisParam[1] = pMM[x];
/* 258:    */       
/* 259:    */ 
/* 260:417 */       nThisParam[0] = nVM[x];
/* 261:418 */       if (nThisParam[0] <= ZERO) {
/* 262:419 */         nThisParam[0] = 1.0D;
/* 263:    */       }
/* 264:421 */       nThisParam[1] = nMM[x];
/* 265:    */       
/* 266:    */ 
/* 267:424 */       bounds[0][0] = ZERO;
/* 268:425 */       bounds[0][1] = (0.0D / 0.0D);
/* 269:426 */       bounds[1][0] = (0.0D / 0.0D);
/* 270:427 */       bounds[1][1] = (0.0D / 0.0D);
/* 271:    */       
/* 272:429 */       double pminVal = 1.7976931348623157E+308D;double nminVal = 1.7976931348623157E+308D;
/* 273:430 */       TLDSimple_Optm pOp = null;TLDSimple_Optm nOp = null;
/* 274:431 */       boolean isRunValid = true;
/* 275:432 */       double[] sumP = new double[pnum];double[] meanP = new double[pnum];
/* 276:433 */       double[] sumN = new double[nnum];double[] meanN = new double[nnum];
/* 277:436 */       for (int p = 0; p < pnum; p++)
/* 278:    */       {
/* 279:437 */         sumP[p] = this.m_SumP[p][x];
/* 280:438 */         meanP[p] = this.m_MeanP[p][x];
/* 281:    */       }
/* 282:440 */       for (int q = 0; q < nnum; q++)
/* 283:    */       {
/* 284:441 */         sumN[q] = this.m_SumN[q][x];
/* 285:442 */         meanN[q] = this.m_MeanN[q][x];
/* 286:    */       }
/* 287:445 */       for (int y = 0; y < this.m_Run; y++)
/* 288:    */       {
/* 289:448 */         pOp = new TLDSimple_Optm();
/* 290:449 */         pOp.setNum(sumP);
/* 291:450 */         pOp.setSgmSq(this.m_SgmSqP[x]);
/* 292:451 */         if (getDebug()) {
/* 293:452 */           System.out.println("m_SgmSqP[" + x + "]= " + this.m_SgmSqP[x]);
/* 294:    */         }
/* 295:454 */         pOp.setXBar(meanP);
/* 296:    */         
/* 297:456 */         pThisParam = pOp.findArgmin(pThisParam, bounds);
/* 298:457 */         while (pThisParam == null)
/* 299:    */         {
/* 300:458 */           pThisParam = pOp.getVarbValues();
/* 301:459 */           if (getDebug()) {
/* 302:460 */             System.out.println("!!! 200 iterations finished, not enough!");
/* 303:    */           }
/* 304:462 */           pThisParam = pOp.findArgmin(pThisParam, bounds);
/* 305:    */         }
/* 306:465 */         double thisMin = pOp.getMinFunction();
/* 307:466 */         if ((!Double.isNaN(thisMin)) && (thisMin < pminVal))
/* 308:    */         {
/* 309:467 */           pminVal = thisMin;
/* 310:468 */           for (int z = 0; z < 2; z++) {
/* 311:469 */             this.m_ParamsP[(2 * x + z)] = pThisParam[z];
/* 312:    */           }
/* 313:    */         }
/* 314:473 */         if (Double.isNaN(thisMin))
/* 315:    */         {
/* 316:474 */           pThisParam = new double[2];
/* 317:475 */           isRunValid = false;
/* 318:    */         }
/* 319:477 */         if (!isRunValid)
/* 320:    */         {
/* 321:478 */           y--;
/* 322:479 */           isRunValid = true;
/* 323:    */         }
/* 324:483 */         int pone = whichEx.nextInt(pnum);
/* 325:486 */         while (Double.isNaN(this.m_MeanP[pone][x])) {
/* 326:487 */           pone = whichEx.nextInt(pnum);
/* 327:    */         }
/* 328:490 */         double m = this.m_MeanP[pone][x];
/* 329:491 */         double w = (m - pThisParam[1]) * (m - pThisParam[1]);
/* 330:492 */         pThisParam[0] = w;
/* 331:493 */         pThisParam[1] = m;
/* 332:    */       }
/* 333:496 */       for (int y = 0; y < this.m_Run; y++)
/* 334:    */       {
/* 335:499 */         nOp = new TLDSimple_Optm();
/* 336:500 */         nOp.setNum(sumN);
/* 337:501 */         nOp.setSgmSq(this.m_SgmSqN[x]);
/* 338:502 */         if (getDebug()) {
/* 339:503 */           System.out.println(this.m_SgmSqN[x]);
/* 340:    */         }
/* 341:505 */         nOp.setXBar(meanN);
/* 342:    */         
/* 343:507 */         nThisParam = nOp.findArgmin(nThisParam, bounds);
/* 344:509 */         while (nThisParam == null)
/* 345:    */         {
/* 346:510 */           nThisParam = nOp.getVarbValues();
/* 347:511 */           if (getDebug()) {
/* 348:512 */             System.out.println("!!! 200 iterations finished, not enough!");
/* 349:    */           }
/* 350:514 */           nThisParam = nOp.findArgmin(nThisParam, bounds);
/* 351:    */         }
/* 352:517 */         double thisMin = nOp.getMinFunction();
/* 353:518 */         if ((!Double.isNaN(thisMin)) && (thisMin < nminVal))
/* 354:    */         {
/* 355:519 */           nminVal = thisMin;
/* 356:520 */           for (int z = 0; z < 2; z++) {
/* 357:521 */             this.m_ParamsN[(2 * x + z)] = nThisParam[z];
/* 358:    */           }
/* 359:    */         }
/* 360:525 */         if (Double.isNaN(thisMin))
/* 361:    */         {
/* 362:526 */           nThisParam = new double[2];
/* 363:527 */           isRunValid = false;
/* 364:    */         }
/* 365:530 */         if (!isRunValid)
/* 366:    */         {
/* 367:531 */           y--;
/* 368:532 */           isRunValid = true;
/* 369:    */         }
/* 370:536 */         int none = whichEx.nextInt(nnum);
/* 371:539 */         while (Double.isNaN(this.m_MeanN[none][x])) {
/* 372:540 */           none = whichEx.nextInt(nnum);
/* 373:    */         }
/* 374:543 */         double m = this.m_MeanN[none][x];
/* 375:544 */         double w = (m - nThisParam[1]) * (m - nThisParam[1]);
/* 376:545 */         nThisParam[0] = w;
/* 377:546 */         nThisParam[1] = m;
/* 378:    */       }
/* 379:    */     }
/* 380:550 */     this.m_LkRatio = new double[this.m_Dimension];
/* 381:552 */     if (this.m_UseEmpiricalCutOff)
/* 382:    */     {
/* 383:554 */       double[] pLogOdds = new double[pnum];double[] nLogOdds = new double[nnum];
/* 384:555 */       for (int p = 0; p < pnum; p++) {
/* 385:556 */         pLogOdds[p] = likelihoodRatio(this.m_SumP[p], this.m_MeanP[p]);
/* 386:    */       }
/* 387:559 */       for (int q = 0; q < nnum; q++) {
/* 388:560 */         nLogOdds[q] = likelihoodRatio(this.m_SumN[q], this.m_MeanN[q]);
/* 389:    */       }
/* 390:564 */       findCutOff(pLogOdds, nLogOdds);
/* 391:    */     }
/* 392:    */     else
/* 393:    */     {
/* 394:566 */       this.m_Cutoff = (-Math.log(pnum / nnum));
/* 395:    */     }
/* 396:583 */     if (getDebug()) {
/* 397:584 */       System.err.println("\n\n???Cut-off=" + this.m_Cutoff);
/* 398:    */     }
/* 399:    */   }
/* 400:    */   
/* 401:    */   public double classifyInstance(Instance ex)
/* 402:    */     throws Exception
/* 403:    */   {
/* 404:597 */     Instances exi = ex.relationalValue(1);
/* 405:598 */     double[] n = new double[this.m_Dimension];
/* 406:599 */     double[] xBar = new double[this.m_Dimension];
/* 407:600 */     for (int i = 0; i < exi.numAttributes(); i++) {
/* 408:601 */       xBar[i] = exi.meanOrMode(i);
/* 409:    */     }
/* 410:604 */     int w = 0;
/* 411:604 */     for (int t = 0; w < this.m_Dimension; t++)
/* 412:    */     {
/* 413:607 */       for (int u = 0; u < exi.numInstances(); u++) {
/* 414:608 */         if (!exi.instance(u).isMissing(t)) {
/* 415:609 */           n[w] += exi.instance(u).weight();
/* 416:    */         }
/* 417:    */       }
/* 418:604 */       w++;
/* 419:    */     }
/* 420:614 */     double logOdds = likelihoodRatio(n, xBar);
/* 421:615 */     return logOdds > this.m_Cutoff ? 1.0D : 0.0D;
/* 422:    */   }
/* 423:    */   
/* 424:    */   public double[] distributionForInstance(Instance ex)
/* 425:    */     throws Exception
/* 426:    */   {
/* 427:628 */     double[] distribution = new double[2];
/* 428:629 */     Instances exi = ex.relationalValue(1);
/* 429:630 */     double[] n = new double[this.m_Dimension];
/* 430:631 */     double[] xBar = new double[this.m_Dimension];
/* 431:632 */     for (int i = 0; i < exi.numAttributes(); i++) {
/* 432:633 */       xBar[i] = exi.meanOrMode(i);
/* 433:    */     }
/* 434:636 */     int w = 0;
/* 435:636 */     for (int t = 0; w < this.m_Dimension; t++)
/* 436:    */     {
/* 437:637 */       for (int u = 0; u < exi.numInstances(); u++) {
/* 438:638 */         if (!exi.instance(u).isMissing(t)) {
/* 439:639 */           n[w] += exi.instance(u).weight();
/* 440:    */         }
/* 441:    */       }
/* 442:636 */       w++;
/* 443:    */     }
/* 444:644 */     double logOdds = likelihoodRatio(n, xBar);
/* 445:    */     
/* 446:    */ 
/* 447:    */ 
/* 448:    */ 
/* 449:649 */     distribution[0] = (1.0D / (1.0D + Math.exp(logOdds)));
/* 450:    */     
/* 451:651 */     distribution[1] = (1.0D - distribution[0]);
/* 452:    */     
/* 453:653 */     return distribution;
/* 454:    */   }
/* 455:    */   
/* 456:    */   private double likelihoodRatio(double[] n, double[] xBar)
/* 457:    */   {
/* 458:660 */     double LLP = 0.0D;double LLN = 0.0D;
/* 459:662 */     for (int x = 0; x < this.m_Dimension; x++) {
/* 460:663 */       if (!Double.isNaN(xBar[x]))
/* 461:    */       {
/* 462:671 */         double w = this.m_ParamsP[(2 * x)];double m = this.m_ParamsP[(2 * x + 1)];
/* 463:672 */         double llp = Math.log(w * n[x] + this.m_SgmSqP[x]) + n[x] * (m - xBar[x]) * (m - xBar[x]) / (w * n[x] + this.m_SgmSqP[x]);
/* 464:    */         
/* 465:674 */         LLP -= llp;
/* 466:    */         
/* 467:    */ 
/* 468:677 */         w = this.m_ParamsN[(2 * x)];
/* 469:678 */         m = this.m_ParamsN[(2 * x + 1)];
/* 470:679 */         double lln = Math.log(w * n[x] + this.m_SgmSqN[x]) + n[x] * (m - xBar[x]) * (m - xBar[x]) / (w * n[x] + this.m_SgmSqN[x]);
/* 471:    */         
/* 472:681 */         LLN -= lln;
/* 473:    */         
/* 474:683 */         this.m_LkRatio[x] += llp - lln;
/* 475:    */       }
/* 476:    */     }
/* 477:686 */     return LLP - LLN / this.m_Dimension;
/* 478:    */   }
/* 479:    */   
/* 480:    */   private void findCutOff(double[] pos, double[] neg)
/* 481:    */   {
/* 482:690 */     int[] pOrder = Utils.sort(pos);int[] nOrder = Utils.sort(neg);
/* 483:    */     
/* 484:    */ 
/* 485:    */ 
/* 486:    */ 
/* 487:    */ 
/* 488:    */ 
/* 489:    */ 
/* 490:    */ 
/* 491:699 */     int pNum = pos.length;int nNum = neg.length;int p = 0;int n = 0;
/* 492:700 */     double fstAccu = 0.0D;double sndAccu = pNum;
/* 493:701 */     double maxAccu = 0.0D;double minDistTo0 = 1.7976931348623157E+308D;
/* 494:704 */     for (; (n < nNum) && (pos[pOrder[0]] >= neg[nOrder[n]]); fstAccu += 1.0D) {
/* 495:704 */       n++;
/* 496:    */     }
/* 497:708 */     if (n >= nNum)
/* 498:    */     {
/* 499:709 */       this.m_Cutoff = ((neg[nOrder[(nNum - 1)]] + pos[pOrder[0]]) / 2.0D);
/* 500:    */       
/* 501:711 */       return;
/* 502:    */     }
/* 503:715 */     while ((p < pNum) && (n < nNum))
/* 504:    */     {
/* 505:    */       double split;
/* 506:717 */       if (pos[pOrder[p]] >= neg[nOrder[n]])
/* 507:    */       {
/* 508:718 */         fstAccu += 1.0D;
/* 509:719 */         double split = neg[nOrder[n]];
/* 510:720 */         n++;
/* 511:    */       }
/* 512:    */       else
/* 513:    */       {
/* 514:722 */         sndAccu -= 1.0D;
/* 515:723 */         split = pos[pOrder[p]];
/* 516:724 */         p++;
/* 517:    */       }
/* 518:737 */       if ((fstAccu + sndAccu > maxAccu) || ((fstAccu + sndAccu == maxAccu) && (Math.abs(split) < minDistTo0)))
/* 519:    */       {
/* 520:739 */         maxAccu = fstAccu + sndAccu;
/* 521:740 */         this.m_Cutoff = split;
/* 522:741 */         minDistTo0 = Math.abs(split);
/* 523:    */       }
/* 524:    */     }
/* 525:    */   }
/* 526:    */   
/* 527:    */   public Enumeration<Option> listOptions()
/* 528:    */   {
/* 529:754 */     Vector<Option> result = new Vector();
/* 530:    */     
/* 531:756 */     result.addElement(new Option("\tSet whether or not use empirical\n\tlog-odds cut-off instead of 0", "C", 0, "-C"));
/* 532:    */     
/* 533:    */ 
/* 534:759 */     result.addElement(new Option("\tSet the number of multiple runs \n\tneeded for searching the MLE.", "R", 1, "-R <numOfRuns>"));
/* 535:    */     
/* 536:    */ 
/* 537:762 */     result.addAll(Collections.list(super.listOptions()));
/* 538:    */     
/* 539:764 */     return result.elements();
/* 540:    */   }
/* 541:    */   
/* 542:    */   public void setOptions(String[] options)
/* 543:    */     throws Exception
/* 544:    */   {
/* 545:800 */     setUsingCutOff(Utils.getFlag('C', options));
/* 546:    */     
/* 547:802 */     String runString = Utils.getOption('R', options);
/* 548:803 */     if (runString.length() != 0) {
/* 549:804 */       setNumRuns(Integer.parseInt(runString));
/* 550:    */     } else {
/* 551:806 */       setNumRuns(1);
/* 552:    */     }
/* 553:809 */     super.setOptions(options);
/* 554:    */     
/* 555:811 */     Utils.checkForRemainingOptions(options);
/* 556:    */   }
/* 557:    */   
/* 558:    */   public String[] getOptions()
/* 559:    */   {
/* 560:822 */     Vector<String> result = new Vector();
/* 561:824 */     if (getUsingCutOff()) {
/* 562:825 */       result.add("-C");
/* 563:    */     }
/* 564:828 */     result.add("-R");
/* 565:829 */     result.add("" + getNumRuns());
/* 566:    */     
/* 567:831 */     Collections.addAll(result, super.getOptions());
/* 568:    */     
/* 569:833 */     return (String[])result.toArray(new String[result.size()]);
/* 570:    */   }
/* 571:    */   
/* 572:    */   public String numRunsTipText()
/* 573:    */   {
/* 574:843 */     return "The number of runs to perform.";
/* 575:    */   }
/* 576:    */   
/* 577:    */   public void setNumRuns(int numRuns)
/* 578:    */   {
/* 579:852 */     this.m_Run = numRuns;
/* 580:    */   }
/* 581:    */   
/* 582:    */   public int getNumRuns()
/* 583:    */   {
/* 584:861 */     return this.m_Run;
/* 585:    */   }
/* 586:    */   
/* 587:    */   public String usingCutOffTipText()
/* 588:    */   {
/* 589:871 */     return "Whether to use an empirical cutoff.";
/* 590:    */   }
/* 591:    */   
/* 592:    */   public void setUsingCutOff(boolean cutOff)
/* 593:    */   {
/* 594:880 */     this.m_UseEmpiricalCutOff = cutOff;
/* 595:    */   }
/* 596:    */   
/* 597:    */   public boolean getUsingCutOff()
/* 598:    */   {
/* 599:889 */     return this.m_UseEmpiricalCutOff;
/* 600:    */   }
/* 601:    */   
/* 602:    */   public String toString()
/* 603:    */   {
/* 604:899 */     StringBuffer text = new StringBuffer("\n\nTLDSimple:\n");
/* 605:    */     
/* 606:901 */     int x = 0;
/* 607:901 */     for (int y = 0; x < this.m_Dimension; y++)
/* 608:    */     {
/* 609:904 */       double sgm = this.m_SgmSqP[x];
/* 610:905 */       double w = this.m_ParamsP[(2 * x)];
/* 611:906 */       double m = this.m_ParamsP[(2 * x + 1)];
/* 612:907 */       text.append("\n" + this.m_Attribute.attribute(y).name() + "\nPositive: " + "sigma^2=" + sgm + ", w=" + w + ", m=" + m + "\n");
/* 613:    */       
/* 614:909 */       sgm = this.m_SgmSqN[x];
/* 615:910 */       w = this.m_ParamsN[(2 * x)];
/* 616:911 */       m = this.m_ParamsN[(2 * x + 1)];
/* 617:912 */       text.append("Negative: sigma^2=" + sgm + ", w=" + w + ", m=" + m + "\n");x++;
/* 618:    */     }
/* 619:916 */     return text.toString();
/* 620:    */   }
/* 621:    */   
/* 622:    */   public String getRevision()
/* 623:    */   {
/* 624:926 */     return RevisionUtils.extract("$Revision: 10369 $");
/* 625:    */   }
/* 626:    */   
/* 627:    */   public static void main(String[] args)
/* 628:    */   {
/* 629:935 */     runClassifier(new TLDSimple(), args);
/* 630:    */   }
/* 631:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.TLDSimple
 * JD-Core Version:    0.7.0.1
 */