/*   1:    */ package org.apache.commons.codec.language;
/*   2:    */ 
/*   3:    */ import org.apache.commons.codec.EncoderException;
/*   4:    */ import org.apache.commons.codec.StringEncoder;
/*   5:    */ 
/*   6:    */ public class Metaphone
/*   7:    */   implements StringEncoder
/*   8:    */ {
/*   9: 41 */   private String vowels = "AEIOU";
/*  10: 46 */   private String frontv = "EIY";
/*  11: 51 */   private String varson = "CSPTG";
/*  12: 56 */   private int maxCodeLen = 4;
/*  13:    */   
/*  14:    */   public String metaphone(String txt)
/*  15:    */   {
/*  16: 76 */     boolean hard = false;
/*  17: 77 */     if ((txt == null) || (txt.length() == 0)) {
/*  18: 78 */       return "";
/*  19:    */     }
/*  20: 81 */     if (txt.length() == 1) {
/*  21: 82 */       return txt.toUpperCase();
/*  22:    */     }
/*  23: 85 */     char[] inwd = txt.toUpperCase().toCharArray();
/*  24:    */     
/*  25: 87 */     StringBuffer local = new StringBuffer(40);
/*  26: 88 */     StringBuffer code = new StringBuffer(10);
/*  27: 90 */     switch (inwd[0])
/*  28:    */     {
/*  29:    */     case 'G': 
/*  30:    */     case 'K': 
/*  31:    */     case 'P': 
/*  32: 94 */       if (inwd[1] == 'N') {
/*  33: 95 */         local.append(inwd, 1, inwd.length - 1);
/*  34:    */       } else {
/*  35: 97 */         local.append(inwd);
/*  36:    */       }
/*  37: 99 */       break;
/*  38:    */     case 'A': 
/*  39:101 */       if (inwd[1] == 'E') {
/*  40:102 */         local.append(inwd, 1, inwd.length - 1);
/*  41:    */       } else {
/*  42:104 */         local.append(inwd);
/*  43:    */       }
/*  44:106 */       break;
/*  45:    */     case 'W': 
/*  46:108 */       if (inwd[1] == 'R')
/*  47:    */       {
/*  48:109 */         local.append(inwd, 1, inwd.length - 1);
/*  49:    */       }
/*  50:112 */       else if (inwd[1] == 'H')
/*  51:    */       {
/*  52:113 */         local.append(inwd, 1, inwd.length - 1);
/*  53:114 */         local.setCharAt(0, 'W');
/*  54:    */       }
/*  55:    */       else
/*  56:    */       {
/*  57:116 */         local.append(inwd);
/*  58:    */       }
/*  59:118 */       break;
/*  60:    */     case 'X': 
/*  61:120 */       inwd[0] = 'S';
/*  62:121 */       local.append(inwd);
/*  63:122 */       break;
/*  64:    */     default: 
/*  65:124 */       local.append(inwd);
/*  66:    */     }
/*  67:127 */     int wdsz = local.length();
/*  68:128 */     int n = 0;
/*  69:130 */     while ((code.length() < getMaxCodeLen()) && (n < wdsz))
/*  70:    */     {
/*  71:132 */       char symb = local.charAt(n);
/*  72:134 */       if ((symb != 'C') && (isPreviousChar(local, n, symb)))
/*  73:    */       {
/*  74:135 */         n++;
/*  75:    */       }
/*  76:    */       else
/*  77:    */       {
/*  78:137 */         switch (symb)
/*  79:    */         {
/*  80:    */         case 'A': 
/*  81:    */         case 'E': 
/*  82:    */         case 'I': 
/*  83:    */         case 'O': 
/*  84:    */         case 'U': 
/*  85:139 */           if (n == 0) {
/*  86:140 */             code.append(symb);
/*  87:    */           }
/*  88:    */           break;
/*  89:    */         case 'B': 
/*  90:144 */           if ((!isPreviousChar(local, n, 'M')) || (!isLastChar(wdsz, n))) {
/*  91:148 */             code.append(symb);
/*  92:    */           }
/*  93:149 */           break;
/*  94:    */         case 'C': 
/*  95:152 */           if ((!isPreviousChar(local, n, 'S')) || (isLastChar(wdsz, n)) || (this.frontv.indexOf(local.charAt(n + 1)) < 0)) {
/*  96:157 */             if (regionMatch(local, n, "CIA")) {
/*  97:158 */               code.append('X');
/*  98:161 */             } else if ((!isLastChar(wdsz, n)) && (this.frontv.indexOf(local.charAt(n + 1)) >= 0)) {
/*  99:163 */               code.append('S');
/* 100:166 */             } else if ((isPreviousChar(local, n, 'S')) && (isNextChar(local, n, 'H'))) {
/* 101:168 */               code.append('K');
/* 102:171 */             } else if (isNextChar(local, n, 'H'))
/* 103:    */             {
/* 104:172 */               if ((n == 0) && (wdsz >= 3) && (isVowel(local, 2))) {
/* 105:175 */                 code.append('K');
/* 106:    */               } else {
/* 107:177 */                 code.append('X');
/* 108:    */               }
/* 109:    */             }
/* 110:    */             else {
/* 111:180 */               code.append('K');
/* 112:    */             }
/* 113:    */           }
/* 114:182 */           break;
/* 115:    */         case 'D': 
/* 116:184 */           if ((!isLastChar(wdsz, n + 1)) && (isNextChar(local, n, 'G')) && (this.frontv.indexOf(local.charAt(n + 2)) >= 0))
/* 117:    */           {
/* 118:187 */             code.append('J');n += 2;
/* 119:    */           }
/* 120:    */           else
/* 121:    */           {
/* 122:189 */             code.append('T');
/* 123:    */           }
/* 124:191 */           break;
/* 125:    */         case 'G': 
/* 126:193 */           if ((!isLastChar(wdsz, n + 1)) || (!isNextChar(local, n, 'H'))) {
/* 127:197 */             if ((isLastChar(wdsz, n + 1)) || (!isNextChar(local, n, 'H')) || (isVowel(local, n + 2))) {
/* 128:202 */               if ((n <= 0) || ((!regionMatch(local, n, "GN")) && (!regionMatch(local, n, "GNED"))))
/* 129:    */               {
/* 130:207 */                 if (isPreviousChar(local, n, 'G')) {
/* 131:208 */                   hard = true;
/* 132:    */                 } else {
/* 133:210 */                   hard = false;
/* 134:    */                 }
/* 135:212 */                 if ((!isLastChar(wdsz, n)) && (this.frontv.indexOf(local.charAt(n + 1)) >= 0) && (!hard)) {
/* 136:215 */                   code.append('J');
/* 137:    */                 } else {
/* 138:217 */                   code.append('K');
/* 139:    */                 }
/* 140:    */               }
/* 141:    */             }
/* 142:    */           }
/* 143:219 */           break;
/* 144:    */         case 'H': 
/* 145:221 */           if (!isLastChar(wdsz, n)) {
/* 146:224 */             if ((n <= 0) || (this.varson.indexOf(local.charAt(n - 1)) < 0)) {
/* 147:228 */               if (isVowel(local, n + 1)) {
/* 148:229 */                 code.append('H');
/* 149:    */               }
/* 150:    */             }
/* 151:    */           }
/* 152:    */           break;
/* 153:    */         case 'F': 
/* 154:    */         case 'J': 
/* 155:    */         case 'L': 
/* 156:    */         case 'M': 
/* 157:    */         case 'N': 
/* 158:    */         case 'R': 
/* 159:238 */           code.append(symb);
/* 160:239 */           break;
/* 161:    */         case 'K': 
/* 162:241 */           if (n > 0)
/* 163:    */           {
/* 164:242 */             if (!isPreviousChar(local, n, 'C')) {
/* 165:243 */               code.append(symb);
/* 166:    */             }
/* 167:    */           }
/* 168:    */           else {
/* 169:246 */             code.append(symb);
/* 170:    */           }
/* 171:248 */           break;
/* 172:    */         case 'P': 
/* 173:250 */           if (isNextChar(local, n, 'H')) {
/* 174:252 */             code.append('F');
/* 175:    */           } else {
/* 176:254 */             code.append(symb);
/* 177:    */           }
/* 178:256 */           break;
/* 179:    */         case 'Q': 
/* 180:258 */           code.append('K');
/* 181:259 */           break;
/* 182:    */         case 'S': 
/* 183:261 */           if ((regionMatch(local, n, "SH")) || (regionMatch(local, n, "SIO")) || (regionMatch(local, n, "SIA"))) {
/* 184:264 */             code.append('X');
/* 185:    */           } else {
/* 186:266 */             code.append('S');
/* 187:    */           }
/* 188:268 */           break;
/* 189:    */         case 'T': 
/* 190:270 */           if ((regionMatch(local, n, "TIA")) || (regionMatch(local, n, "TIO"))) {
/* 191:272 */             code.append('X');
/* 192:275 */           } else if (!regionMatch(local, n, "TCH")) {
/* 193:280 */             if (regionMatch(local, n, "TH")) {
/* 194:281 */               code.append('0');
/* 195:    */             } else {
/* 196:283 */               code.append('T');
/* 197:    */             }
/* 198:    */           }
/* 199:285 */           break;
/* 200:    */         case 'V': 
/* 201:287 */           code.append('F'); break;
/* 202:    */         case 'W': 
/* 203:    */         case 'Y': 
/* 204:289 */           if ((!isLastChar(wdsz, n)) && (isVowel(local, n + 1))) {
/* 205:291 */             code.append(symb);
/* 206:    */           }
/* 207:    */           break;
/* 208:    */         case 'X': 
/* 209:295 */           code.append('K');code.append('S');
/* 210:296 */           break;
/* 211:    */         case 'Z': 
/* 212:298 */           code.append('S');
/* 213:    */         }
/* 214:300 */         n++;
/* 215:    */       }
/* 216:302 */       if (code.length() > getMaxCodeLen()) {
/* 217:303 */         code.setLength(getMaxCodeLen());
/* 218:    */       }
/* 219:    */     }
/* 220:306 */     return code.toString();
/* 221:    */   }
/* 222:    */   
/* 223:    */   private boolean isVowel(StringBuffer string, int index)
/* 224:    */   {
/* 225:310 */     return this.vowels.indexOf(string.charAt(index)) >= 0;
/* 226:    */   }
/* 227:    */   
/* 228:    */   private boolean isPreviousChar(StringBuffer string, int index, char c)
/* 229:    */   {
/* 230:314 */     boolean matches = false;
/* 231:315 */     if ((index > 0) && (index < string.length())) {
/* 232:317 */       matches = string.charAt(index - 1) == c;
/* 233:    */     }
/* 234:319 */     return matches;
/* 235:    */   }
/* 236:    */   
/* 237:    */   private boolean isNextChar(StringBuffer string, int index, char c)
/* 238:    */   {
/* 239:323 */     boolean matches = false;
/* 240:324 */     if ((index >= 0) && (index < string.length() - 1)) {
/* 241:326 */       matches = string.charAt(index + 1) == c;
/* 242:    */     }
/* 243:328 */     return matches;
/* 244:    */   }
/* 245:    */   
/* 246:    */   private boolean regionMatch(StringBuffer string, int index, String test)
/* 247:    */   {
/* 248:332 */     boolean matches = false;
/* 249:333 */     if ((index >= 0) && (index + test.length() - 1 < string.length()))
/* 250:    */     {
/* 251:335 */       String substring = string.substring(index, index + test.length());
/* 252:336 */       matches = substring.equals(test);
/* 253:    */     }
/* 254:338 */     return matches;
/* 255:    */   }
/* 256:    */   
/* 257:    */   private boolean isLastChar(int wdsz, int n)
/* 258:    */   {
/* 259:342 */     return n + 1 == wdsz;
/* 260:    */   }
/* 261:    */   
/* 262:    */   public Object encode(Object pObject)
/* 263:    */     throws EncoderException
/* 264:    */   {
/* 265:359 */     if (!(pObject instanceof String)) {
/* 266:360 */       throw new EncoderException("Parameter supplied to Metaphone encode is not of type java.lang.String");
/* 267:    */     }
/* 268:362 */     return metaphone((String)pObject);
/* 269:    */   }
/* 270:    */   
/* 271:    */   public String encode(String pString)
/* 272:    */   {
/* 273:372 */     return metaphone(pString);
/* 274:    */   }
/* 275:    */   
/* 276:    */   public boolean isMetaphoneEqual(String str1, String str2)
/* 277:    */   {
/* 278:384 */     return metaphone(str1).equals(metaphone(str2));
/* 279:    */   }
/* 280:    */   
/* 281:    */   public int getMaxCodeLen()
/* 282:    */   {
/* 283:391 */     return this.maxCodeLen;
/* 284:    */   }
/* 285:    */   
/* 286:    */   public void setMaxCodeLen(int maxCodeLen)
/* 287:    */   {
/* 288:397 */     this.maxCodeLen = maxCodeLen;
/* 289:    */   }
/* 290:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.language.Metaphone
 * JD-Core Version:    0.7.0.1
 */