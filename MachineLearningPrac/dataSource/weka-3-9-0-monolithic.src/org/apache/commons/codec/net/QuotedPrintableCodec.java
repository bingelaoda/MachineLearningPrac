/*   1:    */ package org.apache.commons.codec.net;
/*   2:    */ 
/*   3:    */ import java.io.ByteArrayOutputStream;
/*   4:    */ import java.io.UnsupportedEncodingException;
/*   5:    */ import java.util.BitSet;
/*   6:    */ import org.apache.commons.codec.BinaryDecoder;
/*   7:    */ import org.apache.commons.codec.BinaryEncoder;
/*   8:    */ import org.apache.commons.codec.DecoderException;
/*   9:    */ import org.apache.commons.codec.EncoderException;
/*  10:    */ import org.apache.commons.codec.StringDecoder;
/*  11:    */ import org.apache.commons.codec.StringEncoder;
/*  12:    */ 
/*  13:    */ public class QuotedPrintableCodec
/*  14:    */   implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder
/*  15:    */ {
/*  16: 63 */   private String charset = "UTF-8";
/*  17: 68 */   private static final BitSet PRINTABLE_CHARS = new BitSet(256);
/*  18: 70 */   private static byte ESCAPE_CHAR = 61;
/*  19: 72 */   private static byte TAB = 9;
/*  20: 74 */   private static byte SPACE = 32;
/*  21:    */   
/*  22:    */   static
/*  23:    */   {
/*  24: 78 */     for (int i = 33; i <= 60; i++) {
/*  25: 79 */       PRINTABLE_CHARS.set(i);
/*  26:    */     }
/*  27: 81 */     for (int i = 62; i <= 126; i++) {
/*  28: 82 */       PRINTABLE_CHARS.set(i);
/*  29:    */     }
/*  30: 84 */     PRINTABLE_CHARS.set(TAB);
/*  31: 85 */     PRINTABLE_CHARS.set(SPACE);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public QuotedPrintableCodec(String charset)
/*  35:    */   {
/*  36:103 */     this.charset = charset;
/*  37:    */   }
/*  38:    */   
/*  39:    */   private static final void encodeQuotedPrintable(int b, ByteArrayOutputStream buffer)
/*  40:    */   {
/*  41:115 */     buffer.write(ESCAPE_CHAR);
/*  42:116 */     char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
/*  43:117 */     char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
/*  44:118 */     buffer.write(hex1);
/*  45:119 */     buffer.write(hex2);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static final byte[] encodeQuotedPrintable(BitSet printable, byte[] bytes)
/*  49:    */   {
/*  50:137 */     if (bytes == null) {
/*  51:138 */       return null;
/*  52:    */     }
/*  53:140 */     if (printable == null) {
/*  54:141 */       printable = PRINTABLE_CHARS;
/*  55:    */     }
/*  56:143 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/*  57:144 */     for (int i = 0; i < bytes.length; i++)
/*  58:    */     {
/*  59:145 */       int b = bytes[i];
/*  60:146 */       if (b < 0) {
/*  61:147 */         b = 256 + b;
/*  62:    */       }
/*  63:149 */       if (printable.get(b)) {
/*  64:150 */         buffer.write(b);
/*  65:    */       } else {
/*  66:152 */         encodeQuotedPrintable(b, buffer);
/*  67:    */       }
/*  68:    */     }
/*  69:155 */     return buffer.toByteArray();
/*  70:    */   }
/*  71:    */   
/*  72:    */   public static final byte[] decodeQuotedPrintable(byte[] bytes)
/*  73:    */     throws DecoderException
/*  74:    */   {
/*  75:174 */     if (bytes == null) {
/*  76:175 */       return null;
/*  77:    */     }
/*  78:177 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/*  79:178 */     for (int i = 0; i < bytes.length; i++)
/*  80:    */     {
/*  81:179 */       int b = bytes[i];
/*  82:180 */       if (b == ESCAPE_CHAR) {
/*  83:    */         try
/*  84:    */         {
/*  85:182 */           int u = Character.digit((char)bytes[(++i)], 16);
/*  86:183 */           int l = Character.digit((char)bytes[(++i)], 16);
/*  87:184 */           if ((u == -1) || (l == -1)) {
/*  88:185 */             throw new DecoderException("Invalid quoted-printable encoding");
/*  89:    */           }
/*  90:187 */           buffer.write((char)((u << 4) + l));
/*  91:    */         }
/*  92:    */         catch (ArrayIndexOutOfBoundsException e)
/*  93:    */         {
/*  94:189 */           throw new DecoderException("Invalid quoted-printable encoding");
/*  95:    */         }
/*  96:    */       } else {
/*  97:192 */         buffer.write(b);
/*  98:    */       }
/*  99:    */     }
/* 100:195 */     return buffer.toByteArray();
/* 101:    */   }
/* 102:    */   
/* 103:    */   public byte[] encode(byte[] bytes)
/* 104:    */   {
/* 105:211 */     return encodeQuotedPrintable(PRINTABLE_CHARS, bytes);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public byte[] decode(byte[] bytes)
/* 109:    */     throws DecoderException
/* 110:    */   {
/* 111:230 */     return decodeQuotedPrintable(bytes);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public String encode(String pString)
/* 115:    */     throws EncoderException
/* 116:    */   {
/* 117:251 */     if (pString == null) {
/* 118:252 */       return null;
/* 119:    */     }
/* 120:    */     try
/* 121:    */     {
/* 122:255 */       return encode(pString, getDefaultCharset());
/* 123:    */     }
/* 124:    */     catch (UnsupportedEncodingException e)
/* 125:    */     {
/* 126:257 */       throw new EncoderException(e.getMessage());
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:    */   public String decode(String pString, String charset)
/* 131:    */     throws DecoderException, UnsupportedEncodingException
/* 132:    */   {
/* 133:276 */     if (pString == null) {
/* 134:277 */       return null;
/* 135:    */     }
/* 136:279 */     return new String(decode(pString.getBytes("US-ASCII")), charset);
/* 137:    */   }
/* 138:    */   
/* 139:    */   public String decode(String pString)
/* 140:    */     throws DecoderException
/* 141:    */   {
/* 142:296 */     if (pString == null) {
/* 143:297 */       return null;
/* 144:    */     }
/* 145:    */     try
/* 146:    */     {
/* 147:300 */       return decode(pString, getDefaultCharset());
/* 148:    */     }
/* 149:    */     catch (UnsupportedEncodingException e)
/* 150:    */     {
/* 151:302 */       throw new DecoderException(e.getMessage());
/* 152:    */     }
/* 153:    */   }
/* 154:    */   
/* 155:    */   public Object encode(Object pObject)
/* 156:    */     throws EncoderException
/* 157:    */   {
/* 158:317 */     if (pObject == null) {
/* 159:318 */       return null;
/* 160:    */     }
/* 161:319 */     if ((pObject instanceof byte[])) {
/* 162:320 */       return encode((byte[])pObject);
/* 163:    */     }
/* 164:321 */     if ((pObject instanceof String)) {
/* 165:322 */       return encode((String)pObject);
/* 166:    */     }
/* 167:324 */     throw new EncoderException("Objects of type " + pObject.getClass().getName() + " cannot be quoted-printable encoded");
/* 168:    */   }
/* 169:    */   
/* 170:    */   public Object decode(Object pObject)
/* 171:    */     throws DecoderException
/* 172:    */   {
/* 173:342 */     if (pObject == null) {
/* 174:343 */       return null;
/* 175:    */     }
/* 176:344 */     if ((pObject instanceof byte[])) {
/* 177:345 */       return decode((byte[])pObject);
/* 178:    */     }
/* 179:346 */     if ((pObject instanceof String)) {
/* 180:347 */       return decode((String)pObject);
/* 181:    */     }
/* 182:349 */     throw new DecoderException("Objects of type " + pObject.getClass().getName() + " cannot be quoted-printable decoded");
/* 183:    */   }
/* 184:    */   
/* 185:    */   public String getDefaultCharset()
/* 186:    */   {
/* 187:361 */     return this.charset;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String encode(String pString, String charset)
/* 191:    */     throws UnsupportedEncodingException
/* 192:    */   {
/* 193:382 */     if (pString == null) {
/* 194:383 */       return null;
/* 195:    */     }
/* 196:385 */     return new String(encode(pString.getBytes(charset)), "US-ASCII");
/* 197:    */   }
/* 198:    */   
/* 199:    */   public QuotedPrintableCodec() {}
/* 200:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.net.QuotedPrintableCodec
 * JD-Core Version:    0.7.0.1
 */