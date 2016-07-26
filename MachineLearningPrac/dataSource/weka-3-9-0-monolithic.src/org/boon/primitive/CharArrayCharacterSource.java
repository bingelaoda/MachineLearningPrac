/*   1:    */ package org.boon.primitive;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import org.boon.core.reflection.FastStringUtils;
/*   5:    */ 
/*   6:    */ public class CharArrayCharacterSource
/*   7:    */   implements CharacterSource
/*   8:    */ {
/*   9:    */   private char[] chars;
/*  10: 38 */   private int index = 0;
/*  11:    */   private boolean foundEscape;
/*  12:    */   private int ch;
/*  13:    */   
/*  14:    */   public CharArrayCharacterSource(char[] chars)
/*  15:    */   {
/*  16: 47 */     this.chars = chars;
/*  17:    */   }
/*  18:    */   
/*  19:    */   public CharArrayCharacterSource(String string)
/*  20:    */   {
/*  21: 52 */     this.chars = FastStringUtils.toCharArray(string);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public final int nextChar()
/*  25:    */   {
/*  26: 58 */     return this.ch = this.chars[(this.index++)];
/*  27:    */   }
/*  28:    */   
/*  29:    */   public final int safeNextChar()
/*  30:    */   {
/*  31: 62 */     return this.ch = this.index + 1 < this.chars.length ? this.chars[(this.index++)] : -1;
/*  32:    */   }
/*  33:    */   
/*  34: 66 */   private final char[] EMPTY_CHARS = new char[0];
/*  35:    */   
/*  36:    */   public final char[] findNextChar(int match, int esc)
/*  37:    */   {
/*  38: 70 */     int idx = this.index;
/*  39: 71 */     int startIndex = idx;
/*  40: 72 */     this.foundEscape = false;
/*  41: 73 */     char[] _chars = this.chars;
/*  42: 74 */     int ch = 0;
/*  43: 75 */     for (; idx < _chars.length; idx++)
/*  44:    */     {
/*  45: 76 */       ch = _chars[idx];
/*  46: 77 */       if ((ch == match) || (ch == esc))
/*  47:    */       {
/*  48: 78 */         if (ch == match)
/*  49:    */         {
/*  50: 80 */           this.index = (idx + 1);
/*  51: 81 */           this.ch = ch;
/*  52: 82 */           return Arrays.copyOfRange(_chars, startIndex, idx);
/*  53:    */         }
/*  54: 83 */         if (ch == esc)
/*  55:    */         {
/*  56: 84 */           this.foundEscape = true;
/*  57: 88 */           if (idx + 1 < _chars.length) {
/*  58: 89 */             idx++;
/*  59:    */           }
/*  60:    */         }
/*  61:    */       }
/*  62:    */     }
/*  63: 95 */     this.index = idx;
/*  64: 96 */     this.ch = ch;
/*  65: 97 */     return this.EMPTY_CHARS;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public boolean hadEscape()
/*  69:    */   {
/*  70:103 */     return this.foundEscape;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public char[] readNumber()
/*  74:    */   {
/*  75:107 */     char[] results = CharScanner.readNumber(this.chars, this.index);
/*  76:108 */     this.index += results.length;
/*  77:109 */     return results;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public final int currentChar()
/*  81:    */   {
/*  82:114 */     return this.chars[this.index];
/*  83:    */   }
/*  84:    */   
/*  85:    */   public final boolean hasChar()
/*  86:    */   {
/*  87:119 */     return this.index + 1 < this.chars.length;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public final boolean consumeIfMatch(char[] match)
/*  91:    */   {
/*  92:126 */     int idx = this.index;
/*  93:127 */     char[] _chars = this.chars;
/*  94:    */     
/*  95:129 */     boolean ok = true;
/*  96:131 */     for (int i = 0; i < match.length; idx++)
/*  97:    */     {
/*  98:132 */       ok &= match[i] == _chars[idx];
/*  99:133 */       if (!ok) {
/* 100:    */         break;
/* 101:    */       }
/* 102:131 */       i++;
/* 103:    */     }
/* 104:136 */     if (ok)
/* 105:    */     {
/* 106:137 */       this.index = idx;
/* 107:138 */       return true;
/* 108:    */     }
/* 109:140 */     return false;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public final int location()
/* 113:    */   {
/* 114:146 */     return this.index;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void skipWhiteSpace()
/* 118:    */   {
/* 119:154 */     this.index = CharScanner.skipWhiteSpace(this.chars, this.index);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String errorDetails(String message)
/* 123:    */   {
/* 124:161 */     if (this.index < this.chars.length) {
/* 125:162 */       this.ch = this.chars[this.index];
/* 126:    */     } else {
/* 127:164 */       this.ch = this.chars[(this.chars.length - 1)];
/* 128:    */     }
/* 129:166 */     return CharScanner.errorDetails(message, this.chars, this.index, this.ch);
/* 130:    */   }
/* 131:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.CharArrayCharacterSource
 * JD-Core Version:    0.7.0.1
 */