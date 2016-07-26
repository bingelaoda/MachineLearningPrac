/*  1:   */ package org.apache.commons.compress.archivers.jar;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.OutputStream;
/*  5:   */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*  6:   */ import org.apache.commons.compress.archivers.zip.JarMarker;
/*  7:   */ import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
/*  8:   */ import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
/*  9:   */ 
/* 10:   */ public class JarArchiveOutputStream
/* 11:   */   extends ZipArchiveOutputStream
/* 12:   */ {
/* 13:38 */   private boolean jarMarkerAdded = false;
/* 14:   */   
/* 15:   */   public JarArchiveOutputStream(OutputStream out)
/* 16:   */   {
/* 17:41 */     super(out);
/* 18:   */   }
/* 19:   */   
/* 20:   */   public JarArchiveOutputStream(OutputStream out, String encoding)
/* 21:   */   {
/* 22:52 */     super(out);
/* 23:53 */     setEncoding(encoding);
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void putArchiveEntry(ArchiveEntry ze)
/* 27:   */     throws IOException
/* 28:   */   {
/* 29:59 */     if (!this.jarMarkerAdded)
/* 30:   */     {
/* 31:60 */       ((ZipArchiveEntry)ze).addAsFirstExtraField(JarMarker.getInstance());
/* 32:61 */       this.jarMarkerAdded = true;
/* 33:   */     }
/* 34:63 */     super.putArchiveEntry(ze);
/* 35:   */   }
/* 36:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.jar.JarArchiveOutputStream
 * JD-Core Version:    0.7.0.1
 */