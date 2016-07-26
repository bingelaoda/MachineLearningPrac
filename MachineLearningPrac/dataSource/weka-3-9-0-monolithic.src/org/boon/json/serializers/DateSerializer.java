package org.boon.json.serializers;

import java.util.Date;
import org.boon.primitive.CharBuf;

public abstract interface DateSerializer
{
  public abstract void serializeDate(JsonSerializerInternal paramJsonSerializerInternal, Date paramDate, CharBuf paramCharBuf);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.DateSerializer
 * JD-Core Version:    0.7.0.1
 */