/*  1:   */ package org.boon.di;
/*  2:   */ 
/*  3:   */ import java.util.Map;
/*  4:   */ import org.boon.Maps;
/*  5:   */ 
/*  6:   */ public class Creator
/*  7:   */ {
/*  8:   */   public static <T> T create(Class<T> type, Map<?, ?> map)
/*  9:   */   {
/* 10:41 */     Context context = DependencyInjection.fromMap(map);
/* 11:42 */     context.add(DependencyInjection.classes(new Class[] { type }));
/* 12:43 */     return context.get(type);
/* 13:   */   }
/* 14:   */   
/* 15:   */   public static <T> T create(Class<T> type, Context context)
/* 16:   */   {
/* 17:48 */     context.add(DependencyInjection.classes(new Class[] { type }));
/* 18:49 */     return context.get(type);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public static <T> T newOf(Class<T> type, Object... args)
/* 22:   */   {
/* 23:54 */     Map<?, ?> map = Maps.mapFromArray(args);
/* 24:55 */     Context context = DependencyInjection.fromMap(map);
/* 25:56 */     context.add(DependencyInjection.classes(new Class[] { type }));
/* 26:57 */     return context.get(type);
/* 27:   */   }
/* 28:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.di.Creator
 * JD-Core Version:    0.7.0.1
 */