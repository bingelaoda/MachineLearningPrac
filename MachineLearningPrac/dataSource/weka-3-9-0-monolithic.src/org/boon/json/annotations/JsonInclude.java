/*  1:   */ package org.boon.json.annotations;
/*  2:   */ 
/*  3:   */ import java.lang.annotation.Annotation;
/*  4:   */ import java.lang.annotation.Retention;
/*  5:   */ import java.lang.annotation.RetentionPolicy;
/*  6:   */ import java.lang.annotation.Target;
/*  7:   */ 
/*  8:   */ @Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
/*  9:   */ @Retention(RetentionPolicy.RUNTIME)
/* 10:   */ public @interface JsonInclude
/* 11:   */ {
/* 12:   */   Include value() default Include.ALWAYS;
/* 13:   */   
/* 14:   */   public static enum Include
/* 15:   */   {
/* 16:42 */     ALWAYS,  NON_DEFAULT,  NON_EMPTY,  NON_NULL;
/* 17:   */     
/* 18:   */     private Include() {}
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.annotations.JsonInclude
 * JD-Core Version:    0.7.0.1
 */