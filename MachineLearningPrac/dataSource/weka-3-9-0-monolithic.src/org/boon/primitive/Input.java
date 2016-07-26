package org.boon.primitive;

public abstract interface Input
{
  public abstract void readFully(byte[] paramArrayOfByte);
  
  public abstract void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract int skipBytes(int paramInt);
  
  public abstract void location(int paramInt);
  
  public abstract int location();
  
  public abstract void reset();
  
  public abstract boolean readBoolean();
  
  public abstract byte readByte();
  
  public abstract short readUnsignedByte();
  
  public abstract short readShort();
  
  public abstract int readUnsignedShort();
  
  public abstract char readChar();
  
  public abstract int readInt();
  
  public abstract long readUnsignedInt();
  
  public abstract long readLong();
  
  public abstract float readFloat();
  
  public abstract double readDouble();
  
  public abstract String readSmallString();
  
  public abstract String readLargeString();
  
  public abstract String readMediumString();
  
  public abstract byte[] readSmallByteArray();
  
  public abstract byte[] readLargeByteArray();
  
  public abstract byte[] readMediumByteArray();
  
  public abstract short[] readSmallShortArray();
  
  public abstract short[] readLargeShortArray();
  
  public abstract short[] readMediumShortArray();
  
  public abstract int[] readSmallIntArray();
  
  public abstract int[] readLargeIntArray();
  
  public abstract int[] readMediumIntArray();
  
  public abstract byte[] readBytes(int paramInt);
  
  public abstract long[] readSmallLongArray();
  
  public abstract long[] readLargeLongArray();
  
  public abstract long[] readMediumLongArray();
  
  public abstract float[] readSmallFloatArray();
  
  public abstract float[] readLargeFloatArray();
  
  public abstract float[] readMediumFloatArray();
  
  public abstract double[] readSmallDoubleArray();
  
  public abstract double[] readLargeDoubleArray();
  
  public abstract double[] readMediumDoubleArray();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.Input
 * JD-Core Version:    0.7.0.1
 */