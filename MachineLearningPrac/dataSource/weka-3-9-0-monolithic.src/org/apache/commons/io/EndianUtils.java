/*   1:    */ package org.apache.commons.io;
/*   2:    */ 
/*   3:    */ import java.io.EOFException;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.io.OutputStream;
/*   7:    */ 
/*   8:    */ public class EndianUtils
/*   9:    */ {
/*  10:    */   public static short swapShort(short value)
/*  11:    */   {
/*  12: 57 */     return (short)(((value >> 0 & 0xFF) << 8) + ((value >> 8 & 0xFF) << 0));
/*  13:    */   }
/*  14:    */   
/*  15:    */   public static int swapInteger(int value)
/*  16:    */   {
/*  17: 67 */     return ((value >> 0 & 0xFF) << 24) + ((value >> 8 & 0xFF) << 16) + ((value >> 16 & 0xFF) << 8) + ((value >> 24 & 0xFF) << 0);
/*  18:    */   }
/*  19:    */   
/*  20:    */   public static long swapLong(long value)
/*  21:    */   {
/*  22: 80 */     return ((value >> 0 & 0xFF) << 56) + ((value >> 8 & 0xFF) << 48) + ((value >> 16 & 0xFF) << 40) + ((value >> 24 & 0xFF) << 32) + ((value >> 32 & 0xFF) << 24) + ((value >> 40 & 0xFF) << 16) + ((value >> 48 & 0xFF) << 8) + ((value >> 56 & 0xFF) << 0);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public static float swapFloat(float value)
/*  26:    */   {
/*  27: 97 */     return Float.intBitsToFloat(swapInteger(Float.floatToIntBits(value)));
/*  28:    */   }
/*  29:    */   
/*  30:    */   public static double swapDouble(double value)
/*  31:    */   {
/*  32:106 */     return Double.longBitsToDouble(swapLong(Double.doubleToLongBits(value)));
/*  33:    */   }
/*  34:    */   
/*  35:    */   public static void writeSwappedShort(byte[] data, int offset, short value)
/*  36:    */   {
/*  37:119 */     data[(offset + 0)] = ((byte)(value >> 0 & 0xFF));
/*  38:120 */     data[(offset + 1)] = ((byte)(value >> 8 & 0xFF));
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static short readSwappedShort(byte[] data, int offset)
/*  42:    */   {
/*  43:131 */     return (short)(((data[(offset + 0)] & 0xFF) << 0) + ((data[(offset + 1)] & 0xFF) << 8));
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static int readSwappedUnsignedShort(byte[] data, int offset)
/*  47:    */   {
/*  48:144 */     return ((data[(offset + 0)] & 0xFF) << 0) + ((data[(offset + 1)] & 0xFF) << 8);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static void writeSwappedInteger(byte[] data, int offset, int value)
/*  52:    */   {
/*  53:156 */     data[(offset + 0)] = ((byte)(value >> 0 & 0xFF));
/*  54:157 */     data[(offset + 1)] = ((byte)(value >> 8 & 0xFF));
/*  55:158 */     data[(offset + 2)] = ((byte)(value >> 16 & 0xFF));
/*  56:159 */     data[(offset + 3)] = ((byte)(value >> 24 & 0xFF));
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static int readSwappedInteger(byte[] data, int offset)
/*  60:    */   {
/*  61:170 */     return ((data[(offset + 0)] & 0xFF) << 0) + ((data[(offset + 1)] & 0xFF) << 8) + ((data[(offset + 2)] & 0xFF) << 16) + ((data[(offset + 3)] & 0xFF) << 24);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public static long readSwappedUnsignedInteger(byte[] data, int offset)
/*  65:    */   {
/*  66:185 */     long low = ((data[(offset + 0)] & 0xFF) << 0) + ((data[(offset + 1)] & 0xFF) << 8) + ((data[(offset + 2)] & 0xFF) << 16);
/*  67:    */     
/*  68:    */ 
/*  69:    */ 
/*  70:189 */     long high = data[(offset + 3)] & 0xFF;
/*  71:    */     
/*  72:191 */     return (high << 24) + (0xFFFFFFFF & low);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public static void writeSwappedLong(byte[] data, int offset, long value)
/*  76:    */   {
/*  77:202 */     data[(offset + 0)] = ((byte)(int)(value >> 0 & 0xFF));
/*  78:203 */     data[(offset + 1)] = ((byte)(int)(value >> 8 & 0xFF));
/*  79:204 */     data[(offset + 2)] = ((byte)(int)(value >> 16 & 0xFF));
/*  80:205 */     data[(offset + 3)] = ((byte)(int)(value >> 24 & 0xFF));
/*  81:206 */     data[(offset + 4)] = ((byte)(int)(value >> 32 & 0xFF));
/*  82:207 */     data[(offset + 5)] = ((byte)(int)(value >> 40 & 0xFF));
/*  83:208 */     data[(offset + 6)] = ((byte)(int)(value >> 48 & 0xFF));
/*  84:209 */     data[(offset + 7)] = ((byte)(int)(value >> 56 & 0xFF));
/*  85:    */   }
/*  86:    */   
/*  87:    */   public static long readSwappedLong(byte[] data, int offset)
/*  88:    */   {
/*  89:220 */     long low = ((data[(offset + 0)] & 0xFF) << 0) + ((data[(offset + 1)] & 0xFF) << 8) + ((data[(offset + 2)] & 0xFF) << 16) + ((data[(offset + 3)] & 0xFF) << 24);
/*  90:    */     
/*  91:    */ 
/*  92:    */ 
/*  93:    */ 
/*  94:225 */     long high = ((data[(offset + 4)] & 0xFF) << 0) + ((data[(offset + 5)] & 0xFF) << 8) + ((data[(offset + 6)] & 0xFF) << 16) + ((data[(offset + 7)] & 0xFF) << 24);
/*  95:    */     
/*  96:    */ 
/*  97:    */ 
/*  98:    */ 
/*  99:230 */     return (high << 32) + (0xFFFFFFFF & low);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public static void writeSwappedFloat(byte[] data, int offset, float value)
/* 103:    */   {
/* 104:241 */     writeSwappedInteger(data, offset, Float.floatToIntBits(value));
/* 105:    */   }
/* 106:    */   
/* 107:    */   public static float readSwappedFloat(byte[] data, int offset)
/* 108:    */   {
/* 109:252 */     return Float.intBitsToFloat(readSwappedInteger(data, offset));
/* 110:    */   }
/* 111:    */   
/* 112:    */   public static void writeSwappedDouble(byte[] data, int offset, double value)
/* 113:    */   {
/* 114:263 */     writeSwappedLong(data, offset, Double.doubleToLongBits(value));
/* 115:    */   }
/* 116:    */   
/* 117:    */   public static double readSwappedDouble(byte[] data, int offset)
/* 118:    */   {
/* 119:274 */     return Double.longBitsToDouble(readSwappedLong(data, offset));
/* 120:    */   }
/* 121:    */   
/* 122:    */   public static void writeSwappedShort(OutputStream output, short value)
/* 123:    */     throws IOException
/* 124:    */   {
/* 125:287 */     output.write((byte)(value >> 0 & 0xFF));
/* 126:288 */     output.write((byte)(value >> 8 & 0xFF));
/* 127:    */   }
/* 128:    */   
/* 129:    */   public static short readSwappedShort(InputStream input)
/* 130:    */     throws IOException
/* 131:    */   {
/* 132:301 */     return (short)(((read(input) & 0xFF) << 0) + ((read(input) & 0xFF) << 8));
/* 133:    */   }
/* 134:    */   
/* 135:    */   public static int readSwappedUnsignedShort(InputStream input)
/* 136:    */     throws IOException
/* 137:    */   {
/* 138:315 */     int value1 = read(input);
/* 139:316 */     int value2 = read(input);
/* 140:    */     
/* 141:318 */     return ((value1 & 0xFF) << 0) + ((value2 & 0xFF) << 8);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public static void writeSwappedInteger(OutputStream output, int value)
/* 145:    */     throws IOException
/* 146:    */   {
/* 147:332 */     output.write((byte)(value >> 0 & 0xFF));
/* 148:333 */     output.write((byte)(value >> 8 & 0xFF));
/* 149:334 */     output.write((byte)(value >> 16 & 0xFF));
/* 150:335 */     output.write((byte)(value >> 24 & 0xFF));
/* 151:    */   }
/* 152:    */   
/* 153:    */   public static int readSwappedInteger(InputStream input)
/* 154:    */     throws IOException
/* 155:    */   {
/* 156:348 */     int value1 = read(input);
/* 157:349 */     int value2 = read(input);
/* 158:350 */     int value3 = read(input);
/* 159:351 */     int value4 = read(input);
/* 160:    */     
/* 161:353 */     return ((value1 & 0xFF) << 0) + ((value2 & 0xFF) << 8) + ((value3 & 0xFF) << 16) + ((value4 & 0xFF) << 24);
/* 162:    */   }
/* 163:    */   
/* 164:    */   public static long readSwappedUnsignedInteger(InputStream input)
/* 165:    */     throws IOException
/* 166:    */   {
/* 167:369 */     int value1 = read(input);
/* 168:370 */     int value2 = read(input);
/* 169:371 */     int value3 = read(input);
/* 170:372 */     int value4 = read(input);
/* 171:    */     
/* 172:374 */     long low = ((value1 & 0xFF) << 0) + ((value2 & 0xFF) << 8) + ((value3 & 0xFF) << 16);
/* 173:    */     
/* 174:    */ 
/* 175:    */ 
/* 176:378 */     long high = value4 & 0xFF;
/* 177:    */     
/* 178:380 */     return (high << 24) + (0xFFFFFFFF & low);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public static void writeSwappedLong(OutputStream output, long value)
/* 182:    */     throws IOException
/* 183:    */   {
/* 184:393 */     output.write((byte)(int)(value >> 0 & 0xFF));
/* 185:394 */     output.write((byte)(int)(value >> 8 & 0xFF));
/* 186:395 */     output.write((byte)(int)(value >> 16 & 0xFF));
/* 187:396 */     output.write((byte)(int)(value >> 24 & 0xFF));
/* 188:397 */     output.write((byte)(int)(value >> 32 & 0xFF));
/* 189:398 */     output.write((byte)(int)(value >> 40 & 0xFF));
/* 190:399 */     output.write((byte)(int)(value >> 48 & 0xFF));
/* 191:400 */     output.write((byte)(int)(value >> 56 & 0xFF));
/* 192:    */   }
/* 193:    */   
/* 194:    */   public static long readSwappedLong(InputStream input)
/* 195:    */     throws IOException
/* 196:    */   {
/* 197:413 */     byte[] bytes = new byte[8];
/* 198:414 */     for (int i = 0; i < 8; i++) {
/* 199:415 */       bytes[i] = ((byte)read(input));
/* 200:    */     }
/* 201:417 */     return readSwappedLong(bytes, 0);
/* 202:    */   }
/* 203:    */   
/* 204:    */   public static void writeSwappedFloat(OutputStream output, float value)
/* 205:    */     throws IOException
/* 206:    */   {
/* 207:430 */     writeSwappedInteger(output, Float.floatToIntBits(value));
/* 208:    */   }
/* 209:    */   
/* 210:    */   public static float readSwappedFloat(InputStream input)
/* 211:    */     throws IOException
/* 212:    */   {
/* 213:443 */     return Float.intBitsToFloat(readSwappedInteger(input));
/* 214:    */   }
/* 215:    */   
/* 216:    */   public static void writeSwappedDouble(OutputStream output, double value)
/* 217:    */     throws IOException
/* 218:    */   {
/* 219:456 */     writeSwappedLong(output, Double.doubleToLongBits(value));
/* 220:    */   }
/* 221:    */   
/* 222:    */   public static double readSwappedDouble(InputStream input)
/* 223:    */     throws IOException
/* 224:    */   {
/* 225:469 */     return Double.longBitsToDouble(readSwappedLong(input));
/* 226:    */   }
/* 227:    */   
/* 228:    */   private static int read(InputStream input)
/* 229:    */     throws IOException
/* 230:    */   {
/* 231:481 */     int value = input.read();
/* 232:483 */     if (-1 == value) {
/* 233:484 */       throw new EOFException("Unexpected EOF reached");
/* 234:    */     }
/* 235:487 */     return value;
/* 236:    */   }
/* 237:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.EndianUtils
 * JD-Core Version:    0.7.0.1
 */