/*   1:    */ package org.boon.core.value;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import java.util.Currency;
/*   7:    */ import java.util.Date;
/*   8:    */ import org.boon.Boon;
/*   9:    */ import org.boon.Exceptions;
/*  10:    */ import org.boon.core.Conversions;
/*  11:    */ import org.boon.core.Dates;
/*  12:    */ import org.boon.core.TypeType;
/*  13:    */ import org.boon.core.Value;
/*  14:    */ import org.boon.core.reflection.FastStringUtils;
/*  15:    */ import org.boon.primitive.CharBuf;
/*  16:    */ import org.boon.primitive.CharScanner;
/*  17:    */ 
/*  18:    */ public class NumberValue
/*  19:    */   extends Number
/*  20:    */   implements Value
/*  21:    */ {
/*  22:    */   private char[] buffer;
/*  23:    */   private boolean chopped;
/*  24:    */   private int startIndex;
/*  25:    */   private int endIndex;
/*  26:    */   private TypeType type;
/*  27:    */   private Object value;
/*  28:    */   
/*  29:    */   public NumberValue(TypeType type)
/*  30:    */   {
/*  31: 62 */     this.type = type;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public NumberValue() {}
/*  35:    */   
/*  36:    */   public NumberValue(boolean chop, TypeType type, int startIndex, int endIndex, char[] buffer)
/*  37:    */   {
/*  38: 70 */     this.type = type;
/*  39:    */     try
/*  40:    */     {
/*  41: 74 */       if (chop)
/*  42:    */       {
/*  43: 76 */         this.buffer = Arrays.copyOfRange(buffer, startIndex, endIndex);
/*  44: 77 */         this.startIndex = 0;
/*  45: 78 */         this.endIndex = this.buffer.length;
/*  46: 79 */         this.chopped = true;
/*  47:    */       }
/*  48:    */       else
/*  49:    */       {
/*  50: 81 */         this.startIndex = startIndex;
/*  51: 82 */         this.endIndex = endIndex;
/*  52: 83 */         this.buffer = buffer;
/*  53:    */       }
/*  54:    */     }
/*  55:    */     catch (Exception ex)
/*  56:    */     {
/*  57: 86 */       Boon.puts(new Object[] { "exception", ex, "start", Integer.valueOf(startIndex), "end", Integer.valueOf(endIndex) });
/*  58: 87 */       Exceptions.handle(ex);
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String toString()
/*  63:    */   {
/*  64: 95 */     if ((this.startIndex == 0) && (this.endIndex == this.buffer.length)) {
/*  65: 96 */       return FastStringUtils.noCopyStringFromCharsNoCheck(this.buffer);
/*  66:    */     }
/*  67: 98 */     return new String(this.buffer, this.startIndex, this.endIndex - this.startIndex);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public final Object toValue()
/*  71:    */   {
/*  72:105 */     return this.value = doToValue();
/*  73:    */   }
/*  74:    */   
/*  75:    */   public <T extends Enum> T toEnum(Class<T> cls)
/*  76:    */   {
/*  77:111 */     return Conversions.toEnum(cls, intValue());
/*  78:    */   }
/*  79:    */   
/*  80:    */   public boolean isContainer()
/*  81:    */   {
/*  82:116 */     return false;
/*  83:    */   }
/*  84:    */   
/*  85:    */   private final Object doToValue()
/*  86:    */   {
/*  87:121 */     switch (1.$SwitchMap$org$boon$core$TypeType[this.type.ordinal()])
/*  88:    */     {
/*  89:    */     case 1: 
/*  90:    */     case 2: 
/*  91:124 */       return Double.valueOf(doubleValue());
/*  92:    */     case 3: 
/*  93:    */     case 4: 
/*  94:128 */       if (CharScanner.isInteger(this.buffer, this.startIndex, this.endIndex - this.startIndex)) {
/*  95:129 */         return Integer.valueOf(intValue());
/*  96:    */       }
/*  97:131 */       return Long.valueOf(longValue());
/*  98:    */     }
/*  99:134 */     Exceptions.die();
/* 100:135 */     return null;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public boolean equals(Object o)
/* 104:    */   {
/* 105:141 */     if ((o instanceof Integer))
/* 106:    */     {
/* 107:142 */       Integer i = (Integer)o;
/* 108:143 */       return toValue().equals(i);
/* 109:    */     }
/* 110:144 */     if ((o instanceof Float))
/* 111:    */     {
/* 112:145 */       Float i = (Float)o;
/* 113:146 */       return floatValue() == i.floatValue();
/* 114:    */     }
/* 115:147 */     if ((o instanceof Double))
/* 116:    */     {
/* 117:148 */       Double i = (Double)o;
/* 118:149 */       return toValue().equals(i);
/* 119:    */     }
/* 120:150 */     if ((o instanceof Short))
/* 121:    */     {
/* 122:151 */       Short i = (Short)o;
/* 123:152 */       return shortValue() == i.shortValue();
/* 124:    */     }
/* 125:153 */     if ((o instanceof Byte))
/* 126:    */     {
/* 127:154 */       Byte i = (Byte)o;
/* 128:155 */       return byteValue() == i.byteValue();
/* 129:    */     }
/* 130:156 */     if ((o instanceof Date))
/* 131:    */     {
/* 132:157 */       Date i = (Date)o;
/* 133:158 */       return dateValue().equals(i);
/* 134:    */     }
/* 135:159 */     if ((o instanceof Long))
/* 136:    */     {
/* 137:160 */       Long i = (Long)o;
/* 138:161 */       return longValue() == i.longValue();
/* 139:    */     }
/* 140:163 */     if (this == o) {
/* 141:163 */       return true;
/* 142:    */     }
/* 143:164 */     if (!(o instanceof Value)) {
/* 144:164 */       return false;
/* 145:    */     }
/* 146:166 */     NumberValue value1 = (NumberValue)o;
/* 147:168 */     if (this.endIndex != value1.endIndex) {
/* 148:168 */       return false;
/* 149:    */     }
/* 150:169 */     if (this.startIndex != value1.startIndex) {
/* 151:169 */       return false;
/* 152:    */     }
/* 153:170 */     if (!Arrays.equals(this.buffer, value1.buffer)) {
/* 154:170 */       return false;
/* 155:    */     }
/* 156:171 */     if (this.type != value1.type) {
/* 157:171 */       return false;
/* 158:    */     }
/* 159:172 */     if (this.value != null ? !this.value.equals(value1.value) : value1.value != null) {
/* 160:172 */       return false;
/* 161:    */     }
/* 162:174 */     return true;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public int hashCode()
/* 166:    */   {
/* 167:179 */     int result = this.type != null ? this.type.hashCode() : 0;
/* 168:180 */     result = 31 * result + (this.buffer != null ? Arrays.hashCode(this.buffer) : 0);
/* 169:181 */     result = 31 * result + this.startIndex;
/* 170:182 */     result = 31 * result + this.endIndex;
/* 171:183 */     result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
/* 172:184 */     return result;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public BigDecimal bigDecimalValue()
/* 176:    */   {
/* 177:189 */     return new BigDecimal(this.buffer, this.startIndex, this.endIndex - this.startIndex);
/* 178:    */   }
/* 179:    */   
/* 180:    */   public BigInteger bigIntegerValue()
/* 181:    */   {
/* 182:194 */     return new BigInteger(toString());
/* 183:    */   }
/* 184:    */   
/* 185:    */   public String stringValue()
/* 186:    */   {
/* 187:198 */     return toString();
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String stringValue(CharBuf charBuf)
/* 191:    */   {
/* 192:203 */     return toString();
/* 193:    */   }
/* 194:    */   
/* 195:    */   public String stringValueEncoded()
/* 196:    */   {
/* 197:208 */     return toString();
/* 198:    */   }
/* 199:    */   
/* 200:    */   public Date dateValue()
/* 201:    */   {
/* 202:214 */     return new Date(Dates.utc(longValue()));
/* 203:    */   }
/* 204:    */   
/* 205:    */   public int intValue()
/* 206:    */   {
/* 207:220 */     if (CharScanner.isInteger(this.buffer, this.startIndex, this.endIndex - this.startIndex)) {
/* 208:221 */       return CharScanner.parseInt(this.buffer, this.startIndex, this.endIndex);
/* 209:    */     }
/* 210:223 */     return 0;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public long longValue()
/* 214:    */   {
/* 215:230 */     if (CharScanner.isInteger(this.buffer, this.startIndex, this.endIndex - this.startIndex)) {
/* 216:231 */       return CharScanner.parseInt(this.buffer, this.startIndex, this.endIndex);
/* 217:    */     }
/* 218:232 */     if (CharScanner.isLong(this.buffer, this.startIndex, this.endIndex - this.startIndex)) {
/* 219:233 */       return CharScanner.parseLong(this.buffer, this.startIndex, this.endIndex);
/* 220:    */     }
/* 221:235 */     return 0L;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public byte byteValue()
/* 225:    */   {
/* 226:241 */     return (byte)intValue();
/* 227:    */   }
/* 228:    */   
/* 229:    */   public short shortValue()
/* 230:    */   {
/* 231:245 */     return (short)intValue();
/* 232:    */   }
/* 233:    */   
/* 234:    */   public double doubleValue()
/* 235:    */   {
/* 236:252 */     return CharScanner.parseDouble(this.buffer, this.startIndex, this.endIndex);
/* 237:    */   }
/* 238:    */   
/* 239:    */   public boolean booleanValue()
/* 240:    */   {
/* 241:258 */     return Boolean.parseBoolean(toString());
/* 242:    */   }
/* 243:    */   
/* 244:    */   public float floatValue()
/* 245:    */   {
/* 246:264 */     return CharScanner.parseFloat(this.buffer, this.startIndex, this.endIndex);
/* 247:    */   }
/* 248:    */   
/* 249:    */   public Currency currencyValue()
/* 250:    */   {
/* 251:269 */     return Currency.getInstance(toString());
/* 252:    */   }
/* 253:    */   
/* 254:    */   public final void chop()
/* 255:    */   {
/* 256:273 */     if (!this.chopped)
/* 257:    */     {
/* 258:274 */       this.chopped = true;
/* 259:275 */       this.buffer = Arrays.copyOfRange(this.buffer, this.startIndex, this.endIndex);
/* 260:276 */       this.startIndex = 0;
/* 261:277 */       this.endIndex = this.buffer.length;
/* 262:    */     }
/* 263:    */   }
/* 264:    */   
/* 265:    */   public char charValue()
/* 266:    */   {
/* 267:286 */     return this.buffer[this.startIndex];
/* 268:    */   }
/* 269:    */   
/* 270:    */   public TypeType type()
/* 271:    */   {
/* 272:291 */     return TypeType.NUMBER;
/* 273:    */   }
/* 274:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.value.NumberValue
 * JD-Core Version:    0.7.0.1
 */