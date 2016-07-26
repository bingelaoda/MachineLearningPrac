/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import java.util.Formatter;
/*   4:    */ import java.util.Iterator;
/*   5:    */ 
/*   6:    */ public abstract class AbstractMatrix
/*   7:    */   implements Matrix
/*   8:    */ {
/*   9:    */   protected int numRows;
/*  10:    */   protected int numColumns;
/*  11:    */   
/*  12:    */   protected AbstractMatrix(int numRows, int numColumns)
/*  13:    */   {
/*  14: 77 */     if ((numRows < 0) || (numColumns < 0)) {
/*  15: 78 */       throw new IndexOutOfBoundsException("Matrix size cannot be negative");
/*  16:    */     }
/*  17: 80 */     this.numRows = numRows;
/*  18: 81 */     this.numColumns = numColumns;
/*  19:    */   }
/*  20:    */   
/*  21:    */   protected AbstractMatrix(Matrix A)
/*  22:    */   {
/*  23: 89 */     this(A.numRows(), A.numColumns());
/*  24:    */   }
/*  25:    */   
/*  26:    */   public int numRows()
/*  27:    */   {
/*  28: 93 */     return this.numRows;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public int numColumns()
/*  32:    */   {
/*  33: 97 */     return this.numColumns;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public boolean isSquare()
/*  37:    */   {
/*  38:101 */     return this.numRows == this.numColumns;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void set(int row, int column, double value)
/*  42:    */   {
/*  43:105 */     throw new UnsupportedOperationException();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void add(int row, int column, double value)
/*  47:    */   {
/*  48:109 */     set(row, column, value + get(row, column));
/*  49:    */   }
/*  50:    */   
/*  51:    */   public double get(int row, int column)
/*  52:    */   {
/*  53:113 */     throw new UnsupportedOperationException();
/*  54:    */   }
/*  55:    */   
/*  56:    */   protected void check(int row, int column)
/*  57:    */   {
/*  58:120 */     if (row < 0) {
/*  59:121 */       throw new IndexOutOfBoundsException("row index is negative (" + row + ")");
/*  60:    */     }
/*  61:123 */     if (column < 0) {
/*  62:124 */       throw new IndexOutOfBoundsException("column index is negative (" + column + ")");
/*  63:    */     }
/*  64:126 */     if (row >= this.numRows) {
/*  65:127 */       throw new IndexOutOfBoundsException("row index >= numRows (" + row + " >= " + this.numRows + ")");
/*  66:    */     }
/*  67:129 */     if (column >= this.numColumns) {
/*  68:130 */       throw new IndexOutOfBoundsException("column index >= numColumns (" + column + " >= " + this.numColumns + ")");
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:    */   public Matrix copy()
/*  73:    */   {
/*  74:135 */     throw new UnsupportedOperationException();
/*  75:    */   }
/*  76:    */   
/*  77:    */   public Matrix zero()
/*  78:    */   {
/*  79:139 */     for (MatrixEntry e : this) {
/*  80:140 */       e.set(0.0D);
/*  81:    */     }
/*  82:141 */     return this;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Vector mult(Vector x, Vector y)
/*  86:    */   {
/*  87:145 */     return mult(1.0D, x, y);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public Vector mult(double alpha, Vector x, Vector y)
/*  91:    */   {
/*  92:149 */     return multAdd(alpha, x, y.zero());
/*  93:    */   }
/*  94:    */   
/*  95:    */   public Vector multAdd(Vector x, Vector y)
/*  96:    */   {
/*  97:153 */     return multAdd(1.0D, x, y);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public Vector multAdd(double alpha, Vector x, Vector y)
/* 101:    */   {
/* 102:157 */     checkMultAdd(x, y);
/* 103:159 */     if (alpha != 0.0D) {
/* 104:160 */       for (MatrixEntry e : this) {
/* 105:161 */         y.add(e.row(), alpha * e.get() * x.get(e.column()));
/* 106:    */       }
/* 107:    */     }
/* 108:163 */     return y;
/* 109:    */   }
/* 110:    */   
/* 111:    */   protected void checkMultAdd(Vector x, Vector y)
/* 112:    */   {
/* 113:170 */     if (this.numColumns != x.size()) {
/* 114:172 */       throw new IndexOutOfBoundsException("A.numColumns != x.size (" + this.numColumns + " != " + x.size() + ")");
/* 115:    */     }
/* 116:173 */     if (this.numRows != y.size()) {
/* 117:175 */       throw new IndexOutOfBoundsException("A.numRows != y.size (" + this.numRows + " != " + y.size() + ")");
/* 118:    */     }
/* 119:    */   }
/* 120:    */   
/* 121:    */   public Vector transMult(Vector x, Vector y)
/* 122:    */   {
/* 123:179 */     return transMult(1.0D, x, y);
/* 124:    */   }
/* 125:    */   
/* 126:    */   public Vector transMult(double alpha, Vector x, Vector y)
/* 127:    */   {
/* 128:183 */     return transMultAdd(alpha, x, y.zero());
/* 129:    */   }
/* 130:    */   
/* 131:    */   public Vector transMultAdd(Vector x, Vector y)
/* 132:    */   {
/* 133:187 */     return transMultAdd(1.0D, x, y);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public Vector transMultAdd(double alpha, Vector x, Vector y)
/* 137:    */   {
/* 138:191 */     checkTransMultAdd(x, y);
/* 139:193 */     if (alpha != 0.0D) {
/* 140:194 */       for (MatrixEntry e : this) {
/* 141:195 */         y.add(e.column(), alpha * e.get() * x.get(e.row()));
/* 142:    */       }
/* 143:    */     }
/* 144:197 */     return y;
/* 145:    */   }
/* 146:    */   
/* 147:    */   protected void checkTransMultAdd(Vector x, Vector y)
/* 148:    */   {
/* 149:205 */     if (this.numRows != x.size()) {
/* 150:207 */       throw new IndexOutOfBoundsException("A.numRows != x.size (" + this.numRows + " != " + x.size() + ")");
/* 151:    */     }
/* 152:208 */     if (this.numColumns != y.size()) {
/* 153:210 */       throw new IndexOutOfBoundsException("A.numColumns != y.size (" + this.numColumns + " != " + y.size() + ")");
/* 154:    */     }
/* 155:    */   }
/* 156:    */   
/* 157:    */   public Vector solve(Vector b, Vector x)
/* 158:    */   {
/* 159:214 */     throw new UnsupportedOperationException();
/* 160:    */   }
/* 161:    */   
/* 162:    */   public Vector transSolve(Vector b, Vector x)
/* 163:    */   {
/* 164:218 */     throw new UnsupportedOperationException();
/* 165:    */   }
/* 166:    */   
/* 167:    */   protected void checkSolve(Vector b, Vector x)
/* 168:    */   {
/* 169:226 */     if (!isSquare()) {
/* 170:227 */       throw new IndexOutOfBoundsException("!A.isSquare");
/* 171:    */     }
/* 172:228 */     if (this.numRows != b.size()) {
/* 173:230 */       throw new IndexOutOfBoundsException("numRows != b.size (" + this.numRows + " != " + b.size() + ")");
/* 174:    */     }
/* 175:231 */     if (this.numColumns != x.size()) {
/* 176:233 */       throw new IndexOutOfBoundsException("numColumns != x.size (" + this.numColumns + " != " + x.size() + ")");
/* 177:    */     }
/* 178:    */   }
/* 179:    */   
/* 180:    */   public Matrix rank1(Vector x)
/* 181:    */   {
/* 182:237 */     return rank1(1.0D, x);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public Matrix rank1(double alpha, Vector x)
/* 186:    */   {
/* 187:241 */     return rank1(alpha, x, x);
/* 188:    */   }
/* 189:    */   
/* 190:    */   public Matrix rank1(Vector x, Vector y)
/* 191:    */   {
/* 192:245 */     return rank1(1.0D, x, y);
/* 193:    */   }
/* 194:    */   
/* 195:    */   public Matrix rank1(double alpha, Vector x, Vector y)
/* 196:    */   {
/* 197:249 */     checkRank1(x, y);
/* 198:251 */     if (alpha == 0.0D) {
/* 199:252 */       return this;
/* 200:    */     }
/* 201:254 */     for (Iterator localIterator1 = x.iterator(); localIterator1.hasNext();)
/* 202:    */     {
/* 203:254 */       ei = (VectorEntry)localIterator1.next();
/* 204:255 */       if (ei.get() != 0.0D) {
/* 205:256 */         for (VectorEntry ej : y) {
/* 206:257 */           if (ej.get() != 0.0D) {
/* 207:258 */             add(ei.index(), ej.index(), alpha * ei.get() * ej.get());
/* 208:    */           }
/* 209:    */         }
/* 210:    */       }
/* 211:    */     }
/* 212:    */     VectorEntry ei;
/* 213:260 */     return this;
/* 214:    */   }
/* 215:    */   
/* 216:    */   protected void checkRank1(Vector x, Vector y)
/* 217:    */   {
/* 218:267 */     if (!isSquare()) {
/* 219:268 */       throw new IndexOutOfBoundsException("!A.isSquare");
/* 220:    */     }
/* 221:269 */     if (x.size() != this.numRows) {
/* 222:271 */       throw new IndexOutOfBoundsException("x.size != A.numRows (" + x.size() + " != " + this.numRows + ")");
/* 223:    */     }
/* 224:272 */     if (y.size() != this.numColumns) {
/* 225:274 */       throw new IndexOutOfBoundsException("y.size != A.numColumns (" + y.size() + " != " + this.numColumns + ")");
/* 226:    */     }
/* 227:    */   }
/* 228:    */   
/* 229:    */   public Matrix rank2(Vector x, Vector y)
/* 230:    */   {
/* 231:278 */     return rank2(1.0D, x, y);
/* 232:    */   }
/* 233:    */   
/* 234:    */   public Matrix rank2(double alpha, Vector x, Vector y)
/* 235:    */   {
/* 236:282 */     checkRank2(x, y);
/* 237:284 */     if (alpha == 0.0D) {
/* 238:285 */       return this;
/* 239:    */     }
/* 240:287 */     for (Iterator localIterator1 = x.iterator(); localIterator1.hasNext();)
/* 241:    */     {
/* 242:287 */       ei = (VectorEntry)localIterator1.next();
/* 243:288 */       for (VectorEntry ej : y)
/* 244:    */       {
/* 245:289 */         add(ei.index(), ej.index(), alpha * ei.get() * ej.get());
/* 246:290 */         add(ej.index(), ei.index(), alpha * ei.get() * ej.get());
/* 247:    */       }
/* 248:    */     }
/* 249:    */     VectorEntry ei;
/* 250:293 */     return this;
/* 251:    */   }
/* 252:    */   
/* 253:    */   protected void checkRank2(Vector x, Vector y)
/* 254:    */   {
/* 255:300 */     if (!isSquare()) {
/* 256:301 */       throw new IndexOutOfBoundsException("!A.isSquare");
/* 257:    */     }
/* 258:302 */     if (x.size() != this.numRows) {
/* 259:304 */       throw new IndexOutOfBoundsException("x.size != A.numRows (" + x.size() + " != " + this.numRows + ")");
/* 260:    */     }
/* 261:305 */     if (y.size() != this.numRows) {
/* 262:307 */       throw new IndexOutOfBoundsException("y.size != A.numRows (" + y.size() + " != " + this.numRows + ")");
/* 263:    */     }
/* 264:    */   }
/* 265:    */   
/* 266:    */   public Matrix mult(Matrix B, Matrix C)
/* 267:    */   {
/* 268:311 */     return mult(1.0D, B, C);
/* 269:    */   }
/* 270:    */   
/* 271:    */   public Matrix mult(double alpha, Matrix B, Matrix C)
/* 272:    */   {
/* 273:315 */     return multAdd(alpha, B, C.zero());
/* 274:    */   }
/* 275:    */   
/* 276:    */   public Matrix multAdd(Matrix B, Matrix C)
/* 277:    */   {
/* 278:319 */     return multAdd(1.0D, B, C);
/* 279:    */   }
/* 280:    */   
/* 281:    */   public Matrix multAdd(double alpha, Matrix B, Matrix C)
/* 282:    */   {
/* 283:323 */     checkMultAdd(B, C);
/* 284:325 */     if (alpha != 0.0D) {
/* 285:326 */       for (int i = 0; i < this.numRows; i++) {
/* 286:327 */         for (int j = 0; j < C.numColumns(); j++)
/* 287:    */         {
/* 288:328 */           double dot = 0.0D;
/* 289:329 */           for (int k = 0; k < this.numColumns; k++) {
/* 290:330 */             dot += get(i, k) * B.get(k, j);
/* 291:    */           }
/* 292:331 */           C.add(i, j, alpha * dot);
/* 293:    */         }
/* 294:    */       }
/* 295:    */     }
/* 296:334 */     return C;
/* 297:    */   }
/* 298:    */   
/* 299:    */   protected void checkMultAdd(Matrix B, Matrix C)
/* 300:    */   {
/* 301:341 */     if (this.numRows != C.numRows()) {
/* 302:343 */       throw new IndexOutOfBoundsException("A.numRows != C.numRows (" + this.numRows + " != " + C.numRows() + ")");
/* 303:    */     }
/* 304:344 */     if (this.numColumns != B.numRows()) {
/* 305:346 */       throw new IndexOutOfBoundsException("A.numColumns != B.numRows (" + this.numColumns + " != " + B.numRows() + ")");
/* 306:    */     }
/* 307:347 */     if (B.numColumns() != C.numColumns()) {
/* 308:350 */       throw new IndexOutOfBoundsException("B.numColumns != C.numColumns (" + B.numRows() + " != " + C.numColumns() + ")");
/* 309:    */     }
/* 310:    */   }
/* 311:    */   
/* 312:    */   public Matrix transAmult(Matrix B, Matrix C)
/* 313:    */   {
/* 314:354 */     return transAmult(1.0D, B, C);
/* 315:    */   }
/* 316:    */   
/* 317:    */   public Matrix transAmult(double alpha, Matrix B, Matrix C)
/* 318:    */   {
/* 319:358 */     return transAmultAdd(alpha, B, C.zero());
/* 320:    */   }
/* 321:    */   
/* 322:    */   public Matrix transAmultAdd(Matrix B, Matrix C)
/* 323:    */   {
/* 324:362 */     return transAmultAdd(1.0D, B, C);
/* 325:    */   }
/* 326:    */   
/* 327:    */   public Matrix transAmultAdd(double alpha, Matrix B, Matrix C)
/* 328:    */   {
/* 329:366 */     checkTransAmultAdd(B, C);
/* 330:368 */     if (alpha != 0.0D) {
/* 331:369 */       for (int i = 0; i < this.numColumns; i++) {
/* 332:370 */         for (int j = 0; j < C.numColumns(); j++)
/* 333:    */         {
/* 334:371 */           double dot = 0.0D;
/* 335:372 */           for (int k = 0; k < this.numRows; k++) {
/* 336:373 */             dot += get(k, i) * B.get(k, j);
/* 337:    */           }
/* 338:374 */           C.add(i, j, alpha * dot);
/* 339:    */         }
/* 340:    */       }
/* 341:    */     }
/* 342:377 */     return C;
/* 343:    */   }
/* 344:    */   
/* 345:    */   protected void checkTransAmultAdd(Matrix B, Matrix C)
/* 346:    */   {
/* 347:385 */     if (this.numRows != B.numRows()) {
/* 348:387 */       throw new IndexOutOfBoundsException("A.numRows != B.numRows (" + this.numRows + " != " + B.numRows() + ")");
/* 349:    */     }
/* 350:388 */     if (this.numColumns != C.numRows()) {
/* 351:390 */       throw new IndexOutOfBoundsException("A.numColumns != C.numRows (" + this.numColumns + " != " + C.numRows() + ")");
/* 352:    */     }
/* 353:391 */     if (B.numColumns() != C.numColumns()) {
/* 354:394 */       throw new IndexOutOfBoundsException("B.numColumns != C.numColumns (" + B.numColumns() + " != " + C.numColumns() + ")");
/* 355:    */     }
/* 356:    */   }
/* 357:    */   
/* 358:    */   public Matrix transBmult(Matrix B, Matrix C)
/* 359:    */   {
/* 360:398 */     return transBmult(1.0D, B, C);
/* 361:    */   }
/* 362:    */   
/* 363:    */   public Matrix transBmult(double alpha, Matrix B, Matrix C)
/* 364:    */   {
/* 365:402 */     return transBmultAdd(alpha, B, C.zero());
/* 366:    */   }
/* 367:    */   
/* 368:    */   public Matrix transBmultAdd(Matrix B, Matrix C)
/* 369:    */   {
/* 370:406 */     return transBmultAdd(1.0D, B, C);
/* 371:    */   }
/* 372:    */   
/* 373:    */   public Matrix transBmultAdd(double alpha, Matrix B, Matrix C)
/* 374:    */   {
/* 375:410 */     checkTransBmultAdd(B, C);
/* 376:412 */     if (alpha != 0.0D) {
/* 377:413 */       for (int i = 0; i < this.numRows; i++) {
/* 378:414 */         for (int j = 0; j < C.numColumns(); j++)
/* 379:    */         {
/* 380:415 */           double dot = 0.0D;
/* 381:416 */           for (int k = 0; k < this.numColumns; k++) {
/* 382:417 */             dot += get(i, k) * B.get(j, k);
/* 383:    */           }
/* 384:418 */           C.add(i, j, alpha * dot);
/* 385:    */         }
/* 386:    */       }
/* 387:    */     }
/* 388:421 */     return C;
/* 389:    */   }
/* 390:    */   
/* 391:    */   protected void checkTransBmultAdd(Matrix B, Matrix C)
/* 392:    */   {
/* 393:429 */     if (this.numColumns != B.numColumns()) {
/* 394:432 */       throw new IndexOutOfBoundsException("A.numColumns != B.numColumns (" + this.numColumns + " != " + B.numColumns() + ")");
/* 395:    */     }
/* 396:433 */     if (this.numRows != C.numRows()) {
/* 397:435 */       throw new IndexOutOfBoundsException("A.numRows != C.numRows (" + this.numRows + " != " + C.numRows() + ")");
/* 398:    */     }
/* 399:436 */     if (B.numRows() != C.numColumns()) {
/* 400:438 */       throw new IndexOutOfBoundsException("B.numRows != C.numColumns (" + B.numRows() + " != " + C.numColumns() + ")");
/* 401:    */     }
/* 402:    */   }
/* 403:    */   
/* 404:    */   public Matrix transABmult(Matrix B, Matrix C)
/* 405:    */   {
/* 406:442 */     return transABmult(1.0D, B, C);
/* 407:    */   }
/* 408:    */   
/* 409:    */   public Matrix transABmult(double alpha, Matrix B, Matrix C)
/* 410:    */   {
/* 411:446 */     return transABmultAdd(alpha, B, C.zero());
/* 412:    */   }
/* 413:    */   
/* 414:    */   public Matrix transABmultAdd(Matrix B, Matrix C)
/* 415:    */   {
/* 416:450 */     return transABmultAdd(1.0D, B, C);
/* 417:    */   }
/* 418:    */   
/* 419:    */   public Matrix transABmultAdd(double alpha, Matrix B, Matrix C)
/* 420:    */   {
/* 421:454 */     checkTransABmultAdd(B, C);
/* 422:456 */     if (alpha != 0.0D) {
/* 423:457 */       for (int i = 0; i < this.numColumns; i++) {
/* 424:458 */         for (int j = 0; j < C.numColumns(); j++)
/* 425:    */         {
/* 426:459 */           double dot = 0.0D;
/* 427:460 */           for (int k = 0; k < this.numRows; k++) {
/* 428:461 */             dot += get(k, i) * B.get(j, k);
/* 429:    */           }
/* 430:462 */           C.add(i, j, alpha * dot);
/* 431:    */         }
/* 432:    */       }
/* 433:    */     }
/* 434:465 */     return C;
/* 435:    */   }
/* 436:    */   
/* 437:    */   protected void checkTransABmultAdd(Matrix B, Matrix C)
/* 438:    */   {
/* 439:473 */     if (this.numRows != B.numColumns()) {
/* 440:475 */       throw new IndexOutOfBoundsException("A.numRows != B.numColumns (" + this.numRows + " != " + B.numColumns() + ")");
/* 441:    */     }
/* 442:476 */     if (this.numColumns != C.numRows()) {
/* 443:478 */       throw new IndexOutOfBoundsException("A.numColumns != C.numRows (" + this.numColumns + " != " + C.numRows() + ")");
/* 444:    */     }
/* 445:479 */     if (B.numRows() != C.numColumns()) {
/* 446:481 */       throw new IndexOutOfBoundsException("B.numRows != C.numColumns (" + B.numRows() + " != " + C.numColumns() + ")");
/* 447:    */     }
/* 448:    */   }
/* 449:    */   
/* 450:    */   public Matrix solve(Matrix B, Matrix X)
/* 451:    */   {
/* 452:485 */     throw new UnsupportedOperationException();
/* 453:    */   }
/* 454:    */   
/* 455:    */   public Matrix transSolve(Matrix B, Matrix X)
/* 456:    */   {
/* 457:489 */     throw new UnsupportedOperationException();
/* 458:    */   }
/* 459:    */   
/* 460:    */   protected void checkSolve(Matrix B, Matrix X)
/* 461:    */   {
/* 462:497 */     if (!isSquare()) {
/* 463:498 */       throw new IndexOutOfBoundsException("!A.isSquare");
/* 464:    */     }
/* 465:499 */     if (B.numRows() != this.numRows) {
/* 466:501 */       throw new IndexOutOfBoundsException("B.numRows != A.numRows (" + B.numRows() + " != " + this.numRows + ")");
/* 467:    */     }
/* 468:502 */     if (B.numColumns() != X.numColumns()) {
/* 469:505 */       throw new IndexOutOfBoundsException("B.numColumns != X.numColumns (" + B.numColumns() + " != " + X.numColumns() + ")");
/* 470:    */     }
/* 471:506 */     if (X.numRows() != this.numColumns) {
/* 472:508 */       throw new IndexOutOfBoundsException("X.numRows != A.numColumns (" + X.numRows() + " != " + this.numColumns + ")");
/* 473:    */     }
/* 474:    */   }
/* 475:    */   
/* 476:    */   public Matrix rank1(Matrix C)
/* 477:    */   {
/* 478:512 */     return rank1(1.0D, C);
/* 479:    */   }
/* 480:    */   
/* 481:    */   public Matrix rank1(double alpha, Matrix C)
/* 482:    */   {
/* 483:516 */     checkRank1(C);
/* 484:518 */     if (alpha == 0.0D) {
/* 485:519 */       return this;
/* 486:    */     }
/* 487:521 */     return C.transBmultAdd(alpha, C, this);
/* 488:    */   }
/* 489:    */   
/* 490:    */   protected void checkRank1(Matrix C)
/* 491:    */   {
/* 492:528 */     if (!isSquare()) {
/* 493:529 */       throw new IndexOutOfBoundsException("!A.isSquare");
/* 494:    */     }
/* 495:530 */     if (this.numRows != C.numRows()) {
/* 496:532 */       throw new IndexOutOfBoundsException("A.numRows != C.numRows (" + this.numRows + " != " + C.numRows() + ")");
/* 497:    */     }
/* 498:    */   }
/* 499:    */   
/* 500:    */   public Matrix transRank1(Matrix C)
/* 501:    */   {
/* 502:536 */     return transRank1(1.0D, C);
/* 503:    */   }
/* 504:    */   
/* 505:    */   public Matrix transRank1(double alpha, Matrix C)
/* 506:    */   {
/* 507:540 */     checkTransRank1(C);
/* 508:542 */     if (alpha == 0.0D) {
/* 509:543 */       return this;
/* 510:    */     }
/* 511:545 */     return C.transAmultAdd(alpha, C, this);
/* 512:    */   }
/* 513:    */   
/* 514:    */   protected void checkTransRank1(Matrix C)
/* 515:    */   {
/* 516:552 */     if (!isSquare()) {
/* 517:553 */       throw new IndexOutOfBoundsException("!A.isSquare");
/* 518:    */     }
/* 519:554 */     if (this.numRows != C.numColumns()) {
/* 520:556 */       throw new IndexOutOfBoundsException("A.numRows != C.numColumns (" + this.numRows + " != " + C.numColumns() + ")");
/* 521:    */     }
/* 522:    */   }
/* 523:    */   
/* 524:    */   public Matrix rank2(Matrix B, Matrix C)
/* 525:    */   {
/* 526:560 */     return rank2(1.0D, B, C);
/* 527:    */   }
/* 528:    */   
/* 529:    */   public Matrix rank2(double alpha, Matrix B, Matrix C)
/* 530:    */   {
/* 531:564 */     checkRank2(B, C);
/* 532:566 */     if (alpha == 0.0D) {
/* 533:567 */       return this;
/* 534:    */     }
/* 535:569 */     return B.transBmultAdd(alpha, C, C.transBmultAdd(alpha, B, this));
/* 536:    */   }
/* 537:    */   
/* 538:    */   protected void checkRank2(Matrix B, Matrix C)
/* 539:    */   {
/* 540:576 */     if (!isSquare()) {
/* 541:577 */       throw new IndexOutOfBoundsException("!A.isSquare");
/* 542:    */     }
/* 543:578 */     if (B.numRows() != C.numRows()) {
/* 544:580 */       throw new IndexOutOfBoundsException("B.numRows != C.numRows (" + B.numRows() + " != " + C.numRows() + ")");
/* 545:    */     }
/* 546:581 */     if (B.numColumns() != C.numColumns()) {
/* 547:584 */       throw new IndexOutOfBoundsException("B.numColumns != C.numColumns (" + B.numColumns() + " != " + C.numColumns() + ")");
/* 548:    */     }
/* 549:    */   }
/* 550:    */   
/* 551:    */   public Matrix transRank2(Matrix B, Matrix C)
/* 552:    */   {
/* 553:588 */     return transRank2(1.0D, B, C);
/* 554:    */   }
/* 555:    */   
/* 556:    */   public Matrix transRank2(double alpha, Matrix B, Matrix C)
/* 557:    */   {
/* 558:592 */     checkTransRank2(B, C);
/* 559:594 */     if (alpha == 0.0D) {
/* 560:595 */       return this;
/* 561:    */     }
/* 562:597 */     return B.transAmultAdd(alpha, C, C.transAmultAdd(alpha, B, this));
/* 563:    */   }
/* 564:    */   
/* 565:    */   protected void checkTransRank2(Matrix B, Matrix C)
/* 566:    */   {
/* 567:604 */     if (!isSquare()) {
/* 568:605 */       throw new IndexOutOfBoundsException("!A.isSquare");
/* 569:    */     }
/* 570:606 */     if (this.numRows != B.numColumns()) {
/* 571:608 */       throw new IndexOutOfBoundsException("A.numRows != B.numColumns (" + this.numRows + " != " + B.numColumns() + ")");
/* 572:    */     }
/* 573:609 */     if (B.numRows() != C.numRows()) {
/* 574:611 */       throw new IndexOutOfBoundsException("B.numRows != C.numRows (" + B.numRows() + " != " + C.numRows() + ")");
/* 575:    */     }
/* 576:612 */     if (B.numColumns() != C.numColumns()) {
/* 577:615 */       throw new IndexOutOfBoundsException("B.numColumns != C.numColumns (" + B.numColumns() + " != " + C.numColumns() + ")");
/* 578:    */     }
/* 579:    */   }
/* 580:    */   
/* 581:    */   public Matrix scale(double alpha)
/* 582:    */   {
/* 583:619 */     if (alpha == 1.0D) {
/* 584:620 */       return this;
/* 585:    */     }
/* 586:621 */     if (alpha == 0.0D) {
/* 587:622 */       return zero();
/* 588:    */     }
/* 589:624 */     for (MatrixEntry e : this) {
/* 590:625 */       e.set(alpha * e.get());
/* 591:    */     }
/* 592:627 */     return this;
/* 593:    */   }
/* 594:    */   
/* 595:    */   public Matrix set(Matrix B)
/* 596:    */   {
/* 597:631 */     return set(1.0D, B);
/* 598:    */   }
/* 599:    */   
/* 600:    */   public Matrix set(double alpha, Matrix B)
/* 601:    */   {
/* 602:635 */     checkSize(B);
/* 603:637 */     if (alpha == 0.0D) {
/* 604:638 */       return zero();
/* 605:    */     }
/* 606:639 */     if (B == this) {
/* 607:640 */       return scale(alpha);
/* 608:    */     }
/* 609:642 */     zero();
/* 610:643 */     for (MatrixEntry e : B) {
/* 611:644 */       if (e.get() != 0.0D) {
/* 612:645 */         set(e.row(), e.column(), alpha * e.get());
/* 613:    */       }
/* 614:    */     }
/* 615:647 */     return this;
/* 616:    */   }
/* 617:    */   
/* 618:    */   public Matrix add(Matrix B)
/* 619:    */   {
/* 620:651 */     return add(1.0D, B);
/* 621:    */   }
/* 622:    */   
/* 623:    */   public Matrix add(double alpha, Matrix B)
/* 624:    */   {
/* 625:655 */     checkSize(B);
/* 626:657 */     if (alpha != 0.0D) {
/* 627:658 */       for (MatrixEntry e : B) {
/* 628:659 */         add(e.row(), e.column(), alpha * e.get());
/* 629:    */       }
/* 630:    */     }
/* 631:661 */     return this;
/* 632:    */   }
/* 633:    */   
/* 634:    */   protected void checkSize(Matrix B)
/* 635:    */   {
/* 636:668 */     if (this.numRows != B.numRows()) {
/* 637:670 */       throw new IndexOutOfBoundsException("A.numRows != B.numRows (" + this.numRows + " != " + B.numRows() + ")");
/* 638:    */     }
/* 639:671 */     if (this.numColumns != B.numColumns()) {
/* 640:674 */       throw new IndexOutOfBoundsException("A.numColumns != B.numColumns (" + this.numColumns + " != " + B.numColumns() + ")");
/* 641:    */     }
/* 642:    */   }
/* 643:    */   
/* 644:    */   public Matrix transpose()
/* 645:    */   {
/* 646:678 */     checkTranspose();
/* 647:680 */     for (int j = 0; j < this.numColumns; j++) {
/* 648:681 */       for (int i = j + 1; i < this.numRows; i++)
/* 649:    */       {
/* 650:682 */         double value = get(i, j);
/* 651:683 */         set(i, j, get(j, i));
/* 652:684 */         set(j, i, value);
/* 653:    */       }
/* 654:    */     }
/* 655:687 */     return this;
/* 656:    */   }
/* 657:    */   
/* 658:    */   protected void checkTranspose()
/* 659:    */   {
/* 660:694 */     if (!isSquare()) {
/* 661:695 */       throw new IndexOutOfBoundsException("!A.isSquare");
/* 662:    */     }
/* 663:    */   }
/* 664:    */   
/* 665:    */   public Matrix transpose(Matrix B)
/* 666:    */   {
/* 667:699 */     checkTranspose(B);
/* 668:701 */     if (B == this) {
/* 669:702 */       return transpose();
/* 670:    */     }
/* 671:704 */     B.zero();
/* 672:705 */     for (MatrixEntry e : this) {
/* 673:706 */       B.set(e.column(), e.row(), e.get());
/* 674:    */     }
/* 675:708 */     return B;
/* 676:    */   }
/* 677:    */   
/* 678:    */   protected void checkTranspose(Matrix B)
/* 679:    */   {
/* 680:715 */     if (this.numRows != B.numColumns()) {
/* 681:717 */       throw new IndexOutOfBoundsException("A.numRows != B.numColumns (" + this.numRows + " != " + B.numColumns() + ")");
/* 682:    */     }
/* 683:718 */     if (this.numColumns != B.numRows()) {
/* 684:720 */       throw new IndexOutOfBoundsException("A.numColumns != B.numRows (" + this.numColumns + " != " + B.numRows() + ")");
/* 685:    */     }
/* 686:    */   }
/* 687:    */   
/* 688:    */   public double norm(Matrix.Norm type)
/* 689:    */   {
/* 690:724 */     if (type == Matrix.Norm.One) {
/* 691:725 */       return norm1();
/* 692:    */     }
/* 693:726 */     if (type == Matrix.Norm.Frobenius) {
/* 694:727 */       return normF();
/* 695:    */     }
/* 696:728 */     if (type == Matrix.Norm.Infinity) {
/* 697:729 */       return normInf();
/* 698:    */     }
/* 699:732 */     return max();
/* 700:    */   }
/* 701:    */   
/* 702:    */   protected double norm1()
/* 703:    */   {
/* 704:739 */     double[] rowSum = new double[this.numRows];
/* 705:740 */     for (MatrixEntry e : this) {
/* 706:741 */       rowSum[e.row()] += Math.abs(e.get());
/* 707:    */     }
/* 708:742 */     return max(rowSum);
/* 709:    */   }
/* 710:    */   
/* 711:    */   protected double normF()
/* 712:    */   {
/* 713:749 */     double scale = 0.0D;double ssq = 1.0D;
/* 714:750 */     for (MatrixEntry e : this)
/* 715:    */     {
/* 716:751 */       double Aval = e.get();
/* 717:752 */       if (Aval != 0.0D)
/* 718:    */       {
/* 719:753 */         double absxi = Math.abs(Aval);
/* 720:754 */         if (scale < absxi)
/* 721:    */         {
/* 722:755 */           ssq = 1.0D + ssq * Math.pow(scale / absxi, 2.0D);
/* 723:756 */           scale = absxi;
/* 724:    */         }
/* 725:    */         else
/* 726:    */         {
/* 727:758 */           ssq += Math.pow(absxi / scale, 2.0D);
/* 728:    */         }
/* 729:    */       }
/* 730:    */     }
/* 731:761 */     return scale * Math.sqrt(ssq);
/* 732:    */   }
/* 733:    */   
/* 734:    */   protected double normInf()
/* 735:    */   {
/* 736:768 */     double[] columnSum = new double[this.numColumns];
/* 737:769 */     for (MatrixEntry e : this) {
/* 738:770 */       columnSum[e.column()] += Math.abs(e.get());
/* 739:    */     }
/* 740:771 */     return max(columnSum);
/* 741:    */   }
/* 742:    */   
/* 743:    */   protected double max()
/* 744:    */   {
/* 745:778 */     double max = 0.0D;
/* 746:779 */     for (MatrixEntry e : this) {
/* 747:780 */       max = Math.max(Math.abs(e.get()), max);
/* 748:    */     }
/* 749:781 */     return max;
/* 750:    */   }
/* 751:    */   
/* 752:    */   protected double max(double[] x)
/* 753:    */   {
/* 754:788 */     double max = 0.0D;
/* 755:789 */     for (int i = 0; i < x.length; i++) {
/* 756:790 */       max = Math.max(x[i], max);
/* 757:    */     }
/* 758:791 */     return max;
/* 759:    */   }
/* 760:    */   
/* 761:    */   public String toString()
/* 762:    */   {
/* 763:797 */     Formatter out = new Formatter();
/* 764:    */     
/* 765:799 */     out.format("%10d %10d %19d\n", new Object[] { Integer.valueOf(this.numRows), Integer.valueOf(this.numColumns), 
/* 766:800 */       Integer.valueOf(Matrices.cardinality(this)) });
/* 767:    */     
/* 768:802 */     int i = 0;
/* 769:803 */     for (MatrixEntry e : this)
/* 770:    */     {
/* 771:804 */       if (e.get() != 0.0D) {
/* 772:805 */         out.format("%10d %10d % .12e\n", new Object[] { Integer.valueOf(e.row() + 1), Integer.valueOf(e.column() + 1), 
/* 773:806 */           Double.valueOf(e.get()) });
/* 774:    */       }
/* 775:807 */       i++;
/* 776:807 */       if (i == 100)
/* 777:    */       {
/* 778:808 */         out.format("...\n", new Object[0]);
/* 779:809 */         break;
/* 780:    */       }
/* 781:    */     }
/* 782:813 */     return out.toString();
/* 783:    */   }
/* 784:    */   
/* 785:    */   public Iterator<MatrixEntry> iterator()
/* 786:    */   {
/* 787:817 */     return new RefMatrixIterator();
/* 788:    */   }
/* 789:    */   
/* 790:    */   class RefMatrixIterator
/* 791:    */     implements Iterator<MatrixEntry>
/* 792:    */   {
/* 793:    */     int row;
/* 794:    */     int column;
/* 795:833 */     final AbstractMatrix.RefMatrixEntry entry = new AbstractMatrix.RefMatrixEntry(AbstractMatrix.this);
/* 796:    */     
/* 797:    */     RefMatrixIterator() {}
/* 798:    */     
/* 799:    */     public boolean hasNext()
/* 800:    */     {
/* 801:836 */       return (this.row < AbstractMatrix.this.numRows) && (this.column < AbstractMatrix.this.numColumns);
/* 802:    */     }
/* 803:    */     
/* 804:    */     public MatrixEntry next()
/* 805:    */     {
/* 806:840 */       this.entry.update(this.row, this.column);
/* 807:843 */       if (this.row < AbstractMatrix.this.numRows - 1)
/* 808:    */       {
/* 809:844 */         this.row += 1;
/* 810:    */       }
/* 811:    */       else
/* 812:    */       {
/* 813:846 */         this.column += 1;
/* 814:847 */         this.row = 0;
/* 815:    */       }
/* 816:850 */       return this.entry;
/* 817:    */     }
/* 818:    */     
/* 819:    */     public void remove()
/* 820:    */     {
/* 821:854 */       this.entry.set(0.0D);
/* 822:    */     }
/* 823:    */   }
/* 824:    */   
/* 825:    */   class RefMatrixEntry
/* 826:    */     implements MatrixEntry
/* 827:    */   {
/* 828:    */     private int row;
/* 829:    */     private int column;
/* 830:    */     
/* 831:    */     RefMatrixEntry() {}
/* 832:    */     
/* 833:    */     public void update(int row, int column)
/* 834:    */     {
/* 835:873 */       this.row = row;
/* 836:874 */       this.column = column;
/* 837:    */     }
/* 838:    */     
/* 839:    */     public int row()
/* 840:    */     {
/* 841:878 */       return this.row;
/* 842:    */     }
/* 843:    */     
/* 844:    */     public int column()
/* 845:    */     {
/* 846:882 */       return this.column;
/* 847:    */     }
/* 848:    */     
/* 849:    */     public double get()
/* 850:    */     {
/* 851:886 */       return AbstractMatrix.this.get(this.row, this.column);
/* 852:    */     }
/* 853:    */     
/* 854:    */     public void set(double value)
/* 855:    */     {
/* 856:890 */       AbstractMatrix.this.set(this.row, this.column, value);
/* 857:    */     }
/* 858:    */   }
/* 859:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.AbstractMatrix
 * JD-Core Version:    0.7.0.1
 */