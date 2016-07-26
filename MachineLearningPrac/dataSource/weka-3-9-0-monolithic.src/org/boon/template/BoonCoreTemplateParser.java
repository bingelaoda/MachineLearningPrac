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
/*  12:    */ public class BoonCoreTemplateParser
/*  13:    */   implements TemplateParser
/*  14:    */ {
/*  15:    */   char[] charArray;
/*  16:    */   int index;
/*  17:    */   char ch;
/*  18:370 */   private List<Token> tokenList = new ArrayList();
/*  19:    */   
/*  20:    */   public void parse(String string)
/*  21:    */   {
/*  22:376 */     this.charArray = FastStringUtils.toCharArray(string);
/*  23:377 */     this.index = 0;
/*  24:    */     
/*  25:379 */     this.tokenList.clear();
/*  26:    */     
/*  27:381 */     processLoop();
/*  28:    */   }
/*  29:    */   
/*  30:    */   private void processLoop()
/*  31:    */   {
/*  32:387 */     Token text = Token.text(this.index, -1);
/*  33:390 */     for (; this.index < this.charArray.length; this.index += 1)
/*  34:    */     {
/*  35:391 */       this.ch = this.charArray[this.index];
/*  36:393 */       if (this.ch == '<')
/*  37:    */       {
/*  38:394 */         if (CharScanner.matchChars(TokenTypes.COMMAND_START.jstlStyle(), this.index, this.charArray))
/*  39:    */         {
/*  40:397 */           text = textToken(text);
/*  41:    */           
/*  42:399 */           this.index += TokenTypes.COMMAND_START.jstlStyle().length;
/*  43:400 */           handleCommand();
/*  44:    */         }
/*  45:    */       }
/*  46:405 */       else if (this.ch == '$')
/*  47:    */       {
/*  48:408 */         char ch1 = this.charArray[(this.index + 1)];
/*  49:409 */         if (ch1 == '{')
/*  50:    */         {
/*  51:410 */           if (CharScanner.matchChars(TokenTypes.EXPRESSION_START.jstlStyle(), this.index, this.charArray))
/*  52:    */           {
/*  53:413 */             text = textToken(text);
/*  54:    */             
/*  55:415 */             this.index += TokenTypes.EXPRESSION_START.jstlStyle().length;
/*  56:416 */             handleCurlyExpression();
/*  57:417 */             text = Token.text(this.index, -1);
/*  58:418 */             this.index -= 1;
/*  59:    */           }
/*  60:    */         }
/*  61:    */         else
/*  62:    */         {
/*  63:425 */           text = textToken(text);
/*  64:426 */           this.index += 1;
/*  65:427 */           handleExpression(null);
/*  66:428 */           text = Token.text(this.index, -1);
/*  67:429 */           this.index -= 1;
/*  68:    */         }
/*  69:    */       }
/*  70:435 */       else if (text == null)
/*  71:    */       {
/*  72:437 */         text = Token.text(this.index, -1);
/*  73:    */       }
/*  74:    */     }
/*  75:442 */     if (text != null)
/*  76:    */     {
/*  77:443 */       text.stop(this.charArray.length);
/*  78:444 */       this.tokenList.add(text);
/*  79:    */     }
/*  80:    */   }
/*  81:    */   
/*  82:    */   private void handleCurlyExpression()
/*  83:    */   {
/*  84:450 */     int startIndex = this.index;
/*  85:451 */     this.index = CharScanner.findChars(TokenTypes.EXPRESSION_END.jstlStyle(), this.index, this.charArray);
/*  86:452 */     if (this.index > 0)
/*  87:    */     {
/*  88:453 */       this.tokenList.add(Token.expression(startIndex, this.index));
/*  89:454 */       this.index += TokenTypes.EXPRESSION_END.jstlStyle().length;
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   private void handleExpression(String term)
/*  94:    */   {
/*  95:461 */     int startIndex = this.index;
/*  96:462 */     this.index = CharScanner.findWhiteSpace(this.index, this.charArray);
/*  97:465 */     if ((term != null) && 
/*  98:466 */       (this.index == -1))
/*  99:    */     {
/* 100:467 */       this.index = startIndex;
/* 101:468 */       this.index = CharScanner.findChars(term.toCharArray(), this.index, this.charArray);
/* 102:    */     }
/* 103:472 */     if (this.index == -1) {
/* 104:473 */       this.index = this.charArray.length;
/* 105:    */     }
/* 106:475 */     this.tokenList.add(Token.expression(startIndex, this.index));
/* 107:    */   }
/* 108:    */   
/* 109:    */   private void handleCommand()
/* 110:    */   {
/* 111:482 */     int startIndex = this.index;
/* 112:483 */     boolean noBody = false;
/* 113:484 */     this.index = CharScanner.findChars(TokenTypes.COMMAND_END_START.jstlStyle(), this.index, this.charArray);
/* 114:485 */     if (this.index == -1) {
/* 115:486 */       return;
/* 116:    */     }
/* 117:489 */     int foundIndex = CharScanner.findChars(TokenTypes.COMMAND_START_TAG_END.jstlStyle(), this.index - 1, this.charArray);
/* 118:490 */     if (foundIndex != -1) {
/* 119:491 */       noBody = true;
/* 120:    */     }
/* 121:494 */     if (noBody) {
/* 122:495 */       this.index -= 1;
/* 123:    */     }
/* 124:500 */     this.tokenList.add(Token.commandStart(startIndex, this.index));
/* 125:    */     
/* 126:    */ 
/* 127:503 */     this.index += TokenTypes.COMMAND_END_START.jstlStyle().length;
/* 128:506 */     if (noBody)
/* 129:    */     {
/* 130:508 */       this.tokenList.add(Token.commandBody(this.index, this.index));
/* 131:    */       
/* 132:510 */       return;
/* 133:    */     }
/* 134:514 */     Token commandBody = Token.commandBody(this.index, this.index);
/* 135:    */     
/* 136:    */ 
/* 137:517 */     this.tokenList.add(commandBody);
/* 138:    */     
/* 139:    */ 
/* 140:    */ 
/* 141:521 */     Token text = Token.text(this.index, -1);
/* 142:528 */     for (; this.index < this.charArray.length; this.index += 1)
/* 143:    */     {
/* 144:529 */       this.ch = this.charArray[this.index];
/* 145:531 */       if (this.ch == '<')
/* 146:    */       {
/* 147:533 */         if (CharScanner.matchChars(TokenTypes.COMMAND_START.jstlStyle(), this.index, this.charArray))
/* 148:    */         {
/* 149:536 */           text = textToken(text);
/* 150:    */           
/* 151:538 */           this.index += TokenTypes.COMMAND_START.jstlStyle().length;
/* 152:539 */           handleCommand();
/* 153:    */         }
/* 154:541 */         else if (CharScanner.matchChars(TokenTypes.COMMAND_START_END.jstlStyle(), this.index, this.charArray))
/* 155:    */         {
/* 156:545 */           text = textToken(text);
/* 157:    */           
/* 158:547 */           commandBody.stop(this.index);
/* 159:548 */           this.index += 1;
/* 160:549 */           this.index = CharScanner.findChar('>', this.index, this.charArray);
/* 161:550 */           break;
/* 162:    */         }
/* 163:    */       }
/* 164:554 */       else if (this.ch == '$')
/* 165:    */       {
/* 166:556 */         char ch1 = this.charArray[(this.index + 1)];
/* 167:557 */         if (ch1 == '{')
/* 168:    */         {
/* 169:558 */           if (CharScanner.matchChars(TokenTypes.EXPRESSION_START.jstlStyle(), this.index, this.charArray))
/* 170:    */           {
/* 171:561 */             text = textToken(text);
/* 172:    */             
/* 173:563 */             this.index += TokenTypes.EXPRESSION_START.jstlStyle().length;
/* 174:564 */             handleCurlyExpression();
/* 175:565 */             text = Token.text(this.index, -1);
/* 176:566 */             this.index -= 1;
/* 177:    */           }
/* 178:    */         }
/* 179:    */         else
/* 180:    */         {
/* 181:571 */           text = textToken(text);
/* 182:    */           
/* 183:573 */           this.index += 1;
/* 184:574 */           handleExpression("</");
/* 185:575 */           text = Token.text(this.index, -1);
/* 186:576 */           this.index -= 1;
/* 187:    */         }
/* 188:    */       }
/* 189:580 */       else if (text == null)
/* 190:    */       {
/* 191:582 */         text = Token.text(this.index, -1);
/* 192:    */       }
/* 193:    */     }
/* 194:589 */     if (commandBody.stop() == -1) {
/* 195:590 */       commandBody.stop(this.index);
/* 196:    */     }
/* 197:592 */     if (text != null)
/* 198:    */     {
/* 199:593 */       text.stop(this.charArray.length);
/* 200:594 */       this.tokenList.add(text);
/* 201:    */     }
/* 202:    */   }
/* 203:    */   
/* 204:    */   private Token textToken(Token text)
/* 205:    */   {
/* 206:602 */     if (text != null)
/* 207:    */     {
/* 208:603 */       text.stop(this.index);
/* 209:604 */       if (text.start() != text.stop()) {
/* 210:605 */         this.tokenList.add(text);
/* 211:    */       }
/* 212:607 */       text = null;
/* 213:    */     }
/* 214:609 */     return text;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public List<Token> getTokenList()
/* 218:    */   {
/* 219:614 */     return this.tokenList;
/* 220:    */   }
/* 221:    */   
/* 222:    */   public void displayTokens(String template)
/* 223:    */   {
/* 224:621 */     for (Token token : getTokenList()) {
/* 225:622 */       Boon.puts(new Object[] { "token", token, Str.slc(template, token.start(), token.stop()) });
/* 226:    */     }
/* 227:    */   }
/* 228:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.template.BoonCoreTemplateParser
 * JD-Core Version:    0.7.0.1
 */