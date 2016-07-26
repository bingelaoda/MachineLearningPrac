/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ public class TokenMgrError
/*   4:    */   extends Error
/*   5:    */ {
/*   6:    */   static final int LEXICAL_ERROR = 0;
/*   7:    */   static final int STATIC_LEXER_ERROR = 1;
/*   8:    */   static final int INVALID_LEXICAL_STATE = 2;
/*   9:    */   static final int LOOP_DETECTED = 3;
/*  10:    */   int errorCode;
/*  11:    */   
/*  12:    */   protected static final String addEscapes(String paramString)
/*  13:    */   {
/*  14: 41 */     StringBuffer localStringBuffer = new StringBuffer();
/*  15: 43 */     for (int i = 0; i < paramString.length(); i++) {
/*  16: 44 */       switch (paramString.charAt(i))
/*  17:    */       {
/*  18:    */       case '\000': 
/*  19:    */         break;
/*  20:    */       case '\b': 
/*  21: 49 */         localStringBuffer.append("\\b");
/*  22: 50 */         break;
/*  23:    */       case '\t': 
/*  24: 52 */         localStringBuffer.append("\\t");
/*  25: 53 */         break;
/*  26:    */       case '\n': 
/*  27: 55 */         localStringBuffer.append("\\n");
/*  28: 56 */         break;
/*  29:    */       case '\f': 
/*  30: 58 */         localStringBuffer.append("\\f");
/*  31: 59 */         break;
/*  32:    */       case '\r': 
/*  33: 61 */         localStringBuffer.append("\\r");
/*  34: 62 */         break;
/*  35:    */       case '"': 
/*  36: 64 */         localStringBuffer.append("\\\"");
/*  37: 65 */         break;
/*  38:    */       case '\'': 
/*  39: 67 */         localStringBuffer.append("\\'");
/*  40: 68 */         break;
/*  41:    */       case '\\': 
/*  42: 70 */         localStringBuffer.append("\\\\");
/*  43: 71 */         break;
/*  44:    */       default: 
/*  45:    */         char c;
/*  46: 73 */         if (((c = paramString.charAt(i)) < ' ') || (c > '~'))
/*  47:    */         {
/*  48: 74 */           String str = "0000" + Integer.toString(c, 16);
/*  49: 75 */           localStringBuffer.append("\\u" + str.substring(str.length() - 4, str.length()));
/*  50:    */         }
/*  51:    */         else
/*  52:    */         {
/*  53: 77 */           localStringBuffer.append(c);
/*  54:    */         }
/*  55:    */         break;
/*  56:    */       }
/*  57:    */     }
/*  58: 82 */     return localStringBuffer.toString();
/*  59:    */   }
/*  60:    */   
/*  61:    */   protected static String LexicalError(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, String paramString, char paramChar)
/*  62:    */   {
/*  63: 98 */     return "Lexical error at line " + paramInt2 + ", column " + paramInt3 + ".  Encountered: " + (paramBoolean ? "<EOF> " : new StringBuffer().append("\"").append(addEscapes(String.valueOf(paramChar))).append("\"").append(" (").append(paramChar).append("), ").toString()) + "after : \"" + addEscapes(paramString) + "\"";
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getMessage()
/*  67:    */   {
/*  68:115 */     return super.getMessage();
/*  69:    */   }
/*  70:    */   
/*  71:    */   public TokenMgrError() {}
/*  72:    */   
/*  73:    */   public TokenMgrError(String paramString, int paramInt)
/*  74:    */   {
/*  75:126 */     super(paramString);
/*  76:127 */     this.errorCode = paramInt;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public TokenMgrError(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, String paramString, char paramChar, int paramInt4)
/*  80:    */   {
/*  81:131 */     this(LexicalError(paramBoolean, paramInt1, paramInt2, paramInt3, paramString, paramChar), paramInt4);
/*  82:    */   }
/*  83:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.TokenMgrError
 * JD-Core Version:    0.7.0.1
 */