/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ public class ParseException
/*   4:    */   extends Exception
/*   5:    */ {
/*   6:    */   protected boolean specialConstructor;
/*   7:    */   public Token currentToken;
/*   8:    */   public int[][] expectedTokenSequences;
/*   9:    */   public String[] tokenImage;
/*  10:    */   
/*  11:    */   public ParseException(Token paramToken, int[][] paramArrayOfInt, String[] paramArrayOfString)
/*  12:    */   {
/*  13: 32 */     super("");
/*  14: 33 */     this.specialConstructor = true;
/*  15: 34 */     this.currentToken = paramToken;
/*  16: 35 */     this.expectedTokenSequences = paramArrayOfInt;
/*  17: 36 */     this.tokenImage = paramArrayOfString;
/*  18:    */   }
/*  19:    */   
/*  20:    */   public ParseException()
/*  21:    */   {
/*  22: 51 */     this.specialConstructor = false;
/*  23:    */   }
/*  24:    */   
/*  25:    */   public ParseException(String paramString)
/*  26:    */   {
/*  27: 55 */     super(paramString);
/*  28: 56 */     this.specialConstructor = false;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public String getMessage()
/*  32:    */   {
/*  33: 98 */     if (!this.specialConstructor) {
/*  34: 99 */       return super.getMessage();
/*  35:    */     }
/*  36:101 */     StringBuffer localStringBuffer = new StringBuffer();
/*  37:102 */     int i = 0;
/*  38:103 */     for (int j = 0; j < this.expectedTokenSequences.length; j++)
/*  39:    */     {
/*  40:104 */       if (i < this.expectedTokenSequences[j].length) {
/*  41:105 */         i = this.expectedTokenSequences[j].length;
/*  42:    */       }
/*  43:107 */       for (int k = 0; k < this.expectedTokenSequences[j].length; k++) {
/*  44:108 */         localStringBuffer.append(this.tokenImage[this.expectedTokenSequences[j][k]]).append(" ");
/*  45:    */       }
/*  46:110 */       if (this.expectedTokenSequences[j][(this.expectedTokenSequences[j].length - 1)] != 0) {
/*  47:111 */         localStringBuffer.append("...");
/*  48:    */       }
/*  49:113 */       localStringBuffer.append(this.eol).append("    ");
/*  50:    */     }
/*  51:115 */     String str = "Encountered \"";
/*  52:116 */     Token localToken = this.currentToken.next;
/*  53:117 */     for (int m = 0; m < i; m++)
/*  54:    */     {
/*  55:118 */       if (m != 0) {
/*  56:118 */         str = str + " ";
/*  57:    */       }
/*  58:119 */       if (localToken.kind == 0)
/*  59:    */       {
/*  60:120 */         str = str + this.tokenImage[0];
/*  61:121 */         break;
/*  62:    */       }
/*  63:123 */       str = str + add_escapes(localToken.image);
/*  64:124 */       localToken = localToken.next;
/*  65:    */     }
/*  66:126 */     str = str + "\" at line " + this.currentToken.next.beginLine + ", column " + this.currentToken.next.beginColumn;
/*  67:127 */     str = str + "." + this.eol;
/*  68:128 */     if (this.expectedTokenSequences.length == 1) {
/*  69:129 */       str = str + "Was expecting:" + this.eol + "    ";
/*  70:    */     } else {
/*  71:131 */       str = str + "Was expecting one of:" + this.eol + "    ";
/*  72:    */     }
/*  73:133 */     str = str + localStringBuffer.toString();
/*  74:134 */     return str;
/*  75:    */   }
/*  76:    */   
/*  77:140 */   protected String eol = System.getProperty("line.separator", "\n");
/*  78:    */   
/*  79:    */   protected String add_escapes(String paramString)
/*  80:    */   {
/*  81:148 */     StringBuffer localStringBuffer = new StringBuffer();
/*  82:150 */     for (int i = 0; i < paramString.length(); i++) {
/*  83:151 */       switch (paramString.charAt(i))
/*  84:    */       {
/*  85:    */       case '\000': 
/*  86:    */         break;
/*  87:    */       case '\b': 
/*  88:156 */         localStringBuffer.append("\\b");
/*  89:157 */         break;
/*  90:    */       case '\t': 
/*  91:159 */         localStringBuffer.append("\\t");
/*  92:160 */         break;
/*  93:    */       case '\n': 
/*  94:162 */         localStringBuffer.append("\\n");
/*  95:163 */         break;
/*  96:    */       case '\f': 
/*  97:165 */         localStringBuffer.append("\\f");
/*  98:166 */         break;
/*  99:    */       case '\r': 
/* 100:168 */         localStringBuffer.append("\\r");
/* 101:169 */         break;
/* 102:    */       case '"': 
/* 103:171 */         localStringBuffer.append("\\\"");
/* 104:172 */         break;
/* 105:    */       case '\'': 
/* 106:174 */         localStringBuffer.append("\\'");
/* 107:175 */         break;
/* 108:    */       case '\\': 
/* 109:177 */         localStringBuffer.append("\\\\");
/* 110:178 */         break;
/* 111:    */       default: 
/* 112:    */         char c;
/* 113:180 */         if (((c = paramString.charAt(i)) < ' ') || (c > '~'))
/* 114:    */         {
/* 115:181 */           String str = "0000" + Integer.toString(c, 16);
/* 116:182 */           localStringBuffer.append("\\u" + str.substring(str.length() - 4, str.length()));
/* 117:    */         }
/* 118:    */         else
/* 119:    */         {
/* 120:184 */           localStringBuffer.append(c);
/* 121:    */         }
/* 122:    */         break;
/* 123:    */       }
/* 124:    */     }
/* 125:189 */     return localStringBuffer.toString();
/* 126:    */   }
/* 127:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.ParseException
 * JD-Core Version:    0.7.0.1
 */