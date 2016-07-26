/*   1:    */ package weka.classifiers.bayes.net.search.local;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.bayes.BayesNet;
/*   9:    */ import weka.classifiers.bayes.net.ParentSet;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.RevisionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ 
/*  16:    */ public class HillClimber
/*  17:    */   extends LocalScoreSearchAlgorithm
/*  18:    */ {
/*  19:    */   static final long serialVersionUID = 4322783593818122403L;
/*  20:    */   
/*  21:    */   class Operation
/*  22:    */     implements Serializable, RevisionHandler
/*  23:    */   {
/*  24:    */     static final long serialVersionUID = -4880888790432547895L;
/*  25:    */     static final int OPERATION_ADD = 0;
/*  26:    */     static final int OPERATION_DEL = 1;
/*  27:    */     static final int OPERATION_REVERSE = 2;
/*  28:    */     public int m_nTail;
/*  29:    */     public int m_nHead;
/*  30:    */     public int m_nOperation;
/*  31:    */     
/*  32:    */     public Operation() {}
/*  33:    */     
/*  34:    */     public Operation(int nTail, int nHead, int nOperation)
/*  35:    */     {
/*  36:116 */       this.m_nHead = nHead;
/*  37:117 */       this.m_nTail = nTail;
/*  38:118 */       this.m_nOperation = nOperation;
/*  39:    */     }
/*  40:    */     
/*  41:    */     public boolean equals(Operation other)
/*  42:    */     {
/*  43:128 */       if (other == null) {
/*  44:129 */         return false;
/*  45:    */       }
/*  46:131 */       return (this.m_nOperation == other.m_nOperation) && (this.m_nHead == other.m_nHead) && (this.m_nTail == other.m_nTail);
/*  47:    */     }
/*  48:    */     
/*  49:145 */     public double m_fDeltaScore = -1.0E+100D;
/*  50:    */     
/*  51:    */     public String getRevision()
/*  52:    */     {
/*  53:154 */       return RevisionUtils.extract("$Revision: 10154 $");
/*  54:    */     }
/*  55:    */   }
/*  56:    */   
/*  57:    */   class Cache
/*  58:    */     implements RevisionHandler
/*  59:    */   {
/*  60:    */     double[][] m_fDeltaScoreAdd;
/*  61:    */     double[][] m_fDeltaScoreDel;
/*  62:    */     
/*  63:    */     Cache(int nNrOfNodes)
/*  64:    */     {
/*  65:175 */       this.m_fDeltaScoreAdd = new double[nNrOfNodes][nNrOfNodes];
/*  66:176 */       this.m_fDeltaScoreDel = new double[nNrOfNodes][nNrOfNodes];
/*  67:    */     }
/*  68:    */     
/*  69:    */     public void put(HillClimber.Operation oOperation, double fValue)
/*  70:    */     {
/*  71:186 */       if (oOperation.m_nOperation == 0) {
/*  72:187 */         this.m_fDeltaScoreAdd[oOperation.m_nTail][oOperation.m_nHead] = fValue;
/*  73:    */       } else {
/*  74:189 */         this.m_fDeltaScoreDel[oOperation.m_nTail][oOperation.m_nHead] = fValue;
/*  75:    */       }
/*  76:    */     }
/*  77:    */     
/*  78:    */     public double get(HillClimber.Operation oOperation)
/*  79:    */     {
/*  80:200 */       switch (oOperation.m_nOperation)
/*  81:    */       {
/*  82:    */       case 0: 
/*  83:202 */         return this.m_fDeltaScoreAdd[oOperation.m_nTail][oOperation.m_nHead];
/*  84:    */       case 1: 
/*  85:204 */         return this.m_fDeltaScoreDel[oOperation.m_nTail][oOperation.m_nHead];
/*  86:    */       case 2: 
/*  87:206 */         return this.m_fDeltaScoreDel[oOperation.m_nTail][oOperation.m_nHead] + this.m_fDeltaScoreAdd[oOperation.m_nHead][oOperation.m_nTail];
/*  88:    */       }
/*  89:210 */       return 0.0D;
/*  90:    */     }
/*  91:    */     
/*  92:    */     public String getRevision()
/*  93:    */     {
/*  94:220 */       return RevisionUtils.extract("$Revision: 10154 $");
/*  95:    */     }
/*  96:    */   }
/*  97:    */   
/*  98:225 */   Cache m_Cache = null;
/*  99:228 */   boolean m_bUseArcReversal = false;
/* 100:    */   
/* 101:    */   protected void search(BayesNet bayesNet, Instances instances)
/* 102:    */     throws Exception
/* 103:    */   {
/* 104:241 */     initCache(bayesNet, instances);
/* 105:    */     
/* 106:    */ 
/* 107:244 */     Operation oOperation = getOptimalOperation(bayesNet, instances);
/* 108:245 */     while ((oOperation != null) && (oOperation.m_fDeltaScore > 0.0D))
/* 109:    */     {
/* 110:246 */       performOperation(bayesNet, instances, oOperation);
/* 111:247 */       oOperation = getOptimalOperation(bayesNet, instances);
/* 112:    */     }
/* 113:251 */     this.m_Cache = null;
/* 114:    */   }
/* 115:    */   
/* 116:    */   void initCache(BayesNet bayesNet, Instances instances)
/* 117:    */     throws Exception
/* 118:    */   {
/* 119:264 */     double[] fBaseScores = new double[instances.numAttributes()];
/* 120:265 */     int nNrOfAtts = instances.numAttributes();
/* 121:    */     
/* 122:267 */     this.m_Cache = new Cache(nNrOfAtts);
/* 123:269 */     for (int iAttribute = 0; iAttribute < nNrOfAtts; iAttribute++) {
/* 124:270 */       updateCache(iAttribute, nNrOfAtts, bayesNet.getParentSet(iAttribute));
/* 125:    */     }
/* 126:273 */     for (int iAttribute = 0; iAttribute < nNrOfAtts; iAttribute++) {
/* 127:274 */       fBaseScores[iAttribute] = calcNodeScore(iAttribute);
/* 128:    */     }
/* 129:277 */     for (int iAttributeHead = 0; iAttributeHead < nNrOfAtts; iAttributeHead++) {
/* 130:278 */       for (int iAttributeTail = 0; iAttributeTail < nNrOfAtts; iAttributeTail++) {
/* 131:279 */         if (iAttributeHead != iAttributeTail)
/* 132:    */         {
/* 133:280 */           Operation oOperation = new Operation(iAttributeTail, iAttributeHead, 0);
/* 134:    */           
/* 135:282 */           this.m_Cache.put(oOperation, calcScoreWithExtraParent(iAttributeHead, iAttributeTail) - fBaseScores[iAttributeHead]);
/* 136:    */         }
/* 137:    */       }
/* 138:    */     }
/* 139:    */   }
/* 140:    */   
/* 141:    */   boolean isNotTabu(Operation oOperation)
/* 142:    */   {
/* 143:299 */     return true;
/* 144:    */   }
/* 145:    */   
/* 146:    */   Operation getOptimalOperation(BayesNet bayesNet, Instances instances)
/* 147:    */     throws Exception
/* 148:    */   {
/* 149:313 */     Operation oBestOperation = new Operation();
/* 150:    */     
/* 151:    */ 
/* 152:316 */     oBestOperation = findBestArcToAdd(bayesNet, instances, oBestOperation);
/* 153:    */     
/* 154:318 */     oBestOperation = findBestArcToDelete(bayesNet, instances, oBestOperation);
/* 155:320 */     if (getUseArcReversal()) {
/* 156:321 */       oBestOperation = findBestArcToReverse(bayesNet, instances, oBestOperation);
/* 157:    */     }
/* 158:325 */     if (oBestOperation.m_fDeltaScore == -1.0E+100D) {
/* 159:326 */       return null;
/* 160:    */     }
/* 161:329 */     return oBestOperation;
/* 162:    */   }
/* 163:    */   
/* 164:    */   void performOperation(BayesNet bayesNet, Instances instances, Operation oOperation)
/* 165:    */     throws Exception
/* 166:    */   {
/* 167:344 */     switch (oOperation.m_nOperation)
/* 168:    */     {
/* 169:    */     case 0: 
/* 170:346 */       applyArcAddition(bayesNet, oOperation.m_nHead, oOperation.m_nTail, instances);
/* 171:348 */       if (bayesNet.getDebug()) {
/* 172:349 */         System.out.print("Add " + oOperation.m_nHead + " -> " + oOperation.m_nTail);
/* 173:    */       }
/* 174:    */       break;
/* 175:    */     case 1: 
/* 176:354 */       applyArcDeletion(bayesNet, oOperation.m_nHead, oOperation.m_nTail, instances);
/* 177:356 */       if (bayesNet.getDebug()) {
/* 178:357 */         System.out.print("Del " + oOperation.m_nHead + " -> " + oOperation.m_nTail);
/* 179:    */       }
/* 180:    */       break;
/* 181:    */     case 2: 
/* 182:362 */       applyArcDeletion(bayesNet, oOperation.m_nHead, oOperation.m_nTail, instances);
/* 183:    */       
/* 184:364 */       applyArcAddition(bayesNet, oOperation.m_nTail, oOperation.m_nHead, instances);
/* 185:366 */       if (bayesNet.getDebug()) {
/* 186:367 */         System.out.print("Rev " + oOperation.m_nHead + " -> " + oOperation.m_nTail);
/* 187:    */       }
/* 188:    */       break;
/* 189:    */     }
/* 190:    */   }
/* 191:    */   
/* 192:    */   void applyArcAddition(BayesNet bayesNet, int iHead, int iTail, Instances instances)
/* 193:    */   {
/* 194:383 */     ParentSet bestParentSet = bayesNet.getParentSet(iHead);
/* 195:384 */     bestParentSet.addParent(iTail, instances);
/* 196:385 */     updateCache(iHead, instances.numAttributes(), bestParentSet);
/* 197:    */   }
/* 198:    */   
/* 199:    */   void applyArcDeletion(BayesNet bayesNet, int iHead, int iTail, Instances instances)
/* 200:    */   {
/* 201:397 */     ParentSet bestParentSet = bayesNet.getParentSet(iHead);
/* 202:398 */     bestParentSet.deleteParent(iTail, instances);
/* 203:399 */     updateCache(iHead, instances.numAttributes(), bestParentSet);
/* 204:    */   }
/* 205:    */   
/* 206:    */   Operation findBestArcToAdd(BayesNet bayesNet, Instances instances, Operation oBestOperation)
/* 207:    */   {
/* 208:414 */     int nNrOfAtts = instances.numAttributes();
/* 209:416 */     for (int iAttributeHead = 0; iAttributeHead < nNrOfAtts; iAttributeHead++) {
/* 210:417 */       if (bayesNet.getParentSet(iAttributeHead).getNrOfParents() < this.m_nMaxNrOfParents) {
/* 211:418 */         for (int iAttributeTail = 0; iAttributeTail < nNrOfAtts; iAttributeTail++) {
/* 212:419 */           if (addArcMakesSense(bayesNet, instances, iAttributeHead, iAttributeTail))
/* 213:    */           {
/* 214:421 */             Operation oOperation = new Operation(iAttributeTail, iAttributeHead, 0);
/* 215:423 */             if ((this.m_Cache.get(oOperation) > oBestOperation.m_fDeltaScore) && 
/* 216:424 */               (isNotTabu(oOperation)))
/* 217:    */             {
/* 218:425 */               oBestOperation = oOperation;
/* 219:426 */               oBestOperation.m_fDeltaScore = this.m_Cache.get(oOperation);
/* 220:    */             }
/* 221:    */           }
/* 222:    */         }
/* 223:    */       }
/* 224:    */     }
/* 225:433 */     return oBestOperation;
/* 226:    */   }
/* 227:    */   
/* 228:    */   Operation findBestArcToDelete(BayesNet bayesNet, Instances instances, Operation oBestOperation)
/* 229:    */   {
/* 230:447 */     int nNrOfAtts = instances.numAttributes();
/* 231:449 */     for (int iNode = 0; iNode < nNrOfAtts; iNode++)
/* 232:    */     {
/* 233:450 */       ParentSet parentSet = bayesNet.getParentSet(iNode);
/* 234:451 */       for (int iParent = 0; iParent < parentSet.getNrOfParents(); iParent++)
/* 235:    */       {
/* 236:452 */         Operation oOperation = new Operation(parentSet.getParent(iParent), iNode, 1);
/* 237:454 */         if ((this.m_Cache.get(oOperation) > oBestOperation.m_fDeltaScore) && 
/* 238:455 */           (isNotTabu(oOperation)))
/* 239:    */         {
/* 240:456 */           oBestOperation = oOperation;
/* 241:457 */           oBestOperation.m_fDeltaScore = this.m_Cache.get(oOperation);
/* 242:    */         }
/* 243:    */       }
/* 244:    */     }
/* 245:462 */     return oBestOperation;
/* 246:    */   }
/* 247:    */   
/* 248:    */   Operation findBestArcToReverse(BayesNet bayesNet, Instances instances, Operation oBestOperation)
/* 249:    */   {
/* 250:477 */     int nNrOfAtts = instances.numAttributes();
/* 251:479 */     for (int iNode = 0; iNode < nNrOfAtts; iNode++)
/* 252:    */     {
/* 253:480 */       ParentSet parentSet = bayesNet.getParentSet(iNode);
/* 254:481 */       for (int iParent = 0; iParent < parentSet.getNrOfParents(); iParent++)
/* 255:    */       {
/* 256:482 */         int iTail = parentSet.getParent(iParent);
/* 257:484 */         if ((reverseArcMakesSense(bayesNet, instances, iNode, iTail)) && (bayesNet.getParentSet(iTail).getNrOfParents() < this.m_nMaxNrOfParents))
/* 258:    */         {
/* 259:487 */           Operation oOperation = new Operation(parentSet.getParent(iParent), iNode, 2);
/* 260:489 */           if ((this.m_Cache.get(oOperation) > oBestOperation.m_fDeltaScore) && 
/* 261:490 */             (isNotTabu(oOperation)))
/* 262:    */           {
/* 263:491 */             oBestOperation = oOperation;
/* 264:492 */             oBestOperation.m_fDeltaScore = this.m_Cache.get(oOperation);
/* 265:    */           }
/* 266:    */         }
/* 267:    */       }
/* 268:    */     }
/* 269:498 */     return oBestOperation;
/* 270:    */   }
/* 271:    */   
/* 272:    */   void updateCache(int iAttributeHead, int nNrOfAtts, ParentSet parentSet)
/* 273:    */   {
/* 274:510 */     double fBaseScore = calcNodeScore(iAttributeHead);
/* 275:511 */     int nNrOfParents = parentSet.getNrOfParents();
/* 276:512 */     for (int iAttributeTail = 0; iAttributeTail < nNrOfAtts; iAttributeTail++) {
/* 277:513 */       if (iAttributeTail != iAttributeHead) {
/* 278:514 */         if (!parentSet.contains(iAttributeTail))
/* 279:    */         {
/* 280:516 */           if (nNrOfParents < this.m_nMaxNrOfParents)
/* 281:    */           {
/* 282:517 */             Operation oOperation = new Operation(iAttributeTail, iAttributeHead, 0);
/* 283:    */             
/* 284:519 */             this.m_Cache.put(oOperation, calcScoreWithExtraParent(iAttributeHead, iAttributeTail) - fBaseScore);
/* 285:    */           }
/* 286:    */         }
/* 287:    */         else
/* 288:    */         {
/* 289:525 */           Operation oOperation = new Operation(iAttributeTail, iAttributeHead, 1);
/* 290:    */           
/* 291:527 */           this.m_Cache.put(oOperation, calcScoreWithMissingParent(iAttributeHead, iAttributeTail) - fBaseScore);
/* 292:    */         }
/* 293:    */       }
/* 294:    */     }
/* 295:    */   }
/* 296:    */   
/* 297:    */   public void setMaxNrOfParents(int nMaxNrOfParents)
/* 298:    */   {
/* 299:541 */     this.m_nMaxNrOfParents = nMaxNrOfParents;
/* 300:    */   }
/* 301:    */   
/* 302:    */   public int getMaxNrOfParents()
/* 303:    */   {
/* 304:550 */     return this.m_nMaxNrOfParents;
/* 305:    */   }
/* 306:    */   
/* 307:    */   public Enumeration<Option> listOptions()
/* 308:    */   {
/* 309:560 */     Vector<Option> newVector = new Vector(4);
/* 310:    */     
/* 311:562 */     newVector.addElement(new Option("\tMaximum number of parents", "P", 1, "-P <nr of parents>"));
/* 312:    */     
/* 313:564 */     newVector.addElement(new Option("\tUse arc reversal operation.\n\t(default false)", "R", 0, "-R"));
/* 314:    */     
/* 315:566 */     newVector.addElement(new Option("\tInitial structure is empty (instead of Naive Bayes)", "N", 0, "-N"));
/* 316:    */     
/* 317:568 */     newVector.addElement(new Option("\tInitial structure specified in XML BIF file", "X", 1, "-X"));
/* 318:    */     
/* 319:    */ 
/* 320:571 */     newVector.addAll(Collections.list(super.listOptions()));
/* 321:    */     
/* 322:573 */     return newVector.elements();
/* 323:    */   }
/* 324:    */   
/* 325:    */   public void setOptions(String[] options)
/* 326:    */     throws Exception
/* 327:    */   {
/* 328:619 */     setUseArcReversal(Utils.getFlag('R', options));
/* 329:    */     
/* 330:621 */     setInitAsNaiveBayes(!Utils.getFlag('N', options));
/* 331:    */     
/* 332:623 */     this.m_sInitalBIFFile = Utils.getOption('X', options);
/* 333:    */     
/* 334:625 */     String sMaxNrOfParents = Utils.getOption('P', options);
/* 335:626 */     if (sMaxNrOfParents.length() != 0) {
/* 336:627 */       setMaxNrOfParents(Integer.parseInt(sMaxNrOfParents));
/* 337:    */     } else {
/* 338:629 */       setMaxNrOfParents(100000);
/* 339:    */     }
/* 340:632 */     super.setOptions(options);
/* 341:    */   }
/* 342:    */   
/* 343:    */   public String[] getOptions()
/* 344:    */   {
/* 345:643 */     Vector<String> options = new Vector();
/* 346:645 */     if (getUseArcReversal()) {
/* 347:646 */       options.add("-R");
/* 348:    */     }
/* 349:649 */     if (!getInitAsNaiveBayes()) {
/* 350:650 */       options.add("-N");
/* 351:    */     }
/* 352:652 */     if ((this.m_sInitalBIFFile != null) && (!this.m_sInitalBIFFile.equals("")))
/* 353:    */     {
/* 354:653 */       options.add("-X");
/* 355:654 */       options.add(this.m_sInitalBIFFile);
/* 356:    */     }
/* 357:657 */     options.add("-P");
/* 358:658 */     options.add("" + this.m_nMaxNrOfParents);
/* 359:    */     
/* 360:660 */     Collections.addAll(options, super.getOptions());
/* 361:    */     
/* 362:662 */     return (String[])options.toArray(new String[0]);
/* 363:    */   }
/* 364:    */   
/* 365:    */   public void setInitAsNaiveBayes(boolean bInitAsNaiveBayes)
/* 366:    */   {
/* 367:671 */     this.m_bInitAsNaiveBayes = bInitAsNaiveBayes;
/* 368:    */   }
/* 369:    */   
/* 370:    */   public boolean getInitAsNaiveBayes()
/* 371:    */   {
/* 372:680 */     return this.m_bInitAsNaiveBayes;
/* 373:    */   }
/* 374:    */   
/* 375:    */   public boolean getUseArcReversal()
/* 376:    */   {
/* 377:689 */     return this.m_bUseArcReversal;
/* 378:    */   }
/* 379:    */   
/* 380:    */   public void setUseArcReversal(boolean bUseArcReversal)
/* 381:    */   {
/* 382:698 */     this.m_bUseArcReversal = bUseArcReversal;
/* 383:    */   }
/* 384:    */   
/* 385:    */   public String globalInfo()
/* 386:    */   {
/* 387:708 */     return "This Bayes Network learning algorithm uses a hill climbing algorithm adding, deleting and reversing arcs. The search is not restricted by an order on the variables (unlike K2). The difference with B and B2 is that this hill climber also considers arrows part of the naive Bayes structure for deletion.";
/* 388:    */   }
/* 389:    */   
/* 390:    */   public String useArcReversalTipText()
/* 391:    */   {
/* 392:718 */     return "When set to true, the arc reversal operation is used in the search.";
/* 393:    */   }
/* 394:    */   
/* 395:    */   public String getRevision()
/* 396:    */   {
/* 397:728 */     return RevisionUtils.extract("$Revision: 10154 $");
/* 398:    */   }
/* 399:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.local.HillClimber
 * JD-Core Version:    0.7.0.1
 */