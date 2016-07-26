/*   1:    */ package weka.core.matrix;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.lang.reflect.Method;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import weka.core.RevisionHandler;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ 
/*   9:    */ public class DoubleVector
/*  10:    */   implements Cloneable, RevisionHandler
/*  11:    */ {
/*  12:    */   double[] V;
/*  13:    */   private int sizeOfVector;
/*  14:    */   
/*  15:    */   public DoubleVector()
/*  16:    */   {
/*  17: 50 */     this(0);
/*  18:    */   }
/*  19:    */   
/*  20:    */   public DoubleVector(int n)
/*  21:    */   {
/*  22: 59 */     this.V = new double[n];
/*  23: 60 */     setSize(n);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public DoubleVector(int n, double s)
/*  27:    */   {
/*  28: 70 */     this(n);
/*  29: 71 */     set(s);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public DoubleVector(double[] v)
/*  33:    */   {
/*  34: 80 */     if (v == null)
/*  35:    */     {
/*  36: 81 */       this.V = new double[0];
/*  37: 82 */       setSize(0);
/*  38:    */     }
/*  39:    */     else
/*  40:    */     {
/*  41: 84 */       this.V = v;
/*  42: 85 */       setSize(v.length);
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void set(int i, double s)
/*  47:    */   {
/*  48:101 */     this.V[i] = s;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void set(double s)
/*  52:    */   {
/*  53:110 */     set(0, size() - 1, s);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void set(int i0, int i1, double s)
/*  57:    */   {
/*  58:122 */     for (int i = i0; i <= i1; i++) {
/*  59:123 */       this.V[i] = s;
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void set(int i0, int i1, double[] v, int j0)
/*  64:    */   {
/*  65:136 */     for (int i = i0; i <= i1; i++) {
/*  66:137 */       this.V[i] = v[(j0 + i - i0)];
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void set(DoubleVector v)
/*  71:    */   {
/*  72:147 */     set(0, v.size() - 1, v, 0);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void set(int i0, int i1, DoubleVector v, int j0)
/*  76:    */   {
/*  77:159 */     for (int i = i0; i <= i1; i++) {
/*  78:160 */       this.V[i] = v.V[(j0 + i - i0)];
/*  79:    */     }
/*  80:    */   }
/*  81:    */   
/*  82:    */   public double[] getArray()
/*  83:    */   {
/*  84:170 */     return this.V;
/*  85:    */   }
/*  86:    */   
/*  87:    */   void setArray(double[] a)
/*  88:    */   {
/*  89:174 */     this.V = a;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public double[] getArrayCopy()
/*  93:    */   {
/*  94:183 */     double[] v = new double[size()];
/*  95:185 */     for (int i = 0; i < size(); i++) {
/*  96:186 */       v[i] = this.V[i];
/*  97:    */     }
/*  98:189 */     return v;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void sort()
/* 102:    */   {
/* 103:194 */     Arrays.sort(this.V, 0, size());
/* 104:    */   }
/* 105:    */   
/* 106:    */   public IntVector sortWithIndex()
/* 107:    */   {
/* 108:199 */     IntVector index = IntVector.seq(0, size() - 1);
/* 109:200 */     sortWithIndex(0, size() - 1, index);
/* 110:201 */     return index;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void sortWithIndex(int xi, int xj, IntVector index)
/* 114:    */   {
/* 115:212 */     if (xi < xj)
/* 116:    */     {
/* 117:214 */       int xm = (xi + xj) / 2;
/* 118:215 */       double x = Math.min(this.V[xi], Math.max(this.V[xm], this.V[xj]));
/* 119:    */       
/* 120:217 */       int i = xi;
/* 121:218 */       int j = xj;
/* 122:219 */       while (i < j)
/* 123:    */       {
/* 124:220 */         while ((this.V[i] < x) && (i < xj)) {
/* 125:221 */           i++;
/* 126:    */         }
/* 127:223 */         while ((this.V[j] > x) && (j > xi)) {
/* 128:224 */           j--;
/* 129:    */         }
/* 130:226 */         if (i <= j)
/* 131:    */         {
/* 132:227 */           swap(i, j);
/* 133:228 */           index.swap(i, j);
/* 134:229 */           i++;
/* 135:230 */           j--;
/* 136:    */         }
/* 137:    */       }
/* 138:233 */       sortWithIndex(xi, j, index);
/* 139:234 */       sortWithIndex(i, xj, index);
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   public int size()
/* 144:    */   {
/* 145:244 */     return this.sizeOfVector;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void setSize(int m)
/* 149:    */   {
/* 150:253 */     if (m > capacity()) {
/* 151:254 */       throw new IllegalArgumentException("insufficient capacity");
/* 152:    */     }
/* 153:256 */     this.sizeOfVector = m;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public int capacity()
/* 157:    */   {
/* 158:265 */     if (this.V == null) {
/* 159:266 */       return 0;
/* 160:    */     }
/* 161:268 */     return this.V.length;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void setCapacity(int n)
/* 165:    */   {
/* 166:277 */     if (n == capacity()) {
/* 167:278 */       return;
/* 168:    */     }
/* 169:280 */     double[] oldV = this.V;
/* 170:281 */     int m = Math.min(n, size());
/* 171:282 */     this.V = new double[n];
/* 172:283 */     setSize(m);
/* 173:284 */     set(0, m - 1, oldV, 0);
/* 174:    */   }
/* 175:    */   
/* 176:    */   public double get(int i)
/* 177:    */   {
/* 178:294 */     return this.V[i];
/* 179:    */   }
/* 180:    */   
/* 181:    */   public void setPlus(int i, double s)
/* 182:    */   {
/* 183:304 */     this.V[i] += s;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void setTimes(int i, double s)
/* 187:    */   {
/* 188:314 */     this.V[i] *= s;
/* 189:    */   }
/* 190:    */   
/* 191:    */   public void addElement(double x)
/* 192:    */   {
/* 193:323 */     if (capacity() == 0) {
/* 194:324 */       setCapacity(10);
/* 195:    */     }
/* 196:326 */     if (size() == capacity()) {
/* 197:327 */       setCapacity(2 * capacity());
/* 198:    */     }
/* 199:329 */     this.V[size()] = x;
/* 200:330 */     setSize(size() + 1);
/* 201:    */   }
/* 202:    */   
/* 203:    */   public DoubleVector square()
/* 204:    */   {
/* 205:337 */     DoubleVector v = new DoubleVector(size());
/* 206:338 */     for (int i = 0; i < size(); i++) {
/* 207:339 */       this.V[i] *= this.V[i];
/* 208:    */     }
/* 209:341 */     return v;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public DoubleVector sqrt()
/* 213:    */   {
/* 214:348 */     DoubleVector v = new DoubleVector(size());
/* 215:349 */     for (int i = 0; i < size(); i++) {
/* 216:350 */       v.V[i] = Math.sqrt(this.V[i]);
/* 217:    */     }
/* 218:352 */     return v;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public DoubleVector copy()
/* 222:    */   {
/* 223:359 */     return (DoubleVector)clone();
/* 224:    */   }
/* 225:    */   
/* 226:    */   public Object clone()
/* 227:    */   {
/* 228:367 */     int n = size();
/* 229:368 */     DoubleVector u = new DoubleVector(n);
/* 230:369 */     for (int i = 0; i < n; i++) {
/* 231:370 */       u.V[i] = this.V[i];
/* 232:    */     }
/* 233:372 */     return u;
/* 234:    */   }
/* 235:    */   
/* 236:    */   public double innerProduct(DoubleVector v)
/* 237:    */   {
/* 238:382 */     if (size() != v.size()) {
/* 239:383 */       throw new IllegalArgumentException("sizes unmatch");
/* 240:    */     }
/* 241:385 */     double p = 0.0D;
/* 242:386 */     for (int i = 0; i < size(); i++) {
/* 243:387 */       p += this.V[i] * v.V[i];
/* 244:    */     }
/* 245:389 */     return p;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public DoubleVector sign()
/* 249:    */   {
/* 250:396 */     DoubleVector s = new DoubleVector(size());
/* 251:397 */     for (int i = 0; i < size(); i++) {
/* 252:398 */       if (this.V[i] > 0.0D) {
/* 253:399 */         s.V[i] = 1.0D;
/* 254:400 */       } else if (this.V[i] < 0.0D) {
/* 255:401 */         s.V[i] = -1.0D;
/* 256:    */       } else {
/* 257:403 */         s.V[i] = 0.0D;
/* 258:    */       }
/* 259:    */     }
/* 260:406 */     return s;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public double sum()
/* 264:    */   {
/* 265:413 */     double s = 0.0D;
/* 266:414 */     for (int i = 0; i < size(); i++) {
/* 267:415 */       s += this.V[i];
/* 268:    */     }
/* 269:417 */     return s;
/* 270:    */   }
/* 271:    */   
/* 272:    */   public double sum2()
/* 273:    */   {
/* 274:424 */     double s2 = 0.0D;
/* 275:425 */     for (int i = 0; i < size(); i++) {
/* 276:426 */       s2 += this.V[i] * this.V[i];
/* 277:    */     }
/* 278:428 */     return s2;
/* 279:    */   }
/* 280:    */   
/* 281:    */   public double norm1()
/* 282:    */   {
/* 283:435 */     double s = 0.0D;
/* 284:436 */     for (int i = 0; i < size(); i++) {
/* 285:437 */       s += Math.abs(this.V[i]);
/* 286:    */     }
/* 287:439 */     return s;
/* 288:    */   }
/* 289:    */   
/* 290:    */   public double norm2()
/* 291:    */   {
/* 292:446 */     return Math.sqrt(sum2());
/* 293:    */   }
/* 294:    */   
/* 295:    */   public double sum2(DoubleVector v)
/* 296:    */   {
/* 297:455 */     return minus(v).sum2();
/* 298:    */   }
/* 299:    */   
/* 300:    */   public DoubleVector subvector(int i0, int i1)
/* 301:    */   {
/* 302:466 */     DoubleVector v = new DoubleVector(i1 - i0 + 1);
/* 303:467 */     v.set(0, i1 - i0, this, i0);
/* 304:468 */     return v;
/* 305:    */   }
/* 306:    */   
/* 307:    */   public DoubleVector subvector(IntVector index)
/* 308:    */   {
/* 309:478 */     DoubleVector v = new DoubleVector(index.size());
/* 310:479 */     for (int i = 0; i < index.size(); i++) {
/* 311:480 */       v.V[i] = this.V[index.V[i]];
/* 312:    */     }
/* 313:482 */     return v;
/* 314:    */   }
/* 315:    */   
/* 316:    */   public DoubleVector unpivoting(IntVector index, int length)
/* 317:    */   {
/* 318:494 */     if (index.size() > length) {
/* 319:495 */       throw new IllegalArgumentException("index.size() > length ");
/* 320:    */     }
/* 321:497 */     DoubleVector u = new DoubleVector(length);
/* 322:498 */     for (int i = 0; i < index.size(); i++) {
/* 323:499 */       u.V[index.V[i]] = this.V[i];
/* 324:    */     }
/* 325:501 */     return u;
/* 326:    */   }
/* 327:    */   
/* 328:    */   public DoubleVector plus(double x)
/* 329:    */   {
/* 330:510 */     return copy().plusEquals(x);
/* 331:    */   }
/* 332:    */   
/* 333:    */   public DoubleVector plusEquals(double x)
/* 334:    */   {
/* 335:519 */     for (int i = 0; i < size(); i++) {
/* 336:520 */       this.V[i] += x;
/* 337:    */     }
/* 338:522 */     return this;
/* 339:    */   }
/* 340:    */   
/* 341:    */   public DoubleVector plus(DoubleVector v)
/* 342:    */   {
/* 343:531 */     return copy().plusEquals(v);
/* 344:    */   }
/* 345:    */   
/* 346:    */   public DoubleVector plusEquals(DoubleVector v)
/* 347:    */   {
/* 348:540 */     for (int i = 0; i < size(); i++) {
/* 349:541 */       this.V[i] += v.V[i];
/* 350:    */     }
/* 351:543 */     return this;
/* 352:    */   }
/* 353:    */   
/* 354:    */   public DoubleVector minus(double x)
/* 355:    */   {
/* 356:552 */     return plus(-x);
/* 357:    */   }
/* 358:    */   
/* 359:    */   public DoubleVector minusEquals(double x)
/* 360:    */   {
/* 361:561 */     plusEquals(-x);
/* 362:562 */     return this;
/* 363:    */   }
/* 364:    */   
/* 365:    */   public DoubleVector minus(DoubleVector v)
/* 366:    */   {
/* 367:571 */     return copy().minusEquals(v);
/* 368:    */   }
/* 369:    */   
/* 370:    */   public DoubleVector minusEquals(DoubleVector v)
/* 371:    */   {
/* 372:580 */     for (int i = 0; i < size(); i++) {
/* 373:581 */       this.V[i] -= v.V[i];
/* 374:    */     }
/* 375:583 */     return this;
/* 376:    */   }
/* 377:    */   
/* 378:    */   public DoubleVector times(double s)
/* 379:    */   {
/* 380:593 */     return copy().timesEquals(s);
/* 381:    */   }
/* 382:    */   
/* 383:    */   public DoubleVector timesEquals(double s)
/* 384:    */   {
/* 385:603 */     for (int i = 0; i < size(); i++) {
/* 386:604 */       this.V[i] *= s;
/* 387:    */     }
/* 388:606 */     return this;
/* 389:    */   }
/* 390:    */   
/* 391:    */   public DoubleVector times(DoubleVector v)
/* 392:    */   {
/* 393:615 */     return copy().timesEquals(v);
/* 394:    */   }
/* 395:    */   
/* 396:    */   public DoubleVector timesEquals(DoubleVector v)
/* 397:    */   {
/* 398:625 */     for (int i = 0; i < size(); i++) {
/* 399:626 */       this.V[i] *= v.V[i];
/* 400:    */     }
/* 401:628 */     return this;
/* 402:    */   }
/* 403:    */   
/* 404:    */   public DoubleVector dividedBy(DoubleVector v)
/* 405:    */   {
/* 406:637 */     return copy().dividedByEquals(v);
/* 407:    */   }
/* 408:    */   
/* 409:    */   public DoubleVector dividedByEquals(DoubleVector v)
/* 410:    */   {
/* 411:646 */     for (int i = 0; i < size(); i++) {
/* 412:647 */       this.V[i] /= v.V[i];
/* 413:    */     }
/* 414:649 */     return this;
/* 415:    */   }
/* 416:    */   
/* 417:    */   public boolean isEmpty()
/* 418:    */   {
/* 419:656 */     if (size() == 0) {
/* 420:657 */       return true;
/* 421:    */     }
/* 422:659 */     return false;
/* 423:    */   }
/* 424:    */   
/* 425:    */   public DoubleVector cumulate()
/* 426:    */   {
/* 427:666 */     return copy().cumulateInPlace();
/* 428:    */   }
/* 429:    */   
/* 430:    */   public DoubleVector cumulateInPlace()
/* 431:    */   {
/* 432:673 */     for (int i = 1; i < size(); i++) {
/* 433:674 */       this.V[i] += this.V[(i - 1)];
/* 434:    */     }
/* 435:676 */     return this;
/* 436:    */   }
/* 437:    */   
/* 438:    */   public int indexOfMax()
/* 439:    */   {
/* 440:685 */     int index = 0;
/* 441:686 */     double ma = this.V[0];
/* 442:688 */     for (int i = 1; i < size(); i++) {
/* 443:689 */       if (ma < this.V[i])
/* 444:    */       {
/* 445:690 */         ma = this.V[i];
/* 446:691 */         index = i;
/* 447:    */       }
/* 448:    */     }
/* 449:694 */     return index;
/* 450:    */   }
/* 451:    */   
/* 452:    */   public boolean unsorted()
/* 453:    */   {
/* 454:701 */     if (size() < 2) {
/* 455:702 */       return false;
/* 456:    */     }
/* 457:704 */     for (int i = 1; i < size(); i++) {
/* 458:705 */       if (this.V[(i - 1)] > this.V[i]) {
/* 459:706 */         return true;
/* 460:    */       }
/* 461:    */     }
/* 462:709 */     return false;
/* 463:    */   }
/* 464:    */   
/* 465:    */   public DoubleVector cat(DoubleVector v)
/* 466:    */   {
/* 467:718 */     DoubleVector w = new DoubleVector(size() + v.size());
/* 468:719 */     w.set(0, size() - 1, this, 0);
/* 469:720 */     w.set(size(), size() + v.size() - 1, v, 0);
/* 470:721 */     return w;
/* 471:    */   }
/* 472:    */   
/* 473:    */   public void swap(int i, int j)
/* 474:    */   {
/* 475:731 */     if (i == j) {
/* 476:732 */       return;
/* 477:    */     }
/* 478:734 */     double t = this.V[i];
/* 479:735 */     this.V[i] = this.V[j];
/* 480:736 */     this.V[j] = t;
/* 481:    */   }
/* 482:    */   
/* 483:    */   public double max()
/* 484:    */   {
/* 485:743 */     if (size() < 1) {
/* 486:744 */       throw new IllegalArgumentException("zero size");
/* 487:    */     }
/* 488:746 */     double ma = this.V[0];
/* 489:747 */     if (size() < 2) {
/* 490:748 */       return ma;
/* 491:    */     }
/* 492:750 */     for (int i = 1; i < size(); i++) {
/* 493:751 */       if (this.V[i] > ma) {
/* 494:752 */         ma = this.V[i];
/* 495:    */       }
/* 496:    */     }
/* 497:755 */     return ma;
/* 498:    */   }
/* 499:    */   
/* 500:    */   public DoubleVector map(String className, String method)
/* 501:    */   {
/* 502:    */     try
/* 503:    */     {
/* 504:766 */       Class<?> c = Class.forName(className);
/* 505:767 */       Class<?>[] cs = new Class[1];
/* 506:768 */       cs[0] = Double.TYPE;
/* 507:769 */       Method m = c.getMethod(method, cs);
/* 508:    */       
/* 509:771 */       DoubleVector w = new DoubleVector(size());
/* 510:772 */       Object[] obj = new Object[1];
/* 511:773 */       for (int i = 0; i < size(); i++)
/* 512:    */       {
/* 513:774 */         obj[0] = new Double(this.V[i]);
/* 514:775 */         w.set(i, Double.parseDouble(m.invoke(null, obj).toString()));
/* 515:    */       }
/* 516:777 */       return w;
/* 517:    */     }
/* 518:    */     catch (Exception e)
/* 519:    */     {
/* 520:779 */       e.printStackTrace();
/* 521:780 */       System.exit(1);
/* 522:    */     }
/* 523:782 */     return null;
/* 524:    */   }
/* 525:    */   
/* 526:    */   public DoubleVector rev()
/* 527:    */   {
/* 528:789 */     int n = size();
/* 529:790 */     DoubleVector w = new DoubleVector(n);
/* 530:791 */     for (int i = 0; i < n; i++) {
/* 531:792 */       w.V[i] = this.V[(n - i - 1)];
/* 532:    */     }
/* 533:794 */     return w;
/* 534:    */   }
/* 535:    */   
/* 536:    */   public static DoubleVector random(int n)
/* 537:    */   {
/* 538:803 */     DoubleVector v = new DoubleVector(n);
/* 539:804 */     for (int i = 0; i < n; i++) {
/* 540:805 */       v.V[i] = Math.random();
/* 541:    */     }
/* 542:807 */     return v;
/* 543:    */   }
/* 544:    */   
/* 545:    */   public String toString()
/* 546:    */   {
/* 547:815 */     return toString(5, false);
/* 548:    */   }
/* 549:    */   
/* 550:    */   public String toString(int digits, boolean trailing)
/* 551:    */   {
/* 552:825 */     if (isEmpty()) {
/* 553:826 */       return "null vector";
/* 554:    */     }
/* 555:829 */     StringBuffer text = new StringBuffer();
/* 556:830 */     FlexibleDecimalFormat nf = new FlexibleDecimalFormat(digits, trailing);
/* 557:831 */     nf.grouping(true);
/* 558:832 */     for (int i = 0; i < size(); i++) {
/* 559:833 */       nf.update(this.V[i]);
/* 560:    */     }
/* 561:835 */     int count = 0;
/* 562:836 */     int width = 80;
/* 563:838 */     for (int i = 0; i < size(); i++)
/* 564:    */     {
/* 565:839 */       String number = nf.format(this.V[i]);
/* 566:840 */       count += 1 + number.length();
/* 567:841 */       if (count > width - 1)
/* 568:    */       {
/* 569:842 */         text.append('\n');
/* 570:843 */         count = 1 + number.length();
/* 571:    */       }
/* 572:845 */       text.append(" " + number);
/* 573:    */     }
/* 574:848 */     return text.toString();
/* 575:    */   }
/* 576:    */   
/* 577:    */   public String getRevision()
/* 578:    */   {
/* 579:858 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 580:    */   }
/* 581:    */   
/* 582:    */   public static void main(String[] args)
/* 583:    */   {
/* 584:863 */     DoubleVector v = random(10);
/* 585:864 */     DoubleVector a = random(10);
/* 586:865 */     DoubleVector w = a;
/* 587:    */     
/* 588:867 */     System.out.println(random(10).plus(v).plus(w));
/* 589:    */   }
/* 590:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.DoubleVector
 * JD-Core Version:    0.7.0.1
 */