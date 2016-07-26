/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import java.util.Iterator;
/*   5:    */ 
/*   6:    */ public final class Matrices
/*   7:    */ {
/*   8:    */   static int ld(int n)
/*   9:    */   {
/*  10: 41 */     return Math.max(1, n);
/*  11:    */   }
/*  12:    */   
/*  13:    */   static int ld(int m, int n)
/*  14:    */   {
/*  15: 52 */     return Math.max(1, Math.max(m, n));
/*  16:    */   }
/*  17:    */   
/*  18:    */   public static int cardinality(Vector x)
/*  19:    */   {
/*  20: 59 */     int nz = 0;
/*  21: 60 */     for (VectorEntry e : x) {
/*  22: 61 */       if (e.get() != 0.0D) {
/*  23: 62 */         nz++;
/*  24:    */       }
/*  25:    */     }
/*  26: 63 */     return nz;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static int cardinality(Matrix A)
/*  30:    */   {
/*  31: 70 */     int nz = 0;
/*  32: 71 */     for (MatrixEntry e : A) {
/*  33: 72 */       if (e.get() != 0.0D) {
/*  34: 73 */         nz++;
/*  35:    */       }
/*  36:    */     }
/*  37: 74 */     return nz;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public static double[][] getArray(Matrix A)
/*  41:    */   {
/*  42: 82 */     double[][] Ad = new double[A.numRows()][A.numColumns()];
/*  43: 83 */     for (MatrixEntry e : A) {
/*  44: 84 */       Ad[e.row()][e.column()] = e.get();
/*  45:    */     }
/*  46: 85 */     return Ad;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static double[] getArray(Vector x)
/*  50:    */   {
/*  51: 92 */     double[] xd = new double[x.size()];
/*  52: 93 */     for (VectorEntry e : x) {
/*  53: 94 */       xd[e.index()] = e.get();
/*  54:    */     }
/*  55: 95 */     return xd;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static DenseMatrix identity(int size)
/*  59:    */   {
/*  60:106 */     DenseMatrix A = new DenseMatrix(size, size);
/*  61:107 */     for (int i = 0; i < size; i++) {
/*  62:108 */       A.set(i, i, 1.0D);
/*  63:    */     }
/*  64:109 */     return A;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static Vector random(int size)
/*  68:    */   {
/*  69:120 */     return random(new DenseVector(size));
/*  70:    */   }
/*  71:    */   
/*  72:    */   public static Vector random(Vector x)
/*  73:    */   {
/*  74:131 */     for (int i = 0; i < x.size(); i++) {
/*  75:132 */       x.set(i, Math.random());
/*  76:    */     }
/*  77:133 */     return x;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static Matrix random(int numRows, int numColumns)
/*  81:    */   {
/*  82:146 */     return random(new DenseMatrix(numRows, numColumns));
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static Matrix random(Matrix A)
/*  86:    */   {
/*  87:157 */     for (int j = 0; j < A.numColumns(); j++) {
/*  88:158 */       for (int i = 0; i < A.numRows(); i++) {
/*  89:159 */         A.set(i, j, Math.random());
/*  90:    */       }
/*  91:    */     }
/*  92:160 */     return A;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static Vector synchronizedVector(Vector x)
/*  96:    */   {
/*  97:176 */     return new SynchronizedVector(x);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static Matrix synchronizedMatrix(Matrix A)
/* 101:    */   {
/* 102:192 */     return new SynchronizedMatrix(A);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static Matrix synchronizedMatrixByRows(Matrix A)
/* 106:    */   {
/* 107:213 */     return new SynchronizedRowMatrix(A);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public static Matrix synchronizedMatrixByColumns(Matrix A)
/* 111:    */   {
/* 112:235 */     return new SynchronizedColumnMatrix(A);
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static Matrix getSubMatrix(Matrix A, int[] row, int[] column)
/* 116:    */   {
/* 117:254 */     return new RefMatrix(A, row, column);
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static Vector getSubVector(Vector x, int[] index)
/* 121:    */   {
/* 122:271 */     return new RefVector(x, index);
/* 123:    */   }
/* 124:    */   
/* 125:    */   private static class RefMatrix
/* 126:    */     extends AbstractMatrix
/* 127:    */   {
/* 128:    */     private Matrix A;
/* 129:    */     private int[] row;
/* 130:    */     private int[] column;
/* 131:    */     
/* 132:    */     public RefMatrix(Matrix A, int[] row, int[] column)
/* 133:    */     {
/* 134:284 */       super(column.length);
/* 135:285 */       this.A = A;
/* 136:286 */       this.row = row;
/* 137:287 */       this.column = column;
/* 138:    */     }
/* 139:    */     
/* 140:    */     public void add(int row, int column, double value)
/* 141:    */     {
/* 142:292 */       this.A.add(this.row[row], this.column[column], value);
/* 143:    */     }
/* 144:    */     
/* 145:    */     public DenseMatrix copy()
/* 146:    */     {
/* 147:297 */       return new DenseMatrix(this);
/* 148:    */     }
/* 149:    */     
/* 150:    */     public double get(int row, int column)
/* 151:    */     {
/* 152:302 */       return this.A.get(this.row[row], this.column[column]);
/* 153:    */     }
/* 154:    */     
/* 155:    */     public void set(int row, int column, double value)
/* 156:    */     {
/* 157:307 */       this.A.set(this.row[row], this.column[column], value);
/* 158:    */     }
/* 159:    */   }
/* 160:    */   
/* 161:    */   private static class RefVector
/* 162:    */     extends AbstractVector
/* 163:    */   {
/* 164:    */     private Vector x;
/* 165:    */     private int[] index;
/* 166:    */     
/* 167:    */     public RefVector(Vector x, int[] index)
/* 168:    */     {
/* 169:322 */       super();
/* 170:323 */       this.x = x;
/* 171:324 */       this.index = index;
/* 172:    */     }
/* 173:    */     
/* 174:    */     public void add(int index, double value)
/* 175:    */     {
/* 176:329 */       this.x.add(this.index[index], value);
/* 177:    */     }
/* 178:    */     
/* 179:    */     public DenseVector copy()
/* 180:    */     {
/* 181:334 */       return new DenseVector(this);
/* 182:    */     }
/* 183:    */     
/* 184:    */     public double get(int index)
/* 185:    */     {
/* 186:339 */       return this.x.get(this.index[index]);
/* 187:    */     }
/* 188:    */     
/* 189:    */     public void set(int index, double value)
/* 190:    */     {
/* 191:344 */       this.x.set(this.index[index], value);
/* 192:    */     }
/* 193:    */   }
/* 194:    */   
/* 195:    */   private static class SynchronizedVector
/* 196:    */     extends AbstractVector
/* 197:    */   {
/* 198:    */     private Vector x;
/* 199:    */     
/* 200:    */     public SynchronizedVector(Vector x)
/* 201:    */     {
/* 202:360 */       super();
/* 203:361 */       this.x = x;
/* 204:    */     }
/* 205:    */     
/* 206:    */     public synchronized void add(int index, double value)
/* 207:    */     {
/* 208:366 */       this.x.add(index, value);
/* 209:    */     }
/* 210:    */     
/* 211:    */     public synchronized void set(int index, double value)
/* 212:    */     {
/* 213:371 */       this.x.set(index, value);
/* 214:    */     }
/* 215:    */     
/* 216:    */     public synchronized double get(int index)
/* 217:    */     {
/* 218:376 */       return this.x.get(index);
/* 219:    */     }
/* 220:    */     
/* 221:    */     public Vector copy()
/* 222:    */     {
/* 223:381 */       return Matrices.synchronizedVector(this.x.copy());
/* 224:    */     }
/* 225:    */   }
/* 226:    */   
/* 227:    */   private static class SynchronizedMatrix
/* 228:    */     extends AbstractMatrix
/* 229:    */   {
/* 230:    */     private Matrix A;
/* 231:    */     
/* 232:    */     public SynchronizedMatrix(Matrix A)
/* 233:    */     {
/* 234:397 */       super();
/* 235:398 */       this.A = A;
/* 236:    */     }
/* 237:    */     
/* 238:    */     public synchronized void add(int row, int column, double value)
/* 239:    */     {
/* 240:403 */       this.A.add(row, column, value);
/* 241:    */     }
/* 242:    */     
/* 243:    */     public synchronized void set(int row, int column, double value)
/* 244:    */     {
/* 245:408 */       this.A.set(row, column, value);
/* 246:    */     }
/* 247:    */     
/* 248:    */     public synchronized double get(int row, int column)
/* 249:    */     {
/* 250:413 */       return this.A.get(row, column);
/* 251:    */     }
/* 252:    */     
/* 253:    */     public Matrix copy()
/* 254:    */     {
/* 255:418 */       return Matrices.synchronizedMatrix(this.A.copy());
/* 256:    */     }
/* 257:    */   }
/* 258:    */   
/* 259:    */   private static class SynchronizedRowMatrix
/* 260:    */     extends AbstractMatrix
/* 261:    */   {
/* 262:    */     private Matrix A;
/* 263:    */     private Object[] lock;
/* 264:    */     
/* 265:    */     public SynchronizedRowMatrix(Matrix A)
/* 266:    */     {
/* 267:438 */       super();
/* 268:439 */       this.A = A;
/* 269:440 */       this.lock = new Object[A.numRows()];
/* 270:441 */       for (int i = 0; i < this.lock.length; i++) {
/* 271:442 */         this.lock[i] = new Object();
/* 272:    */       }
/* 273:    */     }
/* 274:    */     
/* 275:    */     public void add(int row, int column, double value)
/* 276:    */     {
/* 277:447 */       synchronized (this.lock[row])
/* 278:    */       {
/* 279:448 */         this.A.add(row, column, value);
/* 280:    */       }
/* 281:    */     }
/* 282:    */     
/* 283:    */     public void set(int row, int column, double value)
/* 284:    */     {
/* 285:454 */       synchronized (this.lock[row])
/* 286:    */       {
/* 287:455 */         this.A.set(row, column, value);
/* 288:    */       }
/* 289:    */     }
/* 290:    */     
/* 291:    */     public double get(int row, int column)
/* 292:    */     {
/* 293:461 */       return this.A.get(row, column);
/* 294:    */     }
/* 295:    */     
/* 296:    */     public Matrix copy()
/* 297:    */     {
/* 298:466 */       return Matrices.synchronizedMatrixByRows(this.A.copy());
/* 299:    */     }
/* 300:    */   }
/* 301:    */   
/* 302:    */   private static class SynchronizedColumnMatrix
/* 303:    */     extends AbstractMatrix
/* 304:    */   {
/* 305:    */     private Matrix A;
/* 306:    */     private Object[] lock;
/* 307:    */     
/* 308:    */     public SynchronizedColumnMatrix(Matrix A)
/* 309:    */     {
/* 310:485 */       super();
/* 311:486 */       this.A = A;
/* 312:487 */       this.lock = new Object[A.numColumns()];
/* 313:488 */       for (int i = 0; i < this.lock.length; i++) {
/* 314:489 */         this.lock[i] = new Object();
/* 315:    */       }
/* 316:    */     }
/* 317:    */     
/* 318:    */     public void add(int row, int column, double value)
/* 319:    */     {
/* 320:494 */       synchronized (this.lock[column])
/* 321:    */       {
/* 322:495 */         this.A.add(row, column, value);
/* 323:    */       }
/* 324:    */     }
/* 325:    */     
/* 326:    */     public void set(int row, int column, double value)
/* 327:    */     {
/* 328:501 */       synchronized (this.lock[column])
/* 329:    */       {
/* 330:502 */         this.A.set(row, column, value);
/* 331:    */       }
/* 332:    */     }
/* 333:    */     
/* 334:    */     public double get(int row, int column)
/* 335:    */     {
/* 336:508 */       return this.A.get(row, column);
/* 337:    */     }
/* 338:    */     
/* 339:    */     public Matrix copy()
/* 340:    */     {
/* 341:513 */       return Matrices.synchronizedMatrixByColumns(this.A.copy());
/* 342:    */     }
/* 343:    */   }
/* 344:    */   
/* 345:    */   public static int[] index(int from, int to)
/* 346:    */   {
/* 347:527 */     int length = to - from;
/* 348:529 */     if (length < 0) {
/* 349:530 */       length = 0;
/* 350:    */     }
/* 351:532 */     int[] index = new int[length];
/* 352:533 */     int i = from;
/* 353:533 */     for (int j = 0; j < length; j++)
/* 354:    */     {
/* 355:534 */       index[j] = i;i++;
/* 356:    */     }
/* 357:535 */     return index;
/* 358:    */   }
/* 359:    */   
/* 360:    */   public static int[] index(int from, int stride, int to)
/* 361:    */   {
/* 362:550 */     if (stride == 1) {
/* 363:551 */       return index(from, to);
/* 364:    */     }
/* 365:552 */     if (stride == 0) {
/* 366:553 */       return new int[0];
/* 367:    */     }
/* 368:555 */     if ((to <= from) && (stride > 0)) {
/* 369:556 */       return new int[0];
/* 370:    */     }
/* 371:557 */     if ((from <= to) && (stride < 0)) {
/* 372:558 */       return new int[0];
/* 373:    */     }
/* 374:560 */     int length = Math.abs((to - from) / stride);
/* 375:561 */     if (Math.abs((to - from) % stride) > 0) {
/* 376:562 */       length++;
/* 377:    */     }
/* 378:564 */     if (length < 0) {
/* 379:565 */       length = 0;
/* 380:    */     }
/* 381:567 */     int[] index = new int[length];
/* 382:568 */     int i = from;
/* 383:568 */     for (int j = 0; j < length; j++)
/* 384:    */     {
/* 385:569 */       index[j] = i;i += stride;
/* 386:    */     }
/* 387:570 */     return index;
/* 388:    */   }
/* 389:    */   
/* 390:    */   public static int[] rowBandwidth(Matrix A)
/* 391:    */   {
/* 392:577 */     int[] nz = new int[A.numRows()];
/* 393:579 */     for (MatrixEntry e : A) {
/* 394:580 */       nz[e.row()] += 1;
/* 395:    */     }
/* 396:582 */     return nz;
/* 397:    */   }
/* 398:    */   
/* 399:    */   public static int[] columnBandwidth(Matrix A)
/* 400:    */   {
/* 401:589 */     int[] nz = new int[A.numColumns()];
/* 402:591 */     for (MatrixEntry e : A) {
/* 403:592 */       nz[e.column()] += 1;
/* 404:    */     }
/* 405:594 */     return nz;
/* 406:    */   }
/* 407:    */   
/* 408:    */   public static int getNumSubDiagonals(Matrix A)
/* 409:    */   {
/* 410:602 */     int kl = 0;
/* 411:604 */     for (MatrixEntry e : A) {
/* 412:605 */       kl = Math.max(kl, e.row() - e.column());
/* 413:    */     }
/* 414:607 */     return kl;
/* 415:    */   }
/* 416:    */   
/* 417:    */   public static int getNumSuperDiagonals(Matrix A)
/* 418:    */   {
/* 419:615 */     int ku = 0;
/* 420:617 */     for (MatrixEntry e : A) {
/* 421:618 */       ku = Math.max(ku, e.column() - e.row());
/* 422:    */     }
/* 423:620 */     return ku;
/* 424:    */   }
/* 425:    */   
/* 426:    */   public static void zeroRows(Matrix A, double diagonal, int... row)
/* 427:    */   {
/* 428:630 */     int[] rowS = (int[])row.clone();
/* 429:631 */     Arrays.sort(rowS);
/* 430:633 */     for (Object localObject = A.iterator(); ((Iterator)localObject).hasNext();)
/* 431:    */     {
/* 432:633 */       e = (MatrixEntry)((Iterator)localObject).next();
/* 433:634 */       j = Arrays.binarySearch(rowS, e.row());
/* 434:635 */       if (j >= 0) {
/* 435:636 */         if (e.row() == e.column()) {
/* 436:637 */           e.set(diagonal);
/* 437:    */         } else {
/* 438:640 */           e.set(0.0D);
/* 439:    */         }
/* 440:    */       }
/* 441:    */     }
/* 442:    */     MatrixEntry e;
/* 443:    */     int j;
/* 444:646 */     if (diagonal != 0.0D) {
/* 445:647 */       for (int rowI : row) {
/* 446:648 */         A.set(rowI, rowI, diagonal);
/* 447:    */       }
/* 448:    */     }
/* 449:    */   }
/* 450:    */   
/* 451:    */   public static void zeroColumns(Matrix A, double diagonal, int... column)
/* 452:    */   {
/* 453:658 */     int[] columnS = (int[])column.clone();
/* 454:659 */     Arrays.sort(columnS);
/* 455:661 */     for (Object localObject = A.iterator(); ((Iterator)localObject).hasNext();)
/* 456:    */     {
/* 457:661 */       e = (MatrixEntry)((Iterator)localObject).next();
/* 458:662 */       j = Arrays.binarySearch(columnS, e.column());
/* 459:663 */       if (j >= 0) {
/* 460:664 */         if (e.row() == e.column()) {
/* 461:665 */           e.set(diagonal);
/* 462:    */         } else {
/* 463:668 */           e.set(0.0D);
/* 464:    */         }
/* 465:    */       }
/* 466:    */     }
/* 467:    */     MatrixEntry e;
/* 468:    */     int j;
/* 469:674 */     if (diagonal != 0.0D) {
/* 470:675 */       for (int columnI : column) {
/* 471:676 */         A.set(columnI, columnI, diagonal);
/* 472:    */       }
/* 473:    */     }
/* 474:    */   }
/* 475:    */   
/* 476:    */   public static DenseVector getColumn(Matrix m, int j)
/* 477:    */   {
/* 478:680 */     DenseVector v = new DenseVector(m.numRows());
/* 479:681 */     for (int i = 0; i < v.size(); i++) {
/* 480:682 */       v.set(i, m.get(i, j));
/* 481:    */     }
/* 482:684 */     return v;
/* 483:    */   }
/* 484:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.Matrices
 * JD-Core Version:    0.7.0.1
 */