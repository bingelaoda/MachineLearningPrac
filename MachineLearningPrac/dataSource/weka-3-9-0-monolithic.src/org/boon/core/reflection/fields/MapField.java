/*   1:    */ package org.boon.core.reflection.fields;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Field;
/*   4:    */ import java.lang.reflect.ParameterizedType;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Map;
/*   7:    */ import org.boon.Exceptions;
/*   8:    */ import org.boon.core.Conversions;
/*   9:    */ import org.boon.core.TypeType;
/*  10:    */ import org.boon.core.Value;
/*  11:    */ 
/*  12:    */ public class MapField
/*  13:    */   implements FieldAccess
/*  14:    */ {
/*  15:    */   private final String name;
/*  16:    */   
/*  17:    */   public MapField(String name)
/*  18:    */   {
/*  19: 47 */     this.name = name;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public boolean injectable()
/*  23:    */   {
/*  24: 52 */     return false;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public boolean requiresInjection()
/*  28:    */   {
/*  29: 57 */     return false;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public boolean isNamed()
/*  33:    */   {
/*  34: 62 */     return false;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public boolean hasAlias()
/*  38:    */   {
/*  39: 67 */     return false;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String alias()
/*  43:    */   {
/*  44: 72 */     return this.name;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String named()
/*  48:    */   {
/*  49: 77 */     return alias();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public final String name()
/*  53:    */   {
/*  54: 82 */     return this.name;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public final Object getValue(Object obj)
/*  58:    */   {
/*  59: 87 */     if ((obj instanceof Map))
/*  60:    */     {
/*  61: 88 */       Map map = (Map)obj;
/*  62: 89 */       return map.get(this.name);
/*  63:    */     }
/*  64: 91 */     return Exceptions.die(Object.class, "Object must be a map but was a " + obj.getClass().getName());
/*  65:    */   }
/*  66:    */   
/*  67:    */   public final void setValue(Object obj, Object value)
/*  68:    */   {
/*  69: 96 */     if ((obj instanceof Map))
/*  70:    */     {
/*  71: 97 */       Map map = (Map)obj;
/*  72: 98 */       map.put(this.name, value);
/*  73: 99 */       return;
/*  74:    */     }
/*  75:101 */     Exceptions.die("Object must be a map");
/*  76:    */   }
/*  77:    */   
/*  78:    */   public final void setFromValue(Object obj, Value value)
/*  79:    */   {
/*  80:106 */     setValue(obj, value.toValue());
/*  81:    */   }
/*  82:    */   
/*  83:    */   public final boolean getBoolean(Object obj)
/*  84:    */   {
/*  85:111 */     if ((obj instanceof Map))
/*  86:    */     {
/*  87:112 */       Map map = (Map)obj;
/*  88:113 */       return Conversions.toBoolean(map.get(this.name));
/*  89:    */     }
/*  90:115 */     return ((Boolean)Exceptions.die(Boolean.class, "Object must be a map")).booleanValue();
/*  91:    */   }
/*  92:    */   
/*  93:    */   public final void setBoolean(Object obj, boolean value)
/*  94:    */   {
/*  95:120 */     if ((obj instanceof Map))
/*  96:    */     {
/*  97:121 */       Map map = (Map)obj;
/*  98:122 */       map.put(this.name, Boolean.valueOf(value));
/*  99:    */     }
/* 100:124 */     Exceptions.die("Object must be a map");
/* 101:    */   }
/* 102:    */   
/* 103:    */   public final int getInt(Object obj)
/* 104:    */   {
/* 105:129 */     if ((obj instanceof Map))
/* 106:    */     {
/* 107:130 */       Map map = (Map)obj;
/* 108:131 */       return Conversions.toInt(map.get(this.name));
/* 109:    */     }
/* 110:133 */     Exceptions.die("Object must be a map");
/* 111:134 */     return -1;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public final void setInt(Object obj, int value)
/* 115:    */   {
/* 116:139 */     if ((obj instanceof Map))
/* 117:    */     {
/* 118:140 */       Map map = (Map)obj;
/* 119:141 */       map.put(this.name, Integer.valueOf(value));
/* 120:    */     }
/* 121:143 */     Exceptions.die("Object must be a map");
/* 122:    */   }
/* 123:    */   
/* 124:    */   public final short getShort(Object obj)
/* 125:    */   {
/* 126:148 */     if ((obj instanceof Map))
/* 127:    */     {
/* 128:149 */       Map map = (Map)obj;
/* 129:150 */       return Conversions.toShort(map.get(this.name));
/* 130:    */     }
/* 131:152 */     Exceptions.die("Object must be a map");
/* 132:153 */     return -1;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public final void setShort(Object obj, short value)
/* 136:    */   {
/* 137:158 */     if ((obj instanceof Map))
/* 138:    */     {
/* 139:159 */       Map map = (Map)obj;
/* 140:160 */       map.put(this.name, Short.valueOf(value));
/* 141:    */     }
/* 142:162 */     Exceptions.die("Object must be a map");
/* 143:    */   }
/* 144:    */   
/* 145:    */   public final char getChar(Object obj)
/* 146:    */   {
/* 147:167 */     if ((obj instanceof Map))
/* 148:    */     {
/* 149:168 */       Map map = (Map)obj;
/* 150:169 */       return Conversions.toChar(map.get(this.name));
/* 151:    */     }
/* 152:171 */     Exceptions.die("Object must be a map");
/* 153:172 */     return '\000';
/* 154:    */   }
/* 155:    */   
/* 156:    */   public final void setChar(Object obj, char value)
/* 157:    */   {
/* 158:177 */     if ((obj instanceof Map))
/* 159:    */     {
/* 160:178 */       Map map = (Map)obj;
/* 161:179 */       map.put(this.name, Character.valueOf(value));
/* 162:    */     }
/* 163:181 */     Exceptions.die("Object must be a map");
/* 164:    */   }
/* 165:    */   
/* 166:    */   public final long getLong(Object obj)
/* 167:    */   {
/* 168:186 */     if ((obj instanceof Map))
/* 169:    */     {
/* 170:187 */       Map map = (Map)obj;
/* 171:188 */       return Conversions.toLong(map.get(this.name));
/* 172:    */     }
/* 173:190 */     Exceptions.die("Object must be a map");
/* 174:191 */     return -1L;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public final void setLong(Object obj, long value)
/* 178:    */   {
/* 179:196 */     if ((obj instanceof Map))
/* 180:    */     {
/* 181:197 */       Map map = (Map)obj;
/* 182:198 */       map.put(this.name, Long.valueOf(value));
/* 183:    */     }
/* 184:200 */     Exceptions.die("Object must be a map");
/* 185:    */   }
/* 186:    */   
/* 187:    */   public final double getDouble(Object obj)
/* 188:    */   {
/* 189:205 */     if ((obj instanceof Map))
/* 190:    */     {
/* 191:206 */       Map map = (Map)obj;
/* 192:207 */       return Conversions.toDouble(map.get(this.name));
/* 193:    */     }
/* 194:209 */     Exceptions.die("Object must be a map");
/* 195:210 */     return (0.0D / 0.0D);
/* 196:    */   }
/* 197:    */   
/* 198:    */   public final void setDouble(Object obj, double value)
/* 199:    */   {
/* 200:215 */     if ((obj instanceof Map))
/* 201:    */     {
/* 202:216 */       Map map = (Map)obj;
/* 203:217 */       map.put(this.name, Double.valueOf(value));
/* 204:    */     }
/* 205:219 */     Exceptions.die("Object must be a map");
/* 206:    */   }
/* 207:    */   
/* 208:    */   public final float getFloat(Object obj)
/* 209:    */   {
/* 210:224 */     if ((obj instanceof Map))
/* 211:    */     {
/* 212:225 */       Map map = (Map)obj;
/* 213:226 */       return Conversions.toFloat(map.get(this.name));
/* 214:    */     }
/* 215:228 */     Exceptions.die("Object must be a map");
/* 216:229 */     return (0.0F / 0.0F);
/* 217:    */   }
/* 218:    */   
/* 219:    */   public final void setFloat(Object obj, float value)
/* 220:    */   {
/* 221:234 */     if ((obj instanceof Map))
/* 222:    */     {
/* 223:235 */       Map map = (Map)obj;
/* 224:236 */       map.put(this.name, Float.valueOf(value));
/* 225:    */     }
/* 226:238 */     Exceptions.die("Object must be a map");
/* 227:    */   }
/* 228:    */   
/* 229:    */   public final byte getByte(Object obj)
/* 230:    */   {
/* 231:243 */     if ((obj instanceof Map))
/* 232:    */     {
/* 233:244 */       Map map = (Map)obj;
/* 234:245 */       return Conversions.toByte(map.get(this.name));
/* 235:    */     }
/* 236:247 */     Exceptions.die("Object must be a map");
/* 237:248 */     return 127;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public final void setByte(Object obj, byte value)
/* 241:    */   {
/* 242:253 */     if ((obj instanceof Map))
/* 243:    */     {
/* 244:254 */       Map map = (Map)obj;
/* 245:255 */       map.put(this.name, Byte.valueOf(value));
/* 246:    */     }
/* 247:257 */     Exceptions.die("Object must be a map");
/* 248:    */   }
/* 249:    */   
/* 250:    */   public final Object getObject(Object obj)
/* 251:    */   {
/* 252:263 */     if ((obj instanceof Map))
/* 253:    */     {
/* 254:264 */       Map map = (Map)obj;
/* 255:265 */       return map.get(this.name);
/* 256:    */     }
/* 257:267 */     Exceptions.die("Object must be a map");
/* 258:268 */     return Integer.valueOf(-1);
/* 259:    */   }
/* 260:    */   
/* 261:    */   public final void setObject(Object obj, Object value)
/* 262:    */   {
/* 263:273 */     if ((obj instanceof Map))
/* 264:    */     {
/* 265:274 */       Map map = (Map)obj;
/* 266:275 */       map.put(this.name, value);
/* 267:    */     }
/* 268:277 */     Exceptions.die("Object must be a map");
/* 269:    */   }
/* 270:    */   
/* 271:    */   public final TypeType typeEnum()
/* 272:    */   {
/* 273:282 */     return TypeType.OBJECT;
/* 274:    */   }
/* 275:    */   
/* 276:    */   public final boolean isPrimitive()
/* 277:    */   {
/* 278:288 */     return false;
/* 279:    */   }
/* 280:    */   
/* 281:    */   public final Field getField()
/* 282:    */   {
/* 283:294 */     return (Field)Exceptions.die(Field.class, "Unsupported operation");
/* 284:    */   }
/* 285:    */   
/* 286:    */   public final boolean include()
/* 287:    */   {
/* 288:300 */     return false;
/* 289:    */   }
/* 290:    */   
/* 291:    */   public final boolean ignore()
/* 292:    */   {
/* 293:305 */     return false;
/* 294:    */   }
/* 295:    */   
/* 296:    */   public final ParameterizedType getParameterizedType()
/* 297:    */   {
/* 298:310 */     return null;
/* 299:    */   }
/* 300:    */   
/* 301:    */   public final Class<?> getComponentClass()
/* 302:    */   {
/* 303:315 */     return null;
/* 304:    */   }
/* 305:    */   
/* 306:    */   public final boolean hasAnnotation(String annotationName)
/* 307:    */   {
/* 308:320 */     return false;
/* 309:    */   }
/* 310:    */   
/* 311:    */   public final Map<String, Object> getAnnotationData(String annotationName)
/* 312:    */   {
/* 313:325 */     return Collections.EMPTY_MAP;
/* 314:    */   }
/* 315:    */   
/* 316:    */   public boolean isViewActive(String activeView)
/* 317:    */   {
/* 318:330 */     return true;
/* 319:    */   }
/* 320:    */   
/* 321:    */   public void setStaticValue(Object newValue) {}
/* 322:    */   
/* 323:    */   public TypeType componentType()
/* 324:    */   {
/* 325:340 */     return null;
/* 326:    */   }
/* 327:    */   
/* 328:    */   public final boolean isFinal()
/* 329:    */   {
/* 330:345 */     return false;
/* 331:    */   }
/* 332:    */   
/* 333:    */   public final boolean isStatic()
/* 334:    */   {
/* 335:350 */     return false;
/* 336:    */   }
/* 337:    */   
/* 338:    */   public final boolean isVolatile()
/* 339:    */   {
/* 340:355 */     return false;
/* 341:    */   }
/* 342:    */   
/* 343:    */   public final boolean isQualified()
/* 344:    */   {
/* 345:360 */     return false;
/* 346:    */   }
/* 347:    */   
/* 348:    */   public final boolean isReadOnly()
/* 349:    */   {
/* 350:365 */     return false;
/* 351:    */   }
/* 352:    */   
/* 353:    */   public boolean isWriteOnly()
/* 354:    */   {
/* 355:370 */     return false;
/* 356:    */   }
/* 357:    */   
/* 358:    */   public final Class<?> type()
/* 359:    */   {
/* 360:375 */     return Object.class;
/* 361:    */   }
/* 362:    */   
/* 363:    */   public Class<?> declaringParent()
/* 364:    */   {
/* 365:380 */     return null;
/* 366:    */   }
/* 367:    */   
/* 368:    */   public Object parent()
/* 369:    */   {
/* 370:385 */     return null;
/* 371:    */   }
/* 372:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.fields.MapField
 * JD-Core Version:    0.7.0.1
 */