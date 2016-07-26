/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ class FormatString
/*   6:    */   extends FormatElement
/*   7:    */ {
/*   8:    */   private String s;
/*   9:    */   
/*  10:    */   public FormatString(String paramString)
/*  11:    */   {
/*  12:967 */     this.s = paramString;
/*  13:    */   }
/*  14:    */   
/*  15:    */   public void write(FormatOutputList paramFormatOutputList, PrintStream paramPrintStream)
/*  16:    */   {
/*  17:973 */     paramPrintStream.print(this.s);
/*  18:    */   }
/*  19:    */   
/*  20:    */   public void read(FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer, FormatMap paramFormatMap)
/*  21:    */     throws InputFormatException
/*  22:    */   {
/*  23:983 */     String str = paramInputStreamAndBuffer.getSlice(this.s.length(), paramFormatInputList.getPtr(), this);
/*  24:984 */     if (!this.s.equals(str)) {
/*  25:985 */       throw new UnmatchedStringOnReadException(str, paramFormatInputList.getPtr(), toString(), paramInputStreamAndBuffer.getLineErrorReport());
/*  26:    */     }
/*  27:990 */     paramInputStreamAndBuffer.advance(this.s.length());
/*  28:    */   }
/*  29:    */   
/*  30:    */   public String toString()
/*  31:    */   {
/*  32:996 */     return "'" + this.s + "'";
/*  33:    */   }
/*  34:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatString
 * JD-Core Version:    0.7.0.1
 */