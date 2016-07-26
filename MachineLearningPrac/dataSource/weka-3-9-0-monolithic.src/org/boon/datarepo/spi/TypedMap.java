package org.boon.datarepo.spi;

public abstract interface TypedMap<K, V>
{
  public abstract boolean put(K paramK, boolean paramBoolean);
  
  public abstract boolean getBoolean(K paramK);
  
  public abstract V put(byte paramByte, V paramV);
  
  public abstract byte put(K paramK, byte paramByte);
  
  public abstract byte getByte(K paramK);
  
  public abstract V put(short paramShort, V paramV);
  
  public abstract short put(K paramK, short paramShort);
  
  public abstract short getShort(K paramK);
  
  public abstract V put(int paramInt, V paramV);
  
  public abstract int put(K paramK, int paramInt);
  
  public abstract int getInt(K paramK);
  
  public abstract V put(long paramLong, V paramV);
  
  public abstract long put(K paramK, long paramLong);
  
  public abstract long getLong(K paramK);
  
  public abstract V put(float paramFloat, V paramV);
  
  public abstract float put(K paramK, float paramFloat);
  
  public abstract float getFloat(K paramK);
  
  public abstract V put(double paramDouble, V paramV);
  
  public abstract double put(K paramK, double paramDouble);
  
  public abstract double getDouble(K paramK);
  
  public abstract V put(char paramChar, V paramV);
  
  public abstract char put(K paramK, char paramChar);
  
  public abstract char getChar(K paramK);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.spi.TypedMap
 * JD-Core Version:    0.7.0.1
 */