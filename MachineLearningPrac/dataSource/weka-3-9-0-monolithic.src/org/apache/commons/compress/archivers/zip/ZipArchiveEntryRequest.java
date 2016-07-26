/*  1:   */ package org.apache.commons.compress.archivers.zip;
/*  2:   */ 
/*  3:   */ import java.io.InputStream;
/*  4:   */ import org.apache.commons.compress.parallel.InputStreamSupplier;
/*  5:   */ 
/*  6:   */ public class ZipArchiveEntryRequest
/*  7:   */ {
/*  8:   */   private final ZipArchiveEntry zipArchiveEntry;
/*  9:   */   private final InputStreamSupplier payloadSupplier;
/* 10:   */   private final int method;
/* 11:   */   
/* 12:   */   private ZipArchiveEntryRequest(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier payloadSupplier)
/* 13:   */   {
/* 14:42 */     this.zipArchiveEntry = zipArchiveEntry;
/* 15:43 */     this.payloadSupplier = payloadSupplier;
/* 16:44 */     this.method = zipArchiveEntry.getMethod();
/* 17:   */   }
/* 18:   */   
/* 19:   */   public static ZipArchiveEntryRequest createZipArchiveEntryRequest(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier payloadSupplier)
/* 20:   */   {
/* 21:54 */     return new ZipArchiveEntryRequest(zipArchiveEntry, payloadSupplier);
/* 22:   */   }
/* 23:   */   
/* 24:   */   public InputStream getPayloadStream()
/* 25:   */   {
/* 26:62 */     return this.payloadSupplier.get();
/* 27:   */   }
/* 28:   */   
/* 29:   */   public int getMethod()
/* 30:   */   {
/* 31:70 */     return this.method;
/* 32:   */   }
/* 33:   */   
/* 34:   */   ZipArchiveEntry getZipArchiveEntry()
/* 35:   */   {
/* 36:79 */     return this.zipArchiveEntry;
/* 37:   */   }
/* 38:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest
 * JD-Core Version:    0.7.0.1
 */