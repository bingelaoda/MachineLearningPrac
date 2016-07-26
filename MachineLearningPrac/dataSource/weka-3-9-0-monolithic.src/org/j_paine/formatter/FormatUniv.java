package org.j_paine.formatter;

import java.io.PrintStream;

abstract class FormatUniv
{
  abstract void write(FormatOutputList paramFormatOutputList, PrintStream paramPrintStream)
    throws OutputFormatException;
  
  abstract void read(FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer, FormatMap paramFormatMap)
    throws InputFormatException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatUniv
 * JD-Core Version:    0.7.0.1
 */