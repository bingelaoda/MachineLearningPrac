/*  1:   */ package org.apache.commons.compress.archivers.jar;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.InputStream;
/*  5:   */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*  6:   */ import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
/*  7:   */ import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
/*  8:   */ 
/*  9:   */ public class JarArchiveInputStream
/* 10:   */   extends ZipArchiveInputStream
/* 11:   */ {
/* 12:   */   public JarArchiveInputStream(InputStream inputStream)
/* 13:   */   {
/* 14:41 */     super(inputStream);
/* 15:   */   }
/* 16:   */   
/* 17:   */   public JarArchiveInputStream(InputStream inputStream, String encoding)
/* 18:   */   {
/* 19:52 */     super(inputStream, encoding);
/* 20:   */   }
/* 21:   */   
/* 22:   */   public JarArchiveEntry getNextJarEntry()
/* 23:   */     throws IOException
/* 24:   */   {
/* 25:56 */     ZipArchiveEntry entry = getNextZipEntry();
/* 26:57 */     return entry == null ? null : new JarArchiveEntry(entry);
/* 27:   */   }
/* 28:   */   
/* 29:   */   public ArchiveEntry getNextEntry()
/* 30:   */     throws IOException
/* 31:   */   {
/* 32:62 */     return getNextJarEntry();
/* 33:   */   }
/* 34:   */   
/* 35:   */   public static boolean matches(byte[] signature, int length)
/* 36:   */   {
/* 37:76 */     return ZipArchiveInputStream.matches(signature, length);
/* 38:   */   }
/* 39:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.jar.JarArchiveInputStream
 * JD-Core Version:    0.7.0.1
 */