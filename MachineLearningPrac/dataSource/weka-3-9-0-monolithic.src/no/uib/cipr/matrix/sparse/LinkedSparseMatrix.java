/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import java.beans.ConstructorProperties;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import java.util.logging.Logger;
/*   7:    */ import no.uib.cipr.matrix.AbstractMatrix;
/*   8:    */ import no.uib.cipr.matrix.Matrix;
/*   9:    */ import no.uib.cipr.matrix.MatrixEntry;
/*  10:    */ import no.uib.cipr.matrix.Vector;
/*  11:    */ import no.uib.cipr.matrix.io.MatrixInfo;
/*  12:    */ import no.uib.cipr.matrix.io.MatrixSize;
/*  13:    */ import no.uib.cipr.matrix.io.MatrixVectorReader;
/*  14:    */ 
/*  15:    */ public class LinkedSparseMatrix
/*  16:    */   extends AbstractMatrix
/*  17:    */ {
/*  18: 35 */   private static final Logger log = Logger.getLogger(LinkedSparseMatrix.class.getName());
/*  19:    */   Linked links;
/*  20:    */   
/*  21:    */   static class Node
/*  22:    */   {
/*  23:    */     final int row;
/*  24:    */     final int col;
/*  25:    */     double val;
/*  26:    */     Node rowTail;
/*  27:    */     Node colTail;
/*  28:    */     
/*  29:    */     @ConstructorProperties({"row", "col", "val", "rowTail", "colTail"})
/*  30:    */     public Node(int row, int col, double val, Node rowTail, Node colTail)
/*  31:    */     {
/*  32: 39 */       this.row = row;this.col = col;this.val = val;this.rowTail = rowTail;this.colTail = colTail;
/*  33:    */     }
/*  34:    */     
/*  35:    */     public String toString()
/*  36:    */     {
/*  37: 40 */       return "LinkedSparseMatrix.Node(row=" + this.row + ", col=" + this.col + ", val=" + this.val + ")";
/*  38:    */     }
/*  39:    */   }
/*  40:    */   
/*  41:    */   class Linked
/*  42:    */   {
/*  43: 52 */     final LinkedSparseMatrix.Node head = new LinkedSparseMatrix.Node(0, 0, 0.0D, null, null);
/*  44: 54 */     LinkedSparseMatrix.Node[] rows = new LinkedSparseMatrix.Node[LinkedSparseMatrix.this.numRows];
/*  45: 54 */     LinkedSparseMatrix.Node[] cols = new LinkedSparseMatrix.Node[LinkedSparseMatrix.this.numColumns];
/*  46:    */     
/*  47:    */     Linked() {}
/*  48:    */     
/*  49:    */     private boolean isHead(int row, int col)
/*  50:    */     {
/*  51: 57 */       return (this.head.row == row) && (this.head.col == col);
/*  52:    */     }
/*  53:    */     
/*  54:    */     private boolean isNextByRow(LinkedSparseMatrix.Node node, int row, int col)
/*  55:    */     {
/*  56: 62 */       return (node != null) && (node.rowTail != null) && (node.rowTail.row == row) && (node.rowTail.col == col);
/*  57:    */     }
/*  58:    */     
/*  59:    */     private boolean isNextByCol(LinkedSparseMatrix.Node node, int row, int col)
/*  60:    */     {
/*  61: 68 */       return (node != null) && (node.colTail != null) && (node.colTail.col == col) && (node.colTail.row == row);
/*  62:    */     }
/*  63:    */     
/*  64:    */     public double get(int row, int col)
/*  65:    */     {
/*  66: 73 */       if (isHead(row, col)) {
/*  67: 74 */         return this.head.val;
/*  68:    */       }
/*  69: 75 */       if (col <= row)
/*  70:    */       {
/*  71: 76 */         LinkedSparseMatrix.Node node = findPreceedingByRow(row, col);
/*  72: 77 */         if (isNextByRow(node, row, col)) {
/*  73: 78 */           return node.rowTail.val;
/*  74:    */         }
/*  75:    */       }
/*  76:    */       else
/*  77:    */       {
/*  78: 80 */         LinkedSparseMatrix.Node node = findPreceedingByCol(row, col);
/*  79: 81 */         if (isNextByCol(node, row, col)) {
/*  80: 82 */           return node.colTail.val;
/*  81:    */         }
/*  82:    */       }
/*  83: 84 */       return 0.0D;
/*  84:    */     }
/*  85:    */     
/*  86:    */     public void set(int row, int col, double val)
/*  87:    */     {
/*  88: 88 */       if (val == 0.0D)
/*  89:    */       {
/*  90: 89 */         delete(row, col);
/*  91: 90 */         return;
/*  92:    */       }
/*  93: 92 */       if (isHead(row, col))
/*  94:    */       {
/*  95: 93 */         this.head.val = val;
/*  96:    */       }
/*  97:    */       else
/*  98:    */       {
/*  99: 95 */         LinkedSparseMatrix.Node prevRow = findPreceedingByRow(row, col);
/* 100: 96 */         if (isNextByRow(prevRow, row, col))
/* 101:    */         {
/* 102: 97 */           prevRow.rowTail.val = val;
/* 103:    */         }
/* 104:    */         else
/* 105:    */         {
/* 106: 99 */           LinkedSparseMatrix.Node prevCol = findPreceedingByCol(row, col);
/* 107:100 */           LinkedSparseMatrix.Node nextCol = findNextByCol(row, col);
/* 108:101 */           prevRow.rowTail = new LinkedSparseMatrix.Node(row, col, val, prevRow.rowTail, nextCol);
/* 109:    */           
/* 110:103 */           prevCol.colTail = prevRow.rowTail;
/* 111:104 */           updateCache(prevRow.rowTail);
/* 112:    */         }
/* 113:    */       }
/* 114:    */     }
/* 115:    */     
/* 116:    */     private LinkedSparseMatrix.Node findNextByCol(int row, int col)
/* 117:    */     {
/* 118:110 */       LinkedSparseMatrix.Node cur = cachedByCol(col - 1);
/* 119:111 */       while (cur != null)
/* 120:    */       {
/* 121:112 */         if (((row < cur.row) && (col <= cur.col)) || (col < cur.col)) {
/* 122:113 */           return cur;
/* 123:    */         }
/* 124:114 */         cur = cur.colTail;
/* 125:    */       }
/* 126:116 */       return cur;
/* 127:    */     }
/* 128:    */     
/* 129:    */     private void updateCache(LinkedSparseMatrix.Node inserted)
/* 130:    */     {
/* 131:120 */       if ((this.rows[inserted.row] == null) || (inserted.col > this.rows[inserted.row].col)) {
/* 132:122 */         this.rows[inserted.row] = inserted;
/* 133:    */       }
/* 134:123 */       if ((this.cols[inserted.col] == null) || (inserted.row > this.cols[inserted.col].row)) {
/* 135:125 */         this.cols[inserted.col] = inserted;
/* 136:    */       }
/* 137:    */     }
/* 138:    */     
/* 139:    */     private void delete(int row, int col)
/* 140:    */     {
/* 141:129 */       if (isHead(row, col))
/* 142:    */       {
/* 143:130 */         this.head.val = 0.0D;
/* 144:131 */         return;
/* 145:    */       }
/* 146:133 */       LinkedSparseMatrix.Node precRow = findPreceedingByRow(row, col);
/* 147:134 */       LinkedSparseMatrix.Node precCol = findPreceedingByCol(row, col);
/* 148:135 */       if (isNextByRow(precRow, row, col))
/* 149:    */       {
/* 150:136 */         if (this.rows[row] == precRow.rowTail) {
/* 151:137 */           this.rows[row] = (precRow.row == row ? precRow : null);
/* 152:    */         }
/* 153:138 */         precRow.rowTail = precRow.rowTail.rowTail;
/* 154:    */       }
/* 155:140 */       if (isNextByCol(precCol, row, col))
/* 156:    */       {
/* 157:141 */         if (this.cols[col] == precCol.colTail) {
/* 158:142 */           this.cols[col] = (precCol.col == col ? precCol : null);
/* 159:    */         }
/* 160:143 */         precCol.colTail = precCol.colTail.colTail;
/* 161:    */       }
/* 162:    */     }
/* 163:    */     
/* 164:    */     LinkedSparseMatrix.Node findPreceedingByRow(int row, int col)
/* 165:    */     {
/* 166:150 */       LinkedSparseMatrix.Node last = cachedByRow(row - 1);
/* 167:151 */       LinkedSparseMatrix.Node cur = last;
/* 168:152 */       while ((cur != null) && (cur.row <= row))
/* 169:    */       {
/* 170:153 */         if ((cur.row == row) && (cur.col >= col)) {
/* 171:154 */           return last;
/* 172:    */         }
/* 173:155 */         last = cur;
/* 174:156 */         cur = cur.rowTail;
/* 175:    */       }
/* 176:158 */       return last;
/* 177:    */     }
/* 178:    */     
/* 179:    */     private LinkedSparseMatrix.Node cachedByRow(int row)
/* 180:    */     {
/* 181:163 */       for (int i = row; i >= 0; i--) {
/* 182:164 */         if (this.rows[i] != null) {
/* 183:165 */           return this.rows[i];
/* 184:    */         }
/* 185:    */       }
/* 186:166 */       return this.head;
/* 187:    */     }
/* 188:    */     
/* 189:    */     LinkedSparseMatrix.Node findPreceedingByCol(int row, int col)
/* 190:    */     {
/* 191:170 */       LinkedSparseMatrix.Node last = cachedByCol(col - 1);
/* 192:171 */       LinkedSparseMatrix.Node cur = last;
/* 193:172 */       while ((cur != null) && (cur.col <= col))
/* 194:    */       {
/* 195:173 */         if ((cur.col == col) && (cur.row >= row)) {
/* 196:174 */           return last;
/* 197:    */         }
/* 198:175 */         last = cur;
/* 199:176 */         cur = cur.colTail;
/* 200:    */       }
/* 201:178 */       return last;
/* 202:    */     }
/* 203:    */     
/* 204:    */     private LinkedSparseMatrix.Node cachedByCol(int col)
/* 205:    */     {
/* 206:182 */       for (int i = col; i >= 0; i--) {
/* 207:183 */         if (this.cols[i] != null) {
/* 208:184 */           return this.cols[i];
/* 209:    */         }
/* 210:    */       }
/* 211:185 */       return this.head;
/* 212:    */     }
/* 213:    */     
/* 214:    */     LinkedSparseMatrix.Node startOfRow(int row)
/* 215:    */     {
/* 216:189 */       if (row == 0) {
/* 217:190 */         return this.head;
/* 218:    */       }
/* 219:191 */       LinkedSparseMatrix.Node prec = findPreceedingByRow(row, 0);
/* 220:192 */       if ((prec.rowTail != null) && (prec.rowTail.row == row)) {
/* 221:193 */         return prec.rowTail;
/* 222:    */       }
/* 223:194 */       return null;
/* 224:    */     }
/* 225:    */     
/* 226:    */     LinkedSparseMatrix.Node startOfCol(int col)
/* 227:    */     {
/* 228:198 */       if (col == 0) {
/* 229:199 */         return this.head;
/* 230:    */       }
/* 231:200 */       LinkedSparseMatrix.Node prec = findPreceedingByCol(0, col);
/* 232:201 */       if ((prec != null) && (prec.colTail != null) && (prec.colTail.col == col)) {
/* 233:202 */         return prec.colTail;
/* 234:    */       }
/* 235:203 */       return null;
/* 236:    */     }
/* 237:    */   }
/* 238:    */   
/* 239:    */   public LinkedSparseMatrix(int numRows, int numColumns)
/* 240:    */   {
/* 241:210 */     super(numRows, numColumns);
/* 242:211 */     this.links = new Linked();
/* 243:    */   }
/* 244:    */   
/* 245:    */   public LinkedSparseMatrix(Matrix A)
/* 246:    */   {
/* 247:215 */     super(A);
/* 248:216 */     this.links = new Linked();
/* 249:217 */     set(A);
/* 250:    */   }
/* 251:    */   
/* 252:    */   public LinkedSparseMatrix(MatrixVectorReader r)
/* 253:    */     throws IOException
/* 254:    */   {
/* 255:221 */     super(0, 0);
/* 256:    */     try
/* 257:    */     {
/* 258:223 */       MatrixInfo info = r.readMatrixInfo();
/* 259:224 */       if (info.isComplex()) {
/* 260:225 */         throw new IllegalArgumentException("complex matrices not supported");
/* 261:    */       }
/* 262:227 */       if (!info.isCoordinate()) {
/* 263:228 */         throw new IllegalArgumentException("only coordinate matrices supported");
/* 264:    */       }
/* 265:230 */       MatrixSize size = r.readMatrixSize(info);
/* 266:231 */       this.numRows = size.numRows();
/* 267:232 */       this.numColumns = size.numColumns();
/* 268:233 */       this.links = new Linked();
/* 269:    */       
/* 270:235 */       int nz = size.numEntries();
/* 271:236 */       int[] row = new int[nz];
/* 272:237 */       int[] column = new int[nz];
/* 273:238 */       double[] entry = new double[nz];
/* 274:239 */       r.readCoordinate(row, column, entry);
/* 275:240 */       r.add(-1, row);
/* 276:241 */       r.add(-1, column);
/* 277:242 */       for (int i = 0; i < nz; i++) {
/* 278:243 */         set(row[i], column[i], entry[i]);
/* 279:    */       }
/* 280:    */     }
/* 281:    */     finally
/* 282:    */     {
/* 283:245 */       r.close();
/* 284:    */     }
/* 285:    */   }
/* 286:    */   
/* 287:    */   public Matrix zero()
/* 288:    */   {
/* 289:251 */     this.links = new Linked();
/* 290:252 */     return this;
/* 291:    */   }
/* 292:    */   
/* 293:    */   public double get(int row, int column)
/* 294:    */   {
/* 295:257 */     return this.links.get(row, column);
/* 296:    */   }
/* 297:    */   
/* 298:    */   public void set(int row, int column, double value)
/* 299:    */   {
/* 300:262 */     check(row, column);
/* 301:263 */     this.links.set(row, column, value);
/* 302:    */   }
/* 303:    */   
/* 304:    */   static class ReusableMatrixEntry
/* 305:    */     implements MatrixEntry
/* 306:    */   {
/* 307:    */     int row;
/* 308:    */     int col;
/* 309:    */     double val;
/* 310:    */     
/* 311:    */     public int column()
/* 312:    */     {
/* 313:274 */       return this.col;
/* 314:    */     }
/* 315:    */     
/* 316:    */     public int row()
/* 317:    */     {
/* 318:279 */       return this.row;
/* 319:    */     }
/* 320:    */     
/* 321:    */     public double get()
/* 322:    */     {
/* 323:284 */       return this.val;
/* 324:    */     }
/* 325:    */     
/* 326:    */     public void set(double value)
/* 327:    */     {
/* 328:289 */       throw new UnsupportedOperationException();
/* 329:    */     }
/* 330:    */     
/* 331:    */     public String toString()
/* 332:    */     {
/* 333:294 */       return this.row + "," + this.col + "=" + this.val;
/* 334:    */     }
/* 335:    */   }
/* 336:    */   
/* 337:    */   public Iterator<MatrixEntry> iterator()
/* 338:    */   {
/* 339:301 */     new Iterator()
/* 340:    */     {
/* 341:302 */       LinkedSparseMatrix.Node cur = LinkedSparseMatrix.this.links.head;
/* 342:303 */       LinkedSparseMatrix.ReusableMatrixEntry entry = new LinkedSparseMatrix.ReusableMatrixEntry();
/* 343:    */       
/* 344:    */       public boolean hasNext()
/* 345:    */       {
/* 346:307 */         return this.cur != null;
/* 347:    */       }
/* 348:    */       
/* 349:    */       public MatrixEntry next()
/* 350:    */       {
/* 351:312 */         this.entry.row = this.cur.row;
/* 352:313 */         this.entry.col = this.cur.col;
/* 353:314 */         this.entry.val = this.cur.val;
/* 354:315 */         this.cur = this.cur.rowTail;
/* 355:316 */         return this.entry;
/* 356:    */       }
/* 357:    */       
/* 358:    */       public void remove()
/* 359:    */       {
/* 360:321 */         throw new UnsupportedOperationException("TODO");
/* 361:    */       }
/* 362:    */     };
/* 363:    */   }
/* 364:    */   
/* 365:    */   public Matrix scale(double alpha)
/* 366:    */   {
/* 367:328 */     if (alpha == 0.0D) {
/* 368:329 */       zero();
/* 369:330 */     } else if (alpha != 1.0D) {
/* 370:331 */       for (MatrixEntry e : this) {
/* 371:332 */         set(e.row(), e.column(), e.get() * alpha);
/* 372:    */       }
/* 373:    */     }
/* 374:333 */     return this;
/* 375:    */   }
/* 376:    */   
/* 377:    */   public Matrix copy()
/* 378:    */   {
/* 379:338 */     return new LinkedSparseMatrix(this);
/* 380:    */   }
/* 381:    */   
/* 382:    */   public Matrix transpose()
/* 383:    */   {
/* 384:343 */     Linked old = this.links;
/* 385:344 */     this.numRows = this.numColumns;
/* 386:345 */     this.numColumns = old.rows.length;
/* 387:346 */     this.links = new Linked();
/* 388:347 */     Node node = old.head;
/* 389:348 */     while (node != null)
/* 390:    */     {
/* 391:349 */       set(node.col, node.row, node.val);
/* 392:350 */       node = node.rowTail;
/* 393:    */     }
/* 394:352 */     return this;
/* 395:    */   }
/* 396:    */   
/* 397:    */   public Vector multAdd(double alpha, Vector x, Vector y)
/* 398:    */   {
/* 399:357 */     checkMultAdd(x, y);
/* 400:358 */     if (alpha == 0.0D) {
/* 401:359 */       return y;
/* 402:    */     }
/* 403:360 */     Node node = this.links.head;
/* 404:361 */     while (node != null)
/* 405:    */     {
/* 406:362 */       y.add(node.row, alpha * node.val * x.get(node.col));
/* 407:363 */       node = node.rowTail;
/* 408:    */     }
/* 409:365 */     return y;
/* 410:    */   }
/* 411:    */   
/* 412:    */   public Vector transMultAdd(double alpha, Vector x, Vector y)
/* 413:    */   {
/* 414:370 */     checkTransMultAdd(x, y);
/* 415:371 */     if (alpha == 0.0D) {
/* 416:372 */       return y;
/* 417:    */     }
/* 418:373 */     Node node = this.links.head;
/* 419:374 */     while (node != null)
/* 420:    */     {
/* 421:375 */       y.add(node.col, alpha * node.val * x.get(node.row));
/* 422:376 */       node = node.colTail;
/* 423:    */     }
/* 424:378 */     return y;
/* 425:    */   }
/* 426:    */   
/* 427:    */   public Matrix multAdd(double alpha, Matrix B, Matrix C)
/* 428:    */   {
/* 429:385 */     checkMultAdd(B, C);
/* 430:386 */     if (alpha == 0.0D) {
/* 431:387 */       return C;
/* 432:    */     }
/* 433:388 */     for (int i = 0; i < this.numRows; i++)
/* 434:    */     {
/* 435:389 */       Node row = this.links.startOfRow(i);
/* 436:390 */       if (row != null) {
/* 437:391 */         for (int j = 0; j < B.numColumns(); j++)
/* 438:    */         {
/* 439:392 */           Node node = row;
/* 440:393 */           double v = 0.0D;
/* 441:394 */           while ((node != null) && (node.row == i))
/* 442:    */           {
/* 443:395 */             v += B.get(node.col, j) * node.val;
/* 444:396 */             node = node.rowTail;
/* 445:    */           }
/* 446:398 */           if (v != 0.0D) {
/* 447:399 */             C.add(i, j, alpha * v);
/* 448:    */           }
/* 449:    */         }
/* 450:    */       }
/* 451:    */     }
/* 452:402 */     return C;
/* 453:    */   }
/* 454:    */   
/* 455:    */   public Matrix transBmultAdd(double alpha, Matrix B, Matrix C)
/* 456:    */   {
/* 457:407 */     checkTransBmultAdd(B, C);
/* 458:408 */     if (alpha == 0.0D) {
/* 459:409 */       return C;
/* 460:    */     }
/* 461:410 */     for (int i = 0; i < this.numRows; i++)
/* 462:    */     {
/* 463:411 */       Node row = this.links.startOfRow(i);
/* 464:412 */       if (row != null) {
/* 465:413 */         for (int j = 0; j < B.numRows(); j++)
/* 466:    */         {
/* 467:414 */           Node node = row;
/* 468:415 */           double v = 0.0D;
/* 469:416 */           while ((node != null) && (node.row == i))
/* 470:    */           {
/* 471:417 */             v += B.get(j, node.col) * node.val;
/* 472:418 */             node = node.rowTail;
/* 473:    */           }
/* 474:420 */           if (v != 0.0D) {
/* 475:421 */             C.add(i, j, alpha * v);
/* 476:    */           }
/* 477:    */         }
/* 478:    */       }
/* 479:    */     }
/* 480:424 */     return C;
/* 481:    */   }
/* 482:    */   
/* 483:    */   public Matrix transAmultAdd(double alpha, Matrix B, Matrix C)
/* 484:    */   {
/* 485:429 */     checkTransAmultAdd(B, C);
/* 486:430 */     if (alpha == 0.0D) {
/* 487:431 */       return C;
/* 488:    */     }
/* 489:432 */     for (int i = 0; i < this.numColumns; i++)
/* 490:    */     {
/* 491:433 */       Node row = this.links.startOfCol(i);
/* 492:434 */       if (row != null) {
/* 493:435 */         for (int j = 0; j < B.numColumns(); j++)
/* 494:    */         {
/* 495:436 */           Node node = row;
/* 496:437 */           double v = 0.0D;
/* 497:438 */           while ((node != null) && (node.col == i))
/* 498:    */           {
/* 499:439 */             v += B.get(node.row, j) * node.val;
/* 500:440 */             node = node.colTail;
/* 501:    */           }
/* 502:442 */           if (v != 0.0D) {
/* 503:443 */             C.add(i, j, alpha * v);
/* 504:    */           }
/* 505:    */         }
/* 506:    */       }
/* 507:    */     }
/* 508:446 */     return C;
/* 509:    */   }
/* 510:    */   
/* 511:    */   public Matrix transABmultAdd(double alpha, Matrix B, Matrix C)
/* 512:    */   {
/* 513:451 */     checkTransABmultAdd(B, C);
/* 514:452 */     if (alpha == 0.0D) {
/* 515:453 */       return C;
/* 516:    */     }
/* 517:454 */     for (int i = 0; i < this.numColumns; i++)
/* 518:    */     {
/* 519:455 */       Node row = this.links.startOfCol(i);
/* 520:456 */       if (row != null) {
/* 521:457 */         for (int j = 0; j < B.numRows(); j++)
/* 522:    */         {
/* 523:458 */           Node node = row;
/* 524:459 */           double v = 0.0D;
/* 525:460 */           while ((node != null) && (node.col == i))
/* 526:    */           {
/* 527:461 */             v += B.get(j, node.row) * node.val;
/* 528:462 */             node = node.colTail;
/* 529:    */           }
/* 530:464 */           if (v != 0.0D) {
/* 531:465 */             C.add(i, j, alpha * v);
/* 532:    */           }
/* 533:    */         }
/* 534:    */       }
/* 535:    */     }
/* 536:468 */     return C;
/* 537:    */   }
/* 538:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.LinkedSparseMatrix
 * JD-Core Version:    0.7.0.1
 */