/*  1:   */ package org.apache.commons.compress.archivers.jar;
/*  2:   */ 
/*  3:   */ import java.security.cert.Certificate;
/*  4:   */ import java.util.jar.Attributes;
/*  5:   */ import java.util.jar.JarEntry;
/*  6:   */ import java.util.zip.ZipEntry;
/*  7:   */ import java.util.zip.ZipException;
/*  8:   */ import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
/*  9:   */ 
/* 10:   */ public class JarArchiveEntry
/* 11:   */   extends ZipArchiveEntry
/* 12:   */ {
/* 13:36 */   private final Attributes manifestAttributes = null;
/* 14:37 */   private final Certificate[] certificates = null;
/* 15:   */   
/* 16:   */   public JarArchiveEntry(ZipEntry entry)
/* 17:   */     throws ZipException
/* 18:   */   {
/* 19:40 */     super(entry);
/* 20:   */   }
/* 21:   */   
/* 22:   */   public JarArchiveEntry(String name)
/* 23:   */   {
/* 24:44 */     super(name);
/* 25:   */   }
/* 26:   */   
/* 27:   */   public JarArchiveEntry(ZipArchiveEntry entry)
/* 28:   */     throws ZipException
/* 29:   */   {
/* 30:48 */     super(entry);
/* 31:   */   }
/* 32:   */   
/* 33:   */   public JarArchiveEntry(JarEntry entry)
/* 34:   */     throws ZipException
/* 35:   */   {
/* 36:52 */     super(entry);
/* 37:   */   }
/* 38:   */   
/* 39:   */   @Deprecated
/* 40:   */   public Attributes getManifestAttributes()
/* 41:   */   {
/* 42:65 */     return this.manifestAttributes;
/* 43:   */   }
/* 44:   */   
/* 45:   */   @Deprecated
/* 46:   */   public Certificate[] getCertificates()
/* 47:   */   {
/* 48:77 */     if (this.certificates != null)
/* 49:   */     {
/* 50:78 */       Certificate[] certs = new Certificate[this.certificates.length];
/* 51:79 */       System.arraycopy(this.certificates, 0, certs, 0, certs.length);
/* 52:80 */       return certs;
/* 53:   */     }
/* 54:87 */     return null;
/* 55:   */   }
/* 56:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.jar.JarArchiveEntry
 * JD-Core Version:    0.7.0.1
 */