/*   1:    */ package weka.classifiers.bayes.net.search.global;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.bayes.BayesNet;
/*   8:    */ import weka.classifiers.bayes.net.ParentSet;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.RevisionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Utils;
/*  14:    */ 
/*  15:    */ public class GeneticSearch
/*  16:    */   extends GlobalScoreSearchAlgorithm
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = 4236165533882462203L;
/*  19:    */   int m_nRuns;
/*  20:    */   int m_nPopulationSize;
/*  21:    */   int m_nDescendantPopulationSize;
/*  22:    */   boolean m_bUseCrossOver;
/*  23:    */   boolean m_bUseMutation;
/*  24:    */   boolean m_bUseTournamentSelection;
/*  25:    */   int m_nSeed;
/*  26:    */   Random m_random;
/*  27:    */   boolean[] g_bIsSquare;
/*  28:    */   
/*  29:    */   public GeneticSearch()
/*  30:    */   {
/*  31:117 */     this.m_nRuns = 10;
/*  32:    */     
/*  33:    */ 
/*  34:120 */     this.m_nPopulationSize = 10;
/*  35:    */     
/*  36:    */ 
/*  37:123 */     this.m_nDescendantPopulationSize = 100;
/*  38:    */     
/*  39:    */ 
/*  40:126 */     this.m_bUseCrossOver = true;
/*  41:    */     
/*  42:    */ 
/*  43:129 */     this.m_bUseMutation = true;
/*  44:    */     
/*  45:    */ 
/*  46:132 */     this.m_bUseTournamentSelection = false;
/*  47:    */     
/*  48:    */ 
/*  49:135 */     this.m_nSeed = 1;
/*  50:    */     
/*  51:    */ 
/*  52:138 */     this.m_random = null;
/*  53:    */   }
/*  54:    */   
/*  55:    */   class BayesNetRepresentation
/*  56:    */     implements RevisionHandler
/*  57:    */   {
/*  58:148 */     int m_nNodes = 0;
/*  59:    */     boolean[] m_bits;
/*  60:157 */     double m_fScore = 0.0D;
/*  61:    */     
/*  62:    */     public double getScore()
/*  63:    */     {
/*  64:165 */       return this.m_fScore;
/*  65:    */     }
/*  66:    */     
/*  67:    */     BayesNetRepresentation(int nNodes)
/*  68:    */     {
/*  69:174 */       this.m_nNodes = nNodes;
/*  70:    */     }
/*  71:    */     
/*  72:    */     public void randomInit()
/*  73:    */     {
/*  74:    */       do
/*  75:    */       {
/*  76:182 */         this.m_bits = new boolean[this.m_nNodes * this.m_nNodes];
/*  77:183 */         for (int i = 0; i < this.m_nNodes; i++)
/*  78:    */         {
/*  79:    */           int iPos;
/*  80:    */           do
/*  81:    */           {
/*  82:186 */             iPos = GeneticSearch.this.m_random.nextInt(this.m_nNodes * this.m_nNodes);
/*  83:187 */           } while (isSquare(iPos));
/*  84:188 */           this.m_bits[iPos] = true;
/*  85:    */         }
/*  86:190 */       } while (hasCycles());
/*  87:191 */       calcGlobalScore();
/*  88:    */     }
/*  89:    */     
/*  90:    */     void calcGlobalScore()
/*  91:    */     {
/*  92:200 */       for (int iNode = 0; iNode < this.m_nNodes; iNode++)
/*  93:    */       {
/*  94:201 */         ParentSet parentSet = GeneticSearch.this.m_BayesNet.getParentSet(iNode);
/*  95:202 */         while (parentSet.getNrOfParents() > 0) {
/*  96:203 */           parentSet.deleteLastParent(GeneticSearch.this.m_BayesNet.m_Instances);
/*  97:    */         }
/*  98:    */       }
/*  99:207 */       for (int iNode = 0; iNode < this.m_nNodes; iNode++)
/* 100:    */       {
/* 101:208 */         ParentSet parentSet = GeneticSearch.this.m_BayesNet.getParentSet(iNode);
/* 102:209 */         for (int iNode2 = 0; iNode2 < this.m_nNodes; iNode2++) {
/* 103:210 */           if (this.m_bits[(iNode2 + iNode * this.m_nNodes)] != 0) {
/* 104:211 */             parentSet.addParent(iNode2, GeneticSearch.this.m_BayesNet.m_Instances);
/* 105:    */           }
/* 106:    */         }
/* 107:    */       }
/* 108:    */       try
/* 109:    */       {
/* 110:217 */         this.m_fScore = GeneticSearch.this.calcScore(GeneticSearch.this.m_BayesNet);
/* 111:    */       }
/* 112:    */       catch (Exception e) {}
/* 113:    */     }
/* 114:    */     
/* 115:    */     public boolean hasCycles()
/* 116:    */     {
/* 117:230 */       boolean[] bDone = new boolean[this.m_nNodes];
/* 118:231 */       for (int iNode = 0; iNode < this.m_nNodes; iNode++)
/* 119:    */       {
/* 120:234 */         boolean bFound = false;
/* 121:236 */         for (int iNode2 = 0; (!bFound) && (iNode2 < this.m_nNodes); iNode2++) {
/* 122:237 */           if (bDone[iNode2] == 0)
/* 123:    */           {
/* 124:238 */             boolean bHasNoParents = true;
/* 125:239 */             for (int iParent = 0; iParent < this.m_nNodes; iParent++) {
/* 126:240 */               if ((this.m_bits[(iParent + iNode2 * this.m_nNodes)] != 0) && (bDone[iParent] == 0)) {
/* 127:241 */                 bHasNoParents = false;
/* 128:    */               }
/* 129:    */             }
/* 130:244 */             if (bHasNoParents)
/* 131:    */             {
/* 132:245 */               bDone[iNode2] = true;
/* 133:246 */               bFound = true;
/* 134:    */             }
/* 135:    */           }
/* 136:    */         }
/* 137:250 */         if (!bFound) {
/* 138:251 */           return true;
/* 139:    */         }
/* 140:    */       }
/* 141:254 */       return false;
/* 142:    */     }
/* 143:    */     
/* 144:    */     BayesNetRepresentation copy()
/* 145:    */     {
/* 146:263 */       BayesNetRepresentation b = new BayesNetRepresentation(GeneticSearch.this, this.m_nNodes);
/* 147:264 */       b.m_bits = new boolean[this.m_bits.length];
/* 148:265 */       for (int i = 0; i < this.m_nNodes * this.m_nNodes; i++) {
/* 149:266 */         b.m_bits[i] = this.m_bits[i];
/* 150:    */       }
/* 151:268 */       b.m_fScore = this.m_fScore;
/* 152:269 */       return b;
/* 153:    */     }
/* 154:    */     
/* 155:    */     void mutate()
/* 156:    */     {
/* 157:    */       do
/* 158:    */       {
/* 159:    */         int iBit;
/* 160:    */         do
/* 161:    */         {
/* 162:281 */           iBit = GeneticSearch.this.m_random.nextInt(this.m_nNodes * this.m_nNodes);
/* 163:282 */         } while (isSquare(iBit));
/* 164:284 */         this.m_bits[iBit] = (this.m_bits[iBit] == 0 ? 1 : false);
/* 165:285 */       } while (hasCycles());
/* 166:287 */       calcGlobalScore();
/* 167:    */     }
/* 168:    */     
/* 169:    */     void crossOver(BayesNetRepresentation other)
/* 170:    */     {
/* 171:297 */       boolean[] bits = new boolean[this.m_bits.length];
/* 172:298 */       for (int i = 0; i < this.m_bits.length; i++) {
/* 173:299 */         bits[i] = this.m_bits[i];
/* 174:    */       }
/* 175:301 */       int iCrossOverPoint = this.m_bits.length;
/* 176:    */       do
/* 177:    */       {
/* 178:304 */         for (int i = iCrossOverPoint; i < this.m_bits.length; i++) {
/* 179:305 */           this.m_bits[i] = bits[i];
/* 180:    */         }
/* 181:308 */         iCrossOverPoint = GeneticSearch.this.m_random.nextInt(this.m_bits.length);
/* 182:309 */         for (int i = iCrossOverPoint; i < this.m_bits.length; i++) {
/* 183:310 */           this.m_bits[i] = other.m_bits[i];
/* 184:    */         }
/* 185:312 */       } while (hasCycles());
/* 186:313 */       calcGlobalScore();
/* 187:    */     }
/* 188:    */     
/* 189:    */     boolean isSquare(int nNum)
/* 190:    */     {
/* 191:324 */       if ((GeneticSearch.this.g_bIsSquare == null) || (GeneticSearch.this.g_bIsSquare.length < nNum))
/* 192:    */       {
/* 193:325 */         GeneticSearch.this.g_bIsSquare = new boolean[this.m_nNodes * this.m_nNodes];
/* 194:326 */         for (int i = 0; i < this.m_nNodes; i++) {
/* 195:327 */           GeneticSearch.this.g_bIsSquare[(i * this.m_nNodes + i)] = true;
/* 196:    */         }
/* 197:    */       }
/* 198:330 */       return GeneticSearch.this.g_bIsSquare[nNum];
/* 199:    */     }
/* 200:    */     
/* 201:    */     public String getRevision()
/* 202:    */     {
/* 203:340 */       return RevisionUtils.extract("$Revision: 11247 $");
/* 204:    */     }
/* 205:    */   }
/* 206:    */   
/* 207:    */   protected void search(BayesNet bayesNet, Instances instances)
/* 208:    */     throws Exception
/* 209:    */   {
/* 210:357 */     if (getDescendantPopulationSize() < getPopulationSize()) {
/* 211:358 */       throw new Exception("Descendant PopulationSize should be at least Population Size");
/* 212:    */     }
/* 213:361 */     if ((!getUseCrossOver()) && (!getUseMutation())) {
/* 214:362 */       throw new Exception("At least one of mutation or cross-over should be used");
/* 215:    */     }
/* 216:366 */     this.m_random = new Random(this.m_nSeed);
/* 217:    */     
/* 218:    */ 
/* 219:    */ 
/* 220:    */ 
/* 221:371 */     double fBestScore = calcScore(bayesNet);
/* 222:    */     
/* 223:    */ 
/* 224:374 */     BayesNet bestBayesNet = new BayesNet();
/* 225:375 */     bestBayesNet.m_Instances = instances;
/* 226:376 */     bestBayesNet.initStructure();
/* 227:377 */     copyParentSets(bestBayesNet, bayesNet);
/* 228:    */     
/* 229:    */ 
/* 230:380 */     BayesNetRepresentation[] population = new BayesNetRepresentation[getPopulationSize()];
/* 231:381 */     for (int i = 0; i < getPopulationSize(); i++)
/* 232:    */     {
/* 233:382 */       population[i] = new BayesNetRepresentation(instances.numAttributes());
/* 234:383 */       population[i].randomInit();
/* 235:384 */       if (population[i].getScore() > fBestScore)
/* 236:    */       {
/* 237:385 */         copyParentSets(bestBayesNet, bayesNet);
/* 238:386 */         fBestScore = population[i].getScore();
/* 239:    */       }
/* 240:    */     }
/* 241:392 */     for (int iRun = 0; iRun < this.m_nRuns; iRun++)
/* 242:    */     {
/* 243:394 */       BayesNetRepresentation[] descendantPopulation = new BayesNetRepresentation[getDescendantPopulationSize()];
/* 244:395 */       for (int i = 0; i < getDescendantPopulationSize(); i++)
/* 245:    */       {
/* 246:396 */         descendantPopulation[i] = population[this.m_random.nextInt(getPopulationSize())].copy();
/* 247:398 */         if (getUseMutation())
/* 248:    */         {
/* 249:399 */           if ((getUseCrossOver()) && (this.m_random.nextBoolean())) {
/* 250:400 */             descendantPopulation[i].crossOver(population[this.m_random.nextInt(getPopulationSize())]);
/* 251:    */           } else {
/* 252:403 */             descendantPopulation[i].mutate();
/* 253:    */           }
/* 254:    */         }
/* 255:    */         else {
/* 256:407 */           descendantPopulation[i].crossOver(population[this.m_random.nextInt(getPopulationSize())]);
/* 257:    */         }
/* 258:411 */         if (descendantPopulation[i].getScore() > fBestScore)
/* 259:    */         {
/* 260:412 */           copyParentSets(bestBayesNet, bayesNet);
/* 261:413 */           fBestScore = descendantPopulation[i].getScore();
/* 262:    */         }
/* 263:    */       }
/* 264:417 */       boolean[] bSelected = new boolean[getDescendantPopulationSize()];
/* 265:418 */       for (int i = 0; i < getPopulationSize(); i++)
/* 266:    */       {
/* 267:419 */         int iSelected = 0;
/* 268:420 */         if (this.m_bUseTournamentSelection)
/* 269:    */         {
/* 270:422 */           iSelected = this.m_random.nextInt(getDescendantPopulationSize());
/* 271:423 */           while (bSelected[iSelected] != 0) {
/* 272:424 */             iSelected = (iSelected + 1) % getDescendantPopulationSize();
/* 273:    */           }
/* 274:426 */           int iSelected2 = this.m_random.nextInt(getDescendantPopulationSize());
/* 275:427 */           while (bSelected[iSelected2] != 0) {
/* 276:428 */             iSelected2 = (iSelected2 + 1) % getDescendantPopulationSize();
/* 277:    */           }
/* 278:430 */           if (descendantPopulation[iSelected2].getScore() > descendantPopulation[iSelected].getScore()) {
/* 279:432 */             iSelected = iSelected2;
/* 280:    */           }
/* 281:    */         }
/* 282:    */         else
/* 283:    */         {
/* 284:436 */           while (bSelected[iSelected] != 0) {
/* 285:437 */             iSelected++;
/* 286:    */           }
/* 287:439 */           double fScore = descendantPopulation[iSelected].getScore();
/* 288:440 */           for (int j = 0; j < getDescendantPopulationSize(); j++) {
/* 289:441 */             if ((bSelected[j] == 0) && (descendantPopulation[j].getScore() > fScore))
/* 290:    */             {
/* 291:442 */               fScore = descendantPopulation[j].getScore();
/* 292:443 */               iSelected = j;
/* 293:    */             }
/* 294:    */           }
/* 295:    */         }
/* 296:447 */         population[i] = descendantPopulation[iSelected];
/* 297:448 */         bSelected[iSelected] = true;
/* 298:    */       }
/* 299:    */     }
/* 300:453 */     copyParentSets(bayesNet, bestBayesNet);
/* 301:    */     
/* 302:    */ 
/* 303:456 */     bestBayesNet = null;
/* 304:457 */     this.g_bIsSquare = null;
/* 305:    */   }
/* 306:    */   
/* 307:    */   void copyParentSets(BayesNet dest, BayesNet source)
/* 308:    */   {
/* 309:467 */     int nNodes = source.getNrOfNodes();
/* 310:469 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/* 311:470 */       dest.getParentSet(iNode).copy(source.getParentSet(iNode));
/* 312:    */     }
/* 313:    */   }
/* 314:    */   
/* 315:    */   public int getRuns()
/* 316:    */   {
/* 317:478 */     return this.m_nRuns;
/* 318:    */   }
/* 319:    */   
/* 320:    */   public void setRuns(int nRuns)
/* 321:    */   {
/* 322:487 */     this.m_nRuns = nRuns;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public Enumeration<Option> listOptions()
/* 326:    */   {
/* 327:497 */     Vector<Option> newVector = new Vector(7);
/* 328:    */     
/* 329:499 */     newVector.addElement(new Option("\tPopulation size", "L", 1, "-L <integer>"));
/* 330:    */     
/* 331:501 */     newVector.addElement(new Option("\tDescendant population size", "A", 1, "-A <integer>"));
/* 332:    */     
/* 333:503 */     newVector.addElement(new Option("\tNumber of runs", "U", 1, "-U <integer>"));
/* 334:    */     
/* 335:505 */     newVector.addElement(new Option("\tUse mutation.\n\t(default true)", "M", 0, "-M"));
/* 336:    */     
/* 337:507 */     newVector.addElement(new Option("\tUse cross-over.\n\t(default true)", "C", 0, "-C"));
/* 338:    */     
/* 339:509 */     newVector.addElement(new Option("\tUse tournament selection (true) or maximum subpopulatin (false).\n\t(default false)", "O", 0, "-O"));
/* 340:    */     
/* 341:    */ 
/* 342:    */ 
/* 343:513 */     newVector.addElement(new Option("\tRandom number seed", "R", 1, "-R <seed>"));
/* 344:    */     
/* 345:    */ 
/* 346:516 */     newVector.addAll(Collections.list(super.listOptions()));
/* 347:    */     
/* 348:518 */     return newVector.elements();
/* 349:    */   }
/* 350:    */   
/* 351:    */   public void setOptions(String[] options)
/* 352:    */     throws Exception
/* 353:    */   {
/* 354:592 */     String sPopulationSize = Utils.getOption('L', options);
/* 355:593 */     if (sPopulationSize.length() != 0) {
/* 356:594 */       setPopulationSize(Integer.parseInt(sPopulationSize));
/* 357:    */     }
/* 358:596 */     String sDescendantPopulationSize = Utils.getOption('A', options);
/* 359:597 */     if (sDescendantPopulationSize.length() != 0) {
/* 360:598 */       setDescendantPopulationSize(Integer.parseInt(sDescendantPopulationSize));
/* 361:    */     }
/* 362:600 */     String sRuns = Utils.getOption('U', options);
/* 363:601 */     if (sRuns.length() != 0) {
/* 364:602 */       setRuns(Integer.parseInt(sRuns));
/* 365:    */     }
/* 366:604 */     String sSeed = Utils.getOption('R', options);
/* 367:605 */     if (sSeed.length() != 0) {
/* 368:606 */       setSeed(Integer.parseInt(sSeed));
/* 369:    */     }
/* 370:608 */     setUseMutation(Utils.getFlag('M', options));
/* 371:609 */     setUseCrossOver(Utils.getFlag('C', options));
/* 372:610 */     setUseTournamentSelection(Utils.getFlag('O', options));
/* 373:    */     
/* 374:612 */     super.setOptions(options);
/* 375:    */   }
/* 376:    */   
/* 377:    */   public String[] getOptions()
/* 378:    */   {
/* 379:623 */     Vector<String> options = new Vector();
/* 380:    */     
/* 381:625 */     options.add("-L");
/* 382:626 */     options.add("" + getPopulationSize());
/* 383:    */     
/* 384:628 */     options.add("-A");
/* 385:629 */     options.add("" + getDescendantPopulationSize());
/* 386:    */     
/* 387:631 */     options.add("-U");
/* 388:632 */     options.add("" + getRuns());
/* 389:    */     
/* 390:634 */     options.add("-R");
/* 391:635 */     options.add("" + getSeed());
/* 392:637 */     if (getUseMutation()) {
/* 393:638 */       options.add("-M");
/* 394:    */     }
/* 395:640 */     if (getUseCrossOver()) {
/* 396:641 */       options.add("-C");
/* 397:    */     }
/* 398:643 */     if (getUseTournamentSelection()) {
/* 399:644 */       options.add("-O");
/* 400:    */     }
/* 401:647 */     Collections.addAll(options, super.getOptions());
/* 402:    */     
/* 403:649 */     return (String[])options.toArray(new String[0]);
/* 404:    */   }
/* 405:    */   
/* 406:    */   public boolean getUseCrossOver()
/* 407:    */   {
/* 408:656 */     return this.m_bUseCrossOver;
/* 409:    */   }
/* 410:    */   
/* 411:    */   public boolean getUseMutation()
/* 412:    */   {
/* 413:663 */     return this.m_bUseMutation;
/* 414:    */   }
/* 415:    */   
/* 416:    */   public int getDescendantPopulationSize()
/* 417:    */   {
/* 418:670 */     return this.m_nDescendantPopulationSize;
/* 419:    */   }
/* 420:    */   
/* 421:    */   public int getPopulationSize()
/* 422:    */   {
/* 423:677 */     return this.m_nPopulationSize;
/* 424:    */   }
/* 425:    */   
/* 426:    */   public void setUseCrossOver(boolean bUseCrossOver)
/* 427:    */   {
/* 428:684 */     this.m_bUseCrossOver = bUseCrossOver;
/* 429:    */   }
/* 430:    */   
/* 431:    */   public void setUseMutation(boolean bUseMutation)
/* 432:    */   {
/* 433:691 */     this.m_bUseMutation = bUseMutation;
/* 434:    */   }
/* 435:    */   
/* 436:    */   public boolean getUseTournamentSelection()
/* 437:    */   {
/* 438:699 */     return this.m_bUseTournamentSelection;
/* 439:    */   }
/* 440:    */   
/* 441:    */   public void setUseTournamentSelection(boolean bUseTournamentSelection)
/* 442:    */   {
/* 443:707 */     this.m_bUseTournamentSelection = bUseTournamentSelection;
/* 444:    */   }
/* 445:    */   
/* 446:    */   public void setDescendantPopulationSize(int iDescendantPopulationSize)
/* 447:    */   {
/* 448:714 */     this.m_nDescendantPopulationSize = iDescendantPopulationSize;
/* 449:    */   }
/* 450:    */   
/* 451:    */   public void setPopulationSize(int iPopulationSize)
/* 452:    */   {
/* 453:721 */     this.m_nPopulationSize = iPopulationSize;
/* 454:    */   }
/* 455:    */   
/* 456:    */   public int getSeed()
/* 457:    */   {
/* 458:728 */     return this.m_nSeed;
/* 459:    */   }
/* 460:    */   
/* 461:    */   public void setSeed(int nSeed)
/* 462:    */   {
/* 463:737 */     this.m_nSeed = nSeed;
/* 464:    */   }
/* 465:    */   
/* 466:    */   public String globalInfo()
/* 467:    */   {
/* 468:747 */     return "This Bayes Network learning algorithm uses genetic search for finding a well scoring Bayes network structure. Genetic search works by having a population of Bayes network structures and allow them to mutate and apply cross over to get offspring. The best network structure found during the process is returned.";
/* 469:    */   }
/* 470:    */   
/* 471:    */   public String runsTipText()
/* 472:    */   {
/* 473:757 */     return "Sets the number of generations of Bayes network structure populations.";
/* 474:    */   }
/* 475:    */   
/* 476:    */   public String seedTipText()
/* 477:    */   {
/* 478:764 */     return "Initialization value for random number generator. Setting the seed allows replicability of experiments.";
/* 479:    */   }
/* 480:    */   
/* 481:    */   public String populationSizeTipText()
/* 482:    */   {
/* 483:772 */     return "Sets the size of the population of network structures that is selected each generation.";
/* 484:    */   }
/* 485:    */   
/* 486:    */   public String descendantPopulationSizeTipText()
/* 487:    */   {
/* 488:779 */     return "Sets the size of the population of descendants that is created each generation.";
/* 489:    */   }
/* 490:    */   
/* 491:    */   public String useMutationTipText()
/* 492:    */   {
/* 493:786 */     return "Determines whether mutation is allowed. Mutation flips a bit in the bit representation of the network structure. At least one of mutation or cross-over should be used.";
/* 494:    */   }
/* 495:    */   
/* 496:    */   public String useCrossOverTipText()
/* 497:    */   {
/* 498:795 */     return "Determines whether cross-over is allowed. Cross over combined the bit representations of network structure by taking a random first k bits of oneand adding the remainder of the other. At least one of mutation or cross-over should be used.";
/* 499:    */   }
/* 500:    */   
/* 501:    */   public String useTournamentSelectionTipText()
/* 502:    */   {
/* 503:805 */     return "Determines the method of selecting a population. When set to true, tournament selection is used (pick two at random and the highest is allowed to continue). When set to false, the top scoring network structures are selected.";
/* 504:    */   }
/* 505:    */   
/* 506:    */   public String getRevision()
/* 507:    */   {
/* 508:817 */     return RevisionUtils.extract("$Revision: 11247 $");
/* 509:    */   }
/* 510:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.global.GeneticSearch
 * JD-Core Version:    0.7.0.1
 */