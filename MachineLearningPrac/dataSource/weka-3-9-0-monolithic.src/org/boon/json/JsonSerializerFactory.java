/*   1:    */ package org.boon.json;
/*   2:    */ 
/*   3:    */ import java.util.List;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.concurrent.ConcurrentHashMap;
/*   6:    */ import java.util.concurrent.CopyOnWriteArrayList;
/*   7:    */ import org.boon.core.reflection.fields.FieldAccessMode;
/*   8:    */ import org.boon.core.reflection.fields.FieldFieldsAccessor;
/*   9:    */ import org.boon.core.reflection.fields.FieldsAccessor;
/*  10:    */ import org.boon.core.reflection.fields.FieldsAccessorFieldThenProp;
/*  11:    */ import org.boon.core.reflection.fields.FieldsAccessorsPropertyThenField;
/*  12:    */ import org.boon.core.reflection.fields.PropertyFieldAccessor;
/*  13:    */ import org.boon.json.serializers.ArraySerializer;
/*  14:    */ import org.boon.json.serializers.CollectionSerializer;
/*  15:    */ import org.boon.json.serializers.CustomFieldSerializer;
/*  16:    */ import org.boon.json.serializers.CustomObjectSerializer;
/*  17:    */ import org.boon.json.serializers.DateSerializer;
/*  18:    */ import org.boon.json.serializers.FieldFilter;
/*  19:    */ import org.boon.json.serializers.FieldSerializer;
/*  20:    */ import org.boon.json.serializers.InstanceSerializer;
/*  21:    */ import org.boon.json.serializers.MapSerializer;
/*  22:    */ import org.boon.json.serializers.ObjectSerializer;
/*  23:    */ import org.boon.json.serializers.StringSerializer;
/*  24:    */ import org.boon.json.serializers.UnknownSerializer;
/*  25:    */ import org.boon.json.serializers.impl.BasicObjectSerializerImpl;
/*  26:    */ import org.boon.json.serializers.impl.CollectionSerializerImpl;
/*  27:    */ import org.boon.json.serializers.impl.CustomObjectSerializerImpl;
/*  28:    */ import org.boon.json.serializers.impl.DateSerializerImpl;
/*  29:    */ import org.boon.json.serializers.impl.FieldSerializerImpl;
/*  30:    */ import org.boon.json.serializers.impl.FieldSerializerUseAnnotationsImpl;
/*  31:    */ import org.boon.json.serializers.impl.InstanceSerializerImpl;
/*  32:    */ import org.boon.json.serializers.impl.JsonDateSerializer;
/*  33:    */ import org.boon.json.serializers.impl.JsonSerializerImpl;
/*  34:    */ import org.boon.json.serializers.impl.JsonSimpleSerializerImpl;
/*  35:    */ import org.boon.json.serializers.impl.MapSerializerImpl;
/*  36:    */ import org.boon.json.serializers.impl.StringSerializerImpl;
/*  37:    */ import org.boon.json.serializers.impl.UnknownSerializerImpl;
/*  38:    */ 
/*  39:    */ public class JsonSerializerFactory
/*  40:    */ {
/*  41: 44 */   private boolean outputType = false;
/*  42: 46 */   private FieldAccessMode fieldAccessType = FieldAccessMode.FIELD;
/*  43: 47 */   private boolean includeNulls = false;
/*  44: 48 */   private boolean useAnnotations = false;
/*  45: 49 */   private boolean includeEmpty = false;
/*  46: 50 */   private boolean jsonFormatForDates = false;
/*  47: 51 */   private boolean handleSimpleBackReference = true;
/*  48: 52 */   private boolean handleComplexBackReference = false;
/*  49: 53 */   private boolean includeDefault = false;
/*  50: 54 */   private boolean cacheInstances = true;
/*  51: 55 */   private boolean encodeStrings = true;
/*  52: 56 */   private boolean serializeAsSupport = true;
/*  53: 57 */   private boolean asciiOnly = true;
/*  54:    */   private String view;
/*  55: 60 */   private List<FieldFilter> filterProperties = null;
/*  56: 61 */   private List<CustomFieldSerializer> customFieldSerializers = null;
/*  57: 62 */   private Map<Class, CustomObjectSerializer> customObjectSerializers = null;
/*  58:    */   
/*  59:    */   public JsonSerializer create()
/*  60:    */   {
/*  61: 67 */     if ((!this.outputType) && (!this.includeEmpty) && (!this.includeNulls) && (!this.useAnnotations) && (!this.jsonFormatForDates) && (this.handleSimpleBackReference) && (!this.handleComplexBackReference) && (!this.includeDefault) && (this.filterProperties == null) && (this.customFieldSerializers == null) && (this.customObjectSerializers == null) && (this.fieldAccessType == FieldAccessMode.FIELD)) {
/*  62: 72 */       return new JsonSimpleSerializerImpl(this.view, this.encodeStrings, this.serializeAsSupport, this.asciiOnly);
/*  63:    */     }
/*  64: 88 */     InstanceSerializer instanceSerializer = new InstanceSerializerImpl();
/*  65:    */     ObjectSerializer objectSerializer;
/*  66:    */     ObjectSerializer objectSerializer;
/*  67: 90 */     if (this.customObjectSerializers != null) {
/*  68: 92 */       objectSerializer = new CustomObjectSerializerImpl(this.outputType, this.customObjectSerializers, this.includeNulls);
/*  69:    */     } else {
/*  70: 94 */       objectSerializer = new BasicObjectSerializerImpl(this.includeNulls, this.outputType);
/*  71:    */     }
/*  72: 98 */     StringSerializer stringSerializer = new StringSerializerImpl(this.encodeStrings, this.asciiOnly);
/*  73: 99 */     MapSerializer mapSerializer = new MapSerializerImpl(this.includeNulls);
/*  74:    */     FieldSerializer fieldSerializer;
/*  75:    */     FieldSerializer fieldSerializer;
/*  76:101 */     if ((this.useAnnotations) || (this.includeNulls) || (this.includeEmpty) || (this.handleComplexBackReference) || (!this.includeDefault) || (this.view != null)) {
/*  77:103 */       fieldSerializer = new FieldSerializerUseAnnotationsImpl(this.includeNulls, this.includeDefault, this.useAnnotations, this.includeEmpty, this.handleSimpleBackReference, this.handleComplexBackReference, this.customObjectSerializers, this.filterProperties, null, this.customFieldSerializers, this.view);
/*  78:    */     } else {
/*  79:114 */       fieldSerializer = new FieldSerializerImpl();
/*  80:    */     }
/*  81:116 */     CollectionSerializer collectionSerializer = new CollectionSerializerImpl();
/*  82:117 */     ArraySerializer arraySerializer = (ArraySerializer)collectionSerializer;
/*  83:118 */     UnknownSerializer unknownSerializer = new UnknownSerializerImpl();
/*  84:    */     DateSerializer dateSerializer;
/*  85:    */     DateSerializer dateSerializer;
/*  86:120 */     if (this.jsonFormatForDates) {
/*  87:121 */       dateSerializer = new JsonDateSerializer();
/*  88:    */     } else {
/*  89:123 */       dateSerializer = new DateSerializerImpl();
/*  90:    */     }
/*  91:    */     FieldsAccessor fieldsAccessor;
/*  92:128 */     switch (1.$SwitchMap$org$boon$core$reflection$fields$FieldAccessMode[this.fieldAccessType.ordinal()])
/*  93:    */     {
/*  94:    */     case 1: 
/*  95:130 */       fieldsAccessor = new FieldFieldsAccessor(this.useAnnotations);
/*  96:131 */       break;
/*  97:    */     case 2: 
/*  98:133 */       fieldsAccessor = new PropertyFieldAccessor(this.useAnnotations);
/*  99:134 */       break;
/* 100:    */     case 3: 
/* 101:136 */       fieldsAccessor = new FieldsAccessorFieldThenProp(this.useAnnotations);
/* 102:137 */       break;
/* 103:    */     case 4: 
/* 104:139 */       fieldsAccessor = new FieldsAccessorsPropertyThenField(this.useAnnotations);
/* 105:140 */       break;
/* 106:    */     default: 
/* 107:142 */       fieldsAccessor = new FieldFieldsAccessor(this.useAnnotations);
/* 108:    */     }
/* 109:146 */     return new JsonSerializerImpl(objectSerializer, stringSerializer, mapSerializer, fieldSerializer, instanceSerializer, collectionSerializer, arraySerializer, unknownSerializer, dateSerializer, fieldsAccessor, this.asciiOnly);
/* 110:    */   }
/* 111:    */   
/* 112:    */   public JsonSerializerFactory addFilter(FieldFilter filter)
/* 113:    */   {
/* 114:165 */     if (this.filterProperties == null) {
/* 115:166 */       this.filterProperties = new CopyOnWriteArrayList();
/* 116:    */     }
/* 117:168 */     this.filterProperties.add(filter);
/* 118:169 */     return this;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public JsonSerializerFactory addPropertySerializer(CustomFieldSerializer serializer)
/* 122:    */   {
/* 123:173 */     if (this.customFieldSerializers == null) {
/* 124:174 */       this.customFieldSerializers = new CopyOnWriteArrayList();
/* 125:    */     }
/* 126:176 */     this.customFieldSerializers.add(serializer);
/* 127:177 */     return this;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public JsonSerializerFactory addTypeSerializer(Class<?> type, CustomObjectSerializer serializer)
/* 131:    */   {
/* 132:182 */     if (this.customObjectSerializers == null) {
/* 133:183 */       this.customObjectSerializers = new ConcurrentHashMap();
/* 134:    */     }
/* 135:185 */     this.customObjectSerializers.put(type, serializer);
/* 136:186 */     return this;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public boolean isOutputType()
/* 140:    */   {
/* 141:190 */     return this.outputType;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public JsonSerializerFactory setOutputType(boolean outputType)
/* 145:    */   {
/* 146:194 */     this.outputType = outputType;
/* 147:195 */     return this;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public JsonSerializerFactory outputType()
/* 151:    */   {
/* 152:201 */     this.outputType = true;
/* 153:202 */     return this;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public boolean isUsePropertiesFirst()
/* 157:    */   {
/* 158:206 */     return this.fieldAccessType == FieldAccessMode.PROPERTY_THEN_FIELD;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public JsonSerializerFactory usePropertiesFirst()
/* 162:    */   {
/* 163:211 */     this.fieldAccessType = FieldAccessMode.PROPERTY_THEN_FIELD;
/* 164:212 */     return this;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public boolean isUseFieldsFirst()
/* 168:    */   {
/* 169:216 */     return this.fieldAccessType == FieldAccessMode.FIELD_THEN_PROPERTY;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public JsonSerializerFactory useFieldsFirst()
/* 173:    */   {
/* 174:222 */     this.fieldAccessType = FieldAccessMode.FIELD_THEN_PROPERTY;
/* 175:223 */     return this;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public JsonSerializerFactory useFieldsOnly()
/* 179:    */   {
/* 180:228 */     this.fieldAccessType = FieldAccessMode.FIELD;
/* 181:229 */     return this;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public JsonSerializerFactory usePropertyOnly()
/* 185:    */   {
/* 186:235 */     this.fieldAccessType = FieldAccessMode.PROPERTY;
/* 187:236 */     return this;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public boolean isIncludeNulls()
/* 191:    */   {
/* 192:240 */     return this.includeNulls;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public JsonSerializerFactory setIncludeNulls(boolean includeNulls)
/* 196:    */   {
/* 197:244 */     this.includeNulls = includeNulls;
/* 198:245 */     return this;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public boolean isAsciiOnly()
/* 202:    */   {
/* 203:250 */     return this.asciiOnly;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public JsonSerializerFactory setAsciiOnly(boolean asciiOnly)
/* 207:    */   {
/* 208:255 */     this.asciiOnly = asciiOnly;
/* 209:256 */     return this;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public JsonSerializerFactory asciiOnly()
/* 213:    */   {
/* 214:260 */     this.asciiOnly = true;
/* 215:261 */     return this;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public JsonSerializerFactory includeNulls()
/* 219:    */   {
/* 220:265 */     this.includeNulls = true;
/* 221:266 */     return this;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public boolean isUseAnnotations()
/* 225:    */   {
/* 226:270 */     return this.useAnnotations;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public JsonSerializerFactory setUseAnnotations(boolean useAnnotations)
/* 230:    */   {
/* 231:274 */     this.useAnnotations = useAnnotations;
/* 232:275 */     return this;
/* 233:    */   }
/* 234:    */   
/* 235:    */   public JsonSerializerFactory useAnnotations()
/* 236:    */   {
/* 237:280 */     this.useAnnotations = true;
/* 238:281 */     return this;
/* 239:    */   }
/* 240:    */   
/* 241:    */   public boolean isIncludeEmpty()
/* 242:    */   {
/* 243:286 */     return this.includeEmpty;
/* 244:    */   }
/* 245:    */   
/* 246:    */   public JsonSerializerFactory setIncludeEmpty(boolean includeEmpty)
/* 247:    */   {
/* 248:290 */     this.includeEmpty = includeEmpty;
/* 249:291 */     return this;
/* 250:    */   }
/* 251:    */   
/* 252:    */   public JsonSerializerFactory includeEmpty()
/* 253:    */   {
/* 254:296 */     this.includeEmpty = true;
/* 255:297 */     return this;
/* 256:    */   }
/* 257:    */   
/* 258:    */   public boolean isHandleSimpleBackReference()
/* 259:    */   {
/* 260:301 */     return this.handleSimpleBackReference;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public JsonSerializerFactory setHandleSimpleBackReference(boolean handleSimpleBackReference)
/* 264:    */   {
/* 265:305 */     this.handleSimpleBackReference = handleSimpleBackReference;
/* 266:306 */     return this;
/* 267:    */   }
/* 268:    */   
/* 269:    */   public boolean isHandleComplexBackReference()
/* 270:    */   {
/* 271:310 */     return this.handleComplexBackReference;
/* 272:    */   }
/* 273:    */   
/* 274:    */   public JsonSerializerFactory setHandleComplexBackReference(boolean handleComplexBackReference)
/* 275:    */   {
/* 276:314 */     this.handleComplexBackReference = handleComplexBackReference;
/* 277:315 */     return this;
/* 278:    */   }
/* 279:    */   
/* 280:    */   public JsonSerializerFactory handleComplexBackReference()
/* 281:    */   {
/* 282:320 */     this.handleComplexBackReference = true;
/* 283:321 */     return this;
/* 284:    */   }
/* 285:    */   
/* 286:    */   public boolean isJsonFormatForDates()
/* 287:    */   {
/* 288:326 */     return this.jsonFormatForDates;
/* 289:    */   }
/* 290:    */   
/* 291:    */   public JsonSerializerFactory setJsonFormatForDates(boolean jsonFormatForDates)
/* 292:    */   {
/* 293:330 */     this.jsonFormatForDates = jsonFormatForDates;
/* 294:331 */     return this;
/* 295:    */   }
/* 296:    */   
/* 297:    */   public JsonSerializerFactory useJsonFormatForDates()
/* 298:    */   {
/* 299:336 */     this.jsonFormatForDates = true;
/* 300:337 */     return this;
/* 301:    */   }
/* 302:    */   
/* 303:    */   public boolean isIncludeDefault()
/* 304:    */   {
/* 305:342 */     return this.includeDefault;
/* 306:    */   }
/* 307:    */   
/* 308:    */   public JsonSerializerFactory setIncludeDefault(boolean includeDefault)
/* 309:    */   {
/* 310:346 */     this.includeDefault = includeDefault;
/* 311:347 */     return this;
/* 312:    */   }
/* 313:    */   
/* 314:    */   public JsonSerializerFactory includeDefaultValues()
/* 315:    */   {
/* 316:352 */     this.includeDefault = true;
/* 317:353 */     return this;
/* 318:    */   }
/* 319:    */   
/* 320:    */   public boolean isCacheInstances()
/* 321:    */   {
/* 322:358 */     return this.cacheInstances;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public JsonSerializerFactory setCacheInstances(boolean cacheInstances)
/* 326:    */   {
/* 327:362 */     this.cacheInstances = cacheInstances;
/* 328:363 */     return this;
/* 329:    */   }
/* 330:    */   
/* 331:    */   public JsonSerializerFactory usedCacheInstances()
/* 332:    */   {
/* 333:368 */     this.cacheInstances = true;
/* 334:369 */     return this;
/* 335:    */   }
/* 336:    */   
/* 337:    */   public String getView()
/* 338:    */   {
/* 339:373 */     return this.view;
/* 340:    */   }
/* 341:    */   
/* 342:    */   public JsonSerializerFactory setView(String view)
/* 343:    */   {
/* 344:377 */     this.view = view;
/* 345:378 */     return this;
/* 346:    */   }
/* 347:    */   
/* 348:    */   public boolean isEncodeStrings()
/* 349:    */   {
/* 350:382 */     return this.encodeStrings;
/* 351:    */   }
/* 352:    */   
/* 353:    */   public JsonSerializerFactory setEncodeStrings(boolean encodeStrings)
/* 354:    */   {
/* 355:386 */     this.encodeStrings = encodeStrings;
/* 356:387 */     return this;
/* 357:    */   }
/* 358:    */   
/* 359:    */   public boolean isSerializeAsSupport()
/* 360:    */   {
/* 361:391 */     return this.serializeAsSupport;
/* 362:    */   }
/* 363:    */   
/* 364:    */   public JsonSerializerFactory setSerializeAsSupport(boolean serializeAsSupport)
/* 365:    */   {
/* 366:395 */     this.serializeAsSupport = serializeAsSupport;
/* 367:396 */     return this;
/* 368:    */   }
/* 369:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.JsonSerializerFactory
 * JD-Core Version:    0.7.0.1
 */