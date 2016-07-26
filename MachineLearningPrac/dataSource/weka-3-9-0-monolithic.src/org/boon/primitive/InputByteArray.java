/*   1:    */ package org.boon.primitive;
/*   2:    */ 
/*   3:    */ import java.nio.charset.StandardCharsets;
/*   4:    */ 
/*   5:    */ public class InputByteArray
/*   6:    */   implements Input
/*   7:    */ {
/*   8:    */   private final byte[] array;
/*   9:    */   private int location;
/*  10:    */   
/*  11:    */   public InputByteArray(byte[] array)
/*  12:    */   {
/*  13: 41 */     this.array = array;
/*  14:    */   }
/*  15:    */   
/*  16:    */   public void readFully(byte[] readToThis)
/*  17:    */   {
/*  18: 47 */     Byt._idx(readToThis, 0, this.array, this.location, readToThis.length);
/*  19: 48 */     this.location += readToThis.length;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void readFully(byte[] readToThis, int off, int len)
/*  23:    */   {
/*  24: 53 */     Byt._idx(readToThis, off, this.array, this.location, len);
/*  25: 54 */     this.location += readToThis.length;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public int skipBytes(int n)
/*  29:    */   {
/*  30: 59 */     return this.location += n;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void location(int n)
/*  34:    */   {
/*  35: 64 */     this.location = n;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public int location()
/*  39:    */   {
/*  40: 69 */     return this.location;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void reset()
/*  44:    */   {
/*  45: 74 */     this.location = 0;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public boolean readBoolean()
/*  49:    */   {
/*  50: 79 */     byte val = Byt.idx(this.array, this.location);
/*  51:    */     
/*  52: 81 */     this.location += 1;
/*  53: 83 */     if (val == 0) {
/*  54: 84 */       return false;
/*  55:    */     }
/*  56: 86 */     return true;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public byte readByte()
/*  60:    */   {
/*  61: 93 */     byte value = Byt.idx(this.array, this.location);
/*  62: 94 */     this.location += 1;
/*  63: 95 */     return value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public short readUnsignedByte()
/*  67:    */   {
/*  68:102 */     short value = Byt.idxUnsignedByte(this.array, this.location);
/*  69:103 */     this.location += 1;
/*  70:104 */     return value;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public short readShort()
/*  74:    */   {
/*  75:111 */     short value = Byt.idxShort(this.array, this.location);
/*  76:112 */     this.location += 2;
/*  77:    */     
/*  78:114 */     return value;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public int readUnsignedShort()
/*  82:    */   {
/*  83:120 */     int value = Byt.idxUnsignedShort(this.array, this.location);
/*  84:121 */     this.location += 2;
/*  85:    */     
/*  86:123 */     return value;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public char readChar()
/*  90:    */   {
/*  91:129 */     char value = Byt.idxChar(this.array, this.location);
/*  92:130 */     this.location += 2;
/*  93:    */     
/*  94:132 */     return value;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public int readInt()
/*  98:    */   {
/*  99:138 */     int value = Byt.idxInt(this.array, this.location);
/* 100:139 */     this.location += 4;
/* 101:    */     
/* 102:141 */     return value;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public long readUnsignedInt()
/* 106:    */   {
/* 107:147 */     long value = Byt.idxUnsignedInt(this.array, this.location);
/* 108:148 */     this.location += 4;
/* 109:    */     
/* 110:150 */     return value;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public long readLong()
/* 114:    */   {
/* 115:156 */     long value = Byt.idxLong(this.array, this.location);
/* 116:157 */     this.location += 8;
/* 117:    */     
/* 118:159 */     return value;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public float readFloat()
/* 122:    */   {
/* 123:165 */     float value = Byt.idxFloat(this.array, this.location);
/* 124:166 */     this.location += 4;
/* 125:167 */     return value;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public double readDouble()
/* 129:    */   {
/* 130:174 */     double value = Byt.idxDouble(this.array, this.location);
/* 131:175 */     this.location += 8;
/* 132:176 */     return value;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public String readSmallString()
/* 136:    */   {
/* 137:183 */     short size = readUnsignedByte();
/* 138:    */     
/* 139:185 */     byte[] bytes = readBytes(size);
/* 140:186 */     return new String(bytes, StandardCharsets.UTF_8);
/* 141:    */   }
/* 142:    */   
/* 143:    */   public String readMediumString()
/* 144:    */   {
/* 145:192 */     int size = readUnsignedShort();
/* 146:    */     
/* 147:194 */     byte[] bytes = readBytes(size);
/* 148:    */     
/* 149:196 */     return new String(bytes, StandardCharsets.UTF_8);
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String readLargeString()
/* 153:    */   {
/* 154:202 */     int size = readInt();
/* 155:    */     
/* 156:    */ 
/* 157:205 */     byte[] bytes = readBytes(size);
/* 158:    */     
/* 159:207 */     return new String(bytes, StandardCharsets.UTF_8);
/* 160:    */   }
/* 161:    */   
/* 162:    */   public byte[] readSmallByteArray()
/* 163:    */   {
/* 164:213 */     short size = readUnsignedByte();
/* 165:    */     
/* 166:215 */     byte[] bytes = readBytes(size);
/* 167:216 */     return bytes;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public byte[] readMediumByteArray()
/* 171:    */   {
/* 172:222 */     int size = readUnsignedShort();
/* 173:    */     
/* 174:224 */     byte[] bytes = readBytes(size);
/* 175:    */     
/* 176:226 */     return bytes;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public short[] readSmallShortArray()
/* 180:    */   {
/* 181:232 */     short size = readUnsignedByte();
/* 182:    */     
/* 183:234 */     return doReadShortArray(size);
/* 184:    */   }
/* 185:    */   
/* 186:    */   private short[] doReadShortArray(int size)
/* 187:    */   {
/* 188:240 */     short[] values = new short[size];
/* 189:242 */     for (int index = 0; index < values.length; index++) {
/* 190:243 */       values[index] = readShort();
/* 191:    */     }
/* 192:245 */     return values;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public short[] readLargeShortArray()
/* 196:    */   {
/* 197:251 */     int size = readInt();
/* 198:    */     
/* 199:253 */     return doReadShortArray(size);
/* 200:    */   }
/* 201:    */   
/* 202:    */   public short[] readMediumShortArray()
/* 203:    */   {
/* 204:260 */     int size = readUnsignedShort();
/* 205:    */     
/* 206:262 */     return doReadShortArray(size);
/* 207:    */   }
/* 208:    */   
/* 209:    */   public byte[] readLargeByteArray()
/* 210:    */   {
/* 211:269 */     int size = readInt();
/* 212:    */     
/* 213:    */ 
/* 214:272 */     byte[] bytes = readBytes(size);
/* 215:    */     
/* 216:274 */     return bytes;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public byte[] readBytes(int size)
/* 220:    */   {
/* 221:279 */     byte[] bytes = new byte[size];
/* 222:280 */     readFully(bytes);
/* 223:281 */     return bytes;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public int[] readSmallIntArray()
/* 227:    */   {
/* 228:288 */     short size = readUnsignedByte();
/* 229:    */     
/* 230:290 */     return doReadIntArray(size);
/* 231:    */   }
/* 232:    */   
/* 233:    */   private int[] doReadIntArray(int size)
/* 234:    */   {
/* 235:296 */     int[] values = new int[size];
/* 236:298 */     for (int index = 0; index < values.length; index++) {
/* 237:299 */       values[index] = readInt();
/* 238:    */     }
/* 239:301 */     return values;
/* 240:    */   }
/* 241:    */   
/* 242:    */   public int[] readLargeIntArray()
/* 243:    */   {
/* 244:307 */     int size = readInt();
/* 245:    */     
/* 246:309 */     return doReadIntArray(size);
/* 247:    */   }
/* 248:    */   
/* 249:    */   public int[] readMediumIntArray()
/* 250:    */   {
/* 251:316 */     int size = readUnsignedShort();
/* 252:    */     
/* 253:318 */     return doReadIntArray(size);
/* 254:    */   }
/* 255:    */   
/* 256:    */   public long[] readSmallLongArray()
/* 257:    */   {
/* 258:329 */     short size = readUnsignedByte();
/* 259:    */     
/* 260:331 */     return doReadLongArray(size);
/* 261:    */   }
/* 262:    */   
/* 263:    */   private long[] doReadLongArray(int size)
/* 264:    */   {
/* 265:337 */     long[] values = new long[size];
/* 266:339 */     for (int index = 0; index < values.length; index++) {
/* 267:340 */       values[index] = readLong();
/* 268:    */     }
/* 269:342 */     return values;
/* 270:    */   }
/* 271:    */   
/* 272:    */   public long[] readLargeLongArray()
/* 273:    */   {
/* 274:348 */     int size = readInt();
/* 275:    */     
/* 276:350 */     return doReadLongArray(size);
/* 277:    */   }
/* 278:    */   
/* 279:    */   public long[] readMediumLongArray()
/* 280:    */   {
/* 281:357 */     int size = readUnsignedShort();
/* 282:    */     
/* 283:359 */     return doReadLongArray(size);
/* 284:    */   }
/* 285:    */   
/* 286:    */   public float[] readSmallFloatArray()
/* 287:    */   {
/* 288:365 */     short size = readUnsignedByte();
/* 289:366 */     return doReadFloatArray(size);
/* 290:    */   }
/* 291:    */   
/* 292:    */   public float[] readLargeFloatArray()
/* 293:    */   {
/* 294:371 */     int size = readInt();
/* 295:372 */     return doReadFloatArray(size);
/* 296:    */   }
/* 297:    */   
/* 298:    */   public float[] readMediumFloatArray()
/* 299:    */   {
/* 300:377 */     int size = readUnsignedShort();
/* 301:378 */     return doReadFloatArray(size);
/* 302:    */   }
/* 303:    */   
/* 304:    */   private float[] doReadFloatArray(int size)
/* 305:    */   {
/* 306:382 */     float[] values = new float[size];
/* 307:383 */     for (int index = 0; index < values.length; index++) {
/* 308:384 */       values[index] = readFloat();
/* 309:    */     }
/* 310:386 */     return values;
/* 311:    */   }
/* 312:    */   
/* 313:    */   public double[] readSmallDoubleArray()
/* 314:    */   {
/* 315:392 */     short size = readUnsignedByte();
/* 316:393 */     return doReadDoubleArray(size);
/* 317:    */   }
/* 318:    */   
/* 319:    */   public double[] readLargeDoubleArray()
/* 320:    */   {
/* 321:398 */     int size = readInt();
/* 322:399 */     return doReadDoubleArray(size);
/* 323:    */   }
/* 324:    */   
/* 325:    */   public double[] readMediumDoubleArray()
/* 326:    */   {
/* 327:404 */     int size = readUnsignedShort();
/* 328:405 */     return doReadDoubleArray(size);
/* 329:    */   }
/* 330:    */   
/* 331:    */   private double[] doReadDoubleArray(int size)
/* 332:    */   {
/* 333:410 */     double[] values = new double[size];
/* 334:411 */     for (int index = 0; index < values.length; index++) {
/* 335:412 */       values[index] = readDouble();
/* 336:    */     }
/* 337:414 */     return values;
/* 338:    */   }
/* 339:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.InputByteArray
 * JD-Core Version:    0.7.0.1
 */