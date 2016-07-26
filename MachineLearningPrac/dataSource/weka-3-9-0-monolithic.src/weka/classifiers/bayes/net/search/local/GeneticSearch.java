/*   1:    */ package weka.classifiers.bayes.net.search.local;
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
/*  16:    */   extends LocalScoreSearchAlgorithm
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = -7037070678911459757L;
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
/*  31:111 */     this.m_nRuns = 10;
/*  32:    */     
/*  33:    */ 
/*  34:114 */     this.m_nPopulationSize = 10;
/*  35:    */     
/*  36:    */ 
/*  37:117 */     this.m_nDescendantPopulationSize = 100;
/*  38:    */     
/*  39:    */ 
/*  40:120 */     this.m_bUseCrossOver = true;
/*  41:    */     
/*  42:    */ 
/*  43:123 */     this.m_bUseMutation = true;
/*  44:    */     
/*  45:    */ 
/*  46:126 */     this.m_bUseTournamentSelection = false;
/*  47:    */     
/*  48:    */ 
/*  49:129 */     this.m_nSeed = 1;
/*  50:    */     
/*  51:    */ 
/*  52:132 */     this.m_random = null;
/*  53:    */   }
/*  54:    */   
/*  55:    */   class BayesNetRepresentation
/*  56:    */     implements RevisionHandler
/*  57:    */   {
/*  58:143 */     int m_nNodes = 0;
/*  59:    */     boolean[] m_bits;
/*  60:152 */     double m_fScore = 0.0D;
/*  61:    */     
/*  62:    */     public double getScore()
/*  63:    */     {
/*  64:160 */       return this.m_fScore;
/*  65:    */     }
/*  66:    */     
/*  67:    */     BayesNetRepresentation(int nNodes)
/*  68:    */     {
/*  69:169 */       this.m_nNodes = nNodes;
/*  70:    */     }
/*  71:    */     
/*  72:    */     public void randomInit()
/*  73:    */     {
/*  74:    */       do
/*  75:    */       {
/*  76:177 */         this.m_bits = new boolean[this.m_nNodes * this.m_nNodes];
/*  77:178 */         for (int i = 0; i < this.m_nNodes; i++)
/*  78:    */         {
/*  79:    */           int iPos;
/*  80:    */           do
/*  81:    */           {
/*  82:181 */             iPos = GeneticSearch.this.m_random.nextInt(this.m_nNodes * this.m_nNodes);
/*  83:182 */           } while (isSquare(iPos));
/*  84:183 */           this.m_bits[iPos] = true;
/*  85:    */         }
/*  86:185 */       } while (hasCycles());
/*  87:186 */       calcScore();
/*  88:    */     }
/*  89:    */     
/*  90:    */     void calcScore()
/*  91:    */     {
/*  92:195 */       for (int iNode = 0; iNode < this.m_nNodes; iNode++)
/*  93:    */       {
/*  94:196 */         ParentSet parentSet = GeneticSearch.this.m_BayesNet.getParentSet(iNode);
/*  95:197 */         while (parentSet.getNrOfParents() > 0) {
/*  96:198 */           parentSet.deleteLastParent(GeneticSearch.this.m_BayesNet.m_Instances);
/*  97:    */         }
/*  98:    */       }
/*  99:202 */       for (int iNode = 0; iNode < this.m_nNodes; iNode++)
/* 100:    */       {
/* 101:203 */         ParentSet parentSet = GeneticSearch.this.m_BayesNet.getParentSet(iNode);
/* 102:204 */         for (int iNode2 = 0; iNode2 < this.m_nNodes; iNode2++) {
/* 103:205 */           if (this.m_bits[(iNode2 + iNode * this.m_nNodes)] != 0) {
/* 104:206 */             parentSet.addParent(iNode2, GeneticSearch.this.m_BayesNet.m_Instances);
/* 105:    */           }
/* 106:    */         }
/* 107:    */       }
/* 108:211 */       this.m_fScore = 0.0D;
/* 109:212 */       for (int iNode = 0; iNode < this.m_nNodes; iNode++) {
/* 110:213 */         this.m_fScore += GeneticSearch.this.calcNodeScore(iNode);
/* 111:    */       }
/* 112:    */     }
/* 113:    */     
/* 114:    */     public boolean hasCycles()
/* 115:    */     {
/* 116:224 */       boolean[] bDone = new boolean[this.m_nNodes];
/* 117:225 */       for (int iNode = 0; iNode < this.m_nNodes; iNode++)
/* 118:    */       {
/* 119:228 */         boolean bFound = false;
/* 120:230 */         for (int iNode2 = 0; (!bFound) && (iNode2 < this.m_nNodes); iNode2++) {
/* 121:231 */           if (bDone[iNode2] == 0)
/* 122:    */           {
/* 123:232 */             boolean bHasNoParents = true;
/* 124:233 */             for (int iParent = 0; iParent < this.m_nNodes; iParent++) {
/* 125:234 */               if ((this.m_bits[(iParent + iNode2 * this.m_nNodes)] != 0) && (bDone[iParent] == 0)) {
/* 126:235 */                 bHasNoParents = false;
/* 127:    */               }
/* 128:    */             }
/* 129:238 */             if (bHasNoParents)
/* 130:    */             {
/* 131:239 */               bDone[iNode2] = true;
/* 132:240 */               bFound = true;
/* 133:    */             }
/* 134:    */           }
/* 135:    */         }
/* 136:244 */         if (!bFound) {
/* 137:245 */           return true;
/* 138:    */         }
/* 139:    */       }
/* 140:248 */       return false;
/* 141:    */     }
/* 142:    */     
/* 143:    */     BayesNetRepresentation copy()
/* 144:    */     {
/* 145:257 */       BayesNetRepresentation b = new BayesNetRepresentation(GeneticSearch.this, this.m_nNodes);
/* 146:258 */       b.m_bits = new boolean[this.m_bits.length];
/* 147:259 */       for (int i = 0; i < this.m_nNodes * this.m_nNodes; i++) {
/* 148:260 */         b.m_bits[i] = this.m_bits[i];
/* 149:    */       }
/* 150:262 */       b.m_fScore = this.m_fScore;
/* 151:263 */       return b;
/* 152:    */     }
/* 153:    */     
/* 154:    */     void mutate()
/* 155:    */     {
/* 156:    */       do
/* 157:    */       {
/* 158:    */         int iBit;
/* 159:    */         do
/* 160:    */         {
/* 161:275 */           iBit = GeneticSearch.this.m_random.nextInt(this.m_nNodes * this.m_nNodes);
/* 162:276 */         } while (isSquare(iBit));
/* 163:278 */         this.m_bits[iBit] = (this.m_bits[iBit] == 0 ? 1 : false);
/* 164:279 */       } while (hasCycles());
/* 165:281 */       calcScore();
/* 166:    */     }
/* 167:    */     
/* 168:    */     void crossOver(BayesNetRepresentation other)
/* 169:    */     {
/* 170:291 */       boolean[] bits = new boolean[this.m_bits.length];
/* 171:292 */       for (int i = 0; i < this.m_bits.length; i++) {
/* 172:293 */         bits[i] = this.m_bits[i];
/* 173:    */       }
/* 174:295 */       int iCrossOverPoint = this.m_bits.length;
/* 175:    */       do
/* 176:    */       {
/* 177:298 */         for (int i = iCrossOverPoint; i < this.m_bits.length; i++) {
/* 178:299 */           this.m_bits[i] = bits[i];
/* 179:    */         }
/* 180:302 */         iCrossOverPoint = GeneticSearch.this.m_random.nextInt(this.m_bits.length);
/* 181:303 */         for (int i = iCrossOverPoint; i < this.m_bits.length; i++) {
/* 182:304 */           this.m_bits[i] = other.m_bits[i];
/* 183:    */         }
/* 184:306 */       } while (hasCycles());
/* 185:307 */       calcScore();
/* 186:    */     }
/* 187:    */     
/* 188:    */     boolean isSquare(int nNum)
/* 189:    */     {
/* 190:318 */       if ((GeneticSearch.this.g_bIsSquare == null) || (GeneticSearch.this.g_bIsSquare.length < nNum))
/* 191:    */       {
/* 192:319 */         GeneticSearch.this.g_bIsSquare = new boolean[this.m_nNodes * this.m_nNodes];
/* 193:320 */         for (int i = 0; i < this.m_nNodes; i++) {
/* 194:321 */           GeneticSearch.this.g_bIsSquare[(i * this.m_nNodes + i)] = true;
/* 195:    */         }
/* 196:    */       }
/* 197:324 */       return GeneticSearch.this.g_bIsSquare[nNum];
/* 198:    */     }
/* 199:    */     
/* 200:    */     public String getRevision()
/* 201:    */     {
/* 202:334 */       return RevisionUtils.extract("$Revision: 11247 $");
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:    */   protected void search(BayesNet bayesNet, Instances instances)
/* 207:    */     throws Exception
/* 208:    */   {
/* 209:351 */     if (getDescendantPopulationSize() < getPopulationSize()) {
/* 210:352 */       throw new Exception("Descendant PopulationSize should be at least Population Size");
/* 211:    */     }
/* 212:355 */     if ((!getUseCrossOver()) && (!getUseMutation())) {
/* 213:356 */       throw new Exception("At least one of mutation or cross-over should be used");
/* 214:    */     }
/* 215:360 */     this.m_random = new Random(this.m_nSeed);
/* 216:    */     
/* 217:    */ 
/* 218:    */ 
/* 219:    */ 
/* 220:365 */     double fBestScore = 0.0D;
/* 221:366 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++) {
/* 222:367 */       fBestScore += calcNodeScore(iAttribute);
/* 223:    */     }
/* 224:371 */     BayesNet bestBayesNet = new BayesNet();
/* 225:372 */     bestBayesNet.m_Instances = instances;
/* 226:373 */     bestBayesNet.initStructure();
/* 227:374 */     copyParentSets(bestBayesNet, bayesNet);
/* 228:    */     
/* 229:    */ 
/* 230:377 */     BayesNetRepresentation[] population = new BayesNetRepresentation[getPopulationSize()];
/* 231:378 */     for (int i = 0; i < getPopulationSize(); i++)
/* 232:    */     {
/* 233:379 */       population[i] = new BayesNetRepresentation(instances.numAttributes());
/* 234:380 */       population[i].randomInit();
/* 235:381 */       if (population[i].getScore() > fBestScore)
/* 236:    */       {
/* 237:382 */         copyParentSets(bestBayesNet, bayesNet);
/* 238:383 */         fBestScore = population[i].getScore();
/* 239:    */       }
/* 240:    */     }
/* 241:389 */     for (int iRun = 0; iRun < this.m_nRuns; iRun++)
/* 242:    */     {
/* 243:391 */       BayesNetRepresentation[] descendantPopulation = new BayesNetRepresentation[getDescendantPopulationSize()];
/* 244:392 */       for (int i = 0; i < getDescendantPopulationSize(); i++)
/* 245:    */       {
/* 246:393 */         descendantPopulation[i] = population[this.m_random.nextInt(getPopulationSize())].copy();
/* 247:395 */         if (getUseMutation())
/* 248:    */         {
/* 249:396 */           if ((getUseCrossOver()) && (this.m_random.nextBoolean())) {
/* 250:397 */             descendantPopulation[i].crossOver(population[this.m_random.nextInt(getPopulationSize())]);
/* 251:    */           } else {
/* 252:400 */             descendantPopulation[i].mutate();
/* 253:    */           }
/* 254:    */         }
/* 255:    */         else {
/* 256:404 */           descendantPopulation[i].crossOver(population[this.m_random.nextInt(getPopulationSize())]);
/* 257:    */         }
/* 258:408 */         if (descendantPopulation[i].getScore() > fBestScore)
/* 259:    */         {
/* 260:409 */           copyParentSets(bestBayesNet, bayesNet);
/* 261:410 */           fBestScore = descendantPopulation[i].getScore();
/* 262:    */         }
/* 263:    */       }
/* 264:414 */       boolean[] bSelected = new boolean[getDescendantPopulationSize()];
/* 265:415 */       for (int i = 0; i < getPopulationSize(); i++)
/* 266:    */       {
/* 267:416 */         int iSelected = 0;
/* 268:417 */         if (this.m_bUseTournamentSelection)
/* 269:    */         {
/* 270:419 */           iSelected = this.m_random.nextInt(getDescendantPopulationSize());
/* 271:420 */           while (bSelected[iSelected] != 0) {
/* 272:421 */             iSelected = (iSelected + 1) % getDescendantPopulationSize();
/* 273:    */           }
/* 274:423 */           int iSelected2 = this.m_random.nextInt(getDescendantPopulationSize());
/* 275:424 */           while (bSelected[iSelected2] != 0) {
/* 276:425 */             iSelected2 = (iSelected2 + 1) % getDescendantPopulationSize();
/* 277:    */           }
/* 278:427 */           if (descendantPopulation[iSelected2].getScore() > descendantPopulation[iSelected].getScore()) {
/* 279:429 */             iSelected = iSelected2;
/* 280:    */           }
/* 281:    */         }
/* 282:    */         else
/* 283:    */         {
/* 284:433 */           while (bSelected[iSelected] != 0) {
/* 285:434 */             iSelected++;
/* 286:    */           }
/* 287:436 */           double fScore = descendantPopulation[iSelected].getScore();
/* 288:437 */           for (int j = 0; j < getDescendantPopulationSize(); j++) {
/* 289:438 */             if ((bSelected[j] == 0) && (descendantPopulation[j].getScore() > fScore))
/* 290:    */             {
/* 291:439 */               fScore = descendantPopulation[j].getScore();
/* 292:440 */               iSelected = j;
/* 293:    */             }
/* 294:    */           }
/* 295:    */         }
/* 296:444 */         population[i] = descendantPopulation[iSelected];
/* 297:445 */         bSelected[iSelected] = true;
/* 298:    */       }
/* 299:    */     }
/* 300:450 */     copyParentSets(bayesNet, bestBayesNet);
/* 301:    */     
/* 302:    */ 
/* 303:453 */     bestBayesNet = null;
/* 304:454 */     this.g_bIsSquare = null;
/* 305:    */   }
/* 306:    */   
/* 307:    */   void copyParentSets(BayesNet dest, BayesNet source)
/* 308:    */   {
/* 309:464 */     int nNodes = source.getNrOfNodes();
/* 310:466 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/* 311:467 */       dest.getParentSet(iNode).copy(source.getParentSet(iNode));
/* 312:    */     }
/* 313:    */   }
/* 314:    */   
/* 315:    */   public int getRuns()
/* 316:    */   {
/* 317:475 */     return this.m_nRuns;
/* 318:    */   }
/* 319:    */   
/* 320:    */   public void setRuns(int nRuns)
/* 321:    */   {
/* 322:484 */     this.m_nRuns = nRuns;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public Enumeration<Option> listOptions()
/* 326:    */   {
/* 327:494 */     Vector<Option> newVector = new Vector(7);
/* 328:    */     
/* 329:496 */     newVector.addElement(new Option("\tPopulation size", "L", 1, "-L <integer>"));
/* 330:    */     
/* 331:498 */     newVector.addElement(new Option("\tDescendant population size", "A", 1, "-A <integer>"));
/* 332:    */     
/* 333:500 */     newVector.addElement(new Option("\tNumber of runs", "U", 1, "-U <integer>"));
/* 334:    */     
/* 335:502 */     newVector.addElement(new Option("\tUse mutation.\n\t(default true)", "M", 0, "-M"));
/* 336:    */     
/* 337:504 */     newVector.addElement(new Option("\tUse cross-over.\n\t(default true)", "C", 0, "-C"));
/* 338:    */     
/* 339:506 */     newVector.addElement(new Option("\tUse tournament selection (true) or maximum subpopulatin (false).\n\t(default false)", "O", 0, "-O"));
/* 340:    */     
/* 341:    */ 
/* 342:    */ 
/* 343:510 */     newVector.addElement(new Option("\tRandom number seed", "R", 1, "-R <seed>"));
/* 344:    */     
/* 345:    */ 
/* 346:513 */     newVector.addAll(Collections.list(super.listOptions()));
/* 347:    */     
/* 348:515 */     return newVector.elements();
/* 349:    */   }
/* 350:    */   
/* 351:    */   public void setOptions(String[] options)
/* 352:    */     throws Exception
/* 353:    */   {
/* 354:583 */     String sPopulationSize = Utils.getOption('L', options);
/* 355:584 */     if (sPopulationSize.length() != 0) {
/* 356:585 */       setPopulationSize(Integer.parseInt(sPopulationSize));
/* 357:    */     }
/* 358:587 */     String sDescendantPopulationSize = Utils.getOption('A', options);
/* 359:588 */     if (sDescendantPopulationSize.length() != 0) {
/* 360:589 */       setDescendantPopulationSize(Integer.parseInt(sDescendantPopulationSize));
/* 361:    */     }
/* 362:591 */     String sRuns = Utils.getOption('U', options);
/* 363:592 */     if (sRuns.length() != 0) {
/* 364:593 */       setRuns(Integer.parseInt(sRuns));
/* 365:    */     }
/* 366:595 */     String sSeed = Utils.getOption('R', options);
/* 367:596 */     if (sSeed.length() != 0) {
/* 368:597 */       setSeed(Integer.parseInt(sSeed));
/* 369:    */     }
/* 370:599 */     setUseMutation(Utils.getFlag('M', options));
/* 371:600 */     setUseCrossOver(Utils.getFlag('C', options));
/* 372:601 */     setUseTournamentSelection(Utils.getFlag('O', options));
/* 373:    */     
/* 374:603 */     super.setOptions(options);
/* 375:    */   }
/* 376:    */   
/* 377:    */   public String[] getOptions()
/* 378:    */   {
/* 379:614 */     Vector<String> options = new Vector();
/* 380:    */     
/* 381:616 */     options.add("-L");
/* 382:617 */     options.add("" + getPopulationSize());
/* 383:    */     
/* 384:619 */     options.add("-A");
/* 385:620 */     options.add("" + getDescendantPopulationSize());
/* 386:    */     
/* 387:622 */     options.add("-U");
/* 388:623 */     options.add("" + getRuns());
/* 389:    */     
/* 390:625 */     options.add("-R");
/* 391:626 */     options.add("" + getSeed());
/* 392:628 */     if (getUseMutation()) {
/* 393:629 */       options.add("-M");
/* 394:    */     }
/* 395:631 */     if (getUseCrossOver()) {
/* 396:632 */       options.add("-C");
/* 397:    */     }
/* 398:634 */     if (getUseTournamentSelection()) {
/* 399:635 */       options.add("-O");
/* 400:    */     }
/* 401:638 */     Collections.addAll(options, super.getOptions());
/* 402:    */     
/* 403:640 */     return (String[])options.toArray(new String[0]);
/* 404:    */   }
/* 405:    */   
/* 406:    */   public boolean getUseCrossOver()
/* 407:    */   {
/* 408:647 */     return this.m_bUseCrossOver;
/* 409:    */   }
/* 410:    */   
/* 411:    */   public boolean getUseMutation()
/* 412:    */   {
/* 413:654 */     return this.m_bUseMutation;
/* 414:    */   }
/* 415:    */   
/* 416:    */   public int getDescendantPopulationSize()
/* 417:    */   {
/* 418:661 */     return this.m_nDescendantPopulationSize;
/* 419:    */   }
/* 420:    */   
/* 421:    */   public int getPopulationSize()
/* 422:    */   {
/* 423:668 */     return this.m_nPopulationSize;
/* 424:    */   }
/* 425:    */   
/* 426:    */   public void setUseCrossOver(boolean bUseCrossOver)
/* 427:    */   {
/* 428:675 */     this.m_bUseCrossOver = bUseCrossOver;
/* 429:    */   }
/* 430:    */   
/* 431:    */   public void setUseMutation(boolean bUseMutation)
/* 432:    */   {
/* 433:682 */     this.m_bUseMutation = bUseMutation;
/* 434:    */   }
/* 435:    */   
/* 436:    */   public boolean getUseTournamentSelection()
/* 437:    */   {
/* 438:690 */     return this.m_bUseTournamentSelection;
/* 439:    */   }
/* 440:    */   
/* 441:    */   public void setUseTournamentSelection(boolean bUseTournamentSelection)
/* 442:    */   {
/* 443:698 */     this.m_bUseTournamentSelection = bUseTournamentSelection;
/* 444:    */   }
/* 445:    */   
/* 446:    */   public void setDescendantPopulationSize(int iDescendantPopulationSize)
/* 447:    */   {
/* 448:705 */     this.m_nDescendantPopulationSize = iDescendantPopulationSize;
/* 449:    */   }
/* 450:    */   
/* 451:    */   public void setPopulationSize(int iPopulationSize)
/* 452:    */   {
/* 453:712 */     this.m_nPopulationSize = iPopulationSize;
/* 454:    */   }
/* 455:    */   
/* 456:    */   public int getSeed()
/* 457:    */   {
/* 458:719 */     return this.m_nSeed;
/* 459:    */   }
/* 460:    */   
/* 461:    */   public void setSeed(int nSeed)
/* 462:    */   {
/* 463:728 */     this.m_nSeed = nSeed;
/* 464:    */   }
/* 465:    */   
/* 466:    */   public String globalInfo()
/* 467:    */   {
/* 468:738 */     return "This Bayes Network learning algorithm uses genetic search for finding a well scoring Bayes network structure. Genetic search works by having a population of Bayes network structures and allow them to mutate and apply cross over to get offspring. The best network structure found during the process is returned.";
/* 469:    */   }
/* 470:    */   
/* 471:    */   public String runsTipText()
/* 472:    */   {
/* 473:748 */     return "Sets the number of generations of Bayes network structure populations.";
/* 474:    */   }
/* 475:    */   
/* 476:    */   public String seedTipText()
/* 477:    */   {
/* 478:755 */     return "Initialization value for random number generator. Setting the seed allows replicability of experiments.";
/* 479:    */   }
/* 480:    */   
/* 481:    */   public String populationSizeTipText()
/* 482:    */   {
/* 483:763 */     return "Sets the size of the population of network structures that is selected each generation.";
/* 484:    */   }
/* 485:    */   
/* 486:    */   public String descendantPopulationSizeTipText()
/* 487:    */   {
/* 488:770 */     return "Sets the size of the population of descendants that is created each generation.";
/* 489:    */   }
/* 490:    */   
/* 491:    */   public String useMutationTipText()
/* 492:    */   {
/* 493:777 */     return "Determines whether mutation is allowed. Mutation flips a bit in the bit representation of the network structure. At least one of mutation or cross-over should be used.";
/* 494:    */   }
/* 495:    */   
/* 496:    */   public String useCrossOverTipText()
/* 497:    */   {
/* 498:786 */     return "Determines whether cross-over is allowed. Cross over combined the bit representations of network structure by taking a random first k bits of oneand adding the remainder of the other. At least one of mutation or cross-over should be used.";
/* 499:    */   }
/* 500:    */   
/* 501:    */   public String useTournamentSelectionTipText()
/* 502:    */   {
/* 503:796 */     return "Determines the method of selecting a population. When set to true, tournament selection is used (pick two at random and the highest is allowed to continue). When set to false, the top scoring network structures are selected.";
/* 504:    */   }
/* 505:    */   
/* 506:    */   public String getRevision()
/* 507:    */   {
/* 508:808 */     return RevisionUtils.extract("$Revision: 11247 $");
/* 509:    */   }
/* 510:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.local.GeneticSearch
 * JD-Core Version:    0.7.0.1
 */