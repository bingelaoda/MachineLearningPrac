/*   1:    */ package org.boon.json.implementation;
/*   2:    */ 
/*   3:    */ import java.nio.charset.Charset;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.concurrent.ConcurrentHashMap;
/*   8:    */ import org.boon.collections.LazyMap;
/*   9:    */ import org.boon.core.reflection.FastStringUtils;
/*  10:    */ import org.boon.json.JsonException;
/*  11:    */ import org.boon.primitive.CharBuf;
/*  12:    */ import org.boon.primitive.CharScanner;
/*  13:    */ import org.boon.primitive.Chr;
/*  14:    */ 
/*  15:    */ public class JsonParserCharArray
/*  16:    */   extends BaseJsonParser
/*  17:    */ {
/*  18:    */   protected char[] charArray;
/*  19:    */   protected int __index;
/*  20:    */   protected char __currentChar;
/*  21:    */   protected int lastIndex;
/*  22:    */   
/*  23:    */   public Object parse(char[] chars)
/*  24:    */   {
/*  25: 63 */     this.__index = 0;
/*  26: 64 */     this.charArray = chars;
/*  27: 65 */     this.lastIndex = (chars.length - 1);
/*  28: 66 */     Object value = decodeValue();
/*  29: 67 */     return value;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Object parse(byte[] bytes, Charset charset)
/*  33:    */   {
/*  34: 73 */     char[] chars = FastStringUtils.toCharArrayFromBytes(bytes, charset);
/*  35: 74 */     return parse(chars);
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected final boolean hasMore()
/*  39:    */   {
/*  40: 78 */     return this.__index < this.lastIndex;
/*  41:    */   }
/*  42:    */   
/*  43:    */   protected final boolean hasCurrent()
/*  44:    */   {
/*  45: 83 */     return this.__index <= this.lastIndex;
/*  46:    */   }
/*  47:    */   
/*  48:    */   protected final void skipWhiteSpaceIfNeeded()
/*  49:    */   {
/*  50: 89 */     int ix = this.__index;
/*  51: 92 */     if (hasCurrent()) {
/*  52: 93 */       this.__currentChar = this.charArray[ix];
/*  53:    */     }
/*  54: 96 */     if (this.__currentChar <= ' ')
/*  55:    */     {
/*  56: 97 */       ix = CharScanner.skipWhiteSpaceFast(this.charArray, ix);
/*  57: 98 */       this.__currentChar = this.charArray[ix];
/*  58: 99 */       this.__index = ix;
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   protected final char nextChar()
/*  63:    */   {
/*  64:    */     try
/*  65:    */     {
/*  66:109 */       if (hasMore())
/*  67:    */       {
/*  68:110 */         this.__index += 1;
/*  69:111 */         return this.__currentChar = this.charArray[this.__index];
/*  70:    */       }
/*  71:113 */       return '\000';
/*  72:    */     }
/*  73:    */     catch (Exception ex)
/*  74:    */     {
/*  75:116 */       throw new JsonException(exceptionDetails("unable to advance character"), ex);
/*  76:    */     }
/*  77:    */   }
/*  78:    */   
/*  79:    */   protected String exceptionDetails(String message)
/*  80:    */   {
/*  81:122 */     return CharScanner.errorDetails(message, this.charArray, this.__index, this.__currentChar);
/*  82:    */   }
/*  83:    */   
/*  84:    */   protected final Object decodeJsonObject()
/*  85:    */   {
/*  86:132 */     if (this.__currentChar == '{') {
/*  87:133 */       this.__index += 1;
/*  88:    */     }
/*  89:136 */     LazyMap map = new LazyMap();
/*  90:138 */     for (; this.__index < this.charArray.length; this.__index += 1)
/*  91:    */     {
/*  92:140 */       skipWhiteSpaceIfNeeded();
/*  93:143 */       if (this.__currentChar == '"')
/*  94:    */       {
/*  95:145 */         String key = decodeString();
/*  96:148 */         if (internKeys)
/*  97:    */         {
/*  98:149 */           String keyPrime = (String)internedKeysCache.get(key);
/*  99:150 */           if (keyPrime == null)
/* 100:    */           {
/* 101:151 */             key = key.intern();
/* 102:152 */             internedKeysCache.put(key, key);
/* 103:    */           }
/* 104:    */           else
/* 105:    */           {
/* 106:154 */             key = keyPrime;
/* 107:    */           }
/* 108:    */         }
/* 109:158 */         skipWhiteSpaceIfNeeded();
/* 110:160 */         if (this.__currentChar != ':') {
/* 111:162 */           complain("expecting current character to be " + charDescription(this.__currentChar) + "\n");
/* 112:    */         }
/* 113:164 */         this.__index += 1;
/* 114:    */         
/* 115:166 */         skipWhiteSpaceIfNeeded();
/* 116:    */         
/* 117:168 */         Object value = decodeValueInternal();
/* 118:    */         
/* 119:170 */         skipWhiteSpaceIfNeeded();
/* 120:171 */         map.put(key, value);
/* 121:    */       }
/* 122:175 */       if (this.__currentChar == '}')
/* 123:    */       {
/* 124:176 */         this.__index += 1;
/* 125:177 */         break;
/* 126:    */       }
/* 127:178 */       if (this.__currentChar != ',') {
/* 128:181 */         complain("expecting '}' or ',' but got current char " + charDescription(this.__currentChar));
/* 129:    */       }
/* 130:    */     }
/* 131:188 */     return map;
/* 132:    */   }
/* 133:    */   
/* 134:    */   protected final void complain(String complaint)
/* 135:    */   {
/* 136:193 */     throw new JsonException(exceptionDetails(complaint));
/* 137:    */   }
/* 138:    */   
/* 139:    */   protected Object decodeValue()
/* 140:    */   {
/* 141:198 */     return decodeValueInternal();
/* 142:    */   }
/* 143:    */   
/* 144:    */   private final Object decodeValueInternal()
/* 145:    */   {
/* 146:202 */     Object value = null;
/* 147:203 */     skipWhiteSpaceIfNeeded();
/* 148:205 */     switch (this.__currentChar)
/* 149:    */     {
/* 150:    */     case '"': 
/* 151:208 */       value = decodeString();
/* 152:209 */       break;
/* 153:    */     case 't': 
/* 154:213 */       value = Boolean.valueOf(decodeTrue());
/* 155:214 */       break;
/* 156:    */     case 'f': 
/* 157:217 */       value = Boolean.valueOf(decodeFalse());
/* 158:218 */       break;
/* 159:    */     case 'n': 
/* 160:221 */       value = decodeNull();
/* 161:222 */       break;
/* 162:    */     case '[': 
/* 163:225 */       value = decodeJsonArray();
/* 164:226 */       break;
/* 165:    */     case '{': 
/* 166:232 */       value = decodeJsonObject();
/* 167:233 */       break;
/* 168:    */     case '0': 
/* 169:    */     case '1': 
/* 170:    */     case '2': 
/* 171:    */     case '3': 
/* 172:    */     case '4': 
/* 173:    */     case '5': 
/* 174:    */     case '6': 
/* 175:    */     case '7': 
/* 176:    */     case '8': 
/* 177:    */     case '9': 
/* 178:245 */       value = decodeNumber();
/* 179:246 */       break;
/* 180:    */     case '-': 
/* 181:248 */       value = decodeNumber();
/* 182:249 */       break;
/* 183:    */     default: 
/* 184:252 */       throw new JsonException(exceptionDetails("Unable to determine the current character, it is not a string, number, array, or object"));
/* 185:    */     }
/* 186:257 */     return value;
/* 187:    */   }
/* 188:    */   
/* 189:262 */   int[] endIndex = new int[1];
/* 190:    */   
/* 191:    */   private final Object decodeNumber()
/* 192:    */   {
/* 193:266 */     Number num = CharScanner.parseJsonNumber(this.charArray, this.__index, this.charArray.length, this.endIndex);
/* 194:267 */     this.__index = this.endIndex[0];
/* 195:    */     
/* 196:269 */     return num;
/* 197:    */   }
/* 198:    */   
/* 199:275 */   protected static final char[] NULL = Chr.chars("null");
/* 200:    */   
/* 201:    */   protected final Object decodeNull()
/* 202:    */   {
/* 203:279 */     if ((this.__index + NULL.length <= this.charArray.length) && 
/* 204:280 */       (this.charArray[this.__index] == 'n') && (this.charArray[(++this.__index)] == 'u') && (this.charArray[(++this.__index)] == 'l') && (this.charArray[(++this.__index)] == 'l'))
/* 205:    */     {
/* 206:284 */       this.__index += 1;
/* 207:285 */       return null;
/* 208:    */     }
/* 209:288 */     throw new JsonException(exceptionDetails("null not parse properly"));
/* 210:    */   }
/* 211:    */   
/* 212:292 */   protected static final char[] TRUE = Chr.chars("true");
/* 213:    */   
/* 214:    */   protected final boolean decodeTrue()
/* 215:    */   {
/* 216:296 */     if ((this.__index + TRUE.length <= this.charArray.length) && 
/* 217:297 */       (this.charArray[this.__index] == 't') && (this.charArray[(++this.__index)] == 'r') && (this.charArray[(++this.__index)] == 'u') && (this.charArray[(++this.__index)] == 'e'))
/* 218:    */     {
/* 219:302 */       this.__index += 1;
/* 220:303 */       return true;
/* 221:    */     }
/* 222:308 */     throw new JsonException(exceptionDetails("true not parsed properly"));
/* 223:    */   }
/* 224:    */   
/* 225:312 */   protected static char[] FALSE = Chr.chars("false");
/* 226:    */   
/* 227:    */   protected final boolean decodeFalse()
/* 228:    */   {
/* 229:316 */     if ((this.__index + FALSE.length <= this.charArray.length) && 
/* 230:317 */       (this.charArray[this.__index] == 'f') && (this.charArray[(++this.__index)] == 'a') && (this.charArray[(++this.__index)] == 'l') && (this.charArray[(++this.__index)] == 's') && (this.charArray[(++this.__index)] == 'e'))
/* 231:    */     {
/* 232:322 */       this.__index += 1;
/* 233:323 */       return false;
/* 234:    */     }
/* 235:326 */     throw new JsonException(exceptionDetails("false not parsed properly"));
/* 236:    */   }
/* 237:    */   
/* 238:331 */   private CharBuf builder = CharBuf.create(20);
/* 239:    */   
/* 240:    */   private String decodeString()
/* 241:    */   {
/* 242:388 */     char[] array = this.charArray;
/* 243:389 */     int index = this.__index;
/* 244:390 */     char currentChar = array[index];
/* 245:392 */     if ((index < array.length) && (currentChar == '"')) {
/* 246:393 */       index++;
/* 247:    */     }
/* 248:396 */     int startIndex = index;
/* 249:    */     
/* 250:    */ 
/* 251:399 */     boolean encoded = CharScanner.hasEscapeChar(array, index, this.indexHolder);
/* 252:400 */     index = this.indexHolder[0];
/* 253:    */     
/* 254:    */ 
/* 255:    */ 
/* 256:404 */     String value = null;
/* 257:405 */     if (encoded)
/* 258:    */     {
/* 259:406 */       index = CharScanner.findEndQuote(array, index);
/* 260:407 */       value = this.builder.decodeJsonString(array, startIndex, index).toString();
/* 261:408 */       this.builder.recycle();
/* 262:    */     }
/* 263:    */     else
/* 264:    */     {
/* 265:410 */       value = new String(array, startIndex, index - startIndex);
/* 266:    */     }
/* 267:413 */     if (index < this.charArray.length) {
/* 268:414 */       index++;
/* 269:    */     }
/* 270:416 */     this.__index = index;
/* 271:417 */     return value;
/* 272:    */   }
/* 273:    */   
/* 274:    */   protected final List decodeJsonArray()
/* 275:    */   {
/* 276:423 */     ArrayList<Object> list = null;
/* 277:    */     
/* 278:425 */     boolean foundEnd = false;
/* 279:426 */     char[] charArray = this.charArray;
/* 280:    */     try
/* 281:    */     {
/* 282:429 */       if (this.__currentChar == '[') {
/* 283:430 */         this.__index += 1;
/* 284:    */       }
/* 285:434 */       skipWhiteSpaceIfNeeded();
/* 286:438 */       if (this.__currentChar == ']')
/* 287:    */       {
/* 288:439 */         this.__index += 1;
/* 289:440 */         return Collections.EMPTY_LIST;
/* 290:    */       }
/* 291:443 */       list = new ArrayList();
/* 292:449 */       while (hasMore())
/* 293:    */       {
/* 294:451 */         Object arrayItem = decodeValueInternal();
/* 295:    */         
/* 296:453 */         list.add(arrayItem);
/* 297:    */         for (;;)
/* 298:    */         {
/* 299:457 */           c = charArray[this.__index];
/* 300:458 */           if (c == ',')
/* 301:    */           {
/* 302:459 */             this.__index += 1;
/* 303:460 */             break;
/* 304:    */           }
/* 305:461 */           if (c == ']')
/* 306:    */           {
/* 307:462 */             foundEnd = true;
/* 308:463 */             this.__index += 1;
/* 309:    */             break label242;
/* 310:    */           }
/* 311:465 */           if (c > ' ') {
/* 312:    */             break label153;
/* 313:    */           }
/* 314:466 */           this.__index += 1;
/* 315:    */         }
/* 316:    */         label153:
/* 317:476 */         char c = charArray[this.__index];
/* 318:478 */         if (c == ',')
/* 319:    */         {
/* 320:479 */           this.__index += 1;
/* 321:    */         }
/* 322:    */         else
/* 323:    */         {
/* 324:481 */           if (c == ']')
/* 325:    */           {
/* 326:482 */             this.__index += 1;
/* 327:483 */             foundEnd = true;
/* 328:484 */             break;
/* 329:    */           }
/* 330:487 */           String charString = charDescription(c);
/* 331:    */           
/* 332:489 */           complain(String.format("expecting a ',' or a ']',  but got \nthe current character of  %s  on array index of %s \n", new Object[] { charString, Integer.valueOf(list.size()) }));
/* 333:    */         }
/* 334:    */       }
/* 335:    */     }
/* 336:    */     catch (Exception ex)
/* 337:    */     {
/* 338:    */       label242:
/* 339:499 */       if ((ex instanceof JsonException))
/* 340:    */       {
/* 341:500 */         JsonException jsonException = (JsonException)ex;
/* 342:501 */         throw jsonException;
/* 343:    */       }
/* 344:503 */       throw new JsonException(exceptionDetails("issue parsing JSON array"), ex);
/* 345:    */     }
/* 346:505 */     if (!foundEnd) {
/* 347:506 */       complain("Did not find end of Json Array");
/* 348:    */     }
/* 349:508 */     return list;
/* 350:    */   }
/* 351:    */   
/* 352:    */   protected final char currentChar()
/* 353:    */   {
/* 354:513 */     if (this.__index > this.lastIndex) {
/* 355:514 */       return '\000';
/* 356:    */     }
/* 357:517 */     return this.charArray[this.__index];
/* 358:    */   }
/* 359:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.implementation.JsonParserCharArray
 * JD-Core Version:    0.7.0.1
 */