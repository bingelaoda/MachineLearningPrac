package org.j_paine.formatter;

abstract interface FormatOutputList
{
  public abstract boolean hasCurrentElement();
  
  public abstract void checkCurrentElementForWrite(FormatElement paramFormatElement)
    throws EndOfVectorOnWriteException;
  
  public abstract Object getCurrentElement();
  
  public abstract Object getCurrentElementAndAdvance();
  
  public abstract int getPtr();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatOutputList
 * JD-Core Version:    0.7.0.1
 */