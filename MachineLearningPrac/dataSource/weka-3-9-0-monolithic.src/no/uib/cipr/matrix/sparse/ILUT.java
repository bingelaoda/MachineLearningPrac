/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.List;
/*   6:    */ import no.uib.cipr.matrix.AbstractMatrix;
/*   7:    */ import no.uib.cipr.matrix.DenseVector;
/*   8:    */ import no.uib.cipr.matrix.Matrix;
/*   9:    */ import no.uib.cipr.matrix.Vector;
/*  10:    */ import no.uib.cipr.matrix.Vector.Norm;
/*  11:    */ import no.uib.cipr.matrix.VectorEntry;
/*  12:    */ 
/*  13:    */ public class ILUT
/*  14:    */   implements Preconditioner
/*  15:    */ {
/*  16:    */   private final FlexCompRowMatrix LU;
/*  17:    */   private Matrix L;
/*  18:    */   private Matrix U;
/*  19:    */   private final Vector y;
/*  20:    */   private final double tau;
/*  21:    */   private final int[] diagind;
/*  22:    */   private final List<IntDoubleEntry> lower;
/*  23:    */   private final List<IntDoubleEntry> upper;
/*  24:    */   private final int p;
/*  25:    */   
/*  26:    */   public ILUT(FlexCompRowMatrix LU, double tau, int p)
/*  27:    */   {
/*  28: 92 */     if (!LU.isSquare()) {
/*  29: 93 */       throw new IllegalArgumentException("ILU only applies to square matrices");
/*  30:    */     }
/*  31: 96 */     this.LU = LU;
/*  32: 97 */     this.tau = tau;
/*  33: 98 */     this.p = p;
/*  34:    */     
/*  35:100 */     int n = LU.numRows();
/*  36:101 */     this.lower = new ArrayList(n);
/*  37:102 */     this.upper = new ArrayList(n);
/*  38:103 */     this.y = new DenseVector(n);
/*  39:104 */     this.diagind = new int[n];
/*  40:    */   }
/*  41:    */   
/*  42:    */   public ILUT(FlexCompRowMatrix LU)
/*  43:    */   {
/*  44:117 */     this(LU, 1.0E-006D, 25);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Vector apply(Vector b, Vector x)
/*  48:    */   {
/*  49:122 */     this.L.solve(b, this.y);
/*  50:    */     
/*  51:    */ 
/*  52:125 */     return this.U.solve(this.y, x);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public Vector transApply(Vector b, Vector x)
/*  56:    */   {
/*  57:130 */     this.U.transSolve(b, this.y);
/*  58:    */     
/*  59:    */ 
/*  60:133 */     return this.L.transSolve(this.y, x);
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setMatrix(Matrix A)
/*  64:    */   {
/*  65:137 */     this.LU.set(A);
/*  66:138 */     this.LU.compact();
/*  67:    */     
/*  68:140 */     factor();
/*  69:    */   }
/*  70:    */   
/*  71:    */   private void factor()
/*  72:    */   {
/*  73:144 */     int n = this.LU.numRows();
/*  74:    */     
/*  75:146 */     double[] LUi = new double[n];
/*  76:149 */     for (int k = 0; k < n; k++)
/*  77:    */     {
/*  78:150 */       SparseVector row = this.LU.getRow(k);
/*  79:151 */       this.diagind[k] = findDiagonalIndex(row, k);
/*  80:152 */       if (this.diagind[k] < 0) {
/*  81:153 */         throw new RuntimeException("Missing diagonal entry on row " + (k + 1));
/*  82:    */       }
/*  83:    */     }
/*  84:157 */     for (int i = 1; i < n; i++)
/*  85:    */     {
/*  86:160 */       SparseVector rowi = this.LU.getRow(i);
/*  87:    */       
/*  88:    */ 
/*  89:163 */       double taui = rowi.norm(Vector.Norm.Two) * this.tau;
/*  90:    */       
/*  91:    */ 
/*  92:166 */       scatter(rowi, LUi);
/*  93:168 */       for (int k = 0; k < i; k++)
/*  94:    */       {
/*  95:171 */         SparseVector rowk = this.LU.getRow(k);
/*  96:172 */         int[] rowIndex = rowk.getIndex();
/*  97:173 */         int rowUsed = rowk.getUsed();
/*  98:174 */         double[] rowData = rowk.getData();
/*  99:176 */         if (rowData[this.diagind[k]] == 0.0D) {
/* 100:177 */           throw new RuntimeException("Zero diagonal entry on row " + (k + 1) + " during ILU process");
/* 101:    */         }
/* 102:180 */         double LUik = LUi[k] / rowData[this.diagind[k]];
/* 103:183 */         if (Math.abs(LUik) > taui)
/* 104:    */         {
/* 105:187 */           for (int j = this.diagind[k] + 1; j < rowUsed; j++) {
/* 106:188 */             LUi[rowIndex[j]] -= LUik * rowData[j];
/* 107:    */           }
/* 108:191 */           LUi[k] = LUik;
/* 109:    */         }
/* 110:    */       }
/* 111:195 */       gather(LUi, rowi, taui, i);
/* 112:    */       
/* 113:    */ 
/* 114:198 */       int diagIndex = this.diagind[i];
/* 115:199 */       int[] rowiIndices = rowi.getIndex();
/* 116:200 */       if ((diagIndex >= rowiIndices.length) || (rowiIndices[diagIndex] != i))
/* 117:    */       {
/* 118:201 */         this.diagind[i] = findDiagonalIndex(rowi, i);
/* 119:202 */         if (this.diagind[i] < 0) {
/* 120:203 */           throw new RuntimeException("Missing diagonal entry on row " + (i + 1) + " during ILU process");
/* 121:    */         }
/* 122:    */       }
/* 123:    */     }
/* 124:208 */     this.L = new UnitLowerFlexCompRowMatrix(this.LU, this.diagind);
/* 125:209 */     this.U = new UpperFlexCompRowMatrix(this.LU, this.diagind);
/* 126:    */   }
/* 127:    */   
/* 128:    */   private int findDiagonalIndex(SparseVector v, int k)
/* 129:    */   {
/* 130:213 */     return Arrays.binarySearch(v.getIndex(), k, 0, v
/* 131:214 */       .getUsed());
/* 132:    */   }
/* 133:    */   
/* 134:    */   private void scatter(SparseVector v, double[] z)
/* 135:    */   {
/* 136:221 */     int[] index = v.getIndex();
/* 137:222 */     int used = v.getUsed();
/* 138:223 */     double[] data = v.getData();
/* 139:224 */     java.util.Arrays.fill(z, 0.0D);
/* 140:225 */     for (int i = 0; i < used; i++) {
/* 141:226 */       z[index[i]] = data[i];
/* 142:    */     }
/* 143:    */   }
/* 144:    */   
/* 145:    */   private void gather(double[] z, SparseVector v, double taui, int d)
/* 146:    */   {
/* 147:235 */     int nl = 0;int nu = 0;
/* 148:236 */     for (VectorEntry e : v) {
/* 149:237 */       if (e.index() < d) {
/* 150:238 */         nl++;
/* 151:239 */       } else if (e.index() > d) {
/* 152:240 */         nu++;
/* 153:    */       }
/* 154:    */     }
/* 155:242 */     v.zero();
/* 156:    */     
/* 157:    */ 
/* 158:245 */     this.lower.clear();
/* 159:246 */     for (int i = 0; i < d; i++) {
/* 160:247 */       if (Math.abs(z[i]) > taui) {
/* 161:248 */         this.lower.add(new IntDoubleEntry(i, z[i]));
/* 162:    */       }
/* 163:    */     }
/* 164:251 */     this.upper.clear();
/* 165:252 */     for (int i = d + 1; i < z.length; i++) {
/* 166:253 */       if (Math.abs(z[i]) > taui) {
/* 167:254 */         this.upper.add(new IntDoubleEntry(i, z[i]));
/* 168:    */       }
/* 169:    */     }
/* 170:257 */     Collections.sort(this.lower);
/* 171:258 */     Collections.sort(this.upper);
/* 172:    */     
/* 173:    */ 
/* 174:261 */     v.set(d, z[d]);
/* 175:264 */     for (int i = 0; i < Math.min(nl + this.p, this.lower.size()); i++)
/* 176:    */     {
/* 177:265 */       IntDoubleEntry e = (IntDoubleEntry)this.lower.get(i);
/* 178:266 */       v.set(e.index, e.value);
/* 179:    */     }
/* 180:270 */     for (int i = 0; i < Math.min(nu + this.p, this.upper.size()); i++)
/* 181:    */     {
/* 182:271 */       IntDoubleEntry e = (IntDoubleEntry)this.upper.get(i);
/* 183:272 */       v.set(e.index, e.value);
/* 184:    */     }
/* 185:    */   }
/* 186:    */   
/* 187:    */   private static class IntDoubleEntry
/* 188:    */     implements Comparable<IntDoubleEntry>
/* 189:    */   {
/* 190:    */     public int index;
/* 191:    */     public double value;
/* 192:    */     
/* 193:    */     public IntDoubleEntry(int index, double value)
/* 194:    */     {
/* 195:287 */       this.index = index;
/* 196:288 */       this.value = value;
/* 197:    */     }
/* 198:    */     
/* 199:    */     public int compareTo(IntDoubleEntry o)
/* 200:    */     {
/* 201:293 */       if (Math.abs(this.value) < Math.abs(o.value)) {
/* 202:294 */         return 1;
/* 203:    */       }
/* 204:295 */       if (Math.abs(this.value) == Math.abs(o.value)) {
/* 205:296 */         return 0;
/* 206:    */       }
/* 207:298 */       return -1;
/* 208:    */     }
/* 209:    */     
/* 210:    */     public String toString()
/* 211:    */     {
/* 212:303 */       return "(" + this.index + "=" + this.value + ")";
/* 213:    */     }
/* 214:    */   }
/* 215:    */   
/* 216:    */   private static class UnitLowerFlexCompRowMatrix
/* 217:    */     extends AbstractMatrix
/* 218:    */   {
/* 219:    */     private final FlexCompRowMatrix LU;
/* 220:    */     private final int[] diagind;
/* 221:    */     
/* 222:    */     public UnitLowerFlexCompRowMatrix(FlexCompRowMatrix LU, int[] diagind)
/* 223:    */     {
/* 224:317 */       super();
/* 225:318 */       this.LU = LU;
/* 226:319 */       this.diagind = diagind;
/* 227:    */     }
/* 228:    */     
/* 229:    */     public Vector solve(Vector b, Vector x)
/* 230:    */     {
/* 231:324 */       if ((!(b instanceof DenseVector)) || (!(x instanceof DenseVector))) {
/* 232:325 */         return super.solve(b, x);
/* 233:    */       }
/* 234:327 */       double[] bd = ((DenseVector)b).getData();
/* 235:328 */       double[] xd = ((DenseVector)x).getData();
/* 236:330 */       for (int i = 0; i < this.numRows; i++)
/* 237:    */       {
/* 238:333 */         SparseVector row = this.LU.getRow(i);
/* 239:334 */         int[] index = row.getIndex();
/* 240:335 */         double[] data = row.getData();
/* 241:    */         
/* 242:    */ 
/* 243:338 */         double sum = 0.0D;
/* 244:339 */         for (int j = 0; j < this.diagind[i]; j++) {
/* 245:340 */           sum += data[j] * xd[index[j]];
/* 246:    */         }
/* 247:342 */         bd[i] -= sum;
/* 248:    */       }
/* 249:345 */       return x;
/* 250:    */     }
/* 251:    */     
/* 252:    */     public Vector transSolve(Vector b, Vector x)
/* 253:    */     {
/* 254:350 */       if (!(x instanceof DenseVector)) {
/* 255:351 */         return super.transSolve(b, x);
/* 256:    */       }
/* 257:353 */       x.set(b);
/* 258:    */       
/* 259:355 */       double[] xd = ((DenseVector)x).getData();
/* 260:357 */       for (int i = this.numRows - 1; i >= 0; i--)
/* 261:    */       {
/* 262:360 */         SparseVector row = this.LU.getRow(i);
/* 263:361 */         int[] index = row.getIndex();
/* 264:362 */         double[] data = row.getData();
/* 265:366 */         for (int j = 0; j < this.diagind[i]; j++) {
/* 266:367 */           xd[index[j]] -= data[j] * xd[i];
/* 267:    */         }
/* 268:    */       }
/* 269:371 */       return x;
/* 270:    */     }
/* 271:    */   }
/* 272:    */   
/* 273:    */   private static class UpperFlexCompRowMatrix
/* 274:    */     extends AbstractMatrix
/* 275:    */   {
/* 276:    */     private final FlexCompRowMatrix LU;
/* 277:    */     private final int[] diagind;
/* 278:    */     
/* 279:    */     public UpperFlexCompRowMatrix(FlexCompRowMatrix LU, int[] diagind)
/* 280:    */     {
/* 281:386 */       super();
/* 282:387 */       this.LU = LU;
/* 283:388 */       this.diagind = diagind;
/* 284:    */     }
/* 285:    */     
/* 286:    */     public Vector solve(Vector b, Vector x)
/* 287:    */     {
/* 288:393 */       if ((!(b instanceof DenseVector)) || (!(x instanceof DenseVector))) {
/* 289:394 */         return super.solve(b, x);
/* 290:    */       }
/* 291:396 */       double[] bd = ((DenseVector)b).getData();
/* 292:397 */       double[] xd = ((DenseVector)x).getData();
/* 293:399 */       for (int i = this.numRows - 1; i >= 0; i--)
/* 294:    */       {
/* 295:402 */         SparseVector row = this.LU.getRow(i);
/* 296:403 */         int[] index = row.getIndex();
/* 297:404 */         int used = row.getUsed();
/* 298:405 */         double[] data = row.getData();
/* 299:    */         
/* 300:    */ 
/* 301:408 */         double sum = 0.0D;
/* 302:409 */         for (int j = this.diagind[i] + 1; j < used; j++) {
/* 303:410 */           sum += data[j] * xd[index[j]];
/* 304:    */         }
/* 305:412 */         xd[i] = ((bd[i] - sum) / data[this.diagind[i]]);
/* 306:    */       }
/* 307:415 */       return x;
/* 308:    */     }
/* 309:    */     
/* 310:    */     public Vector transSolve(Vector b, Vector x)
/* 311:    */     {
/* 312:420 */       if (!(x instanceof DenseVector)) {
/* 313:421 */         return super.transSolve(b, x);
/* 314:    */       }
/* 315:423 */       x.set(b);
/* 316:    */       
/* 317:425 */       double[] xd = ((DenseVector)x).getData();
/* 318:427 */       for (int i = 0; i < this.numRows; i++)
/* 319:    */       {
/* 320:430 */         SparseVector row = this.LU.getRow(i);
/* 321:431 */         int[] index = row.getIndex();
/* 322:432 */         int used = row.getUsed();
/* 323:433 */         double[] data = row.getData();
/* 324:    */         
/* 325:    */ 
/* 326:436 */         xd[i] /= data[this.diagind[i]];
/* 327:440 */         for (int j = this.diagind[i] + 1; j < used; j++) {
/* 328:441 */           xd[index[j]] -= data[j] * xd[i];
/* 329:    */         }
/* 330:    */       }
/* 331:444 */       return x;
/* 332:    */     }
/* 333:    */   }
/* 334:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.ILUT
 * JD-Core Version:    0.7.0.1
 */