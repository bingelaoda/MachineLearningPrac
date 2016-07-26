/*   1:    */ package org.boon.datarepo.impl.indexes;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.text.Collator;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Collection;
/*   7:    */ import java.util.Collections;
/*   8:    */ import java.util.Comparator;
/*   9:    */ import java.util.List;
/*  10:    */ import java.util.Map.Entry;
/*  11:    */ import java.util.NavigableMap;
/*  12:    */ import java.util.SortedMap;
/*  13:    */ import org.boon.core.Function;
/*  14:    */ import org.boon.core.Supplier;
/*  15:    */ import org.boon.core.Typ;
/*  16:    */ import org.boon.datarepo.spi.MapCreator;
/*  17:    */ import org.boon.datarepo.spi.SPIFactory;
/*  18:    */ import org.boon.datarepo.spi.SearchIndex;
/*  19:    */ import org.boon.primitive.CharBuf;
/*  20:    */ 
/*  21:    */ public class SearchIndexDefault<KEY, ITEM>
/*  22:    */   extends LookupIndexDefault<KEY, ITEM>
/*  23:    */   implements SearchIndex<KEY, ITEM>
/*  24:    */ {
/*  25:    */   private NavigableMap<KEY, MultiValue> navigableMap;
/*  26:    */   private Comparator collator;
/*  27:    */   private Class<?> keyType;
/*  28:    */   boolean init;
/*  29:    */   
/*  30:    */   public SearchIndexDefault(Class<?> keyType)
/*  31:    */   {
/*  32: 57 */     super(keyType);
/*  33: 58 */     this.keyType = keyType;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public SearchIndexDefault(Class<?> keyType, List<ITEM> items, Function<ITEM, KEY> keyGetter)
/*  37:    */   {
/*  38: 65 */     super(null);
/*  39: 66 */     this.keyGetter = keyGetter;
/*  40: 67 */     this.map = ((MapCreator)SPIFactory.getMapCreatorFactory().get()).createNavigableMap(keyType);
/*  41: 68 */     this.navigableMap = ((NavigableMap)this.map);
/*  42: 70 */     for (ITEM item : items) {
/*  43: 71 */       add(item);
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public SearchIndexDefault(Class<?> keyType, List<ITEM> items, Function<ITEM, KEY> keyGetter, Collator collator)
/*  48:    */   {
/*  49: 78 */     super(null);
/*  50: 79 */     this.keyGetter = keyGetter;
/*  51: 80 */     this.map = ((MapCreator)SPIFactory.getMapCreatorFactory().get()).createNavigableMap(keyType, collator);
/*  52: 81 */     this.navigableMap = ((NavigableMap)this.map);
/*  53: 83 */     for (ITEM item : items) {
/*  54: 84 */       add(item);
/*  55:    */     }
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setComparator(Comparator collator)
/*  59:    */   {
/*  60: 92 */     this.collator = collator;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void init()
/*  64:    */   {
/*  65: 98 */     if (this.collator != null) {
/*  66: 99 */       this.map = ((MapCreator)SPIFactory.getMapCreatorFactory().get()).createNavigableMap(this.keyType, this.collator);
/*  67:102 */     } else if (this.keyType == Typ.number) {
/*  68:103 */       this.map = ((MapCreator)SPIFactory.getMapCreatorFactory().get()).createNavigableMap(this.keyType, new Comparator()
/*  69:    */       {
/*  70:    */         public int compare(Number o1, Number o2)
/*  71:    */         {
/*  72:107 */           if ((o1 instanceof Long))
/*  73:    */           {
/*  74:108 */             long long1 = o1.longValue();
/*  75:109 */             long long2 = o2.longValue();
/*  76:110 */             if (long1 > long2) {
/*  77:111 */               return 1;
/*  78:    */             }
/*  79:112 */             if (long1 < long2) {
/*  80:113 */               return -1;
/*  81:    */             }
/*  82:115 */             return 0;
/*  83:    */           }
/*  84:117 */           if ((o1 instanceof Double))
/*  85:    */           {
/*  86:118 */             double long1 = o1.doubleValue();
/*  87:119 */             double long2 = o2.doubleValue();
/*  88:120 */             if (long1 > long2) {
/*  89:121 */               return 1;
/*  90:    */             }
/*  91:122 */             if (long1 < long2) {
/*  92:123 */               return -1;
/*  93:    */             }
/*  94:125 */             return 0;
/*  95:    */           }
/*  96:127 */           if ((o1 instanceof BigDecimal))
/*  97:    */           {
/*  98:128 */             double long1 = o1.doubleValue();
/*  99:129 */             double long2 = o2.doubleValue();
/* 100:130 */             if (long1 > long2) {
/* 101:131 */               return 1;
/* 102:    */             }
/* 103:132 */             if (long1 < long2) {
/* 104:133 */               return -1;
/* 105:    */             }
/* 106:135 */             return 0;
/* 107:    */           }
/* 108:138 */           double long1 = o1.doubleValue();
/* 109:139 */           double long2 = o2.doubleValue();
/* 110:140 */           if (long1 > long2) {
/* 111:141 */             return 1;
/* 112:    */           }
/* 113:142 */           if (long1 < long2) {
/* 114:143 */             return -1;
/* 115:    */           }
/* 116:145 */           return 0;
/* 117:    */         }
/* 118:    */       });
/* 119:    */     } else {
/* 120:152 */       this.map = ((MapCreator)SPIFactory.getMapCreatorFactory().get()).createNavigableMap(this.keyType);
/* 121:    */     }
/* 122:157 */     this.navigableMap = ((NavigableMap)this.map);
/* 123:    */   }
/* 124:    */   
/* 125:    */   public ITEM findFirst()
/* 126:    */   {
/* 127:164 */     return ((MultiValue)this.navigableMap.firstEntry().getValue()).getValue();
/* 128:    */   }
/* 129:    */   
/* 130:    */   public ITEM findLast()
/* 131:    */   {
/* 132:169 */     return ((MultiValue)this.navigableMap.lastEntry().getValue()).getValue();
/* 133:    */   }
/* 134:    */   
/* 135:    */   public KEY findFirstKey()
/* 136:    */   {
/* 137:174 */     return this.navigableMap.firstEntry().getKey();
/* 138:    */   }
/* 139:    */   
/* 140:    */   public KEY findLastKey()
/* 141:    */   {
/* 142:179 */     return this.navigableMap.lastEntry().getKey();
/* 143:    */   }
/* 144:    */   
/* 145:    */   public List<ITEM> findEquals(KEY key)
/* 146:    */   {
/* 147:184 */     key = getKey(key);
/* 148:185 */     MultiValue<ITEM> items = (MultiValue)this.navigableMap.get(key);
/* 149:186 */     if (items == null) {
/* 150:187 */       return null;
/* 151:    */     }
/* 152:189 */     return items.getValues();
/* 153:    */   }
/* 154:    */   
/* 155:    */   public List<ITEM> findStartsWith(KEY keyFrag)
/* 156:    */   {
/* 157:195 */     keyFrag = getKey(keyFrag);
/* 158:198 */     if ((keyFrag instanceof String))
/* 159:    */     {
/* 160:199 */       String start = (String)keyFrag;
/* 161:200 */       if ((start.length() == 0) || (start == null)) {
/* 162:201 */         return Collections.EMPTY_LIST;
/* 163:    */       }
/* 164:204 */       char endLetter = start.charAt(start.length() - 1);
/* 165:205 */       String sub = start.substring(0, start.length() - 1);
/* 166:    */       
/* 167:207 */       CharBuf after = CharBuf.create(start.length());
/* 168:    */       
/* 169:209 */       after.add(sub);
/* 170:210 */       after.add((char)(endLetter + '\001'));
/* 171:    */       
/* 172:212 */       NavigableMap<String, MultiValue> sortMap = this.navigableMap;
/* 173:    */       
/* 174:    */ 
/* 175:215 */       SortedMap<String, MultiValue> sortedSubMap = sortMap.subMap(start, after.toString());
/* 176:217 */       if (sortedSubMap.size() > 0)
/* 177:    */       {
/* 178:218 */         List<ITEM> results = new ArrayList();
/* 179:219 */         for (MultiValue values : sortedSubMap.values()) {
/* 180:220 */           values.addTo(results);
/* 181:    */         }
/* 182:222 */         return results;
/* 183:    */       }
/* 184:224 */       return Collections.EMPTY_LIST;
/* 185:    */     }
/* 186:226 */     return Collections.EMPTY_LIST;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public List<ITEM> findEndsWith(KEY keyFrag)
/* 190:    */   {
/* 191:232 */     keyFrag = getKey(keyFrag);
/* 192:    */     
/* 193:234 */     List<ITEM> results = new ArrayList();
/* 194:236 */     if ((keyFrag instanceof String))
/* 195:    */     {
/* 196:238 */       Collection<MultiValue> values = this.navigableMap.values();
/* 197:239 */       for (MultiValue<ITEM> mv : values) {
/* 198:240 */         for (ITEM value : mv.getValues())
/* 199:    */         {
/* 200:241 */           String svalue = (String)this.keyGetter.apply(value);
/* 201:242 */           if (svalue.endsWith((String)keyFrag)) {
/* 202:243 */             results.add(value);
/* 203:    */           }
/* 204:    */         }
/* 205:    */       }
/* 206:    */     }
/* 207:248 */     return results;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public List<ITEM> findContains(KEY keyFrag)
/* 211:    */   {
/* 212:253 */     keyFrag = getKey(keyFrag);
/* 213:    */     
/* 214:255 */     List<ITEM> results = new ArrayList();
/* 215:257 */     if ((keyFrag instanceof String))
/* 216:    */     {
/* 217:259 */       Collection<MultiValue> values = this.navigableMap.values();
/* 218:260 */       for (MultiValue<ITEM> mv : values) {
/* 219:261 */         for (ITEM value : mv.getValues())
/* 220:    */         {
/* 221:263 */           String svalue = (String)this.keyGetter.apply(value);
/* 222:264 */           if (svalue.endsWith((String)keyFrag)) {
/* 223:265 */             results.add(value);
/* 224:    */           }
/* 225:    */         }
/* 226:    */       }
/* 227:    */     }
/* 228:270 */     return results;
/* 229:    */   }
/* 230:    */   
/* 231:    */   void initIfNeeded()
/* 232:    */   {
/* 233:    */     ITEM item;
/* 234:277 */     if (!this.init)
/* 235:    */     {
/* 236:278 */       this.init = true;
/* 237:279 */       item = ((MultiValue)this.navigableMap.firstEntry()).getValue();
/* 238:    */     }
/* 239:    */   }
/* 240:    */   
/* 241:    */   public List<ITEM> findBetween(KEY start, KEY end)
/* 242:    */   {
/* 243:286 */     start = getKey(start);
/* 244:287 */     end = getKey(end);
/* 245:    */     
/* 246:    */ 
/* 247:290 */     SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.subMap(start, end);
/* 248:    */     
/* 249:292 */     return getResults(keyMultiValueSortedMap);
/* 250:    */   }
/* 251:    */   
/* 252:    */   private List<ITEM> getResults(SortedMap<KEY, MultiValue> keyMultiValueSortedMap)
/* 253:    */   {
/* 254:297 */     List<ITEM> results = null;
/* 255:298 */     if (keyMultiValueSortedMap.size() > 0)
/* 256:    */     {
/* 257:299 */       results = new ArrayList();
/* 258:300 */       for (MultiValue<ITEM> values : keyMultiValueSortedMap.values()) {
/* 259:301 */         values.addTo(results);
/* 260:    */       }
/* 261:303 */       return results;
/* 262:    */     }
/* 263:305 */     return Collections.EMPTY_LIST;
/* 264:    */   }
/* 265:    */   
/* 266:    */   public List<ITEM> findGreaterThan(KEY key)
/* 267:    */   {
/* 268:310 */     key = getKey(key);
/* 269:    */     
/* 270:    */ 
/* 271:313 */     SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.tailMap(key, false);
/* 272:314 */     return getResults(keyMultiValueSortedMap);
/* 273:    */   }
/* 274:    */   
/* 275:    */   public List<ITEM> findLessThan(KEY key)
/* 276:    */   {
/* 277:319 */     key = getKey(key);
/* 278:    */     
/* 279:321 */     SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.headMap(key, false);
/* 280:322 */     return getResults(keyMultiValueSortedMap);
/* 281:    */   }
/* 282:    */   
/* 283:    */   public List<ITEM> findGreaterThanEqual(KEY key)
/* 284:    */   {
/* 285:327 */     key = getKey(key);
/* 286:    */     
/* 287:329 */     SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.tailMap(key);
/* 288:330 */     return getResults(keyMultiValueSortedMap);
/* 289:    */   }
/* 290:    */   
/* 291:    */   public List<ITEM> findLessThanEqual(KEY key)
/* 292:    */   {
/* 293:335 */     key = getKey(key);
/* 294:    */     
/* 295:337 */     SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.headMap(key);
/* 296:338 */     return getResults(keyMultiValueSortedMap);
/* 297:    */   }
/* 298:    */   
/* 299:    */   public ITEM min()
/* 300:    */   {
/* 301:344 */     return ((MultiValue)this.navigableMap.firstEntry().getValue()).getValue();
/* 302:    */   }
/* 303:    */   
/* 304:    */   public ITEM max()
/* 305:    */   {
/* 306:349 */     return ((MultiValue)this.navigableMap.lastEntry().getValue()).getValue();
/* 307:    */   }
/* 308:    */   
/* 309:    */   public int count(KEY key)
/* 310:    */   {
/* 311:354 */     return ((MultiValue)this.navigableMap.get(key)).size();
/* 312:    */   }
/* 313:    */   
/* 314:    */   public int size()
/* 315:    */   {
/* 316:360 */     return this.navigableMap.size();
/* 317:    */   }
/* 318:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.indexes.SearchIndexDefault
 * JD-Core Version:    0.7.0.1
 */