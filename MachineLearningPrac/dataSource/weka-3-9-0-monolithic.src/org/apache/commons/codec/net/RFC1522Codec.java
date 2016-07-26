/*   1:    */ package org.apache.commons.codec.net;
/*   2:    */ 
/*   3:    */ import java.io.UnsupportedEncodingException;
/*   4:    */ import org.apache.commons.codec.DecoderException;
/*   5:    */ import org.apache.commons.codec.EncoderException;
/*   6:    */ 
/*   7:    */ abstract class RFC1522Codec
/*   8:    */ {
/*   9:    */   protected String encodeText(String text, String charset)
/*  10:    */     throws EncoderException, UnsupportedEncodingException
/*  11:    */   {
/*  12: 68 */     if (text == null) {
/*  13: 69 */       return null;
/*  14:    */     }
/*  15: 71 */     StringBuffer buffer = new StringBuffer();
/*  16: 72 */     buffer.append("=?");
/*  17: 73 */     buffer.append(charset);
/*  18: 74 */     buffer.append('?');
/*  19: 75 */     buffer.append(getEncoding());
/*  20: 76 */     buffer.append('?');
/*  21: 77 */     byte[] rawdata = doEncoding(text.getBytes(charset));
/*  22: 78 */     buffer.append(new String(rawdata, "US-ASCII"));
/*  23: 79 */     buffer.append("?=");
/*  24: 80 */     return buffer.toString();
/*  25:    */   }
/*  26:    */   
/*  27:    */   protected String decodeText(String text)
/*  28:    */     throws DecoderException, UnsupportedEncodingException
/*  29:    */   {
/*  30: 98 */     if (text == null) {
/*  31: 99 */       return null;
/*  32:    */     }
/*  33:101 */     if ((!text.startsWith("=?")) || (!text.endsWith("?="))) {
/*  34:102 */       throw new DecoderException("RFC 1522 violation: malformed encoded content");
/*  35:    */     }
/*  36:104 */     int termnator = text.length() - 2;
/*  37:105 */     int from = 2;
/*  38:106 */     int to = text.indexOf("?", from);
/*  39:107 */     if ((to == -1) || (to == termnator)) {
/*  40:108 */       throw new DecoderException("RFC 1522 violation: charset token not found");
/*  41:    */     }
/*  42:110 */     String charset = text.substring(from, to);
/*  43:111 */     if (charset.equals("")) {
/*  44:112 */       throw new DecoderException("RFC 1522 violation: charset not specified");
/*  45:    */     }
/*  46:114 */     from = to + 1;
/*  47:115 */     to = text.indexOf("?", from);
/*  48:116 */     if ((to == -1) || (to == termnator)) {
/*  49:117 */       throw new DecoderException("RFC 1522 violation: encoding token not found");
/*  50:    */     }
/*  51:119 */     String encoding = text.substring(from, to);
/*  52:120 */     if (!getEncoding().equalsIgnoreCase(encoding)) {
/*  53:121 */       throw new DecoderException("This codec cannot decode " + encoding + " encoded content");
/*  54:    */     }
/*  55:124 */     from = to + 1;
/*  56:125 */     to = text.indexOf("?", from);
/*  57:126 */     byte[] data = text.substring(from, to).getBytes("US-ASCII");
/*  58:127 */     data = doDecoding(data);
/*  59:128 */     return new String(data, charset);
/*  60:    */   }
/*  61:    */   
/*  62:    */   protected abstract String getEncoding();
/*  63:    */   
/*  64:    */   protected abstract byte[] doEncoding(byte[] paramArrayOfByte)
/*  65:    */     throws EncoderException;
/*  66:    */   
/*  67:    */   protected abstract byte[] doDecoding(byte[] paramArrayOfByte)
/*  68:    */     throws DecoderException;
/*  69:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.net.RFC1522Codec
 * JD-Core Version:    0.7.0.1
 */