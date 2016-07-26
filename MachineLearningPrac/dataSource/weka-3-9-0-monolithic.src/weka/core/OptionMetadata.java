package weka.core;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface OptionMetadata
{
  String displayName();
  
  String description();
  
  int displayOrder() default 100;
  
  String commandLineParamName() default "";
  
  boolean commandLineParamIsFlag() default false;
  
  String commandLineParamSynopsis() default "";
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.OptionMetadata
 * JD-Core Version:    0.7.0.1
 */