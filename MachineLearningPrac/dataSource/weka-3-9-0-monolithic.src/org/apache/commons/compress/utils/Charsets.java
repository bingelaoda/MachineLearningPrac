/*   1:    */ package org.apache.commons.compress.utils;
/*   2:    */ 
/*   3:    */ import java.nio.charset.Charset;
/*   4:    */ 
/*   5:    */ public class Charsets
/*   6:    */ {
/*   7:    */   public static Charset toCharset(Charset charset)
/*   8:    */   {
/*   9: 72 */     return charset == null ? Charset.defaultCharset() : charset;
/*  10:    */   }
/*  11:    */   
/*  12:    */   public static Charset toCharset(String charset)
/*  13:    */   {
/*  14: 87 */     return charset == null ? Charset.defaultCharset() : Charset.forName(charset);
/*  15:    */   }
/*  16:    */   
/*  17: 98 */   public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
/*  18:110 */   public static final Charset US_ASCII = Charset.forName("US-ASCII");
/*  19:123 */   public static final Charset UTF_16 = Charset.forName("UTF-16");
/*  20:135 */   public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
/*  21:147 */   public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
/*  22:159 */   public static final Charset UTF_8 = Charset.forName("UTF-8");
/*  23:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.utils.Charsets
 * JD-Core Version:    0.7.0.1
 */