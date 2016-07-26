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
/*  13:    */ public class URLCodec
/*  14:    */   implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder
/*  15:    */ {
/*  16: 55 */   protected String charset = "UTF-8";
/*  17: 57 */   protected static byte ESCAPE_CHAR = 37;
/*  18: 61 */   protected static final BitSet WWW_FORM_URL = new BitSet(256);
/*  19:    */   
/*  20:    */   static
/*  21:    */   {
/*  22: 66 */     for (int i = 97; i <= 122; i++) {
/*  23: 67 */       WWW_FORM_URL.set(i);
/*  24:    */     }
/*  25: 69 */     for (int i = 65; i <= 90; i++) {
/*  26: 70 */       WWW_FORM_URL.set(i);
/*  27:    */     }
/*  28: 73 */     for (int i = 48; i <= 57; i++) {
/*  29: 74 */       WWW_FORM_URL.set(i);
/*  30:    */     }
/*  31: 77 */     WWW_FORM_URL.set(45);
/*  32: 78 */     WWW_FORM_URL.set(95);
/*  33: 79 */     WWW_FORM_URL.set(46);
/*  34: 80 */     WWW_FORM_URL.set(42);
/*  35:    */     
/*  36: 82 */     WWW_FORM_URL.set(32);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public URLCodec(String charset)
/*  40:    */   {
/*  41:100 */     this.charset = charset;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public static final byte[] encodeUrl(BitSet urlsafe, byte[] bytes)
/*  45:    */   {
/*  46:113 */     if (bytes == null) {
/*  47:114 */       return null;
/*  48:    */     }
/*  49:116 */     if (urlsafe == null) {
/*  50:117 */       urlsafe = WWW_FORM_URL;
/*  51:    */     }
/*  52:120 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/*  53:121 */     for (int i = 0; i < bytes.length; i++)
/*  54:    */     {
/*  55:122 */       int b = bytes[i];
/*  56:123 */       if (b < 0) {
/*  57:124 */         b = 256 + b;
/*  58:    */       }
/*  59:126 */       if (urlsafe.get(b))
/*  60:    */       {
/*  61:127 */         if (b == 32) {
/*  62:128 */           b = 43;
/*  63:    */         }
/*  64:130 */         buffer.write(b);
/*  65:    */       }
/*  66:    */       else
/*  67:    */       {
/*  68:132 */         buffer.write(37);
/*  69:133 */         char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
/*  70:    */         
/*  71:135 */         char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
/*  72:    */         
/*  73:137 */         buffer.write(hex1);
/*  74:138 */         buffer.write(hex2);
/*  75:    */       }
/*  76:    */     }
/*  77:141 */     return buffer.toByteArray();
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static final byte[] decodeUrl(byte[] bytes)
/*  81:    */     throws DecoderException
/*  82:    */   {
/*  83:157 */     if (bytes == null) {
/*  84:158 */       return null;
/*  85:    */     }
/*  86:160 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/*  87:161 */     for (int i = 0; i < bytes.length; i++)
/*  88:    */     {
/*  89:162 */       int b = bytes[i];
/*  90:163 */       if (b == 43) {
/*  91:164 */         buffer.write(32);
/*  92:165 */       } else if (b == 37) {
/*  93:    */         try
/*  94:    */         {
/*  95:167 */           int u = Character.digit((char)bytes[(++i)], 16);
/*  96:168 */           int l = Character.digit((char)bytes[(++i)], 16);
/*  97:169 */           if ((u == -1) || (l == -1)) {
/*  98:170 */             throw new DecoderException("Invalid URL encoding");
/*  99:    */           }
/* 100:172 */           buffer.write((char)((u << 4) + l));
/* 101:    */         }
/* 102:    */         catch (ArrayIndexOutOfBoundsException e)
/* 103:    */         {
/* 104:174 */           throw new DecoderException("Invalid URL encoding");
/* 105:    */         }
/* 106:    */       } else {
/* 107:177 */         buffer.write(b);
/* 108:    */       }
/* 109:    */     }
/* 110:180 */     return buffer.toByteArray();
/* 111:    */   }
/* 112:    */   
/* 113:    */   public byte[] encode(byte[] bytes)
/* 114:    */   {
/* 115:192 */     return encodeUrl(WWW_FORM_URL, bytes);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public byte[] decode(byte[] bytes)
/* 119:    */     throws DecoderException
/* 120:    */   {
/* 121:206 */     return decodeUrl(bytes);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public String encode(String pString, String charset)
/* 125:    */     throws UnsupportedEncodingException
/* 126:    */   {
/* 127:223 */     if (pString == null) {
/* 128:224 */       return null;
/* 129:    */     }
/* 130:226 */     return new String(encode(pString.getBytes(charset)), "US-ASCII");
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String encode(String pString)
/* 134:    */     throws EncoderException
/* 135:    */   {
/* 136:241 */     if (pString == null) {
/* 137:242 */       return null;
/* 138:    */     }
/* 139:    */     try
/* 140:    */     {
/* 141:245 */       return encode(pString, getDefaultCharset());
/* 142:    */     }
/* 143:    */     catch (UnsupportedEncodingException e)
/* 144:    */     {
/* 145:247 */       throw new EncoderException(e.getMessage());
/* 146:    */     }
/* 147:    */   }
/* 148:    */   
/* 149:    */   public String decode(String pString, String charset)
/* 150:    */     throws DecoderException, UnsupportedEncodingException
/* 151:    */   {
/* 152:267 */     if (pString == null) {
/* 153:268 */       return null;
/* 154:    */     }
/* 155:270 */     return new String(decode(pString.getBytes("US-ASCII")), charset);
/* 156:    */   }
/* 157:    */   
/* 158:    */   public String decode(String pString)
/* 159:    */     throws DecoderException
/* 160:    */   {
/* 161:286 */     if (pString == null) {
/* 162:287 */       return null;
/* 163:    */     }
/* 164:    */     try
/* 165:    */     {
/* 166:290 */       return decode(pString, getDefaultCharset());
/* 167:    */     }
/* 168:    */     catch (UnsupportedEncodingException e)
/* 169:    */     {
/* 170:292 */       throw new DecoderException(e.getMessage());
/* 171:    */     }
/* 172:    */   }
/* 173:    */   
/* 174:    */   public Object encode(Object pObject)
/* 175:    */     throws EncoderException
/* 176:    */   {
/* 177:307 */     if (pObject == null) {
/* 178:308 */       return null;
/* 179:    */     }
/* 180:309 */     if ((pObject instanceof byte[])) {
/* 181:310 */       return encode((byte[])pObject);
/* 182:    */     }
/* 183:311 */     if ((pObject instanceof String)) {
/* 184:312 */       return encode((String)pObject);
/* 185:    */     }
/* 186:314 */     throw new EncoderException("Objects of type " + pObject.getClass().getName() + " cannot be URL encoded");
/* 187:    */   }
/* 188:    */   
/* 189:    */   public Object decode(Object pObject)
/* 190:    */     throws DecoderException
/* 191:    */   {
/* 192:331 */     if (pObject == null) {
/* 193:332 */       return null;
/* 194:    */     }
/* 195:333 */     if ((pObject instanceof byte[])) {
/* 196:334 */       return decode((byte[])pObject);
/* 197:    */     }
/* 198:335 */     if ((pObject instanceof String)) {
/* 199:336 */       return decode((String)pObject);
/* 200:    */     }
/* 201:338 */     throw new DecoderException("Objects of type " + pObject.getClass().getName() + " cannot be URL decoded");
/* 202:    */   }
/* 203:    */   
/* 204:    */   /**
/* 205:    */    * @deprecated
/* 206:    */    */
/* 207:    */   public String getEncoding()
/* 208:    */   {
/* 209:352 */     return this.charset;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public String getDefaultCharset()
/* 213:    */   {
/* 214:361 */     return this.charset;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public URLCodec() {}
/* 218:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.net.URLCodec
 * JD-Core Version:    0.7.0.1
 */