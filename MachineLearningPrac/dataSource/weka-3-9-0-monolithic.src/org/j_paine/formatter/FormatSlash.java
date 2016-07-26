/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ class FormatSlash
/*   6:    */   extends FormatElement
/*   7:    */ {
/*   8:    */   public void write(FormatOutputList paramFormatOutputList, PrintStream paramPrintStream)
/*   9:    */   {
/*  10:936 */     paramPrintStream.println();
/*  11:    */   }
/*  12:    */   
/*  13:    */   public void read(FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer, FormatMap paramFormatMap)
/*  14:    */     throws InputFormatException
/*  15:    */   {
/*  16:946 */     paramInputStreamAndBuffer.readLine(paramFormatInputList.getPtr(), this);
/*  17:    */   }
/*  18:    */   
/*  19:    */   public String toString()
/*  20:    */   {
/*  21:952 */     return "/";
/*  22:    */   }
/*  23:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatSlash
 * JD-Core Version:    0.7.0.1
 */