/*   1:    */ package org.boon.datarepo.impl.indexes;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.logging.Level;
/*   8:    */ import java.util.logging.Logger;
/*   9:    */ import org.boon.Exceptions;
/*  10:    */ import org.boon.core.Function;
/*  11:    */ import org.boon.core.Supplier;
/*  12:    */ import org.boon.datarepo.LookupIndex;
/*  13:    */ import org.boon.datarepo.spi.MapCreator;
/*  14:    */ import org.boon.datarepo.spi.SPIFactory;
/*  15:    */ 
/*  16:    */ public class LookupIndexDefault<KEY, ITEM>
/*  17:    */   implements LookupIndex<KEY, ITEM>
/*  18:    */ {
/*  19:    */   protected Function<ITEM, KEY> keyGetter;
/*  20:    */   protected Function<ITEM, KEY> primaryKeyGetter;
/*  21:    */   protected Map<KEY, MultiValue> map;
/*  22: 54 */   private Logger log = Logger.getLogger(LookupIndexDefault.class.getName());
/*  23:    */   protected boolean storeKeyInIndexOnly;
/*  24:    */   private Function<Object, KEY> keyTransformer;
/*  25: 58 */   protected int keyBucketSize = 3;
/*  26:    */   
/*  27:    */   public LookupIndexDefault(Class<?> keyType)
/*  28:    */   {
/*  29: 64 */     if (this.log.isLoggable(Level.FINE)) {
/*  30: 65 */       this.log.fine(String.format("key type %s ", new Object[] { keyType.getName() }));
/*  31:    */     }
/*  32: 68 */     if (keyType == null) {
/*  33: 69 */       return;
/*  34:    */     }
/*  35: 71 */     this.map = ((MapCreator)SPIFactory.getMapCreatorFactory().get()).createMap(keyType);
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected void addManyKeys(ITEM item, List<KEY> keys)
/*  39:    */   {
/*  40: 77 */     for (KEY key : keys) {
/*  41: 78 */       if (key != null) {
/*  42: 79 */         put(item, key);
/*  43:    */       }
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public boolean add(ITEM item)
/*  48:    */   {
/*  49: 87 */     if (this.log.isLoggable(Level.FINE)) {
/*  50: 88 */       this.log.fine(String.format("addObject item = %s", new Object[] { item }));
/*  51:    */     }
/*  52: 91 */     KEY key = this.keyGetter.apply(item);
/*  53: 92 */     if (key == null) {
/*  54: 93 */       return false;
/*  55:    */     }
/*  56: 96 */     put(item, key);
/*  57: 97 */     return true;
/*  58:    */   }
/*  59:    */   
/*  60:    */   private void put(ITEM item, KEY key)
/*  61:    */   {
/*  62:103 */     MultiValue mv = null;
/*  63:104 */     Object primaryKey = null;
/*  64:    */     try
/*  65:    */     {
/*  66:108 */       if (this.log.isLoggable(Level.FINE)) {
/*  67:109 */         this.log.fine(String.format("put item = %s with key = %s ", new Object[] { item, key }));
/*  68:    */       }
/*  69:112 */       key = getKey(key);
/*  70:115 */       if ((key instanceof Collection))
/*  71:    */       {
/*  72:116 */         Collection collection = (Collection)key;
/*  73:118 */         for (Object keyComponent : collection) {
/*  74:120 */           if (keyComponent != null)
/*  75:    */           {
/*  76:123 */             mv = (MultiValue)this.map.get(keyComponent);
/*  77:124 */             mv = mvCreateOrAddToMV(mv, item);
/*  78:125 */             this.map.put(keyComponent, mv);
/*  79:    */           }
/*  80:    */         }
/*  81:127 */         return;
/*  82:    */       }
/*  83:131 */       mv = (MultiValue)this.map.get(key);
/*  84:132 */       if (this.storeKeyInIndexOnly)
/*  85:    */       {
/*  86:133 */         primaryKey = this.primaryKeyGetter.apply(item);
/*  87:    */         
/*  88:135 */         mv = mvCreateOrAddToMV(mv, primaryKey);
/*  89:    */       }
/*  90:    */       else
/*  91:    */       {
/*  92:137 */         mv = mvCreateOrAddToMV(mv, item);
/*  93:    */       }
/*  94:140 */       this.map.put(key, mv);
/*  95:    */     }
/*  96:    */     catch (Exception ex)
/*  97:    */     {
/*  98:143 */       Exceptions.handle(ex, new Object[] { "Problem putting item in lookupWithDefault index, item=", item, "key=", key, "mv=", mv, "primaryKey=", primaryKey });
/*  99:    */     }
/* 100:    */   }
/* 101:    */   
/* 102:    */   private MultiValue mvCreateOrAddToMV(MultiValue mv, Object obj)
/* 103:    */   {
/* 104:149 */     return MultiValue.add(mv, obj, this.keyBucketSize);
/* 105:    */   }
/* 106:    */   
/* 107:    */   protected final void removeManyKeys(ITEM item, List<KEY> keys)
/* 108:    */   {
/* 109:154 */     for (KEY key : keys) {
/* 110:155 */       if (key != null) {
/* 111:156 */         removeKey(item, key);
/* 112:    */       }
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   public boolean delete(ITEM item)
/* 117:    */   {
/* 118:163 */     KEY key = this.keyGetter.apply(item);
/* 119:    */     
/* 120:    */ 
/* 121:166 */     return removeKey(item, key);
/* 122:    */   }
/* 123:    */   
/* 124:    */   private boolean removeKey(ITEM item, KEY key)
/* 125:    */   {
/* 126:171 */     key = getKey(key);
/* 127:173 */     if (key == null) {
/* 128:174 */       return false;
/* 129:    */     }
/* 130:177 */     if ((key instanceof Collection))
/* 131:    */     {
/* 132:178 */       Collection collection = (Collection)key;
/* 133:179 */       for (Object objKey : collection) {
/* 134:180 */         removeKey(item, objKey);
/* 135:    */       }
/* 136:    */     }
/* 137:    */     else
/* 138:    */     {
/* 139:183 */       MultiValue mv = (MultiValue)this.map.get(key);
/* 140:185 */       if (mv == null) {
/* 141:186 */         return false;
/* 142:    */       }
/* 143:189 */       mv = MultiValue.remove(mv, item);
/* 144:191 */       if (mv == null) {
/* 145:192 */         this.map.remove(key);
/* 146:    */       }
/* 147:    */     }
/* 148:195 */     return true;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void setKeyGetter(Function<ITEM, KEY> keyGetter)
/* 152:    */   {
/* 153:201 */     Exceptions.requireNonNull(keyGetter, "keyGetter cannot be null");
/* 154:202 */     this.keyGetter = keyGetter;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setPrimaryKeyGetter(Function<ITEM, KEY> keyGetter)
/* 158:    */   {
/* 159:206 */     Exceptions.requireNonNull(keyGetter, "keyGetter cannot be null");
/* 160:207 */     this.storeKeyInIndexOnly = true;
/* 161:208 */     this.primaryKeyGetter = keyGetter;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public List<ITEM> all()
/* 165:    */   {
/* 166:214 */     if (this.log.isLoggable(Level.FINE)) {
/* 167:215 */       this.log.fine("all called");
/* 168:    */     }
/* 169:218 */     List results = new ArrayList(this.map.size());
/* 170:219 */     for (MultiValue values : this.map.values()) {
/* 171:220 */       values.addTo(results);
/* 172:    */     }
/* 173:222 */     return results;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public int size()
/* 177:    */   {
/* 178:227 */     return this.map.size();
/* 179:    */   }
/* 180:    */   
/* 181:    */   public Collection<ITEM> toCollection()
/* 182:    */   {
/* 183:232 */     return this.map.values();
/* 184:    */   }
/* 185:    */   
/* 186:    */   public ITEM get(KEY key)
/* 187:    */   {
/* 188:239 */     key = getKey(key);
/* 189:    */     
/* 190:241 */     MultiValue mv = (MultiValue)this.map.get(key);
/* 191:242 */     if (mv == null) {
/* 192:243 */       return null;
/* 193:    */     }
/* 194:245 */     return mv.getValue();
/* 195:    */   }
/* 196:    */   
/* 197:    */   protected KEY getKey(KEY key)
/* 198:    */   {
/* 199:250 */     if (this.keyTransformer != null) {
/* 200:251 */       key = this.keyTransformer.apply(key);
/* 201:    */     }
/* 202:253 */     return key;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public List<ITEM> getAll(KEY key)
/* 206:    */   {
/* 207:258 */     key = getKey(key);
/* 208:    */     
/* 209:260 */     MultiValue mv = (MultiValue)this.map.get(key);
/* 210:261 */     if (mv == null) {
/* 211:262 */       return null;
/* 212:    */     }
/* 213:264 */     return mv.getValues();
/* 214:    */   }
/* 215:    */   
/* 216:    */   public boolean deleteByKey(KEY key)
/* 217:    */   {
/* 218:270 */     key = getKey(key);
/* 219:    */     
/* 220:272 */     this.map.remove(key);
/* 221:273 */     return true;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public boolean isPrimaryKeyOnly()
/* 225:    */   {
/* 226:281 */     return this.storeKeyInIndexOnly;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public void setInputKeyTransformer(Function<Object, KEY> func)
/* 230:    */   {
/* 231:286 */     this.keyTransformer = func;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public void setBucketSize(int size)
/* 235:    */   {
/* 236:291 */     this.keyBucketSize = size;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public void init() {}
/* 240:    */   
/* 241:    */   public boolean has(KEY key)
/* 242:    */   {
/* 243:300 */     return this.map.containsKey(key);
/* 244:    */   }
/* 245:    */   
/* 246:    */   public void clear()
/* 247:    */   {
/* 248:307 */     if (this.log.isLoggable(Level.FINE)) {
/* 249:308 */       this.log.fine("clear called");
/* 250:    */     }
/* 251:310 */     this.map.clear();
/* 252:    */   }
/* 253:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.indexes.LookupIndexDefault
 * JD-Core Version:    0.7.0.1
 */