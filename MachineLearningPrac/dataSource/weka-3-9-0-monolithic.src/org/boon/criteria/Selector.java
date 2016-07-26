/*   1:    */ package org.boon.criteria;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.LinkedHashMap;
/*   8:    */ import java.util.List;
/*   9:    */ import java.util.Map;
/*  10:    */ import org.boon.Boon;
/*  11:    */ import org.boon.Lists;
/*  12:    */ import org.boon.core.Function;
/*  13:    */ import org.boon.core.reflection.BeanUtils;
/*  14:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  15:    */ import org.boon.template.Template;
/*  16:    */ 
/*  17:    */ public abstract class Selector
/*  18:    */ {
/*  19:    */   protected String name;
/*  20:    */   protected String alias;
/*  21:    */   protected final boolean path;
/*  22:    */   
/*  23:    */   public static void collectFrom(List<Selector> selectors, Collection<?> results)
/*  24:    */   {
/*  25: 55 */     if (results.size() == 0) {
/*  26: 56 */       return;
/*  27:    */     }
/*  28: 58 */     Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject(results.iterator().next());
/*  29:    */     
/*  30: 60 */     collectFrom(selectors, results, fields);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public static void collectFrom(List<Selector> selectors, Collection<?> results, Map<String, FieldAccess> fields)
/*  34:    */   {
/*  35: 73 */     for (Selector s : selectors) {
/*  36: 74 */       s.handleStart(results);
/*  37:    */     }
/*  38: 78 */     int index = 0;
/*  39: 79 */     for (Object item : results)
/*  40:    */     {
/*  41: 80 */       for (Selector s : selectors) {
/*  42: 81 */         s.handleRow(index, null, item, fields);
/*  43:    */       }
/*  44: 83 */       index++;
/*  45:    */     }
/*  46: 86 */     for (Selector s : selectors) {
/*  47: 87 */       s.handleComplete(null);
/*  48:    */     }
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static <ITEM> List<Map<String, Object>> selectFrom(List<Selector> selectors, Collection<ITEM> results)
/*  52:    */   {
/*  53:101 */     if (results.size() == 0) {
/*  54:102 */       return Collections.EMPTY_LIST;
/*  55:    */     }
/*  56:104 */     Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject(results.iterator().next());
/*  57:    */     
/*  58:106 */     return selectFrom(selectors, results, fields);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static <ITEM> List<Map<String, Object>> selectFrom(List<Selector> selectors, Collection<ITEM> results, Map<String, FieldAccess> fields)
/*  62:    */   {
/*  63:118 */     List<Map<String, Object>> rows = new ArrayList(results.size());
/*  64:121 */     for (Selector s : selectors) {
/*  65:122 */       s.handleStart(results);
/*  66:    */     }
/*  67:126 */     int index = 0;
/*  68:127 */     for (ITEM item : results)
/*  69:    */     {
/*  70:128 */       Map<String, Object> row = new LinkedHashMap();
/*  71:129 */       for (Selector s : selectors) {
/*  72:130 */         s.handleRow(index, row, item, fields);
/*  73:    */       }
/*  74:132 */       index++;
/*  75:133 */       rows.add(row);
/*  76:    */     }
/*  77:136 */     for (Selector s : selectors) {
/*  78:137 */       s.handleComplete(rows);
/*  79:    */     }
/*  80:140 */     return rows;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public static List<Selector> selects(Selector... selects)
/*  84:    */   {
/*  85:150 */     return Lists.list(selects);
/*  86:    */   }
/*  87:    */   
/*  88:    */   private static boolean isPropPath(String prop)
/*  89:    */   {
/*  90:166 */     return BeanUtils.isPropPath(prop);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public Selector()
/*  94:    */   {
/*  95:173 */     this.path = false;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public Selector(String nameOrPath, String alias)
/*  99:    */   {
/* 100:184 */     this.name = nameOrPath;
/* 101:185 */     this.alias = (alias == null ? nameOrPath : alias);
/* 102:186 */     this.path = isPropPath(this.name);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public Selector(String nameOrPath)
/* 106:    */   {
/* 107:196 */     this.name = nameOrPath;
/* 108:197 */     this.alias = this.name;
/* 109:198 */     this.path = isPropPath(this.name);
/* 110:    */   }
/* 111:    */   
/* 112:    */   public String getName()
/* 113:    */   {
/* 114:203 */     return this.name;
/* 115:    */   }
/* 116:    */   
/* 117:    */   protected void getPropertyValueAndPutIntoRow(Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 118:    */   {
/* 119:208 */     if ((!this.path) && (fields != null)) {
/* 120:209 */       row.put(this.alias, ((FieldAccess)fields.get(this.name)).getValue(item));
/* 121:    */     } else {
/* 122:211 */       row.put(this.alias, BeanUtils.atIndex(item, this.name));
/* 123:    */     }
/* 124:    */   }
/* 125:    */   
/* 126:    */   protected Object getPropertyValue(Object item, Map<String, FieldAccess> fields)
/* 127:    */   {
/* 128:217 */     if ((!this.path) && (fields != null)) {
/* 129:218 */       return ((FieldAccess)fields.get(this.name)).getValue(item);
/* 130:    */     }
/* 131:220 */     return BeanUtils.atIndex(item, this.name);
/* 132:    */   }
/* 133:    */   
/* 134:    */   public static Selector select(String propName)
/* 135:    */   {
/* 136:231 */     new Selector(propName, propName)
/* 137:    */     {
/* 138:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 139:    */       {
/* 140:237 */         getPropertyValueAndPutIntoRow(row, item, fields);
/* 141:    */       }
/* 142:    */       
/* 143:    */       public void handleStart(Collection<?> results) {}
/* 144:    */       
/* 145:    */       public void handleComplete(List<Map<String, Object>> rows) {}
/* 146:    */     };
/* 147:    */   }
/* 148:    */   
/* 149:    */   public static Selector selectAs(String propName, String alias)
/* 150:    */   {
/* 151:258 */     new Selector(propName, alias)
/* 152:    */     {
/* 153:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 154:    */       {
/* 155:265 */         getPropertyValueAndPutIntoRow(row, item, fields);
/* 156:    */       }
/* 157:    */       
/* 158:    */       public void handleStart(Collection<?> results) {}
/* 159:    */       
/* 160:    */       public void handleComplete(List<Map<String, Object>> rows) {}
/* 161:    */     };
/* 162:    */   }
/* 163:    */   
/* 164:    */   public static Selector selectAs(final String propName, String alias, final Function transform)
/* 165:    */   {
/* 166:289 */     new Selector(propName, alias)
/* 167:    */     {
/* 168:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 169:    */       {
/* 170:295 */         if ((!this.path) && (fields != null)) {
/* 171:296 */           row.put(this.name, transform.apply(((FieldAccess)fields.get(this.name)).getValue(item)));
/* 172:    */         } else {
/* 173:298 */           row.put(this.alias, transform.apply(BeanUtils.atIndex(item, propName)));
/* 174:    */         }
/* 175:    */       }
/* 176:    */       
/* 177:    */       public void handleStart(Collection<?> results) {}
/* 178:    */       
/* 179:    */       public void handleComplete(List<Map<String, Object>> rows) {}
/* 180:    */     };
/* 181:    */   }
/* 182:    */   
/* 183:    */   public static Selector selectAsTemplate(String alias, final String template, final Template transform)
/* 184:    */   {
/* 185:321 */     new Selector(alias, alias)
/* 186:    */     {
/* 187:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 188:    */       {
/* 189:327 */         if ((!this.path) && (fields != null)) {
/* 190:328 */           row.put(this.name, transform.replace(template, new Object[] { item }));
/* 191:    */         } else {
/* 192:330 */           row.put(this.alias, transform.replace(template, new Object[] { item }));
/* 193:    */         }
/* 194:    */       }
/* 195:    */       
/* 196:    */       public void handleStart(Collection<?> results) {}
/* 197:    */       
/* 198:    */       public void handleComplete(List<Map<String, Object>> rows) {}
/* 199:    */     };
/* 200:    */   }
/* 201:    */   
/* 202:    */   public static Selector toStr(String name)
/* 203:    */   {
/* 204:345 */     new Selector(name + ".toString()", null)
/* 205:    */     {
/* 206:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 207:    */       {
/* 208:348 */         Object selected = ((FieldAccess)fields.get(this.name)).getValue(item);
/* 209:349 */         row.put(this.name, selected == null ? "" : selected.toString());
/* 210:    */       }
/* 211:    */       
/* 212:    */       public void handleStart(Collection<?> results) {}
/* 213:    */       
/* 214:    */       public void handleComplete(List<Map<String, Object>> rows) {}
/* 215:    */     };
/* 216:    */   }
/* 217:    */   
/* 218:    */   public static Selector toStr()
/* 219:    */   {
/* 220:363 */     new Selector("toString()", null)
/* 221:    */     {
/* 222:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 223:    */       {
/* 224:366 */         row.put(this.name, item.toString());
/* 225:    */       }
/* 226:    */       
/* 227:    */       public void handleStart(Collection<?> results) {}
/* 228:    */       
/* 229:    */       public void handleComplete(List<Map<String, Object>> rows) {}
/* 230:    */     };
/* 231:    */   }
/* 232:    */   
/* 233:    */   @Deprecated
/* 234:    */   public static Selector select(final String... ppath)
/* 235:    */   {
/* 236:381 */     new Selector(Boon.joinBy('.', ppath), null)
/* 237:    */     {
/* 238:382 */       int index = 0;
/* 239:    */       
/* 240:    */       public void handleRow(int rowNum, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 241:    */       {
/* 242:388 */         Object o = BeanUtils.getPropByPath(item, ppath);
/* 243:    */         
/* 244:    */ 
/* 245:391 */         row.put(this.name, o);
/* 246:    */       }
/* 247:    */       
/* 248:    */       public void handleStart(Collection<?> results) {}
/* 249:    */       
/* 250:    */       public void handleComplete(List<Map<String, Object>> rows) {}
/* 251:    */     };
/* 252:    */   }
/* 253:    */   
/* 254:    */   @Deprecated
/* 255:    */   public static Selector toStr(final String... ppath)
/* 256:    */   {
/* 257:407 */     new Selector(Boon.joinBy('.', ppath) + ".toString()", null)
/* 258:    */     {
/* 259:408 */       int index = 0;
/* 260:    */       
/* 261:    */       public void handleRow(int rowNum, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 262:    */       {
/* 263:414 */         Object o = BeanUtils.getPropByPath(item, ppath);
/* 264:    */         
/* 265:    */ 
/* 266:417 */         row.put(this.name, o == null ? "" : o.toString());
/* 267:    */       }
/* 268:    */       
/* 269:    */       public void handleStart(Collection<?> results) {}
/* 270:    */       
/* 271:    */       public void handleComplete(List<Map<String, Object>> rows) {}
/* 272:    */     };
/* 273:    */   }
/* 274:    */   
/* 275:    */   @Deprecated
/* 276:    */   public static Selector selectPropPath(final String... ppath)
/* 277:    */   {
/* 278:433 */     new Selector(Boon.joinBy('.', ppath), null)
/* 279:    */     {
/* 280:    */       public void handleRow(int rowNum, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 281:    */       {
/* 282:438 */         Object o = BeanUtils.getPropByPath(item, ppath);
/* 283:    */         
/* 284:440 */         row.put(this.name, o);
/* 285:    */       }
/* 286:    */       
/* 287:    */       public void handleStart(Collection<?> results) {}
/* 288:    */       
/* 289:    */       public void handleComplete(List<Map<String, Object>> rows) {}
/* 290:    */     };
/* 291:    */   }
/* 292:    */   
/* 293:    */   public static Selector rowId()
/* 294:    */   {
/* 295:459 */     new Selector("rowId", null)
/* 296:    */     {
/* 297:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 298:    */       {
/* 299:462 */         row.put(this.name, Integer.valueOf(index));
/* 300:    */       }
/* 301:    */       
/* 302:    */       public void handleStart(Collection<?> results) {}
/* 303:    */       
/* 304:    */       public void handleComplete(List<Map<String, Object>> rows) {}
/* 305:    */     };
/* 306:    */   }
/* 307:    */   
/* 308:    */   public abstract void handleRow(int paramInt, Map<String, Object> paramMap, Object paramObject, Map<String, FieldAccess> paramMap1);
/* 309:    */   
/* 310:    */   public abstract void handleStart(Collection<?> paramCollection);
/* 311:    */   
/* 312:    */   public abstract void handleComplete(List<Map<String, Object>> paramList);
/* 313:    */   
/* 314:    */   public String getAlias()
/* 315:    */   {
/* 316:488 */     return this.alias;
/* 317:    */   }
/* 318:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.criteria.Selector
 * JD-Core Version:    0.7.0.1
 */