/*   1:    */ package org.boon.core.value;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import java.util.Currency;
/*   7:    */ import java.util.Date;
/*   8:    */ import org.boon.Exceptions;
/*   9:    */ import org.boon.core.Conversions;
/*  10:    */ import org.boon.core.Dates;
/*  11:    */ import org.boon.core.TypeType;
/*  12:    */ import org.boon.core.Value;
/*  13:    */ import org.boon.core.reflection.FastStringUtils;
/*  14:    */ import org.boon.json.JsonException;
/*  15:    */ import org.boon.json.implementation.JsonStringDecoder;
/*  16:    */ import org.boon.primitive.CharBuf;
/*  17:    */ import org.boon.primitive.CharScanner;
/*  18:    */ 
/*  19:    */ public class CharSequenceValue
/*  20:    */   implements Value, CharSequence
/*  21:    */ {
/*  22:    */   private final TypeType type;
/*  23:    */   private final boolean checkDate;
/*  24:    */   private final boolean decodeStrings;
/*  25:    */   private char[] buffer;
/*  26:    */   private boolean chopped;
/*  27:    */   private int startIndex;
/*  28:    */   private int endIndex;
/*  29:    */   private Object value;
/*  30:    */   
/*  31:    */   public CharSequenceValue(boolean chop, TypeType type, int startIndex, int endIndex, char[] buffer, boolean encoded, boolean checkDate)
/*  32:    */   {
/*  33: 65 */     this.type = type;
/*  34: 66 */     this.checkDate = checkDate;
/*  35: 67 */     this.decodeStrings = encoded;
/*  36: 69 */     if (chop)
/*  37:    */     {
/*  38:    */       try
/*  39:    */       {
/*  40: 71 */         this.buffer = Arrays.copyOfRange(buffer, startIndex, endIndex);
/*  41:    */       }
/*  42:    */       catch (Exception ex)
/*  43:    */       {
/*  44: 73 */         Exceptions.handle(ex);
/*  45:    */       }
/*  46: 75 */       this.startIndex = 0;
/*  47: 76 */       this.endIndex = this.buffer.length;
/*  48: 77 */       this.chopped = true;
/*  49:    */     }
/*  50:    */     else
/*  51:    */     {
/*  52: 80 */       this.startIndex = startIndex;
/*  53: 81 */       this.endIndex = endIndex;
/*  54: 82 */       this.buffer = buffer;
/*  55:    */     }
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String toString()
/*  59:    */   {
/*  60: 88 */     if (this.decodeStrings) {
/*  61: 89 */       return stringValue();
/*  62:    */     }
/*  63: 90 */     if ((this.startIndex == 0) && (this.endIndex == this.buffer.length)) {
/*  64: 91 */       return FastStringUtils.noCopyStringFromCharsNoCheck(this.buffer);
/*  65:    */     }
/*  66: 93 */     return new String(this.buffer, this.startIndex, this.endIndex - this.startIndex);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public final Object toValue()
/*  70:    */   {
/*  71: 99 */     return this.value = doToValue();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public <T extends Enum> T toEnum(Class<T> cls)
/*  75:    */   {
/*  76:105 */     switch (1.$SwitchMap$org$boon$core$TypeType[this.type.ordinal()])
/*  77:    */     {
/*  78:    */     case 1: 
/*  79:107 */       return Conversions.toEnum(cls, stringValue());
/*  80:    */     case 2: 
/*  81:    */     case 3: 
/*  82:110 */       return Conversions.toEnum(cls, intValue());
/*  83:    */     case 4: 
/*  84:112 */       return null;
/*  85:    */     }
/*  86:114 */     Exceptions.die("toEnum " + cls + " value was " + stringValue());
/*  87:115 */     return null;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public boolean isContainer()
/*  91:    */   {
/*  92:120 */     return false;
/*  93:    */   }
/*  94:    */   
/*  95:    */   private final Object doToValue()
/*  96:    */   {
/*  97:125 */     switch (1.$SwitchMap$org$boon$core$TypeType[this.type.ordinal()])
/*  98:    */     {
/*  99:    */     case 5: 
/* 100:127 */       return Double.valueOf(doubleValue());
/* 101:    */     case 2: 
/* 102:    */     case 3: 
/* 103:134 */       if (CharScanner.isInteger(this.buffer, this.startIndex, this.endIndex - this.startIndex)) {
/* 104:135 */         return Integer.valueOf(intValue());
/* 105:    */       }
/* 106:137 */       return Long.valueOf(longValue());
/* 107:    */     case 1: 
/* 108:140 */       if (this.checkDate)
/* 109:    */       {
/* 110:141 */         Date date = null;
/* 111:142 */         if (Dates.isISO8601QuickCheck(this.buffer, this.startIndex, this.endIndex))
/* 112:    */         {
/* 113:143 */           if (Dates.isJsonDate(this.buffer, this.startIndex, this.endIndex)) {
/* 114:144 */             date = Dates.fromJsonDate(this.buffer, this.startIndex, this.endIndex);
/* 115:145 */           } else if (Dates.isISO8601(this.buffer, this.startIndex, this.endIndex)) {
/* 116:146 */             date = Dates.fromISO8601(this.buffer, this.startIndex, this.endIndex);
/* 117:    */           } else {
/* 118:148 */             return stringValue();
/* 119:    */           }
/* 120:151 */           if (date == null) {
/* 121:152 */             return stringValue();
/* 122:    */           }
/* 123:154 */           return date;
/* 124:    */         }
/* 125:    */       }
/* 126:158 */       return stringValue();
/* 127:    */     }
/* 128:160 */     Exceptions.die();
/* 129:161 */     return null;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public boolean equals(Object o)
/* 133:    */   {
/* 134:166 */     if (this == o) {
/* 135:166 */       return true;
/* 136:    */     }
/* 137:167 */     if (!(o instanceof Value)) {
/* 138:167 */       return false;
/* 139:    */     }
/* 140:169 */     CharSequenceValue value1 = (CharSequenceValue)o;
/* 141:171 */     if (this.endIndex != value1.endIndex) {
/* 142:171 */       return false;
/* 143:    */     }
/* 144:172 */     if (this.startIndex != value1.startIndex) {
/* 145:172 */       return false;
/* 146:    */     }
/* 147:173 */     if (!Arrays.equals(this.buffer, value1.buffer)) {
/* 148:173 */       return false;
/* 149:    */     }
/* 150:174 */     if (this.type != value1.type) {
/* 151:174 */       return false;
/* 152:    */     }
/* 153:175 */     if (this.value != null ? !this.value.equals(value1.value) : value1.value != null) {
/* 154:175 */       return false;
/* 155:    */     }
/* 156:177 */     return true;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public int hashCode()
/* 160:    */   {
/* 161:182 */     int result = this.type != null ? this.type.hashCode() : 0;
/* 162:183 */     result = 31 * result + (this.buffer != null ? Arrays.hashCode(this.buffer) : 0);
/* 163:184 */     result = 31 * result + this.startIndex;
/* 164:185 */     result = 31 * result + this.endIndex;
/* 165:186 */     result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
/* 166:187 */     return result;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public final int length()
/* 170:    */   {
/* 171:193 */     return this.buffer.length;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public final char charAt(int index)
/* 175:    */   {
/* 176:198 */     return this.buffer[index];
/* 177:    */   }
/* 178:    */   
/* 179:    */   public final CharSequence subSequence(int start, int end)
/* 180:    */   {
/* 181:203 */     return new CharSequenceValue(false, this.type, start, end, this.buffer, this.decodeStrings, this.checkDate);
/* 182:    */   }
/* 183:    */   
/* 184:    */   public BigDecimal bigDecimalValue()
/* 185:    */   {
/* 186:207 */     return new BigDecimal(this.buffer, this.startIndex, this.endIndex - this.startIndex);
/* 187:    */   }
/* 188:    */   
/* 189:    */   public BigInteger bigIntegerValue()
/* 190:    */   {
/* 191:212 */     return new BigInteger(toString());
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String stringValue()
/* 195:    */   {
/* 196:216 */     if (this.decodeStrings) {
/* 197:217 */       return JsonStringDecoder.decodeForSure(this.buffer, this.startIndex, this.endIndex);
/* 198:    */     }
/* 199:219 */     return toString();
/* 200:    */   }
/* 201:    */   
/* 202:    */   public String stringValue(CharBuf charBuf)
/* 203:    */   {
/* 204:225 */     if (this.decodeStrings) {
/* 205:226 */       return JsonStringDecoder.decodeForSure(charBuf, this.buffer, this.startIndex, this.endIndex);
/* 206:    */     }
/* 207:228 */     return toString();
/* 208:    */   }
/* 209:    */   
/* 210:    */   public String stringValueEncoded()
/* 211:    */   {
/* 212:234 */     return JsonStringDecoder.decode(this.buffer, this.startIndex, this.endIndex);
/* 213:    */   }
/* 214:    */   
/* 215:    */   public Date dateValue()
/* 216:    */   {
/* 217:241 */     if (this.type == TypeType.STRING)
/* 218:    */     {
/* 219:243 */       if (Dates.isISO8601QuickCheck(this.buffer, this.startIndex, this.endIndex))
/* 220:    */       {
/* 221:245 */         if (Dates.isJsonDate(this.buffer, this.startIndex, this.endIndex)) {
/* 222:246 */           return Dates.fromJsonDate(this.buffer, this.startIndex, this.endIndex);
/* 223:    */         }
/* 224:248 */         if (Dates.isISO8601Jackson(this.buffer, this.startIndex, this.endIndex)) {
/* 225:249 */           return Dates.fromISO8601Jackson(this.buffer, this.startIndex, this.endIndex);
/* 226:    */         }
/* 227:250 */         if (Dates.isISO8601(this.buffer, this.startIndex, this.endIndex)) {
/* 228:251 */           return Dates.fromISO8601(this.buffer, this.startIndex, this.endIndex);
/* 229:    */         }
/* 230:253 */         throw new JsonException("Unable to convert " + stringValue() + " to date ");
/* 231:    */       }
/* 232:257 */       throw new JsonException("Unable to convert " + stringValue() + " to date ");
/* 233:    */     }
/* 234:261 */     return new Date(Dates.utc(longValue()));
/* 235:    */   }
/* 236:    */   
/* 237:    */   public int intValue()
/* 238:    */   {
/* 239:267 */     if (CharScanner.isInteger(this.buffer, this.startIndex, this.endIndex - this.startIndex)) {
/* 240:269 */       return CharScanner.parseInt(this.buffer, this.startIndex, this.endIndex);
/* 241:    */     }
/* 242:271 */     return ((Integer)Exceptions.die(Integer.TYPE, "not an int")).intValue();
/* 243:    */   }
/* 244:    */   
/* 245:    */   public long longValue()
/* 246:    */   {
/* 247:277 */     if (CharScanner.isInteger(this.buffer, this.startIndex, this.endIndex - this.startIndex)) {
/* 248:278 */       return CharScanner.parseInt(this.buffer, this.startIndex, this.endIndex);
/* 249:    */     }
/* 250:280 */     return CharScanner.parseLong(this.buffer, this.startIndex, this.endIndex);
/* 251:    */   }
/* 252:    */   
/* 253:    */   public byte byteValue()
/* 254:    */   {
/* 255:285 */     return (byte)intValue();
/* 256:    */   }
/* 257:    */   
/* 258:    */   public short shortValue()
/* 259:    */   {
/* 260:289 */     return (short)intValue();
/* 261:    */   }
/* 262:    */   
/* 263:292 */   private static float[] fpowersOf10 = { 1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 10000000.0F, 1.0E+008F, 1.0E+009F };
/* 264:    */   
/* 265:    */   public double doubleValue()
/* 266:    */   {
/* 267:307 */     return CharScanner.parseDouble(this.buffer, this.startIndex, this.endIndex);
/* 268:    */   }
/* 269:    */   
/* 270:    */   public boolean booleanValue()
/* 271:    */   {
/* 272:312 */     return Boolean.parseBoolean(toString());
/* 273:    */   }
/* 274:    */   
/* 275:    */   public float floatValue()
/* 276:    */   {
/* 277:317 */     return CharScanner.parseFloat(this.buffer, this.startIndex, this.endIndex);
/* 278:    */   }
/* 279:    */   
/* 280:    */   public Currency currencyValue()
/* 281:    */   {
/* 282:322 */     return Currency.getInstance(toString());
/* 283:    */   }
/* 284:    */   
/* 285:    */   public final void chop()
/* 286:    */   {
/* 287:326 */     if (!this.chopped)
/* 288:    */     {
/* 289:327 */       this.chopped = true;
/* 290:328 */       this.buffer = Arrays.copyOfRange(this.buffer, this.startIndex, this.endIndex);
/* 291:329 */       this.startIndex = 0;
/* 292:330 */       this.endIndex = this.buffer.length;
/* 293:    */     }
/* 294:    */   }
/* 295:    */   
/* 296:    */   public char charValue()
/* 297:    */   {
/* 298:336 */     return this.buffer[this.startIndex];
/* 299:    */   }
/* 300:    */   
/* 301:    */   public TypeType type()
/* 302:    */   {
/* 303:341 */     return TypeType.CHAR_SEQUENCE;
/* 304:    */   }
/* 305:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.value.CharSequenceValue
 * JD-Core Version:    0.7.0.1
 */