package org.boon.core.reflection;

public abstract interface Annotated
{
  public abstract Iterable<AnnotationData> annotationData();
  
  public abstract boolean hasAnnotation(String paramString);
  
  public abstract AnnotationData annotation(String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.Annotated
 * JD-Core Version:    0.7.0.1
 */