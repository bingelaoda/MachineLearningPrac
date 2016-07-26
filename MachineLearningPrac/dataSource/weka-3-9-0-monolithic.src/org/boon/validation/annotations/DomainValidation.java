package org.boon.validation.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD})
public @interface DomainValidation
{
  String detailMessage() default "";
  
  String summaryMessage() default "";
  
  String parentProperty() default "";
  
  String method() default "";
  
  boolean global() default false;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.annotations.DomainValidation
 * JD-Core Version:    0.7.0.1
 */