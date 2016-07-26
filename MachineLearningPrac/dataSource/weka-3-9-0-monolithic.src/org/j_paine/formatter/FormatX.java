/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ class FormatX
/*   6:    */   extends FormatElement
/*   7:    */ {
/*   8:    */   public void write(FormatOutputList paramFormatOutputList, PrintStream paramPrintStream)
/*   9:    */   {
/*  10:433 */     paramPrintStream.print(" ");
/*  11:    */   }
/*  12:    */   
/*  13:    */   public void read(FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer, FormatMap paramFormatMap)
/*  14:    */   {
/*  15:442 */     paramInputStreamAndBuffer.advance(1);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public String toString()
/*  19:    */   {
/*  20:448 */     return "X";
/*  21:    */   }
/*  22:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatX
 * JD-Core Version:    0.7.0.1
 */