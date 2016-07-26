package org.boon.primitive;

public abstract interface CharacterSource
{
  public abstract void skipWhiteSpace();
  
  public abstract int nextChar();
  
  public abstract int currentChar();
  
  public abstract boolean hasChar();
  
  public abstract boolean consumeIfMatch(char[] paramArrayOfChar);
  
  public abstract int location();
  
  public abstract int safeNextChar();
  
  public abstract char[] findNextChar(int paramInt1, int paramInt2);
  
  public abstract boolean hadEscape();
  
  public abstract char[] readNumber();
  
  public abstract String errorDetails(String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.CharacterSource
 * JD-Core Version:    0.7.0.1
 */