package org.j_paine.formatter;

import java.io.PrintStream;

abstract class FormatElement
  extends FormatUniv
{
  public abstract void write(FormatOutputList paramFormatOutputList, PrintStream paramPrintStream)
    throws OutputFormatException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatElement
 * JD-Core Version:    0.7.0.1
 */