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
/*  11:    */ import org.boon.primitive.Int;
/*  12:    */ import org.boon.primitive.Int.ReduceBy;
/*  13:    */ 
/*  14:    */ public class IntList
/*  15:    */   extends AbstractList<Integer>
/*  16:    */ {
/*  17:    */   private int[] values;
/*  18:    */   private int end;
/*  19:    */   
/*  20:    */   public IntList(int capacity)
/*  21:    */   {
/*  22: 69 */     this.values = new int[capacity];
/*  23:    */   }
/*  24:    */   
/*  25:    */   public IntList()
/*  26:    */   {
/*  27: 77 */     this.values = new int[10];
/*  28:    */   }
/*  29:    */   
/*  30:    */   public IntList(int[] values)
/*  31:    */   {
/*  32: 85 */     this.values = values;
/*  33: 86 */     this.end = values.length;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static IntList toIntList(Collection<?> inputList, String propertyPath)
/*  37:    */   {
/*  38: 97 */     if (inputList.size() == 0) {
/*  39: 98 */       return new IntList(0);
/*  40:    */     }
/*  41:101 */     IntList outputList = new IntList(inputList.size());
/*  42:    */     String[] properties;
/*  43:    */     FieldAccess fieldAccess;
/*  44:103 */     if ((propertyPath.contains(".")) || (propertyPath.contains("[")))
/*  45:    */     {
/*  46:105 */       properties = StringScanner.splitByDelimiters(propertyPath, ".[]");
/*  47:107 */       for (Object o : inputList) {
/*  48:108 */         outputList.add(BeanUtils.getPropertyInt(o, properties));
/*  49:    */       }
/*  50:    */     }
/*  51:    */     else
/*  52:    */     {
/*  53:113 */       Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject(inputList.iterator().next());
/*  54:114 */       fieldAccess = (FieldAccess)fields.get(propertyPath);
/*  55:115 */       for (Object o : inputList) {
/*  56:116 */         outputList.add(fieldAccess.getInt(o));
/*  57:    */       }
/*  58:    */     }
/*  59:120 */     return outputList;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void clear()
/*  63:    */   {
/*  64:124 */     this.values = new int[10];
/*  65:125 */     this.end = 0;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Integer get(int index)
/*  69:    */   {
/*  70:136 */     return Integer.valueOf(this.values[index]);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public final int getInt(int index)
/*  74:    */   {
/*  75:147 */     return this.values[index];
/*  76:    */   }
/*  77:    */   
/*  78:    */   public boolean add(Integer integer)
/*  79:    */   {
/*  80:158 */     if (this.end + 1 >= this.values.length) {
/*  81:159 */       this.values = Int.grow(this.values);
/*  82:    */     }
/*  83:161 */     this.values[this.end] = integer.intValue();
/*  84:162 */     this.end += 1;
/*  85:163 */     return true;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public boolean addInt(int integer)
/*  89:    */   {
/*  90:173 */     if (this.end + 1 >= this.values.length) {
/*  91:174 */       this.values = Int.grow(this.values);
/*  92:    */     }
/*  93:176 */     this.values[this.end] = integer;
/*  94:177 */     this.end += 1;
/*  95:178 */     return true;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public IntList add(int integer)
/*  99:    */   {
/* 100:188 */     if (this.end + 1 >= this.values.length) {
/* 101:189 */       this.values = Int.grow(this.values);
/* 102:    */     }
/* 103:191 */     this.values[this.end] = integer;
/* 104:192 */     this.end += 1;
/* 105:193 */     return this;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public boolean addArray(int... integers)
/* 109:    */   {
/* 110:202 */     if (this.end + integers.length >= this.values.length) {
/* 111:203 */       this.values = Int.grow(this.values, (this.values.length + integers.length) * 2);
/* 112:    */     }
/* 113:206 */     System.arraycopy(integers, 0, this.values, this.end, integers.length);
/* 114:207 */     this.end += integers.length;
/* 115:208 */     return true;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Integer set(int index, Integer element)
/* 119:    */   {
/* 120:220 */     int oldValue = this.values[index];
/* 121:221 */     this.values[index] = element.intValue();
/* 122:222 */     return Integer.valueOf(oldValue);
/* 123:    */   }
/* 124:    */   
/* 125:    */   public int setInt(int index, int element)
/* 126:    */   {
/* 127:234 */     int oldValue = this.values[index];
/* 128:235 */     this.values[index] = element;
/* 129:236 */     return oldValue;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public int size()
/* 133:    */   {
/* 134:246 */     return this.end;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public int sum()
/* 138:    */   {
/* 139:255 */     return Int.sum(this.values, this.end);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public int[] toValueArray()
/* 143:    */   {
/* 144:266 */     return Arrays.copyOfRange(this.values, 0, this.end);
/* 145:    */   }
/* 146:    */   
/* 147:    */   public long reduceBy(Object function)
/* 148:    */   {
/* 149:277 */     return Int.reduceBy(this.values, this.end, function);
/* 150:    */   }
/* 151:    */   
/* 152:    */   public long reduceBy(Object function, String name)
/* 153:    */   {
/* 154:288 */     return Int.reduceBy(this.values, this.end, function, name);
/* 155:    */   }
/* 156:    */   
/* 157:    */   public long reduceBy(Int.ReduceBy reduceBy)
/* 158:    */   {
/* 159:297 */     return Int.reduceBy(this.values, this.end, reduceBy);
/* 160:    */   }
/* 161:    */   
/* 162:    */   public int mean()
/* 163:    */   {
/* 164:306 */     return Int.mean(this.values, this.end);
/* 165:    */   }
/* 166:    */   
/* 167:    */   public int standardDeviation()
/* 168:    */   {
/* 169:316 */     return Int.standardDeviation(this.values, this.end);
/* 170:    */   }
/* 171:    */   
/* 172:    */   public int variance()
/* 173:    */   {
/* 174:326 */     return Int.variance(this.values, this.end);
/* 175:    */   }
/* 176:    */   
/* 177:    */   public int max()
/* 178:    */   {
/* 179:336 */     return Int.max(this.values, this.end);
/* 180:    */   }
/* 181:    */   
/* 182:    */   public int min()
/* 183:    */   {
/* 184:346 */     return Int.min(this.values, this.end);
/* 185:    */   }
/* 186:    */   
/* 187:    */   public int median()
/* 188:    */   {
/* 189:356 */     return Int.median(this.values, this.end);
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void sort()
/* 193:    */   {
/* 194:366 */     Arrays.sort(this.values, 0, this.end);
/* 195:    */   }
/* 196:    */   
/* 197:    */   public boolean equals(Object o)
/* 198:    */   {
/* 199:371 */     if (this == o) {
/* 200:371 */       return true;
/* 201:    */     }
/* 202:372 */     if ((o == null) || (getClass() != o.getClass())) {
/* 203:372 */       return false;
/* 204:    */     }
/* 205:374 */     IntList integers = (IntList)o;
/* 206:376 */     if (this.end != integers.end) {
/* 207:376 */       return false;
/* 208:    */     }
/* 209:377 */     if (!Int.equals(0, this.end, this.values, integers.values)) {
/* 210:377 */       return false;
/* 211:    */     }
/* 212:379 */     return true;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public int hashCode()
/* 216:    */   {
/* 217:384 */     int result = 1;
/* 218:385 */     result = 31 * result + (this.values != null ? Int.hashCode(0, this.end, this.values) : 0);
/* 219:386 */     result = 31 * result + this.end;
/* 220:387 */     return result;
/* 221:    */   }
/* 222:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.IntList
 * JD-Core Version:    0.7.0.1
 */