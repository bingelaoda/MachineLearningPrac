/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import no.uib.cipr.matrix.io.MatrixVectorReader;
/*   7:    */ import no.uib.cipr.matrix.io.VectorInfo;
/*   8:    */ import no.uib.cipr.matrix.io.VectorInfo.VectorField;
/*   9:    */ import no.uib.cipr.matrix.io.VectorSize;
/*  10:    */ 
/*  11:    */ public class DenseVector
/*  12:    */   extends AbstractVector
/*  13:    */   implements Serializable
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = 5358813524094629362L;
/*  16:    */   private final double[] data;
/*  17:    */   
/*  18:    */   public DenseVector(MatrixVectorReader r)
/*  19:    */     throws IOException
/*  20:    */   {
/*  21: 54 */     super(0);
/*  22:    */     
/*  23:    */ 
/*  24:    */ 
/*  25: 58 */     VectorInfo info = null;
/*  26: 59 */     if (r.hasInfo()) {
/*  27: 60 */       info = r.readVectorInfo();
/*  28:    */     } else {
/*  29: 62 */       info = new VectorInfo(true, VectorInfo.VectorField.Real);
/*  30:    */     }
/*  31: 63 */     VectorSize size = r.readVectorSize(info);
/*  32:    */     
/*  33:    */ 
/*  34: 66 */     this.size = size.size();
/*  35: 67 */     this.data = new double[size.size()];
/*  36: 70 */     if (info.isPattern()) {
/*  37: 71 */       throw new UnsupportedOperationException("Pattern vectors are not supported");
/*  38:    */     }
/*  39: 73 */     if (info.isComplex()) {
/*  40: 74 */       throw new UnsupportedOperationException("Complex vectors are not supported");
/*  41:    */     }
/*  42: 78 */     if (info.isCoordinate())
/*  43:    */     {
/*  44: 81 */       int nz = size.numEntries();
/*  45: 82 */       int[] index = new int[nz];
/*  46: 83 */       double[] entry = new double[nz];
/*  47: 84 */       r.readCoordinate(index, entry);
/*  48:    */       
/*  49:    */ 
/*  50: 87 */       r.add(-1, index);
/*  51: 90 */       for (int i = 0; i < nz; i++) {
/*  52: 91 */         set(index[i], entry[i]);
/*  53:    */       }
/*  54:    */     }
/*  55:    */     else
/*  56:    */     {
/*  57: 95 */       r.readArray(this.data);
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   public DenseVector(int size)
/*  62:    */   {
/*  63:105 */     super(size);
/*  64:106 */     this.data = new double[size];
/*  65:    */   }
/*  66:    */   
/*  67:    */   public DenseVector(Vector x)
/*  68:    */   {
/*  69:116 */     this(x, true);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public DenseVector(Vector x, boolean deep)
/*  73:    */   {
/*  74:129 */     super(x);
/*  75:131 */     if (deep)
/*  76:    */     {
/*  77:132 */       this.data = new double[this.size];
/*  78:133 */       set(x);
/*  79:    */     }
/*  80:    */     else
/*  81:    */     {
/*  82:135 */       this.data = ((DenseVector)x).getData();
/*  83:    */     }
/*  84:    */   }
/*  85:    */   
/*  86:    */   public DenseVector(double[] x, boolean deep)
/*  87:    */   {
/*  88:148 */     super(x.length);
/*  89:150 */     if (deep) {
/*  90:151 */       this.data = ((double[])x.clone());
/*  91:    */     } else {
/*  92:153 */       this.data = x;
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public DenseVector(double[] x)
/*  97:    */   {
/*  98:163 */     this(x, true);
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void set(int index, double value)
/* 102:    */   {
/* 103:168 */     check(index);
/* 104:169 */     this.data[index] = value;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void add(int index, double value)
/* 108:    */   {
/* 109:174 */     check(index);
/* 110:175 */     this.data[index] += value;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public double get(int index)
/* 114:    */   {
/* 115:180 */     check(index);
/* 116:181 */     return this.data[index];
/* 117:    */   }
/* 118:    */   
/* 119:    */   public DenseVector copy()
/* 120:    */   {
/* 121:186 */     return new DenseVector(this);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public DenseVector zero()
/* 125:    */   {
/* 126:191 */     Arrays.fill(this.data, 0.0D);
/* 127:192 */     return this;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public DenseVector scale(double alpha)
/* 131:    */   {
/* 132:197 */     for (int i = 0; i < this.size; i++) {
/* 133:198 */       this.data[i] *= alpha;
/* 134:    */     }
/* 135:199 */     return this;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public Vector set(Vector y)
/* 139:    */   {
/* 140:204 */     if (!(y instanceof DenseVector)) {
/* 141:205 */       return super.set(y);
/* 142:    */     }
/* 143:207 */     checkSize(y);
/* 144:    */     
/* 145:209 */     double[] yd = ((DenseVector)y).getData();
/* 146:210 */     System.arraycopy(yd, 0, this.data, 0, this.size);
/* 147:    */     
/* 148:212 */     return this;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Vector set(double alpha, Vector y)
/* 152:    */   {
/* 153:217 */     if (!(y instanceof DenseVector)) {
/* 154:218 */       return super.set(alpha, y);
/* 155:    */     }
/* 156:220 */     checkSize(y);
/* 157:222 */     if (alpha == 0.0D) {
/* 158:223 */       return zero();
/* 159:    */     }
/* 160:225 */     double[] yd = ((DenseVector)y).getData();
/* 161:227 */     for (int i = 0; i < this.size; i++) {
/* 162:228 */       this.data[i] = (alpha * yd[i]);
/* 163:    */     }
/* 164:230 */     return this;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public Vector add(Vector y)
/* 168:    */   {
/* 169:235 */     if (!(y instanceof DenseVector)) {
/* 170:236 */       return super.add(y);
/* 171:    */     }
/* 172:238 */     checkSize(y);
/* 173:    */     
/* 174:240 */     double[] yd = ((DenseVector)y).getData();
/* 175:242 */     for (int i = 0; i < this.size; i++) {
/* 176:243 */       this.data[i] += yd[i];
/* 177:    */     }
/* 178:245 */     return this;
/* 179:    */   }
/* 180:    */   
/* 181:    */   public Vector add(double alpha, Vector y)
/* 182:    */   {
/* 183:250 */     if (!(y instanceof DenseVector)) {
/* 184:251 */       return super.add(alpha, y);
/* 185:    */     }
/* 186:253 */     checkSize(y);
/* 187:255 */     if (alpha == 0.0D) {
/* 188:256 */       return this;
/* 189:    */     }
/* 190:258 */     double[] yd = ((DenseVector)y).getData();
/* 191:260 */     for (int i = 0; i < this.size; i++) {
/* 192:261 */       this.data[i] += alpha * yd[i];
/* 193:    */     }
/* 194:263 */     return this;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public double dot(Vector y)
/* 198:    */   {
/* 199:268 */     if (!(y instanceof DenseVector)) {
/* 200:269 */       return super.dot(y);
/* 201:    */     }
/* 202:271 */     checkSize(y);
/* 203:    */     
/* 204:273 */     double[] yd = ((DenseVector)y).getData();
/* 205:    */     
/* 206:275 */     double dot = 0.0D;
/* 207:276 */     for (int i = 0; i < this.size; i++) {
/* 208:277 */       dot += this.data[i] * yd[i];
/* 209:    */     }
/* 210:278 */     return dot;
/* 211:    */   }
/* 212:    */   
/* 213:    */   protected double norm1()
/* 214:    */   {
/* 215:283 */     double sum = 0.0D;
/* 216:284 */     for (int i = 0; i < this.size; i++) {
/* 217:285 */       sum += Math.abs(this.data[i]);
/* 218:    */     }
/* 219:286 */     return sum;
/* 220:    */   }
/* 221:    */   
/* 222:    */   protected double norm2()
/* 223:    */   {
/* 224:291 */     double norm = 0.0D;
/* 225:292 */     for (int i = 0; i < this.size; i++) {
/* 226:293 */       norm += this.data[i] * this.data[i];
/* 227:    */     }
/* 228:294 */     return Math.sqrt(norm);
/* 229:    */   }
/* 230:    */   
/* 231:    */   protected double norm2_robust()
/* 232:    */   {
/* 233:299 */     double scale = 0.0D;double ssq = 1.0D;
/* 234:300 */     for (int i = 0; i < this.size; i++) {
/* 235:301 */       if (this.data[i] != 0.0D)
/* 236:    */       {
/* 237:302 */         double absxi = Math.abs(this.data[i]);
/* 238:303 */         if (scale < absxi)
/* 239:    */         {
/* 240:304 */           ssq = 1.0D + ssq * (scale / absxi) * (scale / absxi);
/* 241:305 */           scale = absxi;
/* 242:    */         }
/* 243:    */         else
/* 244:    */         {
/* 245:307 */           ssq += absxi / scale * (absxi / scale);
/* 246:    */         }
/* 247:    */       }
/* 248:    */     }
/* 249:309 */     return scale * Math.sqrt(ssq);
/* 250:    */   }
/* 251:    */   
/* 252:    */   protected double normInf()
/* 253:    */   {
/* 254:314 */     double max = 0.0D;
/* 255:315 */     for (int i = 0; i < this.size; i++) {
/* 256:316 */       max = Math.max(Math.abs(this.data[i]), max);
/* 257:    */     }
/* 258:317 */     return max;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public double[] getData()
/* 262:    */   {
/* 263:325 */     return this.data;
/* 264:    */   }
/* 265:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.DenseVector
 * JD-Core Version:    0.7.0.1
 */