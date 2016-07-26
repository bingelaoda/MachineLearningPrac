/*  1:   */ package org.boon.datarepo.impl.indexes;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ import java.util.List;
/*  5:   */ 
/*  6:   */ public class TypeHierarchyIndex
/*  7:   */   extends BaseIndexWrapper
/*  8:   */ {
/*  9:   */   public TypeHierarchyIndex()
/* 10:   */   {
/* 11:38 */     super(new String[0]);
/* 12:   */   }
/* 13:   */   
/* 14:   */   public boolean add(Object o)
/* 15:   */   {
/* 16:42 */     List keys = getKeys(o);
/* 17:43 */     this.index.addManyKeys(o, keys);
/* 18:44 */     return true;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public boolean delete(Object o)
/* 22:   */   {
/* 23:49 */     List keys = getKeys(o);
/* 24:50 */     this.index.removeManyKeys(o, keys);
/* 25:51 */     return true;
/* 26:   */   }
/* 27:   */   
/* 28:   */   protected List getKeys(Object o)
/* 29:   */   {
/* 30:57 */     List<Object> list = new ArrayList();
/* 31:58 */     Class cls = o.getClass();
/* 32:60 */     while ((cls != null) && (cls != Object.class))
/* 33:   */     {
/* 34:61 */       list.add(cls.getSimpleName());
/* 35:62 */       list.add(cls.getName());
/* 36:64 */       for (Class<?> i : cls.getInterfaces())
/* 37:   */       {
/* 38:65 */         list.add(i.getSimpleName());
/* 39:66 */         list.add(i.getName());
/* 40:   */       }
/* 41:69 */       cls = cls.getSuperclass();
/* 42:   */     }
/* 43:71 */     return list;
/* 44:   */   }
/* 45:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.indexes.TypeHierarchyIndex
 * JD-Core Version:    0.7.0.1
 */