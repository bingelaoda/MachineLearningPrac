/*   1:    */ package org.boon.core.reflection;
/*   2:    */ 
/*   3:    */ import java.beans.BeanInfo;
/*   4:    */ import java.beans.IntrospectionException;
/*   5:    */ import java.beans.Introspector;
/*   6:    */ import java.beans.PropertyDescriptor;
/*   7:    */ import java.lang.annotation.Annotation;
/*   8:    */ import java.lang.ref.WeakReference;
/*   9:    */ import java.lang.reflect.Constructor;
/*  10:    */ import java.lang.reflect.Field;
/*  11:    */ import java.lang.reflect.Method;
/*  12:    */ import java.util.ArrayList;
/*  13:    */ import java.util.Collection;
/*  14:    */ import java.util.Collections;
/*  15:    */ import java.util.HashMap;
/*  16:    */ import java.util.List;
/*  17:    */ import java.util.Map;
/*  18:    */ import java.util.Set;
/*  19:    */ import java.util.concurrent.ConcurrentHashMap;
/*  20:    */ import org.boon.Boon;
/*  21:    */ import org.boon.Exceptions;
/*  22:    */ import org.boon.core.Sys;
/*  23:    */ 
/*  24:    */ public class Annotations
/*  25:    */ {
/*  26:    */   private static final Context _context;
/*  27: 52 */   private static WeakReference<Context> weakContext = new WeakReference(null);
/*  28:    */   
/*  29:    */   private static class Context
/*  30:    */   {
/*  31: 57 */     private Map<Class<?>, Map<String, List<AnnotationData>>> annotationDataCacheProperty = new ConcurrentHashMap();
/*  32: 64 */     private Map<Class<?>, Map<String, List<AnnotationData>>> annotationDataCacheField = new ConcurrentHashMap();
/*  33: 70 */     private Map<Class<?>, List<AnnotationData>> annotationDataCacheClass = new ConcurrentHashMap();
/*  34: 74 */     private Map<Class<?>, Map<String, AnnotationData>> annotationDataCacheClassAsMap = new ConcurrentHashMap();
/*  35:    */   }
/*  36:    */   
/*  37:    */   static
/*  38:    */   {
/*  39: 85 */     boolean noStatics = Boolean.getBoolean("org.boon.noStatics");
/*  40: 86 */     if ((noStatics) || (Sys.inContainer()))
/*  41:    */     {
/*  42: 88 */       _context = null;
/*  43: 89 */       weakContext = new WeakReference(new Context(null));
/*  44:    */     }
/*  45:    */     else
/*  46:    */     {
/*  47: 92 */       _context = new Context(null);
/*  48:    */     }
/*  49:    */   }
/*  50:    */   
/*  51:    */   private static Context context()
/*  52:    */   {
/*  53:101 */     if (_context != null) {
/*  54:102 */       return _context;
/*  55:    */     }
/*  56:104 */     Context context = (Context)weakContext.get();
/*  57:105 */     if (context == null)
/*  58:    */     {
/*  59:106 */       context = new Context(null);
/*  60:107 */       weakContext = new WeakReference(context);
/*  61:    */     }
/*  62:109 */     return context;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public static List<AnnotationData> getAnnotationDataForProperty(Class<?> clazz, String propertyName, boolean useReadMethod, Set<String> allowedPackages)
/*  66:    */   {
/*  67:115 */     Map<Class<?>, Map<String, List<AnnotationData>>> cacheProperty = context().annotationDataCacheProperty;
/*  68:    */     
/*  69:117 */     Map<String, List<AnnotationData>> classMap = (Map)cacheProperty.get(clazz);
/*  70:119 */     if (classMap == null)
/*  71:    */     {
/*  72:120 */       classMap = new ConcurrentHashMap();
/*  73:121 */       cacheProperty.put(clazz, classMap);
/*  74:    */     }
/*  75:124 */     List<AnnotationData> annotationDataList = (List)classMap.get(propertyName);
/*  76:125 */     if (annotationDataList == null)
/*  77:    */     {
/*  78:127 */       annotationDataList = extractValidationAnnotationData(extractAllAnnotationsForProperty(clazz, propertyName, useReadMethod), allowedPackages);
/*  79:128 */       if (annotationDataList == null) {
/*  80:129 */         annotationDataList = Collections.EMPTY_LIST;
/*  81:    */       }
/*  82:131 */       classMap.put(propertyName, annotationDataList);
/*  83:    */     }
/*  84:135 */     return annotationDataList;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public static List<AnnotationData> getAnnotationDataForField(Class<?> clazz, String propertyName, Set<String> allowedPackages)
/*  88:    */   {
/*  89:141 */     Map<Class<?>, Map<String, List<AnnotationData>>> cacheProperty = context().annotationDataCacheField;
/*  90:    */     
/*  91:143 */     Map<String, List<AnnotationData>> classMap = (Map)cacheProperty.get(clazz);
/*  92:145 */     if (classMap == null)
/*  93:    */     {
/*  94:146 */       classMap = new ConcurrentHashMap();
/*  95:147 */       cacheProperty.put(clazz, classMap);
/*  96:    */     }
/*  97:150 */     List<AnnotationData> annotationDataList = (List)classMap.get(propertyName);
/*  98:151 */     if (annotationDataList == null)
/*  99:    */     {
/* 100:153 */       annotationDataList = extractValidationAnnotationData(findFieldAnnotations(clazz, propertyName), allowedPackages);
/* 101:155 */       if (annotationDataList == null) {
/* 102:156 */         annotationDataList = Collections.EMPTY_LIST;
/* 103:    */       }
/* 104:158 */       classMap.put(propertyName, annotationDataList);
/* 105:    */     }
/* 106:162 */     return annotationDataList;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public static List<AnnotationData> getAnnotationDataForClass(Class<?> clazz)
/* 110:    */   {
/* 111:167 */     return getAnnotationDataForClass(clazz, Collections.EMPTY_SET);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public static List<AnnotationData> getAnnotationDataForMethod(Method method)
/* 115:    */   {
/* 116:174 */     List<AnnotationData> list = extractValidationAnnotationData(method.getDeclaredAnnotations(), Collections.EMPTY_SET);
/* 117:175 */     return list;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static List<List<AnnotationData>> getAnnotationDataForMethodParams(Method method)
/* 121:    */   {
/* 122:182 */     Annotation[][] parameterAnnotations = method.getParameterAnnotations();
/* 123:    */     
/* 124:184 */     List<List<AnnotationData>> parameterAnnotationsList = new ArrayList(parameterAnnotations.length);
/* 125:186 */     for (Annotation[] paramAnnotaions : parameterAnnotations)
/* 126:    */     {
/* 127:187 */       List<AnnotationData> list = extractValidationAnnotationData(paramAnnotaions, Collections.EMPTY_SET);
/* 128:188 */       parameterAnnotationsList.add(list);
/* 129:    */     }
/* 130:192 */     return parameterAnnotationsList;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public static List<AnnotationData> getAnnotationDataForMethod(Constructor method)
/* 134:    */   {
/* 135:196 */     List<AnnotationData> list = extractValidationAnnotationData(method.getDeclaredAnnotations(), Collections.EMPTY_SET);
/* 136:197 */     return list;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public static Map<String, AnnotationData> getAnnotationDataForClassAsMap(Class<?> clazz)
/* 140:    */   {
/* 141:203 */     Map<Class<?>, Map<String, AnnotationData>> cache = context().annotationDataCacheClassAsMap;
/* 142:    */     
/* 143:205 */     Map<String, AnnotationData> map = (Map)cache.get(clazz);
/* 144:207 */     if (map == null)
/* 145:    */     {
/* 146:208 */       List<AnnotationData> list = getAnnotationDataForClass(clazz);
/* 147:210 */       if (list.size() == 0)
/* 148:    */       {
/* 149:211 */         map = Collections.EMPTY_MAP;
/* 150:    */       }
/* 151:    */       else
/* 152:    */       {
/* 153:213 */         map = new ConcurrentHashMap(list.size());
/* 154:215 */         for (AnnotationData data : list)
/* 155:    */         {
/* 156:216 */           map.put(data.getFullClassName(), data);
/* 157:217 */           map.put(data.getSimpleClassName(), data);
/* 158:218 */           map.put(data.getName(), data);
/* 159:    */         }
/* 160:    */       }
/* 161:221 */       cache.put(clazz, map);
/* 162:    */     }
/* 163:223 */     return map;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public static List<AnnotationData> getAnnotationDataForClass(Class<?> clazz, Set<String> allowedPackages)
/* 167:    */   {
/* 168:228 */     Map<Class<?>, List<AnnotationData>> cache = context().annotationDataCacheClass;
/* 169:    */     
/* 170:230 */     List<AnnotationData> annotationDataList = (List)cache.get(clazz);
/* 171:231 */     if (annotationDataList == null)
/* 172:    */     {
/* 173:232 */       annotationDataList = extractValidationAnnotationData(findClassAnnotations(clazz), allowedPackages);
/* 174:233 */       cache.put(clazz, annotationDataList);
/* 175:    */     }
/* 176:235 */     return annotationDataList;
/* 177:    */   }
/* 178:    */   
/* 179:    */   private static Annotation[] findClassAnnotations(Class<?> clazz)
/* 180:    */   {
/* 181:239 */     return clazz.getAnnotations();
/* 182:    */   }
/* 183:    */   
/* 184:    */   public static Collection<AnnotationData> getAnnotationDataForFieldAndProperty(Class<?> clazz, String propertyName, Set<String> allowedPackages)
/* 185:    */   {
/* 186:244 */     List<AnnotationData> propertyAnnotationDataList = getAnnotationDataForProperty(clazz, propertyName, false, allowedPackages);
/* 187:    */     
/* 188:    */ 
/* 189:    */ 
/* 190:248 */     List<AnnotationData> fieldAnnotationDataList = getAnnotationDataForField(clazz, propertyName, allowedPackages);
/* 191:    */     
/* 192:    */ 
/* 193:    */ 
/* 194:252 */     Map<String, AnnotationData> map = new HashMap(propertyAnnotationDataList.size() + fieldAnnotationDataList.size());
/* 195:255 */     for (AnnotationData annotationData : propertyAnnotationDataList) {
/* 196:256 */       map.put(annotationData.getName(), annotationData);
/* 197:    */     }
/* 198:260 */     for (AnnotationData annotationData : fieldAnnotationDataList) {
/* 199:261 */       map.put(annotationData.getName(), annotationData);
/* 200:    */     }
/* 201:263 */     return map.values();
/* 202:    */   }
/* 203:    */   
/* 204:    */   public static List<AnnotationData> extractValidationAnnotationData(Annotation[] annotations, Set<String> allowedPackages)
/* 205:    */   {
/* 206:275 */     List<AnnotationData> annotationsList = new ArrayList();
/* 207:276 */     for (Annotation annotation : annotations)
/* 208:    */     {
/* 209:277 */       AnnotationData annotationData = new AnnotationData(annotation, allowedPackages);
/* 210:278 */       if (annotationData.isAllowed()) {
/* 211:279 */         annotationsList.add(annotationData);
/* 212:    */       }
/* 213:    */     }
/* 214:282 */     return annotationsList;
/* 215:    */   }
/* 216:    */   
/* 217:    */   private static Annotation[] extractAllAnnotationsForProperty(Class<?> clazz, String propertyName, boolean useRead)
/* 218:    */   {
/* 219:    */     try
/* 220:    */     {
/* 221:298 */       Annotation[] annotations = findPropertyAnnotations(clazz, propertyName, useRead);
/* 222:303 */       if (annotations.length == 0) {}
/* 223:304 */       return findPropertyAnnotations(clazz.getSuperclass(), propertyName, useRead);
/* 224:    */     }
/* 225:    */     catch (Exception ex)
/* 226:    */     {
/* 227:308 */       return (Annotation[])Exceptions.handle([Ljava.lang.annotation.Annotation.class, Boon.sputs(new Object[] { "Unable to extract annotation for property", propertyName, " of class ", clazz, "  useRead ", Boolean.valueOf(useRead) }), ex);
/* 228:    */     }
/* 229:    */   }
/* 230:    */   
/* 231:    */   private static Annotation[] findPropertyAnnotations(Class<?> clazz, String propertyName, boolean useRead)
/* 232:    */     throws IntrospectionException
/* 233:    */   {
/* 234:331 */     PropertyDescriptor propertyDescriptor = getPropertyDescriptor(clazz, propertyName);
/* 235:332 */     if (propertyDescriptor == null) {
/* 236:333 */       return new Annotation[0];
/* 237:    */     }
/* 238:335 */     Method accessMethod = null;
/* 239:337 */     if (useRead) {
/* 240:338 */       accessMethod = propertyDescriptor.getReadMethod();
/* 241:    */     } else {
/* 242:340 */       accessMethod = propertyDescriptor.getWriteMethod();
/* 243:    */     }
/* 244:343 */     if (accessMethod != null)
/* 245:    */     {
/* 246:344 */       Annotation[] annotations = accessMethod.getAnnotations();
/* 247:345 */       return annotations;
/* 248:    */     }
/* 249:347 */     return new Annotation[0];
/* 250:    */   }
/* 251:    */   
/* 252:    */   private static PropertyDescriptor getPropertyDescriptor(Class<?> type, String propertyName)
/* 253:    */   {
/* 254:357 */     Exceptions.requireNonNull(type);
/* 255:358 */     Exceptions.requireNonNull(propertyName);
/* 256:360 */     if (!propertyName.contains(".")) {
/* 257:361 */       return doGetPropertyDescriptor(type, propertyName);
/* 258:    */     }
/* 259:363 */     String[] propertyNames = propertyName.split("[.]");
/* 260:364 */     Class<?> clazz = type;
/* 261:365 */     PropertyDescriptor propertyDescriptor = null;
/* 262:366 */     for (String pName : propertyNames)
/* 263:    */     {
/* 264:367 */       propertyDescriptor = doGetPropertyDescriptor(clazz, pName);
/* 265:368 */       if (propertyDescriptor == null) {
/* 266:369 */         return null;
/* 267:    */       }
/* 268:371 */       clazz = propertyDescriptor.getPropertyType();
/* 269:    */     }
/* 270:373 */     return propertyDescriptor;
/* 271:    */   }
/* 272:    */   
/* 273:    */   private static Annotation[] findFieldAnnotations(Class<?> clazz, String propertyName)
/* 274:    */   {
/* 275:379 */     Field field = getField(clazz, propertyName);
/* 276:381 */     if (field == null) {
/* 277:382 */       return new Annotation[0];
/* 278:    */     }
/* 279:384 */     Annotation[] annotations = field.getAnnotations();
/* 280:385 */     return annotations;
/* 281:    */   }
/* 282:    */   
/* 283:    */   private static PropertyDescriptor doGetPropertyDescriptor(Class<?> type, String propertyName)
/* 284:    */   {
/* 285:    */     try
/* 286:    */     {
/* 287:394 */       BeanInfo beanInfo = Introspector.getBeanInfo(type);
/* 288:395 */       PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
/* 289:396 */       for (PropertyDescriptor pd : propertyDescriptors) {
/* 290:397 */         if (pd.getName().equals(propertyName)) {
/* 291:398 */           return pd;
/* 292:    */         }
/* 293:    */       }
/* 294:401 */       Class<?> superclass = type.getSuperclass();
/* 295:402 */       if (superclass != null) {
/* 296:403 */         return doGetPropertyDescriptor(superclass, propertyName);
/* 297:    */       }
/* 298:405 */       return null;
/* 299:    */     }
/* 300:    */     catch (Exception ex)
/* 301:    */     {
/* 302:408 */       throw new RuntimeException("Unable to get property " + propertyName + " for class " + type, ex);
/* 303:    */     }
/* 304:    */   }
/* 305:    */   
/* 306:    */   private static Field getField(Class<?> type, String fieldName)
/* 307:    */   {
/* 308:415 */     if (!fieldName.contains(".")) {
/* 309:416 */       return doFindFieldInHeirarchy(type, fieldName);
/* 310:    */     }
/* 311:418 */     String[] fieldNames = fieldName.split("[.]");
/* 312:419 */     Class<?> clazz = type;
/* 313:420 */     Field field = null;
/* 314:421 */     for (String fName : fieldNames)
/* 315:    */     {
/* 316:422 */       field = doFindFieldInHeirarchy(clazz, fName);
/* 317:423 */       if (field == null) {
/* 318:424 */         return null;
/* 319:    */       }
/* 320:426 */       clazz = field.getType();
/* 321:    */     }
/* 322:429 */     field.setAccessible(true);
/* 323:430 */     return field;
/* 324:    */   }
/* 325:    */   
/* 326:    */   private static Field doFindFieldInHeirarchy(Class<?> clazz, String propertyName)
/* 327:    */   {
/* 328:436 */     Field field = doGetField(clazz, propertyName);
/* 329:    */     
/* 330:438 */     Class<?> sclazz = clazz.getSuperclass();
/* 331:439 */     if (field == null) {
/* 332:    */       for (;;)
/* 333:    */       {
/* 334:441 */         if (sclazz != null)
/* 335:    */         {
/* 336:442 */           field = doGetField(sclazz, propertyName);
/* 337:443 */           sclazz = sclazz.getSuperclass();
/* 338:    */         }
/* 339:445 */         if (field == null) {
/* 340:448 */           if (sclazz == null) {
/* 341:    */             break;
/* 342:    */           }
/* 343:    */         }
/* 344:    */       }
/* 345:    */     }
/* 346:454 */     if (field != null) {
/* 347:454 */       field.setAccessible(true);
/* 348:    */     }
/* 349:455 */     return field;
/* 350:    */   }
/* 351:    */   
/* 352:    */   private static Field doGetField(Class<?> clazz, String fieldName)
/* 353:    */   {
/* 354:459 */     Field field = null;
/* 355:    */     try
/* 356:    */     {
/* 357:461 */       field = clazz.getDeclaredField(fieldName);
/* 358:    */     }
/* 359:    */     catch (SecurityException se)
/* 360:    */     {
/* 361:463 */       field = null;
/* 362:    */     }
/* 363:    */     catch (NoSuchFieldException nsfe)
/* 364:    */     {
/* 365:465 */       field = null;
/* 366:    */     }
/* 367:467 */     if (field == null)
/* 368:    */     {
/* 369:468 */       Field[] fields = clazz.getDeclaredFields();
/* 370:469 */       for (Field f : fields) {
/* 371:470 */         if (f.getName().equals(fieldName)) {
/* 372:471 */           field = f;
/* 373:    */         }
/* 374:    */       }
/* 375:    */     }
/* 376:475 */     if (field != null) {
/* 377:476 */       field.setAccessible(true);
/* 378:    */     }
/* 379:478 */     return field;
/* 380:    */   }
/* 381:    */   
/* 382:    */   public static Object contextToHold()
/* 383:    */   {
/* 384:484 */     return context();
/* 385:    */   }
/* 386:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.Annotations
 * JD-Core Version:    0.7.0.1
 */