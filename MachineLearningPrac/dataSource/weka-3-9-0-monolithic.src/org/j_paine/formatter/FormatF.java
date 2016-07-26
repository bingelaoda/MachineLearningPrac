/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.StringBufferInputStream;
/*   4:    */ 
/*   5:    */ class FormatF
/*   6:    */   extends FormatIOElement
/*   7:    */ {
/*   8:    */   private int d;
/*   9:    */   
/*  10:    */   public FormatF(int paramInt1, int paramInt2)
/*  11:    */   {
/*  12:741 */     setWidth(paramInt1);
/*  13:742 */     this.d = paramInt2;
/*  14:    */   }
/*  15:    */   
/*  16:    */   String convertToString(Object paramObject, int paramInt)
/*  17:    */     throws IllegalObjectOnWriteException, NumberTooWideOnWriteException
/*  18:    */   {
/*  19:753 */     if (((paramObject instanceof Integer)) || ((paramObject instanceof Long)) || ((paramObject instanceof Float)) || ((paramObject instanceof Double)))
/*  20:    */     {
/*  21:755 */       CJFormat localCJFormat = new CJFormat();
/*  22:756 */       localCJFormat.setWidth(getWidth());
/*  23:757 */       localCJFormat.setPrecision(this.d);
/*  24:758 */       localCJFormat.setPre("");
/*  25:759 */       localCJFormat.setPost("");
/*  26:760 */       localCJFormat.setLeadingZeroes(false);
/*  27:761 */       localCJFormat.setShowPlus(false);
/*  28:762 */       localCJFormat.setAlternate(false);
/*  29:763 */       localCJFormat.setShowSpace(false);
/*  30:764 */       localCJFormat.setLeftAlign(false);
/*  31:765 */       localCJFormat.setFmt('f');
/*  32:766 */       String str = localCJFormat.form(((Number)paramObject).doubleValue());
/*  33:769 */       if (str.length() > getWidth()) {
/*  34:770 */         throw new NumberTooWideOnWriteException((Number)paramObject, paramInt, toString());
/*  35:    */       }
/*  36:775 */       return str;
/*  37:    */     }
/*  38:778 */     throw new IllegalObjectOnWriteException(paramObject, paramInt, toString());
/*  39:    */   }
/*  40:    */   
/*  41:    */   Object convertFromString(String paramString, FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer)
/*  42:    */     throws InvalidNumberOnReadException
/*  43:    */   {
/*  44:796 */     NumberParser localNumberParser = Parsers.theParsers().number_parser;
/*  45:    */     
/*  46:798 */     localNumberParser.ReInit(new StringBufferInputStream(paramString));
/*  47:    */     try
/*  48:    */     {
/*  49:800 */       int i = localNumberParser.Float();
/*  50:801 */       return new Double(paramString.substring(i));
/*  51:    */     }
/*  52:    */     catch (ParseException localParseException)
/*  53:    */     {
/*  54:805 */       throw new InvalidNumberOnReadException(paramString, paramFormatInputList.getPtr(), toString(), paramInputStreamAndBuffer.getLineErrorReport(), localParseException.getMessage());
/*  55:    */     }
/*  56:    */     catch (TokenMgrError localTokenMgrError)
/*  57:    */     {
/*  58:813 */       throw new InvalidNumberOnReadException(paramString, paramFormatInputList.getPtr(), toString(), paramInputStreamAndBuffer.getLineErrorReport(), localTokenMgrError.getMessage());
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String toString()
/*  63:    */   {
/*  64:825 */     return "F" + getWidth() + "." + this.d;
/*  65:    */   }
/*  66:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatF
 * JD-Core Version:    0.7.0.1
 */