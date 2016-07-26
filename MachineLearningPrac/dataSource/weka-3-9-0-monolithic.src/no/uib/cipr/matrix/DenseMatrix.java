/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.BLAS;
/*   4:    */ import com.github.fommil.netlib.LAPACK;
/*   5:    */ import java.io.IOException;
/*   6:    */ import no.uib.cipr.matrix.io.MatrixInfo;
/*   7:    */ import no.uib.cipr.matrix.io.MatrixInfo.MatrixField;
/*   8:    */ import no.uib.cipr.matrix.io.MatrixInfo.MatrixSymmetry;
/*   9:    */ import no.uib.cipr.matrix.io.MatrixSize;
/*  10:    */ import no.uib.cipr.matrix.io.MatrixVectorReader;
/*  11:    */ import org.netlib.util.intW;
/*  12:    */ 
/*  13:    */ public class DenseMatrix
/*  14:    */   extends AbstractDenseMatrix
/*  15:    */ {
/*  16:    */   public DenseMatrix(MatrixVectorReader r)
/*  17:    */     throws IOException
/*  18:    */   {
/*  19:100 */     super(0, 0);
/*  20:    */     
/*  21:    */ 
/*  22:    */ 
/*  23:104 */     MatrixInfo info = null;
/*  24:105 */     if (r.hasInfo()) {
/*  25:106 */       info = r.readMatrixInfo();
/*  26:    */     } else {
/*  27:108 */       info = new MatrixInfo(true, MatrixInfo.MatrixField.Real, MatrixInfo.MatrixSymmetry.General);
/*  28:    */     }
/*  29:110 */     MatrixSize size = r.readMatrixSize(info);
/*  30:    */     
/*  31:    */ 
/*  32:113 */     this.numRows = size.numRows();
/*  33:114 */     this.numColumns = size.numColumns();
/*  34:115 */     this.data = new double[this.numRows * this.numColumns];
/*  35:118 */     if (info.isPattern()) {
/*  36:119 */       throw new UnsupportedOperationException("Pattern matrices are not supported");
/*  37:    */     }
/*  38:121 */     if (info.isComplex()) {
/*  39:122 */       throw new UnsupportedOperationException("Complex matrices are not supported");
/*  40:    */     }
/*  41:126 */     if (info.isCoordinate())
/*  42:    */     {
/*  43:129 */       int nz = size.numEntries();
/*  44:130 */       int[] row = new int[nz];
/*  45:131 */       int[] column = new int[nz];
/*  46:132 */       double[] entry = new double[nz];
/*  47:133 */       r.readCoordinate(row, column, entry);
/*  48:    */       
/*  49:    */ 
/*  50:136 */       r.add(-1, row);
/*  51:137 */       r.add(-1, column);
/*  52:140 */       for (int i = 0; i < nz; i++) {
/*  53:141 */         set(row[i], column[i], entry[i]);
/*  54:    */       }
/*  55:    */     }
/*  56:    */     else
/*  57:    */     {
/*  58:145 */       r.readArray(this.data);
/*  59:    */     }
/*  60:148 */     if (info.isSymmetric()) {
/*  61:149 */       for (int i = 0; i < this.numRows; i++) {
/*  62:150 */         for (int j = 0; j < i; j++) {
/*  63:151 */           set(j, i, get(i, j));
/*  64:    */         }
/*  65:    */       }
/*  66:152 */     } else if (info.isSkewSymmetric()) {
/*  67:153 */       for (int i = 0; i < this.numRows; i++) {
/*  68:154 */         for (int j = 0; j < i; j++) {
/*  69:155 */           set(j, i, -get(i, j));
/*  70:    */         }
/*  71:    */       }
/*  72:    */     }
/*  73:    */   }
/*  74:    */   
/*  75:    */   public DenseMatrix(int numRows, int numColumns)
/*  76:    */   {
/*  77:167 */     super(numRows, numColumns);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public DenseMatrix(Matrix A)
/*  81:    */   {
/*  82:177 */     super(A);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public DenseMatrix(Matrix A, boolean deep)
/*  86:    */   {
/*  87:191 */     super(A, deep);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public DenseMatrix(Vector x, boolean deep)
/*  91:    */   {
/*  92:205 */     super(x.size(), 1);
/*  93:207 */     if (deep)
/*  94:    */     {
/*  95:208 */       for (VectorEntry e : x) {
/*  96:209 */         set(e.index(), 0, e.get());
/*  97:    */       }
/*  98:    */     }
/*  99:    */     else
/* 100:    */     {
/* 101:211 */       if (!(x instanceof DenseVector)) {
/* 102:212 */         throw new IllegalArgumentException("x must be a DenseVector");
/* 103:    */       }
/* 104:213 */       this.data = ((DenseVector)x).getData();
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public DenseMatrix(Vector x)
/* 109:    */   {
/* 110:225 */     this(x, true);
/* 111:    */   }
/* 112:    */   
/* 113:    */   public DenseMatrix(Vector[] x)
/* 114:    */   {
/* 115:237 */     super(x[0].size(), x.length);
/* 116:240 */     for (Vector v : x) {
/* 117:241 */       if (v.size() != this.numRows) {
/* 118:242 */         throw new IllegalArgumentException("All vectors must be of the same size");
/* 119:    */       }
/* 120:    */     }
/* 121:246 */     for (int j = 0; j < x.length; j++) {
/* 122:247 */       for (VectorEntry e : x[j]) {
/* 123:248 */         set(e.index(), j, e.get());
/* 124:    */       }
/* 125:    */     }
/* 126:    */   }
/* 127:    */   
/* 128:    */   public DenseMatrix(double[][] values)
/* 129:    */   {
/* 130:258 */     super(values.length, values[0].length);
/* 131:261 */     for (int i = 0; i < values.length; i++)
/* 132:    */     {
/* 133:262 */       if (values[i].length != this.numColumns) {
/* 134:263 */         throw new IllegalArgumentException("Array cannot be jagged");
/* 135:    */       }
/* 136:264 */       for (int j = 0; j < values[i].length; j++) {
/* 137:265 */         set(i, j, values[i][j]);
/* 138:    */       }
/* 139:    */     }
/* 140:    */   }
/* 141:    */   
/* 142:    */   public DenseMatrix(int numRows, int numColumns, double[] values, boolean deep)
/* 143:    */   {
/* 144:279 */     super(numRows, numColumns);
/* 145:280 */     if (numRows * numColumns != values.length) {
/* 146:281 */       throw new IllegalArgumentException("dimensions do not match");
/* 147:    */     }
/* 148:282 */     if (deep) {
/* 149:283 */       this.data = ((double[])values.clone());
/* 150:    */     } else {
/* 151:285 */       this.data = values;
/* 152:    */     }
/* 153:    */   }
/* 154:    */   
/* 155:    */   public DenseMatrix copy()
/* 156:    */   {
/* 157:290 */     return new DenseMatrix(this);
/* 158:    */   }
/* 159:    */   
/* 160:    */   void copy(Matrix A)
/* 161:    */   {
/* 162:295 */     for (MatrixEntry e : A) {
/* 163:296 */       set(e.row(), e.column(), e.get());
/* 164:    */     }
/* 165:    */   }
/* 166:    */   
/* 167:    */   public Matrix multAdd(double alpha, Matrix B, Matrix C)
/* 168:    */   {
/* 169:301 */     if ((!(B instanceof DenseMatrix)) || (!(C instanceof DenseMatrix))) {
/* 170:302 */       return super.multAdd(alpha, B, C);
/* 171:    */     }
/* 172:304 */     checkMultAdd(B, C);
/* 173:    */     
/* 174:306 */     double[] Bd = ((DenseMatrix)B).getData();
/* 175:307 */     double[] Cd = ((DenseMatrix)C).getData();
/* 176:    */     
/* 177:309 */     BLAS.getInstance().dgemm(Transpose.NoTranspose.netlib(), Transpose.NoTranspose
/* 178:310 */       .netlib(), C.numRows(), C.numColumns(), this.numColumns, alpha, this.data, 
/* 179:311 */       Math.max(1, this.numRows), Bd, 
/* 180:312 */       Math.max(1, B.numRows()), 1.0D, Cd, Math.max(1, C.numRows()));
/* 181:    */     
/* 182:314 */     return C;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public Matrix transAmultAdd(double alpha, Matrix B, Matrix C)
/* 186:    */   {
/* 187:319 */     if ((!(B instanceof DenseMatrix)) || (!(C instanceof DenseMatrix))) {
/* 188:320 */       return super.transAmultAdd(alpha, B, C);
/* 189:    */     }
/* 190:322 */     checkTransAmultAdd(B, C);
/* 191:    */     
/* 192:324 */     double[] Bd = ((DenseMatrix)B).getData();
/* 193:325 */     double[] Cd = ((DenseMatrix)C).getData();
/* 194:    */     
/* 195:327 */     BLAS.getInstance().dgemm(Transpose.Transpose.netlib(), Transpose.NoTranspose
/* 196:328 */       .netlib(), C.numRows(), C.numColumns(), this.numRows, alpha, this.data, 
/* 197:329 */       Math.max(1, this.numRows), Bd, 
/* 198:330 */       Math.max(1, B.numRows()), 1.0D, Cd, Math.max(1, C.numRows()));
/* 199:    */     
/* 200:332 */     return C;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public Matrix transBmultAdd(double alpha, Matrix B, Matrix C)
/* 204:    */   {
/* 205:337 */     if ((!(B instanceof DenseMatrix)) || (!(C instanceof DenseMatrix))) {
/* 206:338 */       return super.transBmultAdd(alpha, B, C);
/* 207:    */     }
/* 208:340 */     checkTransBmultAdd(B, C);
/* 209:    */     
/* 210:342 */     double[] Bd = ((DenseMatrix)B).getData();
/* 211:343 */     double[] Cd = ((DenseMatrix)C).getData();
/* 212:    */     
/* 213:345 */     BLAS.getInstance().dgemm(Transpose.NoTranspose.netlib(), Transpose.Transpose
/* 214:346 */       .netlib(), C.numRows(), C.numColumns(), this.numColumns, alpha, this.data, 
/* 215:347 */       Math.max(1, this.numRows), Bd, 
/* 216:348 */       Math.max(1, B.numRows()), 1.0D, Cd, Math.max(1, C.numRows()));
/* 217:    */     
/* 218:350 */     return C;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public Matrix transABmultAdd(double alpha, Matrix B, Matrix C)
/* 222:    */   {
/* 223:355 */     if ((!(B instanceof DenseMatrix)) || (!(C instanceof DenseMatrix))) {
/* 224:356 */       return super.transABmultAdd(alpha, B, C);
/* 225:    */     }
/* 226:358 */     checkTransABmultAdd(B, C);
/* 227:    */     
/* 228:360 */     double[] Bd = ((DenseMatrix)B).getData();
/* 229:361 */     double[] Cd = ((DenseMatrix)C).getData();
/* 230:    */     
/* 231:363 */     BLAS.getInstance().dgemm(Transpose.Transpose.netlib(), Transpose.Transpose
/* 232:364 */       .netlib(), C.numRows(), C.numColumns(), this.numRows, alpha, this.data, 
/* 233:365 */       Math.max(1, this.numRows), Bd, 
/* 234:366 */       Math.max(1, B.numRows()), 1.0D, Cd, Math.max(1, C.numRows()));
/* 235:    */     
/* 236:368 */     return C;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public Matrix rank1(double alpha, Vector x, Vector y)
/* 240:    */   {
/* 241:373 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/* 242:374 */       return super.rank1(alpha, x, y);
/* 243:    */     }
/* 244:376 */     checkRank1(x, y);
/* 245:    */     
/* 246:378 */     double[] xd = ((DenseVector)x).getData();
/* 247:379 */     double[] yd = ((DenseVector)y).getData();
/* 248:    */     
/* 249:381 */     BLAS.getInstance().dger(this.numRows, this.numColumns, alpha, xd, 1, yd, 1, this.data, 
/* 250:382 */       Math.max(1, this.numRows));
/* 251:    */     
/* 252:384 */     return this;
/* 253:    */   }
/* 254:    */   
/* 255:    */   public Vector multAdd(double alpha, Vector x, Vector y)
/* 256:    */   {
/* 257:389 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/* 258:390 */       return super.multAdd(alpha, x, y);
/* 259:    */     }
/* 260:392 */     checkMultAdd(x, y);
/* 261:    */     
/* 262:394 */     double[] xd = ((DenseVector)x).getData();
/* 263:395 */     double[] yd = ((DenseVector)y).getData();
/* 264:    */     
/* 265:397 */     BLAS.getInstance().dgemv(Transpose.NoTranspose.netlib(), this.numRows, this.numColumns, alpha, this.data, 
/* 266:398 */       Math.max(this.numRows, 1), xd, 1, 1.0D, yd, 1);
/* 267:    */     
/* 268:400 */     return y;
/* 269:    */   }
/* 270:    */   
/* 271:    */   public Vector transMultAdd(double alpha, Vector x, Vector y)
/* 272:    */   {
/* 273:405 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/* 274:406 */       return super.transMultAdd(alpha, x, y);
/* 275:    */     }
/* 276:408 */     checkTransMultAdd(x, y);
/* 277:    */     
/* 278:410 */     double[] xd = ((DenseVector)x).getData();
/* 279:411 */     double[] yd = ((DenseVector)y).getData();
/* 280:    */     
/* 281:413 */     BLAS.getInstance().dgemv(Transpose.Transpose.netlib(), this.numRows, this.numColumns, alpha, this.data, 
/* 282:414 */       Math.max(this.numRows, 1), xd, 1, 1.0D, yd, 1);
/* 283:    */     
/* 284:416 */     return y;
/* 285:    */   }
/* 286:    */   
/* 287:    */   public Matrix solve(Matrix B, Matrix X)
/* 288:    */   {
/* 289:422 */     if (this.numRows != B.numRows()) {
/* 290:424 */       throw new IllegalArgumentException("numRows != B.numRows() (" + this.numRows + " != " + B.numRows() + ")");
/* 291:    */     }
/* 292:425 */     if (this.numColumns != X.numRows()) {
/* 293:427 */       throw new IllegalArgumentException("numColumns != X.numRows() (" + this.numColumns + " != " + X.numRows() + ")");
/* 294:    */     }
/* 295:428 */     if (X.numColumns() != B.numColumns()) {
/* 296:431 */       throw new IllegalArgumentException("X.numColumns() != B.numColumns() (" + X.numColumns() + " != " + B.numColumns() + ")");
/* 297:    */     }
/* 298:433 */     if (isSquare()) {
/* 299:434 */       return LUsolve(B, X);
/* 300:    */     }
/* 301:436 */     return QRsolve(B, X, Transpose.NoTranspose);
/* 302:    */   }
/* 303:    */   
/* 304:    */   public Vector solve(Vector b, Vector x)
/* 305:    */   {
/* 306:441 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/* 307:442 */     solve(B, X);
/* 308:443 */     return x;
/* 309:    */   }
/* 310:    */   
/* 311:    */   public Matrix transSolve(Matrix B, Matrix X)
/* 312:    */   {
/* 313:449 */     if (this.numColumns != B.numRows()) {
/* 314:451 */       throw new IllegalArgumentException("numColumns != B.numRows() (" + this.numColumns + " != " + B.numRows() + ")");
/* 315:    */     }
/* 316:452 */     if (this.numRows != X.numRows()) {
/* 317:454 */       throw new IllegalArgumentException("numRows != X.numRows() (" + this.numRows + " != " + X.numRows() + ")");
/* 318:    */     }
/* 319:455 */     if (X.numColumns() != B.numColumns()) {
/* 320:458 */       throw new IllegalArgumentException("X.numColumns() != B.numColumns() (" + X.numColumns() + " != " + B.numColumns() + ")");
/* 321:    */     }
/* 322:460 */     return QRsolve(B, X, Transpose.Transpose);
/* 323:    */   }
/* 324:    */   
/* 325:    */   public Vector transSolve(Vector b, Vector x)
/* 326:    */   {
/* 327:465 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/* 328:466 */     transSolve(B, X);
/* 329:467 */     return x;
/* 330:    */   }
/* 331:    */   
/* 332:    */   Matrix LUsolve(Matrix B, Matrix X)
/* 333:    */   {
/* 334:471 */     if (!(X instanceof DenseMatrix)) {
/* 335:472 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/* 336:    */     }
/* 337:474 */     double[] Xd = ((DenseMatrix)X).getData();
/* 338:    */     
/* 339:476 */     X.set(B);
/* 340:    */     
/* 341:478 */     int[] piv = new int[this.numRows];
/* 342:    */     
/* 343:480 */     intW info = new intW(0);
/* 344:481 */     LAPACK.getInstance().dgesv(this.numRows, B.numColumns(), (double[])this.data.clone(), 
/* 345:482 */       Matrices.ld(this.numRows), piv, Xd, Matrices.ld(this.numRows), info);
/* 346:484 */     if (info.val > 0) {
/* 347:485 */       throw new MatrixSingularException();
/* 348:    */     }
/* 349:486 */     if (info.val < 0) {
/* 350:487 */       throw new IllegalArgumentException();
/* 351:    */     }
/* 352:489 */     return X;
/* 353:    */   }
/* 354:    */   
/* 355:    */   Matrix QRsolve(Matrix B, Matrix X, Transpose trans)
/* 356:    */   {
/* 357:493 */     int nrhs = B.numColumns();
/* 358:    */     
/* 359:    */ 
/* 360:496 */     DenseMatrix Xtmp = new DenseMatrix(Math.max(this.numRows, this.numColumns), nrhs);
/* 361:497 */     int M = trans == Transpose.NoTranspose ? this.numRows : this.numColumns;
/* 362:498 */     for (int j = 0; j < nrhs; j++) {
/* 363:499 */       for (int i = 0; i < M; i++) {
/* 364:500 */         Xtmp.set(i, j, B.get(i, j));
/* 365:    */       }
/* 366:    */     }
/* 367:501 */     double[] newData = (double[])this.data.clone();
/* 368:    */     
/* 369:    */ 
/* 370:504 */     double[] work = new double[1];
/* 371:505 */     intW info = new intW(0);
/* 372:506 */     LAPACK.getInstance().dgels(trans.netlib(), this.numRows, this.numColumns, nrhs, newData, 
/* 373:507 */       Matrices.ld(this.numRows), Xtmp.getData(), 
/* 374:508 */       Matrices.ld(this.numRows, this.numColumns), work, -1, info);
/* 375:    */     
/* 376:    */ 
/* 377:511 */     int lwork = -1;
/* 378:512 */     if (info.val != 0) {
/* 379:513 */       lwork = Math.max(1, 
/* 380:    */       
/* 381:515 */         Math.min(this.numRows, this.numColumns) + 
/* 382:516 */         Math.max(Math.min(this.numRows, this.numColumns), nrhs));
/* 383:    */     } else {
/* 384:518 */       lwork = Math.max((int)work[0], 1);
/* 385:    */     }
/* 386:519 */     work = new double[lwork];
/* 387:    */     
/* 388:    */ 
/* 389:522 */     info.val = 0;
/* 390:523 */     LAPACK.getInstance().dgels(trans.netlib(), this.numRows, this.numColumns, nrhs, newData, 
/* 391:524 */       Matrices.ld(this.numRows), Xtmp.getData(), 
/* 392:525 */       Matrices.ld(this.numRows, this.numColumns), work, lwork, info);
/* 393:527 */     if (info.val < 0) {
/* 394:528 */       throw new IllegalArgumentException();
/* 395:    */     }
/* 396:531 */     int N = trans == Transpose.NoTranspose ? this.numColumns : this.numRows;
/* 397:532 */     for (int j = 0; j < nrhs; j++) {
/* 398:533 */       for (int i = 0; i < N; i++) {
/* 399:534 */         X.set(i, j, Xtmp.get(i, j));
/* 400:    */       }
/* 401:    */     }
/* 402:535 */     return X;
/* 403:    */   }
/* 404:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.DenseMatrix
 * JD-Core Version:    0.7.0.1
 */