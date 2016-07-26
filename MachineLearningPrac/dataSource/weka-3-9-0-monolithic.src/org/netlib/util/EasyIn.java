/*   1:    */ package org.netlib.util;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ 
/*   7:    */ public class EasyIn
/*   8:    */ {
/*   9: 40 */   static String line = null;
/*  10:    */   static int idx;
/*  11:    */   static int len;
/*  12: 42 */   static String blank_string = "                                                                                           ";
/*  13:    */   
/*  14:    */   public static String myCrappyReadLine()
/*  15:    */     throws IOException
/*  16:    */   {
/*  17: 50 */     StringBuffer localStringBuffer = new StringBuffer();
/*  18: 51 */     int i = 0;
/*  19: 53 */     while (i >= 0)
/*  20:    */     {
/*  21: 54 */       i = System.in.read();
/*  22: 56 */       if (i < 0) {
/*  23: 57 */         return null;
/*  24:    */       }
/*  25: 59 */       if ((char)i == '\n') {
/*  26:    */         break;
/*  27:    */       }
/*  28: 62 */       localStringBuffer.append((char)i);
/*  29:    */     }
/*  30: 65 */     return localStringBuffer.toString();
/*  31:    */   }
/*  32:    */   
/*  33:    */   private void initTokenizer()
/*  34:    */     throws IOException
/*  35:    */   {
/*  36:    */     do
/*  37:    */     {
/*  38: 75 */       line = myCrappyReadLine();
/*  39: 77 */       if (line == null) {
/*  40: 78 */         throw new IOException("EOF");
/*  41:    */       }
/*  42: 80 */       idx = 0;
/*  43: 81 */       len = line.length();
/*  44: 82 */     } while (!hasTokens(line));
/*  45:    */   }
/*  46:    */   
/*  47:    */   private boolean hasTokens(String paramString)
/*  48:    */   {
/*  49: 96 */     int j = paramString.length();
/*  50: 98 */     for (int i = 0; i < j; i++) {
/*  51: 99 */       if (!isDelim(paramString.charAt(i))) {
/*  52:100 */         return true;
/*  53:    */       }
/*  54:    */     }
/*  55:102 */     return false;
/*  56:    */   }
/*  57:    */   
/*  58:    */   private boolean isDelim(char paramChar)
/*  59:    */   {
/*  60:114 */     return (paramChar == ' ') || (paramChar == '\t') || (paramChar == '\r') || (paramChar == '\n');
/*  61:    */   }
/*  62:    */   
/*  63:    */   private boolean moreTokens()
/*  64:    */   {
/*  65:124 */     return idx < len;
/*  66:    */   }
/*  67:    */   
/*  68:    */   private String getToken()
/*  69:    */     throws IOException
/*  70:    */   {
/*  71:137 */     if ((line == null) || (!moreTokens())) {
/*  72:138 */       initTokenizer();
/*  73:    */     }
/*  74:140 */     while ((idx < len) && (isDelim(line.charAt(idx)))) {
/*  75:141 */       idx += 1;
/*  76:    */     }
/*  77:143 */     if (idx == len)
/*  78:    */     {
/*  79:144 */       initTokenizer();
/*  80:145 */       while ((idx < len) && (isDelim(line.charAt(idx)))) {
/*  81:146 */         idx += 1;
/*  82:    */       }
/*  83:    */     }
/*  84:149 */     int i = idx;
/*  85:151 */     while ((idx < len) && (!isDelim(line.charAt(idx)))) {
/*  86:152 */       idx += 1;
/*  87:    */     }
/*  88:154 */     int j = idx;
/*  89:    */     
/*  90:156 */     return line.substring(i, j);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String readchars(int paramInt)
/*  94:    */     throws IOException
/*  95:    */   {
/*  96:172 */     if ((line == null) || (!moreTokens())) {
/*  97:173 */       initTokenizer();
/*  98:    */     }
/*  99:175 */     int i = idx;
/* 100:177 */     if (i + paramInt < len)
/* 101:    */     {
/* 102:179 */       idx += paramInt;
/* 103:180 */       return line.substring(i, i + paramInt);
/* 104:    */     }
/* 105:184 */     idx = len;
/* 106:185 */     return line.substring(i, len) + blank_string.substring(0, paramInt - (len - i));
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String readChars(int paramInt)
/* 110:    */   {
/* 111:    */     try
/* 112:    */     {
/* 113:199 */       return readchars(paramInt);
/* 114:    */     }
/* 115:    */     catch (IOException localIOException)
/* 116:    */     {
/* 117:201 */       System.err.println("IO Exception in EasyIn.readChars");
/* 118:    */     }
/* 119:202 */     return null;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void skipRemaining()
/* 123:    */   {
/* 124:210 */     line = null;
/* 125:211 */     idx = len;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public boolean readboolean()
/* 129:    */     throws IOException
/* 130:    */   {
/* 131:222 */     int i = getToken().charAt(0);
/* 132:223 */     if ((i == 116) || (i == 84)) {
/* 133:224 */       return true;
/* 134:    */     }
/* 135:226 */     return false;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public boolean readBoolean()
/* 139:    */   {
/* 140:    */     try
/* 141:    */     {
/* 142:237 */       int i = getToken().charAt(0);
/* 143:238 */       if ((i == 116) || (i == 84)) {
/* 144:239 */         return true;
/* 145:    */       }
/* 146:241 */       return false;
/* 147:    */     }
/* 148:    */     catch (IOException localIOException)
/* 149:    */     {
/* 150:243 */       System.err.println("IO Exception in EasyIn.readBoolean");
/* 151:    */     }
/* 152:244 */     return false;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public byte readbyte()
/* 156:    */     throws IOException
/* 157:    */   {
/* 158:256 */     return Byte.parseByte(getToken());
/* 159:    */   }
/* 160:    */   
/* 161:    */   public byte readByte()
/* 162:    */   {
/* 163:    */     try
/* 164:    */     {
/* 165:267 */       return Byte.parseByte(getToken());
/* 166:    */     }
/* 167:    */     catch (IOException localIOException)
/* 168:    */     {
/* 169:269 */       System.err.println("IO Exception in EasyIn.readByte");
/* 170:    */     }
/* 171:270 */     return 0;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public short readshort()
/* 175:    */     throws IOException
/* 176:    */   {
/* 177:282 */     return Short.parseShort(getToken());
/* 178:    */   }
/* 179:    */   
/* 180:    */   public short readShort()
/* 181:    */   {
/* 182:    */     try
/* 183:    */     {
/* 184:293 */       return Short.parseShort(getToken());
/* 185:    */     }
/* 186:    */     catch (IOException localIOException)
/* 187:    */     {
/* 188:295 */       System.err.println("IO Exception in EasyIn.readShort");
/* 189:    */     }
/* 190:296 */     return 0;
/* 191:    */   }
/* 192:    */   
/* 193:    */   public int readint()
/* 194:    */     throws IOException
/* 195:    */   {
/* 196:308 */     return Integer.parseInt(getToken());
/* 197:    */   }
/* 198:    */   
/* 199:    */   public int readInt()
/* 200:    */   {
/* 201:    */     try
/* 202:    */     {
/* 203:319 */       return Integer.parseInt(getToken());
/* 204:    */     }
/* 205:    */     catch (IOException localIOException)
/* 206:    */     {
/* 207:321 */       System.err.println("IO Exception in EasyIn.readInt");
/* 208:    */     }
/* 209:322 */     return 0;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public long readlong()
/* 213:    */     throws IOException
/* 214:    */   {
/* 215:334 */     return Long.parseLong(getToken());
/* 216:    */   }
/* 217:    */   
/* 218:    */   public long readLong()
/* 219:    */   {
/* 220:    */     try
/* 221:    */     {
/* 222:345 */       return Long.parseLong(getToken());
/* 223:    */     }
/* 224:    */     catch (IOException localIOException)
/* 225:    */     {
/* 226:347 */       System.err.println("IO Exception in EasyIn.readLong");
/* 227:    */     }
/* 228:348 */     return 0L;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public float readfloat()
/* 232:    */     throws IOException
/* 233:    */   {
/* 234:360 */     return new Float(getToken()).floatValue();
/* 235:    */   }
/* 236:    */   
/* 237:    */   public float readFloat()
/* 238:    */   {
/* 239:    */     try
/* 240:    */     {
/* 241:371 */       return new Float(getToken()).floatValue();
/* 242:    */     }
/* 243:    */     catch (IOException localIOException)
/* 244:    */     {
/* 245:373 */       System.err.println("IO Exception in EasyIn.readFloat");
/* 246:    */     }
/* 247:374 */     return 0.0F;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public double readdouble()
/* 251:    */     throws IOException
/* 252:    */   {
/* 253:386 */     String str = getToken();
/* 254:    */     
/* 255:388 */     str = str.replace('D', 'E');
/* 256:389 */     str = str.replace('d', 'e');
/* 257:    */     
/* 258:391 */     return new Double(str).doubleValue();
/* 259:    */   }
/* 260:    */   
/* 261:    */   public double readDouble()
/* 262:    */   {
/* 263:    */     try
/* 264:    */     {
/* 265:402 */       String str = getToken();
/* 266:    */       
/* 267:404 */       str = str.replace('D', 'E');
/* 268:405 */       str = str.replace('d', 'e');
/* 269:    */       
/* 270:407 */       return new Double(str).doubleValue();
/* 271:    */     }
/* 272:    */     catch (IOException localIOException)
/* 273:    */     {
/* 274:409 */       System.err.println("IO Exception in EasyIn.readDouble");
/* 275:    */     }
/* 276:410 */     return 0.0D;
/* 277:    */   }
/* 278:    */   
/* 279:    */   public char readchar()
/* 280:    */     throws IOException
/* 281:    */   {
/* 282:422 */     return getToken().charAt(0);
/* 283:    */   }
/* 284:    */   
/* 285:    */   public char readChar()
/* 286:    */   {
/* 287:    */     try
/* 288:    */     {
/* 289:433 */       return getToken().charAt(0);
/* 290:    */     }
/* 291:    */     catch (IOException localIOException)
/* 292:    */     {
/* 293:435 */       System.err.println("IO Exception in EasyIn.readChar");
/* 294:    */     }
/* 295:436 */     return '\000';
/* 296:    */   }
/* 297:    */   
/* 298:    */   public String readstring()
/* 299:    */     throws IOException
/* 300:    */   {
/* 301:448 */     return myCrappyReadLine();
/* 302:    */   }
/* 303:    */   
/* 304:    */   public String readString()
/* 305:    */   {
/* 306:    */     try
/* 307:    */     {
/* 308:459 */       return myCrappyReadLine();
/* 309:    */     }
/* 310:    */     catch (IOException localIOException)
/* 311:    */     {
/* 312:461 */       System.err.println("IO Exception in EasyIn.readString");
/* 313:    */     }
/* 314:462 */     return "";
/* 315:    */   }
/* 316:    */   
/* 317:    */   public static void main(String[] paramArrayOfString)
/* 318:    */   {
/* 319:471 */     EasyIn localEasyIn = new EasyIn();
/* 320:    */     
/* 321:473 */     System.out.print("enter char: ");System.out.flush();
/* 322:474 */     System.out.println("You entered: " + localEasyIn.readChar());
/* 323:    */     
/* 324:476 */     System.out.print("enter String: ");System.out.flush();
/* 325:477 */     System.out.println("You entered: " + localEasyIn.readString());
/* 326:    */     
/* 327:479 */     System.out.print("enter boolean: ");System.out.flush();
/* 328:480 */     System.out.println("You entered: " + localEasyIn.readBoolean());
/* 329:    */     
/* 330:482 */     System.out.print("enter byte: ");System.out.flush();
/* 331:483 */     System.out.println("You entered: " + localEasyIn.readByte());
/* 332:    */     
/* 333:485 */     System.out.print("enter short: ");System.out.flush();
/* 334:486 */     System.out.println("You entered: " + localEasyIn.readShort());
/* 335:    */     
/* 336:488 */     System.out.print("enter int: ");System.out.flush();
/* 337:489 */     System.out.println("You entered: " + localEasyIn.readInt());
/* 338:    */     
/* 339:491 */     System.out.print("enter long: ");System.out.flush();
/* 340:492 */     System.out.println("You entered: " + localEasyIn.readLong());
/* 341:    */     
/* 342:494 */     System.out.print("enter float: ");System.out.flush();
/* 343:495 */     System.out.println("You entered: " + localEasyIn.readFloat());
/* 344:    */     
/* 345:497 */     System.out.print("enter double: ");System.out.flush();
/* 346:498 */     System.out.println("You entered: " + localEasyIn.readDouble());
/* 347:    */   }
/* 348:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.util.EasyIn
 * JD-Core Version:    0.7.0.1
 */