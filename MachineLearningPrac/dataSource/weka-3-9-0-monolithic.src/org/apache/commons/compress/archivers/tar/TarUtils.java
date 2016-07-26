/*   1:    */ package org.apache.commons.compress.archivers.tar;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.nio.ByteBuffer;
/*   6:    */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*   7:    */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*   8:    */ 
/*   9:    */ public class TarUtils
/*  10:    */ {
/*  11:    */   private static final int BYTE_MASK = 255;
/*  12: 40 */   static final ZipEncoding DEFAULT_ENCODING = ZipEncodingHelper.getZipEncoding(null);
/*  13: 47 */   static final ZipEncoding FALLBACK_ENCODING = new ZipEncoding()
/*  14:    */   {
/*  15:    */     public boolean canEncode(String name)
/*  16:    */     {
/*  17: 48 */       return true;
/*  18:    */     }
/*  19:    */     
/*  20:    */     public ByteBuffer encode(String name)
/*  21:    */     {
/*  22: 51 */       int length = name.length();
/*  23: 52 */       byte[] buf = new byte[length];
/*  24: 55 */       for (int i = 0; i < length; i++) {
/*  25: 56 */         buf[i] = ((byte)name.charAt(i));
/*  26:    */       }
/*  27: 58 */       return ByteBuffer.wrap(buf);
/*  28:    */     }
/*  29:    */     
/*  30:    */     public String decode(byte[] buffer)
/*  31:    */     {
/*  32: 62 */       int length = buffer.length;
/*  33: 63 */       StringBuilder result = new StringBuilder(length);
/*  34: 65 */       for (byte b : buffer)
/*  35:    */       {
/*  36: 66 */         if (b == 0) {
/*  37:    */           break;
/*  38:    */         }
/*  39: 69 */         result.append((char)(b & 0xFF));
/*  40:    */       }
/*  41: 72 */       return result.toString();
/*  42:    */     }
/*  43:    */   };
/*  44:    */   
/*  45:    */   public static long parseOctal(byte[] buffer, int offset, int length)
/*  46:    */   {
/*  47:102 */     long result = 0L;
/*  48:103 */     int end = offset + length;
/*  49:104 */     int start = offset;
/*  50:106 */     if (length < 2) {
/*  51:107 */       throw new IllegalArgumentException("Length " + length + " must be at least 2");
/*  52:    */     }
/*  53:110 */     if (buffer[start] == 0) {
/*  54:111 */       return 0L;
/*  55:    */     }
/*  56:115 */     while ((start < end) && 
/*  57:116 */       (buffer[start] == 32)) {
/*  58:117 */       start++;
/*  59:    */     }
/*  60:127 */     byte trailer = buffer[(end - 1)];
/*  61:128 */     while ((start < end) && ((trailer == 0) || (trailer == 32)))
/*  62:    */     {
/*  63:129 */       end--;
/*  64:130 */       trailer = buffer[(end - 1)];
/*  65:    */     }
/*  66:133 */     for (; start < end; start++)
/*  67:    */     {
/*  68:134 */       byte currentByte = buffer[start];
/*  69:136 */       if ((currentByte < 48) || (currentByte > 55)) {
/*  70:137 */         throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, start, currentByte));
/*  71:    */       }
/*  72:140 */       result = (result << 3) + (currentByte - 48);
/*  73:    */     }
/*  74:144 */     return result;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static long parseOctalOrBinary(byte[] buffer, int offset, int length)
/*  78:    */   {
/*  79:167 */     if ((buffer[offset] & 0x80) == 0) {
/*  80:168 */       return parseOctal(buffer, offset, length);
/*  81:    */     }
/*  82:170 */     boolean negative = buffer[offset] == -1;
/*  83:171 */     if (length < 9) {
/*  84:172 */       return parseBinaryLong(buffer, offset, length, negative);
/*  85:    */     }
/*  86:174 */     return parseBinaryBigInteger(buffer, offset, length, negative);
/*  87:    */   }
/*  88:    */   
/*  89:    */   private static long parseBinaryLong(byte[] buffer, int offset, int length, boolean negative)
/*  90:    */   {
/*  91:180 */     if (length >= 9) {
/*  92:181 */       throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number" + " exceeds maximum signed long" + " value");
/*  93:    */     }
/*  94:186 */     long val = 0L;
/*  95:187 */     for (int i = 1; i < length; i++) {
/*  96:188 */       val = (val << 8) + (buffer[(offset + i)] & 0xFF);
/*  97:    */     }
/*  98:190 */     if (negative)
/*  99:    */     {
/* 100:192 */       val -= 1L;
/* 101:193 */       val ^= Math.pow(2.0D, (length - 1) * 8) - 1L;
/* 102:    */     }
/* 103:195 */     return negative ? -val : val;
/* 104:    */   }
/* 105:    */   
/* 106:    */   private static long parseBinaryBigInteger(byte[] buffer, int offset, int length, boolean negative)
/* 107:    */   {
/* 108:202 */     byte[] remainder = new byte[length - 1];
/* 109:203 */     System.arraycopy(buffer, offset + 1, remainder, 0, length - 1);
/* 110:204 */     BigInteger val = new BigInteger(remainder);
/* 111:205 */     if (negative) {
/* 112:207 */       val = val.add(BigInteger.valueOf(-1L)).not();
/* 113:    */     }
/* 114:209 */     if (val.bitLength() > 63) {
/* 115:210 */       throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number" + " exceeds maximum signed long" + " value");
/* 116:    */     }
/* 117:215 */     return negative ? -val.longValue() : val.longValue();
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static boolean parseBoolean(byte[] buffer, int offset)
/* 121:    */   {
/* 122:229 */     return buffer[offset] == 1;
/* 123:    */   }
/* 124:    */   
/* 125:    */   private static String exceptionMessage(byte[] buffer, int offset, int length, int current, byte currentByte)
/* 126:    */   {
/* 127:242 */     String string = new String(buffer, offset, length);
/* 128:    */     
/* 129:244 */     string = string.replaceAll("", "{NUL}");
/* 130:245 */     String s = "Invalid byte " + currentByte + " at offset " + (current - offset) + " in '" + string + "' len=" + length;
/* 131:246 */     return s;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public static String parseName(byte[] buffer, int offset, int length)
/* 135:    */   {
/* 136:    */     try
/* 137:    */     {
/* 138:261 */       return parseName(buffer, offset, length, DEFAULT_ENCODING);
/* 139:    */     }
/* 140:    */     catch (IOException ex)
/* 141:    */     {
/* 142:    */       try
/* 143:    */       {
/* 144:264 */         return parseName(buffer, offset, length, FALLBACK_ENCODING);
/* 145:    */       }
/* 146:    */       catch (IOException ex2)
/* 147:    */       {
/* 148:267 */         throw new RuntimeException(ex2);
/* 149:    */       }
/* 150:    */     }
/* 151:    */   }
/* 152:    */   
/* 153:    */   public static String parseName(byte[] buffer, int offset, int length, ZipEncoding encoding)
/* 154:    */     throws IOException
/* 155:    */   {
/* 156:290 */     for (int len = length; len > 0; len--) {
/* 157:292 */       if (buffer[(offset + len - 1)] != 0) {
/* 158:    */         break;
/* 159:    */       }
/* 160:    */     }
/* 161:296 */     if (len > 0)
/* 162:    */     {
/* 163:297 */       byte[] b = new byte[len];
/* 164:298 */       System.arraycopy(buffer, offset, b, 0, len);
/* 165:299 */       return encoding.decode(b);
/* 166:    */     }
/* 167:301 */     return "";
/* 168:    */   }
/* 169:    */   
/* 170:    */   public static int formatNameBytes(String name, byte[] buf, int offset, int length)
/* 171:    */   {
/* 172:    */     try
/* 173:    */     {
/* 174:321 */       return formatNameBytes(name, buf, offset, length, DEFAULT_ENCODING);
/* 175:    */     }
/* 176:    */     catch (IOException ex)
/* 177:    */     {
/* 178:    */       try
/* 179:    */       {
/* 180:324 */         return formatNameBytes(name, buf, offset, length, FALLBACK_ENCODING);
/* 181:    */       }
/* 182:    */       catch (IOException ex2)
/* 183:    */       {
/* 184:328 */         throw new RuntimeException(ex2);
/* 185:    */       }
/* 186:    */     }
/* 187:    */   }
/* 188:    */   
/* 189:    */   public static int formatNameBytes(String name, byte[] buf, int offset, int length, ZipEncoding encoding)
/* 190:    */     throws IOException
/* 191:    */   {
/* 192:355 */     int len = name.length();
/* 193:356 */     ByteBuffer b = encoding.encode(name);
/* 194:357 */     while ((b.limit() > length) && (len > 0)) {
/* 195:358 */       b = encoding.encode(name.substring(0, --len));
/* 196:    */     }
/* 197:360 */     int limit = b.limit() - b.position();
/* 198:361 */     System.arraycopy(b.array(), b.arrayOffset(), buf, offset, limit);
/* 199:364 */     for (int i = limit; i < length; i++) {
/* 200:365 */       buf[(offset + i)] = 0;
/* 201:    */     }
/* 202:368 */     return offset + length;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public static void formatUnsignedOctalString(long value, byte[] buffer, int offset, int length)
/* 206:    */   {
/* 207:382 */     int remaining = length;
/* 208:383 */     remaining--;
/* 209:384 */     if (value == 0L)
/* 210:    */     {
/* 211:385 */       buffer[(offset + remaining--)] = 48;
/* 212:    */     }
/* 213:    */     else
/* 214:    */     {
/* 215:387 */       long val = value;
/* 216:388 */       for (; (remaining >= 0) && (val != 0L); remaining--)
/* 217:    */       {
/* 218:390 */         buffer[(offset + remaining)] = ((byte)(48 + (byte)(int)(val & 0x7)));
/* 219:391 */         val >>>= 3;
/* 220:    */       }
/* 221:394 */       if (val != 0L) {
/* 222:395 */         throw new IllegalArgumentException(value + "=" + Long.toOctalString(value) + " will not fit in octal number buffer of length " + length);
/* 223:    */       }
/* 224:    */     }
/* 225:400 */     for (; remaining >= 0; remaining--) {
/* 226:401 */       buffer[(offset + remaining)] = 48;
/* 227:    */     }
/* 228:    */   }
/* 229:    */   
/* 230:    */   public static int formatOctalBytes(long value, byte[] buf, int offset, int length)
/* 231:    */   {
/* 232:421 */     int idx = length - 2;
/* 233:422 */     formatUnsignedOctalString(value, buf, offset, idx);
/* 234:    */     
/* 235:424 */     buf[(offset + idx++)] = 32;
/* 236:425 */     buf[(offset + idx)] = 0;
/* 237:    */     
/* 238:427 */     return offset + length;
/* 239:    */   }
/* 240:    */   
/* 241:    */   public static int formatLongOctalBytes(long value, byte[] buf, int offset, int length)
/* 242:    */   {
/* 243:446 */     int idx = length - 1;
/* 244:    */     
/* 245:448 */     formatUnsignedOctalString(value, buf, offset, idx);
/* 246:449 */     buf[(offset + idx)] = 32;
/* 247:    */     
/* 248:451 */     return offset + length;
/* 249:    */   }
/* 250:    */   
/* 251:    */   public static int formatLongOctalOrBinaryBytes(long value, byte[] buf, int offset, int length)
/* 252:    */   {
/* 253:475 */     long maxAsOctalChar = length == 8 ? 2097151L : 8589934591L;
/* 254:    */     
/* 255:477 */     boolean negative = value < 0L;
/* 256:478 */     if ((!negative) && (value <= maxAsOctalChar)) {
/* 257:479 */       return formatLongOctalBytes(value, buf, offset, length);
/* 258:    */     }
/* 259:482 */     if (length < 9) {
/* 260:483 */       formatLongBinary(value, buf, offset, length, negative);
/* 261:    */     }
/* 262:485 */     formatBigIntegerBinary(value, buf, offset, length, negative);
/* 263:    */     
/* 264:487 */     buf[offset] = ((byte)(negative ? 'ÿ' : ''));
/* 265:488 */     return offset + length;
/* 266:    */   }
/* 267:    */   
/* 268:    */   private static void formatLongBinary(long value, byte[] buf, int offset, int length, boolean negative)
/* 269:    */   {
/* 270:494 */     int bits = (length - 1) * 8;
/* 271:495 */     long max = 1L << bits;
/* 272:496 */     long val = Math.abs(value);
/* 273:497 */     if (val >= max) {
/* 274:498 */       throw new IllegalArgumentException("Value " + value + " is too large for " + length + " byte field.");
/* 275:    */     }
/* 276:501 */     if (negative)
/* 277:    */     {
/* 278:502 */       val ^= max - 1L;
/* 279:503 */       val |= 255 << bits;
/* 280:504 */       val += 1L;
/* 281:    */     }
/* 282:506 */     for (int i = offset + length - 1; i >= offset; i--)
/* 283:    */     {
/* 284:507 */       buf[i] = ((byte)(int)val);
/* 285:508 */       val >>= 8;
/* 286:    */     }
/* 287:    */   }
/* 288:    */   
/* 289:    */   private static void formatBigIntegerBinary(long value, byte[] buf, int offset, int length, boolean negative)
/* 290:    */   {
/* 291:516 */     BigInteger val = BigInteger.valueOf(value);
/* 292:517 */     byte[] b = val.toByteArray();
/* 293:518 */     int len = b.length;
/* 294:519 */     int off = offset + length - len;
/* 295:520 */     System.arraycopy(b, 0, buf, off, len);
/* 296:521 */     byte fill = (byte)(negative ? 255 : 0);
/* 297:522 */     for (int i = offset + 1; i < off; i++) {
/* 298:523 */       buf[i] = fill;
/* 299:    */     }
/* 300:    */   }
/* 301:    */   
/* 302:    */   public static int formatCheckSumOctalBytes(long value, byte[] buf, int offset, int length)
/* 303:    */   {
/* 304:543 */     int idx = length - 2;
/* 305:544 */     formatUnsignedOctalString(value, buf, offset, idx);
/* 306:    */     
/* 307:546 */     buf[(offset + idx++)] = 0;
/* 308:547 */     buf[(offset + idx)] = 32;
/* 309:    */     
/* 310:549 */     return offset + length;
/* 311:    */   }
/* 312:    */   
/* 313:    */   public static long computeCheckSum(byte[] buf)
/* 314:    */   {
/* 315:559 */     long sum = 0L;
/* 316:561 */     for (byte element : buf) {
/* 317:562 */       sum += (0xFF & element);
/* 318:    */     }
/* 319:565 */     return sum;
/* 320:    */   }
/* 321:    */   
/* 322:    */   public static boolean verifyCheckSum(byte[] header)
/* 323:    */   {
/* 324:604 */     long storedSum = 0L;
/* 325:605 */     long unsignedSum = 0L;
/* 326:606 */     long signedSum = 0L;
/* 327:    */     
/* 328:608 */     int digits = 0;
/* 329:609 */     for (int i = 0; i < header.length; i++)
/* 330:    */     {
/* 331:610 */       byte b = header[i];
/* 332:611 */       if ((148 <= i) && (i < 156))
/* 333:    */       {
/* 334:612 */         if ((48 <= b) && (b <= 55) && (digits++ < 6)) {
/* 335:613 */           storedSum = storedSum * 8L + b - 48L;
/* 336:614 */         } else if (digits > 0) {
/* 337:615 */           digits = 6;
/* 338:    */         }
/* 339:617 */         b = 32;
/* 340:    */       }
/* 341:619 */       unsignedSum += (0xFF & b);
/* 342:620 */       signedSum += b;
/* 343:    */     }
/* 344:623 */     return (storedSum == unsignedSum) || (storedSum == signedSum) || (storedSum > unsignedSum);
/* 345:    */   }
/* 346:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.tar.TarUtils
 * JD-Core Version:    0.7.0.1
 */