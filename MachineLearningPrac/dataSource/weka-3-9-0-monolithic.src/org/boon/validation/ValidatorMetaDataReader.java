package org.boon.validation;

import java.util.List;

public abstract interface ValidatorMetaDataReader
{
  public abstract List<ValidatorMetaData> readMetaData(Class<?> paramClass, String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.ValidatorMetaDataReader
 * JD-Core Version:    0.7.0.1
 */