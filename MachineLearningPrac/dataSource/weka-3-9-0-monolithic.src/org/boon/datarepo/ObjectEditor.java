package org.boon.datarepo;

import java.util.Collection;
import java.util.List;
import org.boon.criteria.Update;

public abstract interface ObjectEditor<KEY, ITEM>
  extends Bag<ITEM>
{
  public abstract ITEM get(KEY paramKEY);
  
  public abstract KEY getKey(ITEM paramITEM);
  
  public abstract void put(ITEM paramITEM);
  
  public abstract void removeByKey(KEY paramKEY);
  
  public abstract void removeAll(ITEM... paramVarArgs);
  
  public abstract void removeAllAsync(Collection<ITEM> paramCollection);
  
  public abstract void addAll(ITEM... paramVarArgs);
  
  public abstract void addAllAsync(Collection<ITEM> paramCollection);
  
  public abstract void modifyAll(ITEM... paramVarArgs);
  
  public abstract void modifyAll(Collection<ITEM> paramCollection);
  
  public abstract void modify(ITEM paramITEM);
  
  public abstract void update(ITEM paramITEM);
  
  public abstract void modifyByValue(ITEM paramITEM, String paramString1, String paramString2);
  
  public abstract void modify(ITEM paramITEM, String paramString, Object paramObject);
  
  public abstract void modify(ITEM paramITEM, String paramString, int paramInt);
  
  public abstract void modify(ITEM paramITEM, String paramString, long paramLong);
  
  public abstract void modify(ITEM paramITEM, String paramString, char paramChar);
  
  public abstract void modify(ITEM paramITEM, String paramString, short paramShort);
  
  public abstract void modify(ITEM paramITEM, String paramString, byte paramByte);
  
  public abstract void modify(ITEM paramITEM, String paramString, float paramFloat);
  
  public abstract void modify(ITEM paramITEM, String paramString, double paramDouble);
  
  public abstract void modify(ITEM paramITEM, Update... paramVarArgs);
  
  public abstract void updateByValue(KEY paramKEY, String paramString1, String paramString2);
  
  public abstract void update(KEY paramKEY, String paramString, Object paramObject);
  
  public abstract void update(KEY paramKEY, String paramString, int paramInt);
  
  public abstract void update(KEY paramKEY, String paramString, long paramLong);
  
  public abstract void update(KEY paramKEY, String paramString, char paramChar);
  
  public abstract void update(KEY paramKEY, String paramString, short paramShort);
  
  public abstract void update(KEY paramKEY, String paramString, byte paramByte);
  
  public abstract void update(KEY paramKEY, String paramString, float paramFloat);
  
  public abstract void update(KEY paramKEY, String paramString, double paramDouble);
  
  public abstract void update(KEY paramKEY, Update... paramVarArgs);
  
  public abstract boolean compareAndUpdate(KEY paramKEY, String paramString, Object paramObject1, Object paramObject2);
  
  public abstract boolean compareAndUpdate(KEY paramKEY, String paramString, int paramInt1, int paramInt2);
  
  public abstract boolean compareAndUpdate(KEY paramKEY, String paramString, long paramLong1, long paramLong2);
  
  public abstract boolean compareAndUpdate(KEY paramKEY, String paramString, char paramChar1, char paramChar2);
  
  public abstract boolean compareAndUpdate(KEY paramKEY, String paramString, short paramShort1, short paramShort2);
  
  public abstract boolean compareAndUpdate(KEY paramKEY, String paramString, byte paramByte1, byte paramByte2);
  
  public abstract boolean compareAndUpdate(KEY paramKEY, String paramString, float paramFloat1, float paramFloat2);
  
  public abstract boolean compareAndUpdate(KEY paramKEY, String paramString, double paramDouble1, double paramDouble2);
  
  public abstract boolean compareAndIncrement(KEY paramKEY, String paramString, int paramInt);
  
  public abstract boolean compareAndIncrement(KEY paramKEY, String paramString, long paramLong);
  
  public abstract boolean compareAndIncrement(KEY paramKEY, String paramString, short paramShort);
  
  public abstract boolean compareAndIncrement(KEY paramKEY, String paramString, byte paramByte);
  
  public abstract void addAll(List<ITEM> paramList);
  
  public abstract Object readNestedValue(KEY paramKEY, String... paramVarArgs);
  
  public abstract int readNestedInt(KEY paramKEY, String... paramVarArgs);
  
  public abstract short readNestedShort(KEY paramKEY, String... paramVarArgs);
  
  public abstract char readNestedChar(KEY paramKEY, String... paramVarArgs);
  
  public abstract byte readNestedByte(KEY paramKEY, String... paramVarArgs);
  
  public abstract double readNestedDouble(KEY paramKEY, String... paramVarArgs);
  
  public abstract float readNestedFloat(KEY paramKEY, String... paramVarArgs);
  
  public abstract long readNestedLong(KEY paramKEY, String... paramVarArgs);
  
  public abstract Object readObject(KEY paramKEY, String paramString);
  
  public abstract <T> T readValue(KEY paramKEY, String paramString, Class<T> paramClass);
  
  public abstract int readInt(KEY paramKEY, String paramString);
  
  public abstract long readLong(KEY paramKEY, String paramString);
  
  public abstract char readChar(KEY paramKEY, String paramString);
  
  public abstract short readShort(KEY paramKEY, String paramString);
  
  public abstract byte readByte(KEY paramKEY, String paramString);
  
  public abstract float readFloat(KEY paramKEY, String paramString);
  
  public abstract double readDouble(KEY paramKEY, String paramString);
  
  public abstract Object getObject(ITEM paramITEM, String paramString);
  
  public abstract <T> T getValue(ITEM paramITEM, String paramString, Class<T> paramClass);
  
  public abstract int getInt(ITEM paramITEM, String paramString);
  
  public abstract long getLong(ITEM paramITEM, String paramString);
  
  public abstract char getChar(ITEM paramITEM, String paramString);
  
  public abstract short getShort(ITEM paramITEM, String paramString);
  
  public abstract byte getByte(ITEM paramITEM, String paramString);
  
  public abstract float getFloat(ITEM paramITEM, String paramString);
  
  public abstract double getDouble(ITEM paramITEM, String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.ObjectEditor
 * JD-Core Version:    0.7.0.1
 */