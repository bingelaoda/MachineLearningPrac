/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ abstract class FormatIOElement
/*   6:    */   extends FormatElement
/*   7:    */ {
/*   8:    */   private int width;
/*   9:    */   
/*  10:    */   void setWidth(int paramInt)
/*  11:    */   {
/*  12:316 */     this.width = paramInt;
/*  13:    */   }
/*  14:    */   
/*  15:    */   int getWidth()
/*  16:    */   {
/*  17:321 */     return this.width;
/*  18:    */   }
/*  19:    */   
/*  20:    */   public void write(FormatOutputList paramFormatOutputList, PrintStream paramPrintStream)
/*  21:    */     throws OutputFormatException
/*  22:    */   {
/*  23:328 */     paramFormatOutputList.checkCurrentElementForWrite(this);
/*  24:329 */     Object localObject = paramFormatOutputList.getCurrentElementAndAdvance();
/*  25:330 */     paramPrintStream.print(convertToString(localObject, paramFormatOutputList.getPtr() - 1));
/*  26:    */   }
/*  27:    */   
/*  28:    */   abstract String convertToString(Object paramObject, int paramInt)
/*  29:    */     throws OutputFormatException;
/*  30:    */   
/*  31:    */   public void read(FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer, FormatMap paramFormatMap)
/*  32:    */     throws InputFormatException
/*  33:    */   {
/*  34:352 */     Object localObject1 = paramInputStreamAndBuffer.getSlice(this.width, paramFormatInputList.getPtr(), this);
/*  35:355 */     if (paramFormatMap != null)
/*  36:    */     {
/*  37:356 */       localObject2 = paramFormatMap.getMapping((String)localObject1);
/*  38:357 */       if (localObject2 != null) {
/*  39:358 */         localObject1 = localObject2;
/*  40:    */       }
/*  41:    */     }
/*  42:365 */     Object localObject2 = convertFromString((String)localObject1, paramFormatInputList, paramInputStreamAndBuffer);
/*  43:366 */     paramFormatInputList.checkCurrentElementForRead(this, paramInputStreamAndBuffer);
/*  44:367 */     paramFormatInputList.putElementAndAdvance(localObject2, this, paramInputStreamAndBuffer);
/*  45:368 */     paramInputStreamAndBuffer.advance(this.width);
/*  46:    */   }
/*  47:    */   
/*  48:    */   abstract Object convertFromString(String paramString, FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer)
/*  49:    */     throws InputFormatException;
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatIOElement
 * JD-Core Version:    0.7.0.1
 */