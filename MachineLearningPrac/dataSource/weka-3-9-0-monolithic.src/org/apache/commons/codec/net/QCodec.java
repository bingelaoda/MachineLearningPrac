/*   1:    */ package org.apache.commons.codec.net;
/*   2:    */ 
/*   3:    */ import java.io.UnsupportedEncodingException;
/*   4:    */ import java.util.BitSet;
/*   5:    */ import org.apache.commons.codec.DecoderException;
/*   6:    */ import org.apache.commons.codec.EncoderException;
/*   7:    */ import org.apache.commons.codec.StringDecoder;
/*   8:    */ import org.apache.commons.codec.StringEncoder;
/*   9:    */ 
/*  10:    */ public class QCodec
/*  11:    */   extends RFC1522Codec
/*  12:    */   implements StringEncoder, StringDecoder
/*  13:    */ {
/*  14: 51 */   private String charset = "UTF-8";
/*  15: 56 */   private static final BitSet PRINTABLE_CHARS = new BitSet(256);
/*  16:    */   
/*  17:    */   static
/*  18:    */   {
/*  19: 60 */     PRINTABLE_CHARS.set(32);
/*  20: 61 */     PRINTABLE_CHARS.set(33);
/*  21: 62 */     PRINTABLE_CHARS.set(34);
/*  22: 63 */     PRINTABLE_CHARS.set(35);
/*  23: 64 */     PRINTABLE_CHARS.set(36);
/*  24: 65 */     PRINTABLE_CHARS.set(37);
/*  25: 66 */     PRINTABLE_CHARS.set(38);
/*  26: 67 */     PRINTABLE_CHARS.set(39);
/*  27: 68 */     PRINTABLE_CHARS.set(40);
/*  28: 69 */     PRINTABLE_CHARS.set(41);
/*  29: 70 */     PRINTABLE_CHARS.set(42);
/*  30: 71 */     PRINTABLE_CHARS.set(43);
/*  31: 72 */     PRINTABLE_CHARS.set(44);
/*  32: 73 */     PRINTABLE_CHARS.set(45);
/*  33: 74 */     PRINTABLE_CHARS.set(46);
/*  34: 75 */     PRINTABLE_CHARS.set(47);
/*  35: 76 */     for (int i = 48; i <= 57; i++) {
/*  36: 77 */       PRINTABLE_CHARS.set(i);
/*  37:    */     }
/*  38: 79 */     PRINTABLE_CHARS.set(58);
/*  39: 80 */     PRINTABLE_CHARS.set(59);
/*  40: 81 */     PRINTABLE_CHARS.set(60);
/*  41: 82 */     PRINTABLE_CHARS.set(62);
/*  42: 83 */     PRINTABLE_CHARS.set(64);
/*  43: 84 */     for (int i = 65; i <= 90; i++) {
/*  44: 85 */       PRINTABLE_CHARS.set(i);
/*  45:    */     }
/*  46: 87 */     PRINTABLE_CHARS.set(91);
/*  47: 88 */     PRINTABLE_CHARS.set(92);
/*  48: 89 */     PRINTABLE_CHARS.set(93);
/*  49: 90 */     PRINTABLE_CHARS.set(94);
/*  50: 91 */     PRINTABLE_CHARS.set(96);
/*  51: 92 */     for (int i = 97; i <= 122; i++) {
/*  52: 93 */       PRINTABLE_CHARS.set(i);
/*  53:    */     }
/*  54: 95 */     PRINTABLE_CHARS.set(123);
/*  55: 96 */     PRINTABLE_CHARS.set(124);
/*  56: 97 */     PRINTABLE_CHARS.set(125);
/*  57: 98 */     PRINTABLE_CHARS.set(126);
/*  58:    */   }
/*  59:    */   
/*  60:101 */   private static byte BLANK = 32;
/*  61:103 */   private static byte UNDERSCORE = 95;
/*  62:105 */   private boolean encodeBlanks = false;
/*  63:    */   
/*  64:    */   public QCodec(String charset)
/*  65:    */   {
/*  66:125 */     this.charset = charset;
/*  67:    */   }
/*  68:    */   
/*  69:    */   protected String getEncoding()
/*  70:    */   {
/*  71:129 */     return "Q";
/*  72:    */   }
/*  73:    */   
/*  74:    */   protected byte[] doEncoding(byte[] bytes)
/*  75:    */     throws EncoderException
/*  76:    */   {
/*  77:133 */     if (bytes == null) {
/*  78:134 */       return null;
/*  79:    */     }
/*  80:136 */     byte[] data = QuotedPrintableCodec.encodeQuotedPrintable(PRINTABLE_CHARS, bytes);
/*  81:137 */     if (this.encodeBlanks) {
/*  82:138 */       for (int i = 0; i < data.length; i++) {
/*  83:139 */         if (data[i] == BLANK) {
/*  84:140 */           data[i] = UNDERSCORE;
/*  85:    */         }
/*  86:    */       }
/*  87:    */     }
/*  88:144 */     return data;
/*  89:    */   }
/*  90:    */   
/*  91:    */   protected byte[] doDecoding(byte[] bytes)
/*  92:    */     throws DecoderException
/*  93:    */   {
/*  94:148 */     if (bytes == null) {
/*  95:149 */       return null;
/*  96:    */     }
/*  97:151 */     boolean hasUnderscores = false;
/*  98:152 */     for (int i = 0; i < bytes.length; i++) {
/*  99:153 */       if (bytes[i] == UNDERSCORE)
/* 100:    */       {
/* 101:154 */         hasUnderscores = true;
/* 102:155 */         break;
/* 103:    */       }
/* 104:    */     }
/* 105:158 */     if (hasUnderscores)
/* 106:    */     {
/* 107:159 */       byte[] tmp = new byte[bytes.length];
/* 108:160 */       for (int i = 0; i < bytes.length; i++)
/* 109:    */       {
/* 110:161 */         byte b = bytes[i];
/* 111:162 */         if (b != UNDERSCORE) {
/* 112:163 */           tmp[i] = b;
/* 113:    */         } else {
/* 114:165 */           tmp[i] = BLANK;
/* 115:    */         }
/* 116:    */       }
/* 117:168 */       return QuotedPrintableCodec.decodeQuotedPrintable(tmp);
/* 118:    */     }
/* 119:170 */     return QuotedPrintableCodec.decodeQuotedPrintable(bytes);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String encode(String pString, String charset)
/* 123:    */     throws EncoderException
/* 124:    */   {
/* 125:186 */     if (pString == null) {
/* 126:187 */       return null;
/* 127:    */     }
/* 128:    */     try
/* 129:    */     {
/* 130:190 */       return encodeText(pString, charset);
/* 131:    */     }
/* 132:    */     catch (UnsupportedEncodingException e)
/* 133:    */     {
/* 134:192 */       throw new EncoderException(e.getMessage());
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String encode(String pString)
/* 139:    */     throws EncoderException
/* 140:    */   {
/* 141:207 */     if (pString == null) {
/* 142:208 */       return null;
/* 143:    */     }
/* 144:210 */     return encode(pString, getDefaultCharset());
/* 145:    */   }
/* 146:    */   
/* 147:    */   public String decode(String pString)
/* 148:    */     throws DecoderException
/* 149:    */   {
/* 150:226 */     if (pString == null) {
/* 151:227 */       return null;
/* 152:    */     }
/* 153:    */     try
/* 154:    */     {
/* 155:230 */       return decodeText(pString);
/* 156:    */     }
/* 157:    */     catch (UnsupportedEncodingException e)
/* 158:    */     {
/* 159:232 */       throw new DecoderException(e.getMessage());
/* 160:    */     }
/* 161:    */   }
/* 162:    */   
/* 163:    */   public Object encode(Object pObject)
/* 164:    */     throws EncoderException
/* 165:    */   {
/* 166:247 */     if (pObject == null) {
/* 167:248 */       return null;
/* 168:    */     }
/* 169:249 */     if ((pObject instanceof String)) {
/* 170:250 */       return encode((String)pObject);
/* 171:    */     }
/* 172:252 */     throw new EncoderException("Objects of type " + pObject.getClass().getName() + " cannot be encoded using Q codec");
/* 173:    */   }
/* 174:    */   
/* 175:    */   public Object decode(Object pObject)
/* 176:    */     throws DecoderException
/* 177:    */   {
/* 178:271 */     if (pObject == null) {
/* 179:272 */       return null;
/* 180:    */     }
/* 181:273 */     if ((pObject instanceof String)) {
/* 182:274 */       return decode((String)pObject);
/* 183:    */     }
/* 184:276 */     throw new DecoderException("Objects of type " + pObject.getClass().getName() + " cannot be decoded using Q codec");
/* 185:    */   }
/* 186:    */   
/* 187:    */   public String getDefaultCharset()
/* 188:    */   {
/* 189:288 */     return this.charset;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public boolean isEncodeBlanks()
/* 193:    */   {
/* 194:297 */     return this.encodeBlanks;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public void setEncodeBlanks(boolean b)
/* 198:    */   {
/* 199:307 */     this.encodeBlanks = b;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public QCodec() {}
/* 203:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.net.QCodec
 * JD-Core Version:    0.7.0.1
 */