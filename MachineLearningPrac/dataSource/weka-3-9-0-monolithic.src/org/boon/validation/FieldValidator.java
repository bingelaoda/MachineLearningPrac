package org.boon.validation;

import java.io.Serializable;

public abstract interface FieldValidator
  extends Serializable
{
  public abstract ValidatorMessageHolder validate(Object paramObject, String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.FieldValidator
 * JD-Core Version:    0.7.0.1
 */