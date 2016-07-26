/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.nio.ByteBuffer;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.List;
/*   8:    */ 
/*   9:    */ class Simple8BitZipEncoding
/*  10:    */   implements ZipEncoding
/*  11:    */ {
/*  12:    */   private final char[] highChars;
/*  13:    */   private final List<Simple8BitChar> reverseMapping;
/*  14:    */   
/*  15:    */   private static final class Simple8BitChar
/*  16:    */     implements Comparable<Simple8BitChar>
/*  17:    */   {
/*  18:    */     public final char unicode;
/*  19:    */     public final byte code;
/*  20:    */     
/*  21:    */     Simple8BitChar(byte code, char unicode)
/*  22:    */     {
/*  23: 58 */       this.code = code;
/*  24: 59 */       this.unicode = unicode;
/*  25:    */     }
/*  26:    */     
/*  27:    */     public int compareTo(Simple8BitChar a)
/*  28:    */     {
/*  29: 63 */       return this.unicode - a.unicode;
/*  30:    */     }
/*  31:    */     
/*  32:    */     public String toString()
/*  33:    */     {
/*  34: 68 */       return "0x" + Integer.toHexString(0xFFFF & this.unicode) + "->0x" + Integer.toHexString(0xFF & this.code);
/*  35:    */     }
/*  36:    */     
/*  37:    */     public boolean equals(Object o)
/*  38:    */     {
/*  39: 74 */       if ((o instanceof Simple8BitChar))
/*  40:    */       {
/*  41: 75 */         Simple8BitChar other = (Simple8BitChar)o;
/*  42: 76 */         return (this.unicode == other.unicode) && (this.code == other.code);
/*  43:    */       }
/*  44: 78 */       return false;
/*  45:    */     }
/*  46:    */     
/*  47:    */     public int hashCode()
/*  48:    */     {
/*  49: 83 */       return this.unicode;
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   public Simple8BitZipEncoding(char[] highChars)
/*  54:    */   {
/*  55:105 */     this.highChars = ((char[])highChars.clone());
/*  56:106 */     List<Simple8BitChar> temp = new ArrayList(this.highChars.length);
/*  57:    */     
/*  58:    */ 
/*  59:109 */     byte code = 127;
/*  60:111 */     for (char highChar : this.highChars)
/*  61:    */     {
/*  62:112 */       code = (byte)(code + 1);temp.add(new Simple8BitChar(code, highChar));
/*  63:    */     }
/*  64:115 */     Collections.sort(temp);
/*  65:116 */     this.reverseMapping = Collections.unmodifiableList(temp);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public char decodeByte(byte b)
/*  69:    */   {
/*  70:127 */     if (b >= 0) {
/*  71:128 */       return (char)b;
/*  72:    */     }
/*  73:132 */     return this.highChars[(128 + b)];
/*  74:    */   }
/*  75:    */   
/*  76:    */   public boolean canEncodeChar(char c)
/*  77:    */   {
/*  78:141 */     if ((c >= 0) && (c < '')) {
/*  79:142 */       return true;
/*  80:    */     }
/*  81:145 */     Simple8BitChar r = encodeHighChar(c);
/*  82:146 */     return r != null;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public boolean pushEncodedChar(ByteBuffer bb, char c)
/*  86:    */   {
/*  87:160 */     if ((c >= 0) && (c < ''))
/*  88:    */     {
/*  89:161 */       bb.put((byte)c);
/*  90:162 */       return true;
/*  91:    */     }
/*  92:165 */     Simple8BitChar r = encodeHighChar(c);
/*  93:166 */     if (r == null) {
/*  94:167 */       return false;
/*  95:    */     }
/*  96:169 */     bb.put(r.code);
/*  97:170 */     return true;
/*  98:    */   }
/*  99:    */   
/* 100:    */   private Simple8BitChar encodeHighChar(char c)
/* 101:    */   {
/* 102:182 */     int i0 = 0;
/* 103:183 */     int i1 = this.reverseMapping.size();
/* 104:185 */     while (i1 > i0)
/* 105:    */     {
/* 106:187 */       int i = i0 + (i1 - i0) / 2;
/* 107:    */       
/* 108:189 */       Simple8BitChar m = (Simple8BitChar)this.reverseMapping.get(i);
/* 109:191 */       if (m.unicode == c) {
/* 110:192 */         return m;
/* 111:    */       }
/* 112:195 */       if (m.unicode < c) {
/* 113:196 */         i0 = i + 1;
/* 114:    */       } else {
/* 115:198 */         i1 = i;
/* 116:    */       }
/* 117:    */     }
/* 118:202 */     if (i0 >= this.reverseMapping.size()) {
/* 119:203 */       return null;
/* 120:    */     }
/* 121:206 */     Simple8BitChar r = (Simple8BitChar)this.reverseMapping.get(i0);
/* 122:208 */     if (r.unicode != c) {
/* 123:209 */       return null;
/* 124:    */     }
/* 125:212 */     return r;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public boolean canEncode(String name)
/* 129:    */   {
/* 130:221 */     for (int i = 0; i < name.length(); i++)
/* 131:    */     {
/* 132:223 */       char c = name.charAt(i);
/* 133:225 */       if (!canEncodeChar(c)) {
/* 134:226 */         return false;
/* 135:    */       }
/* 136:    */     }
/* 137:230 */     return true;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public ByteBuffer encode(String name)
/* 141:    */   {
/* 142:238 */     ByteBuffer out = ByteBuffer.allocate(name.length() + 6 + (name.length() + 1) / 2);
/* 143:241 */     for (int i = 0; i < name.length(); i++)
/* 144:    */     {
/* 145:243 */       char c = name.charAt(i);
/* 146:245 */       if (out.remaining() < 6) {
/* 147:246 */         out = ZipEncodingHelper.growBuffer(out, out.position() + 6);
/* 148:    */       }
/* 149:249 */       if (!pushEncodedChar(out, c)) {
/* 150:251 */         ZipEncodingHelper.appendSurrogate(out, c);
/* 151:    */       }
/* 152:    */     }
/* 153:255 */     out.limit(out.position());
/* 154:256 */     out.rewind();
/* 155:257 */     return out;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public String decode(byte[] data)
/* 159:    */     throws IOException
/* 160:    */   {
/* 161:265 */     char[] ret = new char[data.length];
/* 162:267 */     for (int i = 0; i < data.length; i++) {
/* 163:268 */       ret[i] = decodeByte(data[i]);
/* 164:    */     }
/* 165:271 */     return new String(ret);
/* 166:    */   }
/* 167:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.Simple8BitZipEncoding
 * JD-Core Version:    0.7.0.1
 */