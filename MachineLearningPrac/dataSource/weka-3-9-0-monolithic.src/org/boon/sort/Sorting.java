/*   1:    */ package org.boon.sort;
/*   2:    */ 
/*   3:    */ import java.text.Collator;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Collection;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.Comparator;
/*   8:    */ import java.util.Iterator;
/*   9:    */ import java.util.LinkedHashSet;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.Map.Entry;
/*  13:    */ import org.boon.Boon;
/*  14:    */ import org.boon.Lists;
/*  15:    */ import org.boon.Logger;
/*  16:    */ import org.boon.core.Conversions;
/*  17:    */ import org.boon.core.Typ;
/*  18:    */ import org.boon.core.reflection.BeanUtils;
/*  19:    */ import org.boon.core.reflection.Fields;
/*  20:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  21:    */ 
/*  22:    */ public class Sorting
/*  23:    */ {
/*  24: 48 */   private static final Logger log = Boon.configurableLogger(Sorting.class.getName());
/*  25:    */   
/*  26:    */   public static void sort(List list, Sort... sorts)
/*  27:    */   {
/*  28: 57 */     Sort.sorts(sorts).sort(list);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public static void sort(List list, String sortBy, boolean ascending, boolean nullsFirst)
/*  32:    */   {
/*  33: 70 */     if ((list == null) || (list.size() == 0)) {
/*  34: 71 */       return;
/*  35:    */     }
/*  36: 74 */     if (sortBy.equals("this"))
/*  37:    */     {
/*  38: 76 */       Collections.sort(list, thisUniversalComparator(ascending, nullsFirst));
/*  39: 77 */       return;
/*  40:    */     }
/*  41: 79 */     Iterator iterator = list.iterator();
/*  42: 80 */     Object object = iterator.next();
/*  43:    */     
/*  44: 82 */     Map<String, FieldAccess> fields = null;
/*  45: 84 */     if (object != null) {
/*  46: 85 */       fields = BeanUtils.getFieldsFromObject(object);
/*  47:    */     } else {
/*  48: 87 */       while (iterator.hasNext())
/*  49:    */       {
/*  50: 89 */         object = iterator.next();
/*  51: 90 */         if (object != null) {
/*  52: 91 */           fields = BeanUtils.getFieldsFromObject(object);
/*  53:    */         }
/*  54:    */       }
/*  55:    */     }
/*  56: 99 */     if (fields != null)
/*  57:    */     {
/*  58:101 */       FieldAccess field = (FieldAccess)fields.get(sortBy);
/*  59:103 */       if (field != null) {
/*  60:105 */         Collections.sort(list, universalComparator(field, ascending, nullsFirst));
/*  61:    */       }
/*  62:    */     }
/*  63:    */   }
/*  64:    */   
/*  65:    */   public static <V> Collection<V> sort(Class<V> componentType, Collection<V> collection, String sortBy, boolean ascending, boolean nullsFirst)
/*  66:    */   {
/*  67:121 */     if ((collection instanceof List))
/*  68:    */     {
/*  69:122 */       sort((List)collection, sortBy, ascending, nullsFirst);
/*  70:123 */       return collection;
/*  71:    */     }
/*  72:125 */     V[] array = Conversions.toArray(componentType, collection);
/*  73:126 */     sort(array, sortBy, ascending, nullsFirst);
/*  74:127 */     if ((collection instanceof LinkedHashSet)) {
/*  75:128 */       return new LinkedHashSet(Lists.list(array));
/*  76:    */     }
/*  77:130 */     return Lists.list(array);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static <K, V> Collection<Map.Entry<K, V>> sortEntries(Class<V> componentType, Map<K, V> map, String sortBy, boolean ascending, boolean nullsFirst)
/*  81:    */   {
/*  82:146 */     return sort(componentType, map.entrySet(), sortBy, ascending, nullsFirst);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static <K, V> Collection<Map.Entry<K, V>> sortValues(Class<V> componentType, Map<K, V> map, String sortBy, boolean ascending, boolean nullsFirst)
/*  86:    */   {
/*  87:160 */     return sort(componentType, map.values(), sortBy, ascending, nullsFirst);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static <K, V> Collection<Map.Entry<K, V>> sortKeys(Class<V> componentType, Map<K, V> map, String sortBy, boolean ascending, boolean nullsFirst)
/*  91:    */   {
/*  92:175 */     return sort(componentType, map.keySet(), sortBy, ascending, nullsFirst);
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static <V> Iterable<V> sort(Class<V> componentType, Iterable<V> iterable, String sortBy, boolean ascending, boolean nullsFirst)
/*  96:    */   {
/*  97:192 */     if ((iterable instanceof List))
/*  98:    */     {
/*  99:193 */       sort((List)iterable, sortBy, ascending, nullsFirst);
/* 100:194 */       return iterable;
/* 101:    */     }
/* 102:195 */     if ((iterable instanceof Collection)) {
/* 103:196 */       return sort(componentType, (Collection)iterable, sortBy, ascending, nullsFirst);
/* 104:    */     }
/* 105:198 */     List<V> list = Lists.list(iterable);
/* 106:199 */     sort(list, sortBy, ascending, nullsFirst);
/* 107:200 */     return list;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public static <T> void sort(T[] array, String sortBy, boolean ascending, boolean nullsFirst)
/* 111:    */   {
/* 112:212 */     if ((array == null) || (array.length == 0)) {
/* 113:213 */       return;
/* 114:    */     }
/* 115:216 */     if (sortBy.equals("this"))
/* 116:    */     {
/* 117:218 */       Arrays.sort(array, thisUniversalComparator(ascending, nullsFirst));
/* 118:219 */       return;
/* 119:    */     }
/* 120:222 */     Object object = array[0];
/* 121:    */     
/* 122:224 */     Map<String, FieldAccess> fields = null;
/* 123:226 */     if (object != null) {
/* 124:227 */       fields = BeanUtils.getFieldsFromObject(object);
/* 125:    */     } else {
/* 126:229 */       for (int index = 1; index < array.length; index++)
/* 127:    */       {
/* 128:231 */         object = array[index];
/* 129:232 */         if (object != null)
/* 130:    */         {
/* 131:233 */           fields = BeanUtils.getFieldsFromObject(object);
/* 132:234 */           break;
/* 133:    */         }
/* 134:    */       }
/* 135:    */     }
/* 136:241 */     if (fields != null)
/* 137:    */     {
/* 138:243 */       FieldAccess field = (FieldAccess)fields.get(sortBy);
/* 139:245 */       if (field != null) {
/* 140:247 */         Arrays.sort(array, universalComparator(field, ascending, nullsFirst));
/* 141:    */       }
/* 142:    */     }
/* 143:    */   }
/* 144:    */   
/* 145:    */   public static void sort(List list)
/* 146:    */   {
/* 147:260 */     sort(list, "this", true, false);
/* 148:    */   }
/* 149:    */   
/* 150:    */   public static <T> void sort(T[] array)
/* 151:    */   {
/* 152:269 */     sort(array, "this", true, false);
/* 153:    */   }
/* 154:    */   
/* 155:    */   public static <T> Collection<T> sort(Class<T> componentType, Collection<T> collection)
/* 156:    */   {
/* 157:278 */     return sort(componentType, collection, "this", true, false);
/* 158:    */   }
/* 159:    */   
/* 160:    */   public static <T> Iterable<T> sort(Class<T> componentType, Iterable<T> iterable)
/* 161:    */   {
/* 162:287 */     return sort(componentType, iterable, "this", true, false);
/* 163:    */   }
/* 164:    */   
/* 165:    */   public static void sortNullsFirst(List list)
/* 166:    */   {
/* 167:299 */     sort(list, "this", true, true);
/* 168:    */   }
/* 169:    */   
/* 170:    */   public static <T> void sortNullsFirst(T[] array)
/* 171:    */   {
/* 172:308 */     sort(array, "this", true, true);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public static <T> Collection<T> sortNullsFirst(Class<T> componentType, Collection<T> collection)
/* 176:    */   {
/* 177:317 */     return sort(componentType, collection, "this", true, true);
/* 178:    */   }
/* 179:    */   
/* 180:    */   public static <T> Iterable<T> sortNullsFirst(Class<T> componentType, Iterable<T> iterable)
/* 181:    */   {
/* 182:327 */     return sort(componentType, iterable, "this", true, true);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public static void sortDesc(List list)
/* 186:    */   {
/* 187:340 */     sort(list, "this", false, false);
/* 188:    */   }
/* 189:    */   
/* 190:    */   public static <T> void sortDesc(T[] array)
/* 191:    */   {
/* 192:350 */     sort(array, "this", false, false);
/* 193:    */   }
/* 194:    */   
/* 195:    */   public static <T> Iterable<T> sortDesc(Class<T> componentType, Iterable<T> iterable)
/* 196:    */   {
/* 197:360 */     return sort(componentType, iterable, "this", false, false);
/* 198:    */   }
/* 199:    */   
/* 200:    */   public static <T> Collection<T> sortDesc(Class<T> componentType, Collection<T> collection)
/* 201:    */   {
/* 202:369 */     return sort(componentType, collection, "this", false, false);
/* 203:    */   }
/* 204:    */   
/* 205:    */   public static void sortDescNullsFirst(List list)
/* 206:    */   {
/* 207:382 */     sort(list, "this", false, true);
/* 208:    */   }
/* 209:    */   
/* 210:    */   public static <T> void sortDescNullsFirst(T[] array)
/* 211:    */   {
/* 212:394 */     sort(array, "this", false, true);
/* 213:    */   }
/* 214:    */   
/* 215:    */   public static <T> Iterable<T> sortDescNullsFirst(Class<T> componentType, Iterable<T> iterable)
/* 216:    */   {
/* 217:404 */     return sort(componentType, iterable, "this", false, true);
/* 218:    */   }
/* 219:    */   
/* 220:    */   public static <T> Collection<T> sortDescNullsFirst(Class<T> componentType, Collection<T> collection)
/* 221:    */   {
/* 222:413 */     return sort(componentType, collection, "this", false, true);
/* 223:    */   }
/* 224:    */   
/* 225:    */   public static void sort(List list, String sortBy)
/* 226:    */   {
/* 227:425 */     sort(list, sortBy, true, false);
/* 228:    */   }
/* 229:    */   
/* 230:    */   public static <T> void sort(T[] array, String sortBy)
/* 231:    */   {
/* 232:439 */     sort(array, sortBy, true, false);
/* 233:    */   }
/* 234:    */   
/* 235:    */   public static <T> Iterable<T> sort(Class<T> componentType, Iterable<T> iterable, String sortBy)
/* 236:    */   {
/* 237:451 */     return sort(componentType, iterable, sortBy, true, false);
/* 238:    */   }
/* 239:    */   
/* 240:    */   public static <T> Collection<T> sort(Class<T> componentType, Collection<T> collection, String sortBy)
/* 241:    */   {
/* 242:463 */     return sort(componentType, collection, sortBy, true, false);
/* 243:    */   }
/* 244:    */   
/* 245:    */   public static void sortNullsFirst(List list, String sortBy)
/* 246:    */   {
/* 247:477 */     sort(list, sortBy, true, true);
/* 248:    */   }
/* 249:    */   
/* 250:    */   public static <T> void sortNullsFirst(T[] array, String sortBy)
/* 251:    */   {
/* 252:490 */     sort(array, sortBy, true, true);
/* 253:    */   }
/* 254:    */   
/* 255:    */   public static <T> Iterable<T> sortNullsFirst(Class<T> componentType, Iterable<T> iterable, String sortBy)
/* 256:    */   {
/* 257:502 */     return sort(componentType, iterable, sortBy, true, true);
/* 258:    */   }
/* 259:    */   
/* 260:    */   public static <T> Collection<T> sortNullsFirst(Class<T> componentType, Collection<T> collection, String sortBy)
/* 261:    */   {
/* 262:514 */     return sort(componentType, collection, sortBy, true, true);
/* 263:    */   }
/* 264:    */   
/* 265:    */   public static void sortDesc(List list, String sortBy)
/* 266:    */   {
/* 267:525 */     sort(list, sortBy, false, false);
/* 268:    */   }
/* 269:    */   
/* 270:    */   public static <T> void sortDesc(T[] array, String sortBy)
/* 271:    */   {
/* 272:539 */     sort(array, sortBy, false, false);
/* 273:    */   }
/* 274:    */   
/* 275:    */   public static <T> Iterable<T> sortDesc(Class<T> componentType, Iterable<T> iterable, String sortBy)
/* 276:    */   {
/* 277:551 */     return sort(componentType, iterable, sortBy, false, false);
/* 278:    */   }
/* 279:    */   
/* 280:    */   public static <T> Collection<T> sortDesc(Class<T> componentType, Collection<T> collection, String sortBy)
/* 281:    */   {
/* 282:563 */     return sort(componentType, collection, sortBy, false, false);
/* 283:    */   }
/* 284:    */   
/* 285:    */   public static void sortDescNullsFirst(List list, String sortBy)
/* 286:    */   {
/* 287:575 */     sort(list, sortBy, false, true);
/* 288:    */   }
/* 289:    */   
/* 290:    */   public static <T> void sortDescNullsFirst(T[] array, String sortBy)
/* 291:    */   {
/* 292:587 */     sort(array, sortBy, false, true);
/* 293:    */   }
/* 294:    */   
/* 295:    */   public static <T> Iterable<T> sortDescNullsFirst(Class<T> componentType, Iterable<T> iterable, String sortBy)
/* 296:    */   {
/* 297:599 */     return sort(componentType, iterable, sortBy, false, true);
/* 298:    */   }
/* 299:    */   
/* 300:    */   public static <T> Collection<T> sortDescNullsFirst(Class<T> componentType, Collection<T> collection, String sortBy)
/* 301:    */   {
/* 302:611 */     return sort(componentType, collection, sortBy, false, true);
/* 303:    */   }
/* 304:    */   
/* 305:    */   public static Comparator universalComparator(final FieldAccess field, boolean ascending, final boolean nullsFirst)
/* 306:    */   {
/* 307:625 */     new Comparator()
/* 308:    */     {
/* 309:    */       public int compare(Object o1, Object o2)
/* 310:    */       {
/* 311:628 */         Object value1 = null;
/* 312:629 */         Object value2 = null;
/* 313:631 */         if (this.val$ascending)
/* 314:    */         {
/* 315:632 */           value1 = field.getValue(o1);
/* 316:633 */           value2 = field.getValue(o2);
/* 317:    */         }
/* 318:    */         else
/* 319:    */         {
/* 320:635 */           value1 = field.getValue(o2);
/* 321:636 */           value2 = field.getValue(o1);
/* 322:    */         }
/* 323:638 */         return Sorting.compare(value1, value2, nullsFirst);
/* 324:    */       }
/* 325:    */     };
/* 326:    */   }
/* 327:    */   
/* 328:    */   public static Comparator thisUniversalComparator(boolean ascending, final boolean nullsFirst)
/* 329:    */   {
/* 330:653 */     new Comparator()
/* 331:    */     {
/* 332:    */       public int compare(Object o1, Object o2)
/* 333:    */       {
/* 334:    */         Object value2;
/* 335:    */         Object value1;
/* 336:    */         Object value2;
/* 337:660 */         if (this.val$ascending)
/* 338:    */         {
/* 339:661 */           Object value1 = o1;
/* 340:662 */           value2 = o2;
/* 341:    */         }
/* 342:    */         else
/* 343:    */         {
/* 344:664 */           value1 = o2;
/* 345:665 */           value2 = o1;
/* 346:    */         }
/* 347:668 */         return Sorting.compare(value1, value2, nullsFirst);
/* 348:    */       }
/* 349:    */     };
/* 350:    */   }
/* 351:    */   
/* 352:    */   public static int compare(Object value1, Object value2)
/* 353:    */   {
/* 354:680 */     return compare(value1, value2, false);
/* 355:    */   }
/* 356:    */   
/* 357:    */   public static int compare(Object value1, Object value2, boolean nullsLast)
/* 358:    */   {
/* 359:696 */     if ((value1 == null) && (value2 == null)) {
/* 360:697 */       return 0;
/* 361:    */     }
/* 362:698 */     if ((value1 == null) && (value2 != null)) {
/* 363:699 */       return nullsLast ? -1 : 1;
/* 364:    */     }
/* 365:700 */     if ((value1 != null) && (value2 == null)) {
/* 366:701 */       return nullsLast ? 1 : -1;
/* 367:    */     }
/* 368:706 */     if ((value1 instanceof CharSequence))
/* 369:    */     {
/* 370:707 */       String str1 = Conversions.toString(value1);
/* 371:708 */       String str2 = Conversions.toString(value2);
/* 372:709 */       Collator collator = Collator.getInstance();
/* 373:710 */       return collator.compare(str1, str2);
/* 374:    */     }
/* 375:713 */     if ((Typ.isComparable(value1)) && (value1.getClass() == value2.getClass()))
/* 376:    */     {
/* 377:714 */       Comparable c1 = Conversions.comparable(value1);
/* 378:715 */       Comparable c2 = Conversions.comparable(value2);
/* 379:716 */       return c1.compareTo(c2);
/* 380:    */     }
/* 381:717 */     if (((value1 instanceof Integer)) && ((value2 instanceof Integer)))
/* 382:    */     {
/* 383:718 */       Comparable c1 = Conversions.comparable(value1);
/* 384:719 */       Comparable c2 = Conversions.comparable(value2);
/* 385:720 */       return c1.compareTo(c2);
/* 386:    */     }
/* 387:722 */     if (((value1 instanceof Double)) && ((value2 instanceof Double)))
/* 388:    */     {
/* 389:723 */       Comparable c1 = Conversions.comparable(value1);
/* 390:724 */       Comparable c2 = Conversions.comparable(value2);
/* 391:725 */       return c1.compareTo(c2);
/* 392:    */     }
/* 393:727 */     if (((value1 instanceof Long)) && ((value2 instanceof Long)))
/* 394:    */     {
/* 395:728 */       Comparable c1 = Conversions.comparable(value1);
/* 396:729 */       Comparable c2 = Conversions.comparable(value2);
/* 397:730 */       return c1.compareTo(c2);
/* 398:    */     }
/* 399:732 */     if (((value1 instanceof Number)) && ((value2 instanceof Number)))
/* 400:    */     {
/* 401:733 */       Double c1 = Double.valueOf(Conversions.toDouble(value1));
/* 402:734 */       Double c2 = Double.valueOf(Conversions.toDouble(value2));
/* 403:735 */       return c1.compareTo(c2);
/* 404:    */     }
/* 405:742 */     String name = Fields.getSortableField(value1);
/* 406:743 */     String sv1 = (String)BeanUtils.getPropByPath(value1, new String[] { name });
/* 407:744 */     String sv2 = (String)BeanUtils.getPropByPath(value2, new String[] { name });
/* 408:745 */     return compare(sv1, sv2);
/* 409:    */   }
/* 410:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.sort.Sorting
 * JD-Core Version:    0.7.0.1
 */