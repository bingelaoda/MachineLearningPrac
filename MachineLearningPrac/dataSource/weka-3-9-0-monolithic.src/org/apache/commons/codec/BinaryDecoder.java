package org.apache.commons.codec;

public abstract interface BinaryDecoder
  extends Decoder
{
  public abstract byte[] decode(byte[] paramArrayOfByte)
    throws DecoderException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.BinaryDecoder
 * JD-Core Version:    0.7.0.1
 */