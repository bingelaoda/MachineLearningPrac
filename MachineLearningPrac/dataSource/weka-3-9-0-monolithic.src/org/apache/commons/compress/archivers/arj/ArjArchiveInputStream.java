/*   1:    */ package org.apache.commons.compress.archivers.arj;
/*   2:    */ 
/*   3:    */ import java.io.ByteArrayInputStream;
/*   4:    */ import java.io.ByteArrayOutputStream;
/*   5:    */ import java.io.DataInputStream;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.InputStream;
/*   8:    */ import java.util.ArrayList;
/*   9:    */ import java.util.zip.CRC32;
/*  10:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*  11:    */ import org.apache.commons.compress.archivers.ArchiveException;
/*  12:    */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*  13:    */ import org.apache.commons.compress.utils.BoundedInputStream;
/*  14:    */ import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
/*  15:    */ import org.apache.commons.compress.utils.IOUtils;
/*  16:    */ 
/*  17:    */ public class ArjArchiveInputStream
/*  18:    */   extends ArchiveInputStream
/*  19:    */ {
/*  20:    */   private static final int ARJ_MAGIC_1 = 96;
/*  21:    */   private static final int ARJ_MAGIC_2 = 234;
/*  22:    */   private final DataInputStream in;
/*  23:    */   private final String charsetName;
/*  24:    */   private final MainHeader mainHeader;
/*  25: 48 */   private LocalFileHeader currentLocalFileHeader = null;
/*  26: 49 */   private InputStream currentInputStream = null;
/*  27:    */   
/*  28:    */   public ArjArchiveInputStream(InputStream inputStream, String charsetName)
/*  29:    */     throws ArchiveException
/*  30:    */   {
/*  31: 60 */     this.in = new DataInputStream(inputStream);
/*  32: 61 */     this.charsetName = charsetName;
/*  33:    */     try
/*  34:    */     {
/*  35: 63 */       this.mainHeader = readMainHeader();
/*  36: 64 */       if ((this.mainHeader.arjFlags & 0x1) != 0) {
/*  37: 65 */         throw new ArchiveException("Encrypted ARJ files are unsupported");
/*  38:    */       }
/*  39: 67 */       if ((this.mainHeader.arjFlags & 0x4) != 0) {
/*  40: 68 */         throw new ArchiveException("Multi-volume ARJ files are unsupported");
/*  41:    */       }
/*  42:    */     }
/*  43:    */     catch (IOException ioException)
/*  44:    */     {
/*  45: 71 */       throw new ArchiveException(ioException.getMessage(), ioException);
/*  46:    */     }
/*  47:    */   }
/*  48:    */   
/*  49:    */   public ArjArchiveInputStream(InputStream inputStream)
/*  50:    */     throws ArchiveException
/*  51:    */   {
/*  52: 83 */     this(inputStream, "CP437");
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void close()
/*  56:    */     throws IOException
/*  57:    */   {
/*  58: 88 */     this.in.close();
/*  59:    */   }
/*  60:    */   
/*  61:    */   private int read8(DataInputStream dataIn)
/*  62:    */     throws IOException
/*  63:    */   {
/*  64: 92 */     int value = dataIn.readUnsignedByte();
/*  65: 93 */     count(1);
/*  66: 94 */     return value;
/*  67:    */   }
/*  68:    */   
/*  69:    */   private int read16(DataInputStream dataIn)
/*  70:    */     throws IOException
/*  71:    */   {
/*  72: 98 */     int value = dataIn.readUnsignedShort();
/*  73: 99 */     count(2);
/*  74:100 */     return Integer.reverseBytes(value) >>> 16;
/*  75:    */   }
/*  76:    */   
/*  77:    */   private int read32(DataInputStream dataIn)
/*  78:    */     throws IOException
/*  79:    */   {
/*  80:104 */     int value = dataIn.readInt();
/*  81:105 */     count(4);
/*  82:106 */     return Integer.reverseBytes(value);
/*  83:    */   }
/*  84:    */   
/*  85:    */   private String readString(DataInputStream dataIn)
/*  86:    */     throws IOException
/*  87:    */   {
/*  88:110 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/*  89:    */     int nextByte;
/*  90:112 */     while ((nextByte = dataIn.readUnsignedByte()) != 0) {
/*  91:113 */       buffer.write(nextByte);
/*  92:    */     }
/*  93:115 */     if (this.charsetName != null) {
/*  94:116 */       return new String(buffer.toByteArray(), this.charsetName);
/*  95:    */     }
/*  96:119 */     return new String(buffer.toByteArray());
/*  97:    */   }
/*  98:    */   
/*  99:    */   private void readFully(DataInputStream dataIn, byte[] b)
/* 100:    */     throws IOException
/* 101:    */   {
/* 102:125 */     dataIn.readFully(b);
/* 103:126 */     count(b.length);
/* 104:    */   }
/* 105:    */   
/* 106:    */   private byte[] readHeader()
/* 107:    */     throws IOException
/* 108:    */   {
/* 109:130 */     boolean found = false;
/* 110:131 */     byte[] basicHeaderBytes = null;
/* 111:    */     do
/* 112:    */     {
/* 113:133 */       int first = 0;
/* 114:134 */       int second = read8(this.in);
/* 115:    */       do
/* 116:    */       {
/* 117:136 */         first = second;
/* 118:137 */         second = read8(this.in);
/* 119:138 */       } while ((first != 96) && (second != 234));
/* 120:139 */       int basicHeaderSize = read16(this.in);
/* 121:140 */       if (basicHeaderSize == 0) {
/* 122:142 */         return null;
/* 123:    */       }
/* 124:144 */       if (basicHeaderSize <= 2600)
/* 125:    */       {
/* 126:145 */         basicHeaderBytes = new byte[basicHeaderSize];
/* 127:146 */         readFully(this.in, basicHeaderBytes);
/* 128:147 */         long basicHeaderCrc32 = read32(this.in) & 0xFFFFFFFF;
/* 129:148 */         CRC32 crc32 = new CRC32();
/* 130:149 */         crc32.update(basicHeaderBytes);
/* 131:150 */         if (basicHeaderCrc32 == crc32.getValue()) {
/* 132:151 */           found = true;
/* 133:    */         }
/* 134:    */       }
/* 135:154 */     } while (!found);
/* 136:155 */     return basicHeaderBytes;
/* 137:    */   }
/* 138:    */   
/* 139:    */   private MainHeader readMainHeader()
/* 140:    */     throws IOException
/* 141:    */   {
/* 142:159 */     byte[] basicHeaderBytes = readHeader();
/* 143:160 */     if (basicHeaderBytes == null) {
/* 144:161 */       throw new IOException("Archive ends without any headers");
/* 145:    */     }
/* 146:163 */     DataInputStream basicHeader = new DataInputStream(new ByteArrayInputStream(basicHeaderBytes));
/* 147:    */     
/* 148:    */ 
/* 149:166 */     int firstHeaderSize = basicHeader.readUnsignedByte();
/* 150:167 */     byte[] firstHeaderBytes = new byte[firstHeaderSize - 1];
/* 151:168 */     basicHeader.readFully(firstHeaderBytes);
/* 152:169 */     DataInputStream firstHeader = new DataInputStream(new ByteArrayInputStream(firstHeaderBytes));
/* 153:    */     
/* 154:    */ 
/* 155:172 */     MainHeader hdr = new MainHeader();
/* 156:173 */     hdr.archiverVersionNumber = firstHeader.readUnsignedByte();
/* 157:174 */     hdr.minVersionToExtract = firstHeader.readUnsignedByte();
/* 158:175 */     hdr.hostOS = firstHeader.readUnsignedByte();
/* 159:176 */     hdr.arjFlags = firstHeader.readUnsignedByte();
/* 160:177 */     hdr.securityVersion = firstHeader.readUnsignedByte();
/* 161:178 */     hdr.fileType = firstHeader.readUnsignedByte();
/* 162:179 */     hdr.reserved = firstHeader.readUnsignedByte();
/* 163:180 */     hdr.dateTimeCreated = read32(firstHeader);
/* 164:181 */     hdr.dateTimeModified = read32(firstHeader);
/* 165:182 */     hdr.archiveSize = (0xFFFFFFFF & read32(firstHeader));
/* 166:183 */     hdr.securityEnvelopeFilePosition = read32(firstHeader);
/* 167:184 */     hdr.fileSpecPosition = read16(firstHeader);
/* 168:185 */     hdr.securityEnvelopeLength = read16(firstHeader);
/* 169:186 */     pushedBackBytes(20L);
/* 170:187 */     hdr.encryptionVersion = firstHeader.readUnsignedByte();
/* 171:188 */     hdr.lastChapter = firstHeader.readUnsignedByte();
/* 172:190 */     if (firstHeaderSize >= 33)
/* 173:    */     {
/* 174:191 */       hdr.arjProtectionFactor = firstHeader.readUnsignedByte();
/* 175:192 */       hdr.arjFlags2 = firstHeader.readUnsignedByte();
/* 176:193 */       firstHeader.readUnsignedByte();
/* 177:194 */       firstHeader.readUnsignedByte();
/* 178:    */     }
/* 179:197 */     hdr.name = readString(basicHeader);
/* 180:198 */     hdr.comment = readString(basicHeader);
/* 181:    */     
/* 182:200 */     int extendedHeaderSize = read16(this.in);
/* 183:201 */     if (extendedHeaderSize > 0)
/* 184:    */     {
/* 185:202 */       hdr.extendedHeaderBytes = new byte[extendedHeaderSize];
/* 186:203 */       readFully(this.in, hdr.extendedHeaderBytes);
/* 187:204 */       long extendedHeaderCrc32 = 0xFFFFFFFF & read32(this.in);
/* 188:205 */       CRC32 crc32 = new CRC32();
/* 189:206 */       crc32.update(hdr.extendedHeaderBytes);
/* 190:207 */       if (extendedHeaderCrc32 != crc32.getValue()) {
/* 191:208 */         throw new IOException("Extended header CRC32 verification failure");
/* 192:    */       }
/* 193:    */     }
/* 194:212 */     return hdr;
/* 195:    */   }
/* 196:    */   
/* 197:    */   private LocalFileHeader readLocalFileHeader()
/* 198:    */     throws IOException
/* 199:    */   {
/* 200:216 */     byte[] basicHeaderBytes = readHeader();
/* 201:217 */     if (basicHeaderBytes == null) {
/* 202:218 */       return null;
/* 203:    */     }
/* 204:220 */     DataInputStream basicHeader = new DataInputStream(new ByteArrayInputStream(basicHeaderBytes));
/* 205:    */     
/* 206:    */ 
/* 207:223 */     int firstHeaderSize = basicHeader.readUnsignedByte();
/* 208:224 */     byte[] firstHeaderBytes = new byte[firstHeaderSize - 1];
/* 209:225 */     basicHeader.readFully(firstHeaderBytes);
/* 210:226 */     DataInputStream firstHeader = new DataInputStream(new ByteArrayInputStream(firstHeaderBytes));
/* 211:    */     
/* 212:    */ 
/* 213:229 */     LocalFileHeader localFileHeader = new LocalFileHeader();
/* 214:230 */     localFileHeader.archiverVersionNumber = firstHeader.readUnsignedByte();
/* 215:231 */     localFileHeader.minVersionToExtract = firstHeader.readUnsignedByte();
/* 216:232 */     localFileHeader.hostOS = firstHeader.readUnsignedByte();
/* 217:233 */     localFileHeader.arjFlags = firstHeader.readUnsignedByte();
/* 218:234 */     localFileHeader.method = firstHeader.readUnsignedByte();
/* 219:235 */     localFileHeader.fileType = firstHeader.readUnsignedByte();
/* 220:236 */     localFileHeader.reserved = firstHeader.readUnsignedByte();
/* 221:237 */     localFileHeader.dateTimeModified = read32(firstHeader);
/* 222:238 */     localFileHeader.compressedSize = (0xFFFFFFFF & read32(firstHeader));
/* 223:239 */     localFileHeader.originalSize = (0xFFFFFFFF & read32(firstHeader));
/* 224:240 */     localFileHeader.originalCrc32 = (0xFFFFFFFF & read32(firstHeader));
/* 225:241 */     localFileHeader.fileSpecPosition = read16(firstHeader);
/* 226:242 */     localFileHeader.fileAccessMode = read16(firstHeader);
/* 227:243 */     pushedBackBytes(20L);
/* 228:244 */     localFileHeader.firstChapter = firstHeader.readUnsignedByte();
/* 229:245 */     localFileHeader.lastChapter = firstHeader.readUnsignedByte();
/* 230:    */     
/* 231:247 */     readExtraData(firstHeaderSize, firstHeader, localFileHeader);
/* 232:    */     
/* 233:249 */     localFileHeader.name = readString(basicHeader);
/* 234:250 */     localFileHeader.comment = readString(basicHeader);
/* 235:    */     
/* 236:252 */     ArrayList<byte[]> extendedHeaders = new ArrayList();
/* 237:    */     int extendedHeaderSize;
/* 238:254 */     while ((extendedHeaderSize = read16(this.in)) > 0)
/* 239:    */     {
/* 240:255 */       byte[] extendedHeaderBytes = new byte[extendedHeaderSize];
/* 241:256 */       readFully(this.in, extendedHeaderBytes);
/* 242:257 */       long extendedHeaderCrc32 = 0xFFFFFFFF & read32(this.in);
/* 243:258 */       CRC32 crc32 = new CRC32();
/* 244:259 */       crc32.update(extendedHeaderBytes);
/* 245:260 */       if (extendedHeaderCrc32 != crc32.getValue()) {
/* 246:261 */         throw new IOException("Extended header CRC32 verification failure");
/* 247:    */       }
/* 248:263 */       extendedHeaders.add(extendedHeaderBytes);
/* 249:    */     }
/* 250:265 */     localFileHeader.extendedHeaders = ((byte[][])extendedHeaders.toArray(new byte[extendedHeaders.size()][]));
/* 251:    */     
/* 252:267 */     return localFileHeader;
/* 253:    */   }
/* 254:    */   
/* 255:    */   private void readExtraData(int firstHeaderSize, DataInputStream firstHeader, LocalFileHeader localFileHeader)
/* 256:    */     throws IOException
/* 257:    */   {
/* 258:272 */     if (firstHeaderSize >= 33)
/* 259:    */     {
/* 260:273 */       localFileHeader.extendedFilePosition = read32(firstHeader);
/* 261:274 */       if (firstHeaderSize >= 45)
/* 262:    */       {
/* 263:275 */         localFileHeader.dateTimeAccessed = read32(firstHeader);
/* 264:276 */         localFileHeader.dateTimeCreated = read32(firstHeader);
/* 265:277 */         localFileHeader.originalSizeEvenForVolumes = read32(firstHeader);
/* 266:278 */         pushedBackBytes(12L);
/* 267:    */       }
/* 268:280 */       pushedBackBytes(4L);
/* 269:    */     }
/* 270:    */   }
/* 271:    */   
/* 272:    */   public static boolean matches(byte[] signature, int length)
/* 273:    */   {
/* 274:294 */     return (length >= 2) && ((0xFF & signature[0]) == 96) && ((0xFF & signature[1]) == 234);
/* 275:    */   }
/* 276:    */   
/* 277:    */   public String getArchiveName()
/* 278:    */   {
/* 279:304 */     return this.mainHeader.name;
/* 280:    */   }
/* 281:    */   
/* 282:    */   public String getArchiveComment()
/* 283:    */   {
/* 284:312 */     return this.mainHeader.comment;
/* 285:    */   }
/* 286:    */   
/* 287:    */   public ArjArchiveEntry getNextEntry()
/* 288:    */     throws IOException
/* 289:    */   {
/* 290:317 */     if (this.currentInputStream != null)
/* 291:    */     {
/* 292:319 */       IOUtils.skip(this.currentInputStream, 9223372036854775807L);
/* 293:320 */       this.currentInputStream.close();
/* 294:321 */       this.currentLocalFileHeader = null;
/* 295:322 */       this.currentInputStream = null;
/* 296:    */     }
/* 297:325 */     this.currentLocalFileHeader = readLocalFileHeader();
/* 298:326 */     if (this.currentLocalFileHeader != null)
/* 299:    */     {
/* 300:327 */       this.currentInputStream = new BoundedInputStream(this.in, this.currentLocalFileHeader.compressedSize);
/* 301:328 */       if (this.currentLocalFileHeader.method == 0) {
/* 302:329 */         this.currentInputStream = new CRC32VerifyingInputStream(this.currentInputStream, this.currentLocalFileHeader.originalSize, this.currentLocalFileHeader.originalCrc32);
/* 303:    */       }
/* 304:332 */       return new ArjArchiveEntry(this.currentLocalFileHeader);
/* 305:    */     }
/* 306:334 */     this.currentInputStream = null;
/* 307:335 */     return null;
/* 308:    */   }
/* 309:    */   
/* 310:    */   public boolean canReadEntryData(ArchiveEntry ae)
/* 311:    */   {
/* 312:341 */     return ((ae instanceof ArjArchiveEntry)) && (((ArjArchiveEntry)ae).getMethod() == 0);
/* 313:    */   }
/* 314:    */   
/* 315:    */   public int read(byte[] b, int off, int len)
/* 316:    */     throws IOException
/* 317:    */   {
/* 318:347 */     if (this.currentLocalFileHeader == null) {
/* 319:348 */       throw new IllegalStateException("No current arj entry");
/* 320:    */     }
/* 321:350 */     if (this.currentLocalFileHeader.method != 0) {
/* 322:351 */       throw new IOException("Unsupported compression method " + this.currentLocalFileHeader.method);
/* 323:    */     }
/* 324:353 */     return this.currentInputStream.read(b, off, len);
/* 325:    */   }
/* 326:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.arj.ArjArchiveInputStream
 * JD-Core Version:    0.7.0.1
 */