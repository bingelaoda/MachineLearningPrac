/*   1:    */ package org.boon.json.implementation;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.List;
/*   6:    */ import org.boon.core.TypeType;
/*   7:    */ import org.boon.core.Value;
/*   8:    */ import org.boon.core.value.CharSequenceValue;
/*   9:    */ import org.boon.core.value.LazyValueMap;
/*  10:    */ import org.boon.core.value.MapItemValue;
/*  11:    */ import org.boon.core.value.NumberValue;
/*  12:    */ import org.boon.core.value.ValueContainer;
/*  13:    */ import org.boon.core.value.ValueList;
/*  14:    */ import org.boon.core.value.ValueMap;
/*  15:    */ import org.boon.core.value.ValueMapImpl;
/*  16:    */ import org.boon.primitive.CharScanner;
/*  17:    */ 
/*  18:    */ public class JsonFastParser
/*  19:    */   extends JsonParserCharArray
/*  20:    */ {
/*  21: 47 */   protected static ValueContainer EMPTY_LIST = new ValueContainer(Collections.emptyList());
/*  22:    */   protected final boolean useValues;
/*  23:    */   protected final boolean chop;
/*  24:    */   protected final boolean lazyChop;
/*  25:    */   protected final boolean checkDates;
/*  26:    */   
/*  27:    */   public boolean isUseValues()
/*  28:    */   {
/*  29: 55 */     return this.useValues;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public JsonFastParser()
/*  33:    */   {
/*  34: 59 */     this(false);
/*  35:    */   }
/*  36:    */   
/*  37:    */   public JsonFastParser(boolean useValues)
/*  38:    */   {
/*  39: 63 */     this(useValues, false);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public JsonFastParser(boolean useValues, boolean chop)
/*  43:    */   {
/*  44: 67 */     this(useValues, chop, !chop);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public JsonFastParser(boolean useValues, boolean chop, boolean lazyChop)
/*  48:    */   {
/*  49: 71 */     this(useValues, chop, lazyChop, true);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public JsonFastParser(boolean useValues, boolean chop, boolean lazyChop, boolean checkDates)
/*  53:    */   {
/*  54: 75 */     this.useValues = useValues;
/*  55: 76 */     this.chop = chop;
/*  56: 77 */     this.lazyChop = lazyChop;
/*  57: 78 */     this.checkDates = checkDates;
/*  58:    */   }
/*  59:    */   
/*  60:    */   protected final Value decodeJsonObjectLazyFinalParse()
/*  61:    */   {
/*  62: 83 */     char[] array = this.charArray;
/*  63: 85 */     if (this.__currentChar == '{') {
/*  64: 86 */       this.__index += 1;
/*  65:    */     }
/*  66: 88 */     ValueMap map = (ValueMap)(this.useValues ? new ValueMapImpl() : new LazyValueMap(this.lazyChop));
/*  67: 89 */     Value value = new ValueContainer(map);
/*  68: 92 */     for (; this.__index < array.length; this.__index += 1)
/*  69:    */     {
/*  70: 93 */       skipWhiteSpaceIfNeeded();
/*  71: 94 */       switch (this.__currentChar)
/*  72:    */       {
/*  73:    */       case '"': 
/*  74: 97 */         Value key = decodeStringOverlay();
/*  75: 98 */         skipWhiteSpaceIfNeeded();
/*  76:100 */         if (this.__currentChar != ':') {
/*  77:102 */           complain("expecting current character to be " + charDescription(this.__currentChar) + "\n");
/*  78:    */         }
/*  79:104 */         this.__index += 1;
/*  80:    */         
/*  81:106 */         Value item = decodeValueOverlay();
/*  82:    */         
/*  83:108 */         skipWhiteSpaceIfNeeded();
/*  84:    */         
/*  85:    */ 
/*  86:111 */         MapItemValue miv = new MapItemValue(key, item);
/*  87:    */         
/*  88:113 */         map.add(miv);
/*  89:    */       }
/*  90:116 */       switch (this.__currentChar)
/*  91:    */       {
/*  92:    */       case '}': 
/*  93:118 */         this.__index += 1;
/*  94:119 */         break;
/*  95:    */       case ',': 
/*  96:    */         break;
/*  97:    */       default: 
/*  98:126 */         complain("expecting '}' or ',' but got current char " + charDescription(this.__currentChar));
/*  99:    */       }
/* 100:    */     }
/* 101:131 */     return value;
/* 102:    */   }
/* 103:    */   
/* 104:    */   private Value decodeValueOverlay()
/* 105:    */   {
/* 106:136 */     skipWhiteSpaceIfNeeded();
/* 107:138 */     switch (this.__currentChar)
/* 108:    */     {
/* 109:    */     case '"': 
/* 110:141 */       return decodeStringOverlay();
/* 111:    */     case '{': 
/* 112:144 */       return decodeJsonObjectLazyFinalParse();
/* 113:    */     case 't': 
/* 114:147 */       return decodeTrue() == true ? ValueContainer.TRUE : ValueContainer.FALSE;
/* 115:    */     case 'f': 
/* 116:150 */       return !decodeFalse() ? ValueContainer.FALSE : ValueContainer.TRUE;
/* 117:    */     case 'n': 
/* 118:153 */       return decodeNull() == null ? ValueContainer.NULL : ValueContainer.NULL;
/* 119:    */     case '[': 
/* 120:156 */       return decodeJsonArrayOverlay();
/* 121:    */     case '0': 
/* 122:    */     case '1': 
/* 123:    */     case '2': 
/* 124:    */     case '3': 
/* 125:    */     case '4': 
/* 126:    */     case '5': 
/* 127:    */     case '6': 
/* 128:    */     case '7': 
/* 129:    */     case '8': 
/* 130:    */     case '9': 
/* 131:168 */       return decodeNumberOverlay(false);
/* 132:    */     case '-': 
/* 133:171 */       return decodeNumberOverlay(true);
/* 134:    */     }
/* 135:174 */     complain("Unable to determine the current character, it is not a string, number, array, or object");
/* 136:    */     
/* 137:176 */     return null;
/* 138:    */   }
/* 139:    */   
/* 140:    */   private final Value decodeNumberOverlay(boolean minus)
/* 141:    */   {
/* 142:182 */     char[] array = this.charArray;
/* 143:    */     
/* 144:184 */     int startIndex = this.__index;
/* 145:185 */     int index = this.__index;
/* 146:    */     
/* 147:187 */     boolean doubleFloat = false;
/* 148:189 */     if ((minus) && (index + 1 < array.length)) {
/* 149:190 */       index++;
/* 150:    */     }
/* 151:    */     char currentChar;
/* 152:    */     for (;;)
/* 153:    */     {
/* 154:194 */       currentChar = array[index];
/* 155:195 */       if (!CharScanner.isNumberDigit(currentChar))
/* 156:    */       {
/* 157:197 */         if (currentChar <= ' ') {
/* 158:    */           break;
/* 159:    */         }
/* 160:199 */         if (CharScanner.isDelimiter(currentChar)) {
/* 161:    */           break;
/* 162:    */         }
/* 163:201 */         if (CharScanner.isDecimalChar(currentChar)) {
/* 164:202 */           doubleFloat = true;
/* 165:    */         }
/* 166:    */       }
/* 167:204 */       index++;
/* 168:205 */       if (index >= array.length) {
/* 169:    */         break;
/* 170:    */       }
/* 171:    */     }
/* 172:208 */     this.__index = index;
/* 173:209 */     this.__currentChar = currentChar;
/* 174:    */     
/* 175:211 */     TypeType type = doubleFloat ? TypeType.DOUBLE : TypeType.INT;
/* 176:    */     
/* 177:213 */     NumberValue value = new NumberValue(this.chop, type, startIndex, this.__index, this.charArray);
/* 178:    */     
/* 179:215 */     return value;
/* 180:    */   }
/* 181:    */   
/* 182:    */   private Value decodeStringOverlay()
/* 183:    */   {
/* 184:220 */     char[] array = this.charArray;
/* 185:221 */     int index = this.__index;
/* 186:222 */     char currentChar = this.charArray[index];
/* 187:224 */     if ((index < array.length) && (currentChar == '"')) {
/* 188:225 */       index++;
/* 189:    */     }
/* 190:228 */     int startIndex = index;
/* 191:    */     
/* 192:230 */     boolean encoded = CharScanner.hasEscapeChar(array, index, this.indexHolder);
/* 193:231 */     index = this.indexHolder[0];
/* 194:233 */     if (encoded) {
/* 195:234 */       index = CharScanner.findEndQuote(array, index);
/* 196:    */     }
/* 197:237 */     Value value = new CharSequenceValue(this.chop, TypeType.STRING, startIndex, index, array, encoded, this.checkDates);
/* 198:239 */     if (index < array.length) {
/* 199:240 */       index++;
/* 200:    */     }
/* 201:243 */     this.__index = index;
/* 202:244 */     return value;
/* 203:    */   }
/* 204:    */   
/* 205:    */   private Value decodeJsonArrayOverlay()
/* 206:    */   {
/* 207:249 */     char[] array = this.charArray;
/* 208:250 */     if (this.__currentChar == '[') {
/* 209:251 */       this.__index += 1;
/* 210:    */     }
/* 211:254 */     skipWhiteSpaceIfNeeded();
/* 212:257 */     if (this.__currentChar == ']')
/* 213:    */     {
/* 214:258 */       this.__index += 1;
/* 215:259 */       return EMPTY_LIST;
/* 216:    */     }
/* 217:    */     List<Object> list;
/* 218:    */     List<Object> list;
/* 219:264 */     if (this.useValues) {
/* 220:265 */       list = new ArrayList();
/* 221:    */     } else {
/* 222:267 */       list = new ValueList(this.lazyChop);
/* 223:    */     }
/* 224:270 */     Value value = new ValueContainer(list);
/* 225:    */     
/* 226:    */ 
/* 227:    */ 
/* 228:    */ 
/* 229:275 */     boolean foundEnd = false;
/* 230:278 */     for (; this.__index < array.length; this.__index += 1)
/* 231:    */     {
/* 232:279 */       Value item = decodeValueOverlay();
/* 233:    */       
/* 234:281 */       list.add(item);
/* 235:282 */       char c = currentChar();
/* 236:283 */       switch (c)
/* 237:    */       {
/* 238:    */       case ',': 
/* 239:    */         break;
/* 240:    */       case ']': 
/* 241:287 */         this.__index += 1;
/* 242:288 */         foundEnd = true;
/* 243:289 */         break;
/* 244:    */       default: 
/* 245:293 */         int lastIndex = this.__index;
/* 246:294 */         skipWhiteSpaceIfNeeded();
/* 247:295 */         c = currentChar();
/* 248:297 */         switch (c)
/* 249:    */         {
/* 250:    */         case ',': 
/* 251:    */           break;
/* 252:    */         case ']': 
/* 253:301 */           if (this.__index == lastIndex) {
/* 254:302 */             complain("missing ]");
/* 255:    */           }
/* 256:304 */           foundEnd = true;
/* 257:305 */           this.__index += 1;
/* 258:306 */           break;
/* 259:    */         default: 
/* 260:308 */           complain(String.format("expecting a ',' or a ']',  but got \nthe current character of  %s  on array size of %s \n", new Object[] { charDescription(this.__currentChar), Integer.valueOf(list.size()) }));
/* 261:    */         }
/* 262:    */         break;
/* 263:    */       }
/* 264:    */     }
/* 265:317 */     if (!foundEnd) {
/* 266:318 */       complain("Did not find end of Json Array");
/* 267:    */     }
/* 268:320 */     return value;
/* 269:    */   }
/* 270:    */   
/* 271:    */   public Object parse(char[] chars)
/* 272:    */   {
/* 273:324 */     this.lastIndex = (chars.length - 1);
/* 274:325 */     this.__index = 0;
/* 275:326 */     this.charArray = chars;
/* 276:    */     
/* 277:328 */     Value value = decodeValueOverlay();
/* 278:329 */     if (value.isContainer()) {
/* 279:330 */       return value.toValue();
/* 280:    */     }
/* 281:332 */     return value;
/* 282:    */   }
/* 283:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.implementation.JsonFastParser
 * JD-Core Version:    0.7.0.1
 */