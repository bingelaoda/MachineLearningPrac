/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.util.Iterator;
/*   5:    */ import java.util.Set;
/*   6:    */ import java.util.TreeSet;
/*   7:    */ import no.uib.cipr.matrix.AbstractMatrix;
/*   8:    */ import no.uib.cipr.matrix.DenseVector;
/*   9:    */ import no.uib.cipr.matrix.Matrix;
/*  10:    */ import no.uib.cipr.matrix.MatrixEntry;
/*  11:    */ import no.uib.cipr.matrix.Vector;
/*  12:    */ import no.uib.cipr.matrix.io.MatrixInfo;
/*  13:    */ import no.uib.cipr.matrix.io.MatrixInfo.MatrixField;
/*  14:    */ import no.uib.cipr.matrix.io.MatrixInfo.MatrixSymmetry;
/*  15:    */ import no.uib.cipr.matrix.io.MatrixSize;
/*  16:    */ import no.uib.cipr.matrix.io.MatrixVectorReader;
/*  17:    */ 
/*  18:    */ public class CompDiagMatrix
/*  19:    */   extends AbstractMatrix
/*  20:    */ {
/*  21:    */   double[][] diag;
/*  22:    */   int[] ind;
/*  23:    */   
/*  24:    */   public CompDiagMatrix(MatrixVectorReader r)
/*  25:    */     throws IOException
/*  26:    */   {
/*  27: 63 */     super(0, 0);
/*  28:    */     
/*  29:    */ 
/*  30:    */ 
/*  31: 67 */     MatrixInfo info = null;
/*  32: 68 */     if (r.hasInfo()) {
/*  33: 69 */       info = r.readMatrixInfo();
/*  34:    */     } else {
/*  35: 71 */       info = new MatrixInfo(true, MatrixInfo.MatrixField.Real, MatrixInfo.MatrixSymmetry.General);
/*  36:    */     }
/*  37: 73 */     MatrixSize size = r.readMatrixSize(info);
/*  38:    */     
/*  39:    */ 
/*  40: 76 */     this.numRows = size.numRows();
/*  41: 77 */     this.numColumns = size.numColumns();
/*  42: 80 */     if (info.isPattern()) {
/*  43: 81 */       throw new UnsupportedOperationException("Pattern matrices are not supported");
/*  44:    */     }
/*  45: 83 */     if (info.isDense()) {
/*  46: 84 */       throw new UnsupportedOperationException("Dense matrices are not supported");
/*  47:    */     }
/*  48: 86 */     if (info.isComplex()) {
/*  49: 87 */       throw new UnsupportedOperationException("Complex matrices are not supported");
/*  50:    */     }
/*  51: 91 */     int[] row = new int[size.numEntries()];
/*  52: 92 */     int[] column = new int[size.numEntries()];
/*  53: 93 */     double[] entry = new double[size.numEntries()];
/*  54: 94 */     r.readCoordinate(row, column, entry);
/*  55:    */     
/*  56:    */ 
/*  57: 97 */     r.add(-1, row);
/*  58: 98 */     r.add(-1, column);
/*  59:    */     
/*  60:    */ 
/*  61:101 */     Set<Integer> diags = new TreeSet();
/*  62:102 */     for (int i = 0; i < size.numEntries(); i++) {
/*  63:103 */       diags.add(Integer.valueOf(getDiagonal(row[i], column[i])));
/*  64:    */     }
/*  65:105 */     if ((info.isSymmetric()) || (info.isSkewSymmetric())) {
/*  66:106 */       for (int i = 0; i < size.numEntries(); i++) {
/*  67:107 */         if (row[i] != column[i]) {
/*  68:108 */           diags.add(Integer.valueOf(getDiagonal(column[i], row[i])));
/*  69:    */         }
/*  70:    */       }
/*  71:    */     }
/*  72:111 */     int[] ind = new int[diags.size()];
/*  73:    */     
/*  74:113 */     Integer[] ints = new Integer[diags.size()];
/*  75:114 */     diags.toArray(ints);
/*  76:115 */     for (int i = 0; i < diags.size(); i++) {
/*  77:116 */       ind[i] = ints[i].intValue();
/*  78:    */     }
/*  79:120 */     construct(ind);
/*  80:123 */     for (int i = 0; i < size.numEntries(); i++) {
/*  81:124 */       set(row[i], column[i], entry[i]);
/*  82:    */     }
/*  83:127 */     if (info.isSymmetric()) {
/*  84:128 */       for (int i = 0; i < size.numEntries(); i++) {
/*  85:129 */         if (row[i] != column[i]) {
/*  86:130 */           set(column[i], row[i], entry[i]);
/*  87:    */         }
/*  88:    */       }
/*  89:132 */     } else if (info.isSkewSymmetric()) {
/*  90:133 */       for (int i = 0; i < size.numEntries(); i++) {
/*  91:134 */         if (row[i] != column[i]) {
/*  92:135 */           set(column[i], row[i], -entry[i]);
/*  93:    */         }
/*  94:    */       }
/*  95:    */     }
/*  96:    */   }
/*  97:    */   
/*  98:    */   public CompDiagMatrix(int numRows, int numColumns, int[] diagonal)
/*  99:    */   {
/* 100:144 */     super(numRows, numColumns);
/* 101:145 */     construct(diagonal);
/* 102:    */   }
/* 103:    */   
/* 104:    */   private void construct(int[] diagonal)
/* 105:    */   {
/* 106:149 */     this.diag = new double[diagonal.length][];
/* 107:150 */     this.ind = new int[diagonal.length];
/* 108:    */     
/* 109:    */ 
/* 110:153 */     int[] sortedDiagonal = new int[diagonal.length];
/* 111:154 */     System.arraycopy(diagonal, 0, sortedDiagonal, 0, diagonal.length);
/* 112:155 */     java.util.Arrays.sort(sortedDiagonal);
/* 113:157 */     for (int i = 0; i < diagonal.length; i++)
/* 114:    */     {
/* 115:158 */       this.ind[i] = sortedDiagonal[i];
/* 116:159 */       this.diag[i] = new double[getDiagSize(sortedDiagonal[i])];
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   public CompDiagMatrix(int numRows, int numColumns)
/* 121:    */   {
/* 122:167 */     this(numRows, numColumns, new int[0]);
/* 123:    */   }
/* 124:    */   
/* 125:    */   public CompDiagMatrix(Matrix A, int[] diagonal, boolean deep)
/* 126:    */   {
/* 127:177 */     super(A);
/* 128:179 */     if (deep)
/* 129:    */     {
/* 130:180 */       construct(diagonal);
/* 131:181 */       set(A);
/* 132:    */     }
/* 133:    */     else
/* 134:    */     {
/* 135:183 */       CompDiagMatrix Ac = (CompDiagMatrix)A;
/* 136:184 */       this.diag = Ac.getDiagonals();
/* 137:185 */       this.ind = Ac.getIndex();
/* 138:    */     }
/* 139:    */   }
/* 140:    */   
/* 141:    */   public CompDiagMatrix(Matrix A, int[] diagonal)
/* 142:    */   {
/* 143:194 */     this(A, diagonal, true);
/* 144:    */   }
/* 145:    */   
/* 146:    */   public CompDiagMatrix(Matrix A, boolean deep)
/* 147:    */   {
/* 148:203 */     this(A, new int[0], deep);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public CompDiagMatrix(Matrix A)
/* 152:    */   {
/* 153:211 */     this(A, new int[0], true);
/* 154:    */   }
/* 155:    */   
/* 156:    */   public double[][] getDiagonals()
/* 157:    */   {
/* 158:218 */     return this.diag;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public int[] getIndex()
/* 162:    */   {
/* 163:225 */     return this.ind;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void add(int row, int column, double value)
/* 167:    */   {
/* 168:230 */     check(row, column);
/* 169:    */     
/* 170:232 */     int diagonal = getCompDiagIndex(row, column);
/* 171:    */     
/* 172:234 */     this.diag[diagonal][getOnDiagIndex(row, column)] += value;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public double get(int row, int column)
/* 176:    */   {
/* 177:239 */     check(row, column);
/* 178:    */     
/* 179:241 */     int diagonal = java.util.Arrays.binarySearch(this.ind, getDiagonal(row, column));
/* 180:243 */     if (diagonal >= 0) {
/* 181:244 */       return this.diag[diagonal][getOnDiagIndex(row, column)];
/* 182:    */     }
/* 183:246 */     return 0.0D;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void set(int row, int column, double value)
/* 187:    */   {
/* 188:251 */     check(row, column);
/* 189:    */     
/* 190:253 */     int diagonal = getCompDiagIndex(row, column);
/* 191:    */     
/* 192:255 */     this.diag[diagonal][getOnDiagIndex(row, column)] = value;
/* 193:    */   }
/* 194:    */   
/* 195:    */   private int getDiagonal(int row, int column)
/* 196:    */   {
/* 197:259 */     return column - row;
/* 198:    */   }
/* 199:    */   
/* 200:    */   private int getOnDiagIndex(int row, int column)
/* 201:    */   {
/* 202:263 */     return row > column ? column : row;
/* 203:    */   }
/* 204:    */   
/* 205:    */   private int getCompDiagIndex(int row, int column)
/* 206:    */   {
/* 207:267 */     int diagonal = getDiagonal(row, column);
/* 208:    */     
/* 209:    */ 
/* 210:270 */     int index = Arrays.binarySearchGreater(this.ind, diagonal);
/* 211:272 */     if ((index < this.ind.length) && (this.ind[index] == diagonal)) {
/* 212:273 */       return index;
/* 213:    */     }
/* 214:276 */     int size = getDiagSize(diagonal);
/* 215:    */     
/* 216:    */ 
/* 217:279 */     double[] newDiag = new double[size];
/* 218:280 */     double[][] newDiagArray = new double[this.diag.length + 1][];
/* 219:281 */     int[] newInd = new int[this.ind.length + 1];
/* 220:    */     
/* 221:    */ 
/* 222:284 */     System.arraycopy(this.ind, 0, newInd, 0, index);
/* 223:285 */     System.arraycopy(this.ind, index, newInd, index + 1, this.ind.length - index);
/* 224:286 */     for (int i = 0; i < index; i++) {
/* 225:287 */       newDiagArray[i] = this.diag[i];
/* 226:    */     }
/* 227:288 */     for (int i = index; i < this.diag.length; i++) {
/* 228:289 */       newDiagArray[(i + 1)] = this.diag[i];
/* 229:    */     }
/* 230:291 */     newInd[index] = diagonal;
/* 231:292 */     newDiagArray[index] = newDiag;
/* 232:    */     
/* 233:    */ 
/* 234:295 */     this.ind = newInd;
/* 235:296 */     this.diag = newDiagArray;
/* 236:    */     
/* 237:298 */     return index;
/* 238:    */   }
/* 239:    */   
/* 240:    */   private int getDiagSize(int diagonal)
/* 241:    */   {
/* 242:305 */     if (diagonal < 0) {
/* 243:306 */       return Math.min(this.numRows + diagonal, this.numColumns);
/* 244:    */     }
/* 245:308 */     return Math.min(this.numRows, this.numColumns - diagonal);
/* 246:    */   }
/* 247:    */   
/* 248:    */   public Matrix copy()
/* 249:    */   {
/* 250:313 */     return new CompDiagMatrix(this, this.ind);
/* 251:    */   }
/* 252:    */   
/* 253:    */   public Matrix zero()
/* 254:    */   {
/* 255:318 */     for (int i = 0; i < this.diag.length; i++) {
/* 256:319 */       java.util.Arrays.fill(this.diag[i], 0.0D);
/* 257:    */     }
/* 258:320 */     return this;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public Vector mult(Vector x, Vector y)
/* 262:    */   {
/* 263:325 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/* 264:326 */       return super.mult(x, y);
/* 265:    */     }
/* 266:328 */     checkMultAdd(x, y);
/* 267:    */     
/* 268:330 */     double[] xd = ((DenseVector)x).getData();
/* 269:331 */     double[] yd = ((DenseVector)y).getData();
/* 270:    */     
/* 271:333 */     y.zero();
/* 272:335 */     for (int i = 0; i < this.ind.length; i++)
/* 273:    */     {
/* 274:336 */       int row = this.ind[i] < 0 ? -this.ind[i] : 0;
/* 275:337 */       int column = this.ind[i] > 0 ? this.ind[i] : 0;
/* 276:338 */       double[] locDiag = this.diag[i];
/* 277:339 */       for (int j = 0; j < locDiag.length; column++)
/* 278:    */       {
/* 279:340 */         yd[row] += locDiag[j] * xd[column];j++;row++;
/* 280:    */       }
/* 281:    */     }
/* 282:343 */     return y;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public Vector multAdd(double alpha, Vector x, Vector y)
/* 286:    */   {
/* 287:348 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/* 288:349 */       return super.multAdd(alpha, x, y);
/* 289:    */     }
/* 290:351 */     checkMultAdd(x, y);
/* 291:    */     
/* 292:353 */     double[] xd = ((DenseVector)x).getData();
/* 293:354 */     double[] yd = ((DenseVector)y).getData();
/* 294:356 */     for (int i = 0; i < this.ind.length; i++)
/* 295:    */     {
/* 296:357 */       int row = this.ind[i] < 0 ? -this.ind[i] : 0;
/* 297:358 */       int column = this.ind[i] > 0 ? this.ind[i] : 0;
/* 298:359 */       double[] locDiag = this.diag[i];
/* 299:360 */       for (int j = 0; j < locDiag.length; column++)
/* 300:    */       {
/* 301:361 */         yd[row] += alpha * locDiag[j] * xd[column];j++;row++;
/* 302:    */       }
/* 303:    */     }
/* 304:364 */     return y;
/* 305:    */   }
/* 306:    */   
/* 307:    */   public Vector transMultAdd(double alpha, Vector x, Vector y)
/* 308:    */   {
/* 309:369 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/* 310:370 */       return super.transMultAdd(alpha, x, y);
/* 311:    */     }
/* 312:372 */     checkTransMultAdd(x, y);
/* 313:    */     
/* 314:374 */     double[] xd = ((DenseVector)x).getData();
/* 315:375 */     double[] yd = ((DenseVector)y).getData();
/* 316:377 */     for (int i = 0; i < this.ind.length; i++)
/* 317:    */     {
/* 318:378 */       int row = this.ind[i] < 0 ? -this.ind[i] : 0;
/* 319:379 */       int column = this.ind[i] > 0 ? this.ind[i] : 0;
/* 320:380 */       double[] locDiag = this.diag[i];
/* 321:381 */       for (int j = 0; j < locDiag.length; column++)
/* 322:    */       {
/* 323:382 */         yd[column] += alpha * locDiag[j] * xd[row];j++;row++;
/* 324:    */       }
/* 325:    */     }
/* 326:385 */     return y;
/* 327:    */   }
/* 328:    */   
/* 329:    */   public Iterator<MatrixEntry> iterator()
/* 330:    */   {
/* 331:390 */     return new CompDiagMatrixIterator(null);
/* 332:    */   }
/* 333:    */   
/* 334:    */   private class CompDiagMatrixIterator
/* 335:    */     implements Iterator<MatrixEntry>
/* 336:    */   {
/* 337:    */     private int diagonal;
/* 338:    */     private int index;
/* 339:400 */     private CompDiagMatrix.CompDiagMatrixEntry entry = new CompDiagMatrix.CompDiagMatrixEntry(CompDiagMatrix.this, null);
/* 340:    */     
/* 341:    */     private CompDiagMatrixIterator() {}
/* 342:    */     
/* 343:    */     public boolean hasNext()
/* 344:    */     {
/* 345:403 */       return this.diagonal < CompDiagMatrix.this.diag.length;
/* 346:    */     }
/* 347:    */     
/* 348:    */     public MatrixEntry next()
/* 349:    */     {
/* 350:407 */       this.entry.update(this.diagonal, this.index);
/* 351:410 */       if (this.index < CompDiagMatrix.this.diag[this.diagonal].length - 1)
/* 352:    */       {
/* 353:411 */         this.index += 1;
/* 354:    */       }
/* 355:    */       else
/* 356:    */       {
/* 357:415 */         this.diagonal += 1;
/* 358:416 */         this.index = 0;
/* 359:    */       }
/* 360:419 */       return this.entry;
/* 361:    */     }
/* 362:    */     
/* 363:    */     public void remove()
/* 364:    */     {
/* 365:423 */       this.entry.set(0.0D);
/* 366:    */     }
/* 367:    */   }
/* 368:    */   
/* 369:    */   private class CompDiagMatrixEntry
/* 370:    */     implements MatrixEntry
/* 371:    */   {
/* 372:    */     private int diagonal;
/* 373:    */     private int index;
/* 374:    */     
/* 375:    */     private CompDiagMatrixEntry() {}
/* 376:    */     
/* 377:    */     public void update(int diagonal, int index)
/* 378:    */     {
/* 379:436 */       this.diagonal = diagonal;
/* 380:437 */       this.index = index;
/* 381:    */     }
/* 382:    */     
/* 383:    */     public int row()
/* 384:    */     {
/* 385:441 */       return this.index + (CompDiagMatrix.this.ind[this.diagonal] < 0 ? -CompDiagMatrix.this.ind[this.diagonal] : 0);
/* 386:    */     }
/* 387:    */     
/* 388:    */     public int column()
/* 389:    */     {
/* 390:445 */       return this.index + (CompDiagMatrix.this.ind[this.diagonal] > 0 ? CompDiagMatrix.this.ind[this.diagonal] : 0);
/* 391:    */     }
/* 392:    */     
/* 393:    */     public double get()
/* 394:    */     {
/* 395:449 */       return CompDiagMatrix.this.diag[this.diagonal][this.index];
/* 396:    */     }
/* 397:    */     
/* 398:    */     public void set(double value)
/* 399:    */     {
/* 400:453 */       CompDiagMatrix.this.diag[this.diagonal][this.index] = value;
/* 401:    */     }
/* 402:    */   }
/* 403:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.CompDiagMatrix
 * JD-Core Version:    0.7.0.1
 */