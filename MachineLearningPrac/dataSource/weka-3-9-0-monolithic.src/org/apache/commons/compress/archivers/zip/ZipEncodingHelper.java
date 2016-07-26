/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.nio.ByteBuffer;
/*   4:    */ import java.nio.charset.Charset;
/*   5:    */ import java.nio.charset.UnsupportedCharsetException;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.HashMap;
/*   8:    */ import java.util.Map;
/*   9:    */ import org.apache.commons.compress.utils.Charsets;
/*  10:    */ 
/*  11:    */ public abstract class ZipEncodingHelper
/*  12:    */ {
/*  13:    */   private static final Map<String, SimpleEncodingHolder> simpleEncodings;
/*  14:    */   
/*  15:    */   private static class SimpleEncodingHolder
/*  16:    */   {
/*  17:    */     private final char[] highChars;
/*  18:    */     private Simple8BitZipEncoding encoding;
/*  19:    */     
/*  20:    */     SimpleEncodingHolder(char[] highChars)
/*  21:    */     {
/*  22: 53 */       this.highChars = highChars;
/*  23:    */     }
/*  24:    */     
/*  25:    */     public synchronized Simple8BitZipEncoding getEncoding()
/*  26:    */     {
/*  27: 61 */       if (this.encoding == null) {
/*  28: 62 */         this.encoding = new Simple8BitZipEncoding(this.highChars);
/*  29:    */       }
/*  30: 64 */       return this.encoding;
/*  31:    */     }
/*  32:    */   }
/*  33:    */   
/*  34:    */   static
/*  35:    */   {
/*  36: 71 */     Map<String, SimpleEncodingHolder> se = new HashMap();
/*  37:    */     
/*  38:    */ 
/*  39: 74 */     char[] cp437_high_chars = { 'Ç', 'ü', 'é', 'â', 'ä', 'à', 'å', 'ç', 'ê', 'ë', 'è', 'ï', 'î', 'ì', 'Ä', 'Å', 'É', 'æ', 'Æ', 'ô', 'ö', 'ò', 'û', 'ù', 'ÿ', 'Ö', 'Ü', '¢', '£', '¥', '₧', 'ƒ', 'á', 'í', 'ó', 'ú', 'ñ', 'Ñ', 'ª', 'º', '¿', '⌐', '¬', '½', '¼', '¡', '«', '»', '░', '▒', '▓', '│', '┤', '╡', '╢', '╖', '╕', '╣', '║', '╗', '╝', '╜', '╛', '┐', '└', '┴', '┬', '├', '─', '┼', '╞', '╟', '╚', '╔', '╩', '╦', '╠', '═', '╬', '╧', '╨', '╤', '╥', '╙', '╘', '╒', '╓', '╫', '╪', '┘', '┌', '█', '▄', '▌', '▐', '▀', 'α', 'ß', 'Γ', 'π', 'Σ', 'σ', 'µ', 'τ', 'Φ', 'Θ', 'Ω', 'δ', '∞', 'φ', 'ε', '∩', '≡', '±', '≥', '≤', '⌠', '⌡', '÷', '≈', '°', '∙', '·', '√', 'ⁿ', '²', '■', ' ' };
/*  40:    */     
/*  41:    */ 
/*  42:    */ 
/*  43:    */ 
/*  44:    */ 
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54:    */ 
/*  55:    */ 
/*  56:    */ 
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62:    */ 
/*  63: 98 */     SimpleEncodingHolder cp437 = new SimpleEncodingHolder(cp437_high_chars);
/*  64:    */     
/*  65:100 */     se.put("CP437", cp437);
/*  66:101 */     se.put("Cp437", cp437);
/*  67:102 */     se.put("cp437", cp437);
/*  68:103 */     se.put("IBM437", cp437);
/*  69:104 */     se.put("ibm437", cp437);
/*  70:    */     
/*  71:106 */     char[] cp850_high_chars = { 'Ç', 'ü', 'é', 'â', 'ä', 'à', 'å', 'ç', 'ê', 'ë', 'è', 'ï', 'î', 'ì', 'Ä', 'Å', 'É', 'æ', 'Æ', 'ô', 'ö', 'ò', 'û', 'ù', 'ÿ', 'Ö', 'Ü', 'ø', '£', 'Ø', '×', 'ƒ', 'á', 'í', 'ó', 'ú', 'ñ', 'Ñ', 'ª', 'º', '¿', '®', '¬', '½', '¼', '¡', '«', '»', '░', '▒', '▓', '│', '┤', 'Á', 'Â', 'À', '©', '╣', '║', '╗', '╝', '¢', '¥', '┐', '└', '┴', '┬', '├', '─', '┼', 'ã', 'Ã', '╚', '╔', '╩', '╦', '╠', '═', '╬', '¤', 'ð', 'Ð', 'Ê', 'Ë', 'È', 'ı', 'Í', 'Î', 'Ï', '┘', '┌', '█', '▄', '¦', 'Ì', '▀', 'Ó', 'ß', 'Ô', 'Ò', 'õ', 'Õ', 'µ', 'þ', 'Þ', 'Ú', 'Û', 'Ù', 'ý', 'Ý', '¯', '´', '­', '±', '‗', '¾', '¶', '§', '÷', '¸', '°', '¨', '·', '¹', '³', '²', '■', ' ' };
/*  72:    */     
/*  73:    */ 
/*  74:    */ 
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81:    */ 
/*  82:    */ 
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:    */ 
/*  87:    */ 
/*  88:    */ 
/*  89:    */ 
/*  90:    */ 
/*  91:    */ 
/*  92:    */ 
/*  93:    */ 
/*  94:    */ 
/*  95:130 */     SimpleEncodingHolder cp850 = new SimpleEncodingHolder(cp850_high_chars);
/*  96:    */     
/*  97:132 */     se.put("CP850", cp850);
/*  98:133 */     se.put("Cp850", cp850);
/*  99:134 */     se.put("cp850", cp850);
/* 100:135 */     se.put("IBM850", cp850);
/* 101:136 */     se.put("ibm850", cp850);
/* 102:137 */     simpleEncodings = Collections.unmodifiableMap(se);
/* 103:    */   }
/* 104:    */   
/* 105:    */   static ByteBuffer growBuffer(ByteBuffer b, int newCapacity)
/* 106:    */   {
/* 107:153 */     b.limit(b.position());
/* 108:154 */     b.rewind();
/* 109:    */     
/* 110:156 */     int c2 = b.capacity() * 2;
/* 111:157 */     ByteBuffer on = ByteBuffer.allocate(c2 < newCapacity ? newCapacity : c2);
/* 112:    */     
/* 113:159 */     on.put(b);
/* 114:160 */     return on;
/* 115:    */   }
/* 116:    */   
/* 117:168 */   private static final byte[] HEX_DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
/* 118:    */   static final String UTF8 = "UTF8";
/* 119:    */   
/* 120:    */   static void appendSurrogate(ByteBuffer bb, char c)
/* 121:    */   {
/* 122:183 */     bb.put((byte)37);
/* 123:184 */     bb.put((byte)85);
/* 124:    */     
/* 125:186 */     bb.put(HEX_DIGITS[(c >> '\f' & 0xF)]);
/* 126:187 */     bb.put(HEX_DIGITS[(c >> '\b' & 0xF)]);
/* 127:188 */     bb.put(HEX_DIGITS[(c >> '\004' & 0xF)]);
/* 128:189 */     bb.put(HEX_DIGITS[(c & 0xF)]);
/* 129:    */   }
/* 130:    */   
/* 131:201 */   static final ZipEncoding UTF8_ZIP_ENCODING = new FallbackZipEncoding("UTF8");
/* 132:    */   
/* 133:    */   public static ZipEncoding getZipEncoding(String name)
/* 134:    */   {
/* 135:213 */     if (isUTF8(name)) {
/* 136:214 */       return UTF8_ZIP_ENCODING;
/* 137:    */     }
/* 138:217 */     if (name == null) {
/* 139:218 */       return new FallbackZipEncoding();
/* 140:    */     }
/* 141:221 */     SimpleEncodingHolder h = (SimpleEncodingHolder)simpleEncodings.get(name);
/* 142:223 */     if (h != null) {
/* 143:224 */       return h.getEncoding();
/* 144:    */     }
/* 145:    */     try
/* 146:    */     {
/* 147:229 */       Charset cs = Charset.forName(name);
/* 148:230 */       return new NioZipEncoding(cs);
/* 149:    */     }
/* 150:    */     catch (UnsupportedCharsetException e) {}
/* 151:233 */     return new FallbackZipEncoding(name);
/* 152:    */   }
/* 153:    */   
/* 154:    */   static boolean isUTF8(String charsetName)
/* 155:    */   {
/* 156:244 */     if (charsetName == null) {
/* 157:246 */       charsetName = Charset.defaultCharset().name();
/* 158:    */     }
/* 159:248 */     if (Charsets.UTF_8.name().equalsIgnoreCase(charsetName)) {
/* 160:249 */       return true;
/* 161:    */     }
/* 162:251 */     for (String alias : Charsets.UTF_8.aliases()) {
/* 163:252 */       if (alias.equalsIgnoreCase(charsetName)) {
/* 164:253 */         return true;
/* 165:    */       }
/* 166:    */     }
/* 167:256 */     return false;
/* 168:    */   }
/* 169:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipEncodingHelper
 * JD-Core Version:    0.7.0.1
 */