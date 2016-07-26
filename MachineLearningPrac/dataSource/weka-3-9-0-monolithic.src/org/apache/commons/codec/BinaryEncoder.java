package org.apache.commons.codec;

public abstract interface BinaryEncoder
  extends Encoder
{
  public abstract byte[] encode(byte[] paramArrayOfByte)
    throws EncoderException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.BinaryEncoder
 * JD-Core Version:    0.7.0.1
 */