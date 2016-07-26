/*   1:    */ package weka.classifiers.rules;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public class RuleStats
/*  15:    */   implements Serializable, RevisionHandler
/*  16:    */ {
/*  17:    */   static final long serialVersionUID = -5708153367675298624L;
/*  18:    */   private Instances m_Data;
/*  19:    */   private ArrayList<Rule> m_Ruleset;
/*  20:    */   private ArrayList<double[]> m_SimpleStats;
/*  21:    */   private ArrayList<Instances[]> m_Filtered;
/*  22:    */   private double m_Total;
/*  23: 73 */   private static double REDUNDANCY_FACTOR = 0.5D;
/*  24: 76 */   private double MDL_THEORY_WEIGHT = 1.0D;
/*  25:    */   private ArrayList<double[]> m_Distributions;
/*  26:    */   
/*  27:    */   public RuleStats()
/*  28:    */   {
/*  29: 83 */     this.m_Data = null;
/*  30: 84 */     this.m_Ruleset = null;
/*  31: 85 */     this.m_SimpleStats = null;
/*  32: 86 */     this.m_Filtered = null;
/*  33: 87 */     this.m_Distributions = null;
/*  34: 88 */     this.m_Total = -1.0D;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public RuleStats(Instances data, ArrayList<Rule> rules)
/*  38:    */   {
/*  39: 98 */     this();
/*  40: 99 */     this.m_Data = data;
/*  41:100 */     this.m_Ruleset = rules;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void cleanUp()
/*  45:    */   {
/*  46:107 */     this.m_Data = null;
/*  47:108 */     this.m_Filtered = null;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setNumAllConds(double total)
/*  51:    */   {
/*  52:119 */     if (total < 0.0D) {
/*  53:120 */       this.m_Total = numAllConditions(this.m_Data);
/*  54:    */     } else {
/*  55:122 */       this.m_Total = total;
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setData(Instances data)
/*  60:    */   {
/*  61:132 */     this.m_Data = data;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public Instances getData()
/*  65:    */   {
/*  66:141 */     return this.m_Data;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void setRuleset(ArrayList<Rule> rules)
/*  70:    */   {
/*  71:150 */     this.m_Ruleset = rules;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public ArrayList<Rule> getRuleset()
/*  75:    */   {
/*  76:159 */     return this.m_Ruleset;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public int getRulesetSize()
/*  80:    */   {
/*  81:168 */     return this.m_Ruleset.size();
/*  82:    */   }
/*  83:    */   
/*  84:    */   public double[] getSimpleStats(int index)
/*  85:    */   {
/*  86:180 */     if ((this.m_SimpleStats != null) && (index < this.m_SimpleStats.size())) {
/*  87:181 */       return (double[])this.m_SimpleStats.get(index);
/*  88:    */     }
/*  89:184 */     return null;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public Instances[] getFiltered(int index)
/*  93:    */   {
/*  94:195 */     if ((this.m_Filtered != null) && (index < this.m_Filtered.size())) {
/*  95:196 */       return (Instances[])this.m_Filtered.get(index);
/*  96:    */     }
/*  97:199 */     return null;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public double[] getDistributions(int index)
/* 101:    */   {
/* 102:210 */     if ((this.m_Distributions != null) && (index < this.m_Distributions.size())) {
/* 103:211 */       return (double[])this.m_Distributions.get(index);
/* 104:    */     }
/* 105:214 */     return null;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void setMDLTheoryWeight(double weight)
/* 109:    */   {
/* 110:223 */     this.MDL_THEORY_WEIGHT = weight;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public static double numAllConditions(Instances data)
/* 114:    */   {
/* 115:236 */     double total = 0.0D;
/* 116:237 */     Enumeration<Attribute> attEnum = data.enumerateAttributes();
/* 117:238 */     while (attEnum.hasMoreElements())
/* 118:    */     {
/* 119:239 */       Attribute att = (Attribute)attEnum.nextElement();
/* 120:240 */       if (att.isNominal()) {
/* 121:241 */         total += att.numValues();
/* 122:    */       } else {
/* 123:243 */         total += 2.0D * data.numDistinctValues(att);
/* 124:    */       }
/* 125:    */     }
/* 126:246 */     return total;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void countData()
/* 130:    */   {
/* 131:254 */     if ((this.m_Filtered != null) || (this.m_Ruleset == null) || (this.m_Data == null)) {
/* 132:255 */       return;
/* 133:    */     }
/* 134:258 */     int size = this.m_Ruleset.size();
/* 135:259 */     this.m_Filtered = new ArrayList(size);
/* 136:260 */     this.m_SimpleStats = new ArrayList(size);
/* 137:261 */     this.m_Distributions = new ArrayList(size);
/* 138:262 */     Instances data = new Instances(this.m_Data);
/* 139:264 */     for (int i = 0; i < size; i++)
/* 140:    */     {
/* 141:265 */       double[] stats = new double[6];
/* 142:266 */       double[] classCounts = new double[this.m_Data.classAttribute().numValues()];
/* 143:267 */       Instances[] filtered = computeSimpleStats(i, data, stats, classCounts);
/* 144:268 */       this.m_Filtered.add(filtered);
/* 145:269 */       this.m_SimpleStats.add(stats);
/* 146:270 */       this.m_Distributions.add(classCounts);
/* 147:271 */       data = filtered[1];
/* 148:    */     }
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void countData(int index, Instances uncovered, double[][] prevRuleStats)
/* 152:    */   {
/* 153:288 */     if ((this.m_Filtered != null) || (this.m_Ruleset == null)) {
/* 154:289 */       return;
/* 155:    */     }
/* 156:292 */     int size = this.m_Ruleset.size();
/* 157:293 */     this.m_Filtered = new ArrayList(size);
/* 158:294 */     this.m_SimpleStats = new ArrayList(size);
/* 159:295 */     Instances[] data = new Instances[2];
/* 160:296 */     data[1] = uncovered;
/* 161:298 */     for (int i = 0; i < index; i++)
/* 162:    */     {
/* 163:299 */       this.m_SimpleStats.add(prevRuleStats[i]);
/* 164:300 */       if (i + 1 == index) {
/* 165:301 */         this.m_Filtered.add(data);
/* 166:    */       } else {
/* 167:303 */         this.m_Filtered.add(new Instances[0]);
/* 168:    */       }
/* 169:    */     }
/* 170:307 */     for (int j = index; j < size; j++)
/* 171:    */     {
/* 172:308 */       double[] stats = new double[6];
/* 173:309 */       Instances[] filtered = computeSimpleStats(j, data[1], stats, null);
/* 174:310 */       this.m_Filtered.add(filtered);
/* 175:311 */       this.m_SimpleStats.add(stats);
/* 176:312 */       data = filtered;
/* 177:    */     }
/* 178:    */   }
/* 179:    */   
/* 180:    */   private Instances[] computeSimpleStats(int index, Instances insts, double[] stats, double[] dist)
/* 181:    */   {
/* 182:331 */     Rule rule = (Rule)this.m_Ruleset.get(index);
/* 183:    */     
/* 184:333 */     Instances[] data = new Instances[2];
/* 185:334 */     data[0] = new Instances(insts, insts.numInstances());
/* 186:335 */     data[1] = new Instances(insts, insts.numInstances());
/* 187:337 */     for (int i = 0; i < insts.numInstances(); i++)
/* 188:    */     {
/* 189:338 */       Instance datum = insts.instance(i);
/* 190:339 */       double weight = datum.weight();
/* 191:340 */       if (rule.covers(datum))
/* 192:    */       {
/* 193:341 */         data[0].add(datum);
/* 194:342 */         stats[0] += weight;
/* 195:343 */         if ((int)datum.classValue() == (int)rule.getConsequent()) {
/* 196:344 */           stats[2] += weight;
/* 197:    */         } else {
/* 198:346 */           stats[4] += weight;
/* 199:    */         }
/* 200:348 */         if (dist != null) {
/* 201:349 */           dist[((int)datum.classValue())] += weight;
/* 202:    */         }
/* 203:    */       }
/* 204:    */       else
/* 205:    */       {
/* 206:352 */         data[1].add(datum);
/* 207:353 */         stats[1] += weight;
/* 208:354 */         if ((int)datum.classValue() != (int)rule.getConsequent()) {
/* 209:355 */           stats[3] += weight;
/* 210:    */         } else {
/* 211:357 */           stats[5] += weight;
/* 212:    */         }
/* 213:    */       }
/* 214:    */     }
/* 215:362 */     return data;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public void addAndUpdate(Rule lastRule)
/* 219:    */   {
/* 220:371 */     if (this.m_Ruleset == null) {
/* 221:372 */       this.m_Ruleset = new ArrayList();
/* 222:    */     }
/* 223:374 */     this.m_Ruleset.add(lastRule);
/* 224:    */     
/* 225:376 */     Instances data = this.m_Filtered == null ? this.m_Data : ((Instances[])this.m_Filtered.get(this.m_Filtered.size() - 1))[1];
/* 226:    */     
/* 227:378 */     double[] stats = new double[6];
/* 228:379 */     double[] classCounts = new double[this.m_Data.classAttribute().numValues()];
/* 229:380 */     Instances[] filtered = computeSimpleStats(this.m_Ruleset.size() - 1, data, stats, classCounts);
/* 230:383 */     if (this.m_Filtered == null) {
/* 231:384 */       this.m_Filtered = new ArrayList();
/* 232:    */     }
/* 233:386 */     this.m_Filtered.add(filtered);
/* 234:388 */     if (this.m_SimpleStats == null) {
/* 235:389 */       this.m_SimpleStats = new ArrayList();
/* 236:    */     }
/* 237:391 */     this.m_SimpleStats.add(stats);
/* 238:393 */     if (this.m_Distributions == null) {
/* 239:394 */       this.m_Distributions = new ArrayList();
/* 240:    */     }
/* 241:396 */     this.m_Distributions.add(classCounts);
/* 242:    */   }
/* 243:    */   
/* 244:    */   public static double subsetDL(double t, double k, double p)
/* 245:    */   {
/* 246:411 */     double rt = Utils.gr(p, 0.0D) ? -k * Utils.log2(p) : 0.0D;
/* 247:412 */     rt -= (t - k) * Utils.log2(1.0D - p);
/* 248:413 */     return rt;
/* 249:    */   }
/* 250:    */   
/* 251:    */   public double theoryDL(int index)
/* 252:    */   {
/* 253:432 */     double k = ((Rule)this.m_Ruleset.get(index)).size();
/* 254:434 */     if (k == 0.0D) {
/* 255:435 */       return 0.0D;
/* 256:    */     }
/* 257:438 */     double tdl = Utils.log2(k);
/* 258:439 */     if (k > 1.0D) {
/* 259:440 */       tdl += 2.0D * Utils.log2(tdl);
/* 260:    */     }
/* 261:442 */     tdl += subsetDL(this.m_Total, k, k / this.m_Total);
/* 262:    */     
/* 263:    */ 
/* 264:445 */     return this.MDL_THEORY_WEIGHT * REDUNDANCY_FACTOR * tdl;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public static double dataDL(double expFPOverErr, double cover, double uncover, double fp, double fn)
/* 268:    */   {
/* 269:464 */     double totalBits = Utils.log2(cover + uncover + 1.0D);
/* 270:    */     double uncoverBits;
/* 271:    */     double coverBits;
/* 272:    */     double uncoverBits;
/* 273:468 */     if (Utils.gr(cover, uncover))
/* 274:    */     {
/* 275:469 */       double expErr = expFPOverErr * (fp + fn);
/* 276:470 */       double coverBits = subsetDL(cover, fp, expErr / cover);
/* 277:471 */       uncoverBits = Utils.gr(uncover, 0.0D) ? subsetDL(uncover, fn, fn / uncover) : 0.0D;
/* 278:    */     }
/* 279:    */     else
/* 280:    */     {
/* 281:474 */       double expErr = (1.0D - expFPOverErr) * (fp + fn);
/* 282:475 */       coverBits = Utils.gr(cover, 0.0D) ? subsetDL(cover, fp, fp / cover) : 0.0D;
/* 283:476 */       uncoverBits = subsetDL(uncover, fn, expErr / uncover);
/* 284:    */     }
/* 285:485 */     return totalBits + coverBits + uncoverBits;
/* 286:    */   }
/* 287:    */   
/* 288:    */   public double potential(int index, double expFPOverErr, double[] rulesetStat, double[] ruleStat, boolean checkErr)
/* 289:    */   {
/* 290:514 */     double pcov = rulesetStat[0] - ruleStat[0];
/* 291:515 */     double puncov = rulesetStat[1] + ruleStat[0];
/* 292:516 */     double pfp = rulesetStat[4] - ruleStat[4];
/* 293:517 */     double pfn = rulesetStat[5] + ruleStat[2];
/* 294:    */     
/* 295:519 */     double dataDLWith = dataDL(expFPOverErr, rulesetStat[0], rulesetStat[1], rulesetStat[4], rulesetStat[5]);
/* 296:    */     
/* 297:521 */     double theoryDLWith = theoryDL(index);
/* 298:522 */     double dataDLWithout = dataDL(expFPOverErr, pcov, puncov, pfp, pfn);
/* 299:    */     
/* 300:524 */     double potential = dataDLWith + theoryDLWith - dataDLWithout;
/* 301:525 */     double err = ruleStat[4] / ruleStat[0];
/* 302:    */     
/* 303:    */ 
/* 304:    */ 
/* 305:    */ 
/* 306:530 */     boolean overErr = Utils.grOrEq(err, 0.5D);
/* 307:531 */     if (!checkErr) {
/* 308:532 */       overErr = false;
/* 309:    */     }
/* 310:535 */     if ((Utils.grOrEq(potential, 0.0D)) || (overErr))
/* 311:    */     {
/* 312:537 */       rulesetStat[0] = pcov;
/* 313:538 */       rulesetStat[1] = puncov;
/* 314:539 */       rulesetStat[4] = pfp;
/* 315:540 */       rulesetStat[5] = pfn;
/* 316:541 */       return potential;
/* 317:    */     }
/* 318:543 */     return (0.0D / 0.0D);
/* 319:    */   }
/* 320:    */   
/* 321:    */   public double minDataDLIfDeleted(int index, double expFPRate, boolean checkErr)
/* 322:    */   {
/* 323:559 */     double[] rulesetStat = new double[6];
/* 324:560 */     int more = this.m_Ruleset.size() - 1 - index;
/* 325:561 */     ArrayList<double[]> indexPlus = new ArrayList(more);
/* 326:565 */     for (int j = 0; j < index; j++)
/* 327:    */     {
/* 328:567 */       rulesetStat[0] += ((double[])this.m_SimpleStats.get(j))[0];
/* 329:568 */       rulesetStat[2] += ((double[])this.m_SimpleStats.get(j))[2];
/* 330:569 */       rulesetStat[4] += ((double[])this.m_SimpleStats.get(j))[4];
/* 331:    */     }
/* 332:573 */     Instances data = index == 0 ? this.m_Data : ((Instances[])this.m_Filtered.get(index - 1))[1];
/* 333:576 */     for (int j = index + 1; j < this.m_Ruleset.size(); j++)
/* 334:    */     {
/* 335:577 */       double[] stats = new double[6];
/* 336:578 */       Instances[] split = computeSimpleStats(j, data, stats, null);
/* 337:579 */       indexPlus.add(stats);
/* 338:580 */       rulesetStat[0] += stats[0];
/* 339:581 */       rulesetStat[2] += stats[2];
/* 340:582 */       rulesetStat[4] += stats[4];
/* 341:583 */       data = split[1];
/* 342:    */     }
/* 343:586 */     if (more > 0)
/* 344:    */     {
/* 345:587 */       rulesetStat[1] = ((double[])indexPlus.get(indexPlus.size() - 1))[1];
/* 346:588 */       rulesetStat[3] = ((double[])indexPlus.get(indexPlus.size() - 1))[3];
/* 347:589 */       rulesetStat[5] = ((double[])indexPlus.get(indexPlus.size() - 1))[5];
/* 348:    */     }
/* 349:590 */     else if (index > 0)
/* 350:    */     {
/* 351:591 */       rulesetStat[1] = ((double[])this.m_SimpleStats.get(index - 1))[1];
/* 352:592 */       rulesetStat[3] = ((double[])this.m_SimpleStats.get(index - 1))[3];
/* 353:593 */       rulesetStat[5] = ((double[])this.m_SimpleStats.get(index - 1))[5];
/* 354:    */     }
/* 355:    */     else
/* 356:    */     {
/* 357:595 */       rulesetStat[1] = (((double[])this.m_SimpleStats.get(0))[0] + ((double[])this.m_SimpleStats.get(0))[1]);
/* 358:596 */       rulesetStat[3] = (((double[])this.m_SimpleStats.get(0))[3] + ((double[])this.m_SimpleStats.get(0))[4]);
/* 359:597 */       rulesetStat[5] = (((double[])this.m_SimpleStats.get(0))[2] + ((double[])this.m_SimpleStats.get(0))[5]);
/* 360:    */     }
/* 361:601 */     double potential = 0.0D;
/* 362:602 */     for (int k = index + 1; k < this.m_Ruleset.size(); k++)
/* 363:    */     {
/* 364:603 */       double[] ruleStat = (double[])indexPlus.get(k - index - 1);
/* 365:604 */       double ifDeleted = potential(k, expFPRate, rulesetStat, ruleStat, checkErr);
/* 366:606 */       if (!Double.isNaN(ifDeleted)) {
/* 367:607 */         potential += ifDeleted;
/* 368:    */       }
/* 369:    */     }
/* 370:614 */     double dataDLWithout = dataDL(expFPRate, rulesetStat[0], rulesetStat[1], rulesetStat[4], rulesetStat[5]);
/* 371:    */     
/* 372:    */ 
/* 373:    */ 
/* 374:    */ 
/* 375:619 */     return dataDLWithout - potential;
/* 376:    */   }
/* 377:    */   
/* 378:    */   public double minDataDLIfExists(int index, double expFPRate, boolean checkErr)
/* 379:    */   {
/* 380:634 */     double[] rulesetStat = new double[6];
/* 381:635 */     for (int j = 0; j < this.m_SimpleStats.size(); j++)
/* 382:    */     {
/* 383:637 */       rulesetStat[0] += ((double[])this.m_SimpleStats.get(j))[0];
/* 384:638 */       rulesetStat[2] += ((double[])this.m_SimpleStats.get(j))[2];
/* 385:639 */       rulesetStat[4] += ((double[])this.m_SimpleStats.get(j))[4];
/* 386:640 */       if (j == this.m_SimpleStats.size() - 1)
/* 387:    */       {
/* 388:641 */         rulesetStat[1] = ((double[])this.m_SimpleStats.get(j))[1];
/* 389:642 */         rulesetStat[3] = ((double[])this.m_SimpleStats.get(j))[3];
/* 390:643 */         rulesetStat[5] = ((double[])this.m_SimpleStats.get(j))[5];
/* 391:    */       }
/* 392:    */     }
/* 393:648 */     double potential = 0.0D;
/* 394:649 */     for (int k = index + 1; k < this.m_SimpleStats.size(); k++)
/* 395:    */     {
/* 396:650 */       double[] ruleStat = getSimpleStats(k);
/* 397:651 */       double ifDeleted = potential(k, expFPRate, rulesetStat, ruleStat, checkErr);
/* 398:653 */       if (!Double.isNaN(ifDeleted)) {
/* 399:654 */         potential += ifDeleted;
/* 400:    */       }
/* 401:    */     }
/* 402:661 */     double dataDLWith = dataDL(expFPRate, rulesetStat[0], rulesetStat[1], rulesetStat[4], rulesetStat[5]);
/* 403:    */     
/* 404:    */ 
/* 405:    */ 
/* 406:665 */     return dataDLWith - potential;
/* 407:    */   }
/* 408:    */   
/* 409:    */   public double relativeDL(int index, double expFPRate, boolean checkErr)
/* 410:    */   {
/* 411:683 */     return minDataDLIfExists(index, expFPRate, checkErr) + theoryDL(index) - minDataDLIfDeleted(index, expFPRate, checkErr);
/* 412:    */   }
/* 413:    */   
/* 414:    */   public void reduceDL(double expFPRate, boolean checkErr)
/* 415:    */   {
/* 416:696 */     boolean needUpdate = false;
/* 417:697 */     double[] rulesetStat = new double[6];
/* 418:698 */     for (int j = 0; j < this.m_SimpleStats.size(); j++)
/* 419:    */     {
/* 420:700 */       rulesetStat[0] += ((double[])this.m_SimpleStats.get(j))[0];
/* 421:701 */       rulesetStat[2] += ((double[])this.m_SimpleStats.get(j))[2];
/* 422:702 */       rulesetStat[4] += ((double[])this.m_SimpleStats.get(j))[4];
/* 423:703 */       if (j == this.m_SimpleStats.size() - 1)
/* 424:    */       {
/* 425:704 */         rulesetStat[1] = ((double[])this.m_SimpleStats.get(j))[1];
/* 426:705 */         rulesetStat[3] = ((double[])this.m_SimpleStats.get(j))[3];
/* 427:706 */         rulesetStat[5] = ((double[])this.m_SimpleStats.get(j))[5];
/* 428:    */       }
/* 429:    */     }
/* 430:711 */     for (int k = this.m_SimpleStats.size() - 1; k >= 0; k--)
/* 431:    */     {
/* 432:713 */       double[] ruleStat = (double[])this.m_SimpleStats.get(k);
/* 433:    */       
/* 434:    */ 
/* 435:716 */       double ifDeleted = potential(k, expFPRate, rulesetStat, ruleStat, checkErr);
/* 436:718 */       if (!Double.isNaN(ifDeleted)) {
/* 437:725 */         if (k == this.m_SimpleStats.size() - 1)
/* 438:    */         {
/* 439:726 */           removeLast();
/* 440:    */         }
/* 441:    */         else
/* 442:    */         {
/* 443:728 */           this.m_Ruleset.remove(k);
/* 444:729 */           needUpdate = true;
/* 445:    */         }
/* 446:    */       }
/* 447:    */     }
/* 448:734 */     if (needUpdate)
/* 449:    */     {
/* 450:735 */       this.m_Filtered = null;
/* 451:736 */       this.m_SimpleStats = null;
/* 452:737 */       countData();
/* 453:    */     }
/* 454:    */   }
/* 455:    */   
/* 456:    */   public void removeLast()
/* 457:    */   {
/* 458:747 */     int last = this.m_Ruleset.size() - 1;
/* 459:748 */     this.m_Ruleset.remove(last);
/* 460:749 */     this.m_Filtered.remove(last);
/* 461:750 */     this.m_SimpleStats.remove(last);
/* 462:751 */     if (this.m_Distributions != null) {
/* 463:752 */       this.m_Distributions.remove(last);
/* 464:    */     }
/* 465:    */   }
/* 466:    */   
/* 467:    */   public static Instances rmCoveredBySuccessives(Instances data, ArrayList<Rule> rules, int index)
/* 468:    */   {
/* 469:768 */     Instances rt = new Instances(data, 0);
/* 470:770 */     for (int i = 0; i < data.numInstances(); i++)
/* 471:    */     {
/* 472:771 */       Instance datum = data.instance(i);
/* 473:772 */       boolean covered = false;
/* 474:774 */       for (int j = index + 1; j < rules.size(); j++)
/* 475:    */       {
/* 476:775 */         Rule rule = (Rule)rules.get(j);
/* 477:776 */         if (rule.covers(datum))
/* 478:    */         {
/* 479:777 */           covered = true;
/* 480:778 */           break;
/* 481:    */         }
/* 482:    */       }
/* 483:782 */       if (!covered) {
/* 484:783 */         rt.add(datum);
/* 485:    */       }
/* 486:    */     }
/* 487:786 */     return rt;
/* 488:    */   }
/* 489:    */   
/* 490:    */   public static final Instances stratify(Instances data, int folds, Random rand)
/* 491:    */   {
/* 492:801 */     if (!data.classAttribute().isNominal()) {
/* 493:802 */       return data;
/* 494:    */     }
/* 495:805 */     Instances result = new Instances(data, 0);
/* 496:806 */     Instances[] bagsByClasses = new Instances[data.numClasses()];
/* 497:808 */     for (int i = 0; i < bagsByClasses.length; i++) {
/* 498:809 */       bagsByClasses[i] = new Instances(data, 0);
/* 499:    */     }
/* 500:813 */     for (int j = 0; j < data.numInstances(); j++)
/* 501:    */     {
/* 502:814 */       Instance datum = data.instance(j);
/* 503:815 */       bagsByClasses[((int)datum.classValue())].add(datum);
/* 504:    */     }
/* 505:819 */     for (Instances bagsByClasse : bagsByClasses) {
/* 506:820 */       bagsByClasse.randomize(rand);
/* 507:    */     }
/* 508:823 */     for (int k = 0; k < folds; k++)
/* 509:    */     {
/* 510:824 */       int offset = k;int bag = 0;
/* 511:    */       for (;;)
/* 512:    */       {
/* 513:826 */         if (offset >= bagsByClasses[bag].numInstances())
/* 514:    */         {
/* 515:827 */           offset -= bagsByClasses[bag].numInstances();
/* 516:828 */           bag++;
/* 517:828 */           if (bag >= bagsByClasses.length) {
/* 518:    */             break;
/* 519:    */           }
/* 520:    */         }
/* 521:    */         else
/* 522:    */         {
/* 523:833 */           result.add(bagsByClasses[bag].instance(offset));
/* 524:834 */           offset += folds;
/* 525:    */         }
/* 526:    */       }
/* 527:    */     }
/* 528:838 */     return result;
/* 529:    */   }
/* 530:    */   
/* 531:    */   public double combinedDL(double expFPRate, double predicted)
/* 532:    */   {
/* 533:851 */     double rt = 0.0D;
/* 534:853 */     if (getRulesetSize() > 0)
/* 535:    */     {
/* 536:854 */       double[] stats = (double[])this.m_SimpleStats.get(this.m_SimpleStats.size() - 1);
/* 537:855 */       for (int j = getRulesetSize() - 2; j >= 0; j--)
/* 538:    */       {
/* 539:856 */         stats[0] += getSimpleStats(j)[0];
/* 540:857 */         stats[2] += getSimpleStats(j)[2];
/* 541:858 */         stats[4] += getSimpleStats(j)[4];
/* 542:    */       }
/* 543:860 */       rt += dataDL(expFPRate, stats[0], stats[1], stats[4], stats[5]);
/* 544:    */     }
/* 545:    */     else
/* 546:    */     {
/* 547:863 */       double fn = 0.0D;
/* 548:864 */       for (int j = 0; j < this.m_Data.numInstances(); j++) {
/* 549:865 */         if ((int)this.m_Data.instance(j).classValue() == (int)predicted) {
/* 550:866 */           fn += this.m_Data.instance(j).weight();
/* 551:    */         }
/* 552:    */       }
/* 553:869 */       rt += dataDL(expFPRate, 0.0D, this.m_Data.sumOfWeights(), 0.0D, fn);
/* 554:    */     }
/* 555:872 */     for (int i = 0; i < getRulesetSize(); i++) {
/* 556:873 */       rt += theoryDL(i);
/* 557:    */     }
/* 558:876 */     return rt;
/* 559:    */   }
/* 560:    */   
/* 561:    */   public static final Instances[] partition(Instances data, int numFolds)
/* 562:    */   {
/* 563:889 */     Instances[] rt = new Instances[2];
/* 564:890 */     int splits = data.numInstances() * (numFolds - 1) / numFolds;
/* 565:    */     
/* 566:892 */     rt[0] = new Instances(data, 0, splits);
/* 567:893 */     rt[1] = new Instances(data, splits, data.numInstances() - splits);
/* 568:    */     
/* 569:895 */     return rt;
/* 570:    */   }
/* 571:    */   
/* 572:    */   public String getRevision()
/* 573:    */   {
/* 574:905 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 575:    */   }
/* 576:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.RuleStats
 * JD-Core Version:    0.7.0.1
 */