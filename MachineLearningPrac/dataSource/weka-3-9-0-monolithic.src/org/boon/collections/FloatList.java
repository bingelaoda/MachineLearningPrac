/*   1:    */ package org.boon.collections;
/*   2:    */ 
/*   3:    */ import java.util.AbstractList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Collection;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.Map;
/*   8:    */ import org.boon.StringScanner;
/*   9:    */ import org.boon.core.reflection.BeanUtils;
/*  10:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  11:    */ import org.boon.primitive.Flt;
/*  12:    */ import org.boon.primitive.Flt.ReduceBy;
/*  13:    */ 
/*  14:    */ public class FloatList
/*  15:    */   extends AbstractList<Float>
/*  16:    */ {
/*  17:    */   private float[] values;
/*  18:    */   private int end;
/*  19:    */   
/*  20:    */   public FloatList(int capacity)
/*  21:    */   {
/*  22: 69 */     this.values = new float[capacity];
/*  23:    */   }
/*  24:    */   
/*  25:    */   public FloatList()
/*  26:    */   {
/*  27: 77 */     this.values = new float[10];
/*  28:    */   }
/*  29:    */   
/*  30:    */   public FloatList(float[] values)
/*  31:    */   {
/*  32: 85 */     this.values = values;
/*  33: 86 */     this.end = values.length;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static FloatList toFloatList(Collection<?> inputList, String propertyPath)
/*  37:    */   {
/*  38: 97 */     if (inputList.size() == 0) {
/*  39: 98 */       return new FloatList(0);
/*  40:    */     }
/*  41:101 */     FloatList outputList = new FloatList(inputList.size());
/*  42:    */     String[] properties;
/*  43:    */     FieldAccess fieldAccess;
/*  44:103 */     if ((propertyPath.contains(".")) || (propertyPath.contains("[")))
/*  45:    */     {
/*  46:105 */       properties = StringScanner.splitByDelimiters(propertyPath, ".[]");
/*  47:107 */       for (Object o : inputList) {
/*  48:108 */         outputList.add(BeanUtils.getPropertyFloat(o, properties));
/*  49:    */       }
/*  50:    */     }
/*  51:    */     else
/*  52:    */     {
/*  53:113 */       Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject(inputList.iterator().next());
/*  54:114 */       fieldAccess = (FieldAccess)fields.get(propertyPath);
/*  55:115 */       for (Object o : inputList) {
/*  56:116 */         outputList.add(fieldAccess.getFloat(o));
/*  57:    */       }
/*  58:    */     }
/*  59:120 */     return outputList;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public Float get(int index)
/*  63:    */   {
/*  64:131 */     return Float.valueOf(this.values[index]);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public float idx(int index)
/*  68:    */   {
/*  69:142 */     return this.values[index];
/*  70:    */   }
/*  71:    */   
/*  72:    */   public float atIndex(int index)
/*  73:    */   {
/*  74:153 */     return this.values[index];
/*  75:    */   }
/*  76:    */   
/*  77:    */   public final float getFloat(int index)
/*  78:    */   {
/*  79:164 */     return this.values[index];
/*  80:    */   }
/*  81:    */   
/*  82:    */   public boolean add(Float integer)
/*  83:    */   {
/*  84:175 */     if (this.end + 1 >= this.values.length) {
/*  85:176 */       this.values = Flt.grow(this.values);
/*  86:    */     }
/*  87:178 */     this.values[this.end] = integer.floatValue();
/*  88:179 */     this.end += 1;
/*  89:180 */     return true;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public boolean addFloat(float value)
/*  93:    */   {
/*  94:190 */     if (this.end + 1 >= this.values.length) {
/*  95:191 */       this.values = Flt.grow(this.values);
/*  96:    */     }
/*  97:193 */     this.values[this.end] = value;
/*  98:194 */     this.end += 1;
/*  99:195 */     return true;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public FloatList add(float integer)
/* 103:    */   {
/* 104:205 */     if (this.end + 1 >= this.values.length) {
/* 105:206 */       this.values = Flt.grow(this.values);
/* 106:    */     }
/* 107:208 */     this.values[this.end] = integer;
/* 108:209 */     this.end += 1;
/* 109:210 */     return this;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public boolean addArray(float... integers)
/* 113:    */   {
/* 114:219 */     if (this.end + integers.length >= this.values.length) {
/* 115:220 */       this.values = Flt.grow(this.values, (this.values.length + integers.length) * 2);
/* 116:    */     }
/* 117:223 */     System.arraycopy(integers, 0, this.values, this.end, integers.length);
/* 118:224 */     this.end += integers.length;
/* 119:225 */     return true;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public Float set(int index, Float element)
/* 123:    */   {
/* 124:237 */     float oldValue = this.values[index];
/* 125:238 */     this.values[index] = element.floatValue();
/* 126:239 */     return Float.valueOf(oldValue);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public float idx(int index, float element)
/* 130:    */   {
/* 131:251 */     float oldValue = this.values[index];
/* 132:252 */     this.values[index] = element;
/* 133:253 */     return oldValue;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public float atIndex(int index, float element)
/* 137:    */   {
/* 138:265 */     float oldValue = this.values[index];
/* 139:266 */     this.values[index] = element;
/* 140:267 */     return oldValue;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public float setFloat(int index, float element)
/* 144:    */   {
/* 145:279 */     float oldValue = this.values[index];
/* 146:280 */     this.values[index] = element;
/* 147:281 */     return oldValue;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public int size()
/* 151:    */   {
/* 152:291 */     return this.end;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public float sum()
/* 156:    */   {
/* 157:300 */     return Flt.sum(this.values, this.end);
/* 158:    */   }
/* 159:    */   
/* 160:    */   public float[] toValueArray()
/* 161:    */   {
/* 162:311 */     return Arrays.copyOfRange(this.values, 0, this.end);
/* 163:    */   }
/* 164:    */   
/* 165:    */   public double reduceBy(Object function)
/* 166:    */   {
/* 167:322 */     return Flt.reduceBy(this.values, this.end, function);
/* 168:    */   }
/* 169:    */   
/* 170:    */   public double reduceBy(Object function, String name)
/* 171:    */   {
/* 172:333 */     return Flt.reduceBy(this.values, this.end, function, name);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public double reduceBy(Flt.ReduceBy reduceBy)
/* 176:    */   {
/* 177:342 */     return Flt.reduceBy(this.values, this.end, reduceBy);
/* 178:    */   }
/* 179:    */   
/* 180:    */   public float mean()
/* 181:    */   {
/* 182:351 */     return Flt.mean(this.values, this.end);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public float standardDeviation()
/* 186:    */   {
/* 187:361 */     return Flt.standardDeviation(this.values, this.end);
/* 188:    */   }
/* 189:    */   
/* 190:    */   public float variance()
/* 191:    */   {
/* 192:371 */     return Flt.variance(this.values, this.end);
/* 193:    */   }
/* 194:    */   
/* 195:    */   public float max()
/* 196:    */   {
/* 197:381 */     return Flt.max(this.values, this.end);
/* 198:    */   }
/* 199:    */   
/* 200:    */   public float min()
/* 201:    */   {
/* 202:391 */     return Flt.min(this.values, this.end);
/* 203:    */   }
/* 204:    */   
/* 205:    */   public float median()
/* 206:    */   {
/* 207:401 */     return Flt.median(this.values, this.end);
/* 208:    */   }
/* 209:    */   
/* 210:    */   public void sort()
/* 211:    */   {
/* 212:411 */     Arrays.sort(this.values, 0, this.end);
/* 213:    */   }
/* 214:    */   
/* 215:    */   public boolean equals(Object o)
/* 216:    */   {
/* 217:416 */     if (this == o) {
/* 218:416 */       return true;
/* 219:    */     }
/* 220:417 */     if ((o == null) || (getClass() != o.getClass())) {
/* 221:417 */       return false;
/* 222:    */     }
/* 223:419 */     FloatList values = (FloatList)o;
/* 224:421 */     if (this.end != values.end) {
/* 225:421 */       return false;
/* 226:    */     }
/* 227:422 */     if (!Flt.equals(0, this.end, this.values, values.values)) {
/* 228:422 */       return false;
/* 229:    */     }
/* 230:424 */     return true;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public int hashCode()
/* 234:    */   {
/* 235:429 */     int result = 1;
/* 236:430 */     result = 31 * result + (this.values != null ? Flt.hashCode(0, this.end, this.values) : 0);
/* 237:431 */     result = 31 * result + this.end;
/* 238:432 */     return result;
/* 239:    */   }
/* 240:    */   
/* 241:    */   public void clear()
/* 242:    */   {
/* 243:437 */     this.values = new float[10];
/* 244:438 */     this.end = 0;
/* 245:    */   }
/* 246:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.FloatList
 * JD-Core Version:    0.7.0.1
 */