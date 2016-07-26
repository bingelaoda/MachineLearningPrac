/*   1:    */ package org.apache.commons.codec.net;
/*   2:    */ 
/*   3:    */ import java.io.UnsupportedEncodingException;
/*   4:    */ import org.apache.commons.codec.DecoderException;
/*   5:    */ import org.apache.commons.codec.EncoderException;
/*   6:    */ import org.apache.commons.codec.StringDecoder;
/*   7:    */ import org.apache.commons.codec.StringEncoder;
/*   8:    */ import org.apache.commons.codec.binary.Base64;
/*   9:    */ 
/*  10:    */ public class BCodec
/*  11:    */   extends RFC1522Codec
/*  12:    */   implements StringEncoder, StringDecoder
/*  13:    */ {
/*  14: 49 */   private String charset = "UTF-8";
/*  15:    */   
/*  16:    */   public BCodec() {}
/*  17:    */   
/*  18:    */   public BCodec(String charset)
/*  19:    */   {
/*  20: 69 */     this.charset = charset;
/*  21:    */   }
/*  22:    */   
/*  23:    */   protected String getEncoding()
/*  24:    */   {
/*  25: 73 */     return "B";
/*  26:    */   }
/*  27:    */   
/*  28:    */   protected byte[] doEncoding(byte[] bytes)
/*  29:    */     throws EncoderException
/*  30:    */   {
/*  31: 77 */     if (bytes == null) {
/*  32: 78 */       return null;
/*  33:    */     }
/*  34: 80 */     return Base64.encodeBase64(bytes);
/*  35:    */   }
/*  36:    */   
/*  37:    */   protected byte[] doDecoding(byte[] bytes)
/*  38:    */     throws DecoderException
/*  39:    */   {
/*  40: 84 */     if (bytes == null) {
/*  41: 85 */       return null;
/*  42:    */     }
/*  43: 87 */     return Base64.decodeBase64(bytes);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String encode(String value, String charset)
/*  47:    */     throws EncoderException
/*  48:    */   {
/*  49:103 */     if (value == null) {
/*  50:104 */       return null;
/*  51:    */     }
/*  52:    */     try
/*  53:    */     {
/*  54:107 */       return encodeText(value, charset);
/*  55:    */     }
/*  56:    */     catch (UnsupportedEncodingException e)
/*  57:    */     {
/*  58:109 */       throw new EncoderException(e.getMessage());
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String encode(String value)
/*  63:    */     throws EncoderException
/*  64:    */   {
/*  65:124 */     if (value == null) {
/*  66:125 */       return null;
/*  67:    */     }
/*  68:127 */     return encode(value, getDefaultCharset());
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String decode(String value)
/*  72:    */     throws DecoderException
/*  73:    */   {
/*  74:143 */     if (value == null) {
/*  75:144 */       return null;
/*  76:    */     }
/*  77:    */     try
/*  78:    */     {
/*  79:147 */       return decodeText(value);
/*  80:    */     }
/*  81:    */     catch (UnsupportedEncodingException e)
/*  82:    */     {
/*  83:149 */       throw new DecoderException(e.getMessage());
/*  84:    */     }
/*  85:    */   }
/*  86:    */   
/*  87:    */   public Object encode(Object value)
/*  88:    */     throws EncoderException
/*  89:    */   {
/*  90:164 */     if (value == null) {
/*  91:165 */       return null;
/*  92:    */     }
/*  93:166 */     if ((value instanceof String)) {
/*  94:167 */       return encode((String)value);
/*  95:    */     }
/*  96:169 */     throw new EncoderException("Objects of type " + value.getClass().getName() + " cannot be encoded using BCodec");
/*  97:    */   }
/*  98:    */   
/*  99:    */   public Object decode(Object value)
/* 100:    */     throws DecoderException
/* 101:    */   {
/* 102:188 */     if (value == null) {
/* 103:189 */       return null;
/* 104:    */     }
/* 105:190 */     if ((value instanceof String)) {
/* 106:191 */       return decode((String)value);
/* 107:    */     }
/* 108:193 */     throw new DecoderException("Objects of type " + value.getClass().getName() + " cannot be decoded using BCodec");
/* 109:    */   }
/* 110:    */   
/* 111:    */   public String getDefaultCharset()
/* 112:    */   {
/* 113:205 */     return this.charset;
/* 114:    */   }
/* 115:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.net.BCodec
 * JD-Core Version:    0.7.0.1
 */