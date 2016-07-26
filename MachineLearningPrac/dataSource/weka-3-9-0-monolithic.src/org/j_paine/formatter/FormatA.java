/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ class FormatA
/*   4:    */   extends FormatIOElement
/*   5:    */ {
/*   6:    */   public FormatA(int paramInt)
/*   7:    */   {
/*   8:459 */     setWidth(paramInt);
/*   9:    */   }
/*  10:    */   
/*  11:    */   String convertToString(Object paramObject, int paramInt)
/*  12:    */     throws IllegalObjectOnWriteException, StringTooWideOnWriteException
/*  13:    */   {
/*  14:469 */     if ((paramObject instanceof String))
/*  15:    */     {
/*  16:471 */       String str = (String)paramObject;
/*  17:472 */       if ((getWidth() != -1) && (str.length() > getWidth())) {
/*  18:473 */         return str.substring(0, getWidth());
/*  19:    */       }
/*  20:475 */       if (getWidth() > str.length())
/*  21:    */       {
/*  22:476 */         arrayOfChar = new char[getWidth() - str.length()];
/*  23:478 */         for (i = 0; i < arrayOfChar.length; i++) {
/*  24:479 */           arrayOfChar[i] = ' ';
/*  25:    */         }
/*  26:481 */         return new String(arrayOfChar) + str;
/*  27:    */       }
/*  28:484 */       return str;
/*  29:    */     }
/*  30:488 */     char[] arrayOfChar = new char[getWidth()];
/*  31:493 */     for (int i = 0; i < arrayOfChar.length; i++) {
/*  32:494 */       arrayOfChar[i] = '#';
/*  33:    */     }
/*  34:496 */     return new String(arrayOfChar);
/*  35:    */   }
/*  36:    */   
/*  37:    */   Object convertFromString(String paramString, FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer)
/*  38:    */     throws InvalidNumberOnReadException
/*  39:    */   {
/*  40:511 */     int i = getWidth() - paramString.length();
/*  41:516 */     if (i > 0)
/*  42:    */     {
/*  43:517 */       char[] arrayOfChar = new char[i];
/*  44:518 */       for (int j = 0; j < i; j++) {
/*  45:519 */         arrayOfChar[j] = ' ';
/*  46:    */       }
/*  47:520 */       String str = new String(arrayOfChar);
/*  48:    */       
/*  49:522 */       return paramString.concat(str);
/*  50:    */     }
/*  51:526 */     return paramString;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String toString()
/*  55:    */   {
/*  56:532 */     return "A" + getWidth();
/*  57:    */   }
/*  58:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatA
 * JD-Core Version:    0.7.0.1
 */