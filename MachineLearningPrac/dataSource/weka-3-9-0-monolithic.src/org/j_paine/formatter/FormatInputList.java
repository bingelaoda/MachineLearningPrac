package org.j_paine.formatter;

abstract interface FormatInputList
{
  public abstract void checkCurrentElementForRead(FormatElement paramFormatElement, InputStreamAndBuffer paramInputStreamAndBuffer)
    throws InputFormatException;
  
  public abstract void putElementAndAdvance(Object paramObject, FormatElement paramFormatElement, InputStreamAndBuffer paramInputStreamAndBuffer)
    throws InputFormatException;
  
  public abstract int getPtr();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatInputList
 * JD-Core Version:    0.7.0.1
 */