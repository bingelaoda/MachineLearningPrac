package org.apache.commons.codec;

public abstract interface StringEncoder
  extends Encoder
{
  public abstract String encode(String paramString)
    throws EncoderException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.StringEncoder
 * JD-Core Version:    0.7.0.1
 */