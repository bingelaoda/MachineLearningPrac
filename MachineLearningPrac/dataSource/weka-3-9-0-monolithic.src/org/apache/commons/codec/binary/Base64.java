/*   1:    */ package org.apache.commons.codec.binary;
/*   2:    */ 
/*   3:    */ import org.apache.commons.codec.BinaryDecoder;
/*   4:    */ import org.apache.commons.codec.BinaryEncoder;
/*   5:    */ import org.apache.commons.codec.DecoderException;
/*   6:    */ import org.apache.commons.codec.EncoderException;
/*   7:    */ 
/*   8:    */ public class Base64
/*   9:    */   implements BinaryEncoder, BinaryDecoder
/*  10:    */ {
/*  11:    */   static final int CHUNK_SIZE = 76;
/*  12: 53 */   static final byte[] CHUNK_SEPARATOR = "\r\n".getBytes();
/*  13:    */   static final int BASELENGTH = 255;
/*  14:    */   static final int LOOKUPLENGTH = 64;
/*  15:    */   static final int EIGHTBIT = 8;
/*  16:    */   static final int SIXTEENBIT = 16;
/*  17:    */   static final int TWENTYFOURBITGROUP = 24;
/*  18:    */   static final int FOURBYTE = 4;
/*  19:    */   static final int SIGN = -128;
/*  20:    */   static final byte PAD = 61;
/*  21: 97 */   private static byte[] base64Alphabet = new byte['ÿ'];
/*  22: 98 */   private static byte[] lookUpBase64Alphabet = new byte[64];
/*  23:    */   
/*  24:    */   static
/*  25:    */   {
/*  26:102 */     for (int i = 0; i < 255; i++) {
/*  27:103 */       base64Alphabet[i] = -1;
/*  28:    */     }
/*  29:105 */     for (int i = 90; i >= 65; i--) {
/*  30:106 */       base64Alphabet[i] = ((byte)(i - 65));
/*  31:    */     }
/*  32:108 */     for (int i = 122; i >= 97; i--) {
/*  33:109 */       base64Alphabet[i] = ((byte)(i - 97 + 26));
/*  34:    */     }
/*  35:111 */     for (int i = 57; i >= 48; i--) {
/*  36:112 */       base64Alphabet[i] = ((byte)(i - 48 + 52));
/*  37:    */     }
/*  38:115 */     base64Alphabet[43] = 62;
/*  39:116 */     base64Alphabet[47] = 63;
/*  40:118 */     for (int i = 0; i <= 25; i++) {
/*  41:119 */       lookUpBase64Alphabet[i] = ((byte)(65 + i));
/*  42:    */     }
/*  43:122 */     int i = 26;
/*  44:122 */     for (int j = 0; i <= 51; j++)
/*  45:    */     {
/*  46:123 */       lookUpBase64Alphabet[i] = ((byte)(97 + j));i++;
/*  47:    */     }
/*  48:126 */     int i = 52;
/*  49:126 */     for (int j = 0; i <= 61; j++)
/*  50:    */     {
/*  51:127 */       lookUpBase64Alphabet[i] = ((byte)(48 + j));i++;
/*  52:    */     }
/*  53:130 */     lookUpBase64Alphabet[62] = 43;
/*  54:131 */     lookUpBase64Alphabet[63] = 47;
/*  55:    */   }
/*  56:    */   
/*  57:    */   private static boolean isBase64(byte octect)
/*  58:    */   {
/*  59:135 */     if (octect == 61) {
/*  60:136 */       return true;
/*  61:    */     }
/*  62:137 */     if (base64Alphabet[octect] == -1) {
/*  63:138 */       return false;
/*  64:    */     }
/*  65:140 */     return true;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static boolean isArrayByteBase64(byte[] arrayOctect)
/*  69:    */   {
/*  70:154 */     arrayOctect = discardWhitespace(arrayOctect);
/*  71:    */     
/*  72:156 */     int length = arrayOctect.length;
/*  73:157 */     if (length == 0) {
/*  74:160 */       return true;
/*  75:    */     }
/*  76:162 */     for (int i = 0; i < length; i++) {
/*  77:163 */       if (!isBase64(arrayOctect[i])) {
/*  78:164 */         return false;
/*  79:    */       }
/*  80:    */     }
/*  81:167 */     return true;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public static byte[] encodeBase64(byte[] binaryData)
/*  85:    */   {
/*  86:178 */     return encodeBase64(binaryData, false);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public static byte[] encodeBase64Chunked(byte[] binaryData)
/*  90:    */   {
/*  91:189 */     return encodeBase64(binaryData, true);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public Object decode(Object pObject)
/*  95:    */     throws DecoderException
/*  96:    */   {
/*  97:206 */     if (!(pObject instanceof byte[])) {
/*  98:207 */       throw new DecoderException("Parameter supplied to Base64 decode is not a byte[]");
/*  99:    */     }
/* 100:209 */     return decode((byte[])pObject);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public byte[] decode(byte[] pArray)
/* 104:    */   {
/* 105:220 */     return decodeBase64(pArray);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked)
/* 109:    */   {
/* 110:233 */     int lengthDataBits = binaryData.length * 8;
/* 111:234 */     int fewerThan24bits = lengthDataBits % 24;
/* 112:235 */     int numberTriplets = lengthDataBits / 24;
/* 113:236 */     byte[] encodedData = null;
/* 114:237 */     int encodedDataLength = 0;
/* 115:238 */     int nbrChunks = 0;
/* 116:240 */     if (fewerThan24bits != 0) {
/* 117:242 */       encodedDataLength = (numberTriplets + 1) * 4;
/* 118:    */     } else {
/* 119:245 */       encodedDataLength = numberTriplets * 4;
/* 120:    */     }
/* 121:251 */     if (isChunked)
/* 122:    */     {
/* 123:253 */       nbrChunks = CHUNK_SEPARATOR.length == 0 ? 0 : (int)Math.ceil(encodedDataLength / 76.0F);
/* 124:    */       
/* 125:255 */       encodedDataLength += nbrChunks * CHUNK_SEPARATOR.length;
/* 126:    */     }
/* 127:258 */     encodedData = new byte[encodedDataLength];
/* 128:    */     
/* 129:260 */     byte k = 0;byte l = 0;byte b1 = 0;byte b2 = 0;byte b3 = 0;
/* 130:    */     
/* 131:262 */     int encodedIndex = 0;
/* 132:263 */     int dataIndex = 0;
/* 133:264 */     int i = 0;
/* 134:265 */     int nextSeparatorIndex = 76;
/* 135:266 */     int chunksSoFar = 0;
/* 136:269 */     for (i = 0; i < numberTriplets; i++)
/* 137:    */     {
/* 138:270 */       dataIndex = i * 3;
/* 139:271 */       b1 = binaryData[dataIndex];
/* 140:272 */       b2 = binaryData[(dataIndex + 1)];
/* 141:273 */       b3 = binaryData[(dataIndex + 2)];
/* 142:    */       
/* 143:    */ 
/* 144:    */ 
/* 145:277 */       l = (byte)(b2 & 0xF);
/* 146:278 */       k = (byte)(b1 & 0x3);
/* 147:    */       
/* 148:280 */       byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);
/* 149:    */       
/* 150:282 */       byte val2 = (b2 & 0xFFFFFF80) == 0 ? (byte)(b2 >> 4) : (byte)(b2 >> 4 ^ 0xF0);
/* 151:    */       
/* 152:284 */       byte val3 = (b3 & 0xFFFFFF80) == 0 ? (byte)(b3 >> 6) : (byte)(b3 >> 6 ^ 0xFC);
/* 153:    */       
/* 154:    */ 
/* 155:287 */       encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
/* 156:    */       
/* 157:    */ 
/* 158:    */ 
/* 159:291 */       encodedData[(encodedIndex + 1)] = lookUpBase64Alphabet[(val2 | k << 4)];
/* 160:    */       
/* 161:293 */       encodedData[(encodedIndex + 2)] = lookUpBase64Alphabet[(l << 2 | val3)];
/* 162:    */       
/* 163:295 */       encodedData[(encodedIndex + 3)] = lookUpBase64Alphabet[(b3 & 0x3F)];
/* 164:    */       
/* 165:297 */       encodedIndex += 4;
/* 166:300 */       if (isChunked) {
/* 167:302 */         if (encodedIndex == nextSeparatorIndex)
/* 168:    */         {
/* 169:303 */           System.arraycopy(CHUNK_SEPARATOR, 0, encodedData, encodedIndex, CHUNK_SEPARATOR.length);
/* 170:    */           
/* 171:    */ 
/* 172:    */ 
/* 173:    */ 
/* 174:    */ 
/* 175:309 */           chunksSoFar++;
/* 176:310 */           nextSeparatorIndex = 76 * (chunksSoFar + 1) + chunksSoFar * CHUNK_SEPARATOR.length;
/* 177:    */           
/* 178:    */ 
/* 179:313 */           encodedIndex += CHUNK_SEPARATOR.length;
/* 180:    */         }
/* 181:    */       }
/* 182:    */     }
/* 183:319 */     dataIndex = i * 3;
/* 184:321 */     if (fewerThan24bits == 8)
/* 185:    */     {
/* 186:322 */       b1 = binaryData[dataIndex];
/* 187:323 */       k = (byte)(b1 & 0x3);
/* 188:    */       
/* 189:    */ 
/* 190:326 */       byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);
/* 191:    */       
/* 192:328 */       encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
/* 193:329 */       encodedData[(encodedIndex + 1)] = lookUpBase64Alphabet[(k << 4)];
/* 194:330 */       encodedData[(encodedIndex + 2)] = 61;
/* 195:331 */       encodedData[(encodedIndex + 3)] = 61;
/* 196:    */     }
/* 197:332 */     else if (fewerThan24bits == 16)
/* 198:    */     {
/* 199:334 */       b1 = binaryData[dataIndex];
/* 200:335 */       b2 = binaryData[(dataIndex + 1)];
/* 201:336 */       l = (byte)(b2 & 0xF);
/* 202:337 */       k = (byte)(b1 & 0x3);
/* 203:    */       
/* 204:339 */       byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);
/* 205:    */       
/* 206:341 */       byte val2 = (b2 & 0xFFFFFF80) == 0 ? (byte)(b2 >> 4) : (byte)(b2 >> 4 ^ 0xF0);
/* 207:    */       
/* 208:    */ 
/* 209:344 */       encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
/* 210:345 */       encodedData[(encodedIndex + 1)] = lookUpBase64Alphabet[(val2 | k << 4)];
/* 211:    */       
/* 212:347 */       encodedData[(encodedIndex + 2)] = lookUpBase64Alphabet[(l << 2)];
/* 213:348 */       encodedData[(encodedIndex + 3)] = 61;
/* 214:    */     }
/* 215:351 */     if (isChunked) {
/* 216:353 */       if (chunksSoFar < nbrChunks) {
/* 217:354 */         System.arraycopy(CHUNK_SEPARATOR, 0, encodedData, encodedDataLength - CHUNK_SEPARATOR.length, CHUNK_SEPARATOR.length);
/* 218:    */       }
/* 219:    */     }
/* 220:363 */     return encodedData;
/* 221:    */   }
/* 222:    */   
/* 223:    */   public static byte[] decodeBase64(byte[] base64Data)
/* 224:    */   {
/* 225:374 */     base64Data = discardNonBase64(base64Data);
/* 226:377 */     if (base64Data.length == 0) {
/* 227:378 */       return new byte[0];
/* 228:    */     }
/* 229:381 */     int numberQuadruple = base64Data.length / 4;
/* 230:382 */     byte[] decodedData = null;
/* 231:383 */     byte b1 = 0;byte b2 = 0;byte b3 = 0;byte b4 = 0;byte marker0 = 0;byte marker1 = 0;
/* 232:    */     
/* 233:    */ 
/* 234:    */ 
/* 235:387 */     int encodedIndex = 0;
/* 236:388 */     int dataIndex = 0;
/* 237:    */     
/* 238:    */ 
/* 239:391 */     int lastData = base64Data.length;
/* 240:393 */     while (base64Data[(lastData - 1)] == 61)
/* 241:    */     {
/* 242:394 */       lastData--;
/* 243:394 */       if (lastData == 0) {
/* 244:395 */         return new byte[0];
/* 245:    */       }
/* 246:    */     }
/* 247:398 */     decodedData = new byte[lastData - numberQuadruple];
/* 248:401 */     for (int i = 0; i < numberQuadruple; i++)
/* 249:    */     {
/* 250:402 */       dataIndex = i * 4;
/* 251:403 */       marker0 = base64Data[(dataIndex + 2)];
/* 252:404 */       marker1 = base64Data[(dataIndex + 3)];
/* 253:    */       
/* 254:406 */       b1 = base64Alphabet[base64Data[dataIndex]];
/* 255:407 */       b2 = base64Alphabet[base64Data[(dataIndex + 1)]];
/* 256:409 */       if ((marker0 != 61) && (marker1 != 61))
/* 257:    */       {
/* 258:411 */         b3 = base64Alphabet[marker0];
/* 259:412 */         b4 = base64Alphabet[marker1];
/* 260:    */         
/* 261:414 */         decodedData[encodedIndex] = ((byte)(b1 << 2 | b2 >> 4));
/* 262:415 */         decodedData[(encodedIndex + 1)] = ((byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF));
/* 263:    */         
/* 264:417 */         decodedData[(encodedIndex + 2)] = ((byte)(b3 << 6 | b4));
/* 265:    */       }
/* 266:418 */       else if (marker0 == 61)
/* 267:    */       {
/* 268:420 */         decodedData[encodedIndex] = ((byte)(b1 << 2 | b2 >> 4));
/* 269:    */       }
/* 270:421 */       else if (marker1 == 61)
/* 271:    */       {
/* 272:423 */         b3 = base64Alphabet[marker0];
/* 273:    */         
/* 274:425 */         decodedData[encodedIndex] = ((byte)(b1 << 2 | b2 >> 4));
/* 275:426 */         decodedData[(encodedIndex + 1)] = ((byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF));
/* 276:    */       }
/* 277:429 */       encodedIndex += 3;
/* 278:    */     }
/* 279:431 */     return decodedData;
/* 280:    */   }
/* 281:    */   
/* 282:    */   static byte[] discardWhitespace(byte[] data)
/* 283:    */   {
/* 284:442 */     byte[] groomedData = new byte[data.length];
/* 285:443 */     int bytesCopied = 0;
/* 286:445 */     for (int i = 0; i < data.length; i++) {
/* 287:446 */       switch (data[i])
/* 288:    */       {
/* 289:    */       case 9: 
/* 290:    */       case 10: 
/* 291:    */       case 13: 
/* 292:    */       case 32: 
/* 293:    */         break;
/* 294:    */       default: 
/* 295:453 */         groomedData[(bytesCopied++)] = data[i];
/* 296:    */       }
/* 297:    */     }
/* 298:457 */     byte[] packedData = new byte[bytesCopied];
/* 299:    */     
/* 300:459 */     System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
/* 301:    */     
/* 302:461 */     return packedData;
/* 303:    */   }
/* 304:    */   
/* 305:    */   static byte[] discardNonBase64(byte[] data)
/* 306:    */   {
/* 307:474 */     byte[] groomedData = new byte[data.length];
/* 308:475 */     int bytesCopied = 0;
/* 309:477 */     for (int i = 0; i < data.length; i++) {
/* 310:478 */       if (isBase64(data[i])) {
/* 311:479 */         groomedData[(bytesCopied++)] = data[i];
/* 312:    */       }
/* 313:    */     }
/* 314:483 */     byte[] packedData = new byte[bytesCopied];
/* 315:    */     
/* 316:485 */     System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
/* 317:    */     
/* 318:487 */     return packedData;
/* 319:    */   }
/* 320:    */   
/* 321:    */   public Object encode(Object pObject)
/* 322:    */     throws EncoderException
/* 323:    */   {
/* 324:506 */     if (!(pObject instanceof byte[])) {
/* 325:507 */       throw new EncoderException("Parameter supplied to Base64 encode is not a byte[]");
/* 326:    */     }
/* 327:510 */     return encode((byte[])pObject);
/* 328:    */   }
/* 329:    */   
/* 330:    */   public byte[] encode(byte[] pArray)
/* 331:    */   {
/* 332:521 */     return encodeBase64(pArray, false);
/* 333:    */   }
/* 334:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.binary.Base64
 * JD-Core Version:    0.7.0.1
 */