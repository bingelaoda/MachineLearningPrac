/*   1:    */ package weka.classifiers.bayes.net;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.bayes.net.estimate.BayesNetEstimator;
/*   9:    */ import weka.classifiers.bayes.net.estimate.DiscreteEstimatorBayes;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.DenseInstance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.estimators.Estimator;
/*  18:    */ 
/*  19:    */ public class BayesNetGenerator
/*  20:    */   extends EditableBayesNet
/*  21:    */ {
/*  22: 98 */   int m_nSeed = 1;
/*  23:    */   Random random;
/*  24:    */   static final long serialVersionUID = -7462571170596157720L;
/*  25:    */   
/*  26:    */   public void generateRandomNetwork()
/*  27:    */     throws Exception
/*  28:    */   {
/*  29:120 */     if (this.m_otherBayesNet == null)
/*  30:    */     {
/*  31:122 */       Init(this.m_nNrOfNodes, this.m_nCardinality);
/*  32:123 */       generateRandomNetworkStructure(this.m_nNrOfNodes, this.m_nNrOfArcs);
/*  33:124 */       generateRandomDistributions(this.m_nNrOfNodes, this.m_nCardinality);
/*  34:    */     }
/*  35:    */     else
/*  36:    */     {
/*  37:127 */       this.m_nNrOfNodes = this.m_otherBayesNet.getNrOfNodes();
/*  38:128 */       this.m_ParentSets = this.m_otherBayesNet.getParentSets();
/*  39:129 */       this.m_Distributions = this.m_otherBayesNet.getDistributions();
/*  40:    */       
/*  41:131 */       this.random = new Random(this.m_nSeed);
/*  42:    */       
/*  43:133 */       ArrayList<Attribute> attInfo = new ArrayList(this.m_nNrOfNodes);
/*  44:136 */       for (int iNode = 0; iNode < this.m_nNrOfNodes; iNode++)
/*  45:    */       {
/*  46:137 */         int nValues = this.m_otherBayesNet.getCardinality(iNode);
/*  47:138 */         ArrayList<String> nomStrings = new ArrayList(nValues + 1);
/*  48:139 */         for (int iValue = 0; iValue < nValues; iValue++) {
/*  49:140 */           nomStrings.add(this.m_otherBayesNet.getNodeValue(iNode, iValue));
/*  50:    */         }
/*  51:142 */         Attribute att = new Attribute(this.m_otherBayesNet.getNodeName(iNode), nomStrings);
/*  52:    */         
/*  53:144 */         attInfo.add(att);
/*  54:    */       }
/*  55:147 */       this.m_Instances = new Instances(this.m_otherBayesNet.getName(), attInfo, 100);
/*  56:148 */       this.m_Instances.setClassIndex(this.m_nNrOfNodes - 1);
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void Init(int nNodes, int nValues)
/*  61:    */     throws Exception
/*  62:    */   {
/*  63:160 */     this.random = new Random(this.m_nSeed);
/*  64:    */     
/*  65:162 */     ArrayList<Attribute> attInfo = new ArrayList(nNodes);
/*  66:    */     
/*  67:164 */     ArrayList<String> nomStrings = new ArrayList(nValues + 1);
/*  68:165 */     for (int iValue = 0; iValue < nValues; iValue++) {
/*  69:166 */       nomStrings.add("Value" + (iValue + 1));
/*  70:    */     }
/*  71:169 */     for (int iNode = 0; iNode < nNodes; iNode++)
/*  72:    */     {
/*  73:170 */       Attribute att = new Attribute("Node" + (iNode + 1), nomStrings);
/*  74:171 */       attInfo.add(att);
/*  75:    */     }
/*  76:173 */     this.m_Instances = new Instances("RandomNet", attInfo, 100);
/*  77:174 */     this.m_Instances.setClassIndex(nNodes - 1);
/*  78:175 */     setUseADTree(false);
/*  79:    */     
/*  80:    */ 
/*  81:178 */     initStructure();
/*  82:    */     
/*  83:    */ 
/*  84:181 */     this.m_Distributions = new Estimator[nNodes][1];
/*  85:182 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/*  86:183 */       this.m_Distributions[iNode][0] = new DiscreteEstimatorBayes(nValues, getEstimator().getAlpha());
/*  87:    */     }
/*  88:186 */     this.m_nEvidence = new ArrayList(nNodes);
/*  89:187 */     for (int i = 0; i < nNodes; i++) {
/*  90:188 */       this.m_nEvidence.add(Integer.valueOf(-1));
/*  91:    */     }
/*  92:190 */     this.m_fMarginP = new ArrayList(nNodes);
/*  93:191 */     for (int i = 0; i < nNodes; i++)
/*  94:    */     {
/*  95:192 */       double[] P = new double[getCardinality(i)];
/*  96:193 */       this.m_fMarginP.add(P);
/*  97:    */     }
/*  98:196 */     this.m_nPositionX = new ArrayList(nNodes);
/*  99:197 */     this.m_nPositionY = new ArrayList(nNodes);
/* 100:198 */     for (int iNode = 0; iNode < nNodes; iNode++)
/* 101:    */     {
/* 102:199 */       this.m_nPositionX.add(Integer.valueOf(iNode % 10 * 50));
/* 103:200 */       this.m_nPositionY.add(Integer.valueOf(iNode / 10 * 50));
/* 104:    */     }
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void generateRandomNetworkStructure(int nNodes, int nArcs)
/* 108:    */     throws Exception
/* 109:    */   {
/* 110:214 */     if (nArcs < nNodes - 1) {
/* 111:215 */       throw new Exception("Number of arcs should be at least (nNodes - 1) = " + (nNodes - 1) + " instead of " + nArcs);
/* 112:    */     }
/* 113:218 */     if (nArcs > nNodes * (nNodes - 1) / 2) {
/* 114:219 */       throw new Exception("Number of arcs should be at most nNodes * (nNodes - 1) / 2 = " + nNodes * (nNodes - 1) / 2 + " instead of " + nArcs);
/* 115:    */     }
/* 116:223 */     if (nArcs == 0) {
/* 117:224 */       return;
/* 118:    */     }
/* 119:228 */     generateTree(nNodes);
/* 120:233 */     for (int iArc = nNodes - 1; iArc < nArcs; iArc++)
/* 121:    */     {
/* 122:234 */       boolean bDone = false;
/* 123:235 */       while (!bDone)
/* 124:    */       {
/* 125:236 */         int nNode1 = this.random.nextInt(nNodes);
/* 126:237 */         int nNode2 = this.random.nextInt(nNodes);
/* 127:238 */         if (nNode1 == nNode2) {
/* 128:239 */           nNode2 = (nNode1 + 1) % nNodes;
/* 129:    */         }
/* 130:241 */         if (nNode2 < nNode1)
/* 131:    */         {
/* 132:242 */           int h = nNode1;
/* 133:243 */           nNode1 = nNode2;
/* 134:244 */           nNode2 = h;
/* 135:    */         }
/* 136:246 */         if (!this.m_ParentSets[nNode2].contains(nNode1))
/* 137:    */         {
/* 138:247 */           this.m_ParentSets[nNode2].addParent(nNode1, this.m_Instances);
/* 139:248 */           bDone = true;
/* 140:    */         }
/* 141:    */       }
/* 142:    */     }
/* 143:    */   }
/* 144:    */   
/* 145:    */   void generateTree(int nNodes)
/* 146:    */   {
/* 147:264 */     boolean[] bConnected = new boolean[nNodes];
/* 148:    */     
/* 149:266 */     int nNode1 = this.random.nextInt(nNodes);
/* 150:267 */     int nNode2 = this.random.nextInt(nNodes);
/* 151:268 */     if (nNode1 == nNode2) {
/* 152:269 */       nNode2 = (nNode1 + 1) % nNodes;
/* 153:    */     }
/* 154:271 */     if (nNode2 < nNode1)
/* 155:    */     {
/* 156:272 */       int h = nNode1;
/* 157:273 */       nNode1 = nNode2;
/* 158:274 */       nNode2 = h;
/* 159:    */     }
/* 160:276 */     this.m_ParentSets[nNode2].addParent(nNode1, this.m_Instances);
/* 161:277 */     bConnected[nNode1] = true;
/* 162:278 */     bConnected[nNode2] = true;
/* 163:283 */     for (int iArc = 2; iArc < nNodes; iArc++)
/* 164:    */     {
/* 165:284 */       int nNode = this.random.nextInt(nNodes);
/* 166:285 */       nNode1 = 0;
/* 167:286 */       while (nNode >= 0)
/* 168:    */       {
/* 169:287 */         nNode1 = (nNode1 + 1) % nNodes;
/* 170:288 */         while (bConnected[nNode1] == 0) {
/* 171:289 */           nNode1 = (nNode1 + 1) % nNodes;
/* 172:    */         }
/* 173:291 */         nNode--;
/* 174:    */       }
/* 175:293 */       nNode = this.random.nextInt(nNodes);
/* 176:294 */       nNode2 = 0;
/* 177:295 */       while (nNode >= 0)
/* 178:    */       {
/* 179:296 */         nNode2 = (nNode2 + 1) % nNodes;
/* 180:297 */         while (bConnected[nNode2] != 0) {
/* 181:298 */           nNode2 = (nNode2 + 1) % nNodes;
/* 182:    */         }
/* 183:300 */         nNode--;
/* 184:    */       }
/* 185:302 */       if (nNode2 < nNode1)
/* 186:    */       {
/* 187:303 */         int h = nNode1;
/* 188:304 */         nNode1 = nNode2;
/* 189:305 */         nNode2 = h;
/* 190:    */       }
/* 191:307 */       this.m_ParentSets[nNode2].addParent(nNode1, this.m_Instances);
/* 192:308 */       bConnected[nNode1] = true;
/* 193:309 */       bConnected[nNode2] = true;
/* 194:    */     }
/* 195:    */   }
/* 196:    */   
/* 197:    */   void generateRandomDistributions(int nNodes, int nValues)
/* 198:    */   {
/* 199:323 */     int nMaxParentCardinality = 1;
/* 200:324 */     for (int iAttribute = 0; iAttribute < nNodes; iAttribute++) {
/* 201:325 */       if (this.m_ParentSets[iAttribute].getCardinalityOfParents() > nMaxParentCardinality) {
/* 202:326 */         nMaxParentCardinality = this.m_ParentSets[iAttribute].getCardinalityOfParents();
/* 203:    */       }
/* 204:    */     }
/* 205:332 */     this.m_Distributions = new Estimator[this.m_Instances.numAttributes()][nMaxParentCardinality];
/* 206:335 */     for (int iAttribute = 0; iAttribute < nNodes; iAttribute++)
/* 207:    */     {
/* 208:336 */       int[] nPs = new int[nValues + 1];
/* 209:337 */       nPs[0] = 0;
/* 210:338 */       nPs[nValues] = 1000;
/* 211:339 */       for (int iParent = 0; iParent < this.m_ParentSets[iAttribute].getCardinalityOfParents(); iParent++)
/* 212:    */       {
/* 213:342 */         for (int iValue = 1; iValue < nValues; iValue++) {
/* 214:343 */           nPs[iValue] = this.random.nextInt(1000);
/* 215:    */         }
/* 216:346 */         for (int iValue = 1; iValue < nValues; iValue++) {
/* 217:347 */           for (int iValue2 = iValue + 1; iValue2 < nValues; iValue2++) {
/* 218:348 */             if (nPs[iValue2] < nPs[iValue])
/* 219:    */             {
/* 220:349 */               int h = nPs[iValue2];
/* 221:350 */               nPs[iValue2] = nPs[iValue];
/* 222:351 */               nPs[iValue] = h;
/* 223:    */             }
/* 224:    */           }
/* 225:    */         }
/* 226:356 */         DiscreteEstimatorBayes d = new DiscreteEstimatorBayes(nValues, getEstimator().getAlpha());
/* 227:358 */         for (int iValue = 0; iValue < nValues; iValue++) {
/* 228:359 */           d.addValue(iValue, nPs[(iValue + 1)] - nPs[iValue]);
/* 229:    */         }
/* 230:361 */         this.m_Distributions[iAttribute][iParent] = d;
/* 231:    */       }
/* 232:    */     }
/* 233:    */   }
/* 234:    */   
/* 235:    */   public void generateInstances()
/* 236:    */     throws Exception
/* 237:    */   {
/* 238:374 */     int[] order = getOrder();
/* 239:375 */     for (int iInstance = 0; iInstance < this.m_nNrOfInstances; iInstance++)
/* 240:    */     {
/* 241:376 */       int nNrOfAtts = this.m_Instances.numAttributes();
/* 242:377 */       double[] instance = new double[nNrOfAtts];
/* 243:378 */       for (int iAtt2 = 0; iAtt2 < nNrOfAtts; iAtt2++)
/* 244:    */       {
/* 245:379 */         int iAtt = order[iAtt2];
/* 246:    */         
/* 247:381 */         double iCPT = 0.0D;
/* 248:383 */         for (int iParent = 0; iParent < this.m_ParentSets[iAtt].getNrOfParents(); iParent++)
/* 249:    */         {
/* 250:384 */           int nParent = this.m_ParentSets[iAtt].getParent(iParent);
/* 251:385 */           iCPT = iCPT * this.m_Instances.attribute(nParent).numValues() + instance[nParent];
/* 252:    */         }
/* 253:389 */         double fRandom = this.random.nextInt(1000) / 1000.0F;
/* 254:390 */         int iValue = 0;
/* 255:391 */         while (fRandom > this.m_Distributions[iAtt][((int)iCPT)].getProbability(iValue))
/* 256:    */         {
/* 257:393 */           fRandom -= this.m_Distributions[iAtt][((int)iCPT)].getProbability(iValue);
/* 258:    */           
/* 259:395 */           iValue++;
/* 260:    */         }
/* 261:397 */         instance[iAtt] = iValue;
/* 262:    */       }
/* 263:399 */       this.m_Instances.add(new DenseInstance(1.0D, instance));
/* 264:    */     }
/* 265:    */   }
/* 266:    */   
/* 267:    */   int[] getOrder()
/* 268:    */     throws Exception
/* 269:    */   {
/* 270:407 */     int nNrOfAtts = this.m_Instances.numAttributes();
/* 271:408 */     int[] order = new int[nNrOfAtts];
/* 272:409 */     boolean[] bDone = new boolean[nNrOfAtts];
/* 273:410 */     for (int iAtt = 0; iAtt < nNrOfAtts; iAtt++)
/* 274:    */     {
/* 275:411 */       int iAtt2 = 0;
/* 276:412 */       boolean allParentsDone = false;
/* 277:413 */       while ((!allParentsDone) && (iAtt2 < nNrOfAtts)) {
/* 278:414 */         if (bDone[iAtt2] == 0)
/* 279:    */         {
/* 280:415 */           allParentsDone = true;
/* 281:416 */           int iParent = 0;
/* 282:418 */           while ((allParentsDone) && (iParent < this.m_ParentSets[iAtt2].getNrOfParents())) {
/* 283:419 */             allParentsDone = bDone[this.m_ParentSets[iAtt2].getParent(iParent++)];
/* 284:    */           }
/* 285:421 */           if ((allParentsDone) && (iParent == this.m_ParentSets[iAtt2].getNrOfParents()))
/* 286:    */           {
/* 287:422 */             order[iAtt] = iAtt2;
/* 288:423 */             bDone[iAtt2] = true;
/* 289:    */           }
/* 290:    */           else
/* 291:    */           {
/* 292:425 */             iAtt2++;
/* 293:    */           }
/* 294:    */         }
/* 295:    */         else
/* 296:    */         {
/* 297:428 */           iAtt2++;
/* 298:    */         }
/* 299:    */       }
/* 300:431 */       if ((!allParentsDone) && (iAtt2 == nNrOfAtts)) {
/* 301:432 */         throw new Exception("There appears to be a cycle in the graph");
/* 302:    */       }
/* 303:    */     }
/* 304:435 */     return order;
/* 305:    */   }
/* 306:    */   
/* 307:    */   public String toString()
/* 308:    */   {
/* 309:445 */     if (this.m_bGenerateNet) {
/* 310:446 */       return toXMLBIF03();
/* 311:    */     }
/* 312:448 */     return this.m_Instances.toString();
/* 313:    */   }
/* 314:    */   
/* 315:451 */   boolean m_bGenerateNet = false;
/* 316:452 */   int m_nNrOfNodes = 10;
/* 317:453 */   int m_nNrOfArcs = 10;
/* 318:454 */   int m_nNrOfInstances = 10;
/* 319:455 */   int m_nCardinality = 2;
/* 320:456 */   String m_sBIFFile = "";
/* 321:    */   
/* 322:    */   void setNrOfNodes(int nNrOfNodes)
/* 323:    */   {
/* 324:459 */     this.m_nNrOfNodes = nNrOfNodes;
/* 325:    */   }
/* 326:    */   
/* 327:    */   void setNrOfArcs(int nNrOfArcs)
/* 328:    */   {
/* 329:463 */     this.m_nNrOfArcs = nNrOfArcs;
/* 330:    */   }
/* 331:    */   
/* 332:    */   void setNrOfInstances(int nNrOfInstances)
/* 333:    */   {
/* 334:467 */     this.m_nNrOfInstances = nNrOfInstances;
/* 335:    */   }
/* 336:    */   
/* 337:    */   void setCardinality(int nCardinality)
/* 338:    */   {
/* 339:471 */     this.m_nCardinality = nCardinality;
/* 340:    */   }
/* 341:    */   
/* 342:    */   void setSeed(int nSeed)
/* 343:    */   {
/* 344:475 */     this.m_nSeed = nSeed;
/* 345:    */   }
/* 346:    */   
/* 347:    */   public Enumeration<Option> listOptions()
/* 348:    */   {
/* 349:485 */     Vector<Option> newVector = new Vector(6);
/* 350:    */     
/* 351:487 */     newVector.addElement(new Option("\tGenerate network (instead of instances)\n", "B", 0, "-B"));
/* 352:    */     
/* 353:489 */     newVector.addElement(new Option("\tNr of nodes\n", "N", 1, "-N <integer>"));
/* 354:490 */     newVector.addElement(new Option("\tNr of arcs\n", "A", 1, "-A <integer>"));
/* 355:491 */     newVector.addElement(new Option("\tNr of instances\n", "M", 1, "-M <integer>"));
/* 356:    */     
/* 357:493 */     newVector.addElement(new Option("\tCardinality of the variables\n", "C", 1, "-C <integer>"));
/* 358:    */     
/* 359:495 */     newVector.addElement(new Option("\tSeed for random number generator\n", "S", 1, "-S <integer>"));
/* 360:    */     
/* 361:497 */     newVector.addElement(new Option("\tThe BIF file to obtain the structure from.\n", "F", 1, "-F <file>"));
/* 362:    */     
/* 363:    */ 
/* 364:500 */     return newVector.elements();
/* 365:    */   }
/* 366:    */   
/* 367:    */   public void setOptions(String[] options)
/* 368:    */     throws Exception
/* 369:    */   {
/* 370:552 */     this.m_bGenerateNet = Utils.getFlag('B', options);
/* 371:    */     
/* 372:554 */     String sNrOfNodes = Utils.getOption('N', options);
/* 373:555 */     if (sNrOfNodes.length() != 0) {
/* 374:556 */       setNrOfNodes(Integer.parseInt(sNrOfNodes));
/* 375:    */     } else {
/* 376:558 */       setNrOfNodes(10);
/* 377:    */     }
/* 378:561 */     String sNrOfArcs = Utils.getOption('A', options);
/* 379:562 */     if (sNrOfArcs.length() != 0) {
/* 380:563 */       setNrOfArcs(Integer.parseInt(sNrOfArcs));
/* 381:    */     } else {
/* 382:565 */       setNrOfArcs(10);
/* 383:    */     }
/* 384:568 */     String sNrOfInstances = Utils.getOption('M', options);
/* 385:569 */     if (sNrOfInstances.length() != 0) {
/* 386:570 */       setNrOfInstances(Integer.parseInt(sNrOfInstances));
/* 387:    */     } else {
/* 388:572 */       setNrOfInstances(10);
/* 389:    */     }
/* 390:575 */     String sCardinality = Utils.getOption('C', options);
/* 391:576 */     if (sCardinality.length() != 0) {
/* 392:577 */       setCardinality(Integer.parseInt(sCardinality));
/* 393:    */     } else {
/* 394:579 */       setCardinality(2);
/* 395:    */     }
/* 396:582 */     String sSeed = Utils.getOption('S', options);
/* 397:583 */     if (sSeed.length() != 0) {
/* 398:584 */       setSeed(Integer.parseInt(sSeed));
/* 399:    */     } else {
/* 400:586 */       setSeed(1);
/* 401:    */     }
/* 402:589 */     String sBIFFile = Utils.getOption('F', options);
/* 403:590 */     if ((sBIFFile != null) && (sBIFFile != "")) {
/* 404:591 */       setBIFFile(sBIFFile);
/* 405:    */     }
/* 406:    */   }
/* 407:    */   
/* 408:    */   public String[] getOptions()
/* 409:    */   {
/* 410:602 */     String[] options = new String[13];
/* 411:603 */     int current = 0;
/* 412:604 */     if (this.m_bGenerateNet) {
/* 413:605 */       options[(current++)] = "-B";
/* 414:    */     }
/* 415:608 */     options[(current++)] = "-N";
/* 416:609 */     options[(current++)] = ("" + this.m_nNrOfNodes);
/* 417:    */     
/* 418:611 */     options[(current++)] = "-A";
/* 419:612 */     options[(current++)] = ("" + this.m_nNrOfArcs);
/* 420:    */     
/* 421:614 */     options[(current++)] = "-M";
/* 422:615 */     options[(current++)] = ("" + this.m_nNrOfInstances);
/* 423:    */     
/* 424:617 */     options[(current++)] = "-C";
/* 425:618 */     options[(current++)] = ("" + this.m_nCardinality);
/* 426:    */     
/* 427:620 */     options[(current++)] = "-S";
/* 428:621 */     options[(current++)] = ("" + this.m_nSeed);
/* 429:623 */     if (this.m_sBIFFile.length() != 0)
/* 430:    */     {
/* 431:624 */       options[(current++)] = "-F";
/* 432:625 */       options[(current++)] = ("" + this.m_sBIFFile);
/* 433:    */     }
/* 434:629 */     while (current < options.length) {
/* 435:630 */       options[(current++)] = "";
/* 436:    */     }
/* 437:633 */     return options;
/* 438:    */   }
/* 439:    */   
/* 440:    */   protected static void printOptions(OptionHandler o)
/* 441:    */   {
/* 442:640 */     Enumeration<Option> enm = o.listOptions();
/* 443:    */     
/* 444:642 */     System.out.println("Options for " + o.getClass().getName() + ":\n");
/* 445:644 */     while (enm.hasMoreElements())
/* 446:    */     {
/* 447:645 */       Option option = (Option)enm.nextElement();
/* 448:646 */       System.out.println(option.synopsis());
/* 449:647 */       System.out.println(option.description());
/* 450:    */     }
/* 451:    */   }
/* 452:    */   
/* 453:    */   public String getRevision()
/* 454:    */   {
/* 455:658 */     return RevisionUtils.extract("$Revision: 12448 $");
/* 456:    */   }
/* 457:    */   
/* 458:    */   public static void main(String[] args)
/* 459:    */   {
/* 460:667 */     BayesNetGenerator b = new BayesNetGenerator();
/* 461:    */     try
/* 462:    */     {
/* 463:669 */       if ((args.length == 0) || (Utils.getFlag('h', args)))
/* 464:    */       {
/* 465:670 */         printOptions(b);
/* 466:671 */         return;
/* 467:    */       }
/* 468:673 */       b.setOptions(args);
/* 469:    */       
/* 470:675 */       b.generateRandomNetwork();
/* 471:676 */       if (!b.m_bGenerateNet) {
/* 472:677 */         b.generateInstances();
/* 473:    */       }
/* 474:679 */       System.out.println(b.toString());
/* 475:    */     }
/* 476:    */     catch (Exception e)
/* 477:    */     {
/* 478:681 */       e.printStackTrace();
/* 479:682 */       printOptions(b);
/* 480:    */     }
/* 481:    */   }
/* 482:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.BayesNetGenerator
 * JD-Core Version:    0.7.0.1
 */