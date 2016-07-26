package org.boon.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.Date;
import org.boon.primitive.CharBuf;

public abstract interface Value
{
  public abstract byte byteValue();
  
  public abstract short shortValue();
  
  public abstract int intValue();
  
  public abstract long longValue();
  
  public abstract BigDecimal bigDecimalValue();
  
  public abstract BigInteger bigIntegerValue();
  
  public abstract float floatValue();
  
  public abstract double doubleValue();
  
  public abstract boolean booleanValue();
  
  public abstract Date dateValue();
  
  public abstract String stringValue();
  
  public abstract String stringValue(CharBuf paramCharBuf);
  
  public abstract String stringValueEncoded();
  
  public abstract Currency currencyValue();
  
  public abstract Object toValue();
  
  public abstract <T extends Enum> T toEnum(Class<T> paramClass);
  
  public abstract boolean isContainer();
  
  public abstract void chop();
  
  public abstract char charValue();
  
  public abstract TypeType type();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.Value
 * JD-Core Version:    0.7.0.1
 */