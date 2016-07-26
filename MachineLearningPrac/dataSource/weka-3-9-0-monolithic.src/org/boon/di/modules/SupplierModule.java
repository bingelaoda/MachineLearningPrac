/*   1:    */ package org.boon.di.modules;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.Map.Entry;
/*   7:    */ import java.util.Set;
/*   8:    */ import java.util.concurrent.ConcurrentHashMap;
/*   9:    */ import org.boon.Exceptions;
/*  10:    */ import org.boon.Sets;
/*  11:    */ import org.boon.collections.MultiMapImpl;
/*  12:    */ import org.boon.core.Supplier;
/*  13:    */ import org.boon.core.reflection.BeanUtils;
/*  14:    */ import org.boon.core.reflection.MapObjectConversion;
/*  15:    */ import org.boon.core.reflection.Reflection;
/*  16:    */ import org.boon.di.ProviderInfo;
/*  17:    */ 
/*  18:    */ public class SupplierModule
/*  19:    */   extends BaseModule
/*  20:    */ {
/*  21: 54 */   private Map<Class, ProviderInfo> supplierTypeMap = new ConcurrentHashMap();
/*  22: 56 */   private MultiMapImpl<String, ProviderInfo> supplierNameMap = new MultiMapImpl();
/*  23:    */   
/*  24:    */   public SupplierModule(ProviderInfo... suppliers)
/*  25:    */   {
/*  26: 59 */     supplierExtraction(suppliers);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public SupplierModule(List<ProviderInfo> suppliers)
/*  30:    */   {
/*  31: 64 */     supplierExtraction((ProviderInfo[])suppliers.toArray(new ProviderInfo[suppliers.size()]));
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Iterable<Object> values()
/*  35:    */   {
/*  36: 71 */     return (Iterable)this.supplierNameMap.values();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Iterable<String> names()
/*  40:    */   {
/*  41: 76 */     return this.supplierNameMap.keySet();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Iterable types()
/*  45:    */   {
/*  46: 81 */     return this.supplierTypeMap.keySet();
/*  47:    */   }
/*  48:    */   
/*  49:    */   public SupplierModule(Map<?, ?> map)
/*  50:    */   {
/*  51: 85 */     List<ProviderInfo> list = new ArrayList();
/*  52: 87 */     for (Map.Entry<?, ?> entry : map.entrySet())
/*  53:    */     {
/*  54: 88 */       Object key = entry.getKey();
/*  55: 89 */       Object value = entry.getValue();
/*  56: 90 */       if ((value instanceof Map))
/*  57:    */       {
/*  58: 91 */         Map<String, Object> valueMap = (Map)value;
/*  59: 92 */         ProviderInfo pi = addProviderFromMapToList(key, valueMap);
/*  60: 93 */         list.add(pi);
/*  61:    */       }
/*  62:    */       else
/*  63:    */       {
/*  64: 96 */         list.add(ProviderInfo.provider(key, value));
/*  65:    */       }
/*  66:    */     }
/*  67: 99 */     supplierExtraction((ProviderInfo[])list.toArray(new ProviderInfo[list.size()]));
/*  68:    */   }
/*  69:    */   
/*  70:    */   private ProviderInfo addProviderFromMapToList(Object key, final Map<String, Object> valueMap)
/*  71:    */   {
/*  72:103 */     if (valueMap.containsKey("class"))
/*  73:    */     {
/*  74:104 */       CharSequence className = (String)valueMap.get("class");
/*  75:105 */       if (className != null) {
/*  76:    */         try
/*  77:    */         {
/*  78:108 */           Class<Object> type = Class.forName(className.toString());
/*  79:    */           
/*  80:110 */           Supplier<Object> supplier = new Supplier()
/*  81:    */           {
/*  82:    */             public Object get()
/*  83:    */             {
/*  84:113 */               return MapObjectConversion.fromMap(valueMap);
/*  85:    */             }
/*  86:115 */           };
/*  87:116 */           return ProviderInfo.providerOf(key.toString(), type, supplier);
/*  88:    */         }
/*  89:    */         catch (ClassNotFoundException e)
/*  90:    */         {
/*  91:118 */           return ProviderInfo.provider(key, valueMap);
/*  92:    */         }
/*  93:    */       }
/*  94:    */     }
/*  95:124 */     return ProviderInfo.provider(key, valueMap);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public <T> T get(Class<T> type)
/*  99:    */   {
/* 100:131 */     ProviderInfo pi = (ProviderInfo)this.supplierTypeMap.get(type);
/* 101:132 */     if (pi != null) {
/* 102:133 */       return pi.supplier().get();
/* 103:    */     }
/* 104:135 */     return null;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public Object get(String name)
/* 108:    */   {
/* 109:142 */     ProviderInfo pi = (ProviderInfo)this.supplierNameMap.get(name);
/* 110:143 */     if (pi != null) {
/* 111:144 */       return pi.supplier().get();
/* 112:    */     }
/* 113:146 */     return null;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public <T> T get(Class<T> type, String name)
/* 117:    */   {
/* 118:153 */     ProviderInfo providerInfo = getProviderInfo(type, name);
/* 119:154 */     if (providerInfo != null) {
/* 120:155 */       return providerInfo.supplier().get();
/* 121:    */     }
/* 122:157 */     return null;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public ProviderInfo getProviderInfo(Class<?> type)
/* 126:    */   {
/* 127:162 */     return (ProviderInfo)this.supplierTypeMap.get(type);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public ProviderInfo getProviderInfo(String name)
/* 131:    */   {
/* 132:167 */     return (ProviderInfo)this.supplierNameMap.get(name);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public ProviderInfo getProviderInfo(Class<?> type, String name)
/* 136:    */   {
/* 137:172 */     return doGetProvider(type, name);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public <T> Supplier<T> getSupplier(Class<T> type, String name)
/* 141:    */   {
/* 142:180 */     ProviderInfo nullInfo = null;
/* 143:    */     try
/* 144:    */     {
/* 145:183 */       Set<ProviderInfo> set = Sets.set(this.supplierNameMap.getAll(name));
/* 146:185 */       for (ProviderInfo info : set) {
/* 147:187 */         if (info.type() == null) {
/* 148:188 */           nullInfo = info;
/* 149:191 */         } else if (type.isAssignableFrom(info.type())) {
/* 150:192 */           return info.supplier();
/* 151:    */         }
/* 152:    */       }
/* 153:196 */       nullInfo != null ? nullInfo.supplier() : new Supplier()
/* 154:    */       {
/* 155:    */         public T get()
/* 156:    */         {
/* 157:199 */           return null;
/* 158:    */         }
/* 159:    */       };
/* 160:    */     }
/* 161:    */     catch (Exception e)
/* 162:    */     {
/* 163:204 */       Exceptions.handle(e);
/* 164:    */     }
/* 165:205 */     return null;
/* 166:    */   }
/* 167:    */   
/* 168:    */   private ProviderInfo doGetProvider(Class<?> type, String name)
/* 169:    */   {
/* 170:213 */     Set<ProviderInfo> set = Sets.set(this.supplierNameMap.getAll(name));
/* 171:    */     
/* 172:    */ 
/* 173:216 */     ProviderInfo nullTypeInfo = null;
/* 174:218 */     for (ProviderInfo info : set) {
/* 175:220 */       if (info.type() == null) {
/* 176:221 */         nullTypeInfo = info;
/* 177:225 */       } else if (type.isAssignableFrom(info.type())) {
/* 178:226 */         return info;
/* 179:    */       }
/* 180:    */     }
/* 181:229 */     return nullTypeInfo;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public <T> Supplier<T> getSupplier(Class<T> type)
/* 185:    */   {
/* 186:235 */     Supplier<T> supplier = (Supplier)this.supplierTypeMap.get(type);
/* 187:236 */     if (supplier == null) {
/* 188:237 */       supplier = new Supplier()
/* 189:    */       {
/* 190:    */         public T get()
/* 191:    */         {
/* 192:240 */           return null;
/* 193:    */         }
/* 194:    */       };
/* 195:    */     }
/* 196:245 */     return supplier;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public boolean has(Class type)
/* 200:    */   {
/* 201:251 */     return this.supplierTypeMap.containsKey(type);
/* 202:    */   }
/* 203:    */   
/* 204:    */   public boolean has(String name)
/* 205:    */   {
/* 206:256 */     return this.supplierNameMap.containsKey(name);
/* 207:    */   }
/* 208:    */   
/* 209:    */   private void extractClassIntoMaps(ProviderInfo info, Class type, boolean foundName, Supplier supplier)
/* 210:    */   {
/* 211:262 */     if (type == null) {
/* 212:263 */       return;
/* 213:    */     }
/* 214:265 */     String named = null;
/* 215:    */     
/* 216:    */ 
/* 217:268 */     Class superClass = type.getSuperclass();
/* 218:    */     
/* 219:    */ 
/* 220:271 */     Class[] superTypes = type.getInterfaces();
/* 221:273 */     for (Class superType : superTypes) {
/* 222:274 */       this.supplierTypeMap.put(superType, info);
/* 223:    */     }
/* 224:277 */     while (superClass != Object.class)
/* 225:    */     {
/* 226:278 */       this.supplierTypeMap.put(superClass, info);
/* 227:280 */       if (!foundName)
/* 228:    */       {
/* 229:281 */         named = NamedUtils.namedValueForClass(superClass);
/* 230:282 */         if (named != null)
/* 231:    */         {
/* 232:283 */           this.supplierNameMap.put(named, new ProviderInfo(named, type, supplier, null));
/* 233:284 */           foundName = true;
/* 234:    */         }
/* 235:    */       }
/* 236:288 */       superTypes = type.getInterfaces();
/* 237:289 */       for (Class superType : superTypes) {
/* 238:290 */         this.supplierTypeMap.put(superType, info);
/* 239:    */       }
/* 240:292 */       superClass = superClass.getSuperclass();
/* 241:    */     }
/* 242:    */   }
/* 243:    */   
/* 244:    */   private void supplierExtraction(ProviderInfo[] suppliers)
/* 245:    */   {
/* 246:300 */     for (ProviderInfo providerInfo : suppliers)
/* 247:    */     {
/* 248:302 */       Class<?> type = providerInfo.type();
/* 249:304 */       if ((type == null) && (providerInfo.value() != null)) {
/* 250:305 */         type = providerInfo.value().getClass();
/* 251:    */       }
/* 252:308 */       String named = providerInfo.name();
/* 253:309 */       Supplier supplier = providerInfo.supplier();
/* 254:311 */       if (supplier == null)
/* 255:    */       {
/* 256:312 */         supplier = createSupplier(providerInfo.prototype(), type, providerInfo.value());
/* 257:313 */         providerInfo = new ProviderInfo(named, type, supplier, providerInfo.value());
/* 258:    */       }
/* 259:316 */       if (type != null)
/* 260:    */       {
/* 261:318 */         this.supplierTypeMap.put(type, providerInfo);
/* 262:322 */         if (named == null) {
/* 263:324 */           named = NamedUtils.namedValueForClass(type);
/* 264:    */         }
/* 265:    */       }
/* 266:328 */       extractClassIntoMaps(providerInfo, type, named != null, supplier);
/* 267:329 */       if (named != null) {
/* 268:330 */         this.supplierNameMap.put(named, new ProviderInfo(named, type, supplier, providerInfo.value()));
/* 269:    */       }
/* 270:    */     }
/* 271:    */   }
/* 272:    */   
/* 273:    */   private Supplier createSupplier(boolean prototype, final Class<?> type, final Object value)
/* 274:    */   {
/* 275:337 */     if ((value != null) && (!prototype)) {
/* 276:338 */       new Supplier()
/* 277:    */       {
/* 278:    */         public Object get()
/* 279:    */         {
/* 280:341 */           return value;
/* 281:    */         }
/* 282:    */       };
/* 283:    */     }
/* 284:344 */     if ((value != null) && (prototype)) {
/* 285:345 */       new Supplier()
/* 286:    */       {
/* 287:    */         public Object get()
/* 288:    */         {
/* 289:348 */           return BeanUtils.copy(value);
/* 290:    */         }
/* 291:    */       };
/* 292:    */     }
/* 293:351 */     if (type != null) {
/* 294:352 */       new Supplier()
/* 295:    */       {
/* 296:    */         public Object get()
/* 297:    */         {
/* 298:355 */           return Reflection.newInstance(type);
/* 299:    */         }
/* 300:    */       };
/* 301:    */     }
/* 302:359 */     new Supplier()
/* 303:    */     {
/* 304:    */       public Object get()
/* 305:    */       {
/* 306:362 */         return null;
/* 307:    */       }
/* 308:    */     };
/* 309:    */   }
/* 310:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.di.modules.SupplierModule
 * JD-Core Version:    0.7.0.1
 */