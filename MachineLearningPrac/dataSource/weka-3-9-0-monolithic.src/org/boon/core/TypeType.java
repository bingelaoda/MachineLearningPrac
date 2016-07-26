/*   1:    */ package org.boon.core;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.Set;
/*   8:    */ 
/*   9:    */ public enum TypeType
/*  10:    */ {
/*  11: 37 */   BOOLEAN(false, true),  BYTE(false, true),  SHORT(false, true),  CHAR(false, true),  INT(false, true),  FLOAT(false, true),  LONG(false, true),  DOUBLE(false, true),  LONG_WRAPPER(LONG),  INTEGER_WRAPPER(INT),  SHORT_WRAPPER(SHORT),  CHAR_WRAPPER(CHAR),  BOOLEAN_WRAPPER(BOOLEAN),  BYTE_WRAPPER(BYTE),  FLOAT_WRAPPER(FLOAT),  DOUBLE_WRAPPER(DOUBLE),  TRUE(BOOLEAN),  FALSE(BOOLEAN),  INSTANCE,  NULL,  INTERFACE,  ABSTRACT,  SYSTEM,  VOID,  UNKNOWN,  BASIC_TYPE,  CHAR_SEQUENCE,  NUMBER,  OBJECT,  CLASS,  ENUM,  STRING(CHAR_SEQUENCE),  CALENDAR,  DATE,  URL(BASIC_TYPE),  URI(BASIC_TYPE),  LOCALE(BASIC_TYPE),  TIME_ZONE(BASIC_TYPE),  CURRENCY(BASIC_TYPE),  FILE(BASIC_TYPE),  PATH(BASIC_TYPE),  UUID(BASIC_TYPE),  BIG_INT(NUMBER),  BIG_DECIMAL(NUMBER),  COLLECTION,  LIST(COLLECTION),  SET(COLLECTION),  MAP,  MAP_STRING_OBJECT(MAP),  ARRAY(true),  ARRAY_INT(true, INT),  ARRAY_BYTE(true, SHORT),  ARRAY_SHORT(true, SHORT),  ARRAY_FLOAT(true, FLOAT),  ARRAY_DOUBLE(true, DOUBLE),  ARRAY_LONG(true, LONG),  ARRAY_STRING(true, STRING),  ARRAY_OBJECT(true, OBJECT),  VALUE_MAP,  VALUE,  HANDLER;
/*  12:    */   
/*  13:    */   final TypeType baseTypeOrWrapper;
/*  14:    */   private final boolean array;
/*  15:    */   private final boolean primitive;
/*  16:    */   
/*  17:    */   private TypeType()
/*  18:    */   {
/*  19: 94 */     this.baseTypeOrWrapper = null;
/*  20: 95 */     this.array = false;
/*  21: 96 */     this.primitive = false;
/*  22:    */   }
/*  23:    */   
/*  24:    */   private TypeType(TypeType type)
/*  25:    */   {
/*  26:101 */     this.baseTypeOrWrapper = type;
/*  27:102 */     this.array = false;
/*  28:103 */     this.primitive = false;
/*  29:    */   }
/*  30:    */   
/*  31:    */   private TypeType(boolean isarray)
/*  32:    */   {
/*  33:108 */     this.array = isarray;
/*  34:109 */     this.baseTypeOrWrapper = null;
/*  35:110 */     this.primitive = false;
/*  36:    */   }
/*  37:    */   
/*  38:    */   private TypeType(boolean isarray, TypeType type)
/*  39:    */   {
/*  40:116 */     this.array = isarray;
/*  41:117 */     this.baseTypeOrWrapper = type;
/*  42:118 */     this.primitive = false;
/*  43:    */   }
/*  44:    */   
/*  45:    */   private TypeType(boolean array, boolean primitive)
/*  46:    */   {
/*  47:123 */     this.array = array;
/*  48:124 */     this.primitive = primitive;
/*  49:125 */     this.baseTypeOrWrapper = null;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public static TypeType getInstanceType(Object object)
/*  53:    */   {
/*  54:131 */     if (object == null) {
/*  55:132 */       return NULL;
/*  56:    */     }
/*  57:134 */     return getType(object.getClass(), object);
/*  58:    */   }
/*  59:    */   
/*  60:    */   public static TypeType getType(Class<?> clazz)
/*  61:    */   {
/*  62:141 */     return getType(clazz, null);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public static TypeType getType(Class<?> clazz, Object object)
/*  66:    */   {
/*  67:146 */     String className = clazz.getName();
/*  68:147 */     TypeType type = getType(className);
/*  69:149 */     if (type != UNKNOWN) {
/*  70:150 */       return type;
/*  71:    */     }
/*  72:153 */     if (clazz.isInterface()) {
/*  73:154 */       type = INTERFACE;
/*  74:155 */     } else if (clazz.isEnum()) {
/*  75:156 */       type = ENUM;
/*  76:157 */     } else if (clazz.isArray()) {
/*  77:158 */       type = getArrayType(clazz);
/*  78:159 */     } else if (Typ.isAbstract(clazz)) {
/*  79:160 */       type = ABSTRACT;
/*  80:161 */     } else if (className.startsWith("java"))
/*  81:    */     {
/*  82:162 */       if (Typ.isCharSequence(clazz)) {
/*  83:163 */         type = CHAR_SEQUENCE;
/*  84:164 */       } else if (Typ.isCollection(clazz))
/*  85:    */       {
/*  86:165 */         if (Typ.isList(clazz)) {
/*  87:166 */           type = LIST;
/*  88:167 */         } else if (Typ.isSet(clazz)) {
/*  89:168 */           type = SET;
/*  90:    */         } else {
/*  91:170 */           type = COLLECTION;
/*  92:    */         }
/*  93:    */       }
/*  94:172 */       else if (Typ.isMap(clazz)) {
/*  95:173 */         type = MAP;
/*  96:    */       } else {
/*  97:176 */         type = SYSTEM;
/*  98:    */       }
/*  99:    */     }
/* 100:178 */     else if ((className.startsWith("com.sun")) || (className.startsWith("sun."))) {
/* 101:179 */       type = SYSTEM;
/* 102:180 */     } else if (object != null)
/* 103:    */     {
/* 104:183 */       if ((object instanceof Map))
/* 105:    */       {
/* 106:184 */         type = MAP;
/* 107:    */       }
/* 108:185 */       else if ((object instanceof Collection))
/* 109:    */       {
/* 110:187 */         type = COLLECTION;
/* 111:188 */         if ((object instanceof List)) {
/* 112:189 */           type = LIST;
/* 113:190 */         } else if ((object instanceof Set)) {
/* 114:191 */           type = SET;
/* 115:    */         }
/* 116:    */       }
/* 117:    */       else
/* 118:    */       {
/* 119:194 */         type = INSTANCE;
/* 120:    */       }
/* 121:    */     }
/* 122:    */     else {
/* 123:198 */       type = INSTANCE;
/* 124:    */     }
/* 125:201 */     return type;
/* 126:    */   }
/* 127:    */   
/* 128:    */   private static TypeType getArrayType(Class<?> clazz)
/* 129:    */   {
/* 130:209 */     TypeType componentType = getType(clazz.getComponentType());
/* 131:    */     TypeType type;
/* 132:210 */     switch (1.$SwitchMap$org$boon$core$TypeType[componentType.ordinal()])
/* 133:    */     {
/* 134:    */     case 1: 
/* 135:214 */       type = ARRAY_BYTE;
/* 136:215 */       break;
/* 137:    */     case 2: 
/* 138:218 */       type = ARRAY_SHORT;
/* 139:219 */       break;
/* 140:    */     case 3: 
/* 141:222 */       type = ARRAY_INT;
/* 142:223 */       break;
/* 143:    */     case 4: 
/* 144:226 */       type = ARRAY_FLOAT;
/* 145:227 */       break;
/* 146:    */     case 5: 
/* 147:230 */       type = ARRAY_DOUBLE;
/* 148:231 */       break;
/* 149:    */     case 6: 
/* 150:234 */       type = ARRAY_LONG;
/* 151:235 */       break;
/* 152:    */     case 7: 
/* 153:238 */       type = ARRAY_STRING;
/* 154:239 */       break;
/* 155:    */     case 8: 
/* 156:242 */       type = ARRAY_OBJECT;
/* 157:243 */       break;
/* 158:    */     default: 
/* 159:246 */       type = ARRAY;
/* 160:    */     }
/* 161:250 */     return type;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public static TypeType getType(String typeName)
/* 165:    */   {
/* 166:255 */     switch (typeName)
/* 167:    */     {
/* 168:    */     case "int": 
/* 169:257 */       return INT;
/* 170:    */     case "short": 
/* 171:259 */       return SHORT;
/* 172:    */     case "byte": 
/* 173:261 */       return BYTE;
/* 174:    */     case "float": 
/* 175:263 */       return FLOAT;
/* 176:    */     case "double": 
/* 177:265 */       return DOUBLE;
/* 178:    */     case "boolean": 
/* 179:267 */       return BOOLEAN;
/* 180:    */     case "char": 
/* 181:269 */       return CHAR;
/* 182:    */     case "long": 
/* 183:271 */       return LONG;
/* 184:    */     case "java.lang.String": 
/* 185:274 */       return STRING;
/* 186:    */     case "java.lang.Boolean": 
/* 187:276 */       return BOOLEAN_WRAPPER;
/* 188:    */     case "java.lang.Byte": 
/* 189:278 */       return BYTE_WRAPPER;
/* 190:    */     case "java.lang.Short": 
/* 191:280 */       return SHORT_WRAPPER;
/* 192:    */     case "java.lang.Integer": 
/* 193:282 */       return INTEGER_WRAPPER;
/* 194:    */     case "java.lang.Double": 
/* 195:284 */       return DOUBLE_WRAPPER;
/* 196:    */     case "java.lang.Float": 
/* 197:286 */       return FLOAT_WRAPPER;
/* 198:    */     case "java.lang.Character": 
/* 199:288 */       return CHAR_WRAPPER;
/* 200:    */     case "java.lang.Number": 
/* 201:290 */       return NUMBER;
/* 202:    */     case "java.lang.Class": 
/* 203:293 */       return CLASS;
/* 204:    */     case "java.lang.Void": 
/* 205:299 */       return VOID;
/* 206:    */     case "java.lang.Long": 
/* 207:306 */       return LONG_WRAPPER;
/* 208:    */     case "java.util.Set": 
/* 209:    */     case "java.util.HashSet": 
/* 210:    */     case "java.util.TreeSet": 
/* 211:312 */       return SET;
/* 212:    */     case "java.util.List": 
/* 213:    */     case "java.util.ArrayList": 
/* 214:    */     case "java.util.LinkedList": 
/* 215:    */     case "org.boon.core.value.ValueList": 
/* 216:318 */       return LIST;
/* 217:    */     case "java.util.Map": 
/* 218:    */     case "org.boon.collections.LazyMap": 
/* 219:    */     case "java.util.HashMap": 
/* 220:    */     case "java.util.LinkedHashMap": 
/* 221:    */     case "java.util.TreeMap": 
/* 222:    */     case "org.boon.core.value.LazyValueMap": 
/* 223:326 */       return MAP;
/* 224:    */     case "java.lang.CharSequence": 
/* 225:329 */       return CHAR_SEQUENCE;
/* 226:    */     case "java.math.BigDecimal": 
/* 227:332 */       return BIG_DECIMAL;
/* 228:    */     case "java.math.BigInteger": 
/* 229:334 */       return BIG_INT;
/* 230:    */     case "java.util.Date": 
/* 231:    */     case "java.sql.Date": 
/* 232:    */     case "java.sql.Time": 
/* 233:    */     case "java.sql.Timestamp": 
/* 234:340 */       return DATE;
/* 235:    */     case "java.util.Calendar": 
/* 236:345 */       return CALENDAR;
/* 237:    */     case "org.boon.core.value.ValueMapImpl": 
/* 238:348 */       return VALUE_MAP;
/* 239:    */     case "org.boon.core.value.NumberValue": 
/* 240:    */     case "org.boon.core.value.CharSequenceValue": 
/* 241:352 */       return VALUE;
/* 242:    */     case "org.boon.core.Handler": 
/* 243:355 */       return HANDLER;
/* 244:    */     case "java.lang.Object": 
/* 245:358 */       return OBJECT;
/* 246:    */     case "java.io.File": 
/* 247:361 */       return FILE;
/* 248:    */     case "java.net.URI": 
/* 249:364 */       return URI;
/* 250:    */     case "java.net.URL": 
/* 251:367 */       return URL;
/* 252:    */     case "java.nio.file.Path": 
/* 253:370 */       return PATH;
/* 254:    */     case "java.util.UUID": 
/* 255:373 */       return UUID;
/* 256:    */     case "java.util.Locale": 
/* 257:377 */       return LOCALE;
/* 258:    */     case "java.util.TimeZone": 
/* 259:381 */       return TIME_ZONE;
/* 260:    */     case "java.util.Currency": 
/* 261:384 */       return CURRENCY;
/* 262:    */     }
/* 263:387 */     return UNKNOWN;
/* 264:    */   }
/* 265:    */   
/* 266:    */   public boolean hasLength()
/* 267:    */   {
/* 268:394 */     switch (1.$SwitchMap$org$boon$core$TypeType[ordinal()])
/* 269:    */     {
/* 270:    */     case 7: 
/* 271:    */     case 9: 
/* 272:    */     case 10: 
/* 273:    */     case 11: 
/* 274:    */     case 12: 
/* 275:    */     case 13: 
/* 276:401 */       return true;
/* 277:    */     }
/* 278:403 */     return (isArray()) || (isCollection());
/* 279:    */   }
/* 280:    */   
/* 281:    */   public boolean isCollection()
/* 282:    */   {
/* 283:409 */     switch (1.$SwitchMap$org$boon$core$TypeType[ordinal()])
/* 284:    */     {
/* 285:    */     case 9: 
/* 286:    */     case 12: 
/* 287:    */     case 13: 
/* 288:413 */       return true;
/* 289:    */     }
/* 290:415 */     return false;
/* 291:    */   }
/* 292:    */   
/* 293:    */   public static List<Object> gatherTypes(List<?> list)
/* 294:    */   {
/* 295:423 */     List<Object> types = new ArrayList();
/* 296:425 */     for (Object o : list) {
/* 297:426 */       if ((o instanceof List)) {
/* 298:427 */         types.add(gatherTypes((List)o));
/* 299:    */       } else {
/* 300:430 */         types.add(getInstanceType(o));
/* 301:    */       }
/* 302:    */     }
/* 303:434 */     return types;
/* 304:    */   }
/* 305:    */   
/* 306:    */   public static List<Object> gatherActualTypes(List<?> list)
/* 307:    */   {
/* 308:441 */     List<Object> types = new ArrayList();
/* 309:443 */     for (Object o : list) {
/* 310:444 */       if ((o instanceof List)) {
/* 311:445 */         types.add(gatherActualTypes((List)o));
/* 312:    */       } else {
/* 313:448 */         types.add(getActualType(o));
/* 314:    */       }
/* 315:    */     }
/* 316:452 */     return types;
/* 317:    */   }
/* 318:    */   
/* 319:    */   private static Object getActualType(Object o)
/* 320:    */   {
/* 321:456 */     if (o == null) {
/* 322:457 */       return NULL;
/* 323:    */     }
/* 324:459 */     return o.getClass().getSimpleName();
/* 325:    */   }
/* 326:    */   
/* 327:    */   public static List<TypeType> gatherTypes(Object... list)
/* 328:    */   {
/* 329:465 */     List<TypeType> types = new ArrayList();
/* 330:467 */     for (Object o : list) {
/* 331:468 */       types.add(getInstanceType(o));
/* 332:    */     }
/* 333:471 */     return types;
/* 334:    */   }
/* 335:    */   
/* 336:    */   public TypeType wraps()
/* 337:    */   {
/* 338:475 */     return this.baseTypeOrWrapper;
/* 339:    */   }
/* 340:    */   
/* 341:    */   public TypeType componentType()
/* 342:    */   {
/* 343:480 */     return this.baseTypeOrWrapper == null ? OBJECT : this.baseTypeOrWrapper;
/* 344:    */   }
/* 345:    */   
/* 346:    */   public boolean isArray()
/* 347:    */   {
/* 348:485 */     return this.array;
/* 349:    */   }
/* 350:    */   
/* 351:    */   public boolean isPrimitive()
/* 352:    */   {
/* 353:490 */     return this.primitive;
/* 354:    */   }
/* 355:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.TypeType
 * JD-Core Version:    0.7.0.1
 */