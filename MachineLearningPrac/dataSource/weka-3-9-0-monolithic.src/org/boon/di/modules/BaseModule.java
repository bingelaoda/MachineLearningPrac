/*  1:   */ package org.boon.di.modules;
/*  2:   */ 
/*  3:   */ import java.util.concurrent.atomic.AtomicReference;
/*  4:   */ import org.boon.core.Supplier;
/*  5:   */ import org.boon.di.Context;
/*  6:   */ import org.boon.di.Module;
/*  7:   */ 
/*  8:   */ public abstract class BaseModule
/*  9:   */   implements Module
/* 10:   */ {
/* 11:42 */   private AtomicReference<Context> parent = new AtomicReference();
/* 12:   */   
/* 13:   */   public void parent(Context context)
/* 14:   */   {
/* 15:46 */     this.parent.set(context);
/* 16:   */   }
/* 17:   */   
/* 18:   */   public <T> T get(Class<T> type)
/* 19:   */   {
/* 20:51 */     return null;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public Object get(String name)
/* 24:   */   {
/* 25:56 */     return null;
/* 26:   */   }
/* 27:   */   
/* 28:   */   public <T> T get(Class<T> type, String name)
/* 29:   */   {
/* 30:61 */     return null;
/* 31:   */   }
/* 32:   */   
/* 33:   */   public boolean has(Class type)
/* 34:   */   {
/* 35:66 */     return false;
/* 36:   */   }
/* 37:   */   
/* 38:   */   public boolean has(String name)
/* 39:   */   {
/* 40:71 */     return false;
/* 41:   */   }
/* 42:   */   
/* 43:   */   public <T> Supplier<T> getSupplier(Class<T> type, String name)
/* 44:   */   {
/* 45:76 */     return null;
/* 46:   */   }
/* 47:   */   
/* 48:   */   public <T> Supplier<T> getSupplier(Class<T> type)
/* 49:   */   {
/* 50:81 */     return null;
/* 51:   */   }
/* 52:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.di.modules.BaseModule
 * JD-Core Version:    0.7.0.1
 */