/*   1:    */ package org.boon.sort;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Collection;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.Comparator;
/*   8:    */ import java.util.Iterator;
/*   9:    */ import java.util.LinkedHashSet;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.Set;
/*  13:    */ import org.boon.Lists;
/*  14:    */ import org.boon.core.Conversions;
/*  15:    */ import org.boon.core.reflection.BeanUtils;
/*  16:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  17:    */ 
/*  18:    */ public class Sort
/*  19:    */ {
/*  20:    */   private final String name;
/*  21:    */   private final SortType type;
/*  22:    */   private final boolean nullsFirst;
/*  23: 58 */   private List<Sort> sorts = new ArrayList();
/*  24: 63 */   private String toString = null;
/*  25: 64 */   private int hashCode = -1;
/*  26:    */   private List<Comparator> comparators;
/*  27:    */   private Comparator comparator;
/*  28:    */   
/*  29:    */   public Sort()
/*  30:    */   {
/*  31: 69 */     this.name = "this";
/*  32: 70 */     this.type = SortType.ASCENDING;
/*  33: 71 */     this.nullsFirst = false;
/*  34: 72 */     this.hashCode = doHashCode();
/*  35:    */   }
/*  36:    */   
/*  37:    */   public Sort(String name, SortType type, boolean nullsFirst)
/*  38:    */   {
/*  39: 76 */     this.name = name;
/*  40: 77 */     this.type = type;
/*  41: 78 */     this.nullsFirst = nullsFirst;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Sort(String name, SortType type)
/*  45:    */   {
/*  46: 83 */     this.name = name;
/*  47: 84 */     this.type = type;
/*  48: 85 */     this.nullsFirst = false;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static Sort sorts(Sort... sorts)
/*  52:    */   {
/*  53:104 */     if ((sorts == null) || (sorts.length == 0)) {
/*  54:105 */       return null;
/*  55:    */     }
/*  56:108 */     Sort main = sorts[0];
/*  57:109 */     for (int index = 1; index < sorts.length; index++) {
/*  58:110 */       main.then(sorts[index]);
/*  59:    */     }
/*  60:112 */     return main;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static Sort asc(String name)
/*  64:    */   {
/*  65:117 */     return new Sort(name, SortType.ASCENDING);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static Sort sortBy(String name)
/*  69:    */   {
/*  70:123 */     return new Sort(name, SortType.ASCENDING);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public static Sort sortByNullsFirst(String name)
/*  74:    */   {
/*  75:129 */     return new Sort(name, SortType.ASCENDING, true);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public static Sort desc(String name)
/*  79:    */   {
/*  80:135 */     return new Sort(name, SortType.DESCENDING);
/*  81:    */   }
/*  82:    */   
/*  83:    */   public static Sort sortByDesc(String name)
/*  84:    */   {
/*  85:141 */     return new Sort(name, SortType.DESCENDING);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public static Sort sortByDescending(String name)
/*  89:    */   {
/*  90:147 */     return new Sort(name, SortType.DESCENDING);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public static Sort sortByDescendingNullsFirst(String name)
/*  94:    */   {
/*  95:152 */     return new Sort(name, SortType.ASCENDING, true);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public static Sort descNullsFirst(String name)
/*  99:    */   {
/* 100:158 */     return new Sort(name, SortType.ASCENDING, true);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public SortType getType()
/* 104:    */   {
/* 105:163 */     return this.type;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public String getName()
/* 109:    */   {
/* 110:167 */     return this.name;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public Sort then(Sort sort)
/* 114:    */   {
/* 115:172 */     this.sorts.add(sort);
/* 116:173 */     return this;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public Sort then(String name)
/* 120:    */   {
/* 121:177 */     this.sorts.add(new Sort(name, SortType.ASCENDING));
/* 122:178 */     return this;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public Sort thenAsc(String name)
/* 126:    */   {
/* 127:182 */     this.sorts.add(new Sort(name, SortType.ASCENDING));
/* 128:183 */     return this;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public Sort thenDesc(String name)
/* 132:    */   {
/* 133:187 */     this.sorts.add(new Sort(name, SortType.DESCENDING));
/* 134:188 */     return this;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void sort(List list, Map<String, FieldAccess> fields)
/* 138:    */   {
/* 139:197 */     Collections.sort(list, comparator(fields));
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void sort(List list)
/* 143:    */   {
/* 144:206 */     if ((list == null) || (list.size() == 0)) {
/* 145:207 */       return;
/* 146:    */     }
/* 147:210 */     Object item = list.iterator().next();
/* 148:    */     
/* 149:212 */     Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject(item);
/* 150:213 */     Collections.sort(list, comparator(fields));
/* 151:    */   }
/* 152:    */   
/* 153:    */   public <T> Collection<T> sort(Class<T> componentClass, Collection<T> collection)
/* 154:    */   {
/* 155:223 */     if ((collection instanceof List))
/* 156:    */     {
/* 157:224 */       sort((List)collection);
/* 158:225 */       return collection;
/* 159:    */     }
/* 160:228 */     if ((collection == null) || (collection.size() == 0)) {
/* 161:229 */       return Collections.EMPTY_LIST;
/* 162:    */     }
/* 163:233 */     Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject(componentClass);
/* 164:234 */     T[] array = Conversions.toArray(componentClass, collection);
/* 165:235 */     Arrays.sort(array, comparator(fields));
/* 166:237 */     if ((collection instanceof Set)) {
/* 167:238 */       return new LinkedHashSet(Lists.list(array));
/* 168:    */     }
/* 169:240 */     return Lists.list(array);
/* 170:    */   }
/* 171:    */   
/* 172:    */   public <T> Iterable<T> sort(Class<T> componentClass, Iterable<T> iterable)
/* 173:    */   {
/* 174:252 */     if ((iterable instanceof List))
/* 175:    */     {
/* 176:253 */       sort((List)iterable);
/* 177:254 */       return iterable;
/* 178:    */     }
/* 179:257 */     if ((iterable instanceof Collection)) {
/* 180:258 */       return sort(componentClass, (Collection)iterable);
/* 181:    */     }
/* 182:261 */     if (iterable == null) {
/* 183:262 */       return Collections.EMPTY_LIST;
/* 184:    */     }
/* 185:265 */     List<T> list = Lists.list(iterable);
/* 186:266 */     sort(list);
/* 187:267 */     return list;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public <T> void sort(T[] array)
/* 191:    */   {
/* 192:276 */     if ((array == null) || (array.length == 0)) {
/* 193:277 */       return;
/* 194:    */     }
/* 195:280 */     Object item = array[0];
/* 196:    */     
/* 197:282 */     Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject(item);
/* 198:283 */     Arrays.sort(array, comparator(fields));
/* 199:    */   }
/* 200:    */   
/* 201:    */   public Comparator comparator(Map<String, FieldAccess> fields)
/* 202:    */   {
/* 203:289 */     if (this.comparator == null) {
/* 204:290 */       this.comparator = UniversalComparator.universalComparator(getName(), fields, getType(), childComparators(fields));
/* 205:    */     }
/* 206:293 */     return this.comparator;
/* 207:    */   }
/* 208:    */   
/* 209:    */   private List<Comparator> childComparators(Map<String, FieldAccess> fields)
/* 210:    */   {
/* 211:302 */     if (this.comparators == null)
/* 212:    */     {
/* 213:303 */       this.comparators = new ArrayList(this.sorts.size() + 1);
/* 214:305 */       for (Sort sort : this.sorts)
/* 215:    */       {
/* 216:306 */         Comparator comparator = UniversalComparator.universalComparator(sort.getName(), fields, sort.getType(), sort.childComparators(fields));
/* 217:    */         
/* 218:    */ 
/* 219:    */ 
/* 220:    */ 
/* 221:    */ 
/* 222:312 */         this.comparators.add(comparator);
/* 223:    */       }
/* 224:    */     }
/* 225:315 */     return this.comparators;
/* 226:    */   }
/* 227:    */   
/* 228:    */   public boolean equals(Object o)
/* 229:    */   {
/* 230:321 */     if (this == o) {
/* 231:321 */       return true;
/* 232:    */     }
/* 233:322 */     if ((o == null) || (getClass() != o.getClass())) {
/* 234:322 */       return false;
/* 235:    */     }
/* 236:324 */     Sort sort = (Sort)o;
/* 237:326 */     if (this.hashCode != sort.hashCode) {
/* 238:326 */       return false;
/* 239:    */     }
/* 240:327 */     if (this.nullsFirst != sort.nullsFirst) {
/* 241:327 */       return false;
/* 242:    */     }
/* 243:328 */     if (this.comparator != null ? !this.comparator.equals(sort.comparator) : sort.comparator != null) {
/* 244:328 */       return false;
/* 245:    */     }
/* 246:329 */     if (this.comparators != null ? !this.comparators.equals(sort.comparators) : sort.comparators != null) {
/* 247:329 */       return false;
/* 248:    */     }
/* 249:330 */     if (this.name != null ? !this.name.equals(sort.name) : sort.name != null) {
/* 250:330 */       return false;
/* 251:    */     }
/* 252:331 */     if (this.sorts != null ? !this.sorts.equals(sort.sorts) : sort.sorts != null) {
/* 253:331 */       return false;
/* 254:    */     }
/* 255:332 */     if (this.toString != null ? !this.toString.equals(sort.toString) : sort.toString != null) {
/* 256:332 */       return false;
/* 257:    */     }
/* 258:333 */     if (this.type != sort.type) {
/* 259:333 */       return false;
/* 260:    */     }
/* 261:335 */     return true;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public int hashCode()
/* 265:    */   {
/* 266:343 */     if (this.hashCode == -1) {
/* 267:344 */       this.hashCode = doHashCode();
/* 268:    */     }
/* 269:346 */     return this.hashCode;
/* 270:    */   }
/* 271:    */   
/* 272:    */   public int doHashCode()
/* 273:    */   {
/* 274:350 */     int result = this.name != null ? this.name.hashCode() : 0;
/* 275:351 */     result = 31 * result + (this.type != null ? this.type.hashCode() : 0);
/* 276:352 */     result = 31 * result + (this.nullsFirst ? 1 : 0);
/* 277:353 */     result = 31 * result + (this.sorts != null ? this.sorts.hashCode() : 0);
/* 278:354 */     result = 31 * result + (this.toString != null ? this.toString.hashCode() : 0);
/* 279:355 */     result = 31 * result + this.hashCode;
/* 280:356 */     result = 31 * result + (this.comparators != null ? this.comparators.hashCode() : 0);
/* 281:357 */     result = 31 * result + (this.comparator != null ? this.comparator.hashCode() : 0);
/* 282:358 */     return result;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public String toString()
/* 286:    */   {
/* 287:364 */     return "Sort{name='" + this.name + '\'' + ", type=" + this.type + ", nullsFirst=" + this.nullsFirst + ", sorts=" + this.sorts + ", toString='" + this.toString + '\'' + ", hashCode=" + this.hashCode + ", comparators=" + this.comparators + ", comparator=" + this.comparator + '}';
/* 288:    */   }
/* 289:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.sort.Sort
 * JD-Core Version:    0.7.0.1
 */