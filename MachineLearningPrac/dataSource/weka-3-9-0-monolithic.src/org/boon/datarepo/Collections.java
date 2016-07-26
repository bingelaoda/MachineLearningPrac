/*   1:    */ package org.boon.datarepo;
/*   2:    */ 
/*   3:    */ import java.util.AbstractList;
/*   4:    */ import java.util.AbstractSet;
/*   5:    */ import java.util.Collection;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.LinkedHashMap;
/*   8:    */ import java.util.List;
/*   9:    */ import java.util.Map;
/*  10:    */ import java.util.Set;
/*  11:    */ import org.boon.Exceptions;
/*  12:    */ import org.boon.core.Function;
/*  13:    */ import org.boon.core.Supplier;
/*  14:    */ import org.boon.core.Typ;
/*  15:    */ import org.boon.core.reflection.BeanUtils;
/*  16:    */ import org.boon.core.reflection.Fields;
/*  17:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  18:    */ import org.boon.criteria.internal.Criteria;
/*  19:    */ import org.boon.datarepo.impl.decorators.FilterWithSimpleCache;
/*  20:    */ import org.boon.datarepo.spi.SPIFactory;
/*  21:    */ import org.boon.datarepo.spi.SearchIndex;
/*  22:    */ import org.boon.datarepo.spi.SearchableCollectionComposer;
/*  23:    */ 
/*  24:    */ public class Collections
/*  25:    */ {
/*  26:    */   public static <T> List<T> $q(List<T> list, Class<?>... classes)
/*  27:    */   {
/*  28: 61 */     return listQuery(list, true, true, classes);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public static <T> List<T> $c(List<T> list)
/*  32:    */   {
/*  33: 74 */     return plainList(list);
/*  34:    */   }
/*  35:    */   
/*  36:    */   private static <T> List<T> plainList(List<T> list)
/*  37:    */   {
/*  38: 87 */     if ((list instanceof QList)) {
/*  39: 88 */       return ((QList)list).list;
/*  40:    */     }
/*  41: 90 */     return list;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public static <T> List<T> listQuery(List<T> list)
/*  45:    */   {
/*  46:104 */     return listQuery(list, true, true, new Class[0]);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static <T> List<T> listQuery(List<T> list, boolean useField, boolean useUnSafe, Class<?>... classes)
/*  50:    */   {
/*  51:119 */     if ((list == null) || (list.size() == 0)) {
/*  52:120 */       return list;
/*  53:    */     }
/*  54:123 */     SearchableCollectionComposer query = null;
/*  55:125 */     if ((classes == null) || (classes.length == 0))
/*  56:    */     {
/*  57:126 */       Class<?> clazz = list.get(0).getClass();
/*  58:    */       
/*  59:128 */       query = getSearchableCollectionComposer(list, useField, useUnSafe, new Class[] { clazz });
/*  60:    */     }
/*  61:    */     else
/*  62:    */     {
/*  63:131 */       query = getSearchableCollectionComposer(list, useField, useUnSafe, classes);
/*  64:    */     }
/*  65:135 */     return new QList(list, (SearchableCollection)query);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static <T> Set<T> $q(Set<T> set)
/*  69:    */   {
/*  70:146 */     return setQuery(set, true, true);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public static <T> Set<T> $c(Set<T> set)
/*  74:    */   {
/*  75:157 */     return plainSet(set);
/*  76:    */   }
/*  77:    */   
/*  78:    */   private static <T> Set<T> plainSet(Set<T> set)
/*  79:    */   {
/*  80:169 */     if ((set instanceof QSet)) {
/*  81:170 */       return ((QSet)set).set;
/*  82:    */     }
/*  83:172 */     return set;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static <T> Set<T> setQuery(Set<T> set)
/*  87:    */   {
/*  88:184 */     return setQuery(set, true, true);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static <T> Set<T> setQuery(Set<T> set, boolean useField, boolean useUnSafe)
/*  92:    */   {
/*  93:197 */     if ((set == null) || (set.size() == 0)) {
/*  94:198 */       return set;
/*  95:    */     }
/*  96:201 */     Class<?> clazz = set.iterator().next().getClass();
/*  97:    */     
/*  98:203 */     SearchableCollectionComposer query = getSearchableCollectionComposer(set, useField, useUnSafe, new Class[] { clazz });
/*  99:    */     
/* 100:205 */     return new QSet(set, (SearchableCollection)query);
/* 101:    */   }
/* 102:    */   
/* 103:    */   private static <T> SearchableCollectionComposer getSearchableCollectionComposer(Collection set, boolean useField, boolean useUnSafe, Class<?>... classes)
/* 104:    */   {
/* 105:219 */     SearchableCollectionComposer query = (SearchableCollectionComposer)SPIFactory.getSearchableCollectionFactory().get();
/* 106:    */     
/* 107:    */ 
/* 108:222 */     Map<String, FieldAccess> fields = new LinkedHashMap();
/* 109:    */     Map<String, FieldAccess> fieldsSubType;
/* 110:224 */     for (Class<?> cls : classes)
/* 111:    */     {
/* 112:226 */       fieldsSubType = BeanUtils.getFieldsFromObject(cls);
/* 113:229 */       for (String sKey : fieldsSubType.keySet()) {
/* 114:230 */         if (!fields.containsKey(sKey)) {
/* 115:231 */           fields.put(sKey, fieldsSubType.get(sKey));
/* 116:    */         }
/* 117:    */       }
/* 118:    */     }
/* 119:238 */     String primaryKey = findPrimaryKey(fields);
/* 120:239 */     FieldAccess field = (FieldAccess)fields.get(primaryKey);
/* 121:240 */     Function keyGetter = createKeyGetter(field);
/* 122:    */     
/* 123:242 */     query.setFields(fields);
/* 124:243 */     query.setPrimaryKeyGetter(keyGetter);
/* 125:244 */     query.setPrimaryKeyName(primaryKey);
/* 126:245 */     Filter filter = (Filter)SPIFactory.getFilterFactory().get();
/* 127:246 */     query.setFilter(filter);
/* 128:    */     
/* 129:    */ 
/* 130:249 */     LookupIndex index = (LookupIndex)SPIFactory.getUniqueLookupIndexFactory().apply(((FieldAccess)fields.get(primaryKey)).type());
/* 131:250 */     index.setKeyGetter(keyGetter);
/* 132:251 */     ((SearchableCollection)query).addLookupIndex(primaryKey, index);
/* 133:254 */     for (FieldAccess f : fields.values()) {
/* 134:255 */       if (!f.name().equals(primaryKey)) {
/* 135:258 */         if (Typ.isBasicType(f.type())) {
/* 136:259 */           configIndexes((SearchableCollection)query, f.name(), fields);
/* 137:    */         }
/* 138:    */       }
/* 139:    */     }
/* 140:263 */     query.init();
/* 141:    */     
/* 142:265 */     query.setFilter(new FilterWithSimpleCache(filter));
/* 143:    */     
/* 144:267 */     ((SearchableCollection)query).addAll(set);
/* 145:268 */     return query;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public static <T> List<T> query(List<T> list, Criteria... expressions)
/* 149:    */   {
/* 150:281 */     if ((list instanceof QList))
/* 151:    */     {
/* 152:282 */       QList qlist = (QList)list;
/* 153:283 */       return qlist.searchCollection().query(expressions);
/* 154:    */     }
/* 155:285 */     throw new DataRepoException("Not a criteria-able listStream.");
/* 156:    */   }
/* 157:    */   
/* 158:    */   public static <T> List<T> sortedQuery(List<T> list, String sortBy, Criteria... expressions)
/* 159:    */   {
/* 160:298 */     if ((list instanceof QList))
/* 161:    */     {
/* 162:299 */       QList qlist = (QList)list;
/* 163:300 */       return qlist.searchCollection().sortedQuery(sortBy, expressions);
/* 164:    */     }
/* 165:302 */     throw new DataRepoException("Not a criteria-able listStream.");
/* 166:    */   }
/* 167:    */   
/* 168:    */   public static <T> List<T> query(Set<T> set, Criteria... expressions)
/* 169:    */   {
/* 170:316 */     if ((set instanceof QSet))
/* 171:    */     {
/* 172:317 */       QSet qset = (QSet)set;
/* 173:318 */       return qset.searchCollection().query(expressions);
/* 174:    */     }
/* 175:320 */     return null;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public static <T> List<T> sortedQuery(Set<T> set, String sortBy, Criteria... expressions)
/* 179:    */   {
/* 180:332 */     if ((set instanceof QSet))
/* 181:    */     {
/* 182:333 */       QSet qset = (QSet)set;
/* 183:334 */       return qset.searchCollection().sortedQuery(sortBy, expressions);
/* 184:    */     }
/* 185:336 */     return null;
/* 186:    */   }
/* 187:    */   
/* 188:    */   private static String findPrimaryKey(Map<String, FieldAccess> fields)
/* 189:    */   {
/* 190:347 */     return "id";
/* 191:    */   }
/* 192:    */   
/* 193:    */   private static Function createKeyGetter(FieldAccess field)
/* 194:    */   {
/* 195:359 */     Exceptions.requireNonNull(field, "field cannot be null");
/* 196:360 */     new Function()
/* 197:    */     {
/* 198:    */       public Object apply(Object o)
/* 199:    */       {
/* 200:364 */         if (Fields.hasField(o.getClass(), this.val$field.name())) {
/* 201:365 */           return this.val$field.getValue(o);
/* 202:    */         }
/* 203:367 */         return null;
/* 204:    */       }
/* 205:    */     };
/* 206:    */   }
/* 207:    */   
/* 208:    */   static class QSet<T>
/* 209:    */     extends AbstractSet<T>
/* 210:    */     implements CollectionDecorator
/* 211:    */   {
/* 212:    */     final Set<T> set;
/* 213:    */     final SearchableCollection searchCollection;
/* 214:    */     
/* 215:    */     QSet(Set<T> set, SearchableCollection searchCollection)
/* 216:    */     {
/* 217:385 */       this.set = set;
/* 218:386 */       this.searchCollection = searchCollection;
/* 219:    */     }
/* 220:    */     
/* 221:    */     public boolean add(T item)
/* 222:    */     {
/* 223:391 */       this.searchCollection.add(item);
/* 224:392 */       return this.set.add(item);
/* 225:    */     }
/* 226:    */     
/* 227:    */     public boolean remove(Object item)
/* 228:    */     {
/* 229:397 */       this.searchCollection.delete(item);
/* 230:398 */       return this.set.remove(item);
/* 231:    */     }
/* 232:    */     
/* 233:    */     public Iterator<T> iterator()
/* 234:    */     {
/* 235:404 */       return this.set.iterator();
/* 236:    */     }
/* 237:    */     
/* 238:    */     public int size()
/* 239:    */     {
/* 240:409 */       return this.set.size();
/* 241:    */     }
/* 242:    */     
/* 243:    */     public SearchableCollection searchCollection()
/* 244:    */     {
/* 245:414 */       return this.searchCollection;
/* 246:    */     }
/* 247:    */     
/* 248:    */     public Collection collection()
/* 249:    */     {
/* 250:419 */       return this.set;
/* 251:    */     }
/* 252:    */   }
/* 253:    */   
/* 254:    */   static class QList<T>
/* 255:    */     extends AbstractList<T>
/* 256:    */     implements CollectionDecorator
/* 257:    */   {
/* 258:    */     List<T> list;
/* 259:    */     SearchableCollection query;
/* 260:    */     
/* 261:    */     QList(List<T> list, SearchableCollection query)
/* 262:    */     {
/* 263:431 */       this.list = list;
/* 264:432 */       this.query = query;
/* 265:    */     }
/* 266:    */     
/* 267:    */     public boolean add(T item)
/* 268:    */     {
/* 269:437 */       this.query.add(item);
/* 270:438 */       return this.list.add(item);
/* 271:    */     }
/* 272:    */     
/* 273:    */     public boolean remove(Object item)
/* 274:    */     {
/* 275:443 */       this.query.delete(item);
/* 276:444 */       return this.list.remove(item);
/* 277:    */     }
/* 278:    */     
/* 279:    */     public T get(int index)
/* 280:    */     {
/* 281:450 */       return this.list.get(index);
/* 282:    */     }
/* 283:    */     
/* 284:    */     public int size()
/* 285:    */     {
/* 286:456 */       return this.list.size();
/* 287:    */     }
/* 288:    */     
/* 289:    */     public SearchableCollection searchCollection()
/* 290:    */     {
/* 291:462 */       return this.query;
/* 292:    */     }
/* 293:    */     
/* 294:    */     public Collection collection()
/* 295:    */     {
/* 296:467 */       return this.list;
/* 297:    */     }
/* 298:    */   }
/* 299:    */   
/* 300:    */   private static void configIndexes(SearchableCollection query, String prop, Map<String, FieldAccess> fields)
/* 301:    */   {
/* 302:482 */     SearchIndex searchIndex = (SearchIndex)SPIFactory.getSearchIndexFactory().apply(((FieldAccess)fields.get(prop)).type());
/* 303:483 */     searchIndex.init();
/* 304:484 */     Function kg = createKeyGetter((FieldAccess)fields.get(prop));
/* 305:485 */     searchIndex.setKeyGetter(kg);
/* 306:486 */     query.addSearchIndex(prop, searchIndex);
/* 307:    */     
/* 308:488 */     LookupIndex index = (LookupIndex)SPIFactory.getLookupIndexFactory().apply(((FieldAccess)fields.get(prop)).type());
/* 309:489 */     index.setKeyGetter(kg);
/* 310:490 */     query.addLookupIndex(prop, index);
/* 311:    */   }
/* 312:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.Collections
 * JD-Core Version:    0.7.0.1
 */