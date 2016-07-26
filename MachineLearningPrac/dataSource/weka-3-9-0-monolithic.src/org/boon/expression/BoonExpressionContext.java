/*   1:    */ package org.boon.expression;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.HashMap;
/*   5:    */ import java.util.LinkedList;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import org.boon.Boon;
/*   9:    */ import org.boon.Pair;
/*  10:    */ import org.boon.Str;
/*  11:    */ import org.boon.StringScanner;
/*  12:    */ import org.boon.core.Conversions;
/*  13:    */ import org.boon.core.reflection.BeanUtils;
/*  14:    */ import org.boon.core.reflection.ClassMeta;
/*  15:    */ import org.boon.core.reflection.MethodAccess;
/*  16:    */ import org.boon.json.JsonFactory;
/*  17:    */ import org.boon.json.JsonParserAndMapper;
/*  18:    */ import org.boon.json.JsonParserFactory;
/*  19:    */ import org.boon.primitive.Arry;
/*  20:    */ 
/*  21:    */ public class BoonExpressionContext
/*  22:    */   implements ExpressionContext
/*  23:    */ {
/*  24:    */   private LinkedList<Object> context;
/*  25:    */   private BoonExpressionContext parent;
/*  26: 57 */   private final JsonParserAndMapper jsonParser = new JsonParserFactory().lax().create();
/*  27: 61 */   protected Map<String, MethodAccess> staticMethodMap = new HashMap(100);
/*  28:    */   
/*  29:    */   public BoonExpressionContext(List<Object> root)
/*  30:    */   {
/*  31: 67 */     this.context = new LinkedList();
/*  32: 69 */     if (root != null) {
/*  33: 70 */       this.context.add(root);
/*  34:    */     }
/*  35: 73 */     addFunctions("fn", StandardFunctions.class);
/*  36:    */     
/*  37: 75 */     this.parent = this;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void addFunctions(String prefix, Class<?> functions)
/*  41:    */   {
/*  42: 81 */     ClassMeta<?> funcs = ClassMeta.classMeta(functions);
/*  43: 84 */     for (MethodAccess m : funcs.methods()) {
/*  44: 85 */       if (m.isStatic())
/*  45:    */       {
/*  46: 86 */         String funcName = Str.add(new String[] { prefix, ":", m.name() });
/*  47: 87 */         this.staticMethodMap.put(funcName, m);
/*  48:    */       }
/*  49:    */     }
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void initContext(Object... array)
/*  53:    */   {
/*  54: 94 */     this.context = new LinkedList();
/*  55: 95 */     if (array == null) {
/*  56: 96 */       return;
/*  57:    */     }
/*  58: 99 */     for (Object root : array) {
/*  59:100 */       if (root != null)
/*  60:    */       {
/*  61:103 */         if ((root instanceof CharSequence))
/*  62:    */         {
/*  63:105 */           String str = root.toString().trim();
/*  64:107 */           if ((str.startsWith("[")) || (str.startsWith("{"))) {
/*  65:108 */             this.context.add(JsonFactory.fromJson(root.toString()));
/*  66:    */           }
/*  67:    */         }
/*  68:    */         else
/*  69:    */         {
/*  70:115 */           this.context.add(root);
/*  71:    */         }
/*  72:118 */         this.parent = this;
/*  73:    */       }
/*  74:    */     }
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void addFunctions(Class<?> functionsClass)
/*  78:    */   {
/*  79:126 */     ClassMeta<?> standardFunctionsClassMeta = ClassMeta.classMeta(functionsClass);
/*  80:129 */     for (MethodAccess m : standardFunctionsClassMeta.methods()) {
/*  81:130 */       if (m.isStatic())
/*  82:    */       {
/*  83:131 */         String funcName = Str.add(new String[] { m.name() });
/*  84:132 */         this.staticMethodMap.put(funcName, m);
/*  85:    */       }
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public BoonExpressionContext(Object... array)
/*  90:    */   {
/*  91:141 */     this.context = new LinkedList();
/*  92:    */     
/*  93:    */ 
/*  94:144 */     initContext(array);
/*  95:    */     
/*  96:146 */     addFunctions("fn", StandardFunctions.class);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public char idxChar(String property)
/* 100:    */   {
/* 101:159 */     return Conversions.toChar(lookup(property));
/* 102:    */   }
/* 103:    */   
/* 104:    */   Object findProperty(String propertyPath, boolean searchChildren)
/* 105:    */   {
/* 106:172 */     String formatRule = null;
/* 107:    */     
/* 108:    */ 
/* 109:175 */     Object outputValue = null;
/* 110:    */     Object defaultValue;
/* 111:178 */     if (propertyPath.indexOf('|') != -1)
/* 112:    */     {
/* 113:180 */       String[] splitByPipe = Str.splitByPipe(propertyPath);
/* 114:181 */       Object defaultValue = splitByPipe[1];
/* 115:182 */       propertyPath = splitByPipe[0];
/* 116:    */     }
/* 117:    */     else
/* 118:    */     {
/* 119:185 */       defaultValue = null;
/* 120:    */     }
/* 121:188 */     if (propertyPath.indexOf('%') != -1)
/* 122:    */     {
/* 123:190 */       String[] splitByPercentSign = StringScanner.split(propertyPath, '%', 1);
/* 124:191 */       formatRule = splitByPercentSign[1];
/* 125:192 */       propertyPath = splitByPercentSign[0];
/* 126:    */     }
/* 127:196 */     for (Object ctx : this.context) {
/* 128:198 */       if ((searchChildren) && ((ctx instanceof BoonExpressionContext)))
/* 129:    */       {
/* 130:199 */         BoonExpressionContext basicContext = (BoonExpressionContext)ctx;
/* 131:200 */         outputValue = basicContext.findProperty(propertyPath, true);
/* 132:201 */         if (outputValue != null) {
/* 133:    */           break;
/* 134:    */         }
/* 135:    */       }
/* 136:204 */       else if ((ctx instanceof Pair))
/* 137:    */       {
/* 138:205 */         Pair<String, Object> pair = (Pair)ctx;
/* 139:206 */         if (((String)pair.getKey()).equals(propertyPath))
/* 140:    */         {
/* 141:207 */           outputValue = pair.getValue();
/* 142:208 */           break;
/* 143:    */         }
/* 144:209 */         if (propertyPath.startsWith((String)pair.getKey()))
/* 145:    */         {
/* 146:211 */           String subPath = StringScanner.substringAfter(propertyPath, (String)pair.getKey());
/* 147:    */           
/* 148:    */ 
/* 149:214 */           Object o = pair.getValue();
/* 150:215 */           outputValue = BeanUtils.idx(o, subPath);
/* 151:216 */           break;
/* 152:    */         }
/* 153:    */       }
/* 154:    */       else
/* 155:    */       {
/* 156:220 */         outputValue = BeanUtils.idx(ctx, propertyPath);
/* 157:221 */         if (outputValue != null) {
/* 158:    */           break;
/* 159:    */         }
/* 160:    */       }
/* 161:    */     }
/* 162:228 */     if (outputValue == null) {
/* 163:229 */       outputValue = defaultValue;
/* 164:    */     }
/* 165:232 */     if (formatRule != null) {
/* 166:233 */       outputValue = applyFormatRule(formatRule, outputValue);
/* 167:    */     }
/* 168:236 */     return outputValue;
/* 169:    */   }
/* 170:    */   
/* 171:    */   private Object applyFormatRule(String formatRule, Object outputValue)
/* 172:    */   {
/* 173:243 */     return String.format(Str.add("%", formatRule), new Object[] { outputValue });
/* 174:    */   }
/* 175:    */   
/* 176:    */   public byte idxByte(String property)
/* 177:    */   {
/* 178:251 */     return Conversions.toByte(lookup(property));
/* 179:    */   }
/* 180:    */   
/* 181:    */   public short idxShort(String property)
/* 182:    */   {
/* 183:257 */     return Conversions.toShort(lookup(property));
/* 184:    */   }
/* 185:    */   
/* 186:    */   public String idxString(String property)
/* 187:    */   {
/* 188:263 */     return Conversions.toString(lookup(property));
/* 189:    */   }
/* 190:    */   
/* 191:    */   public int idxInt(String property)
/* 192:    */   {
/* 193:269 */     return Conversions.toInt(lookup(property));
/* 194:    */   }
/* 195:    */   
/* 196:    */   public float idxFloat(String property)
/* 197:    */   {
/* 198:275 */     return Conversions.toFloat(lookup(property));
/* 199:    */   }
/* 200:    */   
/* 201:    */   public double idxDouble(String property)
/* 202:    */   {
/* 203:281 */     return Conversions.toDouble(lookup(property));
/* 204:    */   }
/* 205:    */   
/* 206:    */   public long idxLong(String property)
/* 207:    */   {
/* 208:287 */     return Conversions.toLong(lookup(property));
/* 209:    */   }
/* 210:    */   
/* 211:    */   public Object idx(String property)
/* 212:    */   {
/* 213:293 */     return lookup(property);
/* 214:    */   }
/* 215:    */   
/* 216:    */   public <T> T idx(Class<T> type, String property)
/* 217:    */   {
/* 218:299 */     return lookup(property);
/* 219:    */   }
/* 220:    */   
/* 221:    */   public int size()
/* 222:    */   {
/* 223:304 */     return this.context.size();
/* 224:    */   }
/* 225:    */   
/* 226:    */   public boolean isEmpty()
/* 227:    */   {
/* 228:308 */     return this.context.isEmpty();
/* 229:    */   }
/* 230:    */   
/* 231:    */   public Object get(Object key)
/* 232:    */   {
/* 233:313 */     return lookup(key.toString());
/* 234:    */   }
/* 235:    */   
/* 236:    */   public void put(String key, Object value)
/* 237:    */   {
/* 238:321 */     Pair<String, Object> pair = new Pair(key, value);
/* 239:322 */     this.context.add(0, pair);
/* 240:    */   }
/* 241:    */   
/* 242:    */   public Object lookup(String objectName)
/* 243:    */   {
/* 244:333 */     return lookupWithDefault(objectName, objectName);
/* 245:    */   }
/* 246:    */   
/* 247:    */   private Object doLookup(String objectExpression, Object defaultValue, boolean searchChildren)
/* 248:    */   {
/* 249:348 */     if (Str.isEmpty(objectExpression)) {
/* 250:349 */       return defaultValue;
/* 251:    */     }
/* 252:352 */     char firstChar = Str.idx(objectExpression, 0);
/* 253:353 */     char secondChar = Str.idx(objectExpression, 1);
/* 254:354 */     char lastChar = Str.idx(objectExpression, -1);
/* 255:    */     
/* 256:356 */     boolean escape = false;
/* 257:358 */     switch (firstChar)
/* 258:    */     {
/* 259:    */     case '$': 
/* 260:360 */       if (lastChar == '}') {
/* 261:361 */         objectExpression = Str.slc(objectExpression, 2, -1);
/* 262:    */       } else {
/* 263:363 */         objectExpression = Str.slc(objectExpression, 1);
/* 264:    */       }
/* 265:365 */       break;
/* 266:    */     case '{': 
/* 267:367 */       if ((secondChar == '{') && (lastChar == '}'))
/* 268:    */       {
/* 269:368 */         char thirdChar = Str.idx(objectExpression, 2);
/* 270:370 */         if (thirdChar == '{')
/* 271:    */         {
/* 272:371 */           escape = true;
/* 273:372 */           objectExpression = Str.slc(objectExpression, 3, -3);
/* 274:    */         }
/* 275:    */         else
/* 276:    */         {
/* 277:374 */           objectExpression = Str.slc(objectExpression, 2, -2);
/* 278:    */         }
/* 279:    */       }
/* 280:    */       else
/* 281:    */       {
/* 282:378 */         if (lastChar == '}') {
/* 283:379 */           return this.jsonParser.parse(objectExpression);
/* 284:    */         }
/* 285:381 */         escape = true;
/* 286:382 */         objectExpression = Str.slc(objectExpression, 1);
/* 287:    */       }
/* 288:385 */       break;
/* 289:    */     case '[': 
/* 290:387 */       return this.jsonParser.parse(objectExpression);
/* 291:    */     case '.': 
/* 292:389 */       if (secondChar == '.')
/* 293:    */       {
/* 294:391 */         String newExp = Str.slc(objectExpression, 2);
/* 295:392 */         return this.parent.doLookup(newExp, newExp, false);
/* 296:    */       }
/* 297:    */       break;
/* 298:    */     }
/* 299:397 */     lastChar = Str.idx(objectExpression, -1);
/* 300:    */     Object value;
/* 301:    */     Object value;
/* 302:398 */     if (lastChar == ')')
/* 303:    */     {
/* 304:399 */       value = handleFunction(objectExpression, searchChildren);
/* 305:    */     }
/* 306:    */     else
/* 307:    */     {
/* 308:403 */       value = findProperty(objectExpression, searchChildren);
/* 309:    */       
/* 310:405 */       value = value == null ? defaultValue : value;
/* 311:    */     }
/* 312:409 */     if (!escape) {
/* 313:410 */       return value;
/* 314:    */     }
/* 315:412 */     return StandardFunctions.escapeXml(value);
/* 316:    */   }
/* 317:    */   
/* 318:    */   public Object lookupWithDefault(String objectExpression, Object defaultValue)
/* 319:    */   {
/* 320:417 */     return doLookup(objectExpression, defaultValue, true);
/* 321:    */   }
/* 322:    */   
/* 323:    */   private Object handleFunction(String functionCall, boolean searchChildren)
/* 324:    */   {
/* 325:426 */     String[] split = StringScanner.split(functionCall, '(', 1);
/* 326:    */     
/* 327:    */ 
/* 328:429 */     String methodName = split[0];
/* 329:430 */     String arguments = Str.slc(split[1], 0, -1);
/* 330:431 */     List<Object> args = getObjectFromArguments(arguments, searchChildren);
/* 331:    */     
/* 332:433 */     MethodAccess method = (MethodAccess)this.staticMethodMap.get(methodName);
/* 333:435 */     if (method != null) {
/* 334:437 */       return method.invokeDynamic(null, Arry.objectArray(args));
/* 335:    */     }
/* 336:439 */     return handleMethodCall(methodName, args);
/* 337:    */   }
/* 338:    */   
/* 339:    */   private Object handleMethodCall(String objectPath, List<Object> args)
/* 340:    */   {
/* 341:446 */     int lastIndexOf = objectPath.lastIndexOf('.');
/* 342:    */     
/* 343:448 */     String beanPath = objectPath.substring(0, lastIndexOf);
/* 344:449 */     String methodName = objectPath.substring(lastIndexOf + 1, objectPath.length());
/* 345:    */     
/* 346:451 */     Object bean = lookup(beanPath);
/* 347:453 */     if (bean == null) {
/* 348:454 */       return null;
/* 349:    */     }
/* 350:457 */     Class<?> cls = Boon.cls(bean);
/* 351:459 */     if (cls == null) {
/* 352:460 */       return null;
/* 353:    */     }
/* 354:463 */     ClassMeta<?> classMeta = ClassMeta.classMeta(cls);
/* 355:    */     
/* 356:465 */     MethodAccess method = classMeta.method(methodName);
/* 357:467 */     if (method == null) {
/* 358:468 */       return null;
/* 359:    */     }
/* 360:471 */     return method.invokeDynamic(bean, Arry.array(args));
/* 361:    */   }
/* 362:    */   
/* 363:    */   protected List<Object> getObjectFromArguments(String arguments, boolean searchChildren)
/* 364:    */   {
/* 365:476 */     String[] strings = StringScanner.splitByChars(arguments, new char[] { ',' });
/* 366:    */     
/* 367:478 */     List list = new ArrayList();
/* 368:480 */     for (String string : strings)
/* 369:    */     {
/* 370:481 */       Object object = doLookup(string, string, searchChildren);
/* 371:482 */       list.add(object);
/* 372:    */     }
/* 373:485 */     return list;
/* 374:    */   }
/* 375:    */   
/* 376:    */   public void pushContext(Object value)
/* 377:    */   {
/* 378:493 */     BoonExpressionContext child = new BoonExpressionContext(new Object[] { value });
/* 379:494 */     child.parent = this;
/* 380:495 */     this.context.add(0, child);
/* 381:    */   }
/* 382:    */   
/* 383:    */   public void removeLastContext()
/* 384:    */   {
/* 385:500 */     this.context.remove(0);
/* 386:    */   }
/* 387:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.expression.BoonExpressionContext
 * JD-Core Version:    0.7.0.1
 */