/*   1:    */ package org.apache.commons.compress.archivers.ar;
/*   2:    */ 
/*   3:    */ import java.io.EOFException;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   7:    */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*   8:    */ import org.apache.commons.compress.utils.ArchiveUtils;
/*   9:    */ import org.apache.commons.compress.utils.IOUtils;
/*  10:    */ 
/*  11:    */ public class ArArchiveInputStream
/*  12:    */   extends ArchiveInputStream
/*  13:    */ {
/*  14:    */   private final InputStream input;
/*  15: 39 */   private long offset = 0L;
/*  16:    */   private boolean closed;
/*  17: 46 */   private ArArchiveEntry currentEntry = null;
/*  18: 49 */   private byte[] namebuffer = null;
/*  19: 55 */   private long entryOffset = -1L;
/*  20: 58 */   private final byte[] NAME_BUF = new byte[16];
/*  21: 59 */   private final byte[] LAST_MODIFIED_BUF = new byte[12];
/*  22: 60 */   private final byte[] ID_BUF = new byte[6];
/*  23: 61 */   private final byte[] FILE_MODE_BUF = new byte[8];
/*  24: 62 */   private final byte[] LENGTH_BUF = new byte[10];
/*  25:    */   static final String BSD_LONGNAME_PREFIX = "#1/";
/*  26:    */   
/*  27:    */   public ArArchiveInputStream(InputStream pInput)
/*  28:    */   {
/*  29: 71 */     this.input = pInput;
/*  30: 72 */     this.closed = false;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public ArArchiveEntry getNextArEntry()
/*  34:    */     throws IOException
/*  35:    */   {
/*  36: 83 */     if (this.currentEntry != null)
/*  37:    */     {
/*  38: 84 */       long entryEnd = this.entryOffset + this.currentEntry.getLength();
/*  39: 85 */       IOUtils.skip(this, entryEnd - this.offset);
/*  40: 86 */       this.currentEntry = null;
/*  41:    */     }
/*  42: 89 */     if (this.offset == 0L)
/*  43:    */     {
/*  44: 90 */       byte[] expected = ArchiveUtils.toAsciiBytes("!<arch>\n");
/*  45: 91 */       byte[] realized = new byte[expected.length];
/*  46: 92 */       int read = IOUtils.readFully(this, realized);
/*  47: 93 */       if (read != expected.length) {
/*  48: 94 */         throw new IOException("failed to read header. Occured at byte: " + getBytesRead());
/*  49:    */       }
/*  50: 96 */       for (int i = 0; i < expected.length; i++) {
/*  51: 97 */         if (expected[i] != realized[i]) {
/*  52: 98 */           throw new IOException("invalid header " + ArchiveUtils.toAsciiString(realized));
/*  53:    */         }
/*  54:    */       }
/*  55:    */     }
/*  56:103 */     if ((this.offset % 2L != 0L) && (read() < 0)) {
/*  57:105 */       return null;
/*  58:    */     }
/*  59:108 */     if (this.input.available() == 0) {
/*  60:109 */       return null;
/*  61:    */     }
/*  62:112 */     IOUtils.readFully(this, this.NAME_BUF);
/*  63:113 */     IOUtils.readFully(this, this.LAST_MODIFIED_BUF);
/*  64:114 */     IOUtils.readFully(this, this.ID_BUF);
/*  65:115 */     int userId = asInt(this.ID_BUF, true);
/*  66:116 */     IOUtils.readFully(this, this.ID_BUF);
/*  67:117 */     IOUtils.readFully(this, this.FILE_MODE_BUF);
/*  68:118 */     IOUtils.readFully(this, this.LENGTH_BUF);
/*  69:    */     
/*  70:    */ 
/*  71:121 */     byte[] expected = ArchiveUtils.toAsciiBytes("`\n");
/*  72:122 */     byte[] realized = new byte[expected.length];
/*  73:123 */     int read = IOUtils.readFully(this, realized);
/*  74:124 */     if (read != expected.length) {
/*  75:125 */       throw new IOException("failed to read entry trailer. Occured at byte: " + getBytesRead());
/*  76:    */     }
/*  77:127 */     for (int i = 0; i < expected.length; i++) {
/*  78:128 */       if (expected[i] != realized[i]) {
/*  79:129 */         throw new IOException("invalid entry trailer. not read the content? Occured at byte: " + getBytesRead());
/*  80:    */       }
/*  81:    */     }
/*  82:134 */     this.entryOffset = this.offset;
/*  83:    */     
/*  84:    */ 
/*  85:    */ 
/*  86:    */ 
/*  87:139 */     String temp = ArchiveUtils.toAsciiString(this.NAME_BUF).trim();
/*  88:140 */     if (isGNUStringTable(temp))
/*  89:    */     {
/*  90:141 */       this.currentEntry = readGNUStringTable(this.LENGTH_BUF);
/*  91:142 */       return getNextArEntry();
/*  92:    */     }
/*  93:145 */     long len = asLong(this.LENGTH_BUF);
/*  94:146 */     if (temp.endsWith("/"))
/*  95:    */     {
/*  96:147 */       temp = temp.substring(0, temp.length() - 1);
/*  97:    */     }
/*  98:148 */     else if (isGNULongName(temp))
/*  99:    */     {
/* 100:149 */       int off = Integer.parseInt(temp.substring(1));
/* 101:150 */       temp = getExtendedName(off);
/* 102:    */     }
/* 103:151 */     else if (isBSDLongName(temp))
/* 104:    */     {
/* 105:152 */       temp = getBSDLongName(temp);
/* 106:    */       
/* 107:    */ 
/* 108:    */ 
/* 109:156 */       int nameLen = temp.length();
/* 110:157 */       len -= nameLen;
/* 111:158 */       this.entryOffset += nameLen;
/* 112:    */     }
/* 113:161 */     this.currentEntry = new ArArchiveEntry(temp, len, userId, asInt(this.ID_BUF, true), asInt(this.FILE_MODE_BUF, 8), asLong(this.LAST_MODIFIED_BUF));
/* 114:    */     
/* 115:    */ 
/* 116:    */ 
/* 117:165 */     return this.currentEntry;
/* 118:    */   }
/* 119:    */   
/* 120:    */   private String getExtendedName(int offset)
/* 121:    */     throws IOException
/* 122:    */   {
/* 123:176 */     if (this.namebuffer == null) {
/* 124:177 */       throw new IOException("Cannot process GNU long filename as no // record was found");
/* 125:    */     }
/* 126:179 */     for (int i = offset; i < this.namebuffer.length; i++) {
/* 127:180 */       if (this.namebuffer[i] == 10)
/* 128:    */       {
/* 129:181 */         if (this.namebuffer[(i - 1)] == 47) {
/* 130:182 */           i--;
/* 131:    */         }
/* 132:184 */         return ArchiveUtils.toAsciiString(this.namebuffer, offset, i - offset);
/* 133:    */       }
/* 134:    */     }
/* 135:187 */     throw new IOException("Failed to read entry: " + offset);
/* 136:    */   }
/* 137:    */   
/* 138:    */   private long asLong(byte[] input)
/* 139:    */   {
/* 140:190 */     return Long.parseLong(ArchiveUtils.toAsciiString(input).trim());
/* 141:    */   }
/* 142:    */   
/* 143:    */   private int asInt(byte[] input)
/* 144:    */   {
/* 145:194 */     return asInt(input, 10, false);
/* 146:    */   }
/* 147:    */   
/* 148:    */   private int asInt(byte[] input, boolean treatBlankAsZero)
/* 149:    */   {
/* 150:198 */     return asInt(input, 10, treatBlankAsZero);
/* 151:    */   }
/* 152:    */   
/* 153:    */   private int asInt(byte[] input, int base)
/* 154:    */   {
/* 155:202 */     return asInt(input, base, false);
/* 156:    */   }
/* 157:    */   
/* 158:    */   private int asInt(byte[] input, int base, boolean treatBlankAsZero)
/* 159:    */   {
/* 160:206 */     String string = ArchiveUtils.toAsciiString(input).trim();
/* 161:207 */     if ((string.length() == 0) && (treatBlankAsZero)) {
/* 162:208 */       return 0;
/* 163:    */     }
/* 164:210 */     return Integer.parseInt(string, base);
/* 165:    */   }
/* 166:    */   
/* 167:    */   public ArchiveEntry getNextEntry()
/* 168:    */     throws IOException
/* 169:    */   {
/* 170:221 */     return getNextArEntry();
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void close()
/* 174:    */     throws IOException
/* 175:    */   {
/* 176:231 */     if (!this.closed)
/* 177:    */     {
/* 178:232 */       this.closed = true;
/* 179:233 */       this.input.close();
/* 180:    */     }
/* 181:235 */     this.currentEntry = null;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public int read(byte[] b, int off, int len)
/* 185:    */     throws IOException
/* 186:    */   {
/* 187:245 */     int toRead = len;
/* 188:246 */     if (this.currentEntry != null)
/* 189:    */     {
/* 190:247 */       long entryEnd = this.entryOffset + this.currentEntry.getLength();
/* 191:248 */       if ((len > 0) && (entryEnd > this.offset)) {
/* 192:249 */         toRead = (int)Math.min(len, entryEnd - this.offset);
/* 193:    */       } else {
/* 194:251 */         return -1;
/* 195:    */       }
/* 196:    */     }
/* 197:254 */     int ret = this.input.read(b, off, toRead);
/* 198:255 */     count(ret);
/* 199:256 */     this.offset += (ret > 0 ? ret : 0L);
/* 200:257 */     return ret;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public static boolean matches(byte[] signature, int length)
/* 204:    */   {
/* 205:273 */     if (length < 8) {
/* 206:274 */       return false;
/* 207:    */     }
/* 208:276 */     if (signature[0] != 33) {
/* 209:277 */       return false;
/* 210:    */     }
/* 211:279 */     if (signature[1] != 60) {
/* 212:280 */       return false;
/* 213:    */     }
/* 214:282 */     if (signature[2] != 97) {
/* 215:283 */       return false;
/* 216:    */     }
/* 217:285 */     if (signature[3] != 114) {
/* 218:286 */       return false;
/* 219:    */     }
/* 220:288 */     if (signature[4] != 99) {
/* 221:289 */       return false;
/* 222:    */     }
/* 223:291 */     if (signature[5] != 104) {
/* 224:292 */       return false;
/* 225:    */     }
/* 226:294 */     if (signature[6] != 62) {
/* 227:295 */       return false;
/* 228:    */     }
/* 229:297 */     if (signature[7] != 10) {
/* 230:298 */       return false;
/* 231:    */     }
/* 232:301 */     return true;
/* 233:    */   }
/* 234:    */   
/* 235:305 */   private static final int BSD_LONGNAME_PREFIX_LEN = "#1/".length();
/* 236:    */   private static final String BSD_LONGNAME_PATTERN = "^#1/\\d+";
/* 237:    */   private static final String GNU_STRING_TABLE_NAME = "//";
/* 238:    */   private static final String GNU_LONGNAME_PATTERN = "^/\\d+";
/* 239:    */   
/* 240:    */   private static boolean isBSDLongName(String name)
/* 241:    */   {
/* 242:333 */     return (name != null) && (name.matches("^#1/\\d+"));
/* 243:    */   }
/* 244:    */   
/* 245:    */   private String getBSDLongName(String bsdLongName)
/* 246:    */     throws IOException
/* 247:    */   {
/* 248:345 */     int nameLen = Integer.parseInt(bsdLongName.substring(BSD_LONGNAME_PREFIX_LEN));
/* 249:    */     
/* 250:347 */     byte[] name = new byte[nameLen];
/* 251:348 */     int read = IOUtils.readFully(this.input, name);
/* 252:349 */     count(read);
/* 253:350 */     if (read != nameLen) {
/* 254:351 */       throw new EOFException();
/* 255:    */     }
/* 256:353 */     return ArchiveUtils.toAsciiString(name);
/* 257:    */   }
/* 258:    */   
/* 259:    */   private static boolean isGNUStringTable(String name)
/* 260:    */   {
/* 261:376 */     return "//".equals(name);
/* 262:    */   }
/* 263:    */   
/* 264:    */   private ArArchiveEntry readGNUStringTable(byte[] length)
/* 265:    */     throws IOException
/* 266:    */   {
/* 267:385 */     int bufflen = asInt(length);
/* 268:386 */     this.namebuffer = new byte[bufflen];
/* 269:387 */     int read = IOUtils.readFully(this, this.namebuffer, 0, bufflen);
/* 270:388 */     if (read != bufflen) {
/* 271:389 */       throw new IOException("Failed to read complete // record: expected=" + bufflen + " read=" + read);
/* 272:    */     }
/* 273:392 */     return new ArArchiveEntry("//", bufflen);
/* 274:    */   }
/* 275:    */   
/* 276:    */   private boolean isGNULongName(String name)
/* 277:    */   {
/* 278:404 */     return (name != null) && (name.matches("^/\\d+"));
/* 279:    */   }
/* 280:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.ar.ArArchiveInputStream
 * JD-Core Version:    0.7.0.1
 */