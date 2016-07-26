/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.LinkedList;
/*   5:    */ import java.util.Queue;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.CapabilitiesHandler;
/*   8:    */ import weka.core.Drawable;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.RevisionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Utils;
/*  14:    */ 
/*  15:    */ public class ClassifierTree
/*  16:    */   implements Drawable, Serializable, CapabilitiesHandler, RevisionHandler
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = -8722249377542734193L;
/*  19:    */   protected ModelSelection m_toSelectModel;
/*  20:    */   protected ClassifierSplitModel m_localModel;
/*  21:    */   protected ClassifierTree[] m_sons;
/*  22:    */   protected boolean m_isLeaf;
/*  23:    */   protected boolean m_isEmpty;
/*  24:    */   protected Instances m_train;
/*  25:    */   protected Distribution m_test;
/*  26:    */   protected int m_id;
/*  27: 77 */   private static long PRINTED_NODES = 0L;
/*  28:    */   
/*  29:    */   protected static long nextID()
/*  30:    */   {
/*  31: 86 */     return PRINTED_NODES++;
/*  32:    */   }
/*  33:    */   
/*  34:    */   protected static void resetID()
/*  35:    */   {
/*  36: 95 */     PRINTED_NODES = 0L;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public ClassifierTree(ModelSelection toSelectLocModel)
/*  40:    */   {
/*  41:103 */     this.m_toSelectModel = toSelectLocModel;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Capabilities getCapabilities()
/*  45:    */   {
/*  46:113 */     Capabilities result = new Capabilities(this);
/*  47:114 */     result.enableAll();
/*  48:    */     
/*  49:116 */     return result;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void buildClassifier(Instances data)
/*  53:    */     throws Exception
/*  54:    */   {
/*  55:128 */     getCapabilities().testWithFail(data);
/*  56:    */     
/*  57:    */ 
/*  58:131 */     data = new Instances(data);
/*  59:132 */     data.deleteWithMissingClass();
/*  60:    */     
/*  61:134 */     buildTree(data, false);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void buildTree(Instances data, boolean keepData)
/*  65:    */     throws Exception
/*  66:    */   {
/*  67:148 */     if (keepData) {
/*  68:149 */       this.m_train = data;
/*  69:    */     }
/*  70:151 */     this.m_test = null;
/*  71:152 */     this.m_isLeaf = false;
/*  72:153 */     this.m_isEmpty = false;
/*  73:154 */     this.m_sons = null;
/*  74:155 */     this.m_localModel = this.m_toSelectModel.selectModel(data);
/*  75:156 */     if (this.m_localModel.numSubsets() > 1)
/*  76:    */     {
/*  77:157 */       Instances[] localInstances = this.m_localModel.split(data);
/*  78:158 */       data = null;
/*  79:159 */       this.m_sons = new ClassifierTree[this.m_localModel.numSubsets()];
/*  80:160 */       for (int i = 0; i < this.m_sons.length; i++)
/*  81:    */       {
/*  82:161 */         this.m_sons[i] = getNewTree(localInstances[i]);
/*  83:162 */         localInstances[i] = null;
/*  84:    */       }
/*  85:    */     }
/*  86:    */     else
/*  87:    */     {
/*  88:165 */       this.m_isLeaf = true;
/*  89:166 */       if (Utils.eq(data.sumOfWeights(), 0.0D)) {
/*  90:167 */         this.m_isEmpty = true;
/*  91:    */       }
/*  92:169 */       data = null;
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void buildTree(Instances train, Instances test, boolean keepData)
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:187 */     if (keepData) {
/* 100:188 */       this.m_train = train;
/* 101:    */     }
/* 102:190 */     this.m_isLeaf = false;
/* 103:191 */     this.m_isEmpty = false;
/* 104:192 */     this.m_sons = null;
/* 105:193 */     this.m_localModel = this.m_toSelectModel.selectModel(train, test);
/* 106:194 */     this.m_test = new Distribution(test, this.m_localModel);
/* 107:195 */     if (this.m_localModel.numSubsets() > 1)
/* 108:    */     {
/* 109:196 */       Instances[] localTrain = this.m_localModel.split(train);
/* 110:197 */       Instances[] localTest = this.m_localModel.split(test);
/* 111:198 */       train = null;
/* 112:199 */       test = null;
/* 113:200 */       this.m_sons = new ClassifierTree[this.m_localModel.numSubsets()];
/* 114:201 */       for (int i = 0; i < this.m_sons.length; i++)
/* 115:    */       {
/* 116:202 */         this.m_sons[i] = getNewTree(localTrain[i], localTest[i]);
/* 117:203 */         localTrain[i] = null;
/* 118:204 */         localTest[i] = null;
/* 119:    */       }
/* 120:    */     }
/* 121:207 */     this.m_isLeaf = true;
/* 122:208 */     if (Utils.eq(train.sumOfWeights(), 0.0D)) {
/* 123:209 */       this.m_isEmpty = true;
/* 124:    */     }
/* 125:211 */     train = null;
/* 126:212 */     test = null;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public double classifyInstance(Instance instance)
/* 130:    */     throws Exception
/* 131:    */   {
/* 132:225 */     double maxProb = -1.0D;
/* 133:    */     
/* 134:227 */     int maxIndex = 0;
/* 135:230 */     for (int j = 0; j < instance.numClasses(); j++)
/* 136:    */     {
/* 137:231 */       double currentProb = getProbs(j, instance, 1.0D);
/* 138:232 */       if (Utils.gr(currentProb, maxProb))
/* 139:    */       {
/* 140:233 */         maxIndex = j;
/* 141:234 */         maxProb = currentProb;
/* 142:    */       }
/* 143:    */     }
/* 144:238 */     return maxIndex;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public final void cleanup(Instances justHeaderInfo)
/* 148:    */   {
/* 149:248 */     this.m_train = justHeaderInfo;
/* 150:249 */     this.m_test = null;
/* 151:250 */     if (!this.m_isLeaf) {
/* 152:251 */       for (ClassifierTree m_son : this.m_sons) {
/* 153:252 */         m_son.cleanup(justHeaderInfo);
/* 154:    */       }
/* 155:    */     }
/* 156:    */   }
/* 157:    */   
/* 158:    */   public final double[] distributionForInstance(Instance instance, boolean useLaplace)
/* 159:    */     throws Exception
/* 160:    */   {
/* 161:268 */     double[] doubles = new double[instance.numClasses()];
/* 162:270 */     for (int i = 0; i < doubles.length; i++) {
/* 163:271 */       if (!useLaplace) {
/* 164:272 */         doubles[i] = getProbs(i, instance, 1.0D);
/* 165:    */       } else {
/* 166:274 */         doubles[i] = getProbsLaplace(i, instance, 1.0D);
/* 167:    */       }
/* 168:    */     }
/* 169:278 */     return doubles;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public int assignIDs(int lastID)
/* 173:    */   {
/* 174:289 */     int currLastID = lastID + 1;
/* 175:    */     
/* 176:291 */     this.m_id = currLastID;
/* 177:292 */     if (this.m_sons != null) {
/* 178:293 */       for (ClassifierTree m_son : this.m_sons) {
/* 179:294 */         currLastID = m_son.assignIDs(currLastID);
/* 180:    */       }
/* 181:    */     }
/* 182:297 */     return currLastID;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public int graphType()
/* 186:    */   {
/* 187:307 */     return 1;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String graph()
/* 191:    */     throws Exception
/* 192:    */   {
/* 193:319 */     StringBuffer text = new StringBuffer();
/* 194:    */     
/* 195:321 */     assignIDs(-1);
/* 196:322 */     text.append("digraph J48Tree {\n");
/* 197:323 */     if (this.m_isLeaf)
/* 198:    */     {
/* 199:324 */       text.append("N" + this.m_id + " [label=\"" + Utils.backQuoteChars(this.m_localModel.dumpLabel(0, this.m_train)) + "\" " + "shape=box style=filled ");
/* 200:327 */       if ((this.m_train != null) && (this.m_train.numInstances() > 0))
/* 201:    */       {
/* 202:328 */         text.append("data =\n" + this.m_train + "\n");
/* 203:329 */         text.append(",\n");
/* 204:    */       }
/* 205:332 */       text.append("]\n");
/* 206:    */     }
/* 207:    */     else
/* 208:    */     {
/* 209:334 */       text.append("N" + this.m_id + " [label=\"" + Utils.backQuoteChars(this.m_localModel.leftSide(this.m_train)) + "\" ");
/* 210:336 */       if ((this.m_train != null) && (this.m_train.numInstances() > 0))
/* 211:    */       {
/* 212:337 */         text.append("data =\n" + this.m_train + "\n");
/* 213:338 */         text.append(",\n");
/* 214:    */       }
/* 215:340 */       text.append("]\n");
/* 216:341 */       graphTree(text);
/* 217:    */     }
/* 218:344 */     return text.toString() + "}\n";
/* 219:    */   }
/* 220:    */   
/* 221:    */   public String prefix()
/* 222:    */     throws Exception
/* 223:    */   {
/* 224:357 */     StringBuffer text = new StringBuffer();
/* 225:358 */     if (this.m_isLeaf) {
/* 226:359 */       text.append("[" + this.m_localModel.dumpLabel(0, this.m_train) + "]");
/* 227:    */     } else {
/* 228:361 */       prefixTree(text);
/* 229:    */     }
/* 230:364 */     return text.toString();
/* 231:    */   }
/* 232:    */   
/* 233:    */   public StringBuffer[] toSource(String className)
/* 234:    */     throws Exception
/* 235:    */   {
/* 236:381 */     StringBuffer[] result = new StringBuffer[2];
/* 237:382 */     if (this.m_isLeaf)
/* 238:    */     {
/* 239:383 */       result[0] = new StringBuffer("    p = " + this.m_localModel.distribution().maxClass(0) + ";\n");
/* 240:    */       
/* 241:385 */       result[1] = new StringBuffer("");
/* 242:    */     }
/* 243:    */     else
/* 244:    */     {
/* 245:387 */       StringBuffer text = new StringBuffer();
/* 246:388 */       StringBuffer atEnd = new StringBuffer();
/* 247:    */       
/* 248:390 */       long printID = nextID();
/* 249:    */       
/* 250:392 */       text.append("  static double N").append(Integer.toHexString(this.m_localModel.hashCode()) + printID).append("(Object []i) {\n").append("    double p = Double.NaN;\n");
/* 251:    */       
/* 252:    */ 
/* 253:    */ 
/* 254:396 */       text.append("    if (").append(this.m_localModel.sourceExpression(-1, this.m_train)).append(") {\n");
/* 255:    */       
/* 256:398 */       text.append("      p = ").append(this.m_localModel.distribution().maxClass(0)).append(";\n");
/* 257:    */       
/* 258:400 */       text.append("    } ");
/* 259:401 */       for (int i = 0; i < this.m_sons.length; i++)
/* 260:    */       {
/* 261:402 */         text.append("else if (" + this.m_localModel.sourceExpression(i, this.m_train) + ") {\n");
/* 262:404 */         if (this.m_sons[i].m_isLeaf)
/* 263:    */         {
/* 264:405 */           text.append("      p = " + this.m_localModel.distribution().maxClass(i) + ";\n");
/* 265:    */         }
/* 266:    */         else
/* 267:    */         {
/* 268:408 */           StringBuffer[] sub = this.m_sons[i].toSource(className);
/* 269:409 */           text.append(sub[0]);
/* 270:410 */           atEnd.append(sub[1]);
/* 271:    */         }
/* 272:412 */         text.append("    } ");
/* 273:413 */         if (i == this.m_sons.length - 1) {
/* 274:414 */           text.append('\n');
/* 275:    */         }
/* 276:    */       }
/* 277:418 */       text.append("    return p;\n  }\n");
/* 278:    */       
/* 279:420 */       result[0] = new StringBuffer("    p = " + className + ".N");
/* 280:421 */       result[0].append(Integer.toHexString(this.m_localModel.hashCode()) + printID).append("(i);\n");
/* 281:    */       
/* 282:423 */       result[1] = text.append(atEnd);
/* 283:    */     }
/* 284:425 */     return result;
/* 285:    */   }
/* 286:    */   
/* 287:    */   public int numLeaves()
/* 288:    */   {
/* 289:435 */     int num = 0;
/* 290:438 */     if (this.m_isLeaf) {
/* 291:439 */       return 1;
/* 292:    */     }
/* 293:441 */     for (int i = 0; i < this.m_sons.length; i++) {
/* 294:442 */       num += this.m_sons[i].numLeaves();
/* 295:    */     }
/* 296:446 */     return num;
/* 297:    */   }
/* 298:    */   
/* 299:    */   public int numNodes()
/* 300:    */   {
/* 301:456 */     int no = 1;
/* 302:459 */     if (!this.m_isLeaf) {
/* 303:460 */       for (int i = 0; i < this.m_sons.length; i++) {
/* 304:461 */         no += this.m_sons[i].numNodes();
/* 305:    */       }
/* 306:    */     }
/* 307:465 */     return no;
/* 308:    */   }
/* 309:    */   
/* 310:    */   public String toString()
/* 311:    */   {
/* 312:    */     try
/* 313:    */     {
/* 314:477 */       StringBuffer text = new StringBuffer();
/* 315:479 */       if (this.m_isLeaf)
/* 316:    */       {
/* 317:480 */         text.append(": ");
/* 318:481 */         text.append(this.m_localModel.dumpLabel(0, this.m_train));
/* 319:    */       }
/* 320:    */       else
/* 321:    */       {
/* 322:483 */         dumpTree(0, text);
/* 323:    */       }
/* 324:485 */       text.append("\n\nNumber of Leaves  : \t" + numLeaves() + "\n");
/* 325:486 */       text.append("\nSize of the tree : \t" + numNodes() + "\n");
/* 326:    */       
/* 327:488 */       return text.toString();
/* 328:    */     }
/* 329:    */     catch (Exception e) {}
/* 330:490 */     return "Can't print classification tree.";
/* 331:    */   }
/* 332:    */   
/* 333:    */   protected ClassifierTree getNewTree(Instances data)
/* 334:    */     throws Exception
/* 335:    */   {
/* 336:503 */     ClassifierTree newTree = new ClassifierTree(this.m_toSelectModel);
/* 337:504 */     newTree.buildTree(data, false);
/* 338:    */     
/* 339:506 */     return newTree;
/* 340:    */   }
/* 341:    */   
/* 342:    */   protected ClassifierTree getNewTree(Instances train, Instances test)
/* 343:    */     throws Exception
/* 344:    */   {
/* 345:520 */     ClassifierTree newTree = new ClassifierTree(this.m_toSelectModel);
/* 346:521 */     newTree.buildTree(train, test, false);
/* 347:    */     
/* 348:523 */     return newTree;
/* 349:    */   }
/* 350:    */   
/* 351:    */   private void dumpTree(int depth, StringBuffer text)
/* 352:    */     throws Exception
/* 353:    */   {
/* 354:537 */     for (int i = 0; i < this.m_sons.length; i++)
/* 355:    */     {
/* 356:538 */       text.append("\n");
/* 357:540 */       for (int j = 0; j < depth; j++) {
/* 358:541 */         text.append("|   ");
/* 359:    */       }
/* 360:543 */       text.append(this.m_localModel.leftSide(this.m_train));
/* 361:544 */       text.append(this.m_localModel.rightSide(i, this.m_train));
/* 362:545 */       if (this.m_sons[i].m_isLeaf)
/* 363:    */       {
/* 364:546 */         text.append(": ");
/* 365:547 */         text.append(this.m_localModel.dumpLabel(i, this.m_train));
/* 366:    */       }
/* 367:    */       else
/* 368:    */       {
/* 369:549 */         this.m_sons[i].dumpTree(depth + 1, text);
/* 370:    */       }
/* 371:    */     }
/* 372:    */   }
/* 373:    */   
/* 374:    */   private void graphTree(StringBuffer text)
/* 375:    */     throws Exception
/* 376:    */   {
/* 377:562 */     for (int i = 0; i < this.m_sons.length; i++)
/* 378:    */     {
/* 379:563 */       text.append("N" + this.m_id + "->" + "N" + this.m_sons[i].m_id + " [label=\"" + Utils.backQuoteChars(this.m_localModel.rightSide(i, this.m_train).trim()) + "\"]\n");
/* 380:566 */       if (this.m_sons[i].m_isLeaf)
/* 381:    */       {
/* 382:567 */         text.append("N" + this.m_sons[i].m_id + " [label=\"" + Utils.backQuoteChars(this.m_localModel.dumpLabel(i, this.m_train)) + "\" " + "shape=box style=filled ");
/* 383:570 */         if ((this.m_train != null) && (this.m_train.numInstances() > 0))
/* 384:    */         {
/* 385:571 */           text.append("data =\n" + this.m_sons[i].m_train + "\n");
/* 386:572 */           text.append(",\n");
/* 387:    */         }
/* 388:574 */         text.append("]\n");
/* 389:    */       }
/* 390:    */       else
/* 391:    */       {
/* 392:576 */         text.append("N" + this.m_sons[i].m_id + " [label=\"" + Utils.backQuoteChars(this.m_sons[i].m_localModel.leftSide(this.m_train)) + "\" ");
/* 393:579 */         if ((this.m_train != null) && (this.m_train.numInstances() > 0))
/* 394:    */         {
/* 395:580 */           text.append("data =\n" + this.m_sons[i].m_train + "\n");
/* 396:581 */           text.append(",\n");
/* 397:    */         }
/* 398:583 */         text.append("]\n");
/* 399:584 */         this.m_sons[i].graphTree(text);
/* 400:    */       }
/* 401:    */     }
/* 402:    */   }
/* 403:    */   
/* 404:    */   private void prefixTree(StringBuffer text)
/* 405:    */     throws Exception
/* 406:    */   {
/* 407:597 */     text.append("[");
/* 408:598 */     text.append(this.m_localModel.leftSide(this.m_train) + ":");
/* 409:599 */     for (int i = 0; i < this.m_sons.length; i++)
/* 410:    */     {
/* 411:600 */       if (i > 0) {
/* 412:601 */         text.append(",\n");
/* 413:    */       }
/* 414:603 */       text.append(this.m_localModel.rightSide(i, this.m_train));
/* 415:    */     }
/* 416:605 */     for (int i = 0; i < this.m_sons.length; i++) {
/* 417:606 */       if (this.m_sons[i].m_isLeaf)
/* 418:    */       {
/* 419:607 */         text.append("[");
/* 420:608 */         text.append(this.m_localModel.dumpLabel(i, this.m_train));
/* 421:609 */         text.append("]");
/* 422:    */       }
/* 423:    */       else
/* 424:    */       {
/* 425:611 */         this.m_sons[i].prefixTree(text);
/* 426:    */       }
/* 427:    */     }
/* 428:614 */     text.append("]");
/* 429:    */   }
/* 430:    */   
/* 431:    */   private double getProbsLaplace(int classIndex, Instance instance, double weight)
/* 432:    */     throws Exception
/* 433:    */   {
/* 434:629 */     double prob = 0.0D;
/* 435:631 */     if (this.m_isLeaf) {
/* 436:632 */       return weight * localModel().classProbLaplace(classIndex, instance, -1);
/* 437:    */     }
/* 438:634 */     int treeIndex = localModel().whichSubset(instance);
/* 439:635 */     if (treeIndex == -1)
/* 440:    */     {
/* 441:636 */       double[] weights = localModel().weights(instance);
/* 442:637 */       for (int i = 0; i < this.m_sons.length; i++) {
/* 443:638 */         if (!son(i).m_isEmpty) {
/* 444:639 */           prob += son(i).getProbsLaplace(classIndex, instance, weights[i] * weight);
/* 445:    */         }
/* 446:    */       }
/* 447:643 */       return prob;
/* 448:    */     }
/* 449:645 */     if (son(treeIndex).m_isEmpty) {
/* 450:646 */       return weight * localModel().classProbLaplace(classIndex, instance, treeIndex);
/* 451:    */     }
/* 452:649 */     return son(treeIndex).getProbsLaplace(classIndex, instance, weight);
/* 453:    */   }
/* 454:    */   
/* 455:    */   private double getProbs(int classIndex, Instance instance, double weight)
/* 456:    */     throws Exception
/* 457:    */   {
/* 458:667 */     double prob = 0.0D;
/* 459:669 */     if (this.m_isLeaf) {
/* 460:670 */       return weight * localModel().classProb(classIndex, instance, -1);
/* 461:    */     }
/* 462:672 */     int treeIndex = localModel().whichSubset(instance);
/* 463:673 */     if (treeIndex == -1)
/* 464:    */     {
/* 465:674 */       double[] weights = localModel().weights(instance);
/* 466:675 */       for (int i = 0; i < this.m_sons.length; i++) {
/* 467:676 */         if (!son(i).m_isEmpty) {
/* 468:677 */           prob += son(i).getProbs(classIndex, instance, weights[i] * weight);
/* 469:    */         }
/* 470:    */       }
/* 471:680 */       return prob;
/* 472:    */     }
/* 473:682 */     if (son(treeIndex).m_isEmpty) {
/* 474:683 */       return weight * localModel().classProb(classIndex, instance, treeIndex);
/* 475:    */     }
/* 476:686 */     return son(treeIndex).getProbs(classIndex, instance, weight);
/* 477:    */   }
/* 478:    */   
/* 479:    */   private ClassifierSplitModel localModel()
/* 480:    */   {
/* 481:697 */     return this.m_localModel;
/* 482:    */   }
/* 483:    */   
/* 484:    */   private ClassifierTree son(int index)
/* 485:    */   {
/* 486:705 */     return this.m_sons[index];
/* 487:    */   }
/* 488:    */   
/* 489:    */   public double[] getMembershipValues(Instance instance)
/* 490:    */     throws Exception
/* 491:    */   {
/* 492:714 */     double[] a = new double[numNodes()];
/* 493:    */     
/* 494:    */ 
/* 495:717 */     Queue<Double> queueOfWeights = new LinkedList();
/* 496:718 */     Queue<ClassifierTree> queueOfNodes = new LinkedList();
/* 497:719 */     queueOfWeights.add(Double.valueOf(instance.weight()));
/* 498:720 */     queueOfNodes.add(this);
/* 499:721 */     int index = 0;
/* 500:724 */     while (!queueOfNodes.isEmpty())
/* 501:    */     {
/* 502:726 */       a[(index++)] = ((Double)queueOfWeights.poll()).doubleValue();
/* 503:727 */       ClassifierTree node = (ClassifierTree)queueOfNodes.poll();
/* 504:730 */       if (!node.m_isLeaf)
/* 505:    */       {
/* 506:735 */         int treeIndex = node.localModel().whichSubset(instance);
/* 507:    */         
/* 508:    */ 
/* 509:738 */         double[] weights = new double[node.m_sons.length];
/* 510:741 */         if (treeIndex == -1) {
/* 511:742 */           weights = node.localModel().weights(instance);
/* 512:    */         } else {
/* 513:744 */           weights[treeIndex] = 1.0D;
/* 514:    */         }
/* 515:746 */         for (int i = 0; i < node.m_sons.length; i++)
/* 516:    */         {
/* 517:747 */           queueOfNodes.add(node.son(i));
/* 518:748 */           queueOfWeights.add(Double.valueOf(a[(index - 1)] * weights[i]));
/* 519:    */         }
/* 520:    */       }
/* 521:    */     }
/* 522:751 */     return a;
/* 523:    */   }
/* 524:    */   
/* 525:    */   public String getRevision()
/* 526:    */   {
/* 527:761 */     return RevisionUtils.extract("$Revision: 11269 $");
/* 528:    */   }
/* 529:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.ClassifierTree
 * JD-Core Version:    0.7.0.1
 */