/*   1:    */ package jsyntaxpane.lexers;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.Reader;
/*   7:    */ import jsyntaxpane.Token;
/*   8:    */ import jsyntaxpane.TokenType;
/*   9:    */ 
/*  10:    */ public final class PropertiesLexer
/*  11:    */   extends DefaultJFlexLexer
/*  12:    */ {
/*  13:    */   public static final int YYEOF = -1;
/*  14:    */   private static final int ZZ_BUFFERSIZE = 16384;
/*  15:    */   public static final int YYINITIAL = 0;
/*  16: 47 */   private static final int[] ZZ_LEXSTATE = { 0, 0 };
/*  17:    */   private static final String ZZ_CMAP_PACKED = "";
/*  18: 62 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*  19: 67 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*  20:    */   private static final String ZZ_ACTION_PACKED_0 = "";
/*  21:    */   
/*  22:    */   private static int[] zzUnpackAction()
/*  23:    */   {
/*  24: 73 */     int[] result = new int[10];
/*  25: 74 */     int offset = 0;
/*  26: 75 */     offset = zzUnpackAction("", offset, result);
/*  27: 76 */     return result;
/*  28:    */   }
/*  29:    */   
/*  30:    */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*  31:    */   {
/*  32: 80 */     int i = 0;
/*  33: 81 */     int j = offset;
/*  34: 82 */     int l = packed.length();
/*  35:    */     int count;
/*  36: 83 */     for (; i < l; count > 0)
/*  37:    */     {
/*  38: 84 */       count = packed.charAt(i++);
/*  39: 85 */       int value = packed.charAt(i++);
/*  40: 86 */       result[(j++)] = value;count--;
/*  41:    */     }
/*  42: 88 */     return j;
/*  43:    */   }
/*  44:    */   
/*  45: 95 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*  46:    */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*  47:    */   
/*  48:    */   private static int[] zzUnpackRowMap()
/*  49:    */   {
/*  50:102 */     int[] result = new int[10];
/*  51:103 */     int offset = 0;
/*  52:104 */     offset = zzUnpackRowMap("", offset, result);
/*  53:105 */     return result;
/*  54:    */   }
/*  55:    */   
/*  56:    */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*  57:    */   {
/*  58:109 */     int i = 0;
/*  59:110 */     int j = offset;
/*  60:111 */     int l = packed.length();
/*  61:112 */     while (i < l)
/*  62:    */     {
/*  63:113 */       int high = packed.charAt(i++) << '\020';
/*  64:114 */       result[(j++)] = (high | packed.charAt(i++));
/*  65:    */     }
/*  66:116 */     return j;
/*  67:    */   }
/*  68:    */   
/*  69:122 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*  70:    */   private static final String ZZ_TRANS_PACKED_0 = "";
/*  71:    */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*  72:    */   private static final int ZZ_NO_MATCH = 1;
/*  73:    */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*  74:    */   
/*  75:    */   private static int[] zzUnpackTrans()
/*  76:    */   {
/*  77:131 */     int[] result = new int[56];
/*  78:132 */     int offset = 0;
/*  79:133 */     offset = zzUnpackTrans("", offset, result);
/*  80:134 */     return result;
/*  81:    */   }
/*  82:    */   
/*  83:    */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*  84:    */   {
/*  85:138 */     int i = 0;
/*  86:139 */     int j = offset;
/*  87:140 */     int l = packed.length();
/*  88:    */     int count;
/*  89:141 */     for (; i < l; count > 0)
/*  90:    */     {
/*  91:142 */       count = packed.charAt(i++);
/*  92:143 */       int value = packed.charAt(i++);
/*  93:144 */       value--;
/*  94:145 */       result[(j++)] = value;count--;
/*  95:    */     }
/*  96:147 */     return j;
/*  97:    */   }
/*  98:    */   
/*  99:157 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/* 100:166 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/* 101:    */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/* 102:    */   private Reader zzReader;
/* 103:    */   private int zzState;
/* 104:    */   
/* 105:    */   private static int[] zzUnpackAttribute()
/* 106:    */   {
/* 107:172 */     int[] result = new int[10];
/* 108:173 */     int offset = 0;
/* 109:174 */     offset = zzUnpackAttribute("", offset, result);
/* 110:175 */     return result;
/* 111:    */   }
/* 112:    */   
/* 113:    */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/* 114:    */   {
/* 115:179 */     int i = 0;
/* 116:180 */     int j = offset;
/* 117:181 */     int l = packed.length();
/* 118:    */     int count;
/* 119:182 */     for (; i < l; count > 0)
/* 120:    */     {
/* 121:183 */       count = packed.charAt(i++);
/* 122:184 */       int value = packed.charAt(i++);
/* 123:185 */       result[(j++)] = value;count--;
/* 124:    */     }
/* 125:187 */     return j;
/* 126:    */   }
/* 127:    */   
/* 128:197 */   private int zzLexicalState = 0;
/* 129:201 */   private char[] zzBuffer = new char[16384];
/* 130:    */   private int zzMarkedPos;
/* 131:    */   private int zzCurrentPos;
/* 132:    */   private int zzStartRead;
/* 133:    */   private int zzEndRead;
/* 134:    */   private int yyline;
/* 135:    */   private int yychar;
/* 136:    */   private int yycolumn;
/* 137:231 */   private boolean zzAtBOL = true;
/* 138:    */   private boolean zzAtEOF;
/* 139:    */   private boolean zzEOFDone;
/* 140:    */   
/* 141:    */   public PropertiesLexer() {}
/* 142:    */   
/* 143:    */   public int yychar()
/* 144:    */   {
/* 145:250 */     return this.yychar;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public PropertiesLexer(Reader in)
/* 149:    */   {
/* 150:261 */     this.zzReader = in;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public PropertiesLexer(InputStream in)
/* 154:    */   {
/* 155:271 */     this(new InputStreamReader(in));
/* 156:    */   }
/* 157:    */   
/* 158:    */   private static char[] zzUnpackCMap(String packed)
/* 159:    */   {
/* 160:281 */     char[] map = new char[65536];
/* 161:282 */     int i = 0;
/* 162:283 */     int j = 0;
/* 163:    */     int count;
/* 164:284 */     for (; i < 44; count > 0)
/* 165:    */     {
/* 166:285 */       count = packed.charAt(i++);
/* 167:286 */       char value = packed.charAt(i++);
/* 168:287 */       map[(j++)] = value;count--;
/* 169:    */     }
/* 170:289 */     return map;
/* 171:    */   }
/* 172:    */   
/* 173:    */   private boolean zzRefill()
/* 174:    */     throws IOException
/* 175:    */   {
/* 176:303 */     if (this.zzStartRead > 0)
/* 177:    */     {
/* 178:304 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/* 179:    */       
/* 180:    */ 
/* 181:    */ 
/* 182:    */ 
/* 183:309 */       this.zzEndRead -= this.zzStartRead;
/* 184:310 */       this.zzCurrentPos -= this.zzStartRead;
/* 185:311 */       this.zzMarkedPos -= this.zzStartRead;
/* 186:312 */       this.zzStartRead = 0;
/* 187:    */     }
/* 188:316 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/* 189:    */     {
/* 190:318 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 191:319 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 192:320 */       this.zzBuffer = newBuffer;
/* 193:    */     }
/* 194:324 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/* 195:327 */     if (numRead > 0)
/* 196:    */     {
/* 197:328 */       this.zzEndRead += numRead;
/* 198:329 */       return false;
/* 199:    */     }
/* 200:332 */     if (numRead == 0)
/* 201:    */     {
/* 202:333 */       int c = this.zzReader.read();
/* 203:334 */       if (c == -1) {
/* 204:335 */         return true;
/* 205:    */       }
/* 206:337 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/* 207:338 */       return false;
/* 208:    */     }
/* 209:343 */     return true;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public final void yyclose()
/* 213:    */     throws IOException
/* 214:    */   {
/* 215:351 */     this.zzAtEOF = true;
/* 216:352 */     this.zzEndRead = this.zzStartRead;
/* 217:354 */     if (this.zzReader != null) {
/* 218:355 */       this.zzReader.close();
/* 219:    */     }
/* 220:    */   }
/* 221:    */   
/* 222:    */   public final void yyreset(Reader reader)
/* 223:    */   {
/* 224:370 */     this.zzReader = reader;
/* 225:371 */     this.zzAtBOL = true;
/* 226:372 */     this.zzAtEOF = false;
/* 227:373 */     this.zzEOFDone = false;
/* 228:374 */     this.zzEndRead = (this.zzStartRead = 0);
/* 229:375 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 230:376 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 231:377 */     this.zzLexicalState = 0;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public final int yystate()
/* 235:    */   {
/* 236:385 */     return this.zzLexicalState;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public final void yybegin(int newState)
/* 240:    */   {
/* 241:395 */     this.zzLexicalState = newState;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public final String yytext()
/* 245:    */   {
/* 246:403 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/* 247:    */   }
/* 248:    */   
/* 249:    */   public final char yycharat(int pos)
/* 250:    */   {
/* 251:419 */     return this.zzBuffer[(this.zzStartRead + pos)];
/* 252:    */   }
/* 253:    */   
/* 254:    */   public final int yylength()
/* 255:    */   {
/* 256:427 */     return this.zzMarkedPos - this.zzStartRead;
/* 257:    */   }
/* 258:    */   
/* 259:    */   private void zzScanError(int errorCode)
/* 260:    */   {
/* 261:    */     String message;
/* 262:    */     try
/* 263:    */     {
/* 264:448 */       message = ZZ_ERROR_MSG[errorCode];
/* 265:    */     }
/* 266:    */     catch (ArrayIndexOutOfBoundsException e)
/* 267:    */     {
/* 268:451 */       message = ZZ_ERROR_MSG[0];
/* 269:    */     }
/* 270:454 */     throw new Error(message);
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void yypushback(int number)
/* 274:    */   {
/* 275:467 */     if (number > yylength()) {
/* 276:468 */       zzScanError(2);
/* 277:    */     }
/* 278:470 */     this.zzMarkedPos -= number;
/* 279:    */   }
/* 280:    */   
/* 281:    */   public Token yylex()
/* 282:    */     throws IOException
/* 283:    */   {
/* 284:488 */     int zzEndReadL = this.zzEndRead;
/* 285:489 */     char[] zzBufferL = this.zzBuffer;
/* 286:490 */     char[] zzCMapL = ZZ_CMAP;
/* 287:    */     
/* 288:492 */     int[] zzTransL = ZZ_TRANS;
/* 289:493 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 290:494 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/* 291:    */     for (;;)
/* 292:    */     {
/* 293:497 */       int zzMarkedPosL = this.zzMarkedPos;
/* 294:    */       
/* 295:499 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/* 296:    */       
/* 297:501 */       int zzAction = -1;
/* 298:    */       
/* 299:503 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/* 300:    */       
/* 301:505 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/* 302:    */       int zzInput;
/* 303:    */       for (;;)
/* 304:    */       {
/* 305:    */         int zzInput;
/* 306:511 */         if (zzCurrentPosL < zzEndReadL)
/* 307:    */         {
/* 308:512 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 309:    */         }
/* 310:    */         else
/* 311:    */         {
/* 312:513 */           if (this.zzAtEOF)
/* 313:    */           {
/* 314:514 */             int zzInput = -1;
/* 315:515 */             break;
/* 316:    */           }
/* 317:519 */           this.zzCurrentPos = zzCurrentPosL;
/* 318:520 */           this.zzMarkedPos = zzMarkedPosL;
/* 319:521 */           boolean eof = zzRefill();
/* 320:    */           
/* 321:523 */           zzCurrentPosL = this.zzCurrentPos;
/* 322:524 */           zzMarkedPosL = this.zzMarkedPos;
/* 323:525 */           zzBufferL = this.zzBuffer;
/* 324:526 */           zzEndReadL = this.zzEndRead;
/* 325:527 */           if (eof)
/* 326:    */           {
/* 327:528 */             int zzInput = -1;
/* 328:529 */             break;
/* 329:    */           }
/* 330:532 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 331:    */         }
/* 332:535 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 333:536 */         if (zzNext == -1) {
/* 334:    */           break;
/* 335:    */         }
/* 336:537 */         this.zzState = zzNext;
/* 337:    */         
/* 338:539 */         int zzAttributes = zzAttrL[this.zzState];
/* 339:540 */         if ((zzAttributes & 0x1) == 1)
/* 340:    */         {
/* 341:541 */           zzAction = this.zzState;
/* 342:542 */           zzMarkedPosL = zzCurrentPosL;
/* 343:543 */           if ((zzAttributes & 0x8) == 8) {
/* 344:    */             break;
/* 345:    */           }
/* 346:    */         }
/* 347:    */       }
/* 348:550 */       this.zzMarkedPos = zzMarkedPosL;
/* 349:552 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/* 350:    */       {
/* 351:    */       case 3: 
/* 352:554 */         return token(TokenType.KEYWORD);
/* 353:    */       case 4: 
/* 354:    */         break;
/* 355:    */       case 1: 
/* 356:    */       case 5: 
/* 357:    */         break;
/* 358:    */       case 2: 
/* 359:562 */         return token(TokenType.COMMENT);
/* 360:    */       case 6: 
/* 361:    */         break;
/* 362:    */       default: 
/* 363:566 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/* 364:    */         {
/* 365:567 */           this.zzAtEOF = true;
/* 366:    */           
/* 367:569 */           return null;
/* 368:    */         }
/* 369:573 */         zzScanError(1);
/* 370:    */       }
/* 371:    */     }
/* 372:    */   }
/* 373:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.PropertiesLexer
 * JD-Core Version:    0.7.0.1
 */