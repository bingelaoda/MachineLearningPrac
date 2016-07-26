/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.InputStream;
/*   4:    */ import java.io.Reader;
/*   5:    */ import java.io.UnsupportedEncodingException;
/*   6:    */ import java.util.Vector;
/*   7:    */ 
/*   8:    */ class NumberParser
/*   9:    */   implements NumberParserConstants
/*  10:    */ {
/*  11:    */   public NumberParserTokenManager token_source;
/*  12:    */   SimpleCharStream jj_input_stream;
/*  13:    */   public Token token;
/*  14:    */   public Token jj_nt;
/*  15:    */   private int jj_ntk;
/*  16:    */   private int jj_gen;
/*  17:    */   
/*  18:    */   public final int Float()
/*  19:    */     throws ParseException
/*  20:    */   {
/*  21:  7 */     int i = 0;
/*  22:    */     for (;;)
/*  23:    */     {
/*  24: 10 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*  25:    */       {
/*  26:    */       case 6: 
/*  27:    */         break;
/*  28:    */       default: 
/*  29: 15 */         this.jj_la1[0] = this.jj_gen;
/*  30: 16 */         break;
/*  31:    */       }
/*  32: 18 */       jj_consume_token(6);
/*  33: 19 */       i++;
/*  34:    */     }
/*  35: 21 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*  36:    */     {
/*  37:    */     case 7: 
/*  38:    */     case 8: 
/*  39: 24 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*  40:    */       {
/*  41:    */       case 7: 
/*  42: 26 */         jj_consume_token(7);
/*  43: 27 */         break;
/*  44:    */       case 8: 
/*  45: 29 */         jj_consume_token(8);
/*  46: 30 */         break;
/*  47:    */       default: 
/*  48: 32 */         this.jj_la1[1] = this.jj_gen;
/*  49: 33 */         jj_consume_token(-1);
/*  50: 34 */         throw new ParseException();
/*  51:    */       }
/*  52:    */       break;
/*  53:    */     default: 
/*  54: 38 */       this.jj_la1[2] = this.jj_gen;
/*  55:    */     }
/*  56: 41 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*  57:    */     {
/*  58:    */     case 1: 
/*  59: 43 */       jj_consume_token(1);
/*  60: 44 */       break;
/*  61:    */     case 4: 
/*  62: 46 */       jj_consume_token(4);
/*  63: 47 */       break;
/*  64:    */     default: 
/*  65: 49 */       this.jj_la1[3] = this.jj_gen;
/*  66: 50 */       jj_consume_token(-1);
/*  67: 51 */       throw new ParseException();
/*  68:    */     }
/*  69: 53 */     jj_consume_token(0);
/*  70: 54 */     return i;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public final int Integer()
/*  74:    */     throws ParseException
/*  75:    */   {
/*  76: 64 */     int i = 0;
/*  77:    */     for (;;)
/*  78:    */     {
/*  79: 67 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*  80:    */       {
/*  81:    */       case 6: 
/*  82:    */         break;
/*  83:    */       default: 
/*  84: 72 */         this.jj_la1[4] = this.jj_gen;
/*  85: 73 */         break;
/*  86:    */       }
/*  87: 75 */       jj_consume_token(6);
/*  88: 76 */       i++;
/*  89:    */     }
/*  90: 78 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*  91:    */     {
/*  92:    */     case 7: 
/*  93:    */     case 8: 
/*  94: 81 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*  95:    */       {
/*  96:    */       case 7: 
/*  97: 83 */         jj_consume_token(7);
/*  98: 84 */         break;
/*  99:    */       case 8: 
/* 100: 86 */         jj_consume_token(8);
/* 101: 87 */         break;
/* 102:    */       default: 
/* 103: 89 */         this.jj_la1[5] = this.jj_gen;
/* 104: 90 */         jj_consume_token(-1);
/* 105: 91 */         throw new ParseException();
/* 106:    */       }
/* 107:    */       break;
/* 108:    */     default: 
/* 109: 95 */       this.jj_la1[6] = this.jj_gen;
/* 110:    */     }
/* 111: 98 */     jj_consume_token(1);
/* 112: 99 */     jj_consume_token(0);
/* 113:100 */     return i;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public final int Boolean()
/* 117:    */     throws ParseException
/* 118:    */   {
/* 119:109 */     int i = 0;
/* 120:    */     for (;;)
/* 121:    */     {
/* 122:112 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/* 123:    */       {
/* 124:    */       case 6: 
/* 125:    */         break;
/* 126:    */       default: 
/* 127:117 */         this.jj_la1[7] = this.jj_gen;
/* 128:118 */         break;
/* 129:    */       }
/* 130:120 */       jj_consume_token(6);
/* 131:121 */       i++;
/* 132:    */     }
/* 133:123 */     jj_consume_token(3);
/* 134:124 */     jj_consume_token(0);
/* 135:125 */     return i;
/* 136:    */   }
/* 137:    */   
/* 138:134 */   private final int[] jj_la1 = new int[8];
/* 139:    */   private static int[] jj_la1_0;
/* 140:    */   
/* 141:    */   private static void jj_la1_0()
/* 142:    */   {
/* 143:140 */     jj_la1_0 = new int[] { 64, 384, 384, 18, 64, 384, 384, 64 };
/* 144:    */   }
/* 145:    */   
/* 146:    */   public NumberParser(InputStream paramInputStream)
/* 147:    */   {
/* 148:144 */     this(paramInputStream, null);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public NumberParser(InputStream paramInputStream, String paramString)
/* 152:    */   {
/* 153:    */     try
/* 154:    */     {
/* 155:147 */       this.jj_input_stream = new SimpleCharStream(paramInputStream, paramString, 1, 1);
/* 156:    */     }
/* 157:    */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/* 158:    */     {
/* 159:147 */       throw new RuntimeException(localUnsupportedEncodingException);
/* 160:    */     }
/* 161:148 */     this.token_source = new NumberParserTokenManager(this.jj_input_stream);
/* 162:149 */     this.token = new Token();
/* 163:150 */     this.jj_ntk = -1;
/* 164:151 */     this.jj_gen = 0;
/* 165:152 */     for (int i = 0; i < 8; i++) {
/* 166:152 */       this.jj_la1[i] = -1;
/* 167:    */     }
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void ReInit(InputStream paramInputStream)
/* 171:    */   {
/* 172:156 */     ReInit(paramInputStream, null);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void ReInit(InputStream paramInputStream, String paramString)
/* 176:    */   {
/* 177:    */     try
/* 178:    */     {
/* 179:159 */       this.jj_input_stream.ReInit(paramInputStream, paramString, 1, 1);
/* 180:    */     }
/* 181:    */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/* 182:    */     {
/* 183:159 */       throw new RuntimeException(localUnsupportedEncodingException);
/* 184:    */     }
/* 185:160 */     this.token_source.ReInit(this.jj_input_stream);
/* 186:161 */     this.token = new Token();
/* 187:162 */     this.jj_ntk = -1;
/* 188:163 */     this.jj_gen = 0;
/* 189:164 */     for (int i = 0; i < 8; i++) {
/* 190:164 */       this.jj_la1[i] = -1;
/* 191:    */     }
/* 192:    */   }
/* 193:    */   
/* 194:    */   public NumberParser(Reader paramReader)
/* 195:    */   {
/* 196:168 */     this.jj_input_stream = new SimpleCharStream(paramReader, 1, 1);
/* 197:169 */     this.token_source = new NumberParserTokenManager(this.jj_input_stream);
/* 198:170 */     this.token = new Token();
/* 199:171 */     this.jj_ntk = -1;
/* 200:172 */     this.jj_gen = 0;
/* 201:173 */     for (int i = 0; i < 8; i++) {
/* 202:173 */       this.jj_la1[i] = -1;
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void ReInit(Reader paramReader)
/* 207:    */   {
/* 208:177 */     this.jj_input_stream.ReInit(paramReader, 1, 1);
/* 209:178 */     this.token_source.ReInit(this.jj_input_stream);
/* 210:179 */     this.token = new Token();
/* 211:180 */     this.jj_ntk = -1;
/* 212:181 */     this.jj_gen = 0;
/* 213:182 */     for (int i = 0; i < 8; i++) {
/* 214:182 */       this.jj_la1[i] = -1;
/* 215:    */     }
/* 216:    */   }
/* 217:    */   
/* 218:    */   public NumberParser(NumberParserTokenManager paramNumberParserTokenManager)
/* 219:    */   {
/* 220:186 */     this.token_source = paramNumberParserTokenManager;
/* 221:187 */     this.token = new Token();
/* 222:188 */     this.jj_ntk = -1;
/* 223:189 */     this.jj_gen = 0;
/* 224:190 */     for (int i = 0; i < 8; i++) {
/* 225:190 */       this.jj_la1[i] = -1;
/* 226:    */     }
/* 227:    */   }
/* 228:    */   
/* 229:    */   public void ReInit(NumberParserTokenManager paramNumberParserTokenManager)
/* 230:    */   {
/* 231:194 */     this.token_source = paramNumberParserTokenManager;
/* 232:195 */     this.token = new Token();
/* 233:196 */     this.jj_ntk = -1;
/* 234:197 */     this.jj_gen = 0;
/* 235:198 */     for (int i = 0; i < 8; i++) {
/* 236:198 */       this.jj_la1[i] = -1;
/* 237:    */     }
/* 238:    */   }
/* 239:    */   
/* 240:    */   private final Token jj_consume_token(int paramInt)
/* 241:    */     throws ParseException
/* 242:    */   {
/* 243:    */     Token localToken;
/* 244:203 */     if ((localToken = this.token).next != null) {
/* 245:203 */       this.token = this.token.next;
/* 246:    */     } else {
/* 247:204 */       this.token = (this.token.next = this.token_source.getNextToken());
/* 248:    */     }
/* 249:205 */     this.jj_ntk = -1;
/* 250:206 */     if (this.token.kind == paramInt)
/* 251:    */     {
/* 252:207 */       this.jj_gen += 1;
/* 253:208 */       return this.token;
/* 254:    */     }
/* 255:210 */     this.token = localToken;
/* 256:211 */     this.jj_kind = paramInt;
/* 257:212 */     throw generateParseException();
/* 258:    */   }
/* 259:    */   
/* 260:    */   public final Token getNextToken()
/* 261:    */   {
/* 262:216 */     if (this.token.next != null) {
/* 263:216 */       this.token = this.token.next;
/* 264:    */     } else {
/* 265:217 */       this.token = (this.token.next = this.token_source.getNextToken());
/* 266:    */     }
/* 267:218 */     this.jj_ntk = -1;
/* 268:219 */     this.jj_gen += 1;
/* 269:220 */     return this.token;
/* 270:    */   }
/* 271:    */   
/* 272:    */   public final Token getToken(int paramInt)
/* 273:    */   {
/* 274:224 */     Token localToken = this.token;
/* 275:225 */     for (int i = 0; i < paramInt; i++) {
/* 276:226 */       if (localToken.next != null) {
/* 277:226 */         localToken = localToken.next;
/* 278:    */       } else {
/* 279:227 */         localToken = localToken.next = this.token_source.getNextToken();
/* 280:    */       }
/* 281:    */     }
/* 282:229 */     return localToken;
/* 283:    */   }
/* 284:    */   
/* 285:    */   private final int jj_ntk()
/* 286:    */   {
/* 287:233 */     if ((this.jj_nt = this.token.next) == null) {
/* 288:234 */       return this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind;
/* 289:    */     }
/* 290:236 */     return this.jj_ntk = this.jj_nt.kind;
/* 291:    */   }
/* 292:    */   
/* 293:239 */   private Vector jj_expentries = new Vector();
/* 294:    */   private int[] jj_expentry;
/* 295:241 */   private int jj_kind = -1;
/* 296:    */   
/* 297:    */   public ParseException generateParseException()
/* 298:    */   {
/* 299:244 */     this.jj_expentries.removeAllElements();
/* 300:245 */     boolean[] arrayOfBoolean = new boolean[9];
/* 301:246 */     for (int i = 0; i < 9; i++) {
/* 302:247 */       arrayOfBoolean[i] = false;
/* 303:    */     }
/* 304:249 */     if (this.jj_kind >= 0)
/* 305:    */     {
/* 306:250 */       arrayOfBoolean[this.jj_kind] = true;
/* 307:251 */       this.jj_kind = -1;
/* 308:    */     }
/* 309:253 */     for (i = 0; i < 8; i++) {
/* 310:254 */       if (this.jj_la1[i] == this.jj_gen) {
/* 311:255 */         for (j = 0; j < 32; j++) {
/* 312:256 */           if ((jj_la1_0[i] & 1 << j) != 0) {
/* 313:257 */             arrayOfBoolean[j] = true;
/* 314:    */           }
/* 315:    */         }
/* 316:    */       }
/* 317:    */     }
/* 318:262 */     for (i = 0; i < 9; i++) {
/* 319:263 */       if (arrayOfBoolean[i] != 0)
/* 320:    */       {
/* 321:264 */         this.jj_expentry = new int[1];
/* 322:265 */         this.jj_expentry[0] = i;
/* 323:266 */         this.jj_expentries.addElement(this.jj_expentry);
/* 324:    */       }
/* 325:    */     }
/* 326:269 */     int[][] arrayOfInt = new int[this.jj_expentries.size()][];
/* 327:270 */     for (int j = 0; j < this.jj_expentries.size(); j++) {
/* 328:271 */       arrayOfInt[j] = ((int[])(int[])this.jj_expentries.elementAt(j));
/* 329:    */     }
/* 330:273 */     return new ParseException(this.token, arrayOfInt, tokenImage);
/* 331:    */   }
/* 332:    */   
/* 333:    */   public final void enable_tracing() {}
/* 334:    */   
/* 335:    */   public final void disable_tracing() {}
/* 336:    */   
/* 337:    */   static {}
/* 338:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.NumberParser
 * JD-Core Version:    0.7.0.1
 */