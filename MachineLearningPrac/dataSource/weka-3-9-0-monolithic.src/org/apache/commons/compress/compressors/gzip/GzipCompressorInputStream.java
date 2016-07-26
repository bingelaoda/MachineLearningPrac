/*   1:    */ package org.apache.commons.compress.compressors.gzip;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.ByteArrayOutputStream;
/*   5:    */ import java.io.DataInputStream;
/*   6:    */ import java.io.EOFException;
/*   7:    */ import java.io.IOException;
/*   8:    */ import java.io.InputStream;
/*   9:    */ import java.util.zip.CRC32;
/*  10:    */ import java.util.zip.DataFormatException;
/*  11:    */ import java.util.zip.Inflater;
/*  12:    */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*  13:    */ 
/*  14:    */ public class GzipCompressorInputStream
/*  15:    */   extends CompressorInputStream
/*  16:    */ {
/*  17:    */   private static final int FHCRC = 2;
/*  18:    */   private static final int FEXTRA = 4;
/*  19:    */   private static final int FNAME = 8;
/*  20:    */   private static final int FCOMMENT = 16;
/*  21:    */   private static final int FRESERVED = 224;
/*  22:    */   private final InputStream in;
/*  23:    */   private final boolean decompressConcatenated;
/*  24: 66 */   private final byte[] buf = new byte[8192];
/*  25: 69 */   private int bufUsed = 0;
/*  26: 72 */   private Inflater inf = new Inflater(true);
/*  27: 75 */   private final CRC32 crc = new CRC32();
/*  28: 78 */   private boolean endReached = false;
/*  29: 81 */   private final byte[] oneByte = new byte[1];
/*  30: 83 */   private final GzipParameters parameters = new GzipParameters();
/*  31:    */   
/*  32:    */   public GzipCompressorInputStream(InputStream inputStream)
/*  33:    */     throws IOException
/*  34:    */   {
/*  35:100 */     this(inputStream, false);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public GzipCompressorInputStream(InputStream inputStream, boolean decompressConcatenated)
/*  39:    */     throws IOException
/*  40:    */   {
/*  41:128 */     if (inputStream.markSupported()) {
/*  42:129 */       this.in = inputStream;
/*  43:    */     } else {
/*  44:131 */       this.in = new BufferedInputStream(inputStream);
/*  45:    */     }
/*  46:134 */     this.decompressConcatenated = decompressConcatenated;
/*  47:135 */     init(true);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public GzipParameters getMetaData()
/*  51:    */   {
/*  52:145 */     return this.parameters;
/*  53:    */   }
/*  54:    */   
/*  55:    */   private boolean init(boolean isFirstMember)
/*  56:    */     throws IOException
/*  57:    */   {
/*  58:149 */     assert ((isFirstMember) || (this.decompressConcatenated));
/*  59:    */     
/*  60:    */ 
/*  61:152 */     int magic0 = this.in.read();
/*  62:153 */     int magic1 = this.in.read();
/*  63:157 */     if ((magic0 == -1) && (!isFirstMember)) {
/*  64:158 */       return false;
/*  65:    */     }
/*  66:161 */     if ((magic0 != 31) || (magic1 != 139)) {
/*  67:162 */       throw new IOException(isFirstMember ? "Input is not in the .gz format" : "Garbage after a valid .gz stream");
/*  68:    */     }
/*  69:168 */     DataInputStream inData = new DataInputStream(this.in);
/*  70:169 */     int method = inData.readUnsignedByte();
/*  71:170 */     if (method != 8) {
/*  72:171 */       throw new IOException("Unsupported compression method " + method + " in the .gz header");
/*  73:    */     }
/*  74:175 */     int flg = inData.readUnsignedByte();
/*  75:176 */     if ((flg & 0xE0) != 0) {
/*  76:177 */       throw new IOException("Reserved flags are set in the .gz header");
/*  77:    */     }
/*  78:181 */     this.parameters.setModificationTime(readLittleEndianInt(inData) * 1000L);
/*  79:182 */     switch (inData.readUnsignedByte())
/*  80:    */     {
/*  81:    */     case 2: 
/*  82:184 */       this.parameters.setCompressionLevel(9);
/*  83:185 */       break;
/*  84:    */     case 4: 
/*  85:187 */       this.parameters.setCompressionLevel(1);
/*  86:188 */       break;
/*  87:    */     }
/*  88:193 */     this.parameters.setOperatingSystem(inData.readUnsignedByte());
/*  89:196 */     if ((flg & 0x4) != 0)
/*  90:    */     {
/*  91:197 */       int xlen = inData.readUnsignedByte();
/*  92:198 */       xlen |= inData.readUnsignedByte() << 8;
/*  93:203 */       while (xlen-- > 0) {
/*  94:204 */         inData.readUnsignedByte();
/*  95:    */       }
/*  96:    */     }
/*  97:209 */     if ((flg & 0x8) != 0) {
/*  98:210 */       this.parameters.setFilename(new String(readToNull(inData), "ISO-8859-1"));
/*  99:    */     }
/* 100:215 */     if ((flg & 0x10) != 0) {
/* 101:216 */       this.parameters.setComment(new String(readToNull(inData), "ISO-8859-1"));
/* 102:    */     }
/* 103:225 */     if ((flg & 0x2) != 0) {
/* 104:226 */       inData.readShort();
/* 105:    */     }
/* 106:230 */     this.inf.reset();
/* 107:231 */     this.crc.reset();
/* 108:    */     
/* 109:233 */     return true;
/* 110:    */   }
/* 111:    */   
/* 112:    */   private byte[] readToNull(DataInputStream inData)
/* 113:    */     throws IOException
/* 114:    */   {
/* 115:237 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/* 116:238 */     int b = 0;
/* 117:239 */     while ((b = inData.readUnsignedByte()) != 0) {
/* 118:240 */       bos.write(b);
/* 119:    */     }
/* 120:242 */     return bos.toByteArray();
/* 121:    */   }
/* 122:    */   
/* 123:    */   private long readLittleEndianInt(DataInputStream inData)
/* 124:    */     throws IOException
/* 125:    */   {
/* 126:246 */     return inData.readUnsignedByte() | inData.readUnsignedByte() << 8 | inData.readUnsignedByte() << 16 | inData.readUnsignedByte() << 24;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public int read()
/* 130:    */     throws IOException
/* 131:    */   {
/* 132:254 */     return read(this.oneByte, 0, 1) == -1 ? -1 : this.oneByte[0] & 0xFF;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public int read(byte[] b, int off, int len)
/* 136:    */     throws IOException
/* 137:    */   {
/* 138:264 */     if (this.endReached) {
/* 139:265 */       return -1;
/* 140:    */     }
/* 141:268 */     int size = 0;
/* 142:270 */     while (len > 0)
/* 143:    */     {
/* 144:271 */       if (this.inf.needsInput())
/* 145:    */       {
/* 146:274 */         this.in.mark(this.buf.length);
/* 147:    */         
/* 148:276 */         this.bufUsed = this.in.read(this.buf);
/* 149:277 */         if (this.bufUsed == -1) {
/* 150:278 */           throw new EOFException();
/* 151:    */         }
/* 152:281 */         this.inf.setInput(this.buf, 0, this.bufUsed);
/* 153:    */       }
/* 154:    */       int ret;
/* 155:    */       try
/* 156:    */       {
/* 157:286 */         ret = this.inf.inflate(b, off, len);
/* 158:    */       }
/* 159:    */       catch (DataFormatException e)
/* 160:    */       {
/* 161:288 */         throw new IOException("Gzip-compressed data is corrupt");
/* 162:    */       }
/* 163:291 */       this.crc.update(b, off, ret);
/* 164:292 */       off += ret;
/* 165:293 */       len -= ret;
/* 166:294 */       size += ret;
/* 167:295 */       count(ret);
/* 168:297 */       if (this.inf.finished())
/* 169:    */       {
/* 170:303 */         this.in.reset();
/* 171:    */         
/* 172:305 */         int skipAmount = this.bufUsed - this.inf.getRemaining();
/* 173:306 */         if (this.in.skip(skipAmount) != skipAmount) {
/* 174:307 */           throw new IOException();
/* 175:    */         }
/* 176:310 */         this.bufUsed = 0;
/* 177:    */         
/* 178:312 */         DataInputStream inData = new DataInputStream(this.in);
/* 179:    */         
/* 180:    */ 
/* 181:315 */         long crcStored = readLittleEndianInt(inData);
/* 182:317 */         if (crcStored != this.crc.getValue()) {
/* 183:318 */           throw new IOException("Gzip-compressed data is corrupt (CRC32 error)");
/* 184:    */         }
/* 185:323 */         long isize = readLittleEndianInt(inData);
/* 186:325 */         if (isize != (this.inf.getBytesWritten() & 0xFFFFFFFF)) {
/* 187:326 */           throw new IOException("Gzip-compressed data is corrupt(uncompressed size mismatch)");
/* 188:    */         }
/* 189:331 */         if ((!this.decompressConcatenated) || (!init(false)))
/* 190:    */         {
/* 191:332 */           this.inf.end();
/* 192:333 */           this.inf = null;
/* 193:334 */           this.endReached = true;
/* 194:335 */           return size == 0 ? -1 : size;
/* 195:    */         }
/* 196:    */       }
/* 197:    */     }
/* 198:340 */     return size;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public static boolean matches(byte[] signature, int length)
/* 202:    */   {
/* 203:354 */     if (length < 2) {
/* 204:355 */       return false;
/* 205:    */     }
/* 206:358 */     if (signature[0] != 31) {
/* 207:359 */       return false;
/* 208:    */     }
/* 209:362 */     if (signature[1] != -117) {
/* 210:363 */       return false;
/* 211:    */     }
/* 212:366 */     return true;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public void close()
/* 216:    */     throws IOException
/* 217:    */   {
/* 218:376 */     if (this.inf != null)
/* 219:    */     {
/* 220:377 */       this.inf.end();
/* 221:378 */       this.inf = null;
/* 222:    */     }
/* 223:381 */     if (this.in != System.in) {
/* 224:382 */       this.in.close();
/* 225:    */     }
/* 226:    */   }
/* 227:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
 * JD-Core Version:    0.7.0.1
 */