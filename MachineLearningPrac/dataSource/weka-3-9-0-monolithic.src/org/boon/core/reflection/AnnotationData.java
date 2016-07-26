/*   1:    */ package org.boon.core.reflection;
/*   2:    */ 
/*   3:    */ import java.lang.annotation.Annotation;
/*   4:    */ import java.lang.reflect.Method;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.HashSet;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.Set;
/*   9:    */ import org.boon.Str;
/*  10:    */ 
/*  11:    */ public class AnnotationData
/*  12:    */ {
/*  13:    */   private String annotationClassName;
/*  14:    */   private String annotationSimpleName;
/*  15:    */   private String annotationPackageName;
/*  16:    */   private Set<String> allowedAnnotations;
/*  17:    */   private String name;
/*  18:    */   private Map<String, Object> values;
/*  19:    */   
/*  20:    */   public AnnotationData(Annotation annotation)
/*  21:    */   {
/*  22: 74 */     this(annotation, new HashSet());
/*  23:    */   }
/*  24:    */   
/*  25:    */   public AnnotationData(Annotation annotation, Set<String> allowedAnnotations)
/*  26:    */   {
/*  27: 79 */     this.annotationSimpleName = annotation.annotationType().getSimpleName();
/*  28: 80 */     this.annotationClassName = annotation.annotationType().getName();
/*  29: 81 */     this.annotationPackageName = this.annotationClassName.substring(0, this.annotationClassName.length() - this.annotationSimpleName.length() - 1);
/*  30:    */     
/*  31: 83 */     this.allowedAnnotations = allowedAnnotations;
/*  32: 84 */     this.name = Str.uncapitalize(this.annotationSimpleName);
/*  33: 85 */     this.values = doGetValues(annotation);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public boolean isAllowed()
/*  37:    */   {
/*  38: 96 */     if ((this.allowedAnnotations == null) || (this.allowedAnnotations.size() == 0)) {
/*  39: 96 */       return true;
/*  40:    */     }
/*  41: 97 */     return this.allowedAnnotations.contains(this.annotationPackageName);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getName()
/*  45:    */   {
/*  46:107 */     return this.name;
/*  47:    */   }
/*  48:    */   
/*  49:    */   Map<String, Object> doGetValues(Annotation annotation)
/*  50:    */   {
/*  51:119 */     Map<String, Object> values = new HashMap();
/*  52:    */     
/*  53:121 */     Method[] methods = annotation.annotationType().getDeclaredMethods();
/*  54:    */     
/*  55:123 */     Object[] noargs = (Object[])null;
/*  56:128 */     for (Method method : methods) {
/*  57:130 */       if (method.getParameterTypes().length == 0) {
/*  58:    */         try
/*  59:    */         {
/*  60:133 */           Object value = method.invoke(annotation, noargs);
/*  61:134 */           if ((value instanceof Enum))
/*  62:    */           {
/*  63:135 */             Enum enumVal = (Enum)value;
/*  64:136 */             value = enumVal.name();
/*  65:    */           }
/*  66:138 */           values.put(method.getName(), value);
/*  67:    */         }
/*  68:    */         catch (Exception ex)
/*  69:    */         {
/*  70:140 */           throw new RuntimeException(ex);
/*  71:    */         }
/*  72:    */       }
/*  73:    */     }
/*  74:144 */     return values;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public Map<String, Object> getValues()
/*  78:    */   {
/*  79:148 */     return this.values;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String toString()
/*  83:    */   {
/*  84:152 */     return this.name;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String getFullClassName()
/*  88:    */   {
/*  89:157 */     return this.annotationClassName;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public String getSimpleClassName()
/*  93:    */   {
/*  94:162 */     return this.annotationSimpleName;
/*  95:    */   }
/*  96:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.AnnotationData
 * JD-Core Version:    0.7.0.1
 */