/*   1:    */ package org.apache.commons.codec.language;
/*   2:    */ 
/*   3:    */ import org.apache.commons.codec.EncoderException;
/*   4:    */ import org.apache.commons.codec.StringEncoder;
/*   5:    */ 
/*   6:    */ final class SoundexUtils
/*   7:    */ {
/*   8:    */   static String clean(String str)
/*   9:    */   {
/*  10: 40 */     if ((str == null) || (str.length() == 0)) {
/*  11: 41 */       return str;
/*  12:    */     }
/*  13: 43 */     int len = str.length();
/*  14: 44 */     char[] chars = new char[len];
/*  15: 45 */     int count = 0;
/*  16: 46 */     for (int i = 0; i < len; i++) {
/*  17: 47 */       if (Character.isLetter(str.charAt(i))) {
/*  18: 48 */         chars[(count++)] = str.charAt(i);
/*  19:    */       }
/*  20:    */     }
/*  21: 51 */     if (count == len) {
/*  22: 52 */       return str.toUpperCase();
/*  23:    */     }
/*  24: 54 */     return new String(chars, 0, count).toUpperCase();
/*  25:    */   }
/*  26:    */   
/*  27:    */   static int difference(StringEncoder encoder, String s1, String s2)
/*  28:    */     throws EncoderException
/*  29:    */   {
/*  30: 84 */     return differenceEncoded(encoder.encode(s1), encoder.encode(s2));
/*  31:    */   }
/*  32:    */   
/*  33:    */   static int differenceEncoded(String es1, String es2)
/*  34:    */   {
/*  35:109 */     if ((es1 == null) || (es2 == null)) {
/*  36:110 */       return 0;
/*  37:    */     }
/*  38:112 */     int lengthToMatch = Math.min(es1.length(), es2.length());
/*  39:113 */     int diff = 0;
/*  40:114 */     for (int i = 0; i < lengthToMatch; i++) {
/*  41:115 */       if (es1.charAt(i) == es2.charAt(i)) {
/*  42:116 */         diff++;
/*  43:    */       }
/*  44:    */     }
/*  45:119 */     return diff;
/*  46:    */   }
/*  47:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.language.SoundexUtils
 * JD-Core Version:    0.7.0.1
 */