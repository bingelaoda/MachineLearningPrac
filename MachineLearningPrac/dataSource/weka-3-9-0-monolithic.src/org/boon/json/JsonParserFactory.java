/*   1:    */ package org.boon.json;
/*   2:    */ 
/*   3:    */ import java.nio.charset.Charset;
/*   4:    */ import java.nio.charset.StandardCharsets;
/*   5:    */ import java.util.Set;
/*   6:    */ import org.boon.Str;
/*   7:    */ import org.boon.core.reflection.Mapper;
/*   8:    */ import org.boon.core.reflection.MapperComplex;
/*   9:    */ import org.boon.core.reflection.MapperSimple;
/*  10:    */ import org.boon.core.reflection.fields.FieldAccessMode;
/*  11:    */ import org.boon.json.implementation.BaseJsonParserAndMapper;
/*  12:    */ import org.boon.json.implementation.JsonAsciiParser;
/*  13:    */ import org.boon.json.implementation.JsonFastParser;
/*  14:    */ import org.boon.json.implementation.JsonParserCharArray;
/*  15:    */ import org.boon.json.implementation.JsonParserLax;
/*  16:    */ import org.boon.json.implementation.JsonParserUsingCharacterSource;
/*  17:    */ import org.boon.json.implementation.JsonUTF8Parser;
/*  18:    */ import org.boon.json.implementation.PlistParser;
/*  19:    */ 
/*  20:    */ public class JsonParserFactory
/*  21:    */ {
/*  22: 45 */   private Charset charset = StandardCharsets.UTF_8;
/*  23:    */   private boolean lax;
/*  24: 47 */   private boolean chop = false;
/*  25: 48 */   private boolean lazyChop = true;
/*  26: 49 */   private FieldAccessMode fieldAccessType = FieldAccessMode.FIELD;
/*  27: 50 */   private boolean useAnnotations = true;
/*  28:    */   private boolean caseInsensitiveFields;
/*  29:    */   private Set<String> ignoreSet;
/*  30:    */   private String view;
/*  31: 55 */   private boolean respectIgnore = true;
/*  32:    */   private boolean acceptSingleValueAsArray;
/*  33: 58 */   private boolean checkDates = true;
/*  34:    */   
/*  35:    */   public FieldAccessMode getFieldAccessType()
/*  36:    */   {
/*  37: 62 */     return this.fieldAccessType;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public boolean isChop()
/*  41:    */   {
/*  42: 67 */     return this.chop;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public JsonParserFactory setChop(boolean chop)
/*  46:    */   {
/*  47: 71 */     this.chop = chop;
/*  48: 72 */     return this;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public boolean isLazyChop()
/*  52:    */   {
/*  53: 76 */     return this.lazyChop;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public JsonParserFactory setLazyChop(boolean lazyChop)
/*  57:    */   {
/*  58: 80 */     this.lazyChop = lazyChop;
/*  59: 81 */     return this;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public JsonParserFactory lax()
/*  63:    */   {
/*  64: 85 */     this.lax = true;
/*  65: 86 */     return this;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public JsonParserFactory strict()
/*  69:    */   {
/*  70: 90 */     this.lax = false;
/*  71: 91 */     return this;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public JsonParserFactory setCharset(Charset charset)
/*  75:    */   {
/*  76: 96 */     this.charset = charset;
/*  77: 97 */     return this;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public JsonParserAndMapper createFastParser()
/*  81:    */   {
/*  82:107 */     BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper(new JsonFastParser(false, this.chop, this.lazyChop, this.checkDates), createMapper());
/*  83:    */     
/*  84:    */ 
/*  85:110 */     jsonParser.setCharset(this.charset);
/*  86:111 */     return jsonParser;
/*  87:    */   }
/*  88:    */   
/*  89:    */   private Mapper createMapper()
/*  90:    */   {
/*  91:115 */     if ((this.useAnnotations) && (!this.caseInsensitiveFields) && (!this.acceptSingleValueAsArray) && (this.ignoreSet == null) && (Str.isEmpty(this.view)) && (this.respectIgnore)) {
/*  92:118 */       return new MapperSimple(this.fieldAccessType.create(true));
/*  93:    */     }
/*  94:120 */     return new MapperComplex(this.fieldAccessType, this.useAnnotations, this.caseInsensitiveFields, this.ignoreSet, this.view, this.respectIgnore, this.acceptSingleValueAsArray);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public JsonParserAndMapper createFastObjectMapperParser()
/*  98:    */   {
/*  99:127 */     BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper(new JsonFastParser(true), createMapper());
/* 100:    */     
/* 101:    */ 
/* 102:130 */     jsonParser.setCharset(this.charset);
/* 103:131 */     return jsonParser;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public JsonParserAndMapper createUTF8DirectByteParser()
/* 107:    */   {
/* 108:138 */     BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper(new JsonUTF8Parser(), createMapper());
/* 109:    */     
/* 110:    */ 
/* 111:    */ 
/* 112:    */ 
/* 113:143 */     jsonParser.setCharset(StandardCharsets.UTF_8);
/* 114:144 */     return jsonParser;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public JsonParserAndMapper createASCIIParser()
/* 118:    */   {
/* 119:149 */     BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper(new JsonAsciiParser(), createMapper());
/* 120:    */     
/* 121:    */ 
/* 122:    */ 
/* 123:    */ 
/* 124:154 */     jsonParser.setCharset(StandardCharsets.US_ASCII);
/* 125:155 */     return jsonParser;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public JsonParserAndMapper createLaxParser()
/* 129:    */   {
/* 130:161 */     BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper(new JsonParserLax(false, this.chop, this.lazyChop, this.checkDates), createMapper());
/* 131:    */     
/* 132:    */ 
/* 133:    */ 
/* 134:165 */     jsonParser.setCharset(this.charset);
/* 135:166 */     return jsonParser;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public JsonParserAndMapper createParserWithEvents(JsonParserEvents events)
/* 139:    */   {
/* 140:172 */     BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper(new JsonParserLax(false, this.chop, this.lazyChop, false, events), createMapper());
/* 141:    */     
/* 142:    */ 
/* 143:    */ 
/* 144:    */ 
/* 145:177 */     jsonParser.setCharset(this.charset);
/* 146:178 */     return jsonParser;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public JsonParserAndMapper createCharacterSourceParser()
/* 150:    */   {
/* 151:183 */     BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper(new JsonParserUsingCharacterSource(), createMapper());
/* 152:    */     
/* 153:    */ 
/* 154:186 */     jsonParser.setCharset(this.charset);
/* 155:187 */     return jsonParser;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public JsonParserAndMapper createJsonCharArrayParser()
/* 159:    */   {
/* 160:191 */     BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper(new JsonParserCharArray(), createMapper());
/* 161:    */     
/* 162:    */ 
/* 163:194 */     jsonParser.setCharset(this.charset);
/* 164:195 */     return jsonParser;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public JsonParserAndMapper createPlistParser()
/* 168:    */   {
/* 169:200 */     if (this.charset == null) {
/* 170:201 */       this.charset = StandardCharsets.US_ASCII;
/* 171:    */     }
/* 172:203 */     BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper(new PlistParser(false, this.chop, this.lazyChop), createMapper());
/* 173:    */     
/* 174:    */ 
/* 175:    */ 
/* 176:    */ 
/* 177:208 */     jsonParser.setCharset(this.charset);
/* 178:209 */     return jsonParser;
/* 179:    */   }
/* 180:    */   
/* 181:    */   public JsonParserAndMapper createLazyFinalParser()
/* 182:    */   {
/* 183:213 */     return createFastParser();
/* 184:    */   }
/* 185:    */   
/* 186:    */   public JsonParserAndMapper createJsonParserForJsonPath()
/* 187:    */   {
/* 188:217 */     return createFastParser();
/* 189:    */   }
/* 190:    */   
/* 191:    */   public JsonParserAndMapper create()
/* 192:    */   {
/* 193:224 */     if (this.charset == null) {
/* 194:225 */       this.charset = StandardCharsets.UTF_8;
/* 195:    */     }
/* 196:228 */     return new JsonMappingParser(createMapper(), this.charset, this.lax, this.chop, this.lazyChop);
/* 197:    */   }
/* 198:    */   
/* 199:    */   public boolean isUsePropertiesFirst()
/* 200:    */   {
/* 201:235 */     return this.fieldAccessType == FieldAccessMode.PROPERTY_THEN_FIELD;
/* 202:    */   }
/* 203:    */   
/* 204:    */   public JsonParserFactory usePropertiesFirst()
/* 205:    */   {
/* 206:240 */     this.fieldAccessType = FieldAccessMode.PROPERTY_THEN_FIELD;
/* 207:241 */     return this;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public boolean isUseFieldsFirst()
/* 211:    */   {
/* 212:245 */     return this.fieldAccessType == FieldAccessMode.FIELD_THEN_PROPERTY;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public JsonParserFactory useFieldsFirst()
/* 216:    */   {
/* 217:251 */     this.fieldAccessType = FieldAccessMode.FIELD_THEN_PROPERTY;
/* 218:252 */     return this;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public JsonParserFactory useFieldsOnly()
/* 222:    */   {
/* 223:257 */     this.fieldAccessType = FieldAccessMode.FIELD;
/* 224:258 */     return this;
/* 225:    */   }
/* 226:    */   
/* 227:    */   public JsonParserFactory usePropertyOnly()
/* 228:    */   {
/* 229:264 */     this.fieldAccessType = FieldAccessMode.PROPERTY;
/* 230:265 */     return this;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public JsonParserFactory useAnnotations()
/* 234:    */   {
/* 235:271 */     this.useAnnotations = true;
/* 236:272 */     return this;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public boolean isUseAnnotations()
/* 240:    */   {
/* 241:276 */     return this.useAnnotations;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public JsonParserFactory setUseAnnotations(boolean useAnnotations)
/* 245:    */   {
/* 246:280 */     this.useAnnotations = useAnnotations;
/* 247:281 */     return this;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public JsonParserFactory caseInsensitiveFields()
/* 251:    */   {
/* 252:287 */     this.caseInsensitiveFields = true;
/* 253:288 */     return this;
/* 254:    */   }
/* 255:    */   
/* 256:    */   public boolean isCaseInsensitiveFields()
/* 257:    */   {
/* 258:292 */     return this.caseInsensitiveFields;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public JsonParserFactory setCaseInsensitiveFields(boolean caseInsensitiveFields)
/* 262:    */   {
/* 263:296 */     this.caseInsensitiveFields = caseInsensitiveFields;
/* 264:297 */     return this;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public Set<String> getIgnoreSet()
/* 268:    */   {
/* 269:305 */     return this.ignoreSet;
/* 270:    */   }
/* 271:    */   
/* 272:    */   public JsonParserFactory setIgnoreSet(Set<String> ignoreSet)
/* 273:    */   {
/* 274:309 */     this.ignoreSet = ignoreSet;
/* 275:310 */     return this;
/* 276:    */   }
/* 277:    */   
/* 278:    */   public String getView()
/* 279:    */   {
/* 280:314 */     return this.view;
/* 281:    */   }
/* 282:    */   
/* 283:    */   public JsonParserFactory setView(String view)
/* 284:    */   {
/* 285:318 */     this.view = view;
/* 286:319 */     return this;
/* 287:    */   }
/* 288:    */   
/* 289:    */   public boolean isRespectIgnore()
/* 290:    */   {
/* 291:323 */     return this.respectIgnore;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public JsonParserFactory setRespectIgnore(boolean respectIgnore)
/* 295:    */   {
/* 296:327 */     this.respectIgnore = respectIgnore;
/* 297:328 */     return this;
/* 298:    */   }
/* 299:    */   
/* 300:    */   public JsonParserFactory acceptSingleValueAsArray()
/* 301:    */   {
/* 302:332 */     this.acceptSingleValueAsArray = true;
/* 303:333 */     return this;
/* 304:    */   }
/* 305:    */   
/* 306:    */   public boolean isAcceptSingleValueAsArray()
/* 307:    */   {
/* 308:337 */     return this.acceptSingleValueAsArray;
/* 309:    */   }
/* 310:    */   
/* 311:    */   public JsonParserFactory setAcceptSingleValueAsArray(boolean acceptSingleValueAsArray)
/* 312:    */   {
/* 313:341 */     this.acceptSingleValueAsArray = acceptSingleValueAsArray;
/* 314:342 */     return this;
/* 315:    */   }
/* 316:    */   
/* 317:    */   public JsonParserFactory setCheckDates(boolean flag)
/* 318:    */   {
/* 319:348 */     this.checkDates = flag;
/* 320:349 */     return this;
/* 321:    */   }
/* 322:    */   
/* 323:    */   public boolean isCheckDatesSet()
/* 324:    */   {
/* 325:353 */     return this.checkDates;
/* 326:    */   }
/* 327:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.JsonParserFactory
 * JD-Core Version:    0.7.0.1
 */