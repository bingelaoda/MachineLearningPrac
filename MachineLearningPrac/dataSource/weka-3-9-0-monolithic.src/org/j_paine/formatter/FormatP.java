/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ class FormatP
/*   6:    */   extends FormatElement
/*   7:    */ {
/*   8:392 */   FormatRepeatedItem ritem = null;
/*   9:    */   
/*  10:    */   public FormatRepeatedItem getRepeatedItem()
/*  11:    */   {
/*  12:395 */     return this.ritem;
/*  13:    */   }
/*  14:    */   
/*  15:    */   public FormatP(int paramInt, FormatUniv paramFormatUniv)
/*  16:    */   {
/*  17:399 */     if (paramFormatUniv != null) {
/*  18:400 */       this.ritem = new FormatRepeatedItem(paramInt, paramFormatUniv);
/*  19:    */     }
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void write(FormatOutputList paramFormatOutputList, PrintStream paramPrintStream) {}
/*  23:    */   
/*  24:    */   public void read(FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer, FormatMap paramFormatMap) {}
/*  25:    */   
/*  26:    */   public String toString()
/*  27:    */   {
/*  28:422 */     return "P";
/*  29:    */   }
/*  30:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatP
 * JD-Core Version:    0.7.0.1
 */