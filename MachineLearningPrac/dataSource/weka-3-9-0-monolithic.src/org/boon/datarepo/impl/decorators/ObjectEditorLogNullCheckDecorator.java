/*   1:    */ package org.boon.datarepo.impl.decorators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.logging.Level;
/*   5:    */ import java.util.logging.Logger;
/*   6:    */ import org.boon.Exceptions;
/*   7:    */ import org.boon.criteria.Update;
/*   8:    */ import org.boon.datarepo.ObjectEditor;
/*   9:    */ 
/*  10:    */ public class ObjectEditorLogNullCheckDecorator<KEY, ITEM>
/*  11:    */   extends ObjectEditorDecoratorBase<KEY, ITEM>
/*  12:    */ {
/*  13: 43 */   Logger logger = Logger.getLogger(ObjectEditorLogNullCheckDecorator.class.getName());
/*  14: 44 */   Level level = Level.FINER;
/*  15: 47 */   private boolean debug = false;
/*  16:    */   
/*  17:    */   void log(String msg, Object... items)
/*  18:    */   {
/*  19: 51 */     if (this.debug) {
/*  20: 52 */       System.out.printf(msg, items);
/*  21:    */     }
/*  22: 54 */     String message = String.format(msg, items);
/*  23: 55 */     this.logger.log(this.level, message);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void setLevel(Level level)
/*  27:    */   {
/*  28: 61 */     this.level = level;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setDebug(boolean debug)
/*  32:    */   {
/*  33: 65 */     this.debug = debug;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setLogger(Logger logger)
/*  37:    */   {
/*  38: 70 */     this.logger = logger;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public ObjectEditorLogNullCheckDecorator() {}
/*  42:    */   
/*  43:    */   public ObjectEditorLogNullCheckDecorator(ObjectEditor oe)
/*  44:    */   {
/*  45: 79 */     super(oe);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void put(ITEM item)
/*  49:    */   {
/*  50: 86 */     Exceptions.requireNonNull(item, "item cannot be null");
/*  51: 87 */     log("put (item=%s)", new Object[] { item });
/*  52: 88 */     super.put(item);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public boolean add(ITEM item)
/*  56:    */   {
/*  57: 94 */     Exceptions.requireNonNull(item, "item cannot be null");
/*  58: 95 */     log("addObject (item=%s)", new Object[] { item });
/*  59: 96 */     return super.add(item);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public ITEM get(KEY key)
/*  63:    */   {
/*  64:101 */     Exceptions.requireNonNull(key, "key cannot be null");
/*  65:102 */     log("get (key=%s)", new Object[] { key });
/*  66:    */     
/*  67:104 */     return super.get(key);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void modify(ITEM item)
/*  71:    */   {
/*  72:110 */     Exceptions.requireNonNull(item, "item cannot be null");
/*  73:111 */     log("modify (item=%s)", new Object[] { item });
/*  74:    */     
/*  75:113 */     super.modify(item);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void modify(ITEM item, String property, Object value)
/*  79:    */   {
/*  80:118 */     Exceptions.requireNonNull(item, "item cannot be null");
/*  81:119 */     Exceptions.requireNonNull(property, "property cannot be null");
/*  82:120 */     Exceptions.requireNonNull(value, "value cannot be null");
/*  83:    */     
/*  84:122 */     log("modify (item=%s, property=%s, set=%s)", new Object[] { item, property, value });
/*  85:    */     
/*  86:124 */     super.modify(item, property, value);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void modifyByValue(ITEM item, String property, String value)
/*  90:    */   {
/*  91:129 */     Exceptions.requireNonNull(item, "item cannot be null");
/*  92:130 */     Exceptions.requireNonNull(property, "property cannot be null");
/*  93:131 */     Exceptions.requireNonNull(value, "value cannot be null");
/*  94:    */     
/*  95:133 */     log("modify (item=%s, property=%s, set=%s)", new Object[] { item, property, value });
/*  96:    */     
/*  97:135 */     super.modifyByValue(item, property, value);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void modify(ITEM item, String property, int value)
/* 101:    */   {
/* 102:140 */     Exceptions.requireNonNull(item, "item cannot be null");
/* 103:141 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 104:142 */     Exceptions.requireNonNull(Integer.valueOf(value), "value cannot be null");
/* 105:    */     
/* 106:144 */     log("modify (item=%s, property=%s, set=%s)", new Object[] { item, property, Integer.valueOf(value) });
/* 107:    */     
/* 108:146 */     super.modify(item, property, value);
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void modify(ITEM item, String property, long value)
/* 112:    */   {
/* 113:152 */     Exceptions.requireNonNull(item, "item cannot be null");
/* 114:153 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 115:154 */     Exceptions.requireNonNull(Long.valueOf(value), "value cannot be null");
/* 116:    */     
/* 117:156 */     log("modify (item=%s, property=%s, set=%s)", new Object[] { item, property, Long.valueOf(value) });
/* 118:    */     
/* 119:158 */     super.modify(item, property, value);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void modify(ITEM item, String property, char value)
/* 123:    */   {
/* 124:164 */     Exceptions.requireNonNull(item, "item cannot be null");
/* 125:165 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 126:166 */     Exceptions.requireNonNull(Character.valueOf(value), "value cannot be null");
/* 127:    */     
/* 128:168 */     log("modify (item=%s, property=%s, set=%s)", new Object[] { item, property, Character.valueOf(value) });
/* 129:    */     
/* 130:170 */     super.modify(item, property, value);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void modify(ITEM item, String property, short value)
/* 134:    */   {
/* 135:176 */     Exceptions.requireNonNull(item, "item cannot be null");
/* 136:177 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 137:178 */     Exceptions.requireNonNull(Short.valueOf(value), "value cannot be null");
/* 138:    */     
/* 139:180 */     log("modify (item=%s, property=%s, set=%s)", new Object[] { item, property, Short.valueOf(value) });
/* 140:    */     
/* 141:182 */     super.modify(item, property, value);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void modify(ITEM item, String property, byte value)
/* 145:    */   {
/* 146:187 */     Exceptions.requireNonNull(item, "item cannot be null");
/* 147:188 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 148:189 */     Exceptions.requireNonNull(Byte.valueOf(value), "value cannot be null");
/* 149:    */     
/* 150:191 */     log("modify (item=%s, property=%s, set=%s)", new Object[] { item, property, Byte.valueOf(value) });
/* 151:    */     
/* 152:193 */     super.modify(item, property, value);
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void modify(ITEM item, String property, float value)
/* 156:    */   {
/* 157:198 */     Exceptions.requireNonNull(item, "item cannot be null");
/* 158:199 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 159:200 */     Exceptions.requireNonNull(Float.valueOf(value), "value cannot be null");
/* 160:    */     
/* 161:202 */     log("modify (item=%s, property=%s, set=%s)", new Object[] { item, property, Float.valueOf(value) });
/* 162:    */     
/* 163:204 */     super.modify(item, property, value);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void modify(ITEM item, String property, double value)
/* 167:    */   {
/* 168:209 */     Exceptions.requireNonNull(item, "item cannot be null");
/* 169:210 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 170:211 */     Exceptions.requireNonNull(Double.valueOf(value), "value cannot be null");
/* 171:    */     
/* 172:213 */     log("modify (item=%s, property=%s, set=%s)", new Object[] { item, property, Double.valueOf(value) });
/* 173:    */     
/* 174:215 */     super.modify(item, property, value);
/* 175:    */   }
/* 176:    */   
/* 177:    */   public void modify(ITEM item, Update... values)
/* 178:    */   {
/* 179:220 */     Exceptions.requireNonNull(item, "item cannot be null");
/* 180:221 */     Exceptions.requireNonNull(values, "value cannot be null");
/* 181:    */     
/* 182:223 */     log("modify (item=%s, property=%s, update=%s)", new Object[] { item, values });
/* 183:    */     
/* 184:225 */     super.modify(item, values);
/* 185:    */   }
/* 186:    */   
/* 187:    */   public void update(KEY key, String property, Object value)
/* 188:    */   {
/* 189:230 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 190:231 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 191:232 */     Exceptions.requireNonNull(value, "value cannot be null");
/* 192:233 */     log("update (key=%s, property=%s, set=%s)", new Object[] { key, property, value });
/* 193:    */     
/* 194:235 */     super.update(key, property, value);
/* 195:    */   }
/* 196:    */   
/* 197:    */   public void updateByValue(KEY key, String property, String value)
/* 198:    */   {
/* 199:240 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 200:241 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 201:242 */     Exceptions.requireNonNull(value, "value cannot be null");
/* 202:243 */     log("updateByValue (key=%s, property=%s, set=%s)", new Object[] { key, property, value });
/* 203:    */     
/* 204:245 */     super.updateByValue(key, property, value);
/* 205:    */   }
/* 206:    */   
/* 207:    */   public void update(KEY key, String property, int value)
/* 208:    */   {
/* 209:250 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 210:251 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 211:252 */     Exceptions.requireNonNull(Integer.valueOf(value), "value cannot be null");
/* 212:253 */     log("update (key=%s, property=%s, set=%s)", new Object[] { key, property, Integer.valueOf(value) });
/* 213:254 */     super.update(key, property, value);
/* 214:    */   }
/* 215:    */   
/* 216:    */   public void update(KEY key, String property, long value)
/* 217:    */   {
/* 218:259 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 219:260 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 220:261 */     Exceptions.requireNonNull(Long.valueOf(value), "value cannot be null");
/* 221:262 */     log("update (key=%s, property=%s, set=%s)", new Object[] { key, property, Long.valueOf(value) });
/* 222:    */     
/* 223:264 */     super.update(key, property, value);
/* 224:    */   }
/* 225:    */   
/* 226:    */   public void update(KEY key, String property, char value)
/* 227:    */   {
/* 228:269 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 229:270 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 230:271 */     Exceptions.requireNonNull(Character.valueOf(value), "value cannot be null");
/* 231:272 */     log("update (key=%s, property=%s, set=%s)", new Object[] { key, property, Character.valueOf(value) });
/* 232:    */     
/* 233:274 */     super.update(key, property, value);
/* 234:    */   }
/* 235:    */   
/* 236:    */   public void update(KEY key, String property, short value)
/* 237:    */   {
/* 238:279 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 239:280 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 240:281 */     Exceptions.requireNonNull(Short.valueOf(value), "value cannot be null");
/* 241:282 */     log("update (key=%s, property=%s, set=%s)", new Object[] { key, property, Short.valueOf(value) });
/* 242:    */     
/* 243:284 */     super.update(key, property, value);
/* 244:    */   }
/* 245:    */   
/* 246:    */   public void update(KEY key, String property, byte value)
/* 247:    */   {
/* 248:289 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 249:290 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 250:291 */     Exceptions.requireNonNull(Byte.valueOf(value), "value cannot be null");
/* 251:292 */     log("update (key=%s, property=%s, set=%s)", new Object[] { key, property, Byte.valueOf(value) });
/* 252:    */     
/* 253:294 */     super.update(key, property, value);
/* 254:    */   }
/* 255:    */   
/* 256:    */   public void update(KEY key, String property, float value)
/* 257:    */   {
/* 258:299 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 259:300 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 260:301 */     Exceptions.requireNonNull(Float.valueOf(value), "value cannot be null");
/* 261:302 */     log("update (key=%s, property=%s, set=%s)", new Object[] { key, property, Float.valueOf(value) });
/* 262:    */     
/* 263:304 */     super.update(key, property, value);
/* 264:    */   }
/* 265:    */   
/* 266:    */   public void update(KEY key, String property, double value)
/* 267:    */   {
/* 268:309 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 269:310 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 270:311 */     Exceptions.requireNonNull(Double.valueOf(value), "value cannot be null");
/* 271:312 */     log("update (key=%s, property=%s, set=%s)", new Object[] { key, property, Double.valueOf(value) });
/* 272:    */     
/* 273:314 */     super.update(key, property, value);
/* 274:    */   }
/* 275:    */   
/* 276:    */   public void update(KEY key, Update... values)
/* 277:    */   {
/* 278:319 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 279:320 */     Exceptions.requireNonNull(values, "values cannot be null");
/* 280:    */     
/* 281:322 */     log("update (key=%s, update=%s)", new Object[] { key, values });
/* 282:    */     
/* 283:324 */     super.update(key, values);
/* 284:    */   }
/* 285:    */   
/* 286:    */   public boolean compareAndUpdate(KEY key, String property, Object compare, Object value)
/* 287:    */   {
/* 288:329 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 289:330 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 290:331 */     Exceptions.requireNonNull(compare, "compare cannot be null");
/* 291:332 */     Exceptions.requireNonNull(value, "value cannot be null");
/* 292:333 */     log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", new Object[] { key, property, compare, value });
/* 293:    */     
/* 294:335 */     return super.compareAndUpdate(key, property, compare, value);
/* 295:    */   }
/* 296:    */   
/* 297:    */   public boolean compareAndUpdate(KEY key, String property, int compare, int value)
/* 298:    */   {
/* 299:340 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 300:341 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 301:342 */     Exceptions.requireNonNull(Integer.valueOf(compare), "compare cannot be null");
/* 302:343 */     Exceptions.requireNonNull(Integer.valueOf(value), "value cannot be null");
/* 303:344 */     log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", new Object[] { key, property, Integer.valueOf(compare), Integer.valueOf(value) });
/* 304:    */     
/* 305:346 */     return super.compareAndUpdate(key, property, compare, value);
/* 306:    */   }
/* 307:    */   
/* 308:    */   public boolean compareAndUpdate(KEY key, String property, long compare, long value)
/* 309:    */   {
/* 310:351 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 311:352 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 312:353 */     Exceptions.requireNonNull(Long.valueOf(compare), "compare cannot be null");
/* 313:354 */     Exceptions.requireNonNull(Long.valueOf(value), "value cannot be null");
/* 314:355 */     log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", new Object[] { key, property, Long.valueOf(compare), Long.valueOf(value) });
/* 315:    */     
/* 316:357 */     return super.compareAndUpdate(key, property, compare, value);
/* 317:    */   }
/* 318:    */   
/* 319:    */   public boolean compareAndUpdate(KEY key, String property, char compare, char value)
/* 320:    */   {
/* 321:362 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 322:363 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 323:364 */     Exceptions.requireNonNull(Character.valueOf(compare), "compare cannot be null");
/* 324:365 */     Exceptions.requireNonNull(Character.valueOf(value), "value cannot be null");
/* 325:366 */     log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", new Object[] { key, property, Character.valueOf(compare), Character.valueOf(value) });
/* 326:    */     
/* 327:368 */     return super.compareAndUpdate(key, property, compare, value);
/* 328:    */   }
/* 329:    */   
/* 330:    */   public boolean compareAndUpdate(KEY key, String property, short compare, short value)
/* 331:    */   {
/* 332:373 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 333:374 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 334:375 */     Exceptions.requireNonNull(Short.valueOf(compare), "compare cannot be null");
/* 335:376 */     Exceptions.requireNonNull(Short.valueOf(value), "value cannot be null");
/* 336:377 */     log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", new Object[] { key, property, Short.valueOf(compare), Short.valueOf(value) });
/* 337:    */     
/* 338:379 */     return super.compareAndUpdate(key, property, compare, value);
/* 339:    */   }
/* 340:    */   
/* 341:    */   public boolean compareAndUpdate(KEY key, String property, byte compare, byte value)
/* 342:    */   {
/* 343:384 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 344:385 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 345:386 */     Exceptions.requireNonNull(Byte.valueOf(compare), "compare cannot be null");
/* 346:387 */     Exceptions.requireNonNull(Byte.valueOf(value), "value cannot be null");
/* 347:388 */     log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", new Object[] { key, property, Byte.valueOf(compare), Byte.valueOf(value) });
/* 348:    */     
/* 349:390 */     return super.compareAndUpdate(key, property, compare, value);
/* 350:    */   }
/* 351:    */   
/* 352:    */   public boolean compareAndUpdate(KEY key, String property, float compare, float value)
/* 353:    */   {
/* 354:395 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 355:396 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 356:397 */     Exceptions.requireNonNull(Float.valueOf(compare), "compare cannot be null");
/* 357:398 */     Exceptions.requireNonNull(Float.valueOf(value), "value cannot be null");
/* 358:399 */     log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", new Object[] { key, property, Float.valueOf(compare), Float.valueOf(value) });
/* 359:    */     
/* 360:401 */     return super.compareAndUpdate(key, property, compare, value);
/* 361:    */   }
/* 362:    */   
/* 363:    */   public boolean compareAndUpdate(KEY key, String property, double compare, double value)
/* 364:    */   {
/* 365:406 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 366:407 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 367:408 */     Exceptions.requireNonNull(Double.valueOf(compare), "compare cannot be null");
/* 368:409 */     Exceptions.requireNonNull(Double.valueOf(value), "value cannot be null");
/* 369:410 */     log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", new Object[] { key, property, Double.valueOf(compare), Double.valueOf(value) });
/* 370:    */     
/* 371:412 */     return super.compareAndUpdate(key, property, compare, value);
/* 372:    */   }
/* 373:    */   
/* 374:    */   public boolean compareAndIncrement(KEY key, String property, int compare)
/* 375:    */   {
/* 376:417 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 377:418 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 378:419 */     Exceptions.requireNonNull(Integer.valueOf(compare), "compare cannot be null");
/* 379:420 */     log("compareAndIncrement (key=%s, property=%s, compare=%s)", new Object[] { key, property, Integer.valueOf(compare) });
/* 380:    */     
/* 381:422 */     return super.compareAndIncrement(key, property, compare);
/* 382:    */   }
/* 383:    */   
/* 384:    */   public boolean compareAndIncrement(KEY key, String property, long compare)
/* 385:    */   {
/* 386:427 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 387:428 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 388:429 */     Exceptions.requireNonNull(Long.valueOf(compare), "compare cannot be null");
/* 389:430 */     log("compareAndIncrement (key=%s, property=%s, compare=%s)", new Object[] { key, property, Long.valueOf(compare) });
/* 390:    */     
/* 391:432 */     return super.compareAndIncrement(key, property, compare);
/* 392:    */   }
/* 393:    */   
/* 394:    */   public boolean compareAndIncrement(KEY key, String property, short compare)
/* 395:    */   {
/* 396:437 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 397:438 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 398:439 */     Exceptions.requireNonNull(Short.valueOf(compare), "compare cannot be null");
/* 399:440 */     log("compareAndIncrement (key=%s, property=%s, set=%s)", new Object[] { key, property, Short.valueOf(compare) });
/* 400:    */     
/* 401:442 */     return super.compareAndIncrement(key, property, compare);
/* 402:    */   }
/* 403:    */   
/* 404:    */   public boolean compareAndIncrement(KEY key, String property, byte compare)
/* 405:    */   {
/* 406:447 */     Exceptions.requireNonNull(key, "key cannot be null");
/* 407:448 */     Exceptions.requireNonNull(property, "property cannot be null");
/* 408:449 */     Exceptions.requireNonNull(Byte.valueOf(compare), "compare cannot be null");
/* 409:450 */     log("compareAndIncrement (key=%s, property=%s, set=%s)", new Object[] { key, property, Byte.valueOf(compare) });
/* 410:    */     
/* 411:452 */     return super.compareAndIncrement(key, property, compare);
/* 412:    */   }
/* 413:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.decorators.ObjectEditorLogNullCheckDecorator
 * JD-Core Version:    0.7.0.1
 */