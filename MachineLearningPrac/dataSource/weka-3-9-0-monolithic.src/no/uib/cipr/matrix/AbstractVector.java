/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Formatter;
/*   5:    */ import java.util.Iterator;
/*   6:    */ 
/*   7:    */ public abstract class AbstractVector
/*   8:    */   implements Vector, Serializable
/*   9:    */ {
/*  10:    */   protected int size;
/*  11:    */   
/*  12:    */   protected AbstractVector(int size)
/*  13:    */   {
/*  14: 64 */     if (size < 0) {
/*  15: 65 */       throw new IllegalArgumentException("Vector size cannot be negative");
/*  16:    */     }
/*  17: 66 */     this.size = size;
/*  18:    */   }
/*  19:    */   
/*  20:    */   protected AbstractVector(Vector x)
/*  21:    */   {
/*  22: 76 */     this.size = x.size();
/*  23:    */   }
/*  24:    */   
/*  25:    */   public int size()
/*  26:    */   {
/*  27: 80 */     return this.size;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void set(int index, double value)
/*  31:    */   {
/*  32: 84 */     throw new UnsupportedOperationException();
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void add(int index, double value)
/*  36:    */   {
/*  37: 88 */     set(index, value + get(index));
/*  38:    */   }
/*  39:    */   
/*  40:    */   public double get(int index)
/*  41:    */   {
/*  42: 92 */     throw new UnsupportedOperationException();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Vector copy()
/*  46:    */   {
/*  47: 96 */     throw new UnsupportedOperationException();
/*  48:    */   }
/*  49:    */   
/*  50:    */   protected void check(int index)
/*  51:    */   {
/*  52:103 */     if (index < 0) {
/*  53:104 */       throw new IndexOutOfBoundsException("index is negative (" + index + ")");
/*  54:    */     }
/*  55:106 */     if (index >= this.size) {
/*  56:107 */       throw new IndexOutOfBoundsException("index >= size (" + index + " >= " + this.size + ")");
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   public Vector zero()
/*  61:    */   {
/*  62:112 */     for (VectorEntry e : this) {
/*  63:113 */       e.set(0.0D);
/*  64:    */     }
/*  65:114 */     return this;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Vector scale(double alpha)
/*  69:    */   {
/*  70:118 */     if (alpha == 0.0D) {
/*  71:119 */       return zero();
/*  72:    */     }
/*  73:120 */     if (alpha == 1.0D) {
/*  74:121 */       return this;
/*  75:    */     }
/*  76:123 */     for (VectorEntry e : this) {
/*  77:124 */       e.set(alpha * e.get());
/*  78:    */     }
/*  79:126 */     return this;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public Vector set(Vector y)
/*  83:    */   {
/*  84:130 */     return set(1.0D, y);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public Vector set(double alpha, Vector y)
/*  88:    */   {
/*  89:134 */     checkSize(y);
/*  90:136 */     if (alpha == 0.0D) {
/*  91:137 */       return zero();
/*  92:    */     }
/*  93:139 */     zero();
/*  94:140 */     for (VectorEntry e : y) {
/*  95:141 */       set(e.index(), alpha * e.get());
/*  96:    */     }
/*  97:143 */     return this;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public Vector add(Vector y)
/* 101:    */   {
/* 102:147 */     return add(1.0D, y);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public Vector add(double alpha, Vector y)
/* 106:    */   {
/* 107:151 */     checkSize(y);
/* 108:153 */     if (alpha == 0.0D) {
/* 109:154 */       return this;
/* 110:    */     }
/* 111:156 */     for (VectorEntry e : y) {
/* 112:157 */       add(e.index(), alpha * e.get());
/* 113:    */     }
/* 114:159 */     return this;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public double dot(Vector y)
/* 118:    */   {
/* 119:163 */     checkSize(y);
/* 120:    */     
/* 121:165 */     double ret = 0.0D;
/* 122:166 */     for (VectorEntry e : this) {
/* 123:167 */       ret += e.get() * y.get(e.index());
/* 124:    */     }
/* 125:168 */     return ret;
/* 126:    */   }
/* 127:    */   
/* 128:    */   protected void checkSize(Vector y)
/* 129:    */   {
/* 130:175 */     if (this.size != y.size()) {
/* 131:177 */       throw new IndexOutOfBoundsException("x.size != y.size (" + this.size + " != " + y.size() + ")");
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   public double norm(Vector.Norm type)
/* 136:    */   {
/* 137:181 */     if (type == Vector.Norm.One) {
/* 138:182 */       return norm1();
/* 139:    */     }
/* 140:183 */     if (type == Vector.Norm.Two) {
/* 141:184 */       return norm2();
/* 142:    */     }
/* 143:185 */     if (type == Vector.Norm.TwoRobust) {
/* 144:186 */       return norm2_robust();
/* 145:    */     }
/* 146:189 */     return normInf();
/* 147:    */   }
/* 148:    */   
/* 149:    */   protected double norm1()
/* 150:    */   {
/* 151:193 */     double sum = 0.0D;
/* 152:194 */     for (VectorEntry e : this) {
/* 153:195 */       sum += Math.abs(e.get());
/* 154:    */     }
/* 155:196 */     return sum;
/* 156:    */   }
/* 157:    */   
/* 158:    */   protected double norm2()
/* 159:    */   {
/* 160:200 */     double norm = 0.0D;
/* 161:201 */     for (VectorEntry e : this) {
/* 162:202 */       norm += e.get() * e.get();
/* 163:    */     }
/* 164:203 */     return Math.sqrt(norm);
/* 165:    */   }
/* 166:    */   
/* 167:    */   protected double norm2_robust()
/* 168:    */   {
/* 169:207 */     double scale = 0.0D;double ssq = 1.0D;
/* 170:208 */     for (VectorEntry e : this)
/* 171:    */     {
/* 172:209 */       double xval = e.get();
/* 173:210 */       if (xval != 0.0D)
/* 174:    */       {
/* 175:211 */         double absxi = Math.abs(xval);
/* 176:212 */         if (scale < absxi)
/* 177:    */         {
/* 178:213 */           ssq = 1.0D + ssq * Math.pow(scale / absxi, 2.0D);
/* 179:214 */           scale = absxi;
/* 180:    */         }
/* 181:    */         else
/* 182:    */         {
/* 183:216 */           ssq += Math.pow(absxi / scale, 2.0D);
/* 184:    */         }
/* 185:    */       }
/* 186:    */     }
/* 187:219 */     return scale * Math.sqrt(ssq);
/* 188:    */   }
/* 189:    */   
/* 190:    */   protected double normInf()
/* 191:    */   {
/* 192:223 */     double max = 0.0D;
/* 193:224 */     for (VectorEntry e : this) {
/* 194:225 */       max = Math.max(Math.abs(e.get()), max);
/* 195:    */     }
/* 196:226 */     return max;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public Iterator<VectorEntry> iterator()
/* 200:    */   {
/* 201:230 */     return new RefVectorIterator(null);
/* 202:    */   }
/* 203:    */   
/* 204:    */   public String toString()
/* 205:    */   {
/* 206:236 */     Formatter out = new Formatter();
/* 207:    */     
/* 208:238 */     out.format("%10d %19d\n", new Object[] { Integer.valueOf(this.size), Integer.valueOf(Matrices.cardinality(this)) });
/* 209:    */     
/* 210:240 */     int i = 0;
/* 211:241 */     for (VectorEntry e : this)
/* 212:    */     {
/* 213:242 */       if (e.get() != 0.0D) {
/* 214:243 */         out.format("%10d % .12e\n", new Object[] { Integer.valueOf(e.index() + 1), Double.valueOf(e.get()) });
/* 215:    */       }
/* 216:244 */       i++;
/* 217:244 */       if (i == 100)
/* 218:    */       {
/* 219:245 */         out.format("...\n", new Object[0]);
/* 220:246 */         break;
/* 221:    */       }
/* 222:    */     }
/* 223:250 */     return out.toString();
/* 224:    */   }
/* 225:    */   
/* 226:    */   private class RefVectorIterator
/* 227:    */     implements Iterator<VectorEntry>
/* 228:    */   {
/* 229:    */     private int index;
/* 230:260 */     private final AbstractVector.RefVectorEntry entry = new AbstractVector.RefVectorEntry(AbstractVector.this, null);
/* 231:    */     
/* 232:    */     private RefVectorIterator() {}
/* 233:    */     
/* 234:    */     public boolean hasNext()
/* 235:    */     {
/* 236:263 */       return this.index < AbstractVector.this.size;
/* 237:    */     }
/* 238:    */     
/* 239:    */     public VectorEntry next()
/* 240:    */     {
/* 241:267 */       this.entry.update(this.index);
/* 242:    */       
/* 243:269 */       this.index += 1;
/* 244:    */       
/* 245:271 */       return this.entry;
/* 246:    */     }
/* 247:    */     
/* 248:    */     public void remove()
/* 249:    */     {
/* 250:275 */       this.entry.set(0.0D);
/* 251:    */     }
/* 252:    */   }
/* 253:    */   
/* 254:    */   private class RefVectorEntry
/* 255:    */     implements VectorEntry
/* 256:    */   {
/* 257:    */     private int index;
/* 258:    */     
/* 259:    */     private RefVectorEntry() {}
/* 260:    */     
/* 261:    */     public void update(int index)
/* 262:    */     {
/* 263:291 */       this.index = index;
/* 264:    */     }
/* 265:    */     
/* 266:    */     public int index()
/* 267:    */     {
/* 268:295 */       return this.index;
/* 269:    */     }
/* 270:    */     
/* 271:    */     public double get()
/* 272:    */     {
/* 273:299 */       return AbstractVector.this.get(this.index);
/* 274:    */     }
/* 275:    */     
/* 276:    */     public void set(double value)
/* 277:    */     {
/* 278:303 */       AbstractVector.this.set(this.index, value);
/* 279:    */     }
/* 280:    */   }
/* 281:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.AbstractVector
 * JD-Core Version:    0.7.0.1
 */