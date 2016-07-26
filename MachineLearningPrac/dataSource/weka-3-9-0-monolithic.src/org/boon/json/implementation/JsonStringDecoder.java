/*  1:   */ package org.boon.json.implementation;
/*  2:   */ 
/*  3:   */ import org.boon.primitive.CharBuf;
/*  4:   */ import org.boon.primitive.Chr;
/*  5:   */ 
/*  6:   */ public class JsonStringDecoder
/*  7:   */ {
/*  8:   */   public static String decode(char[] chars, int start, int to)
/*  9:   */   {
/* 10:43 */     if (!Chr.contains(chars, '\\', start, to - start)) {
/* 11:44 */       return new String(chars, start, to - start);
/* 12:   */     }
/* 13:46 */     return decodeForSure(chars, start, to);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public static String decodeForSure(char[] chars, int start, int to)
/* 17:   */   {
/* 18:53 */     CharBuf builder = CharBuf.create(to - start);
/* 19:54 */     builder.decodeJsonString(chars, start, to);
/* 20:55 */     return builder.toString();
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static String decodeForSure(CharBuf charBuf, char[] chars, int start, int to)
/* 24:   */   {
/* 25:62 */     charBuf.recycle();
/* 26:   */     
/* 27:64 */     charBuf.decodeJsonString(chars, start, to);
/* 28:65 */     return charBuf.toString();
/* 29:   */   }
/* 30:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.implementation.JsonStringDecoder
 * JD-Core Version:    0.7.0.1
 */