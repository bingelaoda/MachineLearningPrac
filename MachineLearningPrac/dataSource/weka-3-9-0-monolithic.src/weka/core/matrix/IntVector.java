/*   1:    */ package weka.core.matrix;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import weka.core.RevisionHandler;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public class IntVector
/*   9:    */   implements Cloneable, RevisionHandler
/*  10:    */ {
/*  11:    */   int[] V;
/*  12:    */   private int sizeOfVector;
/*  13:    */   
/*  14:    */   public IntVector()
/*  15:    */   {
/*  16: 52 */     this.V = new int[0];
/*  17: 53 */     setSize(0);
/*  18:    */   }
/*  19:    */   
/*  20:    */   public IntVector(int n)
/*  21:    */   {
/*  22: 60 */     this.V = new int[n];
/*  23: 61 */     setSize(n);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public IntVector(int n, int s)
/*  27:    */   {
/*  28: 68 */     this(n);
/*  29: 69 */     set(s);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public IntVector(int[] v)
/*  33:    */   {
/*  34: 76 */     if (v == null)
/*  35:    */     {
/*  36: 77 */       this.V = new int[0];
/*  37: 78 */       setSize(0);
/*  38:    */     }
/*  39:    */     else
/*  40:    */     {
/*  41: 81 */       this.V = new int[v.length];
/*  42: 82 */       setSize(v.length);
/*  43: 83 */       set(0, size() - 1, v, 0);
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public int size()
/*  48:    */   {
/*  49: 94 */     return this.sizeOfVector;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setSize(int size)
/*  53:    */   {
/*  54:103 */     if (size > capacity()) {
/*  55:104 */       throw new IllegalArgumentException("insufficient capacity");
/*  56:    */     }
/*  57:105 */     this.sizeOfVector = size;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void set(int s)
/*  61:    */   {
/*  62:111 */     for (int i = 0; i < size(); i++) {
/*  63:112 */       set(i, s);
/*  64:    */     }
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void set(int i0, int i1, int[] v, int j0)
/*  68:    */   {
/*  69:121 */     for (int i = i0; i <= i1; i++) {
/*  70:122 */       set(i, v[(j0 + i - i0)]);
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void set(int i0, int i1, IntVector v, int j0)
/*  75:    */   {
/*  76:131 */     for (int i = i0; i <= i1; i++) {
/*  77:132 */       set(i, v.get(j0 + i - i0));
/*  78:    */     }
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void set(IntVector v)
/*  82:    */   {
/*  83:139 */     set(0, v.size() - 1, v, 0);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static IntVector seq(int i0, int i1)
/*  87:    */   {
/*  88:148 */     if (i1 < i0) {
/*  89:148 */       throw new IllegalArgumentException("i1 < i0 ");
/*  90:    */     }
/*  91:149 */     IntVector v = new IntVector(i1 - i0 + 1);
/*  92:150 */     for (int i = 0; i < i1 - i0 + 1; i++) {
/*  93:151 */       v.set(i, i + i0);
/*  94:    */     }
/*  95:153 */     return v;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public int[] getArray()
/*  99:    */   {
/* 100:159 */     return this.V;
/* 101:    */   }
/* 102:    */   
/* 103:    */   protected void setArray(int[] a)
/* 104:    */   {
/* 105:165 */     this.V = a;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void sort()
/* 109:    */   {
/* 110:171 */     Arrays.sort(this.V, 0, size());
/* 111:    */   }
/* 112:    */   
/* 113:    */   public int[] getArrayCopy()
/* 114:    */   {
/* 115:177 */     int[] b = new int[size()];
/* 116:178 */     for (int i = 0; i <= size() - 1; i++) {
/* 117:179 */       b[i] = this.V[i];
/* 118:    */     }
/* 119:181 */     return b;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public int capacity()
/* 123:    */   {
/* 124:187 */     return this.V.length;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void setCapacity(int capacity)
/* 128:    */   {
/* 129:194 */     if (capacity == capacity()) {
/* 130:194 */       return;
/* 131:    */     }
/* 132:195 */     int[] old_V = this.V;
/* 133:196 */     int m = Math.min(capacity, size());
/* 134:197 */     this.V = new int[capacity];
/* 135:198 */     setSize(capacity);
/* 136:199 */     set(0, m - 1, old_V, 0);
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void set(int i, int s)
/* 140:    */   {
/* 141:207 */     this.V[i] = s;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public int get(int i)
/* 145:    */   {
/* 146:215 */     return this.V[i];
/* 147:    */   }
/* 148:    */   
/* 149:    */   public IntVector copy()
/* 150:    */   {
/* 151:221 */     return (IntVector)clone();
/* 152:    */   }
/* 153:    */   
/* 154:    */   public Object clone()
/* 155:    */   {
/* 156:227 */     IntVector u = new IntVector(size());
/* 157:228 */     for (int i = 0; i < size(); i++) {
/* 158:229 */       u.V[i] = this.V[i];
/* 159:    */     }
/* 160:230 */     return u;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public IntVector subvector(int i0, int i1)
/* 164:    */   {
/* 165:240 */     IntVector v = new IntVector(i1 - i0 + 1);
/* 166:241 */     v.set(0, i1 - i0, this, i0);
/* 167:242 */     return v;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public IntVector subvector(IntVector index)
/* 171:    */   {
/* 172:250 */     IntVector v = new IntVector(index.size());
/* 173:251 */     for (int i = 0; i < index.size(); i++) {
/* 174:252 */       v.V[i] = this.V[index.V[i]];
/* 175:    */     }
/* 176:253 */     return v;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void swap(int i, int j)
/* 180:    */   {
/* 181:262 */     if (i == j) {
/* 182:262 */       return;
/* 183:    */     }
/* 184:263 */     int t = get(i);
/* 185:264 */     set(i, get(j));
/* 186:265 */     set(j, t);
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void shift(int i, int j)
/* 190:    */   {
/* 191:274 */     if (i == j) {
/* 192:274 */       return;
/* 193:    */     }
/* 194:275 */     if (i < j)
/* 195:    */     {
/* 196:276 */       int t = this.V[i];
/* 197:277 */       for (int k = i; k <= j - 1; k++) {
/* 198:278 */         this.V[k] = this.V[(k + 1)];
/* 199:    */       }
/* 200:279 */       this.V[j] = t;
/* 201:    */     }
/* 202:    */     else
/* 203:    */     {
/* 204:281 */       shift(j, i);
/* 205:    */     }
/* 206:    */   }
/* 207:    */   
/* 208:    */   public void shiftToEnd(int j)
/* 209:    */   {
/* 210:290 */     shift(j, size() - 1);
/* 211:    */   }
/* 212:    */   
/* 213:    */   public boolean isEmpty()
/* 214:    */   {
/* 215:297 */     if (size() == 0) {
/* 216:297 */       return true;
/* 217:    */     }
/* 218:298 */     return false;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public String toString()
/* 222:    */   {
/* 223:304 */     return toString(5, false);
/* 224:    */   }
/* 225:    */   
/* 226:    */   public String toString(int digits, boolean trailing)
/* 227:    */   {
/* 228:312 */     if (isEmpty()) {
/* 229:312 */       return "null vector";
/* 230:    */     }
/* 231:314 */     StringBuffer text = new StringBuffer();
/* 232:315 */     FlexibleDecimalFormat nf = new FlexibleDecimalFormat(digits, trailing);
/* 233:    */     
/* 234:317 */     nf.grouping(true);
/* 235:318 */     for (int i = 0; i < size(); i++) {
/* 236:318 */       nf.update(get(i));
/* 237:    */     }
/* 238:319 */     int count = 0;
/* 239:320 */     int width = 80;
/* 240:322 */     for (int i = 0; i < size(); i++)
/* 241:    */     {
/* 242:323 */       String number = nf.format(get(i));
/* 243:324 */       count += 1 + number.length();
/* 244:325 */       if (count > width - 1)
/* 245:    */       {
/* 246:326 */         text.append('\n');
/* 247:327 */         count = 1 + number.length();
/* 248:    */       }
/* 249:329 */       text.append(" " + number);
/* 250:    */     }
/* 251:332 */     return text.toString();
/* 252:    */   }
/* 253:    */   
/* 254:    */   public String getRevision()
/* 255:    */   {
/* 256:341 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 257:    */   }
/* 258:    */   
/* 259:    */   public static void main(String[] args)
/* 260:    */   {
/* 261:349 */     IntVector u = new IntVector();
/* 262:350 */     System.out.println(u);
/* 263:    */     
/* 264:352 */     IntVector v = seq(10, 25);
/* 265:353 */     System.out.println(v);
/* 266:    */     
/* 267:355 */     IntVector w = seq(25, 10);
/* 268:356 */     System.out.println(w);
/* 269:    */   }
/* 270:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.IntVector
 * JD-Core Version:    0.7.0.1
 */