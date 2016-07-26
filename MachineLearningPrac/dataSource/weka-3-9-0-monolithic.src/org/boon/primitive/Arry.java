/*   1:    */ package org.boon.primitive;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Array;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import org.boon.Exceptions;
/*   9:    */ import org.boon.Lists;
/*  10:    */ import org.boon.Universal;
/*  11:    */ import org.boon.core.reflection.Invoker;
/*  12:    */ import org.boon.core.reflection.MapObjectConversion;
/*  13:    */ 
/*  14:    */ public class Arry
/*  15:    */ {
/*  16:    */   @Universal
/*  17:    */   public static <V> V[] array(Class<V> clasz, int size)
/*  18:    */   {
/*  19: 50 */     Object newArray = Array.newInstance(clasz, size);
/*  20: 51 */     return (Object[])newArray;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public static <V> V[] grow(V[] array, int size)
/*  24:    */   {
/*  25: 56 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length + size);
/*  26:    */     
/*  27: 58 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  28: 59 */     return (Object[])newArray;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public static <V> V[] grow(V[] array)
/*  32:    */   {
/*  33: 64 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length * 2);
/*  34:    */     
/*  35: 66 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  36: 67 */     return (Object[])newArray;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static <V> V[] shrink(V[] array, int size)
/*  40:    */   {
/*  41: 71 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length - size);
/*  42:    */     
/*  43: 73 */     System.arraycopy(array, 0, newArray, 0, array.length - size);
/*  44: 74 */     return (Object[])newArray;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public static <V> V[] compact(V[] array)
/*  48:    */   {
/*  49: 79 */     int nullCount = 0;
/*  50: 80 */     for (V anArray1 : array) {
/*  51: 82 */       if (anArray1 == null) {
/*  52: 83 */         nullCount++;
/*  53:    */       }
/*  54:    */     }
/*  55: 86 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length - nullCount);
/*  56:    */     
/*  57:    */ 
/*  58: 89 */     int j = 0;
/*  59: 90 */     for (V anArray : array) {
/*  60: 92 */       if (anArray != null)
/*  61:    */       {
/*  62: 96 */         Array.set(newArray, j, anArray);
/*  63: 97 */         j++;
/*  64:    */       }
/*  65:    */     }
/*  66: 99 */     return (Object[])newArray;
/*  67:    */   }
/*  68:    */   
/*  69:    */   @SafeVarargs
/*  70:    */   public static <V> V[] array(V... array)
/*  71:    */   {
/*  72:104 */     return array;
/*  73:    */   }
/*  74:    */   
/*  75:    */   @Universal
/*  76:    */   public static <V> int len(V[] array)
/*  77:    */   {
/*  78:112 */     return array.length;
/*  79:    */   }
/*  80:    */   
/*  81:    */   @Universal
/*  82:    */   public static <V> int lengthOf(V[] array)
/*  83:    */   {
/*  84:117 */     return array.length;
/*  85:    */   }
/*  86:    */   
/*  87:    */   @Universal
/*  88:    */   public static <V> V idx(V[] array, int index)
/*  89:    */   {
/*  90:122 */     int i = calculateIndex(array, index);
/*  91:    */     
/*  92:124 */     return array[i];
/*  93:    */   }
/*  94:    */   
/*  95:    */   @Universal
/*  96:    */   public static Object idx(Object array, int index)
/*  97:    */   {
/*  98:129 */     int i = calculateIndex(array, index);
/*  99:    */     
/* 100:131 */     return Array.get(array, i);
/* 101:    */   }
/* 102:    */   
/* 103:    */   @Universal
/* 104:    */   public static Object fastIndex(Object array, int index)
/* 105:    */   {
/* 106:137 */     int i = calculateIndex(array, index);
/* 107:    */     
/* 108:139 */     return Array.get(array, index);
/* 109:    */   }
/* 110:    */   
/* 111:    */   @Universal
/* 112:    */   public static <V> V atIndex(V[] array, int index)
/* 113:    */   {
/* 114:145 */     int i = calculateIndex(array, index);
/* 115:    */     
/* 116:147 */     return array[i];
/* 117:    */   }
/* 118:    */   
/* 119:    */   @Universal
/* 120:    */   public static <V> void idx(V[] array, int index, V value)
/* 121:    */   {
/* 122:152 */     int i = calculateIndex(array, index);
/* 123:    */     
/* 124:154 */     array[i] = value;
/* 125:    */   }
/* 126:    */   
/* 127:    */   @Universal
/* 128:    */   public static <V> void atIndex(V[] array, int index, V value)
/* 129:    */   {
/* 130:159 */     int i = calculateIndex(array, index);
/* 131:    */     
/* 132:161 */     array[i] = value;
/* 133:    */   }
/* 134:    */   
/* 135:    */   @Universal
/* 136:    */   public static <V> V[] sliceOf(V[] array, int startIndex, int endIndex)
/* 137:    */   {
/* 138:168 */     return slc(array, startIndex, endIndex);
/* 139:    */   }
/* 140:    */   
/* 141:    */   @Universal
/* 142:    */   public static <V> V[] slc(V[] array, int startIndex, int endIndex)
/* 143:    */   {
/* 144:174 */     int start = calculateIndex(array, startIndex);
/* 145:175 */     int end = calculateEndIndex(array, endIndex);
/* 146:176 */     int newLength = end - start;
/* 147:177 */     if (newLength < 0) {
/* 148:178 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, end index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/* 149:    */     }
/* 150:184 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), newLength);
/* 151:185 */     System.arraycopy(array, start, newArray, 0, newLength);
/* 152:186 */     return (Object[])newArray;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public static <V> V[] fastSlice(V[] array, int start, int end)
/* 156:    */   {
/* 157:192 */     int newLength = end - start;
/* 158:    */     
/* 159:    */ 
/* 160:195 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), newLength);
/* 161:196 */     System.arraycopy(array, start, newArray, 0, newLength);
/* 162:197 */     return (Object[])newArray;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public static Object[] fastObjectArraySlice(Object[] array, int start, int end)
/* 166:    */   {
/* 167:202 */     int newLength = end - start;
/* 168:    */     
/* 169:204 */     Object[] newArray = new Object[newLength];
/* 170:205 */     System.arraycopy(array, start, newArray, 0, newLength);
/* 171:206 */     return newArray;
/* 172:    */   }
/* 173:    */   
/* 174:    */   @Universal
/* 175:    */   public static <V> boolean in(V value, V[] array)
/* 176:    */   {
/* 177:211 */     for (V currentValue : array) {
/* 178:212 */       if (currentValue.equals(value)) {
/* 179:213 */         return true;
/* 180:    */       }
/* 181:    */     }
/* 182:216 */     return false;
/* 183:    */   }
/* 184:    */   
/* 185:    */   @Universal
/* 186:    */   public static <V> V[] sliceOf(V[] array, int startIndex)
/* 187:    */   {
/* 188:222 */     return slc(array, startIndex);
/* 189:    */   }
/* 190:    */   
/* 191:    */   @Universal
/* 192:    */   public static <V> V[] slc(V[] array, int startIndex)
/* 193:    */   {
/* 194:229 */     int start = calculateIndex(array, startIndex);
/* 195:230 */     int newLength = array.length - start;
/* 196:232 */     if (newLength < 0) {
/* 197:233 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(array.length) }));
/* 198:    */     }
/* 199:239 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), newLength);
/* 200:240 */     System.arraycopy(array, start, newArray, 0, newLength);
/* 201:241 */     return (Object[])newArray;
/* 202:    */   }
/* 203:    */   
/* 204:    */   @Universal
/* 205:    */   public static <V> V[] copy(V[] array)
/* 206:    */   {
/* 207:247 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length);
/* 208:248 */     System.arraycopy(array, 0, newArray, 0, array.length);
/* 209:249 */     return (Object[])newArray;
/* 210:    */   }
/* 211:    */   
/* 212:    */   @Universal
/* 213:    */   public static <V> V[] add(V[] array, V v)
/* 214:    */   {
/* 215:255 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length + 1);
/* 216:256 */     System.arraycopy(array, 0, newArray, 0, array.length);
/* 217:257 */     Array.set(newArray, array.length, v);
/* 218:258 */     return (Object[])newArray;
/* 219:    */   }
/* 220:    */   
/* 221:    */   @Universal
/* 222:    */   public static <V> V[] add(V[] array, V[] array2)
/* 223:    */   {
/* 224:264 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length + array2.length);
/* 225:265 */     System.arraycopy(array, 0, newArray, 0, array.length);
/* 226:266 */     System.arraycopy(array2, 0, newArray, array.length, array2.length);
/* 227:    */     
/* 228:268 */     return (Object[])newArray;
/* 229:    */   }
/* 230:    */   
/* 231:    */   @Universal
/* 232:    */   public static <V> V[] insert(V[] array, int index, V v)
/* 233:    */   {
/* 234:273 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length + 1);
/* 235:274 */     if (index != 0) {
/* 236:275 */       System.arraycopy(array, 0, newArray, 0, index);
/* 237:    */     }
/* 238:279 */     boolean lastIndex = index == array.length - 1;
/* 239:280 */     int remainingArrayLengthAfterIndex = array.length - index;
/* 240:282 */     if (lastIndex) {
/* 241:283 */       System.arraycopy(array, index, newArray, index + 1, remainingArrayLengthAfterIndex);
/* 242:    */     } else {
/* 243:286 */       System.arraycopy(array, index, newArray, index + 1, remainingArrayLengthAfterIndex);
/* 244:    */     }
/* 245:290 */     Array.set(newArray, index, v);
/* 246:291 */     return (Object[])newArray;
/* 247:    */   }
/* 248:    */   
/* 249:    */   @Universal
/* 250:    */   public static <V> V[] endSliceOf(V[] array, int endIndex)
/* 251:    */   {
/* 252:297 */     return slcEnd(array, endIndex);
/* 253:    */   }
/* 254:    */   
/* 255:    */   @Universal
/* 256:    */   public static <V> V[] slcEnd(V[] array, int endIndex)
/* 257:    */   {
/* 258:303 */     int end = calculateEndIndex(array, endIndex);
/* 259:304 */     int newLength = end;
/* 260:306 */     if (newLength < 0) {
/* 261:307 */       throw new ArrayIndexOutOfBoundsException(String.format("end index %d, length %d", new Object[] { Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/* 262:    */     }
/* 263:313 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), newLength);
/* 264:314 */     System.arraycopy(array, 0, newArray, 0, newLength);
/* 265:315 */     return (Object[])newArray;
/* 266:    */   }
/* 267:    */   
/* 268:    */   private static int calculateIndex(Object array, int originalIndex)
/* 269:    */   {
/* 270:321 */     int length = Array.getLength(array);
/* 271:322 */     int index = originalIndex;
/* 272:327 */     if (index < 0) {
/* 273:328 */       index = length + index;
/* 274:    */     }
/* 275:335 */     if (index < 0) {
/* 276:336 */       index = 0;
/* 277:    */     }
/* 278:338 */     if (index >= length) {
/* 279:339 */       index = length - 1;
/* 280:    */     }
/* 281:341 */     return index;
/* 282:    */   }
/* 283:    */   
/* 284:    */   private static <T> int calculateIndex(T[] array, int originalIndex)
/* 285:    */   {
/* 286:347 */     int length = array.length;
/* 287:348 */     int index = originalIndex;
/* 288:353 */     if (index < 0) {
/* 289:354 */       index = length + index;
/* 290:    */     }
/* 291:361 */     if (index < 0) {
/* 292:362 */       index = 0;
/* 293:    */     }
/* 294:364 */     if (index >= length) {
/* 295:365 */       index = length - 1;
/* 296:    */     }
/* 297:367 */     return index;
/* 298:    */   }
/* 299:    */   
/* 300:    */   private static <T> int calculateEndIndex(T[] array, int originalIndex)
/* 301:    */   {
/* 302:374 */     int length = array.length;
/* 303:375 */     int index = originalIndex;
/* 304:380 */     if (index < 0) {
/* 305:381 */       index = length + index;
/* 306:    */     }
/* 307:388 */     if (index < 0) {
/* 308:389 */       index = 0;
/* 309:    */     }
/* 310:391 */     if (index > length) {
/* 311:392 */       index = length;
/* 312:    */     }
/* 313:394 */     return index;
/* 314:    */   }
/* 315:    */   
/* 316:    */   public static List<Map<String, Object>> toListOfMaps(Object... array)
/* 317:    */   {
/* 318:399 */     return MapObjectConversion.toListOfMaps(Lists.list(array));
/* 319:    */   }
/* 320:    */   
/* 321:    */   public static Object reduceBy(Object[] array, Object object)
/* 322:    */   {
/* 323:405 */     Object sum = null;
/* 324:406 */     for (Object v : array) {
/* 325:407 */       sum = Invoker.invokeReducer(object, sum, v);
/* 326:    */     }
/* 327:409 */     return sum;
/* 328:    */   }
/* 329:    */   
/* 330:    */   public static int len(Object obj)
/* 331:    */   {
/* 332:413 */     return Array.getLength(obj);
/* 333:    */   }
/* 334:    */   
/* 335:    */   public static <V> V[] array(Collection<V> collection)
/* 336:    */   {
/* 337:418 */     if (collection.size() > 0)
/* 338:    */     {
/* 339:419 */       Object newInstance = Array.newInstance(collection.iterator().next().getClass(), collection.size());
/* 340:    */       
/* 341:421 */       return collection.toArray((Object[])newInstance);
/* 342:    */     }
/* 343:423 */     Exceptions.die("array(listStream): The collection has to have at least one item in it");
/* 344:424 */     return null;
/* 345:    */   }
/* 346:    */   
/* 347:    */   public static Object[] objectArray(Collection collection)
/* 348:    */   {
/* 349:430 */     return collection.toArray(new Object[collection.size()]);
/* 350:    */   }
/* 351:    */   
/* 352:    */   public static Object[] objectArray(Iterable iter)
/* 353:    */   {
/* 354:435 */     if ((iter instanceof Collection)) {
/* 355:436 */       return objectArray((Collection)iter);
/* 356:    */     }
/* 357:438 */     return objectArray(Lists.list(iter));
/* 358:    */   }
/* 359:    */   
/* 360:    */   public static <V> V[] array(Class<V> cls, Collection<V> collection)
/* 361:    */   {
/* 362:443 */     Object newInstance = Array.newInstance(cls, collection.size());
/* 363:444 */     return collection.toArray((Object[])newInstance);
/* 364:    */   }
/* 365:    */   
/* 366:    */   public static <T> boolean equals(T[] array1, T[] array2)
/* 367:    */   {
/* 368:448 */     if (array1 == array2) {
/* 369:449 */       return true;
/* 370:    */     }
/* 371:451 */     if ((array1 == null) || (array2 == null)) {
/* 372:452 */       return false;
/* 373:    */     }
/* 374:455 */     int length = array1.length;
/* 375:456 */     if (array2.length != length) {
/* 376:457 */       return false;
/* 377:    */     }
/* 378:460 */     int index = 0;
/* 379:460 */     if (index < length)
/* 380:    */     {
/* 381:461 */       Object value1 = array1[index];
/* 382:462 */       Object value2 = array2[index];
/* 383:463 */       if ((value1 == null) || (value2 == null)) {
/* 384:464 */         return false;
/* 385:    */       }
/* 386:466 */       return value1.equals(value2);
/* 387:    */     }
/* 388:470 */     return true;
/* 389:    */   }
/* 390:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.Arry
 * JD-Core Version:    0.7.0.1
 */