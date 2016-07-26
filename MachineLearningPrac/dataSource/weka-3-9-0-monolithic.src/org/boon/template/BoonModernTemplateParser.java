/*   1:    */ package org.boon.template;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import org.boon.Boon;
/*   6:    */ import org.boon.Str;
/*   7:    */ import org.boon.core.reflection.FastStringUtils;
/*   8:    */ import org.boon.primitive.CharScanner;
/*   9:    */ import org.boon.template.support.Token;
/*  10:    */ import org.boon.template.support.TokenTypes;
/*  11:    */ 
/*  12:    */ public class BoonModernTemplateParser
/*  13:    */   implements TemplateParser
/*  14:    */ {
/*  15:    */   char[] charArray;
/*  16:    */   int index;
/*  17:    */   int ch;
/*  18: 52 */   private List<Token> tokenList = new ArrayList();
/*  19:    */   
/*  20:    */   public void TemplateParser() {}
/*  21:    */   
/*  22:    */   public void parse(String string)
/*  23:    */   {
/*  24: 62 */     this.charArray = FastStringUtils.toCharArray(string);
/*  25: 63 */     this.index = 0;
/*  26:    */     
/*  27: 65 */     this.tokenList.clear();
/*  28:    */     
/*  29:    */ 
/*  30: 68 */     processLoop();
/*  31:    */   }
/*  32:    */   
/*  33:    */   public List<Token> getTokenList()
/*  34:    */   {
/*  35: 74 */     return this.tokenList;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void displayTokens(String template)
/*  39:    */   {
/*  40: 81 */     for (Token token : getTokenList()) {
/*  41: 82 */       Boon.puts(new Object[] { "token", token, Str.slc(template, token.start(), token.stop()) });
/*  42:    */     }
/*  43:    */   }
/*  44:    */   
/*  45:    */   private void processLoop()
/*  46:    */   {
/*  47:    */     for (;;)
/*  48:    */     {
/*  49: 92 */       int startIndex = this.index;
/*  50:    */       
/*  51: 94 */       this.index = CharScanner.findChars(TokenTypes.EXPRESSION_START.handleBarStyle(), this.index, this.charArray);
/*  52: 97 */       if (this.index != -1)
/*  53:    */       {
/*  54: 99 */         if (startIndex != this.index) {
/*  55:100 */           this.tokenList.add(Token.text(startIndex, this.index));
/*  56:    */         }
/*  57:103 */         this.index += TokenTypes.EXPRESSION_START.handleBarStyle().length;
/*  58:105 */         if (!handleCommandOrExpression()) {
/*  59:    */           break;
/*  60:    */         }
/*  61:    */       }
/*  62:    */       else
/*  63:    */       {
/*  64:109 */         if (startIndex == this.charArray.length) {
/*  65:    */           break;
/*  66:    */         }
/*  67:110 */         this.tokenList.add(Token.text(startIndex, this.charArray.length)); break;
/*  68:    */       }
/*  69:115 */       if (this.index <= this.charArray.length) {
/*  70:115 */         if (this.index == -1) {
/*  71:    */           break;
/*  72:    */         }
/*  73:    */       }
/*  74:    */     }
/*  75:    */   }
/*  76:    */   
/*  77:    */   private boolean handleCommandOrExpression()
/*  78:    */   {
/*  79:124 */     if (this.index > this.charArray.length) {
/*  80:125 */       return false;
/*  81:    */     }
/*  82:128 */     this.ch = this.charArray[this.index];
/*  83:130 */     if (this.ch == 35) {
/*  84:131 */       return handleCommand();
/*  85:    */     }
/*  86:132 */     if (this.ch == 33) {
/*  87:133 */       return handleComment();
/*  88:    */     }
/*  89:136 */     return handleExpression();
/*  90:    */   }
/*  91:    */   
/*  92:    */   private boolean handleComment()
/*  93:    */   {
/*  94:142 */     this.index += 1;
/*  95:143 */     int foundIndex = CharScanner.findChars(TokenTypes.EXPRESSION_END.handleBarStyle(), this.index, this.charArray);
/*  96:146 */     if (foundIndex == -1) {
/*  97:147 */       return false;
/*  98:    */     }
/*  99:150 */     this.index = (foundIndex + TokenTypes.EXPRESSION_END.handleBarStyle().length);
/* 100:151 */     return true;
/* 101:    */   }
/* 102:    */   
/* 103:    */   private boolean handleExpression()
/* 104:    */   {
/* 105:156 */     int startIndex = this.index;
/* 106:157 */     this.index = CharScanner.findChars(TokenTypes.EXPRESSION_END.handleBarStyle(), this.index, this.charArray);
/* 107:158 */     if (this.index > 0)
/* 108:    */     {
/* 109:159 */       this.tokenList.add(Token.expression(startIndex, this.index));
/* 110:160 */       this.index += TokenTypes.EXPRESSION_END.handleBarStyle().length;
/* 111:162 */       if (this.index < this.charArray.length)
/* 112:    */       {
/* 113:163 */         char c = this.charArray[this.index];
/* 114:164 */         if (c == '}') {
/* 115:165 */           this.index += 1;
/* 116:    */         }
/* 117:    */       }
/* 118:169 */       return true;
/* 119:    */     }
/* 120:171 */     return false;
/* 121:    */   }
/* 122:    */   
/* 123:    */   private boolean handleCommand()
/* 124:    */   {
/* 125:178 */     int startIndex = this.index + 1;
/* 126:179 */     this.index = CharScanner.findChars(TokenTypes.EXPRESSION_END.handleBarStyle(), this.index, this.charArray);
/* 127:180 */     if (this.index == -1) {
/* 128:181 */       return false;
/* 129:    */     }
/* 130:184 */     this.tokenList.add(Token.commandStart(startIndex, this.index));
/* 131:    */     
/* 132:    */ 
/* 133:187 */     this.index += TokenTypes.EXPRESSION_END.handleBarStyle().length;
/* 134:    */     
/* 135:    */ 
/* 136:190 */     Token commandBody = Token.commandBody(this.index, -1);
/* 137:191 */     this.tokenList.add(commandBody);
/* 138:    */     for (;;)
/* 139:    */     {
/* 140:195 */       startIndex = this.index;
/* 141:    */       
/* 142:197 */       this.index = CharScanner.findChars(TokenTypes.EXPRESSION_START.handleBarStyle(), this.index, this.charArray);
/* 143:201 */       if (this.index == -1) {
/* 144:202 */         return false;
/* 145:    */       }
/* 146:206 */       if (startIndex != this.index) {
/* 147:207 */         this.tokenList.add(Token.text(startIndex, this.index));
/* 148:    */       }
/* 149:209 */       this.index += TokenTypes.EXPRESSION_START.handleBarStyle().length;
/* 150:212 */       if (this.index >= this.charArray.length) {
/* 151:213 */         return false;
/* 152:    */       }
/* 153:216 */       this.ch = this.charArray[this.index];
/* 154:218 */       if (this.ch == 35)
/* 155:    */       {
/* 156:219 */         if (!handleCommand()) {
/* 157:    */           break;
/* 158:    */         }
/* 159:    */       }
/* 160:220 */       else if (this.ch == 47)
/* 161:    */       {
/* 162:221 */         commandBody.stop(this.index - 2);
/* 163:    */         
/* 164:    */ 
/* 165:224 */         this.index = CharScanner.findChars(TokenTypes.EXPRESSION_END.handleBarStyle(), this.index, this.charArray);
/* 166:226 */         if (this.index < 0) {
/* 167:226 */           return false;
/* 168:    */         }
/* 169:227 */         this.index += TokenTypes.EXPRESSION_END.handleBarStyle().length;
/* 170:    */       }
/* 171:232 */       else if (!handleExpression())
/* 172:    */       {
/* 173:    */         break;
/* 174:    */       }
/* 175:    */     }
/* 176:238 */     return true;
/* 177:    */   }
/* 178:    */   
/* 179:    */   private Token textToken(Token text)
/* 180:    */   {
/* 181:245 */     if (text != null)
/* 182:    */     {
/* 183:246 */       text.stop(this.index);
/* 184:247 */       if (text.start() != text.stop()) {
/* 185:248 */         this.tokenList.add(text);
/* 186:    */       }
/* 187:250 */       text = null;
/* 188:    */     }
/* 189:252 */     return text;
/* 190:    */   }
/* 191:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.template.BoonModernTemplateParser
 * JD-Core Version:    0.7.0.1
 */