/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Random;
/*   6:    */ 
/*   7:    */ public class AlgVector
/*   8:    */   implements Cloneable, Serializable, RevisionHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -4023736016850256591L;
/*  11:    */   protected double[] m_Elements;
/*  12:    */   
/*  13:    */   public AlgVector(int n)
/*  14:    */   {
/*  15: 50 */     this.m_Elements = new double[n];
/*  16: 51 */     initialize();
/*  17:    */   }
/*  18:    */   
/*  19:    */   public AlgVector(double[] array)
/*  20:    */   {
/*  21: 61 */     this.m_Elements = new double[array.length];
/*  22: 62 */     for (int i = 0; i < array.length; i++) {
/*  23: 63 */       this.m_Elements[i] = array[i];
/*  24:    */     }
/*  25:    */   }
/*  26:    */   
/*  27:    */   public AlgVector(Instances format, Random random)
/*  28:    */     throws Exception
/*  29:    */   {
/*  30: 79 */     int len = format.numAttributes();
/*  31: 80 */     for (int i = 0; i < format.numAttributes(); i++) {
/*  32: 81 */       if (!format.attribute(i).isNumeric()) {
/*  33: 81 */         len--;
/*  34:    */       }
/*  35:    */     }
/*  36: 83 */     if (len > 0)
/*  37:    */     {
/*  38: 84 */       this.m_Elements = new double[len];
/*  39: 85 */       initialize(random);
/*  40:    */     }
/*  41:    */   }
/*  42:    */   
/*  43:    */   public AlgVector(Instance instance)
/*  44:    */     throws Exception
/*  45:    */   {
/*  46:100 */     int len = instance.numAttributes();
/*  47:101 */     for (int i = 0; i < instance.numAttributes(); i++) {
/*  48:102 */       if (!instance.attribute(i).isNumeric()) {
/*  49:103 */         len--;
/*  50:    */       }
/*  51:    */     }
/*  52:105 */     if (len > 0)
/*  53:    */     {
/*  54:106 */       this.m_Elements = new double[len];
/*  55:107 */       int n = 0;
/*  56:108 */       for (int i = 0; i < instance.numAttributes(); i++) {
/*  57:109 */         if (instance.attribute(i).isNumeric())
/*  58:    */         {
/*  59:111 */           this.m_Elements[n] = instance.value(i);
/*  60:112 */           n++;
/*  61:    */         }
/*  62:    */       }
/*  63:    */     }
/*  64:    */     else
/*  65:    */     {
/*  66:116 */       throw new IllegalArgumentException("No numeric attributes in data!");
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   public Object clone()
/*  71:    */     throws CloneNotSupportedException
/*  72:    */   {
/*  73:128 */     AlgVector v = (AlgVector)super.clone();
/*  74:129 */     v.m_Elements = new double[numElements()];
/*  75:130 */     for (int i = 0; i < numElements(); i++) {
/*  76:131 */       v.m_Elements[i] = this.m_Elements[i];
/*  77:    */     }
/*  78:134 */     return v;
/*  79:    */   }
/*  80:    */   
/*  81:    */   protected void initialize()
/*  82:    */   {
/*  83:142 */     for (int i = 0; i < this.m_Elements.length; i++) {
/*  84:143 */       this.m_Elements[i] = 0.0D;
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:    */   protected void initialize(Random random)
/*  89:    */   {
/*  90:154 */     for (int i = 0; i < this.m_Elements.length; i++) {
/*  91:155 */       this.m_Elements[i] = random.nextDouble();
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   public final double getElement(int index)
/*  96:    */   {
/*  97:166 */     return this.m_Elements[index];
/*  98:    */   }
/*  99:    */   
/* 100:    */   public final int numElements()
/* 101:    */   {
/* 102:177 */     return this.m_Elements.length;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public final void setElement(int index, double value)
/* 106:    */   {
/* 107:189 */     this.m_Elements[index] = value;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public final void setElements(double[] elements)
/* 111:    */   {
/* 112:200 */     for (int i = 0; i < elements.length; i++) {
/* 113:201 */       this.m_Elements[i] = elements[i];
/* 114:    */     }
/* 115:    */   }
/* 116:    */   
/* 117:    */   public double[] getElements()
/* 118:    */   {
/* 119:212 */     double[] elements = new double[numElements()];
/* 120:213 */     for (int i = 0; i < elements.length; i++) {
/* 121:214 */       elements[i] = this.m_Elements[i];
/* 122:    */     }
/* 123:216 */     return elements;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public Instance getAsInstance(Instances model, Random random)
/* 127:    */     throws Exception
/* 128:    */   {
/* 129:231 */     Instance newInst = null;
/* 130:233 */     if (this.m_Elements != null)
/* 131:    */     {
/* 132:234 */       newInst = new DenseInstance(model.numAttributes());
/* 133:235 */       newInst.setDataset(model);
/* 134:    */       
/* 135:237 */       int i = 0;
/* 136:237 */       for (int j = 0; i < model.numAttributes(); i++)
/* 137:    */       {
/* 138:238 */         if (model.attribute(i).isNumeric())
/* 139:    */         {
/* 140:239 */           if (j >= this.m_Elements.length) {
/* 141:240 */             throw new Exception("Datatypes are not compatible.");
/* 142:    */           }
/* 143:241 */           newInst.setValue(i, this.m_Elements[(j++)]);
/* 144:    */         }
/* 145:243 */         if (model.attribute(i).isNominal())
/* 146:    */         {
/* 147:244 */           int newVal = (int)(random.nextDouble() * model.attribute(i).numValues());
/* 148:246 */           if (newVal == model.attribute(i).numValues()) {
/* 149:247 */             newVal--;
/* 150:    */           }
/* 151:248 */           newInst.setValue(i, newVal);
/* 152:    */         }
/* 153:    */       }
/* 154:    */     }
/* 155:252 */     return newInst;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public final AlgVector add(AlgVector other)
/* 159:    */   {
/* 160:263 */     AlgVector b = null;
/* 161:265 */     if (this.m_Elements != null)
/* 162:    */     {
/* 163:266 */       int n = this.m_Elements.length;
/* 164:    */       try
/* 165:    */       {
/* 166:268 */         b = (AlgVector)clone();
/* 167:    */       }
/* 168:    */       catch (CloneNotSupportedException ex)
/* 169:    */       {
/* 170:270 */         b = new AlgVector(n);
/* 171:    */       }
/* 172:273 */       for (int i = 0; i < n; i++) {
/* 173:274 */         this.m_Elements[i] += other.m_Elements[i];
/* 174:    */       }
/* 175:    */     }
/* 176:278 */     return b;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public final AlgVector substract(AlgVector other)
/* 180:    */   {
/* 181:289 */     int n = this.m_Elements.length;
/* 182:    */     AlgVector b;
/* 183:    */     try
/* 184:    */     {
/* 185:292 */       b = (AlgVector)clone();
/* 186:    */     }
/* 187:    */     catch (CloneNotSupportedException ex)
/* 188:    */     {
/* 189:294 */       b = new AlgVector(n);
/* 190:    */     }
/* 191:297 */     for (int i = 0; i < n; i++) {
/* 192:298 */       this.m_Elements[i] -= other.m_Elements[i];
/* 193:    */     }
/* 194:301 */     return b;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public final double dotMultiply(AlgVector b)
/* 198:    */   {
/* 199:312 */     double sum = 0.0D;
/* 200:314 */     if (this.m_Elements != null)
/* 201:    */     {
/* 202:315 */       int n = this.m_Elements.length;
/* 203:317 */       for (int i = 0; i < n; i++) {
/* 204:318 */         sum += this.m_Elements[i] * b.m_Elements[i];
/* 205:    */       }
/* 206:    */     }
/* 207:322 */     return sum;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public final void scalarMultiply(double s)
/* 211:    */   {
/* 212:332 */     if (this.m_Elements != null)
/* 213:    */     {
/* 214:333 */       int n = this.m_Elements.length;
/* 215:335 */       for (int i = 0; i < n; i++) {
/* 216:336 */         this.m_Elements[i] = (s * this.m_Elements[i]);
/* 217:    */       }
/* 218:    */     }
/* 219:    */   }
/* 220:    */   
/* 221:    */   public void changeLength(double len)
/* 222:    */   {
/* 223:348 */     double factor = norm();
/* 224:349 */     factor = len / factor;
/* 225:350 */     scalarMultiply(factor);
/* 226:    */   }
/* 227:    */   
/* 228:    */   public double norm()
/* 229:    */   {
/* 230:360 */     if (this.m_Elements != null)
/* 231:    */     {
/* 232:361 */       int n = this.m_Elements.length;
/* 233:362 */       double sum = 0.0D;
/* 234:364 */       for (int i = 0; i < n; i++) {
/* 235:365 */         sum += this.m_Elements[i] * this.m_Elements[i];
/* 236:    */       }
/* 237:367 */       return Math.pow(sum, 0.5D);
/* 238:    */     }
/* 239:369 */     return 0.0D;
/* 240:    */   }
/* 241:    */   
/* 242:    */   public final void normVector()
/* 243:    */   {
/* 244:377 */     double len = norm();
/* 245:378 */     scalarMultiply(1.0D / len);
/* 246:    */   }
/* 247:    */   
/* 248:    */   public String toString()
/* 249:    */   {
/* 250:388 */     StringBuffer text = new StringBuffer();
/* 251:389 */     for (int i = 0; i < this.m_Elements.length; i++)
/* 252:    */     {
/* 253:390 */       if (i > 0) {
/* 254:390 */         text.append(",");
/* 255:    */       }
/* 256:391 */       text.append(Utils.doubleToString(this.m_Elements[i], 6));
/* 257:    */     }
/* 258:394 */     text.append("\n");
/* 259:395 */     return text.toString();
/* 260:    */   }
/* 261:    */   
/* 262:    */   public String getRevision()
/* 263:    */   {
/* 264:404 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 265:    */   }
/* 266:    */   
/* 267:    */   public static void main(String[] args)
/* 268:    */     throws Exception
/* 269:    */   {
/* 270:415 */     double[] first = { 2.3D, 1.2D, 5.0D };
/* 271:    */     try
/* 272:    */     {
/* 273:418 */       AlgVector test = new AlgVector(first);
/* 274:419 */       System.out.println("test:\n " + test);
/* 275:    */     }
/* 276:    */     catch (Exception e)
/* 277:    */     {
/* 278:422 */       e.printStackTrace();
/* 279:    */     }
/* 280:    */   }
/* 281:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.AlgVector
 * JD-Core Version:    0.7.0.1
 */