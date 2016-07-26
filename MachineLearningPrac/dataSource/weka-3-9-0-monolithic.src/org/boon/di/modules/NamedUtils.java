/*  1:   */ package org.boon.di.modules;
/*  2:   */ 
/*  3:   */ import java.util.Map;
/*  4:   */ import java.util.Set;
/*  5:   */ import org.boon.Sets;
/*  6:   */ import org.boon.Str;
/*  7:   */ import org.boon.core.reflection.Annotated;
/*  8:   */ import org.boon.core.reflection.AnnotationData;
/*  9:   */ import org.boon.core.reflection.ClassMeta;
/* 10:   */ import org.boon.core.reflection.MethodAccess;
/* 11:   */ 
/* 12:   */ public class NamedUtils
/* 13:   */ {
/* 14:47 */   private static Set<String> annotationsThatHaveNamed = Sets.set(new String[] { "jsonProperty", "serializedName", "named", "id", "in", "qualifier" });
/* 15:   */   
/* 16:   */   public static String namedValueForClass(Class<?> type)
/* 17:   */   {
/* 18:52 */     ClassMeta cls = ClassMeta.classMeta(type);
/* 19:   */     
/* 20:   */ 
/* 21:55 */     String named = findNamed(cls, type);
/* 22:   */     
/* 23:57 */     return named;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public static String namedValueForMethod(MethodAccess method)
/* 27:   */   {
/* 28:64 */     String named = findNamed(method, method.returnType());
/* 29:67 */     if (named == null) {
/* 30:68 */       named = namedValueForClass(method.returnType());
/* 31:   */     }
/* 32:70 */     return named;
/* 33:   */   }
/* 34:   */   
/* 35:   */   private static String findNamed(Annotated annotated, Class<?> type)
/* 36:   */   {
/* 37:74 */     String named = null;
/* 38:76 */     for (String annotationName : annotationsThatHaveNamed)
/* 39:   */     {
/* 40:77 */       named = getName(annotated, annotationName, type);
/* 41:78 */       if (named != null) {
/* 42:   */         break;
/* 43:   */       }
/* 44:   */     }
/* 45:84 */     return named;
/* 46:   */   }
/* 47:   */   
/* 48:   */   private static String getName(Annotated annotated, String annotationName, Class<?> type)
/* 49:   */   {
/* 50:88 */     String named = null;
/* 51:89 */     if (annotated.hasAnnotation(annotationName))
/* 52:   */     {
/* 53:90 */       named = (String)annotated.annotation(annotationName).getValues().get("value");
/* 54:91 */       if (Str.isEmpty(named)) {
/* 55:92 */         named = Str.uncapitalize(type.getSimpleName());
/* 56:   */       }
/* 57:   */     }
/* 58:95 */     return named;
/* 59:   */   }
/* 60:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.di.modules.NamedUtils
 * JD-Core Version:    0.7.0.1
 */