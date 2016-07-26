/*  1:   */ package org.boon.json.implementation;
/*  2:   */ 
/*  3:   */ import java.nio.charset.StandardCharsets;
/*  4:   */ import org.boon.primitive.ByteScanner;
/*  5:   */ import org.boon.primitive.CharBuf;
/*  6:   */ 
/*  7:   */ public class JsonAsciiParser
/*  8:   */   extends JsonBaseByteArrayParser
/*  9:   */ {
/* 10:   */   public JsonAsciiParser()
/* 11:   */   {
/* 12:42 */     this.charset = StandardCharsets.US_ASCII;
/* 13:   */   }
/* 14:   */   
/* 15:   */   protected final String decodeString()
/* 16:   */   {
/* 17:47 */     byte[] array = this.charArray;
/* 18:48 */     int index = this.__index;
/* 19:49 */     int currentChar = array[index];
/* 20:51 */     if ((index < array.length) && (currentChar == 34)) {
/* 21:52 */       index++;
/* 22:   */     }
/* 23:55 */     int startIndex = index;
/* 24:   */     
/* 25:   */ 
/* 26:58 */     boolean encoded = ByteScanner.hasEscapeChar(array, index, this.indexHolder);
/* 27:59 */     index = this.indexHolder[0];
/* 28:   */     
/* 29:   */ 
/* 30:   */ 
/* 31:63 */     String value = null;
/* 32:64 */     if (encoded)
/* 33:   */     {
/* 34:65 */       index = ByteScanner.findEndQuote(array, index);
/* 35:66 */       value = this.builder.decodeJsonString(array, startIndex, index).toString();
/* 36:67 */       this.builder.recycle();
/* 37:   */     }
/* 38:   */     else
/* 39:   */     {
/* 40:69 */       value = new String(array, startIndex, index - startIndex);
/* 41:   */     }
/* 42:72 */     if (index < this.charArray.length) {
/* 43:73 */       index++;
/* 44:   */     }
/* 45:75 */     this.__index = index;
/* 46:76 */     return value;
/* 47:   */   }
/* 48:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.implementation.JsonAsciiParser
 * JD-Core Version:    0.7.0.1
 */