/*  1:   */ package org.boon.json.implementation;
/*  2:   */ 
/*  3:   */ import org.boon.primitive.ByteScanner;
/*  4:   */ import org.boon.primitive.CharBuf;
/*  5:   */ 
/*  6:   */ public class JsonUTF8Parser
/*  7:   */   extends JsonBaseByteArrayParser
/*  8:   */ {
/*  9:   */   protected final String decodeString()
/* 10:   */   {
/* 11:44 */     byte[] array = this.charArray;
/* 12:45 */     int index = this.__index;
/* 13:46 */     int currentChar = array[index];
/* 14:48 */     if ((index < array.length) && (currentChar == 34)) {
/* 15:49 */       index++;
/* 16:   */     }
/* 17:52 */     int startIndex = index;
/* 18:   */     
/* 19:   */ 
/* 20:55 */     boolean encoded = ByteScanner.hasEscapeCharUTF8(array, index, this.indexHolder);
/* 21:56 */     index = this.indexHolder[0];
/* 22:   */     
/* 23:   */ 
/* 24:   */ 
/* 25:60 */     String value = null;
/* 26:61 */     if (encoded)
/* 27:   */     {
/* 28:62 */       index = ByteScanner.findEndQuoteUTF8(array, index);
/* 29:63 */       value = this.builder.decodeJsonString(array, startIndex, index).toString();
/* 30:64 */       this.builder.recycle();
/* 31:   */     }
/* 32:   */     else
/* 33:   */     {
/* 34:66 */       value = new String(array, startIndex, index - startIndex);
/* 35:   */     }
/* 36:69 */     if (index < this.charArray.length) {
/* 37:70 */       index++;
/* 38:   */     }
/* 39:72 */     this.__index = index;
/* 40:73 */     return value;
/* 41:   */   }
/* 42:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.implementation.JsonUTF8Parser
 * JD-Core Version:    0.7.0.1
 */