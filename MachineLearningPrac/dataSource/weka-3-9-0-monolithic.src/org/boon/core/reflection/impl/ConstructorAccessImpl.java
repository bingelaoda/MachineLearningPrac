/*   1:    */ package org.boon.core.reflection.impl;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Constructor;
/*   4:    */ import java.lang.reflect.Type;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.concurrent.ConcurrentHashMap;
/*   9:    */ import org.boon.Exceptions;
/*  10:    */ import org.boon.core.reflection.AnnotationData;
/*  11:    */ import org.boon.core.reflection.Annotations;
/*  12:    */ import org.boon.core.reflection.ConstructorAccess;
/*  13:    */ 
/*  14:    */ public class ConstructorAccessImpl<T>
/*  15:    */   implements ConstructorAccess
/*  16:    */ {
/*  17:    */   final Constructor<T> constructor;
/*  18:    */   final List<AnnotationData> annotationData;
/*  19:    */   final Map<String, AnnotationData> annotationMap;
/*  20:    */   
/*  21:    */   ConstructorAccessImpl()
/*  22:    */   {
/*  23: 56 */     this.constructor = null;
/*  24: 57 */     this.annotationData = null;
/*  25: 58 */     this.annotationMap = null;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public ConstructorAccessImpl(Constructor<T> method)
/*  29:    */   {
/*  30: 62 */     this.constructor = method;
/*  31: 63 */     this.constructor.setAccessible(true);
/*  32: 64 */     this.annotationData = Annotations.getAnnotationDataForMethod(method);
/*  33:    */     
/*  34: 66 */     this.annotationMap = new ConcurrentHashMap();
/*  35: 67 */     for (AnnotationData data : this.annotationData)
/*  36:    */     {
/*  37: 68 */       this.annotationMap.put(data.getName(), data);
/*  38: 69 */       this.annotationMap.put(data.getSimpleClassName(), data);
/*  39: 70 */       this.annotationMap.put(data.getFullClassName(), data);
/*  40:    */     }
/*  41:    */   }
/*  42:    */   
/*  43:    */   public Iterable<AnnotationData> annotationData()
/*  44:    */   {
/*  45: 78 */     new Iterable()
/*  46:    */     {
/*  47:    */       public Iterator<AnnotationData> iterator()
/*  48:    */       {
/*  49: 81 */         return ConstructorAccessImpl.this.annotationData.iterator();
/*  50:    */       }
/*  51:    */     };
/*  52:    */   }
/*  53:    */   
/*  54:    */   public boolean hasAnnotation(String annotationName)
/*  55:    */   {
/*  56: 88 */     return this.annotationMap.containsKey(annotationName);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public AnnotationData annotation(String annotationName)
/*  60:    */   {
/*  61: 93 */     return (AnnotationData)this.annotationMap.get(annotationName);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public Class<?>[] parameterTypes()
/*  65:    */   {
/*  66: 98 */     return this.constructor.getParameterTypes();
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Type[] getGenericParameterTypes()
/*  70:    */   {
/*  71:103 */     return this.constructor.getGenericParameterTypes();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public T create(Object... args)
/*  75:    */   {
/*  76:    */     try
/*  77:    */     {
/*  78:109 */       return this.constructor.newInstance(args);
/*  79:    */     }
/*  80:    */     catch (Exception ex)
/*  81:    */     {
/*  82:111 */       return Exceptions.handle(this.constructor.getDeclaringClass(), ex, new Object[] { "\nunable to invoke constructor", this.constructor, "\n on object ", this.constructor.getDeclaringClass(), "\nwith arguments", args, "\nparams", this.constructor.getParameterTypes() });
/*  83:    */     }
/*  84:    */   }
/*  85:    */   
/*  86:    */   public String toString()
/*  87:    */   {
/*  88:123 */     return this.constructor.toString();
/*  89:    */   }
/*  90:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.impl.ConstructorAccessImpl
 * JD-Core Version:    0.7.0.1
 */