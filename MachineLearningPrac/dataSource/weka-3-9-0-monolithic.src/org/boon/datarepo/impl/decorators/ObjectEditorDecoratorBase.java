/*   1:    */ package org.boon.datarepo.impl.decorators;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.List;
/*   5:    */ import org.boon.criteria.Update;
/*   6:    */ import org.boon.datarepo.ObjectEditor;
/*   7:    */ 
/*   8:    */ public class ObjectEditorDecoratorBase<KEY, ITEM>
/*   9:    */   implements ObjectEditor<KEY, ITEM>
/*  10:    */ {
/*  11:    */   private final ObjectEditor<KEY, ITEM> objectEditorDefault;
/*  12:    */   
/*  13:    */   public ObjectEditorDecoratorBase()
/*  14:    */   {
/*  15: 41 */     this.objectEditorDefault = null;
/*  16:    */   }
/*  17:    */   
/*  18:    */   public ObjectEditorDecoratorBase(ObjectEditor oe)
/*  19:    */   {
/*  20: 45 */     this.objectEditorDefault = oe;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public ObjectEditor<KEY, ITEM> delegate()
/*  24:    */   {
/*  25: 50 */     return this.objectEditorDefault;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void put(ITEM item)
/*  29:    */   {
/*  30: 54 */     this.objectEditorDefault.put(item);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void removeByKey(KEY key)
/*  34:    */   {
/*  35: 58 */     this.objectEditorDefault.removeByKey(key);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void removeAll(ITEM... items)
/*  39:    */   {
/*  40: 62 */     this.objectEditorDefault.removeAll(items);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void removeAllAsync(Collection<ITEM> items)
/*  44:    */   {
/*  45: 66 */     this.objectEditorDefault.removeAllAsync(items);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void addAll(ITEM... items)
/*  49:    */   {
/*  50: 70 */     this.objectEditorDefault.addAll(items);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void addAllAsync(Collection<ITEM> items)
/*  54:    */   {
/*  55: 74 */     this.objectEditorDefault.addAllAsync(items);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void modifyAll(ITEM... items)
/*  59:    */   {
/*  60: 78 */     this.objectEditorDefault.modifyAll(items);
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void modifyAll(Collection<ITEM> items)
/*  64:    */   {
/*  65: 82 */     this.objectEditorDefault.modifyAll(items);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void modify(ITEM item)
/*  69:    */   {
/*  70: 86 */     this.objectEditorDefault.modify(item);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void update(ITEM item)
/*  74:    */   {
/*  75: 91 */     this.objectEditorDefault.update(item);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void modify(ITEM item, String property, Object value)
/*  79:    */   {
/*  80: 95 */     this.objectEditorDefault.modify(item, property, value);
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void modifyByValue(ITEM item, String property, String value)
/*  84:    */   {
/*  85: 99 */     this.objectEditorDefault.modifyByValue(item, property, value);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void modify(ITEM item, String property, int value)
/*  89:    */   {
/*  90:103 */     this.objectEditorDefault.modify(item, property, value);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void modify(ITEM item, String property, long value)
/*  94:    */   {
/*  95:107 */     this.objectEditorDefault.modify(item, property, value);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void modify(ITEM item, String property, char value)
/*  99:    */   {
/* 100:111 */     this.objectEditorDefault.modify(item, property, value);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void modify(ITEM item, String property, short value)
/* 104:    */   {
/* 105:115 */     this.objectEditorDefault.modify(item, property, value);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void modify(ITEM item, String property, byte value)
/* 109:    */   {
/* 110:119 */     this.objectEditorDefault.modify(item, property, value);
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void modify(ITEM item, String property, float value)
/* 114:    */   {
/* 115:123 */     this.objectEditorDefault.modify(item, property, value);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void modify(ITEM item, String property, double value)
/* 119:    */   {
/* 120:127 */     this.objectEditorDefault.modify(item, property, value);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void modify(ITEM item, Update... values)
/* 124:    */   {
/* 125:131 */     this.objectEditorDefault.modify(item, values);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void update(KEY key, String property, Object value)
/* 129:    */   {
/* 130:135 */     this.objectEditorDefault.update(key, property, value);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void updateByValue(KEY key, String property, String value)
/* 134:    */   {
/* 135:139 */     this.objectEditorDefault.updateByValue(key, property, value);
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void update(KEY key, String property, int value)
/* 139:    */   {
/* 140:143 */     this.objectEditorDefault.update(key, property, value);
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void update(KEY key, String property, long value)
/* 144:    */   {
/* 145:147 */     this.objectEditorDefault.update(key, property, value);
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void update(KEY key, String property, char value)
/* 149:    */   {
/* 150:151 */     this.objectEditorDefault.update(key, property, value);
/* 151:    */   }
/* 152:    */   
/* 153:    */   public void update(KEY key, String property, short value)
/* 154:    */   {
/* 155:155 */     this.objectEditorDefault.update(key, property, value);
/* 156:    */   }
/* 157:    */   
/* 158:    */   public void update(KEY key, String property, byte value)
/* 159:    */   {
/* 160:159 */     this.objectEditorDefault.update(key, property, value);
/* 161:    */   }
/* 162:    */   
/* 163:    */   public void update(KEY key, String property, float value)
/* 164:    */   {
/* 165:163 */     this.objectEditorDefault.update(key, property, value);
/* 166:    */   }
/* 167:    */   
/* 168:    */   public void update(KEY key, String property, double value)
/* 169:    */   {
/* 170:167 */     this.objectEditorDefault.update(key, property, value);
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void update(KEY key, Update... values)
/* 174:    */   {
/* 175:171 */     this.objectEditorDefault.update(key, values);
/* 176:    */   }
/* 177:    */   
/* 178:    */   public boolean compareAndUpdate(KEY key, String property, Object compare, Object value)
/* 179:    */   {
/* 180:175 */     return this.objectEditorDefault.compareAndUpdate(key, property, compare, value);
/* 181:    */   }
/* 182:    */   
/* 183:    */   public boolean compareAndUpdate(KEY key, String property, int compare, int value)
/* 184:    */   {
/* 185:179 */     return this.objectEditorDefault.compareAndUpdate(key, property, compare, value);
/* 186:    */   }
/* 187:    */   
/* 188:    */   public boolean compareAndUpdate(KEY key, String property, long compare, long value)
/* 189:    */   {
/* 190:183 */     return this.objectEditorDefault.compareAndUpdate(key, property, compare, value);
/* 191:    */   }
/* 192:    */   
/* 193:    */   public boolean compareAndUpdate(KEY key, String property, char compare, char value)
/* 194:    */   {
/* 195:187 */     return this.objectEditorDefault.compareAndUpdate(key, property, compare, value);
/* 196:    */   }
/* 197:    */   
/* 198:    */   public boolean compareAndUpdate(KEY key, String property, short compare, short value)
/* 199:    */   {
/* 200:191 */     return this.objectEditorDefault.compareAndUpdate(key, property, compare, value);
/* 201:    */   }
/* 202:    */   
/* 203:    */   public boolean compareAndUpdate(KEY key, String property, byte compare, byte value)
/* 204:    */   {
/* 205:195 */     return this.objectEditorDefault.compareAndUpdate(key, property, compare, value);
/* 206:    */   }
/* 207:    */   
/* 208:    */   public boolean compareAndUpdate(KEY key, String property, float compare, float value)
/* 209:    */   {
/* 210:199 */     return this.objectEditorDefault.compareAndUpdate(key, property, compare, value);
/* 211:    */   }
/* 212:    */   
/* 213:    */   public boolean compareAndUpdate(KEY key, String property, double compare, double value)
/* 214:    */   {
/* 215:203 */     return this.objectEditorDefault.compareAndUpdate(key, property, compare, value);
/* 216:    */   }
/* 217:    */   
/* 218:    */   public boolean compareAndIncrement(KEY key, String property, int compare)
/* 219:    */   {
/* 220:207 */     return this.objectEditorDefault.compareAndIncrement(key, property, compare);
/* 221:    */   }
/* 222:    */   
/* 223:    */   public boolean compareAndIncrement(KEY key, String property, long compare)
/* 224:    */   {
/* 225:211 */     return this.objectEditorDefault.compareAndIncrement(key, property, compare);
/* 226:    */   }
/* 227:    */   
/* 228:    */   public boolean compareAndIncrement(KEY key, String property, short compare)
/* 229:    */   {
/* 230:215 */     return this.objectEditorDefault.compareAndIncrement(key, property, compare);
/* 231:    */   }
/* 232:    */   
/* 233:    */   public boolean compareAndIncrement(KEY key, String property, byte compare)
/* 234:    */   {
/* 235:219 */     return this.objectEditorDefault.compareAndIncrement(key, property, compare);
/* 236:    */   }
/* 237:    */   
/* 238:    */   public void addAll(List<ITEM> items)
/* 239:    */   {
/* 240:223 */     this.objectEditorDefault.addAll(items);
/* 241:    */   }
/* 242:    */   
/* 243:    */   public Object readNestedValue(KEY key, String... properties)
/* 244:    */   {
/* 245:228 */     return this.objectEditorDefault.readNestedValue(key, properties);
/* 246:    */   }
/* 247:    */   
/* 248:    */   public int readNestedInt(KEY key, String... properties)
/* 249:    */   {
/* 250:233 */     return this.objectEditorDefault.readNestedInt(key, properties);
/* 251:    */   }
/* 252:    */   
/* 253:    */   public short readNestedShort(KEY key, String... properties)
/* 254:    */   {
/* 255:238 */     return this.objectEditorDefault.readNestedShort(key, properties);
/* 256:    */   }
/* 257:    */   
/* 258:    */   public char readNestedChar(KEY key, String... properties)
/* 259:    */   {
/* 260:243 */     return this.objectEditorDefault.readNestedChar(key, properties);
/* 261:    */   }
/* 262:    */   
/* 263:    */   public byte readNestedByte(KEY key, String... properties)
/* 264:    */   {
/* 265:248 */     return this.objectEditorDefault.readNestedByte(key, properties);
/* 266:    */   }
/* 267:    */   
/* 268:    */   public double readNestedDouble(KEY key, String... properties)
/* 269:    */   {
/* 270:253 */     return this.objectEditorDefault.readNestedDouble(key, properties);
/* 271:    */   }
/* 272:    */   
/* 273:    */   public float readNestedFloat(KEY key, String... properties)
/* 274:    */   {
/* 275:258 */     return this.objectEditorDefault.readNestedFloat(key, properties);
/* 276:    */   }
/* 277:    */   
/* 278:    */   public long readNestedLong(KEY key, String... properties)
/* 279:    */   {
/* 280:263 */     return this.objectEditorDefault.readNestedLong(key, properties);
/* 281:    */   }
/* 282:    */   
/* 283:    */   public Object readObject(KEY key, String property)
/* 284:    */   {
/* 285:268 */     return this.objectEditorDefault.readObject(key, property);
/* 286:    */   }
/* 287:    */   
/* 288:    */   public <T> T readValue(KEY key, String property, Class<T> type)
/* 289:    */   {
/* 290:273 */     return this.objectEditorDefault.readValue(key, property, type);
/* 291:    */   }
/* 292:    */   
/* 293:    */   public int readInt(KEY key, String property)
/* 294:    */   {
/* 295:278 */     return this.objectEditorDefault.readInt(key, property);
/* 296:    */   }
/* 297:    */   
/* 298:    */   public long readLong(KEY key, String property)
/* 299:    */   {
/* 300:283 */     return this.objectEditorDefault.readLong(key, property);
/* 301:    */   }
/* 302:    */   
/* 303:    */   public char readChar(KEY key, String property)
/* 304:    */   {
/* 305:289 */     return this.objectEditorDefault.readChar(key, property);
/* 306:    */   }
/* 307:    */   
/* 308:    */   public short readShort(KEY key, String property)
/* 309:    */   {
/* 310:295 */     return this.objectEditorDefault.readShort(key, property);
/* 311:    */   }
/* 312:    */   
/* 313:    */   public byte readByte(KEY key, String property)
/* 314:    */   {
/* 315:301 */     return this.objectEditorDefault.readByte(key, property);
/* 316:    */   }
/* 317:    */   
/* 318:    */   public float readFloat(KEY key, String property)
/* 319:    */   {
/* 320:307 */     return this.objectEditorDefault.readFloat(key, property);
/* 321:    */   }
/* 322:    */   
/* 323:    */   public double readDouble(KEY key, String property)
/* 324:    */   {
/* 325:312 */     return this.objectEditorDefault.readDouble(key, property);
/* 326:    */   }
/* 327:    */   
/* 328:    */   public Object getObject(ITEM item, String property)
/* 329:    */   {
/* 330:318 */     return this.objectEditorDefault.getObject(item, property);
/* 331:    */   }
/* 332:    */   
/* 333:    */   public <T> T getValue(ITEM item, String property, Class<T> type)
/* 334:    */   {
/* 335:323 */     return this.objectEditorDefault.getValue(item, property, type);
/* 336:    */   }
/* 337:    */   
/* 338:    */   public int getInt(ITEM item, String property)
/* 339:    */   {
/* 340:328 */     return this.objectEditorDefault.getInt(item, property);
/* 341:    */   }
/* 342:    */   
/* 343:    */   public long getLong(ITEM item, String property)
/* 344:    */   {
/* 345:333 */     return this.objectEditorDefault.getLong(item, property);
/* 346:    */   }
/* 347:    */   
/* 348:    */   public char getChar(ITEM item, String property)
/* 349:    */   {
/* 350:338 */     return this.objectEditorDefault.getChar(item, property);
/* 351:    */   }
/* 352:    */   
/* 353:    */   public short getShort(ITEM item, String property)
/* 354:    */   {
/* 355:343 */     return this.objectEditorDefault.getShort(item, property);
/* 356:    */   }
/* 357:    */   
/* 358:    */   public byte getByte(ITEM item, String property)
/* 359:    */   {
/* 360:348 */     return this.objectEditorDefault.getByte(item, property);
/* 361:    */   }
/* 362:    */   
/* 363:    */   public float getFloat(ITEM item, String property)
/* 364:    */   {
/* 365:353 */     return this.objectEditorDefault.getFloat(item, property);
/* 366:    */   }
/* 367:    */   
/* 368:    */   public double getDouble(ITEM item, String property)
/* 369:    */   {
/* 370:358 */     return this.objectEditorDefault.getDouble(item, property);
/* 371:    */   }
/* 372:    */   
/* 373:    */   public boolean add(ITEM item)
/* 374:    */   {
/* 375:362 */     return this.objectEditorDefault.add(item);
/* 376:    */   }
/* 377:    */   
/* 378:    */   public ITEM get(KEY key)
/* 379:    */   {
/* 380:367 */     return this.objectEditorDefault.get(key);
/* 381:    */   }
/* 382:    */   
/* 383:    */   public KEY getKey(ITEM item)
/* 384:    */   {
/* 385:371 */     return this.objectEditorDefault.getKey(item);
/* 386:    */   }
/* 387:    */   
/* 388:    */   public void clear()
/* 389:    */   {
/* 390:375 */     this.objectEditorDefault.clear();
/* 391:    */   }
/* 392:    */   
/* 393:    */   public boolean delete(ITEM item)
/* 394:    */   {
/* 395:379 */     return this.objectEditorDefault.delete(item);
/* 396:    */   }
/* 397:    */   
/* 398:    */   public List<ITEM> all()
/* 399:    */   {
/* 400:383 */     return this.objectEditorDefault.all();
/* 401:    */   }
/* 402:    */   
/* 403:    */   public int size()
/* 404:    */   {
/* 405:387 */     return this.objectEditorDefault.size();
/* 406:    */   }
/* 407:    */   
/* 408:    */   public Collection<ITEM> toCollection()
/* 409:    */   {
/* 410:391 */     return this.objectEditorDefault.toCollection();
/* 411:    */   }
/* 412:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.decorators.ObjectEditorDecoratorBase
 * JD-Core Version:    0.7.0.1
 */