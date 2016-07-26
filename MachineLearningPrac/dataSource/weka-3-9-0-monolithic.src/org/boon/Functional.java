/*   1:    */ package org.boon;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.Map.Entry;
/*   6:    */ import org.boon.core.Function;
/*   7:    */ import org.boon.core.reflection.Invoker;
/*   8:    */ import org.boon.core.reflection.MethodAccess;
/*   9:    */ 
/*  10:    */ public class Functional
/*  11:    */ {
/*  12:    */   public static void each(Iterable<?> objects, Class<?> cls, String methodName)
/*  13:    */   {
/*  14: 48 */     for (Object o : objects) {
/*  15: 49 */       Invoker.invoke(cls, methodName, new Object[] { o });
/*  16:    */     }
/*  17:    */   }
/*  18:    */   
/*  19:    */   public static void each(Object[] objects, Object instance, String methodName)
/*  20:    */   {
/*  21: 55 */     for (Object o : objects) {
/*  22: 56 */       Invoker.invoke(instance, methodName, new Object[] { o });
/*  23:    */     }
/*  24:    */   }
/*  25:    */   
/*  26:    */   public static void each(Object[] objects, Class<?> cls, String methodName)
/*  27:    */   {
/*  28: 62 */     for (Object o : objects) {
/*  29: 63 */       Invoker.invoke(cls, methodName, new Object[] { o });
/*  30:    */     }
/*  31:    */   }
/*  32:    */   
/*  33:    */   public static void each(Iterable<?> objects, Object instance, String methodName)
/*  34:    */   {
/*  35: 73 */     for (Object o : objects) {
/*  36: 74 */       Invoker.invoke(instance, methodName, new Object[] { o });
/*  37:    */     }
/*  38:    */   }
/*  39:    */   
/*  40:    */   public static void each(Collection<?> objects, Class<?> cls, String methodName)
/*  41:    */   {
/*  42: 81 */     MethodAccess methodAccess = Invoker.invokeMethodAccess(cls, methodName);
/*  43: 83 */     for (Object o : objects) {
/*  44: 84 */       methodAccess.invokeStatic(new Object[] { o });
/*  45:    */     }
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static void each(Collection<?> objects, Object function)
/*  49:    */   {
/*  50: 91 */     MethodAccess methodAccess = Invoker.invokeFunctionMethodAccess(function);
/*  51: 93 */     for (Object o : objects) {
/*  52: 94 */       methodAccess.invoke(function, new Object[] { o });
/*  53:    */     }
/*  54:    */   }
/*  55:    */   
/*  56:    */   public static void each(Map<?, ?> map, Object object)
/*  57:    */   {
/*  58:103 */     MethodAccess methodAccess = Invoker.invokeFunctionMethodAccess(object);
/*  59:105 */     for (Object o : map.entrySet())
/*  60:    */     {
/*  61:106 */       Map.Entry entry = (Map.Entry)o;
/*  62:    */       
/*  63:108 */       methodAccess.invoke(object, new Object[] { ((Map.Entry)o).getKey(), ((Map.Entry)o).getValue() });
/*  64:    */     }
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static void each(Iterable<?> objects, Object function)
/*  68:    */   {
/*  69:116 */     MethodAccess methodAccess = Invoker.invokeFunctionMethodAccess(function);
/*  70:118 */     for (Object o : objects) {
/*  71:119 */       methodAccess.invoke(function, new Object[] { o });
/*  72:    */     }
/*  73:    */   }
/*  74:    */   
/*  75:    */   public static void each(Object[] objects, Object function)
/*  76:    */   {
/*  77:126 */     MethodAccess methodAccess = Invoker.invokeFunctionMethodAccess(function);
/*  78:128 */     for (Object o : objects) {
/*  79:129 */       methodAccess.invoke(function, new Object[] { o });
/*  80:    */     }
/*  81:    */   }
/*  82:    */   
/*  83:    */   public static void each(Collection<?> objects, Object object, String methodName)
/*  84:    */   {
/*  85:138 */     MethodAccess methodAccess = Invoker.invokeMethodAccess(object.getClass(), methodName);
/*  86:140 */     for (Object o : objects) {
/*  87:141 */       methodAccess.invoke(object, new Object[] { o });
/*  88:    */     }
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static void each(Map<?, ?> map, Object object, String methodName)
/*  92:    */   {
/*  93:149 */     MethodAccess methodAccess = Invoker.invokeMethodAccess(object.getClass(), methodName);
/*  94:151 */     for (Object o : map.entrySet())
/*  95:    */     {
/*  96:152 */       Map.Entry entry = (Map.Entry)o;
/*  97:    */       
/*  98:154 */       methodAccess.invoke(object, new Object[] { ((Map.Entry)o).getKey(), ((Map.Entry)o).getValue() });
/*  99:    */     }
/* 100:    */   }
/* 101:    */   
/* 102:    */   public static <V, N> void each(V[] array, Function<V, N> function)
/* 103:    */   {
/* 104:161 */     for (V v : array) {
/* 105:162 */       function.apply(v);
/* 106:    */     }
/* 107:    */   }
/* 108:    */   
/* 109:    */   public static <V, N> void each(Collection<V> array, Function<V, N> function)
/* 110:    */   {
/* 111:168 */     for (V v : array) {
/* 112:169 */       function.apply(v);
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   public static <V, N> void each(Iterable<V> array, Function<V, N> function)
/* 117:    */   {
/* 118:175 */     for (V v : array) {
/* 119:176 */       function.apply(v);
/* 120:    */     }
/* 121:    */   }
/* 122:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Functional
 * JD-Core Version:    0.7.0.1
 */