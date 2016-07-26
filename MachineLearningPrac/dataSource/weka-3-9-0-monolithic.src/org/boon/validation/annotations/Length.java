package org.boon.validation.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD})
public @interface Length
{
  long min() default 1L;
  
  long max() default 9223372036854775807L;
  
  String detailMessage() default "";
  
  String summaryMessage() default "";
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.annotations.Length
 * JD-Core Version:    0.7.0.1
 */