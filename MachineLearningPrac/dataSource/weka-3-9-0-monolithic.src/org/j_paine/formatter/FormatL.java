/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.StringBufferInputStream;
/*   4:    */ 
/*   5:    */ class FormatL
/*   6:    */   extends FormatIOElement
/*   7:    */ {
/*   8:    */   public FormatL(int paramInt)
/*   9:    */   {
/*  10:642 */     setWidth(paramInt);
/*  11:    */   }
/*  12:    */   
/*  13:    */   String convertToString(Object paramObject, int paramInt)
/*  14:    */     throws IllegalObjectOnWriteException, NumberTooWideOnWriteException
/*  15:    */   {
/*  16:652 */     if ((paramObject instanceof Boolean))
/*  17:    */     {
/*  18:653 */       char[] arrayOfChar = new char[getWidth()];
/*  19:656 */       for (int i = 0; i < arrayOfChar.length - 1; i++) {
/*  20:657 */         arrayOfChar[i] = ' ';
/*  21:    */       }
/*  22:659 */       arrayOfChar[i] = (((Boolean)paramObject).booleanValue() == true ? 84 : 'F');
/*  23:    */       
/*  24:661 */       String str = new String(arrayOfChar);
/*  25:664 */       if (str.length() > getWidth()) {
/*  26:665 */         throw new NumberTooWideOnWriteException((Number)paramObject, paramInt, toString());
/*  27:    */       }
/*  28:670 */       return str;
/*  29:    */     }
/*  30:673 */     throw new IllegalObjectOnWriteException(paramObject, paramInt, toString());
/*  31:    */   }
/*  32:    */   
/*  33:    */   Object convertFromString(String paramString, FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer)
/*  34:    */     throws InvalidNumberOnReadException
/*  35:    */   {
/*  36:691 */     NumberParser localNumberParser = Parsers.theParsers().number_parser;
/*  37:    */     
/*  38:693 */     localNumberParser.ReInit(new StringBufferInputStream(paramString));
/*  39:    */     try
/*  40:    */     {
/*  41:695 */       int i = localNumberParser.Boolean();
/*  42:696 */       int j = paramString.substring(i).charAt(0);
/*  43:    */       Boolean localBoolean;
/*  44:699 */       if ((j == 116) || (j == 84)) {
/*  45:700 */         localBoolean = new Boolean(true);
/*  46:701 */       } else if ((j == 102) || (j == 70)) {
/*  47:702 */         localBoolean = new Boolean(false);
/*  48:    */       } else {
/*  49:704 */         throw new ParseException("bad logical value");
/*  50:    */       }
/*  51:705 */       return localBoolean;
/*  52:    */     }
/*  53:    */     catch (ParseException localParseException)
/*  54:    */     {
/*  55:708 */       throw new InvalidNumberOnReadException(paramString, paramFormatInputList.getPtr(), toString(), paramInputStreamAndBuffer.getLineErrorReport(), localParseException.getMessage());
/*  56:    */     }
/*  57:    */     catch (TokenMgrError localTokenMgrError)
/*  58:    */     {
/*  59:716 */       throw new InvalidNumberOnReadException(paramString, paramFormatInputList.getPtr(), toString(), paramInputStreamAndBuffer.getLineErrorReport(), localTokenMgrError.getMessage());
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public String toString()
/*  64:    */   {
/*  65:727 */     return "L" + getWidth();
/*  66:    */   }
/*  67:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatL
 * JD-Core Version:    0.7.0.1
 */