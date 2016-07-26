/*   1:    */ package weka.classifiers.functions.supportVector;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.Utils;
/*  12:    */ 
/*  13:    */ public abstract class CachedKernel
/*  14:    */   extends Kernel
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 702810182699015136L;
/*  17:    */   protected int m_kernelEvals;
/*  18:    */   protected int m_cacheHits;
/*  19: 57 */   protected int m_cacheSize = 250007;
/*  20:    */   protected double[] m_storage;
/*  21:    */   protected long[] m_keys;
/*  22:    */   protected double[][] m_kernelMatrix;
/*  23:    */   protected int m_numInsts;
/*  24: 70 */   protected int m_cacheSlots = 4;
/*  25:    */   
/*  26:    */   public CachedKernel() {}
/*  27:    */   
/*  28:    */   protected CachedKernel(Instances data, int cacheSize)
/*  29:    */     throws Exception
/*  30:    */   {
/*  31: 90 */     setCacheSize(cacheSize);
/*  32:    */     
/*  33: 92 */     buildKernel(data);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Enumeration<Option> listOptions()
/*  37:    */   {
/*  38:102 */     Vector<Option> result = new Vector();
/*  39:    */     
/*  40:104 */     result.addElement(new Option("\tThe size of the cache (a prime number), 0 for full cache and \n\t-1 to turn it off.\n\t(default: 250007)", "C", 1, "-C <num>"));
/*  41:    */     
/*  42:    */ 
/*  43:    */ 
/*  44:    */ 
/*  45:    */ 
/*  46:110 */     result.addAll(Collections.list(super.listOptions()));
/*  47:    */     
/*  48:112 */     return result.elements();
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setOptions(String[] options)
/*  52:    */     throws Exception
/*  53:    */   {
/*  54:126 */     String tmpStr = Utils.getOption('C', options);
/*  55:127 */     if (tmpStr.length() != 0) {
/*  56:128 */       setCacheSize(Integer.parseInt(tmpStr));
/*  57:    */     } else {
/*  58:130 */       setCacheSize(250007);
/*  59:    */     }
/*  60:133 */     super.setOptions(options);
/*  61:    */   }
/*  62:    */   
/*  63:    */   public String[] getOptions()
/*  64:    */   {
/*  65:144 */     Vector<String> result = new Vector();
/*  66:    */     
/*  67:146 */     result.add("-C");
/*  68:147 */     result.add("" + getCacheSize());
/*  69:    */     
/*  70:149 */     Collections.addAll(result, super.getOptions());
/*  71:    */     
/*  72:151 */     return (String[])result.toArray(new String[result.size()]);
/*  73:    */   }
/*  74:    */   
/*  75:    */   protected abstract double evaluate(int paramInt1, int paramInt2, Instance paramInstance)
/*  76:    */     throws Exception;
/*  77:    */   
/*  78:    */   public double eval(int id1, int id2, Instance inst1)
/*  79:    */     throws Exception
/*  80:    */   {
/*  81:179 */     double result = 0.0D;
/*  82:180 */     long key = -1L;
/*  83:181 */     int location = -1;
/*  84:185 */     if ((id1 >= 0) && (this.m_cacheSize != -1))
/*  85:    */     {
/*  86:188 */       if (this.m_cacheSize == 0)
/*  87:    */       {
/*  88:189 */         if (this.m_kernelMatrix == null)
/*  89:    */         {
/*  90:190 */           this.m_kernelMatrix = new double[this.m_data.numInstances()][];
/*  91:191 */           for (int i = 0; i < this.m_data.numInstances(); i++)
/*  92:    */           {
/*  93:192 */             this.m_kernelMatrix[i] = new double[i + 1];
/*  94:193 */             for (int j = 0; j <= i; j++)
/*  95:    */             {
/*  96:194 */               this.m_kernelEvals += 1;
/*  97:195 */               this.m_kernelMatrix[i][j] = evaluate(i, j, this.m_data.instance(i));
/*  98:    */             }
/*  99:    */           }
/* 100:    */         }
/* 101:199 */         this.m_cacheHits += 1;
/* 102:200 */         result = id1 > id2 ? this.m_kernelMatrix[id1][id2] : this.m_kernelMatrix[id2][id1];
/* 103:    */         
/* 104:202 */         return result;
/* 105:    */       }
/* 106:206 */       if (id1 > id2) {
/* 107:207 */         key = id1 + id2 * this.m_numInsts;
/* 108:    */       } else {
/* 109:209 */         key = id2 + id1 * this.m_numInsts;
/* 110:    */       }
/* 111:211 */       location = (int)(key % this.m_cacheSize) * this.m_cacheSlots;
/* 112:212 */       int loc = location;
/* 113:213 */       for (int i = 0; i < this.m_cacheSlots; i++)
/* 114:    */       {
/* 115:214 */         long thiskey = this.m_keys[loc];
/* 116:215 */         if (thiskey == 0L) {
/* 117:    */           break;
/* 118:    */         }
/* 119:218 */         if (thiskey == key + 1L)
/* 120:    */         {
/* 121:219 */           this.m_cacheHits += 1;
/* 122:222 */           if (i > 0)
/* 123:    */           {
/* 124:223 */             double tmps = this.m_storage[loc];
/* 125:224 */             this.m_storage[loc] = this.m_storage[location];
/* 126:225 */             this.m_keys[loc] = this.m_keys[location];
/* 127:226 */             this.m_storage[location] = tmps;
/* 128:227 */             this.m_keys[location] = thiskey;
/* 129:228 */             return tmps;
/* 130:    */           }
/* 131:230 */           return this.m_storage[loc];
/* 132:    */         }
/* 133:233 */         loc++;
/* 134:    */       }
/* 135:    */     }
/* 136:237 */     result = evaluate(id1, id2, inst1);
/* 137:    */     
/* 138:239 */     this.m_kernelEvals += 1;
/* 139:242 */     if ((key != -1L) && (this.m_cacheSize != -1))
/* 140:    */     {
/* 141:245 */       System.arraycopy(this.m_keys, location, this.m_keys, location + 1, this.m_cacheSlots - 1);
/* 142:    */       
/* 143:247 */       System.arraycopy(this.m_storage, location, this.m_storage, location + 1, this.m_cacheSlots - 1);
/* 144:    */       
/* 145:249 */       this.m_storage[location] = result;
/* 146:250 */       this.m_keys[location] = (key + 1L);
/* 147:    */     }
/* 148:252 */     return result;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public int numEvals()
/* 152:    */   {
/* 153:262 */     return this.m_kernelEvals;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public int numCacheHits()
/* 157:    */   {
/* 158:272 */     return this.m_cacheHits;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void clean()
/* 162:    */   {
/* 163:280 */     this.m_storage = null;
/* 164:281 */     this.m_keys = null;
/* 165:282 */     this.m_kernelMatrix = ((double[][])null);
/* 166:    */   }
/* 167:    */   
/* 168:    */   protected final double dotProd(Instance inst1, Instance inst2)
/* 169:    */     throws Exception
/* 170:    */   {
/* 171:296 */     double result = 0.0D;
/* 172:    */     
/* 173:    */ 
/* 174:299 */     int n1 = inst1.numValues();
/* 175:300 */     int n2 = inst2.numValues();
/* 176:301 */     int classIndex = this.m_data.classIndex();
/* 177:302 */     int p1 = 0;
/* 178:302 */     for (int p2 = 0; (p1 < n1) && (p2 < n2);)
/* 179:    */     {
/* 180:303 */       int ind1 = inst1.index(p1);
/* 181:304 */       int ind2 = inst2.index(p2);
/* 182:305 */       if (ind1 == ind2)
/* 183:    */       {
/* 184:306 */         if (ind1 != classIndex) {
/* 185:307 */           result += inst1.valueSparse(p1) * inst2.valueSparse(p2);
/* 186:    */         }
/* 187:309 */         p1++;
/* 188:310 */         p2++;
/* 189:    */       }
/* 190:311 */       else if (ind1 > ind2)
/* 191:    */       {
/* 192:312 */         p2++;
/* 193:    */       }
/* 194:    */       else
/* 195:    */       {
/* 196:314 */         p1++;
/* 197:    */       }
/* 198:    */     }
/* 199:317 */     return result;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public void setCacheSize(int value)
/* 203:    */   {
/* 204:326 */     if (value >= -1)
/* 205:    */     {
/* 206:327 */       this.m_cacheSize = value;
/* 207:328 */       clean();
/* 208:    */     }
/* 209:    */     else
/* 210:    */     {
/* 211:330 */       System.out.println("Cache size cannot be smaller than -1 (provided: " + value + ")!");
/* 212:    */     }
/* 213:    */   }
/* 214:    */   
/* 215:    */   public int getCacheSize()
/* 216:    */   {
/* 217:341 */     return this.m_cacheSize;
/* 218:    */   }
/* 219:    */   
/* 220:    */   public String cacheSizeTipText()
/* 221:    */   {
/* 222:351 */     return "The size of the cache (a prime number), 0 for full cache and -1 to turn it off.";
/* 223:    */   }
/* 224:    */   
/* 225:    */   protected void initVars(Instances data)
/* 226:    */   {
/* 227:361 */     super.initVars(data);
/* 228:    */     
/* 229:363 */     this.m_kernelEvals = 0;
/* 230:364 */     this.m_cacheHits = 0;
/* 231:365 */     this.m_numInsts = this.m_data.numInstances();
/* 232:367 */     if (getCacheSize() > 0)
/* 233:    */     {
/* 234:369 */       this.m_storage = new double[this.m_cacheSize * this.m_cacheSlots];
/* 235:370 */       this.m_keys = new long[this.m_cacheSize * this.m_cacheSlots];
/* 236:    */     }
/* 237:    */     else
/* 238:    */     {
/* 239:372 */       this.m_storage = null;
/* 240:373 */       this.m_keys = null;
/* 241:374 */       this.m_kernelMatrix = ((double[][])null);
/* 242:    */     }
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void buildKernel(Instances data)
/* 246:    */     throws Exception
/* 247:    */   {
/* 248:388 */     if (!getChecksTurnedOff()) {
/* 249:389 */       getCapabilities().testWithFail(data);
/* 250:    */     }
/* 251:392 */     initVars(data);
/* 252:    */   }
/* 253:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.CachedKernel
 * JD-Core Version:    0.7.0.1
 */