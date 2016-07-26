/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.StringBufferInputStream;
/*   4:    */ 
/*   5:    */ class FormatI
/*   6:    */   extends FormatIOElement
/*   7:    */ {
/*   8:    */   public FormatI(int paramInt)
/*   9:    */   {
/*  10:543 */     setWidth(paramInt);
/*  11:    */   }
/*  12:    */   
/*  13:    */   String convertToString(Object paramObject, int paramInt)
/*  14:    */     throws IllegalObjectOnWriteException, NumberTooWideOnWriteException
/*  15:    */   {
/*  16:554 */     if (((paramObject instanceof Integer)) || ((paramObject instanceof Long)))
/*  17:    */     {
/*  18:555 */       CJFormat localCJFormat = new CJFormat();
/*  19:556 */       localCJFormat.setWidth(getWidth());
/*  20:557 */       localCJFormat.setPre("");
/*  21:558 */       localCJFormat.setPost("");
/*  22:559 */       localCJFormat.setLeadingZeroes(false);
/*  23:560 */       localCJFormat.setShowPlus(false);
/*  24:561 */       localCJFormat.setAlternate(false);
/*  25:562 */       localCJFormat.setShowSpace(false);
/*  26:563 */       localCJFormat.setLeftAlign(false);
/*  27:564 */       localCJFormat.setFmt('i');
/*  28:565 */       String str = localCJFormat.form(((Number)paramObject).longValue());
/*  29:568 */       if (str.length() > getWidth()) {
/*  30:569 */         throw new NumberTooWideOnWriteException((Number)paramObject, paramInt, toString());
/*  31:    */       }
/*  32:574 */       return str;
/*  33:    */     }
/*  34:576 */     if ((paramObject instanceof String)) {
/*  35:580 */       return convertToString(new Integer(((String)paramObject).charAt(0)), paramInt);
/*  36:    */     }
/*  37:583 */     throw new IllegalObjectOnWriteException(paramObject, paramInt, toString());
/*  38:    */   }
/*  39:    */   
/*  40:    */   Object convertFromString(String paramString, FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer)
/*  41:    */     throws InvalidNumberOnReadException
/*  42:    */   {
/*  43:601 */     NumberParser localNumberParser = Parsers.theParsers().number_parser;
/*  44:    */     
/*  45:603 */     localNumberParser.ReInit(new StringBufferInputStream(paramString));
/*  46:    */     try
/*  47:    */     {
/*  48:605 */       int i = localNumberParser.Integer();
/*  49:606 */       return new Long(paramString.substring(i));
/*  50:    */     }
/*  51:    */     catch (ParseException localParseException)
/*  52:    */     {
/*  53:610 */       throw new InvalidNumberOnReadException(paramString, paramFormatInputList.getPtr(), toString(), paramInputStreamAndBuffer.getLineErrorReport(), localParseException.getMessage());
/*  54:    */     }
/*  55:    */     catch (TokenMgrError localTokenMgrError)
/*  56:    */     {
/*  57:618 */       throw new InvalidNumberOnReadException(paramString, paramFormatInputList.getPtr(), toString(), paramInputStreamAndBuffer.getLineErrorReport(), localTokenMgrError.getMessage());
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   public String toString()
/*  62:    */   {
/*  63:630 */     return "I" + getWidth();
/*  64:    */   }
/*  65:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatI
 * JD-Core Version:    0.7.0.1
 */