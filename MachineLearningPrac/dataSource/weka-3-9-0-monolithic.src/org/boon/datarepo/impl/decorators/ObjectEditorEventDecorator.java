/*   1:    */ package org.boon.datarepo.impl.decorators;
/*   2:    */ 
/*   3:    */ import java.util.List;
/*   4:    */ import java.util.concurrent.CopyOnWriteArrayList;
/*   5:    */ import org.boon.Lists;
/*   6:    */ import org.boon.criteria.Update;
/*   7:    */ import org.boon.datarepo.ObjectEditor;
/*   8:    */ import org.boon.datarepo.modification.ModificationEvent;
/*   9:    */ import org.boon.datarepo.modification.ModificationListener;
/*  10:    */ import org.boon.datarepo.modification.ModificationType;
/*  11:    */ 
/*  12:    */ public class ObjectEditorEventDecorator<KEY, ITEM>
/*  13:    */   extends ObjectEditorDecoratorBase<KEY, ITEM>
/*  14:    */ {
/*  15: 46 */   List<ModificationListener<KEY, ITEM>> listeners = new CopyOnWriteArrayList();
/*  16:    */   
/*  17:    */   public void add(ModificationListener l)
/*  18:    */   {
/*  19: 49 */     this.listeners.add(l);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void remove(ModificationListener l)
/*  23:    */   {
/*  24: 55 */     this.listeners.add(l);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public ObjectEditorEventDecorator() {}
/*  28:    */   
/*  29:    */   public ObjectEditorEventDecorator(ObjectEditor oe)
/*  30:    */   {
/*  31: 63 */     super(oe);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void modify(ITEM item)
/*  35:    */   {
/*  36: 69 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_MODIFY, super.getKey(item), item, null, null));
/*  37: 70 */     super.modify(item);
/*  38: 71 */     fire(ModificationEvent.createModification(ModificationType.AFTER_MODIFY, super.getKey(item), item, null, null));
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void update(ITEM item)
/*  42:    */   {
/*  43: 75 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, super.getKey(item), item, null, null));
/*  44: 76 */     super.modify(item);
/*  45: 77 */     fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, super.getKey(item), item, null, null));
/*  46:    */   }
/*  47:    */   
/*  48:    */   private void fire(ModificationEvent<KEY, ITEM> event)
/*  49:    */   {
/*  50: 81 */     for (ModificationListener l : this.listeners) {
/*  51: 82 */       l.modification(event);
/*  52:    */     }
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void modify(ITEM item, String property, Object value)
/*  56:    */   {
/*  57: 87 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_MODIFY, super.getKey(item), item, property, value));
/*  58: 88 */     super.modify(item, property, value);
/*  59: 89 */     fire(ModificationEvent.createModification(ModificationType.AFTER_MODIFY, super.getKey(item), item, property, value));
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void modifyByValue(ITEM item, String property, String value)
/*  63:    */   {
/*  64: 94 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_MODIFY, super.getKey(item), item, property, value));
/*  65: 95 */     super.modifyByValue(item, property, value);
/*  66: 96 */     fire(ModificationEvent.createModification(ModificationType.AFTER_MODIFY, super.getKey(item), item, property, value));
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void modify(ITEM item, String property, int value)
/*  70:    */   {
/*  71:101 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_MODIFY, super.getKey(item), item, property, value));
/*  72:102 */     super.modify(item, property, value);
/*  73:103 */     fire(ModificationEvent.createModification(ModificationType.AFTER_MODIFY, super.getKey(item), item, property, value));
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void modify(ITEM item, String property, long value)
/*  77:    */   {
/*  78:108 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_MODIFY, super.getKey(item), item, property, value));
/*  79:109 */     super.modify(item, property, value);
/*  80:110 */     fire(ModificationEvent.createModification(ModificationType.AFTER_MODIFY, super.getKey(item), item, property, value));
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void modify(ITEM item, String property, char value)
/*  84:    */   {
/*  85:114 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_MODIFY, super.getKey(item), item, property, value));
/*  86:115 */     super.modify(item, property, value);
/*  87:116 */     fire(ModificationEvent.createModification(ModificationType.AFTER_MODIFY, super.getKey(item), item, property, value));
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void modify(ITEM item, String property, short value)
/*  91:    */   {
/*  92:120 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_MODIFY, super.getKey(item), item, property, value));
/*  93:121 */     super.modify(item, property, value);
/*  94:122 */     fire(ModificationEvent.createModification(ModificationType.AFTER_MODIFY, super.getKey(item), item, property, value));
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void modify(ITEM item, String property, byte value)
/*  98:    */   {
/*  99:126 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_MODIFY, super.getKey(item), item, property, value));
/* 100:127 */     super.modify(item, property, value);
/* 101:128 */     fire(ModificationEvent.createModification(ModificationType.AFTER_MODIFY, super.getKey(item), item, property, value));
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void modify(ITEM item, String property, float value)
/* 105:    */   {
/* 106:132 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_MODIFY, super.getKey(item), item, property, value));
/* 107:133 */     super.modify(item, property, value);
/* 108:134 */     fire(ModificationEvent.createModification(ModificationType.AFTER_MODIFY, super.getKey(item), item, property, value));
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void modify(ITEM item, String property, double value)
/* 112:    */   {
/* 113:138 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_MODIFY, super.getKey(item), item, property, value));
/* 114:139 */     super.modify(item, property, value);
/* 115:140 */     fire(ModificationEvent.createModification(ModificationType.AFTER_MODIFY, super.getKey(item), item, property, value));
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void modify(ITEM item, Update... values)
/* 119:    */   {
/* 120:144 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_MODIFY_BY_VALUE_SETTERS, super.getKey(item), item, null, values));
/* 121:145 */     super.modify(item, values);
/* 122:146 */     fire(ModificationEvent.createModification(ModificationType.AFTER_MODIFY_BY_VALUE_SETTERS, super.getKey(item), item, null, values));
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void update(KEY key, String property, Object value)
/* 126:    */   {
/* 127:150 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, value));
/* 128:151 */     super.update(key, property, value);
/* 129:152 */     fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, value));
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void updateByValue(KEY key, String property, String value)
/* 133:    */   {
/* 134:156 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, value));
/* 135:157 */     super.update(key, property, value);
/* 136:158 */     fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, value));
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void update(KEY key, String property, int value)
/* 140:    */   {
/* 141:162 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, value));
/* 142:163 */     super.update(key, property, value);
/* 143:164 */     fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, value));
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void update(KEY key, String property, long value)
/* 147:    */   {
/* 148:168 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, value));
/* 149:169 */     super.update(key, property, value);
/* 150:170 */     fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, value));
/* 151:    */   }
/* 152:    */   
/* 153:    */   public void update(KEY key, String property, char value)
/* 154:    */   {
/* 155:174 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, value));
/* 156:175 */     super.update(key, property, value);
/* 157:176 */     fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, value));
/* 158:    */   }
/* 159:    */   
/* 160:    */   public void update(KEY key, String property, short value)
/* 161:    */   {
/* 162:180 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, value));
/* 163:181 */     super.update(key, property, value);
/* 164:182 */     fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, value));
/* 165:    */   }
/* 166:    */   
/* 167:    */   public void update(KEY key, String property, byte value)
/* 168:    */   {
/* 169:186 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, value));
/* 170:187 */     super.update(key, property, value);
/* 171:188 */     fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, value));
/* 172:    */   }
/* 173:    */   
/* 174:    */   public void update(KEY key, String property, float value)
/* 175:    */   {
/* 176:192 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, value));
/* 177:193 */     super.update(key, property, value);
/* 178:194 */     fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, value));
/* 179:    */   }
/* 180:    */   
/* 181:    */   public void update(KEY key, String property, double value)
/* 182:    */   {
/* 183:198 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, value));
/* 184:199 */     super.update(key, property, value);
/* 185:200 */     fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, value));
/* 186:    */   }
/* 187:    */   
/* 188:    */   public void update(KEY key, Update... values)
/* 189:    */   {
/* 190:204 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE_BY_VALUE_SETTERS, key, (Object)null, null, values));
/* 191:205 */     super.update(key, values);
/* 192:206 */     fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE_BY_VALUE_SETTERS, key, (Object)null, null, values));
/* 193:    */   }
/* 194:    */   
/* 195:    */   public boolean compareAndUpdate(KEY key, String property, Object compare, Object value)
/* 196:    */   {
/* 197:210 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, Lists.list(new Object[] { compare, value })));
/* 198:211 */     boolean updated = super.compareAndUpdate(key, property, compare, value);
/* 199:212 */     if (updated) {
/* 200:213 */       fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, Lists.list(new Object[] { compare, value })));
/* 201:    */     }
/* 202:215 */     return updated;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public boolean compareAndUpdate(KEY key, String property, int compare, int value)
/* 206:    */   {
/* 207:219 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, Lists.list(new Integer[] { Integer.valueOf(compare), Integer.valueOf(value) })));
/* 208:220 */     boolean updated = super.compareAndUpdate(key, property, compare, value);
/* 209:221 */     if (updated) {
/* 210:222 */       fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, Lists.list(new Integer[] { Integer.valueOf(compare), Integer.valueOf(value) })));
/* 211:    */     }
/* 212:224 */     return updated;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public boolean compareAndUpdate(KEY key, String property, long compare, long value)
/* 216:    */   {
/* 217:228 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, Lists.list(new Long[] { Long.valueOf(compare), Long.valueOf(value) })));
/* 218:229 */     boolean updated = super.compareAndUpdate(key, property, compare, value);
/* 219:230 */     if (updated) {
/* 220:231 */       fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, Lists.list(new Long[] { Long.valueOf(compare), Long.valueOf(value) })));
/* 221:    */     }
/* 222:233 */     return updated;
/* 223:    */   }
/* 224:    */   
/* 225:    */   public boolean compareAndUpdate(KEY key, String property, char compare, char value)
/* 226:    */   {
/* 227:237 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, Lists.list(new Character[] { Character.valueOf(compare), Character.valueOf(value) })));
/* 228:238 */     boolean updated = super.compareAndUpdate(key, property, compare, value);
/* 229:239 */     if (updated) {
/* 230:240 */       fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, Lists.list(new Character[] { Character.valueOf(compare), Character.valueOf(value) })));
/* 231:    */     }
/* 232:242 */     return updated;
/* 233:    */   }
/* 234:    */   
/* 235:    */   public boolean compareAndUpdate(KEY key, String property, short compare, short value)
/* 236:    */   {
/* 237:246 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, Lists.list(new Short[] { Short.valueOf(compare), Short.valueOf(value) })));
/* 238:247 */     boolean updated = super.compareAndUpdate(key, property, compare, value);
/* 239:248 */     if (updated) {
/* 240:249 */       fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, Lists.list(new Short[] { Short.valueOf(compare), Short.valueOf(value) })));
/* 241:    */     }
/* 242:251 */     return updated;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public boolean compareAndUpdate(KEY key, String property, byte compare, byte value)
/* 246:    */   {
/* 247:255 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, Lists.list(new Byte[] { Byte.valueOf(compare), Byte.valueOf(value) })));
/* 248:256 */     boolean updated = super.compareAndUpdate(key, property, compare, value);
/* 249:257 */     if (updated) {
/* 250:258 */       fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, Lists.list(new Byte[] { Byte.valueOf(compare), Byte.valueOf(value) })));
/* 251:    */     }
/* 252:260 */     return updated;
/* 253:    */   }
/* 254:    */   
/* 255:    */   public boolean compareAndUpdate(KEY key, String property, float compare, float value)
/* 256:    */   {
/* 257:264 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, Lists.list(new Float[] { Float.valueOf(compare), Float.valueOf(value) })));
/* 258:265 */     boolean updated = super.compareAndUpdate(key, property, compare, value);
/* 259:266 */     if (updated) {
/* 260:267 */       fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, Lists.list(new Float[] { Float.valueOf(compare), Float.valueOf(value) })));
/* 261:    */     }
/* 262:269 */     return updated;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public boolean compareAndUpdate(KEY key, String property, double compare, double value)
/* 266:    */   {
/* 267:273 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_UPDATE, key, (Object)null, property, Lists.list(new Double[] { Double.valueOf(compare), Double.valueOf(value) })));
/* 268:274 */     boolean updated = super.compareAndUpdate(key, property, compare, value);
/* 269:275 */     if (updated) {
/* 270:276 */       fire(ModificationEvent.createModification(ModificationType.AFTER_UPDATE, key, (Object)null, property, Lists.list(new Double[] { Double.valueOf(compare), Double.valueOf(value) })));
/* 271:    */     }
/* 272:278 */     return updated;
/* 273:    */   }
/* 274:    */   
/* 275:    */   public boolean compareAndIncrement(KEY key, String property, int compare)
/* 276:    */   {
/* 277:282 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_INCREMENT, key, (Object)null, property, compare));
/* 278:283 */     boolean updated = super.compareAndIncrement(key, property, compare);
/* 279:284 */     fire(ModificationEvent.createModification(ModificationType.AFTER_INCREMENT, key, (Object)null, property, compare));
/* 280:285 */     return updated;
/* 281:    */   }
/* 282:    */   
/* 283:    */   public boolean compareAndIncrement(KEY key, String property, long compare)
/* 284:    */   {
/* 285:289 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_INCREMENT, key, (Object)null, property, compare));
/* 286:290 */     boolean updated = super.compareAndIncrement(key, property, compare);
/* 287:291 */     fire(ModificationEvent.createModification(ModificationType.AFTER_INCREMENT, key, (Object)null, property, compare));
/* 288:292 */     return updated;
/* 289:    */   }
/* 290:    */   
/* 291:    */   public boolean compareAndIncrement(KEY key, String property, short compare)
/* 292:    */   {
/* 293:296 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_INCREMENT, key, (Object)null, property, compare));
/* 294:297 */     boolean updated = super.compareAndIncrement(key, property, compare);
/* 295:298 */     fire(ModificationEvent.createModification(ModificationType.AFTER_INCREMENT, key, (Object)null, property, compare));
/* 296:299 */     return updated;
/* 297:    */   }
/* 298:    */   
/* 299:    */   public boolean compareAndIncrement(KEY key, String property, byte compare)
/* 300:    */   {
/* 301:303 */     fire(ModificationEvent.createModification(ModificationType.BEFORE_INCREMENT, key, (Object)null, property, compare));
/* 302:304 */     boolean updated = super.compareAndIncrement(key, property, compare);
/* 303:305 */     fire(ModificationEvent.createModification(ModificationType.AFTER_INCREMENT, key, (Object)null, property, compare));
/* 304:306 */     return updated;
/* 305:    */   }
/* 306:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.decorators.ObjectEditorEventDecorator
 * JD-Core Version:    0.7.0.1
 */