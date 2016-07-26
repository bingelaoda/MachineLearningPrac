/*   1:    */ package org.boon.primitive;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.Reader;
/*   5:    */ import java.io.StringReader;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import org.boon.Exceptions;
/*   8:    */ 
/*   9:    */ public class ReaderCharacterSource
/*  10:    */   implements CharacterSource
/*  11:    */ {
/*  12:    */   private static final int MAX_TOKEN_SIZE = 5;
/*  13:    */   private final Reader reader;
/*  14:    */   private int readAheadSize;
/*  15: 46 */   private int ch = -2;
/*  16:    */   private boolean foundEscape;
/*  17:    */   private char[] readBuf;
/*  18:    */   private int index;
/*  19:    */   private int length;
/*  20: 59 */   boolean more = true;
/*  21: 60 */   private boolean done = false;
/*  22:    */   
/*  23:    */   public ReaderCharacterSource(Reader reader, int readAheadSize)
/*  24:    */   {
/*  25: 65 */     this.reader = reader;
/*  26: 66 */     this.readBuf = new char[readAheadSize + 5];
/*  27: 67 */     this.readAheadSize = readAheadSize;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public ReaderCharacterSource(Reader reader)
/*  31:    */   {
/*  32: 72 */     this.reader = reader;
/*  33: 73 */     this.readAheadSize = 10000;
/*  34: 74 */     this.readBuf = new char[this.readAheadSize + 5];
/*  35:    */   }
/*  36:    */   
/*  37:    */   public ReaderCharacterSource(String string)
/*  38:    */   {
/*  39: 78 */     this(new StringReader(string));
/*  40:    */   }
/*  41:    */   
/*  42:    */   private void readForToken()
/*  43:    */   {
/*  44:    */     try
/*  45:    */     {
/*  46: 84 */       this.length += this.reader.read(this.readBuf, this.readBuf.length - 5, 5);
/*  47:    */     }
/*  48:    */     catch (IOException e)
/*  49:    */     {
/*  50: 86 */       Exceptions.handle(e);
/*  51:    */     }
/*  52:    */   }
/*  53:    */   
/*  54:    */   private void ensureBuffer()
/*  55:    */   {
/*  56:    */     try
/*  57:    */     {
/*  58: 93 */       if ((this.index >= this.length) && (!this.done)) {
/*  59: 94 */         readNextBuffer();
/*  60: 95 */       } else if ((this.done) && (this.index >= this.length)) {
/*  61: 96 */         this.more = false;
/*  62:    */       } else {
/*  63: 98 */         this.more = true;
/*  64:    */       }
/*  65:    */     }
/*  66:    */     catch (Exception ex)
/*  67:    */     {
/*  68:101 */       String str = CharScanner.errorDetails("ensureBuffer issue", this.readBuf, this.index, this.ch);
/*  69:102 */       Exceptions.handle(str, ex);
/*  70:    */     }
/*  71:    */   }
/*  72:    */   
/*  73:    */   private void readNextBuffer()
/*  74:    */     throws IOException
/*  75:    */   {
/*  76:109 */     this.length = this.reader.read(this.readBuf, 0, this.readAheadSize);
/*  77:    */     
/*  78:    */ 
/*  79:112 */     this.index = 0;
/*  80:113 */     if (this.length == -1)
/*  81:    */     {
/*  82:114 */       this.ch = -1;
/*  83:115 */       this.length = 0;
/*  84:116 */       this.more = false;
/*  85:117 */       this.done = true;
/*  86:    */     }
/*  87:    */     else
/*  88:    */     {
/*  89:119 */       this.more = true;
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   public final int nextChar()
/*  94:    */   {
/*  95:125 */     ensureBuffer();
/*  96:126 */     return this.ch = this.readBuf[(this.index++)];
/*  97:    */   }
/*  98:    */   
/*  99:    */   public final int currentChar()
/* 100:    */   {
/* 101:131 */     ensureBuffer();
/* 102:132 */     return this.readBuf[this.index];
/* 103:    */   }
/* 104:    */   
/* 105:    */   public final boolean hasChar()
/* 106:    */   {
/* 107:137 */     ensureBuffer();
/* 108:138 */     return this.more;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public final boolean consumeIfMatch(char[] match)
/* 112:    */   {
/* 113:    */     try
/* 114:    */     {
/* 115:145 */       char[] _chars = this.readBuf;
/* 116:146 */       int i = 0;
/* 117:147 */       int idx = this.index;
/* 118:148 */       boolean ok = true;
/* 119:150 */       if (idx + match.length > this.length) {
/* 120:151 */         readForToken();
/* 121:    */       }
/* 122:154 */       for (; i < match.length; idx++)
/* 123:    */       {
/* 124:155 */         ok &= match[i] == _chars[idx];
/* 125:156 */         if (!ok) {
/* 126:    */           break;
/* 127:    */         }
/* 128:154 */         i++;
/* 129:    */       }
/* 130:159 */       if (ok)
/* 131:    */       {
/* 132:160 */         this.index = idx;
/* 133:161 */         return true;
/* 134:    */       }
/* 135:163 */       return false;
/* 136:    */     }
/* 137:    */     catch (Exception ex)
/* 138:    */     {
/* 139:166 */       String str = CharScanner.errorDetails("consumeIfMatch issue", this.readBuf, this.index, this.ch);
/* 140:167 */       return ((Boolean)Exceptions.handle(Boolean.TYPE, str, ex)).booleanValue();
/* 141:    */     }
/* 142:    */   }
/* 143:    */   
/* 144:    */   public final int location()
/* 145:    */   {
/* 146:174 */     return this.index;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public final int safeNextChar()
/* 150:    */   {
/* 151:    */     try
/* 152:    */     {
/* 153:179 */       ensureBuffer();
/* 154:180 */       return this.index + 1 < this.readBuf.length ? this.readBuf[(this.index++)] : -1;
/* 155:    */     }
/* 156:    */     catch (Exception ex)
/* 157:    */     {
/* 158:182 */       String str = CharScanner.errorDetails("safeNextChar issue", this.readBuf, this.index, this.ch);
/* 159:183 */       return ((Integer)Exceptions.handle(Integer.TYPE, str, ex)).intValue();
/* 160:    */     }
/* 161:    */   }
/* 162:    */   
/* 163:188 */   private final char[] EMPTY_CHARS = new char[0];
/* 164:    */   
/* 165:    */   public final char[] findNextChar(int match, int esc)
/* 166:    */   {
/* 167:193 */     return findNextChar(false, false, match, esc);
/* 168:    */   }
/* 169:    */   
/* 170:    */   public final char[] findNextChar(boolean inMiddleOfString, boolean wasEscapeChar, int match, int esc)
/* 171:    */   {
/* 172:    */     try
/* 173:    */     {
/* 174:210 */       ensureBuffer();
/* 175:    */       
/* 176:    */ 
/* 177:213 */       int idx = this.index;
/* 178:214 */       char[] _chars = this.readBuf;
/* 179:    */       
/* 180:216 */       int length = this.length;
/* 181:    */       
/* 182:    */ 
/* 183:219 */       int ch = this.ch;
/* 184:221 */       if (!inMiddleOfString)
/* 185:    */       {
/* 186:222 */         this.foundEscape = false;
/* 187:224 */         if (ch != match) {
/* 188:227 */           if (idx < length - 1)
/* 189:    */           {
/* 190:228 */             ch = _chars[idx];
/* 191:231 */             if (ch == match) {
/* 192:232 */               idx++;
/* 193:    */             }
/* 194:    */           }
/* 195:    */         }
/* 196:    */       }
/* 197:236 */       if (idx < length) {
/* 198:237 */         ch = _chars[idx];
/* 199:    */       }
/* 200:241 */       if ((ch == 34) && (!wasEscapeChar))
/* 201:    */       {
/* 202:243 */         this.index = idx;
/* 203:244 */         this.index += 1;
/* 204:245 */         return this.EMPTY_CHARS;
/* 205:    */       }
/* 206:247 */       int start = idx;
/* 207:249 */       if (wasEscapeChar) {
/* 208:250 */         idx++;
/* 209:    */       }
/* 210:253 */       boolean foundEnd = false;
/* 211:    */       
/* 212:    */ 
/* 213:256 */       boolean _foundEscape = false;
/* 214:    */       for (;;)
/* 215:    */       {
/* 216:260 */         ch = _chars[idx];
/* 217:264 */         if ((ch == match) || (ch == esc))
/* 218:    */         {
/* 219:265 */           if (ch == match)
/* 220:    */           {
/* 221:266 */             foundEnd = true;
/* 222:267 */             break;
/* 223:    */           }
/* 224:268 */           if (ch == esc)
/* 225:    */           {
/* 226:269 */             wasEscapeChar = true;
/* 227:270 */             _foundEscape = true;
/* 228:275 */             if (idx + 1 < length)
/* 229:    */             {
/* 230:277 */               wasEscapeChar = false;
/* 231:    */               
/* 232:279 */               idx++;
/* 233:    */             }
/* 234:    */           }
/* 235:    */         }
/* 236:285 */         if (idx >= length) {
/* 237:    */           break;
/* 238:    */         }
/* 239:286 */         idx++;
/* 240:    */       }
/* 241:290 */       this.foundEscape = _foundEscape;
/* 242:    */       char[] results;
/* 243:    */       char[] results;
/* 244:293 */       if (idx == 0) {
/* 245:294 */         results = this.EMPTY_CHARS;
/* 246:    */       } else {
/* 247:296 */         results = Arrays.copyOfRange(_chars, start, idx);
/* 248:    */       }
/* 249:301 */       this.index = idx;
/* 250:305 */       if (foundEnd)
/* 251:    */       {
/* 252:306 */         this.index += 1;
/* 253:307 */         if (this.index < length)
/* 254:    */         {
/* 255:308 */           ch = _chars[this.index];
/* 256:309 */           this.ch = ch;
/* 257:    */         }
/* 258:311 */         return results;
/* 259:    */       }
/* 260:315 */       if ((this.index >= length) && (!this.done))
/* 261:    */       {
/* 262:318 */         ensureBuffer();
/* 263:    */         
/* 264:320 */         char[] results2 = findNextChar(true, wasEscapeChar, match, esc);
/* 265:321 */         return Chr.add(results, results2);
/* 266:    */       }
/* 267:323 */       return (char[])Exceptions.die([C.class, "Unable to find close char " + (char)match + " " + new String(results));
/* 268:    */     }
/* 269:    */     catch (Exception ex)
/* 270:    */     {
/* 271:327 */       String str = CharScanner.errorDetails("findNextChar issue", this.readBuf, this.index, this.ch);
/* 272:328 */       return (char[])Exceptions.handle([C.class, str, ex);
/* 273:    */     }
/* 274:    */   }
/* 275:    */   
/* 276:    */   public boolean hadEscape()
/* 277:    */   {
/* 278:336 */     return this.foundEscape;
/* 279:    */   }
/* 280:    */   
/* 281:    */   public void skipWhiteSpace()
/* 282:    */   {
/* 283:    */     try
/* 284:    */     {
/* 285:343 */       this.index = CharScanner.skipWhiteSpace(this.readBuf, this.index, this.length);
/* 286:344 */       if ((this.index >= this.length) && (this.more))
/* 287:    */       {
/* 288:346 */         ensureBuffer();
/* 289:    */         
/* 290:348 */         skipWhiteSpace();
/* 291:    */       }
/* 292:    */     }
/* 293:    */     catch (Exception ex)
/* 294:    */     {
/* 295:353 */       ex.printStackTrace();
/* 296:354 */       String str = CharScanner.errorDetails("skipWhiteSpaceIfNeeded issue", this.readBuf, this.index, this.ch);
/* 297:355 */       Exceptions.handle(ex, new Object[] { str, "\n\nLENGTH", Integer.valueOf(this.length), "INDEX", Integer.valueOf(this.index) });
/* 298:    */     }
/* 299:    */   }
/* 300:    */   
/* 301:    */   public char[] readNumber()
/* 302:    */   {
/* 303:    */     try
/* 304:    */     {
/* 305:367 */       ensureBuffer();
/* 306:    */       
/* 307:369 */       char[] results = CharScanner.readNumber(this.readBuf, this.index, this.length);
/* 308:370 */       this.index += results.length;
/* 309:372 */       if ((this.index >= this.length) && (this.more))
/* 310:    */       {
/* 311:373 */         ensureBuffer();
/* 312:374 */         if (this.length != 0)
/* 313:    */         {
/* 314:375 */           char[] results2 = readNumber();
/* 315:376 */           return Chr.add(results, results2);
/* 316:    */         }
/* 317:378 */         return results;
/* 318:    */       }
/* 319:381 */       return results;
/* 320:    */     }
/* 321:    */     catch (Exception ex)
/* 322:    */     {
/* 323:384 */       String str = CharScanner.errorDetails("readNumber issue", this.readBuf, this.index, this.ch);
/* 324:385 */       return (char[])Exceptions.handle([C.class, str, ex);
/* 325:    */     }
/* 326:    */   }
/* 327:    */   
/* 328:    */   public String errorDetails(String message)
/* 329:    */   {
/* 330:393 */     return CharScanner.errorDetails(message, this.readBuf, this.index, this.ch);
/* 331:    */   }
/* 332:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.ReaderCharacterSource
 * JD-Core Version:    0.7.0.1
 */