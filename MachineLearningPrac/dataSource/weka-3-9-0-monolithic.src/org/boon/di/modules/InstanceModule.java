/*   1:    */ package org.boon.di.modules;
/*   2:    */ 
/*   3:    */ import java.util.Map;
/*   4:    */ import java.util.Set;
/*   5:    */ import java.util.concurrent.ConcurrentHashMap;
/*   6:    */ import org.boon.Exceptions;
/*   7:    */ import org.boon.Sets;
/*   8:    */ import org.boon.collections.MultiMapImpl;
/*   9:    */ import org.boon.core.Supplier;
/*  10:    */ import org.boon.core.reflection.ClassMeta;
/*  11:    */ import org.boon.core.reflection.MethodAccess;
/*  12:    */ import org.boon.di.ProviderInfo;
/*  13:    */ 
/*  14:    */ public class InstanceModule
/*  15:    */   extends BaseModule
/*  16:    */ {
/*  17: 47 */   private Map<Class, ProviderInfo> supplierTypeMap = new ConcurrentHashMap();
/*  18: 48 */   private MultiMapImpl<String, ProviderInfo> supplierNameMap = new MultiMapImpl();
/*  19:    */   private Object module;
/*  20:    */   
/*  21:    */   public InstanceModule(Object object)
/*  22:    */   {
/*  23: 54 */     this.module = object;
/*  24:    */     
/*  25: 56 */     ClassMeta classMeta = ClassMeta.classMeta(object.getClass());
/*  26: 57 */     Iterable<MethodAccess> methods = classMeta.methods();
/*  27: 59 */     for (MethodAccess method : methods) {
/*  28: 61 */       if ((!method.isStatic()) && (method.name().startsWith("provide"))) {
/*  29: 62 */         addCreationMethod(method, false);
/*  30: 63 */       } else if ((!method.isStatic()) && (method.name().startsWith("create"))) {
/*  31: 64 */         addCreationMethod(method, true);
/*  32:    */       }
/*  33:    */     }
/*  34:    */   }
/*  35:    */   
/*  36:    */   private static class InternalSupplier
/*  37:    */     implements Supplier<Object>
/*  38:    */   {
/*  39:    */     private final Object module;
/*  40:    */     private final MethodAccess method;
/*  41:    */     
/*  42:    */     InternalSupplier(MethodAccess method, Object module)
/*  43:    */     {
/*  44: 79 */       this.method = method;
/*  45: 80 */       this.module = module;
/*  46:    */     }
/*  47:    */     
/*  48:    */     public Object get()
/*  49:    */     {
/*  50:    */       try
/*  51:    */       {
/*  52: 86 */         return this.method.invoke(this.module, new Object[0]);
/*  53:    */       }
/*  54:    */       catch (Exception e)
/*  55:    */       {
/*  56: 88 */         return Exceptions.handle(Object.class, e);
/*  57:    */       }
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   private Supplier<Object> createSupplier(MethodAccess method)
/*  62:    */   {
/*  63: 94 */     return new InternalSupplier(method, this.module);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public <T> T get(Class<T> type)
/*  67:    */   {
/*  68:101 */     ProviderInfo pi = (ProviderInfo)this.supplierTypeMap.get(type);
/*  69:102 */     if (pi != null) {
/*  70:103 */       return pi.supplier().get();
/*  71:    */     }
/*  72:105 */     return null;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public Object get(String name)
/*  76:    */   {
/*  77:114 */     ProviderInfo pi = (ProviderInfo)this.supplierNameMap.get(name);
/*  78:115 */     if (pi != null) {
/*  79:116 */       return pi.supplier().get();
/*  80:    */     }
/*  81:118 */     return null;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public <T> T get(Class<T> type, String name)
/*  85:    */   {
/*  86:126 */     return getSupplier(type, name).get();
/*  87:    */   }
/*  88:    */   
/*  89:    */   public ProviderInfo getProviderInfo(Class<?> type)
/*  90:    */   {
/*  91:131 */     return (ProviderInfo)this.supplierTypeMap.get(type);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public ProviderInfo getProviderInfo(String name)
/*  95:    */   {
/*  96:136 */     return (ProviderInfo)this.supplierNameMap.get(name);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public ProviderInfo getProviderInfo(Class<?> type, String name)
/* 100:    */   {
/* 101:141 */     return doGetProvider(type, name);
/* 102:    */   }
/* 103:    */   
/* 104:    */   private ProviderInfo doGetProvider(Class<?> type, String name)
/* 105:    */   {
/* 106:149 */     Set<ProviderInfo> set = Sets.set(this.supplierNameMap.getAll(name));
/* 107:    */     
/* 108:    */ 
/* 109:152 */     ProviderInfo nullTypeInfo = null;
/* 110:154 */     for (ProviderInfo info : set) {
/* 111:156 */       if (info.type() == null) {
/* 112:157 */         nullTypeInfo = info;
/* 113:161 */       } else if (type.isAssignableFrom(info.type())) {
/* 114:162 */         return info;
/* 115:    */       }
/* 116:    */     }
/* 117:165 */     return nullTypeInfo;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public <T> Supplier<T> getSupplier(Class<T> type, String name)
/* 121:    */   {
/* 122:    */     try
/* 123:    */     {
/* 124:173 */       Set<ProviderInfo> set = Sets.set(this.supplierNameMap.getAll(name));
/* 125:174 */       for (ProviderInfo s : set)
/* 126:    */       {
/* 127:175 */         InternalSupplier supplier = (InternalSupplier)s.supplier();
/* 128:176 */         if (type.isAssignableFrom(supplier.method.returnType())) {
/* 129:177 */           return supplier;
/* 130:    */         }
/* 131:    */       }
/* 132:181 */       new Supplier()
/* 133:    */       {
/* 134:    */         public T get()
/* 135:    */         {
/* 136:184 */           return null;
/* 137:    */         }
/* 138:    */       };
/* 139:    */     }
/* 140:    */     catch (Exception e)
/* 141:    */     {
/* 142:189 */       Exceptions.handle(e);
/* 143:    */     }
/* 144:190 */     return null;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public <T> Supplier<T> getSupplier(Class<T> type)
/* 148:    */   {
/* 149:197 */     Supplier<T> supplier = (Supplier)this.supplierTypeMap.get(type);
/* 150:198 */     if (supplier == null) {
/* 151:199 */       supplier = new Supplier()
/* 152:    */       {
/* 153:    */         public T get()
/* 154:    */         {
/* 155:202 */           return null;
/* 156:    */         }
/* 157:    */       };
/* 158:    */     }
/* 159:207 */     return supplier;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public Iterable<Object> values()
/* 163:    */   {
/* 164:212 */     return (Iterable)this.supplierTypeMap.values();
/* 165:    */   }
/* 166:    */   
/* 167:    */   public Iterable<String> names()
/* 168:    */   {
/* 169:218 */     return this.supplierNameMap.keySet();
/* 170:    */   }
/* 171:    */   
/* 172:    */   public Iterable types()
/* 173:    */   {
/* 174:223 */     return this.supplierTypeMap.keySet();
/* 175:    */   }
/* 176:    */   
/* 177:    */   public boolean has(Class type)
/* 178:    */   {
/* 179:228 */     return this.supplierTypeMap.containsKey(type);
/* 180:    */   }
/* 181:    */   
/* 182:    */   public boolean has(String name)
/* 183:    */   {
/* 184:233 */     return this.supplierNameMap.containsKey(name);
/* 185:    */   }
/* 186:    */   
/* 187:    */   private void addCreationMethod(MethodAccess method, boolean creates)
/* 188:    */   {
/* 189:239 */     Object providerInfo = creates ? null : new Object();
/* 190:    */     
/* 191:    */ 
/* 192:242 */     String named = NamedUtils.namedValueForMethod(method);
/* 193:243 */     boolean foundName = named != null;
/* 194:    */     
/* 195:245 */     Class cls = method.returnType();
/* 196:249 */     if (!foundName)
/* 197:    */     {
/* 198:250 */       named = NamedUtils.namedValueForClass(cls);
/* 199:251 */       foundName = named != null;
/* 200:    */     }
/* 201:254 */     Supplier<Object> supplier = createSupplier(method);
/* 202:255 */     this.supplierTypeMap.put(cls, new ProviderInfo(named, cls, supplier, providerInfo));
/* 203:    */     
/* 204:257 */     Class superClass = cls.getSuperclass();
/* 205:    */     
/* 206:    */ 
/* 207:260 */     Class[] superTypes = cls.getInterfaces();
/* 208:262 */     for (Class superType : superTypes) {
/* 209:263 */       this.supplierTypeMap.put(superType, new ProviderInfo(named, cls, supplier, providerInfo));
/* 210:    */     }
/* 211:266 */     if (superClass != null) {
/* 212:267 */       while (superClass != Object.class)
/* 213:    */       {
/* 214:268 */         this.supplierTypeMap.put(superClass, new ProviderInfo(named, cls, supplier, providerInfo));
/* 215:271 */         if (!foundName)
/* 216:    */         {
/* 217:272 */           named = NamedUtils.namedValueForClass(cls);
/* 218:273 */           foundName = named != null;
/* 219:    */         }
/* 220:275 */         superTypes = cls.getInterfaces();
/* 221:276 */         for (Class superType : superTypes) {
/* 222:277 */           this.supplierTypeMap.put(superType, new ProviderInfo(named, cls, supplier, providerInfo));
/* 223:    */         }
/* 224:279 */         superClass = superClass.getSuperclass();
/* 225:    */       }
/* 226:    */     }
/* 227:284 */     if (foundName) {
/* 228:285 */       this.supplierNameMap.put(named, new ProviderInfo(named, cls, supplier, providerInfo));
/* 229:    */     }
/* 230:    */   }
/* 231:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.di.modules.InstanceModule
 * JD-Core Version:    0.7.0.1
 */