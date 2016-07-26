/*   1:    */ package org.boon.messages;
/*   2:    */ 
/*   3:    */ import java.util.MissingResourceException;
/*   4:    */ import java.util.ResourceBundle;
/*   5:    */ 
/*   6:    */ public final class MessageUtils
/*   7:    */ {
/*   8:    */   public static final String TOOL_TIP = "toolTip";
/*   9:    */   public static final String LABEL_TOOL_TIP = "labelToolTip";
/*  10:    */   
/*  11:    */   public static String createLabelNoPlural(String fieldName, ResourceBundle bundle)
/*  12:    */   {
/*  13: 48 */     if (fieldName.endsWith("es")) {
/*  14: 49 */       fieldName = fieldName.substring(0, fieldName.length() - 2);
/*  15: 50 */     } else if (fieldName.endsWith("s")) {
/*  16: 51 */       fieldName = fieldName.substring(0, fieldName.length() - 1);
/*  17:    */     }
/*  18: 53 */     return getLabel(fieldName, bundle);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public static String getLabel(String fieldName, ResourceBundle bundle)
/*  22:    */   {
/*  23:    */     String label;
/*  24:    */     try
/*  25:    */     {
/*  26: 71 */       label = bundle.getString(fieldName);
/*  27:    */     }
/*  28:    */     catch (MissingResourceException mre)
/*  29:    */     {
/*  30: 73 */       label = generateLabelValue(fieldName);
/*  31:    */     }
/*  32: 76 */     return label;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public static String createLabelWithNameSpace(String namespace, String fieldName, ResourceBundle bundle)
/*  36:    */   {
/*  37:    */     String label;
/*  38:    */     try
/*  39:    */     {
/*  40:    */       try
/*  41:    */       {
/*  42: 94 */         label = bundle.getString(namespace + '.' + fieldName);
/*  43:    */       }
/*  44:    */       catch (MissingResourceException mre)
/*  45:    */       {
/*  46: 97 */         label = bundle.getString(fieldName);
/*  47:    */       }
/*  48:    */     }
/*  49:    */     catch (MissingResourceException mre)
/*  50:    */     {
/*  51:101 */       label = generateLabelValue(fieldName);
/*  52:    */     }
/*  53:104 */     return label;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public static String createToolTipWithNameSpace(String namespace, String fieldName, ResourceBundle bundle, String toolTipType)
/*  57:    */   {
/*  58:128 */     String toolTip = null;
/*  59:    */     try
/*  60:    */     {
/*  61:    */       try
/*  62:    */       {
/*  63:132 */         toolTip = bundle.getString(namespace + '.' + fieldName + '.' + toolTipType);
/*  64:    */       }
/*  65:    */       catch (MissingResourceException mre)
/*  66:    */       {
/*  67:136 */         toolTip = bundle.getString(fieldName + '.' + toolTipType);
/*  68:    */       }
/*  69:    */     }
/*  70:    */     catch (MissingResourceException mre) {}
/*  71:141 */     return toolTip;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static String generateLabelValue(String fieldName)
/*  75:    */   {
/*  76:154 */     final StringBuilder buffer = new StringBuilder(fieldName.length() * 2);
/*  77:    */     
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81:    */ 
/*  82:    */ 
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:    */ 
/*  87:    */ 
/*  88:    */ 
/*  89:    */ 
/*  90:    */ 
/*  91:    */ 
/*  92:    */ 
/*  93:    */ 
/*  94:    */ 
/*  95:    */ 
/*  96:    */ 
/*  97:    */ 
/*  98:    */ 
/*  99:    */ 
/* 100:    */ 
/* 101:    */ 
/* 102:    */ 
/* 103:    */ 
/* 104:    */ 
/* 105:    */ 
/* 106:    */ 
/* 107:    */ 
/* 108:    */ 
/* 109:    */ 
/* 110:    */ 
/* 111:    */ 
/* 112:    */ 
/* 113:    */ 
/* 114:    */ 
/* 115:    */ 
/* 116:    */ 
/* 117:    */ 
/* 118:    */ 
/* 119:    */ 
/* 120:    */ 
/* 121:    */ 
/* 122:    */ 
/* 123:    */ 
/* 124:    */ 
/* 125:    */ 
/* 126:    */ 
/* 127:    */ 
/* 128:    */ 
/* 129:    */ 
/* 130:    */ 
/* 131:    */ 
/* 132:    */ 
/* 133:    */ 
/* 134:    */ 
/* 135:    */ 
/* 136:    */ 
/* 137:    */ 
/* 138:    */ 
/* 139:    */ 
/* 140:    */ 
/* 141:    */ 
/* 142:    */ 
/* 143:    */ 
/* 144:    */ 
/* 145:    */ 
/* 146:    */ 
/* 147:    */ 
/* 148:    */ 
/* 149:    */ 
/* 150:    */ 
/* 151:    */ 
/* 152:    */ 
/* 153:    */ 
/* 154:    */ 
/* 155:    */ 
/* 156:    */ 
/* 157:    */ 
/* 158:    */ 
/* 159:    */ 
/* 160:    */ 
/* 161:    */ 
/* 162:    */ 
/* 163:    */ 
/* 164:    */ 
/* 165:    */ 
/* 166:    */ 
/* 167:    */ 
/* 168:    */ 
/* 169:    */ 
/* 170:    */ 
/* 171:    */ 
/* 172:    */ 
/* 173:    */ 
/* 174:    */ 
/* 175:    */ 
/* 176:    */ 
/* 177:    */ 
/* 178:    */ 
/* 179:    */ 
/* 180:    */ 
/* 181:    */ 
/* 182:    */ 
/* 183:    */ 
/* 184:    */ 
/* 185:    */ 
/* 186:    */ 
/* 187:    */ 
/* 188:    */ 
/* 189:    */ 
/* 190:    */ 
/* 191:    */ 
/* 192:    */ 
/* 193:    */ 
/* 194:    */ 
/* 195:    */ 
/* 196:    */ 
/* 197:    */ 
/* 198:    */ 
/* 199:    */ 
/* 200:278 */     Object gc = new Object()
/* 201:    */     {
/* 202:158 */       boolean capNextChar = false;
/* 203:159 */       boolean lastCharWasUpperCase = false;
/* 204:160 */       boolean lastCharWasNumber = false;
/* 205:161 */       boolean lastCharWasSpecial = false;
/* 206:162 */       boolean shouldContinue = true;
/* 207:163 */       char[] chars = this.val$fieldName.toCharArray();
/* 208:    */       
/* 209:    */       void processFieldName()
/* 210:    */       {
/* 211:167 */         for (int index = 0; index < this.chars.length; index++)
/* 212:    */         {
/* 213:168 */           char cchar = this.chars[index];
/* 214:169 */           this.shouldContinue = true;
/* 215:    */           
/* 216:171 */           processCharWasNumber(buffer, index, cchar);
/* 217:172 */           if (this.shouldContinue)
/* 218:    */           {
/* 219:176 */             processCharWasUpperCase(buffer, index, cchar);
/* 220:177 */             if (this.shouldContinue)
/* 221:    */             {
/* 222:181 */               processSpecialChars(buffer, cchar);
/* 223:183 */               if (this.shouldContinue)
/* 224:    */               {
/* 225:187 */                 cchar = processCapitalizeCommand(cchar);
/* 226:    */                 
/* 227:189 */                 cchar = processFirstCharacterCheck(buffer, index, cchar);
/* 228:191 */                 if (this.shouldContinue) {
/* 229:195 */                   buffer.append(cchar);
/* 230:    */                 }
/* 231:    */               }
/* 232:    */             }
/* 233:    */           }
/* 234:    */         }
/* 235:    */       }
/* 236:    */       
/* 237:    */       private void processCharWasNumber(StringBuilder buffer, int index, char cchar)
/* 238:    */       {
/* 239:202 */         if (this.lastCharWasSpecial) {
/* 240:203 */           return;
/* 241:    */         }
/* 242:206 */         if (Character.isDigit(cchar))
/* 243:    */         {
/* 244:208 */           if ((index != 0) && (!this.lastCharWasNumber)) {
/* 245:209 */             buffer.append(' ');
/* 246:    */           }
/* 247:212 */           this.lastCharWasNumber = true;
/* 248:213 */           buffer.append(cchar);
/* 249:    */           
/* 250:215 */           this.shouldContinue = false;
/* 251:    */         }
/* 252:    */         else
/* 253:    */         {
/* 254:217 */           this.lastCharWasNumber = false;
/* 255:    */         }
/* 256:    */       }
/* 257:    */       
/* 258:    */       private char processFirstCharacterCheck(StringBuilder buffer, int index, char cchar)
/* 259:    */       {
/* 260:224 */         if (index == 0)
/* 261:    */         {
/* 262:225 */           cchar = Character.toUpperCase(cchar);
/* 263:226 */           buffer.append(cchar);
/* 264:227 */           this.shouldContinue = false;
/* 265:    */         }
/* 266:229 */         return cchar;
/* 267:    */       }
/* 268:    */       
/* 269:    */       private char processCapitalizeCommand(char cchar)
/* 270:    */       {
/* 271:234 */         if (this.capNextChar)
/* 272:    */         {
/* 273:235 */           this.capNextChar = false;
/* 274:236 */           cchar = Character.toUpperCase(cchar);
/* 275:    */         }
/* 276:238 */         return cchar;
/* 277:    */       }
/* 278:    */       
/* 279:    */       private void processSpecialChars(StringBuilder buffer, char cchar)
/* 280:    */       {
/* 281:243 */         this.lastCharWasSpecial = false;
/* 282:247 */         if ((cchar == '.') || (cchar == '_'))
/* 283:    */         {
/* 284:248 */           buffer.append(' ');
/* 285:249 */           this.capNextChar = true;
/* 286:250 */           this.lastCharWasSpecial = false;
/* 287:251 */           this.shouldContinue = false;
/* 288:    */         }
/* 289:    */       }
/* 290:    */       
/* 291:    */       private void processCharWasUpperCase(StringBuilder buffer, int index, char cchar)
/* 292:    */       {
/* 293:262 */         if (Character.isUpperCase(cchar))
/* 294:    */         {
/* 295:264 */           if ((index != 0) && (!this.lastCharWasUpperCase)) {
/* 296:265 */             buffer.append(' ');
/* 297:    */           }
/* 298:268 */           this.lastCharWasUpperCase = true;
/* 299:269 */           buffer.append(cchar);
/* 300:    */           
/* 301:271 */           this.shouldContinue = false;
/* 302:    */         }
/* 303:    */         else
/* 304:    */         {
/* 305:273 */           this.lastCharWasUpperCase = false;
/* 306:    */         }
/* 307:    */       }
/* 308:278 */     };
/* 309:279 */     gc.processFieldName();
/* 310:    */     
/* 311:    */ 
/* 312:282 */     return buffer.toString().replace("  ", " ");
/* 313:    */   }
/* 314:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.messages.MessageUtils
 * JD-Core Version:    0.7.0.1
 */