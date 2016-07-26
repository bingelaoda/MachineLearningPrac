/*   1:    */ package org.apache.commons.compress.compressors;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
/*   7:    */ import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
/*   8:    */ import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
/*   9:    */ import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
/*  10:    */ import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
/*  11:    */ import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
/*  12:    */ import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
/*  13:    */ import org.apache.commons.compress.compressors.lzma.LZMAUtils;
/*  14:    */ import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
/*  15:    */ import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
/*  16:    */ import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
/*  17:    */ import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
/*  18:    */ import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
/*  19:    */ import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
/*  20:    */ import org.apache.commons.compress.compressors.xz.XZUtils;
/*  21:    */ import org.apache.commons.compress.compressors.z.ZCompressorInputStream;
/*  22:    */ import org.apache.commons.compress.utils.IOUtils;
/*  23:    */ 
/*  24:    */ public class CompressorStreamFactory
/*  25:    */ {
/*  26:    */   public static final String BZIP2 = "bzip2";
/*  27:    */   public static final String GZIP = "gz";
/*  28:    */   public static final String PACK200 = "pack200";
/*  29:    */   public static final String XZ = "xz";
/*  30:    */   public static final String LZMA = "lzma";
/*  31:    */   public static final String SNAPPY_FRAMED = "snappy-framed";
/*  32:    */   public static final String SNAPPY_RAW = "snappy-raw";
/*  33:    */   public static final String Z = "z";
/*  34:    */   public static final String DEFLATE = "deflate";
/*  35:    */   private final Boolean decompressUntilEOF;
/*  36:144 */   private volatile boolean decompressConcatenated = false;
/*  37:    */   
/*  38:    */   public CompressorStreamFactory()
/*  39:    */   {
/*  40:150 */     this.decompressUntilEOF = null;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public CompressorStreamFactory(boolean decompressUntilEOF)
/*  44:    */   {
/*  45:164 */     this.decompressUntilEOF = Boolean.valueOf(decompressUntilEOF);
/*  46:    */     
/*  47:166 */     this.decompressConcatenated = decompressUntilEOF;
/*  48:    */   }
/*  49:    */   
/*  50:    */   @Deprecated
/*  51:    */   public void setDecompressConcatenated(boolean decompressConcatenated)
/*  52:    */   {
/*  53:187 */     if (this.decompressUntilEOF != null) {
/*  54:188 */       throw new IllegalStateException("Cannot override the setting defined by the constructor");
/*  55:    */     }
/*  56:190 */     this.decompressConcatenated = decompressConcatenated;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public CompressorInputStream createCompressorInputStream(InputStream in)
/*  60:    */     throws CompressorException
/*  61:    */   {
/*  62:206 */     if (in == null) {
/*  63:207 */       throw new IllegalArgumentException("Stream must not be null.");
/*  64:    */     }
/*  65:210 */     if (!in.markSupported()) {
/*  66:211 */       throw new IllegalArgumentException("Mark is not supported.");
/*  67:    */     }
/*  68:214 */     byte[] signature = new byte[12];
/*  69:215 */     in.mark(signature.length);
/*  70:    */     try
/*  71:    */     {
/*  72:217 */       int signatureLength = IOUtils.readFully(in, signature);
/*  73:218 */       in.reset();
/*  74:220 */       if (BZip2CompressorInputStream.matches(signature, signatureLength)) {
/*  75:221 */         return new BZip2CompressorInputStream(in, this.decompressConcatenated);
/*  76:    */       }
/*  77:224 */       if (GzipCompressorInputStream.matches(signature, signatureLength)) {
/*  78:225 */         return new GzipCompressorInputStream(in, this.decompressConcatenated);
/*  79:    */       }
/*  80:228 */       if (Pack200CompressorInputStream.matches(signature, signatureLength)) {
/*  81:229 */         return new Pack200CompressorInputStream(in);
/*  82:    */       }
/*  83:232 */       if (FramedSnappyCompressorInputStream.matches(signature, signatureLength)) {
/*  84:233 */         return new FramedSnappyCompressorInputStream(in);
/*  85:    */       }
/*  86:236 */       if (ZCompressorInputStream.matches(signature, signatureLength)) {
/*  87:237 */         return new ZCompressorInputStream(in);
/*  88:    */       }
/*  89:240 */       if (DeflateCompressorInputStream.matches(signature, signatureLength)) {
/*  90:241 */         return new DeflateCompressorInputStream(in);
/*  91:    */       }
/*  92:244 */       if ((XZUtils.matches(signature, signatureLength)) && (XZUtils.isXZCompressionAvailable())) {
/*  93:246 */         return new XZCompressorInputStream(in, this.decompressConcatenated);
/*  94:    */       }
/*  95:249 */       if ((LZMAUtils.matches(signature, signatureLength)) && (LZMAUtils.isLZMACompressionAvailable())) {
/*  96:251 */         return new LZMACompressorInputStream(in);
/*  97:    */       }
/*  98:    */     }
/*  99:    */     catch (IOException e)
/* 100:    */     {
/* 101:255 */       throw new CompressorException("Failed to detect Compressor from InputStream.", e);
/* 102:    */     }
/* 103:258 */     throw new CompressorException("No Compressor found for the stream signature.");
/* 104:    */   }
/* 105:    */   
/* 106:    */   public CompressorInputStream createCompressorInputStream(String name, InputStream in)
/* 107:    */     throws CompressorException
/* 108:    */   {
/* 109:275 */     if ((name == null) || (in == null)) {
/* 110:276 */       throw new IllegalArgumentException("Compressor name and stream must not be null.");
/* 111:    */     }
/* 112:    */     try
/* 113:    */     {
/* 114:282 */       if ("gz".equalsIgnoreCase(name)) {
/* 115:283 */         return new GzipCompressorInputStream(in, this.decompressConcatenated);
/* 116:    */       }
/* 117:286 */       if ("bzip2".equalsIgnoreCase(name)) {
/* 118:287 */         return new BZip2CompressorInputStream(in, this.decompressConcatenated);
/* 119:    */       }
/* 120:290 */       if ("xz".equalsIgnoreCase(name)) {
/* 121:291 */         return new XZCompressorInputStream(in, this.decompressConcatenated);
/* 122:    */       }
/* 123:294 */       if ("lzma".equalsIgnoreCase(name)) {
/* 124:295 */         return new LZMACompressorInputStream(in);
/* 125:    */       }
/* 126:298 */       if ("pack200".equalsIgnoreCase(name)) {
/* 127:299 */         return new Pack200CompressorInputStream(in);
/* 128:    */       }
/* 129:302 */       if ("snappy-raw".equalsIgnoreCase(name)) {
/* 130:303 */         return new SnappyCompressorInputStream(in);
/* 131:    */       }
/* 132:306 */       if ("snappy-framed".equalsIgnoreCase(name)) {
/* 133:307 */         return new FramedSnappyCompressorInputStream(in);
/* 134:    */       }
/* 135:310 */       if ("z".equalsIgnoreCase(name)) {
/* 136:311 */         return new ZCompressorInputStream(in);
/* 137:    */       }
/* 138:314 */       if ("deflate".equalsIgnoreCase(name)) {
/* 139:315 */         return new DeflateCompressorInputStream(in);
/* 140:    */       }
/* 141:    */     }
/* 142:    */     catch (IOException e)
/* 143:    */     {
/* 144:319 */       throw new CompressorException("Could not create CompressorInputStream.", e);
/* 145:    */     }
/* 146:322 */     throw new CompressorException("Compressor: " + name + " not found.");
/* 147:    */   }
/* 148:    */   
/* 149:    */   public CompressorOutputStream createCompressorOutputStream(String name, OutputStream out)
/* 150:    */     throws CompressorException
/* 151:    */   {
/* 152:339 */     if ((name == null) || (out == null)) {
/* 153:340 */       throw new IllegalArgumentException("Compressor name and stream must not be null.");
/* 154:    */     }
/* 155:    */     try
/* 156:    */     {
/* 157:346 */       if ("gz".equalsIgnoreCase(name)) {
/* 158:347 */         return new GzipCompressorOutputStream(out);
/* 159:    */       }
/* 160:350 */       if ("bzip2".equalsIgnoreCase(name)) {
/* 161:351 */         return new BZip2CompressorOutputStream(out);
/* 162:    */       }
/* 163:354 */       if ("xz".equalsIgnoreCase(name)) {
/* 164:355 */         return new XZCompressorOutputStream(out);
/* 165:    */       }
/* 166:358 */       if ("pack200".equalsIgnoreCase(name)) {
/* 167:359 */         return new Pack200CompressorOutputStream(out);
/* 168:    */       }
/* 169:362 */       if ("deflate".equalsIgnoreCase(name)) {
/* 170:363 */         return new DeflateCompressorOutputStream(out);
/* 171:    */       }
/* 172:    */     }
/* 173:    */     catch (IOException e)
/* 174:    */     {
/* 175:367 */       throw new CompressorException("Could not create CompressorOutputStream", e);
/* 176:    */     }
/* 177:370 */     throw new CompressorException("Compressor: " + name + " not found.");
/* 178:    */   }
/* 179:    */   
/* 180:    */   boolean getDecompressConcatenated()
/* 181:    */   {
/* 182:375 */     return this.decompressConcatenated;
/* 183:    */   }
/* 184:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.CompressorStreamFactory
 * JD-Core Version:    0.7.0.1
 */