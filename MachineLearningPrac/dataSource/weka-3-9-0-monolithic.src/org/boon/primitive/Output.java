package org.boon.primitive;

public abstract interface Output
{
  public abstract void write(int paramInt);
  
  public abstract void write(byte[] paramArrayOfByte);
  
  public abstract void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract void writeBoolean(boolean paramBoolean);
  
  public abstract void writeByte(byte paramByte);
  
  public abstract void writeUnsignedByte(short paramShort);
  
  public abstract void writeShort(short paramShort);
  
  public abstract void writeUnsignedShort(int paramInt);
  
  public abstract void writeChar(char paramChar);
  
  public abstract void writeInt(int paramInt);
  
  public abstract void writeUnsignedInt(long paramLong);
  
  public abstract void writeLong(long paramLong);
  
  public abstract void writeFloat(float paramFloat);
  
  public abstract void writeDouble(double paramDouble);
  
  public abstract void writeLargeString(String paramString);
  
  public abstract void writeSmallString(String paramString);
  
  public abstract void writeMediumString(String paramString);
  
  public abstract void writeLargeByteArray(byte[] paramArrayOfByte);
  
  public abstract void writeSmallByteArray(byte[] paramArrayOfByte);
  
  public abstract void writeMediumByteArray(byte[] paramArrayOfByte);
  
  public abstract void writeLargeShortArray(short[] paramArrayOfShort);
  
  public abstract void writeSmallShortArray(short[] paramArrayOfShort);
  
  public abstract void writeMediumShortArray(short[] paramArrayOfShort);
  
  public abstract void writeLargeIntArray(int[] paramArrayOfInt);
  
  public abstract void writeSmallIntArray(int[] paramArrayOfInt);
  
  public abstract void writeMediumIntArray(int[] paramArrayOfInt);
  
  public abstract void writeLargeLongArray(long[] paramArrayOfLong);
  
  public abstract void writeSmallLongArray(long[] paramArrayOfLong);
  
  public abstract void writeMediumLongArray(long[] paramArrayOfLong);
  
  public abstract void writeLargeFloatArray(float[] paramArrayOfFloat);
  
  public abstract void writeSmallFloatArray(float[] paramArrayOfFloat);
  
  public abstract void writeMediumFloatArray(float[] paramArrayOfFloat);
  
  public abstract void writeLargeDoubleArray(double[] paramArrayOfDouble);
  
  public abstract void writeSmallDoubleArray(double[] paramArrayOfDouble);
  
  public abstract void writeMediumDoubleArray(double[] paramArrayOfDouble);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.Output
 * JD-Core Version:    0.7.0.1
 */