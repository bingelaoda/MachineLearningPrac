/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ class FormatRepeatedItem
/*   6:    */   extends FormatUniv
/*   7:    */ {
/*   8:249 */   private int r = 1;
/*   9:250 */   private FormatUniv format_univ = null;
/*  10:    */   
/*  11:    */   public FormatRepeatedItem(FormatUniv paramFormatUniv)
/*  12:    */   {
/*  13:255 */     this(1, paramFormatUniv);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public FormatRepeatedItem(int paramInt, FormatUniv paramFormatUniv)
/*  17:    */   {
/*  18:260 */     this.r = paramInt;
/*  19:261 */     this.format_univ = paramFormatUniv;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void write(FormatOutputList paramFormatOutputList, PrintStream paramPrintStream)
/*  23:    */     throws OutputFormatException
/*  24:    */   {
/*  25:268 */     for (int i = 1; i <= this.r; i++) {
/*  26:269 */       this.format_univ.write(paramFormatOutputList, paramPrintStream);
/*  27:    */     }
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void read(FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer, FormatMap paramFormatMap)
/*  31:    */     throws InputFormatException
/*  32:    */   {
/*  33:279 */     for (int i = 1; i <= this.r; i++) {
/*  34:280 */       this.format_univ.read(paramFormatInputList, paramInputStreamAndBuffer, paramFormatMap);
/*  35:    */     }
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String toString()
/*  39:    */   {
/*  40:286 */     if (this.r == 1) {
/*  41:287 */       return this.format_univ.toString();
/*  42:    */     }
/*  43:289 */     return this.r + "(" + this.format_univ.toString() + ")";
/*  44:    */   }
/*  45:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatRepeatedItem
 * JD-Core Version:    0.7.0.1
 */