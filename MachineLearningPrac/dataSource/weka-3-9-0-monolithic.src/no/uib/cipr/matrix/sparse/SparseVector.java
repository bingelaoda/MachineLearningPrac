/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import java.util.Iterator;
/*   4:    */ import no.uib.cipr.matrix.AbstractVector;
/*   5:    */ import no.uib.cipr.matrix.DenseVector;
/*   6:    */ import no.uib.cipr.matrix.Matrices;
/*   7:    */ import no.uib.cipr.matrix.Vector;
/*   8:    */ import no.uib.cipr.matrix.VectorEntry;
/*   9:    */ 
/*  10:    */ public class SparseVector
/*  11:    */   extends AbstractVector
/*  12:    */   implements ISparseVector
/*  13:    */ {
/*  14:    */   double[] data;
/*  15:    */   int[] index;
/*  16:    */   int used;
/*  17:    */   
/*  18:    */   public SparseVector(int size, int nz)
/*  19:    */   {
/*  20: 60 */     super(size);
/*  21: 61 */     this.data = new double[nz];
/*  22: 62 */     this.index = new int[nz];
/*  23:    */   }
/*  24:    */   
/*  25:    */   public SparseVector(Vector x, boolean deep)
/*  26:    */   {
/*  27: 76 */     super(x);
/*  28: 78 */     if (deep)
/*  29:    */     {
/*  30: 79 */       int nz = Matrices.cardinality(x);
/*  31: 80 */       this.data = new double[nz];
/*  32: 81 */       this.index = new int[nz];
/*  33: 82 */       set(x);
/*  34:    */     }
/*  35:    */     else
/*  36:    */     {
/*  37: 84 */       SparseVector xs = (SparseVector)x;
/*  38: 85 */       this.data = xs.getData();
/*  39: 86 */       this.index = xs.getIndex();
/*  40: 87 */       this.used = xs.getUsed();
/*  41:    */     }
/*  42:    */   }
/*  43:    */   
/*  44:    */   public SparseVector(Vector x)
/*  45:    */   {
/*  46: 99 */     this(x, true);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public SparseVector(int size)
/*  50:    */   {
/*  51:109 */     this(size, 0);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public SparseVector(int size, int[] index, double[] data, boolean deep)
/*  55:    */   {
/*  56:126 */     super(size);
/*  57:128 */     if (index.length != data.length) {
/*  58:129 */       throw new IllegalArgumentException("index.length != data.length");
/*  59:    */     }
/*  60:131 */     if (deep)
/*  61:    */     {
/*  62:132 */       this.used = index.length;
/*  63:133 */       this.index = ((int[])index.clone());
/*  64:134 */       this.data = ((double[])data.clone());
/*  65:    */     }
/*  66:    */     else
/*  67:    */     {
/*  68:136 */       this.index = index;
/*  69:137 */       this.data = data;
/*  70:138 */       this.used = index.length;
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   public SparseVector(int size, int[] index, double[] data)
/*  75:    */   {
/*  76:153 */     this(size, index, data, true);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void set(int index, double value)
/*  80:    */   {
/*  81:158 */     check(index);
/*  82:    */     
/*  83:    */ 
/*  84:    */ 
/*  85:162 */     int i = getIndex(index);
/*  86:163 */     this.data[i] = value;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void add(int index, double value)
/*  90:    */   {
/*  91:168 */     check(index);
/*  92:    */     
/*  93:170 */     int i = getIndex(index);
/*  94:171 */     this.data[i] += value;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public double get(int index)
/*  98:    */   {
/*  99:176 */     check(index);
/* 100:    */     
/* 101:178 */     int in = Arrays.binarySearch(this.index, index, 0, this.used);
/* 102:179 */     if (in >= 0) {
/* 103:180 */       return this.data[in];
/* 104:    */     }
/* 105:181 */     return 0.0D;
/* 106:    */   }
/* 107:    */   
/* 108:    */   private int getIndex(int ind)
/* 109:    */   {
/* 110:191 */     int i = Arrays.binarySearchGreater(this.index, ind, 0, this.used);
/* 111:194 */     if ((i < this.used) && (this.index[i] == ind)) {
/* 112:195 */       return i;
/* 113:    */     }
/* 114:197 */     int[] newIndex = this.index;
/* 115:198 */     double[] newData = this.data;
/* 116:201 */     if (++this.used > this.data.length)
/* 117:    */     {
/* 118:204 */       int newLength = this.data.length != 0 ? this.data.length << 1 : 1;
/* 119:    */       
/* 120:    */ 
/* 121:207 */       newLength = Math.min(newLength, this.size);
/* 122:    */       
/* 123:    */ 
/* 124:210 */       newIndex = new int[newLength];
/* 125:211 */       newData = new double[newLength];
/* 126:212 */       System.arraycopy(this.index, 0, newIndex, 0, i);
/* 127:213 */       System.arraycopy(this.data, 0, newData, 0, i);
/* 128:    */     }
/* 129:217 */     System.arraycopy(this.index, i, newIndex, i + 1, this.used - i - 1);
/* 130:218 */     System.arraycopy(this.data, i, newData, i + 1, this.used - i - 1);
/* 131:    */     
/* 132:    */ 
/* 133:221 */     newIndex[i] = ind;
/* 134:222 */     newData[i] = 0.0D;
/* 135:    */     
/* 136:    */ 
/* 137:225 */     this.index = newIndex;
/* 138:226 */     this.data = newData;
/* 139:    */     
/* 140:    */ 
/* 141:229 */     return i;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public SparseVector copy()
/* 145:    */   {
/* 146:234 */     return new SparseVector(this);
/* 147:    */   }
/* 148:    */   
/* 149:    */   public SparseVector zero()
/* 150:    */   {
/* 151:239 */     java.util.Arrays.fill(this.data, 0.0D);
/* 152:240 */     this.used = 0;
/* 153:241 */     return this;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public SparseVector scale(double alpha)
/* 157:    */   {
/* 158:247 */     if (alpha == 0.0D) {
/* 159:248 */       return zero();
/* 160:    */     }
/* 161:249 */     if (alpha == 1.0D) {
/* 162:250 */       return this;
/* 163:    */     }
/* 164:252 */     for (int i = 0; i < this.used; i++) {
/* 165:253 */       this.data[i] *= alpha;
/* 166:    */     }
/* 167:255 */     return this;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public double dot(Vector y)
/* 171:    */   {
/* 172:260 */     if (!(y instanceof DenseVector)) {
/* 173:261 */       return super.dot(y);
/* 174:    */     }
/* 175:263 */     checkSize(y);
/* 176:    */     
/* 177:265 */     double[] yd = ((DenseVector)y).getData();
/* 178:    */     
/* 179:267 */     double ret = 0.0D;
/* 180:268 */     for (int i = 0; i < this.used; i++) {
/* 181:269 */       ret += this.data[i] * yd[this.index[i]];
/* 182:    */     }
/* 183:270 */     return ret;
/* 184:    */   }
/* 185:    */   
/* 186:    */   protected double norm1()
/* 187:    */   {
/* 188:275 */     double sum = 0.0D;
/* 189:276 */     for (int i = 0; i < this.used; i++) {
/* 190:277 */       sum += Math.abs(this.data[i]);
/* 191:    */     }
/* 192:278 */     return sum;
/* 193:    */   }
/* 194:    */   
/* 195:    */   protected double norm2()
/* 196:    */   {
/* 197:283 */     double norm = 0.0D;
/* 198:284 */     for (int i = 0; i < this.used; i++) {
/* 199:285 */       norm += this.data[i] * this.data[i];
/* 200:    */     }
/* 201:286 */     return Math.sqrt(norm);
/* 202:    */   }
/* 203:    */   
/* 204:    */   protected double norm2_robust()
/* 205:    */   {
/* 206:291 */     double scale = 0.0D;double ssq = 1.0D;
/* 207:292 */     for (int i = 0; i < this.used; i++) {
/* 208:293 */       if (this.data[i] != 0.0D)
/* 209:    */       {
/* 210:294 */         double absxi = Math.abs(this.data[i]);
/* 211:295 */         if (scale < absxi)
/* 212:    */         {
/* 213:296 */           ssq = 1.0D + ssq * Math.pow(scale / absxi, 2.0D);
/* 214:297 */           scale = absxi;
/* 215:    */         }
/* 216:    */         else
/* 217:    */         {
/* 218:299 */           ssq += Math.pow(absxi / scale, 2.0D);
/* 219:    */         }
/* 220:    */       }
/* 221:    */     }
/* 222:302 */     return scale * Math.sqrt(ssq);
/* 223:    */   }
/* 224:    */   
/* 225:    */   protected double normInf()
/* 226:    */   {
/* 227:307 */     double max = 0.0D;
/* 228:308 */     for (int i = 0; i < this.used; i++) {
/* 229:309 */       max = Math.max(Math.abs(this.data[i]), max);
/* 230:    */     }
/* 231:310 */     return max;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public double[] getData()
/* 235:    */   {
/* 236:323 */     return this.data;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public int[] getIndex()
/* 240:    */   {
/* 241:330 */     if (this.used == this.index.length) {
/* 242:331 */       return this.index;
/* 243:    */     }
/* 244:335 */     int[] indices = new int[this.used];
/* 245:336 */     System.arraycopy(this.index, 0, indices, 0, this.used);
/* 246:337 */     return indices;
/* 247:    */   }
/* 248:    */   
/* 249:    */   public int[] getRawIndex()
/* 250:    */   {
/* 251:352 */     return this.index;
/* 252:    */   }
/* 253:    */   
/* 254:    */   public double[] getRawData()
/* 255:    */   {
/* 256:367 */     return this.data;
/* 257:    */   }
/* 258:    */   
/* 259:    */   public int getUsed()
/* 260:    */   {
/* 261:374 */     return this.used;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public void compact()
/* 265:    */   {
/* 266:381 */     int nz = Matrices.cardinality(this);
/* 267:383 */     if (nz < this.data.length)
/* 268:    */     {
/* 269:384 */       int[] newIndex = new int[nz];
/* 270:385 */       double[] newData = new double[nz];
/* 271:    */       
/* 272:    */ 
/* 273:388 */       int i = 0;
/* 274:388 */       for (int j = 0; i < this.data.length; i++) {
/* 275:389 */         if (this.data[i] != 0.0D)
/* 276:    */         {
/* 277:390 */           newIndex[j] = this.index[i];
/* 278:391 */           newData[j] = this.data[i];
/* 279:392 */           j++;
/* 280:    */         }
/* 281:    */       }
/* 282:395 */       this.data = newData;
/* 283:396 */       this.index = newIndex;
/* 284:397 */       this.used = this.data.length;
/* 285:    */     }
/* 286:    */   }
/* 287:    */   
/* 288:    */   public Iterator<VectorEntry> iterator()
/* 289:    */   {
/* 290:403 */     return new SparseVectorIterator(null);
/* 291:    */   }
/* 292:    */   
/* 293:    */   public Vector set(Vector y)
/* 294:    */   {
/* 295:408 */     if (!(y instanceof SparseVector)) {
/* 296:409 */       return super.set(y);
/* 297:    */     }
/* 298:411 */     checkSize(y);
/* 299:    */     
/* 300:413 */     SparseVector yc = (SparseVector)y;
/* 301:415 */     if (yc.index.length != this.index.length)
/* 302:    */     {
/* 303:416 */       this.data = new double[yc.data.length];
/* 304:417 */       this.index = new int[yc.data.length];
/* 305:    */     }
/* 306:420 */     System.arraycopy(yc.data, 0, this.data, 0, this.data.length);
/* 307:421 */     System.arraycopy(yc.index, 0, this.index, 0, this.index.length);
/* 308:422 */     this.used = yc.used;
/* 309:    */     
/* 310:424 */     return this;
/* 311:    */   }
/* 312:    */   
/* 313:    */   private class SparseVectorIterator
/* 314:    */     implements Iterator<VectorEntry>
/* 315:    */   {
/* 316:    */     private int cursor;
/* 317:434 */     private final SparseVector.SparseVectorEntry entry = new SparseVector.SparseVectorEntry(SparseVector.this, null);
/* 318:    */     
/* 319:    */     private SparseVectorIterator() {}
/* 320:    */     
/* 321:    */     public boolean hasNext()
/* 322:    */     {
/* 323:437 */       return this.cursor < SparseVector.this.used;
/* 324:    */     }
/* 325:    */     
/* 326:    */     public VectorEntry next()
/* 327:    */     {
/* 328:441 */       this.entry.update(this.cursor);
/* 329:    */       
/* 330:443 */       this.cursor += 1;
/* 331:    */       
/* 332:445 */       return this.entry;
/* 333:    */     }
/* 334:    */     
/* 335:    */     public void remove()
/* 336:    */     {
/* 337:449 */       this.entry.set(0.0D);
/* 338:    */     }
/* 339:    */   }
/* 340:    */   
/* 341:    */   private class SparseVectorEntry
/* 342:    */     implements VectorEntry
/* 343:    */   {
/* 344:    */     private int cursor;
/* 345:    */     
/* 346:    */     private SparseVectorEntry() {}
/* 347:    */     
/* 348:    */     public void update(int cursor)
/* 349:    */     {
/* 350:462 */       this.cursor = cursor;
/* 351:    */     }
/* 352:    */     
/* 353:    */     public int index()
/* 354:    */     {
/* 355:466 */       return SparseVector.this.index[this.cursor];
/* 356:    */     }
/* 357:    */     
/* 358:    */     public double get()
/* 359:    */     {
/* 360:470 */       return SparseVector.this.data[this.cursor];
/* 361:    */     }
/* 362:    */     
/* 363:    */     public void set(double value)
/* 364:    */     {
/* 365:474 */       SparseVector.this.data[this.cursor] = value;
/* 366:    */     }
/* 367:    */   }
/* 368:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.SparseVector
 * JD-Core Version:    0.7.0.1
 */