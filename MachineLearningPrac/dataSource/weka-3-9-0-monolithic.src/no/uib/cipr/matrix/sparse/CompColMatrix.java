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
/*  20:    */ public class CompColMatrix
/*  21:    */   extends AbstractMatrix
/*  22:    */ {
/*  23:    */   double[] data;
/*  24:    */   int[] columnPointer;
/*  25:    */   int[] rowIndex;
/*  26:    */   
/*  27:    */   public CompColMatrix(MatrixVectorReader r)
/*  28:    */     throws IOException
/*  29:    */   {
/*  30: 68 */     super(0, 0);
/*  31:    */     
/*  32:    */ 
/*  33:    */ 
/*  34: 72 */     MatrixInfo info = null;
/*  35: 73 */     if (r.hasInfo()) {
/*  36: 74 */       info = r.readMatrixInfo();
/*  37:    */     } else {
/*  38: 76 */       info = new MatrixInfo(true, MatrixInfo.MatrixField.Real, MatrixInfo.MatrixSymmetry.General);
/*  39:    */     }
/*  40: 80 */     if (info.isPattern()) {
/*  41: 81 */       throw new UnsupportedOperationException("Pattern matrices are not supported");
/*  42:    */     }
/*  43: 83 */     if (info.isDense()) {
/*  44: 84 */       throw new UnsupportedOperationException("Dense matrices are not supported");
/*  45:    */     }
/*  46: 86 */     if (info.isComplex()) {
/*  47: 87 */       throw new UnsupportedOperationException("Complex matrices are not supported");
/*  48:    */     }
/*  49: 91 */     MatrixSize size = r.readMatrixSize(info);
/*  50: 92 */     this.numRows = size.numRows();
/*  51: 93 */     this.numColumns = size.numColumns();
/*  52:    */     
/*  53:    */ 
/*  54: 96 */     int numEntries = size.numEntries();
/*  55: 97 */     int[] row = new int[numEntries];
/*  56: 98 */     int[] column = new int[numEntries];
/*  57: 99 */     double[] entry = new double[numEntries];
/*  58:100 */     r.readCoordinate(row, column, entry);
/*  59:    */     
/*  60:    */ 
/*  61:103 */     r.add(-1, row);
/*  62:104 */     r.add(-1, column);
/*  63:    */     
/*  64:    */ 
/*  65:107 */     List<Set<Integer>> cnz = new ArrayList(this.numColumns);
/*  66:108 */     for (int i = 0; i < this.numColumns; i++) {
/*  67:109 */       cnz.add(new HashSet());
/*  68:    */     }
/*  69:111 */     for (int i = 0; i < numEntries; i++) {
/*  70:112 */       ((Set)cnz.get(column[i])).add(Integer.valueOf(row[i]));
/*  71:    */     }
/*  72:115 */     if ((info.isSymmetric()) || (info.isSkewSymmetric())) {
/*  73:116 */       for (int i = 0; i < numEntries; i++) {
/*  74:117 */         if (row[i] != column[i]) {
/*  75:118 */           ((Set)cnz.get(row[i])).add(Integer.valueOf(column[i]));
/*  76:    */         }
/*  77:    */       }
/*  78:    */     }
/*  79:120 */     int[][] nz = new int[this.numColumns][];
/*  80:    */     int j;
/*  81:121 */     for (int i = 0; i < this.numColumns; i++)
/*  82:    */     {
/*  83:122 */       nz[i] = new int[((Set)cnz.get(i)).size()];
/*  84:123 */       j = 0;
/*  85:124 */       for (Integer rowind : (Set)cnz.get(i)) {
/*  86:125 */         nz[i][(j++)] = rowind.intValue();
/*  87:    */       }
/*  88:    */     }
/*  89:129 */     construct(nz);
/*  90:132 */     for (int i = 0; i < size.numEntries(); i++) {
/*  91:133 */       set(row[i], column[i], entry[i]);
/*  92:    */     }
/*  93:136 */     if (info.isSymmetric()) {
/*  94:137 */       for (int i = 0; i < numEntries; i++) {
/*  95:138 */         if (row[i] != column[i]) {
/*  96:139 */           set(column[i], row[i], entry[i]);
/*  97:    */         }
/*  98:    */       }
/*  99:141 */     } else if (info.isSkewSymmetric()) {
/* 100:142 */       for (int i = 0; i < numEntries; i++) {
/* 101:143 */         if (row[i] != column[i]) {
/* 102:144 */           set(column[i], row[i], -entry[i]);
/* 103:    */         }
/* 104:    */       }
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public CompColMatrix(int numRows, int numColumns, int[][] nz)
/* 109:    */   {
/* 110:159 */     super(numRows, numColumns);
/* 111:160 */     construct(nz);
/* 112:    */   }
/* 113:    */   
/* 114:    */   private void construct(int[][] nz)
/* 115:    */   {
/* 116:164 */     int nnz = 0;
/* 117:165 */     for (int i = 0; i < nz.length; i++) {
/* 118:166 */       nnz += nz[i].length;
/* 119:    */     }
/* 120:168 */     this.columnPointer = new int[this.numColumns + 1];
/* 121:169 */     this.rowIndex = new int[nnz];
/* 122:170 */     this.data = new double[nnz];
/* 123:172 */     if (nz.length != this.numColumns) {
/* 124:173 */       throw new IllegalArgumentException("nz.length != numColumns");
/* 125:    */     }
/* 126:175 */     for (int i = 1; i <= this.numColumns; i++)
/* 127:    */     {
/* 128:176 */       this.columnPointer[i] = (this.columnPointer[(i - 1)] + nz[(i - 1)].length);
/* 129:    */       
/* 130:178 */       int j = this.columnPointer[(i - 1)];
/* 131:178 */       for (int k = 0; j < this.columnPointer[i]; k++)
/* 132:    */       {
/* 133:179 */         this.rowIndex[j] = nz[(i - 1)][k];
/* 134:180 */         if ((nz[(i - 1)][k] < 0) || (nz[(i - 1)][k] >= this.numRows)) {
/* 135:181 */           throw new IllegalArgumentException("nz[" + (i - 1) + "][" + k + "]=" + nz[(i - 1)][k] + ", which is not a valid row index");
/* 136:    */         }
/* 137:178 */         j++;
/* 138:    */       }
/* 139:186 */       java.util.Arrays.sort(this.rowIndex, this.columnPointer[(i - 1)], this.columnPointer[i]);
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   private void construct(Matrix A, boolean deep)
/* 144:    */   {
/* 145:191 */     if (deep)
/* 146:    */     {
/* 147:192 */       if ((A instanceof CompColMatrix))
/* 148:    */       {
/* 149:193 */         CompColMatrix Ac = (CompColMatrix)A;
/* 150:194 */         this.data = new double[Ac.data.length];
/* 151:195 */         this.columnPointer = new int[Ac.columnPointer.length];
/* 152:196 */         this.rowIndex = new int[Ac.rowIndex.length];
/* 153:    */         
/* 154:198 */         System.arraycopy(Ac.data, 0, this.data, 0, this.data.length);
/* 155:199 */         System.arraycopy(Ac.columnPointer, 0, this.columnPointer, 0, this.columnPointer.length);
/* 156:    */         
/* 157:201 */         System.arraycopy(Ac.rowIndex, 0, this.rowIndex, 0, this.rowIndex.length);
/* 158:    */       }
/* 159:    */       else
/* 160:    */       {
/* 161:204 */         List<Set<Integer>> cnz = new ArrayList(this.numColumns);
/* 162:205 */         for (int i = 0; i < this.numColumns; i++) {
/* 163:206 */           cnz.add(new HashSet());
/* 164:    */         }
/* 165:208 */         for (MatrixEntry e : A) {
/* 166:209 */           ((Set)cnz.get(e.column())).add(Integer.valueOf(e.row()));
/* 167:    */         }
/* 168:211 */         int[][] nz = new int[this.numColumns][];
/* 169:    */         int j;
/* 170:212 */         for (int i = 0; i < this.numColumns; i++)
/* 171:    */         {
/* 172:213 */           nz[i] = new int[((Set)cnz.get(i)).size()];
/* 173:214 */           j = 0;
/* 174:215 */           for (Integer rowind : (Set)cnz.get(i)) {
/* 175:216 */             nz[i][(j++)] = rowind.intValue();
/* 176:    */           }
/* 177:    */         }
/* 178:219 */         construct(nz);
/* 179:220 */         set(A);
/* 180:    */       }
/* 181:    */     }
/* 182:    */     else
/* 183:    */     {
/* 184:224 */       CompColMatrix Ac = (CompColMatrix)A;
/* 185:225 */       this.columnPointer = Ac.getColumnPointers();
/* 186:226 */       this.rowIndex = Ac.getRowIndices();
/* 187:227 */       this.data = Ac.getData();
/* 188:    */     }
/* 189:    */   }
/* 190:    */   
/* 191:    */   public CompColMatrix(Matrix A, boolean deep)
/* 192:    */   {
/* 193:241 */     super(A);
/* 194:242 */     construct(A, deep);
/* 195:    */   }
/* 196:    */   
/* 197:    */   public CompColMatrix(Matrix A)
/* 198:    */   {
/* 199:252 */     this(A, true);
/* 200:    */   }
/* 201:    */   
/* 202:    */   public int[] getColumnPointers()
/* 203:    */   {
/* 204:259 */     return this.columnPointer;
/* 205:    */   }
/* 206:    */   
/* 207:    */   public int[] getRowIndices()
/* 208:    */   {
/* 209:266 */     return this.rowIndex;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public double[] getData()
/* 213:    */   {
/* 214:273 */     return this.data;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public Vector multAdd(double alpha, Vector x, Vector y)
/* 218:    */   {
/* 219:278 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/* 220:279 */       return super.multAdd(alpha, x, y);
/* 221:    */     }
/* 222:281 */     checkMultAdd(x, y);
/* 223:    */     
/* 224:283 */     double[] xd = ((DenseVector)x).getData();
/* 225:284 */     double[] yd = ((DenseVector)y).getData();
/* 226:    */     
/* 227:    */ 
/* 228:287 */     y.scale(1.0D / alpha);
/* 229:290 */     for (int i = 0; i < this.numColumns; i++) {
/* 230:291 */       for (int j = this.columnPointer[i]; j < this.columnPointer[(i + 1)]; j++) {
/* 231:292 */         yd[this.rowIndex[j]] += this.data[j] * xd[i];
/* 232:    */       }
/* 233:    */     }
/* 234:295 */     return y.scale(alpha);
/* 235:    */   }
/* 236:    */   
/* 237:    */   public Vector transMult(Vector x, Vector y)
/* 238:    */   {
/* 239:300 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/* 240:301 */       return super.transMult(x, y);
/* 241:    */     }
/* 242:303 */     checkTransMultAdd(x, y);
/* 243:    */     
/* 244:305 */     double[] xd = ((DenseVector)x).getData();
/* 245:306 */     double[] yd = ((DenseVector)y).getData();
/* 246:308 */     for (int i = 0; i < this.numColumns; i++)
/* 247:    */     {
/* 248:309 */       double dot = 0.0D;
/* 249:310 */       for (int j = this.columnPointer[i]; j < this.columnPointer[(i + 1)]; j++) {
/* 250:311 */         dot += this.data[j] * xd[this.rowIndex[j]];
/* 251:    */       }
/* 252:312 */       yd[i] = dot;
/* 253:    */     }
/* 254:315 */     return y;
/* 255:    */   }
/* 256:    */   
/* 257:    */   public Vector transMultAdd(double alpha, Vector x, Vector y)
/* 258:    */   {
/* 259:320 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/* 260:321 */       return super.transMultAdd(alpha, x, y);
/* 261:    */     }
/* 262:323 */     checkTransMultAdd(x, y);
/* 263:    */     
/* 264:325 */     double[] xd = ((DenseVector)x).getData();
/* 265:326 */     double[] yd = ((DenseVector)y).getData();
/* 266:328 */     for (int i = 0; i < this.numColumns; i++)
/* 267:    */     {
/* 268:329 */       double dot = 0.0D;
/* 269:330 */       for (int j = this.columnPointer[i]; j < this.columnPointer[(i + 1)]; j++) {
/* 270:331 */         dot += this.data[j] * xd[this.rowIndex[j]];
/* 271:    */       }
/* 272:332 */       yd[i] += alpha * dot;
/* 273:    */     }
/* 274:335 */     return y;
/* 275:    */   }
/* 276:    */   
/* 277:    */   public void set(int row, int column, double value)
/* 278:    */   {
/* 279:340 */     check(row, column);
/* 280:    */     
/* 281:342 */     int index = getIndex(row, column);
/* 282:343 */     this.data[index] = value;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public void add(int row, int column, double value)
/* 286:    */   {
/* 287:348 */     check(row, column);
/* 288:    */     
/* 289:350 */     int index = getIndex(row, column);
/* 290:351 */     this.data[index] += value;
/* 291:    */   }
/* 292:    */   
/* 293:    */   public double get(int row, int column)
/* 294:    */   {
/* 295:356 */     check(row, column);
/* 296:    */     
/* 297:358 */     int index = Arrays.binarySearch(this.rowIndex, row, this.columnPointer[column], this.columnPointer[(column + 1)]);
/* 298:361 */     if (index >= 0) {
/* 299:362 */       return this.data[index];
/* 300:    */     }
/* 301:364 */     return 0.0D;
/* 302:    */   }
/* 303:    */   
/* 304:    */   private int getIndex(int row, int column)
/* 305:    */   {
/* 306:371 */     int i = Arrays.binarySearch(this.rowIndex, row, this.columnPointer[column], this.columnPointer[(column + 1)]);
/* 307:374 */     if ((i != -1) && (this.rowIndex[i] == row)) {
/* 308:375 */       return i;
/* 309:    */     }
/* 310:377 */     throw new IndexOutOfBoundsException("Entry (" + (row + 1) + ", " + (column + 1) + ") is not in the matrix structure");
/* 311:    */   }
/* 312:    */   
/* 313:    */   public CompColMatrix copy()
/* 314:    */   {
/* 315:383 */     return new CompColMatrix(this);
/* 316:    */   }
/* 317:    */   
/* 318:    */   public Iterator<MatrixEntry> iterator()
/* 319:    */   {
/* 320:388 */     return new CompColMatrixIterator();
/* 321:    */   }
/* 322:    */   
/* 323:    */   public CompColMatrix zero()
/* 324:    */   {
/* 325:393 */     java.util.Arrays.fill(this.data, 0.0D);
/* 326:394 */     return this;
/* 327:    */   }
/* 328:    */   
/* 329:    */   private class CompColMatrixIterator
/* 330:    */     implements Iterator<MatrixEntry>
/* 331:    */   {
/* 332:    */     private int column;
/* 333:    */     private int cursor;
/* 334:404 */     private CompColMatrix.CompColMatrixEntry entry = new CompColMatrix.CompColMatrixEntry(CompColMatrix.this, null);
/* 335:    */     
/* 336:    */     public CompColMatrixIterator()
/* 337:    */     {
/* 338:408 */       nextNonEmptyColumn();
/* 339:    */     }
/* 340:    */     
/* 341:    */     private void nextNonEmptyColumn()
/* 342:    */     {
/* 343:416 */       while ((this.column < CompColMatrix.this.numColumns()) && (CompColMatrix.this.columnPointer[this.column] == CompColMatrix.this.columnPointer[(this.column + 1)])) {
/* 344:418 */         this.column += 1;
/* 345:    */       }
/* 346:419 */       this.cursor = CompColMatrix.this.columnPointer[this.column];
/* 347:    */     }
/* 348:    */     
/* 349:    */     public boolean hasNext()
/* 350:    */     {
/* 351:423 */       return this.cursor < CompColMatrix.this.data.length;
/* 352:    */     }
/* 353:    */     
/* 354:    */     public MatrixEntry next()
/* 355:    */     {
/* 356:427 */       this.entry.update(this.column, this.cursor);
/* 357:430 */       if (this.cursor < CompColMatrix.this.columnPointer[(this.column + 1)] - 1)
/* 358:    */       {
/* 359:431 */         this.cursor += 1;
/* 360:    */       }
/* 361:    */       else
/* 362:    */       {
/* 363:435 */         this.column += 1;
/* 364:436 */         nextNonEmptyColumn();
/* 365:    */       }
/* 366:439 */       return this.entry;
/* 367:    */     }
/* 368:    */     
/* 369:    */     public void remove()
/* 370:    */     {
/* 371:443 */       this.entry.set(0.0D);
/* 372:    */     }
/* 373:    */   }
/* 374:    */   
/* 375:    */   private class CompColMatrixEntry
/* 376:    */     implements MatrixEntry
/* 377:    */   {
/* 378:    */     private int column;
/* 379:    */     private int cursor;
/* 380:    */     
/* 381:    */     private CompColMatrixEntry() {}
/* 382:    */     
/* 383:    */     public void update(int column, int cursor)
/* 384:    */     {
/* 385:459 */       this.column = column;
/* 386:460 */       this.cursor = cursor;
/* 387:    */     }
/* 388:    */     
/* 389:    */     public int row()
/* 390:    */     {
/* 391:464 */       return CompColMatrix.this.rowIndex[this.cursor];
/* 392:    */     }
/* 393:    */     
/* 394:    */     public int column()
/* 395:    */     {
/* 396:468 */       return this.column;
/* 397:    */     }
/* 398:    */     
/* 399:    */     public double get()
/* 400:    */     {
/* 401:472 */       return CompColMatrix.this.data[this.cursor];
/* 402:    */     }
/* 403:    */     
/* 404:    */     public void set(double value)
/* 405:    */     {
/* 406:476 */       CompColMatrix.this.data[this.cursor] = value;
/* 407:    */     }
/* 408:    */   }
/* 409:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.CompColMatrix
 * JD-Core Version:    0.7.0.1
 */