/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.Reader;
/*   7:    */ import java.io.UnsupportedEncodingException;
/*   8:    */ 
/*   9:    */ public class SimpleCharStream
/*  10:    */ {
/*  11:    */   public static final boolean staticFlag = false;
/*  12:    */   int bufsize;
/*  13:    */   int available;
/*  14:    */   int tokenBegin;
/*  15: 15 */   public int bufpos = -1;
/*  16:    */   protected int[] bufline;
/*  17:    */   protected int[] bufcolumn;
/*  18: 19 */   protected int column = 0;
/*  19: 20 */   protected int line = 1;
/*  20: 22 */   protected boolean prevCharIsCR = false;
/*  21: 23 */   protected boolean prevCharIsLF = false;
/*  22:    */   protected Reader inputStream;
/*  23:    */   protected char[] buffer;
/*  24: 28 */   protected int maxNextCharInd = 0;
/*  25: 29 */   protected int inBuf = 0;
/*  26: 30 */   protected int tabSize = 8;
/*  27:    */   
/*  28:    */   protected void setTabSize(int paramInt)
/*  29:    */   {
/*  30: 32 */     this.tabSize = paramInt;
/*  31:    */   }
/*  32:    */   
/*  33:    */   protected int getTabSize(int paramInt)
/*  34:    */   {
/*  35: 33 */     return this.tabSize;
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected void ExpandBuff(boolean paramBoolean)
/*  39:    */   {
/*  40: 38 */     char[] arrayOfChar = new char[this.bufsize + 2048];
/*  41: 39 */     int[] arrayOfInt1 = new int[this.bufsize + 2048];
/*  42: 40 */     int[] arrayOfInt2 = new int[this.bufsize + 2048];
/*  43:    */     try
/*  44:    */     {
/*  45: 44 */       if (paramBoolean)
/*  46:    */       {
/*  47: 46 */         System.arraycopy(this.buffer, this.tokenBegin, arrayOfChar, 0, this.bufsize - this.tokenBegin);
/*  48: 47 */         System.arraycopy(this.buffer, 0, arrayOfChar, this.bufsize - this.tokenBegin, this.bufpos);
/*  49:    */         
/*  50: 49 */         this.buffer = arrayOfChar;
/*  51:    */         
/*  52: 51 */         System.arraycopy(this.bufline, this.tokenBegin, arrayOfInt1, 0, this.bufsize - this.tokenBegin);
/*  53: 52 */         System.arraycopy(this.bufline, 0, arrayOfInt1, this.bufsize - this.tokenBegin, this.bufpos);
/*  54: 53 */         this.bufline = arrayOfInt1;
/*  55:    */         
/*  56: 55 */         System.arraycopy(this.bufcolumn, this.tokenBegin, arrayOfInt2, 0, this.bufsize - this.tokenBegin);
/*  57: 56 */         System.arraycopy(this.bufcolumn, 0, arrayOfInt2, this.bufsize - this.tokenBegin, this.bufpos);
/*  58: 57 */         this.bufcolumn = arrayOfInt2;
/*  59:    */         
/*  60: 59 */         this.maxNextCharInd = (this.bufpos += this.bufsize - this.tokenBegin);
/*  61:    */       }
/*  62:    */       else
/*  63:    */       {
/*  64: 63 */         System.arraycopy(this.buffer, this.tokenBegin, arrayOfChar, 0, this.bufsize - this.tokenBegin);
/*  65: 64 */         this.buffer = arrayOfChar;
/*  66:    */         
/*  67: 66 */         System.arraycopy(this.bufline, this.tokenBegin, arrayOfInt1, 0, this.bufsize - this.tokenBegin);
/*  68: 67 */         this.bufline = arrayOfInt1;
/*  69:    */         
/*  70: 69 */         System.arraycopy(this.bufcolumn, this.tokenBegin, arrayOfInt2, 0, this.bufsize - this.tokenBegin);
/*  71: 70 */         this.bufcolumn = arrayOfInt2;
/*  72:    */         
/*  73: 72 */         this.maxNextCharInd = (this.bufpos -= this.tokenBegin);
/*  74:    */       }
/*  75:    */     }
/*  76:    */     catch (Throwable localThrowable)
/*  77:    */     {
/*  78: 77 */       throw new Error(localThrowable.getMessage());
/*  79:    */     }
/*  80: 81 */     this.bufsize += 2048;
/*  81: 82 */     this.available = this.bufsize;
/*  82: 83 */     this.tokenBegin = 0;
/*  83:    */   }
/*  84:    */   
/*  85:    */   protected void FillBuff()
/*  86:    */     throws IOException
/*  87:    */   {
/*  88: 88 */     if (this.maxNextCharInd == this.available) {
/*  89: 90 */       if (this.available == this.bufsize)
/*  90:    */       {
/*  91: 92 */         if (this.tokenBegin > 2048)
/*  92:    */         {
/*  93: 94 */           this.bufpos = (this.maxNextCharInd = 0);
/*  94: 95 */           this.available = this.tokenBegin;
/*  95:    */         }
/*  96: 97 */         else if (this.tokenBegin < 0)
/*  97:    */         {
/*  98: 98 */           this.bufpos = (this.maxNextCharInd = 0);
/*  99:    */         }
/* 100:    */         else
/* 101:    */         {
/* 102:100 */           ExpandBuff(false);
/* 103:    */         }
/* 104:    */       }
/* 105:102 */       else if (this.available > this.tokenBegin) {
/* 106:103 */         this.available = this.bufsize;
/* 107:104 */       } else if (this.tokenBegin - this.available < 2048) {
/* 108:105 */         ExpandBuff(true);
/* 109:    */       } else {
/* 110:107 */         this.available = this.tokenBegin;
/* 111:    */       }
/* 112:    */     }
/* 113:    */     try
/* 114:    */     {
/* 115:    */       int i;
/* 116:112 */       if ((i = this.inputStream.read(this.buffer, this.maxNextCharInd, this.available - this.maxNextCharInd)) == -1)
/* 117:    */       {
/* 118:115 */         this.inputStream.close();
/* 119:116 */         throw new IOException();
/* 120:    */       }
/* 121:119 */       this.maxNextCharInd += i;
/* 122:120 */       return;
/* 123:    */     }
/* 124:    */     catch (IOException localIOException)
/* 125:    */     {
/* 126:123 */       this.bufpos -= 1;
/* 127:124 */       backup(0);
/* 128:125 */       if (this.tokenBegin == -1) {
/* 129:126 */         this.tokenBegin = this.bufpos;
/* 130:    */       }
/* 131:127 */       throw localIOException;
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   public char BeginToken()
/* 136:    */     throws IOException
/* 137:    */   {
/* 138:133 */     this.tokenBegin = -1;
/* 139:134 */     char c = readChar();
/* 140:135 */     this.tokenBegin = this.bufpos;
/* 141:    */     
/* 142:137 */     return c;
/* 143:    */   }
/* 144:    */   
/* 145:    */   protected void UpdateLineColumn(char paramChar)
/* 146:    */   {
/* 147:142 */     this.column += 1;
/* 148:144 */     if (this.prevCharIsLF)
/* 149:    */     {
/* 150:146 */       this.prevCharIsLF = false;
/* 151:147 */       this.line += (this.column = 1);
/* 152:    */     }
/* 153:149 */     else if (this.prevCharIsCR)
/* 154:    */     {
/* 155:151 */       this.prevCharIsCR = false;
/* 156:152 */       if (paramChar == '\n') {
/* 157:154 */         this.prevCharIsLF = true;
/* 158:    */       } else {
/* 159:157 */         this.line += (this.column = 1);
/* 160:    */       }
/* 161:    */     }
/* 162:160 */     switch (paramChar)
/* 163:    */     {
/* 164:    */     case '\r': 
/* 165:163 */       this.prevCharIsCR = true;
/* 166:164 */       break;
/* 167:    */     case '\n': 
/* 168:166 */       this.prevCharIsLF = true;
/* 169:167 */       break;
/* 170:    */     case '\t': 
/* 171:169 */       this.column -= 1;
/* 172:170 */       this.column += this.tabSize - this.column % this.tabSize;
/* 173:171 */       break;
/* 174:    */     }
/* 175:176 */     this.bufline[this.bufpos] = this.line;
/* 176:177 */     this.bufcolumn[this.bufpos] = this.column;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public char readChar()
/* 180:    */     throws IOException
/* 181:    */   {
/* 182:182 */     if (this.inBuf > 0)
/* 183:    */     {
/* 184:184 */       this.inBuf -= 1;
/* 185:186 */       if (++this.bufpos == this.bufsize) {
/* 186:187 */         this.bufpos = 0;
/* 187:    */       }
/* 188:189 */       return this.buffer[this.bufpos];
/* 189:    */     }
/* 190:192 */     if (++this.bufpos >= this.maxNextCharInd) {
/* 191:193 */       FillBuff();
/* 192:    */     }
/* 193:195 */     char c = this.buffer[this.bufpos];
/* 194:    */     
/* 195:197 */     UpdateLineColumn(c);
/* 196:198 */     return c;
/* 197:    */   }
/* 198:    */   
/* 199:    */   /**
/* 200:    */    * @deprecated
/* 201:    */    */
/* 202:    */   public int getColumn()
/* 203:    */   {
/* 204:207 */     return this.bufcolumn[this.bufpos];
/* 205:    */   }
/* 206:    */   
/* 207:    */   /**
/* 208:    */    * @deprecated
/* 209:    */    */
/* 210:    */   public int getLine()
/* 211:    */   {
/* 212:216 */     return this.bufline[this.bufpos];
/* 213:    */   }
/* 214:    */   
/* 215:    */   public int getEndColumn()
/* 216:    */   {
/* 217:220 */     return this.bufcolumn[this.bufpos];
/* 218:    */   }
/* 219:    */   
/* 220:    */   public int getEndLine()
/* 221:    */   {
/* 222:224 */     return this.bufline[this.bufpos];
/* 223:    */   }
/* 224:    */   
/* 225:    */   public int getBeginColumn()
/* 226:    */   {
/* 227:228 */     return this.bufcolumn[this.tokenBegin];
/* 228:    */   }
/* 229:    */   
/* 230:    */   public int getBeginLine()
/* 231:    */   {
/* 232:232 */     return this.bufline[this.tokenBegin];
/* 233:    */   }
/* 234:    */   
/* 235:    */   public void backup(int paramInt)
/* 236:    */   {
/* 237:237 */     this.inBuf += paramInt;
/* 238:238 */     if (this.bufpos -= paramInt < 0) {
/* 239:239 */       this.bufpos += this.bufsize;
/* 240:    */     }
/* 241:    */   }
/* 242:    */   
/* 243:    */   public SimpleCharStream(Reader paramReader, int paramInt1, int paramInt2, int paramInt3)
/* 244:    */   {
/* 245:245 */     this.inputStream = paramReader;
/* 246:246 */     this.line = paramInt1;
/* 247:247 */     this.column = (paramInt2 - 1);
/* 248:    */     
/* 249:249 */     this.available = (this.bufsize = paramInt3);
/* 250:250 */     this.buffer = new char[paramInt3];
/* 251:251 */     this.bufline = new int[paramInt3];
/* 252:252 */     this.bufcolumn = new int[paramInt3];
/* 253:    */   }
/* 254:    */   
/* 255:    */   public SimpleCharStream(Reader paramReader, int paramInt1, int paramInt2)
/* 256:    */   {
/* 257:258 */     this(paramReader, paramInt1, paramInt2, 4096);
/* 258:    */   }
/* 259:    */   
/* 260:    */   public SimpleCharStream(Reader paramReader)
/* 261:    */   {
/* 262:263 */     this(paramReader, 1, 1, 4096);
/* 263:    */   }
/* 264:    */   
/* 265:    */   public void ReInit(Reader paramReader, int paramInt1, int paramInt2, int paramInt3)
/* 266:    */   {
/* 267:268 */     this.inputStream = paramReader;
/* 268:269 */     this.line = paramInt1;
/* 269:270 */     this.column = (paramInt2 - 1);
/* 270:272 */     if ((this.buffer == null) || (paramInt3 != this.buffer.length))
/* 271:    */     {
/* 272:274 */       this.available = (this.bufsize = paramInt3);
/* 273:275 */       this.buffer = new char[paramInt3];
/* 274:276 */       this.bufline = new int[paramInt3];
/* 275:277 */       this.bufcolumn = new int[paramInt3];
/* 276:    */     }
/* 277:279 */     this.prevCharIsLF = (this.prevCharIsCR = 0);
/* 278:280 */     this.tokenBegin = (this.inBuf = this.maxNextCharInd = 0);
/* 279:281 */     this.bufpos = -1;
/* 280:    */   }
/* 281:    */   
/* 282:    */   public void ReInit(Reader paramReader, int paramInt1, int paramInt2)
/* 283:    */   {
/* 284:287 */     ReInit(paramReader, paramInt1, paramInt2, 4096);
/* 285:    */   }
/* 286:    */   
/* 287:    */   public void ReInit(Reader paramReader)
/* 288:    */   {
/* 289:292 */     ReInit(paramReader, 1, 1, 4096);
/* 290:    */   }
/* 291:    */   
/* 292:    */   public SimpleCharStream(InputStream paramInputStream, String paramString, int paramInt1, int paramInt2, int paramInt3)
/* 293:    */     throws UnsupportedEncodingException
/* 294:    */   {
/* 295:297 */     this(paramString == null ? new InputStreamReader(paramInputStream) : new InputStreamReader(paramInputStream, paramString), paramInt1, paramInt2, paramInt3);
/* 296:    */   }
/* 297:    */   
/* 298:    */   public SimpleCharStream(InputStream paramInputStream, int paramInt1, int paramInt2, int paramInt3)
/* 299:    */   {
/* 300:303 */     this(new InputStreamReader(paramInputStream), paramInt1, paramInt2, paramInt3);
/* 301:    */   }
/* 302:    */   
/* 303:    */   public SimpleCharStream(InputStream paramInputStream, String paramString, int paramInt1, int paramInt2)
/* 304:    */     throws UnsupportedEncodingException
/* 305:    */   {
/* 306:309 */     this(paramInputStream, paramString, paramInt1, paramInt2, 4096);
/* 307:    */   }
/* 308:    */   
/* 309:    */   public SimpleCharStream(InputStream paramInputStream, int paramInt1, int paramInt2)
/* 310:    */   {
/* 311:315 */     this(paramInputStream, paramInt1, paramInt2, 4096);
/* 312:    */   }
/* 313:    */   
/* 314:    */   public SimpleCharStream(InputStream paramInputStream, String paramString)
/* 315:    */     throws UnsupportedEncodingException
/* 316:    */   {
/* 317:320 */     this(paramInputStream, paramString, 1, 1, 4096);
/* 318:    */   }
/* 319:    */   
/* 320:    */   public SimpleCharStream(InputStream paramInputStream)
/* 321:    */   {
/* 322:325 */     this(paramInputStream, 1, 1, 4096);
/* 323:    */   }
/* 324:    */   
/* 325:    */   public void ReInit(InputStream paramInputStream, String paramString, int paramInt1, int paramInt2, int paramInt3)
/* 326:    */     throws UnsupportedEncodingException
/* 327:    */   {
/* 328:331 */     ReInit(paramString == null ? new InputStreamReader(paramInputStream) : new InputStreamReader(paramInputStream, paramString), paramInt1, paramInt2, paramInt3);
/* 329:    */   }
/* 330:    */   
/* 331:    */   public void ReInit(InputStream paramInputStream, int paramInt1, int paramInt2, int paramInt3)
/* 332:    */   {
/* 333:337 */     ReInit(new InputStreamReader(paramInputStream), paramInt1, paramInt2, paramInt3);
/* 334:    */   }
/* 335:    */   
/* 336:    */   public void ReInit(InputStream paramInputStream, String paramString)
/* 337:    */     throws UnsupportedEncodingException
/* 338:    */   {
/* 339:342 */     ReInit(paramInputStream, paramString, 1, 1, 4096);
/* 340:    */   }
/* 341:    */   
/* 342:    */   public void ReInit(InputStream paramInputStream)
/* 343:    */   {
/* 344:347 */     ReInit(paramInputStream, 1, 1, 4096);
/* 345:    */   }
/* 346:    */   
/* 347:    */   public void ReInit(InputStream paramInputStream, String paramString, int paramInt1, int paramInt2)
/* 348:    */     throws UnsupportedEncodingException
/* 349:    */   {
/* 350:352 */     ReInit(paramInputStream, paramString, paramInt1, paramInt2, 4096);
/* 351:    */   }
/* 352:    */   
/* 353:    */   public void ReInit(InputStream paramInputStream, int paramInt1, int paramInt2)
/* 354:    */   {
/* 355:357 */     ReInit(paramInputStream, paramInt1, paramInt2, 4096);
/* 356:    */   }
/* 357:    */   
/* 358:    */   public String GetImage()
/* 359:    */   {
/* 360:361 */     if (this.bufpos >= this.tokenBegin) {
/* 361:362 */       return new String(this.buffer, this.tokenBegin, this.bufpos - this.tokenBegin + 1);
/* 362:    */     }
/* 363:364 */     return new String(this.buffer, this.tokenBegin, this.bufsize - this.tokenBegin) + new String(this.buffer, 0, this.bufpos + 1);
/* 364:    */   }
/* 365:    */   
/* 366:    */   public char[] GetSuffix(int paramInt)
/* 367:    */   {
/* 368:370 */     char[] arrayOfChar = new char[paramInt];
/* 369:372 */     if (this.bufpos + 1 >= paramInt)
/* 370:    */     {
/* 371:373 */       System.arraycopy(this.buffer, this.bufpos - paramInt + 1, arrayOfChar, 0, paramInt);
/* 372:    */     }
/* 373:    */     else
/* 374:    */     {
/* 375:376 */       System.arraycopy(this.buffer, this.bufsize - (paramInt - this.bufpos - 1), arrayOfChar, 0, paramInt - this.bufpos - 1);
/* 376:    */       
/* 377:378 */       System.arraycopy(this.buffer, 0, arrayOfChar, paramInt - this.bufpos - 1, this.bufpos + 1);
/* 378:    */     }
/* 379:381 */     return arrayOfChar;
/* 380:    */   }
/* 381:    */   
/* 382:    */   public void Done()
/* 383:    */   {
/* 384:386 */     this.buffer = null;
/* 385:387 */     this.bufline = null;
/* 386:388 */     this.bufcolumn = null;
/* 387:    */   }
/* 388:    */   
/* 389:    */   public void adjustBeginLineColumn(int paramInt1, int paramInt2)
/* 390:    */   {
/* 391:396 */     int i = this.tokenBegin;
/* 392:    */     int j;
/* 393:399 */     if (this.bufpos >= this.tokenBegin) {
/* 394:401 */       j = this.bufpos - this.tokenBegin + this.inBuf + 1;
/* 395:    */     } else {
/* 396:405 */       j = this.bufsize - this.tokenBegin + this.bufpos + 1 + this.inBuf;
/* 397:    */     }
/* 398:408 */     int k = 0;int m = 0;int n = 0;
/* 399:409 */     int i1 = 0;int i2 = 0;
/* 400:412 */     while ((k < j) && (this.bufline[(m = i % this.bufsize)] == this.bufline[(n = ++i % this.bufsize)]))
/* 401:    */     {
/* 402:414 */       this.bufline[m] = paramInt1;
/* 403:415 */       i1 = i2 + this.bufcolumn[n] - this.bufcolumn[m];
/* 404:416 */       this.bufcolumn[m] = (paramInt2 + i2);
/* 405:417 */       i2 = i1;
/* 406:418 */       k++;
/* 407:    */     }
/* 408:421 */     if (k < j)
/* 409:    */     {
/* 410:423 */       this.bufline[m] = (paramInt1++);
/* 411:424 */       this.bufcolumn[m] = (paramInt2 + i2);
/* 412:426 */       while (k++ < j) {
/* 413:428 */         if (this.bufline[(m = i % this.bufsize)] != this.bufline[(++i % this.bufsize)]) {
/* 414:429 */           this.bufline[m] = (paramInt1++);
/* 415:    */         } else {
/* 416:431 */           this.bufline[m] = paramInt1;
/* 417:    */         }
/* 418:    */       }
/* 419:    */     }
/* 420:435 */     this.line = this.bufline[m];
/* 421:436 */     this.column = this.bufcolumn[m];
/* 422:    */   }
/* 423:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.SimpleCharStream
 * JD-Core Version:    0.7.0.1
 */