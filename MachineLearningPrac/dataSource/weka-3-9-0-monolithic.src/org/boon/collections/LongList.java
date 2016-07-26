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
/*  11:    */ import org.boon.primitive.Lng;
/*  12:    */ import org.boon.primitive.Lng.ReduceBy;
/*  13:    */ 
/*  14:    */ public class LongList
/*  15:    */   extends AbstractList<Long>
/*  16:    */ {
/*  17:    */   private long[] values;
/*  18:    */   private int end;
/*  19:    */   
/*  20:    */   public LongList(int capacity)
/*  21:    */   {
/*  22: 70 */     this.values = new long[capacity];
/*  23:    */   }
/*  24:    */   
/*  25:    */   public LongList()
/*  26:    */   {
/*  27: 78 */     this.values = new long[10];
/*  28:    */   }
/*  29:    */   
/*  30:    */   public LongList(long[] values)
/*  31:    */   {
/*  32: 86 */     this.values = values;
/*  33: 87 */     this.end = values.length;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static LongList toLongList(Collection<?> inputList, String propertyPath)
/*  37:    */   {
/*  38: 98 */     if (inputList.size() == 0) {
/*  39: 99 */       return new LongList(0);
/*  40:    */     }
/*  41:102 */     LongList outputList = new LongList(inputList.size());
/*  42:    */     String[] properties;
/*  43:    */     FieldAccess fieldAccess;
/*  44:104 */     if ((propertyPath.contains(".")) || (propertyPath.contains("[")))
/*  45:    */     {
/*  46:106 */       properties = StringScanner.splitByDelimiters(propertyPath, ".[]");
/*  47:108 */       for (Object o : inputList) {
/*  48:109 */         outputList.add(BeanUtils.getPropertyLong(o, properties));
/*  49:    */       }
/*  50:    */     }
/*  51:    */     else
/*  52:    */     {
/*  53:114 */       Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject(inputList.iterator().next());
/*  54:115 */       fieldAccess = (FieldAccess)fields.get(propertyPath);
/*  55:116 */       for (Object o : inputList) {
/*  56:117 */         outputList.add(fieldAccess.getLong(o));
/*  57:    */       }
/*  58:    */     }
/*  59:121 */     return outputList;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public Long get(int index)
/*  63:    */   {
/*  64:132 */     return Long.valueOf(this.values[index]);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public final long getInt(int index)
/*  68:    */   {
/*  69:143 */     return this.values[index];
/*  70:    */   }
/*  71:    */   
/*  72:    */   public boolean add(Long integer)
/*  73:    */   {
/*  74:154 */     if (this.end + 1 >= this.values.length) {
/*  75:155 */       this.values = Lng.grow(this.values);
/*  76:    */     }
/*  77:157 */     this.values[this.end] = integer.longValue();
/*  78:158 */     this.end += 1;
/*  79:159 */     return true;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public boolean addLong(long integer)
/*  83:    */   {
/*  84:169 */     if (this.end + 1 >= this.values.length) {
/*  85:170 */       this.values = Lng.grow(this.values);
/*  86:    */     }
/*  87:172 */     this.values[this.end] = integer;
/*  88:173 */     this.end += 1;
/*  89:174 */     return true;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public LongList add(long integer)
/*  93:    */   {
/*  94:185 */     if (this.end + 1 >= this.values.length) {
/*  95:186 */       this.values = Lng.grow(this.values);
/*  96:    */     }
/*  97:188 */     this.values[this.end] = integer;
/*  98:189 */     this.end += 1;
/*  99:190 */     return this;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public boolean addArray(long... integers)
/* 103:    */   {
/* 104:200 */     if (this.end + integers.length >= this.values.length) {
/* 105:201 */       this.values = Lng.grow(this.values, (this.values.length + integers.length) * 2);
/* 106:    */     }
/* 107:204 */     System.arraycopy(integers, 0, this.values, this.end, integers.length);
/* 108:205 */     this.end += integers.length;
/* 109:206 */     return true;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public Long set(int index, Long element)
/* 113:    */   {
/* 114:218 */     long oldValue = this.values[index];
/* 115:219 */     this.values[index] = element.longValue();
/* 116:220 */     return Long.valueOf(oldValue);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public long setLong(int index, int element)
/* 120:    */   {
/* 121:232 */     long oldValue = this.values[index];
/* 122:233 */     this.values[index] = element;
/* 123:234 */     return oldValue;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public int size()
/* 127:    */   {
/* 128:244 */     return this.end;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public long sum()
/* 132:    */   {
/* 133:253 */     return Lng.sum(this.values, this.end);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public long[] toValueArray()
/* 137:    */   {
/* 138:264 */     return Arrays.copyOfRange(this.values, 0, this.end);
/* 139:    */   }
/* 140:    */   
/* 141:    */   public long reduceBy(Object function)
/* 142:    */   {
/* 143:275 */     return Lng.reduceBy(this.values, this.end, function);
/* 144:    */   }
/* 145:    */   
/* 146:    */   public long reduceBy(Object function, String name)
/* 147:    */   {
/* 148:286 */     return Lng.reduceBy(this.values, this.end, function, name);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public long reduceBy(Lng.ReduceBy reduceBy)
/* 152:    */   {
/* 153:295 */     return Lng.reduceBy(this.values, this.end, reduceBy);
/* 154:    */   }
/* 155:    */   
/* 156:    */   public long mean()
/* 157:    */   {
/* 158:304 */     return Lng.mean(this.values, this.end);
/* 159:    */   }
/* 160:    */   
/* 161:    */   public long standardDeviation()
/* 162:    */   {
/* 163:314 */     return Lng.standardDeviation(this.values, this.end);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public long variance()
/* 167:    */   {
/* 168:324 */     return Lng.variance(this.values, this.end);
/* 169:    */   }
/* 170:    */   
/* 171:    */   public long max()
/* 172:    */   {
/* 173:334 */     return Lng.max(this.values, this.end);
/* 174:    */   }
/* 175:    */   
/* 176:    */   public long min()
/* 177:    */   {
/* 178:344 */     return Lng.min(this.values, this.end);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public long median()
/* 182:    */   {
/* 183:354 */     return Lng.median(this.values, this.end);
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void sort()
/* 187:    */   {
/* 188:364 */     Arrays.sort(this.values, 0, this.end);
/* 189:    */   }
/* 190:    */   
/* 191:    */   public boolean equals(Object o)
/* 192:    */   {
/* 193:369 */     if (this == o) {
/* 194:369 */       return true;
/* 195:    */     }
/* 196:370 */     if ((o == null) || (getClass() != o.getClass())) {
/* 197:370 */       return false;
/* 198:    */     }
/* 199:372 */     LongList longs = (LongList)o;
/* 200:374 */     if (this.end != longs.end) {
/* 201:374 */       return false;
/* 202:    */     }
/* 203:375 */     if (!Lng.equals(0, this.end, this.values, longs.values)) {
/* 204:375 */       return false;
/* 205:    */     }
/* 206:377 */     return true;
/* 207:    */   }
/* 208:    */   
/* 209:    */   public int hashCode()
/* 210:    */   {
/* 211:382 */     int result = 131313;
/* 212:383 */     result = 31 * result + (this.values != null ? Lng.hashCode(0, this.end, this.values) : 0);
/* 213:384 */     result = 31 * result + this.end;
/* 214:385 */     return result;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public void clear()
/* 218:    */   {
/* 219:390 */     this.values = new long[10];
/* 220:391 */     this.end = 0;
/* 221:    */   }
/* 222:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.LongList
 * JD-Core Version:    0.7.0.1
 */