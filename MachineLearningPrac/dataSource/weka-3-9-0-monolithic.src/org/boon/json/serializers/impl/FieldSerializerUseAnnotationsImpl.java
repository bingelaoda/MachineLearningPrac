/*   1:    */ package org.boon.json.serializers.impl;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.util.Collection;
/*   6:    */ import java.util.Currency;
/*   7:    */ import java.util.Date;
/*   8:    */ import java.util.IdentityHashMap;
/*   9:    */ import java.util.List;
/*  10:    */ import java.util.Map;
/*  11:    */ import java.util.Set;
/*  12:    */ import java.util.TimeZone;
/*  13:    */ import org.boon.Sets;
/*  14:    */ import org.boon.core.TypeType;
/*  15:    */ import org.boon.core.reflection.FastStringUtils;
/*  16:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  17:    */ import org.boon.json.serializers.CustomFieldSerializer;
/*  18:    */ import org.boon.json.serializers.CustomObjectSerializer;
/*  19:    */ import org.boon.json.serializers.FieldFilter;
/*  20:    */ import org.boon.json.serializers.FieldSerializer;
/*  21:    */ import org.boon.json.serializers.JsonSerializerInternal;
/*  22:    */ import org.boon.primitive.Arry;
/*  23:    */ import org.boon.primitive.CharBuf;
/*  24:    */ 
/*  25:    */ public class FieldSerializerUseAnnotationsImpl
/*  26:    */   implements FieldSerializer
/*  27:    */ {
/*  28:    */   private final boolean includeNulls;
/*  29:    */   private final boolean includeDefault;
/*  30:    */   private final boolean useAnnotations;
/*  31:    */   private final boolean includeEmpty;
/*  32:    */   private final boolean handleSimpleBackReference;
/*  33:    */   private final boolean handleComplexBackReference;
/*  34:    */   private final String view;
/*  35:    */   private IdentityHashMap idMap;
/*  36:    */   private Map<Class, CustomObjectSerializer> overrideMap;
/*  37:    */   private Map<String, CustomFieldSerializer> customFieldSerializerMap;
/*  38:    */   private List<CustomFieldSerializer> customFieldSerializers;
/*  39: 60 */   private final Set<Class> noHandle = Sets.safeSet(Class.class);
/*  40:    */   private final List<FieldFilter> filterProperties;
/*  41:    */   
/*  42:    */   public FieldSerializerUseAnnotationsImpl(boolean includeNulls, boolean includeDefault, boolean useAnnotations, boolean includeEmpty, boolean handleSimpleBackReference, boolean handleComplexBackReference, Map<Class, CustomObjectSerializer> overrideMap, List<FieldFilter> filterProperties, Map<String, CustomFieldSerializer> customFieldSerializerMap, List<CustomFieldSerializer> customFieldSerializers, String view)
/*  43:    */   {
/*  44: 76 */     this.includeNulls = includeNulls;
/*  45: 77 */     this.includeDefault = includeDefault;
/*  46: 78 */     this.useAnnotations = useAnnotations;
/*  47: 79 */     this.includeEmpty = includeEmpty;
/*  48: 80 */     this.handleSimpleBackReference = handleSimpleBackReference;
/*  49: 81 */     this.handleComplexBackReference = handleComplexBackReference;
/*  50: 82 */     this.view = view;
/*  51: 84 */     if (handleComplexBackReference) {
/*  52: 85 */       this.idMap = new IdentityHashMap();
/*  53:    */     }
/*  54: 88 */     this.overrideMap = overrideMap;
/*  55: 89 */     this.filterProperties = filterProperties;
/*  56: 90 */     this.customFieldSerializerMap = customFieldSerializerMap;
/*  57: 91 */     this.customFieldSerializers = customFieldSerializers;
/*  58:    */   }
/*  59:    */   
/*  60:    */   private void serializeFieldName(String name, CharBuf builder)
/*  61:    */   {
/*  62: 96 */     builder.addJsonFieldName(FastStringUtils.toCharArray(name));
/*  63:    */   }
/*  64:    */   
/*  65:    */   public final boolean serializeField(JsonSerializerInternal serializer, Object parent, FieldAccess fieldAccess, CharBuf builder)
/*  66:    */   {
/*  67:102 */     String fieldName = fieldAccess.alias();
/*  68:103 */     TypeType typeEnum = fieldAccess.typeEnum();
/*  69:105 */     if ((this.useAnnotations) && (fieldAccess.ignore())) {
/*  70:106 */       return false;
/*  71:    */     }
/*  72:109 */     if ((this.useAnnotations) && (this.view != null) && (!fieldAccess.isViewActive(this.view))) {
/*  73:110 */       return false;
/*  74:    */     }
/*  75:114 */     boolean include = (this.useAnnotations) && (fieldAccess.include());
/*  76:117 */     if (this.filterProperties != null) {
/*  77:118 */       for (FieldFilter filter : this.filterProperties) {
/*  78:119 */         if (!filter.include(parent, fieldAccess)) {
/*  79:120 */           return false;
/*  80:    */         }
/*  81:    */       }
/*  82:    */     }
/*  83:126 */     if (this.customFieldSerializerMap != null)
/*  84:    */     {
/*  85:128 */       CustomFieldSerializer customFieldSerializer = (CustomFieldSerializer)this.customFieldSerializerMap.get(fieldAccess.name());
/*  86:129 */       if (customFieldSerializer.serializeField(serializer, parent, fieldAccess, builder)) {
/*  87:130 */         return true;
/*  88:    */       }
/*  89:    */     }
/*  90:134 */     if (this.customFieldSerializers != null) {
/*  91:136 */       for (CustomFieldSerializer cfs : this.customFieldSerializers) {
/*  92:137 */         if (cfs.serializeField(serializer, parent, fieldAccess, builder) == true) {
/*  93:138 */           return true;
/*  94:    */         }
/*  95:    */       }
/*  96:    */     }
/*  97:146 */     switch (1.$SwitchMap$org$boon$core$TypeType[typeEnum.ordinal()])
/*  98:    */     {
/*  99:    */     case 1: 
/* 100:148 */       int value = fieldAccess.getInt(parent);
/* 101:149 */       if ((this.includeDefault) || (include) || (value != 0))
/* 102:    */       {
/* 103:150 */         serializeFieldName(fieldName, builder);
/* 104:151 */         builder.addInt(value);
/* 105:152 */         return true;
/* 106:    */       }
/* 107:154 */       return false;
/* 108:    */     case 2: 
/* 109:156 */       boolean bvalue = fieldAccess.getBoolean(parent);
/* 110:157 */       if ((this.includeDefault) || (include) || (bvalue))
/* 111:    */       {
/* 112:158 */         serializeFieldName(fieldName, builder);
/* 113:159 */         builder.addBoolean(bvalue);
/* 114:160 */         return true;
/* 115:    */       }
/* 116:162 */       return false;
/* 117:    */     case 3: 
/* 118:165 */       byte byvalue = fieldAccess.getByte(parent);
/* 119:166 */       if ((this.includeDefault) || (include) || (byvalue != 0))
/* 120:    */       {
/* 121:167 */         serializeFieldName(fieldName, builder);
/* 122:168 */         builder.addByte(byvalue);
/* 123:169 */         return true;
/* 124:    */       }
/* 125:171 */       return false;
/* 126:    */     case 4: 
/* 127:173 */       long lvalue = fieldAccess.getLong(parent);
/* 128:174 */       if ((this.includeDefault) || (include) || (lvalue != 0L))
/* 129:    */       {
/* 130:175 */         serializeFieldName(fieldName, builder);
/* 131:176 */         builder.addLong(lvalue);
/* 132:177 */         return true;
/* 133:    */       }
/* 134:179 */       return false;
/* 135:    */     case 5: 
/* 136:181 */       double dvalue = fieldAccess.getDouble(parent);
/* 137:182 */       if ((this.includeDefault) || (include) || (dvalue != 0.0D))
/* 138:    */       {
/* 139:183 */         serializeFieldName(fieldName, builder);
/* 140:184 */         builder.addDouble(dvalue);
/* 141:185 */         return true;
/* 142:    */       }
/* 143:187 */       return false;
/* 144:    */     case 6: 
/* 145:189 */       float fvalue = fieldAccess.getFloat(parent);
/* 146:190 */       if ((this.includeDefault) || (include) || (fvalue != 0.0F))
/* 147:    */       {
/* 148:191 */         serializeFieldName(fieldName, builder);
/* 149:192 */         builder.addFloat(fvalue);
/* 150:193 */         return true;
/* 151:    */       }
/* 152:195 */       return false;
/* 153:    */     case 7: 
/* 154:197 */       short svalue = fieldAccess.getShort(parent);
/* 155:198 */       if ((this.includeDefault) || (include) || (svalue != 0))
/* 156:    */       {
/* 157:199 */         serializeFieldName(fieldName, builder);
/* 158:200 */         builder.addShort(svalue);
/* 159:201 */         return true;
/* 160:    */       }
/* 161:203 */       return false;
/* 162:    */     case 8: 
/* 163:205 */       char cvalue = fieldAccess.getChar(parent);
/* 164:206 */       if ((this.includeDefault) || (include) || (cvalue != 0))
/* 165:    */       {
/* 166:207 */         serializeFieldName(fieldName, builder);
/* 167:208 */         builder.addQuoted("" + cvalue);
/* 168:209 */         return true;
/* 169:    */       }
/* 170:211 */       return false;
/* 171:    */     }
/* 172:215 */     Object value = fieldAccess.getObject(parent);
/* 173:217 */     if ((!this.includeNulls) && (!include) && (value == null)) {
/* 174:218 */       return false;
/* 175:    */     }
/* 176:222 */     if (((this.includeNulls) || (fieldAccess.include())) && (value == null))
/* 177:    */     {
/* 178:223 */       serializeFieldName(fieldName, builder);
/* 179:224 */       builder.addNull();
/* 180:225 */       return true;
/* 181:    */     }
/* 182:234 */     switch (1.$SwitchMap$org$boon$core$TypeType[typeEnum.ordinal()])
/* 183:    */     {
/* 184:    */     case 9: 
/* 185:236 */       serializeFieldName(fieldName, builder);
/* 186:237 */       builder.addBigDecimal((BigDecimal)value);
/* 187:238 */       return true;
/* 188:    */     case 10: 
/* 189:240 */       serializeFieldName(fieldName, builder);
/* 190:241 */       builder.addBigInteger((BigInteger)value);
/* 191:242 */       return true;
/* 192:    */     case 11: 
/* 193:244 */       serializeFieldName(fieldName, builder);
/* 194:245 */       serializer.serializeDate((Date)value, builder);
/* 195:246 */       return true;
/* 196:    */     case 12: 
/* 197:248 */       String string = (String)value;
/* 198:249 */       if ((this.includeEmpty) || (include) || (string.length() > 0))
/* 199:    */       {
/* 200:250 */         serializeFieldName(fieldName, builder);
/* 201:251 */         serializer.serializeString(string, builder);
/* 202:252 */         return true;
/* 203:    */       }
/* 204:254 */       return false;
/* 205:    */     case 13: 
/* 206:256 */       serializeFieldName(fieldName, builder);
/* 207:257 */       builder.addQuoted(((Class)value).getName());
/* 208:258 */       return true;
/* 209:    */     case 14: 
/* 210:262 */       serializeFieldName(fieldName, builder);
/* 211:263 */       TimeZone zone = (TimeZone)value;
/* 212:    */       
/* 213:265 */       builder.addQuoted(zone.getID());
/* 214:266 */       return true;
/* 215:    */     case 15: 
/* 216:268 */       String s2 = value.toString();
/* 217:269 */       if ((this.includeEmpty) || (include) || (s2.length() > 0))
/* 218:    */       {
/* 219:270 */         serializeFieldName(fieldName, builder);
/* 220:271 */         serializer.serializeString(s2, builder);
/* 221:272 */         return true;
/* 222:    */       }
/* 223:274 */       return false;
/* 224:    */     case 16: 
/* 225:276 */       serializeFieldName(fieldName, builder);
/* 226:277 */       builder.addInt((Integer)value);
/* 227:278 */       return true;
/* 228:    */     case 17: 
/* 229:280 */       serializeFieldName(fieldName, builder);
/* 230:281 */       builder.addLong((Long)value);
/* 231:282 */       return true;
/* 232:    */     case 18: 
/* 233:284 */       serializeFieldName(fieldName, builder);
/* 234:285 */       builder.addFloat((Float)value);
/* 235:286 */       return true;
/* 236:    */     case 19: 
/* 237:288 */       serializeFieldName(fieldName, builder);
/* 238:289 */       builder.addDouble((Double)value);
/* 239:290 */       return true;
/* 240:    */     case 20: 
/* 241:292 */       serializeFieldName(fieldName, builder);
/* 242:293 */       builder.addShort(((Short)value).shortValue());
/* 243:294 */       return true;
/* 244:    */     case 21: 
/* 245:296 */       serializeFieldName(fieldName, builder);
/* 246:297 */       builder.addByte(((Byte)value).byteValue());
/* 247:298 */       return true;
/* 248:    */     case 22: 
/* 249:300 */       serializeFieldName(fieldName, builder);
/* 250:301 */       builder.addQuoted(value.toString());
/* 251:302 */       return true;
/* 252:    */     case 23: 
/* 253:304 */       serializeFieldName(fieldName, builder);
/* 254:305 */       builder.addQuoted(value.toString());
/* 255:306 */       return true;
/* 256:    */     case 24: 
/* 257:    */     case 25: 
/* 258:    */     case 26: 
/* 259:310 */       Collection collection = (Collection)value;
/* 260:311 */       if ((this.includeEmpty) || (include) || (collection.size() > 0))
/* 261:    */       {
/* 262:312 */         serializeFieldName(fieldName, builder);
/* 263:313 */         serializer.serializeCollection(collection, builder);
/* 264:314 */         return true;
/* 265:    */       }
/* 266:316 */       return false;
/* 267:    */     case 27: 
/* 268:318 */       Map map = (Map)value;
/* 269:319 */       if ((this.includeEmpty) || (include) || (map.size() > 0))
/* 270:    */       {
/* 271:320 */         serializeFieldName(fieldName, builder);
/* 272:321 */         serializer.serializeMap(map, builder);
/* 273:322 */         return true;
/* 274:    */       }
/* 275:324 */       return false;
/* 276:    */     case 28: 
/* 277:    */     case 29: 
/* 278:    */     case 30: 
/* 279:    */     case 31: 
/* 280:    */     case 32: 
/* 281:    */     case 33: 
/* 282:    */     case 34: 
/* 283:    */     case 35: 
/* 284:    */     case 36: 
/* 285:336 */       if ((this.includeEmpty) || (include) || (Arry.len(value) > 0))
/* 286:    */       {
/* 287:337 */         serializeFieldName(fieldName, builder);
/* 288:338 */         serializer.serializeArray(value, builder);
/* 289:339 */         return true;
/* 290:    */       }
/* 291:341 */       return false;
/* 292:    */     case 37: 
/* 293:    */     case 38: 
/* 294:345 */       if ((this.handleSimpleBackReference) && (value == parent)) {
/* 295:346 */         return false;
/* 296:    */       }
/* 297:347 */       if (this.handleComplexBackReference)
/* 298:    */       {
/* 299:348 */         if (this.idMap.containsKey(value)) {
/* 300:349 */           return false;
/* 301:    */         }
/* 302:351 */         this.idMap.put(value, value);
/* 303:    */       }
/* 304:355 */       serializeFieldName(fieldName, builder);
/* 305:357 */       if (this.overrideMap != null) {
/* 306:359 */         SerializeUtils.handleInstance(serializer, value, builder, this.overrideMap, this.noHandle, false, typeEnum);
/* 307:    */       } else {
/* 308:363 */         serializer.serializeSubtypeInstance(value, builder);
/* 309:    */       }
/* 310:366 */       return true;
/* 311:    */     case 39: 
/* 312:368 */       if ((this.handleSimpleBackReference) && (value == parent)) {
/* 313:369 */         return false;
/* 314:    */       }
/* 315:370 */       if (this.handleComplexBackReference)
/* 316:    */       {
/* 317:371 */         if (this.idMap.containsKey(value)) {
/* 318:372 */           return false;
/* 319:    */         }
/* 320:374 */         this.idMap.put(value, value);
/* 321:    */       }
/* 322:378 */       serializeFieldName(fieldName, builder);
/* 323:381 */       if (this.overrideMap != null) {
/* 324:383 */         SerializeUtils.handleInstance(serializer, value, builder, this.overrideMap, this.noHandle, false, typeEnum);
/* 325:387 */       } else if (fieldAccess.type() == value.getClass()) {
/* 326:388 */         serializer.serializeInstance(value, builder);
/* 327:    */       } else {
/* 328:390 */         serializer.serializeSubtypeInstance(value, builder);
/* 329:    */       }
/* 330:395 */       return true;
/* 331:    */     case 40: 
/* 332:398 */       serializeFieldName(fieldName, builder);
/* 333:399 */       builder.addCurrency((Currency)value);
/* 334:400 */       return true;
/* 335:    */     }
/* 336:403 */     serializeFieldName(fieldName, builder);
/* 337:404 */     serializer.serializeUnknown(value, builder);
/* 338:405 */     return true;
/* 339:    */   }
/* 340:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.FieldSerializerUseAnnotationsImpl
 * JD-Core Version:    0.7.0.1
 */