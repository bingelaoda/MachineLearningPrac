/*   1:    */ package org.apache.commons.compress.archivers;
/*   2:    */ 
/*   3:    */ import java.io.ByteArrayInputStream;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.io.OutputStream;
/*   7:    */ import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
/*   8:    */ import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
/*   9:    */ import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;
/*  10:    */ import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
/*  11:    */ import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
/*  12:    */ import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
/*  13:    */ import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
/*  14:    */ import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
/*  15:    */ import org.apache.commons.compress.archivers.sevenz.SevenZFile;
/*  16:    */ import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
/*  17:    */ import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
/*  18:    */ import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
/*  19:    */ import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
/*  20:    */ import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
/*  21:    */ import org.apache.commons.compress.utils.IOUtils;
/*  22:    */ 
/*  23:    */ public class ArchiveStreamFactory
/*  24:    */ {
/*  25:    */   public static final String AR = "ar";
/*  26:    */   public static final String ARJ = "arj";
/*  27:    */   public static final String CPIO = "cpio";
/*  28:    */   public static final String DUMP = "dump";
/*  29:    */   public static final String JAR = "jar";
/*  30:    */   public static final String TAR = "tar";
/*  31:    */   public static final String ZIP = "zip";
/*  32:    */   public static final String SEVEN_Z = "7z";
/*  33:    */   private final String encoding;
/*  34:130 */   private volatile String entryEncoding = null;
/*  35:    */   
/*  36:    */   public ArchiveStreamFactory()
/*  37:    */   {
/*  38:136 */     this(null);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public ArchiveStreamFactory(String encoding)
/*  42:    */   {
/*  43:148 */     this.encoding = encoding;
/*  44:    */     
/*  45:150 */     this.entryEncoding = encoding;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String getEntryEncoding()
/*  49:    */   {
/*  50:161 */     return this.entryEncoding;
/*  51:    */   }
/*  52:    */   
/*  53:    */   @Deprecated
/*  54:    */   public void setEntryEncoding(String entryEncoding)
/*  55:    */   {
/*  56:176 */     if (this.encoding != null) {
/*  57:177 */       throw new IllegalStateException("Cannot overide encoding set by the constructor");
/*  58:    */     }
/*  59:179 */     this.entryEncoding = entryEncoding;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public ArchiveInputStream createArchiveInputStream(String archiverName, InputStream in)
/*  63:    */     throws ArchiveException
/*  64:    */   {
/*  65:198 */     if (archiverName == null) {
/*  66:199 */       throw new IllegalArgumentException("Archivername must not be null.");
/*  67:    */     }
/*  68:202 */     if (in == null) {
/*  69:203 */       throw new IllegalArgumentException("InputStream must not be null.");
/*  70:    */     }
/*  71:206 */     if ("ar".equalsIgnoreCase(archiverName)) {
/*  72:207 */       return new ArArchiveInputStream(in);
/*  73:    */     }
/*  74:209 */     if ("arj".equalsIgnoreCase(archiverName))
/*  75:    */     {
/*  76:210 */       if (this.entryEncoding != null) {
/*  77:211 */         return new ArjArchiveInputStream(in, this.entryEncoding);
/*  78:    */       }
/*  79:213 */       return new ArjArchiveInputStream(in);
/*  80:    */     }
/*  81:216 */     if ("zip".equalsIgnoreCase(archiverName))
/*  82:    */     {
/*  83:217 */       if (this.entryEncoding != null) {
/*  84:218 */         return new ZipArchiveInputStream(in, this.entryEncoding);
/*  85:    */       }
/*  86:220 */       return new ZipArchiveInputStream(in);
/*  87:    */     }
/*  88:223 */     if ("tar".equalsIgnoreCase(archiverName))
/*  89:    */     {
/*  90:224 */       if (this.entryEncoding != null) {
/*  91:225 */         return new TarArchiveInputStream(in, this.entryEncoding);
/*  92:    */       }
/*  93:227 */       return new TarArchiveInputStream(in);
/*  94:    */     }
/*  95:230 */     if ("jar".equalsIgnoreCase(archiverName))
/*  96:    */     {
/*  97:231 */       if (this.entryEncoding != null) {
/*  98:232 */         return new JarArchiveInputStream(in, this.entryEncoding);
/*  99:    */       }
/* 100:234 */       return new JarArchiveInputStream(in);
/* 101:    */     }
/* 102:237 */     if ("cpio".equalsIgnoreCase(archiverName))
/* 103:    */     {
/* 104:238 */       if (this.entryEncoding != null) {
/* 105:239 */         return new CpioArchiveInputStream(in, this.entryEncoding);
/* 106:    */       }
/* 107:241 */       return new CpioArchiveInputStream(in);
/* 108:    */     }
/* 109:244 */     if ("dump".equalsIgnoreCase(archiverName))
/* 110:    */     {
/* 111:245 */       if (this.entryEncoding != null) {
/* 112:246 */         return new DumpArchiveInputStream(in, this.entryEncoding);
/* 113:    */       }
/* 114:248 */       return new DumpArchiveInputStream(in);
/* 115:    */     }
/* 116:251 */     if ("7z".equalsIgnoreCase(archiverName)) {
/* 117:252 */       throw new StreamingNotSupportedException("7z");
/* 118:    */     }
/* 119:255 */     throw new ArchiveException("Archiver: " + archiverName + " not found.");
/* 120:    */   }
/* 121:    */   
/* 122:    */   public ArchiveOutputStream createArchiveOutputStream(String archiverName, OutputStream out)
/* 123:    */     throws ArchiveException
/* 124:    */   {
/* 125:273 */     if (archiverName == null) {
/* 126:274 */       throw new IllegalArgumentException("Archivername must not be null.");
/* 127:    */     }
/* 128:276 */     if (out == null) {
/* 129:277 */       throw new IllegalArgumentException("OutputStream must not be null.");
/* 130:    */     }
/* 131:280 */     if ("ar".equalsIgnoreCase(archiverName)) {
/* 132:281 */       return new ArArchiveOutputStream(out);
/* 133:    */     }
/* 134:283 */     if ("zip".equalsIgnoreCase(archiverName))
/* 135:    */     {
/* 136:284 */       ZipArchiveOutputStream zip = new ZipArchiveOutputStream(out);
/* 137:285 */       if (this.entryEncoding != null) {
/* 138:286 */         zip.setEncoding(this.entryEncoding);
/* 139:    */       }
/* 140:288 */       return zip;
/* 141:    */     }
/* 142:290 */     if ("tar".equalsIgnoreCase(archiverName))
/* 143:    */     {
/* 144:291 */       if (this.entryEncoding != null) {
/* 145:292 */         return new TarArchiveOutputStream(out, this.entryEncoding);
/* 146:    */       }
/* 147:294 */       return new TarArchiveOutputStream(out);
/* 148:    */     }
/* 149:297 */     if ("jar".equalsIgnoreCase(archiverName))
/* 150:    */     {
/* 151:298 */       if (this.entryEncoding != null) {
/* 152:299 */         return new JarArchiveOutputStream(out, this.entryEncoding);
/* 153:    */       }
/* 154:301 */       return new JarArchiveOutputStream(out);
/* 155:    */     }
/* 156:304 */     if ("cpio".equalsIgnoreCase(archiverName))
/* 157:    */     {
/* 158:305 */       if (this.entryEncoding != null) {
/* 159:306 */         return new CpioArchiveOutputStream(out, this.entryEncoding);
/* 160:    */       }
/* 161:308 */       return new CpioArchiveOutputStream(out);
/* 162:    */     }
/* 163:311 */     if ("7z".equalsIgnoreCase(archiverName)) {
/* 164:312 */       throw new StreamingNotSupportedException("7z");
/* 165:    */     }
/* 166:314 */     throw new ArchiveException("Archiver: " + archiverName + " not found.");
/* 167:    */   }
/* 168:    */   
/* 169:    */   public ArchiveInputStream createArchiveInputStream(InputStream in)
/* 170:    */     throws ArchiveException
/* 171:    */   {
/* 172:331 */     if (in == null) {
/* 173:332 */       throw new IllegalArgumentException("Stream must not be null.");
/* 174:    */     }
/* 175:335 */     if (!in.markSupported()) {
/* 176:336 */       throw new IllegalArgumentException("Mark is not supported.");
/* 177:    */     }
/* 178:339 */     byte[] signature = new byte[12];
/* 179:340 */     in.mark(signature.length);
/* 180:    */     try
/* 181:    */     {
/* 182:342 */       int signatureLength = IOUtils.readFully(in, signature);
/* 183:343 */       in.reset();
/* 184:344 */       if (ZipArchiveInputStream.matches(signature, signatureLength))
/* 185:    */       {
/* 186:345 */         if (this.entryEncoding != null) {
/* 187:346 */           return new ZipArchiveInputStream(in, this.entryEncoding);
/* 188:    */         }
/* 189:348 */         return new ZipArchiveInputStream(in);
/* 190:    */       }
/* 191:350 */       if (JarArchiveInputStream.matches(signature, signatureLength))
/* 192:    */       {
/* 193:351 */         if (this.entryEncoding != null) {
/* 194:352 */           return new JarArchiveInputStream(in, this.entryEncoding);
/* 195:    */         }
/* 196:354 */         return new JarArchiveInputStream(in);
/* 197:    */       }
/* 198:356 */       if (ArArchiveInputStream.matches(signature, signatureLength)) {
/* 199:357 */         return new ArArchiveInputStream(in);
/* 200:    */       }
/* 201:358 */       if (CpioArchiveInputStream.matches(signature, signatureLength))
/* 202:    */       {
/* 203:359 */         if (this.entryEncoding != null) {
/* 204:360 */           return new CpioArchiveInputStream(in, this.entryEncoding);
/* 205:    */         }
/* 206:362 */         return new CpioArchiveInputStream(in);
/* 207:    */       }
/* 208:364 */       if (ArjArchiveInputStream.matches(signature, signatureLength))
/* 209:    */       {
/* 210:365 */         if (this.entryEncoding != null) {
/* 211:366 */           return new ArjArchiveInputStream(in, this.entryEncoding);
/* 212:    */         }
/* 213:368 */         return new ArjArchiveInputStream(in);
/* 214:    */       }
/* 215:370 */       if (SevenZFile.matches(signature, signatureLength)) {
/* 216:371 */         throw new StreamingNotSupportedException("7z");
/* 217:    */       }
/* 218:375 */       byte[] dumpsig = new byte[32];
/* 219:376 */       in.mark(dumpsig.length);
/* 220:377 */       signatureLength = IOUtils.readFully(in, dumpsig);
/* 221:378 */       in.reset();
/* 222:379 */       if (DumpArchiveInputStream.matches(dumpsig, signatureLength)) {
/* 223:380 */         return new DumpArchiveInputStream(in, this.entryEncoding);
/* 224:    */       }
/* 225:384 */       byte[] tarheader = new byte[512];
/* 226:385 */       in.mark(tarheader.length);
/* 227:386 */       signatureLength = IOUtils.readFully(in, tarheader);
/* 228:387 */       in.reset();
/* 229:388 */       if (TarArchiveInputStream.matches(tarheader, signatureLength)) {
/* 230:389 */         return new TarArchiveInputStream(in, this.entryEncoding);
/* 231:    */       }
/* 232:392 */       if (signatureLength >= 512)
/* 233:    */       {
/* 234:393 */         TarArchiveInputStream tais = null;
/* 235:    */         try
/* 236:    */         {
/* 237:395 */           tais = new TarArchiveInputStream(new ByteArrayInputStream(tarheader));
/* 238:397 */           if (tais.getNextTarEntry().isCheckSumOK()) {
/* 239:398 */             return new TarArchiveInputStream(in, this.encoding);
/* 240:    */           }
/* 241:    */         }
/* 242:    */         catch (Exception e) {}finally
/* 243:    */         {
/* 244:406 */           IOUtils.closeQuietly(tais);
/* 245:    */         }
/* 246:    */       }
/* 247:    */     }
/* 248:    */     catch (IOException e)
/* 249:    */     {
/* 250:410 */       throw new ArchiveException("Could not use reset and mark operations.", e);
/* 251:    */     }
/* 252:413 */     throw new ArchiveException("No Archiver found for the stream signature");
/* 253:    */   }
/* 254:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.ArchiveStreamFactory
 * JD-Core Version:    0.7.0.1
 */