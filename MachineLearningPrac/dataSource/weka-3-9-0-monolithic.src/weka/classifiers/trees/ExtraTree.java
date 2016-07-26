/*   1:    */ package weka.classifiers.trees;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.LinkedList;
/*   8:    */ import java.util.Queue;
/*   9:    */ import java.util.Random;
/*  10:    */ import java.util.Vector;
/*  11:    */ import weka.classifiers.RandomizableClassifier;
/*  12:    */ import weka.core.Attribute;
/*  13:    */ import weka.core.Capabilities;
/*  14:    */ import weka.core.Capabilities.Capability;
/*  15:    */ import weka.core.ContingencyTables;
/*  16:    */ import weka.core.Instance;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Option;
/*  19:    */ import weka.core.OptionHandler;
/*  20:    */ import weka.core.PartitionGenerator;
/*  21:    */ import weka.core.RevisionUtils;
/*  22:    */ import weka.core.TechnicalInformation;
/*  23:    */ import weka.core.TechnicalInformation.Field;
/*  24:    */ import weka.core.TechnicalInformation.Type;
/*  25:    */ import weka.core.TechnicalInformationHandler;
/*  26:    */ import weka.core.Utils;
/*  27:    */ import weka.core.WeightedInstancesHandler;
/*  28:    */ 
/*  29:    */ public class ExtraTree
/*  30:    */   extends RandomizableClassifier
/*  31:    */   implements Serializable, OptionHandler, TechnicalInformationHandler, WeightedInstancesHandler, PartitionGenerator
/*  32:    */ {
/*  33:    */   static final long serialVersionUID = 7354290459723928536L;
/*  34:    */   protected Tree m_tree;
/*  35:    */   protected int m_n_min;
/*  36:    */   protected int m_K;
/*  37:    */   
/*  38:    */   public ExtraTree()
/*  39:    */   {
/*  40:115 */     this.m_tree = null;
/*  41:    */     
/*  42:    */ 
/*  43:118 */     this.m_n_min = -1;
/*  44:    */     
/*  45:    */ 
/*  46:121 */     this.m_K = -1;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public String globalInfo()
/*  50:    */   {
/*  51:131 */     return "Class for generating a single Extra-Tree. Use with the RandomCommittee meta classifier to generate an Extra-Trees forest for classification or regression. This classifier requires all predictors to be numeric. Missing values are not allowed. Instance weights are taken into account. For more information, see\n\n" + getTechnicalInformation().toString();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public TechnicalInformation getTechnicalInformation()
/*  55:    */   {
/*  56:149 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  57:150 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Pierre Geurts and Damien Ernst and Louis Wehenkel");
/*  58:    */     
/*  59:152 */     result.setValue(TechnicalInformation.Field.TITLE, "Extremely randomized trees");
/*  60:153 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  61:154 */     result.setValue(TechnicalInformation.Field.YEAR, "2006");
/*  62:155 */     result.setValue(TechnicalInformation.Field.VOLUME, "63");
/*  63:156 */     result.setValue(TechnicalInformation.Field.PAGES, "3-42");
/*  64:157 */     result.setValue(TechnicalInformation.Field.NUMBER, "1");
/*  65:    */     
/*  66:159 */     return result;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String kTipText()
/*  70:    */   {
/*  71:169 */     return "Number of attributes to randomly choose at a node. If values is -1, (m - 1) will be used for regression problems, and Math.rint(sqrt(m - 1)) for classification problems, where m is the number of predictors, as specified in Geurts et al.";
/*  72:    */   }
/*  73:    */   
/*  74:    */   public int getK()
/*  75:    */   {
/*  76:182 */     return this.m_K;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void setK(int k)
/*  80:    */   {
/*  81:192 */     this.m_K = k;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public String nminTipText()
/*  85:    */   {
/*  86:202 */     return "The minimum number of instances required at a node for splitting to be considered. If value is -1, 5 will be used for regression problems and 2 for classification problems, as specified in Geurts et al.";
/*  87:    */   }
/*  88:    */   
/*  89:    */   public int getNmin()
/*  90:    */   {
/*  91:214 */     return this.m_n_min;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setNmin(int n)
/*  95:    */   {
/*  96:224 */     this.m_n_min = n;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public Enumeration<Option> listOptions()
/* 100:    */   {
/* 101:235 */     Vector<Option> newVector = new Vector();
/* 102:    */     
/* 103:237 */     newVector.addElement(new Option("\t" + kTipText() + " (default -1).", "K", 1, "-K <number of attributes>"));
/* 104:    */     
/* 105:    */ 
/* 106:240 */     newVector.addElement(new Option("\t" + nminTipText() + " (default -1).", "N", 1, "-N <minimum number of instances>"));
/* 107:    */     
/* 108:    */ 
/* 109:243 */     newVector.addAll(Collections.list(super.listOptions()));
/* 110:    */     
/* 111:245 */     return newVector.elements();
/* 112:    */   }
/* 113:    */   
/* 114:    */   public String[] getOptions()
/* 115:    */   {
/* 116:258 */     Vector<String> result = new Vector();
/* 117:    */     
/* 118:260 */     result.add("-K");
/* 119:261 */     result.add("" + getK());
/* 120:    */     
/* 121:263 */     result.add("-N");
/* 122:264 */     result.add("" + getNmin());
/* 123:    */     
/* 124:266 */     Collections.addAll(result, super.getOptions());
/* 125:    */     
/* 126:268 */     return (String[])result.toArray(new String[result.size()]);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void setOptions(String[] options)
/* 130:    */     throws Exception
/* 131:    */   {
/* 132:309 */     String tmpStr = Utils.getOption('K', options);
/* 133:310 */     if (tmpStr.length() != 0) {
/* 134:311 */       this.m_K = Integer.parseInt(tmpStr);
/* 135:    */     } else {
/* 136:313 */       this.m_K = -1;
/* 137:    */     }
/* 138:316 */     tmpStr = Utils.getOption('N', options);
/* 139:317 */     if (tmpStr.length() != 0) {
/* 140:318 */       this.m_n_min = Integer.parseInt(tmpStr);
/* 141:    */     } else {
/* 142:320 */       this.m_n_min = -1;
/* 143:    */     }
/* 144:323 */     super.setOptions(options);
/* 145:    */     
/* 146:325 */     Utils.checkForRemainingOptions(options);
/* 147:    */   }
/* 148:    */   
/* 149:    */   protected class Tree
/* 150:    */     implements Serializable
/* 151:    */   {
/* 152:    */     private static final long serialVersionUID = 2396257956703850154L;
/* 153:    */     protected double[] m_dist;
/* 154:    */     protected int m_attIndex;
/* 155:    */     protected double m_splitPoint;
/* 156:    */     protected Tree[] m_successors;
/* 157:    */     
/* 158:    */     protected Tree(Instances data, Random rand)
/* 159:    */     {
/* 160:354 */       ArrayList<Integer> al = eligibleAttributes(data);
/* 161:355 */       if (al == null)
/* 162:    */       {
/* 163:358 */         if (data.classAttribute().isNumeric())
/* 164:    */         {
/* 165:359 */           this.m_dist = new double[1];
/* 166:360 */           this.m_dist[0] = data.meanOrMode(data.classIndex());
/* 167:    */         }
/* 168:    */         else
/* 169:    */         {
/* 170:362 */           this.m_dist = new double[data.numClasses()];
/* 171:363 */           for (int i = 0; i < data.numInstances(); i++) {
/* 172:364 */             this.m_dist[((int)data.instance(i).classValue())] += data.instance(i).weight();
/* 173:    */           }
/* 174:367 */           Utils.normalize(this.m_dist);
/* 175:    */         }
/* 176:    */       }
/* 177:    */       else
/* 178:    */       {
/* 179:372 */         int actualK = ExtraTree.this.m_K;
/* 180:373 */         if (ExtraTree.this.m_K == -1) {
/* 181:374 */           if (data.classAttribute().isNumeric()) {
/* 182:375 */             actualK = data.numAttributes() - 1;
/* 183:    */           } else {
/* 184:377 */             actualK = (int)Math.rint(Math.sqrt(data.numAttributes() - 1));
/* 185:    */           }
/* 186:    */         }
/* 187:382 */         int k = 0;
/* 188:383 */         double bestQuality = -1.797693134862316E+308D;
/* 189:384 */         while ((k < actualK) && (al.size() > 0))
/* 190:    */         {
/* 191:385 */           k++;
/* 192:    */           
/* 193:    */ 
/* 194:388 */           int randIndex = rand.nextInt(al.size());
/* 195:389 */           int attIndex = ((Integer)al.get(randIndex)).intValue();
/* 196:390 */           al.remove(randIndex);
/* 197:    */           
/* 198:    */ 
/* 199:393 */           double min = 1.7976931348623157E+308D;
/* 200:394 */           double max = -1.797693134862316E+308D;
/* 201:395 */           for (int i = 0; i < data.numInstances(); i++)
/* 202:    */           {
/* 203:396 */             double val = data.instance(i).value(attIndex);
/* 204:397 */             if (val < min) {
/* 205:398 */               min = val;
/* 206:    */             }
/* 207:400 */             if (val > max) {
/* 208:401 */               max = val;
/* 209:    */             }
/* 210:    */           }
/* 211:404 */           double splitPoint = rand.nextDouble() * (max - min) + min;
/* 212:    */           
/* 213:    */ 
/* 214:407 */           double splitQuality = splitQuality(data, attIndex, splitPoint);
/* 215:410 */           if (splitQuality > bestQuality)
/* 216:    */           {
/* 217:411 */             bestQuality = splitQuality;
/* 218:412 */             this.m_attIndex = attIndex;
/* 219:413 */             this.m_splitPoint = splitPoint;
/* 220:    */           }
/* 221:    */         }
/* 222:418 */         this.m_successors = new Tree[2];
/* 223:419 */         for (int i = 0; i < 2; i++)
/* 224:    */         {
/* 225:420 */           Instances tempData = new Instances(data, data.numInstances());
/* 226:421 */           for (int j = 0; j < data.numInstances(); j++)
/* 227:    */           {
/* 228:422 */             if ((i == 0) && (data.instance(j).value(this.m_attIndex) < this.m_splitPoint)) {
/* 229:423 */               tempData.add(data.instance(j));
/* 230:    */             }
/* 231:425 */             if ((i == 1) && (data.instance(j).value(this.m_attIndex) >= this.m_splitPoint)) {
/* 232:427 */               tempData.add(data.instance(j));
/* 233:    */             }
/* 234:    */           }
/* 235:430 */           tempData.compactify();
/* 236:431 */           this.m_successors[i] = new Tree(ExtraTree.this, tempData, rand);
/* 237:    */         }
/* 238:    */       }
/* 239:    */     }
/* 240:    */     
/* 241:    */     protected double splitQuality(Instances data, int attIndex, double splitPoint)
/* 242:    */     {
/* 243:443 */       double[][] dist = new double[2][data.numClasses()];
/* 244:444 */       double numLeft = 0.0D;
/* 245:445 */       double mean = 0.0D;
/* 246:446 */       double sumOfWeights = 0.0D;
/* 247:447 */       for (int i = 0; i < data.numInstances(); i++)
/* 248:    */       {
/* 249:448 */         Instance inst = data.instance(i);
/* 250:449 */         double weight = inst.weight();
/* 251:450 */         if (data.classAttribute().isNominal())
/* 252:    */         {
/* 253:451 */           if (inst.value(attIndex) < splitPoint) {
/* 254:452 */             dist[0][((int)inst.classValue())] += weight;
/* 255:    */           }
/* 256:454 */           if (inst.value(attIndex) >= splitPoint) {
/* 257:455 */             dist[1][((int)inst.classValue())] += weight;
/* 258:    */           }
/* 259:    */         }
/* 260:    */         else
/* 261:    */         {
/* 262:458 */           sumOfWeights += weight;
/* 263:459 */           mean += weight * inst.classValue();
/* 264:460 */           if (inst.value(attIndex) < splitPoint)
/* 265:    */           {
/* 266:461 */             dist[0][0] += weight * inst.classValue();
/* 267:462 */             numLeft += weight;
/* 268:    */           }
/* 269:464 */           if (inst.value(attIndex) >= splitPoint) {
/* 270:465 */             dist[1][0] += weight * inst.classValue();
/* 271:    */           }
/* 272:    */         }
/* 273:    */       }
/* 274:471 */       if (data.classAttribute().isNominal())
/* 275:    */       {
/* 276:472 */         double[][] table = new double[2][0];
/* 277:473 */         table[0] = dist[0];
/* 278:474 */         table[1] = dist[1];
/* 279:475 */         return ContingencyTables.symmetricalUncertainty(table);
/* 280:    */       }
/* 281:477 */       double[] var = new double[2];
/* 282:478 */       double priorVar = 0.0D;
/* 283:479 */       if (mean > 0.0D) {
/* 284:480 */         mean /= sumOfWeights;
/* 285:    */       }
/* 286:482 */       if (numLeft > 0.0D) {
/* 287:483 */         dist[0][0] /= numLeft;
/* 288:    */       }
/* 289:485 */       if (sumOfWeights - numLeft > 0.0D) {
/* 290:486 */         dist[1][0] /= (sumOfWeights - numLeft);
/* 291:    */       }
/* 292:488 */       for (int i = 0; i < data.numInstances(); i++)
/* 293:    */       {
/* 294:489 */         Instance inst = data.instance(i);
/* 295:490 */         double weight = inst.weight();
/* 296:491 */         if (inst.value(attIndex) < splitPoint)
/* 297:    */         {
/* 298:492 */           double diff = inst.classValue() - dist[0][0];
/* 299:493 */           var[0] += weight * diff * diff;
/* 300:    */         }
/* 301:495 */         if (inst.value(attIndex) >= splitPoint)
/* 302:    */         {
/* 303:496 */           double diff = inst.classValue() - dist[1][0];
/* 304:497 */           var[1] += weight * diff * diff;
/* 305:    */         }
/* 306:499 */         double diffGlobal = inst.classValue() - mean;
/* 307:500 */         priorVar += weight * diffGlobal * diffGlobal;
/* 308:    */       }
/* 309:502 */       if (priorVar > 0.0D) {
/* 310:503 */         return (priorVar - (var[0] + var[1])) / priorVar;
/* 311:    */       }
/* 312:505 */       return 0.0D;
/* 313:    */     }
/* 314:    */     
/* 315:    */     protected double[] distributionForInstance(Instance inst)
/* 316:    */     {
/* 317:516 */       if (this.m_successors == null) {
/* 318:517 */         return this.m_dist;
/* 319:    */       }
/* 320:519 */       double val = inst.value(this.m_attIndex);
/* 321:520 */       if (val < this.m_splitPoint) {
/* 322:521 */         return this.m_successors[0].distributionForInstance(inst);
/* 323:    */       }
/* 324:523 */       return this.m_successors[1].distributionForInstance(inst);
/* 325:    */     }
/* 326:    */     
/* 327:    */     protected ArrayList<Integer> eligibleAttributes(Instances data)
/* 328:    */     {
/* 329:533 */       ArrayList<Integer> al = null;
/* 330:    */       
/* 331:    */ 
/* 332:536 */       int actual_min = ExtraTree.this.m_n_min;
/* 333:537 */       if (ExtraTree.this.m_n_min == -1) {
/* 334:538 */         if (data.classAttribute().isNumeric()) {
/* 335:539 */           actual_min = 5;
/* 336:    */         } else {
/* 337:541 */           actual_min = 2;
/* 338:    */         }
/* 339:    */       }
/* 340:546 */       if (data.sumOfWeights() >= actual_min)
/* 341:    */       {
/* 342:549 */         double val = data.instance(0).classValue();
/* 343:550 */         boolean allTheSame = true;
/* 344:551 */         for (int i = 1; i < data.numInstances(); i++) {
/* 345:552 */           if (val != data.instance(i).classValue())
/* 346:    */           {
/* 347:553 */             allTheSame = false;
/* 348:554 */             break;
/* 349:    */           }
/* 350:    */         }
/* 351:557 */         if (!allTheSame) {
/* 352:560 */           for (int j = 0; j < data.numAttributes(); j++) {
/* 353:561 */             if (j != data.classIndex())
/* 354:    */             {
/* 355:562 */               val = data.instance(0).value(j);
/* 356:563 */               for (int i = 1; i < data.numInstances(); i++) {
/* 357:564 */                 if (val != data.instance(i).value(j))
/* 358:    */                 {
/* 359:565 */                   if (al == null) {
/* 360:566 */                     al = new ArrayList();
/* 361:    */                   }
/* 362:568 */                   al.add(Integer.valueOf(j));
/* 363:569 */                   break;
/* 364:    */                 }
/* 365:    */               }
/* 366:    */             }
/* 367:    */           }
/* 368:    */         }
/* 369:    */       }
/* 370:576 */       return al;
/* 371:    */     }
/* 372:    */   }
/* 373:    */   
/* 374:    */   public Capabilities getCapabilities()
/* 375:    */   {
/* 376:587 */     Capabilities result = super.getCapabilities();
/* 377:588 */     result.disableAll();
/* 378:    */     
/* 379:    */ 
/* 380:591 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 381:592 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 382:    */     
/* 383:    */ 
/* 384:595 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 385:596 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 386:    */     
/* 387:598 */     return result;
/* 388:    */   }
/* 389:    */   
/* 390:    */   public void buildClassifier(Instances data)
/* 391:    */     throws Exception
/* 392:    */   {
/* 393:608 */     getCapabilities().testWithFail(data);
/* 394:    */     
/* 395:610 */     this.m_tree = new Tree(data, data.getRandomNumberGenerator(this.m_Seed));
/* 396:    */   }
/* 397:    */   
/* 398:    */   public double[] distributionForInstance(Instance inst)
/* 399:    */   {
/* 400:619 */     return this.m_tree.distributionForInstance(inst);
/* 401:    */   }
/* 402:    */   
/* 403:    */   public String toString()
/* 404:    */   {
/* 405:628 */     if (this.m_tree == null) {
/* 406:629 */       return "No tree has been built yet.";
/* 407:    */     }
/* 408:    */     try
/* 409:    */     {
/* 410:632 */       return "Extra-Tree with K = " + getK() + " and Nmin = " + getNmin() + " (" + numElements() + " nodes in tree)";
/* 411:    */     }
/* 412:    */     catch (Exception e) {}
/* 413:635 */     return "Could not compute number of nodes in tree.";
/* 414:    */   }
/* 415:    */   
/* 416:    */   public void generatePartition(Instances data)
/* 417:    */     throws Exception
/* 418:    */   {
/* 419:646 */     buildClassifier(data);
/* 420:    */   }
/* 421:    */   
/* 422:    */   public double[] getMembershipValues(Instance instance)
/* 423:    */     throws Exception
/* 424:    */   {
/* 425:657 */     double[] a = new double[numElements()];
/* 426:    */     
/* 427:    */ 
/* 428:660 */     Queue<Double> queueOfWeights = new LinkedList();
/* 429:661 */     Queue<Tree> queueOfNodes = new LinkedList();
/* 430:662 */     queueOfWeights.add(Double.valueOf(instance.weight()));
/* 431:663 */     queueOfNodes.add(this.m_tree);
/* 432:664 */     int index = 0;
/* 433:667 */     while (!queueOfNodes.isEmpty())
/* 434:    */     {
/* 435:669 */       a[(index++)] = ((Double)queueOfWeights.poll()).doubleValue();
/* 436:670 */       Tree node = (Tree)queueOfNodes.poll();
/* 437:673 */       if (node.m_successors != null)
/* 438:    */       {
/* 439:678 */         double[] weights = new double[node.m_successors.length];
/* 440:679 */         if (instance.value(node.m_attIndex) < node.m_splitPoint) {
/* 441:680 */           weights[0] = 1.0D;
/* 442:    */         } else {
/* 443:682 */           weights[1] = 1.0D;
/* 444:    */         }
/* 445:684 */         for (int i = 0; i < node.m_successors.length; i++)
/* 446:    */         {
/* 447:685 */           queueOfNodes.add(node.m_successors[i]);
/* 448:686 */           queueOfWeights.add(Double.valueOf(a[(index - 1)] * weights[i]));
/* 449:    */         }
/* 450:    */       }
/* 451:    */     }
/* 452:689 */     return a;
/* 453:    */   }
/* 454:    */   
/* 455:    */   public int numElements()
/* 456:    */     throws Exception
/* 457:    */   {
/* 458:698 */     int numNodes = 0;
/* 459:699 */     Queue<Tree> queueOfNodes = new LinkedList();
/* 460:700 */     queueOfNodes.add(this.m_tree);
/* 461:701 */     while (!queueOfNodes.isEmpty())
/* 462:    */     {
/* 463:702 */       Tree node = (Tree)queueOfNodes.poll();
/* 464:703 */       numNodes++;
/* 465:704 */       if (node.m_successors != null) {
/* 466:705 */         for (Tree m_successor : node.m_successors) {
/* 467:706 */           queueOfNodes.add(m_successor);
/* 468:    */         }
/* 469:    */       }
/* 470:    */     }
/* 471:710 */     return numNodes;
/* 472:    */   }
/* 473:    */   
/* 474:    */   public String getRevision()
/* 475:    */   {
/* 476:720 */     return RevisionUtils.extract("$Revision: 10343 $");
/* 477:    */   }
/* 478:    */   
/* 479:    */   public static void main(String[] args)
/* 480:    */   {
/* 481:728 */     runClassifier(new ExtraTree(), args);
/* 482:    */   }
/* 483:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ExtraTree
 * JD-Core Version:    0.7.0.1
 */