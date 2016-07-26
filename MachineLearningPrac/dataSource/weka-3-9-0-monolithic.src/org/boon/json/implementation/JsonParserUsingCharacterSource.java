/*   1:    */ package org.boon.json.implementation;
/*   2:    */ 
/*   3:    */ import java.io.ByteArrayInputStream;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.Reader;
/*   7:    */ import java.io.StringReader;
/*   8:    */ import java.nio.charset.Charset;
/*   9:    */ import java.util.ArrayList;
/*  10:    */ import java.util.Collections;
/*  11:    */ import java.util.List;
/*  12:    */ import java.util.concurrent.ConcurrentHashMap;
/*  13:    */ import org.boon.Boon;
/*  14:    */ import org.boon.Logger;
/*  15:    */ import org.boon.collections.LazyMap;
/*  16:    */ import org.boon.core.reflection.BeanUtils;
/*  17:    */ import org.boon.json.JsonException;
/*  18:    */ import org.boon.primitive.CharArrayCharacterSource;
/*  19:    */ import org.boon.primitive.CharBuf;
/*  20:    */ import org.boon.primitive.CharScanner;
/*  21:    */ import org.boon.primitive.CharacterSource;
/*  22:    */ import org.boon.primitive.Chr;
/*  23:    */ import org.boon.primitive.IOInputStream;
/*  24:    */ import org.boon.primitive.InMemoryInputStream;
/*  25:    */ import org.boon.primitive.ReaderCharacterSource;
/*  26:    */ 
/*  27:    */ public class JsonParserUsingCharacterSource
/*  28:    */   extends BaseJsonParser
/*  29:    */ {
/*  30:    */   private CharacterSource characterSource;
/*  31:    */   
/*  32:    */   protected String exceptionDetails(String message)
/*  33:    */   {
/*  34: 65 */     return this.characterSource.errorDetails(message);
/*  35:    */   }
/*  36:    */   
/*  37:    */   protected final Object decodeJsonObject()
/*  38:    */   {
/*  39: 70 */     LazyMap map = new LazyMap();
/*  40:    */     try
/*  41:    */     {
/*  42: 74 */       CharacterSource characterSource = this.characterSource;
/*  43: 76 */       if (characterSource.currentChar() == 123) {
/*  44: 77 */         characterSource.nextChar();
/*  45:    */       }
/*  46: 81 */       while (characterSource.hasChar())
/*  47:    */       {
/*  48: 83 */         characterSource.skipWhiteSpace();
/*  49: 86 */         if (characterSource.currentChar() == 34)
/*  50:    */         {
/*  51: 88 */           String key = decodeString();
/*  52: 91 */           if (internKeys)
/*  53:    */           {
/*  54: 92 */             String keyPrime = (String)internedKeysCache.get(key);
/*  55: 93 */             if (keyPrime == null)
/*  56:    */             {
/*  57: 94 */               key = key.intern();
/*  58: 95 */               internedKeysCache.put(key, key);
/*  59:    */             }
/*  60:    */             else
/*  61:    */             {
/*  62: 97 */               key = keyPrime;
/*  63:    */             }
/*  64:    */           }
/*  65:101 */           characterSource.skipWhiteSpace();
/*  66:102 */           if (characterSource.currentChar() != 58) {
/*  67:104 */             complain("expecting current character to be : but was " + charDescription(characterSource.currentChar()) + "\n");
/*  68:    */           }
/*  69:107 */           characterSource.nextChar();
/*  70:    */           
/*  71:109 */           characterSource.skipWhiteSpace();
/*  72:    */           
/*  73:111 */           Object value = decodeValue();
/*  74:    */           
/*  75:    */ 
/*  76:    */ 
/*  77:115 */           characterSource.skipWhiteSpace();
/*  78:    */           
/*  79:117 */           map.put(key, value);
/*  80:    */         }
/*  81:122 */         int ch = characterSource.currentChar();
/*  82:123 */         if (ch == 125)
/*  83:    */         {
/*  84:124 */           characterSource.nextChar();
/*  85:125 */           break;
/*  86:    */         }
/*  87:126 */         if (ch == 44) {
/*  88:127 */           characterSource.nextChar();
/*  89:    */         } else {
/*  90:130 */           complain("expecting '}' or ',' but got current char " + charDescription(ch));
/*  91:    */         }
/*  92:    */       }
/*  93:    */     }
/*  94:    */     catch (Exception ex)
/*  95:    */     {
/*  96:136 */       if ((ex instanceof JsonException)) {
/*  97:137 */         throw ex;
/*  98:    */       }
/*  99:139 */       throw new JsonException(exceptionDetails("Unable to parse JSON object"), ex);
/* 100:    */     }
/* 101:143 */     return map;
/* 102:    */   }
/* 103:    */   
/* 104:    */   protected final void complain(String complaint)
/* 105:    */   {
/* 106:148 */     throw new JsonException(exceptionDetails(complaint));
/* 107:    */   }
/* 108:    */   
/* 109:    */   private final Object decodeValue()
/* 110:    */   {
/* 111:154 */     CharacterSource characterSource = this.characterSource;
/* 112:155 */     Object value = null;
/* 113:156 */     characterSource.skipWhiteSpace();
/* 114:158 */     switch (characterSource.currentChar())
/* 115:    */     {
/* 116:    */     case 34: 
/* 117:161 */       value = decodeString();
/* 118:162 */       break;
/* 119:    */     case 116: 
/* 120:166 */       value = Boolean.valueOf(decodeTrue());
/* 121:167 */       break;
/* 122:    */     case 102: 
/* 123:170 */       value = Boolean.valueOf(decodeFalse());
/* 124:171 */       break;
/* 125:    */     case 110: 
/* 126:174 */       value = decodeNull();
/* 127:175 */       break;
/* 128:    */     case 91: 
/* 129:178 */       value = decodeJsonArray();
/* 130:179 */       break;
/* 131:    */     case 123: 
/* 132:182 */       value = decodeJsonObject();
/* 133:183 */       break;
/* 134:    */     case 48: 
/* 135:    */     case 49: 
/* 136:    */     case 50: 
/* 137:    */     case 51: 
/* 138:    */     case 52: 
/* 139:    */     case 53: 
/* 140:    */     case 54: 
/* 141:    */     case 55: 
/* 142:    */     case 56: 
/* 143:    */     case 57: 
/* 144:195 */       value = decodeNumber(false);
/* 145:196 */       break;
/* 146:    */     case 45: 
/* 147:199 */       value = decodeNumber(true);
/* 148:200 */       break;
/* 149:    */     default: 
/* 150:203 */       throw new JsonException(exceptionDetails("Unable to determine the current character, it is not a string, number, array, or object"));
/* 151:    */     }
/* 152:208 */     return value;
/* 153:    */   }
/* 154:    */   
/* 155:    */   private final Object decodeNumber(boolean negative)
/* 156:    */   {
/* 157:216 */     char[] chars = this.characterSource.readNumber();
/* 158:217 */     Object value = null;
/* 159:219 */     if (CharScanner.hasDecimalChar(chars, negative)) {
/* 160:220 */       value = Double.valueOf(CharScanner.parseDouble(chars));
/* 161:221 */     } else if (CharScanner.isInteger(chars)) {
/* 162:222 */       value = Integer.valueOf(CharScanner.parseInt(chars));
/* 163:223 */     } else if (CharScanner.isLong(chars)) {
/* 164:224 */       value = Long.valueOf(CharScanner.parseLong(chars));
/* 165:    */     }
/* 166:227 */     return value;
/* 167:    */   }
/* 168:    */   
/* 169:232 */   protected static final char[] NULL = Chr.chars("null");
/* 170:    */   
/* 171:    */   protected final Object decodeNull()
/* 172:    */   {
/* 173:235 */     if (!this.characterSource.consumeIfMatch(NULL)) {
/* 174:236 */       throw new JsonException(exceptionDetails("null not parse properly"));
/* 175:    */     }
/* 176:238 */     return null;
/* 177:    */   }
/* 178:    */   
/* 179:242 */   protected static final char[] TRUE = Chr.chars("true");
/* 180:    */   
/* 181:    */   protected final boolean decodeTrue()
/* 182:    */   {
/* 183:246 */     if (this.characterSource.consumeIfMatch(TRUE)) {
/* 184:247 */       return true;
/* 185:    */     }
/* 186:249 */     throw new JsonException(exceptionDetails("true not parsed properly"));
/* 187:    */   }
/* 188:    */   
/* 189:254 */   protected static char[] FALSE = Chr.chars("false");
/* 190:    */   
/* 191:    */   protected final boolean decodeFalse()
/* 192:    */   {
/* 193:258 */     if (this.characterSource.consumeIfMatch(FALSE)) {
/* 194:259 */       return false;
/* 195:    */     }
/* 196:261 */     throw new JsonException(exceptionDetails("false not parsed properly"));
/* 197:    */   }
/* 198:    */   
/* 199:268 */   private CharBuf builder = CharBuf.create(20);
/* 200:    */   IOInputStream ioInputStream;
/* 201:    */   
/* 202:    */   private String decodeString()
/* 203:    */   {
/* 204:272 */     CharacterSource characterSource = this.characterSource;
/* 205:    */     
/* 206:    */ 
/* 207:275 */     characterSource.nextChar();
/* 208:    */     
/* 209:    */ 
/* 210:278 */     char[] chars = characterSource.findNextChar(34, 92);
/* 211:    */     
/* 212:    */ 
/* 213:    */ 
/* 214:282 */     String value = null;
/* 215:283 */     if (characterSource.hadEscape())
/* 216:    */     {
/* 217:284 */       value = this.builder.decodeJsonString(chars).toString();
/* 218:285 */       this.builder.recycle();
/* 219:    */     }
/* 220:    */     else
/* 221:    */     {
/* 222:287 */       value = new String(chars);
/* 223:    */     }
/* 224:290 */     return value;
/* 225:    */   }
/* 226:    */   
/* 227:    */   protected final List decodeJsonArray()
/* 228:    */   {
/* 229:296 */     ArrayList<Object> list = null;
/* 230:    */     
/* 231:298 */     boolean foundEnd = false;
/* 232:    */     try
/* 233:    */     {
/* 234:302 */       CharacterSource characterSource = this.characterSource;
/* 235:304 */       if (this.characterSource.currentChar() == 91) {
/* 236:305 */         characterSource.nextChar();
/* 237:    */       }
/* 238:309 */       characterSource.skipWhiteSpace();
/* 239:314 */       if (this.characterSource.currentChar() == 93)
/* 240:    */       {
/* 241:315 */         characterSource.nextChar();
/* 242:316 */         return Collections.EMPTY_LIST;
/* 243:    */       }
/* 244:319 */       list = new ArrayList();
/* 245:    */       do
/* 246:    */       {
/* 247:325 */         characterSource.skipWhiteSpace();
/* 248:    */         
/* 249:327 */         Object arrayItem = decodeValue();
/* 250:    */         
/* 251:329 */         list.add(arrayItem);
/* 252:    */         
/* 253:    */ 
/* 254:    */ 
/* 255:333 */         characterSource.skipWhiteSpace();
/* 256:    */         
/* 257:335 */         int c = characterSource.currentChar();
/* 258:337 */         if (c == 44)
/* 259:    */         {
/* 260:338 */           characterSource.nextChar();
/* 261:    */         }
/* 262:    */         else
/* 263:    */         {
/* 264:340 */           if (c == 93)
/* 265:    */           {
/* 266:341 */             foundEnd = true;
/* 267:342 */             characterSource.nextChar();
/* 268:343 */             break;
/* 269:    */           }
/* 270:346 */           String charString = charDescription(c);
/* 271:    */           
/* 272:348 */           complain(String.format("expecting a ',' or a ']',  but got \nthe current character of  %s  on array index of %s \n", new Object[] { charString, Integer.valueOf(list.size()) }));
/* 273:    */         }
/* 274:355 */       } while (characterSource.hasChar());
/* 275:    */     }
/* 276:    */     catch (Exception ex)
/* 277:    */     {
/* 278:359 */       if ((ex instanceof JsonException)) {
/* 279:360 */         throw ex;
/* 280:    */       }
/* 281:363 */       throw new JsonException(exceptionDetails("Unexpected issue"), ex);
/* 282:    */     }
/* 283:366 */     if (!foundEnd) {
/* 284:367 */       throw new JsonException(exceptionDetails("Could not find end of JSON array"));
/* 285:    */     }
/* 286:370 */     return list;
/* 287:    */   }
/* 288:    */   
/* 289:    */   public Object parse(char[] chars)
/* 290:    */   {
/* 291:380 */     this.characterSource = new CharArrayCharacterSource(chars);
/* 292:381 */     return decodeValue();
/* 293:    */   }
/* 294:    */   
/* 295:    */   public Object parse(Reader reader)
/* 296:    */   {
/* 297:392 */     if ((reader instanceof StringReader)) {
/* 298:    */       try
/* 299:    */       {
/* 300:394 */         String str = BeanUtils.idxStr(reader, "str");
/* 301:395 */         int length = BeanUtils.idxInt(reader, "length");
/* 302:396 */         int next = BeanUtils.idxInt(reader, "next");
/* 303:398 */         if ((str != null) && (next == 0) && (length == str.length())) {
/* 304:399 */           return parse(str);
/* 305:    */         }
/* 306:    */       }
/* 307:    */       catch (Exception ex)
/* 308:    */       {
/* 309:402 */         Boon.logger("JSON PARSER").fatal(ex);
/* 310:    */       }
/* 311:    */     }
/* 312:407 */     this.characterSource = new ReaderCharacterSource(reader);
/* 313:408 */     return decodeValue();
/* 314:    */   }
/* 315:    */   
/* 316:    */   public Object parse(byte[] value, Charset charset)
/* 317:    */   {
/* 318:415 */     if (value.length < 20000)
/* 319:    */     {
/* 320:416 */       this.characterSource = new CharArrayCharacterSource(new String(value, charset));
/* 321:417 */       return decodeValue();
/* 322:    */     }
/* 323:420 */     return parse(new InMemoryInputStream(value), charset);
/* 324:    */   }
/* 325:    */   
/* 326:    */   public Object parse(InputStream inputStream, Charset charset)
/* 327:    */   {
/* 328:430 */     if ((inputStream instanceof ByteArrayInputStream)) {
/* 329:432 */       return parse(new InputStreamReader(inputStream, charset));
/* 330:    */     }
/* 331:435 */     this.ioInputStream = IOInputStream.input(this.ioInputStream, 50000).input(inputStream);
/* 332:436 */     return parse(new InputStreamReader(this.ioInputStream, charset));
/* 333:    */   }
/* 334:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.implementation.JsonParserUsingCharacterSource
 * JD-Core Version:    0.7.0.1
 */