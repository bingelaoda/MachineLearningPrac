/*   1:    */ package weka.classifiers.bayes.net.search.local;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.bayes.BayesNet;
/*   7:    */ import weka.classifiers.bayes.net.ADNode;
/*   8:    */ import weka.classifiers.bayes.net.ParentSet;
/*   9:    */ import weka.classifiers.bayes.net.search.SearchAlgorithm;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.SelectedTag;
/*  16:    */ import weka.core.Statistics;
/*  17:    */ import weka.core.Tag;
/*  18:    */ import weka.core.Utils;
/*  19:    */ 
/*  20:    */ public class LocalScoreSearchAlgorithm
/*  21:    */   extends SearchAlgorithm
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = 3325995552474190374L;
/*  24:    */   BayesNet m_BayesNet;
/*  25:    */   
/*  26:    */   public LocalScoreSearchAlgorithm() {}
/*  27:    */   
/*  28:    */   public LocalScoreSearchAlgorithm(BayesNet bayesNet, Instances instances)
/*  29:    */   {
/*  30: 89 */     this.m_BayesNet = bayesNet;
/*  31:    */   }
/*  32:    */   
/*  33: 96 */   double m_fAlpha = 0.5D;
/*  34: 99 */   public static final Tag[] TAGS_SCORE_TYPE = { new Tag(0, "BAYES"), new Tag(1, "BDeu"), new Tag(2, "MDL"), new Tag(3, "ENTROPY"), new Tag(4, "AIC") };
/*  35:107 */   int m_nScoreType = 0;
/*  36:    */   
/*  37:    */   public double logScore(int nType)
/*  38:    */   {
/*  39:117 */     if (this.m_BayesNet.m_Distributions == null) {
/*  40:118 */       return 0.0D;
/*  41:    */     }
/*  42:120 */     if (nType < 0) {
/*  43:121 */       nType = this.m_nScoreType;
/*  44:    */     }
/*  45:124 */     double fLogScore = 0.0D;
/*  46:    */     
/*  47:126 */     Instances instances = this.m_BayesNet.m_Instances;
/*  48:128 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++)
/*  49:    */     {
/*  50:129 */       int nCardinality = this.m_BayesNet.getParentSet(iAttribute).getCardinalityOfParents();
/*  51:131 */       for (int iParent = 0; iParent < nCardinality; iParent++) {
/*  52:132 */         fLogScore += ((Scoreable)this.m_BayesNet.m_Distributions[iAttribute][iParent]).logScore(nType, nCardinality);
/*  53:    */       }
/*  54:136 */       switch (nType)
/*  55:    */       {
/*  56:    */       case 2: 
/*  57:138 */         fLogScore -= 0.5D * this.m_BayesNet.getParentSet(iAttribute).getCardinalityOfParents() * (instances.attribute(iAttribute).numValues() - 1) * Math.log(this.m_BayesNet.getNumInstances());
/*  58:    */         
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62:143 */         break;
/*  63:    */       case 4: 
/*  64:145 */         fLogScore -= this.m_BayesNet.getParentSet(iAttribute).getCardinalityOfParents() * (instances.attribute(iAttribute).numValues() - 1);
/*  65:    */       }
/*  66:    */     }
/*  67:153 */     return fLogScore;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void buildStructure(BayesNet bayesNet, Instances instances)
/*  71:    */     throws Exception
/*  72:    */   {
/*  73:168 */     this.m_BayesNet = bayesNet;
/*  74:169 */     super.buildStructure(bayesNet, instances);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public double calcNodeScore(int nNode)
/*  78:    */   {
/*  79:179 */     if ((this.m_BayesNet.getUseADTree()) && (this.m_BayesNet.getADTree() != null)) {
/*  80:180 */       return calcNodeScoreADTree(nNode);
/*  81:    */     }
/*  82:182 */     return calcNodeScorePlain(nNode);
/*  83:    */   }
/*  84:    */   
/*  85:    */   private double calcNodeScoreADTree(int nNode)
/*  86:    */   {
/*  87:193 */     Instances instances = this.m_BayesNet.m_Instances;
/*  88:194 */     ParentSet oParentSet = this.m_BayesNet.getParentSet(nNode);
/*  89:    */     
/*  90:196 */     int nNrOfParents = oParentSet.getNrOfParents();
/*  91:197 */     int[] nNodes = new int[nNrOfParents + 1];
/*  92:198 */     for (int iParent = 0; iParent < nNrOfParents; iParent++) {
/*  93:199 */       nNodes[iParent] = oParentSet.getParent(iParent);
/*  94:    */     }
/*  95:201 */     nNodes[nNrOfParents] = nNode;
/*  96:    */     
/*  97:    */ 
/*  98:204 */     int[] nOffsets = new int[nNrOfParents + 1];
/*  99:205 */     int nOffset = 1;
/* 100:206 */     nOffsets[nNrOfParents] = 1;
/* 101:207 */     nOffset *= instances.attribute(nNode).numValues();
/* 102:208 */     for (int iNode = nNrOfParents - 1; iNode >= 0; iNode--)
/* 103:    */     {
/* 104:209 */       nOffsets[iNode] = nOffset;
/* 105:210 */       nOffset *= instances.attribute(nNodes[iNode]).numValues();
/* 106:    */     }
/* 107:214 */     for (int iNode = 1; iNode < nNodes.length; iNode++)
/* 108:    */     {
/* 109:215 */       int iNode2 = iNode;
/* 110:216 */       while ((iNode2 > 0) && (nNodes[iNode2] < nNodes[(iNode2 - 1)]))
/* 111:    */       {
/* 112:217 */         int h = nNodes[iNode2];
/* 113:218 */         nNodes[iNode2] = nNodes[(iNode2 - 1)];
/* 114:219 */         nNodes[(iNode2 - 1)] = h;
/* 115:220 */         h = nOffsets[iNode2];
/* 116:221 */         nOffsets[iNode2] = nOffsets[(iNode2 - 1)];
/* 117:222 */         nOffsets[(iNode2 - 1)] = h;
/* 118:223 */         iNode2--;
/* 119:    */       }
/* 120:    */     }
/* 121:228 */     int nCardinality = oParentSet.getCardinalityOfParents();
/* 122:229 */     int numValues = instances.attribute(nNode).numValues();
/* 123:230 */     int[] nCounts = new int[nCardinality * numValues];
/* 124:    */     
/* 125:    */ 
/* 126:233 */     this.m_BayesNet.getADTree().getCounts(nCounts, nNodes, nOffsets, 0, 0, false);
/* 127:    */     
/* 128:235 */     return calcScoreOfCounts(nCounts, nCardinality, numValues, instances);
/* 129:    */   }
/* 130:    */   
/* 131:    */   private double calcNodeScorePlain(int nNode)
/* 132:    */   {
/* 133:239 */     Instances instances = this.m_BayesNet.m_Instances;
/* 134:240 */     ParentSet oParentSet = this.m_BayesNet.getParentSet(nNode);
/* 135:    */     
/* 136:    */ 
/* 137:243 */     int nCardinality = oParentSet.getCardinalityOfParents();
/* 138:244 */     int numValues = instances.attribute(nNode).numValues();
/* 139:245 */     int[] nCounts = new int[nCardinality * numValues];
/* 140:248 */     for (int iParent = 0; iParent < nCardinality * numValues; iParent++) {
/* 141:249 */       nCounts[iParent] = 0;
/* 142:    */     }
/* 143:253 */     Enumeration<Instance> enumInsts = instances.enumerateInstances();
/* 144:255 */     while (enumInsts.hasMoreElements())
/* 145:    */     {
/* 146:256 */       Instance instance = (Instance)enumInsts.nextElement();
/* 147:    */       
/* 148:    */ 
/* 149:259 */       double iCPT = 0.0D;
/* 150:261 */       for (int iParent = 0; iParent < oParentSet.getNrOfParents(); iParent++)
/* 151:    */       {
/* 152:262 */         int nParent = oParentSet.getParent(iParent);
/* 153:    */         
/* 154:264 */         iCPT = iCPT * instances.attribute(nParent).numValues() + instance.value(nParent);
/* 155:    */       }
/* 156:268 */       nCounts[(numValues * (int)iCPT + (int)instance.value(nNode))] += 1;
/* 157:    */     }
/* 158:271 */     return calcScoreOfCounts(nCounts, nCardinality, numValues, instances);
/* 159:    */   }
/* 160:    */   
/* 161:    */   protected double calcScoreOfCounts(int[] nCounts, int nCardinality, int numValues, Instances instances)
/* 162:    */   {
/* 163:288 */     double fLogScore = 0.0D;
/* 164:290 */     for (int iParent = 0; iParent < nCardinality; iParent++) {
/* 165:291 */       switch (this.m_nScoreType)
/* 166:    */       {
/* 167:    */       case 0: 
/* 168:294 */         double nSumOfCounts = 0.0D;
/* 169:296 */         for (int iSymbol = 0; iSymbol < numValues; iSymbol++) {
/* 170:297 */           if (this.m_fAlpha + nCounts[(iParent * numValues + iSymbol)] != 0.0D)
/* 171:    */           {
/* 172:298 */             fLogScore += Statistics.lnGamma(this.m_fAlpha + nCounts[(iParent * numValues + iSymbol)]);
/* 173:    */             
/* 174:300 */             nSumOfCounts += this.m_fAlpha + nCounts[(iParent * numValues + iSymbol)];
/* 175:    */           }
/* 176:    */         }
/* 177:304 */         if (nSumOfCounts != 0.0D) {
/* 178:305 */           fLogScore -= Statistics.lnGamma(nSumOfCounts);
/* 179:    */         }
/* 180:308 */         if (this.m_fAlpha != 0.0D)
/* 181:    */         {
/* 182:309 */           fLogScore -= numValues * Statistics.lnGamma(this.m_fAlpha);
/* 183:310 */           fLogScore += Statistics.lnGamma(numValues * this.m_fAlpha);
/* 184:    */         }
/* 185:314 */         break;
/* 186:    */       case 1: 
/* 187:316 */         double nSumOfCounts = 0.0D;
/* 188:318 */         for (int iSymbol = 0; iSymbol < numValues; iSymbol++) {
/* 189:319 */           if (this.m_fAlpha + nCounts[(iParent * numValues + iSymbol)] != 0.0D)
/* 190:    */           {
/* 191:320 */             fLogScore += Statistics.lnGamma(1.0D / (numValues * nCardinality) + nCounts[(iParent * numValues + iSymbol)]);
/* 192:    */             
/* 193:322 */             nSumOfCounts += 1.0D / (numValues * nCardinality) + nCounts[(iParent * numValues + iSymbol)];
/* 194:    */           }
/* 195:    */         }
/* 196:326 */         fLogScore -= Statistics.lnGamma(nSumOfCounts);
/* 197:    */         
/* 198:328 */         fLogScore -= numValues * Statistics.lnGamma(1.0D / (numValues * nCardinality));
/* 199:    */         
/* 200:330 */         fLogScore += Statistics.lnGamma(1.0D / nCardinality);
/* 201:    */         
/* 202:332 */         break;
/* 203:    */       case 2: 
/* 204:    */       case 3: 
/* 205:    */       case 4: 
/* 206:339 */         double nSumOfCounts = 0.0D;
/* 207:341 */         for (int iSymbol = 0; iSymbol < numValues; iSymbol++) {
/* 208:342 */           nSumOfCounts += nCounts[(iParent * numValues + iSymbol)];
/* 209:    */         }
/* 210:345 */         for (int iSymbol = 0; iSymbol < numValues; iSymbol++) {
/* 211:346 */           if (nCounts[(iParent * numValues + iSymbol)] > 0) {
/* 212:347 */             fLogScore += nCounts[(iParent * numValues + iSymbol)] * Math.log(nCounts[(iParent * numValues + iSymbol)] / nSumOfCounts);
/* 213:    */           }
/* 214:    */         }
/* 215:    */       }
/* 216:    */     }
/* 217:360 */     switch (this.m_nScoreType)
/* 218:    */     {
/* 219:    */     case 2: 
/* 220:363 */       fLogScore -= 0.5D * nCardinality * (numValues - 1) * Math.log(this.m_BayesNet.getNumInstances());
/* 221:    */       
/* 222:    */ 
/* 223:    */ 
/* 224:    */ 
/* 225:    */ 
/* 226:369 */       break;
/* 227:    */     case 4: 
/* 228:372 */       fLogScore -= nCardinality * (numValues - 1);
/* 229:    */     }
/* 230:378 */     return fLogScore;
/* 231:    */   }
/* 232:    */   
/* 233:    */   protected double calcScoreOfCounts2(int[][] nCounts, int nCardinality, int numValues, Instances instances)
/* 234:    */   {
/* 235:385 */     double fLogScore = 0.0D;
/* 236:387 */     for (int iParent = 0; iParent < nCardinality; iParent++) {
/* 237:388 */       switch (this.m_nScoreType)
/* 238:    */       {
/* 239:    */       case 0: 
/* 240:391 */         double nSumOfCounts = 0.0D;
/* 241:393 */         for (int iSymbol = 0; iSymbol < numValues; iSymbol++) {
/* 242:394 */           if (this.m_fAlpha + nCounts[iParent][iSymbol] != 0.0D)
/* 243:    */           {
/* 244:395 */             fLogScore += Statistics.lnGamma(this.m_fAlpha + nCounts[iParent][iSymbol]);
/* 245:    */             
/* 246:397 */             nSumOfCounts += this.m_fAlpha + nCounts[iParent][iSymbol];
/* 247:    */           }
/* 248:    */         }
/* 249:401 */         if (nSumOfCounts != 0.0D) {
/* 250:402 */           fLogScore -= Statistics.lnGamma(nSumOfCounts);
/* 251:    */         }
/* 252:405 */         if (this.m_fAlpha != 0.0D)
/* 253:    */         {
/* 254:406 */           fLogScore -= numValues * Statistics.lnGamma(this.m_fAlpha);
/* 255:407 */           fLogScore += Statistics.lnGamma(numValues * this.m_fAlpha);
/* 256:    */         }
/* 257:411 */         break;
/* 258:    */       case 1: 
/* 259:414 */         double nSumOfCounts = 0.0D;
/* 260:416 */         for (int iSymbol = 0; iSymbol < numValues; iSymbol++) {
/* 261:417 */           if (this.m_fAlpha + nCounts[iParent][iSymbol] != 0.0D)
/* 262:    */           {
/* 263:418 */             fLogScore += Statistics.lnGamma(1.0D / (numValues * nCardinality) + nCounts[iParent][iSymbol]);
/* 264:    */             
/* 265:420 */             nSumOfCounts += 1.0D / (numValues * nCardinality) + nCounts[iParent][iSymbol];
/* 266:    */           }
/* 267:    */         }
/* 268:424 */         fLogScore -= Statistics.lnGamma(nSumOfCounts);
/* 269:    */         
/* 270:426 */         fLogScore -= numValues * Statistics.lnGamma(1.0D / (nCardinality * numValues));
/* 271:    */         
/* 272:428 */         fLogScore += Statistics.lnGamma(1.0D / nCardinality);
/* 273:    */         
/* 274:430 */         break;
/* 275:    */       case 2: 
/* 276:    */       case 3: 
/* 277:    */       case 4: 
/* 278:437 */         double nSumOfCounts = 0.0D;
/* 279:439 */         for (int iSymbol = 0; iSymbol < numValues; iSymbol++) {
/* 280:440 */           nSumOfCounts += nCounts[iParent][iSymbol];
/* 281:    */         }
/* 282:443 */         for (int iSymbol = 0; iSymbol < numValues; iSymbol++) {
/* 283:444 */           if (nCounts[iParent][iSymbol] > 0) {
/* 284:445 */             fLogScore += nCounts[iParent][iSymbol] * Math.log(nCounts[iParent][iSymbol] / nSumOfCounts);
/* 285:    */           }
/* 286:    */         }
/* 287:    */       }
/* 288:    */     }
/* 289:458 */     switch (this.m_nScoreType)
/* 290:    */     {
/* 291:    */     case 2: 
/* 292:461 */       fLogScore -= 0.5D * nCardinality * (numValues - 1) * Math.log(this.m_BayesNet.getNumInstances());
/* 293:    */       
/* 294:    */ 
/* 295:    */ 
/* 296:    */ 
/* 297:    */ 
/* 298:467 */       break;
/* 299:    */     case 4: 
/* 300:470 */       fLogScore -= nCardinality * (numValues - 1);
/* 301:    */     }
/* 302:476 */     return fLogScore;
/* 303:    */   }
/* 304:    */   
/* 305:    */   public double calcScoreWithExtraParent(int nNode, int nCandidateParent)
/* 306:    */   {
/* 307:487 */     ParentSet oParentSet = this.m_BayesNet.getParentSet(nNode);
/* 308:490 */     if (oParentSet.contains(nCandidateParent)) {
/* 309:491 */       return -1.0E+100D;
/* 310:    */     }
/* 311:495 */     oParentSet.addParent(nCandidateParent, this.m_BayesNet.m_Instances);
/* 312:    */     
/* 313:    */ 
/* 314:498 */     double logScore = calcNodeScore(nNode);
/* 315:    */     
/* 316:    */ 
/* 317:501 */     oParentSet.deleteLastParent(this.m_BayesNet.m_Instances);
/* 318:    */     
/* 319:503 */     return logScore;
/* 320:    */   }
/* 321:    */   
/* 322:    */   public double calcScoreWithMissingParent(int nNode, int nCandidateParent)
/* 323:    */   {
/* 324:515 */     ParentSet oParentSet = this.m_BayesNet.getParentSet(nNode);
/* 325:518 */     if (!oParentSet.contains(nCandidateParent)) {
/* 326:519 */       return -1.0E+100D;
/* 327:    */     }
/* 328:523 */     int iParent = oParentSet.deleteParent(nCandidateParent, this.m_BayesNet.m_Instances);
/* 329:    */     
/* 330:    */ 
/* 331:    */ 
/* 332:527 */     double logScore = calcNodeScore(nNode);
/* 333:    */     
/* 334:    */ 
/* 335:530 */     oParentSet.addParent(nCandidateParent, iParent, this.m_BayesNet.m_Instances);
/* 336:    */     
/* 337:532 */     return logScore;
/* 338:    */   }
/* 339:    */   
/* 340:    */   public void setScoreType(SelectedTag newScoreType)
/* 341:    */   {
/* 342:541 */     if (newScoreType.getTags() == TAGS_SCORE_TYPE) {
/* 343:542 */       this.m_nScoreType = newScoreType.getSelectedTag().getID();
/* 344:    */     }
/* 345:    */   }
/* 346:    */   
/* 347:    */   public SelectedTag getScoreType()
/* 348:    */   {
/* 349:552 */     return new SelectedTag(this.m_nScoreType, TAGS_SCORE_TYPE);
/* 350:    */   }
/* 351:    */   
/* 352:    */   public void setMarkovBlanketClassifier(boolean bMarkovBlanketClassifier)
/* 353:    */   {
/* 354:561 */     super.setMarkovBlanketClassifier(bMarkovBlanketClassifier);
/* 355:    */   }
/* 356:    */   
/* 357:    */   public boolean getMarkovBlanketClassifier()
/* 358:    */   {
/* 359:570 */     return super.getMarkovBlanketClassifier();
/* 360:    */   }
/* 361:    */   
/* 362:    */   public Enumeration<Option> listOptions()
/* 363:    */   {
/* 364:580 */     Vector<Option> newVector = new Vector();
/* 365:    */     
/* 366:582 */     newVector.addElement(new Option("\tApplies a Markov Blanket correction to the network structure, \n\tafter a network structure is learned. This ensures that all \n\tnodes in the network are part of the Markov blanket of the \n\tclassifier node.", "mbc", 0, "-mbc"));
/* 367:    */     
/* 368:    */ 
/* 369:    */ 
/* 370:    */ 
/* 371:    */ 
/* 372:588 */     newVector.addElement(new Option("\tScore type (BAYES, BDeu, MDL, ENTROPY and AIC)", "S", 1, "-S [BAYES|MDL|ENTROPY|AIC|CROSS_CLASSIC|CROSS_BAYES]"));
/* 373:    */     
/* 374:    */ 
/* 375:    */ 
/* 376:592 */     newVector.addAll(Collections.list(super.listOptions()));
/* 377:    */     
/* 378:594 */     return newVector.elements();
/* 379:    */   }
/* 380:    */   
/* 381:    */   public void setOptions(String[] options)
/* 382:    */     throws Exception
/* 383:    */   {
/* 384:625 */     setMarkovBlanketClassifier(Utils.getFlag("mbc", options));
/* 385:    */     
/* 386:627 */     String sScore = Utils.getOption('S', options);
/* 387:629 */     if (sScore.compareTo("BAYES") == 0) {
/* 388:630 */       setScoreType(new SelectedTag(0, TAGS_SCORE_TYPE));
/* 389:    */     }
/* 390:632 */     if (sScore.compareTo("BDeu") == 0) {
/* 391:633 */       setScoreType(new SelectedTag(1, TAGS_SCORE_TYPE));
/* 392:    */     }
/* 393:635 */     if (sScore.compareTo("MDL") == 0) {
/* 394:636 */       setScoreType(new SelectedTag(2, TAGS_SCORE_TYPE));
/* 395:    */     }
/* 396:638 */     if (sScore.compareTo("ENTROPY") == 0) {
/* 397:639 */       setScoreType(new SelectedTag(3, TAGS_SCORE_TYPE));
/* 398:    */     }
/* 399:641 */     if (sScore.compareTo("AIC") == 0) {
/* 400:642 */       setScoreType(new SelectedTag(4, TAGS_SCORE_TYPE));
/* 401:    */     }
/* 402:645 */     super.setOptions(options);
/* 403:    */   }
/* 404:    */   
/* 405:    */   public String[] getOptions()
/* 406:    */   {
/* 407:656 */     Vector<String> options = new Vector();
/* 408:658 */     if (getMarkovBlanketClassifier()) {
/* 409:659 */       options.add("-mbc");
/* 410:    */     }
/* 411:662 */     options.add("-S");
/* 412:664 */     switch (this.m_nScoreType)
/* 413:    */     {
/* 414:    */     case 0: 
/* 415:667 */       options.add("BAYES");
/* 416:668 */       break;
/* 417:    */     case 1: 
/* 418:671 */       options.add("BDeu");
/* 419:672 */       break;
/* 420:    */     case 2: 
/* 421:675 */       options.add("MDL");
/* 422:676 */       break;
/* 423:    */     case 3: 
/* 424:679 */       options.add("ENTROPY");
/* 425:    */       
/* 426:681 */       break;
/* 427:    */     case 4: 
/* 428:684 */       options.add("AIC");
/* 429:    */     }
/* 430:688 */     Collections.addAll(options, super.getOptions());
/* 431:    */     
/* 432:690 */     return (String[])options.toArray(new String[0]);
/* 433:    */   }
/* 434:    */   
/* 435:    */   public String scoreTypeTipText()
/* 436:    */   {
/* 437:697 */     return "The score type determines the measure used to judge the quality of a network structure. It can be one of Bayes, BDeu, Minimum Description Length (MDL), Akaike Information Criterion (AIC), and Entropy.";
/* 438:    */   }
/* 439:    */   
/* 440:    */   public String markovBlanketClassifierTipText()
/* 441:    */   {
/* 442:707 */     return super.markovBlanketClassifierTipText();
/* 443:    */   }
/* 444:    */   
/* 445:    */   public String globalInfo()
/* 446:    */   {
/* 447:716 */     return "The ScoreBasedSearchAlgorithm class supports Bayes net structure search algorithms that are based on maximizing scores (as opposed to for example conditional independence based search algorithms).";
/* 448:    */   }
/* 449:    */   
/* 450:    */   public String getRevision()
/* 451:    */   {
/* 452:729 */     return RevisionUtils.extract("$Revision: 10378 $");
/* 453:    */   }
/* 454:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.local.LocalScoreSearchAlgorithm
 * JD-Core Version:    0.7.0.1
 */