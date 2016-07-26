/*   1:    */ package org.boon.primitive;
/*   2:    */ 
/*   3:    */ import org.boon.Exceptions;
/*   4:    */ import org.boon.Universal;
/*   5:    */ import org.boon.core.reflection.Invoker;
/*   6:    */ 
/*   7:    */ public class Shrt
/*   8:    */ {
/*   9:    */   public static short[] shorts(short... array)
/*  10:    */   {
/*  11: 43 */     return array;
/*  12:    */   }
/*  13:    */   
/*  14:    */   public static short[] grow(short[] array, int size)
/*  15:    */   {
/*  16: 47 */     Exceptions.requireNonNull(array);
/*  17:    */     
/*  18: 49 */     short[] newArray = new short[array.length + size];
/*  19: 50 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  20: 51 */     return newArray;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public static short[] grow(short[] array)
/*  24:    */   {
/*  25: 56 */     Exceptions.requireNonNull(array);
/*  26:    */     
/*  27: 58 */     short[] newArray = new short[array.length * 2];
/*  28: 59 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  29: 60 */     return newArray;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public static short[] shrink(short[] array, int size)
/*  33:    */   {
/*  34: 65 */     Exceptions.requireNonNull(array);
/*  35:    */     
/*  36: 67 */     short[] newArray = new short[array.length - size];
/*  37:    */     
/*  38: 69 */     System.arraycopy(array, 0, newArray, 0, array.length - size);
/*  39: 70 */     return newArray;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public static short[] compact(short[] array)
/*  43:    */   {
/*  44: 75 */     Exceptions.requireNonNull(array);
/*  45:    */     
/*  46: 77 */     int nullCount = 0;
/*  47: 78 */     for (short ch : array) {
/*  48: 80 */       if (ch == 0) {
/*  49: 81 */         nullCount++;
/*  50:    */       }
/*  51:    */     }
/*  52: 84 */     short[] newArray = new short[array.length - nullCount];
/*  53:    */     
/*  54: 86 */     int j = 0;
/*  55: 87 */     for (short ch : array) {
/*  56: 89 */       if (ch != 0)
/*  57:    */       {
/*  58: 93 */         newArray[j] = ch;
/*  59: 94 */         j++;
/*  60:    */       }
/*  61:    */     }
/*  62: 96 */     return newArray;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public static short[] arrayOfShort(int size)
/*  66:    */   {
/*  67:107 */     return new short[size];
/*  68:    */   }
/*  69:    */   
/*  70:    */   @Universal
/*  71:    */   public static short[] array(short... array)
/*  72:    */   {
/*  73:116 */     Exceptions.requireNonNull(array);
/*  74:117 */     return array;
/*  75:    */   }
/*  76:    */   
/*  77:    */   @Universal
/*  78:    */   public static int len(short[] array)
/*  79:    */   {
/*  80:123 */     return array.length;
/*  81:    */   }
/*  82:    */   
/*  83:    */   @Universal
/*  84:    */   public static int lengthOf(short[] array)
/*  85:    */   {
/*  86:129 */     return array.length;
/*  87:    */   }
/*  88:    */   
/*  89:    */   @Universal
/*  90:    */   public static short idx(short[] array, int index)
/*  91:    */   {
/*  92:136 */     int i = calculateIndex(array, index);
/*  93:    */     
/*  94:138 */     return array[i];
/*  95:    */   }
/*  96:    */   
/*  97:    */   @Universal
/*  98:    */   public static void idx(short[] array, int index, short value)
/*  99:    */   {
/* 100:144 */     int i = calculateIndex(array, index);
/* 101:    */     
/* 102:146 */     array[i] = value;
/* 103:    */   }
/* 104:    */   
/* 105:    */   @Universal
/* 106:    */   public static short[] slc(short[] array, int startIndex, int endIndex)
/* 107:    */   {
/* 108:153 */     int start = calculateIndex(array, startIndex);
/* 109:154 */     int end = calculateEndIndex(array, endIndex);
/* 110:155 */     int newLength = end - start;
/* 111:157 */     if (newLength < 0) {
/* 112:158 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, end index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/* 113:    */     }
/* 114:164 */     short[] newArray = new short[newLength];
/* 115:165 */     System.arraycopy(array, start, newArray, 0, newLength);
/* 116:166 */     return newArray;
/* 117:    */   }
/* 118:    */   
/* 119:    */   @Universal
/* 120:    */   public static short[] sliceOf(short[] array, int startIndex, int endIndex)
/* 121:    */   {
/* 122:173 */     int start = calculateIndex(array, startIndex);
/* 123:174 */     int end = calculateEndIndex(array, endIndex);
/* 124:175 */     int newLength = end - start;
/* 125:177 */     if (newLength < 0) {
/* 126:178 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, end index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/* 127:    */     }
/* 128:184 */     short[] newArray = new short[newLength];
/* 129:185 */     System.arraycopy(array, start, newArray, 0, newLength);
/* 130:186 */     return newArray;
/* 131:    */   }
/* 132:    */   
/* 133:    */   @Universal
/* 134:    */   public static short[] slc(short[] array, int startIndex)
/* 135:    */   {
/* 136:192 */     int start = calculateIndex(array, startIndex);
/* 137:193 */     int newLength = array.length - start;
/* 138:195 */     if (newLength < 0) {
/* 139:196 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(array.length) }));
/* 140:    */     }
/* 141:202 */     short[] newArray = new short[newLength];
/* 142:203 */     System.arraycopy(array, start, newArray, 0, newLength);
/* 143:204 */     return newArray;
/* 144:    */   }
/* 145:    */   
/* 146:    */   @Universal
/* 147:    */   public static short[] sliceOf(short[] array, int startIndex)
/* 148:    */   {
/* 149:210 */     int start = calculateIndex(array, startIndex);
/* 150:211 */     int newLength = array.length - start;
/* 151:213 */     if (newLength < 0) {
/* 152:214 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(array.length) }));
/* 153:    */     }
/* 154:220 */     short[] newArray = new short[newLength];
/* 155:221 */     System.arraycopy(array, start, newArray, 0, newLength);
/* 156:222 */     return newArray;
/* 157:    */   }
/* 158:    */   
/* 159:    */   @Universal
/* 160:    */   public static short[] endSliceOf(short[] array, int endIndex)
/* 161:    */   {
/* 162:227 */     Exceptions.requireNonNull(array);
/* 163:    */     
/* 164:229 */     int end = calculateEndIndex(array, endIndex);
/* 165:230 */     int newLength = end;
/* 166:232 */     if (newLength < 0) {
/* 167:233 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/* 168:    */     }
/* 169:239 */     short[] newArray = new short[newLength];
/* 170:240 */     System.arraycopy(array, 0, newArray, 0, newLength);
/* 171:241 */     return newArray;
/* 172:    */   }
/* 173:    */   
/* 174:    */   @Universal
/* 175:    */   public static short[] slcEnd(short[] array, int endIndex)
/* 176:    */   {
/* 177:246 */     Exceptions.requireNonNull(array);
/* 178:    */     
/* 179:248 */     int end = calculateEndIndex(array, endIndex);
/* 180:249 */     int newLength = end;
/* 181:251 */     if (newLength < 0) {
/* 182:252 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/* 183:    */     }
/* 184:258 */     short[] newArray = new short[newLength];
/* 185:259 */     System.arraycopy(array, 0, newArray, 0, newLength);
/* 186:260 */     return newArray;
/* 187:    */   }
/* 188:    */   
/* 189:    */   @Universal
/* 190:    */   public static boolean in(short value, short[] array)
/* 191:    */   {
/* 192:265 */     for (short currentValue : array) {
/* 193:266 */       if (currentValue == value) {
/* 194:267 */         return true;
/* 195:    */       }
/* 196:    */     }
/* 197:270 */     return false;
/* 198:    */   }
/* 199:    */   
/* 200:    */   @Universal
/* 201:    */   public static short[] copy(short[] array)
/* 202:    */   {
/* 203:276 */     Exceptions.requireNonNull(array);
/* 204:277 */     short[] newArray = new short[array.length];
/* 205:278 */     System.arraycopy(array, 0, newArray, 0, array.length);
/* 206:279 */     return newArray;
/* 207:    */   }
/* 208:    */   
/* 209:    */   @Universal
/* 210:    */   public static short[] add(short[] array, short v)
/* 211:    */   {
/* 212:285 */     Exceptions.requireNonNull(array);
/* 213:286 */     short[] newArray = new short[array.length + 1];
/* 214:287 */     System.arraycopy(array, 0, newArray, 0, array.length);
/* 215:288 */     newArray[array.length] = v;
/* 216:289 */     return newArray;
/* 217:    */   }
/* 218:    */   
/* 219:    */   @Universal
/* 220:    */   public static short[] add(short[] array, short[] array2)
/* 221:    */   {
/* 222:294 */     Exceptions.requireNonNull(array);
/* 223:295 */     short[] newArray = new short[array.length + array2.length];
/* 224:296 */     System.arraycopy(array, 0, newArray, 0, array.length);
/* 225:297 */     System.arraycopy(array2, 0, newArray, array.length, array2.length);
/* 226:298 */     return newArray;
/* 227:    */   }
/* 228:    */   
/* 229:    */   @Universal
/* 230:    */   public static short[] insert(short[] array, int idx, short v)
/* 231:    */   {
/* 232:304 */     Exceptions.requireNonNull(array);
/* 233:306 */     if (idx >= array.length) {
/* 234:307 */       return add(array, v);
/* 235:    */     }
/* 236:310 */     int index = calculateIndex(array, idx);
/* 237:    */     
/* 238:    */ 
/* 239:313 */     short[] newArray = new short[array.length + 1];
/* 240:315 */     if (index != 0) {
/* 241:318 */       System.arraycopy(array, 0, newArray, 0, index);
/* 242:    */     }
/* 243:322 */     boolean lastIndex = index == array.length - 1;
/* 244:323 */     int remainingIndex = array.length - index;
/* 245:325 */     if (lastIndex) {
/* 246:328 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/* 247:    */     } else {
/* 248:333 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/* 249:    */     }
/* 250:337 */     newArray[index] = v;
/* 251:338 */     return newArray;
/* 252:    */   }
/* 253:    */   
/* 254:    */   @Universal
/* 255:    */   public static short[] insert(short[] array, int fromIndex, short[] values)
/* 256:    */   {
/* 257:344 */     Exceptions.requireNonNull(array);
/* 258:346 */     if (fromIndex >= array.length) {
/* 259:347 */       return add(array, values);
/* 260:    */     }
/* 261:350 */     int index = calculateIndex(array, fromIndex);
/* 262:    */     
/* 263:    */ 
/* 264:353 */     short[] newArray = new short[array.length + values.length];
/* 265:355 */     if (index != 0) {
/* 266:358 */       System.arraycopy(array, 0, newArray, 0, index);
/* 267:    */     }
/* 268:362 */     boolean lastIndex = index == array.length - 1;
/* 269:    */     
/* 270:364 */     int toIndex = index + values.length;
/* 271:365 */     int remainingIndex = newArray.length - toIndex;
/* 272:367 */     if (lastIndex) {
/* 273:370 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/* 274:    */     } else {
/* 275:375 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/* 276:    */     }
/* 277:379 */     int i = index;
/* 278:379 */     for (int j = 0; i < toIndex; j++)
/* 279:    */     {
/* 280:380 */       newArray[i] = values[j];i++;
/* 281:    */     }
/* 282:382 */     return newArray;
/* 283:    */   }
/* 284:    */   
/* 285:    */   private static int calculateIndex(short[] array, int originalIndex)
/* 286:    */   {
/* 287:388 */     int length = array.length;
/* 288:    */     
/* 289:390 */     Exceptions.requireNonNull(array, "array cannot be null");
/* 290:    */     
/* 291:    */ 
/* 292:393 */     int index = originalIndex;
/* 293:398 */     if (index < 0) {
/* 294:399 */       index = length + index;
/* 295:    */     }
/* 296:410 */     if (index < 0) {
/* 297:411 */       index = 0;
/* 298:    */     }
/* 299:413 */     if (index >= length) {
/* 300:414 */       index = length - 1;
/* 301:    */     }
/* 302:416 */     return index;
/* 303:    */   }
/* 304:    */   
/* 305:    */   private static int calculateEndIndex(short[] array, int originalIndex)
/* 306:    */   {
/* 307:422 */     int length = array.length;
/* 308:    */     
/* 309:424 */     Exceptions.requireNonNull(array, "array cannot be null");
/* 310:    */     
/* 311:    */ 
/* 312:427 */     int index = originalIndex;
/* 313:432 */     if (index < 0) {
/* 314:433 */       index = length + index;
/* 315:    */     }
/* 316:444 */     if (index < 0) {
/* 317:445 */       index = 0;
/* 318:    */     }
/* 319:447 */     if (index > length) {
/* 320:448 */       index = length;
/* 321:    */     }
/* 322:450 */     return index;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public static int reduceBy(short[] array, Object object)
/* 326:    */   {
/* 327:456 */     int sum = 0;
/* 328:457 */     for (short v : array) {
/* 329:458 */       sum = ((Short)Invoker.invokeReducer(object, Integer.valueOf(sum), Short.valueOf(v))).shortValue();
/* 330:    */     }
/* 331:460 */     return sum;
/* 332:    */   }
/* 333:    */   
/* 334:    */   public static boolean equalsOrDie(short[] expected, short[] got)
/* 335:    */   {
/* 336:473 */     if (expected.length != got.length) {
/* 337:474 */       Exceptions.die(new Object[] { "Lengths did not match, expected length", Integer.valueOf(expected.length), "but got", Integer.valueOf(got.length) });
/* 338:    */     }
/* 339:478 */     for (int index = 0; index < expected.length; index++) {
/* 340:479 */       if (expected[index] != got[index]) {
/* 341:480 */         Exceptions.die(new Object[] { "value at index did not match index", Integer.valueOf(index), "expected value", Short.valueOf(expected[index]), "but got", Short.valueOf(got[index]) });
/* 342:    */       }
/* 343:    */     }
/* 344:486 */     return true;
/* 345:    */   }
/* 346:    */   
/* 347:    */   public static boolean equals(short[] expected, short[] got)
/* 348:    */   {
/* 349:498 */     if (expected.length != got.length) {
/* 350:499 */       return false;
/* 351:    */     }
/* 352:502 */     for (int index = 0; index < expected.length; index++) {
/* 353:503 */       if (expected[index] != got[index]) {
/* 354:504 */         return false;
/* 355:    */       }
/* 356:    */     }
/* 357:507 */     return true;
/* 358:    */   }
/* 359:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.Shrt
 * JD-Core Version:    0.7.0.1
 */