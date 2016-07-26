/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.StringBufferInputStream;
/*   4:    */ 
/*   5:    */ class FormatE
/*   6:    */   extends FormatIOElement
/*   7:    */ {
/*   8:    */   int d;
/*   9:    */   
/*  10:    */   public FormatE(int paramInt1, int paramInt2)
/*  11:    */   {
/*  12:841 */     setWidth(paramInt1);
/*  13:842 */     this.d = paramInt2;
/*  14:    */   }
/*  15:    */   
/*  16:    */   String convertToString(Object paramObject, int paramInt)
/*  17:    */     throws IllegalObjectOnWriteException, NumberTooWideOnWriteException
/*  18:    */   {
/*  19:853 */     if (((paramObject instanceof Integer)) || ((paramObject instanceof Long)) || ((paramObject instanceof Float)) || ((paramObject instanceof Double)))
/*  20:    */     {
/*  21:855 */       CJFormat localCJFormat = new CJFormat();
/*  22:856 */       localCJFormat.setWidth(getWidth());
/*  23:857 */       localCJFormat.setPrecision(this.d);
/*  24:858 */       localCJFormat.setPre("");
/*  25:859 */       localCJFormat.setPost("");
/*  26:860 */       localCJFormat.setLeadingZeroes(false);
/*  27:861 */       localCJFormat.setShowPlus(false);
/*  28:862 */       localCJFormat.setAlternate(false);
/*  29:863 */       localCJFormat.setShowSpace(false);
/*  30:864 */       localCJFormat.setLeftAlign(false);
/*  31:865 */       localCJFormat.setFmt('E');
/*  32:866 */       String str = localCJFormat.form(((Number)paramObject).doubleValue());
/*  33:869 */       if (str.length() > getWidth()) {
/*  34:870 */         throw new NumberTooWideOnWriteException((Number)paramObject, paramInt, toString());
/*  35:    */       }
/*  36:875 */       return str;
/*  37:    */     }
/*  38:878 */     throw new IllegalObjectOnWriteException(paramObject, paramInt, toString());
/*  39:    */   }
/*  40:    */   
/*  41:    */   Object convertFromString(String paramString, FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer)
/*  42:    */     throws InvalidNumberOnReadException
/*  43:    */   {
/*  44:896 */     NumberParser localNumberParser = Parsers.theParsers().number_parser;
/*  45:    */     
/*  46:898 */     localNumberParser.ReInit(new StringBufferInputStream(paramString));
/*  47:    */     try
/*  48:    */     {
/*  49:900 */       int i = localNumberParser.Float();
/*  50:901 */       return new Double(paramString.substring(i));
/*  51:    */     }
/*  52:    */     catch (ParseException localParseException)
/*  53:    */     {
/*  54:905 */       throw new InvalidNumberOnReadException(paramString, paramFormatInputList.getPtr(), toString(), paramInputStreamAndBuffer.getLineErrorReport(), localParseException.getMessage());
/*  55:    */     }
/*  56:    */     catch (TokenMgrError localTokenMgrError)
/*  57:    */     {
/*  58:913 */       throw new InvalidNumberOnReadException(paramString, paramFormatInputList.getPtr(), toString(), paramInputStreamAndBuffer.getLineErrorReport(), localTokenMgrError.getMessage());
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String toString()
/*  63:    */   {
/*  64:925 */     return "E" + getWidth() + "." + this.d;
/*  65:    */   }
/*  66:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatE
 * JD-Core Version:    0.7.0.1
 */