/*  1:   */ package org.boon.di;
/*  2:   */ 
/*  3:   */ import java.util.List;
/*  4:   */ import java.util.Map;
/*  5:   */ import org.boon.Lists;
/*  6:   */ import org.boon.di.impl.ContextImpl;
/*  7:   */ import org.boon.di.modules.InstanceModule;
/*  8:   */ import org.boon.di.modules.SupplierModule;
/*  9:   */ 
/* 10:   */ public class DependencyInjection
/* 11:   */ {
/* 12:   */   public static Context context(Module... modules)
/* 13:   */   {
/* 14:43 */     return new ContextImpl(modules);
/* 15:   */   }
/* 16:   */   
/* 17:   */   public static Module classes(Class... classes)
/* 18:   */   {
/* 19:47 */     List<ProviderInfo> wrap = Lists.wrap(ProviderInfo.class, classes);
/* 20:48 */     return new SupplierModule(wrap);
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static Module objects(Object... objects)
/* 24:   */   {
/* 25:56 */     List<ProviderInfo> wrap = Lists.mapBy(objects, ProviderInfo.class, "objectProviderOf");
/* 26:   */     
/* 27:58 */     return new SupplierModule(wrap);
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static Module prototypes(Object... objects)
/* 31:   */   {
/* 32:64 */     List<ProviderInfo> wrap = Lists.mapBy(objects, ProviderInfo.class, "prototypeProviderOf");
/* 33:   */     
/* 34:66 */     return new SupplierModule(wrap);
/* 35:   */   }
/* 36:   */   
/* 37:   */   public static Module module(Object module)
/* 38:   */   {
/* 39:71 */     return new InstanceModule(module);
/* 40:   */   }
/* 41:   */   
/* 42:   */   public static Module suppliers(ProviderInfo... suppliers)
/* 43:   */   {
/* 44:76 */     return new SupplierModule(suppliers);
/* 45:   */   }
/* 46:   */   
/* 47:   */   public static Context fromMap(Map<?, ?> map)
/* 48:   */   {
/* 49:80 */     return new ContextImpl(new Module[] { new SupplierModule(map) });
/* 50:   */   }
/* 51:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.di.DependencyInjection
 * JD-Core Version:    0.7.0.1
 */