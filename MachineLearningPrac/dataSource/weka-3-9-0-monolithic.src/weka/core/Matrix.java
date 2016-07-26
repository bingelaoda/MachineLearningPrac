/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.FileReader;
/*   4:    */ import java.io.FileWriter;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.Reader;
/*   7:    */ import java.io.Serializable;
/*   8:    */ import java.io.Writer;
/*   9:    */ import weka.core.matrix.EigenvalueDecomposition;
/*  10:    */ import weka.core.matrix.LUDecomposition;
/*  11:    */ import weka.core.matrix.LinearRegression;
/*  12:    */ import weka.core.matrix.Maths;
/*  13:    */ 
/*  14:    */ @Deprecated
/*  15:    */ public class Matrix
/*  16:    */   implements Cloneable, Serializable, RevisionHandler
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -3604757095849145838L;
/*  19: 52 */   protected weka.core.matrix.Matrix m_Matrix = null;
/*  20:    */   
/*  21:    */   public Matrix(int nr, int nc)
/*  22:    */   {
/*  23: 61 */     this.m_Matrix = new weka.core.matrix.Matrix(nr, nc);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public Matrix(double[][] array)
/*  27:    */     throws Exception
/*  28:    */   {
/*  29: 70 */     this.m_Matrix = new weka.core.matrix.Matrix(array);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Matrix(Reader r)
/*  33:    */     throws Exception
/*  34:    */   {
/*  35: 82 */     this.m_Matrix = new weka.core.matrix.Matrix(r);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Object clone()
/*  39:    */   {
/*  40:    */     try
/*  41:    */     {
/*  42: 94 */       return new Matrix(this.m_Matrix.getArrayCopy());
/*  43:    */     }
/*  44:    */     catch (Exception e)
/*  45:    */     {
/*  46: 96 */       e.printStackTrace();
/*  47:    */     }
/*  48: 97 */     return null;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void write(Writer w)
/*  52:    */     throws Exception
/*  53:    */   {
/*  54:108 */     this.m_Matrix.write(w);
/*  55:    */   }
/*  56:    */   
/*  57:    */   protected weka.core.matrix.Matrix getMatrix()
/*  58:    */   {
/*  59:117 */     return this.m_Matrix;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public final double getElement(int rowIndex, int columnIndex)
/*  63:    */   {
/*  64:128 */     return this.m_Matrix.get(rowIndex, columnIndex);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public final void addElement(int rowIndex, int columnIndex, double value)
/*  68:    */   {
/*  69:139 */     this.m_Matrix.set(rowIndex, columnIndex, this.m_Matrix.get(rowIndex, columnIndex) + value);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public final int numRows()
/*  73:    */   {
/*  74:149 */     return this.m_Matrix.getRowDimension();
/*  75:    */   }
/*  76:    */   
/*  77:    */   public final int numColumns()
/*  78:    */   {
/*  79:158 */     return this.m_Matrix.getColumnDimension();
/*  80:    */   }
/*  81:    */   
/*  82:    */   public final void setElement(int rowIndex, int columnIndex, double value)
/*  83:    */   {
/*  84:169 */     this.m_Matrix.set(rowIndex, columnIndex, value);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public final void setRow(int index, double[] newRow)
/*  88:    */   {
/*  89:179 */     for (int i = 0; i < newRow.length; i++) {
/*  90:180 */       this.m_Matrix.set(index, i, newRow[i]);
/*  91:    */     }
/*  92:    */   }
/*  93:    */   
/*  94:    */   public double[] getRow(int index)
/*  95:    */   {
/*  96:191 */     double[] newRow = new double[numColumns()];
/*  97:192 */     for (int i = 0; i < newRow.length; i++) {
/*  98:193 */       newRow[i] = getElement(index, i);
/*  99:    */     }
/* 100:196 */     return newRow;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public double[] getColumn(int index)
/* 104:    */   {
/* 105:206 */     double[] newColumn = new double[numRows()];
/* 106:207 */     for (int i = 0; i < newColumn.length; i++) {
/* 107:208 */       newColumn[i] = getElement(i, index);
/* 108:    */     }
/* 109:211 */     return newColumn;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public final void setColumn(int index, double[] newColumn)
/* 113:    */   {
/* 114:221 */     for (int i = 0; i < numRows(); i++) {
/* 115:222 */       this.m_Matrix.set(i, index, newColumn[i]);
/* 116:    */     }
/* 117:    */   }
/* 118:    */   
/* 119:    */   public String toString()
/* 120:    */   {
/* 121:233 */     return this.m_Matrix.toString();
/* 122:    */   }
/* 123:    */   
/* 124:    */   public final Matrix add(Matrix other)
/* 125:    */   {
/* 126:    */     try
/* 127:    */     {
/* 128:243 */       return new Matrix(this.m_Matrix.plus(other.getMatrix()).getArrayCopy());
/* 129:    */     }
/* 130:    */     catch (Exception e)
/* 131:    */     {
/* 132:245 */       e.printStackTrace();
/* 133:    */     }
/* 134:246 */     return null;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public final Matrix transpose()
/* 138:    */   {
/* 139:    */     try
/* 140:    */     {
/* 141:257 */       return new Matrix(this.m_Matrix.transpose().getArrayCopy());
/* 142:    */     }
/* 143:    */     catch (Exception e)
/* 144:    */     {
/* 145:259 */       e.printStackTrace();
/* 146:    */     }
/* 147:260 */     return null;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public boolean isSymmetric()
/* 151:    */   {
/* 152:270 */     return this.m_Matrix.isSymmetric();
/* 153:    */   }
/* 154:    */   
/* 155:    */   public final Matrix multiply(Matrix b)
/* 156:    */   {
/* 157:    */     try
/* 158:    */     {
/* 159:281 */       return new Matrix(getMatrix().times(b.getMatrix()).getArrayCopy());
/* 160:    */     }
/* 161:    */     catch (Exception e)
/* 162:    */     {
/* 163:283 */       e.printStackTrace();
/* 164:    */     }
/* 165:284 */     return null;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public final double[] regression(Matrix y, double ridge)
/* 169:    */   {
/* 170:297 */     return getMatrix().regression(y.getMatrix(), ridge).getCoefficients();
/* 171:    */   }
/* 172:    */   
/* 173:    */   public final double[] regression(Matrix y, double[] w, double ridge)
/* 174:    */   {
/* 175:311 */     return getMatrix().regression(y.getMatrix(), w, ridge).getCoefficients();
/* 176:    */   }
/* 177:    */   
/* 178:    */   public Matrix getL()
/* 179:    */     throws Exception
/* 180:    */   {
/* 181:322 */     int nr = numRows();
/* 182:323 */     int nc = numColumns();
/* 183:324 */     double[][] ld = new double[nr][nc];
/* 184:326 */     for (int i = 0; i < nr; i++)
/* 185:    */     {
/* 186:327 */       for (int j = 0; (j < i) && (j < nc); j++) {
/* 187:328 */         ld[i][j] = getElement(i, j);
/* 188:    */       }
/* 189:330 */       if (i < nc) {
/* 190:331 */         ld[i][i] = 1.0D;
/* 191:    */       }
/* 192:    */     }
/* 193:334 */     Matrix l = new Matrix(ld);
/* 194:335 */     return l;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public Matrix getU()
/* 198:    */     throws Exception
/* 199:    */   {
/* 200:346 */     int nr = numRows();
/* 201:347 */     int nc = numColumns();
/* 202:348 */     double[][] ud = new double[nr][nc];
/* 203:350 */     for (int i = 0; i < nr; i++) {
/* 204:351 */       for (int j = i; j < nc; j++) {
/* 205:352 */         ud[i][j] = getElement(i, j);
/* 206:    */       }
/* 207:    */     }
/* 208:355 */     Matrix u = new Matrix(ud);
/* 209:356 */     return u;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public int[] LUDecomposition()
/* 213:    */     throws Exception
/* 214:    */   {
/* 215:367 */     LUDecomposition lu = this.m_Matrix.lu();
/* 216:370 */     if (!lu.isNonsingular()) {
/* 217:371 */       throw new Exception("Matrix is singular");
/* 218:    */     }
/* 219:374 */     weka.core.matrix.Matrix u = lu.getU();
/* 220:375 */     weka.core.matrix.Matrix l = lu.getL();
/* 221:    */     
/* 222:    */ 
/* 223:378 */     int nr = numRows();
/* 224:379 */     int nc = numColumns();
/* 225:380 */     for (int i = 0; i < nr; i++) {
/* 226:381 */       for (int j = 0; j < nc; j++) {
/* 227:382 */         if (j < i) {
/* 228:383 */           setElement(i, j, l.get(i, j));
/* 229:    */         } else {
/* 230:385 */           setElement(i, j, u.get(i, j));
/* 231:    */         }
/* 232:    */       }
/* 233:    */     }
/* 234:390 */     u = null;
/* 235:391 */     l = null;
/* 236:    */     
/* 237:393 */     return lu.getPivot();
/* 238:    */   }
/* 239:    */   
/* 240:    */   public void solve(double[] bb)
/* 241:    */     throws Exception
/* 242:    */   {
/* 243:405 */     weka.core.matrix.Matrix x = this.m_Matrix.solve(new weka.core.matrix.Matrix(bb, bb.length));
/* 244:    */     
/* 245:    */ 
/* 246:    */ 
/* 247:409 */     int nr = x.getRowDimension();
/* 248:410 */     for (int i = 0; i < nr; i++) {
/* 249:411 */       bb[i] = x.get(i, 0);
/* 250:    */     }
/* 251:    */   }
/* 252:    */   
/* 253:    */   public void eigenvalueDecomposition(double[][] V, double[] d)
/* 254:    */     throws Exception
/* 255:    */   {
/* 256:430 */     if (!isSymmetric()) {
/* 257:431 */       throw new Exception("EigenvalueDecomposition: Matrix must be symmetric.");
/* 258:    */     }
/* 259:435 */     EigenvalueDecomposition eig = this.m_Matrix.eig();
/* 260:436 */     weka.core.matrix.Matrix v = eig.getV();
/* 261:437 */     double[] d2 = eig.getRealEigenvalues();
/* 262:    */     
/* 263:    */ 
/* 264:440 */     int nr = numRows();
/* 265:441 */     int nc = numColumns();
/* 266:442 */     for (int i = 0; i < nr; i++) {
/* 267:443 */       for (int j = 0; j < nc; j++) {
/* 268:444 */         V[i][j] = v.get(i, j);
/* 269:    */       }
/* 270:    */     }
/* 271:448 */     for (int i = 0; i < d2.length; i++) {
/* 272:449 */       d[i] = d2[i];
/* 273:    */     }
/* 274:    */   }
/* 275:    */   
/* 276:    */   protected static double hypot(double a, double b)
/* 277:    */   {
/* 278:461 */     return Maths.hypot(a, b);
/* 279:    */   }
/* 280:    */   
/* 281:    */   public String toMatlab()
/* 282:    */   {
/* 283:472 */     return getMatrix().toMatlab();
/* 284:    */   }
/* 285:    */   
/* 286:    */   public static Matrix parseMatlab(String matlab)
/* 287:    */     throws Exception
/* 288:    */   {
/* 289:483 */     return new Matrix(weka.core.matrix.Matrix.parseMatlab(matlab).getArray());
/* 290:    */   }
/* 291:    */   
/* 292:    */   public String getRevision()
/* 293:    */   {
/* 294:493 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 295:    */   }
/* 296:    */   
/* 297:    */   public static void main(String[] ops)
/* 298:    */   {
/* 299:501 */     double[] first = { 2.3D, 1.2D, 5.0D };
/* 300:502 */     double[] second = { 5.2D, 1.4D, 9.0D };
/* 301:503 */     double[] response = { 4.0D, 7.0D, 8.0D };
/* 302:504 */     double[] weights = { 1.0D, 2.0D, 3.0D };
/* 303:    */     try
/* 304:    */     {
/* 305:508 */       double[][] m = { { 1.0D, 2.0D, 3.0D }, { 2.0D, 5.0D, 6.0D }, { 3.0D, 6.0D, 9.0D } };
/* 306:509 */       Matrix M = new Matrix(m);
/* 307:510 */       int n = M.numRows();
/* 308:511 */       double[][] V = new double[n][n];
/* 309:512 */       double[] d = new double[n];
/* 310:513 */       M.eigenvalueDecomposition(V, d);
/* 311:    */       
/* 312:515 */       Matrix a = new Matrix(2, 3);
/* 313:516 */       Matrix b = new Matrix(3, 2);
/* 314:517 */       System.out.println("Number of columns for a: " + a.numColumns());
/* 315:518 */       System.out.println("Number of rows for a: " + a.numRows());
/* 316:519 */       a.setRow(0, first);
/* 317:520 */       a.setRow(1, second);
/* 318:521 */       b.setColumn(0, first);
/* 319:522 */       b.setColumn(1, second);
/* 320:523 */       System.out.println("a:\n " + a);
/* 321:524 */       System.out.println("b:\n " + b);
/* 322:525 */       System.out.println("a (0, 0): " + a.getElement(0, 0));
/* 323:526 */       System.out.println("a transposed:\n " + a.transpose());
/* 324:527 */       System.out.println("a * b:\n " + a.multiply(b));
/* 325:528 */       Matrix r = new Matrix(3, 1);
/* 326:529 */       r.setColumn(0, response);
/* 327:530 */       System.out.println("r:\n " + r);
/* 328:531 */       System.out.println("Coefficients of regression of b on r: ");
/* 329:532 */       double[] coefficients = b.regression(r, 1.0E-008D);
/* 330:533 */       for (double coefficient : coefficients) {
/* 331:534 */         System.out.print(coefficient + " ");
/* 332:    */       }
/* 333:536 */       System.out.println();
/* 334:537 */       System.out.println("Weights: ");
/* 335:538 */       for (double weight : weights) {
/* 336:539 */         System.out.print(weight + " ");
/* 337:    */       }
/* 338:541 */       System.out.println();
/* 339:542 */       System.out.println("Coefficients of weighted regression of b on r: ");
/* 340:543 */       coefficients = b.regression(r, weights, 1.0E-008D);
/* 341:544 */       for (double coefficient : coefficients) {
/* 342:545 */         System.out.print(coefficient + " ");
/* 343:    */       }
/* 344:547 */       System.out.println();
/* 345:548 */       a.setElement(0, 0, 6.0D);
/* 346:549 */       System.out.println("a with (0, 0) set to 6:\n " + a);
/* 347:550 */       a.write(new FileWriter("main.matrix"));
/* 348:551 */       System.out.println("wrote matrix to \"main.matrix\"\n" + a);
/* 349:552 */       a = new Matrix(new FileReader("main.matrix"));
/* 350:553 */       System.out.println("read matrix from \"main.matrix\"\n" + a);
/* 351:    */     }
/* 352:    */     catch (Exception e)
/* 353:    */     {
/* 354:555 */       e.printStackTrace();
/* 355:    */     }
/* 356:    */   }
/* 357:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Matrix
 * JD-Core Version:    0.7.0.1
 */