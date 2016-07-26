/*   1:    */ package weka.core.neighboursearch;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.AdditionalMeasureProducer;
/*   9:    */ import weka.core.DistanceFunction;
/*  10:    */ import weka.core.EuclideanDistance;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionHandler;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.Utils;
/*  18:    */ 
/*  19:    */ public abstract class NearestNeighbourSearch
/*  20:    */   implements Serializable, OptionHandler, AdditionalMeasureProducer, RevisionHandler
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = 7516898393890379876L;
/*  23:    */   protected Instances m_Instances;
/*  24:    */   protected int m_kNN;
/*  25:    */   
/*  26:    */   protected class MyHeap
/*  27:    */     implements RevisionHandler
/*  28:    */   {
/*  29: 63 */     NearestNeighbourSearch.MyHeapElement[] m_heap = null;
/*  30:    */     
/*  31:    */     public MyHeap(int maxSize)
/*  32:    */     {
/*  33: 71 */       if (maxSize % 2 == 0) {
/*  34: 72 */         maxSize++;
/*  35:    */       }
/*  36: 75 */       this.m_heap = new NearestNeighbourSearch.MyHeapElement[maxSize + 1];
/*  37: 76 */       this.m_heap[0] = new NearestNeighbourSearch.MyHeapElement(NearestNeighbourSearch.this, 0, 0.0D);
/*  38:    */     }
/*  39:    */     
/*  40:    */     public int size()
/*  41:    */     {
/*  42: 85 */       return this.m_heap[0].index;
/*  43:    */     }
/*  44:    */     
/*  45:    */     public NearestNeighbourSearch.MyHeapElement peek()
/*  46:    */     {
/*  47: 94 */       return this.m_heap[1];
/*  48:    */     }
/*  49:    */     
/*  50:    */     public NearestNeighbourSearch.MyHeapElement get()
/*  51:    */       throws Exception
/*  52:    */     {
/*  53:104 */       if (this.m_heap[0].index == 0) {
/*  54:105 */         throw new Exception("No elements present in the heap");
/*  55:    */       }
/*  56:107 */       NearestNeighbourSearch.MyHeapElement r = this.m_heap[1];
/*  57:108 */       this.m_heap[1] = this.m_heap[this.m_heap[0].index];
/*  58:109 */       this.m_heap[0].index -= 1;
/*  59:110 */       downheap();
/*  60:111 */       return r;
/*  61:    */     }
/*  62:    */     
/*  63:    */     public void put(int i, double d)
/*  64:    */       throws Exception
/*  65:    */     {
/*  66:122 */       if (this.m_heap[0].index + 1 > this.m_heap.length - 1) {
/*  67:123 */         throw new Exception("the number of elements cannot exceed the initially set maximum limit");
/*  68:    */       }
/*  69:126 */       this.m_heap[0].index += 1;
/*  70:127 */       this.m_heap[this.m_heap[0].index] = new NearestNeighbourSearch.MyHeapElement(NearestNeighbourSearch.this, i, d);
/*  71:128 */       upheap();
/*  72:    */     }
/*  73:    */     
/*  74:    */     public void putBySubstitute(int i, double d)
/*  75:    */       throws Exception
/*  76:    */     {
/*  77:139 */       NearestNeighbourSearch.MyHeapElement head = get();
/*  78:140 */       put(i, d);
/*  79:142 */       if (head.distance == this.m_heap[1].distance)
/*  80:    */       {
/*  81:144 */         putKthNearest(head.index, head.distance);
/*  82:    */       }
/*  83:145 */       else if (head.distance > this.m_heap[1].distance)
/*  84:    */       {
/*  85:147 */         this.m_KthNearest = null;
/*  86:148 */         this.m_KthNearestSize = 0;
/*  87:149 */         this.initSize = 10;
/*  88:    */       }
/*  89:150 */       else if (head.distance < this.m_heap[1].distance)
/*  90:    */       {
/*  91:151 */         throw new Exception("The substituted element is smaller than the head element. put() should have been called in place of putBySubstitute()");
/*  92:    */       }
/*  93:    */     }
/*  94:    */     
/*  95:158 */     NearestNeighbourSearch.MyHeapElement[] m_KthNearest = null;
/*  96:161 */     int m_KthNearestSize = 0;
/*  97:164 */     int initSize = 10;
/*  98:    */     
/*  99:    */     public int noOfKthNearest()
/* 100:    */     {
/* 101:173 */       return this.m_KthNearestSize;
/* 102:    */     }
/* 103:    */     
/* 104:    */     public void putKthNearest(int i, double d)
/* 105:    */     {
/* 106:183 */       if (this.m_KthNearest == null) {
/* 107:184 */         this.m_KthNearest = new NearestNeighbourSearch.MyHeapElement[this.initSize];
/* 108:    */       }
/* 109:186 */       if (this.m_KthNearestSize >= this.m_KthNearest.length)
/* 110:    */       {
/* 111:187 */         this.initSize += this.initSize;
/* 112:188 */         NearestNeighbourSearch.MyHeapElement[] temp = new NearestNeighbourSearch.MyHeapElement[this.initSize];
/* 113:189 */         System.arraycopy(this.m_KthNearest, 0, temp, 0, this.m_KthNearest.length);
/* 114:190 */         this.m_KthNearest = temp;
/* 115:    */       }
/* 116:192 */       this.m_KthNearest[(this.m_KthNearestSize++)] = new NearestNeighbourSearch.MyHeapElement(NearestNeighbourSearch.this, i, d);
/* 117:    */     }
/* 118:    */     
/* 119:    */     public NearestNeighbourSearch.MyHeapElement getKthNearest()
/* 120:    */     {
/* 121:201 */       if (this.m_KthNearestSize == 0) {
/* 122:202 */         return null;
/* 123:    */       }
/* 124:204 */       this.m_KthNearestSize -= 1;
/* 125:205 */       return this.m_KthNearest[this.m_KthNearestSize];
/* 126:    */     }
/* 127:    */     
/* 128:    */     protected void upheap()
/* 129:    */     {
/* 130:212 */       int i = this.m_heap[0].index;
/* 131:214 */       while ((i > 1) && (this.m_heap[i].distance > this.m_heap[(i / 2)].distance))
/* 132:    */       {
/* 133:215 */         NearestNeighbourSearch.MyHeapElement temp = this.m_heap[i];
/* 134:216 */         this.m_heap[i] = this.m_heap[(i / 2)];
/* 135:217 */         i /= 2;
/* 136:218 */         this.m_heap[i] = temp;
/* 137:    */       }
/* 138:    */     }
/* 139:    */     
/* 140:    */     protected void downheap()
/* 141:    */     {
/* 142:226 */       int i = 1;
/* 143:229 */       while (((2 * i <= this.m_heap[0].index) && (this.m_heap[i].distance < this.m_heap[(2 * i)].distance)) || ((2 * i + 1 <= this.m_heap[0].index) && (this.m_heap[i].distance < this.m_heap[(2 * i + 1)].distance))) {
/* 144:230 */         if (2 * i + 1 <= this.m_heap[0].index)
/* 145:    */         {
/* 146:231 */           if (this.m_heap[(2 * i)].distance > this.m_heap[(2 * i + 1)].distance)
/* 147:    */           {
/* 148:232 */             NearestNeighbourSearch.MyHeapElement temp = this.m_heap[i];
/* 149:233 */             this.m_heap[i] = this.m_heap[(2 * i)];
/* 150:234 */             i = 2 * i;
/* 151:235 */             this.m_heap[i] = temp;
/* 152:    */           }
/* 153:    */           else
/* 154:    */           {
/* 155:237 */             NearestNeighbourSearch.MyHeapElement temp = this.m_heap[i];
/* 156:238 */             this.m_heap[i] = this.m_heap[(2 * i + 1)];
/* 157:239 */             i = 2 * i + 1;
/* 158:240 */             this.m_heap[i] = temp;
/* 159:    */           }
/* 160:    */         }
/* 161:    */         else
/* 162:    */         {
/* 163:243 */           NearestNeighbourSearch.MyHeapElement temp = this.m_heap[i];
/* 164:244 */           this.m_heap[i] = this.m_heap[(2 * i)];
/* 165:245 */           i = 2 * i;
/* 166:246 */           this.m_heap[i] = temp;
/* 167:    */         }
/* 168:    */       }
/* 169:    */     }
/* 170:    */     
/* 171:    */     public int totalSize()
/* 172:    */     {
/* 173:257 */       return size() + noOfKthNearest();
/* 174:    */     }
/* 175:    */     
/* 176:    */     public String getRevision()
/* 177:    */     {
/* 178:267 */       return RevisionUtils.extract("$Revision: 10203 $");
/* 179:    */     }
/* 180:    */   }
/* 181:    */   
/* 182:    */   protected class MyHeapElement
/* 183:    */     implements RevisionHandler
/* 184:    */   {
/* 185:    */     public int index;
/* 186:    */     public double distance;
/* 187:    */     
/* 188:    */     public MyHeapElement(int i, double d)
/* 189:    */     {
/* 190:292 */       this.distance = d;
/* 191:293 */       this.index = i;
/* 192:    */     }
/* 193:    */     
/* 194:    */     public String getRevision()
/* 195:    */     {
/* 196:303 */       return RevisionUtils.extract("$Revision: 10203 $");
/* 197:    */     }
/* 198:    */   }
/* 199:    */   
/* 200:    */   protected class NeighborNode
/* 201:    */     implements RevisionHandler
/* 202:    */   {
/* 203:    */     public Instance m_Instance;
/* 204:    */     public double m_Distance;
/* 205:    */     public NeighborNode m_Next;
/* 206:    */     
/* 207:    */     public NeighborNode(double distance, Instance instance, NeighborNode next)
/* 208:    */     {
/* 209:333 */       this.m_Distance = distance;
/* 210:334 */       this.m_Instance = instance;
/* 211:335 */       this.m_Next = next;
/* 212:    */     }
/* 213:    */     
/* 214:    */     public NeighborNode(double distance, Instance instance)
/* 215:    */     {
/* 216:346 */       this(distance, instance, null);
/* 217:    */     }
/* 218:    */     
/* 219:    */     public String getRevision()
/* 220:    */     {
/* 221:356 */       return RevisionUtils.extract("$Revision: 10203 $");
/* 222:    */     }
/* 223:    */   }
/* 224:    */   
/* 225:    */   protected class NeighborList
/* 226:    */     implements RevisionHandler
/* 227:    */   {
/* 228:    */     protected NearestNeighbourSearch.NeighborNode m_First;
/* 229:    */     protected NearestNeighbourSearch.NeighborNode m_Last;
/* 230:378 */     protected int m_Length = 1;
/* 231:    */     
/* 232:    */     public NeighborList(int length)
/* 233:    */     {
/* 234:386 */       this.m_Length = length;
/* 235:    */     }
/* 236:    */     
/* 237:    */     public boolean isEmpty()
/* 238:    */     {
/* 239:395 */       return this.m_First == null;
/* 240:    */     }
/* 241:    */     
/* 242:    */     public int currentLength()
/* 243:    */     {
/* 244:404 */       int i = 0;
/* 245:405 */       NearestNeighbourSearch.NeighborNode current = this.m_First;
/* 246:406 */       while (current != null)
/* 247:    */       {
/* 248:407 */         i++;
/* 249:408 */         current = current.m_Next;
/* 250:    */       }
/* 251:410 */       return i;
/* 252:    */     }
/* 253:    */     
/* 254:    */     public void insertSorted(double distance, Instance instance)
/* 255:    */     {
/* 256:422 */       if (isEmpty())
/* 257:    */       {
/* 258:423 */         this.m_First = (this.m_Last = new NearestNeighbourSearch.NeighborNode(NearestNeighbourSearch.this, distance, instance));
/* 259:    */       }
/* 260:    */       else
/* 261:    */       {
/* 262:425 */         NearestNeighbourSearch.NeighborNode current = this.m_First;
/* 263:426 */         if (distance < this.m_First.m_Distance)
/* 264:    */         {
/* 265:427 */           this.m_First = new NearestNeighbourSearch.NeighborNode(NearestNeighbourSearch.this, distance, instance, this.m_First);
/* 266:    */         }
/* 267:    */         else
/* 268:    */         {
/* 269:430 */           while ((current.m_Next != null) && (current.m_Next.m_Distance < distance)) {
/* 270:430 */             current = current.m_Next;
/* 271:    */           }
/* 272:433 */           current.m_Next = new NearestNeighbourSearch.NeighborNode(NearestNeighbourSearch.this, distance, instance, current.m_Next);
/* 273:434 */           if (current.equals(this.m_Last)) {
/* 274:435 */             this.m_Last = current.m_Next;
/* 275:    */           }
/* 276:    */         }
/* 277:441 */         int valcount = 0;
/* 278:442 */         for (current = this.m_First; current.m_Next != null; current = current.m_Next)
/* 279:    */         {
/* 280:443 */           valcount++;
/* 281:444 */           if ((valcount >= this.m_Length) && (current.m_Distance != current.m_Next.m_Distance))
/* 282:    */           {
/* 283:446 */             this.m_Last = current;
/* 284:447 */             current.m_Next = null;
/* 285:448 */             break;
/* 286:    */           }
/* 287:    */         }
/* 288:    */       }
/* 289:    */     }
/* 290:    */     
/* 291:    */     public void pruneToK(int k)
/* 292:    */     {
/* 293:462 */       if (isEmpty()) {
/* 294:463 */         return;
/* 295:    */       }
/* 296:465 */       if (k < 1) {
/* 297:466 */         k = 1;
/* 298:    */       }
/* 299:468 */       int currentK = 0;
/* 300:469 */       double currentDist = this.m_First.m_Distance;
/* 301:470 */       for (NearestNeighbourSearch.NeighborNode current = this.m_First; current.m_Next != null; current = current.m_Next)
/* 302:    */       {
/* 303:472 */         currentK++;
/* 304:473 */         currentDist = current.m_Distance;
/* 305:474 */         if ((currentK >= k) && (currentDist != current.m_Next.m_Distance))
/* 306:    */         {
/* 307:475 */           this.m_Last = current;
/* 308:476 */           current.m_Next = null;
/* 309:477 */           break;
/* 310:    */         }
/* 311:    */       }
/* 312:    */     }
/* 313:    */     
/* 314:    */     public void printList()
/* 315:    */     {
/* 316:487 */       if (isEmpty())
/* 317:    */       {
/* 318:488 */         System.out.println("Empty list");
/* 319:    */       }
/* 320:    */       else
/* 321:    */       {
/* 322:490 */         NearestNeighbourSearch.NeighborNode current = this.m_First;
/* 323:491 */         while (current != null)
/* 324:    */         {
/* 325:492 */           System.out.println("Node: instance " + current.m_Instance + ", distance " + current.m_Distance);
/* 326:    */           
/* 327:494 */           current = current.m_Next;
/* 328:    */         }
/* 329:496 */         System.out.println();
/* 330:    */       }
/* 331:    */     }
/* 332:    */     
/* 333:    */     public NearestNeighbourSearch.NeighborNode getFirst()
/* 334:    */     {
/* 335:506 */       return this.m_First;
/* 336:    */     }
/* 337:    */     
/* 338:    */     public NearestNeighbourSearch.NeighborNode getLast()
/* 339:    */     {
/* 340:515 */       return this.m_Last;
/* 341:    */     }
/* 342:    */     
/* 343:    */     public String getRevision()
/* 344:    */     {
/* 345:525 */       return RevisionUtils.extract("$Revision: 10203 $");
/* 346:    */     }
/* 347:    */   }
/* 348:    */   
/* 349:536 */   protected DistanceFunction m_DistanceFunction = new EuclideanDistance();
/* 350:539 */   protected PerformanceStats m_Stats = null;
/* 351:542 */   protected boolean m_MeasurePerformance = false;
/* 352:    */   
/* 353:    */   public NearestNeighbourSearch()
/* 354:    */   {
/* 355:548 */     if (this.m_MeasurePerformance) {
/* 356:549 */       this.m_Stats = new PerformanceStats();
/* 357:    */     }
/* 358:    */   }
/* 359:    */   
/* 360:    */   public NearestNeighbourSearch(Instances insts)
/* 361:    */   {
/* 362:559 */     this();
/* 363:560 */     this.m_Instances = insts;
/* 364:    */   }
/* 365:    */   
/* 366:    */   public String globalInfo()
/* 367:    */   {
/* 368:570 */     return "Abstract class for nearest neighbour search. All algorithms (classes) that do nearest neighbour search should extend this class.";
/* 369:    */   }
/* 370:    */   
/* 371:    */   public Enumeration<Option> listOptions()
/* 372:    */   {
/* 373:581 */     Vector<Option> newVector = new Vector();
/* 374:    */     
/* 375:583 */     newVector.add(new Option("\tDistance function to use.\n\t(default: weka.core.EuclideanDistance)", "A", 1, "-A <classname and options>"));
/* 376:    */     
/* 377:    */ 
/* 378:    */ 
/* 379:587 */     newVector.add(new Option("\tCalculate performance statistics.", "P", 0, "-P"));
/* 380:    */     
/* 381:    */ 
/* 382:590 */     return newVector.elements();
/* 383:    */   }
/* 384:    */   
/* 385:    */   public void setOptions(String[] options)
/* 386:    */     throws Exception
/* 387:    */   {
/* 388:603 */     String nnSearchClass = Utils.getOption('A', options);
/* 389:604 */     if (nnSearchClass.length() != 0)
/* 390:    */     {
/* 391:605 */       String[] nnSearchClassSpec = Utils.splitOptions(nnSearchClass);
/* 392:606 */       if (nnSearchClassSpec.length == 0) {
/* 393:607 */         throw new Exception("Invalid DistanceFunction specification string.");
/* 394:    */       }
/* 395:609 */       String className = nnSearchClassSpec[0];
/* 396:610 */       nnSearchClassSpec[0] = "";
/* 397:    */       
/* 398:612 */       setDistanceFunction((DistanceFunction)Utils.forName(DistanceFunction.class, className, nnSearchClassSpec));
/* 399:    */     }
/* 400:    */     else
/* 401:    */     {
/* 402:615 */       setDistanceFunction(new EuclideanDistance());
/* 403:    */     }
/* 404:618 */     setMeasurePerformance(Utils.getFlag('P', options));
/* 405:    */   }
/* 406:    */   
/* 407:    */   public String[] getOptions()
/* 408:    */   {
/* 409:630 */     Vector<String> result = new Vector();
/* 410:    */     
/* 411:632 */     result.add("-A");
/* 412:633 */     result.add((this.m_DistanceFunction.getClass().getName() + " " + Utils.joinOptions(this.m_DistanceFunction.getOptions())).trim());
/* 413:636 */     if (getMeasurePerformance()) {
/* 414:637 */       result.add("-P");
/* 415:    */     }
/* 416:640 */     return (String[])result.toArray(new String[result.size()]);
/* 417:    */   }
/* 418:    */   
/* 419:    */   public String distanceFunctionTipText()
/* 420:    */   {
/* 421:650 */     return "The distance function to use for finding neighbours (default: weka.core.EuclideanDistance). ";
/* 422:    */   }
/* 423:    */   
/* 424:    */   public DistanceFunction getDistanceFunction()
/* 425:    */   {
/* 426:660 */     return this.m_DistanceFunction;
/* 427:    */   }
/* 428:    */   
/* 429:    */   public void setDistanceFunction(DistanceFunction df)
/* 430:    */     throws Exception
/* 431:    */   {
/* 432:670 */     this.m_DistanceFunction = df;
/* 433:    */   }
/* 434:    */   
/* 435:    */   public String measurePerformanceTipText()
/* 436:    */   {
/* 437:680 */     return "Whether to calculate performance statistics for the NN search or not";
/* 438:    */   }
/* 439:    */   
/* 440:    */   public boolean getMeasurePerformance()
/* 441:    */   {
/* 442:690 */     return this.m_MeasurePerformance;
/* 443:    */   }
/* 444:    */   
/* 445:    */   public void setMeasurePerformance(boolean measurePerformance)
/* 446:    */   {
/* 447:699 */     this.m_MeasurePerformance = measurePerformance;
/* 448:700 */     if (this.m_MeasurePerformance)
/* 449:    */     {
/* 450:701 */       if (this.m_Stats == null) {
/* 451:702 */         this.m_Stats = new PerformanceStats();
/* 452:    */       }
/* 453:    */     }
/* 454:    */     else {
/* 455:705 */       this.m_Stats = null;
/* 456:    */     }
/* 457:    */   }
/* 458:    */   
/* 459:    */   public abstract Instance nearestNeighbour(Instance paramInstance)
/* 460:    */     throws Exception;
/* 461:    */   
/* 462:    */   public abstract Instances kNearestNeighbours(Instance paramInstance, int paramInt)
/* 463:    */     throws Exception;
/* 464:    */   
/* 465:    */   public abstract double[] getDistances()
/* 466:    */     throws Exception;
/* 467:    */   
/* 468:    */   public abstract void update(Instance paramInstance)
/* 469:    */     throws Exception;
/* 470:    */   
/* 471:    */   public void addInstanceInfo(Instance ins) {}
/* 472:    */   
/* 473:    */   public void setInstances(Instances insts)
/* 474:    */     throws Exception
/* 475:    */   {
/* 476:767 */     this.m_Instances = insts;
/* 477:    */   }
/* 478:    */   
/* 479:    */   public Instances getInstances()
/* 480:    */   {
/* 481:776 */     return this.m_Instances;
/* 482:    */   }
/* 483:    */   
/* 484:    */   public PerformanceStats getPerformanceStats()
/* 485:    */   {
/* 486:786 */     return this.m_Stats;
/* 487:    */   }
/* 488:    */   
/* 489:    */   public Enumeration<String> enumerateMeasures()
/* 490:    */   {
/* 491:    */     Vector<String> newVector;
/* 492:    */     Vector<String> newVector;
/* 493:797 */     if (this.m_Stats == null)
/* 494:    */     {
/* 495:798 */       newVector = new Vector(0);
/* 496:    */     }
/* 497:    */     else
/* 498:    */     {
/* 499:800 */       newVector = new Vector();
/* 500:801 */       Enumeration<String> en = this.m_Stats.enumerateMeasures();
/* 501:802 */       newVector.addAll(Collections.list(en));
/* 502:    */     }
/* 503:804 */     return newVector.elements();
/* 504:    */   }
/* 505:    */   
/* 506:    */   public double getMeasure(String additionalMeasureName)
/* 507:    */   {
/* 508:816 */     if (this.m_Stats == null) {
/* 509:817 */       throw new IllegalArgumentException(additionalMeasureName + " not supported (NearestNeighbourSearch)");
/* 510:    */     }
/* 511:820 */     return this.m_Stats.getMeasure(additionalMeasureName);
/* 512:    */   }
/* 513:    */   
/* 514:    */   public static void combSort11(double[] arrayToSort, int[] linkedArray)
/* 515:    */   {
/* 516:835 */     int gap = arrayToSort.length;
/* 517:    */     int switches;
/* 518:    */     do
/* 519:    */     {
/* 520:837 */       gap = (int)(gap / 1.3D);
/* 521:838 */       switch (gap)
/* 522:    */       {
/* 523:    */       case 0: 
/* 524:840 */         gap = 1;
/* 525:841 */         break;
/* 526:    */       case 9: 
/* 527:    */       case 10: 
/* 528:844 */         gap = 11;
/* 529:845 */         break;
/* 530:    */       }
/* 531:849 */       switches = 0;
/* 532:850 */       int top = arrayToSort.length - gap;
/* 533:851 */       for (int i = 0; i < top; i++)
/* 534:    */       {
/* 535:852 */         int j = i + gap;
/* 536:853 */         if (arrayToSort[i] > arrayToSort[j])
/* 537:    */         {
/* 538:854 */           double hold1 = arrayToSort[i];
/* 539:855 */           int hold2 = linkedArray[i];
/* 540:856 */           arrayToSort[i] = arrayToSort[j];
/* 541:857 */           linkedArray[i] = linkedArray[j];
/* 542:858 */           arrayToSort[j] = hold1;
/* 543:859 */           linkedArray[j] = hold2;
/* 544:860 */           switches++;
/* 545:    */         }
/* 546:    */       }
/* 547:863 */     } while ((switches > 0) || (gap > 1));
/* 548:    */   }
/* 549:    */   
/* 550:    */   protected static int partition(double[] arrayToSort, double[] linkedArray, int l, int r)
/* 551:    */   {
/* 552:878 */     double pivot = arrayToSort[((l + r) / 2)];
/* 553:881 */     while (l < r)
/* 554:    */     {
/* 555:882 */       while ((arrayToSort[l] < pivot) && (l < r)) {
/* 556:883 */         l++;
/* 557:    */       }
/* 558:885 */       while ((arrayToSort[r] > pivot) && (l < r)) {
/* 559:886 */         r--;
/* 560:    */       }
/* 561:888 */       if (l < r)
/* 562:    */       {
/* 563:889 */         double help = arrayToSort[l];
/* 564:890 */         arrayToSort[l] = arrayToSort[r];
/* 565:891 */         arrayToSort[r] = help;
/* 566:892 */         help = linkedArray[l];
/* 567:893 */         linkedArray[l] = linkedArray[r];
/* 568:894 */         linkedArray[r] = help;
/* 569:895 */         l++;
/* 570:896 */         r--;
/* 571:    */       }
/* 572:    */     }
/* 573:899 */     if ((l == r) && (arrayToSort[r] > pivot)) {
/* 574:900 */       r--;
/* 575:    */     }
/* 576:903 */     return r;
/* 577:    */   }
/* 578:    */   
/* 579:    */   public static void quickSort(double[] arrayToSort, double[] linkedArray, int left, int right)
/* 580:    */   {
/* 581:916 */     if (left < right)
/* 582:    */     {
/* 583:917 */       int middle = partition(arrayToSort, linkedArray, left, right);
/* 584:918 */       quickSort(arrayToSort, linkedArray, left, middle);
/* 585:919 */       quickSort(arrayToSort, linkedArray, middle + 1, right);
/* 586:    */     }
/* 587:    */   }
/* 588:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.NearestNeighbourSearch
 * JD-Core Version:    0.7.0.1
 */