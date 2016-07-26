/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.HashSet;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Set;
/*   9:    */ import no.uib.cipr.matrix.AbstractMatrix;
/*  10:    */ import no.uib.cipr.matrix.DenseVector;
/*  11:    */ import no.uib.cipr.matrix.Matrix;
/*  12:    */ import no.uib.cipr.matrix.MatrixEntry;
/*  13:    */ import no.uib.cipr.matrix.Vector;
/*  14:    */ import no.uib.cipr.matrix.io.MatrixInfo;
/*  15:    */ import no.uib.cipr.matrix.io.MatrixInfo.MatrixField;
/*  16:    */ import no.uib.cipr.matrix.io.MatrixInfo.MatrixSymmetry;
/*  17:    */ import no.uib.cipr.matrix.io.MatrixSize;
/*  18:    */ import no.uib.cipr.matrix.io.MatrixVectorReader;
/*  19:    */ 
/*  20:    */ public class CompRowMatrix
/*  21:    */   extends AbstractMatrix
/*  22:    */ {
/*  23:    */   double[] data;
/*  24:    */   int[] columnIndex;
/*  25:    */   int[] rowPointer;
/*  26:    */   
/*  27:    */   public CompRowMatrix(MatrixVectorReader r)
/*  28:    */     throws IOException
/*  29:    */   {
/*  30: 70 */     super(0, 0);
/*  31:    */     
/*  32:    */ 
/*  33:    */ 
/*  34: 74 */     MatrixInfo info = null;
/*  35: 75 */     if (r.hasInfo()) {
/*  36: 76 */       info = r.readMatrixInfo();
/*  37:    */     } else {
/*  38: 78 */       info = new MatrixInfo(true, MatrixInfo.MatrixField.Real, MatrixInfo.MatrixSymmetry.General);
/*  39:    */     }
/*  40: 82 */     if (info.isPattern()) {
/*  41: 83 */       throw new UnsupportedOperationException("Pattern matrices are not supported");
/*  42:    */     }
/*  43: 85 */     if (info.isDense()) {
/*  44: 86 */       throw new UnsupportedOperationException("Dense matrices are not supported");
/*  45:    */     }
/*  46: 88 */     if (info.isComplex()) {
/*  47: 89 */       throw new UnsupportedOperationException("Complex matrices are not supported");
/*  48:    */     }
/*  49: 93 */     MatrixSize size = r.readMatrixSize(info);
/*  50: 94 */     this.numRows = size.numRows();
/*  51: 95 */     this.numColumns = size.numColumns();
/*  52:    */     
/*  53:    */ 
/*  54: 98 */     int numEntries = size.numEntries();
/*  55: 99 */     int[] row = new int[numEntries];
/*  56:100 */     int[] column = new int[numEntries];
/*  57:101 */     double[] entry = new double[numEntries];
/*  58:102 */     r.readCoordinate(row, column, entry);
/*  59:    */     
/*  60:    */ 
/*  61:105 */     r.add(-1, row);
/*  62:106 */     r.add(-1, column);
/*  63:    */     
/*  64:    */ 
/*  65:109 */     List<Set<Integer>> rnz = new ArrayList(this.numRows);
/*  66:110 */     for (int i = 0; i < this.numRows; i++) {
/*  67:111 */       rnz.add(new HashSet());
/*  68:    */     }
/*  69:113 */     for (int i = 0; i < numEntries; i++) {
/*  70:114 */       ((Set)rnz.get(row[i])).add(Integer.valueOf(column[i]));
/*  71:    */     }
/*  72:117 */     if ((info.isSymmetric()) || (info.isSkewSymmetric())) {
/*  73:118 */       for (int i = 0; i < numEntries; i++) {
/*  74:119 */         if (row[i] != column[i]) {
/*  75:120 */           ((Set)rnz.get(column[i])).add(Integer.valueOf(row[i]));
/*  76:    */         }
/*  77:    */       }
/*  78:    */     }
/*  79:122 */     int[][] nz = new int[this.numRows][];
/*  80:    */     int j;
/*  81:123 */     for (int i = 0; i < this.numRows; i++)
/*  82:    */     {
/*  83:124 */       nz[i] = new int[((Set)rnz.get(i)).size()];
/*  84:125 */       j = 0;
/*  85:126 */       for (Integer colind : (Set)rnz.get(i)) {
/*  86:127 */         nz[i][(j++)] = colind.intValue();
/*  87:    */       }
/*  88:    */     }
/*  89:131 */     construct(nz);
/*  90:134 */     for (int i = 0; i < size.numEntries(); i++) {
/*  91:135 */       set(row[i], column[i], entry[i]);
/*  92:    */     }
/*  93:138 */     if (info.isSymmetric()) {
/*  94:139 */       for (int i = 0; i < numEntries; i++) {
/*  95:140 */         if (row[i] != column[i]) {
/*  96:141 */           set(column[i], row[i], entry[i]);
/*  97:    */         }
/*  98:    */       }
/*  99:143 */     } else if (info.isSkewSymmetric()) {
/* 100:144 */       for (int i = 0; i < numEntries; i++) {
/* 101:145 */         if (row[i] != column[i]) {
/* 102:146 */           set(column[i], row[i], -entry[i]);
/* 103:    */         }
/* 104:    */       }
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public CompRowMatrix(int numRows, int numColumns, int[][] nz)
/* 109:    */   {
/* 110:161 */     super(numRows, numColumns);
/* 111:162 */     construct(nz);
/* 112:    */   }
/* 113:    */   
/* 114:    */   private void construct(int[][] nz)
/* 115:    */   {
/* 116:166 */     int nnz = 0;
/* 117:167 */     for (int i = 0; i < nz.length; i++) {
/* 118:168 */       nnz += nz[i].length;
/* 119:    */     }
/* 120:170 */     this.rowPointer = new int[this.numRows + 1];
/* 121:171 */     this.columnIndex = new int[nnz];
/* 122:172 */     this.data = new double[nnz];
/* 123:174 */     if (nz.length != this.numRows) {
/* 124:175 */       throw new IllegalArgumentException("nz.length != numRows");
/* 125:    */     }
/* 126:177 */     for (int i = 1; i <= this.numRows; i++)
/* 127:    */     {
/* 128:178 */       this.rowPointer[i] = (this.rowPointer[(i - 1)] + nz[(i - 1)].length);
/* 129:    */       
/* 130:180 */       int j = this.rowPointer[(i - 1)];
/* 131:180 */       for (int k = 0; j < this.rowPointer[i]; k++)
/* 132:    */       {
/* 133:181 */         this.columnIndex[j] = nz[(i - 1)][k];
/* 134:182 */         if ((nz[(i - 1)][k] < 0) || (nz[(i - 1)][k] >= this.numColumns)) {
/* 135:183 */           throw new IllegalArgumentException("nz[" + (i - 1) + "][" + k + "]=" + nz[(i - 1)][k] + ", which is not a valid column index");
/* 136:    */         }
/* 137:180 */         j++;
/* 138:    */       }
/* 139:188 */       java.util.Arrays.sort(this.columnIndex, this.rowPointer[(i - 1)], this.rowPointer[i]);
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   private void construct(Matrix A, boolean deep)
/* 144:    */   {
/* 145:193 */     if (deep)
/* 146:    */     {
/* 147:194 */       if ((A instanceof CompRowMatrix))
/* 148:    */       {
/* 149:195 */         CompRowMatrix Ac = (CompRowMatrix)A;
/* 150:196 */         this.data = new double[Ac.data.length];
/* 151:197 */         this.columnIndex = new int[Ac.columnIndex.length];
/* 152:198 */         this.rowPointer = new int[Ac.rowPointer.length];
/* 153:    */         
/* 154:200 */         System.arraycopy(Ac.data, 0, this.data, 0, this.data.length);
/* 155:201 */         System.arraycopy(Ac.columnIndex, 0, this.columnIndex, 0, this.columnIndex.length);
/* 156:    */         
/* 157:203 */         System.arraycopy(Ac.rowPointer, 0, this.rowPointer, 0, this.rowPointer.length);
/* 158:    */       }
/* 159:    */       else
/* 160:    */       {
/* 161:207 */         List<Set<Integer>> rnz = new ArrayList(this.numRows);
/* 162:208 */         for (int i = 0; i < this.numRows; i++) {
/* 163:209 */           rnz.add(new HashSet());
/* 164:    */         }
/* 165:211 */         for (MatrixEntry e : A) {
/* 166:212 */           ((Set)rnz.get(e.row())).add(Integer.valueOf(e.column()));
/* 167:    */         }
/* 168:214 */         int[][] nz = new int[this.numRows][];
/* 169:    */         int j;
/* 170:215 */         for (int i = 0; i < this.numRows; i++)
/* 171:    */         {
/* 172:216 */           nz[i] = new int[((Set)rnz.get(i)).size()];
/* 173:217 */           j = 0;
/* 174:218 */           for (Integer colind : (Set)rnz.get(i)) {
/* 175:219 */             nz[i][(j++)] = colind.intValue();
/* 176:    */           }
/* 177:    */         }
/* 178:222 */         construct(nz);
/* 179:223 */         set(A);
/* 180:    */       }
/* 181:    */     }
/* 182:    */     else
/* 183:    */     {
/* 184:227 */       CompRowMatrix Ac = (CompRowMatrix)A;
/* 185:228 */       this.columnIndex = Ac.getColumnIndices();
/* 186:229 */       this.rowPointer = Ac.getRowPointers();
/* 187:230 */       this.data = Ac.getData();
/* 188:    */     }
/* 189:    */   }
/* 190:    */   
/* 191:    */   public CompRowMatrix(Matrix A, boolean deep)
/* 192:    */   {
/* 193:244 */     super(A);
/* 194:245 */     construct(A, deep);
/* 195:    */   }
/* 196:    */   
/* 197:    */   public CompRowMatrix(Matrix A)
/* 198:    */   {
/* 199:255 */     this(A, true);
/* 200:    */   }
/* 201:    */   
/* 202:    */   public int[] getColumnIndices()
/* 203:    */   {
/* 204:262 */     return this.columnIndex;
/* 205:    */   }
/* 206:    */   
/* 207:    */   public int[] getRowPointers()
/* 208:    */   {
/* 209:269 */     return this.rowPointer;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public double[] getData()
/* 213:    */   {
/* 214:276 */     return this.data;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public Matrix mult(Matrix B, Matrix C)
/* 218:    */   {
/* 219:281 */     checkMultAdd(B, C);
/* 220:282 */     C.zero();
/* 221:286 */     for (int i = 0; i < this.numRows; i++) {
/* 222:287 */       for (int j = 0; j < C.numColumns(); j++)
/* 223:    */       {
/* 224:288 */         double dot = 0.0D;
/* 225:289 */         for (int k = this.rowPointer[i]; k < this.rowPointer[(i + 1)]; k++) {
/* 226:290 */           dot += this.data[k] * B.get(this.columnIndex[k], j);
/* 227:    */         }
/* 228:292 */         if (dot != 0.0D) {
/* 229:293 */           C.set(i, j, dot);
/* 230:    */         }
/* 231:    */       }
/* 232:    */     }
/* 233:297 */     return C;
/* 234:    */   }
/* 235:    */   
/* 236:    */   public Vector mult(Vector x, Vector y)
/* 237:    */   {
/* 238:303 */     checkMultAdd(x, y);
/* 239:    */     
/* 240:305 */     y.zero();
/* 241:307 */     if ((x instanceof DenseVector))
/* 242:    */     {
/* 243:309 */       double[] xd = ((DenseVector)x).getData();
/* 244:310 */       for (int i = 0; i < this.numRows; i++)
/* 245:    */       {
/* 246:311 */         double dot = 0.0D;
/* 247:312 */         for (int j = this.rowPointer[i]; j < this.rowPointer[(i + 1)]; j++) {
/* 248:313 */           dot += this.data[j] * xd[this.columnIndex[j]];
/* 249:    */         }
/* 250:315 */         if (dot != 0.0D) {
/* 251:316 */           y.set(i, dot);
/* 252:    */         }
/* 253:    */       }
/* 254:319 */       return y;
/* 255:    */     }
/* 256:325 */     for (int i = 0; i < this.numRows; i++)
/* 257:    */     {
/* 258:326 */       double dot = 0.0D;
/* 259:327 */       for (int j = this.rowPointer[i]; j < this.rowPointer[(i + 1)]; j++) {
/* 260:328 */         dot += this.data[j] * x.get(this.columnIndex[j]);
/* 261:    */       }
/* 262:330 */       y.set(i, dot);
/* 263:    */     }
/* 264:332 */     return y;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public Vector multAdd(double alpha, Vector x, Vector y)
/* 268:    */   {
/* 269:337 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/* 270:338 */       return super.multAdd(alpha, x, y);
/* 271:    */     }
/* 272:340 */     checkMultAdd(x, y);
/* 273:    */     
/* 274:342 */     double[] xd = ((DenseVector)x).getData();
/* 275:343 */     double[] yd = ((DenseVector)y).getData();
/* 276:345 */     for (int i = 0; i < this.numRows; i++)
/* 277:    */     {
/* 278:346 */       double dot = 0.0D;
/* 279:347 */       for (int j = this.rowPointer[i]; j < this.rowPointer[(i + 1)]; j++) {
/* 280:348 */         dot += this.data[j] * xd[this.columnIndex[j]];
/* 281:    */       }
/* 282:349 */       yd[i] += alpha * dot;
/* 283:    */     }
/* 284:352 */     return y;
/* 285:    */   }
/* 286:    */   
/* 287:    */   public Vector transMult(Vector x, Vector y)
/* 288:    */   {
/* 289:357 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/* 290:358 */       return super.transMult(x, y);
/* 291:    */     }
/* 292:360 */     checkTransMultAdd(x, y);
/* 293:    */     
/* 294:362 */     double[] xd = ((DenseVector)x).getData();
/* 295:363 */     double[] yd = ((DenseVector)y).getData();
/* 296:    */     
/* 297:365 */     y.zero();
/* 298:367 */     for (int i = 0; i < this.numRows; i++) {
/* 299:368 */       for (int j = this.rowPointer[i]; j < this.rowPointer[(i + 1)]; j++) {
/* 300:369 */         yd[this.columnIndex[j]] += this.data[j] * xd[i];
/* 301:    */       }
/* 302:    */     }
/* 303:371 */     return y;
/* 304:    */   }
/* 305:    */   
/* 306:    */   public Vector transMultAdd(double alpha, Vector x, Vector y)
/* 307:    */   {
/* 308:376 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/* 309:377 */       return super.transMultAdd(alpha, x, y);
/* 310:    */     }
/* 311:379 */     checkTransMultAdd(x, y);
/* 312:    */     
/* 313:381 */     double[] xd = ((DenseVector)x).getData();
/* 314:382 */     double[] yd = ((DenseVector)y).getData();
/* 315:    */     
/* 316:    */ 
/* 317:385 */     y.scale(1.0D / alpha);
/* 318:388 */     for (int i = 0; i < this.numRows; i++) {
/* 319:389 */       for (int j = this.rowPointer[i]; j < this.rowPointer[(i + 1)]; j++) {
/* 320:390 */         yd[this.columnIndex[j]] += this.data[j] * xd[i];
/* 321:    */       }
/* 322:    */     }
/* 323:393 */     return y.scale(alpha);
/* 324:    */   }
/* 325:    */   
/* 326:    */   public void set(int row, int column, double value)
/* 327:    */   {
/* 328:398 */     check(row, column);
/* 329:    */     
/* 330:400 */     int index = getIndex(row, column);
/* 331:401 */     this.data[index] = value;
/* 332:    */   }
/* 333:    */   
/* 334:    */   public void add(int row, int column, double value)
/* 335:    */   {
/* 336:406 */     check(row, column);
/* 337:    */     
/* 338:408 */     int index = getIndex(row, column);
/* 339:409 */     this.data[index] += value;
/* 340:    */   }
/* 341:    */   
/* 342:    */   public double get(int row, int column)
/* 343:    */   {
/* 344:414 */     check(row, column);
/* 345:    */     
/* 346:416 */     int index = Arrays.binarySearch(this.columnIndex, column, this.rowPointer[row], this.rowPointer[(row + 1)]);
/* 347:419 */     if (index >= 0) {
/* 348:420 */       return this.data[index];
/* 349:    */     }
/* 350:422 */     return 0.0D;
/* 351:    */   }
/* 352:    */   
/* 353:    */   private int getIndex(int row, int column)
/* 354:    */   {
/* 355:429 */     int i = Arrays.binarySearch(this.columnIndex, column, this.rowPointer[row], this.rowPointer[(row + 1)]);
/* 356:432 */     if ((i != -1) && (this.columnIndex[i] == column)) {
/* 357:433 */       return i;
/* 358:    */     }
/* 359:435 */     throw new IndexOutOfBoundsException("Entry (" + (row + 1) + ", " + (column + 1) + ") is not in the matrix structure");
/* 360:    */   }
/* 361:    */   
/* 362:    */   public CompRowMatrix copy()
/* 363:    */   {
/* 364:441 */     return new CompRowMatrix(this);
/* 365:    */   }
/* 366:    */   
/* 367:    */   public Iterator<MatrixEntry> iterator()
/* 368:    */   {
/* 369:446 */     return new CompRowMatrixIterator();
/* 370:    */   }
/* 371:    */   
/* 372:    */   public CompRowMatrix zero()
/* 373:    */   {
/* 374:451 */     java.util.Arrays.fill(this.data, 0.0D);
/* 375:452 */     return this;
/* 376:    */   }
/* 377:    */   
/* 378:    */   public Matrix set(Matrix B)
/* 379:    */   {
/* 380:457 */     if (!(B instanceof CompRowMatrix)) {
/* 381:458 */       return super.set(B);
/* 382:    */     }
/* 383:460 */     checkSize(B);
/* 384:    */     
/* 385:462 */     CompRowMatrix Bc = (CompRowMatrix)B;
/* 386:465 */     if ((Bc.columnIndex.length != this.columnIndex.length) || (Bc.rowPointer.length != this.rowPointer.length))
/* 387:    */     {
/* 388:467 */       this.data = new double[Bc.data.length];
/* 389:468 */       this.columnIndex = new int[Bc.columnIndex.length];
/* 390:469 */       this.rowPointer = new int[Bc.rowPointer.length];
/* 391:    */     }
/* 392:472 */     System.arraycopy(Bc.data, 0, this.data, 0, this.data.length);
/* 393:473 */     System.arraycopy(Bc.columnIndex, 0, this.columnIndex, 0, this.columnIndex.length);
/* 394:474 */     System.arraycopy(Bc.rowPointer, 0, this.rowPointer, 0, this.rowPointer.length);
/* 395:    */     
/* 396:476 */     return this;
/* 397:    */   }
/* 398:    */   
/* 399:    */   private class CompRowMatrixIterator
/* 400:    */     implements Iterator<MatrixEntry>
/* 401:    */   {
/* 402:    */     private int row;
/* 403:    */     private int cursor;
/* 404:486 */     private CompRowMatrix.CompRowMatrixEntry entry = new CompRowMatrix.CompRowMatrixEntry(CompRowMatrix.this, null);
/* 405:    */     
/* 406:    */     public CompRowMatrixIterator()
/* 407:    */     {
/* 408:490 */       nextNonEmptyRow();
/* 409:    */     }
/* 410:    */     
/* 411:    */     private void nextNonEmptyRow()
/* 412:    */     {
/* 413:498 */       while ((this.row < CompRowMatrix.this.numRows()) && (CompRowMatrix.this.rowPointer[this.row] == CompRowMatrix.this.rowPointer[(this.row + 1)])) {
/* 414:499 */         this.row += 1;
/* 415:    */       }
/* 416:500 */       this.cursor = CompRowMatrix.this.rowPointer[this.row];
/* 417:    */     }
/* 418:    */     
/* 419:    */     public boolean hasNext()
/* 420:    */     {
/* 421:504 */       return this.cursor < CompRowMatrix.this.data.length;
/* 422:    */     }
/* 423:    */     
/* 424:    */     public MatrixEntry next()
/* 425:    */     {
/* 426:508 */       this.entry.update(this.row, this.cursor);
/* 427:511 */       if (this.cursor < CompRowMatrix.this.rowPointer[(this.row + 1)] - 1)
/* 428:    */       {
/* 429:512 */         this.cursor += 1;
/* 430:    */       }
/* 431:    */       else
/* 432:    */       {
/* 433:516 */         this.row += 1;
/* 434:517 */         nextNonEmptyRow();
/* 435:    */       }
/* 436:520 */       return this.entry;
/* 437:    */     }
/* 438:    */     
/* 439:    */     public void remove()
/* 440:    */     {
/* 441:524 */       this.entry.set(0.0D);
/* 442:    */     }
/* 443:    */   }
/* 444:    */   
/* 445:    */   private class CompRowMatrixEntry
/* 446:    */     implements MatrixEntry
/* 447:    */   {
/* 448:    */     private int row;
/* 449:    */     private int cursor;
/* 450:    */     
/* 451:    */     private CompRowMatrixEntry() {}
/* 452:    */     
/* 453:    */     public void update(int row, int cursor)
/* 454:    */     {
/* 455:540 */       this.row = row;
/* 456:541 */       this.cursor = cursor;
/* 457:    */     }
/* 458:    */     
/* 459:    */     public int row()
/* 460:    */     {
/* 461:545 */       return this.row;
/* 462:    */     }
/* 463:    */     
/* 464:    */     public int column()
/* 465:    */     {
/* 466:549 */       return CompRowMatrix.this.columnIndex[this.cursor];
/* 467:    */     }
/* 468:    */     
/* 469:    */     public double get()
/* 470:    */     {
/* 471:553 */       return CompRowMatrix.this.data[this.cursor];
/* 472:    */     }
/* 473:    */     
/* 474:    */     public void set(double value)
/* 475:    */     {
/* 476:557 */       CompRowMatrix.this.data[this.cursor] = value;
/* 477:    */     }
/* 478:    */   }
/* 479:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.CompRowMatrix
 * JD-Core Version:    0.7.0.1
 */